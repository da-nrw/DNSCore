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
import de.uzk.hki.da.core.MailContents;
import de.uzk.hki.da.grid.GridFacade;
import de.uzk.hki.da.model.Job;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.model.Package;
import de.uzk.hki.da.model.StoragePolicy;
import de.uzk.hki.da.util.ConfigurationException;
import de.uzk.hki.da.util.RelativePath;

/**
 * Audit Action.
 * Asks Storage Layer about the data integrity of an Object 
 * informs Node Admin if an error is found
 *  
 * @author Jens Peters
 */

public class AuditAction extends AbstractAction {

	private GridFacade gridRoot;

	public AuditAction() {
		setKILLATEXIT(true);
	}
	
	@Override
	public void checkConfiguration() {
		if (getGridRoot()==null) throw new ConfigurationException("gridRoot");
	}
	

	@Override
	public void checkPreconditions() {
	}
	
	/*
	 * 
	 * (non-Javadoc)
	 * @see de.uzk.hki.da.cb.AbstractAction#implementation()
	 */
	@Override
	public boolean implementation() {
		setObjectState(j,Object.ObjectStatus.UnderAudit);
		StoragePolicy sp = new StoragePolicy();
		sp.setMinNodes(preservationSystem.getMinRepls());
		
		String msg= "";
		// TODO: refactor to same implementation IntegrityScanner uses
		boolean completelyValid = true; 
		for (Package pack : o.getPackages()) {
			String pname = pack.getName();
			if (pname.equals("")) pname = "1";
			String logicalPath = new RelativePath(o.getContractor().getShort_name(), o.getIdentifier(), o.getIdentifier()).toString() + ".pack_"+pname+".tar";
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
			o.setObject_state(Object.ObjectStatus.ArchivedAndValid);
		} else {
			o.setObject_state(Object.ObjectStatus.Error);
			logger.error("Object " + o.getIdentifier()  + " has following errors :" +  msg);
			unloadAndRepair(o);
			new MailContents(preservationSystem,n).auditInformNodeAdmin(o, msg);
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
		if (o!=null) { 
			o.setObject_state(state);
		}
		
	}
	public GridFacade getGridRoot() {
		return gridRoot;
	}

	public void setGridRoot(GridFacade gridRoot) {
		this.gridRoot = gridRoot;
	}
}
