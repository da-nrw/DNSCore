package de.uzk.hki.da.metadata;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.jdom.JDOMException;

import de.uzk.hki.da.model.DAFile;

/**
 * @author Polina Gubaidullina
 */

public class UnknownMetadataStructure extends MetadataStructure{

	public UnknownMetadataStructure(File metadataFile, List<DAFile> daFiles)
			throws FileNotFoundException, JDOMException, IOException {
		super(metadataFile, daFiles);
		System.out.println("UnknownMetadataStructure; TODO Parse file");
	}

	@Override
	public boolean isValid() {
		return true;
	}

	@Override
	public File getMetadataFile() {
		return null;
	}

}
