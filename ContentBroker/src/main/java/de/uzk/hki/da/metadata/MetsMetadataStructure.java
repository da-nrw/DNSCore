package de.uzk.hki.da.metadata;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
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

import de.uzk.hki.da.model.DAFile;
import de.uzk.hki.da.utils.XMLUtils;

public class MetsMetadataStructure extends MetadataStructure {

	private File metsFile;
	private String METS_XPATH_EXPRESSION= 		"//mets:file";
	private XPath metsXPath = 					XPath.newInstance(METS_XPATH_EXPRESSION);
	private static final Namespace METS_NS = 	Namespace.getNamespace("http://www.loc.gov/METS/");
	private static final Namespace XLINK_NS = 	Namespace.getNamespace("http://www.w3.org/1999/xlink");

	@SuppressWarnings("rawtypes")
	List fileElements;
	
	public MetsMetadataStructure(File metadataFile, List<DAFile> daFiles)
			throws FileNotFoundException, JDOMException, IOException {
		super(metadataFile, daFiles);
		
		metsFile = metadataFile;
		
		SAXBuilder builder = XMLUtils.createNonvalidatingSaxBuilder();
		FileInputStream fileInputStream = new FileInputStream(metsFile);
		BOMInputStream bomInputStream = new BOMInputStream(fileInputStream);
		Document metsDoc = builder.build(bomInputStream);
		fileElements = getFileElementsFromMetsDoc(metsDoc);
	}
	
//	::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::  GETTER  ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
	
	public String getMimetype(Element fileElement) {
		return fileElement.getAttribute("MIMETYPE").getValue();
	}
	
	public String getLoctype(Element fileElement) {
		return fileElement.getChild("FLocat", METS_NS).getAttribute("LOCTYPE").getValue();
	}
	
	public String getHref(Element fileElement) {
		return fileElement.getChild("FLocat", METS_NS).getAttribute("href", XLINK_NS).getValue();
	}
	
	@SuppressWarnings("unchecked")
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
	
//	::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::  SETTER  ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
	
	public void setMimetype(Element fileElement, String mimetype) {
		if(fileElement.getAttribute("MIMETYPE")!=null) {
			fileElement.getAttribute("MIMETYPE").setValue(mimetype);
		} else {
			fileElement.setAttribute("MIMETYPE", mimetype);
		}
	}
	
	public void setLoctype(Element fileElement, String loctype) {
		if(loctype!=null) {
			if(fileElement.getChild("FLocat", METS_NS).getAttribute("LOCTYPE")!=null) {
				fileElement.getChild("FLocat", METS_NS).getAttribute("LOCTYPE").setValue(loctype);
			} else {
				fileElement.getChild("FLocat", METS_NS).setAttribute("LOCTYPE", loctype);
			}
		}
	}
	
	public void setHref(Element fileElement, String newHref) {
		fileElement.getChild("FLocat", METS_NS).getAttribute("href", XLINK_NS).setValue(newHref);
	}
	
//	:::::::::::::::::::::::::::::::::::::::::::::::::::::::::  REPLACEMENTS  :::::::::::::::::::::::::::::::::::::::::::::::::::::::::

	public void makeReplacementsInMetsFileElement(File metsFile, String currentHref, String targetHref, String mimetype, String loctype) throws IOException, JDOMException {

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

	@Override
	public boolean isValid() {
		return true;
	}

}
