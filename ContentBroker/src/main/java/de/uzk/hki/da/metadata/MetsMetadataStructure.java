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
 * @author Eugen Trebunski
 */

public class MetsMetadataStructure extends MetadataStructure {

	/** The logger. */
	public static Logger logger = LoggerFactory
			.getLogger(MetsMetadataStructure.class);
	
	private File metsFile;
	private List<de.uzk.hki.da.model.Document> currentDocuments;
	private Document metsDoc;

	List<Element> fileElements;
	Element modsXmlData;
	MetsParser metsParser;
	
	public MetsParser getMetsParser() {
		return metsParser;
	}

	public MetsMetadataStructure(Path workPath,File metadataFile, List<de.uzk.hki.da.model.Document> documents)
			throws FileNotFoundException, JDOMException, IOException {
		super(workPath,metadataFile, documents);
		
		metsFile = metadataFile;
		currentDocuments = documents;
		
		SAXBuilder builder = XMLUtils.createValidatingSaxBuilder();		
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
	
	public String getHref(Element fileElement) {
		return metsParser.getHref(fileElement);
	}
	
	public String getUrn() {
		return metsParser.getUrn();
	}

	@Override
	public HashMap<String, HashMap<String, List<String>>> getIndexInfo(String ObjectId) {
		return metsParser.getIndexInfo(ObjectId);
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
	
	@Override
	public File getMetadataFile() {
		return metsFile;
	}
	
//	:::::::::::::::::::::::::::::::::::::::::::::::::::::::::  REPLACEMENTS  :::::::::::::::::::::::::::::::::::::::::::::::::::::::::

	public void makeReplacementsHrefInMetsFile(File targetMetsFile, String currentHref, String targetHref, String mimetype, String loctype) throws IOException, JDOMException {
		SAXBuilder builder = XMLUtils.createValidatingSaxBuilder();	
		logger.debug(":::"+workPath+":::"+targetMetsFile.getPath());
		FileInputStream fileInputStream = new FileInputStream(Path.makeFile(workPath,targetMetsFile.getPath()));
		BOMInputStream bomInputStream = new BOMInputStream(fileInputStream);
		Reader reader = new InputStreamReader(bomInputStream,"UTF-8");
		InputSource is = new InputSource(reader);
		is.setEncoding("UTF-8");
		Document metsDoc = builder.build(is);
		
		List<Element> metsFileElements = metsParser.getFileElementsFromMetsDoc(metsDoc);
		
		for (int i=0; i<metsFileElements.size(); i++) { 
			Element fileElement = (Element) metsFileElements.get(i);
			if(metsParser.getHref(fileElement).equals(currentHref)) {
				setHref(fileElement, targetHref);
				setMimetype(fileElement, mimetype);
				setLoctype(fileElement, loctype);
			}
		}

		fileInputStream.close();
		bomInputStream.close();
		reader.close();
		
		writeDocumentToFile(metsDoc,Path.makeFile(workPath,targetMetsFile.getPath()));
	}
	
	/**
	 * Append to each dmdSec in a Mets-File one accessCondition-Element and save it.
	 * 
	 * @param targetMetsFile
	 * @param licenseHref
	 * @param displayLabel
	 * @param text
	 * @throws IOException
	 * @throws JDOMException
	 */
	public void appendAccessCondition(File targetMetsFile, String licenseHref, String displayLabel, String text) throws IOException, JDOMException {
		SAXBuilder builder = XMLUtils.createValidatingSaxBuilder();	
		
		FileInputStream fileInputStream = new FileInputStream(Path.makeFile(workPath,targetMetsFile.getPath()));
		BOMInputStream bomInputStream = new BOMInputStream(fileInputStream);
		Reader reader = new InputStreamReader(bomInputStream,"UTF-8");
		InputSource is = new InputSource(reader);
		is.setEncoding("UTF-8");
		Document metsDoc = builder.build(is);
		
		List<Element> dmdSections= metsDoc.getRootElement().getChildren("dmdSec", C.METS_NS);
		
		for (int i=0; i<dmdSections.size(); i++) { 
			Element newAccessConditionE=generateAccessCondition(licenseHref,displayLabel,text);
			logger.debug("Append to Mets new LicenseElement: "+newAccessConditionE.toString());
			Element dmdSecElement = (Element) dmdSections.get(i);
			Element modsXmlData = MetsParser.getModsXmlData(dmdSecElement);
			modsXmlData.addContent(newAccessConditionE);
		}
		fileInputStream.close();
		bomInputStream.close();
		reader.close();
		
		writeDocumentToFile(metsDoc,Path.makeFile(workPath,targetMetsFile.getPath()));
	}
	
	
	/**
	 * Method wraps given attributes into accessCondition-Element and returns it.
	 * 
	 * @param href
	 * @param displayLabel
	 * @param text
	 * @return
	 */
	public static Element generateAccessCondition(String href, String displayLabel, String text) {
		Element newAccessCondition=new Element("accessCondition",C.MODS_NS);
		newAccessCondition.setAttribute("type", "use and reproduction");
		newAccessCondition.setAttribute(new Attribute("href", href,C.XLINK_NS));
		newAccessCondition.setAttribute("displayLabel", displayLabel!=null?displayLabel:"");
		newAccessCondition.addContent(text!=null?text:"");
		return newAccessCondition;
	}
	
	
//	:::::::::::::::::::::::::::::::::::::::::::::::::::::::::   VALIDATION   :::::::::::::::::::::::::::::::::::::::::::::::::::::::::

	private boolean checkReferencedFiles() {
		Boolean valid = true;
		int numReferences = metsParser.getReferences().size();
		logger.debug("Number of references "+numReferences);
		int numReferencesFiles = getReferencedFiles(metsFile, metsParser.getReferences(), currentDocuments).size();
		logger.debug("Number of referenced files: "+numReferencesFiles);
		if(numReferences!=numReferencesFiles) {
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
