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
import org.apache.commons.lang.NotImplementedException;

import de.uzk.hki.da.action.AbstractAction;
import static de.uzk.hki.da.core.C.*;
import de.uzk.hki.da.core.IngestGate;
import de.uzk.hki.da.grid.DistributedConversionAdapter;
import de.uzk.hki.da.util.ConfigurationException;
import de.uzk.hki.da.util.Path;

/**
 * Fetches the PIPs from the nodes on which they've originally been created.
 * Overwrites any previously generated PIPs.
 * 
 * @author Daniel M. de Oliveira
 */
public class FetchPIPsAction extends AbstractAction {

	private IngestGate ingestGate;
	
	private DistributedConversionAdapter distributedConversionAdapter;
	
	Path publicContractorFolder = null;
	Path institutionContractorFolder = null;
	
	@Override
	public void checkActionSpecificConfiguration() throws ConfigurationException {
		if (distributedConversionAdapter==null) throw new ConfigurationException("distributedConversionAdapter not set");
	}



	@Override
	public void checkSystemStatePreconditions() throws IllegalStateException {
		// Auto-generated method stub
	}



	@Override
	public boolean implementation() throws FileNotFoundException, IOException {
		
		replicateFromSourceResourceToWorkingResource();
		// the rename is necessary because at the moment we donl't have another possibility to delete or trim the irods
		// collections on specific resources.
		deletePreviousPIPs();
		renamePIPs();
		
		// cleanup
		distributedConversionAdapter.remove(makePIPSourceFolder(WA_PUBLIC).toString());
		distributedConversionAdapter.remove(makePIPSourceFolder(WA_INSTITUTION).toString());

		return true;
	}

	@Override
	public void rollback() throws Exception {
		throw new NotImplementedException("No rollback implemented for this action");
	}

	private File makePIPFolder(String pipType) {
		return Path.makeFile(n.getWorkAreaRootPath(),WA_PIPS,pipType,o.getContractor().getShort_name(),o.getIdentifier());
	}
	
	private File makePIPSourceFolder(String pipType) {
		return Path.makeFile(n.getWorkAreaRootPath(),WA_PIPS,pipType,o.getContractor().getShort_name(),o.getIdentifier()+"_"+o.getLatestPackage().getId());
	}
	
	


	private void deletePreviousPIPs() throws IOException{	
		
		if (makePIPFolder(WA_PUBLIC).exists());
			FileUtils.deleteDirectory(makePIPFolder(WA_PUBLIC));
		
		if (makePIPFolder(WA_INSTITUTION).exists())
			FileUtils.deleteDirectory(makePIPFolder(WA_INSTITUTION));
	}



	/**
	 * @param sourceDIPName
	 * @param targetDIPName
	 * @throws IOException
	 */
	private void renamePIPs() throws IOException {
		
		logger.debug("Rename PIP Public " +makePIPSourceFolder(WA_PUBLIC).toString()  + " to "  + makePIPFolder(WA_PUBLIC).toString() );
		logger.debug("Rename PIP Institution " +makePIPSourceFolder(WA_INSTITUTION).toString()  + " to "  + makePIPFolder(WA_INSTITUTION).toString() );
		
		if (makePIPSourceFolder(WA_PUBLIC).exists()) {
			FileUtils.moveDirectory(
					makePIPSourceFolder(WA_PUBLIC), 
					makePIPFolder(WA_PUBLIC));
		} else logger.debug(makePIPSourceFolder(WA_PUBLIC).toString()+ " does not exist, could not perform rename!" );
		if (makePIPSourceFolder(WA_INSTITUTION).exists()) {
			FileUtils.moveDirectory(
					makePIPSourceFolder(WA_INSTITUTION), 
					makePIPFolder(WA_INSTITUTION));
		} else logger.debug(makePIPSourceFolder(WA_INSTITUTION).toString()+ " does not exist, could not perform rename!" );
	}

	
	
	


	/**
	 * @author Daniel M. de Oliveira
	 * @author Jens Peters
	 * @param dipSourcePartialPath
	 */
	private void replicateFromSourceResourceToWorkingResource() {
//		TODO check if source exists
			distributedConversionAdapter.replicateToLocalNode(
					makePIPSourceFolder(WA_PUBLIC).toString());
//		TODO check if source exists
			 distributedConversionAdapter.replicateToLocalNode(
					 makePIPSourceFolder(WA_INSTITUTION).toString());
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
