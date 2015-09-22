package de.uzk.hki.da.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.apache.tika.Tika;


public class formatDetectionService {
	
	private final static String metsPattern = ".*(?s)\\A.{0,1000}\\x3c([^: ]+:)?mets[^\\xce]{0,100}xmlns:?[^=]{0,10}=\"http://www.loc.gov/METS.*";
	public File file;
	
	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public formatDetectionService(File f) {
		this.file = f;
	}
	
	public String detectMimeType() throws IOException {
		String mimeType;
		
		Tika tika = new Tika();
	    try {
	        mimeType = tika.detect(file);
	    }  catch (IOException e) {
	        throw new IOException("Unable to open file for mime type detection: " + file.getAbsolutePath(), e);
	    }
		return mimeType;	
	}
	
	public boolean isXml() throws IOException {
		if(this.detectMimeType().equals("application/xml")) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean isMets() throws IOException {
		boolean isMets = false;
		if(this.isXml()) {
			String beginningOfFile = convertFirst10LinesOfFileToString(this.getFile());
			if(beginningOfFile.matches(metsPattern)) {
				isMets = true;
			}
		}
		return isMets;
	}
	
	private String convertFirst10LinesOfFileToString(File f) throws IOException {
		String result = "";
		BufferedReader br = new BufferedReader(new FileReader(f));
		String line;
		int lineCount=0;
		while ((line = br.readLine()) != null) {
		   // process the line.
			result+=line;
			lineCount++;
			if (lineCount==10) break;
		}
		br.close();
		return result;
	}
	
}
