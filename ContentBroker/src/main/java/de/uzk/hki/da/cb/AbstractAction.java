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

package de.uzk.hki.da.cb;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;

import javax.mail.MessagingException;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import de.uzk.hki.da.core.ActionCommunicatorService;
import de.uzk.hki.da.core.ActionRegistry;
import de.uzk.hki.da.core.UserException;
import de.uzk.hki.da.db.CentralDatabaseDAO;
import de.uzk.hki.da.db.HibernateUtil;
import de.uzk.hki.da.grid.IrodsSystemConnector;
import de.uzk.hki.da.model.Job;
import de.uzk.hki.da.model.Node;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.service.Mail;
import de.uzk.hki.da.service.UserExceptionManager;
import de.uzk.hki.da.utils.LinuxEnvironmentUtils;


/**
 * Fetches Jobs with a given start state, but only when the local
 * ContentBroker is the owner of the Job. The class makes heavy use of the template method pattern.
 * 
 * Extension notes: In order to extend the BaseAction please follow a few instructions:
 * 
 * <ol><li>Helper methods of extended classes 
 * which should be separetely tested should have default (package) visibility.
 * <li>Constructors which should only be seen by tests should also have default (package) visibility.
 * </ol>
 * @author Daniel M. de Oliveira
 *
 */
public abstract class AbstractAction implements Runnable {	
	
	private String irodsZonePath;
	
	private boolean KILLATEXIT = false;
	private boolean INTEGRATIONTEST = false;
	
	protected ActionRegistry actionMap;
	private String name;
	protected Node localNode;
	protected CentralDatabaseDAO dao;
	protected String startStatus;
	protected Job job;
	protected Object object;
	protected String endStatus;
	protected int concurrentJobs = 3;
	protected IrodsSystemConnector irodsSystemConnector;
	protected ActionCommunicatorService actionCommunicatorService;
	private UserExceptionManager userExceptionManager;

	AbstractAction(){}
	
	Logger logger = LoggerFactory.getLogger( this.getClass().getName() );
	
	/**
	 * false means: i (node) am not responsible 
	 * true means: succesful
	 * errors lead to an error status in run()
	 * 
	 * For good readability every implementation() should contain only
	 * the business logic for the action. The details should be package private 
	 * for unit testing purposes.
	 */
	abstract boolean implementation() throws FileNotFoundException, IOException, UserException;

	/**
	 * Implementations which fail (due to exceptions in implementation() which will be caught in run())
	 * should clean up and set back the file system etc. to the initial state at the beginning of the action.
	 * @throws Exception 
	 */
	abstract void rollback() throws Exception;
	
	/**
	 * Checks settings that are common for all actions.
	 * Can be overridden by extensions.
	 */
	public void checkCommonPreConditions(){
		if (dao==null) throw new IllegalStateException("dao not set");
		if (actionMap==null) throw new IllegalStateException("actionMap not set");
		if (object==null) throw new IllegalStateException("object not set");
		if (localNode==null) throw new IllegalStateException("localNode not set");
		if (job==null) throw new IllegalStateException("job not set");
		if (object.getContractor()==null) throw new IllegalStateException("contractor not set in job");
		if (object.getContractor().getShort_name()==null) throw new IllegalStateException("contractor short name not set in job");
		if (actionCommunicatorService==null) throw new IllegalStateException("action communicator service not set");
		if (userExceptionManager==null) throw new IllegalStateException("user exception manager not set");
	}

	
	public void run() {
		
		logger.info("Running \""+this.getClass().getName()+"\"");
		logger.debug(LinuxEnvironmentUtils.logHeapSpaceInformation());
		checkCommonPreConditions();
		
		
		try {
			
			// --- MUST happen before setting up object style logging ---
			logger.info("AbstractAction fetched job from queue. See logfile: "+object.getIdentifier()+".log");
			setupObjectLogging(object.getIdentifier());

			if (irodsSystemConnector!=null)
				if (!irodsSystemConnector.connect()){
					throw new RuntimeException("Couldn't establish iRODS-Connection");
				}

			logger.info("Stubbing implementation of "+this.getClass().getName());
			if (!implementation()){				
				logger.info(this.getClass().getName()+": implementation returned false. Setting job back to start state ("+startStatus+").");  
				job.setStatus(startStatus);
				
				Session session = HibernateUtil.openSession();
				session.beginTransaction();
				session.update(job);
				session.getTransaction().commit();
				session.close();
				
				
			} else {
				actionCommunicatorService.serialize();
				job.setDate_modified(String.valueOf(new Date().getTime()/1000L));
				if (KILLATEXIT)	{

					logger.trace(this.getClass().getName()+" finished working on job: "+job.getId()+". Deleting job.");
					job.setStatus(endStatus); // XXX needed just for integration test	
					Session session = HibernateUtil.openSession();
					session.beginTransaction();
					session.delete(job);
					session.update(object);
					session.getTransaction().commit();
					session.close();
					
				} else {
					logger.trace(this.getClass().getName()+" finished working on job: "+job.getId()+". Setting job to end state ("+endStatus+").");
					job.setStatus(endStatus);	
					Session session = HibernateUtil.openSession();
					session.beginTransaction();
					session.update(job);
					session.update(object);
					session.getTransaction().commit();
					session.close();
				}
			}
			
		} catch (UserException e) {
			logger.error(this.getClass().getName()+": UserException in action: ",e);
			handleError();
			createUserReport(e);
			if (e.checkForAdminReport())
				createAdminReport(e);
		} catch (Exception e) {
			logger.error(this.getClass().getName()+": Exception in action: ",e);
			handleError();			
			createAdminReport(e);
		} finally {
			
			if (irodsSystemConnector!=null)
				irodsSystemConnector.logoff();
			
			unsetObjectLogging();
			actionMap.deregisterAction(this);
		}
	}
	
