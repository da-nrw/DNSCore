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

package de.uzk.hki.da.core;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.MDC;

import de.uzk.hki.da.model.Job;
import de.uzk.hki.da.model.Node;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.model.User;
import de.uzk.hki.da.service.HibernateUtil;
import de.uzk.hki.da.utils.C;
import de.uzk.hki.da.utils.Path;


/**
 * Scans the staging area for new packages. If new files are there whose stay 
 * has been longer as specified in (minAge), they get moved to a destination area
 * and a job gets created for them.
 * 
 * <pre><code>
 *   Staging area may look like
 *   .../stagingRoot/TEST1
 *   .../stagingRoot/TEST2
 *   
 *   and destination area like
 *   .../destinationRoot/TEST1
 *   .../destinationRoot/TEST2
 * </code></pre>
 * A file located under .../stagingRoot/TEST1/a.txt would then be moved to .../destinationRoot/TEST1/a.txt
 * 
 * @author Daniel M. de Oliveira
 *
 */
public class IngestAreaScannerWorker extends Worker{

	
	/**
	 * To rule everything out except containers of valid formats.
	 */
	private class AcceptedContainerFormatsFilter implements FilenameFilter {

		/* (non-Javadoc)
		 * @see java.io.FilenameFilter#accept(java.io.File, java.lang.String)
		 */
		@Override
		public boolean accept(File dir, String name) {
			return (name.endsWith(".zip")
					||name.endsWith(".tar.gz")
					||name.endsWith(".tgz")
					||name.endsWith(".tar"));
		}
	}
	
	private class BagitScanner implements FilenameFilter {

		/* (non-Javadoc)
		 * @see java.io.FilenameFilter#accept(java.io.File, java.lang.String)
		 */
		@Override
		public boolean accept(File dir, String name) {
			return (name.indexOf("bagit")>=0 && name.endsWith(".txt"));
		}
	}
	/** The min age. */
	private long maxQueueCount;

	private int minAge; // required minimum age in milliseconds
	
	private Path ingestAreaRootPath;
	
	/** The local node name. */
	private String localNodeId;
	
	/** The register object service. */
	private RegisterObjectService registerObjectService;
	
	/** The files. */
	private Map<String, Map<String,Long>> sipsForIngestForCSN = new HashMap<String,Map<String,Long>>(); //CSN-String -> FileName-String -> Timestamp-Long
	
	/** The contractor short names. */
	private List<User> contractors = new ArrayList<User>();

	protected boolean isQueueFull;
	/**
	 * @return The contractors whose ingest folder will get scanned for files. 
	 */
	public Set<User> init(){
		if ((ingestAreaRootPath==null)
			||(ingestAreaRootPath.toString().isEmpty())) 
			throw new IllegalStateException("ingestAreaRootPath must not set");
		
		if (! ingestAreaRootPath.toFile().exists()) throw new RuntimeException("No file or directory: "+ingestAreaRootPath);
		
		logger.info("Scanning staging area for contractor folders");
		String children[] = ingestAreaRootPath.toFile().list();

		for (int i=0;i<children.length;i++){
			if (! Path.makeFile(ingestAreaRootPath,children[i]).isDirectory())
				continue;
			
			Session session = HibernateUtil.openSession();
			session.beginTransaction();
			User contractor = getContractor(session, children[i]);
			session.close();
			if (contractor==null) {
				logger.warn("Cannot find in ObjectDB: "+children[i]+" -  will not scan sips for contractor");
				continue;
			}else
				logger.info("Will scan sips for contractor: "+children[i]);
			
			contractors.add(contractor);
			if(!sipsForIngestForCSN.keySet().contains(contractor.getShort_name()))
				sipsForIngestForCSN.put(contractor.getShort_name(), new HashMap<String,Long>());
		}
		return new HashSet<User>(contractors);
	}
	
