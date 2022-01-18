package de.uzk.hki.da.metadata;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
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

// kann man vielleich spaeter noch woanders brauchen: dann eigene Klasse
	private static List<Element> xmlChainElements(Element element, String[] chain, Namespace[] spacy, int chainIndex){
		List<Element> retti = new ArrayList<Element>();
		if (chainIndex == chain.length) {
			retti.add(element);
		} else {
			String nnn = chain[chainIndex];
			List<Element> childs;
			if (nnn.isEmpty()) {
				@SuppressWarnings("unchecked")
				List<Element> childi = element.getChildren();
				childs = childi;
			}else {
				@SuppressWarnings("unchecked")
				List<Element> childi = element.getChildren(nnn, spacy[chainIndex]);
				childs = childi;
			}
			chainIndex++;
			for (Element child : childs) {
				List<Element> wallies = xmlChainElements(child, chain, spacy, chainIndex);
				if (wallies != null) {
					retti.addAll(wallies);
				}
			}
		}
		return retti;
	} 
	
	private static List<String> xmlChainValues(Element element, String[] chain, Namespace[] spacy, int chainIndex) {
		HashSet<String> hashies = new HashSet<String>();
		List<Element> descs = xmlChainElements(element, chain, spacy, chainIndex);
		for (Element desc : descs) {
			String wally = desc.getValue();
			if (wally != null && !wally.isEmpty()) {
				hashies.add(wally);
			}
		}
		
		List<String> retti = new ArrayList<String>(hashies);
		return retti;
	}

	private static List<String> lidoChainValues(Element element, String chainString){
		String[] deffiArr = chainString.split("/", -1);
		Namespace[] spacyArr = new Namespace[deffiArr.length];
		for (int sss = 0; sss<spacyArr.length; sss++) {
			spacyArr[sss] = C.LIDO_NS;
		}
	
		List<String> strings = xmlChainValues(element, deffiArr, spacyArr, 0);
		return strings;
	}
	
	private static List<Element> lidoChainElements(Element element, String chainString){
		String[] deffiArr = chainString.split("/", -1);
		Namespace[] spacyArr = new Namespace[deffiArr.length];
		for (int sss = 0; sss<spacyArr.length; sss++) {
			spacyArr[sss] = C.LIDO_NS;
		}
	
		List<Element> elements = xmlChainElements(element, deffiArr, spacyArr, 0);
		return elements;
	}
	
	private List<String> getTitle(Element lidoElement) {
		String deffi = "descriptiveMetadata/objectIdentificationWrap/titleWrap/titleSet/appellationValue"; 
		List<String> titles = lidoChainValues(lidoElement, deffi);
		return titles;
	}

	private List<String> getDescription(Element lidoElement) {
		String deffi = "descriptiveMetadata/objectIdentificationWrap/objectDescriptionWrap/"
				+ "objectDescriptionSet/descriptiveNoteValue";
		
		List<String> descries = lidoChainValues(lidoElement, deffi);
		return descries;
	}
	
	private List<String> getIdentifier(Element lidoElement) {
		String inve = "";
		String inveDef = "descriptiveMetadata/objectIdentificationWrap/repositoryWrap/"
					   + "repositorySet/workID";
		List<Element> workies = lidoChainElements(lidoElement, inveDef); 
		for (Element worky :  workies) {
			try {
				String typpi = worky.getAttributeValue("type", C.LIDO_NS);
				if ("inventory number".equals(typpi)) {
					inve = worky.getValue();
					break;
				}
			} catch (Exception e) {
				logger.error("No identifier inventory number Element found!");
			}
		}

		String ise = "";
		String iseDef = "administrativeMetadata/resourceWrap/resourceSet/" 
					  + "resourceSource/legalBodyID";
		List<Element> isies = lidoChainElements(lidoElement, iseDef);
		for (Element isy : isies) {
			try {
				String sourcy = isy.getAttributeValue("source", C.LIDO_NS);
				if ("isil".equals(sourcy)) {
					ise = isy.getValue();
					break;
				}
			} catch (Exception e) {
				logger.error("No identifier legalBodyID Element found!");
			}
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

	private List<String> getEdmDataProvider(Element lidoElement) {
//		$1=administrativeMetadata/recordWrap/recordSource/legalBodyName/appellationValue
//		$2=administrativeMetadata/recordWrap/recordSource/legalBodyID

		String deffi = "administrativeMetadata/recordWrap/recordSource";

		List<Element> recSors = lidoChainElements(lidoElement, deffi);
		List<String> ret = new ArrayList<String>();
		for (Element recSor : recSors) {
			String legalBodyName = "";
			try {
				legalBodyName = recSor
							.getChild("legalBodyName", C.LIDO_NS)
							.getChild("appellationValue", C.LIDO_NS)
							.getValue();
			} catch (Exception e) {
				logger.error("No legalBodyName Element found!");
			}
			String legalBodyID = "";
			try {
				legalBodyID = recSor.getChild("legalBodyID", C.LIDO_NS).getValue();
			} catch (Exception e) {
				logger.error("No legalBodyID Element found!");
			}
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
		}
		
		return  ret;
	}

	private List<String> getDcRightsHolder(Element lidoElement) {
		String deffi = "administrativeMetadata/resourceWrap/resourceSet/rightsResource/" + 
				"rightsHolder/legalBodyName/appellationValue";
		
		List<String> retti = lidoChainValues(lidoElement, deffi);
		return retti;
	}
	
	private List<String> getDcRights(Element lidoElement) {
//$1=administrativeMetadata/resourceWrap/resourceSet/rightsResource/rightsType/conceptID
		List<String> rightIds = new ArrayList<String>();

		List<LidoLicense> licenses=getLicenseFromOneLidoPart(lidoElement);
		Collections.sort(licenses, new NullLastComparator<LidoLicense>());
		
		if (licenses.size()!=0 && licenses.get(0) != null)
			rightIds.add(licenses.get(0).getHref());
		
		return rightIds;
	}

	private List<String> getDate(Element lidoElement) {
		HashSet<String> dates = new HashSet<String>();
		String deffi = "descriptiveMetadata/eventWrap/eventSet/event/eventDate/";

		List<Element> eventDatesChildren = lidoChainElements(lidoElement, deffi);
		
		for (Element e : eventDatesChildren) {
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
		List<String> ret = new ArrayList<String>(dates);
		return ret;
	}

	private List<String> getType(Element lidoElement) {
		String deffi = "administrativeMetadata/resourceWrap/resourceSet/resourceType/term";
		
		List<String> retti = lidoChainValues(lidoElement, deffi);
		for (int iii=0; iii<retti.size(); iii++) {
			retti.set(iii, retti.get(iii).toUpperCase());
		}
		
		return retti;
	}

	private List<String> getProvenance(Element lidoElement) {
		String deffi = "descriptiveMetadata/eventWrap/eventSet/event/" 
					 + "eventDescriptionSet/descriptiveNoteValue";

		List<String> retti = lidoChainValues(lidoElement, deffi);
		return retti;
	}

	private List<String> getEdmProvider(Element lidoElement) {
		List<String> ret = new ArrayList<String>(); 
		ret.add("Digitales Archiv NRW");
		return ret;
	}
	
	private List<String> getExtent(Element lidoElement) {
		HashSet<String> extents = new HashSet<String>();
		String deffi = "descriptiveMetadata/objectIdentificationWrap/objectMeasurementsWrap/objectMeasurementsSet";

		List<Element> messies = lidoChainElements(lidoElement, deffi);
		for (Element messy : messies) {
			String ext = "";
			try {
				ext = messy.getChild("displayObjectMeasurements", C.LIDO_NS).getValue();
			} catch (Exception e) {
				logger.error("No displayObjectMeasurements Element found!");
			}
			String unit = "";
			try {
				unit = messy.getChild("objectMeasurements", C.LIDO_NS)
							.getChild("measurementsSet", C.LIDO_NS)
							.getChild("measurementUnit", C.LIDO_NS).getValue();
			} catch (Exception e) {
				logger.error("No measurementUnit Element found!");
			}
			ext+=" "+unit;
			extents.add(ext);
		}
		
		List<String> ret = new ArrayList<String>(extents);
		return ret;
	}

	private List<String> getSpatial(Element lidoElement) {
//		descriptiveMetadata/eventWrap/eventSet/event/eventPlace/displayPlace
//		oder
//		descriptiveMetadata/objectIdentificationWrap/repositoryWrap/repositorySet/
//		  repositoryLocation/namePlaceSet/appellationValue

		HashSet<String> spatials = new HashSet<String>();
		String cS =  "descriptiveMetadata/eventWrap/eventSet/event/eventPlace/displayPlace";
		List<String> spatials1 = lidoChainValues(lidoElement, cS);
		spatials.addAll(spatials1);
		
		cS =  "descriptiveMetadata/eventWrap/eventSet/event/eventPlace/place/namePlaceSet/appellationValue";
		List<String> spatials2 = lidoChainValues(lidoElement, cS);
		spatials.addAll(spatials2);
		
		cS =  "descriptiveMetadata/objectIdentificationWrap/repositoryWrap/repositorySet" + 
				"/repositoryLocation/namePlaceSet/appellationValue";
		List<String> spatials3 = lidoChainValues(lidoElement, cS);
		spatials.addAll(spatials3);

		List<String> ret = new ArrayList<String>(spatials);
		return ret;
	}

	public HashMap<String, HashMap<String, List<String>>> getIndexInfo(String objectId) {
		HashMap<String, HashMap<String, List<String>>> indexInfo = new HashMap<String, HashMap<String, List<String>>>();

//		edm:dataProvider - ist die Institution, die die Entscheidung trifft, die Daten öffentlich zugänglich zu machen. Das ist die dafür die Rechte besitzende Institution. Im Fall von Roidkin also der LVR, wenn wir es ganz genau nehmen.
//		edm:Provider - Ist die tasächlich an die Europeana abliefernde Institution. 
//							In unserem Fall ist das immer der Aggregator, also das DA NRW
//		dc:rights - Hier wird der Rechteinhaber angegeben. Im Fall Roidkin also LVR
//		edm:rights - Hier wird die Lizenz angegeben, unter der der Rechteinhaber das Objekt öffentlich zugänglich macht.  

		
		try {
			HashMap<String, List<String>> lidoElementInfo;
			List<Element> lidoElements = getLidoElements(); 
			for (Element lidoElement : lidoElements) {
				lidoElementInfo = new HashMap<String, List<String>>();
				String id = objectId + "-" + getLidoRecID(lidoElement);
				logger.debug("ID: " + id);
				lidoElementInfo.put(C.EDM_TITLE, getTitle(lidoElement));
				lidoElementInfo.put(C.EDM_DATE, getDate(lidoElement));
				lidoElementInfo.put(C.EDM_SPATIAL, getSpatial(lidoElement)); 
				
				lidoElementInfo.put(C.EDM_TYPE, getType(lidoElement));

				lidoElementInfo.put(C.DC_RIGHTS_HOLDER, getDcRightsHolder(lidoElement));
				lidoElementInfo.put(C.EDM_DATA_PROVIDER, getEdmDataProvider(lidoElement));
				lidoElementInfo.put(C.EDM_PROVIDER, getEdmProvider(lidoElement));
				lidoElementInfo.put(C.DC_RIGHTS, getDcRights(lidoElement));
				lidoElementInfo.put(C.EDM_RIGHTS, getDcRights(lidoElement));

				lidoElementInfo.put(C.EDM_CREATOR, getCreators(lidoElement));
				lidoElementInfo.put(C.EDM_DESCRIPTION, getDescription(lidoElement));
				lidoElementInfo.put(C.EDM_IDENTIFIER, getIdentifier(lidoElement));
				lidoElementInfo.put(C.EDM_EXTENT, getExtent(lidoElement));
				lidoElementInfo.put(C.EDM_PROVENANCE, getProvenance(lidoElement));
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

	private List<String> getCreators(Element lidoElement) {
		String deffi = "descriptiveMetadata/eventWrap/eventSet/event/eventActor/displayActorInRole";
		List<String> namePartValue = lidoChainValues(lidoElement, deffi);
		return namePartValue;
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
	 * Method search in given lidoSec for license. It returns LidoLicense object or null;
	 * 
	 * @param lidoSec
	 * @return metsLicense
	 */
	protected List<LidoLicense>  getLicenseFromOneLidoPart(Element lidoSec) {
		List<LidoLicense> lidoLicenses = new ArrayList<LidoLicense>();
	
		@SuppressWarnings("unchecked")
		List<Element> admSections= lidoSec.getChildren("administrativeMetadata", C.LIDO_NS);
		
		for (int i=0; i<admSections.size(); i++) { 
			Element resourceWrap=admSections.get(i).getChild("resourceWrap", C.LIDO_NS);
			if(resourceWrap==null){
				lidoLicenses.add(null);
				continue;
			}
			@SuppressWarnings("unchecked")
			List<Element> resourceSets= resourceWrap.getChildren("resourceSet", C.LIDO_NS);
			if(resourceSets.size()==0){
				lidoLicenses.add(null);
				continue;
			}
			for (int j=0; j<resourceSets.size(); j++) { 
				@SuppressWarnings("unchecked")
				List<Element> rightsResources= resourceSets.get(j).getChildren("rightsResource", C.LIDO_NS);
				if(rightsResources.size()==0){
					lidoLicenses.add(null);
					continue;
				}
				for (int k=0; k<rightsResources.size(); k++) {
					@SuppressWarnings("unchecked")
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
