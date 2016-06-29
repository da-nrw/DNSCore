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

package de.uzk.hki.da.action;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;

import javax.xml.parsers.ParserConfigurationException;

import org.hibernate.Session;
import org.jdom.JDOMException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.xml.sax.SAXException;

import de.uzk.hki.da.core.MailContents;
import de.uzk.hki.da.core.SubsystemNotAvailableException;
import de.uzk.hki.da.core.UserException;
import de.uzk.hki.da.core.UserExceptionManager;
import de.uzk.hki.da.format.UserFileFormatException;
import de.uzk.hki.da.model.Copy;
import de.uzk.hki.da.model.Job;
import de.uzk.hki.da.model.Node;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.model.PreservationSystem;
import de.uzk.hki.da.model.WorkArea;
import de.uzk.hki.da.repository.RepositoryException;
import de.uzk.hki.da.service.HibernateUtil;
import de.uzk.hki.da.service.JmsMessage;
import de.uzk.hki.da.service.JmsMessageServiceHandler;
import de.uzk.hki.da.util.TimeStampLogging;
import de.uzk.hki.da.utils.C;
import de.uzk.hki.da.utils.StringUtilities;


/**
 * Actions should get extended to execute business code. 
 * Business code should be placed into the implementation method.
 * After performing the implementation, the database gets updated according to the changes 
 * made to the model (object,job) during implementation and according to the behaviour specified through the modifiers.
 * <br>
 * <br> 
 * Template method.
 * 
 * @author Daniel M. de Oliveira
 * @author Thomas Kleinke
 * @author Sebastian Cuy
 * @author Jens Peters 
 */
public abstract class AbstractAction implements Runnable {	
	
	// behaviour modifier
	private boolean rOLLBACKONLY = false;
	private boolean kILLATEXIT = false;
	protected boolean SUPPRESS_OBJECT_CONSISTENCY_CHECK = false;
	protected boolean DELETEOBJECT = false;
	protected Job toCreate = null;
	
	
	private ActionFactory actionFactory;
	protected ActionRegistry actionMap;
	private String name;
	protected String startStatus;
	protected Job j;
	protected Object o;
	protected String endStatus;
	protected String description;
	private UserExceptionManager userExceptionManager;
	
	private JmsMessageServiceHandler jmsMessageServiceHandler;

	
	protected Node n;                        // Implementations should never alter the state of this object to ensure thread safety
	protected PreservationSystem preservationSystem; // Implementations should never alter the state of this object to ensure thread safety

	
	
	
	protected int concurrentJobs = 3;
	
	
	
	public AbstractAction(){}
	
	
	protected Logger logger = LoggerFactory.getLogger( this.getClass().getName() );
	private Logger baseLogger = LoggerFactory.getLogger("de.uzk.hki.da.action.AbstractAction"); // contentbrokerlog
	protected WorkArea wa;
	
	public abstract void checkConfiguration();
	
	public abstract void checkPreconditions();
	
	/**
	 * 
	 * Implementations should place business logic here.
	 * 
	 * @throws RepositoryException 
	 * @throws SAXException 
	 * @throws ParserConfigurationException 
	 * @throws JDOMException 
	 * 
	 * @return <i>false</i> if the business code decides that the action needs to be re-executed from the start state later
	 * <br><i>true</i> if business code has been successfully executed. 
	 */
	public abstract boolean implementation() 
			throws FileNotFoundException, IOException, UserException, RepositoryException, JDOMException, 
			ParserConfigurationException, SAXException, SubsystemNotAvailableException;

