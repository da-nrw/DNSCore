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
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.hibernate.Session;

import de.uzk.hki.da.core.C;
import de.uzk.hki.da.grid.GridFacade;
import de.uzk.hki.da.model.Copy;
import de.uzk.hki.da.model.Job;
import de.uzk.hki.da.model.Node;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.model.ObjectNamedQueryDAO;
import de.uzk.hki.da.model.Package;
import de.uzk.hki.da.model.StoragePolicy;
import de.uzk.hki.da.model.User;
import de.uzk.hki.da.pkg.NativeJavaTarArchiveBuilder;
import de.uzk.hki.da.repository.MetadataIndex;
import de.uzk.hki.da.service.HibernateUtil;
import de.uzk.hki.da.test.TC;
import de.uzk.hki.da.util.Path;
import de.uzk.hki.da.util.RelativePath;
import de.uzk.hki.da.utils.MD5Checksum;

/**
 * @author Daniel M. de Oliveira
 */
public class AcceptanceTestHelper {

	private static final String MSG_READY = "ready";
	private static final String MSG_ERROR_WHEN_TIMEOUT_REACHED = "waited to long. test considered failed";
	private static final String TEMP_FOLDER = "/tmp/";
	static final String URN_NBN_DE_DANRW = "urn:nbn:de:danrw-";
	protected static Path TEST_DATA_ROOT_PATH = new RelativePath("src/test/resources/at/");
	
	private static final int INTERVAL=2000; // in ms
	private static final int TIMEOUT=600000; // ins ms
	
	private GridFacade gridFacade;
	private Node localNode;
	private User testContractor;
	private StoragePolicy sp;
	
	public AcceptanceTestHelper(
			GridFacade gridFacade,
			Node localNode,
			User testContractor,
			StoragePolicy sp){
		this.gridFacade=gridFacade;
		this.localNode=localNode;
		this.testContractor=testContractor;
		this.sp = sp;
	}
			
			
	
	/**
	 * Retrieves a package and unpacks it to a target folder.
	 * <br>
	 * <strong>!</strong> Make sure to delete targetFolder at tearDown in acceptance tests.
	 * 
	 * @param originalName of the object.
	 * @param targetFolder to extract the DIP to.
	 * @param packageName number of the package of the object.
	 * @return a new instance that represents the object. fetched from the from the database. 
	 * @throws IOException if cannot fetch file from grid.
	 * 
	 * @author Daniel M. de Oliveira
	 */
	Object retrievePackage(Object o,File targetFolder,String packageName) throws IOException{

		final String packSuffix = ".pack_";
		
		Object object=fetchObjectFromDB(o.getOrig_name());
		if (object==null) throw new RuntimeException("cannot find object");
		System.out.println("object: "+object.getIdentifier());
		
		gridFacade.get(Path.makeFile(TEMP_FOLDER,object.getIdentifier()+packSuffix+packageName+C.FILE_EXTENSION_TAR), 
			testContractor.getShort_name()+
			    "/"+object.getIdentifier()+"/"+object.getIdentifier()+packSuffix+packageName+C.FILE_EXTENSION_TAR);
		try {
			new NativeJavaTarArchiveBuilder().unarchiveFolder(Path.makeFile(TEMP_FOLDER,object.getIdentifier()+packSuffix+packageName+C.FILE_EXTENSION_TAR), 
					Path.makeFile(TEMP_FOLDER));
		} catch (Exception e) {
			fail("could not find source file or unarchive source file to tmp");
		}
		
		if (targetFolder.exists()) FileUtils.deleteDirectory(targetFolder);
		FileUtils.moveDirectory(Path.makeFile(TEMP_FOLDER,object.getIdentifier()+packSuffix+packageName),targetFolder);
		Path.makeFile(TEMP_FOLDER,object.getIdentifier()+packSuffix+packageName+C.FILE_EXTENSION_TAR).delete();
		
		return object;
	}
	
	Object fetchObjectFromDB(String originalName){
		Object object = null;
		try {
			object = new ObjectNamedQueryDAO().getUniqueObject(originalName, testContractor.getShort_name());
		} catch (Exception e) {
			fail("Exception loading Object called " + originalName + " " + e.getStackTrace()); 
		}
		
		return object;
	}

	
	
