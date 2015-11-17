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
import java.util.UUID;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.input.BOMInputStream;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
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
	private Namespace EAD_NS;
	
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
		EAD_NS = eadDoc.getRootElement().getNamespace();
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

	@SuppressWarnings("unchecked")
	public HashMap<String, HashMap<String, List<String>>> getIndexInfo(String objectId) {
		
//		<ID<Attribut, Value>>
		HashMap<String, HashMap<String, List<String>>> indexInfo = new HashMap<String, HashMap<String,List<String>>>();
		
//		Root
		Element archdesc = eadDoc.getRootElement().getChild("archdesc", EAD_NS);

		Element archdescDid = archdesc.getChild("did", EAD_NS);
		HashMap<String, List<String>> rootInfo = new HashMap<String, List<String>>();
		setNodeInfoAndChildeElements(archdescDid, rootInfo, null, null, null);
		indexInfo.put(objectId, rootInfo);

		Element dsc = archdesc.getChild("dsc", EAD_NS);
		List<Element> c01 = dsc.getChildren("c01", EAD_NS);
		if(c01.isEmpty()) {
			c01 = dsc.getChildren("c", EAD_NS);
		}

//		Element: childElement
//		String: isPartOf parentID
		HashMap<Element, String> childElements = new HashMap<Element, String>();
		for(Element e : c01) {
			childElements.put(e, objectId);
		}
		
//		String ID 
//		ArrayList<String> partIDs
		HashMap<String, ArrayList<String>> parentHasParts = new HashMap<String, ArrayList<String>>();
		
		for(int i=1; i<13; i++) {
			
			String nextLevel = (Integer.toString(i+1));
			if(i<9) {
				nextLevel = "c0"+nextLevel;
			} else nextLevel = "c"+nextLevel;
			
			HashMap<Element, String> currentElements = new HashMap<Element, String>();
			currentElements = childElements;
			childElements = new HashMap<Element, String>();
			
			String isPartOf = "";
			for(Element element : currentElements.keySet()) {
				HashMap<String, List<String>> nodeInfo = new HashMap<String, List<String>>();
				String uniqueID = UUID.randomUUID().toString();
				uniqueID = uniqueID.replace("-", "");
				String id = objectId+"-"+uniqueID;
				
				String parentId = currentElements.get(element);
				isPartOf = parentId;
				
				if(parentHasParts.get(parentId)==null) {
					ArrayList<String> hasPart = new ArrayList<String>();
					parentHasParts.put(parentId, hasPart);
				}
				parentHasParts.get(parentId).add(id);
				
				ArrayList<String> partOf = new ArrayList<String>();
				partOf.add(isPartOf);
				nodeInfo.put(C.EDM_IS_PART_OF, partOf);
				
				List<Element> children = element.getChildren();
				for(Element child : children) {
					setNodeInfoAndChildeElements(child, nodeInfo, nextLevel, childElements, id);
				}
				indexInfo.put(id, nodeInfo);
			}
			for(String parentId : parentHasParts.keySet()) {
				indexInfo.get(parentId).put(C.EDM_HAS_PART, parentHasParts.get(parentId));
			}
		}
		return indexInfo;
	}
	
	void setNodeInfoAndChildeElements(Element child, HashMap<String, List<String>> nodeInfo, String nextLevel, HashMap<Element, String> childElements, String uniqueID) {
		if(child.getName().equals("did")) {
			nodeInfo.put(C.EDM_TITLE, eadParser.getTitle(child));
			nodeInfo.put(C.EDM_DATE, eadParser.getDate(child));
			nodeInfo.put(C.EDM_IDENTIFIER, eadParser.getUnitIDs(child));
		} else if(child.getName().equals("daogrp")) {
			List<String> references = eadParser.getHref(child);
			
//			Replace mets references by file references
			references = updateReferences(references);

			if(references!=null & references.size()!=0) {
				List<String> shownBy = new ArrayList<String>();
				shownBy.add(references.get(0));
				nodeInfo.put(C.EDM_IS_SHOWN_BY, shownBy);
				nodeInfo.put(C.EDM_OBJECT, shownBy);
			} 
			if(references.size()>1) {
				nodeInfo.put(C.EDM_HAS_VIEW, references);
			}
			
		} else if(uniqueID!=null && (child.getName().equals(nextLevel) || child.getName().equals("c"))) {
			childElements.put(child, uniqueID);
		} 
	}
	
	public List<String> updateReferences(List<String> referencesInEad) {
		List<String> referencesInMetsFiles = new ArrayList<String>();
		for(String r : referencesInEad) {
			logger.debug("Search for references in references mets file "+r);
			try {
				List<String> metsRefs = getMetsReferences(r);
				if(metsRefs!=null && !metsRefs.isEmpty()) {
					for(String metsRef : metsRefs) {
						referencesInMetsFiles.add(metsRef);
					}
				}
			} catch (JDOMException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		if(!referencesInMetsFiles.isEmpty()) {
			referencesInEad = referencesInMetsFiles;
		}
		return referencesInEad;
	}

	public File getMetsFileFromPIPHref(String href) {
		File metsFile = null;
		try {
			metsFile = Path.makeFile(workPath, new File(href).getCanonicalFile().getName());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return metsFile;
	}
	
	public List<String> getMetsReferences(String metsRefInEad) throws JDOMException, IOException {
		logger.debug("Search for references in mets: "+metsRefInEad);
		List<String> fileReferencesInMets = null;
		File metsFile = getMetsFileFromPIPHref(metsRefInEad);
		if(metsFile.exists()) {
			Document metsDoc = getMetsDocument(metsFile);
			MetsParser mp = new MetsParser(metsDoc);
			fileReferencesInMets = mp.getReferences();
		} else {
			logger.debug("Mets file "+metsFile+" does not exist.");
		}
		return fileReferencesInMets;
	}
	
	private Document getMetsDocument(File metsFile) throws JDOMException, IOException {
		SAXBuilder builder = XMLUtils.createNonvalidatingSaxBuilder();		
		FileInputStream fileInputStream = new FileInputStream(metsFile);
		BOMInputStream bomInputStream = new BOMInputStream(fileInputStream);
		Reader reader = new InputStreamReader(bomInputStream,"UTF-8");
		InputSource is = new InputSource(reader);
		is.setEncoding("UTF-8");
		eadDoc = builder.build(is);
		return eadDoc;
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
