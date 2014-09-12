/*
 DA-NRW Software Suite | ContentBroker
 Copyright (C) 2013 Historisch-Kulturwissenschaftliche Informationsverarbeitung
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

package de.uzk.hki.da.core;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * Should protect the WorkArea from reaching its limits.
 * The WorkArea is the primary space for the ContentBroker to work on.
 * 
 * 
 * For a first implementation, UnpackAction and ObjectToWorkAreaAction should use it to 
 * ask if a package can be moved from lza or staging to WorkArea
 * 
 * @author Christian Weitz
 * @author Thomas Kleinke
 */
public class IngestGate {

	/** The Constant logger. */
	@SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory.getLogger(IngestGate.class);
	
	/** The work area root path. */
	private String workAreaRootPath;
	
	/** The file size factor. */
	private int fileSizeFactor;	// factor applied to the file size (to account for compression etc.)
	
	/** The free disk space percent. */
	private int freeDiskSpacePercent; // minimum space on disk in percent that has to be available for new packages to be processed	
	
	/**
	 * @param sourceFileOriginalSize the source file original size
	 * @return true, if successful
	 */
	public boolean canHandle(Long sourceFileOriginalSize){
		
		if (!checkAvailableDiskSpace())
			return false;
		
		if (!checkPackageSize(sourceFileOriginalSize))
			return false;
		
		return true;
	}
	
	/**
	 * Check available disk space.
	 *
	 * @return true, if successful
	 */
	private boolean checkAvailableDiskSpace() {
		
		long totalSpaceOnDestination = new File(workAreaRootPath).getTotalSpace();
		long freeSpaceOnDestination = new File(workAreaRootPath).getUsableSpace();
		
		if (freeDiskSpacePercent < 1)
			return true;
		
		if (freeSpaceOnDestination == 0)
			return false;
		
		return ((totalSpaceOnDestination / freeSpaceOnDestination) < (100 / freeDiskSpacePercent));		
	}
	
	/**
	 * Check package size.
	 *
	 * @param sourceFileOriginalSize the source file original size
	 * @return true, if successful
	 */
	private boolean checkPackageSize(long sourceFileOriginalSize) {
		
		long freeSpaceOnDestination = new File(workAreaRootPath).getUsableSpace();
		long requiredSpace = sourceFileOriginalSize * fileSizeFactor;
		
		return (freeSpaceOnDestination > requiredSpace);
	}

	/**
	 * Gets the file size factor.
	 *
	 * @return the file size factor
	 */
	public int getFileSizeFactor() {
		return fileSizeFactor;
	}

	/**
	 * Sets the file size factor.
	 *
	 * @param fileSizeFactor the new file size factor
	 */
	public void setFileSizeFactor(int fileSizeFactor) {
		this.fileSizeFactor = fileSizeFactor;
	}

	/**
	 * Gets the free disk space percent.
	 *
	 * @return the free disk space percent
	 */
	public int getFreeDiskSpacePercent() {
		return freeDiskSpacePercent;
	}

	/**
	 * Sets the free disk space percent.
	 *
	 * @param freeDiskSpacePercent the new free disk space percent
	 */
	public void setFreeDiskSpacePercent(int freeDiskSpacePercent) {
		this.freeDiskSpacePercent = freeDiskSpacePercent;
	}

	/**
	 * Gets the work area root path.
	 *
	 * @return the work area root path
	 */
	public String getWorkAreaRootPath() {
		return workAreaRootPath;
	}

	/**
	 * Sets the work area root path.
	 *
	 * @param workAreaRootPath the new work area root path
	 */
	public void setWorkAreaRootPath(String workAreaRootPath) {
		this.workAreaRootPath = workAreaRootPath;
	}
}
