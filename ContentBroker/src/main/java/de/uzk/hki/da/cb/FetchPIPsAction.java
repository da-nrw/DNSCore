package de.uzk.hki.da.cb;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.NotImplementedException;

import de.uzk.hki.da.core.ConfigurationException;
import de.uzk.hki.da.core.IngestGate;
import de.uzk.hki.da.grid.DistributedConversionAdapter;
import de.uzk.hki.da.utils.Path;

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
	boolean implementation() throws FileNotFoundException, IOException {
		if (distributedConversionAdapter==null) throw new ConfigurationException("irodsSystemConnector not set");
		
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
		
		if (Path.make(localNode.getWorkAreaRootPath(),publicContractorFolder.toString(),sourceDIPName).toFile().exists())
			FileUtils.moveDirectory(
					Path.make(localNode.getWorkAreaRootPath(),publicContractorFolder,sourceDIPName).toFile(), 
					Path.make(localNode.getWorkAreaRootPath(),publicContractorFolder,targetDIPName).toFile());
		if (Path.make(localNode.getWorkAreaRootPath(),institutionContractorFolder,sourceDIPName).toFile().exists())
			FileUtils.moveDirectory(
					Path.make(localNode.getWorkAreaRootPath(),institutionContractorFolder,sourceDIPName).toFile(), 
					Path.make(localNode.getWorkAreaRootPath(),institutionContractorFolder,targetDIPName).toFile());
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
	
	
	@Override
	void rollback() throws Exception {
		throw new NotImplementedException("No rollback implemented for this action");
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
