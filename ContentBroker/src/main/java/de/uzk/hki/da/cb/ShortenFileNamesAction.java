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

package de.uzk.hki.da.cb;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import de.uzk.hki.da.model.DAFile;
import de.uzk.hki.da.model.Event;

/**
 * 
 * @author Sebastian Cuy
 *
 */
public class ShortenFileNamesAction extends AbstractAction {
	
	Map<String,String> map = new HashMap<String,String>();

	@Override
	boolean implementation() throws FileNotFoundException, IOException {

		String metadataFile = object.getMetadata_file();
		
		// rename results of conversions
		for (Event e:object.getLatestPackage().getEvents()) {
			
			logger.debug("checking if event is CONVERT for {}", e);
			
			if (!"CONVERT".equals(e.getType())) continue;
			
			logger.debug("event is CONVERT: {}", e);

			DAFile daFile = e.getTarget_file();
			if (!daFile.getRep_name().startsWith("dip")) continue;
			
			final File file = daFile.toRegularFile();
			final String filePath = daFile.getRelative_path();
			logger.debug("filePath: " + filePath);
			String extension = FilenameUtils.getExtension(filePath);
			logger.debug("extension: " + extension);
		
			String newFilePath;
			if (filePath.equals(metadataFile)) {
				logger.warn("Metadata file should not be subject to a conversion!");
				continue;
			} else {				
				final String hash = DigestUtils.md5Hex(filePath);
				logger.debug("hash: " + hash);
				newFilePath = "_" + hash + "." + extension;				
			}
			
			logger.debug("newFilePath: " + newFilePath);
			File newFile = new File(file.getAbsolutePath().replaceAll(Pattern.quote(filePath)+"$", newFilePath));
			logger.debug("newFile: " + newFile.getAbsolutePath());
			
			daFile.setRelative_path(newFilePath);
			FileUtils.moveFile(file, newFile);
			map.put(newFilePath, filePath);
			
			deleteEmptyDirsRecursively(file.getAbsolutePath());

		}
			
		return true;
		
	}

	@Override
	void rollback() throws Exception {
		
		for (Event e:object.getLatestPackage().getEvents()) {
			if (!"CONVERT".equals(e.getType())) continue;
			
			DAFile daFile = e.getTarget_file();
			File file = daFile.toRegularFile();
			String filePath = daFile.getRelative_path();
			if (!map.containsKey(filePath)) continue;
			String oldFilePath = map.get(filePath);
			File oldFile = new File(file.getAbsolutePath().replaceAll(filePath+"$", oldFilePath));
			
			daFile.setRelative_path(oldFilePath);
			FileUtils.forceMkdir(oldFile.getParentFile());
			FileUtils.moveFile(file, oldFile);
			
		}
		
		logger.info("@Admin: You can safely roll back this job to status "+this.getStartStatus()+" now.");
	}
	
	private void deleteEmptyDirsRecursively(String path) {
		String dirPath = FilenameUtils.getFullPath(path);
		File dir = new File(dirPath);
		if (dir.isDirectory() && dir.list().length == 0) {
			dir.delete();
			deleteEmptyDirsRecursively(dir.getAbsolutePath());
		}
	}

}
