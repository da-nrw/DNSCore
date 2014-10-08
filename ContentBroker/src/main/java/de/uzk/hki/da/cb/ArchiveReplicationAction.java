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


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.NotImplementedException;

import de.uzk.hki.da.action.AbstractAction;
import de.uzk.hki.da.core.ConfigurationException;
import de.uzk.hki.da.core.Path;
import de.uzk.hki.da.grid.GridFacade;
import de.uzk.hki.da.model.StoragePolicy;

/**
 * Does the decoupled and time based Archive Replication to the given minimum number of required nodes. 
 * @author Jens Peters
 */
public class ArchiveReplicationAction extends AbstractAction {
	
	public ArchiveReplicationAction(){SUPPRESS_OBJECT_CONSISTENCY_CHECK=true;}
	
	private GridFacade gridRoot;
	
	@Override
	public void checkActionSpecificConfiguration() throws ConfigurationException {
		if (gridRoot==null) throw new ConfigurationException("gridRoot not set");
	}

	@Override
	public void checkSystemStatePreconditions() throws IllegalStateException {
		// Auto-generated method stub
	}

	@Override
	public
	boolean implementation() {
		
		String filename = object.getIdentifier() + ".pack_" + object.getLatestPackage().getName() + ".tar";
		Path target = Path.make(object.getContractor().getShort_name(), object.getIdentifier(), filename);
		StoragePolicy sp = new StoragePolicy(localNode);
		sp.setMinNodes(preservationSystem.getMinRepls());
		sp.setDestinations(new ArrayList<String>(getDestinations()));
		sp.setForbiddenNodes(object.getContractor().getForbidden_nodes());
		// TODO: this is a user/system exception!! commented out due to federation
		if (!sp.isPolicyAchievable()) {
			logger.warn("STORAGE POLICY not achievable!!");
			//throw new RuntimeException ("POLICY is not achievable!!");
		}
		
		try {
			Path newFilePath = Path.make(localNode.getWorkAreaRootPath(), "work", object.getContractor().getShort_name(), filename);
			if (gridRoot.put(new File(newFilePath.toString()), 
					target.toString(), sp )) {
					new File(newFilePath.toString()).delete();
			}
		} catch (IOException e) {
			throw new RuntimeException("Error while putting file into grid or work deletion! ",e);
		}
				
		return true;
	}
	
	@Override
	public void rollback() {
		throw new NotImplementedException("No rollback implemented for this action");
	}

	/**
	 * @author Jens Peters
	 * @author Daniel M. de Oliveira
	 * @return
	 */
	private Collection<String> getDestinations() {
		String replDestinations = "";
		String forbiddenNodes = "";
		if (localNode.getReplDestinations()!=null) replDestinations = localNode.getReplDestinations();
		if (object.getContractor().getForbidden_nodes()!=null) forbiddenNodes = object.getContractor().getForbidden_nodes();
		
		List<String> targetDest = Arrays.asList(replDestinations.split(","));
		List<String> forbidden = Arrays.asList(forbiddenNodes.split(","));
		@SuppressWarnings("unchecked")
		Collection <String>destinations = CollectionUtils.subtract(targetDest, forbidden);
		return destinations; 
	}
	
	
	public GridFacade getGridRoot() {
		return gridRoot;
	}

	public void setGridRoot(GridFacade gridRoot) {
		this.gridRoot = gridRoot;
	}
}