	@Override
	public void setMDC() {
		MDC.put(WORKER_ID, "ingest");
	}
	
	
	/**
	 * Checking for new files in the staging area.	
	 */
	@Override
	public void scheduleTaskImplementation(){

		class UserChild{
			public User contractor;
			public String child;
			;}

		//----------------------------------------------------
		try {
			
			Session session = HibernateUtil.openSession();
			Node node = (Node) session.get(Node.class, Integer.parseInt(this.localNodeId));
			String nodeName = node.getName();

			Query query = session.createQuery("select count(*) from Job j where " 
					+ "j.responsibleNodeName=:responsibleNodeName and status < :maxStatus");
			query.setParameter("responsibleNodeName", nodeName);
			query.setParameter("maxStatus", "950");
			@SuppressWarnings("unchecked")
			List<Long> countyList = query.list();
			Long county = countyList.get(0);
			session.close();
			
			if (county >= this.maxQueueCount) {
				if (!isQueueFull) {
					isQueueFull = true;
					logger.info("Found " + county + " jobs in queue. Limit is " + this.maxQueueCount + ". Wait for decrease.");
				}
				return;
			}
			isQueueFull = false;
			
			long freeEntries = this.maxQueueCount - county; 
			
			// ----------------------------------------------------

			List<UserChild> toIngest = new ArrayList<UserChild>();
		
			long currentTimeStamp = System.currentTimeMillis();
	
			for (User contractor:contractors){

				for (String child:scanContractorFolderForReadySips(contractor.getShort_name(), currentTimeStamp)){
					
					UserChild userChild = new UserChild();
					userChild.contractor = contractor;
					userChild.child = child;
					toIngest.add(userChild);
				}
			}

			Collections.shuffle(toIngest);
			for (UserChild userChild : toIngest) {
				User contractor = userChild.contractor;
				String child = userChild.child;

				Object object = null;
				try {
					object = registerObjectService.registerObject(child, contractor);
				} catch (UserException e) {
					logger.error("cannot register object " + child + " for contractor " + contractor
							+ ". Skip creating job for object.", e);
					continue;
				}

				String origName = convertMaskedSlashes(FilenameUtils.removeExtension(child));
				Job job = insertJobIntoQueueAndSetWorkFlowState(contractor, origName, localNodeId, object);
				logger.debug("Created new Object " + object + ":::: Created job: " + job);
				--freeEntries;
				if (freeEntries <= 0L) {
					break;
				}
			}
		}
		catch (Exception e){ // Should catch everything in scheduleTask. Otherwise thread dies without notice.
			logger.error("Caught: "+e,e);
		}
	}
	
	
	
	
	private List<String> scanContractorFolderForReadySips(String short_name,
			long currentTimeStamp) {
		List<String> childrenWhichAreReadyFiles = new ArrayList<String>();
		List<String> childrenWhichAreReadyDirs = new ArrayList<String>();
		List<String> childrenWhichAreReadySips = new ArrayList<String>();		
		childrenWhichAreReadyFiles = scanContractorFolderForReadyFiles(short_name, currentTimeStamp);
		childrenWhichAreReadyDirs = scanContractorFolderForReadyDirs(short_name, currentTimeStamp);
		if (childrenWhichAreReadyFiles!=null)
		childrenWhichAreReadySips.addAll(childrenWhichAreReadyFiles);
		if (childrenWhichAreReadyDirs!=null)
		childrenWhichAreReadySips.addAll(childrenWhichAreReadyDirs);
		return childrenWhichAreReadySips;
	}

	/**
	 * Insert job into queue.
	 *
	 * @param contractorShortName the contractor short name
	 * @param origName the orig name
	 * @param responsibleNodeName the initial node name
	 * @return the job
	 */
	private Job insertJobIntoQueueAndSetWorkFlowState(User c,String origName,String responsibleNodeId,Object object){

		object.setObject_state(Object.ObjectStatus.InWorkflow);
		
		Session session = HibernateUtil.openSession();
		session.beginTransaction();
		session.saveOrUpdate(object);
		
		Node node = (Node) session.get(Node.class,Integer.parseInt(responsibleNodeId));
		
		Job job = new Job();
		job.setObject(object);
		
		job.setStatus(C.WORKFLOW_STATUS_START___INGEST_UNPACK_ACTION);
		job.setResponsibleNodeName(node.getName());
		job.setCreatedAt(new Date());
		job.setModifiedAt(new Date());
	
		session.save(job);
		session.getTransaction().commit();
		session.close();

		return job;
	}
	
	
	
	
	/**
	 * Gets the contractor.
	 *
	 * @param contractorShortName the contractor short name
	 * @return null if no contractor for short name could be found
	 */
	private User getContractor(Session session, String contractorShortName) {
		@SuppressWarnings("rawtypes")
		List list;	
		list = session.createQuery("from User where short_name=?1")
	
				.setParameter("1",contractorShortName).setReadOnly(true).list();
		
		if (list.isEmpty())
			return null;
	
		return (User) list.get(0);
	}
	
	
	
	
	
