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
import java.io.FileFilter;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uzk.hki.da.model.StoragePolicy;
import de.uzk.hki.da.pkg.ArchiveBuilderFactory;
import de.uzk.hki.da.utils.Utilities;

/**
 * For acceptance testing on developer machines
 * @author Daniel M. de Oliveira
 * @author jpeters
 *
 */
public class FakeGridFacade implements GridFacade {

	static final Logger logger = LoggerFactory.getLogger(FakeGridFacade.class);
	
	private String gridCacheAreaRootPath;
	
	
	@Override
	public boolean put(File file, String address_dest, StoragePolicy sp) throws IOException {
		logger.debug("putting: "+file+" to "+getGridCacheAreaRootPath()+address_dest);
		FileUtils.copyFile(file, new File(getGridCacheAreaRootPath()+address_dest));
		return true;
		
	}

	@Override
	public void get(File destination, String sourceFileAdress)
			throws IOException {
		logger.debug("retrieving: " + getGridCacheAreaRootPath() + sourceFileAdress + " to " + destination);
		FileUtils.copyFile(new File(getGridCacheAreaRootPath() + sourceFileAdress),destination);
	}

	@Override
	public boolean isValid(String address_dest) {
		File file = new File (getGridCacheAreaRootPath()+address_dest);
		try {
			FileUtils.copyFileToDirectory(
					file, 
					new File("/tmp/"), false);
			;
		ArchiveBuilderFactory.getArchiveBuilderForFile(new File("/tmp/"+file.getName()))
			.unarchiveFolder(new File("/tmp/" + file.getName()), new File ("/tmp/"));
		
		logger.debug("Extracting " + file.getName() + " to /tmp  , Dir name " +  FilenameUtils.getBaseName(file.getName()));
		FileFilter filter = new WildcardFileFilter("DESTROYED*");
		File []found = new File("/tmp/" + FilenameUtils.getBaseName(file.getName())).listFiles(filter);
		if (found.length==0) {
			logger.debug("found destroy marker");
			FileUtils.deleteDirectory(new File("/tmp/" + FilenameUtils.getBaseName(file.getName())));
			return false;
		} else FileUtils.deleteDirectory(new File("/tmp/" + FilenameUtils.getBaseName(file.getName())));
		} catch (Exception e) {
			logger.error("Error while checking validity on fakedGridfacade on " + address_dest + ": "+ e.getMessage());
			new RuntimeException("Error while checking validity on fakedGridFacade on " + address_dest + ": "+ e.getMessage(), e);
	} return true;
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
		this.gridCacheAreaRootPath = Utilities.slashize(gridCacheAreaRootPath);
	}

	
}
