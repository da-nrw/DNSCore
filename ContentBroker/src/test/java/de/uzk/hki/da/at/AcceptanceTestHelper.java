/*
  DA-NRW Software Suite | ContentBroker
  Copyright (C) 2014 LVRInfoKom
  Landschaftsverband Rheinland

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.hibernate.classic.Session;

import de.uzk.hki.da.core.HibernateUtil;
import de.uzk.hki.da.grid.GridFacade;
import de.uzk.hki.da.model.CentralDatabaseDAO;
import de.uzk.hki.da.model.Job;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.model.Node;
import de.uzk.hki.da.model.Package;
import de.uzk.hki.da.model.StoragePolicy;
import de.uzk.hki.da.model.User;
import de.uzk.hki.da.path.Path;
import de.uzk.hki.da.path.RelativePath;
import de.uzk.hki.da.pkg.NativeJavaTarArchiveBuilder;
import de.uzk.hki.da.test.TC;
import de.uzk.hki.da.utils.C;

/**
 * @author Daniel M. de Oliveira
 */
public class AcceptanceTestHelper {

	private static final String URN_NBN_DE_DANRW = "urn:nbn:de:danrw:";
	protected static Path testDataRootPath = new RelativePath("src/test/resources/at/");
	
	private static final int wait_interval=2000; // in ms
	private static final int TIMEOUT=300000; // ins ms
	
	private GridFacade gridFacade;
	private CentralDatabaseDAO dao;
	private Node localNode;
	private User testContractor;


	public AcceptanceTestHelper(
			GridFacade gridFacade,
			CentralDatabaseDAO dao,
			Node localNode,
			User testContractor){
		this.gridFacade=gridFacade;
		this.dao=dao;
		this.localNode=localNode;
		this.testContractor=testContractor;
	}
			
			
	
