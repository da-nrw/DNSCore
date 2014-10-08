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

import java.io.IOException;

import org.apache.commons.lang.NotImplementedException;

import de.uzk.hki.da.action.AbstractAction;
import de.uzk.hki.da.core.ConfigurationException;
import de.uzk.hki.da.core.IngestGate;
import de.uzk.hki.da.grid.GridFacade;

/**
 * @author Daniel M. de Oliveira
 */

public class ObjectToWorkAreaAction extends AbstractAction {

	private IngestGate ingestGate;
	private GridFacade gridFacade;
	
	public ObjectToWorkAreaAction(){SUPPRESS_OBJECT_CONSISTENCY_CHECK = true;}
	
	@Override
	public void checkActionSpecificConfiguration() throws ConfigurationException {
		// Auto-generated method stub
		
	}

	@Override
	public void checkSystemStatePreconditions() throws IllegalStateException {
		// Auto-generated method stub
	}

	@Override
	public boolean implementation() {
		
		object.getDataPath().toFile().mkdirs();
		
		RetrievePackagesHelper retrievePackagesHelper = new RetrievePackagesHelper(getGridFacade());
		
		try {
			if (!ingestGate.canHandle(retrievePackagesHelper.getObjectSize(object, job))) {
				logger.warn("ResourceMonitor prevents further processing of package due to space limitations. Setting job back to start state.");
				return false;
			}
		} catch (IOException e) {
			throw new RuntimeException("Failed to determine object size for object " + object.getIdentifier(), e);
		}
		
		try {
			retrievePackagesHelper.loadPackages(object, true);
		} catch (IOException e) {
			throw new RuntimeException("error while trying to get existing packages from lza area",e);
		}
		
//		distributedConversionAdapter.register("work/"+object.getContractor().getShort_name()+"/"+object.getIdentifier(),
//				object.getPath().toString());
		return true;
	}

	
	
	
	
	@Override
	public void rollback() throws Exception {
		throw new NotImplementedException("No rollback implemented for this action");
	}





	public IngestGate getIngestGate() {
		return ingestGate;
	}





	public void setIngestGate(IngestGate ingestGate) {
		this.ingestGate = ingestGate;
	}




	public GridFacade getGridFacade() {
		return gridFacade;
	}





	public void setGridFacade(GridFacade gridFacade) {
		this.gridFacade = gridFacade;
	}

}
