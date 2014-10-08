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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uzk.hki.da.model.Job;
import de.uzk.hki.da.model.Node;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.model.User;
import de.uzk.hki.da.utils.Utilities;


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
public class IngestAreaScannerWorker {

	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(IngestAreaScannerWorker.class);
	
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
	
	/** The min age. */
	private int minAge; // required minimum age in milliseconds
	
	/** The ingest area root path. */
	private String ingestAreaRootPath;
	
	/** The local node name. */
	private String localNodeId;
	
	/** The register object service. */
	private RegisterObjectService registerObjectService;
	
	/** The root dir. */
	File rootDir = null; 
	
	/** The files. */
	private Map<String,Long> files = new HashMap<String,Long>();
	
	/** The contractor short names. */
	private List<String> contractorShortNames = new ArrayList<String>();

	/**
	 * Inits the.
	 */
	public void init(){
		if (!rootDir.exists()) throw new RuntimeException("path ingestAreaRootPath points to does not exist.");
		
		logger.info("Scanning staging area for contractor folders");
		String children[] = rootDir.list();

		for (int i=0;i<children.length;i++){
		
			logger.info(children[i]);
			contractorShortNames.add(children[i]);
		}
	}
	
	/**
	 * Checking for new files in the staging area.	
	 */
	public void scheduleTask(){
		
		try {
		
			long currentTimeStamp = System.currentTimeMillis();
			
			for (String contractorShortName:contractorShortNames){
			
				Session session = HibernateUtil.openSession();
				session.beginTransaction();
				User contractor = getContractor(session, contractorShortName);
				session.close();
				
				
				logger.debug("Scanning folder of contractor: "+contractorShortName);
				for (String child:scanContractorFolderForReadyFiles(contractorShortName, currentTimeStamp)){
					
					logger.info("Found file \""+child+"\" in ingest Area. Creating job for \""+contractorShortName+"\"");
					Object object = registerObjectService.registerObject( child, contractor);	

					logger.debug("Created new Object with id: "+object.getData_pk());
					
					Session session2 = HibernateUtil.openSession();
					session2.beginTransaction();
					insertJobIntoQueue(
							session2,
							contractor, 
							convertMaskedSlashes(FilenameUtils.removeExtension(child)),
							localNodeId,
							object);
					session2.getTransaction().commit();
					session2.close();
					
				}
			}
		}
		catch (Exception e){ // Should catch everything in scheduleTask. Otherwise thread dies without notice.
			logger.error("Caught: "+e,e);
		}
			
	}
	
	/**
	 * Insert job into queue.
	 *
	 * @param contractorShortName the contractor short name
	 * @param origName the orig name
	 * @param responsibleNodeName the initial node name
	 * @return the job
	 */
	private Job insertJobIntoQueue(Session session, User c,String origName,String responsibleNodeId,Object object){

		Node node = (Node) session.get(Node.class,Integer.parseInt(responsibleNodeId));
		
		Job job = new Job();
		job.setObject(object);
		
		job.setStatus("110");
		job.setResponsibleNodeName(node.getName());
		job.setDate_created(String.valueOf(new Date().getTime()/1000L));
	
		session.save(job);
		return job;
	}
	
	
	/**
	 * Gets the contractor.
	 *
	 * @param contractorShortName the contractor short name
	 * @return null if no contractor for short name could be found
	 */
	private User getContractor(Session session, String contractorShortName) {
		logger.trace("CentralDatabaseDAO.getContractor(\"" + contractorShortName + "\")");
	
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
		
		String children[] = new File(ingestAreaRootPath+contractorShortName).list(new AcceptedContainerFormatsFilter());
		List<String> childrenWhichAreReady = new ArrayList<String>();
		
		for (int i=0;i<children.length;i++){
			
			if (!files.containsKey(children[i])){

				Session session = HibernateUtil.openSession();
				session.beginTransaction();
				if (getJob(session, convertMaskedSlashes(FilenameUtils.removeExtension(children[i])),
						contractorShortName) == null) {				
					logger.debug("New file found, making timestamp for: "+children[i]);
					files.put(children[i], currentTimeStamp);
				}
				session.close();
			}
			else
			{
				long diff = currentTimeStamp - files.get(children[i]);
				logger.debug("Old file found, lets look how their timestamps differ: "+children[i]+" diff: "+diff);
				
				if (diff>minAge){
					logger.debug("File "+children[i]+" which has lasted "+minAge+" miliseconds is ready.");
					files.remove(children[i]);
					childrenWhichAreReady.add(children[i]);
				}
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
			logger.debug("search for a job with orig_name " + orig_name + " for user " +
						 csn + " returns null!");
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
	
	/**
	 * The rootFolderPath is an absolute path on the local file system which contains
	 * the contractors staging folders. These folders will be scanned for new files.
	 *
	 * @return the ingest area root path
	 */
	public String getIngestAreaRootPath() {
		return ingestAreaRootPath;
	}

	/**
	 * Sets the ingest area root path.
	 *
	 * @param ingestAreaRootPath the new ingest area root path
	 */
	public void setIngestAreaRootPath(String ingestAreaRootPath) {
		ingestAreaRootPath = Utilities.slashize(ingestAreaRootPath);
		
		this.ingestAreaRootPath = ingestAreaRootPath;
		rootDir = new File(ingestAreaRootPath);
	}
	
	/**
	 * Sets the min age.
	 *
	 * @param minAge the new min age
	 */
	public void setMinAge(int minAge){
		this.minAge = minAge;
	}
	
	/**
	 * Gets the min age.
	 *
	 * @return the min age
	 */
	public int getMinAge(){
		return minAge;
	}

	/**
	 * Gets the local node name.
	 *
	 * @return the local node name
	 */
	public String getLocalNodeId() {
		return localNodeId;
	}

	/**
	 * Sets the local node name.
	 *
	 * @param localNodeName the new local node name
	 */
	public void setLocalNodeId(String localNodeId) {
		this.localNodeId = localNodeId;
	}

	/**
	 * Gets the register object service.
	 *
	 * @return the register object service
	 */
	public RegisterObjectService getRegisterObjectService() {
		return registerObjectService;
	}

	/**
	 * Sets the register object service.
	 *
	 * @param registerObjectService the new register object service
	 */
	public void setRegisterObjectService(RegisterObjectService registerObjectService) {
		this.registerObjectService = registerObjectService;
	}

}
