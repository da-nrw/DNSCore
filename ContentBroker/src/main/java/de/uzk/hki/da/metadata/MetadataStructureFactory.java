package de.uzk.hki.da.metadata;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.jdom.JDOMException;
import org.xml.sax.SAXException;

public class MetadataStructureFactory {

	public MetadataStructure create (String type, File file) throws FileNotFoundException, JDOMException, IOException, ParserConfigurationException, SAXException {
		if (type.equalsIgnoreCase ("EAD")){
              return new EadMetsMetadataStructure(file);
		} else if(type. equalsIgnoreCase ("LIDO")){
              return new LidoMetadataStructure(file);
		} else if(type.equalsIgnoreCase ("METS")) {
			return new MetsMetadataStructure(file);
		} else if (type.equalsIgnoreCase ("XMP")) {
			return new XMPMetadataStructure(file);
		} else return new UnknownMetadataStructure(file);
	}
}
