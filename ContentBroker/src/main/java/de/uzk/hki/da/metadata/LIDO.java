package de.uzk.hki.da.metadata;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.input.BOMInputStream;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import de.uzk.hki.da.utils.XMLUtils;


public class LIDO extends MetadataFile{
	
	private static final Namespace LIDO_NS = Namespace.getNamespace("http://www.lido-schema.org");
	private Document doc;
	private List<Element> linkResources;
	private File currentMetadataFile;
	
	public LIDO(File metadataFile) throws FileNotFoundException, JDOMException,
			IOException {
		super(metadataFile);
		
		currentMetadataFile = metadataFile;
		
		SAXBuilder builder = XMLUtils.createNonvalidatingSaxBuilder();
		FileInputStream fileInputStream = new FileInputStream(metadataFile);
		BOMInputStream bomInputStream = new BOMInputStream(fileInputStream);
		
		doc = builder.build(bomInputStream);
		linkResources = getLinkResourceElements();
	}
	
	public List<Element> getLinkResourceElements() {
		
		linkResources = new ArrayList<Element>();
		
		@SuppressWarnings("unchecked")
		List<Element> lidoElements = doc.getRootElement().getChildren();
		for(Element element : lidoElements) {
			if(element.getName().equalsIgnoreCase("lido")) {
				Element currentLinkResource = element
				.getChild("administrativeMetadata", LIDO_NS)
				.getChild("resourceWrap", LIDO_NS)
				.getChild("resourceSet", LIDO_NS)
				.getChild("resourceRepresentation", LIDO_NS)
				.getChild("linkResource", LIDO_NS);
				
				linkResources.add(currentLinkResource);
			}
		}
		return linkResources;
	}
	
	public void setRefResources(HashMap<String, String> linkResourceReplacements) throws IOException {
		
		for(String sourceLinkResource : linkResourceReplacements.keySet()) {
			for(int i=0; i<linkResources.size(); i++) {
				if(sourceLinkResource.equals(linkResources.get(i).getValue())) {
					linkResources.get(i).setText(linkResourceReplacements.get(sourceLinkResource));
				}
			}
		}
		
		XMLOutputter outputter = new XMLOutputter();
		outputter.setFormat(Format.getPrettyFormat());
		outputter.output(doc, new FileWriter(currentMetadataFile));
	}
}
