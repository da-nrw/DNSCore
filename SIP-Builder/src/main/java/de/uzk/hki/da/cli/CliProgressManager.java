/*
  DA-NRW Software Suite | SIP-Builder
  Copyright (C) 2014 Historisch-Kulturwissenschaftliche Informationsverarbeitung
  Universität zu Köln

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

package de.uzk.hki.da.cli;

import de.uzk.hki.da.sb.ProgressManager;
import de.uzk.hki.da.sb.ProgressManager.SIPCreationJob;

/**
 * A specialized progress manager responsible for updating the progress bar in CLI mode
 * 
 *  @author Thomas Kleinke
 */
class CliProgressManager extends ProgressManager {
	
	private long copiedFilesFromListCount = 0; 
	
	/**
	 * Creates an abort message
	 */
	@Override
	public void abort() {
		System.out.println("\nSIP-Erstellungsvorgang abgebrochen");
	}

	/**
	 * Creates a start message
	 */
	@Override
	public void createStartMessage() {
		System.out.println("\n\nSIP-Erstellung läuft...");
	}
	
	/**
	 * Informs the progress manager that a certain job is active now
	 * 
	 * @param id The ID of the job to start
	 */
	@Override
	public void startJob(int id) {
		
		SIPCreationJob job = jobMap.get(id);
		job.initialTotalProgress = totalProgress;

		updateProgressBar();
	}
	
	/**
	 * Updates the copy progress
	 * 
	 * @param id The job ID
	 * @param processedData The amount of data already copied
	 */
	@Override
	public void copyProgress(int id, long processedData) {
		
		super.copyProgress(id, processedData);
		updateProgressBar();	
	}
	
	/**
	 * Updates the premis file creation progress
	 * 
	 * @param id The job ID
	 * @param progress The premis creation progress in percent
	 */
	@Override
	public void premisProgress(int id, double progress) {
		
		super.premisProgress(id, progress);
		updateProgressBar();
	}
	
	/**
	 * Updates the BagIt metadata creation progress
	 * 
	 * @param id The job ID
	 * @param progress The BagIt metadata creation progress in percent
	 */
	@Override
	public void bagitProgress(int id, double progress) {
		
		super.bagitProgress(id, progress);
		updateProgressBar();
	}
	
	/**
	 * Updates the archive file creation progress
	 * 
	 * @param id The job ID
	 * @param archivedData The amount of data already archived
	 */
	@Override
	public void archiveProgress(int id, long archivedData) {
		
		super.archiveProgress(id, archivedData);
		updateProgressBar();	
	}
	
		/**
	 * Updates the temporary file deletion progress
	 * 
	 * @param id The job ID
	 * @param progress The temporary file deletion progress in percent
	 */
	@Override
	public void deleteTempProgress(int id, double progress) {
		
		super.deleteTempProgress(id, progress);
		updateProgressBar();
	}
	
	/**
	 * Updates the copy files from list progress (if a file list or SIP list is used to create
	 * the SIP)
	 */
	public void copyFilesFromListProgress() {
		
		copiedFilesFromListCount++;
		totalProgress = ((double) copiedFilesFromListCount / (double) totalSize) * 100;
		updateProgressBar();
	}
	
	/**
	 * Skips the job (e.g. if the user chose to not overwrite an already existing SIP)
	 * 
	 * @param id The ID of the job to skip
	 */
	@Override
	public void skipJob(int id) {
		super.skipJob(id);
		updateProgressBar();
	}
	
	/**
	 * Creates a success message
	 * 
	 * @param skippedFiles Indicates if some files were skipped during the SIP creation process
	 */
	@Override
	public void createSuccessMessage(boolean skippedFiles) {
		
		super.createSuccessMessage(skippedFiles);
		updateProgressBar();
		System.out.println("\n\nDie SIP-Erstellung wurde erfolgreich abgeschlossen.");
		if (skippedFiles)
			System.out.println("Bereits existierende SIPs wurden nicht neu erstellt. " +
							   "Starten Sie den SIP-Builder mit der Option -alwaysOverwrite, um " + 
							   "sämtliche SIPs neu zu generieren und existierende SIPs gleichen Namens " +
							   "im Zielordner zu überschreiben.");
	}
	
	/**
	 * Updates the CLI progress bar according to the current totalProgress value
	 */
	private void updateProgressBar() {
		
		System.out.print("\r\r[");

		for (int i = 0; i < 25; i++) {
			if (i < (int) (totalProgress / 4))
				System.out.print("=");
			else
				System.out.print(" ");
		}
		
		System.out.print("] " + Math.round(totalProgress * 10) / 10.0 + "%");
	}
	
	/**
	 * Sets the total number of files listed in a file list or SIP list
	 * 
	 * @param size The number of files listed in a file list or SIP list
	 */
	public void setTotalSize(long size) {
		totalSize = size;
	}
}
