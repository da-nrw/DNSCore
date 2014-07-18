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

/**
 * @author Jens Peters
 * The Federated Grid Facade for having a Federation of independent 
 * iRODS Servers 
 */
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uzk.hki.da.model.StoragePolicy;



/**
 * The Class IrodsFederatedGridFacade.
 */
public class IrodsFederatedGridFacade extends IrodsGridFacade {

	/** The logger. */
	private static Logger logger = LoggerFactory
			.getLogger(IrodsFederatedGridFacade.class);
	

	/* (non-Javadoc)
	 * @see de.uzk.hki.da.grid.IrodsGridFacade#put(java.io.File, java.lang.String, de.uzk.hki.da.model.StoragePolicy)
	 */
	public boolean put (File file, String gridPath, StoragePolicy sp){
		irodsSystemConnector.connect();
		
		Thread  re = new FederatedCopyExecutor(irodsSystemConnector,file, gridPath, sp);
		re.start();
		try {
			re.join();
			irodsSystemConnector.logoff();
			return true;
		} catch (InterruptedException e) {
			irodsSystemConnector.logoff();
			logger.error("catched Interrupted Exception in joining FederatedCopyExecutor() " + e.getMessage());
			return false;
		}
	}	


	/* (non-Javadoc)
	 * @see de.uzk.hki.da.grid.IrodsGridFacade#storagePolicyAchieved(java.lang.String, de.uzk.hki.da.model.StoragePolicy)
	 */
	public boolean storagePolicyAchieved(String gridPath, StoragePolicy sp) {
		irodsSystemConnector.connect();
		
		int i = 0;
		List<String> zp = sp.getDestinations();
		for (String zone : zp){
			if (irodsSystemConnector.fileExists("/"+ zone + gridPath));
			i++;
		}
		if (i>=sp.getMinNodes()) {
			irodsSystemConnector.logoff();
			return true;
		}
		irodsSystemConnector.logoff();
		return false;
	}
	
	public boolean isValid(String gridPath, StoragePolicy sp, String md5Checksum) {
		irodsSystemConnector.connect();

		int i = 0;
		
		List<String> zp = sp.getDestinations();
		for (String zone : zp){
			try {
				String cs = irodsSystemConnector.getChecksum("/"+ zone + gridPath);
					if (cs.equals(md5Checksum)) {
						i++;
					}
				} catch (IrodsRuntimeException e) {
				// TODO : this is an exception, which has to be sent to the nodeamdin!!
				logger.error("FAULTY Checksum " + e.getMessage());
			}
			
		}
		if (i>=sp.getMinNodes()) {
			irodsSystemConnector.logoff();
			return true;
		}
		irodsSystemConnector.logoff();
	
		
		
		
		return false;
	}
}
