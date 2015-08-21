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
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.input.BOMInputStream;
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

import de.uzk.hki.da.core.C;
import de.uzk.hki.da.util.Path;

/**
 * @author Polina Gubaidullina
 */

public class MetsMetadataStructure extends MetadataStructure {

	/** The logger. */
	public Logger logger = LoggerFactory
			.getLogger(MetsMetadataStructure.class);
	
	private File metsFile;
	private String METS_XPATH_EXPRESSION= 		"//mets:file";
	private XPath metsXPath = 					XPath.newInstance(METS_XPATH_EXPRESSION);
	private static final Namespace XLINK_NS = 	Namespace.getNamespace("http://www.w3.org/1999/xlink");
	private List<de.uzk.hki.da.model.Document> currentDocuments;
	private Document metsDoc;
	private final String STRUCTMAP_TYPE_LOGICAL = "LOGICAL";
	private final String TITLE_PAGE = "title_page";

	List<Element> fileElements;
	Element modsXmlData;
	
	public MetsMetadataStructure(Path workPath,File metadataFile, List<de.uzk.hki.da.model.Document> documents)
			throws FileNotFoundException, JDOMException, IOException {
		super(workPath,metadataFile, documents);
		
		metsFile = metadataFile;
		currentDocuments = documents;
		
		SAXBuilder builder = XMLUtils.createNonvalidatingSaxBuilder();		
		FileInputStream fileInputStream = new FileInputStream(Path.makeFile(workPath,metsFile.getPath()));
		BOMInputStream bomInputStream = new BOMInputStream(fileInputStream);
		Reader reader = new InputStreamReader(bomInputStream,"UTF-8");
		InputSource is = new InputSource(reader);
		is.setEncoding("UTF-8");
		metsDoc = builder.build(is);
		
		fileElements = getFileElementsFromMetsDoc(metsDoc);
		fileInputStream.close();
		bomInputStream.close();
		reader.close();
	}
	
//	::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::  GETTER  ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
	
	
	public String getMetsUrn() {
		String urn = null;
		try {
			String rootDmdSecId = getUniqueRootElementInLogicalStructMap().getAttributeValue("DMDID");
			@SuppressWarnings("unchecked")
			List<Element> dmdSecs = metsDoc.getRootElement().getChildren("dmdSec", C.METS_NS);
			for(Element dmdSec : dmdSecs) {
				if(dmdSec.getAttributeValue("ID").equals(rootDmdSecId)) {
					Element rootDmdSec = dmdSec;
					@SuppressWarnings("unchecked")
					List<Element> elements = getModsXmlData(rootDmdSec).getChildren();
					for (Element e : elements) {
						if(e.getName().equals("identifier") && e.getAttributeValue("type").equals("urn")) {
							urn = e.getValue();
						}
					}
				}
			}
		} catch (Exception e) {
			logger.error("Unable to find urn.");
		}
		return urn;
	}
	
	
	@Override
	public HashMap<String, HashMap<String, List<String>>> getIndexInfo(String ObjectId) {
		HashMap<String, HashMap<String, List<String>>> indexInfo = new HashMap<String, HashMap<String,List<String>>>();
		HashMap<String, Element> dmdSections = getSections(ObjectId);
		
		for(String id : dmdSections.keySet()) {
			Element e = dmdSections.get(id);
			HashMap<String, List<String>> dmdSecInfo = new HashMap<String, List<String>>();
			
//			Title
			dmdSecInfo.put(C.EDM_TITLE, getTitle(e));
			
//			Names
			List<String> creators = new ArrayList<String>();
			List<String> contributors = new ArrayList<String>();
			for(Element name : getNameElements(e)) {
				String creator = getCreator(name);
				String contributor = getContributor(name);
				if(!creator.equals("")) {
					creators.add(creator);
				}
				if(!contributor.equals("")) {
					contributors.add(contributor);
				}
			}
			dmdSecInfo.put(C.EDM_CREATOR, creators);
			dmdSecInfo.put(C.EDM_CONTRIBUTOR, contributors);
			
//			Date && Place
			List<String> dates = new ArrayList<String>();
			List<String> publishers = new ArrayList<String>();
			for(Element origInfo : getOrigInfoElements(e)) {
				String date = getDate(origInfo);
				String publisher = getPublisher(origInfo);
				if(!date.equals("")) {
					dates.add(date);
				}
				if(!publisher.equals("")) {
					publishers.add(publisher);
				}
			}
			dmdSecInfo.put(C.EDM_DATE, dates);
			dmdSecInfo.put(C.EDM_PUBLISHER, publishers);
			
//			TitlePage
			List<String> titlePageRefs = getTitlePageReferencesFromDmdId(id, ObjectId);
			List<String> allReferences = getReferencesFromDmdId(id, ObjectId);
			if(titlePageRefs!=null & !titlePageRefs.isEmpty()) {
				List<String> references = new ArrayList<String>();
				references.add(titlePageRefs.get(0));
				dmdSecInfo.put(C.EDM_IS_SHOWN_BY, references);
				dmdSecInfo.put(C.EDM_OBJECT, references);
				if(titlePageRefs.size()>1) {
					dmdSecInfo.put(C.EDM_HAS_VIEW, titlePageRefs);
				}
			} else if(allReferences!=null && !allReferences.isEmpty()){
				List<String> firstReference = new ArrayList<String>();
				firstReference.add(allReferences.get(0));
				dmdSecInfo.put(C.EDM_IS_SHOWN_BY, firstReference);
				dmdSecInfo.put(C.EDM_OBJECT, firstReference);
			}
			
//			hasView
			if(allReferences.size()>1) {
				dmdSecInfo.put(C.EDM_HAS_VIEW, allReferences);
			}
			
//			dataProvider
			dmdSecInfo.put(C.EDM_DATA_PROVIDER, getDataProvider());
			
//			hasPart
			ArrayList<String> childrenDmdIds = getChildrenDmdIds(id, ObjectId);
			if(childrenDmdIds!=null && !childrenDmdIds.isEmpty()) {
				dmdSecInfo.put(C.EDM_HAS_PART, childrenDmdIds);
			}
			
//			isPartOf
			ArrayList<String> parentsDmdIds = getParentDmdIds(id, ObjectId);
			if(parentsDmdIds!=null && !parentsDmdIds.isEmpty()) {
				dmdSecInfo.put(C.EDM_IS_PART_OF, parentsDmdIds);
			}
			indexInfo.put(id, dmdSecInfo);
		}
		return indexInfo;
	}
	
