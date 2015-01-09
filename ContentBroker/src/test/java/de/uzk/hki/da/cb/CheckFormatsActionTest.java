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
package de.uzk.hki.da.cb;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.util.StringUtils;

import de.uzk.hki.da.core.SubsystemNotAvailableException;
import de.uzk.hki.da.format.FileWithFileFormat;
import de.uzk.hki.da.format.StandardFileFormatFacade;
import de.uzk.hki.da.model.DAFile;
import de.uzk.hki.da.model.Package;
import de.uzk.hki.da.service.HibernateUtil;
import de.uzk.hki.da.util.Path;
import de.uzk.hki.da.util.RelativePath;


/**
 * The Class CheckFormatsActionTest.
 *
 * @author Daniel M. de Oliveira
 */
public class CheckFormatsActionTest extends ConcreteActionUnitTest {

	private static final String REP2B = "2011_11_11+11_11+b";

	private static final String REP2A = "2011_11_11+11_11+a";

	private static final String REP1B = "2000_01_01+00_00+b";

	private static final String REP1A = "2000_01_01+00_00+a";

	private Path workAreaRootPath = new RelativePath("src/test/resources/cb/CheckFormatsActionTests/");

	@ActionUnderTest
	CheckFormatsAction action = new CheckFormatsAction();
	
	
	/**
	 * Sets the upformat scan service behaviour.
	 *
	 * @return the format scan service
	 * @author Daniel M. de Oliveira
	 * @throws IOException 
	 */
	@SuppressWarnings("unchecked")
	private StandardFileFormatFacade setUpFakeFormatScanService()
			throws IOException {
		StandardFileFormatFacade formatScanService = mock(StandardFileFormatFacade.class);
		
		when(formatScanService.identify((List<FileWithFileFormat>)anyObject())).thenAnswer(new Answer< List<DAFile> >(){
			@Override
			public List<DAFile> answer(InvocationOnMock invocation)
					throws Throwable {
				java.lang.Object[] args = invocation.getArguments();
				List<DAFile> list = (List<DAFile>) args[0];
				
				for (DAFile f:list){
					if (f.equals(new DAFile(null,REP2A,"_2.jpg"))){
						f.setFormatPUID("fmt/43");
					}
					if (f.equals(new DAFile(null,REP2B,"_2.tif"))){
						f.setFormatPUID("fmt/353");
					}
					if (f.equals(new DAFile(null,REP2A,"_3.avi"))){
						f.setFormatPUID("fmt/5");
						f.setSubformatIdentifier("cinepak");
					}
					
					if (f.equals(new DAFile(null,REP1A,"_1.jpg"))){
						f.setFormatPUID("fmt/43");
					}
					if (f.equals(new DAFile(null,REP1B,"_1.tif"))){
						f.setFormatPUID("fmt/353");
					}
					
					if (f.equals(new DAFile(null,REP1A,"_3.mov"))){
						f.setFormatPUID("x-fmt/384");
						f.setSubformatIdentifier("svq1");
					}
				}
				return list;
			}}
		);
		
		return formatScanService;
	}

	/**
	 * Sets the up.
	 *
	 * @throws Exception the exception
	 */
	@Before
	public void setUp() throws Exception{
		HibernateUtil.init("src/main/xml/hibernateCentralDB.cfg.xml.inmem");

		final Package sipPackage = new Package(); sipPackage.setName("2"); // the SIP / Delta
		sipPackage.setTransientBackRefToObject(o);
		
		DAFile a  = new DAFile(sipPackage,REP1A,"_1.jpg");
		DAFile b  = new DAFile(sipPackage,REP1A,"_3.mov");
		DAFile c = new DAFile(sipPackage,REP1B,"_1.tif");
		o.getPackages().get(0).getFiles().add(a);
		o.getPackages().get(0).getFiles().add(b);
		o.getPackages().get(0).getFiles().add(c);
		
		DAFile sipPackageOriginalFile  = new DAFile(sipPackage,REP2A,"_2.jpg");
		DAFile sipPackageOriginalFile2  = new DAFile(sipPackage,REP2A,"_3.avi");
		DAFile sipPackageConvertedFile = new DAFile(sipPackage,REP2B,"_2.tif"); 
		sipPackage.getFiles().add(sipPackageOriginalFile);
		sipPackage.getFiles().add(sipPackageOriginalFile2);
		sipPackage.getFiles().add(sipPackageConvertedFile);
		o.getPackages().add(sipPackage);
		
		
		n.setWorkAreaRootPath(Path.make(workAreaRootPath));
		
		action.setFileFormatFacade(setUpFakeFormatScanService());
		j.setRep_name("2011_11_11+11_11+");
		
	}

	@After
	public void tearDown() throws IOException {
		FileUtils.deleteDirectory(Path.makeFile(o.getDataPath(),"jhove_temp"));
	}
	
	@Test
	public void rollbackIsImplemented() {
		try {
			action.rollback();
		} catch (Exception e) {
			fail();
		}
	}
	
	@Test
	public void technicalMetadataFoldersForTechnicalMetadataArePresent() throws IOException, SubsystemNotAvailableException {
		action.implementation();
		
		assertTrue(Path.makeFile(o.getDataPath(),REP1A).exists());
		assertTrue(Path.makeFile(o.getDataPath(),REP1B).exists());
		assertTrue(Path.makeFile(o.getDataPath(),REP2A).exists());
		assertTrue(Path.makeFile(o.getDataPath(),REP2B).exists());
	}
	
	@Test
	public void removeExtractedTechnicalMetadata() throws Exception{
		action.implementation();
		action.rollback();
		
		assertFalse(Path.makeFile(o.getDataPath(),"jhove_temp").exists());
		
	}
	
