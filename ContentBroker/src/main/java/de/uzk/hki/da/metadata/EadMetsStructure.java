package de.uzk.hki.da.metadata;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
import org.xml.sax.SAXException;

import de.uzk.hki.da.core.UserException;
import de.uzk.hki.da.core.UserException.UserExceptionId;
import de.uzk.hki.da.utils.Path;
import de.uzk.hki.da.utils.XMLUtils;


public class EadMetsStructure extends MetadataFile{
	
	private String EAD_XPATH_EXPRESSION = 		"//daoloc/@href";
	private String METS_XPATH_EXPRESSION= 		"//mets:file";
	private XPath metsXPath = 					XPath.newInstance(METS_XPATH_EXPRESSION);
	private static final Namespace METS_NS = 	Namespace.getNamespace("http://www.loc.gov/METS/");
	private static final Namespace XLINK_NS = 	Namespace.getNamespace("http://www.w3.org/1999/xlink");
	
	private final File 	currentMetadataFile;
	private File 		packageFile;
	
	private Element metsFileElement;
	
	@SuppressWarnings("rawtypes")
	private List allFileNodesInMets;
	
	HashMap<File, HashMap<String, String>> metsInfo = new HashMap<File, HashMap<String,String>>();
	HashMap<String, Document> metsPathToDocument = new HashMap<String, Document>();
	
	@SuppressWarnings("rawtypes")
	public EadMetsStructure(File metadataFile) throws JDOMException, 
		IOException, ParserConfigurationException, SAXException {
		super(metadataFile);
		
		currentMetadataFile = metadataFile;
		packageFile = currentMetadataFile.getParentFile();
		
		SAXBuilder builder = XMLUtils.createNonvalidatingSaxBuilder();
		FileInputStream fileInputStream = new FileInputStream(currentMetadataFile);
		BOMInputStream bomInputStream = new BOMInputStream(fileInputStream);
		Document eadDoc = builder.build(bomInputStream);
		
		XPath xPath = XPath.newInstance(EAD_XPATH_EXPRESSION);
		
		List allNodes = xPath.selectNodes(eadDoc);
		
		List<String> metsReferences = new ArrayList<String>();
		
		for (Object node : allNodes) {
			Attribute attr = (Attribute) node;
			String href = attr.getValue();
			metsReferences.add(href);
		}
		
		setUpMetsInfo(metsReferences);
//		printMetsContent();
	}
	
//	::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::  SETUP  ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
	
	private void setUpMetsInfo(List<String> metsReferences) throws JDOMException, IOException, ParserConfigurationException, SAXException {
		
		packageFile = currentMetadataFile.getParentFile();
		for(int i=0; i<metsReferences.size(); i++) {
			String href = metsReferences.get(i);
			File metsFile = Path.make(packageFile.getAbsolutePath(), href).toFile();
			metsInfo.put(metsFile, new HashMap<String, String>());
			metsInfo.get(metsFile).put("relativePath", href);
		}
		
		List<File> metsFiles = getMetsFiles();
		parseMetsFiles(metsFiles);		
		
	}
	
	
//	::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::  PARSER  ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
	
//	METS
	private void parseMetsFiles(List<File> metsFiles) throws ParserConfigurationException, SAXException, IOException, JDOMException {
		for(File metsFile : metsInfo.keySet()) {
			parseMetsFile(metsFile);
		}
	}
	
