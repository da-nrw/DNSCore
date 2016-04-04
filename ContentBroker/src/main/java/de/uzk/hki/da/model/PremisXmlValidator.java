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
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.NodeFactory;
import nu.xom.ParsingException;
import nu.xom.ValidityException;

import org.apache.xerces.dom.DOMInputImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import de.uzk.hki.da.metadata.PremisXmlReaderNodeFactory;
import de.uzk.hki.da.utils.C;

/**
 * The Class PremisXmlValidator.
 * 
 * @author <a href="mailto:eugen.trebunski@lvr.de">Eugen Trebunski</a>
 */
public class PremisXmlValidator {

	/** The logger. */
	private static Logger logger = LoggerFactory
			.getLogger(PremisXmlValidator.class);

	/**
	 * Validate premis file.
	 *
	 * @param premisFile
	 *            the premis file
	 * @return true if valid, false otherwise
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @author Thomas Kleinke
	 * @author Daniel M. de Oliveira
	 * @throws SAXException
	 *             Contains detailed parse error location.
	 */
	public static boolean validatePremisFile(File premisFile)
			throws IOException, SAXException {
		return validatePremisFileXSD(premisFile)
				&& validatePremisFileMinContent(premisFile);
	}

	public static boolean validatePremisFileXSD(File premisFile)
			throws IOException, SAXException {

		if (!premisFile.exists())
			throw new FileNotFoundException();

		Source fileSource = new StreamSource(premisFile);
		SchemaFactory factory = SchemaFactory
				.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		factory.setResourceResolver(new PremisResourceResolver());

		Source premisSchemaFile = new StreamSource(new File(C.PREMIS_XSD_PATH));
		Source danrwContractSchemaFile = new StreamSource(new File(
				C.CONTRACT_XSD_PATH));

		Schema schema = null;
		try {
			schema = factory.newSchema(new Source[] { premisSchemaFile,
					danrwContractSchemaFile });
			Validator validator = schema.newValidator();
			validator.validate(fileSource);
		} catch (SAXException e) {
			logger.error(e.getMessage());
			throw e;
		}

		return true;
	}

	public static boolean validatePremisFileMinContent(File premisFile)
			throws IOException, SAXException {
		Reader reader = new FileReader(premisFile);
		boolean sipCreationEventExists = false;
		boolean rightsPartExists = false;

		NodeFactory nodeFactory = new PremisXmlReaderNodeFactory();
		
		XMLReader xmlReader = null;
		SAXParserFactory spf = SAXParserFactory.newInstance();
		try {
			xmlReader = spf.newSAXParser().getXMLReader();
		} catch (Exception e) {
			reader.close();
			throw new IOException("Error creating SAX parser", e);
		}
		xmlReader.setErrorHandler(new DefaultHandler());

		Builder parser = new Builder(xmlReader, false, nodeFactory);
		logger.trace("Successfully built builder and XML reader");

		try {
			Document doc = parser.build(reader);
			Element root = doc.getRootElement();

			logger.trace("validatePremisFileMinContent(): Read root element");
			// search rights part
			if (root.getChildElements("rights", "info:lc/xmlns/premis-v2").size() == 1)
				rightsPartExists = true;
			else{
				return false;
			}

			// search for sip-creation event
			Elements events = root.getChildElements("event",
					"info:lc/xmlns/premis-v2");
			for (int eventNum = 0; !sipCreationEventExists
					& eventNum < events.size(); eventNum++) {
				Element iterEvent = events.get(eventNum);
				// should be only one 'type'-child per event, and only one String-child in type-child
				Elements eventType = iterEvent.getChildElements("eventType","info:lc/xmlns/premis-v2");
				nu.xom.Node eventTypeNode = eventType.get(0).getChild(0);
				if (eventTypeNode.getValue().equals("SIP_CREATION")) {
					sipCreationEventExists = true;
				}

			}
		} catch (ValidityException ve) {
			throw new IOException(ve);
		} catch (ParsingException pe) {
			throw new IOException(pe);
		} catch (IOException ie) {
			throw new IOException(ie);
		}

		finally {
			reader.close();
		}
		return rightsPartExists & sipCreationEventExists;
	}

	/**
	 * The Class PremisResourceResolver.
	 */
	public static class PremisResourceResolver implements LSResourceResolver {

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.w3c.dom.ls.LSResourceResolver#resolveResource(java.lang.String,
		 * java.lang.String, java.lang.String, java.lang.String,
		 * java.lang.String)
		 */
		@Override
		public LSInput resolveResource(String type, String namespaceURI,
				String publicId, String systemId, String baseURI) {

			if (systemId.equals("http://www.loc.gov/standards/xlink/xlink.xsd")) {
				LSInput input = new DOMInputImpl();
				FileInputStream stream;
				try {
					stream = new FileInputStream(new File(C.XLINK_XSD_PATH));
				} catch (FileNotFoundException e) {
					throw new RuntimeException("File " + C.XLINK_XSD_PATH
							+ " not found", e);
				}
				input.setByteStream(stream);
				return input;
			}

			return null;
		}
	}
}
