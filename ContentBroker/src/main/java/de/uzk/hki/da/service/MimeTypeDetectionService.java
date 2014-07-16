package de.uzk.hki.da.service;

import java.io.File;
import java.io.IOException;

import org.apache.tika.Tika;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uzk.hki.da.model.DAFile;

public class MimeTypeDetectionService{

	protected static Logger logger = LoggerFactory.getLogger(MimeTypeDetectionService.class);

	public static String detectMimeType(DAFile dafile) throws IOException {
		
		File file = dafile.toRegularFile();
		String mimeType;
//		
//		if(!isTest) {
//			System.out.println("realy detect mime type");
//			Tika tika = new Tika();
//		    try {
//		        mimeType = tika.detect(file);
//		    }  catch (IOException e) {
//		        throw new IOException("Unable to open file for mime type detection: " + file.getAbsolutePath(), e);
//		    }
//			logger.debug("Detected MIME type {} for file {}",mimeType,file.getName());		
//			return mimeType;	
//		} else {
			System.out.println("fake...");
//			if(file.getName().toString().contains("tif")) {
				return "image/tiff";
//			} else return null;
//		}
	}
}