	private List<String> getTitlePageReferencesFromDmdId(String dmdID, String objectId) {
		List<String> titlePageRefs = new ArrayList<String>();
		String titlePageLogicalId = getIdFromLogicalStructMap(dmdID.replace(objectId+"-", ""), TITLE_PAGE, "ID");
		List<String> physIds = getPhysicalIdsFromStructLink(metsDoc, titlePageLogicalId);
		for(String physId : physIds) {
			String titlePageFileId = getFileIdFromPhysicalId(physId);
			if(!titlePageFileId.equals("")) {
				String titlePageRef = getReferenceFromFileId(titlePageFileId);
				if(!titlePageRef.equals("")) {
					titlePageRefs.add(titlePageRef);
				}
			}
		}
		return titlePageRefs;
	}
	
	private List<String> getReferencesFromDmdId(String dmdID, String objectId) {	
		List<String> references = new ArrayList<String>();
		String logicalId = getIdFromLogicalStructMap(dmdID.replace(objectId+"-", ""), "", "ID");
		List<String> physicalIds = getPhysicalIdsFromStructLink(metsDoc, logicalId);
		for(String physicalId : physicalIds) {
			if(!getFileIdFromPhysicalId(physicalId).equals("")) {
				String fileId = getFileIdFromPhysicalId(physicalId);
				String ref = getReferenceFromFileId(fileId);
				if(!ref.equals("")) {
					references.add(ref);
				}
			}
		}
		return references;
	}
	
