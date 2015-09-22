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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import de.uzk.hki.da.service.HibernateUtil;
import de.uzk.hki.da.test.CTTestHelper;
import de.uzk.hki.da.test.TC;
import de.uzk.hki.da.utils.C;
import de.uzk.hki.da.utils.Path;

/**
 * @author Daniel M. de Oliveira
 */
public class CTFileFormatFacadeTests {

	private static final ConfigurableFileFormatFacade sfff = new ConfigurableFileFormatFacade();
	private static final Path testPath = Path.make(TC.TEST_ROOT_FORMAT,"CTFileFormatFacade");
	private List<FileWithFileFormat> files = new ArrayList<FileWithFileFormat>();;
	
	
	@BeforeClass
	public static void setUp() throws IOException{
		sfff.setFormatScanService(new FidoFormatScanService());
		sfff.setMetadataExtractor(new FakeMetadataExtractor());
		sfff.setSubformatScanService(new SubformatScanService());
		
		HibernateUtil.init("src/main/xml/hibernateCentralDB.cfg.xml.inmem");
		CTTestHelper.prepareWhiteBoxTest();
	}

	@AfterClass
	public static void tearDownAfterClass(){
		CTTestHelper.cleanUpWhiteBoxTest();
	}
	
	
	@Test
	public void pronomFormatIdentification() throws IOException{
		
		files.add(new SimpleFileWithFileFormat(Path.makeFile("healthCheck.tif")));
		
		sfff.identify(testPath,files);
		assertEquals(FFConstants.FMT_353,files.get(0).getFormatPUID());
	}
	
	@Test
	public void pronomFormatIdentificationBlanksInFilename() throws IOException{
		files.add(new SimpleFileWithFileFormat(Path.makeFile("health check.tif")));
		
		sfff.identify(testPath,files);
		assertEquals(FFConstants.FMT_353,files.get(0).getFormatPUID());
	}

	
	@Test
	public void metsFormatIdentification() throws IOException{
		sfff.registerSubformatIdentificationStrategyPuidMapping("de.uzk.hki.da.format.XMLSubformatIdentifier", 
				FFConstants.FMT_101);
		files.add(new SimpleFileWithFileFormat(Path.makeFile("mets_2_99.xml")));
		
		sfff.identify(testPath,files);
		assertEquals(FFConstants.XML_PUID,files.get(0).getFormatPUID());
		assertEquals(C.SUBFORMAT_IDENTIFIER_METS,files.get(0).getSubformatIdentifier());
	}
		
	
	@Test
	public void lidoFormatIdentification() throws IOException{
		sfff.registerSubformatIdentificationStrategyPuidMapping("de.uzk.hki.da.format.XMLSubformatIdentifier", 
				FFConstants.FMT_101);
		files.add(new SimpleFileWithFileFormat(Path.makeFile("LIDO-Testexport2014-07-04-FML-Auswahl.xml")));
		
		sfff.identify(testPath,files);
		assertEquals(FFConstants.XML_PUID,files.get(0).getFormatPUID());
		assertEquals(C.SUBFORMAT_IDENTIFIER_LIDO,files.get(0).getSubformatIdentifier());
	}
		

	@Test
	public void xmpFormatIdentification() throws IOException{
		sfff.registerSubformatIdentificationStrategyPuidMapping("de.uzk.hki.da.format.XMLSubformatIdentifier", 
				FFConstants.FMT_101);
		files.add(new SimpleFileWithFileFormat(Path.makeFile("a.xmp")));
		
		sfff.identify(testPath,files);
		assertEquals(FFConstants.XML_PUID,files.get(0).getFormatPUID());
		assertEquals(C.SUBFORMAT_IDENTIFIER_XMP,files.get(0).getSubformatIdentifier());
	}
	
	
	@Test
	public void ffmpegStrategySubformatIdentification() throws IOException {
		sfff.registerSubformatIdentificationStrategyPuidMapping("de.uzk.hki.da.format.FFmpegSubformatIdentifier", 
				FFConstants.FMT_5);
		files.add(new SimpleFileWithFileFormat(Path.makeFile("a.avi")));
		
		sfff.identify(testPath,files);
		assertTrue(files.get(0).getSubformatIdentifier().equals("cinepak"));
	}

	@Test
	public void ffmpegStrategySubformatIdentificationCodecContainsDigit() throws IOException {
		sfff.registerSubformatIdentificationStrategyPuidMapping("de.uzk.hki.da.format.FFmpegSubformatIdentifier", 
				FFConstants.X_FMT_384);
		files.add(new SimpleFileWithFileFormat(Path.makeFile("a.mov")));
		
		sfff.identify(testPath,files);
		assertTrue(files.get(0).getSubformatIdentifier().equals("svq1"));
	}

	
	
	
	@Test
	public void ffmpegStrategySubformatIdentificationFileWithBlanksInFilename() throws IOException {
		sfff.registerSubformatIdentificationStrategyPuidMapping("de.uzk.hki.da.format.FFmpegSubformatIdentifier", 
				FFConstants.FMT_5);
		files.add(new SimpleFileWithFileFormat(Path.makeFile("a b.avi")));
		
		sfff.identify(testPath,files);
		assertTrue(files.get(0).getSubformatIdentifier().equals("cinepak"));
	}
	@Test
	public void healthCheck() {
		sfff.registerSubformatIdentificationStrategyPuidMapping("de.uzk.hki.da.format.FFmpegSubformatIdentifier", 
				FFConstants.FMT_5);
		sfff.registerSubformatIdentificationStrategyPuidMapping("de.uzk.hki.da.format.ImageMagickSubformatIdentifier", 
				FFConstants.FMT_353);
		assertTrue(sfff.connectivityCheck());
	}
	
	@Test
	public void imageMagickIdentifySubformatIdentification() throws IOException {
		sfff.registerSubformatIdentificationStrategyPuidMapping("de.uzk.hki.da.format.ImageMagickSubformatIdentifier", 
				FFConstants.FMT_353);
		files.add(new SimpleFileWithFileFormat(Path.makeFile("CCITT_1_LZW.TIF")));
		
		sfff.identify(testPath,files);
		assertTrue(files.get(0).getSubformatIdentifier().equals("Group4"));

	}
}
