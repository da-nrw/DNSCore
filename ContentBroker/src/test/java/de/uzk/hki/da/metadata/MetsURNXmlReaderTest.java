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
import java.text.ParseException;

import org.junit.Test;

import de.uzk.hki.da.model.RightsSectionURNMetsXmlReader;


/**
 * The Class MetsURNXmlReaderTest.
 *
 * @author Thomas Kleinke
 */
public class MetsURNXmlReaderTest {

	/** The path to test file. */
	String pathToTestFile = "src/test/resources/metadata/mets_urn_xml_reader_test.xml";
	
	/**
	 * Test read xml from mets file.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws ParseException the parse exception
	 */
	@Test
	public void testReadXmlFromMetsFile() throws IOException, ParseException {
		RightsSectionURNMetsXmlReader urnReader = new RightsSectionURNMetsXmlReader();
		String urn = urnReader.readURN(new File(pathToTestFile));
		
		assertEquals("urn:nbn:de:hbz:5:1-16152", urn);
	}
}
