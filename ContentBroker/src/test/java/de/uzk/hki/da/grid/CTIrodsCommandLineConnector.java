package de.uzk.hki.da.grid;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;


/**
 * @author Jens Peters
 * 
 */


import de.uzk.hki.da.utils.MD5Checksum;

public class CTIrodsCommandLineConnector {
	
	IrodsCommandLineConnector iclc;
	String dao =  "/c-i/aip/TEST/urn.tar";
	String dao2 = "/c-i/aip/TEST2/urn.tar";
	private static String tmpDir = "/tmp/forkDir/";
	File file;
	String md5sum;
	String testCollPhysicalPathOnLTA = "/ci/archiveStorage/aip/TEST";
	String archiveStorage = "ciArchiveResource";
	
	@Before
	public void before() throws IOException {
		iclc = new IrodsCommandLineConnector();
		file = createTestFile();
		md5sum = MD5Checksum.getMD5checksumForLocalFile(file);
		assertTrue(iclc.put(file, dao, archiveStorage ));
	}
	@Test
	public void testPut() throws IOException {

		assertFalse(iclc.put(file, dao));
		assertTrue(iclc.remove(dao));
		assertTrue(iclc.put(file, dao));
	}

	@Test
	public void testRemove() throws IOException {

		assertTrue(iclc.exists(dao));
		assertTrue(iclc.remove(dao));
		assertFalse(iclc.remove(dao));
		assertFalse(iclc.exists(dao));
	}

	@Test
	public void testExists() {
		assertTrue(iclc.exists(dao));
		assertFalse(iclc.exists("/c-i/aip/someFile.txt"));
	}

	@Test
	public void testExistsGetChecksum() {
		assertEquals(iclc.getChecksum(dao),md5sum);
	}

	@Test
	public void testComputeChecksumForce() {
		assertEquals(iclc.computeChecksumForce(dao),md5sum);
	}
	
	@After
	public void remove() {
		iclc.remove(dao);
		iclc.remove(dao2);
	}
	
	@AfterClass
	public static void cleanup () {
		FileUtils.deleteQuietly(new File(tmpDir));
	}

	@Test
	public void testGetChecksum() {
		assertEquals(iclc.computeChecksumForce(dao),md5sum);
	}
	
	@Test
	public void testExistsWithChecksum() {
		assertTrue(iclc.existsWithChecksum(dao, md5sum));
	}
	
	@Test 
	public void testIsValid() {
		assertTrue(iclc.isValid(dao));
	}
	
	@Test
	public void destroyedFile() throws IOException {
		destroyTestFileOnLongTermStorage();
		assertFalse(iclc.isValid(dao));
		assertTrue(iclc.existsWithChecksum(dao, md5sum));
		iclc.computeChecksumForce(dao);
		assertFalse(iclc.existsWithChecksum(dao, md5sum));
		
	}
	
	@Test
	public void testAVUSetting() {
		iclc.setIMeta(dao, "TEST", "123456");
		assertEquals("123456",iclc.iquestDataObjectForAVU(dao, "TEST"));
	}
	
	@Test
	public void testIrule() throws IOException {
		String out = iclc.executeIrule(testiRule());
		assertTrue(out.contains("numberOfCopies = 1"));
	}
	
	@Test
	public void testGet() throws IOException {
		File get = new File(tmpDir + "urn2.tar");
		assertTrue(iclc.get(get,dao));
		assertFalse(iclc.get(new File(tmpDir + "urn2.tar"),dao));
		assertTrue(get.exists());
		assertEquals(md5sum, MD5Checksum.getMD5checksumForLocalFile(file));
	}
	
	@Test
	public void testIRsync() {
		String destColl = 
				FilenameUtils.getFullPathNoEndSeparator(dao2);
		iclc.mkCollection(destColl);
		iclc.rsync(dao, destColl, archiveStorage);
		assertTrue(iclc.exists(dao2));
		assertEquals(iclc.getChecksum(dao2),iclc.getChecksum(dao));
	}
	
	//-----------------------------------------------
	
	private File createTestFile() throws IOException {
		new File(tmpDir).mkdir();
		File temp = new File(tmpDir + "urn.tar");
		FileWriter writer = new FileWriter(temp ,false);
		writer.write("Hallo Wie gehts?");
		writer.close();
		return temp;
	}
	
	private void destroyTestFileOnLongTermStorage() throws IOException {
		File testFile = new File(testCollPhysicalPathOnLTA+"/urn.tar");
		FileWriter writer = new FileWriter(testFile ,false);
		writer.write("Hallo Wie gehts? DESTROYED");
		writer.close(); 
	}
	
	private File testiRule() throws IOException {
		File testFile = new File(tmpDir + "test.r");
		FileWriter writer = new FileWriter(testFile ,false);
		writer.write("checkNumberTest { \n " +
		"*numberOfCopies=0;\n" +
		"acGetNumberOfCopies(*dao,*numberOfCopies);\n"
		+"}\n"
		+"INPUT *dao=\""+dao+"\"\n"
		+"OUTPUT *numberOfCopies");
		writer.close(); 
		return testFile;
	}
	
	

}
