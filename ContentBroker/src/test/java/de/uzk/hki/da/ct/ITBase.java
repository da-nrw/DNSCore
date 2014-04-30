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

package de.uzk.hki.da.ct;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.hibernate.classic.Session;
import org.springframework.context.support.AbstractXmlApplicationContext;

import de.uzk.hki.da.cb.AbstractAction;
import de.uzk.hki.da.core.ActionCommunicatorService;
import de.uzk.hki.da.core.ActionRegistry;
import de.uzk.hki.da.core.HibernateUtil;
import de.uzk.hki.da.grid.IrodsSystemConnector;
import de.uzk.hki.da.model.CentralDatabaseDAO;
import de.uzk.hki.da.model.ConversionInstruction;
import de.uzk.hki.da.model.DAFile;
import de.uzk.hki.da.model.DAOException;
import de.uzk.hki.da.model.Event;
import de.uzk.hki.da.model.Job;
import de.uzk.hki.da.model.Node;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.model.Package;
import de.uzk.hki.da.service.UserExceptionManager;
import de.uzk.hki.da.utils.CommandLineConnector;
import de.uzk.hki.da.utils.ProcessInformation;


/**
 * Common base of all integration tests.
 * @author Daniel M. de Oliveira
 * @author Thomas Kleinke
 *
 */
public class ITBase {

	/** The name of our integration test node. */
	protected static String nameOfOurIntegrationTestNode = "da-nrw-vm3.hki.uni-koeln.de";
	
	/** The cache resource vault path. */
	protected static String cacheResourceVaultPath = "/data/danrw/storage/fs/";
	
	/** The dip area root path. */
	protected static String dipAreaRootPath = "/data/danrw/storage/fs/dip/";
	
	/** The work area root path. */
	protected static String workAreaRootPath = "/data/danrw/storage/fs/fork/";
	
	/** The aip resource vault path. */
	protected static String aipResourceVaultPath = "/data/danrw/storage/sam-fs/";
	
	/** The transfer area root path. */
	protected static String transferAreaRootPath ="/data/danrw/www/default/webdav/";
	
	/** The zone path. */
	protected static String zonePath = "/da-nrw/";
	
	/** The irods system connector. */
	protected static IrodsSystemConnector irodsSystemConnector;

	protected static CentralDatabaseDAO dao = new CentralDatabaseDAO();

	protected static ActionCommunicatorService acs = new ActionCommunicatorService();
	
	protected static UserExceptionManager uem = new UserExceptionManager();
	
	/** The node. */
	protected static Node node = null;
	
	/** The hibernate config file path. */
	protected static String hibernateConfigFilePath = "conf/hibernateCentralDB.cfg.xml";
	
	/** The node admin email. */
	protected static String nodeAdminEmail = "da-nrw-notifier@uni-koeln.de";
	
	/** The context. */
	static AbstractXmlApplicationContext context;
	
	public ITBase() {
		try {
			uem.readConfigFile();
		} catch (IOException e) {
			throw new RuntimeException("Failed to read user exception manager config file", e);
		}
	}	
	
