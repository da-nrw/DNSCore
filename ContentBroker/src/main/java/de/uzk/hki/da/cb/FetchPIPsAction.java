package de.uzk.hki.da.cb;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import de.uzk.hki.da.core.ConfigurationException;
import de.uzk.hki.da.grid.DistributedConversionAdapter;

/**
 * Fetches the PIPs from the nodes on which they've originally been created.
 * Overwrites any previously generated PIPs.
 * 
 * @author Daniel M. de Oliveira
 */
public class FetchPIPsAction extends AbstractAction {

	private DistributedConversionAdapter distributedConversionAdapter;
	
	@Override
	boolean implementation() throws FileNotFoundException, IOException {
		if (distributedConversionAdapter==null) throw new ConfigurationException("irodsSystemConnector not set");
		object.reattach();
		
		String dipSourcePartialPath = 
				object.getContractor().getShort_name()+"/"+object.getIdentifier()+"_"+object.getLatestPackage().getId();
		String dipTargetPartialPath = 
				object.getContractor().getShort_name()+"/"+object.getIdentifier();

		replicateFromSourceResourceToWorkingResource(dipSourcePartialPath);
		deletePreviousPIPs(dipTargetPartialPath);
		// the rename is necessary because at the moment we don't have another possibility to delete or trim the irods
		// collections on specific resources.
		renamePIPs(dipSourcePartialPath, dipTargetPartialPath);
		
		// cleanup
		distributedConversionAdapter.remove("dip/public/"+dipSourcePartialPath);
		distributedConversionAdapter.remove("dip/institution/"+dipSourcePartialPath);

		return true;
	}



	/**
	 * @param dipTargetPartialPath
	 * @throws IOException
	 */
	private void deletePreviousPIPs(String dipTargetPartialPath) throws IOException{
		if (new File(localNode.getDipAreaRootPath()+"public/"+dipTargetPartialPath).exists());
			FileUtils.deleteDirectory(new File(
					localNode.getDipAreaRootPath()+"public/"+dipTargetPartialPath));
		if (new File(localNode.getDipAreaRootPath()+"institution/"+dipTargetPartialPath).exists())
			FileUtils.deleteDirectory(new File(
					localNode.getDipAreaRootPath()+"institution/"+dipTargetPartialPath));
	}



	/**
	 * @param dipSourcePartialPath
	 * @param dipTargetPartialPath
	 * @throws IOException
	 */
	private void renamePIPs(String dipSourcePartialPath,
			String dipTargetPartialPath) throws IOException {
		if (new File(localNode.getDipAreaRootPath()+"public/"+dipSourcePartialPath).exists())
			FileUtils.moveDirectory(
					new File(localNode.getDipAreaRootPath()+"public/"+dipSourcePartialPath), 
					new File(localNode.getDipAreaRootPath()+"public/"+dipTargetPartialPath));
		if (new File(localNode.getDipAreaRootPath()+"institution/"+dipSourcePartialPath).exists())
			FileUtils.moveDirectory(
					new File(localNode.getDipAreaRootPath()+"institution/"+dipSourcePartialPath), 
					new File(localNode.getDipAreaRootPath()+"institution/"+dipTargetPartialPath));
	}



	/**
	 * @author Daniel M. de Oliveira
	 * @param dipSourcePartialPath
	 */
	private void replicateFromSourceResourceToWorkingResource(
			String dipSourcePartialPath) {
		distributedConversionAdapter.replicateToLocalNode(
				"dip/public/"+dipSourcePartialPath);
		distributedConversionAdapter.replicateToLocalNode(
				"dip/institution/"+dipSourcePartialPath);
	}

	
	
	@Override
	void rollback() throws Exception {}



	public DistributedConversionAdapter getDistributedConversionAdapter() {
		return distributedConversionAdapter;
	}



	public void setDistributedConversionAdapter(
			DistributedConversionAdapter distributedConversionAdapter) {
		this.distributedConversionAdapter = distributedConversionAdapter;
	}
}
