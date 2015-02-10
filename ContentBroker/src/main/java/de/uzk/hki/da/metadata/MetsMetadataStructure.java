package de.uzk.hki.da.metadata;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
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
		metsDoc = builder.build(bomInputStream);
		fileElements = getFileElementsFromMetsDoc(metsDoc);
		fileInputStream.close();
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
			List<String> places = new ArrayList<String>();
			for(Element origInfo : getOrigInfoElements(e)) {
				String date = getDate(origInfo);
				String place = getPublisherPlace(origInfo);
				if(!date.equals("")) {
					dates.add(date);
				}
				if(!place.equals("")) {
					places.add(place);
				}
			}
			dmdSecInfo.put(C.EDM_DATE, dates);
			dmdSecInfo.put(C.EDM_PUBLISHER, places);
			
//			Place
			indexInfo.put(id, dmdSecInfo);
			
//			dataProvider
			dmdSecInfo.put(C.EDM_DATA_PROVIDER, getDataProvider());
		}
		return indexInfo;
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
	
	private String getPublisherPlace(Element origInfo) {
		String place = "";
		try {
			String type = origInfo.getChild("place", C.MODS_NS).getChild("placeTerm", C.MODS_NS).getAttributeValue("type");
			if(type.equals("text")) {
				place = origInfo.getChild("place", C.MODS_NS).getChild("placeTerm", C.MODS_NS).getValue();
			}
		} catch (Exception e) {
			logger.debug("Element placeTerm does not exist!");
		}
		return place;
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
			id = e.getAttribute("ID").getValue();
			if(id.equals("")) {
				
			}
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
		Document metsDoc = builder.build(bomInputStream);
		
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
