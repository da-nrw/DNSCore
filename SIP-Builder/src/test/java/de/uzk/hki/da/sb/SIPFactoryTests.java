/*
  DA-NRW Software Suite | SIP-Builder
  Copyright (C) 2014 Historisch-Kulturwissenschaftliche Informationsverarbeitung
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

package de.uzk.hki.da.sb;

import gov.loc.repository.bagit.Bag;
import gov.loc.repository.bagit.BagFactory;
import gov.loc.repository.bagit.utilities.SimpleResult;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.uzk.hki.da.main.SIPBuilder;
import de.uzk.hki.da.metadata.ContractRights;
import de.uzk.hki.da.pkg.ArchiveBuilder;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

/**
 * Class under test: SIPFactory
 * 
 * @author Thomas Kleinke
 */
public class SIPFactoryTests {

	String pathToResourcesFolder = "src/test/resources/SIPFactoryTests/";
	
	SIPFactory sipFactory = new SIPFactory();

	@Before
	public void setUp() {
		
		Logger logger = Logger.getRootLogger();	
	    logger.setLevel(Level.ERROR);
		
		new File(pathToResourcesFolder + "destination").mkdir();
		
		ProgressManager progressManager = mock(ProgressManager.class);
		MessageWriter messageWriter = mock(MessageWriter.class);
		de.uzk.hki.da.sb.Logger sipLogger = mock(de.uzk.hki.da.sb.Logger.class);
		
		sipFactory = new SIPFactory();
		sipFactory.setProgressManager(progressManager);
		sipFactory.setMessageWriter(messageWriter);
		sipFactory.setLogger(sipLogger);
		
		Properties properties = new Properties();
		try {
			properties.load(new InputStreamReader((ClassLoader.getSystemResourceAsStream("configuration/config.properties"))));
		} catch (FileNotFoundException e1) {
			System.exit(SIPFactory.Feedback.GUI_ERROR.toInt());
		} catch (IOException e2) {
			System.exit(SIPFactory.Feedback.GUI_ERROR.toInt());
		}
		SIPBuilder.setProperties(properties);
	}
	
	/**
	 * @throws IOException
	 */
	@After
	public void tearDown() throws IOException {
		
		FileUtils.deleteDirectory(new File(pathToResourcesFolder + "destination"));
	}
	
	/**
	 * Method under test: SIPFactory.startSIPBuilding()
	 * @throws Exception
	 */
	@Test
	public void testBuildSingleCompressedSIP() throws Exception {
		
		ContractRights rights = new ContractRights();
		rights.setConversionCondition("Keine");
		
		sipFactory.setSourcePath(pathToResourcesFolder + "singleFolder");
		sipFactory.setDestinationPath(pathToResourcesFolder + "destination");
		sipFactory.setKindofSIPBuilding(SIPFactory.KindOfSIPBuilding.SINGLE_FOLDER);
		sipFactory.setCompress(true);
		sipFactory.setContractRights(rights);
		
		sipFactory.startSIPBuilding();
		do {
			Thread.sleep(100);
		} while (sipFactory.isWorking());
		
		assertEquals(SIPFactory.Feedback.SUCCESS, sipFactory.getReturnCode());
		
		String pathToSIP = pathToResourcesFolder + "destination/singleFolder.tgz";
		
		ArchiveBuilder builder = new ArchiveBuilder();
		builder.unarchiveFolder(new File(pathToSIP), new File(pathToResourcesFolder + "destination"), true);
		
		File unpackedFolder = new File(pathToResourcesFolder + "destination/singleFolder");
		
		assertTrue(new File(unpackedFolder, "bag-info.txt").exists());
		assertTrue(new File(unpackedFolder, "bagit.txt").exists());
		assertTrue(new File(unpackedFolder, "manifest-md5.txt").exists());
		assertTrue(new File(unpackedFolder, "tagmanifest-md5.txt").exists());
		assertTrue(new File(unpackedFolder, "data/document.pdf").exists());
		assertTrue(new File(unpackedFolder, "data/image.tif").exists());
		assertTrue(new File(unpackedFolder, "data/premis.xml").exists());
				
		assertTrue(validateBagIt(unpackedFolder));
	}
	
