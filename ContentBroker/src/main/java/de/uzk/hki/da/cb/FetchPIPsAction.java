/*
  DA-NRW Software Suite | ContentBroker
  Copyright (C) 2013 Historisch-Kulturwissenschaftliche Informationsverarbeitung
  Universität zu Köln
  Copyright (C) 2014 LVRInfoKom
  Landschaftsverband Rheinland

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

import static de.uzk.hki.da.core.C.WA_INSTITUTION;
import static de.uzk.hki.da.core.C.WA_PIPS;
import static de.uzk.hki.da.core.C.WA_PUBLIC;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import de.uzk.hki.da.action.AbstractAction;
import de.uzk.hki.da.core.IngestGate;
import de.uzk.hki.da.grid.DistributedConversionAdapter;
import de.uzk.hki.da.util.ConfigurationException;
import de.uzk.hki.da.util.Path;

/**
 * Fetches the PIPs from the nodes on which they've originally been created.
 * Overwrites any previously generated PIPs.
 * 
 * @author Daniel M. de Oliveira
 * @author Jens Peters
 */
public class FetchPIPsAction extends AbstractAction {

	private static final String INFO_MSG_MOVED_BACK_PIP = "Moved back PIP: ";
	private static final String INFO_MSG_REPLICATED_SUCESSFULLY = "Replicated sucessfully: ";
	private static final String ERR_MSG_DISTRIBUTED_CONVERSION_ADAPTER_NOT_SET = "DistributedConversionAdapter not set";
	private static final String UNDERSCORE = "_";
	private IngestGate ingestGate; /*unused*/
	private DistributedConversionAdapter distributedConversionAdapter;
	
	@Override
	public void checkActionSpecificConfiguration() throws ConfigurationException {
		if (distributedConversionAdapter==null) throw new ConfigurationException(ERR_MSG_DISTRIBUTED_CONVERSION_ADAPTER_NOT_SET);
	}



	@Override
	public void checkSystemStatePreconditions() throws IllegalStateException {/*none*/}



	@Override
	public boolean implementation() throws FileNotFoundException, IOException {

		deletePreviousPIPs(); // Must be done at the beginning to make sure rollback() can act properly
		
//		TODO check if source exists
		distributedConversionAdapter.replicateToLocalNode(
				makeRelativePIPSourceFolder(WA_PUBLIC).toString());
		distributedConversionAdapter.replicateToLocalNode(
				makeRelativePIPSourceFolder(WA_INSTITUTION).toString());
		
		
		if (makePIPSourceFolder(WA_PUBLIC).exists()) {
			logger.info(INFO_MSG_REPLICATED_SUCESSFULLY+makePIPSourceFolder(WA_PUBLIC));

//          The rename is necessary because at the moment we donl't have another possibility to delete or trim the irods
//          collections on specific resources.
			FileUtils.moveDirectory(
					makePIPSourceFolder(WA_PUBLIC), 
					makePIPFolder(WA_PUBLIC));
		}
		if (makePIPSourceFolder(WA_INSTITUTION).exists()) {
			logger.info(INFO_MSG_REPLICATED_SUCESSFULLY+makePIPSourceFolder(WA_INSTITUTION));
			
			FileUtils.moveDirectory(
					makePIPSourceFolder(WA_INSTITUTION), 
					makePIPFolder(WA_INSTITUTION));
		}
		
		distributedConversionAdapter.remove(makeRelativePIPSourceFolder(WA_PUBLIC).toString());
		distributedConversionAdapter.remove(makeRelativePIPSourceFolder(WA_INSTITUTION).toString());

		return true;
	}

	@Override
	public void rollback() throws Exception {
		
		if (makePIPFolder(WA_PUBLIC).exists()) {
			logger.info(INFO_MSG_MOVED_BACK_PIP+makePIPSourceFolder(WA_PUBLIC));

			FileUtils.moveDirectory(
					makePIPFolder(WA_PUBLIC), 
					makePIPSourceFolder(WA_PUBLIC)); // we know that this is possible to write to this location since we cleared it at beginning of implementation() 
		}
		if (makePIPFolder(WA_INSTITUTION).exists()) {
			logger.info(INFO_MSG_MOVED_BACK_PIP+makePIPSourceFolder(WA_INSTITUTION));
			
			FileUtils.moveDirectory(
					makePIPFolder(WA_INSTITUTION), 
					makePIPSourceFolder(WA_INSTITUTION));
		}
	}

	private File makePIPFolder(String pipType) {
		return Path.makeFile(n.getWorkAreaRootPath(),WA_PIPS,pipType,o.getContractor().getShort_name(),o.getIdentifier());
	}
	
	private File makePIPSourceFolder(String pipType) {
		return Path.makeFile(n.getWorkAreaRootPath(),WA_PIPS,pipType,o.getContractor().getShort_name(),o.getIdentifier()+UNDERSCORE+o.getLatestPackage().getId());
	}
	
	private File makeRelativePIPSourceFolder(String pipType) {
		return Path.makeFile(WA_PIPS,pipType,o.getContractor().getShort_name(),o.getIdentifier()+UNDERSCORE+o.getLatestPackage().getId());
	}
	


	private void deletePreviousPIPs() throws IOException{	
		
		if (makePIPFolder(WA_PUBLIC).exists());
			FileUtils.deleteDirectory(makePIPFolder(WA_PUBLIC));
		
		if (makePIPFolder(WA_INSTITUTION).exists())
			FileUtils.deleteDirectory(makePIPFolder(WA_INSTITUTION));
	}


	
	public DistributedConversionAdapter getDistributedConversionAdapter() {
		return distributedConversionAdapter;
	}



	public void setDistributedConversionAdapter(
			DistributedConversionAdapter distributedConversionAdapter) {
		this.distributedConversionAdapter = distributedConversionAdapter;
	}



	public IngestGate getIngestGate() {
		return ingestGate;
	}



	public void setIngestGate(IngestGate ingestGate) {
		this.ingestGate = ingestGate;
	}
}
