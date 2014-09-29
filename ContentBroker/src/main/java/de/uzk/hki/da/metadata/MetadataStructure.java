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
	private HashMap<File, Boolean> checkExistenceOfReferencedFiles(File metadataFile, List<String> references, List<DAFile> daFiles) {
		HashMap fileExistenceMap = new HashMap<File, Boolean>();
		for(String ref : references) {
			File refFile;
			Boolean fileExists = false;
			try {
				refFile = getCanonicalFileFromReference(ref, metadataFile);
				logger.debug("Check referenced file: "+refFile.getAbsolutePath());
				if(refFile.exists()) {
					fileExists = true; 
				} else {
					fileExists = false;
				}
				fileExistenceMap.put(refFile, fileExists);
			} catch (IOException e) {
				logger.error("File "+ref+" does not exist.");
				e.printStackTrace();
			}
		}
		return fileExistenceMap;
	}
	
	public List<File> getReferencedFiles(File metadataFile, List<String> references, List<DAFile> daFiles) {
		HashMap<File, Boolean> fileExistenceMap = checkExistenceOfReferencedFiles(metadataFile, references, daFiles);
		List<File> existingMetsFiles = new ArrayList<File>();
		for(File file : fileExistenceMap.keySet()) {
			if(fileExistenceMap.get(file)==true) {
				existingMetsFiles.add(file);
			}
		}
		return existingMetsFiles;
	}
}
