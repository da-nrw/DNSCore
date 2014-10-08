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
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import de.uzk.hki.da.core.C;
import de.uzk.hki.da.model.StoragePolicy;
import de.uzk.hki.da.utils.MD5Checksum;

/**
 * In this implementation the replications are done by creating a thread an doing them synchronously from the thread. 
 * @author Jens Peters
 * @author Daniel M. de Oliveira
 *
 */
public class IrodsGridFacade extends IrodsGridFacadeBase {

	/** The logger. */
	private static Logger logger = LoggerFactory
			.getLogger(IrodsGridFacade.class);
	
	
	
	/* (non-Javadoc)
	 * @see de.uzk.hki.da.grid.IrodsGridFacadeBase#put(java.io.File, java.lang.String)
	 */
	@Override
	public boolean put(File file, String gridPath , StoragePolicy sp) throws IOException {
		
		if (!irodsSystemConnector.connect()) throw new RuntimeException("could not connect irodsSystemConnector");
		
		if (!PrepareReplication(file, gridPath)) return false;
		
		String address_dest = gridPath;
		if (!gridPath.startsWith("/")) 
			address_dest = "/" + gridPath;
		String targetPhysically = localNode.getGridCacheAreaRootPath() + "/" + C.WA_AIP + address_dest;
		String targetLogically  = "/" + irodsSystemConnector.getZone() + "/" + C.WA_AIP  + address_dest;	
		File gridfile = new File (targetPhysically); 
		
		if (registerOnWorkingResourceAndComputeChecksum(file,targetLogically,gridfile))

		irodsSystemConnector.logoff();
			
		return startReplications(file, targetLogically, gridfile);
	}
	
	/**
	 * register On Working resource.
	 *
	 * @param file the file
	 * @param targetLogically the target logically
	 * @param gridfile the gridfile
	 * @return true, if successful
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private boolean registerOnWorkingResourceAndComputeChecksum(File file,
			String targetLogically, File gridfile) throws IOException {
		logger.debug("register " + gridfile + " as " + targetLogically);
		if (!irodsSystemConnector.collectionExists(FilenameUtils.getFullPath(targetLogically)))
		irodsSystemConnector.createCollection(FilenameUtils.getFullPath(targetLogically));
		irodsSystemConnector.registerFile(targetLogically, gridfile, localNode.getWorkingResource());
		if (irodsSystemConnector.fileExists(targetLogically)) {
			logger.debug("compute checksum on " + targetLogically);
			String MD5CheckSum = MD5Checksum.getMD5checksumForLocalFile(file);
			if (irodsSystemConnector.computeChecksum(targetLogically).equals(MD5CheckSum)){
				irodsSystemConnector.saveOrUpdateAVUMetadataDataObject(targetLogically, "chksum", MD5CheckSum );
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Start replications.
	 *
	 * @param file the file
	 * @param targetLogically the target logically
	 * @param gridfile the gridfile
	 * @return true, if successful
	 * @author Jens Peters
	 * @author Daniel M. de Oliveira
	 */
	

	private boolean startReplications(File file, String targetLogically,
			File gridfile) {
		logger.debug("starting replications for " + targetLogically); 
		List<String> targetResgroups = Arrays.asList(localNode.getReplDestinations().split(","));
		irodsSystemConnector.saveOrUpdateAVUMetadataDataObject(targetLogically, "replicate_to", localNode.getReplDestinations().replace("cp_",""));
		logger.debug("Starting threads for Replication to " + StringUtils.collectionToDelimitedString(targetResgroups, "|"));
		Thread  re = new ReplicationExecutor(irodsSystemConnector, localNode, targetResgroups, targetLogically);
		re.start();
		return true;
	}
	

	
	
	/* (non-Javadoc)
	 * @see de.uzk.hki.da.grid.IrodsGridFacadeBase#storagePolicyAchieved(java.lang.String, int)
	 * @author Jens Peters
	 */
	@Override
	public boolean storagePolicyAchieved(String gridPath2, StoragePolicy sp) {
		irodsSystemConnector.connect();
		
		String gridPath = "/" + irodsSystemConnector.getZone() + "/" + C.WA_AIP + "/" + gridPath2;
		
		int minNodes = sp.getMinNodes();
		if (minNodes == 0 ) {
			logger.error("Given minnodes setting 0 violates long term preservation");
			return false;
		}
		try {
			logger.debug("checking StoragePolicy achieved for " + gridPath); 
			List<String> targetResgroups = Arrays.asList(localNode.getReplDestinations().split(","));
			int replicasTotal = 0;
			for (String targetResgroup : targetResgroups) {
				int replicas = 0;
				String res = targetResgroup;
				if (targetResgroup.startsWith("cp_")) 
					res = targetResgroup.substring(3);
				replicas = irodsSystemConnector
				.getNumberOfReplsForDataObjectInResGroup(FilenameUtils.getFullPath(gridPath), 
					FilenameUtils.getName(gridPath),
					res);
				if (targetResgroup.startsWith("cp_") && replicas>1) replicas--;
				replicasTotal = replicasTotal + replicas;
			}
			logger.debug("Number of Total Replications on LZA Nodes is now ("
					+ replicasTotal + "). Total Checked Ressources " + targetResgroups.size() 
					+ " Has to exist on (" + minNodes +")");
		
			if (replicasTotal>=minNodes) { 
				irodsSystemConnector.saveOrUpdateAVUMetadataDataObject(gridPath, "replicated", "1");
				irodsSystemConnector.saveOrUpdateAVUMetadataDataObject(gridPath, "min_repls", String.valueOf(minNodes));
				irodsSystemConnector.logoff();
				return true;
			} else {
				irodsSystemConnector.saveOrUpdateAVUMetadataDataObject(gridPath, "replicated", "0");
			}
			irodsSystemConnector.logoff();
			return false;
			
		} catch (IrodsRuntimeException e) {
			logger.error("Failure acquiring repl status of " + gridPath);
			irodsSystemConnector.logoff();
		} 
		return false;
	}

}
