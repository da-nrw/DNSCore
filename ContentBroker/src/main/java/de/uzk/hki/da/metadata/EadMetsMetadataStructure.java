package de.uzk.hki.da.metadata;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
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
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;
import org.xml.sax.SAXException;

/**
 * @author Polina Gubaidullina
 */

public class EadMetsMetadataStructure extends MetadataStructure{
	
	private String EAD_XPATH_EXPRESSION = 		"//daoloc/@href";
	
	private final File eadFile;
	private List<String> metsReferencesInEAD;
	private List<File> metsFiles;
	private List<MetsMetadataStructure> mmsList;
	private List<String> missingMetsFiles;
	private Document eadDoc;
	
	HashMap<String, Document> metsPathToDocument = new HashMap<String, Document>();
	
	public EadMetsMetadataStructure(File metadataFile, List<de.uzk.hki.da.model.Document> documents) throws JDOMException, 
		IOException, ParserConfigurationException, SAXException {
		super(metadataFile, documents);
	
		eadFile = metadataFile;
		
		SAXBuilder builder = XMLUtils.createNonvalidatingSaxBuilder();
		FileInputStream fileInputStream = new FileInputStream(eadFile);
		BOMInputStream bomInputStream = new BOMInputStream(fileInputStream);
		eadDoc = builder.build(bomInputStream);
		
		metsReferencesInEAD = extractMetsRefsInEad();
		metsFiles = getReferencedFiles(eadFile, metsReferencesInEAD, documents);
				
		mmsList = new ArrayList<MetsMetadataStructure>();
		for(File metsFile : metsFiles) {
			MetsMetadataStructure mms = new MetsMetadataStructure(metsFile, documents);
			mmsList.add(mms);
		}
		
		printIndexInfo();
	}
	
//	::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::  GETTER  ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::

	@Override
	protected HashMap<String, HashMap<String, String>> getIndexInfo() {
		
//		<ID<Attribut, Value>>
		HashMap<String, HashMap<String, String>> indexInfo = new HashMap<String, HashMap<String,String>>();
		
//		Root
		Element archdesc = eadDoc.getRootElement().getChild("archdesc");
		
//		First Level
		Element dsc = archdesc.getChild("dsc");
		List<Element> c01 = dsc.getChildren("c01");
		
//		Element: childElement
//		String: isPartOf parentID
		HashMap<Element, String> childElements = new HashMap<Element, String>();
		for(Element e : c01) {
			childElements.put(e, "root");
		}
		
		for(int i=1; i<13; i++) {
			
			String nextLevel = (Integer.toString(i+1));
			if(i<10) {
				nextLevel = "c0"+nextLevel;
			} else nextLevel = "c"+nextLevel;
			
			HashMap<Element, String> currentElements = new HashMap<Element, String>();
			currentElements = childElements;
			
			for(Element element : currentElements.keySet()) {
				HashMap<String, String> nodeInfo = new HashMap<String, String>();
				childElements = new HashMap<Element, String>();
				String uniqueID = UUID.randomUUID().toString();
				uniqueID = uniqueID.replace("-", "");
				String isPartOf = currentElements.get(element);
				
				nodeInfo.put("Level", Integer.toString(i));
				nodeInfo.put("isPartOf", isPartOf);
				
				List<Element> children = element.getChildren();
				for(Element child : children) {
					if(child.getName().equals("did")) {
						nodeInfo.put("title", getTitle(child));
						nodeInfo.put("date", getDate(child));
					} else if(child.getName().equals("daogrp")) {
						nodeInfo.put("href", getHref(child));
					} else if(child.getName().equals(nextLevel)) {
						childElements.put(child, uniqueID);
					}
				}
				indexInfo.put(uniqueID, nodeInfo);
			}	
		}
		return indexInfo;
	}
	
	private String getTitle(Element element) {
		String title = "";
		try {
			title = element.getChild("unittitle").getValue();
		} catch (Exception e) {
			logger.error("No unittitle element found");
		}
		return title;
	}
	
	private String getDate(Element element) {
		String date = "";
		try {
			date = element.getChild("unitdate").getValue();
		} catch (Exception e) {
			logger.error("No unitdate element found");
		}
		return date;
	}
	
	private String getUnitIDs(Element element) {
		String unitID = "";
		try {
			List<Element> unitIdElements = element.getChildren("unitdate");
			if(unitIdElements.size()>1) {
				for(Element id : unitIdElements) {
					String altsignatur = "";
					if(id.getAttribute("type").getName().equals("altsignatur")) {
						altsignatur = id.getValue();
					} else if(id.getAttribute("type").getName().equals("altsignatur")) {
						
					}
				}
			}
			
			
		} catch (Exception e) {
			logger.error("No unitdate element found");
		}
		
		
		
		
		return null;
	}
	
	private String getHref(Element element) {
		String href = "";
		try {
			href = element.getChild("daogrp").getChild("daoloc").getAttributeValue("href");
		} catch (Exception e) {
			logger.error("No unitdate element found");
		}
		return href;
	}
	
	public File getMetadataFile() {
		return eadFile;
	}
	
	public List<String> getMetsRefsInEad() {
		return metsReferencesInEAD;
	}
	
	private List<String> extractMetsRefsInEad() throws JDOMException, IOException {
		
		List<String> metsReferences = new ArrayList<String>();
	
		XPath xPath = XPath.newInstance(EAD_XPATH_EXPRESSION);
		
		@SuppressWarnings("rawtypes")
		List allNodes = xPath.selectNodes(eadDoc);
		
		for (Object node : allNodes) {
			Attribute attr = (Attribute) node;
			String href = attr.getValue();
			metsReferences.add(href);
		}
		return metsReferences;
	}
	
	public List<MetsMetadataStructure> getMetsMetadataStructures() {
		return mmsList;
	}
	
//	:::::::::::::::::::::::::::::::::::::::::::::::::::::::::  REPLACEMENTS  :::::::::::::::::::::::::::::::::::::::::::::::::::::::::
	
	public void replaceMetsRefsInEad(File eadFile, HashMap<String, String> eadReplacements) throws JDOMException, IOException {
		
		File targetEadFile = eadFile;
		
		SAXBuilder builder = XMLUtils.createNonvalidatingSaxBuilder();
		FileInputStream fileInputStream = new FileInputStream(eadFile);
		BOMInputStream bomInputStream = new BOMInputStream(fileInputStream);
		Document currentEadDoc = builder.build(bomInputStream);
				
		XPath xPath = XPath.newInstance(EAD_XPATH_EXPRESSION);
		
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
		outputter.output(currentEadDoc, new FileWriter(targetEadFile));
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
				refFile = getCanonicalFileFromReference(ref, metadataFile);
				logger.debug("Check referenced file: "+refFile.getAbsolutePath());
				if(refFile.exists()) {
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
