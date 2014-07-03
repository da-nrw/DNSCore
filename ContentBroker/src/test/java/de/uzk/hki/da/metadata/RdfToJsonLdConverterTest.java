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

/**
 * @Author Jens Peters
 * Added to test proper UTF-8 parsing of Fedora to ElasticSearch index
 */
package de.uzk.hki.da.metadata;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.github.jsonldjava.core.JSONLDProcessingError;
import com.github.jsonldjava.utils.JSONUtils;

public class RdfToJsonLdConverterTest {
	
	private static final String pathToRdfFile = "src/test/resources/metadata/rdf_to_jsonld_test.rdf";

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		FileUtils.copyFile(new File("src/main/resources/frame.jsonld"), new File("conf/frame.jsonld"));
	}

	@After
	public void tearDown() throws Exception {
		 new File("conf/frame.jsonld").delete();
	}

	@Test
	public void test() throws Exception {
			
		// transform EDM to JSON
		RdfToJsonLdConverter converter = new RdfToJsonLdConverter("conf/frame.jsonld");
		FileInputStream rdfStream = new FileInputStream(new File(pathToRdfFile));
		Map<String, Object> json = converter.convert(IOUtils.toString(rdfStream, "UTF-8"));
		
		System.out.println(JSONUtils.toPrettyString(json));
			
	}

}
