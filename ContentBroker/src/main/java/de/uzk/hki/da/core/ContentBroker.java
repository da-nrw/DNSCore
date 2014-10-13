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

import java.io.IOException;
import java.util.Properties;

import org.apache.activemq.spring.ActiveMQConnectionFactory;
import org.apache.activemq.xbean.XBeanBrokerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.core.task.TaskExecutor;
import org.springframework.core.task.TaskRejectedException;

import de.uzk.hki.da.action.AbstractAction;
import de.uzk.hki.da.action.ActionFactory;
import de.uzk.hki.da.action.ActionInformation;
import de.uzk.hki.da.utils.Utilities;




/**
 * ContentBroker is the main application for DNSCore, serving all needed actions 
 * for SIP to AIP and PIP transformation
 * 
 * @author: Daniel M. de Oliveira
 * @author: Sebastian Cuy
 * @author: Jens Peters
 * @author: Thomas Kleinke
 * @author: Polina Gubaidullina
 * @author: Martin Fischer
 * @author: Johanna Puhl
 * @author: Lisa Rau
 * @author: Chris Weitz
 */
public class ContentBroker {
	
	// set path to logback xml before anything else happens
	static { 
		System.setProperty("logback.configurationFile", "conf/logback.xml");
	}

	/** The props. */
	private static Properties props = new Properties();
	
	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(ContentBroker.class);

	/** The task executor. */
	private TaskExecutor taskExecutor;
	
	/** The action factory. */
	private ActionFactory actionFactory;
	
	/** The action factory. */
	private ActionInformation actionInformation;
	
	/** The controller. */
	private Controller controller;
	
	/** The server socket number. */
	private int serverSocketNumber;
	
	/** The MqBroker */
	private ActiveMQConnectionFactory mqConnectionFactory;
	

	private XBeanBrokerService mqBroker;

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void main( String[] args) throws IOException {		

		if ((args.length>0)&&(args[0].equals("createSchema"))){
			HibernateUtil.createSchema("conf/hibernateCentralDB.cfg.xml");
		}
		
		if ((args.length>0)&&(args[0].equals("diagnostics"))){
			if (Diagnostics.run()!=0)
				System.exit(1);
			else
				System.exit(0);
		}
		
		logger.info("Starting ContentBroker ..");
		
		logger.info("Setting up HibernateUtil ..");
		try {
			HibernateUtil.init("conf/hibernateCentralDB.cfg.xml");
		} catch (Exception e) {
			logger.error("Exception in main!",e);
		}
		
		logger.info("Reading properties");		
		Utilities.parseArguments(args,props);
		
		try {
			@SuppressWarnings("resource")
			AbstractApplicationContext context =
					new FileSystemXmlApplicationContext("conf/beans.xml");
			context.registerShutdownHook();
			logger.info("ContentBroker is up and running");
		} catch (Exception e) {
			logger.error("Exception in main!",e);
		}
		
	}
	
	
	/**
	 * Instantiates a new content broker.
	 */
	public ContentBroker() {
		
	}
	
	/**
	 * Schedule task.
	 */
	public void scheduleTask() {
		
		logger.trace("scheduling task");		
		try {
			AbstractAction action = actionFactory.buildNextAction();
			if(action != null) {
				logger.debug("executing... "+action.getName());
				taskExecutor.execute(action);
			}
		} catch (TaskRejectedException e) {
			logger.warn("Task rejected!",e);
		} catch (Exception e) {
			logger.error("Exception while scheduling task", e);
		}

	}
	
	/**
	 * for testing purposes.
	 */
	public void scheduleTaskWithoutCatchingExceptions() {
		
		logger.trace("scheduling task");		
		try {
			AbstractAction action = actionFactory.buildNextAction();
			if(action != null) taskExecutor.execute(action);
		} catch (TaskRejectedException e) {
			logger.warn("Task rejected!",e);
		}

	}
	
	/**
	 * to be called after setters.
	 */
	public void init(){

		controller = new Controller("localhost",
				getServerSocketNumber(),
				actionFactory, actionInformation, mqBroker, mqConnectionFactory);
		(new Thread(controller)).start();
	}
	
	

	/**
	 * Gets the task executor.
	 *
	 * @return the task executor
	 */
	public TaskExecutor getTaskExecutor() {
		return taskExecutor;
	}


	/**
	 * Sets the task executor.
	 *
	 * @param executor the new task executor
	 */
	public void setTaskExecutor(TaskExecutor executor) {
		this.taskExecutor = executor;
	}


	/**
	 * Gets the action factory.
	 *
	 * @return the action factory
	 */
	public ActionFactory getActionFactory() {
		return actionFactory;
	}


	/**
	 * Sets the action factory.
	 *
	 * @param actionFactory the new action factory
	 */
	public void setActionFactory(ActionFactory actionFactory) {
		this.actionFactory = actionFactory;
	}

	/**
	 * Gets the server socket number.
	 *
	 * @return the server socket number
	 */
	public int getServerSocketNumber() {
		return serverSocketNumber;
	}


	/**
	 * Sets the server socket number.
	 *
	 * @param serverSocketNumber the new server socket number
	 */
	public void setServerSocketNumber(int serverSocketNumber) {
		if (serverSocketNumber<=0)
			throw new RuntimeException("serverSocketNumber must be a value greater 0");
		
		this.serverSocketNumber = serverSocketNumber;
	}


	/**
	 * @param mqBroker the mqBroker to set
	 */
	public void setMqBroker(XBeanBrokerService mqBroker) {
		this.mqBroker = mqBroker;
	}


	/**
	 * @return the mqBroker
	 */
	public XBeanBrokerService getMqBroker() {
		return mqBroker;
	}	

	public ActiveMQConnectionFactory getMqConnectionFactory() {
		return mqConnectionFactory;
	}


	public void setMqConnectionFactory(ActiveMQConnectionFactory mqConnectionFactory) {
		this.mqConnectionFactory = mqConnectionFactory;
	}


	/**
	 * @return the actionInformation
	 */
	public ActionInformation getActionInformation() {
		return actionInformation;
	}


	/**
	 * @param actionInformation the actionInformation to set
	 */
	public void setActionInformation(ActionInformation actionInformation) {
		this.actionInformation = actionInformation;
	}

	
	
}