	/**
	 * Implementations which fail (due to exceptions in implementation() which will be caught in run())
	 * should clean up and set back the file system etc. to the initial state at the beginning of the action.
	 * @throws Exception 
	 */
	public abstract void rollback() throws Exception;
	
	
	@Override
	public void run() {
		
		setupObjectLogging(o.getIdentifier());
		synchronizeObjectDatabaseAndFileSystemState();
		
		Date start = new Date();
		executeConcreteAction();
		Date stop = new Date();
		long duration = stop.getTime()-start.getTime(); // in milliseconds
		new TimeStampLogging().log(o.getIdentifier(), this.getClass().getName(), duration);
		
		// The order of the next two statements must not be changed.
		// The object logging must be> unset in order to prevent another appender to start
		// its lifecycle before the current one has stop its lifecycle.
		
		unsetObjectLogging(); 
		try {
			upateObjectAndJob(n, o, j, DELETEOBJECT, kILLATEXIT, getToCreate());
		} catch (Exception e) {
			resetModifiers();
			execAndPostProcessRollback(o, j);
			try {
				upateObjectAndJob(n, o, j, DELETEOBJECT, kILLATEXIT, getToCreate());
			} catch (Exception e1) {
				baseLogger.error("Exception while committing changes to database after rollback "+e1);
			}
		}
		actionMap.deregisterAction(this); 
	}

	
	private void executeConcreteAction() {

		try {
			
			if (!rOLLBACKONLY) {
				execAndPostProcessImplementation();
				return;
			}
			
		} catch (UserFileFormatException e) {
			j.setQuestion(e.getKnownError().getQuestion());
			new MailContents(preservationSystem,n).informUserAboutPendingDecision(o,e.getMessage());
			
			updateStatus(C.WORKFLOW_STATUS_DIGIT_USER_ERROR);
			resetModifiers();
			return;	
		 } catch (UserException e) {
			reportUserError(e);
			updateStatus(C.WORKFLOW_STATUS_DIGIT_USER_ERROR);
			resetModifiers();
			return;
			
		}catch (SubsystemNotAvailableException e) {
			
			actionFactory.setOnHalt(true,e.getMessage());
			reportTechnicalError(e);
		
		} catch (Exception e) {
			reportTechnicalError(e);

		} finally {
			logger.info(ch.qos.logback.classic.ClassicConstants.FINALIZE_SESSION_MARKER, "Finalize logger session.");
		}
		
		resetModifiers();
		execAndPostProcessRollback(o,j);
	}
	
	
	private void updateStatus(String endDigit) {
		j.setDate_modified(String.valueOf(new Date().getTime()/1000L));
		j.setStatus(getStartStatus().substring(0, getStartStatus().length() - 1) + endDigit);
	}
	
	
	
	private void resetModifiers(){
		DELETEOBJECT=false;
		kILLATEXIT=false;
		toCreate=null;
	}
	
	
	

	/**
	 * Execute the business code implementation.
	 * Adjust job properties depending of implementation outcome.
	 * Adjust modifiers depending of implementation outcome. 
	 * @throws SubsystemNotAvailableException 
	 * @throws UserException 
	 */
	private void execAndPostProcessImplementation() throws FileNotFoundException,
			IOException, RepositoryException, JDOMException,
			ParserConfigurationException, SAXException, UserException, SubsystemNotAvailableException {
		
		baseLogger.info("Stubbing implementation of "+this.getClass().getName());
		
		if (!implementation()){				
			baseLogger.info(this.getClass().getName()+": implementation returned false. Setting job back to start state ("+startStatus+").");  
			j.setStatus(startStatus);
			resetModifiers();
		} else {
			j.setDate_modified(String.valueOf(new Date().getTime()/1000L));
			baseLogger.info(this.getClass().getName()+" finished working on job: "+j.getId()+". Now commiting changes to database.");
			if (kILLATEXIT)	{
				baseLogger.info("Set the job status to the end status "+endStatus+" .");
				logger.info(ch.qos.logback.classic.ClassicConstants.FINALIZE_SESSION_MARKER, "Finalize logger session.");
			} else {
				j.setStatus(endStatus);	
			}
		}
	}
	
	
	
	private void execAndPostProcessRollback(Object object,Job job) {
		
		String errorStatusEndDigit=C.WORKFLOW_STATUS_DIGIT_ERROR_PROPERLY_HANDLED;
		if (rOLLBACKONLY) 
			errorStatusEndDigit=C.WORKFLOW_STATUS_DIGIT_WAITING; // override
		
		baseLogger.info("Stubbing rollback of "+this.getClass().getName());
		try {
			logger.info("Stubbing rollback.");
			rollback();
			logger.info("Finishing rollback.");
		} catch (Exception e) {
			logger.error("@Admin: SEVERE ERROR WHILE TRYING TO ROLLBACK ACTION. DATABASE OR WORKAREA MIGHT BE INCONSISTENT NOW.");
			logger.error(this.getClass().getName()+": couldn't get rollbacked to previous state. Exception in action.rollback(): ",e);
			errorStatusEndDigit = C.WORKFLOW_STATUS_DIGIT_ERROR_BAD_ROLLBACK;
		}
		updateStatus(errorStatusEndDigit);
	}

	
	
