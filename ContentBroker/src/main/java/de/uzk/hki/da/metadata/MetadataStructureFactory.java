package de.uzk.hki.da.metadata;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.jdom.JDOMException;
import org.xml.sax.SAXException;

import de.uzk.hki.da.model.Document;
import de.uzk.hki.da.util.Path;

/**
 * @author Polina Gubaidullina
 */

public class MetadataStructureFactory {

	public MetadataStructure create(Path workPath,String type, File file, List<Document> documents) throws FileNotFoundException, JDOMException, IOException, ParserConfigurationException, SAXException {
		if (type.equalsIgnoreCase ("EAD")){
              return new EadMetsMetadataStructure(workPath,file, documents);
		} else if(type. equalsIgnoreCase ("LIDO")){
              return new LidoMetadataStructure(workPath,file, documents);
		} else if(type.equalsIgnoreCase ("METS")) {
			return new MetsMetadataStructure(workPath,file, documents);
		} else if (type.equalsIgnoreCase ("XMP")) {
			return new XMPMetadataStructure(workPath,file, documents);
		} else return new UnknownMetadataStructure(workPath,file, documents);
	}
}
