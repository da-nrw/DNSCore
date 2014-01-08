package de.uzk.hki.da.cb;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import de.uzk.hki.da.core.ConfigurationException;

/**
 * Fetches the PIPs from the nodes on which they've originally been created.
 * Overwrites any previously generated PIPs.
 * 
 * @author Daniel M. de Oliveira
 */
public class FetchPIPsAction extends AbstractAction {

	@Override
	boolean implementation() throws FileNotFoundException, IOException {
		if (irodsSystemConnector==null) throw new ConfigurationException("irodsSystemConnector not set");
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
		irodsSystemConnector.removeCollectionAndEatException(getIrodsZonePath()+"dip/public/"+dipSourcePartialPath);
		irodsSystemConnector.removeCollectionAndEatException(getIrodsZonePath()+"dip/institution/"+dipSourcePartialPath);

		return true;
	}



	/**
	 * @author Daniel M. de Oliveira
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
	 * @author Daniel M. de Oliveira
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
		getIrodsSystemConnector().replicateCollectionToResource(
				getIrodsZonePath()+"dip/public/"+dipSourcePartialPath, 
				localNode.getWorkingResource());
		getIrodsSystemConnector().replicateCollectionToResource(
				getIrodsZonePath()+"dip/institution/"+dipSourcePartialPath, 
				localNode.getWorkingResource());
	}

	
	
	@Override
	void rollback() throws Exception {}
}
