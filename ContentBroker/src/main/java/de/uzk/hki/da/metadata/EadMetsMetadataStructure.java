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

import de.uzk.hki.da.model.DAFile;
import de.uzk.hki.da.utils.Path;
import de.uzk.hki.da.utils.XMLUtils;


public class EadMetsMetadataStructure extends MetadataStructure{
	
	private String EAD_XPATH_EXPRESSION = 		"//daoloc/@href";
	private String METS_XPATH_EXPRESSION= 		"//mets:file";
	private XPath metsXPath = 					XPath.newInstance(METS_XPATH_EXPRESSION);
	private static final Namespace METS_NS = 	Namespace.getNamespace("http://www.loc.gov/METS/");
	private static final Namespace XLINK_NS = 	Namespace.getNamespace("http://www.w3.org/1999/xlink");
	
	private final File 	eadFile;
	private File 		packageFile;
	
	private List<String> metsReferencesInEAD;
	private List<File> metsFiles;
	private Element metsFileElement;
	
	@SuppressWarnings("rawtypes")
	private List allFileNodesInMets;
	
	private HashMap<File, HashMap<String, String>> metsInfo = new HashMap<File, HashMap<String,String>>();
	HashMap<String, Document> metsPathToDocument = new HashMap<String, Document>();
	
	public EadMetsMetadataStructure(File metadataFile, List<DAFile> daFiles) throws JDOMException, 
		IOException, ParserConfigurationException, SAXException {
		super(metadataFile, daFiles);
		
		eadFile = metadataFile;
		packageFile = eadFile.getParentFile();
		
		metsReferencesInEAD = getMetsRefsInEad();
		metsFiles = getMetsFiles();
		
		setUpMetsInfo(metsReferencesInEAD, metsFiles);
		
		parseMetsFiles(metsFiles);
//		printMetsContent();
	}
	
//	::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::  SETUP  ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
	
	private void setUpMetsInfo(List<String> metsReferences, List<File> metsFiles) throws JDOMException, IOException, ParserConfigurationException, SAXException {
		
		for(int i=0; i<metsReferences.size(); i++) {
			for(int j=0; j<metsFiles.size(); j++) {
				if(metsFiles.get(j).getAbsolutePath().contains(metsReferences.get(i))) {
					File currentMetsFile = metsFiles.get(i);
					metsInfo.put(currentMetsFile, new HashMap<String, String>());
					metsInfo.get(currentMetsFile).put("relativePath", metsReferences.get(i));
				}
			}
		}		
	}
	
//	::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::  PARSER  ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
	
//	METS
	private void parseMetsFiles(List<File> metsFiles) throws ParserConfigurationException, SAXException, IOException, JDOMException {
		for(int i=0; i<metsFiles.size(); i++) {
			parseMetsFile(metsFiles.get(i));
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
			isValid = false;
			logger.error("METS file didn't refer to any file.");
		} else if(allFileNodesInMets.size()>1) {
			isValid = false;
			logger.error("METS file refers to more than one file.");
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
	
	private List<String> getMetsRefsInEad() throws JDOMException, IOException {
		
		List<String> metsReferences = new ArrayList<String>();
		
		SAXBuilder builder = XMLUtils.createNonvalidatingSaxBuilder();
		FileInputStream fileInputStream = new FileInputStream(eadFile);
		BOMInputStream bomInputStream = new BOMInputStream(fileInputStream);
		Document eadDoc = builder.build(bomInputStream);
	
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
	
	public List<File> getMetsFiles() {
		
		System.out.println("get mets files");
		
		List<File> metsFiles = new ArrayList<File>();
		packageFile = eadFile.getParentFile();
		System.out.println("packageFile: "+packageFile.getAbsolutePath());
		
		for(int i=0; i<metsReferencesInEAD.size(); i++) {
			String href = metsReferencesInEAD.get(i);
			System.out.println("mets reference: "+href);
			File metsFile = Path.make(packageFile.getAbsolutePath(), href).toFile();
			metsFiles.add(metsFile);
		}
		
		if(metsReferencesInEAD.size()>metsFiles.size()) {
			isValid = false;
			logger.error("EAD file refers to "+metsReferencesInEAD.size()+" METS files, but there are only "+metsFiles.size()+" METS files.");
		}
		
		return metsFiles;
	}
	
	public HashMap<File, HashMap<String, String>> getContentOfMetsFiles() {
		return metsInfo;
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
	
	public HashMap<File, HashMap<String, String>> getMetsInfo() {
		return metsInfo;
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
	
	public void makeReplacementsInMetsFile(File metsFile, String currentHref, String targetHref, String mimetype, String loctype) throws IOException, JDOMException {
		
		File targetMetsFile = metsFile;
		Document currentMetsDocument = metsPathToDocument.get(targetMetsFile.getAbsolutePath());
		
		allFileNodesInMets = metsXPath.selectNodes(currentMetsDocument);
		metsFileElement = (Element) allFileNodesInMets.get(0);
		
		Element FLocat = metsFileElement.getChild("FLocat", METS_NS);
	
		setMimetype(metsFileElement, mimetype);
		
		if(loctype!=null) {
			setLoctype(FLocat, loctype);
		}
		
		setHref(FLocat, currentHref, targetHref);
		
		XMLOutputter outputter = new XMLOutputter();
		outputter.setFormat(Format.getPrettyFormat());
		outputter.output(currentMetsDocument, new FileWriter(targetMetsFile));
	}
	
	
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
					System.out.println("setValue "+eadReplacements.get(replacement));
					attr.setValue(eadReplacements.get(replacement));
				}
			}
		}
		
		XMLOutputter outputter = new XMLOutputter();
		outputter.setFormat(Format.getPrettyFormat());
		outputter.output(currentEadDoc, new FileWriter(targetEadFile));
	}
	
	
//	::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::  PRINTS  ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
	
	public void printMetsContent() {
		for(File metsFile : metsInfo.keySet()) {
			for(String metsRef : metsInfo.get(metsFile).keySet()) {
				System.out.println("metsInfo: "+metsRef+": "+metsInfo.get(metsFile).get(metsRef));
			}
		}
	}

	@Override
	public boolean isValid() {
		return isValid;
	}
}