	/**
	 * Test that new converted file has correct format info.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws SubsystemNotAvailableException 
	 */
	@Test
	public void testThatNewConvertedFileHasCorrectFormatInfo() throws IOException, SubsystemNotAvailableException{
		action.implementation();
		
		assertThat(o.getLatestPackage().
				getFiles().get(2).getFormatPUID()).isEqualTo("fmt/353");
	}
	
	/**
	 * Test that new converted file has correct format info with deltas.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws SubsystemNotAvailableException 
	 */
	@Test
	public void testThatNewConvertedFileHasCorrectFormatInfoWithDeltas() throws IOException, SubsystemNotAvailableException{
		
		Package oldPackage = new Package(); // the AIP
		oldPackage.setName("1"); oldPackage.setTransientBackRefToObject(o);
		DAFile newPackageOriginalFile  = new DAFile(oldPackage,REP1A,"_1.jpg");
		DAFile newPackageConvertedFile = new DAFile(oldPackage,REP1B,"_1.tif"); 
		List<DAFile> allFiles = new ArrayList<DAFile>(); allFiles.add(newPackageConvertedFile); allFiles.add(newPackageOriginalFile);
		oldPackage.getFiles().addAll(allFiles);
		o.getPackages().add(oldPackage);
		
		action.implementation();
		
		assertThat(o.getLatestPackage().getFiles().get(2).getFormatPUID()).isEqualTo("fmt/353");
	}

	
	
	
	/**
	 * Test that objects format lists are correct.
	 *
	 * @throws FileNotFoundException the file not found exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws SubsystemNotAvailableException 
	 */
	@Test
	public void testThatObjectsFormatListsAreCorrect() throws FileNotFoundException, IOException, SubsystemNotAvailableException{
		action.implementation();
		
		assertThat(o.getMost_recent_formats().toString()).contains("fmt/353");
		assertThat(o.getMost_recent_formats().toString()).contains("fmt/5");
		assertThat(StringUtils.countOccurrencesOf(
				o.getMost_recent_formats().toString(),"fmt/353")).isEqualTo(1);
		assertThat(StringUtils.countOccurrencesOf(
				o.getMost_recent_formats().toString(),"fmt/5")).isEqualTo(1);
		
		assertThat(o.getOriginal_formats().toString()).contains("fmt/43");
		assertThat(StringUtils.countOccurrencesOf(
				o.getOriginal_formats().toString(),"fmt/43")).isEqualTo(1);
		assertThat(o.getOriginal_formats().toString()).contains("fmt/5");
		assertThat(StringUtils.countOccurrencesOf(
				o.getOriginal_formats().toString(),"fmt/5")).isEqualTo(1);
	}
	
	/**
	 * Test that objects codec lists are correct.
	 *
	 * @throws FileNotFoundException the file not found exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws SubsystemNotAvailableException 
	 */
	@Test
	public void testThatObjectsCodecListsAreCorrect() throws FileNotFoundException, IOException, SubsystemNotAvailableException{
		action.implementation();
		
		assertThat(o.getMostRecentSecondaryAttributes().toString()).contains("cinepak");
		assertThat(StringUtils.countOccurrencesOf(
				o.getMostRecentSecondaryAttributes().toString(),"cinepak")).isEqualTo(1);
	}
	
	
	/**
	 * Test that objects formats lists are correct with deltas.
	 *
	 * @throws FileNotFoundException the file not found exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws SubsystemNotAvailableException 
	 */
	@Test
	public void testThatObjectsFormatsListsAreCorrectWithDeltas() throws FileNotFoundException, IOException, SubsystemNotAvailableException{
		
		Package aipPackage = new Package(); 
		aipPackage.setName("1"); 
		o.getPackages().add(aipPackage);
		
		action.implementation();
		
		assertThat(o.getMost_recent_formats().toString()).contains("fmt/353");
		assertThat(StringUtils.countOccurrencesOf(
				o.getMost_recent_formats().toString(),"fmt/353")).isEqualTo(1);
		assertThat(o.getMost_recent_formats().toString()).contains("fmt/5");
		assertThat(StringUtils.countOccurrencesOf(
				o.getMost_recent_formats().toString(),"fmt/5")).isEqualTo(1);
		
		assertThat(o.getOriginal_formats().toString()).contains("fmt/43");
		assertThat(o.getOriginal_formats().toString()).contains("fmt/5");
		assertThat(o.getOriginal_formats().toString()).contains("x-fmt/384");
		assertThat(StringUtils.countOccurrencesOf(
				o.getOriginal_formats().toString(),"fmt/43")).isEqualTo(1);
		assertThat(StringUtils.countOccurrencesOf(
				o.getOriginal_formats().toString(),"fmt/5")).isEqualTo(1);
		assertThat(StringUtils.countOccurrencesOf(
				o.getOriginal_formats().toString(),"x-fmt/384")).isEqualTo(1);
	}
	
	
	/**
	 * Test that objects codec lists are correct with deltas.
	 *
	 * @throws FileNotFoundException the file not found exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws SubsystemNotAvailableException 
	 */
	@Test
	public void testThatObjectsCodecListsAreCorrectWithDeltas() throws FileNotFoundException, IOException, SubsystemNotAvailableException{
		
		Package aipPackage = new Package(); 
		aipPackage.setName("1"); 
		o.getPackages().add(aipPackage);
		
		action.implementation();
		
		assertThat(o.getMostRecentSecondaryAttributes().toString()).contains("cinepak");
		assertThat(StringUtils.countOccurrencesOf(
				o.getMostRecentSecondaryAttributes().toString(),"cinepak")).isEqualTo(1);
		
	}
	
	
	
	
}
