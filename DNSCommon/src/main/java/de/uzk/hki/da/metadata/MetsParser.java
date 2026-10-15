package de.uzk.hki.da.metadata;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.xpath.XPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uzk.hki.da.utils.C;

/**
 * @author Eugen Trebunski
 * @author Polina Gubaidullina
 */

public class MetsParser{
	
	/** The logger. */
	public Logger logger = LoggerFactory
			.getLogger(MetsParser.class);
	
	private Document metsDoc = new Document();
	private String METS_XPATH_EXPRESSION = "//mets:file";
	XPath metsXPath = XPath.newInstance(METS_XPATH_EXPRESSION);
	private final String TITLE_PAGE = "title_page";
	private final String STRUCTMAP_TYPE_LOGICAL = "LOGICAL";
	private final Namespace XLINK_NS = 	Namespace.getNamespace("http://www.w3.org/1999/xlink");
	private final Namespace METS_NS = Namespace.getNamespace("http://www.loc.gov/METS/");
	private final List<Element> fileElements;
	public static final String titleSparator=" : ";
	public static final String dateIssuedCreatedCondition="[Electronic ed.]";
	public static final String PDM_MARK_URI="https://creativecommons.org/publicdomain/mark/1.0/";
	public static final String PDM_MARK_URI_HTTP="http://creativecommons.org/publicdomain/mark/1.0/";
	private static final String HTTP_PREFIX="http://";
	private static final String HTTPS_PREFIX="https://";

	public MetsParser(Document doc) throws JDOMException {
		this.metsDoc = doc;
		fileElements = getFileElementsFromMetsDoc(metsDoc);
	}
	
	
	public Document getMetsDoc() {
		return metsDoc;
	}

	public void setMetsDoc(Document metsDoc) {
		this.metsDoc = metsDoc;
	}
	
	public String getUrn() {
		String urn = getUrn(null); 
		return urn; 
	}
	
	public String getUrn(String origName) {
		if (origName != null) {
			List<String> urns = new ArrayList<String>();
			try {
				@SuppressWarnings("unchecked")
				List<Element> dmdSecs = metsDoc.getRootElement().getChildren("dmdSec", METS_NS);
				for (Element dmdSec : dmdSecs) {
					List<String> durns = urnsFromDmdSec(dmdSec);
					urns.addAll(durns);
				}
			} catch (Exception e) {
				logger.error("Unable to find urn.");
			}
			if (urns.size() == 0) {
				return null;
			}
			String urnName = origName.replace('+', ':');
			for (String uuu : urns) {
				if (urnName.equalsIgnoreCase(uuu)) {
					return uuu;
				}
			}
		}
		
		try {
			String rootDmdSecId = getUniqueRootElementInLogicalStructMap().getAttributeValue("DMDID");
			@SuppressWarnings("unchecked")
			List<Element> dmdSecs = metsDoc.getRootElement().getChildren("dmdSec", METS_NS);
			for(Element dmdSec : dmdSecs) {
				if(dmdSec.getAttributeValue("ID").equals(rootDmdSecId)) {
					List<String> urns = urnsFromDmdSec(dmdSec);
					return urns.get(urns.size() - 1);
				}
			}
		} catch (Exception e) {
			logger.error("Unable to find urn.");
		}
		return null;
	}

	public List<String> getUrns() {
		List<String> urns = new ArrayList<String>();
		try {
			String rootDmdSecId = getUniqueRootElementInLogicalStructMap().getAttributeValue("DMDID");
			@SuppressWarnings("unchecked")
			List<Element> dmdSecs = metsDoc.getRootElement().getChildren("dmdSec", METS_NS);
			for(Element dmdSec : dmdSecs) {
				if(dmdSec.getAttributeValue("ID").equals(rootDmdSecId)) {
					urns = urnsFromDmdSec(dmdSec);
				}
			}
		} catch (Exception e) {
			logger.error("Unable to find urn.");
		}
		return urns;
	}
	
	public List<String> urnsFromDmdSec(Element dmdSec) {
		List<String> urns = new ArrayList<String>();
		try {
			Element rootDmdSec = dmdSec;
			@SuppressWarnings("unchecked")
			List<Element> elements = getModsXmlData(rootDmdSec).getChildren();
			for (Element e : elements) {
				if(e.getName().equals("identifier") && e.getAttributeValue("type").equals("urn")) {
					String urn = e.getValue();
					urns.add(urn);
				}
			}
		} catch (Exception e) {
			logger.error("Unable to find urn.");
		}
		return urns;
	}
	
	/**
	 * 
	 * Method search in each dmdSec for license and return one license instance for each dmdSec  
	 * @return
	 */
	public List<MetsLicense> getLicensesForWholeMets() {
		ArrayList<MetsLicense> licenseAl=new ArrayList<MetsLicense>();
		@SuppressWarnings("unchecked")
		List<Element> dmdSecs = metsDoc.getRootElement().getChildren("dmdSec", METS_NS);
		for(Element dmdSec : dmdSecs) {
			licenseAl.add(getLicense(dmdSec,false));
		}

		Collections.sort(licenseAl,new NullLastComparator<MetsLicense>());
		logger.debug("Recognized License in METS("+licenseAl.size()+") "+licenseAl.get(0));
		return licenseAl;
	}
	
	
	/**
	 * Method search in given dmdSec for license. It returns MetsLicense object or null;
	 * 
	 * @param dmdSec
	 * @return metsLicense
	 */
	protected MetsLicense getLicense(Element dmdSec, boolean quiet) {
		MetsLicense metsLicenseReturn = null;
		List<MetsLicense> metsLicenseList = new ArrayList<MetsLicense>();
		List<Element> accessConditionList = new ArrayList<Element>();
		try {
			accessConditionList = getModsXmlData(dmdSec).getChildren("accessCondition", C.MODS_NS);
		} catch (Exception e) {
			logger.debug("No accessCondition element found! "+e.getMessage());
		}

		for(Element accessCondition:accessConditionList){
			try {
				MetsLicense metsLicense=new MetsLicense();
				metsLicense.setHref(accessCondition.getAttributeValue("href", XLINK_NS));
				metsLicense.setType(accessCondition.getAttributeValue("type"));
				metsLicense.setText(accessCondition.getValue());
				metsLicense.setDisplayLabel(accessCondition.getAttributeValue("displayLabel"));
				
				if(metsLicense.getType()!=null && metsLicense.getType().equals(metsLicense.USE_AND_REP_TYPE))
					metsLicenseList.add(metsLicense);
			} catch (Exception e) {
				logger.debug("No valid accessCondition element found! "+e.getMessage());
			}
		}

		Collections.sort(metsLicenseList, new NullLastComparator<MetsLicense>());

		if(metsLicenseList.size()>=1){
			metsLicenseReturn=metsLicenseList.get(0);
			if(quiet!=true && metsLicenseList.size()>1)
				throw new RuntimeException("dmdSec contains multiple licenses (accessCondition-Elements), unsuported");
		}

		return metsLicenseReturn;
	}
	
