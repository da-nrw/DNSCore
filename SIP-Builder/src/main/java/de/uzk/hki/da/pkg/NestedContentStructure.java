package de.uzk.hki.da.pkg;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

import org.apache.commons.io.input.BOMInputStream;
import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.xml.sax.InputSource;

import de.uzk.hki.da.metadata.MetsParser;
import de.uzk.hki.da.utils.C;
import de.uzk.hki.da.utils.XMLUtils;
import de.uzk.hki.da.utils.formatDetectionService;

/**
 * @author Polina Gubaidullina
 */

public class NestedContentStructure {
	
	private Logger logger = Logger.getLogger(NestedContentStructure.class);
	
	public File rootFile;
	public HashMap<File, String> sipCandidatesWithUrns = new HashMap<File, String>();
	
	public NestedContentStructure(File sourceRootFile) throws Exception {
		setRootFile(sourceRootFile);
		searchForSipCandidates(sourceRootFile);
	}
	
	public File getRootFile() {
		return rootFile;
	}

	public void setRootFile(File rootFile) {
		this.rootFile = rootFile;
	}	
	
	public HashMap<File, String> getSipCandidates() {
		return sipCandidatesWithUrns;
	}
	
	/**
	 * Search directories recursively for sip candidates
	 * @throws Exception 
	 */
	public void searchForSipCandidates(File dir) throws Exception {
		logger.info("Searching in folder "+dir+" for SIP candidates ...");
		File currentDir = dir;
		for(File f : currentDir.listFiles()) {
			if(f.isDirectory() && getIncludedDirs(f).isEmpty()) { 
				logger.info("Current folder "+f+" is leaf");
				TreeMap<File, String> metadataFileWithType;
				metadataFileWithType = new formatDetectionService(f).getMetadataFileWithType();
				if(!metadataFileWithType.isEmpty()) {
					File metadataFile = metadataFileWithType.firstKey();
					String metadataType = metadataFileWithType.get(metadataFile);
					logger.debug("Metadata file found: "+metadataFile+"; type : "+metadataType);
					if(metadataType.equals(C.CB_PACKAGETYPE_METS)) {
						List<File> metsFiles = getMetsFileFromDir(f);
						if(metsFiles.size()==1) {
							File metsFile = metsFiles.get(0);
							String urn = getUrn(metsFile);
							String newPackageName = urn.replace(":", "+");
							sipCandidatesWithUrns.put(f, newPackageName);
						}
					}
				}
			} else if(!f.isDirectory()) {
				logger.info("Current folder "+f+" isn't directory. Skipping ...");
			} else {
				logger.info("Current folder "+f+" isn't leaf directory");
				searchForSipCandidates(f);
			}
		}
	}
	
	private List<File> getIncludedDirs(File dir) {
		logger.info("Check if file "+dir+" contains directories");
		List<File> dirs = new ArrayList<File>();
		for(File f : dir.listFiles()) {
			logger.info("file "+f);
			if(f.isDirectory()) {
				logger.debug("is a directory");
				dirs.add(f);
			}
		}
		return dirs;
	}
	
	private List<File> getMetsFileFromDir(File dir) throws IOException {
		List<File> metsFiles = new ArrayList<File>();
		for(File f : dir.listFiles()) {
			formatDetectionService fds = new formatDetectionService(f);
			if(fds.isXml(f) && fds.getMetadataTypeXml(f).equals(C.CB_PACKAGETYPE_METS)) {
				metsFiles.add(f);
			}
		}
		return metsFiles;
	}
	
	private String getUrn(File metsFile) throws IOException, JDOMException {
		String urn = "";
		try {
			Document metsDoc = getDocumentFromFile(metsFile);
			urn = new MetsParser(metsDoc).getUrn();
		} catch (IOException e1) {
			throw new IOException(e1);
		} catch (JDOMException e2) {
			throw new IOException(e2);
		}
		return urn;
	}
	
	private Document getDocumentFromFile(File file) throws IOException, JDOMException {
		SAXBuilder builder = XMLUtils.createNonvalidatingSaxBuilder();		
		FileInputStream fileInputStream = new FileInputStream(file);
		BOMInputStream bomInputStream = new BOMInputStream(fileInputStream);
		Reader reader = new InputStreamReader(bomInputStream,"UTF-8");
		InputSource is = new InputSource(reader);
		is.setEncoding("UTF-8");
		Document metsDoc = builder.build(is);
		fileInputStream.close();
		bomInputStream.close();
		reader.close();
		return metsDoc;
	}
}