	/**
	 * Method under test: SIPFactory.startSIPBuilding()
	 * @throws Exception
	 */
	@Test
	public void testBuildSingleUncompressedSIP() throws Exception {
		
		ContractRights rights = new ContractRights();
		rights.setConversionCondition("Keine");
		
		sipFactory.setSourcePath(pathToResourcesFolder + "singleFolder");
		sipFactory.setDestinationPath(pathToResourcesFolder + "destination");
		sipFactory.setKindofSIPBuilding(SIPFactory.KindOfSIPBuilding.SINGLE_FOLDER);
		sipFactory.setCompress(false);
		sipFactory.setContractRights(rights);
		
		sipFactory.startSIPBuilding();
		do {
			Thread.sleep(100);
		} while (sipFactory.isWorking());
		
		assertEquals(SIPFactory.Feedback.SUCCESS, sipFactory.getReturnCode());
		
		String pathToSIP = pathToResourcesFolder + "destination/singleFolder.tar";
		
		ArchiveBuilder builder = new ArchiveBuilder();
		builder.unarchiveFolder(new File(pathToSIP), new File(pathToResourcesFolder + "destination"), false);
		
		File unpackedFolder = new File(pathToResourcesFolder + "destination/singleFolder");
		
		assertTrue(new File(unpackedFolder, "bag-info.txt").exists());
		assertTrue(new File(unpackedFolder, "bagit.txt").exists());
		assertTrue(new File(unpackedFolder, "manifest-md5.txt").exists());
		assertTrue(new File(unpackedFolder, "tagmanifest-md5.txt").exists());
		assertTrue(new File(unpackedFolder, "data/document.pdf").exists());
		assertTrue(new File(unpackedFolder, "data/image.tif").exists());
		assertTrue(new File(unpackedFolder, "data/premis.xml").exists());
				
		assertTrue(validateBagIt(unpackedFolder));
	}
	
