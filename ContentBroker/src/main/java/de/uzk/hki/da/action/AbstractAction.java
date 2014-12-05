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

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import de.uzk.hki.da.core.C;
import de.uzk.hki.da.core.ConfigurationException;
import de.uzk.hki.da.core.HibernateUtil;
import de.uzk.hki.da.core.TimeStampLogging;
import de.uzk.hki.da.core.UserException;
import de.uzk.hki.da.model.Job;
import de.uzk.hki.da.model.Node;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.model.PreservationSystem;
import de.uzk.hki.da.repository.RepositoryException;
import de.uzk.hki.da.service.JmsMessage;
import de.uzk.hki.da.service.JmsMessageServiceHandler;
import de.uzk.hki.da.service.MailContents;
import de.uzk.hki.da.service.UserExceptionManager;
import de.uzk.hki.da.utils.LinuxEnvironmentUtils;
import de.uzk.hki.da.utils.Utilities;


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
	private boolean KILLATEXIT = false;
	protected boolean SUPPRESS_OBJECT_CONSISTENCY_CHECK = false;
	protected boolean DELETEOBJECT = false;
	protected Job toCreate = null;
	
	
	
	protected ActionRegistry actionMap;
	private String name;
	protected String startStatus;
	protected Job job;
	protected Object object;
	protected String endStatus;
	protected String description;
	private UserExceptionManager userExceptionManager;
	
	private JmsMessageServiceHandler jmsMessageServiceHandler;

	
	protected Node localNode;                        // Implementations should never alter the state of this object to ensure thread safety
	protected PreservationSystem preservationSystem; // Implementations should never alter the state of this object to ensure thread safety

	
	
	
	protected int concurrentJobs = 3;
	
	
	
	public AbstractAction(){}
	
	
	protected Logger logger = LoggerFactory.getLogger( this.getClass().getName() );
	
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
	public abstract boolean implementation() throws FileNotFoundException, IOException, UserException, RepositoryException, JDOMException, ParserConfigurationException, SAXException;

	/**
	 * Implementations which fail (due to exceptions in implementation() which will be caught in run())
	 * should clean up and set back the file system etc. to the initial state at the beginning of the action.
	 * @throws Exception 
	 */
	public abstract void rollback() throws Exception;
	
	/**
	 * Implementations should check if an action is wired up correctly in terms of spring configuration. 
	 * @throws ConfigurationException
	 */
	public abstract void checkActionSpecificConfiguration() throws ConfigurationException;
	
	/**
	 * Checks the system state wise preconditions which have to be met that the action can operate properly.
	 * @throws IllegalStateException
	 */
	public abstract void checkSystemStatePreconditions() throws IllegalStateException;
	
	
	@Override
	public void run() {
		
		if (!performCommonPreparationsForActionExecution()) return;
		setupObjectLogging(object.getIdentifier());
		
		synchronizeObjectDatabaseAndFileSystemState();
		
		try {
			Date start = new Date();
			TimeStampLogging tsl = new TimeStampLogging();
//			tsl.log("START", this.getClass().getName(), start.getTime());
			
			execAndPostProcessImplementation();
			
			Date stop = new Date();
			long duration = stop.getTime()-start.getTime(); // in milliseconds
//			tsl.log("STOP", this.getClass().getName(), stop.getTime());
			tsl.log(object.getIdentifier(), this.getClass().getName(), duration);
			
			upateObjectAndJob(object,job,isDELETEOBJECT(),KILLATEXIT,toCreate);
			
		} catch (UserException e) {
			
			execAndPostProcessRollback(object,job,C.WORKFLOW_STATE_DIGIT_USER_ERROR);
			upateObjectAndJob(object, job, false, false, null);
			reportUserError(e);
			
		} catch (Exception e) {
			
			execAndPostProcessRollback(object,job,C.WORKFLOW_STATE_DIGIT_ERROR_PROPERLY_HANDLED);
			upateObjectAndJob(object, job, false, false, null);
			reportTechnicalError(e);
		} 		
			
		// For now, do it after update database was successful to prevent 
		// new jobs getting fetched. It would be an improvement if we had 
		// a controller for the action factory that stops it if database 
		// connection is not possible.
		actionMap.deregisterAction(this);
		
		unsetObjectLogging();
//		tsl.log("Stop", object.getIdentifier(), this.getClass().getName());
	}

	private boolean performCommonPreparationsForActionExecution() {
		logger.info("Running \""+this.getClass().getName()+"\"");
		logger.debug(LinuxEnvironmentUtils.logHeapSpaceInformation());
		
		try {
			checkActionSpecificConfiguration();
			checkSystemStatePreconditions();
		} catch (Exception e) {
			logger.error(e.getMessage()); return false;
		}
		return true;
	}

	
	/**
	 * Execute the business code implementation.
	 * Adjust job properties depending of implementation outcome.
	 * Adjust modifiers depending of implementation outcome. 
	 */
	private void execAndPostProcessImplementation() throws FileNotFoundException,
			IOException, RepositoryException, JDOMException,
			ParserConfigurationException, SAXException {
		
		logger.info("Stubbing implementation of "+this.getClass().getName());
		logger.debug(Utilities.getHeapSpaceInformation());
		
		if (!implementation()){				
			logger.info(this.getClass().getName()+": implementation returned false. Setting job back to start state ("+startStatus+").");  
			job.setStatus(startStatus);
			toCreate=null;
			DELETEOBJECT=false;
			KILLATEXIT=false;
		} else {
			job.setDate_modified(String.valueOf(new Date().getTime()/1000L));
			logger.info(this.getClass().getName()+" finished working on job: "+job.getId()+". Now commiting changes to database.");
			if (KILLATEXIT)	{
				logger.info("Set the job status to the end status "+endStatus+" .");
			} else {
				job.setStatus(endStatus);	
			}
		}
	}
	
	
	
	private void execAndPostProcessRollback(Object object,Job job,String errorStatusEndDigit) {
		
		String errorStatus = getStartStatus().substring(0, getStartStatus().length() - 1) + errorStatusEndDigit;
		
		logger.info("Stubbing rollback of "+this.getClass().getName());
		try {
			rollback();
		} catch (Exception e) {
			logger.error("@Admin: SEVERE ERROR WHILE TRYING TO ROLLBACK ACTION. DATABASE OR WORKAREA MIGHT BE INCONSISTENT NOW.");
			logger.error(this.getClass().getName()+": couldn't get rollbacked to previous state. Exception in action.rollback(): ",e);
			errorStatus = errorStatus.substring(0, errorStatus.length() - 1) + C.WORKFLOW_STATE_DIGIT_ERROR_NOT_PROPERLY_HANDLED;
		}
	
		job.setDate_modified(String.valueOf(new Date().getTime()/1000L));
		job.setStatus(errorStatus);
	}

	
	
	/**
	 * Perform the database transaction to synchronize the updates of job and object 
	 * (which happened during implementation) to the database. 
	 * <br>
	 * In case of connection related failures retries it until it succeeds.
	 */
	private void upateObjectAndJob(
			Object object,Job job, 
			boolean deleteObject,boolean deleteJob,
			Job createJob){
		
		boolean transactionSuccessful=false;
		do {
			Session session = null;
			try {
				logger.info("perform transaction with object="+object.getOrig_name()+", job="+job.getStatus());
				session = openSession();
				session.beginTransaction();
				performTransaction(object, job, deleteObject, deleteJob, createJob, session);
				transactionSuccessful=true;
			}
			catch (Exception sqlException) {
				logger.error(this.getClass().getName()+": Exception while committing changes to database after action: ",sqlException);
				sendJMSException(sqlException);
				
				try {    Thread.sleep(2000);
				} catch (InterruptedException e) {}
			}
			session.close();
		} while(!transactionSuccessful);
	}

	
	
	private void performTransaction(
			Object object,Job job, 
			boolean deleteObject,boolean deleteJob,
			Job createJob, Session session){
		
		if (createJob!=null)
			session.save(createJob);

		session.flush();
		
		if (deleteJob) {
			session.delete(job);
			logger.info(this.getClass().getName()+" finished working on job: "+
					job.getId()+". Job deleted. Database transaction successful.");
		}
		else {
			session.update(job);
			logger.info(this.getClass().getName()+" finished working on job: "+
					job.getId()+". Set job to end state ("+endStatus+"). Database transaction successful.");			
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
		new MailContents(preservationSystem,localNode).userExceptionCreateUserReport(userExceptionManager,e,object);
		if (e.checkForAdminReport())
			new MailContents(preservationSystem,localNode).abstractActionCreateAdminReport(e, object, this);
		sendJMSException(e);
	}

	private void reportTechnicalError(Exception e){
		logger.error(this.getClass().getName()+": Exception in action: ",e);
		new MailContents(preservationSystem,localNode).abstractActionCreateAdminReport(e, object, this);
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
		if  (e.getMessage().equals("null")) txt = "-keine weiteren Details- (NPE)"; 	
		JmsMessage jms = new JmsMessage(C.QUEUE_TO_CLIENT,C.QUEUE_TO_SERVER,txt);
		jmsMessageServiceHandler.sendJMSMessage(jms);
	}

	private void synchronizeObjectDatabaseAndFileSystemState() {
		object.reattach();
		if (!SUPPRESS_OBJECT_CONSISTENCY_CHECK){
			if ((!object.isDBtoFSconsistent())||(!object.isFStoDBconsistent())){
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
		
		ch.qos.logback.classic.Logger logger =
				(ch.qos.logback.classic.Logger) LoggerFactory.getLogger("de.uzk.hki.da");
		Appender<ILoggingEvent> appender = logger.getAppender("OBJECT");
		if (appender != null)
			appender.start();
		
		logger.info("AbstractAction fetched job from queue. See logfile: "+object.getIdentifier()+".log");
	}
	
	/**
	 * @author Sebastian Cuy
	 * @author Daniel M. de Oliveira
	 */
	private void unsetObjectLogging() {
		
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		// manually close object log in order to prevent "too many open files"
		ch.qos.logback.classic.Logger logger =
				(ch.qos.logback.classic.Logger) LoggerFactory.getLogger("de.uzk.hki.da");
		
		Appender<ILoggingEvent> appender = logger.getAppender("OBJECT");
		if (appender != null)
			appender.stop();
		
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
		this.job=job;
	}
	
	public void setActionMap(ActionRegistry actionMap) {
		this.actionMap = actionMap;
	}

	public ActionRegistry getActionMap() {
		return actionMap;
	}

	public Node getLocalNode() {
		return localNode;
	}

	public void setLocalNode(Node localNode) {
		this.localNode = localNode;
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
		return job;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Object getObject() {
		return object;
	}

	public void setObject(Object object) {
		this.object = object;
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
		return KILLATEXIT;
	}
	
	protected void setKILLATEXIT(boolean kILLATEXIT) {
		KILLATEXIT = kILLATEXIT;
	}
}
