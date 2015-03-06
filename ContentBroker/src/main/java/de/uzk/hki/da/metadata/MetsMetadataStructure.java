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
import org.xml.sax.InputSource;

import de.uzk.hki.da.core.C;

/**
 * @author Polina Gubaidullina
 */

public class MetsMetadataStructure extends MetadataStructure {

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
	
	public MetsMetadataStructure(File metadataFile, List<de.uzk.hki.da.model.Document> documents)
			throws FileNotFoundException, JDOMException, IOException {
		super(metadataFile, documents);
		
		metsFile = metadataFile;
		currentDocuments = documents;
		
		SAXBuilder builder = XMLUtils.createNonvalidatingSaxBuilder();		
		FileInputStream fileInputStream = new FileInputStream(metsFile);
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
				if(creator.equals("")) {
					creators.add(creator);
				}
				if(contributor.equals("")) {
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
			String titlePageId = getTitlePageReferenceFromDmdId(id, ObjectId);
			if(!titlePageId.equals("")) {
				List<String> references = new ArrayList<String>();
				references.add(titlePageId);
				dmdSecInfo.put(C.EDM_IS_SHOWN_BY, references);
				dmdSecInfo.put(C.EDM_OBJECT, references);
			}
			
//			dataProvider
			dmdSecInfo.put(C.EDM_DATA_PROVIDER, getDataProvider());
			
			getReferencesFromDmdId(id, ObjectId);
			
			indexInfo.put(id, dmdSecInfo);
		}
		return indexInfo;
	}
	
	private String getTitlePageReferenceFromDmdId(String dmdID, String objectId) {
		String titlePageLogicalId = getIdAndParentDmdIdFromLogicalStructMap(dmdID.replace(objectId+"-", ""), TITLE_PAGE, "ID")[0];
		String titlePagePhysicalId = getPhysicalIdFromStructLink(metsDoc, titlePageLogicalId);
		String titlePageFileId = getFileIdFromPhysicalId(titlePagePhysicalId);
		String ref = getReferenceFromFileId(titlePageFileId);
		return ref;
	}
	
	private List<String> getReferencesFromDmdId(String dmdID, String objectId) {
		List<String> references = new ArrayList<String>();
		String logicalId = getIdAndParentDmdIdFromLogicalStructMap(dmdID.replace(objectId+"-", ""), "", "ID")[0];
		System.out.println("LOGICAL_ID: "+logicalId+" from DMDID "+dmdID);
		
		System.out.println("YAY "+getIdAndParentDmdIdFromLogicalStructMap(dmdID.replace(objectId+"-", ""), "", "ID")[0]);
		System.out.println("YAY "+getIdAndParentDmdIdFromLogicalStructMap(dmdID.replace(objectId+"-", ""), "", "ID")[1]);
		
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
	
	@SuppressWarnings({ "unchecked" })
	private String[] getIdAndParentDmdIdFromLogicalStructMap(String dmdID, String type, String idType) {
		String id = "";
		String parentId = "";
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
									parentId = d.getAttributeValue("DMDID");
								}
							}	
						}
					}
				}
			}
		} catch (Exception e) {
			logger.debug("Unable to find the "+idType+" id from dmdID "+dmdID);
		}
		String[] idAndParentDmdId = {id, parentId};
		return idAndParentDmdId;
	}
	
	@SuppressWarnings("unchecked")
	private String getPhysicalIdFromStructLink(Document doc, String logicalId) {
		String physId = "";
		List<Element> structLink = new ArrayList<Element>();
		try {
			structLink = doc.getRootElement().getChild("structLink", C.METS_NS).getChildren("smLink", C.METS_NS);
			for(Element link : structLink) {
				if(link.getAttributeValue("from", XLINK_NS).equals(logicalId)) {
					physId = link.getAttributeValue("to", XLINK_NS);
				}
			}
		} catch (Exception e) {
			logger.debug("Unable to find the physical id from logical id "+logicalId);
		}
		return physId;
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
			if(!(role.equals("aut")||role.equals("creator"))) {
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
		FileInputStream fileInputStream = new FileInputStream(targetMetsFile);
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
		outputter.output(metsDoc, new FileWriter(targetMetsFile));

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
	
	@Override
	public boolean isValid() {
		return checkReferencedFiles();
	}
}
