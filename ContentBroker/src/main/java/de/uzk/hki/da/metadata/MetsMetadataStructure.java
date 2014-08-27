package de.uzk.hki.da.metadata;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.jdom.JDOMException;

public class MetsMetadataStructure extends MetadataStructure {

	public MetsMetadataStructure(File metadataFile)
			throws FileNotFoundException, JDOMException, IOException {
		super(metadataFile);
		System.out.println("MetsMetadataStructure; TODO Parse file");
	}

	@Override
	public boolean isValid() {
		return true;
	}

}
