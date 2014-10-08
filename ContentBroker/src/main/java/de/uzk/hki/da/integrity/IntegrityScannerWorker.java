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
import javax.naming.ConfigurationException;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uzk.hki.da.core.HibernateUtil;
import de.uzk.hki.da.grid.GridFacade;
import de.uzk.hki.da.model.Node;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.model.Package;
import de.uzk.hki.da.model.PreservationSystem;
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

	

	
	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(IntegrityScannerWorker.class);
	
	
	/** The irods grid connector. */
	private GridFacade gridFacade;
	
	/** The local node name. */
	private String localNodeId;
	
	private PreservationSystem pSystem;
	private Node node;
	
	private int retries = 3;
	
	private long sleepFor = 60*60*1000*2L;

	public void init(){
		node = new Node(); node.setId(Integer.parseInt(localNodeId));
		setpSystem(new PreservationSystem()); getPSystem().setId(1);
		Session session = HibernateUtil.openSession();
		session.beginTransaction();
		session.refresh(getPSystem());
		session.refresh(node);
		session.getTransaction().commit();
		session.close();
	}
	
	
	/**
	 * Checking for the AIPs related to this node.
	 * @author Daniel M. de Oliveira
	 * @author Jens Peters
	 */
	public void scheduleTask(){
		logger.trace("Scanning AIP s of node " + localNodeId );

		try {
			
			Object object = null;
			if ((object=fetchObjectForAudit(localNodeId))==null) { 
				logger.warn("Found no object to audit.") ;
				return;
			}
			
			Integer auditResult = checkObjectValidity(object);
			updateObject(object,auditResult);
			
			if (auditResult==Object.ObjectStatus.Error) {
				sendEmail(object);
			}
			
		} catch (Exception e) {
			logger.error("Error in integrityCheck schedule Task " + e.getMessage(),e);
		}
	}
	
	/**
	 * Determines which of the objects that the local node is responsible for 
	 * (since it holds the primary copies of them) is the one which
	 * has not been checked for the longest period of time. 
	 * 
	 * @return the next object that needs audit. null if there is no object in the database which meets the criteria.
	 * 
	 * @author Jens Peters
	 * @author Daniel M. de Oliveira
	 * 
	 */
	private synchronized Object fetchObjectForAudit(String localNodeId) {
		
		Session session = null;
		try {
			session = HibernateUtil.openSession();
			session.beginTransaction();
	
			Node node = (Node) session.get(Node.class,Integer.parseInt(localNodeId));
			
			Calendar now = Calendar.getInstance();
			now.add(Calendar.HOUR_OF_DAY, -24);
			@SuppressWarnings("rawtypes")
			List l = null;
			l = session.createQuery("from Object o where o.initial_node = ?1 and o.last_checked < ?2 and "
					+ "o.object_state != ?3 and o.object_state != ?4 and o.object_state >= 50"
					+ "order by o.last_checked asc")
					.setParameter("1", node.getName())
					.setCalendar("2",now)
					.setParameter("3", Object.ObjectStatus.InWorkflow) // don't consider objects under work
					.setParameter("4", Object.ObjectStatus.UnderAudit) //           ||
							.setReadOnly(true).list();
			
			Object objectToAudit = (Object) l.get(0);
			
			// lock object
			objectToAudit.setObject_state(Object.ObjectStatus.UnderAudit);
			session.update(objectToAudit);
			session.getTransaction().commit();
			session.close();
			
			return objectToAudit;
		
		} catch (IndexOutOfBoundsException e){
			if (session!=null) session.close();
			return null;
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
		if (node.getAdmin().getEmailAddress() != null && !node.getAdmin().getEmailAddress().equals("")) {
			try {
				Mail.sendAMail( getPSystem().getAdmin().getEmailAddress() , node.getAdmin().getEmailAddress(), subject, "Es gibt ein Problem mit dem Objekt an Ihrem Knoten " + obj.getContractor().getShort_name()+ "/" + obj.getIdentifier());
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
		if (getPSystem().getMinRepls() == null || getPSystem().getMinRepls() ==0) throw new IllegalStateException("minNodes not set correctly!");
		Node node = new Node("tobefactoredout");
		StoragePolicy sp = new StoragePolicy(node);
		sp.setMinNodes(getPSystem().getMinRepls());
		logger.debug("Check Object "+ obj.getIdentifier());
		boolean completelyValid = true;		
		if (obj.getContractor()==null) {
			String err= "Could not determine valid Contractor for object " + obj.getIdentifier();
			logger.error(err);
			return Object.ObjectStatus.Error;
		}
		String dao_base = obj.getContractor().getShort_name()+"/"+obj.getIdentifier()+"/"+obj.getIdentifier()+".pack_";
		for (Package pack : obj.getPackages()) {
			String dao = dao_base + pack.getName()+".tar"; 
			logger.debug("Checking: " + dao );
			boolean valid = gridFacade.isValid(dao);
			if (!valid) {
				logger.debug("Recieved false, retrying once again later!");
				int i = 0;
				while (i<retries && !valid) {
					try {
						Thread.sleep(sleepFor);
						logger.debug("...Waking up ");
						valid = gridFacade.isValid(dao);
						i++;
					} catch (InterruptedException e) {
						
					}
				}
				if (!valid) {
				logger.error("SEVERE FAULT " + dao + " is not valid, Checksum could not be verified on all systems!" );
				completelyValid = false;
				}
				continue;
			}
			if (!gridFacade.storagePolicyAchieved(dao, sp)) {
				completelyValid = false;
				logger.error("FAULT " + dao + " has not achieved its replication policy!" );
				continue;
			}
		}
		if (completelyValid) return Object.ObjectStatus.ArchivedAndValid;
		else return Object.ObjectStatus.Error;
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

	public String getLocalNodeId() {
		return localNodeId;
	}

	public void setLocalNodeId(String localNodeId) {
		this.localNodeId = localNodeId;
	}


	public PreservationSystem getPSystem() {
		return pSystem;
	}


	public void setpSystem(PreservationSystem pSystem) {
		this.pSystem = pSystem;
	}
	public int getRetries() {
		return retries;
	}


	public void setRetries(int retries) {
		this.retries = retries;
	}


	public long getSleepFor() {
		return sleepFor;
	}


	public void setSleepFor(long sleepFor) {
		this.sleepFor = sleepFor;
	}

}
