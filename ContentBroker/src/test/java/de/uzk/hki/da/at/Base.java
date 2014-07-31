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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.hibernate.classic.Session;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import de.uzk.hki.da.core.HibernateUtil;
import de.uzk.hki.da.grid.DistributedConversionAdapter;
import de.uzk.hki.da.grid.GridFacade;
import de.uzk.hki.da.model.CentralDatabaseDAO;
import de.uzk.hki.da.model.Contractor;
import de.uzk.hki.da.model.Job;
import de.uzk.hki.da.model.Node;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.model.Package;
import de.uzk.hki.da.model.StoragePolicy;
import de.uzk.hki.da.repository.RepositoryFacade;
import de.uzk.hki.da.utils.C;
import de.uzk.hki.da.utils.NativeJavaTarArchiveBuilder;
import de.uzk.hki.da.utils.Path;
import de.uzk.hki.da.utils.RelativePath;
import de.uzk.hki.da.utils.TC;
import de.uzk.hki.da.utils.Utilities;

public class Base {

	private static final int wait_interval=2000; // in ms
	
	protected Path testDataRootPath = new RelativePath("src/test/resources/at/");
	protected Node localNode;
	protected GridFacade gridFacade;
	protected RepositoryFacade repositoryFacade;
	protected DistributedConversionAdapter distributedConversionAdapter;
	protected CentralDatabaseDAO dao = new CentralDatabaseDAO();
	protected Contractor testContractor;

	
	protected void setUpBase() throws IOException{
		
		Properties properties = Utilities.read(new File("conf/config.properties"));

		HibernateUtil.init("conf/hibernateCentralDB.cfg.xml");
	
		instantiateNode();
		if (localNode==null) throw new IllegalStateException("localNode could not be instantiated");

		System.out.println("localnode: "+localNode.getName());

		
		instantiateGrid(
				properties.getProperty("cb.implementation.grid"),
				properties.getProperty("cb.implementation.distributedConversion"));
		if (gridFacade==null) throw new IllegalStateException("gridFacade could not be instantiated");
		
		instantiateRepository(properties.getProperty("cb.implementation.repository"));
		if (repositoryFacade==null) throw new IllegalStateException("repositoryFacade could not be instantiated");

		CentralDatabaseDAO centralDB = new CentralDatabaseDAO();
		Session session = HibernateUtil.openSession();
		session.beginTransaction();
		testContractor = centralDB.getContractor(session, "TEST");
		session.close();
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
	
	private void instantiateNode() {
		AbstractApplicationContext context = 
				new FileSystemXmlApplicationContext("conf/beans.xml");
		localNode = (Node) context.getBean("localNode");
		
		Session session = HibernateUtil.openSession();
		session.beginTransaction();
		session.refresh(localNode);
		session.close();
		
		context.close();
	}
	
	private void instantiateRepository(String repImplBeanName) {
		AbstractApplicationContext context =
				new FileSystemXmlApplicationContext("conf/beans.xml");
		repositoryFacade = (RepositoryFacade) context.getBean(repImplBeanName);
		context.close();
	}
	
	
	
	/**
	 * Checking the database in regular intervals for a job in an error state ending with errorStatusLastDigit.
	 * 
	 * @param originalName
	 * @param errorStatusLastDigit
	 * @param timeout wait timeout ms until you consider the test failed.
	 * @return job if found job in error state
	 * @throws RuntimeException to signal the test considered failed.
	 * 
	 * @author Daniel M. de Oliveira
	 */
	protected Job waitForJobToBeInErrorStatus(String originalName,String errorStatusLastDigit,int timeout) throws InterruptedException{
		
		int waited_ms_total=0;
		while (true){
			if (waited_ms_total>timeout) throw new RuntimeException("waited to long. test considered failed");
			
			Session session = HibernateUtil.openSession();
			session.beginTransaction();
			Job job = dao.getJob(session, originalName, C.TEST_USER_SHORT_NAME);
			session.close();
			
			if (job==null) continue;
			
			Thread.sleep(wait_interval);
			waited_ms_total+=wait_interval;

			System.out.println("waiting for job to be ready ... "+job.getStatus());
			if (job.getStatus().endsWith(errorStatusLastDigit)){
				System.out.println("ready");
				return job;
			}
		}
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
	
	protected void waitForJobsToFinish(String originalName, int timeout){

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
			
			try {
				Thread.sleep(timeout);
			} catch (InterruptedException e) {} // no problem
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
				String msg = "ERROR: Job in error state: " + job.getStatus() + " in Object-Id "+ oid;
				System.out.println(msg);
				throw new RuntimeException(msg);
			}
			
			System.out.println("waiting for jobs to finish ... "+job.getStatus());
			
			try {
				Thread.sleep(timeout);
			} catch (InterruptedException e) {} // no problem
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
	protected Object retrievePackage(String originalName,String packageName){
		
		Object object=fetchObjectFromDB(originalName);
		
		System.out.println("object: "+object.getIdentifier());
		
		try {
			gridFacade.get(new File("/tmp/"+object.getIdentifier()+".pack_"+packageName+".tar"), 
					"TEST/"+object.getIdentifier()+"/"+object.getIdentifier()+".pack_"+packageName+".tar");
		} catch (IOException e) {
			fail("could not fetch object from grid");
		}
		
		NativeJavaTarArchiveBuilder tar = new NativeJavaTarArchiveBuilder();
		
		try {
			tar.unarchiveFolder(new File("/tmp/"+object.getIdentifier()+".pack_"+packageName+".tar"), new File("/tmp/"));
		} catch (Exception e) {
			fail("could not find source file or unarchive source file to tmp");
		}
		
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
	

	protected void cleanStorage(){
		FileUtils.deleteQuietly(Path.make(localNode.getWorkAreaRootPath(),"/work/TEST").toFile());
		FileUtils.deleteQuietly(Path.make(localNode.getIngestAreaRootPath(),"/TEST").toFile());
		FileUtils.deleteQuietly(Path.makeFile(localNode.getGridCacheAreaRootPath(),C.AIP,C.TEST_USER_SHORT_NAME));
		FileUtils.deleteQuietly(Path.make(localNode.getWorkAreaRootPath(),"/pips/institution/TEST").toFile());
		FileUtils.deleteQuietly(Path.make(localNode.getWorkAreaRootPath(),"/pips/public/TEST").toFile());
		FileUtils.deleteQuietly(Path.make(localNode.getUserAreaRootPath(),"/TEST/outgoing").toFile());
		
		distributedConversionAdapter.remove("work/TEST");
		distributedConversionAdapter.remove("aip/TEST");
		distributedConversionAdapter.remove("pips/institution/TEST");
		distributedConversionAdapter.remove("pips/public/TEST");
		
		distributedConversionAdapter.create("work/TEST");
		distributedConversionAdapter.create("aip/TEST");
		distributedConversionAdapter.create("pips/institution/TEST");
		distributedConversionAdapter.create("pips/public/TEST");
		
		Path.make(localNode.getUserAreaRootPath(),"/TEST/outgoing").toFile().mkdirs();
		Path.makeFile(localNode.getGridCacheAreaRootPath(),"aip",C.TEST_USER_SHORT_NAME).mkdirs();
		Path.make(localNode.getIngestAreaRootPath(),"/TEST").toFile().mkdirs();
		Path.make(localNode.getWorkAreaRootPath(),"/work/TEST").toFile().mkdirs();
		Path.make(localNode.getWorkAreaRootPath(),"/pips/public/TEST").toFile().mkdirs();
		Path.make(localNode.getWorkAreaRootPath(),"/pips/institution/TEST").toFile().mkdirs();
	}
	

	protected void createObjectAndJob(String name,String status) throws IOException{
		createObjectAndJob(name,status,null,null);
	}
	
	/**
	 * @author Daniel M. de Oliveira
	 * @throws IOException 
	 */
	protected Object putPackageToStorage(String identifier,String originalName,String containerName, Date createddate, int object_state) throws IOException{
		if (createddate==null) createddate = new Date();
		String urn =   "urn:nbn:de:danrw:"+identifier;
		int timeout = 2000;
		StoragePolicy sp = new StoragePolicy(localNode);
		ArrayList<String> destinations = new ArrayList<String>();
		destinations.add("ciArchiveResourceGroup");
		sp.setDestinations(destinations);
		sp.setMinNodes(1);
		
		gridFacade.put(Path.makeFile(TC.TEST_ROOT_AT,identifier+".pack_1.tar"), 
				new RelativePath(C.TEST_USER_SHORT_NAME,identifier,identifier+".pack_1.tar").toString(), sp);
		int i = 0;
		while (!gridFacade.storagePolicyAchieved(new RelativePath(C.TEST_USER_SHORT_NAME,identifier,identifier+".pack_1.tar").toString(), sp)) {
			try {
				Thread.sleep(timeout);
			} catch (InterruptedException e) {} // no problem
			if (i>200) fail("Package was not replicated to archive resc");
		}
		Object object = new Object();
		object.setContractor(testContractor);
		object.setInitial_node("localnode");
		object.setIdentifier(identifier);
		object.setObject_state(object_state);
		object.setUrn(urn);
		object.setDate_created(String.valueOf(createddate.getTime()));
		object.setDate_modified(String.valueOf(createddate.getTime()));
		object.setLast_checked(createddate);
		object.setOrig_name(originalName);
		Package pkg = new Package();
		pkg.setName("1");
		pkg.setContainerName(containerName);
		object.getPackages().add(pkg);
		
		Session session = HibernateUtil.openSession();
		session.beginTransaction();
		session.save(object);
		session.getTransaction().commit();
		session.close();
		
		return object;
	}
	

	/**
	 * @author jpeters
	 * @throws IOException 
	 */
	protected Object putPackageToStorage(String identifier,String originalName,String containerName) throws IOException{
		 return putPackageToStorage(identifier,originalName,containerName ,null,0);
	}
	
	
	/**
	 * @throws IOException 
	 */
	protected void createObjectAndJob(String name, String status,String packageType,String metadataFile) throws IOException{
		gridFacade.put(
				new File("src/test/resources/at/"+name+".pack_1.tar"),
				"TEST/ID-"+name+"/ID-"+name+".pack_1.tar",new StoragePolicy(new Node()));
		
		
		Session session = HibernateUtil.openSession();
		session.beginTransaction();
		
		Object object = new Object();
		object.setUrn("");
		object.setIdentifier("ID-"+name);
		object.setOrig_name(name);
		
		object.setContractor(testContractor);
		object.setMetadata_file(metadataFile);
		object.setPackage_type(packageType);
		object.setObject_state(100);
		object.setPublished_flag(0);
		object.setDdbExclusion(false);
		session.save(object);

		int data_pk = object.getData_pk();
		System.out.println("CREATED Object with id " + data_pk);
		String data_pk_string = String.valueOf(data_pk);
		object.setUrn("urn:nbn:de:danrw-test-"+data_pk_string);
		session.saveOrUpdate(object);
		
		
		Package currentPackage = new Package();
		currentPackage.setName("1");
		currentPackage.setContainerName(name);
		List<Package> packages = new ArrayList<Package>();
		packages.add(currentPackage);
		object.setPackages(packages);		
		session.saveOrUpdate(object);
		
		Job job = new Job();
		job.setStatus(status);
		Node node = (Node)session.load(Node.class, localNode .getId());
		job.setResponsibleNodeName(node.getName());
		job.setObject(object);
		session.save(job);
		
		session.getTransaction().commit();
		session.close();
	}
	
	/**
	 * Puts the file with named originalName into the contractor TEST's
	 * subfolder of the ingest area of the running ContentBroker. Waits until 
	 * the file has been ingested.
	 * 
	 * For the source file will be searched for in testDataRootPath.
	 * 
	 * @author Daniel M. de Oliveira
	 * @param originalName just the basename of the file. the extension default to tgz. if you want to explicitely change that use 
	 * ingest(String,String).
	 * @return the database entry for the object of the ingested package.
	 */
	protected Object ingest(String originalName){
		
		return ingest(originalName,"tgz");
	}
			
	/**
	 * @see Base#ingest(String)
	 * 
	 * @author Daniel M. de Oliveira
	 * @param originalName
	 * @param containerSuffix
	 * @return
	 */
	protected Object ingest(String originalName,String containerSuffix){
		
		try {
			System.out.println("copy "+Path.makeFile( testDataRootPath, originalName+"."+containerSuffix )+" to "+Path.makeFile(localNode.getIngestAreaRootPath(),"TEST"));
			FileUtils.copyFileToDirectory( Path.makeFile( testDataRootPath, originalName+"."+containerSuffix ), 
					Path.makeFile(localNode.getIngestAreaRootPath(),"TEST"));
		} catch (IOException e) {
			fail("could not copy file to ingest area. \n"+e);
		}
		waitForJobsToFinish(originalName,500);
		
		Object object = fetchObjectFromDB(originalName);
		return object;
	}	
}
