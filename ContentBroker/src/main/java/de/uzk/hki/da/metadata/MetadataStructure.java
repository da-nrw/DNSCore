/*
  DA-NRW Software Suite | ContentBroker
  Copyright (C) 2014 LVRInfoKom
  Landschaftsverband Rheinland

  This program is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.

  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.

  You should have received a copy of the GNU General Public License
  along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package de.uzk.hki.da.metadata;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.FilenameUtils;
import org.jdom.JDOMException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import de.uzk.hki.da.core.C;
import de.uzk.hki.da.model.DAFile;
import de.uzk.hki.da.util.Path;

/**
 * @author Polina Gubaidullina
 */

public abstract class MetadataStructure {
	
	/** The logger. */
	public Logger logger = LoggerFactory
			.getLogger(MetadataStructure.class);
	
	public MetadataStructure(File metadataFile, List<de.uzk.hki.da.model.Document> documents) 
			throws FileNotFoundException, JDOMException, IOException {
	}
	
	public abstract boolean isValid();
	
	public File getCanonicalFileFromReference(String ref, File metadataFile) throws IOException {
		String tmpFilePath = Path.make(metadataFile.getParentFile().getAbsolutePath(), ref).toString();
		return new File(tmpFilePath).getCanonicalFile();
	}
	
	public abstract File getMetadataFile();
	
	public abstract HashMap<String, HashMap<String, List<String>>> getIndexInfo();
	
	protected void printIndexInfo() {
		HashMap<String, HashMap<String, List<String>>> indexInfo = getIndexInfo();
		for(String id : indexInfo.keySet()) {
			logger.info("-----------------------------------------------------");
			logger.info("ID: "+id);
			for(String info : indexInfo.get(id).keySet()) {
				logger.info(info+": "+indexInfo.get(id).get(info));
			}
			logger.info("-----------------------------------------------------");
		}
	}
	
	public void toEDM(HashMap<String, HashMap<String, List<String>>> indexInfo, File file) {
		try {
			
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            
			Document edmDoc = docBuilder.newDocument();
			Element rootElement = edmDoc.createElement("rdf:RDF");
			edmDoc.appendChild(rootElement);
			
			addXmlNsToEDM(edmDoc, rootElement);
			
			for(String id : indexInfo.keySet()) {
				Element providedCHO = addEdmProvidedCHOtoEdm(id, edmDoc, rootElement);
				Element aggregation = addOreAggregationToEdm(id, edmDoc, rootElement);
				for(String elementName : indexInfo.get(id).keySet()) {
					Element parentNode = null;
					if(elementName.startsWith("dc:") || elementName.startsWith("dcterms:")) {
						parentNode = providedCHO;
					} else if(elementName.startsWith("edm:")) {
						parentNode = aggregation;
					}
					if(parentNode!=null) {
						List<String> values = indexInfo.get(id).get(elementName);
						for(String currentValue : values) {
							if(!currentValue.equals("")) {
								addNewElementToParent(elementName, currentValue, parentNode, edmDoc);
							}
						}
					}
				}
			}
			
			javax.xml.transform.Source source = new javax.xml.transform.dom.DOMSource(edmDoc) ;
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            Result result = new javax.xml.transform.stream.StreamResult(file);
            transformer.transform(source, result);
                    
            System.out.println("File saved!");
            
		} catch (Exception e) {
			logger.error("Unable to create the edm file!");
		}
	}
	
	private void addNewElementToParent(String elementName, String elementValue, Element parent, Document edmDoc) {
		Element eName = edmDoc.createElement(elementName);
		eName.appendChild(edmDoc.createTextNode(elementValue));
		parent.appendChild(eName);
	}
	
	private Element addEdmProvidedCHOtoEdm(String id, Document edmDoc, Element rootElement) {
		String cho_identifier = "cho/identifier";
		if(!id.equals("")) {
			cho_identifier = cho_identifier+"-"+id;
		}
		Element providedCHO = edmDoc.createElement("edm:ProvidedCHO");
		Attr rdfAbout = edmDoc.createAttribute("rdf:about");
		rdfAbout.setValue(cho_identifier);
		providedCHO.setAttributeNode(rdfAbout);
		rootElement.appendChild(providedCHO);
		
		return providedCHO;
	}
	
