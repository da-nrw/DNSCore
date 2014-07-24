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

/**
 * The package integrity.
 */
package de.uzk.hki.da.integrity;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.mail.MessagingException;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uzk.hki.da.core.HibernateUtil;
import de.uzk.hki.da.grid.GridFacade;
import de.uzk.hki.da.model.CentralDatabaseDAO;
import de.uzk.hki.da.model.Node;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.model.Package;
import de.uzk.hki.da.model.StoragePolicy;
import de.uzk.hki.da.service.Mail;


/**
 * Scans the Integrity of AIP Files stored in the Grid.
 * Integrity is classified as number of Repls is reached
 * and Checksum is of all replicas is correct. 
 * 
 * @author Jens Peters
 * @author Daniel M. de Oliveira
 *
 */
public class IntegrityScannerWorker {

	
	private static class ObjectState {
		private static final Integer UnderAudit = 60;
		private static final Integer InWorkflow = 50;
		private static final Integer Error = 51;
		private static final Integer archivedAndValidState = 100;
	}
	
	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(IntegrityScannerWorker.class);
	
	
	/** The irods grid connector. */
	private GridFacade gridFacade;
	
	/** The min nodes. */
	private Integer minNodes;
	
	/** The node admin email. */
	private String nodeAdminEmail;
	
	/** The local node name. */
	private String localNodeId;
	
	/** The system Email Address */
	private String systemFromEmailAddress;


	private CentralDatabaseDAO dao;
	
	/**
	 * Checking for the AIPs related to this node.
	 * @author Daniel M. de Oliveira
	 * @author Jens Peters
	 */
	public void scheduleTask(){
		logger.trace("Scanning AIP s of node " + localNodeId );

		try {
			
			Object object = null;
			if ((object=getDao().fetchObjectForAudit(localNodeId))==null) { 
				logger.warn("Found no object to audit.") ;
				return;
			}
			
			Integer auditResult = checkObjectValidity(object);
			updateObject(object,auditResult);
			
			if (auditResult==ObjectState.Error) {
				sendEmail(object);
			}
			
		} catch (Exception e) {
			logger.error("Error in integrityCheck schedule Task " + e.getMessage(),e);
		}
	}
	
	/**
	 * Updates the object state, sets the current time, and updates
	 * the database object accordingly
	 * @param object
	 * @param auditResult
	 */
	private synchronized void updateObject(Object object,Integer auditResult){
		
		object.setLast_checked(new Date());
		object.setObject_state(auditResult);
		Session session = HibernateUtil.openSession();
		session.beginTransaction();
		session.update(object);
		session.getTransaction().commit();
		session.close();
	}
	
	
	
	
	
	
	/**
	 * Send email.
	 *
	 * @param obj the obj
	 */
	private void sendEmail(Object obj) {
		// send Mail to Admin with Package in Error
		logger.debug("Trying to send email");
		String subject = "[" + "da-nrw".toUpperCase() +  "] Problem Report für " + obj.getIdentifier() + " auf " + localNodeId;
		if (nodeAdminEmail != null && !nodeAdminEmail.equals("")) {
			try {
				Mail.sendAMail(systemFromEmailAddress, nodeAdminEmail, subject, "Es gibt ein Problem mit dem Objekt an Ihrem Knoten " + obj.getContractor().getShort_name()+ "/" + obj.getIdentifier());
			} catch (MessagingException e) {
				logger.error("Sending email problem report for " + obj.getIdentifier() + "failed");
			}
		} else {
			logger.error("Node Admin has no valid Email address!");
		}
	}
	
	
	
	
	
	
	/**
	 * Gets the local node name.
	 *
	 * @return the local node name
	 */
	public String getLocalNodeName() {
		return localNodeId;
	}

	/**
	 * Sets the local node name.
	 *
	 * @param localNodeName the new local node name
	 */
	public void setLocalNodeName(String localNodeName) {
		this.localNodeId = localNodeName;
	}

	/**
	 * @author Jens Peters
	 * @author Daniel M. de Oliveira
	 * @param obj the obj
	 * @return the new object state. Either archivedAndValidState or errorState.
	 */
	int checkObjectValidity(Object obj) {
		if (minNodes == null || minNodes ==0) throw new IllegalStateException("minNodes not set correctly!");
		Node node = new Node("tobefactoredout");
		StoragePolicy sp = new StoragePolicy(node);
		sp.setMinNodes(minNodes);
		
		boolean completelyValid = true;
		for (Package pack : obj.getPackages()) {
			String dao = obj.getContractor().getShort_name()+"/"+obj.getIdentifier()+"/"+obj.getIdentifier()+".pack_" + pack.getName()+".tar"; 
			logger.debug("Checking: " + dao );
			if (!gridFacade.isValid(dao)) {
				logger.error("SEVERE FAULT " + dao + " is not valid, Checksum could not be verified on all systems!" );
				completelyValid = false;
				continue;
			}
			if (!gridFacade.storagePolicyAchieved(dao, sp)) {
				completelyValid = false;
				logger.error("FAULT " + dao + " has not achieved its replication policy!" );
				continue;
			}
		}
		if (completelyValid) return ObjectState.archivedAndValidState;
		else return ObjectState.Error;
	}

	/**
	 * Gets the irods grid connector.
	 *
	 * @return the irods grid connector
	 */
	public GridFacade getGridFacade() {
		return gridFacade;
	}

	/**
	 * Sets the irods grid connector.
	 *
	 * @param gridFacade the new irods grid connector
	 */
	public void setGridFacade(GridFacade gridFacade) {
		this.gridFacade = gridFacade;
	}

	/**
	 * Gets the min nodes.
	 *
	 * @return the min nodes
	 */
	public Integer getMinNodes() {
		return minNodes;
	}
	
	/**
	 * Gets the node admin email.
	 *
	 * @return the node admin email
	 */
	public String getNodeAdminEmail() {
		return nodeAdminEmail;
	}

	/**
	 * Sets the node admin email.
	 *
	 * @param nodeAdminEmail the new node admin email
	 */
	public void setNodeAdminEmail(String nodeAdminEmail) {
		this.nodeAdminEmail = nodeAdminEmail;
	}


	public String getLocalNodeId() {
		return localNodeId;
	}

	public void setLocalNodeId(String localNodeId) {
		this.localNodeId = localNodeId;
	}

	/**
	 * Sets the min nodes.
	 *
	 * @param minNodes the new min nodes
	 */
	public void setMinNodes(Integer minNodes) {
		this.minNodes = minNodes;
	}

	public String getSystemFromEmailAddress() {
		return systemFromEmailAddress;
	}

	public void setSystemFromEmailAddress(String systemFromEmailAddress) {
		this.systemFromEmailAddress = systemFromEmailAddress;
	}

	public CentralDatabaseDAO getDao() {
		return dao;
	}

	public void setDao(CentralDatabaseDAO dao) {
		this.dao = dao;
	}

}
