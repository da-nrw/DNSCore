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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.input.BOMInputStream;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import de.uzk.hki.da.model.DAFile;

/**
 * @author Polina Gubaidullina
 */

public class XMPMetadataStructure extends MetadataStructure{
	
	private static final Namespace RDF_NS = Namespace.getNamespace("http://www.w3.org/1999/02/22-rdf-syntax-ns#");
	private File xmpFile;
	private List<Element> descriptionElements;
	private Document rdfDoc;
	private List<DAFile> currentDAFiles;
	
	public XMPMetadataStructure(File metadataFile, List<DAFile> daFiles) throws FileNotFoundException, JDOMException, IOException {
		super(metadataFile, daFiles);
		
		logger.debug("Instantiate new xmp metadata structure with metadata file "+metadataFile.getAbsolutePath()+" & DAFiles ");
		for(DAFile file : daFiles) {
			logger.debug(file.getRelative_path());
		}
		
		xmpFile = metadataFile;
		currentDAFiles = daFiles;
		
		SAXBuilder builder = XMLUtils.createNonvalidatingSaxBuilder();
		FileInputStream fileInputStream = new FileInputStream(xmpFile);
		BOMInputStream bomInputStream = new BOMInputStream(fileInputStream);
		rdfDoc = builder.build(bomInputStream);
		descriptionElements = getXMPDescriptionElements();
	}
	
//	::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::  GETTER  ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
	
	@Override
	public File getMetadataFile() {
		return xmpFile;
	}
	
	private List<String> getReferences(List<Element> descriptionElements) {
		List<String> references = new ArrayList<String>();
			for(Element element : descriptionElements) {
				if(element.getName().equals("Description")) {
					String reference = element.getAttributeValue("about", RDF_NS);
					references.add(reference);
				}
			}
		return references;
	}
	
	@SuppressWarnings("unchecked")
	private List<Element> getXMPDescriptionElements() {
		List<Element> descriptionElements = new ArrayList<Element>();
		for(Element element : (List<Element>)rdfDoc.getRootElement().getChildren()) {
			if(element.getName().equals("Description")) {
				descriptionElements.add(element);
			}
		}
		return descriptionElements;
	}
	
	private List<DAFile> getReferencedFiles(List<DAFile> daFiles) {
		List<String> references = getReferences(descriptionElements);
		List<DAFile> existingFiles = new ArrayList<DAFile>();
		for(String ref : references) {
			String fileRelPath = "";
			Boolean fileExists = false;
			for(DAFile dafile : daFiles) {
				fileRelPath = dafile.getRelative_path();
				logger.debug("Check dafile: "+fileRelPath);
				if(ref.equals(fileRelPath)) {
					logger.debug("File "+fileRelPath+" exists.");
					fileExists = true;
					
//					calculate file path without suffix
					int indexOfSuffix = fileRelPath.indexOf(dafile.toRegularFile().getName()+".");
					String fileRelPathWithoutSuffix = fileRelPath.substring(0, indexOfSuffix+dafile.toRegularFile().getName().length()-2);
					
					Boolean sidecarFileExists = false;
					for(DAFile sidecarFile : daFiles) {
						if(sidecarFile.getRelative_path().toLowerCase().contains(".xmp")) {
							logger.debug("Check sidecar file "+sidecarFile.getRelative_path());						
//							calculate sidecar file path without suffix
							String sidecarFileRelPath = sidecarFile.getRelative_path();
							int indexOfXMPSuffix = sidecarFileRelPath.lastIndexOf(sidecarFile.toRegularFile().getName()+".");
							String sidecarFileRelPathWithoutSuffix = sidecarFileRelPath.substring(0, indexOfXMPSuffix+sidecarFile.toRegularFile().getName().length()-2);

							if(sidecarFileRelPathWithoutSuffix.equals(fileRelPathWithoutSuffix)) {
								logger.debug("Sidecare file "+sidecarFile.getRelative_path()+" found!");
								sidecarFileExists = true;
								existingFiles.add(dafile);
							}
						}
					}
					if(!sidecarFileExists) {
						logger.error("No matching sidecare file for "+fileRelPath);
					}
				}
			}
			if(!fileExists) {
				logger.error("Referenced file "+fileRelPath+" does not exist.");
			}
		}
		return existingFiles;
	}
	
//	:::::::::::::::::::::::::::::::::::::::::::::::::::::::::  REPLACEMENTS  :::::::::::::::::::::::::::::::::::::::::::::::::::::::::
	
	public void makeReplacementsInRDf(Map<String, String> replacements) throws IOException {
		for(Element element : descriptionElements) {
			if(element.getName().equals("Description")) {
				Attribute attr = element.getAttribute("about", RDF_NS);
				for(String ref : replacements.keySet()) {
					if(ref.equals(attr.getValue())) {
						logger.debug("Replace "+ref+" by "+replacements.get(ref));
						attr.setValue(replacements.get(ref));
					}
				}
			}
		}
		XMLOutputter outputter = new XMLOutputter();
		outputter.setFormat(Format.getPrettyFormat());
		outputter.output(rdfDoc, new FileWriter(xmpFile));
	}
	
//	:::::::::::::::::::::::::::::::::::::::::::::::::::::::::   VALIDATION   :::::::::::::::::::::::::::::::::::::::::::::::::::::::::
	
	private boolean checkReferencedFiles() {
		Boolean valid = true;
		if(getReferences(descriptionElements).size()==0) {
			logger.error("XMP.rdf does not contain any file references");
			valid = false;
		}
		if(getReferences(descriptionElements).size()!=getReferencedFiles(currentDAFiles).size()) {
			valid = false;
		}
		return valid;
	}
	
	@Override
	public boolean isValid() {
		return checkReferencedFiles();
	}
}