	Job waitForJobToBeInErrorStatus(String originalName,String errorStatusLastDigit) throws InterruptedException{
		
		return waitForJobToBeInErrorStatus(originalName,errorStatusLastDigit,TIMEOUT);
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
	Job waitForJobToBeInErrorStatus(String originalName,String errorStatusLastDigit,int timeout) throws InterruptedException{
		System.out.println("waiting for job of object with original name "+originalName+" to be in error status with digit "+errorStatusLastDigit);
		
		int waited_ms_total=0;
		while (true){
			waited_ms_total=updateTimeout(waited_ms_total,timeout,INTERVAL);
			
			Job job = getJob(originalName);

			if (job==null) {
				System.out.println("no job found in db");
				continue;
			}
	
			System.out.println("w/aiting for job to be ready ... "+job.getStatus());
			if (job.getStatus().endsWith(errorStatusLastDigit)){
				System.out.println(MSG_READY);
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
	Job waitForJobToBeInStatus(String originalName,String status) 
			throws InterruptedException{
		System.out.println("waiting for job of object with original name "+originalName+" to be in status "+status);
		
		int waited_ms_total=0;
		while (true){
			waited_ms_total=updateTimeout(waited_ms_total, TIMEOUT,INTERVAL);
	
			Job job = getJob(originalName);
			
			if (job!=null){
				System.out.println("waiting for job to be ready ... "+job.getStatus());
				
				if (job.getStatus().equals(status)){
					System.out.println(MSG_READY);
					return job;
				} else if (isInErrorState(job)) {
					String msg = "ERROR: Job in error state: " + job.getStatus();
					System.out.println(msg);
					
					throw new RuntimeException(msg);
				}
			}  
		}
	}

	/**
	 * Gets the job.
	 *
	 * @param orig_name the orig_name
	 * @param csn the csn
	 * @return the job
	 */
	@SuppressWarnings("unchecked")
	private Job getJob(Session session, String orig_name, String csn) {
		List<Job> l = null;
	
		try {
			l = session.createQuery(
					"SELECT j FROM Job j left join j.obj as o left join o.user as c where o.orig_name=?1 and c.short_name=?2"
					)
							.setParameter("1", orig_name)
							.setParameter("2", csn)
							.setReadOnly(true).list();
			
			return l.get(0);
		} catch (IndexOutOfBoundsException e) {
//			logger.debug("search for a job with orig_name " + orig_name + " for user " +
//						 csn + " returns null!");
			return null;
		}
	}
	
	
	private int updateTimeout(int waited_ms_total,int timeout, int interval){
		System.out.println("(total time: "+waited_ms_total+"ms / timeout: "+timeout+"ms) ");
		if (waited_ms_total>timeout) throw new RuntimeException(MSG_ERROR_WHEN_TIMEOUT_REACHED);
		try {
			Thread.sleep(interval);
		} catch (InterruptedException e) {} // no problem
		return waited_ms_total+=interval;
	}
	
	public Job getJob(String originalName) {
		Job job;
		Session session = HibernateUtil.openSession();
		session.beginTransaction();
		job = getJob(session, originalName, testContractor.getShort_name());
		session.close();
		return job;
	}
	
	
	void waitForObjectToBePublished(String origName) {
		int waited_ms_total=0;
		while (true) {
			waited_ms_total=updateTimeout(waited_ms_total,TIMEOUT,INTERVAL);
			
			Object object=fetchObjectFromDB(origName);
			if (object.getPublished_flag()>0) break;
		}
	}
	
	
	void waitForObjectToBeIndexed(MetadataIndex mi,String identifier) {
		int waited_ms_total=0;
		while (true) {
			waited_ms_total=updateTimeout(waited_ms_total,TIMEOUT,INTERVAL);
			
			if (mi.getIndexedMetadata("portal_ci_test", identifier).contains(identifier)) break;
		}
	}
	
	
	
	
	
	void awaitObjectState(String originalName,int awaitedState){
		int waited_ms_total=0;
		while (true){
			waited_ms_total=updateTimeout(waited_ms_total,TIMEOUT,INTERVAL);
			
			Object o = fetchObjectFromDB(originalName);
			if (o==null) continue;
			
			Job job = getJob(originalName);
			if (job!=null) {
				if (isInErrorState(job)) {
					String oid=job.getObject().getIdentifier();
					String msg = "ERROR: Job in error state: " + job.getStatus() + " in Object-Id "+ oid;
					System.out.println(msg);
					
					if (job.getObject().getIdentifier()!=null){
						try {
							System.out.println("SHOWING OBJECT LOG:");
							String localNodeWorkArea = localNode.getWorkAreaRootPath().toString();
							String localNode = localNodeWorkArea.replace("/storage/WorkArea", "");
							System.out.println(FileUtils.readFileToString(new File(Path.make(localNode, "ContentBroker","log", "object-logs")+"/"+job.getObject().getIdentifier()+".log")));
							System.out.println("END OF OBJECT LOG: "+job.getObject().getIdentifier());
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					throw new RuntimeException(msg);
				}
				System.out.println("Awaiting object state "+awaitedState+". Identifier: "+o.getIdentifier()+". Orig name: "+o.getOrig_name()+". Job state: "+job.getStatus());
			}
			
			
			System.out.println("Awaiting object state "+awaitedState+". Identifier: "+o.getIdentifier()+". Orig name: "+o.getOrig_name()+". Object state: "+o.getObject_state());
			if (o.getObject_state()==awaitedState) {
				return;
			}
			
		}
	}
	
	
	

	
	
	
	
	
	/**
	 * Makes a copy of a file from src/test/resources/at/[sourcePackagename].[ext]
	 * and puts it to the nodes IngestArea at ingestAreaRootPath/test/[originalName].[ext].
	 * 
	 * @param originalName
	 * @throws IOException 
	 */
	void putPackageToIngestArea(String sourcePackageName,String ext,String originalName) throws IOException {
		
		if (localNode==null) throw new IllegalStateException();
		if (localNode.getIngestAreaRootPath()==null) throw new IllegalStateException();
		
		File sourceFile = Path.makeFile(TEST_DATA_ROOT_PATH,sourcePackageName+"."+ext);
		File targetFile = Path.makeFile(localNode.getIngestAreaRootPath(),testContractor.getShort_name(),originalName+"."+ext);
		
		FileUtils.copyFile( sourceFile, targetFile );
	}
	
	

		

	
	
	
	



	/**
	 * @author Daniel M. de Oliveira
	 * @throws IOException 
	 */
	Object putPackageToStorage(String identifier,String originalName, Date createddate, int object_state) throws IOException{
		
		String PACKAGE_NAME = "1";
		int timeout = 2000;
		
		if (createddate==null) createddate = new Date();
		String urn =   URN_NBN_DE_DANRW+identifier;
		gridFacade.put(Path.makeFile(TC.TEST_ROOT_AT,identifier+".pack_"+PACKAGE_NAME+C.FILE_EXTENSION_TAR), 
				new RelativePath(C.TEST_USER_SHORT_NAME,identifier,identifier+".pack_"+PACKAGE_NAME+C.FILE_EXTENSION_TAR).toString(), sp);
		int i = 0;
		while (!gridFacade.storagePolicyAchieved(new RelativePath(C.TEST_USER_SHORT_NAME,identifier,identifier+".pack_"+PACKAGE_NAME+C.FILE_EXTENSION_TAR).toString(), sp)) {
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
		pkg.setName(PACKAGE_NAME);
		pkg.setContainerName(originalName+"."+C.FILE_EXTENSION_TGZ);
		
		Copy copy = new Copy();
		String md5=MD5Checksum.getMD5checksumForLocalFile(Path.makeFile(TC.TEST_ROOT_AT,identifier+".pack_"+PACKAGE_NAME+C.FILE_EXTENSION_TAR));
		pkg.setChecksum(md5);
		copy.setChecksum(md5);
		copy.setChecksumDate(new Date());
		for (Copy c:pkg.getCopies());
		pkg.getCopies().add(copy);
		object.getPackages().add(pkg);
		
		Session session = HibernateUtil.openSession();
		session.beginTransaction();
		session.save(object);
		session.getTransaction().commit();
		session.close();
		
		return object;
	}


	

	void createObjectAndJob(String name,String status) throws IOException{
		createObjectAndJob(name,status,null,null);
	}



	/**
		 * @throws IOException 
		 */
	void createObjectAndJob(String name, 
			String status,
			String packageType,
			String metadataFile) throws IOException{
		sp.setReplDestinations("ciArchiveResource");
		sp.setWorkingResource("ciWorkingResource");
		gridFacade.put(
				Path.makeFile(TEST_DATA_ROOT_PATH,name+".pack_1.tar"),
				testContractor.getShort_name()+"/ID-"+name+"/ID-"+name+".pack_1.tar",sp);
		
		
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
		Node node = (Node)session.load(Node.class, localNode .getId());
		job.setResponsibleNodeName(node.getName());
		job.setObject(object);
		job.setDate_created(String.valueOf(new Date().getTime()/1000L));
		job.setDate_modified(String.valueOf(new Date().getTime()/1000L));
		job.setStatus(status);
	
		session.save(job);
		
		session.getTransaction().commit();
		session.close();
	}



	private boolean isInErrorState(Job job){
		if (job.getStatus().endsWith(C.WORKFLOW_STATUS_DIGIT_ERROR_BAD_ROLLBACK) || 
				job.getStatus().endsWith(C.WORKFLOW_STATUS_DIGIT_ERROR_PROPERLY_HANDLED)
				|| job.getStatus().endsWith(C.WORKFLOW_STATUS_DIGIT_USER_ERROR)) return true;
		return false;
	}
}
