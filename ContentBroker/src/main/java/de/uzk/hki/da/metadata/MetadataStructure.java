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

import org.apache.commons.io.FilenameUtils;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uzk.hki.da.model.DAFile;
import de.uzk.hki.da.model.Document;
import de.uzk.hki.da.util.Path;

/**
 * @author Polina Gubaidullina
 */

public abstract class MetadataStructure {
	
	/** The logger. */
	public Logger logger = LoggerFactory
			.getLogger(MetadataStructure.class);
	
	public MetadataStructure(File metadataFile, List<Document> documents) 
			throws FileNotFoundException, JDOMException, IOException {
	}
	
	public abstract boolean isValid();
	
	public File getCanonicalFileFromReference(String ref, File metadataFile) throws IOException {
		String tmpFilePath = Path.make(metadataFile.getParentFile().getAbsolutePath(), ref).toString();
		return new File(tmpFilePath).getCanonicalFile();
	}
	
	public abstract File getMetadataFile();
	
	protected abstract HashMap<String, HashMap<String, String>> getIndexInfo();
	
	protected void printIndexInfo() {
		HashMap<String, HashMap<String, String>> indexInfo = getIndexInfo();
		for(String id : indexInfo.keySet()) {
			logger.info("-----------------------------------------------------");
			logger.info("ID: "+id);
			for(String info : indexInfo.get(id).keySet()) {
				logger.info(info+": "+indexInfo.get(id).get(info));
			}
			logger.info("-----------------------------------------------------");
		}
	}
	
	protected List<File> getReferencedFiles(File metadataFile, List<String> references, List<Document> documents) {
		List<File> existingFiles = new ArrayList<File>();
		List<String> missingFiles = new ArrayList<String>();
		for(String ref : references) {
			File refFile;
			try {
				refFile = getCanonicalFileFromReference(ref, metadataFile);
				String fileName = FilenameUtils.getBaseName(refFile.getName());
				logger.debug("Check referenced file "+fileName+" (reference: "+ref+")");
				Boolean docExists = false;
				for(Document doc : documents) {
					if(doc.getName().equals(fileName)) {
						docExists = true;
						
						Boolean fileExists = false;
						
						DAFile lastDAFile = doc.getLasttDAFile();
						
						File f = getExistingFile(metadataFile, refFile, lastDAFile);
						if(f!=null) {
							fileExists = true;
						} else {
							while(lastDAFile.getPreviousDAFile() != null){
					        	f = getExistingFile(metadataFile, refFile, lastDAFile.getPreviousDAFile());
					        	if(f!=null) {
					        		fileExists = true;
					        		break;
								}
					        	lastDAFile = lastDAFile.getPreviousDAFile(); 
					        }
						}
						if(fileExists) {
							existingFiles.add(f);
						} else {
							logger.error("File "+ref+" does not exist.");
							missingFiles.add(ref);
						}
					}
				}
				if(!docExists) {
					logger.debug("There is no document "+fileName+"!");
					logger.error("File "+ref+" does not exist.");
					missingFiles.add(ref);
				}
			} catch (IOException e) {
				logger.error("File "+ref+" does not exist.");
				e.printStackTrace();
			}
		}
		if(!missingFiles.isEmpty()) {
			logger.error("Missing files: ");
			for(String missingFile : missingFiles) {
				logger.error(missingFile);
			}
		}
		return existingFiles;
	}
	
	private File getExistingFile(File metadataFile, File refFile, DAFile dafile) {
		File existingFile = null;
		
		String nameOfMetadataParentFile = metadataFile.getParentFile().getName();
		String relPathFromMetadataFile = Path.extractRelPathFromDir(refFile, nameOfMetadataParentFile);
		
		String dafileRelPath = "";	
		File file = dafile.toRegularFile();
		if(nameOfMetadataParentFile.endsWith("+a") || nameOfMetadataParentFile.endsWith("+b") || nameOfMetadataParentFile.equals("public") || nameOfMetadataParentFile.equals("institution")) {
			dafileRelPath = dafile.getRelative_path();
		} else {
			dafileRelPath = Path.extractRelPathFromDir(file, nameOfMetadataParentFile);
		}
		if(dafileRelPath.equals(relPathFromMetadataFile)) {
			existingFile = file;
			logger.debug("File "+existingFile+" exists!");
		} 
		return existingFile;
	}

}
