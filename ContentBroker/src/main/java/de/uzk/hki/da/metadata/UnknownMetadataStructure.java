package de.uzk.hki.da.metadata;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.jdom.JDOMException;

import de.uzk.hki.da.model.Document;

/**
 * @author Polina Gubaidullina
 */

public class UnknownMetadataStructure extends MetadataStructure{

	public UnknownMetadataStructure(File metadataFile, List<Document> documents)
			throws FileNotFoundException, JDOMException, IOException {
		super(metadataFile, documents);
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

	@Override
	protected HashMap<String, HashMap<String, List<String>>> getIndexInfo() {
		// TODO Auto-generated method stub
		return null;
	}
}
