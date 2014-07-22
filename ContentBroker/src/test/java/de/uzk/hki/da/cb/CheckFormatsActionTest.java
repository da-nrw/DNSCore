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
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.util.StringUtils;

import de.uzk.hki.da.format.FormatScanService;
import de.uzk.hki.da.format.JhoveScanService;
import de.uzk.hki.da.model.Contractor;
import de.uzk.hki.da.model.DAFile;
import de.uzk.hki.da.model.Job;
import de.uzk.hki.da.model.Node;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.model.Package;
import de.uzk.hki.da.utils.RelativePath;


/**
 * The Class CheckFormatsActionTest.
 *
 * @author Daniel M. de Oliveira
 */
public class CheckFormatsActionTest {

	private String workAreaRootPath = "src/test/resources/cb/CheckFormatsActionTests/";
	
	/** The action. */
	CheckFormatsAction action = new CheckFormatsAction();
	
	/** The job. */
	private Job job;

	/** The local node. */
	private Node localNode;

	/** The object. */
	private Object object;
	
	/**
	 * Sets the upformat scan service behaviour.
	 *
	 * @return the format scan service
	 * @throws FileNotFoundException the file not found exception
	 * @author Daniel M. de Oliveira
	 */
	@SuppressWarnings("unchecked")
	private FormatScanService setUpformatScanServiceBehaviour()
			throws FileNotFoundException {
		FormatScanService formatScanService = mock(FormatScanService.class);
		
		when(formatScanService.identify((List<DAFile>)anyObject())).thenAnswer(new Answer< List<DAFile> >(){
			@Override
			public List<DAFile> answer(InvocationOnMock invocation)
					throws Throwable {
				java.lang.Object[] args = (java.lang.Object[]) invocation.getArguments();
				List<DAFile> list = (List<DAFile>) args[0];
				
				for (DAFile f:list){
					if (f.equals(new DAFile(null,"2011_11_11+11_11+a","_2.jpg"))){
						f.setFormatPUID("fmt/43");
					}
					if (f.equals(new DAFile(null,"2011_11_11+11_11+b","_2.tif"))){
						f.setFormatPUID("fmt/353");
					}
					if (f.equals(new DAFile(null,"2011_11_11+11_11+a","_3.avi"))){
						f.setFormatPUID("fmt/5");
						f.setFormatSecondaryAttribute("cinepak");
					}
					
					if (f.equals(new DAFile(null,"2000_01_01+00_00+a","_1.jpg"))){
						f.setFormatPUID("fmt/43");
					}
					if (f.equals(new DAFile(null,"2000_01_01+00_00+b","_1.tif"))){
						f.setFormatPUID("fmt/353");
					}
					
					if (f.equals(new DAFile(null,"2000_01_01+00_00+a","_3.mov"))){
						f.setFormatPUID("x-fmt/384");
						f.setFormatSecondaryAttribute("svq1");
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
		localNode = new Node();
		Contractor contractor = new Contractor();
		contractor.setShort_name("TEST");
		localNode.setWorkAreaRootPath(new RelativePath(workAreaRootPath));

		JhoveScanService jhove = mock(JhoveScanService.class);
		when(jhove.extract((File)anyObject(),anyInt())).thenReturn("abc");
		
		final Package sipPackage = new Package(); sipPackage.setName("2"); // the SIP / Delta
		final Package aipPackage = new Package(); aipPackage.setName("1"); // the existing AIP
		
		DAFile a  = new DAFile(sipPackage,"2000_01_01+00_00+a","_1.jpg");
		DAFile b  = new DAFile(sipPackage,"2000_01_01+00_00+a","_3.mov");
		DAFile c = new DAFile(sipPackage,"2000_01_01+00_00+b","_1.tif"); 
		DAFile sipPackageOriginalFile  = new DAFile(sipPackage,"2011_11_11+11_11+a","_2.jpg");
		DAFile sipPackageOriginalFile2  = new DAFile(sipPackage,"2011_11_11+11_11+a","_3.avi");
		DAFile sipPackageConvertedFile = new DAFile(sipPackage,"2011_11_11+11_11+b","_2.tif"); 
		aipPackage.getFiles().add(a);
		aipPackage.getFiles().add(b);
		aipPackage.getFiles().add(c);
		sipPackage.getFiles().add(sipPackageOriginalFile);
		sipPackage.getFiles().add(sipPackageOriginalFile2);
		sipPackage.getFiles().add(sipPackageConvertedFile);
		
		object = new Object();
		List<Package> packages = new ArrayList<Package>(); packages.add(sipPackage); packages.add(aipPackage);
		object.setPackages(packages);
		object.setContractor(contractor);
		object.setIdentifier("identifier");
		object.setTransientNodeRef(localNode);
		object.reattach();
		
		FormatScanService formatScanService = setUpformatScanServiceBehaviour();
		
		job = new Job();
		job.setId(1000);
		job.setRep_name("2011_11_11+11_11+");
		job.setObject(object);
		
		action.setObject(object);
		action.setFormatScanService(formatScanService);
		action.setJob(job);
		action.setLocalNode(localNode);
		action.setSidecarExtensions("");
		action.setJhoveScanService(jhove);
		
	}

	
	/**
	 * Test that new converted file has correct format info.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@Test
	public void testThatNewConvertedFileHasCorrectFormatInfo() throws IOException{
		action.implementation();
		
		assertThat(job.getObject().getLatestPackage().getFiles().get(2).getFormatPUID()).isEqualTo("fmt/353");
	}
	
	/**
	 * Test that new converted file has correct format info with deltas.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@Test
	public void testThatNewConvertedFileHasCorrectFormatInfoWithDeltas() throws IOException{
		
		Package oldPackage = new Package(); // the AIP
		oldPackage.setName("1"); oldPackage.setTransientBackRefToObject(object);
		DAFile newPackageOriginalFile  = new DAFile(oldPackage,"2000_01_01+00_00+a","_1.jpg");
		DAFile newPackageConvertedFile = new DAFile(oldPackage,"2000_01_01+00_00+b","_1.tif"); 
		List<DAFile> allFiles = new ArrayList<DAFile>(); allFiles.add(newPackageConvertedFile); allFiles.add(newPackageOriginalFile);
		oldPackage.getFiles().addAll(allFiles);
		object.getPackages().add(oldPackage);
		
		action.implementation();
		
		assertThat(job.getObject().getLatestPackage().getFiles().get(2).getFormatPUID()).isEqualTo("fmt/353");
	}

	
	
	
	/**
	 * Test that objects format lists are correct.
	 *
	 * @throws FileNotFoundException the file not found exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@Test
	public void testThatObjectsFormatListsAreCorrect() throws FileNotFoundException, IOException{
		action.implementation();
		
		assertThat(job.getObject().getMost_recent_formats().toString()).contains("fmt/353");
		assertThat(job.getObject().getMost_recent_formats().toString()).contains("fmt/5");
		assertThat(StringUtils.countOccurrencesOf(
				job.getObject().getMost_recent_formats().toString(),"fmt/353")).isEqualTo(1);
		assertThat(StringUtils.countOccurrencesOf(
				job.getObject().getMost_recent_formats().toString(),"fmt/5")).isEqualTo(1);
		
		assertThat(job.getObject().getOriginal_formats().toString()).contains("fmt/43");
		assertThat(StringUtils.countOccurrencesOf(
				job.getObject().getOriginal_formats().toString(),"fmt/43")).isEqualTo(1);
		assertThat(job.getObject().getOriginal_formats().toString()).contains("fmt/5");
		assertThat(StringUtils.countOccurrencesOf(
				job.getObject().getOriginal_formats().toString(),"fmt/5")).isEqualTo(1);
	}
	
	/**
	 * Test that objects codec lists are correct.
	 *
	 * @throws FileNotFoundException the file not found exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@Test
	public void testThatObjectsCodecListsAreCorrect() throws FileNotFoundException, IOException{
		action.implementation();
		
		assertThat(job.getObject().getMostRecentSecondaryAttributes().toString()).contains("cinepak");
		assertThat(StringUtils.countOccurrencesOf(
				job.getObject().getMostRecentSecondaryAttributes().toString(),"cinepak")).isEqualTo(1);
	}
	
	
	/**
	 * Test that objects formats lists are correct with deltas.
	 *
	 * @throws FileNotFoundException the file not found exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@Test
	public void testThatObjectsFormatsListsAreCorrectWithDeltas() throws FileNotFoundException, IOException{
		
		Package aipPackage = new Package(); 
		aipPackage.setName("1"); 
		object.getPackages().add(aipPackage);
		
		action.implementation();
		
		assertThat(job.getObject().getMost_recent_formats().toString()).contains("fmt/353");
		assertThat(StringUtils.countOccurrencesOf(
				job.getObject().getMost_recent_formats().toString(),"fmt/353")).isEqualTo(1);
		assertThat(job.getObject().getMost_recent_formats().toString()).contains("fmt/5");
		assertThat(StringUtils.countOccurrencesOf(
				job.getObject().getMost_recent_formats().toString(),"fmt/5")).isEqualTo(1);
		
		assertThat(job.getObject().getOriginal_formats().toString()).contains("fmt/43");
		assertThat(job.getObject().getOriginal_formats().toString()).contains("fmt/5");
		assertThat(job.getObject().getOriginal_formats().toString()).contains("x-fmt/384");
		assertThat(StringUtils.countOccurrencesOf(
				job.getObject().getOriginal_formats().toString(),"fmt/43")).isEqualTo(1);
		assertThat(StringUtils.countOccurrencesOf(
				job.getObject().getOriginal_formats().toString(),"fmt/5")).isEqualTo(1);
		assertThat(StringUtils.countOccurrencesOf(
				job.getObject().getOriginal_formats().toString(),"x-fmt/384")).isEqualTo(1);
	}
	
	
	/**
	 * Test that objects codec lists are correct with deltas.
	 *
	 * @throws FileNotFoundException the file not found exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@Test
	public void testThatObjectsCodecListsAreCorrectWithDeltas() throws FileNotFoundException, IOException{
		
		Package aipPackage = new Package(); 
		aipPackage.setName("1"); 
		object.getPackages().add(aipPackage);
		
		action.implementation();
		
		assertThat(job.getObject().getMostRecentSecondaryAttributes().toString()).contains("cinepak");
		assertThat(StringUtils.countOccurrencesOf(
				job.getObject().getMostRecentSecondaryAttributes().toString(),"cinepak")).isEqualTo(1);
		
	}
	
	
	
	
}
