/*
  DA-NRW Software Suite | ContentBroker
  Copyright (C) 2014 LVRInfoKom
  Landschaftsverband Rheinland

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

package de.uzk.hki.da.metadata;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.jdom.JDOMException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uzk.hki.da.model.DAFile;
import de.uzk.hki.da.path.Path;

/**
 * @author Polina Gubaidullina
 */

public abstract class MetadataStructure {
	
	/** The logger. */
	public Logger logger = LoggerFactory
			.getLogger(MetadataStructure.class);
	
	public MetadataStructure(File metadataFile, List<DAFile> daFiles) 
			throws FileNotFoundException, JDOMException, IOException {
	}
	
	public abstract boolean isValid();
	
	public File getCanonicalFileFromReference(String ref, File metadataFile) throws IOException {
		String tmpFilePath = Path.make(metadataFile.getParentFile().getAbsolutePath(), ref).toString();
		return new File(tmpFilePath).getCanonicalFile();
	}
	
	public abstract File getMetadataFile();
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private HashMap<DAFile, Boolean> checkExistenceOfReferencedFiles(File metadataFile, List<String> references, List<DAFile> daFiles) {
		HashMap fileExistenceMap = new HashMap<File, Boolean>();
		for(String ref : references) {
			File refFile;
			DAFile daFile = null;
			try {
				refFile = getCanonicalFileFromReference(ref, metadataFile);
				logger.debug("Check referenced file: "+refFile.getAbsolutePath());
				Boolean fileExists = false;
				for(DAFile currentDafile : daFiles) {
					daFile = currentDafile;
					logger.debug("DAFile: "+daFile.getRelative_path());
					if(refFile.getAbsolutePath().contains(daFile.getRelative_path())) {
						fileExists = true;
						break;
					} else {
						fileExists = false;
					}
				}
				fileExistenceMap.put(daFile, fileExists);
				if(fileExists) {
					logger.debug("File "+ref+" exists.");
				} else {
					logger.error("File "+ref+" does not exist.");
				}
			} catch (IOException e) {
				logger.error("File "+ref+" does not exist.");
				e.printStackTrace();
			}
		}
		return fileExistenceMap;
	}
	
	public List<DAFile> getReferencedFiles(File metadataFile, List<String> references, List<DAFile> daFiles) {
		HashMap<DAFile, Boolean> fileExistenceMap = checkExistenceOfReferencedFiles(metadataFile, references, daFiles);
		List<DAFile> existingMetsFiles = new ArrayList<DAFile>();
		for(DAFile dafile : fileExistenceMap.keySet()) {
			if(fileExistenceMap.get(dafile)==true) {
				existingMetsFiles.add(dafile);
			}
		}
		return existingMetsFiles;
	}
}
