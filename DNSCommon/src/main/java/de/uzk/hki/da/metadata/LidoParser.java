package de.uzk.hki.da.metadata;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uzk.hki.da.utils.C;

public class LidoParser {

	/** The logger. */
	public Logger logger = LoggerFactory.getLogger(LidoParser.class);

	private Document lidoDoc = new Document();
	private List<Element> lidoLinkResources;
	private List<Element> lidoElements;

	public LidoParser(Document doc) throws JDOMException {
		this.lidoDoc = doc;
		lidoElements = getLidoElements();
		lidoLinkResources = getLinkResourceElements(lidoElements);
	}

	// :::::::::::::::::::::::::::::::::::::::::::::::::::::::::::: GETTER
	// ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::

	public List<Element> getLidoLinkResources() {
		return lidoLinkResources;
	}

	public void setLidoLinkResources(List<Element> lidoLinkResources) {
		this.lidoLinkResources = lidoLinkResources;
	}

	List<Element> getLidoElements() {
		@SuppressWarnings("unchecked")
		List<Element> lidoElements = lidoDoc.getRootElement().getChildren("lido", C.LIDO_NS);
		return lidoElements;
	}

	private String getLidoRecID(Element lidoElement) {
		String recID = lidoElement.getChild("lidoRecID", C.LIDO_NS).getValue();
		return recID;
	}

	@SuppressWarnings("unchecked")
	private List<String> getTitle(Element lidoElement) {
		List<String> titles = new ArrayList<String>();
		String title = "";
		try {
			List<Element> titleSetElements = lidoElement.getChild("descriptiveMetadata", C.LIDO_NS)
					.getChild("objectIdentificationWrap", C.LIDO_NS).getChild("titleWrap", C.LIDO_NS)
					.getChildren("titleSet", C.LIDO_NS);

			for (Element e : titleSetElements) {
				if (e.getChild("appellationValue", C.LIDO_NS) != null) {
					title = e.getChild("appellationValue", C.LIDO_NS).getValue();
					titles.add(title);
				}
			}
		} catch (Exception e) {
			logger.error("No title Element found!");
		}
		return titles;
	}

	private List<String> getDescription(Element lidoElement) {
		HashSet<String> descries = new HashSet<String>();
		List<Element> elis = null;
		try {
			Element element = lidoElement.getChild("descriptiveMetadata", C.LIDO_NS);
			element = element.getChild("objectIdentificationWrap", C.LIDO_NS);
			element = element.getChild("objectDescriptionWrap", C.LIDO_NS);
			elis = element.getChildren("objectDescriptionSet", C.LIDO_NS);
		} catch (Exception e) {
			logger.error("No Extent Element found!");
			return new ArrayList<String>(descries);
		}

		for (Element ele : elis) {
			try {
				Element element = ele.getChild("descriptiveNoteValue", C.LIDO_NS);
				String descry = element.getValue();
				descries.add(descry);
			} catch (Exception e) {
				logger.error("No Extent Element found!");
			}
		}
		return new ArrayList<String>(descries);
	}
	
	@SuppressWarnings("unchecked")
	private List<String> getIdentifier(Element lidoElement) {
		String inve = "";
		try {
			List<Element> workies = lidoElement.getChild("descriptiveMetadata", C.LIDO_NS)
					.getChild("objectIdentificationWrap", C.LIDO_NS)
					.getChild("repositoryWrap", C.LIDO_NS)
					.getChild("repositorySet", C.LIDO_NS)
					.getChildren("workID", C.LIDO_NS);
			
			for (Element worky :  workies) {
				String typpi = worky.getAttributeValue("type",  C.LIDO_NS);
				if ("inventory number".equals(typpi)) {
					inve = worky.getValue();
					break;
				}
			}
		} catch (Exception e) {
			logger.error("No title Identifier:workID found!");
		}

		String ise = "";
		try {
			List<Element> isies = lidoElement.getChild("administrativeMetadata", C.LIDO_NS)
					.getChild("resourceWrap", C.LIDO_NS)
					.getChild("resourceSet", C.LIDO_NS)
					.getChild("resourceSource", C.LIDO_NS)
					.getChildren("legalBodyID", C.LIDO_NS);
			
			for (Element isy :  isies) {
				String sourcy = isy.getAttributeValue("source",  C.LIDO_NS);
				if ("isil".equals(sourcy)) {
					ise = isy.getValue();
					break;
				}
			}
		} catch (Exception e) {
			logger.error("No identifier legalBodyID Element found!");
		}

		List<String> ret = new ArrayList<String>();
		if (inve != null && !inve.equals("")) {
			if (ise != null && !ise.equals("")) {
				ret.add(inve + " " + ise);
			} else {
				ret.add(inve);
			}
		} else {
			if (ise != null && !ise.equals("")) {
				ret.add(ise);
			}
		}
		
		return  ret;
	}

