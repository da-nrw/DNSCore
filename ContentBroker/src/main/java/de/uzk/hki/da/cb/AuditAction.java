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

import org.apache.commons.lang.NotImplementedException;

import de.uzk.hki.da.action.AbstractAction;
import de.uzk.hki.da.core.ConfigurationException;
import de.uzk.hki.da.core.RelativePath;
import de.uzk.hki.da.grid.GridFacade;
import de.uzk.hki.da.model.Job;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.model.Package;
import de.uzk.hki.da.model.StoragePolicy;
import de.uzk.hki.da.service.MailContents;

/**
 * Audit Action.
 * Asks Storage Layer about the data integrity of an Object 
 * informs Node Admin if an error is found
 *  
 * @author Jens Peters
 */

public class AuditAction extends AbstractAction {

	
	private String nodeAdminEmail;
	private GridFacade gridRoot;

	
	@Override
	public void checkActionSpecificConfiguration() throws ConfigurationException {
		if (getGridRoot()==null) throw new ConfigurationException("gridRoot not set");
	}

	@Override
	public void checkSystemStatePreconditions() throws IllegalStateException {
		if (nodeAdminEmail == null) throw new ConfigurationException("nodeAdminEmail is null!");
		if (preservationSystem.getMinRepls()==0) throw new ConfigurationException("minNodes, 0 is not allowed!");
		if (preservationSystem.getAdmin().getEmailAddress()==null)  throw new ConfigurationException("systemFromEmailAdress is not set!");
	}

	/*
	 * 
	 * (non-Javadoc)
	 * @see de.uzk.hki.da.cb.AbstractAction#implementation()
	 */
	@Override
	public boolean implementation() {
		setKILLATEXIT(true);
		setObjectState(job,Object.ObjectStatus.UnderAudit);
		StoragePolicy sp = new StoragePolicy(localNode);
		sp.setMinNodes(preservationSystem.getMinRepls());
		
		String msg= "";
		// TODO: refactor to same implementation IntegrityScanner uses
		boolean completelyValid = true; 
		for (Package pack : object.getPackages()) {
			String pname = pack.getName();
			if (pname.equals("")) pname = "1";
			String logicalPath = new RelativePath(object.getContractor().getShort_name(), object.getIdentifier(), object.getIdentifier()).toString() + ".pack_"+pname+".tar";
			if (!gridRoot.isValid(logicalPath)) {
				msg+="SEVERE FAULT " + logicalPath + " is not valid, Checksum could not be verified on all systems! Please refer to the Storage Layer logs for further information! \n";
				 completelyValid = false;
				continue;
			}
			if (!gridRoot.storagePolicyAchieved(logicalPath, sp)) {
				msg+="FAULT " + logicalPath + " has not achieved its replication policy!\n";
				completelyValid = false;
				continue;
			} 
		}
		if (completelyValid) {
			logger.debug("Object checked OK, setting object state to 100");
			object.setObject_state(Object.ObjectStatus.ArchivedAndValid);
		} else {
			object.setObject_state(Object.ObjectStatus.Error);
			logger.error("Object " + object.getIdentifier()  + " has following errors :" +  msg);
			unloadAndRepair(object);
			new MailContents(preservationSystem,localNode).auditInformNodeAdmin(object, msg);
		}		
		return true;
	}

	@Override
	public void rollback() throws Exception {
		throw new NotImplementedException("No rollback implemented for this action");
	}
	
	
	
	
	
	void unloadAndRepair( Object obj) {
		// TODO TBD unload and Deep Check & repair Package on node
	}
	
	public void setObjectState(Job job, int state) {	
		if (object!=null) { 
			object.setObject_state(state);
		}
		
	}

	/**
	 * @param nodeAdminEmail the nodeAdminEmail to set
	 */
	public void setNodeAdminEmail(String nodeAdminEmail) {
		this.nodeAdminEmail = nodeAdminEmail;
	}

	/**
	 * @return the nodeAdminEmail
	 */
	public String getNodeAdminEmail() {
		return nodeAdminEmail;
	}
	

	public GridFacade getGridRoot() {
		return gridRoot;
	}

	public void setGridRoot(GridFacade gridRoot) {
		this.gridRoot = gridRoot;
	}
}
