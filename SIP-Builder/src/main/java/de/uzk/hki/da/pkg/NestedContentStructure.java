package de.uzk.hki.da.pkg;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.input.BOMInputStream;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.xml.sax.InputSource;

import de.uzk.hki.da.metadata.MetsParser;
import de.uzk.hki.da.utils.XMLUtils;
import de.uzk.hki.da.utils.formatDetectionService;

/**
 * @author Polina Gubaidullina
 */

public class NestedContentStructure {
	
	public File rootFile;
	public HashMap<File, String> sipCandidatesWithUrns = new HashMap<File, String>();
	
	public NestedContentStructure(File sourceRootFile) throws IOException {
		setRootFile(sourceRootFile);
		try {
			searchForSipCandidates(sourceRootFile);
		} catch (JDOMException e) {
			e.printStackTrace();
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
					sipCandidatesWithUrns.put(f, urn);
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
			if(new formatDetectionService(f).isMets()) {
				metsFiles.add(f);
			}
		}
		return metsFiles;
	}
	
	private String getUrn(File metsFile) throws IOException, JDOMException {
		Document metsDoc = getDocumentFromFile(metsFile);
		return new MetsParser(metsDoc).getUrn();
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