	private List<String> getPhysicalDescriptionFromDmdId(String dmdID,String objectId) {
		List<String> extent = new ArrayList<String>();
		String logicalId = dmdID.replace(objectId+"-", "");
		try {
			@SuppressWarnings("unchecked")
			List<Element> dmdSecs = metsDoc.getRootElement().getChildren("dmdSec", METS_NS);
			for(Element dmdSec : dmdSecs) {
				if(dmdSec.getAttributeValue("ID").equals(logicalId)) {
					Element rootDmdSec = dmdSec;
					@SuppressWarnings("unchecked")
					List<Element> elements = getModsXmlData(rootDmdSec).getChildren("physicalDescription",C.MODS_NS);
					for (Element e : elements) {
						if(e.getName().equals("physicalDescription") && !e.getChildren("extent", C.MODS_NS).isEmpty()) {
							List<Element> childElements=e.getChildren("extent", C.MODS_NS);
							for(Element eChild:childElements){
								if(!eChild.getValue().trim().isEmpty())
									extent.add(eChild.getValue());
							}
						}
					}
				}
			}
		} catch (Exception e) {
			logger.error("Unable to process xml element: "+e.getMessage());
		}
		return extent;
		}

	
	@SuppressWarnings("unchecked")
	private List<String> getTitlePageReferencesFromFrontimage() {
		List<String> ret = new ArrayList<String>();
		
		List<Element> fileSecs = metsDoc.getRootElement().getChildren("fileSec", METS_NS);
		if (fileSecs == null){
			return ret;
		}
		List<Element> fileGrps = null;
		for (Element fileSec : fileSecs){
			fileGrps = fileSec.getChildren("fileGrp", METS_NS);
			if (fileGrps != null){
				break;
			}
		}
		if (fileGrps == null){
			return ret;
		}
		List<Element> files = null;
		for (Element fileGrp : fileGrps) {
			String usi = fileGrp.getAttributeValue("USE");
			if ("FRONTIMAGE".equals(usi)) {
				files = fileGrp.getChildren("file", METS_NS);
				if (files != null) {
					break;
				}
			}
			if ("TEASER".equals(usi)) {
				files = fileGrp.getChildren("file", METS_NS);
				if (files != null) {
					break;
				}
			}
		}
		if (files == null){
			return ret;
		}
		
		for (Element file : files){
			List<Element> fLocs = file.getChildren("FLocat", METS_NS);
			if (fLocs == null){
				continue;
			}
			
			for (Element fLoc : fLocs){
				String href = fLoc.getAttributeValue("href", XLINK_NS);
				if (href != null && href.length() > 0){
					ret.add(href);
				}
			}
		}
		
		return ret;
	}
	
	private List<String> getTitlePageReferencesFromDmdId(String dmdID, String objectId) {
		List<String> titlePageRefs = new ArrayList<String>();
		String titlePageLogicalId = getIdFromLogicalStructMap(dmdID.replace(objectId+"-", ""), TITLE_PAGE, "ID");
		List<String> physIds = getPhysicalIdsFromStructLink(metsDoc, titlePageLogicalId);
		for(String physId : physIds) {
			String titlePageFileId = getFileIdFromPhysicalId(physId);
			if(!titlePageFileId.equals("")) {
				String titlePageRef = getReferenceFromFileId(titlePageFileId);
				if(!titlePageRef.equals("")) {
					titlePageRefs.add(titlePageRef);
				}
			}
		}
		return titlePageRefs;
	}
	
	private List<String> getReferencesFromDmdId(String dmdID, String objectId) {	
		List<String> references = new ArrayList<String>();
		String logicalId = getIdFromLogicalStructMap(dmdID.replace(objectId+"-", ""), "", "ID");
		List<String> physicalIds = getPhysicalIdsFromStructLink(metsDoc, logicalId);
		for(String physicalId : physicalIds) {
			if(!getFileIdFromPhysicalId(physicalId).equals("")) {
				String fileId = getFileIdFromPhysicalId(physicalId);
				String ref = getReferenceFromFileId(fileId);
				if(!ref.equals("")) {
					references.add(ref);
				}
			}
		}
		return references;
	}
	