	/**
	 * Perform the database transaction to synchronize the updates of job and object 
	 * (which happened during {@link #implementation()}) to the database. 
	 * <br>
	 * In case of connection related failures retries it until it succeeds.
	 */
	private void upateObjectAndJob(
			Node node,
			Object object,Job job, 
			boolean deleteObject,boolean deleteJob,
			Job createJob) throws Exception{
		
		boolean transactionSuccessful=false;
		int count = 0;
		int maxPermittedTryCount = 3;
		do {
			Session session = null;
			try {
				baseLogger.info("perform transaction with object="+object.getOrig_name()+", "
						+ "job="+job.getStatus()+", node="+node+"deleteObject="+deleteObject+", deleteJob="+deleteJob+", "+"CreateJob="+createJob);
				session = openSession();
				session.beginTransaction();
				performTransaction(node, object, job, deleteObject, deleteJob, createJob, session);
				transactionSuccessful=true;
				baseLogger.info("Transaction successful for object "+object.getIdentifier());
				session.close();
			}
			catch (Exception sqlException) {
				count++;
				baseLogger.error(this.getClass().getName()+": Exception while committing changes to database after action: ",sqlException);
				baseLogger.error(count+". try");
				session.getTransaction().rollback();
				session.close();
				if(count==maxPermittedTryCount) {
					reportTechnicalError(sqlException);
					throw new Exception(sqlException);
				}
			}
		} while(! (transactionSuccessful || count>=maxPermittedTryCount ));
	}
	
	private void performTransaction(
			Node node,
			Object object,Job job, 
			boolean deleteObject,boolean deleteJob,
			Job createJob, Session session){
		
		// This should only happen only once per cooperating node in an ingest workflow. 
		for (Node cn:node.getCooperatingNodes()) { 

			if (cn.getCopyToSave()==null) continue;
			if(!cn.getCopyToSave().getPath().contains(object.getIdentifier())) {
				logger.debug("Avoided copy saving for object with identifier "+object.getIdentifier());
				continue;
			}

			try {
				// we know that these are only the temporary copies of the current action.
			
				Copy copy = cn.getCopyToSave();
				logger.debug("Try to save copy with path "+copy.getPath());
				
				session.save(copy);
				session.flush();
				
				baseLogger.info("Added copy for objects ("+object.getIdentifier()+") last package. Copy path: "+copy.getPath()+". Copy is on node with name: "+cn.getName()+" and has id "+copy.getId()+".");
				
				int updatesNodeId=session.createSQLQuery(
						"UPDATE copies SET node_id="+cn.getId()+", "
								+ "pkg_id="+object.getLatestPackage().getId()+", "
								+ "checksum='" + object.getLatestPackage().getChecksum()+ "', "
								+ "checksumDate = now() WHERE id = "+copy.getId()).executeUpdate();
				if (updatesNodeId!=1) throw new RuntimeException("could not execute update of node_id");

			} catch (Exception e) {
				logger.error("Unable to save copy!");
			} finally {
				logger.debug("Unset copy from node "+cn.getId());
				cn.setCopyToSave(null);
			}
		}
		
		
		
		if (createJob!=null)
			session.save(createJob);

		session.flush();
		
		if (deleteJob) {
			session.delete(job);
			baseLogger.info(this.getClass().getName()+" finished working on job for object with identifier "+job.getObject().getIdentifier()+
					". Job deleted. Database transaction successful.");
		}
		else {
			session.update(job);
			if(job.getStatus().endsWith(C.WORKFLOW_STATUS_DIGIT_WAITING)) {
				baseLogger.info(this.getClass().getName()+" finished working on job for object with identifier "+job.getObject().getIdentifier()+
					". Set job to end state ("+endStatus+"). Database transaction successful.");	
			} else {
				baseLogger.info(this.getClass().getName()+" finished working on job for object with identifier "+job.getObject().getIdentifier()+
						". Set job to error state ("+job.getStatus()+").");	
			}
					
		}

		session.flush();
		
		if (deleteObject) 
			session.delete(object);
		else
			session.update(object);

		session.getTransaction().commit();
	}
	
	
	
	
	
	
	private void reportUserError(UserException e) {
		logger.error(this.getClass().getName()+": UserException in action: ",e);
		new MailContents(preservationSystem,n).userExceptionCreateUserReport(userExceptionManager,e,o);
		if (e.checkForAdminReport())
			new MailContents(preservationSystem,n).abstractActionCreateAdminReport(e, o, this);
		sendJMSException(e);
	}

	protected void reportTechnicalError(Exception e){
		logger.error(this.getClass().getName()+": Exception in action: ",e);
		new MailContents(preservationSystem,n).abstractActionCreateAdminReport(e, o, this);
		sendJMSException(e);
	}