	private Element addOreAggregationToEdm(String id, Document edmDoc, Element rootElement) {
		String aggr_identifier = "aggr/identifier";
		if(!id.equals("")) {
			aggr_identifier = aggr_identifier+"-"+id;
		}
		Element aggregation = edmDoc.createElement("ore:Aggregation");
		Attr rdfAbout = edmDoc.createAttribute("rdf:about");
		rdfAbout.setValue(aggr_identifier);
		aggregation.setAttributeNode(rdfAbout);
		rootElement.appendChild(aggregation);
		
		return aggregation;
	}
	
	private void addXmlNsToEDM(Document edmDoc, Element rootElement) {
		Attr xmlns_dc = edmDoc.createAttribute("xmlns:dc");
		xmlns_dc.setValue(C.DC_NS.getURI());
		rootElement.setAttributeNode(xmlns_dc);
		
		Attr xmlns_edm = edmDoc.createAttribute("xmlns:edm");
		xmlns_edm.setValue(C.EDM_NS.getURI());
		rootElement.setAttributeNode(xmlns_edm);
		
		Attr xmlns_dcterms = edmDoc.createAttribute("xmlns:dcterms");
		xmlns_dcterms.setValue(C.DCTERMS_NS.getURI());
		rootElement.setAttributeNode(xmlns_dcterms);
		
		Attr xmlns_rdf = edmDoc.createAttribute("xmlns:rdf");
		xmlns_rdf.setValue(C.RDF_NS.getURI());
		rootElement.setAttributeNode(xmlns_rdf);
		
		Attr xmlns_ore = edmDoc.createAttribute("xmlns:ore");
		xmlns_ore.setValue(C.ORE_NS.getURI());
		rootElement.setAttributeNode(xmlns_ore);
	}
	
	protected List<File> getReferencedFiles(File metadataFile, List<String> references, List<de.uzk.hki.da.model.Document> documents) {
		List<File> existingFiles = new ArrayList<File>();
		List<String> missingFiles = new ArrayList<String>();
		for(String ref : references) {
			File refFile;
			try {
				refFile = getCanonicalFileFromReference(ref, metadataFile);
				String fileName = FilenameUtils.getBaseName(refFile.getName());
				logger.debug("Check referenced file "+fileName+" (reference: "+ref+")");
				Boolean docExists = false;
				for(de.uzk.hki.da.model.Document doc : documents) {
					if(doc.getName().equals(fileName)) {
						docExists = true;
						
						Boolean fileExists = false;
						
						DAFile lastDAFile = doc.getLasttDAFile();
						
						File f = getExistingFile(metadataFile, refFile, lastDAFile);
						if(f!=null) {
							fileExists = true;
						} else {
							while(lastDAFile.getPreviousDAFile() != null){
					        	f = getExistingFile(metadataFile, refFile, lastDAFile.getPreviousDAFile());
					        	if(f!=null) {
					        		fileExists = true;
					        		break;
								}
					        	lastDAFile = lastDAFile.getPreviousDAFile(); 
					        }
						}
						if(fileExists) {
							existingFiles.add(f);
						} else {
							logger.error("File "+ref+" does not exist.");
							missingFiles.add(ref);
						}
					}
				}
				if(!docExists) {
					logger.debug("There is no document "+fileName+"!");
					logger.error("File "+ref+" does not exist.");
					missingFiles.add(ref);
				}
			} catch (IOException e) {
				logger.error("File "+ref+" does not exist.");
				e.printStackTrace();
			}
		}
		if(!missingFiles.isEmpty()) {
			logger.error("Missing files: ");
			for(String missingFile : missingFiles) {
				logger.error(missingFile);
			}
		}
		return existingFiles;
	}
	
	private File getExistingFile(File metadataFile, File refFile, DAFile dafile) {
		File existingFile = null;
		
		String nameOfMetadataParentFile = metadataFile.getParentFile().getName();
		String relPathFromMetadataFile = Path.extractRelPathFromDir(refFile, nameOfMetadataParentFile);
		
		String dafileRelPath = "";	
		File file = dafile.toRegularFile();
		if(nameOfMetadataParentFile.endsWith("+a") || nameOfMetadataParentFile.endsWith("+b") || nameOfMetadataParentFile.equals("public") || nameOfMetadataParentFile.equals("institution")) {
			dafileRelPath = dafile.getRelative_path();
		} else {
			dafileRelPath = Path.extractRelPathFromDir(file, nameOfMetadataParentFile);
		}
		if(dafileRelPath.equals(relPathFromMetadataFile)) {
			existingFile = file;
			logger.debug("File "+existingFile+" exists!");
		} 
		return existingFile;
	}

}
