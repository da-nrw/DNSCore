package de.uzk.hki.da.cb;

import java.io.File;
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
	
	private Path pipsPublicSource = new Path("pips", "public");
	private Path pipsInstitutionSource = new Path("pips", "institution");
	
	@Override
	boolean implementation() throws FileNotFoundException, IOException {
		
		if (distributedConversionAdapter==null) throw new ConfigurationException("irodsSystemConnector not set");
		Path dipSourcePartial = new Path (object.getContractor().getShort_name(), object.getIdentifier()+"_"+object.getLatestPackage().getId());
		Path dipTargetPartial = new Path (object.getContractor().getShort_name(), object.getIdentifier());
		String dipTargetPartialPath = dipTargetPartial.toString();
		
		replicateFromSourceResourceToWorkingResource(dipSourcePartial);
		deletePreviousPIPs(dipTargetPartial);
		// the rename is necessary because at the moment we don't have another possibility to delete or trim the irods
		// collections on specific resources.
		renamePIPs(dipSourcePartial.toString(), dipTargetPartialPath);
		
		// cleanup
		distributedConversionAdapter.remove(pipsPublicSource.toString()+dipSourcePartial.toString());
		distributedConversionAdapter.remove(pipsInstitutionSource.toString()+dipSourcePartial.toString());

		return true;
	}



	/**
	 * @param dipTargetPartialPath
	 * @throws IOException
	 */
	private void deletePreviousPIPs(Path dipTargetPartial) throws IOException{
		if (new File(localNode.getWorkAreaRootPath()+pipsPublicSource.toString()+dipTargetPartial.toString()).exists());
			FileUtils.deleteDirectory(new File(
					localNode.getWorkAreaRootPath()+pipsPublicSource.toString()+dipTargetPartial.toString()));
		if (new File(localNode.getWorkAreaRootPath()+pipsInstitutionSource.toString()+dipTargetPartial.toString()).exists())
			FileUtils.deleteDirectory(new File(
					localNode.getWorkAreaRootPath()+pipsInstitutionSource.toString()+dipTargetPartial.toString()));
	}



	/**
	 * @param dipSourcePartialPath
	 * @param dipTargetPartialPath
	 * @throws IOException
	 */
	private void renamePIPs(String dipSourcePartialPath,
			String dipTargetPartialPath) throws IOException {
		if (new File(localNode.getWorkAreaRootPath()+pipsPublicSource.toString()+dipSourcePartialPath).exists())
			FileUtils.moveDirectory(
					new File(localNode.getWorkAreaRootPath()+pipsPublicSource.toString()+dipSourcePartialPath), 
					new File(localNode.getWorkAreaRootPath()+pipsPublicSource.toString()+dipTargetPartialPath));
		if (new File(localNode.getWorkAreaRootPath()+pipsInstitutionSource.toString()+dipSourcePartialPath).exists())
			FileUtils.moveDirectory(
					new File(localNode.getWorkAreaRootPath()+pipsInstitutionSource.toString()+dipSourcePartialPath), 
					new File(localNode.getWorkAreaRootPath()+pipsInstitutionSource.toString()+dipTargetPartialPath));
	}



	/**
	 * @author Daniel M. de Oliveira
	 * @param dipSourcePartialPath
	 */
	private void replicateFromSourceResourceToWorkingResource(
			Path dipSourcePartial) {
		distributedConversionAdapter.replicateToLocalNode(
				pipsPublicSource.toString()+dipSourcePartial.toString());
		distributedConversionAdapter.replicateToLocalNode(
				pipsInstitutionSource.toString()+dipSourcePartial.toString());
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
