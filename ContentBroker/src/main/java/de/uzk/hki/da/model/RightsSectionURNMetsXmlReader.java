/*
  DA-NRW Software Suite | ContentBroker
  Copyright (C) 2013 Historisch-Kulturwissenschaftliche Informationsverarbeitung
  Universität zu Köln

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

package de.uzk.hki.da.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;

import javax.xml.parsers.SAXParserFactory;

import nu.xom.Attribute;
import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.NodeFactory;
import nu.xom.ParsingException;
import nu.xom.ValidityException;

import org.apache.commons.io.input.BOMInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

import de.uzk.hki.da.metadata.PremisXmlReaderNodeFactory;


/**
 * The Class MetsURNXmlReader.
 *
 * @author Thomas Kleinke
 */
public class RightsSectionURNMetsXmlReader {

	/** The Constant METS_NS. */
	private static final String METS_NS = "http://www.loc.gov/METS/";
	
	/** The Constant MODS_NS. */
	private static final String MODS_NS = "http://www.loc.gov/mods/v3";

	/** The logger. */
	private static Logger logger = LoggerFactory.getLogger(RightsSectionURNMetsXmlReader.class);

	/** The err. */
	private static ErrorHandler err = new ErrorHandler(){

		@Override
		public void error(SAXParseException e) throws SAXException {
			throw new SAXException("Error while parsing mets file", e);
		}

		@Override
		public void fatalError(SAXParseException e) throws SAXException {
			throw new SAXException("Fatal error while parsing mets file", e);
		}

		@Override
		public void warning(SAXParseException e) throws SAXException {
			logger.warn("Warning while parsing mets file", e);
		}
	};

	/**
	 * Read urn.
	 *
	 * @param file the file
	 * @return The URN specified in the METS file or null if the METS file doesn't specify an URN
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws ParseException the parse exception
	 * @author Thomas Kleinke
	 */
	public String readURN(File file) throws IOException, ParseException {

		FileInputStream fileInputStream = new FileInputStream(file);
		BOMInputStream bomInputStream = new BOMInputStream(fileInputStream);
		
		XMLReader xmlReader = null;
		SAXParserFactory spf = SAXParserFactory.newInstance();
		try {
			xmlReader = spf.newSAXParser().getXMLReader();
		} catch (Exception e) {
			fileInputStream.close();
			bomInputStream.close();
			throw new IOException("Error creating SAX parser", e);
		}
		xmlReader.setErrorHandler(err);
		NodeFactory nodeFactory = new PremisXmlReaderNodeFactory();
		Builder parser = new Builder(xmlReader, false, nodeFactory);
		logger.trace("Successfully built builder and XML reader");

		try {
			String urn = null;
			
			Document doc = parser.build(bomInputStream);
			Element root = doc.getRootElement();			
			
			Element dmdSecEl = root.getFirstChildElement("dmdSec", METS_NS);
			if (dmdSecEl == null)
				return null;
			
			Element mdWrapEl = dmdSecEl.getFirstChildElement("mdWrap", METS_NS);
			if (mdWrapEl == null)
				return null;
			
			Element xmlDataEl = mdWrapEl.getFirstChildElement("xmlData", METS_NS);
			if (xmlDataEl == null)
				return null;
			
			Element modsEl = xmlDataEl.getFirstChildElement("mods", MODS_NS);
			if (modsEl == null)
				return null;
			
			Elements identifierEls = modsEl.getChildElements("identifier", MODS_NS);
			for (int i = 0; i < identifierEls.size(); i++) {
				Element element = identifierEls.get(i);
				Attribute attribute = element.getAttribute("type");
				if (attribute.getValue().toLowerCase().equals("urn"))
					urn = element.getValue();
			}

			if (urn != null && urn.equals(""))
				urn = null;
			
			return urn;
		}
		catch (ValidityException ve) {throw new IOException(ve);}
		catch (ParsingException pe) {throw new IOException(pe);}
		catch (IOException ie) {throw new IOException(ie);}
		finally {
			fileInputStream.close();
			bomInputStream.close();
		}
	}
}