	@SuppressWarnings("unchecked")
	private List<String> getDataProvider(Element lidoElement) {
		String legalBodyName = "";
		try {
			legalBodyName = lidoElement.getChild("administrativeMetadata", C.LIDO_NS)
						.getChild("resourceWrap", C.LIDO_NS)
						.getChild("resourceSet", C.LIDO_NS)
						.getChild("resourceSource", C.LIDO_NS)
						.getChild("legalBodyName", C.LIDO_NS)
						.getChild("appellationValue", C.LIDO_NS)
						.getValue();
		} catch (Exception e) {
			logger.error("No legalBodyName Element found!");
		}

		String legalBodyID = "";
		try {
			legalBodyID = lidoElement.getChild("administrativeMetadata", C.LIDO_NS)
					.getChild("resourceWrap", C.LIDO_NS)
					.getChild("resourceSet", C.LIDO_NS)
					.getChild("resourceSource", C.LIDO_NS)
					.getChild("legalBodyID", C.LIDO_NS).getValue();
			
		} catch (Exception e) {
			logger.error("No legalBodyID Element found!");
		}

		List<String> ret = new ArrayList<String>();
		if (legalBodyName != null && !legalBodyName.equals("")) {
			if (legalBodyID != null && !legalBodyID.equals("")) {
				ret.add(legalBodyName + " " + legalBodyID);
			} else {
				ret.add(legalBodyName);
			}
		} else {
			if (legalBodyID != null && !legalBodyID.equals("")) {
				ret.add(legalBodyID);
			}
		}
		
		return  ret;
	}

	@SuppressWarnings("unchecked")
	private List<String> getRightsHolder(Element lidoElement) {
		List<String> rightsHolder = new ArrayList<String>();
		try {
			String holder = lidoElement.getChild("administrativeMetadata", C.LIDO_NS)
					.getChild("resourceWrap", C.LIDO_NS)
					.getChild("resourceSet", C.LIDO_NS)
					.getChild("rightsResource", C.LIDO_NS)
					.getChild("rightsHolder", C.LIDO_NS)
					.getChild("legalBodyName", C.LIDO_NS)
					.getChild("appellationValue", C.LIDO_NS)
					.getValue();
			rightsHolder.add(holder);
		} catch (Exception e) {
			logger.error("No RightsHolder Element found!");
		}
		return rightsHolder;
	}
	
	@SuppressWarnings("unchecked")
	private List<String> getRecordRights(Element lidoElement) {
		List<String> rightIds = new ArrayList<String>();

		List<LidoLicense> licenses=getLicenseFromOneLidoPart(lidoElement);
		Collections.sort(licenses, new NullLastComparator<LidoLicense>());
		
		if (licenses.size()!=0 && licenses.get(0) != null)
			rightIds.add(licenses.get(0).getHref());
		
		return rightIds;
	}

	@SuppressWarnings("unchecked")
	private List<String> getDate(Element lidoElement) {
		List<String> dates = new ArrayList<String>();
		Element event;
		List<Element> eventDateChildren = new ArrayList<Element>();
		try {
			event = lidoElement.getChild("descriptiveMetadata", C.LIDO_NS).getChild("eventWrap", C.LIDO_NS)
					.getChild("eventSet", C.LIDO_NS).getChild("event", C.LIDO_NS);
			String eviType =event.getAttributeValue("eventType");
			
			eventDateChildren = event.getChild("eventDate", C.LIDO_NS).getChildren();
		} catch (Exception e) {
			 logger.debug("No eventDate element found!");
		}
		for (Element e : eventDateChildren) {
			String date = "";
			if (e.getName().equals("displayDate")) {
				date = e.getValue();
			}
			if (date.equals("") && e.getName().equals("date")) {
				try {
					String earliestDate = e.getChild("earliestDate", C.LIDO_NS).getValue();
					String latestDate = e.getChild("latestDate", C.LIDO_NS).getValue();
					if (!earliestDate.equals(latestDate)) {
						date = earliestDate + "-" + latestDate;
					}
				} catch (Exception e2) {

				}
			}
			if (!date.equals("")) {
				dates.add(date);
			}
		}
		return dates;
	}

