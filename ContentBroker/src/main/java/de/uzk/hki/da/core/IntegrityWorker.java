/*
  DA-NRW Software Suite | ContentBroker
  Copyright (C) 2015 LVR  InfoKom

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

import org.hibernate.Session;
import org.slf4j.MDC;

import de.uzk.hki.da.grid.GridFacade;
import de.uzk.hki.da.model.Node;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.model.PreservationSystem;
import de.uzk.hki.da.service.HibernateUtil;


/**
 * Scans the Integrity of AIP Files stored in the Grid.
 * Integrity is classified as number of Repls is reached
 * and Checksum is of all replicas is correct. 
 * 
 * @author Jens Peters
 *
 */
public class IntegrityWorker extends Worker{

	private String localNodeId;
	
	private PreservationSystem pSystem;
	private Node node;
	/** The irods grid connector. */
	private GridFacade gridFacade;

	private IntegrityService is;
	
	public void init(){
		node = new Node(); 
		node.setId(Integer.parseInt(localNodeId));
		setpSystem(new PreservationSystem()); getPSystem().setId(1);
		Session session = HibernateUtil.openSession();
		session.beginTransaction();
		session.refresh(getPSystem());
		session.refresh(node);
		session.getTransaction().commit();
		session.close();
		
		is = new IntegrityService();
		is.setGridFacade(gridFacade);
	}
	
	@Override
	public void setMDC() {
		MDC.put(WORKER_ID, "integrity");
	} 
	
	
	/**
	 * Checking for the AIPs related to this node.
	 * @author Daniel M. de Oliveira
	 * @author Jens Peters
	 */
	@Override
	public void scheduleTaskImplementation(){
		logger.trace("Scanning AIP s of node " + localNodeId );

		try {
			
			Object object = null;
			if ((object=is.fetchObjectForAudit(node.getName()))==null) { 
				logger.warn("Found no object to audit.") ;
				return;
			}
						
			if (!is.checkObject(object, getPSystem().getMinRepls())) {
				is.sendEmail(node.getName(), object,getPSystem().getAdmin().getEmailAddress(),
						node.getAdmin().getEmailAddress(), node.getAdmin().isMailsPooled());
			} else logger.info("checked OK : " + object.getIdentifier());
		
			
		} catch (Exception e) {
			logger.error("Error in integrityCheck schedule Task " + e.getMessage(),e);
		}
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

	/**
	 * For testing purposes only
	 * @param node
	 */
	public void setNode(Node node){
		this.node = node;
	}

	public IntegrityService getIs() {
		return is;
	}

	public void setIs(IntegrityService is) {
		this.is = is;
	}

}
