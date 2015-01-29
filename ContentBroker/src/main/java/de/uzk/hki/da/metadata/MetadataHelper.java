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

import de.uzk.hki.da.core.C;

public class MetadataHelper {
	
//	LIDO
	
	private static final Namespace LIDO_NS = Namespace.getNamespace("http://www.lido-schema.org");
	
	public List<String> getLIDOURL(Document doc){
		ArrayList<String> linkResources = new ArrayList<String>();
		
		@SuppressWarnings("unchecked")
		List<Element> lidoNodes = (List<Element>) doc.getRootElement().getChildren("lido", LIDO_NS); 
		
		for(Element e : lidoNodes) {
			String linkResource = e.getChild("administrativeMetadata", LIDO_NS)
				.getChild("resourceWrap", LIDO_NS)
				.getChild("resourceSet", LIDO_NS)
				.getChild("resourceRepresentation", LIDO_NS)
				.getChild("linkResource", LIDO_NS)
				.getValue();
			linkResources.add(linkResource);
		}
		return linkResources;
	}
	
//	METS
	
	private static final Namespace METS_NS = Namespace.getNamespace("http://www.loc.gov/METS/");
	private static final Namespace XLINK_NS = Namespace.getNamespace("http://www.w3.org/1999/xlink");
	
	@SuppressWarnings("unchecked")
	public List<Element> getMetsFileElements (Document doc) {
		return (List<Element>) doc.getRootElement()
				.getChild("fileSec", METS_NS)
				.getChild("fileGrp", METS_NS)
				.getChildren("file", METS_NS);	
	} 

	public String getMetsHref(Element fileElement){
		return fileElement
				.getChild("FLocat", METS_NS)
				.getAttributeValue("href", XLINK_NS);
	}
	
	public String getMetsMimetype(Element fileElement){
		return fileElement.getAttributeValue("MIMETYPE");
	}
	
	public String getMetsLoctype(Element fileElement){
		return fileElement
				.getChild("FLocat", C.METS_NS)
				.getAttributeValue("LOCTYPE");
	}
	
	public Element getXmlData(Document doc) {
		return (Element) doc.getRootElement()
				.getChild("dmdSec", C.METS_NS)
				.getChild("mdWrap", C.METS_NS)
				.getChild("xmlData", C.METS_NS);
	}
	
//	EAD
	
	private String EAD_XPATH_EXPRESSION = "//daoloc/@href";
	
	public List<String> getMetsRefsInEad(Document eadDoc) throws JDOMException, IOException {
		
		List<String> metsReferences = new ArrayList<String>();
	
		XPath xPath = XPath.newInstance(EAD_XPATH_EXPRESSION);
		
		@SuppressWarnings("rawtypes")
		List allNodes = xPath.selectNodes(eadDoc);
		
		for (java.lang.Object node : allNodes) {
			Attribute attr = (Attribute) node;
			String href = attr.getValue();
			metsReferences.add(href);
		}
		return metsReferences;
	}	
}