	private void handleError() {
		
		try {
			logger.info("Stubbing rollback of "+this.getClass().getName());
			rollback();
		} catch (Exception e) {
			logger.error(this.getClass().getName()+": couldn't rollback to previous state. Exception in action.rollback(): ",e);
		}

		String errorStatus = getStartStatus().substring(0,getStartStatus().length()-1) + "1";
		logger.debug("Setting job to error state ("+errorStatus+") due to a caught exception");
		job.setDate_modified(String.valueOf(new Date().getTime()/1000L));
		job.setStatus(errorStatus);
		
		Session session = HibernateUtil.openSession();
		session.beginTransaction();
		session.update(job);
		session.getTransaction().commit();
		session.close();
	}
	
	/**
	 * Creates report about the error
	 * Sends Email to the Admin
	 * @author Jpeters
	 */
	private void createAdminReport(Exception e) {

		String errorStatus = getStartStatus().substring(0,getStartStatus().length()-1) + "1";
		String email = localNode.getAdminEmail();
		String subject = "Fehlerreport für " + object.getIdentifier() + " : Status (" + errorStatus + ")" ;
		String msg = e.getMessage();
		msg +="\n\n";
		StringWriter s = new StringWriter();
	    e.printStackTrace(new PrintWriter(s));
	    msg += s.toString();
		
		if (email!=null) {
		try {
			Mail.sendAMail(email, subject, msg);
		} catch (MessagingException ex) {
			logger.error("Sending email reciept for " + object.getIdentifier() + " failed",ex);
		}
		} else logger.info(localNode.getName() + " has no valid email address!");
		
	}
	
	/**
	 * Creates report about the error
	 * Sends e-mail to the User
	 * @author Thomas Kleinke
	 */
	private void createUserReport(UserException e) {
		
		String email = object.getContractor().getEmail_contact();
		String subject = "Fehlerreport für " + object.getIdentifier();
		String message = userExceptionManager.getMessage(e.getId());
		
		message = message.replace("%OBJECT_IDENTIFIER", object.getIdentifier())
			 .replace("%CONTAINER_NAME", object.getLatestPackage().getContainerName())
			 .replace("%ERROR_INFO", e.getErrorInfo());
				
		logger.debug("Sending mail to: " + email + "\n" + subject + "\n" + message);
		
		if (email != null) {			
			try {
				Mail.sendAMail(email, subject, message);
			} catch (MessagingException ex) {
				logger.error("Sending email reciept for " + object.getIdentifier() + " failed", ex);
			}
		}
		else
			logger.info(object.getContractor().getShort_name() + " has no valid email address!");		
	}	
	
	/**
	 * Sets the file name for package logger dynamically
	 */
	private void setupObjectLogging(String logFileBase) {
		MDC.put("object_id", logFileBase);
		
		ch.qos.logback.classic.Logger logger =
				(ch.qos.logback.classic.Logger) LoggerFactory.getLogger("de.uzk.hki.da.cb");
		Appender<ILoggingEvent> appender = logger.getAppender("OBJECT");
		if (appender != null)
			appender.start();
	}
	
	private void unsetObjectLogging() {
		// manually close object log in order to prevent "too many open files"
		ch.qos.logback.classic.Logger logger =
				(ch.qos.logback.classic.Logger) LoggerFactory.getLogger("de.uzk.hki.da.cb");
		
		Appender<ILoggingEvent> appender = logger.getAppender("OBJECT");
		if (appender != null)
			appender.stop();
		
		MDC.remove("object_id");
	}

	public boolean isKILLATEXIT() {
		return KILLATEXIT;
	}

	public void setKILLATEXIT(boolean kILLATEXIT) {
		KILLATEXIT = kILLATEXIT;
	}

	public void setEndStatus(String es){
		this.endStatus=es;
	}
	
	public void setNode(Node node){
		this.localNode=node;
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

	public CentralDatabaseDAO getDao() {
		return dao;
	}

	public void setDao(CentralDatabaseDAO dao) {
		this.dao = dao;
	}

	public IrodsSystemConnector getIrodsSystemConnector() {
		return irodsSystemConnector;
	}

	public void setIrodsSystemConnector(IrodsSystemConnector irods) {
		this.irodsSystemConnector = irods;
	}
	
	public ActionCommunicatorService getActionCommunicatorService() {
		return actionCommunicatorService;
	}
	
	public void setActionCommunicatorService(ActionCommunicatorService acs) {
		this.actionCommunicatorService = acs;
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

	public boolean isINTEGRATIONTEST() {
		return INTEGRATIONTEST;
	}

	public void setINTEGRATIONTEST(boolean iNTEGRATIONTEST) {
		INTEGRATIONTEST = iNTEGRATIONTEST;
	}
	
	public String getIrodsZonePath() {
		return irodsZonePath;
	}

	public void setIrodsZonePath(String irodsZonePath) {
		if (!irodsZonePath.endsWith("/")) irodsZonePath+="/";
		this.irodsZonePath = irodsZonePath;
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
	
	
}
