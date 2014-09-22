package de.uzk.hki.da.metadata;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.jdom.JDOMException;
import org.xml.sax.SAXException;

import de.uzk.hki.da.model.DAFile;

/**
 * @author Polina Gubaidullina
 */

public class MetadataStructureFactory {

	public MetadataStructure create (String type, File file, List<DAFile> daFiles) throws FileNotFoundException, JDOMException, IOException, ParserConfigurationException, SAXException {
		if (type.equalsIgnoreCase ("EAD")){
              return new EadMetsMetadataStructure(file, daFiles);
		} else if(type. equalsIgnoreCase ("LIDO")){
              return new LidoMetadataStructure(file, daFiles);
		} else if(type.equalsIgnoreCase ("METS")) {
			return new MetsMetadataStructure(file, daFiles);
		} else if (type.equalsIgnoreCase ("XMP")) {
			return new XMPMetadataStructure(file, daFiles);
		} else return new UnknownMetadataStructure(file, daFiles);
	}
}
