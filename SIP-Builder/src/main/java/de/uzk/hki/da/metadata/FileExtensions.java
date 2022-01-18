/**
 * 
 */
package de.uzk.hki.da.metadata;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.SAXParserFactory;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.ParsingException;
import nu.xom.ValidityException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

import de.uzk.hki.da.utils.XMLUtils;

/**
 * @author gbender
 *
 */
public class FileExtensions {
	private Logger logger = LogManager.getLogger(FileExtensions.class);

	/**
	 * 
	 * @param fileExtensionsFile
	 * @throws IOException 
	 * @throws ParsingException 
	 * @throws ValidityException 
	 */
	public HashMap<String, List<String>> loadFileExtensionsFromFile(File fileExtensionsFile) throws IOException {
		 HashMap<String, List<String>>  listElements = new HashMap<String, List<String>>() ;
		
		if (!fileExtensionsFile.exists()) {
			logger.error("Missing File " + fileExtensionsFile);
			throw new IOException("Missing File " + fileExtensionsFile);
		} else {
		
		
			XMLReader xmlReader = null;
			SAXParserFactory spf = SAXParserFactory.newInstance();
			try {
				//xmlReader = spf.newSAXParser().getXMLReader();
				xmlReader=XMLUtils.createValidatingSaxParser().getXMLReader();
			} catch (Exception e) {
				throw new IOException("Error creating SAX parser", e);
			}
			xmlReader.setErrorHandler(new ErrorHandler(){
	
				@Override
				public void error(SAXParseException e) throws SAXException {
					throw new SAXException("Error while parsing settings file", e);
				}
	
				@Override
				public void fatalError(SAXParseException e) throws SAXException {
					throw new SAXException("Fatal error while parsing settings file", e);
				}
	
				@Override
				public void warning(SAXParseException e) throws SAXException {
					logger.warn("Warning while parsing settings file:\n" + e.getMessage());
				}
			});
	
			FileReader reader;
			Document doc = null;
			try {
				reader = new FileReader(fileExtensionsFile);
				Builder parser = new Builder(xmlReader);
				doc = parser.build(reader);
				reader.close();
			} catch (Exception e1) {
				e1.printStackTrace();
			} 
			
			Element root = doc.getRootElement();
			
			Element imageFileEl = root.getFirstChildElement("image");

			List<String> image = new  ArrayList<String>();
			 
			for (int i = 0; imageFileEl.getChildCount() > i; i++) {
				if (!imageFileEl.getChild(i).getValue().trim().equals("")) {
					image.add(imageFileEl.getChild(i).getValue());
				}
			}
			listElements.put("image", image);
			
			Element textFileEl = root.getFirstChildElement("text");
			List<String> text = new ArrayList<String>();
			for (int i=0; textFileEl.getChildCount() > i ; i++) {
				if (!textFileEl.getChild(i).getValue().trim().equals("")) {
					text.add(textFileEl.getChild(i).getValue());
				}
			}
			listElements.put("text",text);
			

			Element documentFileEl = root.getFirstChildElement("document");
			List<String> document = new ArrayList<String>();
			for (int i=0; documentFileEl.getChildCount() > i ; i++) {
				if (!documentFileEl.getChild(i).getValue().trim().equals("")) {
					document.add(documentFileEl.getChild(i).getValue());
				}
			}
			listElements.put("document",document);
			
			Element audioFileEl = root.getFirstChildElement("audio");
			List<String> audio = new ArrayList<String>();
			audio.add("audio");
			for(int i=0; audioFileEl.getChildCount()>i; i++) {
				if (!audioFileEl.getChild(i).getValue().trim().equals("")) {
					audio.add(audioFileEl.getChild(i).getValue());
				}
			}
			listElements.put("audio",audio);
			
			Element videoFileEl = root.getFirstChildElement("video");
			List<String> video = new ArrayList<String>();
			video.add("video");
			for(int i=0; videoFileEl.getChildCount()>i; i++) {
				if (!videoFileEl.getChild(i).getValue().trim().equals("")) {
					video.add(videoFileEl.getChild(i).getValue());
				}
			}
			listElements.put("video",video);
			
		}
		return listElements;
	}
	
}
