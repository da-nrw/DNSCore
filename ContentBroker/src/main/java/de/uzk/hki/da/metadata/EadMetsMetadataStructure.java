/*
  DA-NRW Software Suite | ContentBroker
  Copyright (C) 2015 LVRInfoKom
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
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.input.BOMInputStream;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import de.uzk.hki.da.utils.C;
import de.uzk.hki.da.utils.Path;
import de.uzk.hki.da.utils.XMLUtils;

/**
 * @author Polina Gubaidullina
 */

public class EadMetsMetadataStructure extends MetadataStructure{
	
	/** The logger. */
	public Logger logger = LoggerFactory
			.getLogger(EadMetsMetadataStructure.class);
	
	private final File eadFile;
	private List<String> metsReferencesInEAD;
	private List<File> metsFiles;
	private List<MetsMetadataStructure> mmsList;
	private List<String> missingMetsFiles;
	private Document eadDoc;
	private EadParser eadParser;
	
	HashMap<String, Document> metsPathToDocument = new HashMap<String, Document>();
	
	public EadMetsMetadataStructure(Path workPath,File metadataFile, List<de.uzk.hki.da.model.Document> documents) throws JDOMException, 
		IOException, ParserConfigurationException, SAXException {
		super(workPath,metadataFile, documents);
	
		eadFile = metadataFile;

		SAXBuilder builder = XMLUtils.createNonvalidatingSaxBuilder();		
		FileInputStream fileInputStream = new FileInputStream(Path.makeFile(workPath,eadFile.getPath()));
		BOMInputStream bomInputStream = new BOMInputStream(fileInputStream);
		Reader reader = new InputStreamReader(bomInputStream,"UTF-8");
		InputSource is = new InputSource(reader);
		is.setEncoding("UTF-8");
		eadDoc = builder.build(is);
		eadParser = new EadParser(eadDoc);

		metsReferencesInEAD = eadParser.getReferences();
		metsFiles = getReferencedFiles(eadFile, metsReferencesInEAD, documents);
				
		mmsList = new ArrayList<MetsMetadataStructure>();
		for(File metsFile : metsFiles) {
			MetsMetadataStructure mms = new MetsMetadataStructure(workPath,metsFile, documents);
			mmsList.add(mms);
		}
		fileInputStream.close();
		bomInputStream.close();
		reader.close();
	}
	
//	::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::  GETTER  ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::

	@Override
	public HashMap<String, HashMap<String, List<String>>> getIndexInfo(String objectId) {
		return eadParser.getIndexInfo(objectId);
	}
	
	public File getMetadataFile() {
		return eadFile;
	}
	
	public List<String> getMetsRefsInEad() {
		return metsReferencesInEAD;
	}
	
	public List<MetsMetadataStructure> getMetsMetadataStructures() {
		return mmsList;
	}
	
//	:::::::::::::::::::::::::::::::::::::::::::::::::::::::::  REPLACEMENTS  :::::::::::::::::::::::::::::::::::::::::::::::::::::::::
	
