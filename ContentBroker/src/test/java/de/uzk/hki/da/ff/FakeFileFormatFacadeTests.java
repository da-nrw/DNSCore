package de.uzk.hki.da.ff;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import de.uzk.hki.da.core.Path;
import de.uzk.hki.da.model.DAFile;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.test.TC;
import de.uzk.hki.da.test.TESTHelper;


/**
 * @author Daniel M. de Oliveira
 */
public class FakeFileFormatFacadeTests {
	
	private static final Path workAreaRootPath = Path.make(TC.TEST_ROOT_FORMAT,"FakeFormatScanServiceTests","work");
	private static final Object object = TESTHelper.setUpObject("identifier", workAreaRootPath);
	private static final FakeFileFormatFacade fss = new FakeFileFormatFacade();
	
	@Test
	public void testMets() throws IOException{

		DAFile mets = new DAFile(object.getLatestPackage(),"1+a","mets_mods_example.xml");
		List<IFileWithFileFormat> files = new ArrayList<IFileWithFileFormat>();
		files.add(mets);
		
		fss.identify(files);
		
		assertEquals(FFConstants.SUBFORMAT_IDENTIFIER_METS,files.get(0).getFormatSecondaryAttribute());
		assertEquals(FFConstants.XML_PUID,files.get(0).getFormatPUID());
	}
	
	@Test
	public void testEAD() throws IOException{
		DAFile ead = new DAFile(object.getLatestPackage(),"1+a","vda3.XML");
		List<IFileWithFileFormat> files = new ArrayList<IFileWithFileFormat>();
		files.add(ead);
		
		fss.identify(files);
		
		assertEquals(FFConstants.XML_PUID,files.get(0).getFormatPUID());	
		assertEquals(FFConstants.SUBFORMAT_IDENTIFIER_EAD,files.get(0).getFormatSecondaryAttribute());	
	}

	@Test
	public void testEAD2() throws IOException{
		DAFile ead2 = new DAFile(object.getLatestPackage(),"1+a","EAD_Export.XML");
		List<IFileWithFileFormat> files = new ArrayList<IFileWithFileFormat>();
		files.add(ead2);
		
		fss.identify(files);
		
		assertEquals(FFConstants.SUBFORMAT_IDENTIFIER_EAD,files.get(0).getFormatSecondaryAttribute());	
		assertEquals(FFConstants.XML_PUID,files.get(0).getFormatPUID());	
	}
	
	
	
	@Test
	public void testLIDO() throws IOException{
		DAFile ead = new DAFile(object.getLatestPackage(),"1+a","LIDO-Testexport2014-07-04-FML-Auswahl.xml");
		List<IFileWithFileFormat> files = new ArrayList<IFileWithFileFormat>();
		files.add(ead);
		
		fss.identify(files);
		
		assertEquals(FFConstants.XML_PUID,files.get(0).getFormatPUID());	
		assertEquals(FFConstants.SUBFORMAT_IDENTIFIER_LIDO,files.get(0).getFormatSecondaryAttribute());	
	}
	
	@Test
	public void testXMP() throws IOException{
		DAFile xmp = new DAFile(object.getLatestPackage(),"1+a","b.xmp");
		List<IFileWithFileFormat> files = new ArrayList<IFileWithFileFormat>();
		files.add(xmp);
		
		fss.identify(files);
		
		assertEquals(FFConstants.SUBFORMAT_IDENTIFIER_XMP,files.get(0).getFormatSecondaryAttribute());	
		assertEquals(FFConstants.XML_PUID,files.get(0).getFormatPUID());	
	}

	@Test
	public void testTiff() throws IOException{
		DAFile tif = new DAFile(object.getLatestPackage(),"1+a","tif.tif");
		List<IFileWithFileFormat> files = new ArrayList<IFileWithFileFormat>();
		files.add(tif);
		
		fss.identify(files);
		
		assertEquals("fmt/353",files.get(0).getFormatPUID());	
	}
	
	
	@Test
	public void testBmp() throws IOException{
		DAFile bmp = new DAFile(object.getLatestPackage(),"1+a","bmp.bmp");
		List<IFileWithFileFormat> files = new ArrayList<IFileWithFileFormat>();
		files.add(bmp);
		
		fss.identify(files);
		
		assertEquals("fmt/116",files.get(0).getFormatPUID());	
	}
	
	
	@Test
	public void testJp2() throws IOException{
		DAFile jp2 = new DAFile(object.getLatestPackage(),"1+a","jp2.jp2");
		List<IFileWithFileFormat> files = new ArrayList<IFileWithFileFormat>();
		files.add(jp2);
		
		fss.identify(files);
		
		assertEquals("x-fmt/392",files.get(0).getFormatPUID());	
	}
	
	@Test
	public void testGif() throws IOException{
		DAFile gif = new DAFile(object.getLatestPackage(),"1+a","gif.gif");
		List<IFileWithFileFormat> files = new ArrayList<IFileWithFileFormat>();
		files.add(gif);
		
		fss.identify(files);
		
		assertEquals("fmt/4",files.get(0).getFormatPUID());	
	}
	
	@Test
	public void testPdf() throws IOException{
		DAFile pdf = new DAFile(object.getLatestPackage(),"1+a","pdf.pdf");
		List<IFileWithFileFormat> files = new ArrayList<IFileWithFileFormat>();
		files.add(pdf);
		
		fss.identify(files);
		
		assertEquals("fmt/16",files.get(0).getFormatPUID());	
	}

	@Test
	public void testXml() throws IOException{
		DAFile xml = new DAFile(object.getLatestPackage(),"1+a","b.xml");
		List<IFileWithFileFormat> files = new ArrayList<IFileWithFileFormat>();
		files.add(xml);
		
		fss.identify(files);
		
		assertEquals("fmt/101",files.get(0).getFormatPUID());	
	}
	
	@Test
	public void testMoreFiles() throws IOException{
		DAFile gif = new DAFile(object.getLatestPackage(),"1+a","gif.gif");
		DAFile pdf = new DAFile(object.getLatestPackage(),"1+a","pdf.pdf");
		DAFile ead2 = new DAFile(object.getLatestPackage(),"1+a","EAD_Export.XML");
		DAFile mets = new DAFile(object.getLatestPackage(),"1+a","mets_mods_example.xml");
		List<IFileWithFileFormat> files = new ArrayList<IFileWithFileFormat>();
		files.add(pdf);
		files.add(gif);
		files.add(ead2);
		files.add(mets);
		
		fss.identify(files);
		
		assertEquals("fmt/16",files.get(0).getFormatPUID());
		assertEquals("fmt/4",files.get(1).getFormatPUID());
		assertEquals(FFConstants.XML_PUID,files.get(2).getFormatPUID());
		assertEquals(FFConstants.SUBFORMAT_IDENTIFIER_EAD,files.get(2).getFormatSecondaryAttribute());
		assertEquals(FFConstants.XML_PUID,files.get(3).getFormatPUID());
		assertEquals(FFConstants.SUBFORMAT_IDENTIFIER_METS,files.get(3).getFormatSecondaryAttribute());
	}
	
	
	
}