package de.uzk.hki.da.utils;

import java.io.File;
import java.io.IOException;

import org.apache.tika.Tika;


public class formatDetectionService {
	
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
}