	@SuppressWarnings("unchecked")
	private List<String> getType(Element lidoElement) {
		HashSet<String> extents = new HashSet<String>();
		List<Element> elis = null;
		try {
			Element element = lidoElement.getChild("administrativeMetadata", C.LIDO_NS);
			element = element.getChild("resourceWrap", C.LIDO_NS);

			elis = element.getChildren("resourceSet", C.LIDO_NS);
		} catch (Exception e) {
			logger.error("No Type Element found!");
			return new ArrayList<String>(extents);
		}

		for (Element ele : elis) {
			try {
				Element element = ele.getChild("resourceType", C.LIDO_NS);
				element = element.getChild("term", C.LIDO_NS);
				String ext = element.getValue();
				ext = ext.toUpperCase();
				extents.add(ext);
			} catch (Exception e) {
				logger.error("No Type Element found!");
			}
		}
		return new ArrayList<String>(extents);
	}

	@SuppressWarnings("unchecked")
	private List<String> getProvenance(Element lidoElement) {
		HashSet<String> extents = new HashSet<String>();
		List<Element> elis = null;
		try {
			Element element = lidoElement.getChild("descriptiveMetadata", C.LIDO_NS);
			element = element.getChild("eventWrap", C.LIDO_NS);

			elis = element.getChildren("eventSet", C.LIDO_NS);
		} catch (Exception e) {
			logger.error("No Extent Element found!");
			return new ArrayList<String>(extents);
		}

		for (Element ele : elis) {
			try {
				Element element = ele.getChild("event", C.LIDO_NS);
				element = element.getChild("eventDescriptionSet", C.LIDO_NS);
				element = element.getChild("descriptiveNoteValue", C.LIDO_NS);
				String ext = element.getValue();
				extents.add(ext);
			} catch (Exception e) {
				logger.error("No Extent Element found!");
			}
		}
		return new ArrayList<String>(extents);
	}

	@SuppressWarnings("unchecked")
	private List<String> getProvider(Element lidoElement) {
		HashSet<String> providers = new HashSet<String>();
		List<Element> elis = null;
		try {
			Element element = lidoElement.getChild("descriptiveMetadata", C.LIDO_NS);
			element = element.getChild("objectIdentificationWrap", C.LIDO_NS);
			elis = element.getChildren("repositoryWrap", C.LIDO_NS);
		} catch (Exception e) {
			logger.error("No Provider Element found!");
			return new ArrayList<String>(providers);
		}

		for (Element ele : elis) {
			try {
				Element element = ele.getChild("repositorySet", C.LIDO_NS);
				element = element.getChild("repositoryName", C.LIDO_NS);
				element = element.getChild("legalBodyName", C.LIDO_NS);
				element = element.getChild("appellationValue", C.LIDO_NS);
				String provider = element.getValue();
				providers.add(provider);
			} catch (Exception e) {
				logger.error("No Provider Element found!");
			}
		}
		return new ArrayList<String>(providers);
	}

	
	@SuppressWarnings("unchecked")
	private List<String> getMaterialsTech(Element lidoElement) {
		List<String> extents = new ArrayList<String>();
		List<Element> elis = null;
		try {
			Element element = lidoElement.getChild("descriptiveMetadata", C.LIDO_NS);
			element = element.getChild("eventWrap", C.LIDO_NS);

			elis = element.getChildren("eventSet", C.LIDO_NS);
		} catch (Exception e) {
			logger.error("No Extent Element found!");
			return extents;
		}

		for (Element ele : elis) {
			try {
				Element element = ele.getChild("event", C.LIDO_NS);
				element = element.getChild("eventMaterialsTech", C.LIDO_NS);
				element = element.getChild("displayMaterialsTech", C.LIDO_NS);
				String ext = element.getValue();
				extents.add(ext);
			} catch (Exception e) {
				logger.error("No Extent Element found!");
			}
		}
		return extents;
	}

