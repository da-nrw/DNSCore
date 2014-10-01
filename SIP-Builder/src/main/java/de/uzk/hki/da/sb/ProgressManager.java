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

package de.uzk.hki.da.sb;

import java.util.HashMap;
import java.util.Map;

/**
 * The ProgressManager is responsible for updating the progress bars in both CLI and GUI modes
 * 
 *  @author Thomas Kleinke
 */
public abstract class ProgressManager {

	protected Map<Integer, SIPCreationJob> jobMap = new HashMap<Integer, SIPCreationJob>();
	protected long totalSize = 0;
	public double totalProgress = 0.0;
	
	final double copyPercentage = 0.2;
	final double premisPercentage = 0.05;
	final double bagitPercentage = 0.2;
	final double archivePercentage = 0.5;
	final double deleteTempPercentage = 0.05;
	
	/**
	 * Creates a new job with the given job ID, package name and folder size and adds it to the job map
	 * 
	 * @param id The job ID
	 * @param packageName The package name
	 * @param folderSize The size of the package folder
	 */
	public void addJob(int id, String packageName, long folderSize) {
		
		SIPCreationJob job = new SIPCreationJob(id, packageName, folderSize);
		if (folderSize == 0)
			job.copyProgress = 100.0;
		
		jobMap.put(id, job);
		
		totalSize += folderSize;
	}
	
	/**
	 * Clears the job map and sets the total progress to 0
	 */
	public void reset() {
		jobMap.clear();
		totalProgress = 0.0;
	}
	
	/**
	 * Creates an abort message
	 */
	abstract public void abort();
	
	/**
	 * Determines the share each job has in the total progress
	 * 
	 * @param createCollection Specifies if a collection will be created or not
	 */
	public void calculateProgressParts(boolean createCollection) {

		for (SIPCreationJob job : jobMap.values()) {
			
			if (job.id == -1)
				job.progressPart = 5.0;
			else if (job.folderSize == 0) {
				if (jobMap.values().size() == 1)
					job.progressPart = 100.0;
				else
					job.progressPart = 0.0;
			}
			else {
				if (createCollection)
					job.progressPart = (((double) job.folderSize / (double) totalSize) * 95.0);
				else
					job.progressPart = (((double) job.folderSize / (double) totalSize) * 100.0);
			}
		}
	}
	
	/**
	 * Creates a start message
	 */
	abstract public void createStartMessage();
	
	/**
	 * Informs the progress manager that a certain job is active now
	 * 
	 * @param id The ID of the job to start
	 */
	abstract public void startJob(int id);
	
	/**
	 * Updates the copy progress
	 * 
	 * @param id The job ID
	 * @param processedData The amount of data already copied
	 */
	public void copyProgress(int id, long processedData) {
		
		SIPCreationJob job = jobMap.get(id);
		
		job.processedData += processedData;
		if (job.folderSize == 0)
			job.copyProgress = 0;
		else
			job.copyProgress = (job.processedData / (double) job.folderSize) * (job.progressPart * copyPercentage);
		
		totalProgress = job.getProgress();
	}
	
	/**
	 * Updates the premis file creation progress
	 * 
	 * @param id The job ID
	 * @param progress The premis creation progress in percent
	 */
	public void premisProgress(int id, double progress) {
		
		SIPCreationJob job = jobMap.get(id);
		
		job.premisProgress += progress * (job.progressPart * premisPercentage) / 100;

		totalProgress = job.getProgress();
	}
	
	/**
	 * Updates the BagIt metadata creation progress
	 * 
	 * @param id The job ID
	 * @param progress The BagIt metadata creation progress in percent
	 */
	public void bagitProgress(int id, double progress) {
			
		SIPCreationJob job = jobMap.get(id);
		
		if (id == -1)
			job.bagitProgress += progress * job.progressPart / 100;
		else
			job.bagitProgress += progress * (job.progressPart * bagitPercentage) / 100;
				
		totalProgress = job.getProgress();
	}
	
	/**
	 * Updates the archive file creation progress
	 * 
	 * @param id The job ID
	 * @param archivedData The amount of data already archived
	 */
	public void archiveProgress(int id, long archivedData) {
				
		SIPCreationJob job = jobMap.get(id);
		
		job.archivedData += archivedData;
		if (job.folderSize == 0)
			job.archiveProgress = 0;
		else
			job.archiveProgress = ((double) job.archivedData / (double) job.folderSize) * (job.progressPart * archivePercentage);
		
		totalProgress = job.getProgress();
	}
	
	/**
	 * Updates the temporary file deletion progress
	 * 
	 * @param id The job ID
	 * @param progress The temporary file deletion progress in percent
	 */
	public void deleteTempProgress(int id, double progress) {
		
		SIPCreationJob job = jobMap.get(id);
		
		job.deleteTempProgress += progress * (job.progressPart * deleteTempPercentage) / 100;
				
		totalProgress = job.getProgress();
	}
	
	/**
	 * Skips the job (e.g. if the user chose to not overwrite an already existing SIP)
	 * 
	 * @param id The ID of the job to skip
	 */
	public void skipJob(int id) {
		
		SIPCreationJob job = jobMap.get(id);
		
		totalProgress = job.initialTotalProgress + job.progressPart; 
	}
	
	/**
	 * Creates a success message
	 * 
	 * @param skippedFiles Indicates if some files were skipped during the SIP creation process
	 */
	public void createSuccessMessage(boolean skippedFiles) {
		totalProgress = 100.0;
	}
	
	/**
	 * Sets the folder size for the given job to the given value
	 * 
	 * @param id The job ID
	 * @param folderSize The folder size to set
	 */
	public void setJobFolderSize(int id, long folderSize) {
		
		jobMap.get(id).folderSize = folderSize;		
	}
	
	/**
	 * Saves progress and folder size information for a SIP building process
	 * 
	 * @author Thomas Kleinke
	 */
	public class SIPCreationJob {
		
		int id;
		public String packageName;
		long folderSize;
		double copyProgress, premisProgress, bagitProgress, archiveProgress, deleteTempProgress;
		double progressPart;
		public double initialTotalProgress;
		long processedData, archivedData;

		
		public SIPCreationJob(int id, String packageName, long folderSize) {
			
			this.id = id;
			this.packageName = packageName;
			this.folderSize = folderSize;
		}
		
		/**
		 * 
		 * @return The total progress including the progress of all previously created SIPs
		 * and the progress of the SIP connected to this job
		 */
		public double getProgress() {
			
			return initialTotalProgress +
				   copyProgress + 
				   premisProgress + 
				   bagitProgress + 
				   archiveProgress + 
				   deleteTempProgress;
		}
	}
	
}
