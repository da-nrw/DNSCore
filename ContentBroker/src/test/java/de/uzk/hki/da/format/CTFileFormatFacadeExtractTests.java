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


import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.hibernate.Session;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.uzk.hki.da.model.FormatMapping;
import de.uzk.hki.da.model.JHoveParameterMapping;
import de.uzk.hki.da.service.HibernateUtil;
import de.uzk.hki.da.test.CTTestHelper;
import de.uzk.hki.da.test.TC;
import de.uzk.hki.da.utils.C;
import de.uzk.hki.da.utils.CommandLineConnector;
import de.uzk.hki.da.utils.Path;
import de.uzk.hki.da.utils.RelativePath;


/**
 * @author Daniel M. de Oliveira
 * @author Eugen Trebunski
 */
public class CTFileFormatFacadeExtractTests {
	//private static final String PUID_XML = "fmt/120";
	private static final String PUID_PDF = "fmt/18";
	private static final String PUID_TIFF = "fmt/353";
	private static final String PUID_JP2000 = "x-fmt/392";
	private static final String PUID_JPEG = "fmt/43";
	
	private static final String BROKEN_PDF = "BrokenPDF.pdf";
	private static final String BROKEN_JPEG = "BrokenJPG.jpg";
	private static final String BROKEN_JP2 = "BrokenJP2.jp2";
	private static final String BROKEN_TIFF = "BrokenTIF.tif";
	
	private static final String VALID_PDF = "ValidPDF.pdf";
	private static final String VALID_JPEG = "ValidJPG.jpg";
	private static final String VALID_JP2 = "ValidJP2.jp2";
	private static final String VALID_TIFF = "ValidTIF.tif";
	
	static JHoveParameterMapping[] jHoveMap;
	static FormatMapping[] puidMap;
	
	private static final Path testRoot = Path.make(TC.TEST_ROOT_FORMAT,"CTFileFormatFacadeExtract");
	private static final ConfigurableFileFormatFacade fff = new ConfigurableFileFormatFacade();;
	private static final JhoveMetadataExtractor metadataExtractor = new JhoveMetadataExtractor();
	public static final File CI_DATABASE_CFG = new RelativePath("src","main","xml","hibernateCentralDB.cfg.xml.ci").toFile();
	
	
	@BeforeClass
	public static void setUpBeforeClass() throws IOException {
		CTTestHelper.prepareWhiteBoxTest();
		metadataExtractor.setCli(new CommandLineConnector());
		if (!metadataExtractor.isConnectable()) fail();
		fff.setMetadataExtractor(metadataExtractor);
		fff.setFormatScanService(new FakeFormatScanService());
		FileUtils.copyFile(CI_DATABASE_CFG, C.HIBERNATE_CFG);
		
		setupHibernate();
	}
	
	@AfterClass
	public static void afterClass() throws IOException {
		CTTestHelper.cleanUpWhiteBoxTest();
		FileUtils.deleteQuietly(C.HIBERNATE_CFG);
		tearDownHibernate();
	}
	
	@Before
	public void setUpBefore() throws IOException {
		CTTestHelper.prepareWhiteBoxTest();
		metadataExtractor.setCli(new CommandLineConnector());
		if (!metadataExtractor.isConnectable()) fail();
		fff.setMetadataExtractor(metadataExtractor);
		fff.setFormatScanService(new FakeFormatScanService());
		FileUtils.copyFile(CI_DATABASE_CFG, C.HIBERNATE_CFG);
		
		//setupHibernate();
	}
	
	private static void tearDownHibernate(){
		HibernateUtil.init(C.HIBERNATE_CFG.getAbsolutePath());
		Session session = HibernateUtil.openSession();
		try {
			session.beginTransaction();
			for(FormatMapping iter: puidMap)
				session.delete(iter);
			for(JHoveParameterMapping iter: jHoveMap)
				session.delete(iter);			
			session.getTransaction().commit();
		} catch (Exception e) {
			System.out.println("ERROR: CANNOT CONNECT TO DATABASE: "+e.getMessage());
			e.printStackTrace();
			session.getTransaction().rollback();
			throw new RuntimeException(e);
		}
	}
	
	
	private static void setupHibernate(){
		FormatMapping tiffFM=new FormatMapping();
		tiffFM.setPuid(PUID_TIFF);
		tiffFM.setMime_type("image/tiff");
		
		FormatMapping jp2000FM=new FormatMapping();
		jp2000FM.setPuid(PUID_JP2000);
		jp2000FM.setMime_type("image/jp2");
		
		FormatMapping jpegFM=new FormatMapping();
		jpegFM.setPuid(PUID_JPEG);
		jpegFM.setMime_type("image/jpeg");
		
		FormatMapping pdfFM=new FormatMapping();
		pdfFM.setPuid(PUID_PDF);
		pdfFM.setMime_type("application/pdf");
		
		puidMap=new FormatMapping[]{tiffFM,jp2000FM,jpegFM,pdfFM};
		jHoveMap=new JHoveParameterMapping[]{new JHoveParameterMapping(tiffFM.getMime_type(), "-m TIFF-hul"),
				new JHoveParameterMapping(jp2000FM.getMime_type(), "-m JPEG2000-hul"),
				new JHoveParameterMapping(jpegFM.getMime_type(), "-m JPEG-hul"),
				new JHoveParameterMapping(pdfFM.getMime_type(), "-m PDF-hul"),};
		HibernateUtil.init(C.HIBERNATE_CFG.getAbsolutePath());
		Session session = HibernateUtil.openSession();
		
		try {
			session.beginTransaction();
			for(FormatMapping iter: puidMap)
				session.save(iter);
			for(JHoveParameterMapping iter: jHoveMap)
				session.save(iter);			
			session.getTransaction().commit();
		} catch (Exception e) {
			System.out.println("ERROR: CANNOT CONNECT TO DATABASE: "+e.getMessage());
			e.printStackTrace();
			session.getTransaction().rollback();
			throw new RuntimeException(e);
		}
	}

	
	@After
	public void tearDown() throws IOException {
		CTTestHelper.cleanUpWhiteBoxTest();
		FileUtils.deleteQuietly(C.HIBERNATE_CFG);
	}
	
	
	