	/**
	 * Sends Exception to JMS Broker.
	 * 
	 * @author Jens Peters
	 * @param e
	 */
	private void sendJMSException(Exception e) {
	
		String txt =  e.getMessage(); 
		if (StringUtilities.isSet(txt)) {
			if  (e.getMessage().equals("null")) txt = "-keine weiteren Details- (NPE)"; 	
			JmsMessage jms = new JmsMessage(C.QUEUE_TO_CLIENT,C.QUEUE_TO_SERVER,o.getIdentifier() +" : "+ txt);
			jmsMessageServiceHandler.sendJMSMessage(jms);
		}
	}

	public void synchronizeObjectDatabaseAndFileSystemState() {
		
		if (!SUPPRESS_OBJECT_CONSISTENCY_CHECK){
			if ((!wa.isDBtoFSconsistent())||(!wa.isFStoDBconsistent())){
				reportTechnicalError(new RuntimeException("Object DB is not consistent with data on FS."));
			}
		}
	}

	/**
	 * @author Sebastian Cuy
	 * Sets the file name for package logger dynamically
	 */
	private void setupObjectLogging(String logFileBase) {
		MDC.put("object_id", logFileBase);
	}
	
	/**
	 * @author Sebastian Cuy
	 * @author Daniel M. de Oliveira
	 */
	private void unsetObjectLogging() {
		MDC.remove("object_id");
	}


	public JmsMessageServiceHandler getJmsMessageServiceHandler() {
		return jmsMessageServiceHandler;
	}

	public void setJmsMessageServiceHandler(JmsMessageServiceHandler jmsMessageServiceHandler) {
		this.jmsMessageServiceHandler = jmsMessageServiceHandler;
	}

	public void setEndStatus(String es){
		this.endStatus=es;
	}
	
	public void setJob(Job job){
		this.j=job;
	}
	
	public void setActionMap(ActionRegistry actionMap) {
		this.actionMap = actionMap;
	}

	public ActionRegistry getActionMap() {
		return actionMap;
	}

	public Node getLocalNode() {
		return n;
	}

	public void setLocalNode(Node localNode) {
		this.n = localNode;
	}

	public String getStartStatus() {
		return startStatus;
	}

	public void setStartStatus(String startStatus) {
		this.startStatus = startStatus;
	}

	public int getConcurrentJobs() {
		return concurrentJobs;
	}

	public void setConcurrentJobs(int concurrentJobs) {
		this.concurrentJobs = concurrentJobs;
	}

	public String getEndStatus() {
		return endStatus;
	}

	public Job getJob() {
		return j;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Object getObject() {
		return o;
	}

	public void setObject(Object object) {
		this.o = object;
	}
	
	public UserExceptionManager getUserExceptionManager() {
		return userExceptionManager;
	}

	public void setUserExceptionManager(UserExceptionManager userExceptionManager) {
		this.userExceptionManager = userExceptionManager;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	public Session openSession() {
		return HibernateUtil.openSession();
	}

	public PreservationSystem getPreservationSystem() {
		return preservationSystem;
	}

	public void setPSystem(PreservationSystem pSystem) {
		this.preservationSystem = pSystem;
	}
	
	
	@Override
	public boolean equals(java.lang.Object obj) {
		
		AbstractAction other = (AbstractAction) obj;
		
		if (this.getClass().getName().equals(other.getClass().getName())
				&&(this.getName().equals(other.getName()))
				)
			return true;
			
		return false;
	}
	
	@Override
	public int hashCode() {
		return this.getClass().getName().hashCode()+this.getName().hashCode();
	}

	public boolean isDELETEOBJECT() {
		return DELETEOBJECT;
	}

	public boolean isKILLATEXIT() {
		return kILLATEXIT;
	}
	
	protected void setKILLATEXIT(boolean kILLATEXIT) {
		this.kILLATEXIT = kILLATEXIT;
	}
	
	public void setROLLBACKONLY(boolean rollbackOnly) {
		rOLLBACKONLY=rollbackOnly;
	}
	
	public boolean isROLLBACKONLY() {
		return rOLLBACKONLY;
	}
	
	public ActionFactory getActionFactory() {
		return actionFactory;
	}

	public void setActionFactory(ActionFactory actionFactory) {
		this.actionFactory = actionFactory;
	}

	public Job getToCreate() {
		return toCreate;
	}

	public WorkArea getWa() {
		return wa;
	}

	public void setWorkArea(WorkArea wa) {
		this.wa = wa;
	}
}
