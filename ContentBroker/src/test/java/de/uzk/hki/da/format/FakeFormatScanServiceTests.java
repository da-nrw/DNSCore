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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.uzk.hki.da.model.DAFile;
import de.uzk.hki.da.model.Node;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.model.WorkArea;
import de.uzk.hki.da.test.TC;
import de.uzk.hki.da.test.TESTHelper;
import de.uzk.hki.da.utils.C;
import de.uzk.hki.da.utils.Path;


/**
 * @author Daniel M. de Oliveira
 */
public class FakeFormatScanServiceTests {
	
	private static final Path workAreaRootPath = Path.make(TC.TEST_ROOT_FORMAT,"FakeFormatScanServiceTests","work");
	private static final Object object = TESTHelper.setUpObject("identifier", workAreaRootPath);
	private static final FakeFormatScanService fss = new FakeFormatScanService();
	private static WorkArea wa;
	
	@Before
	public void setUp() {
		Node n=new Node();
		n.setWorkAreaRootPath(workAreaRootPath);
		
		wa=new WorkArea(n,object);
	}
	
	@Test
	public void testMets() throws IOException{

		DAFile mets = new DAFile("1+a","mets_mods_example.xml");
		List<FileWithFileFormat> files = new ArrayList<FileWithFileFormat>();
		files.add(mets);
		
		fss.identify(wa.dataPath(),files);
		
		assertEquals(C.SUBFORMAT_IDENTIFIER_METS,files.get(0).getSubformatIdentifier());
		assertEquals(FFConstants.XML_PUID,files.get(0).getFormatPUID());
	}
	
	@Test
	public void testEAD() throws IOException{
		DAFile ead = new DAFile("1+a","vda3.XML");
		List<FileWithFileFormat> files = new ArrayList<FileWithFileFormat>();
		files.add(ead);
		
		fss.identify(wa.dataPath(),files);
		
		assertEquals(FFConstants.XML_PUID,files.get(0).getFormatPUID());	
		assertEquals(C.SUBFORMAT_IDENTIFIER_EAD,files.get(0).getSubformatIdentifier());	
	}

	@Test
	public void testEAD2() throws IOException{
		DAFile ead2 = new DAFile("1+a","EAD_Export.XML");
		List<FileWithFileFormat> files = new ArrayList<FileWithFileFormat>();
		files.add(ead2);
		
		fss.identify(wa.dataPath(),files);
		
		assertEquals(C.SUBFORMAT_IDENTIFIER_EAD,files.get(0).getSubformatIdentifier());	
		assertEquals(FFConstants.XML_PUID,files.get(0).getFormatPUID());	
	}
	
	
	
	@Test
	public void testLIDO() throws IOException{
		DAFile ead = new DAFile("1+a","LIDO-Testexport2014-07-04-FML-Auswahl.xml");
		List<FileWithFileFormat> files = new ArrayList<FileWithFileFormat>();
		files.add(ead);
		
		fss.identify(wa.dataPath(),files);
		
		assertEquals(FFConstants.XML_PUID,files.get(0).getFormatPUID());	
		assertEquals(C.SUBFORMAT_IDENTIFIER_LIDO,files.get(0).getSubformatIdentifier());	
	}
	
	@Test
	public void testXMP() throws IOException{
		DAFile xmp = new DAFile("1+a","b.xmp");
		List<FileWithFileFormat> files = new ArrayList<FileWithFileFormat>();
		files.add(xmp);
		
		fss.identify(wa.dataPath(),files);
		
		assertEquals(C.SUBFORMAT_IDENTIFIER_XMP,files.get(0).getSubformatIdentifier());	
		assertEquals(FFConstants.XML_PUID,files.get(0).getFormatPUID());	
	}

	@Test
	public void testTiff() throws IOException{
		DAFile tif = new DAFile("1+a","tif.tif");
		List<FileWithFileFormat> files = new ArrayList<FileWithFileFormat>();
		files.add(tif);
		
		fss.identify(wa.dataPath(),files);
		
		assertEquals("fmt/353",files.get(0).getFormatPUID());	
	}
	
	
	@Test
	public void testBmp() throws IOException{
		DAFile bmp = new DAFile("1+a","bmp.bmp");
		List<FileWithFileFormat> files = new ArrayList<FileWithFileFormat>();
		files.add(bmp);
		
		fss.identify(wa.dataPath(),files);
		
		assertEquals("fmt/116",files.get(0).getFormatPUID());	
	}
	
	
	@Test
	public void testJp2() throws IOException{
		DAFile jp2 = new DAFile("1+a","jp2.jp2");
		List<FileWithFileFormat> files = new ArrayList<FileWithFileFormat>();
		files.add(jp2);
		
		fss.identify(wa.dataPath(),files);
		
		assertEquals("x-fmt/392",files.get(0).getFormatPUID());	
	}
	
	@Test
	public void testGif() throws IOException{
		DAFile gif = new DAFile("1+a","gif.gif");
		List<FileWithFileFormat> files = new ArrayList<FileWithFileFormat>();
		files.add(gif);
		
		fss.identify(wa.dataPath(),files);
		
		assertEquals("fmt/4",files.get(0).getFormatPUID());	
	}
	
	@Test
	public void testPdf() throws IOException{
		DAFile pdf = new DAFile("1+a","pdf.pdf");
		List<FileWithFileFormat> files = new ArrayList<FileWithFileFormat>();
		files.add(pdf);
		
		fss.identify(wa.dataPath(),files);
		
		assertEquals("fmt/16",files.get(0).getFormatPUID());	
	}

	@Test
	public void testXml() throws IOException{
		DAFile xml = new DAFile("1+a","b.xml");
		List<FileWithFileFormat> files = new ArrayList<FileWithFileFormat>();
		files.add(xml);
		
		fss.identify(wa.dataPath(),files);
		
		assertEquals("fmt/101",files.get(0).getFormatPUID());	
	}
	
	@Test
	public void testMoreFiles() throws IOException{
		DAFile gif = new DAFile("1+a","gif.gif");
		DAFile pdf = new DAFile("1+a","pdf.pdf");
		DAFile ead2 = new DAFile("1+a","EAD_Export.XML");
		DAFile mets = new DAFile("1+a","mets_mods_example.xml");
		List<FileWithFileFormat> files = new ArrayList<FileWithFileFormat>();
		files.add(pdf);
		files.add(gif);
		files.add(ead2);
		files.add(mets);
		
		fss.identify(wa.dataPath(),files);
		
		assertEquals("fmt/16",files.get(0).getFormatPUID());
		assertEquals("fmt/4",files.get(1).getFormatPUID());
		assertEquals(FFConstants.XML_PUID,files.get(2).getFormatPUID());
		assertEquals(C.SUBFORMAT_IDENTIFIER_EAD,files.get(2).getSubformatIdentifier());
		assertEquals(FFConstants.XML_PUID,files.get(3).getFormatPUID());
		assertEquals(C.SUBFORMAT_IDENTIFIER_METS,files.get(3).getSubformatIdentifier());
	}
	
	
	
}