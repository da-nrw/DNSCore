package de.uzk.hki.da.pkg;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.jdom.Document;
import org.jdom.JDOMException;

import de.uzk.hki.da.metadata.MetsParser;
import de.uzk.hki.da.utils.C;
import de.uzk.hki.da.utils.XMLUtils;
import de.uzk.hki.da.utils.formatDetectionService;

/**
 * @author Polina Gubaidullina
 */

public class NestedContentStructure {
	
	public File rootFile;
	public HashMap<File, String> sipCandidatesWithUrns = new HashMap<File, String>();
	
	public NestedContentStructure(File sourceRootFile) throws Exception {
		setRootFile(sourceRootFile);
		try {
			searchForSipCandidates(sourceRootFile);
		} catch (JDOMException e) {
			throw new Exception(e);
		}
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
	 * @throws IOException 
	 * @throws JDOMException 
	 */
	public void searchForSipCandidates(File dir) throws IOException, JDOMException {
		File currentDir = dir;
		for(File f : currentDir.listFiles()) {
			if(getIncludedDirs(f).isEmpty()) {
				List<File> metsFiles = getMetsFileFromDir(f);
				if(metsFiles.size()==1) {
					File metsFile = metsFiles.get(0);
					String urn = getUrn(metsFile);
					String newPackageName = urn.replace(":", "+");
					sipCandidatesWithUrns.put(f, newPackageName);
				}
			} else {
				searchForSipCandidates(f);
			}
		}
	}
	
	private List<File> getIncludedDirs(File dir) {
		List<File> dirs = new ArrayList<File>();
		for(File f : dir.listFiles()) {
			if(f.isDirectory()) {
				dirs.add(f);
			}
		}
		return dirs;
	}
	
	private List<File> getMetsFileFromDir(File dir) throws IOException {
		List<File> metsFiles = new ArrayList<File>();
		for(File f : dir.listFiles()) {
			if(new formatDetectionService(f).isXml() && XMLUtils.identifyMetadataType(f).equals(C.CB_PACKAGETYPE_METS)) {
				metsFiles.add(f);
			}
		}
		return metsFiles;
	}
	
	private String getUrn(File metsFile) throws IOException, JDOMException {
		String urn = "";
		try {
			Document metsDoc = XMLUtils.getDocumentFromXMLFile(metsFile);
			urn = new MetsParser(metsDoc).getUrn();
		} catch (IOException e1) {
			throw new IOException(e1);
		} catch (JDOMException e2) {
			throw new IOException(e2);
		}
		return urn;
	}
}
