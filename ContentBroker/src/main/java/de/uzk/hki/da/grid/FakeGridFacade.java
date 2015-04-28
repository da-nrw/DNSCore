/*
  DA-NRW Software Suite | ContentBroker
  Copyright (C) 2013 Historisch-Kulturwissenschaftliche Informationsverarbeitung
  Universität zu Köln, LVR InfoKom 2014

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

package de.uzk.hki.da.grid;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uzk.hki.da.core.C;
import de.uzk.hki.da.model.Node;
import de.uzk.hki.da.model.StoragePolicy;
import de.uzk.hki.da.pkg.ArchiveBuilderFactory;
import de.uzk.hki.da.utils.MD5Checksum;
import de.uzk.hki.da.utils.StringUtilities;

/**
 * For acceptance testing on developer machines
 * @author Daniel M. de Oliveira
 * @author jpeters
 *
 */
public class FakeGridFacade implements GridFacade {

	static final Logger logger = LoggerFactory.getLogger(FakeGridFacade.class);

	private String gridCacheAreaRootPath;
	private String tmpFolder = "/tmp/";

	@Override
	public boolean put(File file, String address_dest, StoragePolicy sp) throws IOException {
		
		if (!address_dest.startsWith(C.FS_SEPARATOR)) address_dest = C.FS_SEPARATOR + address_dest;
		
		String dest = getGridCacheAreaRootPath()+ C.WA_AIP + address_dest;
		logger.debug("Putting: "+file+" to "+dest);
		FileUtils.copyFile(file, new File(dest));
		return true;	

	}

	@Override
	public void get(File destination, String sourceFileAdress)
			throws IOException {
		
		if (!sourceFileAdress.startsWith(C.FS_SEPARATOR)) sourceFileAdress = C.FS_SEPARATOR + sourceFileAdress;
		
		String source =  getGridCacheAreaRootPath() + C.WA_AIP + sourceFileAdress;
		logger.debug("Retrieving: " + source + " to " + destination);
		FileUtils.copyFile(new File(source),destination);
	}
	
	@Override
	public boolean isValid(String address_dest) {
		File custodyFile =  new File (getGridCacheAreaRootPath()+address_dest);
		if (checkForCorruptedMarker(custodyFile)) {
			return false;
		} else return true;
	} 

	@Override
	public boolean storagePolicyAchieved(String address_dest, StoragePolicy sp) {
		return true;
	}

	@Override
	public long getFileSize(String address_dest) throws IOException {
		return 0;
	}

	public String getGridCacheAreaRootPath() {
		return gridCacheAreaRootPath;
	}

	public void setGridCacheAreaRootPath(String gridCacheAreaRootPath) {
		this.gridCacheAreaRootPath = StringUtilities.slashize(gridCacheAreaRootPath);
	}

	@Override
	public String getChecksumInCustody(String address_dest) {
		return getChecksum(address_dest);
	}


	@Override
	public String reComputeAndGetChecksumInCustody(String address_dest) {
		return getChecksum(address_dest);
	}

	@Override
	public boolean exists(String address_dest) {
		return (new File (getGridCacheAreaRootPath()+address_dest)).exists();
	}
	
	public  boolean distribute(Node localNode, File fileToDistribute, String address_dest, StoragePolicy sp) {
		if (!address_dest.startsWith(C.FS_SEPARATOR)) address_dest = C.FS_SEPARATOR + address_dest;
		
		String dest = getGridCacheAreaRootPath()+ C.WA_AIP + address_dest;
		logger.debug("Putting: "+fileToDistribute+" to "+dest);
		try {
			FileUtils.copyFile(fileToDistribute, new File(dest));
		} catch (IOException e) {
			logger.error("ERROR " + e.getMessage());
		}
		return true;	
	}
	//------------------------------------------------------------------------

	/**
	 * Scans AIP for marker file to mark this file as corrupted for sing in 
	 * acceptance testing on DEV machines
	 * @author Jens Peters
	 * @param custodyFile
	 * @return
	 */
	private boolean checkForCorruptedMarker(File custodyFile) {	
		try {

			FileUtils.copyFileToDirectory(
					custodyFile, 
					new File(tmpFolder), false);
			String packname = custodyFile.getName();
			String dirname = FilenameUtils.getBaseName(custodyFile.getName());

			ArchiveBuilderFactory.getArchiveBuilderForFile(new File("/tmp/"+packname))
			.unarchiveFolder(new File(tmpFolder + packname), new File ("/tmp/"));
			logger.debug("Extracting " + packname + " to + " + tmpFolder + dirname);

			IOFileFilter filter = new WildcardFileFilter("DESTROYED*");
			Collection<File> files = FileUtils.listFiles(new File(tmpFolder + dirname), filter, DirectoryFileFilter.DIRECTORY);

			if (files.size()>0) {
				logger.debug("found destroy marker");
				FileUtils.deleteDirectory(new File(tmpFolder + dirname));
				return true;
			} else FileUtils.deleteDirectory(new File(tmpFolder + dirname));
		} catch (Exception e) {
			logger.error("Error while checking validity on fakedGridfacade on " + custodyFile.getAbsolutePath() + ": "+ e.getMessage());
		} 
		logger.debug("found no destroy marker!");
		return false;
	}

	private String getChecksum(String address_dest) {
		File custodyFile =  new File (getGridCacheAreaRootPath()+address_dest);
		try {	 
			if (!checkForCorruptedMarker(custodyFile)) {
				logger.debug("Returning checksum for File " + custodyFile.getAbsolutePath());
				return MD5Checksum.getMD5checksumForLocalFile(custodyFile);
			} return "";
		} catch (IOException e) {
			logger.error("Error retrieving MD5 for " + custodyFile.getAbsolutePath());
		} return "";
	}

}