	/**
	 * Method under test: SIPFactory.startSIPBuilding()
	 * @throws Exception
	 */
	@Test
	public void testBuildMultipleSIPs() throws Exception {
		
		ContractRights rights = new ContractRights();
		rights.setConversionCondition("Keine");
		
		sipFactory.setSourcePath(pathToResourcesFolder + "multipleFolders");
		sipFactory.setDestinationPath(pathToResourcesFolder + "destination");
		sipFactory.setKindofSIPBuilding(SIPFactory.KindOfSIPBuilding.MULTIPLE_FOLDERS);
		sipFactory.setCompress(true);
		sipFactory.setContractRights(rights);
		
		sipFactory.startSIPBuilding();
		do {
			Thread.sleep(100);
		} while (sipFactory.isWorking());
		
		assertEquals(SIPFactory.Feedback.SUCCESS, sipFactory.getReturnCode());
		
		ArchiveBuilder builder = new ArchiveBuilder();
		
		String pathToSIP1 = pathToResourcesFolder + "destination/SIP_1.tgz";
				
		builder.unarchiveFolder(new File(pathToSIP1), new File(pathToResourcesFolder + "destination"), true);
		
		File unpackedFolder1 = new File(pathToResourcesFolder + "destination/SIP_1");
		
		assertTrue(new File(unpackedFolder1, "bag-info.txt").exists());
		assertTrue(new File(unpackedFolder1, "bagit.txt").exists());
		assertTrue(new File(unpackedFolder1, "manifest-md5.txt").exists());
		assertTrue(new File(unpackedFolder1, "tagmanifest-md5.txt").exists());
		assertTrue(new File(unpackedFolder1, "data/document.pdf").exists());
		
		assertTrue(validateBagIt(unpackedFolder1));
		
		String pathToSIP2 = pathToResourcesFolder + "destination/SIP_2.tgz";
		
		builder.unarchiveFolder(new File(pathToSIP2), new File(pathToResourcesFolder + "destination"), true);
		
		File unpackedFolder2 = new File(pathToResourcesFolder + "destination/SIP_2");
		
		assertTrue(new File(unpackedFolder2, "bag-info.txt").exists());
		assertTrue(new File(unpackedFolder2, "bagit.txt").exists());
		assertTrue(new File(unpackedFolder2, "manifest-md5.txt").exists());
		assertTrue(new File(unpackedFolder2, "tagmanifest-md5.txt").exists());
		assertTrue(new File(unpackedFolder2, "data/image.tif").exists());
		assertTrue(new File(unpackedFolder2, "data/image_2.tif").exists());
		
		assertTrue(validateBagIt(unpackedFolder2));
		
		String pathToSIP3 = pathToResourcesFolder + "destination/SIP_3.tgz";
		
		builder.unarchiveFolder(new File(pathToSIP3), new File(pathToResourcesFolder + "destination"), true);
		
		File unpackedFolder3 = new File(pathToResourcesFolder + "destination/SIP_3");
		
		assertTrue(new File(unpackedFolder3, "bag-info.txt").exists());
		assertTrue(new File(unpackedFolder3, "bagit.txt").exists());
		assertTrue(new File(unpackedFolder3, "manifest-md5.txt").exists());
		assertTrue(new File(unpackedFolder3, "tagmanifest-md5.txt").exists());
		assertTrue(new File(unpackedFolder3, "data/text.txt").exists());
		
		assertTrue(validateBagIt(unpackedFolder3));
	}
	
	/**
	 * Method under test: SIPFactory.startSIPBuilding()
	 * @throws Exception
	 */
	@Test
	public void testCreateCollection() throws Exception {
		
		ContractRights rights = new ContractRights();
		rights.setConversionCondition("Keine");
		
		sipFactory.setSourcePath(pathToResourcesFolder + "multipleFolders");
		sipFactory.setDestinationPath(pathToResourcesFolder + "destination");
		sipFactory.setKindofSIPBuilding(SIPFactory.KindOfSIPBuilding.MULTIPLE_FOLDERS);
		sipFactory.setCompress(true);
		sipFactory.setContractRights(rights);
		sipFactory.setCreateCollection(true);
		sipFactory.setCollectionName("testCollection");
		
		sipFactory.startSIPBuilding();
		do {
			Thread.sleep(100);
		} while (sipFactory.isWorking());
		
		assertEquals(SIPFactory.Feedback.SUCCESS, sipFactory.getReturnCode());
		
		File unpackedFolder = new File(pathToResourcesFolder + "destination/testCollection");
		
		assertTrue(new File(unpackedFolder, "bag-info.txt").exists());
		assertTrue(new File(unpackedFolder, "bagit.txt").exists());
		assertTrue(new File(unpackedFolder, "manifest-md5.txt").exists());
		assertTrue(new File(unpackedFolder, "tagmanifest-md5.txt").exists());
		assertTrue(new File(unpackedFolder, "data/SIP_1.tgz").exists());
		assertTrue(new File(unpackedFolder, "data/SIP_2.tgz").exists());
		assertTrue(new File(unpackedFolder, "data/SIP_3.tgz").exists());
		
		assertTrue(validateBagIt(unpackedFolder));
	}
	
	/**
	 * @param folder The folder to check
	 * @return true if the BagIt metadata is valid, otherwise false
	 */
	private boolean validateBagIt(File folder) {
		
		BagFactory bagFactory = new BagFactory();
		Bag bag = bagFactory.createBag(folder);
		SimpleResult result = bag.verifyValid();
		return result.isSuccess();
	}
}
