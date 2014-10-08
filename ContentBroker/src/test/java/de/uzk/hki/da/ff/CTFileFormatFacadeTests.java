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

package de.uzk.hki.da.ff;

import static org.junit.Assert.assertEquals;

import java.io.File;
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
	public void test() throws IOException{
		FileWithFileFormat ffff = new FileWithFileFormat(new File("conf/healthCheck.tif"));
		List<IFileWithFileFormat> files = new ArrayList<IFileWithFileFormat>();
		files.add(ffff);
		sfff.identify(files);
		
		assertEquals("fmt/353",files.get(0).getFormatPUID());
	}
	
	// Testtiff
	
	@Test
	public void testEAD() throws IOException{
		FileWithFileFormat ffff = new FileWithFileFormat(Path.makeFile(testPath,"vda3.XML"));
		
		List<IFileWithFileFormat> files = new ArrayList<IFileWithFileFormat>();
		files.add(ffff);
		sfff.identify(files);
		
		assertEquals(FFConstants.XML_PUID,files.get(0).getFormatPUID());
		assertEquals(FFConstants.SUBFORMAT_IDENTIFIER_EAD,files.get(0).getFormatSecondaryAttribute());
	}
	
	@Test
	public void testMETS() throws IOException{
		FileWithFileFormat ffff = new FileWithFileFormat(Path.makeFile(testPath,"mets_2_99.xml"));
		
		List<IFileWithFileFormat> files = new ArrayList<IFileWithFileFormat>();
		files.add(ffff);
		sfff.identify(files);
		
		assertEquals(FFConstants.XML_PUID,files.get(0).getFormatPUID());
		assertEquals(FFConstants.SUBFORMAT_IDENTIFIER_METS,files.get(0).getFormatSecondaryAttribute());
	}
	
	@Test
	public void testLIDO() throws IOException{
		FileWithFileFormat ffff = new FileWithFileFormat(Path.makeFile(testPath,"LIDO-Testexport2014-07-04-FML-Auswahl.xml"));
		
		List<IFileWithFileFormat> files = new ArrayList<IFileWithFileFormat>();
		files.add(ffff);
		sfff.identify(files);
		
		assertEquals(FFConstants.XML_PUID,files.get(0).getFormatPUID());
		assertEquals(FFConstants.SUBFORMAT_IDENTIFIER_LIDO,files.get(0).getFormatSecondaryAttribute());
	}

	@Test
	public void testXMP() throws IOException{
		FileWithFileFormat ffff = new FileWithFileFormat(Path.makeFile(testPath,"a.xmp"));
		
		List<IFileWithFileFormat> files = new ArrayList<IFileWithFileFormat>();
		files.add(ffff);
		sfff.identify(files);
		
		assertEquals(FFConstants.XML_PUID,files.get(0).getFormatPUID());
		assertEquals(FFConstants.SUBFORMAT_IDENTIFIER_XMP,files.get(0).getFormatSecondaryAttribute());
	}
	
	
}
