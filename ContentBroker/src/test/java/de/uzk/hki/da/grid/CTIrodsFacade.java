package de.uzk.hki.da.grid;
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

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import de.uzk.hki.da.core.C;
import de.uzk.hki.da.model.Node;
import de.uzk.hki.da.model.StoragePolicy;
import de.uzk.hki.da.util.Path;
import de.uzk.hki.da.utils.Utilities;

/**
 * Component testing for the irods Datagrid
 * 
 * @author jpeters
 *
 */

public class CTIrodsFacade {

	IrodsGridFacade ig;
	
	private static final String BEANS_DIAGNOSTICS_IRODS = "classpath*:META-INF/beans-diagnostics.irods.xml";
	private static final String BEAN_NAME_IRODS_GRID_FACADE = "cb.implementation.grid";
	
	private static final String BEAN_NAME_IRODS_SYSTEM_CONNECTOR = "irodsSystemConnector";
	private static final String PROP_GRID_CACHE_AREA_ROOT_PATH = "localNode.gridCacheAreaRootPath";
	private static final String PROP_WORK_AREA_ROOT_PATH = "localNode.workAreaRootPath";
	
	static final String aipFolder = "aip"; 
	
	/** The isc. */
	IrodsSystemConnector isc;
	
	/** The fork dir. */
	static String tmpDir = "/tmp/forkDir/";
	
	/** The fork dir. */
	static String testColl = "123456";
	
	
	/** AIP dir **/
	static String aipDir = "/ci/archiveStorage/";
	
	String logicalPath = null;
	
	/** The temp. */
	File temp;
	
	StoragePolicy sp ;
	
	Properties properties = null;
	
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
		 FileUtils.deleteDirectory(new File(tmpDir));
	}
	
	/**
	 * Sets the up.
	 *
	 * @throws Exception the exception
	 */
	@Before
	public void setUp() throws Exception {
		File conf = new File("conf");
		conf.mkdir();
		
		FileUtils.copyFile(new File ("src/main/conf/config.properties.ci"), new File(C.CONFIG_PROPS));
		try {
			properties = Utilities.read(new File(C.CONFIG_PROPS));
		} catch (IOException e) {
			System.out.println("error while reading " + C.CONFIG_PROPS);

		}

		AbstractApplicationContext context =
				new ClassPathXmlApplicationContext(BEANS_DIAGNOSTICS_IRODS);
		isc = (IrodsSystemConnector) context.getBean(BEAN_NAME_IRODS_SYSTEM_CONNECTOR);
		ig = (IrodsGridFacade) context.getBean(properties.getProperty(BEAN_NAME_IRODS_GRID_FACADE));
		
		ig.setIrodsSystemConnector(isc);
				
		Node node = new Node();
		node.setWorkingResource("ciWorkingResource");
		node.setGridCacheAreaRootPath(Path.make(properties.getProperty(PROP_GRID_CACHE_AREA_ROOT_PATH)));
		node.setWorkAreaRootPath(Path.make(properties.getProperty(PROP_WORK_AREA_ROOT_PATH)));
		node.setReplDestinations("ciArchiveResourceGroup");
		ig.setLocalNode(node);
		
		sp = new StoragePolicy(node);
		sp.setMinNodes(1);
		new File(tmpDir).mkdir();
		temp = new File(tmpDir + "urn.tar");
		FileWriter writer = new FileWriter(temp ,false);
		writer.write("Hallo Wie gehts?");
		writer.close(); 
		
		logicalPath = "/"+ isc.getZone()+ "/"+ aipFolder +"/" + testColl;
		
		context.close();
	}
	
	/**
	 * Tear down.
	 *
	 * @throws Exception the exception
	 */
	@After
	public void tearDown() throws Exception {
		isc.removeCollectionAndEatException(logicalPath);
		FileUtils.deleteQuietly(new File(C.CONFIG_PROPS)); 
	}
	
	/**
	 * Put file does not exist.
	 * @author Jens Peters
	 * @throws Exception the exception
	 */
	@Test 
	public void putFileDoesNotExist() throws Exception {
		isc.removeCollectionAndEatException(logicalPath);
		assertTrue(ig.put(temp, testColl + "/urn.tar", sp));
		assertTrue(new File(properties.getProperty(PROP_GRID_CACHE_AREA_ROOT_PATH) + "/" + aipFolder+"/"+testColl + "/urn.tar").exists());
	}
	
	/**
	 * Put file already exists
	 * @author Jens Peters
	 * @throws Exception the exception
	 */
	@Test 
	public void putFileAlreadyExists() throws Exception {
		isc.removeCollectionAndEatException(logicalPath);
		assertTrue(ig.put(temp, testColl + "/urn.tar", sp));
		assertTrue(new File(properties.getProperty(PROP_GRID_CACHE_AREA_ROOT_PATH) + "/" + aipFolder+"/"+testColl + "/urn.tar").exists());
		
		try {
		ig.put(temp, testColl + "/urn.tar", sp);
		} catch (IrodsRuntimeException irex) {
			assertTrue(true);
			return;
		}
		fail();
		}
	
	/**
	 * Put file and test replications
	 *
	 * @throws Exception the exception
	 * @author Jens Peters
	 */
	@Test 
	public void putFileAndTestReplications() throws Exception {
		isc.removeCollectionAndEatException(logicalPath);
		assertTrue(ig.put(temp, testColl + "/urn.tar", sp));
		assertTrue(ig.isValid(testColl + "/urn.tar"));
		
		while (true) {
			if (ig.storagePolicyAchieved(testColl + "/urn.tar", sp)) break;
			System.out.println("Storage Policy not yet achieved");
			Thread.sleep(3000);
		}
	}
	
	/**
	 * Test that the checksum AVU Metadata exists
	 * @throws Exception the exception
	 * @author Jens Peters
	 */
	@Test 
	public void testChecksumAVUMetadata() throws Exception {
		assertTrue(ig.put(temp, testColl + "/urn.tar", sp));
		String cs1 = isc.getAVUMetadataDataObjectValue(logicalPath+"/urn.tar", "chksum");
		String cs2 = isc.getChecksum(logicalPath + "/urn.tar");
		assertTrue(cs1.length()>10);
		assertEquals(cs1,cs2);
	} 
	
	/**
	 * Destroy checksum in Long term storage
	 * @author Jens Peters
	 */
	@Test
	public void destroyChecksumInStorage() throws Exception {
		temp = new File(aipDir + "urn.tar");
		FileWriter writer = new FileWriter(temp ,false);
		writer.write("Hallo Wie gehts? DESTROYED");
		writer.close(); 
		assertFalse(ig.isValid(testColl + "/urn.tar"));
	}
	
	
	
	
}
