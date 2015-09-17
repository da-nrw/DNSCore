package de.uzk.hki.da.metadata;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.jdom.JDOMException;

import de.uzk.hki.da.model.Document;
import de.uzk.hki.da.utils.Path;

/**
 * @author Polina Gubaidullina
 */

public class UnknownMetadataStructure extends MetadataStructure{

	public UnknownMetadataStructure(Path workPath,File metadataFile, List<Document> documents)
			throws FileNotFoundException, JDOMException, IOException {
		super(workPath,metadataFile, documents);
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
	public HashMap<String, HashMap<String, List<String>>> getIndexInfo(String objectId) {
		// TODO Auto-generated method stub
		return null;
	}
}
