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

import static org.junit.Assert.fail;

import java.io.File;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.github.jsonldjava.utils.JSONUtils;

import de.uzk.hki.fedorest.Fedora;
import de.uzk.hki.fedorest.FedoraResult;

public class RdfToJsonLdConverterTest {

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
	public void test() {
		
		String pid = "danrw:131614-2014011528642".replace("+", ":");
		Fedora fedora = new Fedora("http://da-nrw-vm2.hki.uni-koeln.de:8080/fedora", "fedoraAdmin",de.uzk.hki.da.utils.PasswordUtils.decryptPassword("/MxDGbm6x9Uqoz4iM+C4EA=="));
		try {
			
			FedoraResult result = fedora.getDatastreamDissemination()
					.param("pid", pid)
					.param("dsID", "EDM")
					.execute();
			if (result.getStatus() != 200)
				throw new RuntimeException("Error getting EDM datastream for pid: " + pid);
			
			// transform EDM to JSON
			RdfToJsonLdConverter converter = new RdfToJsonLdConverter("conf/frame.jsonld");
			Map<String, Object> json = converter.convert(result.getContent());
			
			System.out.println(JSONUtils.toPrettyString(json));
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

}
