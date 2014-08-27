package de.uzk.hki.da.metadata;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.jdom.JDOMException;

public class UnknownMetadataStructure extends MetadataStructure{

	public UnknownMetadataStructure(File metadataFile)
			throws FileNotFoundException, JDOMException, IOException {
		super(metadataFile);
		System.out.println("UnknownMetadataStructure; TODO Parse file");
	}

	@Override
	public boolean isValid() {
		return true;
	}

}
