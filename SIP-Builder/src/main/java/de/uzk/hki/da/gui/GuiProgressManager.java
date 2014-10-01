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

package de.uzk.hki.da.gui;

import java.awt.EventQueue;

import javax.swing.JLabel;
import javax.swing.JProgressBar;

import de.uzk.hki.da.sb.ProgressManager;
import de.uzk.hki.da.sb.ProgressManager.SIPCreationJob;

/**
 * A specialized progress manager responsible for updating the progress bar in GUI mode
 * 
 *  @author Thomas Kleinke
 */
class GuiProgressManager extends ProgressManager {
	
	private JProgressBar progressBar;
	private JLabel progressDisplay;
	private JLabel stepDisplay;
	
	public GuiProgressManager(JProgressBar progressBar, JLabel progressDisplay, JLabel stepDisplay) {
		
		this.progressBar = progressBar;
		this.progressDisplay = progressDisplay;
		this.stepDisplay = stepDisplay;
	}
	
	/**
	 * Resets the progress bar to its initial state
	 */
	@Override
	public void reset() {		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				progressBar.setEnabled(true);
				progressBar.setIndeterminate(false);
				progressBar.setValue(0);
				stepDisplay.setText("");
			}});

		super.reset();
	}

	/**
	 * Creates an abort message and disables the progress bar
	 */
	@Override
	public void abort() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				progressBar.setEnabled(false);
				progressBar.setIndeterminate(false);
				if (progressBar.getValue() == 100) {
					progressBar.setValue(progressBar.getValue() - 1);
					progressBar.setValue(progressBar.getValue() + 1);			
				} else {		
					progressBar.setValue(progressBar.getValue() + 1);
					progressBar.setValue(progressBar.getValue() - 1);
				}
				progressDisplay.setText("SIP-Erstellungsvorgang abgebrochen");
				stepDisplay.setText("");
			}});
	}
	
	/**
	 * No start message is created in GUI mode
	 */
	@Override
	public void createStartMessage() {
	}

	/**
	 * Informs the progress manager that a certain job is active now and displays a text message
	 * 
	 * @param id The ID of the job to start
	 */
	@Override
	public void startJob(int id) {
		SIPCreationJob job = jobMap.get(id);
		job.initialTotalProgress = totalProgress;
		
		final int jobId = id;
		final String packageName = job.packageName;
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				
				if (jobId == -1)
					progressDisplay.setText("Erstelle Lieferung " + "\"" + packageName + "\"");
				else
					progressDisplay.setText("Erstelle SIP aus Ordner " + "\"" + packageName + "\"");
			}});
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
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				progressBar.setValue((int) totalProgress);
				progressBar.setIndeterminate(false);
				stepDisplay.setText("Kopiere Daten...");
			}});
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

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				progressBar.setValue((int) totalProgress);
				progressBar.setIndeterminate(false);
				stepDisplay.setText("Erzeuge Premis-Datei...");
			}});
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

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				progressBar.setValue((int) totalProgress);
				progressBar.setIndeterminate(true);
				stepDisplay.setText("Erstelle Bagit...");
			}});
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

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				progressBar.setValue((int) totalProgress);
				progressBar.setIndeterminate(false);
				stepDisplay.setText("Erstelle Archivdatei...");
			}});
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

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				progressBar.setValue((int) totalProgress);
				progressBar.setIndeterminate(false);
				stepDisplay.setText("Lösche temporäre Daten...");
			}});
	}
	
	/**
	 * Skips the job (e.g. if the user chose to not overwrite an already existing SIP)
	 * 
	 * @param id The ID of the job to skip
	 */
	@Override
	public void skipJob(int id) {
		super.skipJob(id);

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				progressBar.setValue((int) totalProgress);
				progressBar.setIndeterminate(false);
			}});
	}
	
	/**
	 * Creates a success message
	 * 
	 * @param skippedFiles Indicates if some files were skipped during the SIP creation process
	 */
	@Override
	public void createSuccessMessage(boolean skippedFiles) {

		super.createSuccessMessage(skippedFiles);

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				progressBar.setValue((int) totalProgress);
				progressBar.setIndeterminate(false);
				stepDisplay.setText("");

				progressDisplay.setText("Die SIP-Erstellung wurde erfolgreich abgeschlossen.");
			}});
	}
}
