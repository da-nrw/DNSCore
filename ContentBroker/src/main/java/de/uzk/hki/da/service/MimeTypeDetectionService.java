package de.uzk.hki.da.service;

import java.io.File;
import java.io.IOException;

import org.apache.tika.Tika;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uzk.hki.da.model.DAFile;

public class MimeTypeDetectionService{

	protected static Logger logger = LoggerFactory.getLogger(MimeTypeDetectionService.class);

	public String detectMimeType(DAFile dafile) throws IOException {
		
		logger.debug("Detect MIMETYPE ");
		
		File file = dafile.toRegularFile();
		String mimeType;

		Tika tika = new Tika();
	    try {
	        mimeType = tika.detect(file);
	        logger.debug("Detected MIME type {} for file {}",mimeType,file.getName());
	    }  catch (IOException e) {
	        throw new IOException("Unable to open file for mime type detection: " + file.getAbsolutePath(), e);
	    }	
	    logger.debug("return "+mimeType);
		return mimeType;	
	}
}
