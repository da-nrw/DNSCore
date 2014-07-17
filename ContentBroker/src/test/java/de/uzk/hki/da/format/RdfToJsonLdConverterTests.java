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
package de.uzk.hki.da.format;

import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import com.github.jsonldjava.utils.JSONUtils;

import de.uzk.hki.da.metadata.RdfToJsonLdConverter;
import de.uzk.hki.da.utils.Path;
import de.uzk.hki.da.utils.TC;


/**
 * The Class RdfToJsonLdConverterTests.
 */
public class RdfToJsonLdConverterTests {

	private static final String BASE_DIR = Path.make(TC.TEST_ROOT_FORMAT,"RdfToJsonLdConverterTests").toString();
	
	/**
	 * Test.
	 * @throws Exception 
	 */
	@Test
	public void test() throws Exception {
		
		File edmFile = new File(BASE_DIR+"/edm.rdf");
		String edm = FileUtils.readFileToString(edmFile);
		
		RdfToJsonLdConverter converter = new RdfToJsonLdConverter("conf/frame.jsonld");
		Map<String,Object> json = converter.convert(edm);
		
		assertNotNull(json);
		System.out.println(JSONUtils.toPrettyString(json));
		System.out.println(json.get("@graph").getClass());
		
	}

}
