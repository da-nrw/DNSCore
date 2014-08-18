package de.uzk.hki.da.metadata;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.jdom.JDOMException;

public class FakeMetadataStructure extends MetadataStructure{

	public FakeMetadataStructure(File metadataFile)
			throws FileNotFoundException, JDOMException, IOException {
		super(metadataFile);
		logger.debug("Create fake metadata structure.");
	}

	@Override
	public boolean isValid() {
		return true;
	}
	
}
