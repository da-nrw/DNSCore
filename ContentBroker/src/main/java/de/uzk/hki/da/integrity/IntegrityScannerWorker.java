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

package de.uzk.hki.da.integrity;

import java.util.Date;

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
 *
 */
public class IntegrityScannerWorker {

	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(IntegrityScannerWorker.class);
	
	/** The dao. */
	private CentralDatabaseDAO dao = null;
	
	/** The irods grid connector. */
	private GridFacade gridFacade;
	
	/** The min nodes. */
	private Integer minNodes;
	
	/** The node admin email. */
	private String nodeAdminEmail;
	
	private String systemName;
	
	/** The error state. */
	private Integer errorState = 51;

	/**
	 * Inits the.
	 */
	public void init(){
		
		logger.info("Scanning Table Objects for objects to audit!");
		
	}
	
	/** The local node name. */
	private String localNodeName;
	
	/**
	 * Checking for the AIPs related to this node.
	 */
	public void scheduleTask(){
		try {
			if (getDao()==null) {
				logger.warn("dao is not set yet");
				return;
			}
			logger.debug("Scanning AIP s of node " + localNodeName );
			Session session = HibernateUtil.openSession();
			session.beginTransaction();
			Object obj = getDao().getObjectNeedAudit(session,localNodeName, errorState);
			
			if (obj==null) { 
				logger.warn("There seems to be none object to check: Database setup?") ;
				return;
			}
			
			session.beginTransaction();
			obj.setObject_state(60); // in audit state 
			session.update(obj);
			session.getTransaction().commit();
			
			obj.setObject_state(checkObject(obj));
			if (obj.getObject_state()==errorState) {
				sendEmail(obj);
			}
			obj.setLast_checked(new Date());
			
			session.beginTransaction();
			session.merge(obj);
			session.getTransaction().commit();
			session.close();
			
			
		} catch (Exception e) {
			logger.error("Error in integrityCheck schedule Task " + e.getMessage(),e);
		}
	}
	
	
	
	
	/**
	 * Send email.
	 *
	 * @param obj the obj
	 */
	void sendEmail(Object obj) {
		// send Mail to Admin with Package in Error

		String subject = "[" + systemName.toUpperCase() +  "] Problem Report für " + obj.getIdentifier() + " auf " + localNodeName;
		if (nodeAdminEmail != null && !nodeAdminEmail.equals("")) {
			try {
				Mail.sendAMail(nodeAdminEmail, subject, "Es gibt ein Problem mit dem Objekt " + obj.getContractor().getShort_name()+ "/" + obj.getIdentifier());
			} catch (MessagingException e) {
				logger.error("Sending email problem report for " + obj.getIdentifier() + "failed");
			}
		} else {
			logger.warn("Node Admin has no valid Email address!");
		}
	}
	
	
	
	
	
	
	/**
	 * Gets the local node name.
	 *
	 * @return the local node name
	 */
	public String getLocalNodeName() {
		return localNodeName;
	}

	/**
	 * Sets the local node name.
	 *
	 * @param localNodeName the new local node name
	 */
	public void setLocalNodeName(String localNodeName) {
		this.localNodeName = localNodeName;
	}

	/**
	 * Side effect: set objects state to 100 if complete object is valid and policies achieved.
	 * or 0 if not valid or policies not achieved for any of the objects packages.
	 * @author Jens Peters
	 * @author Daniel M. de Oliveira
	 * @param obj the obj
	 * @return the object state
	 */
	int checkObject(Object obj) {
		Node node = new Node("tobefactoredout");
		StoragePolicy sp = new StoragePolicy(node);
		
		if (minNodes == null || minNodes ==0) throw new IllegalStateException("minNodes not set correctly!");
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
		if (completelyValid) return 100;
		else return errorState;
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


	/**
	 * Sets the min nodes.
	 *
	 * @param minNodes the new min nodes
	 */
	public void setMinNodes(Integer minNodes) {
		this.minNodes = minNodes;
	}




	public CentralDatabaseDAO getDao() {
		return dao;
	}




	public void setDao(CentralDatabaseDAO dao) {
		this.dao = dao;
	}




	/**
	 * @return the zoneName
	 */
	public String getSystemName() {
		return systemName;
	}




	/**
	 * @param zoneName the zoneName to set
	 */
	public void setSystemName(String zoneName) {
		this.systemName = zoneName;
	}
	
	
	
	
	
}
