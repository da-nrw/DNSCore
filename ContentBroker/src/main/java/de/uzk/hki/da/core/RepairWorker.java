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
 * Try to repair corrupted packages from replications 
 * 
 * @author Josef Hammer
 *
 */
public class RepairWorker extends Worker{

	private String localNodeId;
	private String zoneName;
	private String replDestinations;
	
	/** The irods grid connector. */
	private GridFacade gridFacade;

	private RepairService repairService;
	
	public void init(){
		Node node = new Node(); 
		PreservationSystem pSystem;
		
		node.setId(Integer.parseInt(localNodeId));
		pSystem = new PreservationSystem(); 
		pSystem.setId(1);

		Session session = HibernateUtil.openSession();
		session.refresh(pSystem);
		session.refresh(node);
		session.close();
		
		this.repairService = new RepairService(pSystem, node, this.zoneName, this.replDestinations);
	}
	
	@Override
	public void setMDC() {
		MDC.put(WORKER_ID, "repair");
	} 
	
	
	@Override
	public void scheduleTaskImplementation(){
		try {
			
			Object object = this.repairService.fetchObjectForAudit();
			if (object == null) { 
				logger.debug("Found no object to audit.") ;
				return;
			}
						
			if (repairService.estimateChecksumms(object)) {
				logger.debug("checked OK : " + object.getIdentifier());
			}
		
			this.repairService.unlockObject(object);
			
		} catch (Exception e) {
			logger.error("Error in repairWorker schedule Task " + e.getMessage(),e);
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

	public RepairService getRepairService() {
		return this.repairService;
	}

	public void setRepairService(RepairService is) {
		this.repairService = is;
	}

	public String getZoneName() {
		return zoneName;
	}

	public void setZoneName(String zoneName) {
		this.zoneName = zoneName;
	}

	public String getReplDestinations() {
		return replDestinations;
	}

	public void setReplDestinations(String replDestinations) {
		this.replDestinations = replDestinations;
	}

}
