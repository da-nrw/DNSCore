package de.uzk.hki.da.metadata;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.jdom.JDOMException;

public class XMPMetadataStructure extends MetadataStructure{

	public XMPMetadataStructure(File metadataFile)
			throws FileNotFoundException, JDOMException, IOException {
		super(metadataFile);
		System.out.println("XMPMetadataStructure; TODO Parse file");
	}

	@Override
	public boolean isValid() {
		return true;
	}

}