	@SuppressWarnings("unchecked")
	private List<String> getExtent(Element lidoElement) {
		List<String> extents = new ArrayList();
		try {
			Element element = lidoElement.getChild("descriptiveMetadata", C.LIDO_NS);
			element = element.getChild("objectIdentificationWrap", C.LIDO_NS);
			element = element.getChild("objectMeasurementsWrap", C.LIDO_NS);
			element = element.getChild("objectMeasurementsSet", C.LIDO_NS);
			element = element.getChild("displayObjectMeasurements", C.LIDO_NS);
			String ext = element.getValue();
			extents.add(ext);
		} catch (Exception e) {
			logger.error("No Extent Element found!");
		}
		return extents;
	}

	@SuppressWarnings("unchecked")
	private List<String> getPlaces(Element lidoElement) {
		List<String> places = new ArrayList<String>();
		List<Element> placeElementChildren = new ArrayList<Element>();

		String displayPlace = "";
		try {
			displayPlace = lidoElement.getChild("descriptiveMetadata", C.LIDO_NS).getChild("eventWrap", C.LIDO_NS)
					.getChild("eventSet", C.LIDO_NS).getChild("event", C.LIDO_NS).getChild("eventPlace", C.LIDO_NS)
					.getChild("displayPlace", C.LIDO_NS).getValue();
			if (!displayPlace.equals("")) {
				places.add(displayPlace);
			}
		} catch (Exception e) {
		}

		if (displayPlace.equals("")) {
			try {
				placeElementChildren = lidoElement.getChild("descriptiveMetadata", C.LIDO_NS)
						.getChild("eventWrap", C.LIDO_NS).getChild("eventSet", C.LIDO_NS).getChild("event", C.LIDO_NS)
						.getChild("eventPlace", C.LIDO_NS).getChild("place", C.LIDO_NS).getChildren();
			} catch (Exception e) {
				 logger.error("No place element found!");
			}
			for (Element e : placeElementChildren) {
				if (e.getName().equals("namePlaceSet")) {
					try {
						places.add(e.getChild("appellationValue", C.LIDO_NS).getValue());
					} catch (Exception e2) {
						 logger.error("No appellationValue found!");
					}
				}
			}
		}
		return places;
	}

