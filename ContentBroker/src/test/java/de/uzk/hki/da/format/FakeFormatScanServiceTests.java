package de.uzk.hki.da.format;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import de.uzk.hki.da.model.DAFile;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.utils.C;
import de.uzk.hki.da.utils.Path;
import de.uzk.hki.da.utils.TC;
import de.uzk.hki.da.utils.TESTHelper;


/**
 * @author Daniel M. de Oliveira
 */
public class FakeFormatScanServiceTests {
	
	private static final Path workAreaRootPath = Path.make(TC.TEST_ROOT_FORMAT,"FakeFormatScanServiceTests","work");
	private static final Object object = TESTHelper.setUpObject("identifier", workAreaRootPath);
	private static final FakeFormatScanService fss = new FakeFormatScanService();
	
	@Test
	public void testMets() throws IOException{

		DAFile mets = new DAFile(object.getLatestPackage(),"1+a","mets_mods_example.xml");
		List<DAFile> files = new ArrayList<DAFile>();
		files.add(mets);
		
		fss.identify(files);
		
		assertEquals(C.METS_PUID,files.get(0).getFormatPUID());
	}
	
	@Test
	public void testEAD() throws IOException{
		DAFile ead = new DAFile(object.getLatestPackage(),"1+a","vda3.XML");
		List<DAFile> files = new ArrayList<DAFile>();
		files.add(ead);
		
		fss.identify(files);
		
		assertEquals(C.EAD_PUID,files.get(0).getFormatPUID());	
	}

	@Test
	public void testEAD2() throws IOException{
		DAFile ead2 = new DAFile(object.getLatestPackage(),"1+a","EAD_Export.XML");
		List<DAFile> files = new ArrayList<DAFile>();
		files.add(ead2);
		
		fss.identify(files);
		
		assertEquals(C.EAD_PUID,files.get(0).getFormatPUID());	
	}
	
	
	
	@Test
	public void testLIDO() throws IOException{
		DAFile ead = new DAFile(object.getLatestPackage(),"1+a","LIDO-Testexport2014-07-04-FML-Auswahl.xml");
		List<DAFile> files = new ArrayList<DAFile>();
		files.add(ead);
		
		fss.identify(files);
		
		assertEquals(C.LIDO_PUID,files.get(0).getFormatPUID());	
	}
	
	@Test
	public void testXMP() throws IOException{
		DAFile xmp = new DAFile(object.getLatestPackage(),"1+a","b.xmp");
		List<DAFile> files = new ArrayList<DAFile>();
		files.add(xmp);
		
		fss.identify(files);
		
		assertEquals(C.XMP_PUID,files.get(0).getFormatPUID());	
	}

	@Test
	public void testTiff() throws IOException{
		DAFile tif = new DAFile(object.getLatestPackage(),"1+a","tif.tif");
		List<DAFile> files = new ArrayList<DAFile>();
		files.add(tif);
		
		fss.identify(files);
		
		assertEquals("fmt/353",files.get(0).getFormatPUID());	
	}
	
	
	@Test
	public void testBmp() throws IOException{
		DAFile bmp = new DAFile(object.getLatestPackage(),"1+a","bmp.bmp");
		List<DAFile> files = new ArrayList<DAFile>();
		files.add(bmp);
		
		fss.identify(files);
		
		assertEquals("fmt/116",files.get(0).getFormatPUID());	
	}
	
	
	@Test
	public void testJp2() throws IOException{
		DAFile jp2 = new DAFile(object.getLatestPackage(),"1+a","jp2.jp2");
		List<DAFile> files = new ArrayList<DAFile>();
		files.add(jp2);
		
		fss.identify(files);
		
		assertEquals("x-fmt/392",files.get(0).getFormatPUID());	
	}
	
	@Test
	public void testGif() throws IOException{
		DAFile gif = new DAFile(object.getLatestPackage(),"1+a","gif.gif");
		List<DAFile> files = new ArrayList<DAFile>();
		files.add(gif);
		
		fss.identify(files);
		
		assertEquals("fmt/4",files.get(0).getFormatPUID());	
	}
	
	@Test
	public void testPdf() throws IOException{
		DAFile pdf = new DAFile(object.getLatestPackage(),"1+a","pdf.pdf");
		List<DAFile> files = new ArrayList<DAFile>();
		files.add(pdf);
		
		fss.identify(files);
		
		assertEquals("fmt/16",files.get(0).getFormatPUID());	
	}

	@Test
	public void testXml() throws IOException{
		DAFile xml = new DAFile(object.getLatestPackage(),"1+a","b.xml");
		List<DAFile> files = new ArrayList<DAFile>();
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
		List<DAFile> files = new ArrayList<DAFile>();
		files.add(pdf);
		files.add(gif);
		files.add(ead2);
		files.add(mets);
		
		fss.identify(files);
		
		assertEquals("fmt/16",files.get(0).getFormatPUID());
		assertEquals("fmt/4",files.get(1).getFormatPUID());
		assertEquals(C.EAD_PUID,files.get(2).getFormatPUID());
		assertEquals(C.METS_PUID,files.get(3).getFormatPUID());
	}
	
	
	
}