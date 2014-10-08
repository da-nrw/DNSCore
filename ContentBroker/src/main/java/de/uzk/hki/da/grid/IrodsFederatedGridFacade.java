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
 * iRODS Servers. Depends on special configuration on your grid
 */
import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uzk.hki.da.core.C;
import de.uzk.hki.da.model.StoragePolicy;
import de.uzk.hki.da.utils.MD5Checksum;



/**
 * The Class IrodsFederatedGridFacade.
 */
public class IrodsFederatedGridFacade extends IrodsGridFacade {

	/** The logger. */
	private static Logger logger = LoggerFactory
			.getLogger(IrodsFederatedGridFacade.class);
	
	

	/* (non-Javadoc)
	 * @see de.uzk.hki.da.grid.IrodsGridFacadeBase#put(java.io.File, java.lang.String)
	 */
	@Override
	public boolean put(File file, String gridPath , StoragePolicy sp) throws IOException {
		boolean ret = false;
		ret = super.put(file, gridPath, sp);
		gridPath = "/" + irodsSystemConnector.getZone() + "/" + C.WA_AIP + gridPath;
		irodsSystemConnector.connect();
		if (sp.getForbiddenNodes()!=null && !sp.getForbiddenNodes().isEmpty()) irodsSystemConnector.saveOrUpdateAVUMetadataDataObject(gridPath, "FORBIDDEN_NODES", String.valueOf(sp));
		irodsSystemConnector.saveOrUpdateAVUMetadataDataObject(gridPath, "MIN_COPIES", String.valueOf(sp.getMinNodes()));
		irodsSystemConnector.saveOrUpdateAVUMetadataDataObject(gridPath, "FEDERATED", "0");
		
		irodsSystemConnector.logoff();
		return ret;
	}
	
	@Override
	public boolean storagePolicyAchieved(String gridPath2, StoragePolicy sp) {
		int minNodes = sp.getMinNodes();
		
		if (minNodes == 0 ) {
			logger.error("Given minnodes setting 0 violates long term preservation");
			return false;
		}
		irodsSystemConnector.connect();
		
		String gridPath = "/" + irodsSystemConnector.getZone() + "/" + C.WA_AIP + "/" + gridPath2;
		
		String number = irodsSystemConnector.executeRule("checkNumber { \n " +
				"*numberOfCopies=0;\n" +
				"acGetNumberOfCopies(*dao,*numberOfCopies);\n"
				+"}\n"
				+"INPUT *dao=\""+gridPath+"\"\n"
				+"OUTPUT *numberOfCopies","*numberOfCopies");
				logger.debug("iRODS tells us, file " +gridPath+ " has already >" + number +"< Copies");
		if (number!=null && !number.isEmpty()) {
			int nr = 0;
			try {
				nr = Integer.parseInt(number);
			} catch (NumberFormatException e) {
				logger.warn("Could not determine Integer out of Value " + number);
			}
			if (nr>= minNodes) {
				logger.debug ("Reached number of Copies :" + nr);
				irodsSystemConnector.logoff();
				return true;
			} 
		}
		irodsSystemConnector.logoff();
		return false;
	}
	
	public boolean isValid(String gridPath, StoragePolicy sp, String md5Checksum) {
		irodsSystemConnector.connect();	
		String check = "checkItemsQuick {\n"
	      + "*status=0\n"
	      + "acIsValid(*dao,*status)\n"
	      + "}\n"
	      + "INPUT *dao=\"" + gridPath +"\"\n"
	      + "OUTPUT *status";
		if (check!=null && !check.isEmpty() ) {
			if (check.equals("1")) return true;
		}	
		irodsSystemConnector.logoff();
		return false;
	}
}
