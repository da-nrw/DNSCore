/*
  DA-NRW Software Suite | ContentBroker
  Copyright (C) 2013 Historisch-Kulturwissenschaftliche Informationsverarbeitung
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

package de.uzk.hki.da.format;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Session;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import de.uzk.hki.da.core.HibernateUtil;
import de.uzk.hki.da.model.CentralDatabaseDAO;
import de.uzk.hki.da.model.DAFile;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.model.SecondStageScanPolicy;
import de.uzk.hki.da.utils.Path;
import de.uzk.hki.da.utils.RelativePath;
import de.uzk.hki.da.utils.TC;
import de.uzk.hki.da.utils.TESTHelper;

/**
 * @author Daniel M. de Oliveira
 */
public class FidoFormatScanServiceTests {

	private static FidoFormatScanService formatScanService;
	private static Path workAreaRootPath = Path.make(TC.TEST_ROOT_FORMAT,"FormatIdentificationTests");
	private static Object object;
	
	private List<FileWithFileFormat> files = new ArrayList<FileWithFileFormat>();
	private static CLIFormatIdentifier tiffCompressionMockIdentifier;
	private static CentralDatabaseDAO dao;
	
	
	private static FileWithFileFormat tiff = new DAFile(null,"","tiff"); 
	private static FileWithFileFormat xml  = new DAFile(null,"","xml");
	
	
	@BeforeClass
	public static void setUpBeforeClass(){
		
		HibernateUtil.init("src/main/xml/hibernateCentralDB.cfg.xml.inmem");
		
		dao = mock(CentralDatabaseDAO.class);
		
		SecondStageScanPolicy scan  = new SecondStageScanPolicy();
		scan.setPUID("fmt/353");
		scan.setAllowedValues("lzw,ccitt");
		scan.setFormatIdentifierScriptName("identify.sh");
		
		List<SecondStageScanPolicy> scans = new ArrayList<SecondStageScanPolicy>();
		scans.add(scan);
		when(dao.getSecondStageScanPolicies((Session)anyObject())).thenReturn(scans);
		
		formatScanService = new FidoFormatScanService(dao);
		
		PronomFormatIdentifierWrapper pronomMockIdentifier = mock(PronomFormatIdentifierWrapper.class);
		when(pronomMockIdentifier.getPuidForFile((tiff))).thenReturn("fmt/353");
		when(pronomMockIdentifier.getPuidForFile((xml))).thenReturn("fmt/101");
		formatScanService.setPronomFormatIdentifier(pronomMockIdentifier);
		
		tiffCompressionMockIdentifier = mock(CLIFormatIdentifier.class);
		when(tiffCompressionMockIdentifier.getConversionScript()).thenReturn(new File("identify.sh"));
		when(tiffCompressionMockIdentifier.healthCheck()).thenReturn(true);
		Set<CLIFormatIdentifier> formatSecondAttributeIdentifiers = new HashSet<CLIFormatIdentifier>();
		formatSecondAttributeIdentifiers.add(tiffCompressionMockIdentifier);
		formatScanService.setFormatSecondaryAttributeIdentifiers(formatSecondAttributeIdentifiers);
		
		object = TESTHelper.setUpObject("123",new RelativePath(workAreaRootPath));
	}




	@After
	public void tearDown(){
		files.clear();
	}


	@Test
	public void foundCodecIsNotAllowed() throws FileNotFoundException {
		Set<String> compression = new HashSet<String>(); compression.add("jpeg");
		when(tiffCompressionMockIdentifier.identify(tiff)).thenReturn(compression);
		
		files.add(tiff);
		
		try{
			formatScanService.identify(files);
			fail();
		}catch(RuntimeException e){
			System.out.println("Caught runtime exception as excpected, "+e);
		}
	}
	
	
	
	
	
	@Test
	public void identifyFileWithCompressionAlgorithm() throws FileNotFoundException{
		
		Set<String> compression = new HashSet<String>(); compression.add("lzw");
		when(tiffCompressionMockIdentifier.identify(tiff)).thenReturn(compression);
		
		files.add(tiff);
		
		formatScanService.identify(files);
		
		assertThat(files.get(0).getFormatPUID()).isEqualTo("fmt/353");
		assertThat(files.get(0).getFormatSecondaryAttribute()).isEqualTo("lzw");
	}
	
	
	
	
	@Test
	public void identifyFileWithoutCompressionAlgorithm() throws FileNotFoundException {
		
		Set<String> compression = new HashSet<String>();
		when(tiffCompressionMockIdentifier.identify(tiff)).thenReturn(compression);
		
		files.add(tiff);
		
		formatScanService.identify(files);
		assertThat(files.get(0).getFormatPUID()).isEqualTo("fmt/353");
		assertThat(files.get(0).getFormatSecondaryAttribute()).isEmpty();
	}
	
	
	
	
	
	@Test
	public void identifyListOfFiles() throws FileNotFoundException{
		
		files.add(tiff); 
		files.add(xml);
		
		formatScanService.identify(files);
		
		assertEquals("fmt/353",tiff.getFormatPUID());
		assertEquals("fmt/101",xml.getFormatPUID());
	}
	
	
	
	
	@Test
	public void fileDoesNotExist(){
		
		List<FileWithFileFormat> files = new ArrayList<FileWithFileFormat>(); 
		files.add(new DAFile(object.getLatestPackage(),"","nonexistent.tif"));
		try{
			formatScanService.identify(files);
			fail();
		}catch(Exception expected){}
		
	}
}
