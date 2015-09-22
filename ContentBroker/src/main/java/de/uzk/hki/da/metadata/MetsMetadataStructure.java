/*
  DA-NRW Software Suite | ContentBroker
  Copyright (C) 2015 LVRInfoKom
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

import de.uzk.hki.da.metadata.MetsParser;
import de.uzk.hki.da.utils.Path;
import de.uzk.hki.da.utils.XMLUtils;

/**
 * @author Polina Gubaidullina
 */

public class MetsMetadataStructure extends MetadataStructure {

	/** The logger. */
	public Logger logger = LoggerFactory
			.getLogger(MetsMetadataStructure.class);
	
	private File metsFile;
	private List<de.uzk.hki.da.model.Document> currentDocuments;
	private Document metsDoc;

	List<Element> fileElements;
	Element modsXmlData;
	MetsParser metsParser;
	
	public MetsMetadataStructure(Path workPath,File metadataFile, List<de.uzk.hki.da.model.Document> documents)
			throws FileNotFoundException, JDOMException, IOException {
		super(workPath,metadataFile, documents);
		
		metsFile = metadataFile;
		currentDocuments = documents;
		
		SAXBuilder builder = XMLUtils.createNonvalidatingSaxBuilder();		
		FileInputStream fileInputStream = new FileInputStream(Path.makeFile(workPath,metsFile.getPath()));
		BOMInputStream bomInputStream = new BOMInputStream(fileInputStream);
		Reader reader = new InputStreamReader(bomInputStream,"UTF-8");
		InputSource is = new InputSource(reader);
		is.setEncoding("UTF-8");
		metsDoc = builder.build(is);
		metsParser = new MetsParser(metsDoc);
		
		fileElements = metsParser.getFileElementsFromMetsDoc(metsDoc);
		fileInputStream.close();
		
		bomInputStream.close();
		reader.close();
	}
	
//	::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::  GETTER  ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
	
	public List<Element> getFileElements() {
		return fileElements;
	}

	public void setFileElements(List<Element> fileElements) {
		this.fileElements = fileElements;
	}

	@Override
	public HashMap<String, HashMap<String, List<String>>> getIndexInfo(String ObjectId) {
		return metsParser.getIndexInfo(ObjectId);
	}
	
	public String getMetsUrn() {
		return metsParser.getUrn();
	}
	
	public File getMetadataFile() {
		return metsFile;
	}
	
	public String getHref(Element fileElement) {
		return metsParser.getHref(fileElement);
	}
	
	private List<String> getReferences() {
		return metsParser.getReferences();
	}
	
	private List<Element> getFileElementsFromMetsDoc(Document doc) throws JDOMException {
		return metsParser.getFileElementsFromMetsDoc(doc);
	} 
	
//	::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::  SETTER  ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
	
	private void setMimetype(Element fileElement, String mimetype) {
		metsParser.setMimetype(fileElement, mimetype);
	}
	
	private void setLoctype(Element fileElement, String loctype) {
		metsParser.setLoctype(fileElement, loctype);
	}
	
	private void setHref(Element fileElement, String newHref) {
		metsParser.setHref(fileElement, newHref);
	}
	
//	:::::::::::::::::::::::::::::::::::::::::::::::::::::::::  REPLACEMENTS  :::::::::::::::::::::::::::::::::::::::::::::::::::::::::

	public void makeReplacementsInMetsFile(File metsFile, String currentHref, String targetHref, String mimetype, String loctype) throws IOException, JDOMException {
		File targetMetsFile = metsFile;
		SAXBuilder builder = XMLUtils.createNonvalidatingSaxBuilder();	
		logger.debug(":::"+workPath+":::"+targetMetsFile.getPath());
		FileInputStream fileInputStream = new FileInputStream(Path.makeFile(workPath,targetMetsFile.getPath()));
		BOMInputStream bomInputStream = new BOMInputStream(fileInputStream);
		Reader reader = new InputStreamReader(bomInputStream,"UTF-8");
		InputSource is = new InputSource(reader);
		is.setEncoding("UTF-8");
		Document metsDoc = builder.build(is);
		
		List<Element> metsFileElements = getFileElementsFromMetsDoc(metsDoc);
		
		for (int i=0; i<metsFileElements.size(); i++) { 
			Element fileElement = (Element) metsFileElements.get(i);
			if(getHref(fileElement).equals(currentHref)) {
				setHref(fileElement, targetHref);
				setMimetype(fileElement, mimetype);
				setLoctype(fileElement, loctype);
			}
		}
		XMLOutputter outputter = new XMLOutputter();
		outputter.setFormat(Format.getPrettyFormat());
		outputter.output(metsDoc, new FileWriter(Path.makeFile(workPath,targetMetsFile.getPath())));

		fileInputStream.close();
		bomInputStream.close();
		reader.close();
	}

//	:::::::::::::::::::::::::::::::::::::::::::::::::::::::::   VALIDATION   :::::::::::::::::::::::::::::::::::::::::::::::::::::::::

	private boolean checkReferencedFiles() {
		Boolean valid = true;
		if(getReferences().size()!=getReferencedFiles(metsFile, getReferences(), currentDocuments).size()) {
			valid = false;
		}
		return valid;
	}
	
	public boolean uniqueRootDivElementExists() {
		if(metsParser.getUniqueRootElementInLogicalStructMap()!=null) {
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public boolean isValid() {
		return checkReferencedFiles();
	}
}
