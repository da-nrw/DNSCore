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


import java.util.List;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import de.uzk.hki.da.core.ConfigurationException;
import de.uzk.hki.da.core.HibernateUtil;
import de.uzk.hki.da.model.ConversionPolicy;
import de.uzk.hki.da.model.Job;
import de.uzk.hki.da.model.Node;
import de.uzk.hki.da.model.PreservationSystem;
import de.uzk.hki.da.model.SecondStageScanPolicy;
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
	
	private QueueConnector qc;
	
	/** The user exception manager. */
	private UserExceptionManager userExceptionManager;
	
	/** The context. */
	private ApplicationContext context;
	
	/** The on halt. */
	private boolean onHalt = false;
	
	/** *The Active MQ Connection factory */
	private ActiveMQConnectionFactory mqConnectionFactory;

	private PreservationSystem preservationSystem;


	public void init(){
		setPreservationSystem(new PreservationSystem()); getPreservationSystem().setId(1);
		// attach policies to preservation system
		Session session = HibernateUtil.openSession();
		session.beginTransaction();
		preservationSystem.setSubformatIdentificationPolicies(getSecondStageScanPolicies(session));
		session.refresh(getPreservationSystem());
		// circumvent lazy initialization issues
		Hibernate.initialize(getPreservationSystem().getConversion_policies());
		// circumvent lazy initialization issues
		for (@SuppressWarnings("unused") 
			ConversionPolicy p:getPreservationSystem().getConversion_policies());
		session.getTransaction().commit();
		session.close();
	}
	
	private void injectProperties(AbstractAction action, Job job){
		action.setUserExceptionManager(userExceptionManager);
		action.setMqConnectionFactory(mqConnectionFactory);
		action.setLocalNode(localNode);
		job.getObject().setTransientNodeRef(localNode);
		action.setObject(job.getObject());
		action.setActionMap(getActionRegistry());			
		action.setJob(job);
		action.setPSystem(getPreservationSystem());
	}
	
	private void checkSystemState(AbstractAction action) {
		if (action.getActionMap() == null) throw new IllegalStateException("Unable to build action. Action map has not been set.");
		if (action.getLocalNode()==null) throw new IllegalStateException("Unable to build action. Node not set.");
		if (action.getPreservationSystem()==null) throw new IllegalStateException("preservationSystem not set");
		if (action.getPreservationSystem().getMinRepls()==null) throw new IllegalStateException("min repls not set");
		if (action.getPreservationSystem().getMinRepls()<3) logger.warn("min_repls lower than 3 not recommended for lta");
		if (action.getPreservationSystem().getAdmin()==null) throw new IllegalStateException("node admin not set");
		if (action.getObject()==null) throw new IllegalStateException("object not set");
		if (action.getObject().getContractor()==null) throw new IllegalStateException("contractor not set");
		if (action.getObject().getContractor().getShort_name()==null) throw new IllegalStateException("contractor short name not set.");
		if (action.getObject().getContractor().getEmailAddress()==null||action.getObject().getContractor().getEmailAddress().isEmpty()) throw new IllegalStateException("user email not set");
		if (action.getObject().getIdentifier()==null) throw new IllegalStateException("object identifier not set");
		if (action.getUserExceptionManager()==null) throw new IllegalStateException("user exception manager not set");
		action.getObject().getLatestPackage();
		if (action.getObject().getLatestPackage().getContainerName()==null) throw new IllegalStateException("containerName of latest package not set");
		if (action.getJob()==null) throw new IllegalStateException("job not set");
	}
	
	
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
		if (context == null) throw new ConfigurationException("Unable to build action. Application context has not been set.");
		
		logger.trace("building action");
		
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
			
			Job jobCandidate = qc.fetchJobFromQueue(action.getStartStatus(), workingStatus
					, localNode, getPreservationSystem());
			if (jobCandidate == null) {
				logger.trace("No job for type {}, checking for types with lower priority", jobType);
				continue;
			}
			logger.info("fetched job: {}", jobCandidate);

			actionRegistry.registerAction(action);
			
			injectProperties(action,jobCandidate);
			checkSystemState(action);
			
			
			
			return action;
		}
		
		logger.info("(for local node) No jobs in queue, nothing to do, shoobidoowoo, ...");
		return null;
		
	}
	
	
	
	
	/**
	 * Gets the second stage scan policies.
	 *
	 * @return the second stage scan policies
	 */
	private List<SecondStageScanPolicy> getSecondStageScanPolicies(Session session) {
		@SuppressWarnings("unchecked")
		List<SecondStageScanPolicy> l = session
				.createQuery("from SecondStageScanPolicy").list();

		return l;
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

	public PreservationSystem getPreservationSystem() {
		return preservationSystem;
	}

	public void setPreservationSystem(PreservationSystem preservationSystem) {
		this.preservationSystem = preservationSystem;
	}

	public QueueConnector getQueueConnector() {
		return qc;
	}

	public void setQueueConnector(QueueConnector qc) {
		this.qc = qc;
	}
}