	public HashMap<String, HashMap<String, List<String>>> getIndexInfo(String objectId) {
		HashMap<String, HashMap<String, List<String>>> indexInfo = new HashMap<String, HashMap<String, List<String>>>();
		try {
			HashMap<String, List<String>> lidoElementInfo;
			List<Element> lidoElements = getLidoElements(); 
			for (Element lidoElement : lidoElements) {
				lidoElementInfo = new HashMap<String, List<String>>();
				String id = objectId + "-" + getLidoRecID(lidoElement);
				logger.debug("ID: " + id);
				lidoElementInfo.put(C.EDM_TITLE, getTitle(lidoElement));
				lidoElementInfo.put(C.DC_RIGHTS, getRecordRights(lidoElement));
				lidoElementInfo.put(C.EDM_RIGHTS, getRecordRights(lidoElement));
				lidoElementInfo.put(C.DC_RIGHTS_HOLDER, getRightsHolder(lidoElement));
				//lidoElementInfo.put(C.EDM_PUBLISHER, getPlaces(lidoElement));
				lidoElementInfo.put(C.EDM_SPATIAL, getPlaces(lidoElement)); 
				// edm:currentLocation dcterms:spatial
				lidoElementInfo.put(C.EDM_DATE, getDate(lidoElement));
				lidoElementInfo.put(C.EDM_TYPE, getType(lidoElement));
				
//				Names
				List<String> creators = new ArrayList<String>();
				List<String> contributors = new ArrayList<String>();
				for(Element actor : getEventActor(lidoElement)) {
					String creator = getCreator(actor);
					String contributor = getContributor(actor);
					if(!creator.equals("")) {
						creators.add(creator);
					}
					if(!contributor.equals("")) {
						contributors.add(contributor);
					}
				}
				lidoElementInfo.put(C.EDM_CREATOR, creators);
				lidoElementInfo.put(C.EDM_CONTRIBUTOR, contributors);
				lidoElementInfo.put(C.EDM_DESCRIPTION, getDescription(lidoElement));
				lidoElementInfo.put(C.EDM_IDENTIFIER, getIdentifier(lidoElement));
				/*				
//				Date && Place
				List<String> datesIssued = new ArrayList<String>();
				List<String> datesCreated = new ArrayList<String>();
				List<String> publishers = new ArrayList<String>();
				for(Element origInfo : getOrigInfoElements(e)) {
					String dateIssued = getDateIssued(origInfo);
					String dateCreated = getDateCreated(origInfo);
					String publisher = getPublisher(origInfo);
					if(!dateIssued.equals("")) {
						datesIssued.add(dateIssued);
					}
					if(!dateCreated.equals("")) {
						datesCreated.add(dateCreated);
					}
					if(!publisher.equals("")) {
						publishers.add(publisher);
					}
				}
				lidoElementInfo.put(C.EDM_DATE_ISSUED, datesIssued);
				lidoElementInfo.put(C.EDM_DATE_CREATED, datesCreated);
				lidoElementInfo.put(C.EDM_PUBLISHER, publishers);
				
				
				List<String> allPhysicalDescr = getPhysicalDescriptionFromDmdId(objectId, id);
				if (!allPhysicalDescr.isEmpty()) {
					lidoElementInfo.put(C.EDM_EXTENT, allPhysicalDescr);
				}
				
				*/
				lidoElementInfo.put(C.EDM_EXTENT, getExtent(lidoElement));
				lidoElementInfo.put(C.EDM_DATA_PROVIDER, getDataProvider(lidoElement));
				lidoElementInfo.put(C.EDM_PROVIDER, getProvider(lidoElement));
				lidoElementInfo.put(C.EDM_PROVENANCE, getProvenance(lidoElement));
				
				List<String> references = getReferencesFromLidoElement(lidoElement);
				if (references != null && !references.isEmpty()) {
					List<String> shownBy = new ArrayList<String>();
					shownBy.add(references.get(0));
					lidoElementInfo.put(C.EDM_IS_SHOWN_BY, shownBy);
					if (references.size() == 1) {
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

	private String getContributor(Element actor) {
		// TODO Auto-generated method stub
		return "";
	}

	private String getCreator(Element actor) {
		String namePartValue = "";
		try {
			Element actDisplay = actor.getChild("displayActorInRole", C.LIDO_NS);
			if(actDisplay!=null && !actDisplay.getValue().trim().isEmpty())
				namePartValue=actDisplay.getValue();
		} catch (Exception e) {
			logger.debug("No creator found!");
		}
		return namePartValue;
	}

	private List<Element> getEventActor(Element lidoElement) {
		List<Element> ret=new ArrayList<Element>();
		try {
			ret = lidoElement.getChild("descriptiveMetadata", C.LIDO_NS).getChild("eventWrap", C.LIDO_NS)
					.getChild("eventSet", C.LIDO_NS).getChild("event", C.LIDO_NS).getChildren("eventActor", C.LIDO_NS);
					
		} catch (Exception e) {
		}
		return ret;
	}

	public List<String> getReferencesFromLidoElement(Element lidoElement) {
		List<String> references = new ArrayList<String>();
		try {
			List<Element> currentLinkResources = getLinkResourceElemensFromLidoElement(lidoElement);
			for (Element resourceElement : currentLinkResources) {
				references.add(resourceElement.getValue());
			}
		} catch (Exception e) {
			logger.error("Unable to find references in lido element " + lidoElement.getName());
		}
		return references;
	}

	public List<Element> getLinkResourceElemensFromLidoElement(Element lidoElement) {
		List<Element> currentLinkResources = new ArrayList<Element>();
		if (lidoElement.getName().equalsIgnoreCase("lido")) {
			Element currentLinkResource = lidoElement.getChild("administrativeMetadata", C.LIDO_NS)
					.getChild("resourceWrap", C.LIDO_NS).getChild("resourceSet", C.LIDO_NS)
					.getChild("resourceRepresentation", C.LIDO_NS).getChild("linkResource", C.LIDO_NS);
			currentLinkResources.add(currentLinkResource);
		}
		return currentLinkResources;
	}

	private List<Element> getLinkResourceElements(List<Element> lidoElements) {
		List<Element> linkResourceElements = new ArrayList<Element>();
		for (Element element : lidoElements) {
			List<Element> elements = getLinkResourceElemensFromLidoElement(element);
			for (Element resourceElement : elements) {
				linkResourceElements.add(resourceElement);
			}
		}
		return linkResourceElements;
	}

	public List<String> getReferences() {
		List<String> linkResources = new ArrayList<String>();
		for (Element element : lidoLinkResources) {
			linkResources.add(element.getValue());
		}
		return linkResources;
	}
	
	
	/**
	 * 
	 * Method search in each lido-Section for license and return one license instance, only if each dmdSec contains same license, otherwise method causes exceptions.
	 * @return
	 */
	public LidoLicense getLicenseForWholeLido() {
		return getLicenseForWholeLido(false);
	}
	
	/**
	 * 
	 * Method search in each lido-Section for license and return one license instance, only if each dmdSec contains same license, otherwise method causes exceptions.
	 * @return
	 */
	protected LidoLicense getLicenseForWholeLido(boolean quiet) {
		ArrayList<LidoLicense> licenseAl=new ArrayList<LidoLicense>();
		@SuppressWarnings("unchecked")
		List<Element> lidoSecs = lidoDoc.getRootElement().getChildren("lido", C.LIDO_NS);
		for(Element lido : lidoSecs) {
			licenseAl.addAll(getLicenseFromOneLidoPart(lido));
		}
		if(licenseAl.size()==0)
			return null;
		//check all licenses, all have to be the same
		Collections.sort(licenseAl,new NullLastComparator<LidoLicense>());
		if(licenseAl.get(0)==null) //all licenses are null
			return null;
		if(!licenseAl.get(0).equals(licenseAl.get(licenseAl.size()-1))) //first and last element have to be same in sorted array
			if(!quiet)
				throw new RuntimeException("LIDO-Metadata contains different licenses("+licenseAl.size()+") e.g.:"+licenseAl.get(licenseAl.size()-1)+" "+licenseAl.get(0));
		
		return licenseAl.get(0);
	}
	
	/**
	 * Method search in given dmdSec for license. It returns MetsLicense object or null;
	 * 
	 * @param lidoSec
	 * @return metsLicense
	 */
	protected List<LidoLicense>  getLicenseFromOneLidoPart(Element lidoSec) {
		List<LidoLicense> lidoLicenses = new ArrayList<LidoLicense>();
		
		
		List<Element> admSections= lidoSec.getChildren("administrativeMetadata", C.LIDO_NS);
		
		for (int i=0; i<admSections.size(); i++) { 
			Element resourceWrap=admSections.get(i).getChild("resourceWrap", C.LIDO_NS);
			if(resourceWrap==null){
				lidoLicenses.add(null);
				continue;
			}
			List<Element> resourceSets= resourceWrap.getChildren("resourceSet", C.LIDO_NS);
			if(resourceSets.size()==0){
				lidoLicenses.add(null);
				continue;
			}
			for (int j=0; j<resourceSets.size(); j++) { 
				List<Element> rightsResources= resourceSets.get(j).getChildren("rightsResource", C.LIDO_NS);
				if(rightsResources.size()==0){
					lidoLicenses.add(null);
					continue;
				}
				for (int k=0; k<rightsResources.size(); k++) {
					List<Element> rightsTypes=rightsResources.get(k).getChildren("rightsType", C.LIDO_NS);
					if(rightsTypes.size()==0){
						lidoLicenses.add(null);
						continue;
					}
					for (int m=0; m<rightsTypes.size(); m++) {
						lidoLicenses.add(getLicenseFromOneRightsType(rightsTypes.get(m)));
					}
				}
			}
		}
		return lidoLicenses;
	}
	
	@SuppressWarnings("unchecked")
	protected LidoLicense getLicenseFromOneRightsType(Element rightsType) {
		List<Element> conceptIDs=rightsType.getChildren("conceptID", C.LIDO_NS);
		List<Element> terms=rightsType.getChildren("term", C.LIDO_NS);
		if(conceptIDs.size()==0 ||terms.size()==0 )
			throw new RuntimeException("LIDO-Metadata RightsType has no conceptID and/or term "+rightsType.toString());
			//return null;
		if(conceptIDs.size()!=1 ||terms.size()!=1 )
			throw new RuntimeException("LIDO-Metadata RightsType has more as one conceptID and/or term: "+rightsType.getValue());
		
		String licenseURI=conceptIDs.get(0).getText();
		String type=conceptIDs.get(0).getAttribute("type",C.LIDO_NS).getValue();
		if(!"URI".equals(type)){
			logger.debug("lido:conceptID type is not URI: "+type);
		}
		String term=terms.get(0).getText();
		return new LidoLicense(licenseURI,term);
	}
	
}
