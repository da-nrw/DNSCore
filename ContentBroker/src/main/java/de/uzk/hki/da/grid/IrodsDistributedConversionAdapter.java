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

package de.uzk.hki.da.grid;

import java.io.File;

import de.uzk.hki.da.core.ConfigurationException;
import de.uzk.hki.da.utils.Path;

/**
 * 
 * @author Daniel M. de Oliveira
 *
 */
public class IrodsDistributedConversionAdapter implements DistributedConversionAdapter {

	private IrodsSystemConnector irodsSystemConnector;
	private Path zone;
	private String workingResource;
	
	@Override
	public void register(String relativePath, String physicalPath) {
		if (irodsSystemConnector==null) throw new ConfigurationException("irodsSystemConnector not set");
		if (zone==null||zone.toString().isEmpty()) throw new ConfigurationException("zonePath not set");
		if (getWorkingResource()==null||getWorkingResource().isEmpty()) throw new ConfigurationException("working resource not set");
		
		if (!irodsSystemConnector.connect()){
			throw new RuntimeException("Couldn't establish iRODS-Connection");
		}
		
		try {
			
			irodsSystemConnector.registerFilesInCollection(
					Path.make(zone,relativePath).toString(),
					new File(physicalPath),
					workingResource
					);
		} finally
		{
			irodsSystemConnector.logoff();
		}
		
	}

	@Override
	public void replicateToLocalNode(String relativePath) {
		
		if (!irodsSystemConnector.connect()){
			throw new RuntimeException("Couldn't establish iRODS-Connection");
		}
		
		try {
			
			irodsSystemConnector.replicateCollectionToResource(
					Path.make(zone,relativePath).toString(),
					workingResource
					);
			
		} finally
		{
			irodsSystemConnector.logoff();
		}
	}
	
	
	@Override
	public void remove(String relativePath) {
		if (irodsSystemConnector==null) throw new ConfigurationException("irodsSystemConnector not set");
		if (zone==null||zone.toString().isEmpty()) throw new ConfigurationException("zonePath not set");
		if (getWorkingResource()==null||getWorkingResource().isEmpty()) throw new ConfigurationException("working resource not set");
		
		
		if (!irodsSystemConnector.connect()){
			throw new RuntimeException("Couldn't establish iRODS-Connection");
		}
		
		try	{
			irodsSystemConnector.removeCollectionAndEatException(Path.make(zone,relativePath).toString());
		} 
		finally {
			irodsSystemConnector.logoff();
		}
	}
	
	
	/**
	 * 
	 */
	@Override
	public void create(String relativePath) {
		
		if (!irodsSystemConnector.connect()){
			throw new RuntimeException("Couldn't establish iRODS-Connection");
		}
		
		try	{
			getIrodsSystemConnector().createCollection(
					Path.make(zone,relativePath).toString());
		} 
		finally {
			irodsSystemConnector.logoff();
		}
	}
	
	
	

	public IrodsSystemConnector getIrodsSystemConnector() {
		return irodsSystemConnector;
	}

	public void setIrodsSystemConnector(IrodsSystemConnector irodsSystemConnector) {
		this.irodsSystemConnector = irodsSystemConnector;
	}

	public String getZonePath() {
		return zone.toString();
	}

	public void setZonePath(String zonePath) {
		this.zone = Path.make(zonePath);
	}

	public String getWorkingResource() {
		return workingResource;
	}

	public void setWorkingResource(String workingResource) {
		this.workingResource = workingResource;
	}
}
