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


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileWriter;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.uzk.hki.da.core.Path;
import de.uzk.hki.da.model.Node;
import de.uzk.hki.da.model.StoragePolicy;


/**
 * The Class IrodsGridConnectorTest.
 */
public class IrodsGridFacadeTest {

	/** The ig. */
	IrodsGridFacade ig;
	
	/** The isc. */
	IrodsSystemConnector isc;
	
	/** The irods dir. */
	static String irodsDir = "/tmp/irods";
	
	/** The fork dir. */
	static String forkDir = "/tmp/fork/";
	
	/** The temp. */
	File temp;
	
	StoragePolicy sp ;
	
	/**
	 * Sets the up before class.
	 *
	 * @throws Exception the exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		}

	
	/**
	 * Tear down after class.
	 *
	 * @throws Exception the exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		 FileUtils.deleteDirectory(new File(irodsDir));
		 FileUtils.deleteDirectory(new File(forkDir));
	}
	
	/**
	 * Sets the up.
	 *
	 * @throws Exception the exception
	 */
	@Before
	public void setUp() throws Exception {
		
		isc = mock(IrodsSystemConnector.class);	
		ig = new IrodsGridFacade();
		ig.setIrodsSystemConnector(isc);
		when (isc.computeChecksum(anyString())).thenReturn("abc");
		when (isc.connect()).thenReturn(true);
		
		
		Node node = new Node();
		node.setWorkingResource("cacheresc");
		node.setGridCacheAreaRootPath(Path.make(irodsDir));
		node.setWorkAreaRootPath(Path.make(forkDir));
		node.setReplDestinations("lvr");
		ig.setLocalNode(node);
		when(isc.getZone()).thenReturn("da-nrw");
		sp = new StoragePolicy(node);
		new File(irodsDir).mkdir();
		new File(forkDir).mkdir();
		temp = new File(forkDir + "urn.tar");
		FileWriter writer = new FileWriter(temp ,false);
		writer.write("Hallo Wie gehts?");
		writer.close();  
	}
	
	/**
	 * Tear down.
	 *
	 * @throws Exception the exception
	 */
	@After
	public void tearDown() throws Exception {
	}
	
	/**
	 * Put file does not exist.
	 *
	 * @throws Exception the exception
	 */
	@Test 
	public void putFileDoesNotExist() throws Exception {
		
		ig.put(temp, "123456/urn.tar", sp);
		assertTrue(new File(irodsDir+ "/aip/123456/urn.tar").exists());
	}
	
	/**
	 * Put file already exists with applicable checksum.
	 *
	 * @throws Exception the exception
	 */
	@Test 
	public void putFileAlreadyExistsWithApplicableChecksum() throws Exception {
		FileUtils.copyFile(temp, new File(irodsDir+ "/aip/123456/urn.tar"));
		when ( isc.executeRule( anyString(), anyString()) )
		.thenReturn( "1" );
		
		when ( isc.fileExists(anyString())). thenReturn(true);
		
		assertEquals(true,ig.put(temp, "123456/urn.tar", sp));
		assertEquals(true, new File(irodsDir+ "/aip/123456/urn.tar").exists());
	}
	
	/**
	 * Put file already exists with not applicable checksum.
	 *
	 * @throws Exception the exception
	 * @author Jens Peters
	 * @author Daniel M. de Oliveira
	 */
	@Test 
	public void putFileAlreadyExistsWithNotApplicableChecksum() throws Exception {
		
		if (!new File(irodsDir+ "/aip/123456").exists() ) new File(irodsDir+ "/aip/123456").mkdirs(); 
		File dis = new File(irodsDir+ "/aip/123456/urn.tar");
		FileWriter writer = new FileWriter(dis ,false);
	    writer.write("Hallo Wie gehtsddfd?");
	    writer.close();
	     
		when ( isc.executeRule( anyString(), anyString()) )
		.thenReturn( "1" ) // -> replication solely on cache
		.thenReturn( "1" ); 
		when (isc.fileExists(anyString())).thenReturn(true);
		
		// assertEquals(false,ig.put(temp, "aip/123456/urn.tar"));
		assertEquals(true, temp.exists());
		assertEquals(true, ig.put(temp, "123456/urn.tar", sp));
		assertEquals(true, new File(irodsDir+ "/aip/123456/urn.tar").exists());
	}
	
	/**
	 * Put file already exists with more than one repls.
	 *
	 * @throws Exception the exception
	 */
	@Test 
	public void putFileAlreadyExistsWithMoreThanOneRepls() throws Exception {
		FileUtils.copyFile(temp, new File(irodsDir+ "/aip/123456/urn.tar"));
		when ( isc.executeRule( anyString(), anyString()) )
		.thenReturn( "2" );
		try {
			ig.put(temp, "123456/urn.tar",sp);
			assertTrue(false);
		} catch (Exception e) {
			
		}; 
		assertEquals(true, new File(irodsDir+ "/aip/123456/urn.tar").exists());
	}
	
	

}