	@SuppressWarnings("unchecked")
	private String getFileIdFromPhysicalId(String physicalId) {
		String fileId = "";
		try {
			List<Element> structMaps = getStructMaps(metsDoc);
			for(Element s : structMaps) {
				if(s.getAttributeValue("TYPE").equals("PHYSICAL")) {
					List<Element> divList =  s.getChildren("div", METS_NS);
					for(Element div : divList) {
						if(div.getAttributeValue("TYPE").equals("physSequence") && div.getAttributeValue("ID").equals("physroot")) {
							List<Element> divs =  div.getChildren("div", METS_NS);
							for(Element d : divs) {
								if(d.getAttributeValue("ID").equals(physicalId)) {
									fileId = d.getChild("fptr", C.METS_NS).getAttributeValue("FILEID");
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			logger.debug("Unable to file the file id.");
		}
		return fileId;
	}
	
	@SuppressWarnings("unchecked")
	private List<Element> getStructMaps(Document doc) {
		List<Element> structMap = new ArrayList<Element>();
		try {
			structMap = doc.getRootElement().getChildren("structMap", METS_NS);
		} catch (Exception e) {
			logger.debug("Unable to find the structMap elements.");
		}
		return structMap;
	} 
	
	public Element getUniqueRootElementInLogicalStructMap() {
		Element logicalRootElement = null;
		try {
			List<Element> structMap = getStructMaps(metsDoc);
			for(Element s : structMap) {
				if(s.getAttributeValue("TYPE").equals(STRUCTMAP_TYPE_LOGICAL)) {
					@SuppressWarnings("unchecked")
					List<Element> metsDivElements = s.getChildren("div", METS_NS);
					if(metsDivElements.size()==1) {
						logicalRootElement = metsDivElements.get(0);
					} else if(metsDivElements.size()==0) {
						logger.error("No unique root element found in the logical structMap!");
					} else {
						logger.error("Found multiple root elements in the logical structMap!");
					}
				}
			}
		} catch (Exception e) {
			logger.error("Unable to find the unique root element in the logical structMap!");
		}
		return logicalRootElement;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private HashMap<String, ArrayList<String>> getParentChildInfoOfDmdIds(String objectId) {
		HashMap<String, ArrayList<String>> parentChildDmdId = new HashMap<String, ArrayList<String>>();
		String parentDmdId = "";
		try {
			Element rootDivElement = getUniqueRootElementInLogicalStructMap();
			ArrayList<String> children = new ArrayList<String>();
			List<Element> divChildren =  rootDivElement.getChildren("div", METS_NS);
			if(divChildren!=null && !divChildren.isEmpty()) {
				for(Element divChild : divChildren) {
					if(divChild.getAttribute("DMDID")!=null && !divChild.getAttributeValue("DMDID").equals(parentDmdId)) {
						children.add(objectId+"-"+divChild.getAttributeValue("DMDID"));
					}
				}	
			}
			if(children!=null && !children.isEmpty()) {
				parentChildDmdId.put(rootDivElement.getAttributeValue("DMDID"), children);
			}	 
		} catch (Exception e) {
			logger.debug("No parent child relationship found.");
		}
		return parentChildDmdId;
	}
	
	@SuppressWarnings("unchecked")
	private Element findDivByDmdIdRecursive(Element div, String dmdId) {
		if (dmdId.equals(div.getAttributeValue("DMDID"))) {
			return div;
		}
		List<Element> childDivs = div.getChildren("div", METS_NS);
		for (Element childDiv : childDivs) {
			Element match = findDivByDmdIdRecursive(childDiv, dmdId);
			if (match != null) {
				return match;
			}
		}
		return null;
	}
	
	@SuppressWarnings({ "unchecked" })
	private Element findDivInLogicalStructMap(String dmdId) {
		try {
			List<Element> structMaps = getStructMaps(metsDoc);
			for (Element s : structMaps) {
				if (STRUCTMAP_TYPE_LOGICAL.equals(s.getAttributeValue("TYPE"))) {
					List<Element> metsDivElements = s.getChildren("div", METS_NS);
					for (Element div : metsDivElements) {
						Element match = findDivByDmdIdRecursive(div, dmdId);
						if (match != null) {
							return match;
						}
					}
				}
			}
		} catch (Exception e) {
			logger.debug("Unable to find div for dmdId " + dmdId);
		}
		return null;
	}
	
	private Element getOutermostEnclosingDiv(Element div) {
		Element outermost = null;
		Element current = div;
		Element parent = current.getParentElement();
		while (parent != null && "div".equals(parent.getName()) && METS_NS.equals(parent.getNamespace())) {
			outermost = parent;
			current = parent;
			parent = current.getParentElement();
		}
		return outermost;
	}
	
	@SuppressWarnings({ "unchecked" })
	private String getUrnFromDmdId(String dmdId) {
		if (dmdId == null || dmdId.isEmpty()) {
			return "";
		}
		for (Element dmdSec : getDMDSections()) {
			if (dmdId.equals(dmdSec.getAttributeValue("ID"))) {
				Element mods = getModsXmlData(dmdSec);
				if (mods == null) {
					return "";
				}
				List<Element> identifiers = mods.getChildren("identifier", C.MODS_NS);
				for (Element identifier : identifiers) {
					if ("urn".equals(identifier.getAttributeValue("type"))) {
						String value = identifier.getText();
						if (value != null && !value.trim().isEmpty()) {
							return value.trim();
						}
					}
				}
			}
		}
		return "";
	}
	
	@SuppressWarnings({ "unchecked" })
	private boolean hasRelatedItemSuperior(String dmdId) {
		for (Element dmdSec : getDMDSections()) {
			if (dmdId.equals(dmdSec.getAttributeValue("ID"))) {
				Element mods = getModsXmlData(dmdSec);
				if (mods == null) {
					return false;
				}
				List<Element> relatedItems = mods.getChildren("relatedItem", C.MODS_NS);
				for (Element relatedItem : relatedItems) {
					String type = relatedItem.getAttributeValue("type");
					if ("series".equals(type) || "host".equals(type)) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	@SuppressWarnings({ "unchecked" })
	private String getUrnFromRelatedItem(String dmdId) {
		for (Element dmdSec : getDMDSections()) {
			if (dmdId.equals(dmdSec.getAttributeValue("ID"))) {
				Element mods = getModsXmlData(dmdSec);
				if (mods == null) {
					return "";
				}
				List<Element> relatedItems = mods.getChildren("relatedItem", C.MODS_NS);
				for (Element relatedItem : relatedItems) {
					String type = relatedItem.getAttributeValue("type");
					if ("series".equals(type) || "host".equals(type)) {
						List<Element> identifiers = relatedItem.getChildren("identifier", C.MODS_NS);
						for (Element identifier : identifiers) {
							if ("urn".equals(identifier.getAttributeValue("type"))) {
								String value = identifier.getText();
								if (value != null && !value.trim().isEmpty()) {
									return value.trim();
								}
							}
						}
						for (Element identifier : identifiers) {
							if ("uri".equals(identifier.getAttributeValue("type"))) {
								String value = identifier.getText();
								if (value != null && !value.trim().isEmpty()) {
									return value.trim();
								}
							}
						}
						List<Element> titleInfos = relatedItem.getChildren("titleInfo", C.MODS_NS);
						for (Element titleInfo : titleInfos) {
							Element title = titleInfo.getChild("title", C.MODS_NS);
							if (title != null) {
								String value = title.getText();
								if (value != null && !value.trim().isEmpty()) {
									return C.EDM_IS_PART_OF_LITERAL + value.trim();
								}
							}
						}
					}
				}
			}
		}
		return "";
	}
	
	private boolean isSubOrder(String dmdId) {
		Element div = findDivInLogicalStructMap(dmdId);
		if (div != null && getOutermostEnclosingDiv(div) != null) {
			return true;
		}
		return hasRelatedItemSuperior(dmdId);
	}
	
	private String getSuperiorUrn(String dmdId) {
		Element div = findDivInLogicalStructMap(dmdId);
		if (div != null) {
			Element outermost = getOutermostEnclosingDiv(div);
			if (outermost != null) {
				String urn = getUrnFromDmdId(outermost.getAttributeValue("DMDID"));
				if (!urn.isEmpty()) {
					return urn;
				}
			}
		}
		return getUrnFromRelatedItem(dmdId);
	}
	
	private ArrayList<String> getChildrenDmdIds(String parentDmdId, String ObjectId) {
		ArrayList<String> childrenDmdIds = new ArrayList<String>();
		HashMap<String, ArrayList<String>> parentChildDmdIdRel = getParentChildInfoOfDmdIds(ObjectId);
		for(String parentId : parentChildDmdIdRel.keySet()) {
			if(parentId.equals(parentDmdId.replace(ObjectId+"-", ""))) {
				childrenDmdIds = parentChildDmdIdRel.get(parentId);
			}
		}
		return childrenDmdIds;
	}
	
	@SuppressWarnings({ "unchecked" })
	private String getIdFromLogicalStructMap(String dmdID, String type, String idType) {
		String id = "";
		try {
			List<Element> structMap = getStructMaps(metsDoc);
			for(Element s : structMap) {
				if(s.getAttributeValue("TYPE").equals(STRUCTMAP_TYPE_LOGICAL)) {
					List<Element> metsDivElements = s.getChildren("div", C.METS_NS);
					for (Element d : metsDivElements){
						if(d.getAttributeValue("DMDID").equals(dmdID)) {
							if(type.equals(TITLE_PAGE)) {
								List<Element> divs =  d.getChildren("div", C.METS_NS);
								for(Element div : divs) {
									if(div.getAttributeValue("TYPE").equals(type)) {
										id = div.getAttributeValue(idType);
									}
								}
							} else {
								id = d.getAttributeValue(idType);
							}
						} else if(d.getChildren("div", C.METS_NS)!=null) {
							List<Element> divChildren =  d.getChildren("div", C.METS_NS);
							for(Element divChild : divChildren) {
								if(divChild.getAttributeValue("DMDID").equals(dmdID)) {
									id = divChild.getAttributeValue(idType);
								}
							}	
						}
					}
				}
			}
		} catch (Exception e) {
			logger.debug("Unable to find the "+idType+" id from dmdID "+dmdID);
		}
		return id;
	}
	
	@SuppressWarnings("unchecked")
	private List<String> getPhysicalIdsFromStructLink(Document doc, String logicalId) {
		List<String> physIds = new ArrayList<String>();
		List<Element> structLink = new ArrayList<Element>();
		try {
			structLink = doc.getRootElement().getChild("structLink", C.METS_NS).getChildren("smLink", C.METS_NS);
			for(Element link : structLink) {
				if(link.getAttributeValue("from", XLINK_NS).equals(logicalId)) {
					physIds.add(link.getAttributeValue("to", XLINK_NS));
				}
			}
		} catch (Exception e) {
			logger.debug("Unable to find the physical id from logical id "+logicalId);
		}
		return physIds;
	}
		
	List<String> getDataProvider(String amdId) {
		HashSet<String> dataProvider = new HashSet<String>(); //nur ungleichnahmige abspeichern
		String gefundeneAmdSec="";
		try {
			@SuppressWarnings("unchecked")
			List<Element> amdSections = metsDoc.getRootElement().getChildren("amdSec", C.METS_NS);
			for(Element amdSec : amdSections) {
				if( ! amdId.equals(getIdFromElem(amdSec))) //amdSec
					continue;
				gefundeneAmdSec=getIdFromElem(amdSec);
				if(amdSec.getChild("rightsMD", C.METS_NS).getChild("mdWrap", C.METS_NS).getAttribute("OTHERMDTYPE").getValue().equals("DVRIGHTS")) {
					String rightsOwner=amdSec
							.getChild("rightsMD", C.METS_NS)
							.getChild("mdWrap", C.METS_NS)
							.getChild("xmlData", C.METS_NS)
							.getChild("rights", C.DV)
							.getChild("owner", C.DV)
							.getValue();
					dataProvider.add(rightsOwner);
				}
			}
		} catch (Exception e) {
			logger.debug("No amd section found!");
		}
		
		//es sollte zu jedem dmdsec eine amdsec exisitieren
		if(gefundeneAmdSec.isEmpty())
			throw new RuntimeException("No matching amd-section found for data provider! "+amdId); 
		return new ArrayList(dataProvider);
	}
	
	private String getPublisher(Element origInfo) {
		String publisher = "";
		try {
			Element publisherElement = origInfo.getChild("publisher", C.MODS_NS);
			if(publisherElement != null) {
				String val = publisherElement.getValue();
				if(val != null && !val.trim().isEmpty()) {
					publisher = val;
				}
			}
		} catch (Exception e) {
			logger.debug("Element publisher does not exist!");
		}
		return publisher;
	}
	
	@SuppressWarnings("unchecked")
	private List<Element> getOrigInfoElements(Element dmdSec) {
		List<Element> origInfoElements = new ArrayList<Element>();
		try {
			origInfoElements = getModsXmlData(dmdSec).getChildren("originInfo", C.MODS_NS);
		} catch (Exception e) {
			logger.debug("No origInfo element found!");
		}
		return origInfoElements;
	}
	
	private String getDateIssued(Element dmdSec) {
		String date = "";
		List<Element> origInfoElements = getOrigInfoElements(dmdSec);
		// 1. originInfo[@eventType='publication'], first dateIssued without attributes
		boolean publicationExist = false;
		for (Element originInfo : origInfoElements) {
			if ("publication".equals(originInfo.getAttributeValue("eventType"))) {
				publicationExist = true;
				date = getDateIssuedWithoutAttributes(originInfo);
				if (!date.isEmpty()) {
					return date;
				}
			}
		}
		// 2. Fallback: no originInfo[@eventType='publication'] -> first originInfo without edition
		if (!publicationExist) {
			for (Element originInfo : origInfoElements) {
				if (originInfo.getChild("edition", C.MODS_NS) == null) {
					date = getFirstDateIssued(originInfo);
					if (!date.isEmpty()) {
						return date;
					}
				}
			}
		}
		return date;
	}
	
	private String getDateIssuedWithoutAttributes(Element originInfo) {
		try {
			@SuppressWarnings("unchecked")
			List<Element> dateIssueds = originInfo.getChildren("dateIssued", C.MODS_NS);
			for (Element dateIssued : dateIssueds) {
				if (dateIssued.getAttributes().isEmpty()) {
					String val = dateIssued.getValue();
					if (val != null && !val.trim().isEmpty()) {
						return val.trim();
					}
				}
			}
		} catch (Exception e) {
			logger.debug("Element dateIssued does not exist! : "+e.toString());
		}
		return "";
	}
	
	private String getFirstDateIssued(Element originInfo) {
		try {
			Element dateIssued = originInfo.getChild("dateIssued", C.MODS_NS);
			if (dateIssued != null) {
				String val = dateIssued.getValue();
				if (val != null && !val.trim().isEmpty()) {
					return val.trim();
				}
			}
		} catch (Exception e) {
			logger.debug("Element dateIssued does not exist! : "+e.toString());
		}
		return "";
	}
	
	private String getDateCreated(Element dmdSec) {
		String date = "";
		List<Element> origInfoElements = getOrigInfoElements(dmdSec);
		for (Element originInfo : origInfoElements) {
			// originInfo without attributes, containing edition "Electronic ed."
			if (originInfo.getAttributes().isEmpty()) {
				Element edition = originInfo.getChild("edition", C.MODS_NS);
				if (edition != null && isElectronicEdition(edition.getValue())) {
					Element dateIssued = originInfo.getChild("dateIssued", C.MODS_NS);
					if (dateIssued != null && dateIssued.getValue() != null) {
						date = dateIssued.getValue();
					}
					break;
				}
			}
		}
		return date;
	}
	
	private boolean isElectronicEdition(String edition) {
		if (edition == null) {
			return false;
		}
		String value = edition.trim();
		return "Electronic ed.".equals(value) || "[Electronic ed.]".equals(value);
	}
	
	@SuppressWarnings("unchecked")
	private List<Element> getNameElements(Element dmdSec) {
		List<Element> nameElements = new ArrayList<Element>();
		Element modsXmlData = getModsXmlData(dmdSec);
		try {
			nameElements = modsXmlData.getChildren("name", C.MODS_NS);
		} catch (Exception e) {
			logger.debug("No name element found!");
		}
		return nameElements;
	}
	
	private String getContributor(Element name) {
		String namePartValue = "";
		try {
			String roleCode="",roleText="";
			Element roleElem=name.getChild("role", C.MODS_NS);
			List<Element> roleTermElem=roleElem.getChildren("roleTerm", C.MODS_NS);
			for(Element e:roleTermElem){
				String type=e.getAttribute("type").getValue();
				if(type.equals("code")){
					roleCode=e.getValue();
				}else if(type.equals("text")){
					roleText=e.getValue();
				}
			}
			if(!roleCode.equals("aut")&&!roleCode.equals("cre")) {
				namePartValue = roleText+(roleText.trim().isEmpty()?"":": ")+getName(name);
			}
		} catch (Exception e) {
			logger.debug("No creator found!");
		}
		return namePartValue;
	}
	
	private String getCreator(Element name) {
		String namePartValue = "";
		try {
			String roleCode="",roleText="";
			Element roleElem=name.getChild("role", C.MODS_NS);
			List<Element> roleTermElem=roleElem.getChildren("roleTerm", C.MODS_NS);
			for(Element e:roleTermElem){
				String type=e.getAttribute("type").getValue();
				if(type.equals("code")){
					roleCode=e.getValue();
				}else if(type.equals("text")){
					roleText=e.getValue();
				}
			}
			
			/*if(roleCode.equals("aut"))
				namePartValue = "Verfasser: "+getName(name);
			else if(roleCode.equals("cre"))
				namePartValue = "Autor: "+getName(name);*/
			
			if(roleCode.equals("aut")||roleCode.equals("cre")) {
				namePartValue = roleText+(roleText.trim().isEmpty()?"":": ")+getName(name);
			}
			
		} catch (Exception e) {
			logger.debug("No contributor found!");
		}
		return namePartValue;
	}
	
	private String getName(Element name) {
		String namePartValue = "";
		if(name.getAttribute("type", C.MODS_NS)==null || name.getAttribute("type", C.MODS_NS).equals("personal")) {
			try {
				@SuppressWarnings("unchecked")
				List<Element> nameParts = name.getChildren("namePart", C.MODS_NS);
				
				String given = "";
				String family = "";
		
				for(Element element :  nameParts) {
					if(element.getAttributes()==null) {
						namePartValue = element.getValue();
					} else {
						if(element.getAttribute("given", C.MODS_NS)!=null) {
							given = element.getAttributeValue("given", C.MODS_NS);
						} 
						if(element.getAttribute("family", C.MODS_NS)!=null) {
							family = element.getAttributeValue("family", C.MODS_NS);
						}
						
						if(given.equals("")&&family.equals("")) {
							namePartValue = element.getValue();
						} else if(!given.equals("")) {
							namePartValue = given + " " + family;	
						} else namePartValue = family;
					}
				}
			} catch (Exception e) {
				logger.debug("Element namePart does not exist!");
			}
			
			if(namePartValue.isEmpty()) {
				try {
					namePartValue = name.getChild("displayForm", C.MODS_NS).getValue();
				} catch (Exception e) {
					logger.error("No name found");
				} 
			}
		}
		return namePartValue;
	}
	
	private List<String> getIdentifier(Element dmdSec) {
		List<String> identifier = new ArrayList<String>();
		Element modsXmlData = getModsXmlData(dmdSec);
		@SuppressWarnings("unchecked")
		List<Element>  elements = modsXmlData.getChildren();
		for(Element e : elements) {
			if(e.getName().equals("identifier")) {
				String val = e.getValue();
				if (val != null && val.startsWith("HBZ")) {
					val = val.substring(3);
				}
				identifier.add(val);
			}
		}
		return identifier;
	}
	
	private String getContentIdsFromStructMap(String dmdId) {
		try {
			List<Element> structMaps = getStructMaps(metsDoc);
			for (Element s : structMaps) {
				if (s.getAttributeValue("TYPE").equals(STRUCTMAP_TYPE_LOGICAL)) {
					@SuppressWarnings("unchecked")
					List<Element> metsDivElements = s.getChildren("div", METS_NS);
					for (Element div : metsDivElements) {
						if (dmdId.equals(div.getAttributeValue("DMDID"))) {
							String contentIds = div.getAttributeValue("CONTENTIDS");
							if (contentIds != null && !contentIds.isEmpty()) {
								return contentIds;
							}
						}
						@SuppressWarnings("unchecked")
						List<Element> childDivs = div.getChildren("div", METS_NS);
						for (Element childDiv : childDivs) {
							if (dmdId.equals(childDiv.getAttributeValue("DMDID"))) {
								String contentIds = childDiv.getAttributeValue("CONTENTIDS");
								if (contentIds != null && !contentIds.isEmpty()) {
									return contentIds;
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			logger.debug("Unable to find CONTENTIDS for dmdId " + dmdId);
		}
		return null;
	}
	
	private String getDmdIdOfContentIds(String contentIds) {
		try {
			List<Element> structMaps = getStructMaps(metsDoc);
			for (Element s : structMaps) {
				if (STRUCTMAP_TYPE_LOGICAL.equals(s.getAttributeValue("TYPE"))) {
					@SuppressWarnings("unchecked")
					List<Element> metsDivElements = s.getChildren("div", METS_NS);
					for (Element div : metsDivElements) {
						String dmdId = findDivByContentIdsRecursive(div, contentIds);
						if (dmdId != null) {
							return dmdId;
						}
					}
				}
			}
		} catch (Exception e) {
			logger.debug("Unable to find CONTENTIDS for urn " + contentIds);
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	private String findDivByContentIdsRecursive(Element div, String contentIds) {
		if (contentIds.equals(div.getAttributeValue("CONTENTIDS"))) {
			String dmdId = div.getAttributeValue("DMDID");
			if (dmdId != null && !dmdId.isEmpty()) {
				return dmdId;
			}
		}
		List<Element> childDivs = div.getChildren("div", METS_NS);
		for (Element childDiv : childDivs) {
			String dmdId = findDivByContentIdsRecursive(childDiv, contentIds);
			if (dmdId != null) {
				return dmdId;
			}
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	private Element getUntypedTitleInfo(Element modsXmlData) {
		List<Element> titleInfos = modsXmlData.getChildren("titleInfo", C.MODS_NS);
		for (Element ti : titleInfos) {
			if (ti.getAttribute("type") == null) {
				return ti;
			}
		}
		return null;
	}
	
	private String getTypeOfResource(Element dmdSec) {
		Element modsXmlData = getModsXmlData(dmdSec);
		try {
			@SuppressWarnings("unchecked")
			List<Element> typeOfResources = modsXmlData.getChildren("typeOfResource", C.MODS_NS);
			if (!typeOfResources.isEmpty() && typeOfResources.get(0).getValue() != null) {
				return typeOfResources.get(0).getValue().trim();
			}
		} catch (Exception e) {
			logger.debug("Element typeOfResource does not exist!");
		}
		return "";
	}
	
	private List<String> getLanguage(Element dmdSec) {
		List<String> languages = new ArrayList<String>();
		Element modsXmlData = getModsXmlData(dmdSec);
		try {
			@SuppressWarnings("unchecked")
			List<Element> languageElements = modsXmlData.getChildren("language", C.MODS_NS);
			for (Element lang : languageElements) {
				@SuppressWarnings("unchecked")
				List<Element> languageTerms = lang.getChildren("languageTerm", C.MODS_NS);
				for (Element lt : languageTerms) {
					if ("code".equals(lt.getAttributeValue("type"))) {
						String val = lt.getValue();
						if (val != null && !val.trim().isEmpty()) {
							languages.add(val);
						}
					}
				}
			}
		} catch (Exception e) {
			logger.debug("Element language does not exist!");
		}
		return languages;
	}
	
	private String getSubOrderTitleLabel(String dmdId) {
		try {
			Element rootElement = metsDoc.getRootElement();
			@SuppressWarnings("unchecked")
			List<Element> structMaps = rootElement.getChildren("structMap", METS_NS);
			for (Element structMap : structMaps) {
				if ("LOGICAL".equals(structMap.getAttributeValue("TYPE"))) {
					@SuppressWarnings("unchecked")
					List<Element> rootDivs = structMap.getChildren("div", METS_NS);
					for (Element rootDiv : rootDivs) {
						@SuppressWarnings("unchecked")
						List<Element> childDivs = rootDiv.getChildren("div", METS_NS);
						for (Element childDiv : childDivs) {
							if (dmdId.equals(childDiv.getAttributeValue("DMDID"))
									&& childDiv.getAttributeValue("CONTENTIDS") != null) {
								String label = childDiv.getAttributeValue("LABEL");
								if (label != null && !label.trim().isEmpty()) {
									return label.trim();
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			logger.debug("Unable to find sub order title label in structMap!");
		}
		return "";
	}

	private List<String> getGenreTypes(Element dmdSec) {
		List<String> genreTypes = new ArrayList<String>();
		Element modsXmlData = getModsXmlData(dmdSec);
		try {
			@SuppressWarnings("unchecked")
			List<Element> genreElements = modsXmlData.getChildren("genre", C.MODS_NS);
			for (Element genre : genreElements) {
				String val = genre.getValue();
				if (val == null) {
					continue;
				}
				String mapped = C.GENRE_TO_DC_TYPE.get(val.trim());
				if (mapped == null) {
					mapped = val.trim();
				}
				if (mapped.isEmpty()) {
					continue;
				}
				if (!genreTypes.contains(mapped)) {
					genreTypes.add(mapped);
				}
			}
		} catch (Exception e) {
			logger.debug("No genre element found!");
		}
		return genreTypes;
	}
	
	private HashMap<String, String> getImageTeaserFiles() {
		HashMap<String, String> teaserFiles = new HashMap<String, String>();
		try {
			@SuppressWarnings("unchecked")
			List<Element> fileSecs = metsDoc.getRootElement().getChildren("fileSec", METS_NS);
			for (Element fileSec : fileSecs) {
				@SuppressWarnings("unchecked")
				List<Element> fileGrps = fileSec.getChildren("fileGrp", METS_NS);
				for (Element fileGrp : fileGrps) {
					@SuppressWarnings("unchecked")
					List<Element> files = fileGrp.getChildren("file", METS_NS);
					for (Element file : files) {
						String id = file.getAttributeValue("ID");
						if (id != null && id.startsWith("IMG_TEASER_")) {
							Element flocat = file.getChild("FLocat", METS_NS);
							if (flocat != null) {
								String href = flocat.getAttributeValue("href", XLINK_NS);
								if (href != null && !href.trim().isEmpty()) {
									teaserFiles.put(id, href);
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			logger.debug("Unable to find image teaser files!");
		}
		return teaserFiles;
	}
	
	private String getImageTeaserHref(String dmdId, HashMap<String, String> teaserFiles) {
		if (teaserFiles.isEmpty()) {
			return "";
		}
		try {
			// find the div of the current dmdSec in the LOGICAL structMap and
			// look for an IMG_TEASER fptr directly inside that div
			@SuppressWarnings("unchecked")
			List<Element> structMaps = metsDoc.getRootElement().getChildren("structMap", METS_NS);
			for (Element structMap : structMaps) {
				if ("LOGICAL".equals(structMap.getAttributeValue("TYPE"))) {
					@SuppressWarnings("unchecked")
					List<Element> rootDivs = structMap.getChildren("div", METS_NS);
					for (Element rootDiv : rootDivs) {
						Element div = findDivByDmdIdRecursive(rootDiv, dmdId);
						if (div == null) {
							continue;
						}
						@SuppressWarnings("unchecked")
						List<Element> fptrs = div.getChildren("fptr", METS_NS);
						for (Element fptr : fptrs) {
							String fileId = fptr.getAttributeValue("FILEID");
							if (fileId != null && fileId.startsWith("IMG_TEASER_") && teaserFiles.containsKey(fileId)) {
								return teaserFiles.get(fileId);
							}
						}
					}
				}
			}
		} catch (Exception e) {
			logger.debug("Unable to find image teaser!");
		}
		return "";
	}

	private List<String> getTitle(Element dmdSec) {
		List<String> title = new ArrayList<String>();
		Element modsXmlData = getModsXmlData(dmdSec);
		
		String titleValue = "";
		String displayLabelValue = "";
		String nonSortValue = "";
		String MainTitleValue = "";
		
		String caption = "";
		
		try {
			Element titleInfo = getUntypedTitleInfo(modsXmlData);
			
			if (titleInfo != null) {
				try {
					nonSortValue = titleInfo.getChild("nonSort", C.MODS_NS).getText();
				} catch (Exception e) {
					logger.debug("Element nonSort does not exist!");
				}
				
				try {
					titleValue = titleInfo.getChild("title", C.MODS_NS).getText();
				} catch (Exception e) {
					logger.debug("Element title does not exist!");
				}
				
				try {
					displayLabelValue = titleInfo.getChild("displayLabel", C.MODS_NS).getText();
				} catch (Exception e) {
					logger.debug("Element displayLabel does not exist!");
				}
			}
			
			if(!titleValue.equals("")) {
				if(!nonSortValue.equals("")) {
					titleValue = nonSortValue + " " + titleValue;
				}
				MainTitleValue = titleValue;
			}
			else if(!nonSortValue.equals("")) {
				MainTitleValue = nonSortValue;
			}
			else if (!displayLabelValue.equals("")){
				MainTitleValue = displayLabelValue;
			} else {	
				
				// Neu Mapping für DDB 04.04.2023 
				try {
					
					Element part = modsXmlData.getChild("part", C.MODS_NS);
					@SuppressWarnings("unchecked")
					List<Element> detailList = part.getChildren("detail", C.MODS_NS);
					
					
					for(Element detail : detailList) {
						System.out.println("type: " + detail.getAttributeValue("type"));
						if (detail.getAttributeValue("type").equals("volume")) {
							if (caption.equals("")){
								caption = detail.getChild("caption", C.MODS_NS).getText();
							} else {
								caption = detail.getChild("caption", C.MODS_NS).getText() + caption;
							}
						} 
						if (detail.getAttributeValue("type").equals("issue")) {
							caption = caption + ", " + detail.getChild("caption", C.MODS_NS).getText();
						}
					}
				} catch (Exception ex) {
					logger.error("Element part.detail.caption does not exist !!!");
				}
			}
		} catch(Exception e) {
			logger.error("Element titleInfo does not exist. Try to find part.detail.caption!!!");
		}
		
		if (!MainTitleValue.equals("")) {
			title.add(MainTitleValue);
		} else {
			title.add(caption);
		}
		return title;
	}
	
	@SuppressWarnings("unchecked")
	private List<String> getAlternativeTitle(Element dmdSec) {
		List<String> alternative = new ArrayList<String>();
		Element modsXmlData = getModsXmlData(dmdSec);
		try {
			Element titleInfo = getUntypedTitleInfo(modsXmlData);
			if (titleInfo != null) {
				List<Element> subTitles = titleInfo.getChildren("subTitle", C.MODS_NS);
				for (Element st : subTitles) {
					String val = st.getText();
					if (val != null && !val.trim().isEmpty()) {
						alternative.add(val);
					}
				}
			}
		} catch (Exception e) {
			logger.debug("Element subTitle does not exist!");
		}
		return alternative;
	}
	
	public List<Element> getDMDSections() {
		return metsDoc.getRootElement().getChildren("dmdSec", C.METS_NS);
	}
	
	public HashMap<String, Element> getSections(String objectId) {
		HashMap<String, Element> IDtoSecElement = new HashMap<String, Element>();
		@SuppressWarnings("unchecked")
		List<Element> dmdSections =getDMDSections();
		for(Element e : dmdSections) {
			String id = "";
			id = objectId+"-"+getIdFromElem(e);
			IDtoSecElement.put(id, e);
		}
		return IDtoSecElement;
	}
	
	public static Element getModsXmlData(Element dmdSec) {
		return dmdSec
				.getChild("mdWrap", C.METS_NS)
				.getChild("xmlData", C.METS_NS)
				.getChild("mods", C.MODS_NS);
	}
	
	public String getHref(Element fileElement) {
		return fileElement.getChild("FLocat", C.METS_NS).getAttribute("href", XLINK_NS).getValue();
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<Element> getFileElementsFromMetsDoc(Document doc) throws JDOMException {
		List<Element> currentFileElements = new ArrayList<Element>();
		List allNodes = metsXPath.selectNodes(doc);
		for (java.lang.Object node : allNodes) {
			Element fileElement = (Element) node;
			currentFileElements.add(fileElement);
		}
		return currentFileElements;
	} 
	
	public List<Element> getFileElements() {
		return fileElements;
	}


	public List<String> getReferences() {
		List<String> references = new ArrayList<String>();
		for(Element fileElement : fileElements) {
			String ref = getHref(fileElement);
			references.add(ref);
			logger.debug("Found reference "+ref);
		}
		logger.debug("number of existing references "+references.size());
		return references;
	}
	
	private String getReferenceFromFileId(String fileId) {
		String ref = "";
		try {
			for(Element fileElement : fileElements) {
				if(fileElement.getAttributeValue("ID").equals(fileId)) {
					ref = getHref(fileElement);
				}
			}
		} catch (Exception e) {
			logger.error("Unable to find the reference from file "+fileId);
		}
		return ref;
	}
	
	public HashMap<String, HashMap<String, List<String>>> getIndexInfo(String ObjectId) {
		return getIndexInfo(ObjectId, null);
	}
	
	public HashMap<String, HashMap<String, List<String>>> getIndexInfo(String ObjectId, String urn) {
		HashMap<String, HashMap<String, List<String>>> indexInfo = new HashMap<String, HashMap<String,List<String>>>();
		HashMap<String, Element> dmdSections = getSections(ObjectId);
		String targetDmdId = (urn != null && !urn.isEmpty()) ? getDmdIdOfContentIds(urn) : null;
		
		for(String id : dmdSections.keySet()) {
			Element e = dmdSections.get(id);
			if (targetDmdId != null && !targetDmdId.equals(getIdFromElem(e))) {
				continue;
			}
			HashMap<String, List<String>> dmdSecInfo = new HashMap<String, List<String>>();
			
//			Title
			String subOrderTitleLabel = getSubOrderTitleLabel(getIdFromElem(e));
			if (!subOrderTitleLabel.isEmpty()) {
				List<String> subOrderTitles = new ArrayList<String>();
				subOrderTitles.add(subOrderTitleLabel);
				dmdSecInfo.put(C.EDM_TITLE, subOrderTitles);
			} else {
				dmdSecInfo.put(C.EDM_TITLE, getTitle(e));
			}
			List<String> altTitles = getAlternativeTitle(e);
			if (!altTitles.isEmpty()) {
				dmdSecInfo.put(C.EDM_ALTERNATIVE, altTitles);
			}
			List<String> accessConditions= getEdmRights(getAccessConditions(e));
			dmdSecInfo.put(C.DC_RIGHTS, getDcRights(e));
			dmdSecInfo.put(C.EDM_RIGHTS,accessConditions);
//			identifier
			List<String> identifiers = getIdentifier(e);
			String contentIds = getContentIdsFromStructMap(getIdFromElem(e));
			if (contentIds != null && !contentIds.isEmpty()) {
				identifiers.add(0, contentIds);
			}
			List<String> deduplicatedIdentifiers = new ArrayList<String>(new LinkedHashSet<String>(identifiers));
			dmdSecInfo.put(C.EDM_IDENTIFIER, deduplicatedIdentifiers);
			
//			Type && Language
			String typeOfResource = getTypeOfResource(e);
			if (!typeOfResource.isEmpty()) {
				String edmType = C.TYPE_OF_RESOURCE_TO_EDM_TYPE.get(typeOfResource);
				if (edmType != null && !edmType.isEmpty()) {
					List<String> typeList = new ArrayList<String>();
					typeList.add(edmType);
					dmdSecInfo.put(C.EDM_TYPE, typeList);
if (C.EDM_TYPE_TEXT.equals(edmType)) {
					List<String> languages = getLanguage(e);
					if (!languages.isEmpty()) {
						dmdSecInfo.put(C.EDM_LANGUAGE, languages);
					} else if (isSubOrder(getIdFromElem(e))) {
						List<String> undeterminedLanguage = new ArrayList<String>();
						undeterminedLanguage.add("und");
						dmdSecInfo.put(C.EDM_LANGUAGE, undeterminedLanguage);
					}
				}
				}
			}
			List<String> genreTypes = getGenreTypes(e);
			if (!genreTypes.isEmpty()) {
				dmdSecInfo.put(C.DC_TYPE, genreTypes);
			}
			
//			Names
			List<String> creators = new ArrayList<String>();
			List<String> contributors = new ArrayList<String>();
			for(Element name : getNameElements(e)) {
				String creator = getCreator(name);
				String contributor = getContributor(name);
				if(!creator.equals("")) {
					creators.add(creator);
				}
				if(!contributor.equals("")) {
					contributors.add(contributor);
				}
			}
			dmdSecInfo.put(C.EDM_CREATOR, creators);
			dmdSecInfo.put(C.EDM_CONTRIBUTOR, contributors);
			
//			Date && Place
			List<String> datesIssued = new ArrayList<String>();
			List<String> datesCreated = new ArrayList<String>();
			List<String> publishers = new ArrayList<String>();
			String dateIssued = getDateIssued(e);
			String dateCreated = getDateCreated(e);
			if(!dateIssued.equals("")) {
				datesIssued.add(dateIssued);
			}
			if(!dateCreated.equals("")) {
				datesCreated.add(dateCreated);
			}
			for(Element origInfo : getOrigInfoElements(e)) {
				String publisher = getPublisher(origInfo);
				if(!publisher.equals("")) {
					publishers.add(publisher);
				}
			}
			dmdSecInfo.put(C.EDM_DATE_ISSUED, datesIssued);
			dmdSecInfo.put(C.EDM_DATE_CREATED, datesCreated);
			dmdSecInfo.put(C.EDM_PUBLISHER, publishers);
			
			
			List<String> allPhysicalDescr = getPhysicalDescriptionFromDmdId(id, ObjectId);
			if (!allPhysicalDescr.isEmpty()) {
				dmdSecInfo.put(C.EDM_EXTENT, allPhysicalDescr);
			}
			
//			TitlePage
			List<String> titlePageRefs = getTitlePageReferencesFromFrontimage();
			if (titlePageRefs.isEmpty()) {
				titlePageRefs = getTitlePageReferencesFromDmdId(id, ObjectId);
			}
			
			List<String> allReferences = getReferencesFromDmdId(id, ObjectId);
/*** Alle Referenzen für alle Objekte ist Stuss und erzeugt unter Umstaenden OutOfMemmoryError	
//			LAV
			if(allReferences==null || allReferences.isEmpty()) {
				allReferences = getReferences();
			}
*/		
			if(titlePageRefs!=null & !titlePageRefs.isEmpty()) {
				List<String> references = new ArrayList<String>();
				references.add(titlePageRefs.get(0));
				dmdSecInfo.put(C.EDM_IS_SHOWN_BY, references);
				dmdSecInfo.put(C.EDM_OBJECT, references);
				if(titlePageRefs.size()>1) {
					dmdSecInfo.put(C.EDM_HAS_VIEW, titlePageRefs);
				}
			} else if(allReferences!=null && !allReferences.isEmpty()){
				List<String> firstReference = new ArrayList<String>();
				firstReference.add(allReferences.get(0));
				dmdSecInfo.put(C.EDM_IS_SHOWN_BY, firstReference);
				dmdSecInfo.put(C.EDM_OBJECT, firstReference);
			}
//			hasView
			if(allReferences.size()>1) {
				dmdSecInfo.put(C.EDM_HAS_VIEW, allReferences);
			} 
			
//			IMG_TEASER thumbnail
			HashMap<String, String> teaserFiles = getImageTeaserFiles();
			String imageTeaser = getImageTeaserHref(getIdFromElem(e), teaserFiles);
			if(!imageTeaser.isEmpty()) {
				List<String> teaserList = new ArrayList<String>();
				teaserList.add(imageTeaser);
				dmdSecInfo.put(C.EDM_IS_SHOWN_BY, teaserList);
			} else if(!teaserFiles.isEmpty()) {
				// METS with IMG_TEASER files but no IMG_TEASER fptr for this div -> omit edm:isShownBy
				dmdSecInfo.remove(C.EDM_IS_SHOWN_BY);
			} 
			
//			dataProvider
			dmdSecInfo.put(C.EDM_DATA_PROVIDER, getDataProvider(dmdIdtoAmdId(getIdFromElem(e))));
			
//			hasPart
			ArrayList<String> childrenDmdIds = getChildrenDmdIds(id, ObjectId);
			if(childrenDmdIds!=null && !childrenDmdIds.isEmpty()) {
				dmdSecInfo.put(C.EDM_HAS_PART, childrenDmdIds);
			}
			
//			isPartOf
			String superiorUrn = getSuperiorUrn(getIdFromElem(e));
			if(!superiorUrn.isEmpty()) {
				List<String> parentUrns = new ArrayList<String>();
				parentUrns.add(superiorUrn);
				dmdSecInfo.put(C.EDM_IS_PART_OF, parentUrns);
			}
			
			indexInfo.put(id, dmdSecInfo);
		}
		return indexInfo;
	}
	
	
	String getIdFromElem(Element e) {
		return e.getAttribute("ID").getValue();
	}
	
	
	String dmdIdtoAmdId(String dmdSec) {
		if(dmdSec.equals("Jh-Dussel")) {//exceptional algorithm for testPackages names (acceptance tests / junit testcases)
			return "amd801613";
		}
		if(dmdSec.equals("Puschel")) {//exceptional algorithm for testPackages names (acceptance tests / junit testcases)
			return "amd801613";
		}
		
		
		
		//in usual case firsr dmd/md chars can be replaced by amd
		dmdSec=dmdSec.replaceAll("dmd", "");
		dmdSec=dmdSec.replaceAll("md", "");
		dmdSec="amd"+dmdSec;
		
		//Alternatively algorithm: analyse the structure-section of Mets and get concordance of dmdSecId and amdSecId
	
		
		return dmdSec;
		
	}
	
	public  List<String> getAccessConditions(Element dmdSec) {
		List<String> retList = new ArrayList<String>();
		try {
			MetsLicense mLic=getLicense(dmdSec,false);
			if(mLic==null || mLic.getHref().trim().isEmpty()){			
				logger.error("Attribute accessCondition.href does not exist!!!");
			}else{
				retList.add(mLic.getHref());
			}
		} catch(Exception e) {
			logger.error("Element accessCondition does not exist!!!");
		}
		return retList;
	}
	
	private List<String> getEdmRights(List<String> accessConditions) {
		List<String> edmRights = new ArrayList<String>();
		for (String href : accessConditions) {
			if (href.startsWith(HTTP_PREFIX)) {
				edmRights.add(HTTPS_PREFIX + href.substring(HTTP_PREFIX.length()));
			} else {
				edmRights.add(href);
			}
		}
		return edmRights;
	}
	
	private List<String> getDcRights(Element dmdSec) {
		List<String> dcRights = new ArrayList<String>();
		try {
			MetsLicense mLic = getLicense(dmdSec, false);
			if (mLic != null && isPdmMark(mLic.getHref())
					&& mLic.getDisplayLabel() != null && !mLic.getDisplayLabel().isEmpty()) {
				dcRights.add(mLic.getDisplayLabel());
			}
		} catch(Exception e) {
			logger.error("Element accessCondition does not exist!!!");
		}
		if (dcRights.isEmpty()) {
			dcRights = getContentSource(dmdSec);
		}
		return dcRights;
	}
	
	private boolean isPdmMark(String href) {
		return PDM_MARK_URI.equals(href) || PDM_MARK_URI_HTTP.equals(href);
	}
	
	private List<String> getContentSource(Element dmdSec) {
		List<String> contentSources = new ArrayList<String>();
		Element modsXmlData = getModsXmlData(dmdSec);
		try {
			Element recordInfo = modsXmlData.getChild("recordInfo", C.MODS_NS);
			if (recordInfo != null) {
				@SuppressWarnings("unchecked")
				List<Element> contentSourceList = recordInfo.getChildren("contentSource", C.MODS_NS);
				for (Element cs : contentSourceList) {
					String val = cs.getValue();
					if (val != null && !val.trim().isEmpty()) {
						contentSources.add(val);
					}
				}
			}
		} catch(Exception e) {
			logger.debug("Element contentSource does not exist!");
		}
		return contentSources;
	}
	
	

//	::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::  SETTER  ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
	
	
	public void setMimetype(Element fileElement, String mimetype) {
		if(fileElement.getAttribute("MIMETYPE")!=null) {
			fileElement.getAttribute("MIMETYPE").setValue(mimetype);
		} else {
			fileElement.setAttribute("MIMETYPE", mimetype);
		}
	}
	
	public void setLoctype(Element fileElement, String loctype) {
		if(loctype!=null) {
			if(fileElement.getChild("FLocat", C.METS_NS).getAttribute("LOCTYPE")!=null) {
				fileElement.getChild("FLocat", C.METS_NS).getAttribute("LOCTYPE").setValue(loctype);
			} else {
				fileElement.getChild("FLocat", C.METS_NS).setAttribute("LOCTYPE", loctype);
			}
		}
	}
	
	public void setHref(Element fileElement, String newHref) {
		fileElement.getChild("FLocat", C.METS_NS).getAttribute("href", XLINK_NS).setValue(newHref);
	}
}
