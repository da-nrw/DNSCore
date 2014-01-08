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

import org.junit.Test;

import de.uzk.hki.da.metadata.XmpCollector;


/**
 * The Class XmpCollectorTests.
 */
public class XmpCollectorTests {

	/**
	 * Test.
	 */
	@Test
	public void test() {
		
		File folder = new File("src/test/resources/metadata/XmpCollectorTests");
		File targetFile = new File("src/test/resources/metadata/XmpCollectorTests/XMP.rdf");
		
		XmpCollector.collect(folder, targetFile);
		
		assertTrue(targetFile.exists());
		
		//targetFile.delete();
		
	}

}