	public void replaceMetsRefsInEad(File eadFile, HashMap<String, String> eadReplacements) throws JDOMException, IOException {
		
		File targetEadFile = eadFile;
		
		SAXBuilder builder = XMLUtils.createNonvalidatingSaxBuilder();		
		FileInputStream fileInputStream = new FileInputStream(Path.makeFile(workPath,eadFile.getPath()));
		BOMInputStream bomInputStream = new BOMInputStream(fileInputStream);
		Reader reader = new InputStreamReader(bomInputStream,"UTF-8");
		InputSource is = new InputSource(reader);
		is.setEncoding("UTF-8");
		Document currentEadDoc = builder.build(is);
				
		String namespaceUri = eadDoc.getRootElement().getNamespace().getURI();
		XPath xPath = XPath.newInstance(C.EAD_XPATH_EXPRESSION);
		
//		Case of new DDB EAD with namespace xmlns="urn:isbn:1-931666-22-9"
		if(!namespaceUri.equals("")) {
			xPath = XPath.newInstance("//isbn:daoloc/@href");
			xPath.addNamespace("isbn", eadDoc.getRootElement().getNamespace().getURI());
		} 
		
		@SuppressWarnings("rawtypes")
		List allNodes = xPath.selectNodes(currentEadDoc);
		
		for (Object node : allNodes) {
			Attribute attr = (Attribute) node;
			for(String replacement : eadReplacements.keySet()) {
				if(attr.getValue().equals(replacement)) {
					attr.setValue(eadReplacements.get(replacement));
				}
			}
		}
		
		XMLOutputter outputter = new XMLOutputter();
		outputter.setFormat(Format.getPrettyFormat());
		outputter.output(currentEadDoc, new FileWriter(Path.makeFile(workPath,targetEadFile.getPath())));
		fileInputStream.close();
		bomInputStream.close();
		reader.close();
	}
	
//	:::::::::::::::::::::::::::::::::::::::::::::::::::::::::   VALIDATION   :::::::::::::::::::::::::::::::::::::::::::::::::::::::::

	private boolean checkReferencedFilesInEad() {
		if(metsReferencesInEAD.size()==getMetsMetadataStructures().size()) {
			return true;
		} else {
			logger.error("Expected "+metsReferencesInEAD.size()+" METS files but found "+metsFiles.size()+" METS files.");
			logger.error("Missing mets files: ");
			for(String missingMetsFile: missingMetsFiles) {
				logger.error(missingMetsFile);
			}
			return false;
		}
	}
	
	private boolean checkReferencedFilesInMetsFiles() {
		Boolean mmsIsValid = true;
		List<MetsMetadataStructure> mmss = getMetsMetadataStructures();
		for (MetsMetadataStructure mms : mmss) {
			if(!mms.isValid()) {
				logger.error("METS metadata structure "+mms.getMetadataFile().getName()+" is not valid!");
				mmsIsValid = false;
			}
		}
		return mmsIsValid;
	}
	
	@Override
	public boolean isValid() {
		return (checkReferencedFilesInEad() && checkReferencedFilesInMetsFiles());
	}
	
//	::::::::::::::::::::::::::::::::::::::::::::::::::::::::::   OVERRIDE   ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private HashMap<File, Boolean> checkExistenceOfReferencedFiles(File metadataFile, List<String> references, List<de.uzk.hki.da.model.Document> documents) {
		HashMap fileExistenceMap = new HashMap<File, Boolean>();
		missingMetsFiles= new ArrayList<String>();
		for(String ref : references) {
			File refFile;
			Boolean fileExists = false;
			try {
				refFile = XMLUtils.getRelativeFileFromReference(ref, metadataFile);
				logger.debug("Check referenced file: "+Path.makeFile(workPath,refFile.getPath()).getCanonicalFile());
				if(Path.makeFile(workPath,refFile.getPath()).exists()) {
					fileExists = true; 
				} else {
					fileExists = false;
					logger.error("File "+ref+" does not exist.");
					missingMetsFiles.add(ref);
				}
				fileExistenceMap.put(refFile, fileExists);
			} catch (IOException e) {
				logger.error("File "+ref+" does not exist.");
				missingMetsFiles.add(ref);
				e.printStackTrace();
			}
		}
		return fileExistenceMap;
	}
	
	@Override
	public List<File> getReferencedFiles(File metadataFile, List<String> references, List<de.uzk.hki.da.model.Document> documents) {
		HashMap<File, Boolean> fileExistenceMap = checkExistenceOfReferencedFiles(metadataFile, references, documents);
		List<File> existingMetsFiles = new ArrayList<File>();
		for(File file : fileExistenceMap.keySet()) {
			if(fileExistenceMap.get(file)==true) {
				existingMetsFiles.add(file);
			}
		}
		return existingMetsFiles;
	}
}
