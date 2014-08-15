package de.uzk.hki.da.metadata;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.jdom.JDOMException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uzk.hki.da.utils.Path;

public abstract class MetadataFile {
	
	/** The logger. */
	private static Logger logger = LoggerFactory
			.getLogger(MetadataFile.class);
	
	String refResource = "";
	String mimetype = "";
	String loctype = "";
	List<File> metsFiles;
	
	public MetadataFile(File metadataFile) throws FileNotFoundException, JDOMException, IOException {
		logger.debug("New metadata file: "+metadataFile.getAbsolutePath());
	}
	
	public boolean isValid(Path objectPath) {
		return false;
	}
}
