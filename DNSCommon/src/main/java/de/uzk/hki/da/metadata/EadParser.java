package de.uzk.hki.da.metadata;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.xpath.XPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uzk.hki.da.utils.C;

public class EadParser {

	/** The logger. */
	public Logger logger = LoggerFactory
			.getLogger(EadParser.class);
	
	private Document eadDoc = new Document();
	
	private Namespace EAD_NS;
	
	public EadParser(Document doc) throws JDOMException {
		this.eadDoc = doc;
		EAD_NS = eadDoc.getRootElement().getNamespace();
		logger.debug("Setting namespace "+EAD_NS);
	}

	
//	::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::  GETTER  ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::

	public List<String> getReferences() throws JDOMException, IOException {	
		List<String> metsReferences = new ArrayList<String>();
	
		XPath xPath = XPath.newInstance(C.EAD_XPATH_EXPRESSION);
		
		@SuppressWarnings("rawtypes")
		List allNodes = xPath.selectNodes(eadDoc);
		
		for (Object node : allNodes) {
			Attribute attr = (Attribute) node;
			String href = attr.getValue();
			metsReferences.add(href);
		}
		return metsReferences;
	}
	
	@SuppressWarnings("unchecked")
	public HashMap<String, HashMap<String, List<String>>> getIndexInfo(String objectId) {
		
//		<ID<Attribut, Value>>
		HashMap<String, HashMap<String, List<String>>> indexInfo = new HashMap<String, HashMap<String,List<String>>>();
		
//		Root
		Element archdesc = eadDoc.getRootElement().getChild("archdesc", EAD_NS);

		Element archdescDid = archdesc.getChild("did", EAD_NS);
		HashMap<String, List<String>> rootInfo = new HashMap<String, List<String>>();
		setNodeInfoAndChildeElements(archdescDid, rootInfo, null, null, null);
		indexInfo.put(objectId, rootInfo);

		Element dsc = archdesc.getChild("dsc", EAD_NS);
		List<Element> c01 = dsc.getChildren("c01", EAD_NS);
		if(c01.isEmpty()) {
			c01 = dsc.getChildren("c", EAD_NS);
		}

//		Element: childElement
//		String: isPartOf parentID
		HashMap<Element, String> childElements = new HashMap<Element, String>();
		for(Element e : c01) {
			childElements.put(e, objectId);
		}
		
//		String ID 
//		ArrayList<String> partIDs
		HashMap<String, ArrayList<String>> parentHasParts = new HashMap<String, ArrayList<String>>();
		
		for(int i=1; i<13; i++) {
			
			String nextLevel = (Integer.toString(i+1));
			if(i<9) {
				nextLevel = "c0"+nextLevel;
			} else nextLevel = "c"+nextLevel;
			
			HashMap<Element, String> currentElements = new HashMap<Element, String>();
			currentElements = childElements;
			childElements = new HashMap<Element, String>();
			
			String isPartOf = "";
			for(Element element : currentElements.keySet()) {
				HashMap<String, List<String>> nodeInfo = new HashMap<String, List<String>>();
				String uniqueID = UUID.randomUUID().toString();
				uniqueID = uniqueID.replace("-", "");
				String id = objectId+"-"+uniqueID;
				
				String parentId = currentElements.get(element);
				isPartOf = parentId;
				
				if(parentHasParts.get(parentId)==null) {
					ArrayList<String> hasPart = new ArrayList<String>();
					parentHasParts.put(parentId, hasPart);
				}
				parentHasParts.get(parentId).add(id);
				
				ArrayList<String> partOf = new ArrayList<String>();
				partOf.add(isPartOf);
				nodeInfo.put(C.EDM_IS_PART_OF, partOf);
				
				List<Element> children = element.getChildren();
				for(Element child : children) {
					setNodeInfoAndChildeElements(child, nodeInfo, nextLevel, childElements, id);
				}
				indexInfo.put(id, nodeInfo);
			}
			for(String parentId : parentHasParts.keySet()) {
				indexInfo.get(parentId).put(C.EDM_HAS_PART, parentHasParts.get(parentId));
			}
		}
		return indexInfo;
	}
	
	private void setNodeInfoAndChildeElements(Element child, HashMap<String, List<String>> nodeInfo, String nextLevel, HashMap<Element, String> childElements, String uniqueID) {
		if(child.getName().equals("did")) {
			nodeInfo.put(C.EDM_TITLE, getTitle(child));
			nodeInfo.put(C.EDM_DATE, getDate(child));
			nodeInfo.put(C.EDM_IDENTIFIER, getUnitIDs(child));
		} else if(child.getName().equals("daogrp")) {
			if(getHref(child)!=null & getHref(child).size()!=0) {
				List<String> shownBy = new ArrayList<String>();
				shownBy.add(getHref(child).get(0));
				nodeInfo.put(C.EDM_IS_SHOWN_BY, shownBy);
				nodeInfo.put(C.EDM_OBJECT, shownBy);
			} else if(getHref(child).size()>1) {
				nodeInfo.put(C.EDM_HAS_VIEW, getHref(child));
			}
		} else if(uniqueID!=null && (child.getName().equals(nextLevel) || child.getName().equals("c"))) {
			childElements.put(child, uniqueID);
		} 
	}
	
	private List<String> getTitle(Element element) {
		List<String> title = new ArrayList<String>();
		String t = "";
		try {
			t = element.getChild("unittitle", EAD_NS).getValue();
		} catch (Exception e) {
			logger.error("No unittitle element found");
		}
		title.add(t);
		return title;
	}
	
	private List<String> getDate(Element element) {
		List<String> date = new ArrayList<String>();
		String d = "";
		try {
			d = element.getChild("unitdate", EAD_NS).getAttribute("normal").getValue();
			if(d.equals("")) {
				d = element.getChild("unitdate", EAD_NS).getValue();
			}
		} catch (Exception e) {
			logger.debug("No unitdate element found");
		}
		date.add(d);
		return date;
	}
	
	private List<String> getUnitIDs(Element did) {
		List<String> unitIDs = new ArrayList<String>();
		@SuppressWarnings("unchecked")
		List<Element> children = did.getChildren("unitid", EAD_NS);
		
		for(Element child : children) {
			String unitID = "";
			String type = "";
			try {
				if(child.getAttribute("type")!=null) {
					type = child.getAttribute("type").getValue();
					if(!type.equals("")) {
						unitID = type+": "+child.getValue();
					}
				} else {
					unitID = child.getValue();
				}
				if(!unitID.equals("")) {
					unitIDs.add(unitID);
				}
			} catch (Exception e) {
				logger.debug("No unitid element found");
			}
		}
		return unitIDs;
	}
	
	private List<String> getHref(Element daogrp) {
		List<String> hrefs = new ArrayList<String>();
		String href = "";
		try {
			href = daogrp.getChild("daoloc", EAD_NS).getAttributeValue("href");
		} catch (Exception e) {
			logger.debug("No unitdate element found");
		}
		hrefs.add(href);
		return hrefs;
	}
}
