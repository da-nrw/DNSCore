package de.uzk.hki.da.metadata;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.jdom.JDOMException;
import org.xml.sax.SAXException;

import de.uzk.hki.da.model.Document;

/**
 * @author Polina Gubaidullina
 */

public class MetadataStructureFactory {

	public MetadataStructure create(String type, File file, List<Document> documents) throws FileNotFoundException, JDOMException, IOException, ParserConfigurationException, SAXException {
		if (type.equalsIgnoreCase ("EAD")){
              return new EadMetsMetadataStructure(file, documents);
		} else if(type. equalsIgnoreCase ("LIDO")){
              return new LidoMetadataStructure(file, documents);
		} else if(type.equalsIgnoreCase ("METS")) {
			return new MetsMetadataStructure(file, documents);
		} else if (type.equalsIgnoreCase ("XMP")) {
			return new XMPMetadataStructure(file, documents);
		} else return new UnknownMetadataStructure(file, documents);
	}
}
