/*
  DA-NRW Software Suite | ContentBroker
  Copyright (C) 2014 LVRInfoKom
  Landschaftsverband Rheinland

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


import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.uzk.hki.da.test.CTTestHelper;
import de.uzk.hki.da.test.TC;
import de.uzk.hki.da.util.Path;
import de.uzk.hki.da.utils.CommandLineConnector;

/**
 * @author Daniel M. de Oliveira
 */
public class CTFileFormatFacadeExtractTests {

	private static final Path testRoot = Path.make(TC.TEST_ROOT_FORMAT,"CTFileFormatFacadeExtract");
	private static final ConfigurableFileFormatFacade fff = new ConfigurableFileFormatFacade();;
	private static final JhoveMetadataExtractor metadataExtractor = new JhoveMetadataExtractor();
	
	@BeforeClass
	public static void setUpBeforeClass() {
		metadataExtractor.setCli(new CommandLineConnector());
		fff.setMetadataExtractor(metadataExtractor);
		fff.setFormatScanService(new FakeFormatScanService());
	}
	
	
	@Before
	public void setUp() throws IOException {
		
		CTTestHelper.prepareWhiteBoxTest();
	}
	
	@After
	public void tearDown() throws IOException {
		CTTestHelper.cleanUpWhiteBoxTest();
	}
	
	@Test
	public void extractEAD() {
		assertTrue(fff.connectivityCheck());
		
		try {
			fff.extract(Path.makeFile(testRoot,"vda3.XML"), Path.makeFile(testRoot,"vda3.XML.output"));
		} catch (IOException e) {
			fail();
		} catch (ConnectionException e) {
			fail();
		}
	}
	
	@Test
	public void extractorNotConnected() {
		CTTestHelper.cleanUpWhiteBoxTest();

		try {
			fff.extract(Path.makeFile(testRoot,"vda3.XML"), Path.makeFile(testRoot,"vda3.XML.output"));
			fail();
		} catch (IOException e) {
			fail();
		} catch (ConnectionException e) {
			assertTrue(e!=null);
		}
	}
	
	
	
}
