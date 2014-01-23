package de.uzk.hki.da.cb;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import de.uzk.hki.da.format.JhoveScanService;
import de.uzk.hki.da.model.Package;

/**
 * 
 * Resets the job from any status between 12x and 36x to status 120.
 * 
 * @author Thomas Kleinke
 */
public class RestartIngestWorkflowAction extends AbstractAction {

	private JhoveScanService jhoveScanService;
	
	@Override
	boolean implementation() {
		
		if (!object.hasDeltas())
			object.setUrn(null);
		
		deleteAIPTarFile();
		cleanDataFolder();		
		deleteDips();
		deleteJhoveTempData();		
		
		Package pkg = object.getLatestPackage();
		pkg.getEvents().clear();
		pkg.getFiles().clear();
		
		job.getConversion_instructions().clear();
				
		return true;
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
		
		File dataFolder = new File(object.getDataPath());
		File newestBRepFolder = new File(dataFolder, object.getNameOfNewestARep().replace("+a", "+b"));
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
		File publicDipFolder = new File(localNode.getDipAreaRootPath() + "public/" +
			object.getContractor().getShort_name() + "/" + object.getIdentifier() + "_" + object.getLatestPackage().getId());
		File institutionDipFolder = new File(localNode.getDipAreaRootPath() + "institution/" +
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
		
		String pathToJhoveFolder = new File(jhoveScanService.getJhoveFolder()).getAbsolutePath();
		File jhoveTempFolder = new File(pathToJhoveFolder + "/temp/" + job.getId() + "/");
		
		if (jhoveTempFolder.exists()) {
			try {
				FileUtils.deleteDirectory(jhoveTempFolder);
			} catch (IOException e) {
				throw new RuntimeException("Failed to delete directory " + jhoveTempFolder.getAbsolutePath(), e);
			}
		}
	}
	
	@Override
	void rollback() throws Exception { }

	public JhoveScanService getJhoveScanService() {
		return jhoveScanService;
	}

	public void setJhoveScanService(JhoveScanService jhoveScanService) {
		this.jhoveScanService = jhoveScanService;
	}
}