	/**
	 * Retrieves a package and unpacks it to a target folder.
	 * <br>
	 * <strong>!</strong> Make sure to delete targetFolder at tearDown in acceptance tests.
	 * 
	 * @param originalName of the object.
	 * @param targetFolder to extract the DIP to.
	 * @param packageName number of the package of the object.
	 * @return the object entry from the database. 
	 * @throws IOException if cannot fetch file from grid.
	 * 
	 * @author Daniel M. de Oliveira
	 */
	protected Object retrievePackage(Object o,File targetFolder,String packageName) throws IOException{
		
		Object object=fetchObjectFromDB(o.getOrig_name());
		
		System.out.println("object: "+object.getIdentifier());
		
		gridFacade.get(new File("/tmp/"+object.getIdentifier()+".pack_"+packageName+".tar"), 
			"TEST/"+object.getIdentifier()+"/"+object.getIdentifier()+".pack_"+packageName+".tar");
		
		NativeJavaTarArchiveBuilder tar = new NativeJavaTarArchiveBuilder();
		
		try {
			tar.unarchiveFolder(new File("/tmp/"+object.getIdentifier()+".pack_"+packageName+".tar"), 
					new File("/tmp/"));
		} catch (Exception e) {
			fail("could not find source file or unarchive source file to tmp");
		}
		
		new File("/tmp/"+object.getIdentifier()+".pack_"+packageName+".tar").delete();
		FileUtils.moveDirectory(new File("/tmp/"+object.getIdentifier()+".pack_"+packageName),targetFolder);
		
		return object;
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
	 * Checking the database in regular intervals for a job in an error state ending with errorStatusLastDigit.
	 * 
	 * @param originalName
	 * @param errorStatusLastDigit
	 * @param timeout wait timeout ms until you consider the test failed.
	 * @return job if found job in error state
	 * @throws RuntimeException to signal the test considered failed. For example if it takes longer than timeout to reach the status.
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

	/**
	 * Waits for a job to reach a certain status.
	 * 
	 * @param originalName
	 * @param status
	 * @param timeout
	 * @return
	 * @throws InterruptedException
	 */
	protected Job waitForJobToBeInStatus(String originalName,String status,int timeout) 
			throws InterruptedException{
	
		int waited_ms_total=0;
		while (true){
			if (waited_ms_total>timeout) throw new RuntimeException("waited to long. test considered failed");
	
			Session session = HibernateUtil.openSession();
			session.beginTransaction();
			Job job = dao.getJob(session, originalName, "TEST");
	
			session.close();
			
			if (job!=null){
				
				System.out.println("waiting for job to be ready ... "+job.getStatus());
				
				if (job.getStatus().equals(status)){
					System.out.println("ready");
					return job;
				} else if (isInErrorState(job)) {
					String msg = "ERROR: Job in error state: " + job.getStatus();
					System.out.println(msg);
					
					throw new RuntimeException(msg);
				}
			}
	
			Thread.sleep(wait_interval);
			waited_ms_total+=wait_interval;
		}
	}

	private boolean isInErrorState(Job job){
		if (job.getStatus().endsWith("1") || job.getStatus().endsWith("3")
				|| job.getStatus().endsWith("4")) return true;
		return false;
	}
	
	
	/**
	 * Waits that a job appears and disappears again.
	 * 
	 * @param originalName
	 * @param timeout
	 * @return
	 * @throws RuntimeException if errorState occured.
	 */
	protected Object waitForJobsToFinish(String originalName, int timeout){
	
		// wait for job to appear
		Job job = null;
		int waited_ms_total=0;
		while(job == null) {
			if (waited_ms_total>timeout) throw new RuntimeException("waited to long. test considered failed");
			
			System.out.println("waiting for job to appear ... " + originalName);
			Session session = HibernateUtil.openSession();
			session.beginTransaction();
			job = dao.getJob(session, originalName, "TEST");
			session.close();
			
			try {
				Thread.sleep(wait_interval);
			} catch (InterruptedException e) {} // no problem
			waited_ms_total+=wait_interval;
		}
		
		Object resultO = job.getObject();
	
		// wait for jobs to disappear
		while (true){
			if (waited_ms_total>timeout) throw new RuntimeException("waited to long. test considered failed");
			
			Session session = HibernateUtil.openSession();
			session.beginTransaction();
			job = dao.getJob(session, originalName, "TEST");
	
			session.close();
			
			if (job==null) {
				System.out.println("finished! " + originalName);
				return resultO;
				
			} else if (job.getStatus().endsWith("1") || job.getStatus().endsWith("3")
					|| job.getStatus().endsWith("4")) {
				String oid=job.getObject().getIdentifier();
				String msg = "ERROR: Job in error state: " + job.getStatus() + " in Object-Id "+ oid;
				System.out.println(msg);
				
				if (job.getObject().getIdentifier()!=null){
					try {
						System.out.println("SHOWING OBJECT LOG:");
						String localNodeWorkArea = localNode.getWorkAreaRootPath().toString();
						String localNode = localNodeWorkArea.replace("/storage/WorkArea", "");
						System.out.println(FileUtils.readFileToString(new File(Path.make(localNode, "log", "object-logs")+"/"+job.getObject().getIdentifier()+".log")));
						System.out.println("END OF OBJECT LOG: "+job.getObject().getIdentifier());
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				
				throw new RuntimeException(msg);
			}
			
			System.out.println("waiting for jobs to finish ... "+job.getStatus());
			
			try {
				Thread.sleep(wait_interval);
			} catch (InterruptedException e) {}
			waited_ms_total+=wait_interval;
		}
	}

	/**
	 * Copies src/test/resources/at/[originalName].tgz to
	 * IngestAreaRootPath/TEST/[originalName].tgz.
	 * Waits until the package has been ingsted.
	 * 
	 * @return the database entry for the object.
	 * @throws IOException 
	 * 
	 * @see {@link Base#ingest(String, String, String)}
	 * 
	 * @author Daniel M. de Oliveira
	 */
	protected Object ingest(String originalName) throws IOException{
		
		return ingest(originalName,"tgz",originalName);
	}
	
	/**
	 * Copies src/test/resources/at/[sourcePackageName].[ext] 
	 * to ingestAreaRootPath/TEST/[originalName].[ext]. 
	 * Waits until the package has been ingested.
	 * 
	 * @param sourcePackageName
	 * @param originalName
	 * @param ext
	 * @return the database entry for the object.
	 * @throws IOException 
	 * 
	 * @author Daniel M. de Oliveira
	 */
	protected Object ingest(
			String sourcePackageName,
			String ext,
			String originalName) throws IOException{
		
		if (localNode==null) throw new IllegalStateException();
		if (localNode.getIngestAreaRootPath()==null) throw new IllegalStateException();
		
		File sourceFile = Path.makeFile(testDataRootPath,sourcePackageName+"."+ext);
		File targetFile = Path.makeFile(localNode.getIngestAreaRootPath(),"TEST",originalName+"."+ext);
		
		FileUtils.copyFile( sourceFile, targetFile );
			
		waitForJobsToFinish(originalName,TIMEOUT);
		
		Object object = fetchObjectFromDB(originalName);
		System.out.println("successfully ingested object with id "+object.getIdentifier());
		return object;
	}

	
	
	
	/**
	 * @param originalName
	 * @param errorState
	 * @return
	 * @throws IOException
	 * @throws InterruptedException
	 */
	protected Object ingestAndWaitForErrorState(String originalName,String errorState) throws IOException, InterruptedException{
		
		return ingestAndWaitForErrorState(originalName, errorState, C.FILE_EXTENSION_TGZ);
	}

	
	/**
	 * Puts src/test/resources/at/[originalName].tgz to 
	 * IngestAreaRootPath/TEST/.
	 * Waits that the test finds a job with status in the queue. 
	 * 
	 * @param originalName
	 * @param status
	 * @return the object entry. created by the running system for the ingested package.
	 * @throws IOException
	 * @throws RuntimeException if a no job found in specified state within the timeframe specified by TIMEOUT.
	 */
	protected Object ingestAndWaitForJobInState(String originalName,String status) throws IOException, InterruptedException{
		
		if (localNode==null) throw new IllegalStateException();
		if (localNode.getIngestAreaRootPath()==null) throw new IllegalStateException();
		
		FileUtils.copyFileToDirectory(Path.makeFile(TC.TEST_ROOT_AT,originalName+"."+C.FILE_EXTENSION_TGZ), 
				Path.makeFile(localNode.getIngestAreaRootPath(),C.TEST_USER_SHORT_NAME));
		
		waitForJobToBeInStatus(originalName,status,TIMEOUT);
		return fetchObjectFromDB(originalName);
	}
	
	
	
	/**
	 * @param originalName
	 * @param errorStateLastDigit
	 * @param containerSuffix
	 * @return
	 * @throws IOException
	 * @throws InterruptedException
	 */
	protected Object ingestAndWaitForErrorState(String originalName,String errorStateLastDigit,String containerSuffix) throws IOException, InterruptedException{
		
		if (!containerSuffix.isEmpty()) containerSuffix="."+containerSuffix;
		
		FileUtils.copyFileToDirectory(Path.makeFile(TC.TEST_ROOT_AT,originalName+containerSuffix), 
				Path.makeFile(localNode.getIngestAreaRootPath(),C.TEST_USER_SHORT_NAME));
		waitForJobToBeInErrorStatus(originalName,errorStateLastDigit,300000);
		return fetchObjectFromDB(originalName);
	}



	/**
	 * @author Daniel M. de Oliveira
	 * @throws IOException 
	 */
	protected Object putPackageToStorage(String identifier,String originalName,String containerName, Date createddate, int object_state) throws IOException{
		if (createddate==null) createddate = new Date();
		String urn =   URN_NBN_DE_DANRW+identifier;
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



	protected Map<Session, Object> createObject(String name, String packageType,String metadataFile) throws IOException {
		
		Map<Session, Object> sessionObjectMap = new HashMap<Session, Object>();
		
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
	
		int current_data_pk = object.getData_pk();
		System.out.println("CREATED Object with id " + current_data_pk);
		String current_data_pk_string = String.valueOf(current_data_pk);
		object.setUrn("urn:nbn:de:danrw-test-"+current_data_pk_string);
		session.saveOrUpdate(object);
		
		Package currentPackage = new Package();
		currentPackage.setName("1");
		currentPackage.setContainerName(name);
		List<Package> packages = new ArrayList<Package>();
		packages.add(currentPackage);
		object.setPackages(packages);		
		session.saveOrUpdate(object);	
		
		sessionObjectMap.put(session, object);
		
		return sessionObjectMap;
	}



	/**
	 * @throws IOException 
	 */
	protected void createJob(Map<Session, Object> sessionObject, String status) {
		
		Session session = (Session) sessionObject.keySet().toArray()[0];
		Object object = sessionObject.get(session);
	
		Job job = new Job();
		job.setStatus(status);
		Node node = (Node)session.load(Node.class, localNode .getId());
		job.setResponsibleNodeName(node.getName());
		job.setObject(object);
		session.save(job);
		
		session.getTransaction().commit();
		session.close();
	}



	protected void createObjectAndJob(String name,String status) throws IOException{
		createObjectAndJob(name,status,null,null);
	}



	/**
		 * @throws IOException 
		 */
		protected void createObjectAndJob(String name, 
				String status,
				String packageType,
				String metadataFile) throws IOException{
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
	//		job.setStatus(status);
			Node node = (Node)session.load(Node.class, localNode .getId());
			job.setResponsibleNodeName(node.getName());
			job.setObject(object);
			
			job.setStatus(status);
		
			session.save(job);
			
			session.getTransaction().commit();
			session.close();
		}
}