	@Test
	public void extractEAD() {
		assertTrue(fff.connectivityCheck());
		
		try {
			fff.extract(Path.makeFile(testRoot,VALID_PDF), Path.makeFile(testRoot,VALID_PDF+".output"),PUID_PDF);
		} catch (ConnectionException e) {
			fail(e.getMessage());
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void binaryNotPresent() {
		CTTestHelper.cleanUpWhiteBoxTest();

		try {
			fff.extract(Path.makeFile(testRoot,VALID_PDF), Path.makeFile(testRoot,VALID_PDF+".output"),PUID_PDF);
			fail();
		} catch (ConnectionException e) {
			assertTrue(e!=null);
		} catch (Exception e) {
			fail(e.getMessage());;
		} 
	}
	
	

	//expected exception can be reactivated later, after jhove is used right way
	@Test //(expected=JHoveValidationException.class)
	public void extractFailPDF() {
		try {
			fff.extract(Path.makeFile(testRoot,BROKEN_PDF), Path.makeFile(testRoot,BROKEN_PDF+".output"),PUID_PDF);
		} catch (ConnectionException e) {
			fail(e.getMessage());
		}catch(JHoveValidationException e){
			Assert.assertTrue( e.getMessage().contains("format=PDF"));
	        throw e;
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	//expected exception can be reactivated later, after jhove is used right way
	@Test //(expected=JHoveValidationException.class)
	public void extractFailJPEG() {
		try {
			fff.extract(Path.makeFile(testRoot,BROKEN_JPEG), Path.makeFile(testRoot,BROKEN_JPEG+".output"),PUID_JPEG);
		} catch (ConnectionException e) {
			fail(e.getMessage());
		}catch(JHoveValidationException e){
			Assert.assertTrue( e.getMessage().contains("format=JPEG"));
	        throw e;
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	//expected exception can be reactivated later, after jhove is used right way
	@Test //(expected=JHoveValidationException.class)
	public void extractFailJP2000() {
		try {
			fff.extract(Path.makeFile(testRoot,BROKEN_JP2), Path.makeFile(testRoot,BROKEN_JP2+".output"),PUID_JP2000);
		} catch (ConnectionException e) {
			fail(e.getMessage());
		}catch(JHoveValidationException e){
			Assert.assertTrue( e.getMessage().contains("format=JPEG 2000"));
	        throw e;
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	//expected exception can be reactivated later, after jhove is used right way
	@Test //(expected=JHoveValidationException.class)
	public void extractFailTIFF() {
		try {
			fff.extract(Path.makeFile(testRoot,BROKEN_TIFF), Path.makeFile(testRoot,BROKEN_TIFF+".output"),PUID_TIFF);
		} catch (ConnectionException e) {
			fail(e.getMessage());
		}catch(JHoveValidationException e){
			Assert.assertTrue( e.getMessage().contains("format=TIFF"));
	        throw e;
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void extractValidPDF() {
		try {
			fff.extract(Path.makeFile(testRoot,VALID_PDF), Path.makeFile(testRoot,VALID_PDF+".output"),PUID_PDF);
		} catch (ConnectionException e) {
			fail(e.getMessage());
		}catch(JHoveValidationException e){
			fail(e.getMessage());
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	
	@Test
	public void extractValidJPEG() {
		try {
			fff.extract(Path.makeFile(testRoot,VALID_JPEG), Path.makeFile(testRoot,VALID_JPEG+".output"),PUID_JPEG);
		} catch (ConnectionException e) {
			fail(e.getMessage());
		}catch(JHoveValidationException e){
			fail(e.getMessage());
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void extractValidJP2000() {
		try {
			fff.extract(Path.makeFile(testRoot,VALID_JP2), Path.makeFile(testRoot,VALID_JP2+".output"),PUID_JP2000);
		} catch (ConnectionException e) {
			fail(e.getMessage());
		}catch(JHoveValidationException e){
			fail(e.getMessage());
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	
	@Test
	public void extractValidTIFF() {
		try {
			fff.extract(Path.makeFile(testRoot,VALID_TIFF), Path.makeFile(testRoot,VALID_TIFF+".output"),PUID_TIFF);
		} catch (ConnectionException e) {
			fail(e.getMessage());
		}catch(JHoveValidationException e){
			fail(e.getMessage());
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
}
