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

package de.uzk.hki.da.grid;


import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.uzk.hki.da.grid.IrodsFederatedGridFacade;
import de.uzk.hki.da.grid.IrodsSystemConnector;
import de.uzk.hki.da.model.Node;
import de.uzk.hki.da.model.StoragePolicy;


/**
 * @author Jens Peters
 */
public class CTIrodsFederatedGridFacadeTest {

	/** The fork dir. */
	static String forkDir = "/tmp/fork/";
	IrodsSystemConnector isc = new IrodsSystemConnector(
			"rods", "WpXlLLg3a4/S/iYrs6UhtQ==", "cihost", "c-i", "ciWorkingResource");
	
	
	/** The temp. */
	File temp;
	
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		 //FileUtils.deleteDirectory(new File(forkDir));
	}

	@Before
	public void setUp() throws Exception {
		new File(forkDir).mkdir();
		temp = new File(forkDir + "urn.tar");
		FileWriter writer = new FileWriter(temp ,false);
		writer.write("Hallo Wie gehts?");
		writer.close();

	}

	@After
	public void tearDown() throws Exception {
		isc.removeFile("/c-i/aip/TEST/12345/12345.pack_1.tar");
		isc.removeFile("/somewhere/aip/TEST/12345/12345.pack_1.tar");
	}
	
	@Test
	public void testPut () {

		IrodsSystemConnector isc = new IrodsSystemConnector("rods", "WpXlLLg3a4/S/iYrs6UhtQ==", "cihost", "c-i", "ciWorkingResource");
		IrodsFederatedGridFacade fg = new IrodsFederatedGridFacade();
		fg.setIrodsSystemConnector(isc);
		
		ArrayList<String> dest = new ArrayList<String>();
		dest.add("c-i");
		dest.add("somewhere");
		
		Node node = mock (Node.class);
		
		StoragePolicy sp = new StoragePolicy(node);
		sp.setDestinations(dest);
		
		assertTrue(sp.isPolicyAchievable());
	
		sp.setMinNodes(2);
		String gridPath = "/aip/TEST/12345/12345.pack_1.tar";
		
		fg.put(temp, gridPath, sp);
		
		isc.connect();
		assertTrue(isc.fileExists("/c-i/aip/TEST/12345/12345.pack_1.tar"));
		assertTrue(isc.fileExists("/somewhere/aip/TEST/12345/12345.pack_1.tar"));
		
		isc.logoff();
		assertTrue(fg.storagePolicyAchieved(gridPath, sp));
	}
}
