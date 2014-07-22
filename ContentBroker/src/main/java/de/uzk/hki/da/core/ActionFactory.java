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


import java.util.List;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.hibernate.UnresolvableObjectException;
import org.hibernate.classic.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import de.uzk.hki.da.cb.AbstractAction;
import de.uzk.hki.da.model.CentralDatabaseDAO;
import de.uzk.hki.da.model.Job;
import de.uzk.hki.da.model.Node;
import de.uzk.hki.da.service.UserExceptionManager;


/**
 * A factory for creating Action objects.
 *
 * @author Sebastian Cuy
 * @author Daniel M. de Oliveira
 */
public class ActionFactory implements ApplicationContextAware {
	
	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(ActionFactory.class);
	
	/** The action registry. */
	private ActionRegistry actionRegistry;
	
	/** The local node. */
	private Node localNode; // Node the ContentBroker actually runs on
	
	/** The dao. */
	private CentralDatabaseDAO dao; // Database Connector
	
	/** The user exception manager. */
	private UserExceptionManager userExceptionManager;
	
	/** The context. */
	private ApplicationContext context;
	
	/** The systemFrom Email Adress **/
	private String systemFromEmailAddress;
	 
	/** The on halt. */
	private boolean onHalt = false;
	
	/** *The Active MQ Connection factory */
	private ActiveMQConnectionFactory mqConnectionFactory;

	
	/**
	 * Following the defined priorities (context) of
	 * job types this method tests for every job type 
	 * if there are jobs for this type found in the database 
	 * which have to be done.
	 * 
	 * @return the first job found or
	 * null if Factory is on halt or no job of any type found
	 * that can be started.
	 */
	public AbstractAction buildNextAction() {		
		
		logger.trace("building action");
		
		if (dao == null) throw new ConfigurationException("Unable to build action. DAO has not been set.");
		if (actionRegistry == null) throw new ConfigurationException("Unable to build action. Action map has not been set.");
		if (context == null) throw new ConfigurationException("Unable to build action. Application context has not been set.");
		if (localNode==null) throw new ConfigurationException("Unable to build action. Node not set.");
		
		if (onHalt){
			logger.info("ActionFactory is on halt. Waiting to resume work ...");
			return null;
		}
		
		// iterate over available job types in order of priority,
		// start action if a corresponding job exists in the database 
		List<String> availableJobTypes = actionRegistry.getAvailableJobTypes();
		logger.trace("available job types: " + availableJobTypes);
		for (String jobType : availableJobTypes) {

			AbstractAction action = (AbstractAction) context.getBean(jobType);
			
			String workingStatus = action.getStartStatus().substring(0,action.getStartStatus().length()-1) + "2";
			
			Job jobCandidate = dao.fetchJobFromQueue(action.getStartStatus(), workingStatus
					, localNode);
			if (jobCandidate == null) {
				logger.trace("No job for type {}, checking for types with lower priority", jobType);
				continue;
			}
			logger.info("fetched job: {}", jobCandidate);

			actionRegistry.registerAction(action);
			
			action.setDao(dao);
			action.setUserExceptionManager(userExceptionManager);
			action.setMqConnectionFactory(mqConnectionFactory);
			action.setLocalNode(localNode);
			action.setSystemFromEmailAddress(systemFromEmailAddress);
			jobCandidate.getObject().setTransientNodeRef(localNode);
			action.setObject(jobCandidate.getObject());
			action.setActionMap(getActionRegistry());			
			action.setJob(jobCandidate);
			return action;
		}
		
		logger.info("(for local node) No jobs in queue, nothing to do, shoobidoowoo, ...");
		return null;
		
	}

	/* (non-Javadoc)
	 * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)
	 */
	@Override
	public void setApplicationContext(ApplicationContext context)
			throws BeansException {
		this.context = context;		
	}

	/**
	 * Sets the dao.
	 *
	 * @param dao the new dao
	 */
	public void setDao(CentralDatabaseDAO dao) {
		this.dao = dao;
	}

	/**
	 * Gets the action registry.
	 *
	 * @return the action registry
	 */
	public ActionRegistry getActionRegistry() {
		return actionRegistry;
	}

	/**
	 * Sets the action registry.
	 *
	 * @param actionRegistry the new action registry
	 */
	public void setActionRegistry(ActionRegistry actionRegistry) {
		this.actionRegistry = actionRegistry;
	}
	
	
	/**
	 * Gets the user exception manager.
	 *
	 * @return the user exception manager
	 */
	public UserExceptionManager getUserExceptionManager() {
		return userExceptionManager;
	}
	
	/**
	 * Sets the user exception manager.
	 *
	 * @param userExceptionManager the new user exception manager
	 */
	public void setUserExceptionManager(UserExceptionManager userExceptionManager) {
		this.userExceptionManager = userExceptionManager;
	}

	/**
	 * Pause.
	 *
	 * @param b the b
	 */
	public void pause(boolean b) {
		onHalt  = b;
	}


	/**
	 * Gets the local node.
	 *
	 * @return the local node
	 */
	public Node getLocalNode() {
		return localNode;
	}

	/**
	 * Sets the local node.
	 *
	 * @param localNode the new local node
	 */
	public void setLocalNode(Node localNode) {
		this.localNode = localNode;
	}

	/**
	 * @return the mqConnectionFactory
	 */
	public ActiveMQConnectionFactory getMqConnectionFactory() {
		return mqConnectionFactory;
	}

	/**
	 * @param mqConnectionFactory the mqConnectionFactory to set
	 */
	public void setMqConnectionFactory(ActiveMQConnectionFactory mqConnectionFactory) {
		this.mqConnectionFactory = mqConnectionFactory;
	}

	public String getSystemFromEmailAddress() {
		return systemFromEmailAddress;
	}

	public void setSystemFromEmailAddress(String systemFromEmailAddress) {
		this.systemFromEmailAddress = systemFromEmailAddress;
	}
	
}
