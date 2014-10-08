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
import java.io.IOException;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.apache.xerces.dom.DOMInputImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.SAXException;

import de.uzk.hki.da.core.C;


/**
 * The Class PremisXmlValidator.
 */
public class PremisXmlValidator {

	/** The logger. */
	private static Logger logger = LoggerFactory.getLogger(PremisXmlValidator.class);
	
	/**
	 * Validate premis file.
	 *
	 * @param premisFile the premis file
	 * @return true if valid, false otherwise
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @author Thomas Kleinke
	 * @author Daniel M. de Oliveira
	 */
	public static boolean validatePremisFile(File premisFile) throws IOException {

		if (!premisFile.exists())
			throw new FileNotFoundException();
		
		Source fileSource = new StreamSource(premisFile);
		
		SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		factory.setResourceResolver(new PremisResourceResolver());
		
		Source schemaFile = new StreamSource(new File(C.PREMIS_XSD_PATH));
		Schema schema = null;
		try {
			schema = factory.newSchema(schemaFile);
			Validator validator = schema.newValidator();
			validator.validate(fileSource);
		} catch (SAXException e) {
			logger.error(e.getMessage());
			return false;
		}

		return true;
	}
	
	/**
	 * The Class PremisResourceResolver.
	 */
	public static class PremisResourceResolver implements LSResourceResolver {
		
		/* (non-Javadoc)
		 * @see org.w3c.dom.ls.LSResourceResolver#resolveResource(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
		 */
		@Override
		public LSInput resolveResource(String type, String namespaceURI, String publicId,
				String systemId, String baseURI) {
			
			if (systemId.equals("http://www.loc.gov/standards/xlink/xlink.xsd")) {
				LSInput input = new DOMInputImpl();
				FileInputStream stream;
				try {
					stream = new FileInputStream(new File(C.XLINK_XSD_PATH));
				} catch (FileNotFoundException e) {
					throw new RuntimeException("File " + C.XLINK_XSD_PATH + " not found", e);
				}
				input.setByteStream(stream);
				return input;
			}			 
			
			return null;
		}
	}
}