	/**
	 * Retrieves Object from the Object Table for a given object identifier.
	 *
	 * @param objectIdentifier the object identifier
	 * @param csn the csn
	 * @return Object object or null if no object with the given combination of object identifier and
	 * contractor short name could be found
	 * @author Thomas Kleinke
	 */
	public Object getUniqueObjectForObjectIdentifier(String objectIdentifier) {
		Session session = HibernateUtil.openSession();
		session.beginTransaction();
		
		@SuppressWarnings("rawtypes")
		List l = null;
	
		try {
			l = session.createQuery("from Object where identifier=?1")
							.setParameter("1", objectIdentifier)
							.list();

			if (l.size() > 1)
				throw new RuntimeException("Found more than one object with object identifier " + objectIdentifier);
			
			Object o = (Object) l.get(0);
			session.close();
			return o;
		} catch (IndexOutOfBoundsException e) {
			System.out.println("search for an object with object identifier " + objectIdentifier + " returns null!");
			session.close();
			return null;
		}
	
	}
	
	
	/**
	 * Sets the up node.
	 */
	protected static void setUpNode(){
		
		Session session = HibernateUtil.openSession();
		session.getTransaction().begin();
		node = (Node) session.get(Node.class,131614);		
		session.close();
		
		if (node==null) { fail("node is null"); }
		node.setWorkAreaRootPath(workAreaRootPath);
		node.setUserAreaRootPath("/data/danrw/www/default/webdav/");
		node.setIngestAreaRootPath("/data/danrw/ingest/");
		node.setGridCacheAreaRootPath("/data/danrw/storage/fs/");
		node.setWorkingResource("01-da-nrw-vm3.hki.uni-koeln.de");
		node.setAdminEmail("da-nrw-notifier@uni-koeln.de");
	}
	
	
	/**
	 * Iput package.
	 *
	 * @param testPackagePath absolute or relative path to the package to put into the /da-nrw/home/TEST-Collection
	 * @param destCollection the dest collection
	 * @author Daniel M. de Oliveira
	 */
	protected void iputPackage(String testPackagePath, String destCollection) {
		
		ProcessInformation pi = CommandLineConnector.runCmdSynchronously(new String[] {
				"/data/danrw/iRODS/clients/icommands/bin/iput","-f", testPackagePath, 
				destCollection }, 
				null);
		if ((pi==null)||(pi.getExitValue()!=0)){
			throw new RuntimeException("iput failed! pi: " + pi + 
					"\nstdOut: " + pi.getStdOut() + 
					"\nstdErr: " + pi.getStdErr());
		}
	}
	
	/**
	 * Setup sys connector.
	 */
	protected void setupSysConnector(){
		irodsSystemConnector = new IrodsSystemConnector(
				"rods", "WpXlLLg3a4/S/iYrs6UhtQ==", nameOfOurIntegrationTestNode, "da-nrw", 
				"01-da-nrw-vm3.hki.uni-koeln.de");
		irodsSystemConnector.connect();
	}
	
	
	
	/**
	 * Fetches a Job named integration test.
	 * Tests if the a queue named integration test is in an error state (i.e. ends with 1).
	 * Returns error in that case.
	 *
	 * @param status the status
	 * @param workingStatus the working status
	 * @param node the node
	 * @return null if not found.
	 * @author Daniel M. de Oliveira
	 */
	@SuppressWarnings("unused")
	protected Job specializedFetchJobFromQueue (String status, String workingStatus, Node node) {
		Session session = HibernateUtil.openSession();
		session.beginTransaction();
		//System.out.println(ITstartcb.class.getName()+".specializedFetchJobFromQueue() got session from Hibernate: "+session.hashCode());

		@SuppressWarnings("rawtypes")
		List joblist=null;
		try{
			joblist = session
					.createQuery(
							"SELECT j from Job j inner join j.obj as obj where "+
							" obj.orig_name like ?1 and j.status=?2 and j.initial_node=?3 order by j.date_modified asc")
					.setParameter("1", "integrationTest%")
					.setParameter("2", status)
					.setParameter("3", node.getName()).setCacheable(false)
					.setMaxResults(1).list();
		
			if ((joblist == null) || (joblist.isEmpty())){
				session.close();
				return null;
			}
			
			Job job = (Job) joblist.get(0);
			for (ConversionInstruction ci:job.getConversion_instructions()){}
			for (Job j:job.getChildren()){}
			for (Package p:job.getObject().getPackages()){
				for (DAFile f:p.getFiles()){}
				for (Event e:p.getEvents()){}
			}
			job.setStatus(workingStatus);
			job.setDate_modified(String.valueOf(new Date().getTime()/1000L));
			session.merge(job);
			session.getTransaction().commit();
			session.close();

		}catch(Exception e){
			session.close();
			throw new DAOException(e.getMessage(), e);
		}
		return (Job) joblist.get(0);
	}


	
	/**
	 * Detects if the queue contains jobs with orig_name integrationTest which are in
	 * error states (i.e. end with 1).
	 *
	 * @param node the node
	 * @return true if jobs in error states were found.
	 * @author Daniel M. de Oliveira
	 */
	protected boolean existITJobsInErrorState (Node node) {
		Session session = HibernateUtil.openSession();
		session.beginTransaction();
		System.out.println(ITUseCaseIngest.class.getName()+".findITJobsInError() "+" got session from Hibernate: "+session.hashCode());
		
		@SuppressWarnings("rawtypes")
		List joblist=null;

		joblist = session
					.createQuery(
							"SELECT j from Job j inner join j.obj as obj where obj.orig_name=?1 and j.status like '?1%'")
					.setParameter("1", "integrationTest")
					.list();
			
		if (joblist.isEmpty()) {
			session.close();
			System.out.println("there are no it jobs in error state");
			return false;
		}
		session.close();
		return true;
	}
	
	
	
