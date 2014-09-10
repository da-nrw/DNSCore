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
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;
import org.xml.sax.SAXException;

import de.uzk.hki.da.model.DAFile;
import de.uzk.hki.da.path.Path;
import de.uzk.hki.da.utils.XMLUtils;


public class EadMetsMetadataStructure extends MetadataStructure{
	
	private String EAD_XPATH_EXPRESSION = 		"//daoloc/@href";
	
	private final File eadFile;
	private File packageFile;
	private List<String> metsReferencesInEAD;
	private List<File> metsFiles;
	private List<MetsMetadataStructure> mmsList;
	
	HashMap<String, Document> metsPathToDocument = new HashMap<String, Document>();
	
	public EadMetsMetadataStructure(File metadataFile, List<DAFile> daFiles) throws JDOMException, 
		IOException, ParserConfigurationException, SAXException {
		super(metadataFile, daFiles);
		
		eadFile = metadataFile;
		packageFile = eadFile.getParentFile();
		
		metsReferencesInEAD = getMetsRefsInEad();
		metsFiles = getMetsFiles();
				
		mmsList = new ArrayList<MetsMetadataStructure>();
		for(File metsFile : metsFiles) {
			MetsMetadataStructure mms = new MetsMetadataStructure(metsFile, daFiles);
			mmsList.add(mms);
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
		
		List<File> metsFiles = new ArrayList<File>();
		packageFile = eadFile.getParentFile();
		
		for(int i=0; i<metsReferencesInEAD.size(); i++) {
			String href = metsReferencesInEAD.get(i);
			File metsFile = Path.make(packageFile.getAbsolutePath(), href).toFile();
			if(metsFile.exists()) {
				metsFiles.add(metsFile);
			}
		}
		
		if(metsReferencesInEAD.size()>metsFiles.size()) {
			isValid = false;
			logger.error("EAD file refers to "+metsReferencesInEAD.size()+" METS files, but there are only "+metsFiles.size()+" METS files.");
		}
		
		return metsFiles;
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
	
	public List<MetsMetadataStructure> getMetsMetadataStructures() {
		return mmsList;
	}
	
	
//	::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::  PRINTS  ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::

	@Override
	public boolean isValid() {
		return isValid;
	}
}
