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
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uzk.hki.da.model.StoragePolicy;

/**
 * The Class FederationExecutor.
 * Does the iPUT to connected iRODS Servers.
 * There could be a rule based federations as well, but we're using 
 * the decoupled iput to all of the servers connected. 
 * 
 *
 * @author Jens Peters
 */
public class FederatedCopyExecutor extends Thread {

	/** The isc. */
	private IrodsSystemConnector isc;
	
	/** The data_name. */
	private File localFile;
	
	private String gridPath;
	
	private StoragePolicy sp;
	
	/** The logger. */
	private static Logger logger = LoggerFactory
			.getLogger(FederatedCopyExecutor.class);
	
	/**
	 * Instantiates a new federation executor.
	 *
	 * @param isc the isc
	 * @param srcResc the src resc
	 * @param targetResgroups the target resgroups
	 * @param data_name the data_name
	 * @author Jens Peters
	 */
	public FederatedCopyExecutor(IrodsSystemConnector isc, File localFile, String gridPath, StoragePolicy sp){
		this.isc = isc;
		this.sp = sp;
		this.localFile = localFile;
		this.gridPath = gridPath;
	}
	
	/**
	 * Runs the federation to nodes
	 * @author Jens Peters
	 */
	@Override
	public void run() {
		logger.trace("run....");
		List<String> zp = sp.getDestinations();
		
		for (String targetFed: zp) {
			logger.info("federate to " + targetFed);
			if (!gridPath.startsWith("/")) gridPath = "/" + gridPath;
			
			String dest = "/" + targetFed + gridPath;
			
			if (!isc.collectionExists(FilenameUtils.getFullPath(dest)))
				isc.createCollection(FilenameUtils.getFullPath(dest));
				
			isc.put(localFile, dest);
			logger.debug("test");
		}
	}	
}

