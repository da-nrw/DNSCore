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

public class FakeMetadataStructure extends MetadataStructure{

	public FakeMetadataStructure(File metadataFile, List<DAFile> daFiles)
			throws FileNotFoundException, JDOMException, IOException {
		super(metadataFile, daFiles);
		logger.debug("Create fake metadata structure.");
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