	private void parseMetsFile(final File currentMetsFile) throws ParserConfigurationException, SAXException, IOException, JDOMException {
		
		File metsFile = currentMetsFile;
		
		SAXBuilder builder = XMLUtils.createNonvalidatingSaxBuilder();
		FileInputStream fileInputStream = new FileInputStream(metsFile);
		BOMInputStream bomInputStream = new BOMInputStream(fileInputStream);
		Document metsDoc = builder.build(bomInputStream);
		
		metsPathToDocument.put(metsFile.getAbsolutePath(), metsDoc);
		
		allFileNodesInMets = metsXPath.selectNodes(metsDoc);
		
		if(allFileNodesInMets.size()==0) {
			throw new UserException(UserExceptionId.INVALID_METADATA_FILE, "METS file didn't refer to any file.");
		} else if(allFileNodesInMets.size()>1) {
			throw new UserException(UserExceptionId.INVALID_METADATA_FILE, "METS file refers to more than one file.");
		} else {
			metsFileElement = (Element) allFileNodesInMets.get(0);
			
			String mimetype = metsFileElement.getAttribute("MIMETYPE").getValue();
			metsInfo.get(metsFile).put("MIMETYPE", mimetype);
			
			Element FLocat = metsFileElement.getChild("FLocat", METS_NS);
			String loctype = FLocat.getAttribute("LOCTYPE").getValue();
			metsInfo.get(metsFile).put("LOCTYPE", loctype);
			String href = FLocat.getAttribute("href", XLINK_NS).getValue();
			metsInfo.get(metsFile).put("href", href);
		}
	}
	
//	::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::  GETTER  ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
	
	public HashMap<File, HashMap<String, String>> getContentOfMetsFiles() {
		return metsInfo;
	}
	
	public List<File> getMetsFiles() {
		List<File> metsFiles = new ArrayList<File>();
		for(File metsFile : metsInfo.keySet()) {
			metsFiles.add(metsFile);
		}
		return metsFiles;
	}
	
	public String getMimetype(Element file) {
		return file.getAttribute("MIMETYPE").getValue();
	}
	
	public String getLoctype(Element FLocat) {
		return FLocat.getAttribute("LOCTYPE").getValue();
	}
	
	public String getHref(Element FLocat) {
		return FLocat.getAttribute("href", XLINK_NS).getValue();
	}
	
//	::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::  SETTER  ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
	
	public void setMimetype(Element file, String mimetype) {
		file.getAttribute("MIMETYPE").setValue(mimetype);
	}
	
	public void setLoctype(Element FLocat, String loctype) {
		FLocat.getAttribute("LOCTYPE").setValue(loctype);
	}
	
	public void setHref(Element FLocat, String currentHref, String targetHref) {
		if(FLocat.getAttribute("href", XLINK_NS).getValue().equals(currentHref)) {
			FLocat.getAttribute("href", XLINK_NS).setValue(targetHref);
		}	
	}
	
//	:::::::::::::::::::::::::::::::::::::::::::::::::::::::::  REPLACEMENTS  :::::::::::::::::::::::::::::::::::::::::::::::::::::::::
	
	public void makeReplacementsInMetsFile(File metsFile, String mimetype, String loctype, String currentHref, String targetHref) throws IOException, JDOMException {
		
		File targetMetsFile = metsFile;
		
		Document currentMetsDocument = metsPathToDocument.get(targetMetsFile.getAbsolutePath());
		
		allFileNodesInMets = metsXPath.selectNodes(currentMetsDocument);
		metsFileElement = (Element) allFileNodesInMets.get(0);
		
		Element FLocat = metsFileElement.getChild("FLocat", METS_NS);
	
		setMimetype(metsFileElement, mimetype);
		
		setLoctype(FLocat, loctype);
		
		setHref(FLocat, currentHref, targetHref);
		
		XMLOutputter outputter = new XMLOutputter();
		outputter.setFormat(Format.getPrettyFormat());
		outputter.output(currentMetsDocument, new FileWriter(targetMetsFile));
	}
	
//	::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::  PRINTS  ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
	
	public void printMetsContent() {
		for(File metsFile : metsInfo.keySet()) {
			System.out.println("");
			System.out.println("mets file: "+metsFile.getAbsolutePath());
			for(String metsRef : metsInfo.get(metsFile).keySet()) {
				System.out.println("metsInfo: "+metsRef+": "+metsInfo.get(metsFile).get(metsRef));
			}
		}
	}
}
