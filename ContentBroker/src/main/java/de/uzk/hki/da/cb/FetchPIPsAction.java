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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import de.uzk.hki.da.action.AbstractAction;
import de.uzk.hki.da.core.IngestGate;
import de.uzk.hki.da.grid.DistributedConversionAdapter;
import de.uzk.hki.da.model.WorkArea;
import de.uzk.hki.da.util.ConfigurationException;
import de.uzk.hki.da.utils.Path;

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
	private static final String UNDERSCORE = "_";
	private IngestGate ingestGate; /*unused*/
	private DistributedConversionAdapter distributedConversionAdapter;
	
	@Override
	public void checkConfiguration() {
		if (distributedConversionAdapter==null) throw new ConfigurationException("distributedConversionAdapter");
	}
	

	@Override
	public void checkPreconditions() {
	}
	

	@Override
	public boolean implementation() throws FileNotFoundException, IOException {

		deletePreviousPIPs(); // Must be done at the beginning to make sure rollback() can act properly
		
//		TODO check if source exists
		distributedConversionAdapter.replicateToLocalNode(
				makeRelativePIPSourceFolder(WorkArea.PUBLIC).toString(), n);
		distributedConversionAdapter.replicateToLocalNode(
				makeRelativePIPSourceFolder(WorkArea.WA_INSTITUTION).toString(),n);
		
		
		if (makePIPSourceFolder(WorkArea.PUBLIC).exists()) {
			logger.info(INFO_MSG_REPLICATED_SUCESSFULLY+makePIPSourceFolder(WorkArea.PUBLIC));

//          The rename is necessary because at the moment we donl't have another possibility to delete or trim the irods
//          collections on specific resources.
			FileUtils.moveDirectory(
					makePIPSourceFolder(WorkArea.PUBLIC), 
					makePIPFolder(WorkArea.PUBLIC));
		}
		if (makePIPSourceFolder(WorkArea.WA_INSTITUTION).exists()) {
			logger.info(INFO_MSG_REPLICATED_SUCESSFULLY+makePIPSourceFolder(WorkArea.WA_INSTITUTION));
			
			FileUtils.moveDirectory(
					makePIPSourceFolder(WorkArea.WA_INSTITUTION), 
					makePIPFolder(WorkArea.WA_INSTITUTION));
		}
		
		distributedConversionAdapter.remove(makeRelativePIPSourceFolder(WorkArea.PUBLIC).toString());
		distributedConversionAdapter.remove(makeRelativePIPSourceFolder(WorkArea.WA_INSTITUTION).toString());

		return true;
	}

	@Override
	public void rollback() throws Exception {
		
		if (makePIPFolder(WorkArea.PUBLIC).exists()) {
			logger.info(INFO_MSG_MOVED_BACK_PIP+makePIPSourceFolder(WorkArea.PUBLIC));

			FileUtils.moveDirectory(
					makePIPFolder(WorkArea.PUBLIC), 
					makePIPSourceFolder(WorkArea.PUBLIC)); // we know that this is possible to write to this location since we cleared it at beginning of implementation() 
		}
		if (makePIPFolder(WorkArea.WA_INSTITUTION).exists()) {
			logger.info(INFO_MSG_MOVED_BACK_PIP+makePIPSourceFolder(WorkArea.WA_INSTITUTION));
			
			FileUtils.moveDirectory(
					makePIPFolder(WorkArea.WA_INSTITUTION), 
					makePIPSourceFolder(WorkArea.WA_INSTITUTION));
		}
	}

	private File makePIPFolder(String pipType) {
		return Path.makeFile(n.getWorkAreaRootPath(),WorkArea.PIPS,pipType,o.getContractor().getShort_name(),o.getIdentifier());
	}
	
	private File makePIPSourceFolder(String pipType) {
		return Path.makeFile(n.getWorkAreaRootPath(),WorkArea.PIPS,pipType,o.getContractor().getShort_name(),o.getIdentifier()+UNDERSCORE+o.getLatestPackage().getId());
	}
	
	private File makeRelativePIPSourceFolder(String pipType) {
		return Path.makeFile(WorkArea.PIPS,pipType,o.getContractor().getShort_name(),o.getIdentifier()+UNDERSCORE+o.getLatestPackage().getId());
	}
	


	private void deletePreviousPIPs() throws IOException{	
		
		if (makePIPFolder(WorkArea.PUBLIC).exists());
			FileUtils.deleteDirectory(makePIPFolder(WorkArea.PUBLIC));
		
		if (makePIPFolder(WorkArea.WA_INSTITUTION).exists())
			FileUtils.deleteDirectory(makePIPFolder(WorkArea.WA_INSTITUTION));
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
