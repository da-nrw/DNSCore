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
package de.uzk.hki.da.convert;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import de.uzk.hki.da.metadata.XsltGenerator;


/**
 * The Class XsltGeneratorTests.
 */
public class XsltGeneratorTests {

	/**
	 * Test.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@Test
	public void test() throws IOException {
		
		File file = new File("src/test/resources/convert/XsltConversionStrategyTests/data/ead_correct.xml");
		String content = FileUtils.readFileToString(file, Charset.forName("UTF-8"));
		
		XsltGenerator generator = new XsltGenerator("conf/xslt/edm/ead_to_edm.xsl", IOUtils.toInputStream(content, "utf-8"));
		generator.setParameter("urn", "urn:nbn:de:danrw-1-20111111");
		generator.setParameter("cho-base-uri", "http://data.danrw.de/cho/1-20111111");
		generator.setParameter("aggr-base-uri", "http://data.danrw.de/aggregation/1-20111111");
		String result = generator.generate();
		
		assertNotNull(result);
		assertFalse(result.isEmpty());
		
		System.out.println(result);
		
	}

}