	/**
	 * Connect and run action.
	 *
	 * @param name the name
	 * @return the job
	 * @author Daniel M. de Oliveira
	 * @author Thomas Kleinke
	 */
	protected Job connectAndRunAction(String name){
		
//		session.beginTransaction();
		
		Job jobCandidate = null;
		try{
			System.out.println("Running: "+name);
			System.out.println("Testing for errors");
			if (existITJobsInErrorState(node)) throw new RuntimeException("Error in IT");
			
			System.out.println("xxx - trying to fetch job");
			AbstractAction action = (AbstractAction) context.getBean(name);

			
			while (jobCandidate == null){
				String workingStatus = action.getStartStatus().substring(0,action.getStartStatus().length()-1) + "2";
				jobCandidate = specializedFetchJobFromQueue(
					action.getStartStatus(), 
					workingStatus, node);
			}
			
			System.out.println("fetched job: "+jobCandidate);
			System.out.println("object inside job looks like: "+jobCandidate.getObject());
			
			action.setINTEGRATIONTEST(true);
			action.setDao(dao);
			action.setActionCommunicatorService(acs);
			action.setUserExceptionManager(uem);
			action.setNode(node);
			action.setIrodsZonePath(zonePath);
			ActionRegistry map = (ActionRegistry) context.getBean("actionRegistry");
			map.registerAction(action);
			action.setActionMap(map);
			action.setJob(jobCandidate);
			action.setObject(jobCandidate.getObject());
			jobCandidate.getObject().setTransientNodeRef(node);
			action.run();
			
//			System.out.println(ITBase.class.getName()+" got session from Hibernate: "+HibernateUtil.getThreadBoundSession().hashCode());
			if (jobCandidate.getStatus().endsWith("1"))
				throw new RuntimeException("job ended in error state");
		
		}catch(Exception e){
			
			System.out.println(e.getMessage());
			throw new RuntimeException("Error while executing action: Error state "+jobCandidate.getStatus(), e);
		}
		return jobCandidate;
		
	}
	
	
	/**
	 * File exists logically and physically.
	 *
	 * @param relativePackagePath the relative package path
	 * @return true, if successful
	 */
	protected boolean fileExistsLogicallyAndPhysically(String relativePackagePath){
		return ((new File(cacheResourceVaultPath + relativePackagePath).exists())
				&&(irodsSystemConnector.fileExists("/da-nrw/" + relativePackagePath)));
	}
	
	/**
	 * File exists logically or physically.
	 *
	 * @param relativePackagePath the relative package path
	 * @return true, if successful
	 */
	protected boolean fileExistsLogicallyOrPhysically(String relativePackagePath){
		return ((new File(cacheResourceVaultPath + relativePackagePath).exists())
				||(irodsSystemConnector.fileExists("/da-nrw/" + relativePackagePath)));
	}
	
	/**
	 * Collection exists logically or physically.
	 *
	 * @param relativePackagePath the relative package path
	 * @return true, if successful
	 */
	protected boolean collectionExistsLogicallyOrPhysically(String relativePackagePath){
		return ((new File(cacheResourceVaultPath + relativePackagePath).exists())
				||(irodsSystemConnector.collectionExists("/da-nrw/" + relativePackagePath)));
	}
	
}
