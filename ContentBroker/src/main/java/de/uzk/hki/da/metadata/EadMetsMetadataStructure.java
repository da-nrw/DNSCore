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
import de.uzk.hki.da.utils.XMLUtils;

/**
 * @author Polina Gubaidullina
 */

public class EadMetsMetadataStructure extends MetadataStructure{
	
	private String EAD_XPATH_EXPRESSION = 		"//daoloc/@href";
	
	private final File eadFile;
	private List<String> metsReferencesInEAD;
	private List<DAFile> metsFiles;
	private List<MetsMetadataStructure> mmsList;
	
	HashMap<String, Document> metsPathToDocument = new HashMap<String, Document>();
	
	public EadMetsMetadataStructure(File metadataFile, List<DAFile> daFiles) throws JDOMException, 
		IOException, ParserConfigurationException, SAXException {
		super(metadataFile, daFiles);
	
		eadFile = metadataFile;
		metsReferencesInEAD = extractMetsRefsInEad();
		metsFiles = getMetsFiles(daFiles);
				
		mmsList = new ArrayList<MetsMetadataStructure>();
		for(DAFile metsFile : metsFiles) {
			MetsMetadataStructure mms = new MetsMetadataStructure(metsFile.toRegularFile(), daFiles);
			mmsList.add(mms);
		}
	}
	
//	::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::  GETTER  ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
	
	public File getMetadataFile() {
		return eadFile;
	}
	
	public List<String> getMetsRefsInEad() {
		return metsReferencesInEAD;
	}
	
	private List<String> extractMetsRefsInEad() throws JDOMException, IOException {
		
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

	private List<DAFile> getMetsFiles(List<DAFile> daFiles) {
		List<DAFile> existingMetsFiles = new ArrayList<DAFile>();
		for(String ref : metsReferencesInEAD) {
			File refFile;
			try {
				refFile = getCanonicalFileFromReference(ref, eadFile);
				logger.debug("Check referenced file: "+refFile.getAbsolutePath());
				Boolean fileExists = false;
				for(DAFile dafile : daFiles) {
					File file = dafile.toRegularFile();
					String dafilePath = file.getAbsolutePath();
					logger.debug("DAFile: "+dafilePath);
					if(refFile.getAbsolutePath().contains(dafilePath)) {
						fileExists = true;
						existingMetsFiles.add(dafile);
						break;
					} else {
						fileExists = false;
					}
				}
				if(fileExists) {
					logger.debug("File "+ref+" exists.");
				} else {
					logger.error("File "+ref+" does not exist.");
				}
			} catch (IOException e) {
				logger.error("File "+ref+" does not exist.");
				e.printStackTrace();
			}
		}
		return existingMetsFiles;
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
	}
	
//	:::::::::::::::::::::::::::::::::::::::::::::::::::::::::   VALIDATION   :::::::::::::::::::::::::::::::::::::::::::::::::::::::::

	private boolean checkReferencedFilesInEad() {
		if(metsReferencesInEAD.size()==getMetsMetadataStructures().size()) {
			return true;
		} else {
			logger.error("Expected "+metsReferencesInEAD.size()+" METS files but found "+metsFiles.size()+" METS files.");
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
}
