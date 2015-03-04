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

import de.uzk.hki.da.core.C;

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
		fileInputStream.close();
	}
	
//	::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::  GETTER  ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::

	@SuppressWarnings("unchecked")
	@Override
	public HashMap<String, HashMap<String, List<String>>> getIndexInfo(String objectId) {
		
//		<ID<Attribut, Value>>
		HashMap<String, HashMap<String, List<String>>> indexInfo = new HashMap<String, HashMap<String,List<String>>>();
		
//		Root
		Element archdesc = eadDoc.getRootElement().getChild("archdesc");
		
		Element archdescDid = archdesc.getChild("did");
		HashMap<String, List<String>> rootInfo = new HashMap<String, List<String>>();
		setNodeInfoAndChildeElements(archdescDid, rootInfo, null, null, null);
		indexInfo.put(objectId, rootInfo);

		Element dsc = archdesc.getChild("dsc");
		List<Element> c01 = dsc.getChildren("c01");

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
	
	private void setNodeInfoAndChildeElements(Element child, HashMap<String, List<String>> nodeInfo, String nextLevel, HashMap<Element, String> childElements, String uniqueID) {
		if(child.getName().equals("did")) {
			nodeInfo.put(C.EDM_TITLE, getTitle(child));
			nodeInfo.put(C.EDM_DATE, getDate(child));
			nodeInfo.put(C.EDM_IDENTIFIER, getUnitIDs(child));
		} else if(child.getName().equals("daogrp")) {
			nodeInfo.put(C.EDM_HAS_VIEW, getHref(child));
		} else if(uniqueID!=null && child.getName().equals(nextLevel)) {
			childElements.put(child, uniqueID);
		}
	}
	
	private List<String> getTitle(Element element) {
		List<String> title = new ArrayList<String>();
		String t = "";
		try {
			t = element.getChild("unittitle").getValue();
		} catch (Exception e) {
			logger.error("No unittitle element found");
		}
		title.add(t);
		return title;
	}
	
	private List<String> getDate(Element element) {
		List<String> date = new ArrayList<String>();
		String d = "";
		try {
			d = element.getChild("unitdate").getAttribute("normal").getValue();
			if(d.equals("")) {
				d = element.getChild("unitdate").getValue();
			}
		} catch (Exception e) {
			logger.debug("No unitdate element found");
		}
		date.add(d);
		return date;
	}
	
	private List<String> getUnitIDs(Element did) {
		List<String> unitIDs = new ArrayList<String>();
		@SuppressWarnings("unchecked")
		List<Element> children = did.getChildren("unitid");
		
		for(Element child : children) {
			String unitID = "";
			String type = "";
			try {
				type = child.getAttribute("type").getValue();
				if(!type.equals("")) {
					unitID = type+": "+child.getValue();
					unitIDs.add(unitID);
				} else unitID = child.getValue();
			} catch (Exception e) {
			}
		}
		return unitIDs;
	}
	
	private List<String> getHref(Element daogrp) {
		List<String> hrefs = new ArrayList<String>();
		String href = "";
		try {
			href = daogrp.getChild("daoloc").getAttributeValue("href");
		} catch (Exception e) {
			logger.debug("No unitdate element found");
		}
		hrefs.add(href);
		return hrefs;
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
		fileInputStream.close();
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
