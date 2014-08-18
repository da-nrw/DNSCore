package de.uzk.hki.da.metadata;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.jdom.JDOMException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class MetadataStructure {
	
	/** The logger. */
	public Logger logger = LoggerFactory
			.getLogger(MetadataStructure.class);
	
	public boolean isValid = true;
	
	public MetadataStructure(File metadataFile) throws FileNotFoundException, JDOMException, IOException {
	}
	
	public abstract boolean isValid();
}
