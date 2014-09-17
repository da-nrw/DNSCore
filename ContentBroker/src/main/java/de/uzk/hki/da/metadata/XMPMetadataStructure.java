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
import de.uzk.hki.da.utils.XMLUtils;

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
		
		xmpFile = metadataFile;
		currentDAFiles = daFiles;
		
		SAXBuilder builder = XMLUtils.createNonvalidatingSaxBuilder();
		FileInputStream fileInputStream = new FileInputStream(xmpFile);
		BOMInputStream bomInputStream = new BOMInputStream(fileInputStream);
		rdfDoc = builder.build(bomInputStream);
		descriptionElements = getXMPDescriptionElements();
	}
	
//	::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::  GETTER  ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
	
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
		Boolean fileExists = false;
		for(String ref : references) {
			for(DAFile dafile : daFiles) {
				if(ref.equals(dafile.getRelative_path())) {
					fileExists = true;
					existingFiles.add(dafile);
				}
				if(fileExists) {
					logger.debug("File "+dafile.getRelative_path()+" exists.");
				} else {
					logger.error("File "+dafile.getRelative_path()+" does not exist.");
				}
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
	
//	private boolean checkReferencedFiles() {
//		Boolean valid = true;
//		if(getReferences(descriptionElements).size()!=getReferencedFiles(currentDAFiles).size()) {
//			valid = false;
//		}
//		return valid;
//	}
	
	@Override
	public boolean isValid() {
		return true;
//		return checkReferencedFiles();
	}
}
