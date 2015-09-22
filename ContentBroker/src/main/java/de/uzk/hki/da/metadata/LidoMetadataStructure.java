package de.uzk.hki.da.metadata;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.input.BOMInputStream;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;

import de.uzk.hki.da.utils.C;
import de.uzk.hki.da.utils.Path;
import de.uzk.hki.da.utils.XMLUtils;

/**
 * @author Polina Gubaidullina
 */

public class LidoMetadataStructure extends MetadataStructure{
	
	/** The logger. */
	public Logger logger = LoggerFactory
			.getLogger(LidoMetadataStructure.class);
	
	private Document doc;
	private List<Element> lidoLinkResources;
	private File lidoFile;
	private List<de.uzk.hki.da.model.Document> currentDocuments;
	
	public LidoMetadataStructure(Path workPath,File metadataFile, List<de.uzk.hki.da.model.Document> documents) throws FileNotFoundException, JDOMException,
			IOException {
		super(workPath,metadataFile, documents);
		
		lidoFile = metadataFile;
		currentDocuments = documents;
		
		SAXBuilder builder = XMLUtils.createNonvalidatingSaxBuilder();		
		FileInputStream fileInputStream = new FileInputStream(Path.makeFile(workPath,metadataFile.getPath()));
		BOMInputStream bomInputStream = new BOMInputStream(fileInputStream);
		Reader reader = new InputStreamReader(bomInputStream,"UTF-8");
		InputSource is = new InputSource(reader);
		is.setEncoding("UTF-8");
		doc = builder.build(is);
		
		List<Element> lidoElements = getLidoElements();
		lidoLinkResources = parseLinkResourceElements(lidoElements);
		fileInputStream.close();
		bomInputStream.close();
	}
	
//	::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::  GETTER  ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
	
	public List<Element> getLinkResourceElemensFromLidoElement(Element lidoElement) {
		List<Element> currentLinkResources = new ArrayList<Element>();
		if(lidoElement.getName().equalsIgnoreCase("lido")) {
			Element currentLinkResource = lidoElement
			.getChild("administrativeMetadata", C.LIDO_NS)
			.getChild("resourceWrap", C.LIDO_NS)
			.getChild("resourceSet", C.LIDO_NS)
			.getChild("resourceRepresentation", C.LIDO_NS)
			.getChild("linkResource", C.LIDO_NS);
			currentLinkResources.add(currentLinkResource);
		}
		return currentLinkResources;
	}
	
	public List<String> getReferencesFromLidoElement(Element lidoElement) {
		List<String> references = new ArrayList<String>();
		try {
			List<Element> currentLinkResources = getLinkResourceElemensFromLidoElement(lidoElement);
			for(Element resourceElement : currentLinkResources) {
				references.add(resourceElement.getValue());
			}
		} catch (Exception e) {
			logger.error("Unable to find references in lido element "+lidoElement.getName());
		}
		return references;
	}
	
	@Override
	public HashMap<String, HashMap<String, List<String>>> getIndexInfo(String objectId) {
		HashMap<String, HashMap<String, List<String>>> indexInfo = new HashMap<String, HashMap<String, List<String>>>();
		try {
			HashMap<String, List<String>> lidoElementInfo;
			List<Element> lidoElements = getLidoElements();
			for(Element lidoElement : lidoElements) {
				lidoElementInfo = new HashMap<String, List<String>>();
				String id = objectId+"-"+getLidoRecID(lidoElement);
				logger.debug("ID: "+id);
				lidoElementInfo.put(C.EDM_TITLE, getTitle(lidoElement));
				lidoElementInfo.put(C.EDM_PUBLISHER, getPlaces(lidoElement));
				lidoElementInfo.put(C.EDM_DATE, getDate(lidoElement));
				List<String> references = getReferencesFromLidoElement(lidoElement);
				if(references!=null && !references.isEmpty()) {
					List<String> shownBy = new ArrayList<String>();
					shownBy.add(references.get(0));
					lidoElementInfo.put(C.EDM_IS_SHOWN_BY, shownBy);
					if(references.size()==1) {
						lidoElementInfo.put(C.EDM_OBJECT, references);
					} else {
						lidoElementInfo.put(C.EDM_OBJECT, shownBy);
						lidoElementInfo.put(C.EDM_HAS_VIEW, references);
					}
				}
				indexInfo.put(id, lidoElementInfo);
			}
		} catch (Exception e) {
			new RuntimeException("Unable to parse the lido file for edm serialization.");
		}
		return indexInfo;
	}
	
