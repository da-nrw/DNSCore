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
import org.jdom.Attribute;
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
		
		printIndexInfo();
	}
	
//	::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::  GETTER  ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
	
	@Override
	protected HashMap<String, HashMap<String, String>> getIndexInfo() {
		HashMap<String, HashMap<String, String>> indexInfo = new HashMap<String, HashMap<String,String>>();
		HashMap<String, Element> dmdSections = getSections();
		
		for(String id : dmdSections.keySet()) {
			Element e = dmdSections.get(id);
			HashMap<String, String> dmdSecInfo = new HashMap<String, String>();
			
			dmdSecInfo.put("Title", getTitle(e));
			dmdSecInfo.put("Author", getAuthor(e));
			dmdSecInfo.put("Date", getDate(e));
			dmdSecInfo.put("Place", getPlace(e));
			
			indexInfo.put(id, dmdSecInfo);
		}
		return indexInfo;
	}
	
	private String getPlace(Element dmdSec) {
		Element modsXmlData = getModsXmlData(dmdSec);
		String place = "";
		try {
			place = modsXmlData.getChild("originInfo", C.MODS_NS).getChild("place", C.MODS_NS).getChild("placeTerm", C.MODS_NS).getValue();
		} catch (Exception e) {
			logger.debug("Element placeTerm does not exist!");
		}
		return place;
	}
	
	private String getDate(Element dmdSec) {
		Element modsXmlData = getModsXmlData(dmdSec);
		String date = "";
		try {
			Element name = modsXmlData.getChild("originInfo", C.MODS_NS);
			try {
				date = name.getChild("dateIssued", C.MODS_NS).getValue(); 
			} catch (Exception e) {
				logger.debug("Element dateIssued does not exist!");
			}
			try {
				date = name.getChild("dateCreated", C.MODS_NS).getValue();
			} catch (Exception e) {
				logger.debug("Element dateCreated does not exist!");
			}
		} catch (Exception e) {
			logger.error("Element originInfo does not exist!");
		}
		return date;
	}
	
	private String getAuthor(Element dmdSec) {
		Element modsXmlData = getModsXmlData(dmdSec);
		String namePartValue = "";
		
		try {
			Element name = modsXmlData.getChild("name", C.MODS_NS);
			
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
		} catch (Exception e) {
			logger.error("Element name does not exist!");
		}
		return namePartValue;
	}
	
	private String getTitle(Element dmdSec) {
		Element modsXmlData = getModsXmlData(dmdSec);
		
		String titleValue = "";
		String displayLabelValue = "";
		String nonSortValue = "";
		String subTitleValue = "";
		String returnTitleValue = "";
		
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
				returnTitleValue = titleValue;
			} else {
				returnTitleValue = displayLabelValue;
			}
		} catch(Exception e) {
			logger.error("Element titleInfo does not exist!!!");
		}
		return returnTitleValue;
	}
	
	private HashMap<String, Element> getSections() {
		HashMap<String, Element> IDtoSecElement = new HashMap<String, Element>();
		@SuppressWarnings("unchecked")
		List<Element> dmdSections = metsDoc.getRootElement().getChildren("dmdSec", C.METS_NS);
		if(dmdSections.size()==1 && dmdSections.get(0).getAttribute("ID", C.METS_NS)==null) {
			IDtoSecElement.put("", dmdSections.get(0));
		} else {
			for(Element e : dmdSections) {
				IDtoSecElement.put(e.getAttribute("ID", C.METS_NS).getValue(), e);
			}
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
