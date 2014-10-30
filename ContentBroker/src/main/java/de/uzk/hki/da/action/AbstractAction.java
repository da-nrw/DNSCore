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

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.TextMessage;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.activemq.ActiveMQConnectionFactory;
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
import de.uzk.hki.da.core.UserException;
import de.uzk.hki.da.model.Job;
import de.uzk.hki.da.model.Node;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.model.PreservationSystem;
import de.uzk.hki.da.repository.RepositoryException;
import de.uzk.hki.da.service.MailContents;
import de.uzk.hki.da.service.UserExceptionManager;
import de.uzk.hki.da.utils.LinuxEnvironmentUtils;
import de.uzk.hki.da.utils.Utilities;


/**
 * Actions should get extended to execute business code. 
 * Business code should be placed into the implementation method.
 * After performing the implementation, the database gets updated according to the changes 
 * made to the model (object,job) during implementation.
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
	
	private boolean KILLATEXIT = false;
	protected boolean SUPPRESS_OBJECT_CONSISTENCY_CHECK = false;
	private boolean INTEGRATIONTEST = false;
	public boolean DELETEOBJECT = false;
	
	protected ActionRegistry actionMap;
	private String name;
	protected Node localNode;
	protected String startStatus;
	protected Job job;
	protected Job toCreate = null;
	protected Object object;
	protected String endStatus;
	protected String description;
	protected int concurrentJobs = 3;
	private UserExceptionManager userExceptionManager;
	private ActiveMQConnectionFactory mqConnectionFactory;
	protected PreservationSystem preservationSystem;
	
	
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
		
		
		logger.info("Running \""+this.getClass().getName()+"\"");
		logger.debug(LinuxEnvironmentUtils.logHeapSpaceInformation());
		
		try {
			checkActionSpecificConfiguration();
			checkSystemStatePreconditions();
		} catch (Exception e) {
			logger.error(e.getMessage()); return;
		}
		
		try {
			logger.info("AbstractAction fetched job from queue. See logfile: "+object.getIdentifier()+".log");
			setupObjectLogging(object.getIdentifier());

			object.reattach();
			if (!SUPPRESS_OBJECT_CONSISTENCY_CHECK){
				if ((!object.isDBtoFSconsistent())||(!object.isFStoDBconsistent())){
					throw new RuntimeException("Object DB is not consistent with data on FS.");
				}
			}

			
			logger.info("Stubbing implementation of "+this.getClass().getName());
			logger.debug(Utilities.getHeapSpaceInformation());
			
			if (!implementation()){				
				logger.info(this.getClass().getName()+": implementation returned false. Setting job back to start state ("+startStatus+").");  
				job.setStatus(startStatus);
				toCreate=null;
			} else {
				job.setDate_modified(String.valueOf(new Date().getTime()/1000L));
				logger.info(this.getClass().getName()+" finished working on job: "+job.getId()+". Now commiting changes to database.");
				if (KILLATEXIT)	{
//					job.setStatus(endStatus); // XXX needed just for integration test	
					logger.info("Set the job status to the end status "+endStatus+" .");
				} else {
					job.setStatus(endStatus);	
				}
			}

			upateObjectAndJob(object,job,DELETEOBJECT,KILLATEXIT,toCreate);
			
		} catch (UserException e) {
			logger.error(this.getClass().getName()+": UserException in action: ",e);
			String errorStatus = getStartStatus().substring(0, getStartStatus().length() - 1) + C.WORKFLOW_STATE_DIGIT_USER_ERROR;
			handleError(errorStatus);
			new MailContents(preservationSystem,localNode).userExceptionCreateUserReport(userExceptionManager,e,object);
			if (e.checkForAdminReport())
				new MailContents(preservationSystem,localNode).abstractActionCreateAdminReport(e, object, this);
			sendJMSException(e);
		} catch (Exception e) {
			logger.error(this.getClass().getName()+": Exception in action: ",e);
			String errorStatus = getStartStatus().substring(0, getStartStatus().length() - 1) + "1";
			handleError(errorStatus);
			new MailContents(preservationSystem,localNode).abstractActionCreateAdminReport(e, object, this);
			sendJMSException(e);
		} finally {		
			
			
			unsetObjectLogging();
			
			actionMap.deregisterAction(this);
		}
	}
	
	

	/**
	 * @param object
	 * @param job
	 * @param deleteObject
	 * @param deleteJob
	 * @param createJob
	 */
	private void upateObjectAndJob(Object object,Job job, boolean deleteObject,boolean deleteJob,Job createJob){
		
		try {
			Session session = openSession();
			session.beginTransaction();
			
			if (deleteObject) 
				session.delete(object);
			else
				session.update(object);
			
			session.flush();
			
			if (createJob!=null)
				session.save(createJob);

			session.flush();
			
			if (deleteJob) {
				session.delete(job);
				logger.info(this.getClass().getName()+" finished working on job: "+job.getId()+". Job deleted. Database transaction successful.");
			}
			else {
				session.update(job);
				logger.info(this.getClass().getName()+" finished working on job: "+job.getId()+". Set job to end state ("+endStatus+"). Database transaction successful.");			
			}
			
			
			session.getTransaction().commit();
			session.close();
		}
		
		catch (org.hibernate.exception.GenericJDBCException sql) {
			
				logger.error(this.getClass().getName()+": Exception while committing changes to database after action: ",sql);
				String errorStatus = getStartStatus().substring(0, getStartStatus().length() - 1) + "1";
				handleError(errorStatus);
				new MailContents(preservationSystem,localNode).abstractActionCreateAdminReport(sql, object, this);
				sendJMSException(sql);
		}	
	}
	
	
	
	
	
	
	/**
	 * Sends Exception to JMS Broker.
	 * 
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
			String txt =  e.getMessage(); 	
			if  (e.getMessage()=="null") txt = "-keine weiteren Details- (NPE)"; 	
			String messageSend = "Package "+  object.getIdentifier() + " " + txt;
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
	
	/**
	 * @param errorStatus
	 */
	private void handleError(String errorStatus) {
		
		try {
			logger.info("Stubbing rollback of "+this.getClass().getName());
			rollback();
		} catch (Exception e) {
			logger.error("@Admin: SEVERE ERROR WHILE TRYING TO ROLLBACK ACTION. DATABASE OR WORKAREA MIGHT BE INCONSISTENT NOW.");
			logger.error(this.getClass().getName()+": couldn't get rollbacked to previous state. Exception in action.rollback(): ",e);
			errorStatus = errorStatus.substring(0, errorStatus.length() - 1) + "3";
		}

		job.setDate_modified(String.valueOf(new Date().getTime()/1000L));
		job.setStatus(errorStatus);
		
		try{
			logger.debug("Set job to error state. Commit changes to database now.");
			Session session = openSession();
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

	public boolean isKILLATEXIT() {
		return KILLATEXIT;
	}

	public void setKILLATEXIT(boolean kILLATEXIT) {
		KILLATEXIT = kILLATEXIT;
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

	public boolean isINTEGRATIONTEST() {
		return INTEGRATIONTEST;
	}

	public void setINTEGRATIONTEST(boolean iNTEGRATIONTEST) {
		INTEGRATIONTEST = iNTEGRATIONTEST;
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
	
	
}
