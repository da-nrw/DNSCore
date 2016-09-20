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

import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.junit.Test;

/**
 * The Class PremisXmlXSDAvailabilityTest.
 * Test in not in the testsuite, becouse it requires internet availability
 * 
 */
public class PremisXmlXSDAvailabilityTest {
	
	/**
	 * Parametrs for preoxy can be looked in $JAVA_OPTS
	 * 
	 * @author Jens Peters
	 * @throws IOException
	 * @throws ParseException
	 */
	@Test
	public void testXSDWebAvailability() throws IOException, ParseException {
		String[] urlStrings = new String[] {
				"http://www.loc.gov/standards/premis/v2/premis-v2-2.xsd",
				"http://www.danrw.de/schemas/contract/v1/danrw-contract-1.xsd",
				"http://www.loc.gov/standards/xlink/xlink.xsd" };
		
		for (String urlString : urlStrings) {
			Source sourceFromXSD=null;
			try {
				URLConnection conn = new URL(urlString).openConnection();
				conn.setConnectTimeout(5000);
				conn.setReadTimeout(5000);
				sourceFromXSD = new StreamSource(conn.getInputStream());
			} catch (SocketTimeoutException e) {
				assertNotNull(urlString + " XSD unavailable", sourceFromXSD);
			}
		}
	}

}
