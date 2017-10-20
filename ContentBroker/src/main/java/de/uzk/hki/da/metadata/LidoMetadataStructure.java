package de.uzk.hki.da.metadata;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.input.BOMInputStream;
import org.jdom.Attribute;
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
	private LidoParser lidoParser;

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
		lidoParser = new LidoParser(doc);
		
		lidoLinkResources = lidoParser.getLidoLinkResources();
		fileInputStream.close();
		bomInputStream.close();
	}
	
//	::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::  GETTER  ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
	
	@Override
	public HashMap<String, HashMap<String, List<String>>> getIndexInfo(String objectId) {
		return lidoParser.getIndexInfo(objectId);
	}
	
	@Override
	public File getMetadataFile() {
		return lidoFile;
	}
	
	public LidoParser getLidoParser() {
		return lidoParser;
	}
	
	public List<String> getReferences() {
		return lidoParser.getReferences();
	}
	
//	:::::::::::::::::::::::::::::::::::::::::::::::::::::::::  REPLACEMENTS  :::::::::::::::::::::::::::::::::::::::::::::::::::::::::

	
	public void replaceRefResources(HashMap<String, String> linkResourceReplacements) throws IOException {
		for(String sourceLinkResource : linkResourceReplacements.keySet()) {
			for(int i=0; i<lidoLinkResources.size(); i++) {
				if(sourceLinkResource.equals(lidoLinkResources.get(i).getValue())) {
					lidoLinkResources.get(i).setText(linkResourceReplacements.get(sourceLinkResource));
				}
			}
		}
		
	/*	XMLOutputter outputter = new XMLOutputter();
		outputter.setFormat(Format.getPrettyFormat());
		outputter.output(doc, new FileWriter(Path.makeFile(workPath,lidoFile.getPath())));
		*/
		writeDocumentToFile(doc,Path.makeFile(workPath,lidoFile.getPath()));
	}
	
	/**
	 * Append to each administrativeMetadata in a Lido-File one RightsResourceType-Element and save it.
	 * 
	 * @param targetLidoFile
	 * @param licenseHref
	 * @param displayLabel
	 * @param text
	 * @throws IOException
	 * @throws JDOMException
	 */
	public void appendRightsResource(File targetLidoFile, String licenseHref, String displayLabel) throws IOException, JDOMException {
		SAXBuilder builder = XMLUtils.createNonvalidatingSaxBuilder();	
		
		FileInputStream fileInputStream = new FileInputStream(Path.makeFile(workPath,targetLidoFile.getPath()));
		BOMInputStream bomInputStream = new BOMInputStream(fileInputStream);
		Reader reader = new InputStreamReader(bomInputStream,"UTF-8");
		InputSource is = new InputSource(reader);
		is.setEncoding("UTF-8");
		Document lidoDoc = builder.build(is);
		
		List<Element> lidoElems= lidoDoc.getRootElement().getChildren("lido", C.LIDO_NS);

		for (int i=0; i<lidoElems.size(); i++) { 
			appendRightsResourceToLido(lidoElems.get(i),licenseHref,displayLabel);
		}
	
		
		fileInputStream.close();
		bomInputStream.close();
		reader.close();
		
		writeDocumentToFile(lidoDoc,Path.makeFile(workPath,targetLidoFile.getPath()));
	}
	

	/**
	 * Append to each administrativeMetadata in a Lido-File one RightsResourceType-Element and save it.
	 * 
	 * @param targetLidoFile
	 * @param licenseHref
	 * @param displayLabel
	 * @param text
	 * @throws IOException
	 * @throws JDOMException
	 */
	public void appendRightsResourceToLido(Element lidoElem, String licenseHref, String displayLabel) throws IOException, JDOMException {
		List<Element> admSections= lidoElem.getChildren("administrativeMetadata", C.LIDO_NS);
		

		for (int i=0; i<admSections.size(); i++) { 
			Element resourceWrap=admSections.get(i).getChild("resourceWrap", C.LIDO_NS);
			if(resourceWrap==null){
				resourceWrap=new Element("resourceWrap",C.LIDO_NS);
				//read or add as new
				//admSections.get(i).removeChildren("resourceWrap", C.LIDO_NS);
				resourceWrap.detach();
				admSections.get(i).addContent(resourceWrap);
			}
			List<Element> resourceSets= resourceWrap.getChildren("resourceSet", C.LIDO_NS);
			if(resourceSets.size()==0){
				Element newEl=new Element("resourceSet",C.LIDO_NS);
				//resourceSets.add(newEl);
				resourceWrap.addContent(newEl);
				resourceSets= resourceWrap.getChildren("resourceSet", C.LIDO_NS);
			}
			for (int j=0; j<resourceSets.size(); j++) { 
				List<Element> rightsResources= resourceSets.get(j).getChildren("rightsResource", C.LIDO_NS);
				if(rightsResources.size()==0){
					Element newEl=new Element("rightsResource",C.LIDO_NS);
					newEl.detach();
					resourceSets.get(j).addContent(newEl);
					rightsResources= resourceSets.get(j).getChildren("rightsResource", C.LIDO_NS);
				}
				for (int k=0; k<rightsResources.size(); k++) {
					List<Element> rightsTypes=rightsResources.get(k).getChildren("rightsType", C.LIDO_NS);
					if(rightsTypes.size()!=0){
						throw new RuntimeException("lido/administrativeMetadata/resourceWrap/resourceSet/rightsResource has already rightsType(s): "+Arrays.toString(rightsTypes.toArray()));
					}
					Element newRightsTypeE=generateRightsType(licenseHref,displayLabel);
					newRightsTypeE.detach();
					logger.debug("Append to Lido new rightsType: "+newRightsTypeE.toString());
					rightsResources.get(k).addContent(newRightsTypeE);
				}
				
			}
			
			
		}
	}
	
	
	/**
	 * Method wraps given attributes into accessCondition-Element and returns it.
	 * 
	 * @param href
	 * @param displayLabel
	 * @param text
	 * @return
	 */
	public static Element generateRightsType(String href, String displayLabel) {
		Element newRightsType=new Element("rightsType",C.LIDO_NS);
		Element newConceptID=new Element("conceptID",C.LIDO_NS);
		newConceptID.setAttribute(new Attribute("type", "URI",C.LIDO_NS));
		newConceptID.setText(href);
		
		Element newTerm=new Element("term",C.LIDO_NS);
		newTerm.setAttribute(new Attribute("pref", "preferred",C.LIDO_NS));
		newTerm.setAttribute(new Attribute("addedSearchTerm", "no",C.LIDO_NS));
		newTerm.setText(displayLabel);
		
		newRightsType.addContent(newConceptID);
		newRightsType.addContent(newTerm);

		return newRightsType;
	}

	
//	:::::::::::::::::::::::::::::::::::::::::::::::::::::::::   VALIDATION   :::::::::::::::::::::::::::::::::::::::::::::::::::::::::
	
	private boolean checkReferencedFiles() {
		Boolean valid = true;
		List<String> lidoLinkResourceValues = lidoParser.getReferences();
		if(lidoLinkResourceValues.size()!=getReferencedFiles(lidoFile, lidoLinkResourceValues, currentDocuments).size()) {
			valid = false;
		}
		return valid;
	}
	
	@Override
	public boolean isValid() {
		return checkReferencedFiles();
	}
}
