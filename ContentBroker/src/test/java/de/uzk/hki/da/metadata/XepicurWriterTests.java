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
package de.uzk.hki.da.metadata;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.junit.After;
import org.junit.Test;
import org.xml.sax.SAXException;


/**
 * The Class XepicurWriterTests.
 */
public class XepicurWriterTests {

	/**
	 * Tear down.
	 */
	@After
	public void tearDown() {
		new File("src/test/resources/metadata/epicur.xml").delete();
	}
	
	/**
	 * Test create xepicur.
	 *
	 * @throws SAXException the sAX exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@Test
	public void testCreateXepicur() throws SAXException, IOException {
		
		XepicurWriter.createXepicur(
			"1-2011111110",
			"METS",
			"http://dfg-viewer.de/show/?set[mets]=",
			"src/test/resources/metadata/","urn:nbn","url/file"
		);
		
		assertTrue(new File("src/test/resources/metadata/epicur.xml").exists());
		
		SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		Schema schema = sf.newSchema();
		Validator validator = schema.newValidator();
		Source source = new StreamSource("src/test/resources/metadata/epicur.xml");
		validator.validate(source);
		
	}
	
}
