package de.uzk.hki.da.cb;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

/**
 * 
 * @author Thomas Kleinke
 */
public class SuperRollbackAction extends AbstractAction {

	private String jhoveFolder;
	
	@Override
	boolean implementation() {

		cleanDataFolder();
		
		deleteJhoveTempData();
		
		return true;
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
		
		for (File f : dataFolder.listFiles()) {
			if (f.getName() != object.getNameOfNewestARep()) {
				if (f.isDirectory()) {
					try {
						FileUtils.deleteDirectory(f);
					} catch (IOException e) {
						throw new RuntimeException("Failed to delete directory " + f.getAbsolutePath(), e);
					}
				} else
					f.delete();
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
		
		String pathToJhoveFolder = new File(jhoveFolder).getAbsolutePath();
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
}
