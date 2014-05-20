/*
  DA-NRW Software Suite | ContentBroker
  Copyright (C) 2013 Historisch-Kulturwissenschaftliche Informationsverarbeitung
  Universität zu Köln

  This program is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.

  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.

  You should have received a copy of the GNU General Public License
  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.uzk.hki.da.at;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.hibernate.classic.Session;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import de.uzk.hki.da.core.HibernateUtil;
import de.uzk.hki.da.grid.DistributedConversionAdapter;
import de.uzk.hki.da.grid.GridFacade;
import de.uzk.hki.da.model.CentralDatabaseDAO;
import de.uzk.hki.da.model.Job;
import de.uzk.hki.da.model.Node;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.model.StoragePolicy;
import de.uzk.hki.da.repository.RepositoryFacade;
import de.uzk.hki.da.utils.NativeJavaTarArchiveBuilder;
import de.uzk.hki.da.utils.Utilities;

public class Base {

	protected String testDataRootPath="src/test/resources/at/";
	protected String ingestAreaRootPath;
	protected String workAreaRootPath;
	protected String gridCacheAreaRootPath;
	protected String userAreaRootPath;
	protected GridFacade gridFacade;
	protected RepositoryFacade repositoryFacade;
	protected DistributedConversionAdapter distributedConversionAdapter;
	protected String nodeName;
	protected CentralDatabaseDAO dao = new CentralDatabaseDAO();
	
	protected void setUpBase() throws IOException{
		
		Properties properties = null;
		InputStream in;
		in = new FileInputStream("conf/config.properties");
		properties = new Properties();
		properties.load(in);

		ingestAreaRootPath = Utilities.slashize((String) properties.get("localNode.ingestAreaRootPath"));
		workAreaRootPath = Utilities.slashize((String) properties.get("localNode.workAreaRootPath"));
		gridCacheAreaRootPath = Utilities.slashize((String) properties.get("localNode.gridCacheAreRootPath"));
		userAreaRootPath = Utilities.slashize((String) properties.get("localNode.userAreaRootPath"));
		nodeName = (String) properties.get("localNode.name");
		System.out.println(ingestAreaRootPath);
	
		HibernateUtil.init("conf/hibernateCentralDB.cfg.xml");
		
		instantiateGrid(properties.getProperty("grid.implementation"),properties.getProperty("implementation.distributedConversion"));
		if (gridFacade==null) throw new IllegalStateException("gridFacade could not be instantiated");
		
		instantiateRepository(properties.getProperty("repository.implementation"));
		if (repositoryFacade==null) throw new IllegalStateException("repositoryFacade could not be instantiated");
	}
	
	/**
	 * @param gridImplBeanName bean name 
	 * @param dcaImplBeanName distributed conversion adapter beanName
	 * @return
	 */
	private void instantiateGrid(String gridImplBeanName,String dcaImplBeanName) {
		
		AbstractApplicationContext context =
				new FileSystemXmlApplicationContext("conf/beans.xml");
		gridFacade = (GridFacade) context.getBean(gridImplBeanName);
		distributedConversionAdapter = (DistributedConversionAdapter) context.getBean(dcaImplBeanName);
		context.close();
	}
	
	private void instantiateRepository(String repImplBeanName) {
		AbstractApplicationContext context =
				new FileSystemXmlApplicationContext("conf/beans.xml");
		repositoryFacade = (RepositoryFacade) context.getBean(repImplBeanName);
		context.close();
	}
	
	
	protected Job waitForJobToBeInStatus(String originalName,String status,int timeout) throws InterruptedException{

		while (true){

			Session session = HibernateUtil.openSession();
			session.beginTransaction();
			Job job = dao.getJob(session, originalName, "TEST");

			session.close();
			
			if (job!=null){
				
				System.out.println("waiting for job to be ready ... "+job.getStatus());
				if (job.getStatus().equals(status)){
					System.out.println("ready");
					return job;
				} else if (job.getStatus().endsWith("1") || job.getStatus().endsWith("3")
						|| job.getStatus().endsWith("4")) {
					String msg = "ERROR: Job in error state: " + job.getStatus();
					System.out.println(msg);
					throw new RuntimeException(msg);
				}
			}
			
			Thread.sleep(timeout);
		}
	}
	
	protected void waitForJobsToFinish(String originalName, int timeout) throws InterruptedException{

		// wait for job to appear
		Job job = null;
		int count = 0;
		while(job == null) {
			
			if(++count * timeout > 60000) {
				throw new RuntimeException("ERROR: Job did not appear after 1 minute! " + originalName);
			}
			
			System.out.println("waiting for job to appear ... " + originalName);
			Session session = HibernateUtil.openSession();
			session.beginTransaction();
			job = dao.getJob(session, originalName, "TEST");
			session.close();
			
			Thread.sleep(timeout);
			
		}
		
		// wait for jobs to disappear
		while (true){

			Session session = HibernateUtil.openSession();
			session.beginTransaction();
			job = dao.getJob(session, originalName, "TEST");

			session.close();
			
			if (job==null) {
				System.out.println("finished! " + originalName);
				return;
			} else if (job.getStatus().endsWith("1") || job.getStatus().endsWith("3")
					|| job.getStatus().endsWith("4")) {
				String oid=job.getObject().getIdentifier();
				String msg = "ERROR: Job in error state: " + job.getStatus() + "in Object-Id "+ oid;
				System.out.println(msg);
				throw new RuntimeException(msg);
			}
			
			System.out.println("waiting for jobs to finish ... "+job.getStatus());
			
			Thread.sleep(timeout);
		}
	}
	
	protected Object fetchObjectFromDB(String originalName){
		Object object = null;
		Session session = HibernateUtil.openSession();
		session.beginTransaction();
		try {
		object = dao.getUniqueObject(session,originalName, "TEST");
		} catch (Exception e) {
			fail("more than 1 Object found!"); 
		}
		session.close();
		return object;
	}
	
	/**
	 * @return physical path to unpacked object
	 * @throws Exception 
	 */
	protected Object retrievePackage(String originalName,String packageName) throws Exception{
		
		Object object=fetchObjectFromDB(originalName);
		
		System.out.println("object: "+object.getIdentifier());
		
		try {
			gridFacade.get(new File("/tmp/"+object.getIdentifier()+".pack_"+packageName+".tar"), 
					"TEST/"+object.getIdentifier()+"/"+object.getIdentifier()+".pack_"+packageName+".tar");
		} catch (IOException e) {
			fail("could not fetch object from grid");
		}
		
		NativeJavaTarArchiveBuilder tar = new NativeJavaTarArchiveBuilder();
		tar.unarchiveFolder(new File("/tmp/"+object.getIdentifier()+".pack_"+packageName+".tar"), new File("/tmp/"));
		
		return object;
	}

	protected void clearDB() {
		Session session = HibernateUtil.openSession();
		session.beginTransaction();
		session.createSQLQuery("DELETE FROM queue").executeUpdate();
		session.createSQLQuery("DELETE FROM events").executeUpdate();
		session.createSQLQuery("DELETE FROM dafiles").executeUpdate();
		session.createSQLQuery("DELETE FROM objects_packages").executeUpdate();
		session.createSQLQuery("DELETE FROM packages").executeUpdate();
		session.createSQLQuery("DELETE FROM objects").executeUpdate();
		session.createSQLQuery("DELETE FROM conversion_queue").executeUpdate();
		
		session.getTransaction().commit();
		session.close();
	}
	
	protected void cleanStorage() throws IOException{
		FileUtils.deleteDirectory(new File(workAreaRootPath+"work/TEST"));
		FileUtils.deleteDirectory(new File(ingestAreaRootPath+"TEST"));
		FileUtils.deleteDirectory(new File(gridCacheAreaRootPath+"TEST"));
		FileUtils.deleteDirectory(new File(workAreaRootPath+"pips/institution/TEST"));
		FileUtils.deleteDirectory(new File(workAreaRootPath+"pips/public/TEST"));
		FileUtils.deleteDirectory(new File(userAreaRootPath+"TEST/outgoing"));
		
		distributedConversionAdapter.remove("work/TEST");
		distributedConversionAdapter.remove("aip/TEST");
		distributedConversionAdapter.remove("pips/institution/TEST");
		distributedConversionAdapter.remove("pips/public/TEST");
		
		distributedConversionAdapter.create("work/TEST");
		distributedConversionAdapter.create("aip/TEST");
		distributedConversionAdapter.create("pips/institution/TEST");
		distributedConversionAdapter.create("pips/public/TEST");
		
		new File(userAreaRootPath+"TEST/outgoing").mkdirs();
		new File(gridCacheAreaRootPath+"TEST").mkdirs();
		new File(ingestAreaRootPath+"TEST").mkdirs();
		new File(workAreaRootPath+"work/TEST").mkdirs();
		new File(workAreaRootPath+"pips/public/TEST").mkdirs();
		new File(workAreaRootPath+"pips/institution/TEST").mkdirs();
	}
	

	/**
	 * @throws IOException 
	 */
	protected void createObjectAndJob(String name, String status) throws IOException{
		gridFacade.put(
				new File("src/test/resources/at/"+name+".pack_1.tar"),
				"TEST/ID-"+name+"/ID-"+name+".pack_1.tar",new StoragePolicy(new Node()));
		
		Session session = HibernateUtil.openSession();
		session.beginTransaction();
		Integer dbid = (Integer) session.createSQLQuery("SELECT MAX(data_pk) FROM objects").uniqueResult(); 
		if (dbid==null) dbid = 0;
		dbid++;
		System.out.println("CREATED Object with id " + dbid);
		session.createSQLQuery("INSERT INTO objects (data_pk,urn,identifier,orig_name,contractor_id,object_state,published_flag,ddb_exclusion) "
				+"VALUES (" + dbid + ",'urn:nbn:de:danrw-test-" + dbid + "','ID-"+name+"','"+name+"',1,'100',0,false);").executeUpdate();
		Integer pkid = (Integer) session.createSQLQuery("SELECT MAX(id) FROM packages").uniqueResult(); 
		if (pkid==null) pkid = 0;
		pkid++;
		session.createSQLQuery("INSERT INTO packages (id,name) VALUES ("+pkid+",'1');").executeUpdate();
		session.createSQLQuery("INSERT INTO objects_packages (objects_data_pk,packages_id) VALUES ("+dbid+","+pkid+");").executeUpdate();
		
		session.createSQLQuery("INSERT INTO queue (id,status,objects_id,initial_node) VALUES ("+dbid+",'"+status+"',"+dbid+","+
				"'"+nodeName+"');").executeUpdate();
		
		session.getTransaction().commit();
		session.close();	
	}

	protected Object ingest(String originalName) throws IOException,
			InterruptedException {
				FileUtils.copyFileToDirectory(new File(testDataRootPath+originalName+".tgz"), 
						new File(ingestAreaRootPath+"TEST"));
				waitForJobsToFinish(originalName,500);
				
				Object object = fetchObjectFromDB(originalName);
				return object;
			}
	
	
}
