package de.uzk.hki.da.format;

import java.io.File;
import java.io.IOException;

import org.apache.tika.Tika;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MimeTypeDetectionService implements FormatIdentifier{

	protected static Logger logger = LoggerFactory.getLogger(MimeTypeDetectionService.class);

	
	@Override
	public String identify(File file) throws IOException {
		
		logger.debug("Detect MIMETYPE ");
		
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