	private String getLidoRecID(Element lidoElement) {
		String recID = lidoElement.getChild("lidoRecID", C.LIDO_NS).getValue();
		return recID;
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
	private List<String> getTitle(Element lidoElement) {
		List<String> titles = new ArrayList<String>();
		String title = "";
		try {
			List<Element> titleSetElements = lidoElement
					.getChild("descriptiveMetadata", C.LIDO_NS)
					.getChild("objectIdentificationWrap", C.LIDO_NS)
					.getChild("titleWrap", C.LIDO_NS)
					.getChildren("titleSet", C.LIDO_NS);
			
			for(Element e : titleSetElements) {
				if(e.getChild("appellationValue", C.LIDO_NS)!=null) {
					title = e.getChild("appellationValue", C.LIDO_NS).getValue();
					titles.add(title);
				}
			}
		} catch (Exception e) {
			logger.error("No title Element found!");
		}
		return titles;
	}
	
	@SuppressWarnings("unchecked")
	private List<String> getDate(Element lidoElement) {
		List<String> dates = new ArrayList<String>();
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
//			logger.debug("No eventDate element found!");
		}
		for(Element e: eventDateChildren) {
			String date = "";
			if(e.getName().equals("displayDate")) {
				date = e.getValue();
			}
			if(date.equals("")&&e.getName().equals("date")) {
				try {
					String earliestDate = e.getChild("earliestDate", C.LIDO_NS).getValue();
					String latestDate = e.getChild("latestDate", C.LIDO_NS).getValue();
					if(!earliestDate.equals(latestDate)) {
						date = earliestDate+"-"+latestDate;
					}
				} catch (Exception e2) {
					
				}
			}
			if(!date.equals("")) {
				dates.add(date);
			}
		}
		return dates;
	}
	
	@SuppressWarnings("unchecked")
	private List<String> getPlaces(Element lidoElement) {
		List<String> places = new ArrayList<String>();
		List<Element> placeElementChildren = new ArrayList<Element>();
		
		String displayPlace = "";
		try {
			displayPlace = lidoElement
					.getChild("descriptiveMetadata", C.LIDO_NS)
					.getChild("eventWrap", C.LIDO_NS)
					.getChild("eventSet", C.LIDO_NS)
					.getChild("event", C.LIDO_NS)
					.getChild("eventPlace", C.LIDO_NS)
					.getChild("displayPlace", C.LIDO_NS)
					.getValue();
			if(!displayPlace.equals("")) {
				places.add(displayPlace);
			}
		} catch (Exception e) {
		}
		
		if(displayPlace.equals("")) {
			try {
				placeElementChildren = lidoElement
						.getChild("descriptiveMetadata", C.LIDO_NS)
						.getChild("eventWrap", C.LIDO_NS)
						.getChild("eventSet", C.LIDO_NS)
						.getChild("event", C.LIDO_NS)
						.getChild("eventPlace", C.LIDO_NS)
						.getChild("place", C.LIDO_NS)
						.getChildren();
			} catch (Exception e) {
//				logger.error("No place element found!");
			}
			for(Element e : placeElementChildren) {
				if(e.getName().equals("namePlaceSet")) {
					try {
						places.add(e.getChild("appellationValue", C.LIDO_NS).getValue());
					} catch (Exception e2) {
//						logger.error("No appellationValue found!");
					}
				}
			}
		}
		return places;
	}
	
	
//	:::::::::::::::::::::::::::::::::::::::::::::::::::::::::  REPLACEMENTS  :::::::::::::::::::::::::::::::::::::::::::::::::::::::::
	
	private List<Element> parseLinkResourceElements(List<Element> lidoElements) {
		List<Element> linkResourceElements = new ArrayList<Element>();
		for(Element element : lidoElements) {
			List<Element> elements = getLinkResourceElemensFromLidoElement(element);
			for(Element resourceElement : elements) {
				linkResourceElements.add(resourceElement);
			}
		}
		return linkResourceElements;
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
		outputter.output(doc, new FileWriter(Path.makeFile(workPath,lidoFile.getPath())));
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
