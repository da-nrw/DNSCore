package de.uzk.hki.da.cb;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.NotImplementedException;

import de.uzk.hki.da.core.ConfigurationException;
import de.uzk.hki.da.model.Package;
import de.uzk.hki.da.path.Path;

/**
 * 
 * Resets the job from any status between 12x and 36x to status 120.
 * 
 * @author Thomas Kleinke
 */
public class RestartIngestWorkflowAction extends AbstractAction {

	@Override
	void checkActionSpecificConfiguration() throws ConfigurationException {
		// Auto-generated method stub
		
	}

	@Override
	void checkSystemStatePreconditions() throws IllegalStateException {
		// Auto-generated method stub
		
	}

	@Override
	boolean implementation() {
		
		if (!object.isDelta())
			object.setUrn(null);
		
		deleteAIPTarFile();
		cleanDataFolder();		
		deleteDips();
		deleteJhoveTempData();		
		
		Package pkg = object.getLatestPackage();
		
		pkg.getEvents().clear();
		pkg.getFiles().clear();
		
		String[] repNames = object.getDataPath().toFile().list();
		for (String repName : repNames) {
			if (Path.make(object.getDataPath(),repName).toFile().isDirectory())
				pkg.scanRepRecursively(repName);
		}
		
		job.getConversion_instructions().clear();
				
		return true;
	}
	
	@Override
	void rollback() throws Exception {
		throw new NotImplementedException("No rollback implemented for this action");
	}

	/**
	 * 
	 * Deletes the AIP tar file (in working area) if it exists
	 * 
	 * @author Thomas Kleinke
	 */
	private void deleteAIPTarFile() {
		
		String tarFileName = object.getIdentifier() + ".pack_" + object.getLatestPackage().getName() + ".tar";		
		File tarFile =
				new File(localNode.getWorkAreaRootPath() + object.getContractor().getShort_name() + "/" + tarFileName);
		
		if (tarFile.exists())
			tarFile.delete();
	}
	
	/**
	 * 
	 * Deletes every file and directory in the data folder (in working area) except the
	 * 'a representation' folder
	 * 
	 * @author Thomas Kleinke
	 */
	private void cleanDataFolder() {
		
		File dataFolder = object.getDataPath().toFile();
		File newestBRepFolder = object.getPath("newest").toFile();
		File dipFolder = new File(dataFolder, "dip");
		
		if (newestBRepFolder.exists()) {
			try {
				FileUtils.deleteDirectory(newestBRepFolder);
			} catch (IOException e) {
				throw new RuntimeException("Failed to delete directory " + newestBRepFolder.getAbsolutePath(), e);
			}
		}
		
		if (dipFolder.exists()) {
			try {
				FileUtils.deleteDirectory(dipFolder);
			} catch (IOException e) {
				throw new RuntimeException("Failed to delete directory " + dipFolder.getAbsolutePath(), e);
			}
		}	
	}
	
	/**
	 * 
	 * Deletes previously created dips
	 * 
	 * @author Thomas Kleinke
	 */
	private void deleteDips() {
		File publicDipFolder = new File(localNode.getWorkAreaRootPath() + "pips/public/" +
			object.getContractor().getShort_name() + "/" + object.getIdentifier() + "_" + object.getLatestPackage().getId());
		File institutionDipFolder = new File(localNode.getWorkAreaRootPath() + "pips/institution/" +
				object.getContractor().getShort_name() + "/" + object.getIdentifier() + "_" + object.getLatestPackage().getId());
		
		if (publicDipFolder.exists()) {
			try {
				FileUtils.deleteDirectory(publicDipFolder);
			} catch (IOException e) {
				throw new RuntimeException("Failed to delete directory " + publicDipFolder.getAbsolutePath(), e);
			}			
		}
		
		if (institutionDipFolder.exists()) {
			try {
				FileUtils.deleteDirectory(institutionDipFolder);
			} catch (IOException e) {
				throw new RuntimeException("Failed to delete directory " + institutionDipFolder.getAbsolutePath(), e);
			}			
		}
	}
	
	/**
	 * 
	 * Deletes the temporarily saved jhove data if it exists
	 * 
	 * @author Thomas Kleinke
	 */
	private void deleteJhoveTempData() {
		
		Path.makeFile(object.getDataPath(),"temp");
		
	}
}