	/**
	 * Tells us which files are inside a contractor specific stage folder are ready to move.
	 * Two conditions must be met in order two be moved
	 * <li>The file must be old enough
	 * <li>The file must pass the AcceptedContainerFormatsFilter
	 *
	 * @param contractorShortName the contractor short name
	 * @param currentTimeStamp the current time stamp
	 * @return list of files which meet the above mentioned conditions.
	 */
	private List<String> scanContractorFolderForReadyFiles(String contractorShortName,long currentTimeStamp){
		
		String children[] = Path.makeFile(ingestAreaRootPath,contractorShortName).list(new AcceptedContainerFormatsFilter());
		List<String> childrenWhichAreReady = new ArrayList<String>();
		if (children!=null){
			for (int i=0;i<children.length;i++){
				if (Path.makeFile(ingestAreaRootPath,contractorShortName,children[i]).isFile())
					childrenWhichAreReady = addToList(children[i],contractorShortName,currentTimeStamp,childrenWhichAreReady);
			} 
		}
		return childrenWhichAreReady;
	} 
	
	/** Allowing uncompressed Bagits
	 * @author jens peters, 2016
	 */
	private List<String> scanContractorFolderForReadyDirs(String contractorShortName,long currentTimeStamp){
		
		String children[] = Path.makeFile(ingestAreaRootPath,contractorShortName).list();
		List<String> childrenWhichAreReady = new ArrayList<String>();
		if (children!=null) {
			for (int i=0;i<children.length;i++){
				
				if (!Path.makeFile(ingestAreaRootPath,contractorShortName,children[i]).isDirectory()) continue;
				String bags[] = Path.makeFile(ingestAreaRootPath,contractorShortName,children[i]).list(new BagitScanner());
				if (bags!=null&&bags.length>0) {
					// we don't check for bagit consistency while UnpackAction does this and we'll get those 
					// packs by move operation
					// logger.debug("found directory " + children[i] + " which may contain unpacked bagit");	
					childrenWhichAreReady = addToList(children[i], contractorShortName, currentTimeStamp,childrenWhichAreReady);
				} 
			}
		}
		return childrenWhichAreReady;
	} 
	
		private List<String> addToList(String sipname,  String csn, long currentTimeStamp,List<String> childrenWhichAreReady) {
			if (!sipsForIngestForCSN.get(csn).containsKey(sipname)){
				Job job = getJob( convertMaskedSlashes(FilenameUtils.removeExtension(sipname)),
						csn);
				if ( job == null) { // consider only containers for which there is not already a job in queue since it is possible that the CB has stopped and now resumes work.
					sipsForIngestForCSN.get(csn).put(sipname, currentTimeStamp);
				} else {
//					USER EMAIL 
				}
			}
			else
			{
				long diff = currentTimeStamp - sipsForIngestForCSN.get(csn).get(sipname);
				if (diff>minAge){
					sipsForIngestForCSN.get(csn).remove(sipname);
					childrenWhichAreReady.add(sipname);
				}
				}
			return childrenWhichAreReady;
		}
	/**
	 * Gets the job.
	 *
	 * @param orig_name the orig_name
	 * @param csn the csn
	 * @return the job
	 */
	@SuppressWarnings("unchecked")
	private Job getJob(String orig_name, String csn) {
		Session session = HibernateUtil.openSession();
		session.beginTransaction();
		List<Job> l = null;
	
			l = session.createQuery(
					"SELECT j FROM Job j left join j.obj as o left join o.user as c where o.orig_name=?1 and c.short_name=?2"
					)
							.setParameter("1", orig_name)
							.setParameter("2", csn)
							.setReadOnly(true).list();
		session.close();

		try {
			return l.get(0);
		} catch (IndexOutOfBoundsException e) {
			return null;
		}
	}
	
	// TODO factor out
	/**
	 * Replaces %2F inside a string to /.
	 *
	 * @param input the input
	 * @return the string
	 */
	String convertMaskedSlashes(String input){
		return input.replaceAll("%2F", "/");
	} 
	
	public Path getIngestAreaRootPath() {
		return ingestAreaRootPath;
	}

	public void setIngestAreaRootPath(Path ingestAreaRootPath) {
		this.ingestAreaRootPath = ingestAreaRootPath;
	}
	
	public long getMaxQueueCount() {
		return maxQueueCount;
	}

	public void setMaxQueueCount(long maxQueueCount) {
		this.maxQueueCount = maxQueueCount;
	}

	public void setMinAge(int minAge){
		this.minAge = minAge;
	}
	
	public int getMinAge(){
		return minAge;
	}

	public String getLocalNodeId() {
		return localNodeId;
	}

	public void setLocalNodeId(String localNodeId) {
		this.localNodeId = localNodeId;
	}

	public RegisterObjectService getRegisterObjectService() {
		return registerObjectService;
	}

	public void setRegisterObjectService(RegisterObjectService registerObjectService) {
		this.registerObjectService = registerObjectService;
	}
}
