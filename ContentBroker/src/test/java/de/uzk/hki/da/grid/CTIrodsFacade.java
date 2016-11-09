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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

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

import de.uzk.hki.da.model.StoragePolicy;
import de.uzk.hki.da.utils.FolderUtils;
import de.uzk.hki.da.utils.MD5Checksum;
import de.uzk.hki.da.utils.Path;
import de.uzk.hki.da.utils.PropertiesUtils;

/**
 * Component testing for the irods Datagrid
 * 
 * @author Jens Peters
 * @author Daniel M. de Oliveira
 */
public class CTIrodsFacade {

	private static final String PROPERTIES_FILE_PATH = "src/main/conf/config.properties.ci";
	private static IrodsSystemConnector isc;
	private static IrodsGridFacade ig;
	
	private static final String BEANS_DIAGNOSTICS_IRODS = "classpath*:META-INF/beans-diagnostics.irods.xml";
	private static final String BEAN_NAME_IRODS_GRID_FACADE = "cb.implementation.grid";
	private static final String BEAN_NAME_IRODS_SYSTEM_CONNECTOR = "irodsSystemConnector";
	private static final String PROP_GRID_CACHE_AREA_ROOT_PATH = "localNode.gridCacheAreaRootPath";
	private static final String PROP_WORK_AREA_ROOT_PATH = "localNode.workAreaRootPath";
	
	private static final String aipFolder = "aip"; 
	private static String tmpDir = "/tmp/forkDir/";
	private static String testColl = "123456";
//	staticString aipDir = "/ci/archiveStorage/";
	private static String testCollLogicalPath = null;
	private static String testCollPhysicalPathOnGridCache = null;
	private static String testCollPhysicalPathOnLTA = null;
	
	File temp;
	public String md5sum = "";
	private static StoragePolicy sp ;
	
	/**
	 * @throws Exception the exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	
		createConfDir();
		Properties properties = readProperties();
		setUpGridInfrastructure(properties);
				
		testCollLogicalPath = "/"+ isc.getZone()+ "/"+ aipFolder +"/" + testColl;
		testCollPhysicalPathOnGridCache = properties.getProperty(PROP_GRID_CACHE_AREA_ROOT_PATH) + "/" + aipFolder+"/"+testColl;
		testCollPhysicalPathOnLTA = "/ci/archiveStorage/"+aipFolder+"/"+testColl;
		
		
		sp = new StoragePolicy();
		sp.setMinNodes(1);
		sp.setReplDestinations("ciArchiveRescGroup");
		sp.setWorkingResource("ciWorkingResource");
		sp.setGridCacheAreaRootPath(properties.getProperty(PROP_GRID_CACHE_AREA_ROOT_PATH));
		sp.setCommonStorageRescName("ciArchiveRescGroup");

	}
	
	
	
	/**
	 * @throws Exception the exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		FolderUtils.deleteDirectorySafe(new File(tmpDir));
		removeConfDir();
	}
	
	
	/**
	 * @throws Exception the exception
	 */
	@Before
	public void setUp() throws Exception {
		temp = createTestFile();
		md5sum = MD5Checksum.getMD5checksumForLocalFile(temp);
	}
	
	/**
	 * Tear down.
	 *
	 * @throws Exception the exception
	 */
	@After
	public void tearDown() throws Exception {
		FolderUtils.deleteQuietlySafe(new File(testCollPhysicalPathOnGridCache));
		FolderUtils.deleteQuietlySafe(new File(testCollPhysicalPathOnLTA));
		isc.removeCollectionAndEatException(testCollLogicalPath);
	}
	
	/**
	 * Put file does not exist.
	 * @author Jens Peters
	 * @throws Exception the exception
	 */
	@Test 
	public void putFileDoesNotExist() throws Exception {
		putFileAndWaitUntilReplicatedAccordingToStoragePolicy();
		assertTrue(new File(testCollPhysicalPathOnLTA + "/urn.tar").exists());
	}
	
	/**
	 * @author Jens Peters
	 * @author Daniel M. de Oliveira
	 */
	@Test 
	public void mustNotPutFileAgainWhenAlreadyHasReplsOnLongTermStorage() throws Exception {
			putFileAndWaitUntilReplicatedAccordingToStoragePolicy();
			assertFalse(ig.put(temp, testColl + "/urn.tar", sp, null));
		}
	
	
	
	
	
	
	/**
	 * Test that the checksum AVU Metadata exists
	 * @throws Exception the exception
	 * @author Jens Peters
	 */
	@Test 
	public void testChecksumAVUMetadata() throws Exception {
		putFileAndWaitUntilReplicatedAccordingToStoragePolicy();
		String cs1 = isc.getAVUMetadataDataObjectValue(testCollLogicalPath+"/urn.tar", "chksum");
		String cs2 = isc.getChecksum(testCollLogicalPath + "/urn.tar");
		assertTrue(cs1.length()>10);
		assertEquals(cs1,cs2);
	} 
	
	
	/**
	 * Test that a given wrong Checksum is evaluated correctly.
	 * @author Jens Peters
	 */
	@Test
	public void testFilePutWithWrongChecksumCausesIOException() {
		
		try {
			ig.put(temp, testColl + "/urn.tar", sp, "abababsbsbsbw2");
			fail();
		} catch (IOException e) {
			System.out.println("catched exception as intended!");
		}
		
	}
	
