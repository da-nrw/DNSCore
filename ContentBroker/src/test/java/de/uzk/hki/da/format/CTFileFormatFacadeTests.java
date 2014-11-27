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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import de.uzk.hki.da.core.HibernateUtil;
import de.uzk.hki.da.core.Path;
import de.uzk.hki.da.test.CTTestHelper;
import de.uzk.hki.da.test.TC;

/**
 * @author Daniel M. de Oliveira
 */
public class CTFileFormatFacadeTests {

	private static final StandardFileFormatFacade sfff = new StandardFileFormatFacade();
	private static final Path testPath = Path.make(TC.TEST_ROOT_FORMAT,"CTFileFormatFacadeTests");
	private List<FileWithFileFormat> files = new ArrayList<FileWithFileFormat>();;
	
	
	@BeforeClass
	public static void setUp() throws IOException{
		HibernateUtil.init("src/main/xml/hibernateCentralDB.cfg.xml.inmem");
		CTTestHelper.prepareWhiteBoxTest();
	}

	
	@AfterClass
	public static void tearDownAfterClass(){
		CTTestHelper.cleanUpWhiteBoxTest();
	}
	
	
	@Test
	public void pronomFormatIdentification() throws IOException{
		files.add(new SimpleFileWithFileFormat(Path.makeFile(testPath,"healthCheck.tif")));
		
		sfff.identify(files);
		assertEquals(FFConstants.FMT_353,files.get(0).getFormatPUID());
	}
	
	@Test
	public void pronomFormatIdentificationBlanksInFilename() throws IOException{
		files.add(new SimpleFileWithFileFormat(Path.makeFile(testPath,"health check.tif")));
		
		sfff.identify(files);
		assertEquals(FFConstants.FMT_353,files.get(0).getFormatPUID());
	}

	
	@Test
	public void metsFormatIdentification() throws IOException{
		sfff.registerSubformatIdentificationStrategyPuidMapping("de.uzk.hki.da.format.XMLSubformatIdentificationStrategy", 
				FFConstants.FMT_101);
		files.add(new SimpleFileWithFileFormat(Path.makeFile(testPath,"mets_2_99.xml")));
		
		sfff.identify(files);
		assertEquals(FFConstants.XML_PUID,files.get(0).getFormatPUID());
		assertEquals(FFConstants.SUBFORMAT_IDENTIFIER_METS,files.get(0).getSubformatIdentifier());
	}
		
	
	@Test
	public void lidoFormatIdentification() throws IOException{
		sfff.registerSubformatIdentificationStrategyPuidMapping("de.uzk.hki.da.format.XMLSubformatIdentificationStrategy", 
				FFConstants.FMT_101);
		files.add(new SimpleFileWithFileFormat(Path.makeFile(testPath,"LIDO-Testexport2014-07-04-FML-Auswahl.xml")));
		
		sfff.identify(files);
		assertEquals(FFConstants.XML_PUID,files.get(0).getFormatPUID());
		assertEquals(FFConstants.SUBFORMAT_IDENTIFIER_LIDO,files.get(0).getSubformatIdentifier());
	}
		

	@Test
	public void xmpFormatIdentification() throws IOException{
		sfff.registerSubformatIdentificationStrategyPuidMapping("de.uzk.hki.da.format.XMLSubformatIdentificationStrategy", 
				FFConstants.FMT_101);
		files.add(new SimpleFileWithFileFormat(Path.makeFile(testPath,"a.xmp")));
		
		sfff.identify(files);
		assertEquals(FFConstants.XML_PUID,files.get(0).getFormatPUID());
		assertEquals(FFConstants.SUBFORMAT_IDENTIFIER_XMP,files.get(0).getSubformatIdentifier());
	}
	
	
	@Test
	public void ffmpegStrategySubformatIdentification() throws IOException {
		sfff.registerSubformatIdentificationStrategyPuidMapping("de.uzk.hki.da.format.FFmpegSubformatIdentificationStrategy", 
				FFConstants.FMT_5);
		files.add(new SimpleFileWithFileFormat(Path.makeFile(testPath,"a.avi")));
		
		sfff.identify(files);
		assertTrue(files.get(0).getSubformatIdentifier().equals("cinepak"));
	}
	
	
	@Test
	public void ffmpegStrategySubformatIdentificationFileWithBlanksInFilename() throws IOException {
		sfff.registerSubformatIdentificationStrategyPuidMapping("de.uzk.hki.da.format.FFmpegSubformatIdentificationStrategy", 
				FFConstants.FMT_5);
		files.add(new SimpleFileWithFileFormat(Path.makeFile(testPath,"a b.avi")));
		
		sfff.identify(files);
		assertTrue(files.get(0).getSubformatIdentifier().equals("cinepak"));
	}

	@Test
	public void healthCheck() {
		sfff.registerSubformatIdentificationStrategyPuidMapping("de.uzk.hki.da.format.FFmpegSubformatIdentificationStrategy", 
				FFConstants.FMT_5);
		sfff.registerSubformatIdentificationStrategyPuidMapping("de.uzk.hki.da.format.ImageMagickIdentifySubformatIdentificationStrategy", 
				FFConstants.FMT_353);
		assertTrue(sfff.healthCheckSubformatIdentificationStrategies());
	}
	
	@Test
	public void imageMagickIdentifySubformatIdentification() throws IOException {
		sfff.registerSubformatIdentificationStrategyPuidMapping("de.uzk.hki.da.format.ImageMagickIdentifySubformatIdentificationStrategy", 
				FFConstants.FMT_353);
		files.add(new SimpleFileWithFileFormat(Path.makeFile(testPath,"CCITT_1_LZW.TIF")));
		
		sfff.identify(files);
		assertTrue(files.get(0).getSubformatIdentifier().equals("Group4"));

	}
	
	
	@Test
	public void registrationNotPossibleUnkownStrategy(){
		try {
			sfff.registerSubformatIdentificationStrategyPuidMapping("de.uzk.hki.da.format.UnkownStrategy","");
			fail();
		} catch (IllegalArgumentException expected) {}
	}
	
}
