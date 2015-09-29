package de.uzk.hki.da.sb;

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