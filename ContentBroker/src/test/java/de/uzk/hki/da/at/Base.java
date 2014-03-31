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
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.repository.RepositoryFacade;
import de.uzk.hki.da.utils.NativeJavaTarArchiveBuilder;
import de.uzk.hki.da.utils.Utilities;

public class Base {

	protected String testDataRootPath="src/test/resources/at/";
	protected String ingestAreaRootPath;
	protected String workAreaRootPath;
	protected String gridCacheAreaRootPath;
	protected String userAreaRootPath;
	protected String dipAreaRootPath;
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
		dipAreaRootPath = Utilities.slashize((String) properties.get("localNode.dipAreaRootPath"));
		nodeName = (String) properties.get("irods.server");
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
				new FileSystemXmlApplicationContext("src/main/resources/META-INF/beans-infrastructure.core.xml");
		gridFacade = (GridFacade) context.getBean(gridImplBeanName);
		distributedConversionAdapter = (DistributedConversionAdapter) context.getBean(dcaImplBeanName);
		context.close();
	}
	
	private void instantiateRepository(String repImplBeanName) {
		AbstractApplicationContext context =
				new FileSystemXmlApplicationContext("src/main/resources/META-INF/beans-infrastructure.core.xml");
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

		while (true){

			Session session = HibernateUtil.openSession();
			session.beginTransaction();
			Job job = dao.getJob(session, originalName, "TEST");

			session.close();
			
			if (job==null) {
				return;
			} else if (job.getStatus().endsWith("1") || job.getStatus().endsWith("3")
					|| job.getStatus().endsWith("4")) {
				String msg = "ERROR: Job in error state: " + job.getStatus();
				System.out.println(msg);
				throw new RuntimeException(msg);
			}
			
			System.out.println("waiting for jobs to finish ... "+job.getStatus());
			
			Thread.sleep(timeout);
		}
	}
	
	protected Object fetchObjectFromDB(String originalName){
		Object object;
		Session session = HibernateUtil.openSession();
		session.beginTransaction();
		object = dao.getUniqueObject(session,originalName, "TEST");
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
					"/aip/TEST/"+object.getIdentifier()+"/"+object.getIdentifier()+".pack_"+packageName+".tar");
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
		session.createSQLQuery("DELETE FROM objects_packages").executeUpdate();
		session.createSQLQuery("DELETE FROM packages").executeUpdate();
		session.createSQLQuery("DELETE FROM objects").executeUpdate();
		session.getTransaction().commit();
		session.close();
	}
	
	protected void cleanStorage() throws IOException{
		FileUtils.deleteDirectory(new File(workAreaRootPath+"TEST"));
		FileUtils.deleteDirectory(new File(ingestAreaRootPath+"TEST"));
		FileUtils.deleteDirectory(new File(gridCacheAreaRootPath+"aip/TEST"));
		FileUtils.deleteDirectory(new File(dipAreaRootPath+"institution/TEST"));
		FileUtils.deleteDirectory(new File(dipAreaRootPath+"public/TEST"));
		FileUtils.deleteDirectory(new File(userAreaRootPath+"TEST/outgoing"));
		
		distributedConversionAdapter.remove("fork/TEST");
		distributedConversionAdapter.remove("aip/TEST");
		distributedConversionAdapter.remove("dip/institution/TEST");
		distributedConversionAdapter.remove("dip/public/TEST");
		
		distributedConversionAdapter.create("fork/TEST");
		distributedConversionAdapter.create("aip/TEST");
		distributedConversionAdapter.create("dip/institution/TEST");
		distributedConversionAdapter.create("dip/public/TEST");
		
		new File(userAreaRootPath+"TEST/outgoing").mkdirs();
		new File(gridCacheAreaRootPath+"aip/TEST").mkdirs();
		new File(ingestAreaRootPath+"TEST").mkdirs();
		new File(workAreaRootPath+"TEST").mkdirs();
		new File(dipAreaRootPath+"public/TEST").mkdirs();
		new File(dipAreaRootPath+"institution/TEST").mkdirs();
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
