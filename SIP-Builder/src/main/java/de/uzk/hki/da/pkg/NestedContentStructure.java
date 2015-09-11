package de.uzk.hki.da.pkg;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Polina Gubaidullina
 */

public class NestedContentStructure {
	
	public File rootFile;
	public List<File> sipCandidates = new ArrayList<File>();
	
	public NestedContentStructure(File sourceRootFile) {
		setRootFile(sourceRootFile);
		searchForSipCandidates(sourceRootFile);
	}
	
	public File getRootFile() {
		return rootFile;
	}

	public void setRootFile(File rootFile) {
		this.rootFile = rootFile;
	}	
	
	public List<File> getSipCandidates() {
		return sipCandidates;
	}
	
	/**
	 * Search directories recursively for sip candidates
	 */
	public void searchForSipCandidates(File dir) {
		File currentDir = dir;
		for(File f : currentDir.listFiles()) {
			List<File> metsFiles = getMetsFileFromDir(f);
			if(getIncludedDirs(f).isEmpty() && metsFiles.size()==1) {
				String urn = getUrn(metsFiles.get(0));
				sipCandidates.add(f);
			} else if(getMetsFileFromDir(f).size()==0) {
				System.out.println("Der Ordner "+f+" enthält keine METS-Datei!");
			} else if(getMetsFileFromDir(f).size()>1) {
				System.out.println("Der Ordner "+f+" enthält mehr als eine METS-Datei!");
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
	
	
//	Erst mal die billige Variante mit Erkennung per Name
	private List<File> getMetsFileFromDir(File dir) {
		List<File> metsFiles = new ArrayList<File>();
		for(File f : dir.listFiles()) {
			if(f.getName().equals("METS.xml")) {
				metsFiles.add(f);
			}
		}
		return metsFiles;
	}
	
	private String getUrn(File metsFile) {
		String urn = null;
//		TODO
		return urn;
	}
}
