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

import de.uzk.hki.da.action.AbstractAction;
import de.uzk.hki.da.core.IntegrityService;
import de.uzk.hki.da.grid.GridFacade;
import de.uzk.hki.da.model.Job;
import de.uzk.hki.da.util.ConfigurationException;

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
		IntegrityService is = new IntegrityService();
		is.setGridFacade(gridRoot);
		is.checkObject(o, preservationSystem.getMinRepls());
		return true;
	}

	@Override
	public void rollback() throws Exception {
		// nothing to do
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
