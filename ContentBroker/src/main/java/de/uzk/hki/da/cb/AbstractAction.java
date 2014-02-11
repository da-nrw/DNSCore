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

import javax.jms.Connection;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.TextMessage;
import javax.mail.MessagingException;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import de.uzk.hki.da.core.ActionCommunicatorService;
import de.uzk.hki.da.core.ActionRegistry;
import de.uzk.hki.da.core.HibernateUtil;
import de.uzk.hki.da.core.UserException;
import de.uzk.hki.da.model.CentralDatabaseDAO;
import de.uzk.hki.da.model.Job;
import de.uzk.hki.da.model.Node;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.service.Mail;
import de.uzk.hki.da.service.UserExceptionManager;
import de.uzk.hki.da.utils.LinuxEnvironmentUtils;
import de.uzk.hki.da.utils.Utilities;


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
 * & the DA-NRW team
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
	protected String description;
	protected int concurrentJobs = 3;
	protected ActionCommunicatorService actionCommunicatorService;
	private UserExceptionManager userExceptionManager;
	private ActiveMQConnectionFactory mqConnectionFactory;
	
	
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

			object.reattach();
			logger.info("Stubbing implementation of "+this.getClass().getName());
			logger.debug(Utilities.getHeapSpaceInformation());
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
					logger.info(this.getClass().getName()+" finished working on job: "+job.getId()+". Now committing changes to database.");
					job.setStatus(endStatus); // XXX needed just for integration test	
					Session session = HibernateUtil.openSession();
					session.beginTransaction();
					session.delete(job);
					session.update(object);
					session.getTransaction().commit();
					session.close();
					logger.info(this.getClass().getName()+" finished working on job: "+job.getId()+". Job deleted. Database transaction successful.");
					
				} else {
					logger.info(this.getClass().getName()+" finished working on job: "+job.getId()+". Now commiting changes to database.");
					job.setStatus(endStatus);	
					Session session = HibernateUtil.openSession();
					session.beginTransaction();
					session.update(job);
					session.update(object);
					session.getTransaction().commit();
					session.close();
					logger.info(this.getClass().getName()+" finished working on job: "+job.getId()+". Set job to end state ("+endStatus+"). Database transaction successful.");
				}
			}
			
		} catch (UserException e) {
			logger.error(this.getClass().getName()+": UserException in action: ",e);
			String errorStatus = getStartStatus().substring(0, getStartStatus().length() - 1) + "4";
			handleError(errorStatus);
			createUserReport(e);
			if (e.checkForAdminReport())
				createAdminReport(e);
			sendJMSException(e);
		} catch (org.hibernate.exception.GenericJDBCException sql) {
			logger.error(this.getClass().getName()+": Exception while committing changes to database after action: ",sql);
			String errorStatus = getStartStatus().substring(0, getStartStatus().length() - 1) + "1";
			handleError(errorStatus);
			createAdminReport(sql);
			sendJMSException(sql);
		} catch (Exception e) {
			logger.error(this.getClass().getName()+": Exception in action: ",e);
			String errorStatus = getStartStatus().substring(0, getStartStatus().length() - 1) + "1";
			handleError(errorStatus);
			createAdminReport(e);
			sendJMSException(e);
		} finally {			
			unsetObjectLogging();
			actionMap.deregisterAction(this);
		}
	}
	/**
	 * Sends Exception to JMS Broker
	 * @author Jens Peters
	 * @param e
	 */
	private void sendJMSException(Exception e) {
		if (mqConnectionFactory!=null) {
		try {
		 
			Connection connection = mqConnectionFactory.createConnection();
			connection.start();
			javax.jms.Session session = connection.createSession(false, javax.jms.Session.AUTO_ACKNOWLEDGE);
			Destination toClient = session.createQueue("CB.ERROR");
			Destination toServer = session.createQueue("CB.ERROR.SERVER");
			MessageProducer producer;
			producer = session.createProducer(toClient);
			producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);         
			String messageSend = "Package "+  object.getIdentifier() + " " + e.getMessage();
			TextMessage message = session.createTextMessage(messageSend);
			message.setJMSReplyTo(toServer);
            producer.send(message);
            producer.close();
            session.close();
            connection.close();
		}catch (JMSException e1) {
			logger.error("Error while connecting to ActiveMQ Broker " + e1.getCause());
		}
		}
	}
	
	private void handleError(String errorStatus) {
		
		try {
			logger.info("Stubbing rollback of "+this.getClass().getName());
			rollback();
		} catch (Exception e) {
			logger.error("@Admin: SEVERE ERROR WHILE TRYING TO ROLLBACK ACTION. DATABASE MIGHT BE INCONSISTENT NOW.");
			logger.error(this.getClass().getName()+": couldn't get rollbacked to previous state. Exception in action.rollback(): ",e);
			errorStatus = errorStatus.substring(0, errorStatus.length() - 1) + "3";
		}

		job.setDate_modified(String.valueOf(new Date().getTime()/1000L));
		job.setStatus(errorStatus);
		
		try{
			logger.debug("Set job to error state. Commit changes to database now.");
			Session session = HibernateUtil.openSession();
			session.beginTransaction();
			session.update(job);
			session.getTransaction().commit();
			session.close();
		}catch(Exception e){
			logger.error("@Admin: SEVERE ERROR WHILE TRYING TO COMMIT CHANGES AFTER ROLLBACK. DATABASE MIGHT BE INCONSISTENT NOW.",e);
		}
		
		logger.info("Database transaction successful. Job set to error state " + errorStatus);
		
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
		
		if (email!=null && !email.equals("")) {
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
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public ActiveMQConnectionFactory getMqConnectionFactory() {
		return mqConnectionFactory;
	}
	public void setMqConnectionFactory(ActiveMQConnectionFactory mqConnectionFactory) {
		this.mqConnectionFactory = mqConnectionFactory;
	}

	
}
