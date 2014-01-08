package de.uzk.hki.da.core;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;


/**
 * The Class UserAreaScannerTests.
 */
public class UserAreaScannerTests {

	/** The Constant basePath. */
	private static final String basePath = "src/test/resources/core/UserAreaScanner/";
	
	/** The Constant userAreaPath. */
	private static final String userAreaPath = basePath+"UserArea/";
	
	/** The Constant ingestAreaPath. */
	private static final String ingestAreaPath  = basePath+"IngestArea/";
	
	/** The Constant scanner. */
	private static final UserAreaScannerWorker scanner = new UserAreaScannerWorker();
	
	/**
	 * Sets the up before class.
	 */
	@BeforeClass
	public static void setUpBeforeClass() {
		
		scanner.setIngestAreaRootPath(ingestAreaPath);
		scanner.setUserAreaRootPath(userAreaPath);
	}
	
	/**
	 * Sets the up.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@Before
	public void setUp() throws IOException {
		
		
		// Simulate: User puts a collection to the webshare incoming direcory.
		
		FileUtils.copyDirectoryToDirectory(
				new File(basePath + "SampleFiles/Collection1"), 
				new File(userAreaPath+"TEST/incoming/"));
		FileUtils.copyDirectoryToDirectory(
				new File(basePath + "SampleFiles/Collection2"), 
				new File(userAreaPath+"TEST/incoming/"));
	}
	
	/**
	 * Tear down.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@After 
	public void tearDown() throws IOException {
		
		FileUtils.deleteDirectory(
				new File(userAreaPath+"TEST/incoming/Collection1"));
		FileUtils.deleteDirectory(
				new File(userAreaPath+"TEST/incoming/Collection2"));
		
		FileUtils.deleteDirectory(
				new File(ingestAreaPath+"TEST"));
		new File(ingestAreaPath+"TEST").mkdirs();
	}
	
	
	
	
	/**
	 * Dont move files of unfinished collections.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@Test
	public void dontMoveFilesOfUnfinishedCollections() throws IOException{
		
		scanner.moveCompletedCollections(userAreaPath+"TEST/incoming/",ingestAreaPath+"TEST/");
		
		assertFalse(new File(ingestAreaPath+"TEST/Collection2%2Fa.tgz").exists());
		assertFalse(new File(ingestAreaPath+"TEST/Collection2%2Fb.tgz").exists());
		assertFalse(new File(ingestAreaPath+"TEST/Collection2%2Fc.tgz").exists());
		
	}	
	
	/**
	 * Move files of finished collections to staging area.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@Test
	public void moveFilesOfFinishedCollectionsToStagingArea() throws IOException{
		
		scanner.moveCompletedCollections(userAreaPath+"TEST/incoming",ingestAreaPath+"TEST");
		
		assertTrue(new File(ingestAreaPath+"TEST/Collection1%2Fa.tgz").exists());
		assertTrue(new File(ingestAreaPath+"TEST/Collection1%2Fb.tgz").exists());
		assertTrue(new File(ingestAreaPath+"TEST/Collection1%2Fc.tgz").exists());
		
		assertFalse(new File(userAreaPath+"TEST/Collection1").exists());
	}
	
}
