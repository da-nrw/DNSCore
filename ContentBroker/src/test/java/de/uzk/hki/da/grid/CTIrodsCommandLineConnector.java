package de.uzk.hki.da.grid;
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

import org.apache.commons.io.FilenameUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.uzk.hki.da.utils.FolderUtils;

/** ComponentTest of IrodsCommandLineConnector
 * @author Jens Peters
 * 
 */

import de.uzk.hki.da.utils.MD5Checksum;
import de.uzk.hki.da.utils.PropertiesUtils;

public class CTIrodsCommandLineConnector {
	
	private static final String PROPERTIES_FILE_PATH = "src/main/conf/config.properties.ci";
	private static String tmpDir = "/tmp/forkDir/";
	static String workingRescPhysicalPath = "/ci/storage/WorkArea";
	
	private static String zone;
	IrodsCommandLineConnector iclc;
	String dao =  "aip/connector/urn.tar";
	String dao2 = "aip/connector2/urn.tar";
	String dao3 = "aip/connector/urn3.tar";
	String daolong = "aip/connector/urnwithextraordinaryLongNameInsteadOfShortName.tar";
	File file;
	String md5sum;
	String testCollPhysicalPathOnLTA = "/ci/archiveStorage/aip/connector";
	String archiveStorage = "ciArchiveRescGroup";
	
	String workingResc = "ciWorkingResource";
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	
		Properties properties = readProperties();			
		zone = properties.getProperty("irods.zone");
	}
	
	
	@Before
	public void before() throws IOException, RuntimeException {
		new File(testCollPhysicalPathOnLTA+"/urn.tar").delete();
		iclc = new IrodsCommandLineConnector();
		dao = "/" + zone + "/"  + dao;
		dao2 = "/" + zone + "/"  + dao2;
		dao3 = "/" + zone + "/"  + dao3;
		daolong = "/" + zone + "/" + daolong; 
		
		file = createTestFile();
		String destColl = 
				FilenameUtils.getFullPathNoEndSeparator(dao);
		iclc.mkCollection(destColl);
		String destColl1 = 
				FilenameUtils.getFullPathNoEndSeparator(dao2);
		iclc.mkCollection(destColl1);
		String destColl2 = 
				FilenameUtils.getFullPathNoEndSeparator(dao3);
		iclc.mkCollection(destColl2);
		md5sum = MD5Checksum.getMD5checksumForLocalFile(file);
		if(iclc.exists(dao))
			iclc.remove(dao);
		assertTrue(iclc.put(file, dao, archiveStorage ));
	}
	
	@After
	public void remove() {
		iclc.remove(dao);
		iclc.remove(dao2);
		iclc.remove(dao3);
		iclc.remove(daolong);
		iclc.remove("/" + zone + "/aip/connector");
		iclc.remove("/" + zone + "/aip/connector2");

		new File(testCollPhysicalPathOnLTA+"/urn.tar").delete();
		}
	
	@AfterClass
	public static void cleanup () throws IOException {
		FolderUtils.deleteDirectorySafe(new File(tmpDir));
		FolderUtils.deleteQuietlySafe(new File(workingRescPhysicalPath + "/aip/connector/urn3.tar"));
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
		assertFalse(iclc.exists("/ci/aip/someFile.txt"));
	}

	@Test
	public void testExistsGetChecksum() {
		assertEquals(iclc.getChecksum(dao),md5sum);
	}

	@Test
	public void testComputeChecksumForce() {
		assertEquals(iclc.computeChecksumForce(dao),md5sum);
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
		assertTrue(out.contains("5"));
	}
	
	@Test
	public void testIputAndChecksumOfExtraOrdinaryLongName() throws IOException {
		assertTrue(iclc.put(file, daolong, archiveStorage ));
		assertEquals(md5sum, iclc.getChecksum(daolong));
	}
	
	@Test
	public void testGet() throws IOException {
		File get = new File(tmpDir + "urn2.tar");
		assertTrue(iclc.get(get,dao));
		assertFalse(iclc.get(new File(tmpDir + "urn2.tar"),dao));
		assertTrue(get.exists());
		assertEquals(md5sum, MD5Checksum.getMD5checksumForLocalFile(file));
	}
	
//	@Test
//	public void testIRsync() {
//		String destColl = 
//				FilenameUtils.getFullPathNoEndSeparator(dao2);
//		iclc.mkCollection(destColl);
//		iclc.rsync(dao, destColl, archiveStorage);
//		assertTrue(iclc.exists(dao2));
//		assertEquals(iclc.getChecksum(dao2),iclc.getChecksum(dao));
//	}
	
	@Test
	public void testIRepl() {
		assertFalse(new File(workingRescPhysicalPath + "/aip/connector/urn.tar").exists());
		iclc.repl(dao, workingResc);
		assertTrue(new File(workingRescPhysicalPath + "/aip/connector/urn.tar").exists());	
	}
	@Test
	public void testItrim() {
		iclc.repl(dao, workingResc);
		assertTrue(new File(workingRescPhysicalPath + "/aip/connector/urn.tar").exists());	
		iclc.itrim(dao, workingResc, 1);
		assertFalse(new File(workingRescPhysicalPath + "/aip/connector/urn.tar").exists());
		
	}

	@Test
	public void testIReg() throws IOException {
		String destColl = new File(dao3).getParentFile().getAbsolutePath();
		iclc.unregColl(destColl);
		assertFalse(iclc.exists(destColl));
		File fileToCreate=new File(workingRescPhysicalPath + "/aip/connector/urn3.tar");
		System.out.println("testIReg(): Try to create emptyFile " + fileToCreate.getAbsolutePath());
		System.out.println("testIReg(): destColl " + destColl);
		System.out.println("testIReg(): dao3 " + dao3);
		System.out.println("testIReg(): workingRescPhysicalPath " + workingRescPhysicalPath);
		fileToCreate.createNewFile();
		iclc.ireg(new File(workingRescPhysicalPath + "/aip/connector/"), workingResc, destColl, true);
		assertTrue(iclc.exists(destColl));;
		assertTrue(iclc.exists(dao3));
		assertTrue(new File(workingRescPhysicalPath + "/aip/connector/urn3.tar").exists());
	}
	
	//-----------------------------------------------

	private void destroyTestFileOnLongTermStorage() throws IOException {
		File testFile = new File(testCollPhysicalPathOnLTA+"/urn.tar");
		FileWriter writer = new FileWriter(testFile ,false);
		writer.write("Hallo Wie gehts? DESTROYED");
		writer.close(); 
	}
	
	private File testiRule() throws IOException {
		File testFile = new File(tmpDir + "test.r");
		FileWriter writer = new FileWriter(testFile ,false);
		writer.write("checkiRule { \n " +
		"*len=0;\n" +
		"msiStrlen(\"hallo\",*len);\n"
		+"}\n"
		+"INPUT null\n"
		+"OUTPUT *len");
		writer.close(); 
		
		return testFile;
	}
	
	
	
	
	
	private File createTestFile() throws IOException {
		new File(tmpDir).mkdir();
		File temp = new File(tmpDir + "urn.tar");
		FileWriter writer = new FileWriter(temp ,false);
		writer.write("Hallo Wie gehts?");
		writer.close();
		return temp;
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
	
	

}
