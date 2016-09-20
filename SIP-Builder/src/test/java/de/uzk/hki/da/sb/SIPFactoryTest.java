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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import gov.loc.repository.bagit.Bag;
import gov.loc.repository.bagit.BagFactory;
import gov.loc.repository.bagit.utilities.SimpleResult;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Properties;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.TTCCLayout;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.uzk.hki.da.cli.CliMessageWriter;
import de.uzk.hki.da.main.SIPBuilder;
import de.uzk.hki.da.metadata.ContractRights;
import de.uzk.hki.da.pkg.SipArchiveBuilder;
import de.uzk.hki.da.sb.MessageWriter.UserInput;

/**
 * Class under test: SIPFactory
 * 
 * @author Thomas Kleinke
 */
public class SIPFactoryTest {

	private Logger logger = Logger.getLogger(SIPFactory.class);
	String pathToResourcesFolder = "src/test/resources/SIPFactoryTests/";
	SIPFactory sipFactory = new SIPFactory();
	CliMessageWriter cliMessageWriter = new CliMessageWriter();

	@Before
	public void setUp() {
		
		new File(pathToResourcesFolder + "destination").mkdir();
		
		ProgressManager progressManager = mock(ProgressManager.class);
		
		sipFactory = new SIPFactory();
		sipFactory.setProgressManager(progressManager);
		sipFactory.setMessageWriter(cliMessageWriter);
		
		setUpLogger();
		
		Properties properties = new Properties();
		try {
			properties.load(new InputStreamReader((ClassLoader.getSystemResourceAsStream("configuration/config.properties"))));
		} catch (FileNotFoundException e1) {
			System.exit(Feedback.GUI_ERROR.toInt());
		} catch (IOException e2) {
			System.exit(Feedback.GUI_ERROR.toInt());
		}
		SIPBuilder.setProperties(properties);
	}
	
