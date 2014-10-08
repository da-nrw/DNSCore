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

import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.NotImplementedException;

import de.uzk.hki.da.action.AbstractAction;
import de.uzk.hki.da.core.ConfigurationException;
import de.uzk.hki.da.core.IngestGate;
import de.uzk.hki.da.core.Path;
import de.uzk.hki.da.grid.DistributedConversionAdapter;

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
		
		publicContractorFolder = Path.make("pips", "public", object.getContractor().getShort_name());
		institutionContractorFolder = Path.make("pips", "institution", object.getContractor().getShort_name());
		String sourceDIPName = object.getIdentifier()+"_"+object.getLatestPackage().getId();
		
		replicateFromSourceResourceToWorkingResource(sourceDIPName);
		// the rename is necessary because at the moment we donl't have another possibility to delete or trim the irods
		// collections on specific resources.
		deletePreviousPIPs(object.getIdentifier());
		renamePIPs(sourceDIPName, object.getIdentifier());
		
		// cleanup
		distributedConversionAdapter.remove(Path.make(publicContractorFolder,sourceDIPName).toString());
		distributedConversionAdapter.remove(Path.make(institutionContractorFolder,sourceDIPName).toString());

		return true;
	}



	@Override
	public void rollback() throws Exception {
		throw new NotImplementedException("No rollback implemented for this action");
	}



	/**
	 * @param dipTargetPartialPath
	 * @throws IOException
	 */
	private void deletePreviousPIPs(String targetDIPName) throws IOException{	
		if (Path.make(localNode.getWorkAreaRootPath(),publicContractorFolder,targetDIPName).toFile().exists());
			FileUtils.deleteDirectory(Path.make(
					localNode.getWorkAreaRootPath(),publicContractorFolder,targetDIPName).toFile());
		if (Path.make(localNode.getWorkAreaRootPath(),institutionContractorFolder,targetDIPName).toFile().exists())
			FileUtils.deleteDirectory(Path.make(
					localNode.getWorkAreaRootPath(),institutionContractorFolder, targetDIPName).toFile());
	}



	/**
	 * @param sourceDIPName
	 * @param targetDIPName
	 * @throws IOException
	 */
	private void renamePIPs(String sourceDIPName,
			String targetDIPName) throws IOException {
		
		Path sourcePIPPublic = Path.make(localNode.getWorkAreaRootPath(),publicContractorFolder,sourceDIPName);
		Path targetPIPPublic = Path.make(localNode.getWorkAreaRootPath(),publicContractorFolder,targetDIPName);
		
		Path sourcePIPInst = Path.make(localNode.getWorkAreaRootPath(),institutionContractorFolder,sourceDIPName);
		Path targetPIPInst = Path.make(localNode.getWorkAreaRootPath(),institutionContractorFolder,targetDIPName);
		
		logger.debug("Rename PIP Public " +sourcePIPPublic.toString()  + " to "  + targetPIPPublic.toString() );
		logger.debug("Rename PIP Institution " +sourcePIPInst.toString()  + " to "  + targetPIPInst.toString() );
		
		if (sourcePIPPublic.toFile().exists()) {
			FileUtils.moveDirectory(
					sourcePIPPublic.toFile(), 
					targetPIPPublic.toFile());
		} else logger.debug(sourcePIPPublic.toString()+ " does not exist, could not perform rename!" );
		if (sourcePIPInst.toFile().exists()) {
			FileUtils.moveDirectory(
					sourcePIPInst.toFile(), 
					targetPIPInst.toFile());
		} else logger.debug(sourcePIPInst.toString()+ " does not exist, could not perform rename!" );
	}



	/**
	 * @author Daniel M. de Oliveira
	 * @author Jens Peters
	 * @param dipSourcePartialPath
	 */
	private void replicateFromSourceResourceToWorkingResource(
			String dipSourcePartial) {
//		TODO check if source exists
			distributedConversionAdapter.replicateToLocalNode(
					Path.make(publicContractorFolder,dipSourcePartial).toString());
//		TODO check if source exists
			 distributedConversionAdapter.replicateToLocalNode(
				Path.make(institutionContractorFolder,dipSourcePartial).toString());
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
