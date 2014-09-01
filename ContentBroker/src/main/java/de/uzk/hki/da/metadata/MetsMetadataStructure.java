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

import de.uzk.hki.da.utils.XMLUtils;

public class MetsMetadataStructure extends MetadataStructure {

	private File metsFile;
	private Document doc;
	private String METS_XPATH_EXPRESSION= 		"//mets:file";
	private XPath metsXPath = 					XPath.newInstance(METS_XPATH_EXPRESSION);
	private static final Namespace METS_NS = 	Namespace.getNamespace("http://www.loc.gov/METS/");
	private static final Namespace XLINK_NS = 	Namespace.getNamespace("http://www.w3.org/1999/xlink");

	@SuppressWarnings("rawtypes")
	List fileElements = new ArrayList<Element>();
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public MetsMetadataStructure(File metadataFile)
			throws FileNotFoundException, JDOMException, IOException {
		super(metadataFile);
		
		metsFile = metadataFile;
		
		SAXBuilder builder = XMLUtils.createNonvalidatingSaxBuilder();
		FileInputStream fileInputStream = new FileInputStream(metsFile);
		BOMInputStream bomInputStream = new BOMInputStream(fileInputStream);
		Document metsDoc = builder.build(bomInputStream);
		
		List allNodes = metsXPath.selectNodes(metsDoc);
		
		for (java.lang.Object node : allNodes) {
			Element fileElement = (Element) node;
			fileElements.add(fileElement);
		}
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
	
	public List<Element> getMetsFileElements() {
		return fileElements;
	} 
	
//	::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::  SETTER  ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
	
	public void setMimetype(Element fileElement, String mimetype) {
		fileElement.getAttribute("MIMETYPE").setValue(mimetype);
	}
	
	public void setLoctype(Element fileElement, String loctype) {
		fileElement.getChild("FLocat", METS_NS).getAttribute("LOCTYPE").setValue(loctype);
	}
	
	public void setHref(Element fileElement, String newHref) {
		fileElement.getChild("FLocat", METS_NS).getAttribute("href", XLINK_NS).setValue(newHref);
	}
	
//	:::::::::::::::::::::::::::::::::::::::::::::::::::::::::  REPLACEMENTS  :::::::::::::::::::::::::::::::::::::::::::::::::::::::::
	
	public void makeReplacementsForMetsFileElement(File metsFile, String currentHref, String targetHref, String mimetype, String loctype) throws IOException, JDOMException {

		SAXBuilder builder = XMLUtils.createNonvalidatingSaxBuilder();
		FileInputStream fileInputStream = new FileInputStream(metsFile);
		BOMInputStream bomInputStream = new BOMInputStream(fileInputStream);
		Document metsDoc = builder.build(bomInputStream);
		
		for (int i=0; i<fileElements.size(); i++) { 
			Element fileElement = (Element) fileElements.get(i);
			if(getHref(fileElement).equals(currentHref)) {
				setHref(fileElement, targetHref);
				setMimetype(fileElement, mimetype);
			}
		}
		
		XMLOutputter outputter = new XMLOutputter();
		outputter.setFormat(Format.getPrettyFormat());
		outputter.output(metsDoc, new FileWriter(metsFile));
	}

	@Override
	public boolean isValid() {
		return true;
	}

}
