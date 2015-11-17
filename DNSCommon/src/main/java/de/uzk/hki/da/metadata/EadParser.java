package de.uzk.hki.da.metadata;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
		
		String namespaceUri = eadDoc.getRootElement().getNamespace().getURI();
		XPath xPath = XPath.newInstance(C.EAD_XPATH_EXPRESSION);
		
//		Case of new DDB EAD with namespace xmlns="urn:isbn:1-931666-22-9"
		if(!namespaceUri.equals("")) {
			xPath = XPath.newInstance("//isbn:daoloc/@href");
			xPath.addNamespace("isbn", eadDoc.getRootElement().getNamespace().getURI());
		} 
		
		@SuppressWarnings("rawtypes")
		List allNodes = xPath.selectNodes(eadDoc);
		
		for (Object node : allNodes) {
			Attribute attr = (Attribute) node;
			String href = attr.getValue();
			metsReferences.add(href);
		}
		return metsReferences;
	}
	
	List<String> getTitle(Element element) {
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
	
	List<String> getDate(Element element) {
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
	
	List<String> getUnitIDs(Element did) {
		List<String> unitIDs = new ArrayList<String>();
		@SuppressWarnings("unchecked")
		List<Element> children = did.getChildren("unitid", EAD_NS);
		
		for(Element child : children) {
			String unitID = "";
			try {
				unitID = child.getValue();
				if(!unitID.equals("")) {
					unitIDs.add(unitID);
				}
			} catch (Exception e) {
				logger.debug("No unitid element found");
			}
		}
		return unitIDs;
	}
	
	List<String> getHref(Element daogrp) {
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