	/**
	 * @throws IOException
	 */
	@After
	public void tearDown() throws IOException {
		FolderUtils.deleteDirectorySafe(new File(pathToResourcesFolder + "destination"));
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
		sipFactory.setWorkingPath(pathToResourcesFolder + "destination");
		sipFactory.setKindofSIPBuilding(SIPFactory.KindOfSIPBuilding.SINGLE_FOLDER);
		sipFactory.setCompress(true);
		sipFactory.setContractRights(rights);
		
		sipFactory.startSIPBuilding();
		do {
			Thread.sleep(100);
		} while (sipFactory.isWorking());
		
		assertEquals(Feedback.SUCCESS, sipFactory.getReturnCode());
		
		String pathToSIP = pathToResourcesFolder + "destination/singleFolder.tgz";
		
		SipArchiveBuilder builder = new SipArchiveBuilder();
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
		sipFactory.setWorkingPath(pathToResourcesFolder + "destination");
		sipFactory.setKindofSIPBuilding(SIPFactory.KindOfSIPBuilding.SINGLE_FOLDER);
		sipFactory.setCompress(false);
		sipFactory.setContractRights(rights);
		
		sipFactory.startSIPBuilding();
		do {
			Thread.sleep(100);
		} while (sipFactory.isWorking());
		
		assertEquals(Feedback.SUCCESS, sipFactory.getReturnCode());
		
		String pathToSIP = pathToResourcesFolder + "destination/singleFolder.tar";
		
		SipArchiveBuilder builder = new SipArchiveBuilder();
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
		sipFactory.setWorkingPath(pathToResourcesFolder + "destination");
		sipFactory.setKindofSIPBuilding(SIPFactory.KindOfSIPBuilding.MULTIPLE_FOLDERS);
		sipFactory.setCompress(true);
		sipFactory.setContractRights(rights);
		
		sipFactory.startSIPBuilding();
		do {
			Thread.sleep(100);
		} while (sipFactory.isWorking());
		
		assertEquals(Feedback.SUCCESS, sipFactory.getReturnCode());
		
		SipArchiveBuilder builder = new SipArchiveBuilder();
		
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
	
	@Test
	public void testBuildNestedSIPs() throws Exception {
		
		UserInput standardAnswerIgnoreWrongReferencesInMetadata = cliMessageWriter.getStandardAnswerIgnoreWrongReferencesInMetadata();
		cliMessageWriter.setStandardAnswerIgnoreWrongReferencesInMetadata(UserInput.YES);
		
		String fixedUrn1 = "urn+nbn+de+hbz+6+1-3602";
		String fixedUrn2 = "urn+nbn+de+hbz+42";
		
		ContractRights rights = new ContractRights();
		rights.setConversionCondition("Keine");
		
		sipFactory.setSourcePath(pathToResourcesFolder + "nestedFolders");
		sipFactory.setDestinationPath(pathToResourcesFolder + "destination");
		sipFactory.setWorkingPath(pathToResourcesFolder + "destination");
		sipFactory.setKindofSIPBuilding(SIPFactory.KindOfSIPBuilding.NESTED_FOLDERS);
		sipFactory.setCompress(true);
		sipFactory.setContractRights(rights);
		
		sipFactory.startSIPBuilding();
		do {
			Thread.sleep(100);
		} while (sipFactory.isWorking());
		
		assertEquals(Feedback.SUCCESS, sipFactory.getReturnCode());
		
		SipArchiveBuilder builder = new SipArchiveBuilder();
		
		String pathToSIP1 = pathToResourcesFolder + "destination/"+fixedUrn1+".tgz";
		builder.unarchiveFolder(new File(pathToSIP1), new File(pathToResourcesFolder + "destination"), true);
		File unpackedFolder1 = new File(pathToResourcesFolder+"destination/"+fixedUrn1);
		
		assertTrue(new File(unpackedFolder1, "bag-info.txt").exists());
		assertTrue(new File(unpackedFolder1, "bagit.txt").exists());
		assertTrue(new File(unpackedFolder1, "manifest-md5.txt").exists());
		assertTrue(new File(unpackedFolder1, "tagmanifest-md5.txt").exists());
		assertTrue(new File(unpackedFolder1, "data/export_mets.xml").exists());
		assertTrue(new File(unpackedFolder1, "data/premis.xml").exists());
		
		assertTrue(validateBagIt(unpackedFolder1));
		
		String pathToSIP2 = pathToResourcesFolder + "destination/"+fixedUrn2+".tgz";
		builder.unarchiveFolder(new File(pathToSIP2), new File(pathToResourcesFolder + "destination"), true);
		File unpackedFolder2 = new File(pathToResourcesFolder+"destination/"+fixedUrn2);
		
		assertTrue(new File(unpackedFolder2, "bag-info.txt").exists());
		assertTrue(new File(unpackedFolder2, "bagit.txt").exists());
		assertTrue(new File(unpackedFolder2, "manifest-md5.txt").exists());
		assertTrue(new File(unpackedFolder2, "tagmanifest-md5.txt").exists());
		assertTrue(new File(unpackedFolder2, "data/export_mets.xml").exists());
		assertTrue(new File(unpackedFolder2, "data/premis.xml").exists());
		
		assertTrue(validateBagIt(unpackedFolder2));
		
		assertFalse(new File(pathToResourcesFolder+"destination/testSubfolder1").exists());
		assertFalse(new File(pathToResourcesFolder+"destination/testSubfolder12").exists());
		assertFalse(new File(pathToResourcesFolder+"destination/testSubfolder13").exists());
		assertFalse(new File(pathToResourcesFolder+"destination/testSubfolder2").exists());
		assertFalse(new File(pathToResourcesFolder+"destination/testSubfolder3").exists());
		assertFalse(new File(pathToResourcesFolder+"destination/testSubfolder4").exists());
		assertFalse(new File(pathToResourcesFolder+"destination/testSubfolder5").exists());
		
		cliMessageWriter.setStandardAnswerIgnoreWrongReferencesInMetadata(standardAnswerIgnoreWrongReferencesInMetadata);
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
		sipFactory.setWorkingPath(pathToResourcesFolder + "destination");
		sipFactory.setKindofSIPBuilding(SIPFactory.KindOfSIPBuilding.MULTIPLE_FOLDERS);
		sipFactory.setCompress(true);
		sipFactory.setContractRights(rights);
		sipFactory.setCreateCollection(true);
		sipFactory.setCollectionName("testCollection");
		
		sipFactory.startSIPBuilding();
		do {
			Thread.sleep(100);
		} while (sipFactory.isWorking());
		
		assertEquals(Feedback.SUCCESS, sipFactory.getReturnCode());
		
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
	 * new Test because of DANRW-1233: wrong publication date
	 * 
	 * @throws Exception
	 */
	@Test
	public void testPublicationDateSZ() throws Exception {
		
		ContractRights rights = new ContractRights();
		rights.setConversionCondition("Keine");
		rights.getPublicRights().setAllowPublication(true);
		rights.getPublicRights().setTempPublication(true);
		rights.getPublicRights().setStartDate("15.05.2016");
		
				
		sipFactory.setSourcePath(pathToResourcesFolder + "singleFolder");
		sipFactory.setDestinationPath(pathToResourcesFolder + "destination");
		sipFactory.setWorkingPath(pathToResourcesFolder + "destination");
		sipFactory.setKindofSIPBuilding(SIPFactory.KindOfSIPBuilding.SINGLE_FOLDER);
		sipFactory.setCompress(false);
		sipFactory.setContractRights(rights);
		
		sipFactory.startSIPBuilding();
		
		do {
			Thread.sleep(100);
		} while (sipFactory.isWorking());
		
		assertEquals(Feedback.SUCCESS, sipFactory.getReturnCode());
		
		String pathToSIP = pathToResourcesFolder + "destination/singleFolder.tar";
		
		SipArchiveBuilder builder = new SipArchiveBuilder();
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
		
		assertTrue(checkPremisFilePubStartDate(new File(unpackedFolder, "data/premis.xml"), rights));
	}
	
	/**
	 * new Test because of DANRW-1233: wrong publication date
	 * 
	 * @throws Exception
	 */
	@Test
	public void testPublicationDateWZ() throws Exception {
		
		ContractRights rights = new ContractRights();
		rights.setConversionCondition("Keine");
		rights.getPublicRights().setAllowPublication(true);
		rights.getPublicRights().setTempPublication(true);
		rights.getPublicRights().setStartDate("15.01.2016");
		
		sipFactory.setSourcePath(pathToResourcesFolder + "singleFolder");
		sipFactory.setDestinationPath(pathToResourcesFolder + "destination");
		sipFactory.setWorkingPath(pathToResourcesFolder + "destination");
		sipFactory.setKindofSIPBuilding(SIPFactory.KindOfSIPBuilding.SINGLE_FOLDER);
		sipFactory.setCompress(false);
		sipFactory.setContractRights(rights);
		
		sipFactory.startSIPBuilding();
		
		do {
			Thread.sleep(100);
		} while (sipFactory.isWorking());
		
		assertEquals(Feedback.SUCCESS, sipFactory.getReturnCode());
		
		String pathToSIP = pathToResourcesFolder + "destination/singleFolder.tar";
		
		SipArchiveBuilder builder = new SipArchiveBuilder();
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
		
		assertTrue(checkPremisFilePubStartDate(new File(unpackedFolder, "data/premis.xml"), rights));
	}
	
	/**
	 * @param premis
	 * @param publicRights 
	 */
	private boolean checkPremisFilePubStartDate(File premis, ContractRights rights) {
	
		XMLInputFactory inputFactory = XMLInputFactory.newInstance();
		XMLStreamReader streamReader;
		try {
			streamReader = inputFactory.createXMLStreamReader(new FileInputStream(premis));
			while(streamReader.hasNext())
			{
				int event = streamReader.next();
				switch (event)
				{
				case XMLStreamConstants.START_ELEMENT:
					if (streamReader.getLocalName().equals("startDate")) {

						String startDate =  streamReader.getElementText().substring(0, 10);
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
						String publicDate = sdf.format(rights.getPublicRights().getStartDate());
						if (startDate.trim().equals(publicDate.trim())) {
							return true;
						}
					}
				default:
					break;
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (XMLStreamException e) {
			e.printStackTrace();
		}
		return false;
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
	
	private void setUpLogger() {
		TTCCLayout layout = new TTCCLayout();
		layout.setDateFormat("yyyy'-'MM'-'dd' 'HH':'mm':'ss");
		layout.setThreadPrinting(false);
	    ConsoleAppender consoleAppender = new ConsoleAppender(layout);
	    logger.addAppender( consoleAppender );
        logger.setLevel(Level.INFO);
		sipFactory.setLogger(logger);
	}
}
