package de.uzk.hki.da.metadata;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.apache.commons.io.input.BOMInputStream;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import de.uzk.hki.da.core.C;

/**
 * @author Polina Gubaidullina
 */

public class LidoMetadataStructure extends MetadataStructure{
	
	private Document doc;
	private List<Element> lidoLinkResources;
	private File lidoFile;
	private List<de.uzk.hki.da.model.Document> currentDocuments;
	
	public LidoMetadataStructure(File metadataFile, List<de.uzk.hki.da.model.Document> documents) throws FileNotFoundException, JDOMException,
			IOException {
		super(metadataFile, documents);
		
		lidoFile = metadataFile;
		currentDocuments = documents;
		
		SAXBuilder builder = XMLUtils.createNonvalidatingSaxBuilder();
		FileInputStream fileInputStream = new FileInputStream(metadataFile);
		BOMInputStream bomInputStream = new BOMInputStream(fileInputStream);
		
		doc = builder.build(bomInputStream);
		lidoLinkResources = parseLinkResourceElements();
		
		printIndexInfo();
	}
	
//	::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::  GETTER  ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
	
	@Override
	protected HashMap<String, HashMap<String, String>> getIndexInfo() {
		
		HashMap<String, HashMap<String, String>> indexInfo = new HashMap<String, HashMap<String,String>>();
		HashMap<String, String> lidoElementInfo = new HashMap<String, String>();
		List<Element> lidoElements = getLidoElements();
		
		for(Element lidoElement : lidoElements) {
			String uniqueID = UUID.randomUUID().toString();
			lidoElementInfo.put("Title", getTitle(lidoElement));
			
			
			List<String> places = getPlaces(lidoElement);
			for(String place : places) {
				lidoElementInfo.put("Place", place);
			}
			
			getDate(lidoElement);
			
			
			indexInfo.put(uniqueID, lidoElementInfo);
		}
		return indexInfo;
	}
	
	private List<Element> getLidoElements() {
		@SuppressWarnings("unchecked")
		List<Element> lidoElements = doc.getRootElement().getChildren("lido", C.LIDO_NS);
		return lidoElements;
	}
	
	@Override
	public File getMetadataFile() {
		return lidoFile;
	}
	
	public List<String> getLidoLinkResources() {
		List<String> linkResources = new ArrayList<String>();
		for(Element element : lidoLinkResources) {
			linkResources.add(element.getValue());
		}
		return linkResources;
	}
	
	@SuppressWarnings("unchecked")
	private String getTitle(Element lidoElement) {
		String title = "";
		try {
			List<Element> titleSetElements = lidoElement
					.getChild("descriptiveMetadata", C.LIDO_NS)
					.getChild("objectIdentificationWrap", C.LIDO_NS)
					.getChild("titleWrap", C.LIDO_NS).getChildren("titleSet", C.LIDO_NS);
			
			for(Element e : titleSetElements) {
				if(e.getChild("appellationValue", C.LIDO_NS)!=null) {
					title = e.getChild("appellationValue", C.LIDO_NS).getValue();
				}
			}
		} catch (Exception e) {
			logger.error("No title Element found!");
		}
		return title;
	}
	
	@SuppressWarnings("unchecked")
	private String getDate(Element lidoElement) {
		String date = "";
		List<Element> eventDateChildren = new ArrayList<Element>();
		try {
			eventDateChildren = lidoElement
			.getChild("descriptiveMetadata", C.LIDO_NS)
			.getChild("eventWrap", C.LIDO_NS)
			.getChild("eventSet", C.LIDO_NS)
			.getChild("event", C.LIDO_NS)
			.getChild("eventDate", C.LIDO_NS)
			.getChildren();
		} catch (Exception e) {
			logger.debug("No eventDate element found!");
		}
		
		
		
//		.getChild("date", C.LIDO_NS)
		
//		lido:eventWrap> 
//		-<lido:eventSet> -
//		<lido:event> 
//		-<lido:eventDate> -
//		<lido:date>
//		<lido:earliestDate
		
		return null;
	}
	
	@SuppressWarnings("unchecked")
	private List<String> getPlaces(Element lidoElement) {
		List<String> places = new ArrayList<String>();
		List<Element> placeElementChildren = new ArrayList<Element>();
		try {
			placeElementChildren =lidoElement
					.getChild("descriptiveMetadata", C.LIDO_NS)
					.getChild("eventWrap", C.LIDO_NS)
					.getChild("eventSet", C.LIDO_NS)
					.getChild("event", C.LIDO_NS)
					.getChild("eventPlace", C.LIDO_NS)
					.getChild("place", C.LIDO_NS)
					.getChildren();
		} catch (Exception e) {
			logger.error("No place element found!");
		}
		for(Element e : placeElementChildren) {
			if(e.getName().equals("namePlaceSet")) {
				try {
					places.add(e.getChild("appellationValue", C.LIDO_NS).getValue());
				} catch (Exception e2) {
					logger.error("No appellationValue found!");
				}
			}
		}
		return places;
	}
	
	
//	:::::::::::::::::::::::::::::::::::::::::::::::::::::::::  REPLACEMENTS  :::::::::::::::::::::::::::::::::::::::::::::::::::::::::
	
	private List<Element> parseLinkResourceElements() {
		
		List<Element> currentLinkResources = new ArrayList<Element>();
		
		List<Element> lidoElements = getLidoElements();
		for(Element element : lidoElements) {
			if(element.getName().equalsIgnoreCase("lido")) {
				Element currentLinkResource = element
				.getChild("administrativeMetadata", C.LIDO_NS)
				.getChild("resourceWrap", C.LIDO_NS)
				.getChild("resourceSet", C.LIDO_NS)
				.getChild("resourceRepresentation", C.LIDO_NS)
				.getChild("linkResource", C.LIDO_NS);
				currentLinkResources.add(currentLinkResource);
			}
		}
		return currentLinkResources;
	}
	
	public void replaceRefResources(HashMap<String, String> linkResourceReplacements) throws IOException {
		for(String sourceLinkResource : linkResourceReplacements.keySet()) {
			for(int i=0; i<lidoLinkResources.size(); i++) {
				if(sourceLinkResource.equals(lidoLinkResources.get(i).getValue())) {
					lidoLinkResources.get(i).setText(linkResourceReplacements.get(sourceLinkResource));
				}
			}
		}
		
		XMLOutputter outputter = new XMLOutputter();
		outputter.setFormat(Format.getPrettyFormat());
		outputter.output(doc, new FileWriter(lidoFile));
	}
	
//	:::::::::::::::::::::::::::::::::::::::::::::::::::::::::   VALIDATION   :::::::::::::::::::::::::::::::::::::::::::::::::::::::::
	
	private boolean checkReferencedFiles() {
		Boolean valid = true;
		if(getLidoLinkResources().size()!=getReferencedFiles(lidoFile, getLidoLinkResources(), currentDocuments).size()) {
			valid = false;
		}
		return valid;
	}
	
	@Override
	public boolean isValid() {
		return checkReferencedFiles();
	}
}