	@SuppressWarnings("unchecked")
	private String getFileIdFromPhysicalId(String physicalId) {
		String fileId = "";
		try {
			List<Element> structMaps = getStructMaps(metsDoc);
			for(Element s : structMaps) {
				if(s.getAttributeValue("TYPE").equals("PHYSICAL")) {
					List<Element> divList =  s.getChildren("div", C.METS_NS);
					for(Element div : divList) {
						if(div.getAttributeValue("TYPE").equals("physSequence") && div.getAttributeValue("ID").equals("physroot")) {
							List<Element> divs =  div.getChildren("div", C.METS_NS);
							for(Element d : divs) {
								if(d.getAttributeValue("ID").equals(physicalId)) {
									fileId = d.getChild("fptr", C.METS_NS).getAttributeValue("FILEID");
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			logger.debug("Unable to file the file id.");
		}
		return fileId;
	}
	
	@SuppressWarnings("unchecked")
	private List<Element> getStructMaps(Document doc) {
		List<Element> structMap = new ArrayList<Element>();
		try {
			structMap = doc.getRootElement().getChildren("structMap", C.METS_NS);
		} catch (Exception e) {
			logger.debug("Unable to find the structMap elements.");
		}
		return structMap;
	} 
	
	private Element getUniqueRootElementInLogicalStructMap() {
		Element logicalRootElement = null;
		try {
			List<Element> structMap = getStructMaps(metsDoc);
			for(Element s : structMap) {
				if(s.getAttributeValue("TYPE").equals(STRUCTMAP_TYPE_LOGICAL)) {
					@SuppressWarnings("unchecked")
					List<Element> metsDivElements = s.getChildren("div", C.METS_NS);
					if(metsDivElements.size()==1) {
						logicalRootElement = metsDivElements.get(0);
					} else if(metsDivElements.size()==0) {
						logger.error("No unique root element found in the logical structMap!");
					} else {
						logger.error("Found multiple root elements in the logical structMap!");
					}
				}
			}
		} catch (Exception e) {
			logger.error("Unable to find the unique root element in the logical structMap!");
		}
		return logicalRootElement;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private HashMap<String, ArrayList<String>> getParentChildInfoOfDmdIds(String objectId) {
		HashMap parentChildDmdId = new HashMap<String, ArrayList<String>>();
		String parentDmdId = "";
		try {
			Element rootDivElement = getUniqueRootElementInLogicalStructMap();
			ArrayList<String> children = new ArrayList<String>();
			List<Element> divChildren =  rootDivElement.getChildren("div", C.METS_NS);
			if(divChildren!=null && !divChildren.isEmpty()) {
				for(Element divChild : divChildren) {
					if(divChild.getAttribute("DMDID")!=null && !divChild.getAttributeValue("DMDID").equals(parentDmdId)) {
						children.add(objectId+"-"+divChild.getAttributeValue("DMDID"));
					}
				}	
			}
			if(children!=null && !children.isEmpty()) {
				parentChildDmdId.put(rootDivElement.getAttributeValue("DMDID"), children);
			}	 
		} catch (Exception e) {
			logger.debug("No parent child relationship found.");
		}
		return parentChildDmdId;
	}
	
	private ArrayList<String> getParentDmdIds(String childDmdId, String ObjectId) {
		ArrayList<String> parentDmdIds = new ArrayList<String>();
		HashMap<String, ArrayList<String>> parentChildDmdIdRel = getParentChildInfoOfDmdIds(ObjectId);
		for(String parentId : parentChildDmdIdRel.keySet()) {
			for(String child : parentChildDmdIdRel.get(parentId)) {
				if(child.equals(childDmdId)) {
					parentDmdIds.add(ObjectId+"-"+parentId);
				}
			}
		}
		return parentDmdIds;
	}
	
	private ArrayList<String> getChildrenDmdIds(String parentDmdId, String ObjectId) {
		ArrayList<String> childrenDmdIds = new ArrayList<String>();
		HashMap<String, ArrayList<String>> parentChildDmdIdRel = getParentChildInfoOfDmdIds(ObjectId);
		for(String parentId : parentChildDmdIdRel.keySet()) {
			if(parentId.equals(parentDmdId.replace(ObjectId+"-", ""))) {
				childrenDmdIds = parentChildDmdIdRel.get(parentId);
			}
		}
		return childrenDmdIds;
	}
	
	@SuppressWarnings({ "unchecked" })
	private String getIdFromLogicalStructMap(String dmdID, String type, String idType) {
		String id = "";
		try {
			List<Element> structMap = getStructMaps(metsDoc);
			for(Element s : structMap) {
				if(s.getAttributeValue("TYPE").equals(STRUCTMAP_TYPE_LOGICAL)) {
					List<Element> metsDivElements = s.getChildren("div", C.METS_NS);
					for (Element d : metsDivElements){
						if(d.getAttributeValue("DMDID").equals(dmdID)) {
							if(type.equals(TITLE_PAGE)) {
								List<Element> divs =  d.getChildren("div", C.METS_NS);
								for(Element div : divs) {
									if(div.getAttributeValue("TYPE").equals(type)) {
										id = div.getAttributeValue(idType);
									}
								}
							} else {
								id = d.getAttributeValue(idType);
							}
						} else if(d.getChildren("div", C.METS_NS)!=null) {
							List<Element> divChildren =  d.getChildren("div", C.METS_NS);
							for(Element divChild : divChildren) {
								if(divChild.getAttributeValue("DMDID").equals(dmdID)) {
									id = divChild.getAttributeValue(idType);
								}
							}	
						}
					}
				}
			}
		} catch (Exception e) {
			logger.debug("Unable to find the "+idType+" id from dmdID "+dmdID);
		}
		return id;
	}
	
	@SuppressWarnings("unchecked")
	private List<String> getPhysicalIdsFromStructLink(Document doc, String logicalId) {
		List<String> physIds = new ArrayList<String>();
		List<Element> structLink = new ArrayList<Element>();
		try {
			structLink = doc.getRootElement().getChild("structLink", C.METS_NS).getChildren("smLink", C.METS_NS);
			for(Element link : structLink) {
				if(link.getAttributeValue("from", XLINK_NS).equals(logicalId)) {
					physIds.add(link.getAttributeValue("to", XLINK_NS));
				}
			}
		} catch (Exception e) {
			logger.debug("Unable to find the physical id from logical id "+logicalId);
		}
		return physIds;
	}
		
	private List<String> getDataProvider() {
		List<String> dataProvider = new ArrayList<String>();
		try {
			@SuppressWarnings("unchecked")
			List<Element> amdSections = metsDoc.getRootElement().getChildren("amdSec", C.METS_NS);
			for(Element amdSec : amdSections) {
				if(amdSec.getChild("rightsMD", C.METS_NS).getChild("mdWrap", C.METS_NS).getAttribute("OTHERMDTYPE").getValue().equals("DVRIGHTS")) {
					dataProvider.add(amdSec
							.getChild("rightsMD", C.METS_NS)
							.getChild("mdWrap", C.METS_NS)
							.getChild("xmlData", C.METS_NS)
							.getChild("rights", C.DV)
							.getChild("owner", C.DV)
							.getValue());
				}
			}
		} catch (Exception e) {
			logger.debug("No amd section found!");
		}
		return dataProvider;
	}
	
	private String getPublisher(Element origInfo) {
		String publisher = "";
		try {
			String type = origInfo.getChild("place", C.MODS_NS).getChild("placeTerm", C.MODS_NS).getAttributeValue("type");
			if(type.equals("text")) {
				publisher = origInfo.getChild("place", C.MODS_NS).getChild("placeTerm", C.MODS_NS).getValue();
			}
			if(origInfo.getChild("publisher", C.MODS_NS)!=null) {
				publisher = origInfo.getChild("publisher", C.MODS_NS).getValue()+" ("+publisher+")";
			}
		} catch (Exception e) {
			logger.debug("Element placeTerm does not exist!");
		}
		return publisher;
	}
	
	@SuppressWarnings("unchecked")
	private List<Element> getOrigInfoElements(Element dmdSec) {
		List<Element> origInfoElements = new ArrayList<Element>();
		try {
			origInfoElements = getModsXmlData(dmdSec).getChildren("originInfo", C.MODS_NS);
		} catch (Exception e) {
			logger.debug("No origInfo element found!");
		}
		return origInfoElements;
	}
	
	private String getDate(Element originInfo) {
		String date = "";
			try {
				date = originInfo.getChild("dateIssued", C.MODS_NS).getValue(); 
			} catch (Exception e) {
				logger.debug("Element dateIssued does not exist!");
			}
		return date;
	}
	
	@SuppressWarnings("unchecked")
	private List<Element> getNameElements(Element dmdSec) {
		List<Element> nameElements = new ArrayList<Element>();
		Element modsXmlData = getModsXmlData(dmdSec);
		try {
			nameElements = modsXmlData.getChildren("name", C.MODS_NS);
		} catch (Exception e) {
			logger.debug("No name element found!");
		}
		return nameElements;
	}
	
	private String getCreator(Element name) {
		String namePartValue = "";
		try {
			String role = name.getChild("role", C.MODS_NS).getValue();
			if(role.equals("aut")||role.equals("creator")) {
				namePartValue = getName(name);
			}
		} catch (Exception e) {
			logger.debug("No creator found!");
		}
		return namePartValue;
	}
	
	private String getContributor(Element name) {
		String namePartValue = "";
		try {
			String role = name.getChild("role", C.MODS_NS).getValue();
			if(!(role.equals("aut") && !role.equals("creator"))) {
				namePartValue = role+": "+getName(name);
			}
		} catch (Exception e) {
			logger.debug("No contributor found!");
		}
		return namePartValue;
	}
	
	private String getName(Element name) {
		String namePartValue = "";
		if(name.getAttribute("type", C.MODS_NS)==null || name.getAttribute("type", C.MODS_NS).equals("personal")) {
			try {
				@SuppressWarnings("unchecked")
				List<Element> nameParts = name.getChildren("namePart", C.MODS_NS);
				
				String given = "";
				String family = "";
		
				for(Element element :  nameParts) {
					if(element.getAttributes()==null) {
						namePartValue = element.getValue();
					} else {
						if(element.getAttribute("given", C.MODS_NS)!=null) {
							given = element.getAttributeValue("given", C.MODS_NS);
						} 
						if(element.getAttribute("family", C.MODS_NS)!=null) {
							family = element.getAttributeValue("family", C.MODS_NS);
						}
						
						if(given.equals("")&&family.equals("")) {
							namePartValue = element.getValue();
						} else if(!given.equals("")) {
							namePartValue = given + " " + family;	
						} else namePartValue = family;
					}
				}
			} catch (Exception e) {
				logger.debug("Element namePart does not exist!");
			}
			
			if(namePartValue.isEmpty()) {
				try {
					namePartValue = name.getChild("displayForm", C.MODS_NS).getValue();
				} catch (Exception e) {
					logger.error("No name found");
				} 
			}
		}
		return namePartValue;
	}
	
	private List<String> getTitle(Element dmdSec) {
		List<String> title = new ArrayList<String>();
		Element modsXmlData = getModsXmlData(dmdSec);
		
		String titleValue = "";
		String displayLabelValue = "";
		String nonSortValue = "";
		String subTitleValue = "";
		String MainTitleValue = "";
		
		try {
			Element titleInfo = modsXmlData.getChild("titleInfo", C.MODS_NS);
			
			try {
				nonSortValue = titleInfo.getChild("nonSort", C.MODS_NS).getText();
			} catch (Exception e) {
				logger.debug("Element nonSort does not exist!");
			}
			
			try {
				titleValue = titleInfo.getChild("title", C.MODS_NS).getText();
			} catch (Exception e) {
				logger.debug("Element title does not exist!");
			}
			
			try {
				displayLabelValue = titleInfo.getChild("displayLabel", C.MODS_NS).getText();
			} catch (Exception e) {
				logger.debug("Element displayLabel does not exist!");
			}
			
			try {
				subTitleValue = titleInfo.getChild("subTitle", C.MODS_NS).getText();
			} catch (Exception e) {
				logger.debug("Element subTitle does not exist!");
			}
			
			if(!titleValue.equals("")) {
				if(!nonSortValue.equals("")) {
					titleValue = nonSortValue + " " + titleValue;
				}
				MainTitleValue = titleValue;
			} else {
				MainTitleValue = displayLabelValue;
			}
		} catch(Exception e) {
			logger.error("Element titleInfo does not exist!!!");
		}
		title.add(MainTitleValue);
		if(!subTitleValue.equals("")) {
			title.add(subTitleValue);
		}
		return title;
	}
	
	private HashMap<String, Element> getSections(String objectId) {
		HashMap<String, Element> IDtoSecElement = new HashMap<String, Element>();
		@SuppressWarnings("unchecked")
		List<Element> dmdSections = metsDoc.getRootElement().getChildren("dmdSec", C.METS_NS);
		for(Element e : dmdSections) {
			String id = "";
			id = objectId+"-"+e.getAttribute("ID").getValue();
			IDtoSecElement.put(id, e);
		}
		return IDtoSecElement;
	}
	
	public Element getModsXmlData(Element dmdSec) {
		return dmdSec
				.getChild("mdWrap", C.METS_NS)
				.getChild("xmlData", C.METS_NS)
				.getChild("mods", C.MODS_NS);
	}
	
	public File getMetadataFile() {
		return metsFile;
	}
	
	public String getHref(Element fileElement) {
		return fileElement.getChild("FLocat", C.METS_NS).getAttribute("href", XLINK_NS).getValue();
	}
	
	public List<Element> getMetsFileElements() {
		return fileElements;
	} 
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private List<Element> getFileElementsFromMetsDoc(Document doc) throws JDOMException {
		List currentFileElements = new ArrayList<Element>();
		List allNodes = metsXPath.selectNodes(doc);
		for (java.lang.Object node : allNodes) {
			Element fileElement = (Element) node;
			currentFileElements.add(fileElement);
		}
		return currentFileElements;
	} 
	
	private List<String> getReferences() {
		List<String> references = new ArrayList<String>();
		for(Element fileElement : fileElements) {
			references.add(getHref(fileElement));
		}
		return references;
	}
	
	private String getReferenceFromFileId(String fileId) {
		String ref = "";
		try {
			for(Element fileElement : fileElements) {
				if(fileElement.getAttributeValue("ID").equals(fileId)) {
					ref = getHref(fileElement);
				}
			}
		} catch (Exception e) {
			logger.error("Unable to find the reference from file "+fileId);
		}
		return ref;
	}
	
//	::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::  SETTER  ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
	
	private void setMimetype(Element fileElement, String mimetype) {
		if(fileElement.getAttribute("MIMETYPE")!=null) {
			fileElement.getAttribute("MIMETYPE").setValue(mimetype);
		} else {
			fileElement.setAttribute("MIMETYPE", mimetype);
		}
	}
	
	private void setLoctype(Element fileElement, String loctype) {
		if(loctype!=null) {
			if(fileElement.getChild("FLocat", C.METS_NS).getAttribute("LOCTYPE")!=null) {
				fileElement.getChild("FLocat", C.METS_NS).getAttribute("LOCTYPE").setValue(loctype);
			} else {
				fileElement.getChild("FLocat", C.METS_NS).setAttribute("LOCTYPE", loctype);
			}
		}
	}
	
	private void setHref(Element fileElement, String newHref) {
		fileElement.getChild("FLocat", C.METS_NS).getAttribute("href", XLINK_NS).setValue(newHref);
	}
	
//	:::::::::::::::::::::::::::::::::::::::::::::::::::::::::  REPLACEMENTS  :::::::::::::::::::::::::::::::::::::::::::::::::::::::::

	public void makeReplacementsInMetsFile(File metsFile, String currentHref, String targetHref, String mimetype, String loctype) throws IOException, JDOMException {
		File targetMetsFile = metsFile;
		SAXBuilder builder = XMLUtils.createNonvalidatingSaxBuilder();	
		logger.debug(":::"+workPath+":::"+targetMetsFile.getPath());
		FileInputStream fileInputStream = new FileInputStream(Path.makeFile(workPath,targetMetsFile.getPath()));
		BOMInputStream bomInputStream = new BOMInputStream(fileInputStream);
		Reader reader = new InputStreamReader(bomInputStream,"UTF-8");
		InputSource is = new InputSource(reader);
		is.setEncoding("UTF-8");
		Document metsDoc = builder.build(is);
		
		List<Element> metsFileElements = getFileElementsFromMetsDoc(metsDoc);
		
		for (int i=0; i<metsFileElements.size(); i++) { 
			Element fileElement = (Element) metsFileElements.get(i);
			if(getHref(fileElement).equals(currentHref)) {
				setHref(fileElement, targetHref);
				setMimetype(fileElement, mimetype);
				setLoctype(fileElement, loctype);
			}
		}
		XMLOutputter outputter = new XMLOutputter();
		outputter.setFormat(Format.getPrettyFormat());
		outputter.output(metsDoc, new FileWriter(Path.makeFile(workPath,targetMetsFile.getPath())));

		fileInputStream.close();
		bomInputStream.close();
		reader.close();
	}

//	:::::::::::::::::::::::::::::::::::::::::::::::::::::::::   VALIDATION   :::::::::::::::::::::::::::::::::::::::::::::::::::::::::

	private boolean checkReferencedFiles() {
		Boolean valid = true;
		if(getReferences().size()!=getReferencedFiles(metsFile, getReferences(), currentDocuments).size()) {
			valid = false;
		}
		return valid;
	}
	
	public boolean uniqueRootDivElementExists() {
		if(getUniqueRootElementInLogicalStructMap()!=null) {
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public boolean isValid() {
		return checkReferencedFiles();
	}
}