	/**
	 * Test that a given correct and wrong Checksum is evaluated correctly on storagePolicyAchieved.
	 * @author Jens Peters
	 */
	@Test
	public void testFilePutAndStoragePolicyAchieved() throws IOException {
		String aip = testColl + "/urn.tar";
		assertTrue(ig.put(temp, aip, sp, md5sum));
		assertTrue(ig.storagePolicyAchieved(aip, sp, md5sum, null));
		assertFalse(ig.storagePolicyAchieved(aip, sp, "ddlldld", null));
	}
	
	/**
	 * Test that minNodes is evaluated correctly
	 *   @author Jens Peters
	 * @throws IOException 
	 */
	@Test
	public void storagePolicyIsNotAchievedDueToCopiesNotReached() throws IOException {
		StoragePolicy sp2 = new StoragePolicy();
		sp2.setMinNodes(10);
		sp2.setReplDestinations("lza");
		sp2.setWorkingResource("ciWorkingResource");
		sp2.setGridCacheAreaRootPath(sp.getGridCacheAreaRootPath());
		sp2.setCommonStorageRescName("ciArchiveRescGroup");
		String aip = testColl + "/urn.tar";
		sp2.setMinNodes(10);
		assertTrue(ig.put(temp, aip, sp2, md5sum));
		assertFalse(ig.storagePolicyAchieved(aip, sp2, md5sum, null));
	}
	
	/**
	 * Test that a given Checksum is evaluated correctly.
	 * @author Jens Peters
	 */
	@Test
	public void testFilePutWithChecksum() {
		
		try {
			assertTrue(ig.put(temp, testColl + "/urn.tar", sp, md5sum));
	
		} catch (IOException e) {
			fail();
		}
		
	}
	
	
	/**
	 * Destroy checksum in Long term storage and verify
	 * that grid facade diagnoses it as not valid.
	 * 
	 * @author Jens Peters
	 * @author Daniel M. de Oliveira
	
	@Test
	public void destroyChecksumInStorage() throws Exception {
		
		putFileAndWaitUntilReplicatedAccordingToStoragePolicy();
		assertTrue(ig.isValid(testColl + "/urn.tar"));
		destroyTestFileOnLongTermStorage();
		assertFalse(ig.isValid(testColl + "/urn.tar"));
	}
	 */
	//-----------------------------------------------------------------
	
	
	
	private void destroyTestFileOnLongTermStorage() throws IOException {
		File testFile = new File(testCollPhysicalPathOnLTA+"/urn.tar");
		FileWriter writer = new FileWriter(testFile ,false);
		writer.write("Hallo Wie gehts? DESTROYED");
		writer.close(); 
	}
	
	
	
	private void putFileAndWaitUntilReplicatedAccordingToStoragePolicy() throws InterruptedException, IOException {
		
		assertTrue(ig.put(temp, testColl + "/urn.tar", sp, null));
		
		while (true) {
			if (ig.storagePolicyAchieved(testColl + "/urn.tar", sp, null, null)) break;
			Thread.sleep(1000);
		}
	}
	
	
	
	/**
	 * Sets up 
	 * <li>node
	 * <li>irodsGridConnector
	 * <li>gridFacade
	 * @param properties
	 */
	private static void setUpGridInfrastructure(Properties properties) {
		
		AbstractApplicationContext context =
				new ClassPathXmlApplicationContext(BEANS_DIAGNOSTICS_IRODS);
		isc = (IrodsSystemConnector) context.getBean(BEAN_NAME_IRODS_SYSTEM_CONNECTOR);
		ig = (IrodsGridFacade) context.getBean(properties.getProperty(BEAN_NAME_IRODS_GRID_FACADE));
		
		ig.setIrodsSystemConnector(isc);
		
		sp = new StoragePolicy();
		sp.setWorkingResource("ciWorkingResource");
		sp.setGridCacheAreaRootPath(Path.make(properties.getProperty(PROP_GRID_CACHE_AREA_ROOT_PATH)).toString());
		sp.setWorkAreaRootPath(Path.make(properties.getProperty(PROP_WORK_AREA_ROOT_PATH)).toString());
		sp.setReplDestinations("ciArchiveResourceGroup");
		
		context.close();
	}

	private static Properties readProperties() throws IOException {
	
		Properties properties = null;
		File propertiesFile = new File (PROPERTIES_FILE_PATH);
		try {
			properties = PropertiesUtils.read(propertiesFile);
		} catch (IOException e) {
			System.out.println("error while reading " + propertiesFile);
			return null;
		}
		return properties;
	}

	private static void createConfDir() throws IOException {
		new File("conf").mkdir();
		FileUtils.copyFile(new File(PROPERTIES_FILE_PATH), new File("conf/config.properties"));
	}

	private static void removeConfDir() {
		FolderUtils.deleteQuietlySafe(new File("conf")); 
	}

	private File createTestFile() throws IOException {
		
		new File(tmpDir).mkdir();
		File temp = new File(tmpDir + "urn.tar");
		FileWriter writer = new FileWriter(temp ,false);
		writer.write("Hallo Wie gehts?");
		writer.close();
	
		return temp;
	}
}
