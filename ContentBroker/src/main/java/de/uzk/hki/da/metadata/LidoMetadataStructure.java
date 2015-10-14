package de.uzk.hki.da.metadata;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
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
		
		XMLOutputter outputter = new XMLOutputter();
		outputter.setFormat(Format.getPrettyFormat());
		outputter.output(doc, new FileWriter(Path.makeFile(workPath,lidoFile.getPath())));
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
