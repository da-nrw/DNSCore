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


import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uzk.hki.da.core.UserException;
import de.uzk.hki.da.model.ConversionPolicy;
import de.uzk.hki.da.model.ConversionRoutine;
import de.uzk.hki.da.model.DAFile;
import de.uzk.hki.da.model.Event;
import de.uzk.hki.da.utils.C;
import de.uzk.hki.da.utils.RelativePath;


/**
 * The Class CreatePremisActionTests.
 *
 * @author Eugen Trebunski
 */
public class QualityLevelCheckActionTests extends ConcreteActionUnitTest{

	static final String minimalQualityLevelPremis="premisMinimalQualityLevel4.xml";
	static final String noMinimalQualityLevelPremis="premisNoMinimalQualityLevel.xml";
	Set<String> allAvents=new HashSet<String>(Arrays.asList(new String[]{C.EVENT_TYPE_QUALITY_FAULT_CONVERSION,
			C.EVENT_TYPE_QUALITY_FAULT_VALIDATION,C.EVENT_TYPE_QUALITY_CHECK_LEVEL_1,
			C.EVENT_TYPE_QUALITY_CHECK_LEVEL_2,C.EVENT_TYPE_QUALITY_CHECK_LEVEL_3,
			C.EVENT_TYPE_QUALITY_CHECK_LEVEL_4,C.EVENT_TYPE_QUALITY_CHECK_LEVEL_5}));
	
	DAFile unknownPuidDAF=null;
	DAFile nonLzaPuidDAF =null;
	DAFile lzaPuidDAF =null;
	DAFile lzaPuidDAF2=null;
	DAFile premisDAF =null;
	
	Event qualityFaultValidationEvent=null;
	Event qualityFaultConversationEvent = null;
	
	
	File premisFile=null;
	
	/** The Constant logger. */
	static final Logger logger = LoggerFactory.getLogger(QualityLevelCheckActionTests.class);
	
	@ActionUnderTest
	QualityLevelCheckAction action = new QualityLevelCheckAction();
	@Rule
    public ExpectedException thrown= ExpectedException.none();
	
	
	/*
	 * fail by minimal quality level from Premis
	 * 
	 * fail by minimal quality level by user configuration e.g. 5
	 * 
	 * quality 4 has unknown filetypes
	 * 
	 * quality 4 has non lza filetypes
	 * 
	 * quality 3 has validation events no conversion events (on same DAFile)
	 * 
	 * quality 2 has conversion events no validation events (on same DAFile)
	 * 
	 * quality 1 has conversion and validation events (on same DAFile)
	 * 
	 */
	

	
	/**
	 * Sets the up.
	 * @throws IOException 
	 */
	@Before
	public void setUp() throws IOException {
		//no conversion Policy defined maximal quality level is 4
		unknownPuidDAF = new DAFile("2018_02_22+11_54","140864.svg");
		unknownPuidDAF.setFormatPUID("fmt/413");
		
		//no LZA conversion Policy for File-Format defined, maximal quality level is 4
		nonLzaPuidDAF = new DAFile("2018_02_22+11_54+b","1408641.wav");
		nonLzaPuidDAF.setFormatPUID("fmt/6");
		
		lzaPuidDAF = new DAFile("2018_02_22+11_54+b","1408641.tif");
		lzaPuidDAF.setFormatPUID("fmt/353");
		
		lzaPuidDAF2 = new DAFile("2018_02_22+11_54+b","1408641.jp2");
		lzaPuidDAF2.setFormatPUID("x-fmt/392");
		
		premisDAF = new DAFile("2018_02_22+11_54+a",C.PREMIS);
		premisDAF.setFormatPUID("fmt/101");
		
		action.getObject().getLatestPackage().getFiles().add(premisDAF);
		
		ConversionRoutine defaultCR=new ConversionRoutine();
		defaultCR.setId(1);
		defaultCR.setName("default");
		defaultCR.setParams("param1 param2");
		
		ConversionPolicy noLZACP=new ConversionPolicy("fmt/6",defaultCR);
		noLZACP.setPresentation(true);
		noLZACP.setFormat_type(ConversionPolicy.FormatType.NONLZA);
		//noLZACP.set
		ConversionPolicy lzaCP1=new ConversionPolicy("fmt/353",defaultCR);
		lzaCP1.setPresentation(true);
		lzaCP1.setFormat_type(ConversionPolicy.FormatType.LZA);
		ConversionPolicy lzaCP2=new ConversionPolicy("x-fmt/392",defaultCR);
		lzaCP2.setPresentation(true);
		lzaCP2.setFormat_type(ConversionPolicy.FormatType.LZA);
		
		action.getPreservationSystem().getConversionRoutines().add(defaultCR);
		action.getPreservationSystem().getConversion_policies().add(noLZACP);
		action.getPreservationSystem().getConversion_policies().add(lzaCP1);
		action.getPreservationSystem().getConversion_policies().add(lzaCP2);

		qualityFaultValidationEvent = new Event();
		qualityFaultValidationEvent.setType(C.EVENT_TYPE_QUALITY_FAULT_VALIDATION);
		qualityFaultValidationEvent.setId(1);
		qualityFaultValidationEvent.setDetail("detail");
		qualityFaultValidationEvent.setAgent_type("NODE");
		qualityFaultValidationEvent.setAgent_name("TESTNODE");
		qualityFaultValidationEvent.setDate(new Date());
		
		
		qualityFaultConversationEvent = new Event();
		qualityFaultConversationEvent.setType(C.EVENT_TYPE_QUALITY_FAULT_CONVERSION);
		qualityFaultConversationEvent.setId(1);
		qualityFaultConversationEvent.setDetail("detail");
		qualityFaultConversationEvent.setAgent_type("NODE");
		qualityFaultConversationEvent.setAgent_name("TESTNODE");
		qualityFaultConversationEvent.setDate(new Date());

		action.getLocalNode().setWorkAreaRootPath(new RelativePath("src/test/resources/cb/QualityLevelCheckActionTests/"));
		premisFile=new File(action.getLocalNode().getWorkAreaRootPath().toFile().getAbsolutePath()+"/work/TEST/identifier/data/"+premisDAF.getPath());
		if(!premisFile.getParentFile().exists())
			premisFile.getParentFile().mkdirs();
		useMinimalQualityPremis(false);
	}
	
	

	/**
	 *  check if no QualityEvents after QualityCheckAction execution are there and C.EVENT_TYPE_QUALITY_CHECK_LEVEL_5 Event is created.
	 *
	 * @throws IOException
	 * @author Eugen Trebunski
	 */
	@Test
	public void testQualityLevel5NoQualityLevelEventsNoMinimalQualityPremis() throws IOException {
		action.getObject().getLatestPackage().getFiles().add( lzaPuidDAF);
		action.getObject().getLatestPackage().getFiles().add(lzaPuidDAF2);
		action.getObject().getLatestPackage().getFiles().add(premisDAF);
		action.implementation();
		
		boolean has5QualityLevelEvents=getEvents(C.EVENT_TYPE_QUALITY_CHECK_LEVEL_5).size()==1;
		boolean hasOtherQualityLevelEvents=getEvents(true,C.EVENT_TYPE_QUALITY_CHECK_LEVEL_5).size()!=0;
		assertTrue("Es sind QualityEvents vorhanden obwohl es keine geben sollte. ",!hasOtherQualityLevelEvents) ;
		assertTrue("Es sind QualityEvents nicht vorhanden obwohl es welche geben sollte. ",has5QualityLevelEvents) ;
	}
	
	@Test
	public void testQualityLevel5MinimalQualityPremis4() throws IOException {
		useMinimalQualityPremis(true);
		testQualityLevel5NoQualityLevelEventsNoMinimalQualityPremis();
	}
	
	@Test
	public void testQualityLevel5MinimalQualityUser5() throws IOException {
		action.getObject().getContractor().setMinimalIngestQualityLevel(5);
		testQualityLevel5NoQualityLevelEventsNoMinimalQualityPremis();
	}
	
	@Test
	public void testQualityLevel4ContainsUnknownFiles() throws IOException {
		action.getObject().getLatestPackage().getFiles().add(unknownPuidDAF);
		
		action.getObject().getLatestPackage().getFiles().add( lzaPuidDAF);
		action.getObject().getLatestPackage().getFiles().add(lzaPuidDAF2);
		action.getObject().getLatestPackage().getFiles().add(premisDAF);
		
		action.implementation();
		
		boolean has4QualityLevelEvents=getEvents(C.EVENT_TYPE_QUALITY_CHECK_LEVEL_4).size()==1;
		boolean hasOtherQualityLevelEvents=getEvents(true,C.EVENT_TYPE_QUALITY_CHECK_LEVEL_4).size()!=0;
		
		assertTrue("Es sind QualityEvents vorhanden obwohl es keine geben sollte. ",!hasOtherQualityLevelEvents) ;
		assertTrue("Es sind QualityEvents nicht vorhanden obwohl es welche geben sollte. ",has4QualityLevelEvents) ;
		Event unknownFilesQualityEvent=getEvents(C.EVENT_TYPE_QUALITY_CHECK_LEVEL_4).get(0);
		assertTrue(unknownFilesQualityEvent.getSource_file().getPath().equals(unknownPuidDAF.getPath()));
	}
	
	@Test
	public void testQualityLevel4ContainsUnknownFilesMinimalQualityLevel4Premis() throws IOException {
		useMinimalQualityPremis(true);
		testQualityLevel4ContainsUnknownFiles();
	}
	
	@Test
	public void testQualityLevel4ContainsUnknownFilesMinimalQualityLevel4User() throws IOException {
		action.getObject().getContractor().setMinimalIngestQualityLevel(4);
		testQualityLevel4ContainsUnknownFiles();
	}
	
	@Test
	public void testQualityLevel4ContainsUnknownFilesMinimalQualityLevel4UserAndPremis() throws IOException {
		action.getObject().getContractor().setMinimalIngestQualityLevel(4);
		useMinimalQualityPremis(true);
		testQualityLevel4ContainsUnknownFiles();
	}
	
	/**
	 * Uebersteuerung der minimalen Qualitätsstufe für den User über die Premis
	 * 
	 * @throws IOException
	 */
	@Test
	public void testQualityLevel4ContainsUnknownFilesMinimalQualityLevel4PremisAndUser5MQL() throws IOException {
		action.getObject().getContractor().setMinimalIngestQualityLevel(5); 
		useMinimalQualityPremis(true);
		testQualityLevel4ContainsUnknownFiles();
	}
	
	@Test
	public void testQualityLevel4ContainsNonLzaFiles() throws IOException {
		action.getObject().getLatestPackage().getFiles().add(nonLzaPuidDAF);
		
		action.getObject().getLatestPackage().getFiles().add( lzaPuidDAF);
		action.getObject().getLatestPackage().getFiles().add(lzaPuidDAF2);
		action.getObject().getLatestPackage().getFiles().add(premisDAF);
		action.implementation();
		
		boolean has4QualityLevelEvents=getEvents(C.EVENT_TYPE_QUALITY_CHECK_LEVEL_4).size()==1;
		boolean hasOtherQualityLevelEvents=getEvents(true,C.EVENT_TYPE_QUALITY_CHECK_LEVEL_4).size()!=0;
		assertTrue("Es sind QualityEvents vorhanden obwohl es keine geben sollte. ",!hasOtherQualityLevelEvents) ;
		assertTrue("Es sind QualityEvents nicht vorhanden obwohl es welche geben sollte. ",has4QualityLevelEvents) ;
	
		Event myEvent=getEvents(C.EVENT_TYPE_QUALITY_CHECK_LEVEL_4).get(0);
		assertTrue(myEvent.getSource_file().getPath().equals(nonLzaPuidDAF.getPath()));
	}
	
	@Test
	public void testQualityLevel3ContainsValidationFEvents() throws IOException {
		action.getObject().getLatestPackage().getFiles().add(unknownPuidDAF);
		action.getObject().getLatestPackage().getFiles().add(nonLzaPuidDAF);
		action.getObject().getLatestPackage().getFiles().add(lzaPuidDAF);
		action.getObject().getLatestPackage().getFiles().add(lzaPuidDAF2);
		action.getObject().getLatestPackage().getFiles().add(premisDAF);
		
		//previous validation fails 
		qualityFaultValidationEvent.setSource_file(lzaPuidDAF); 
		action.getObject().getLatestPackage().getEvents().add(qualityFaultValidationEvent);
		action.implementation();
		
		boolean has3QualityLevelEvents=getEvents(C.EVENT_TYPE_QUALITY_CHECK_LEVEL_3).size()==1;
		boolean hasValidationQualityLevelEvent=getEvents(true,C.EVENT_TYPE_QUALITY_CHECK_LEVEL_3).size()==1;
		assertTrue(hasValidationQualityLevelEvent) ;
		assertTrue("Es sind QualityEvents nicht vorhanden obwohl es welche geben sollte. ",has3QualityLevelEvents) ;
	
		Event myEvent=getEvents(C.EVENT_TYPE_QUALITY_CHECK_LEVEL_3).get(0);
		assertTrue(myEvent.getSource_file().getPath().equals(lzaPuidDAF.getPath()));
		
		myEvent=getEvents(C.EVENT_TYPE_QUALITY_FAULT_VALIDATION).get(0);
		assertTrue(myEvent.getSource_file().getPath().equals(lzaPuidDAF.getPath()));
	}
	@Test
	public void testQualityLevel3ContainsValidationFEventsMinimalQualityLevel4Premis() throws IOException {
		useMinimalQualityPremis(true);
		thrown.expect(UserException.class);
	    thrown.expectMessage("Current QualityLevel(3) is below required 4");
		testQualityLevel3ContainsValidationFEvents();
	}
	
	@Test
	public void testQualityLevel3ContainsValidationFEventsMinimalQualityLevel4User() throws IOException {
		action.getObject().getContractor().setMinimalIngestQualityLevel(4);
		thrown.expect(UserException.class);
	    thrown.expectMessage("Current QualityLevel(3) is below required 4");
		testQualityLevel3ContainsValidationFEvents();
	}
	
	@Test
	public void testQualityLevel3ContainsValidationFEventsMinimalQualityLevel4UserAndPremis() throws IOException {
		action.getObject().getContractor().setMinimalIngestQualityLevel(4);
		useMinimalQualityPremis(true);
		
		thrown.expect(UserException.class);
	    thrown.expectMessage("Current QualityLevel(3) is below required 4");
	    
		testQualityLevel3ContainsValidationFEvents();
	}
	
	@Test
	public void testQualityLevel3ContainsValidationFEventsMinimalQualityLevel4PremisAndUser3MQL() throws IOException {
		action.getObject().getContractor().setMinimalIngestQualityLevel(3);
		useMinimalQualityPremis(true);
		
		thrown.expect(UserException.class);
	    thrown.expectMessage("Current QualityLevel(3) is below required 4");
	    
		testQualityLevel3ContainsValidationFEvents();
	}
	
	
	@Test
	public void testQualityLevel2ContainsConversionAndValidationFEventsOnDifferenceFiles() throws IOException {
		action.getObject().getLatestPackage().getFiles().add(unknownPuidDAF);
		action.getObject().getLatestPackage().getFiles().add(nonLzaPuidDAF);
		action.getObject().getLatestPackage().getFiles().add(lzaPuidDAF);
		action.getObject().getLatestPackage().getFiles().add(lzaPuidDAF2);
		action.getObject().getLatestPackage().getFiles().add(premisDAF);
		
		//previous validation fails 
		qualityFaultValidationEvent.setSource_file(lzaPuidDAF); 
		action.getObject().getLatestPackage().getEvents().add(qualityFaultValidationEvent);
		
		
		//previous conversion fails
		qualityFaultConversationEvent.setSource_file(lzaPuidDAF2); 
		action.getObject().getLatestPackage().getEvents().add(qualityFaultConversationEvent);
		action.implementation();
		
		boolean has2QualityLevelEvents=getEvents(C.EVENT_TYPE_QUALITY_CHECK_LEVEL_2).size()==1;
		boolean hasOtherQualityLevelEvents=getEvents(true,C.EVENT_TYPE_QUALITY_CHECK_LEVEL_2).size()==2; //vault validation && vault conversation
		assertTrue(hasOtherQualityLevelEvents) ;
		assertTrue("Es sind QualityEvents nicht vorhanden obwohl es welche geben sollte. ",has2QualityLevelEvents) ;
	
		Event myEvent=getEvents(C.EVENT_TYPE_QUALITY_CHECK_LEVEL_2).get(0);
		assertTrue(myEvent.getSource_file().getPath().equals(lzaPuidDAF2.getPath()) || myEvent.getSource_file().getPath().equals(lzaPuidDAF.getPath()));
		
		myEvent=getEvents(C.EVENT_TYPE_QUALITY_FAULT_VALIDATION).get(0);
		assertTrue(myEvent.getSource_file().getPath().equals(lzaPuidDAF.getPath()));
		
		myEvent=getEvents(C.EVENT_TYPE_QUALITY_FAULT_CONVERSION).get(0);
		assertTrue(myEvent.getSource_file().getPath().equals(lzaPuidDAF2.getPath()));
	}
	
	@Test
	public void testQualityLevel2ContainsConversionFEvents() throws IOException {
		action.getObject().getLatestPackage().getFiles().add(unknownPuidDAF);
		action.getObject().getLatestPackage().getFiles().add(nonLzaPuidDAF);
		action.getObject().getLatestPackage().getFiles().add(lzaPuidDAF);
		action.getObject().getLatestPackage().getFiles().add(lzaPuidDAF2);
		action.getObject().getLatestPackage().getFiles().add(premisDAF);
		
		//previous conversion fails
		qualityFaultConversationEvent.setSource_file(lzaPuidDAF2); 
		action.getObject().getLatestPackage().getEvents().add(qualityFaultConversationEvent);
		action.implementation();
		
		boolean has2QualityLevelEvents=getEvents(C.EVENT_TYPE_QUALITY_CHECK_LEVEL_2).size()==1;
		boolean hasOtherQualityLevelEvents=getEvents(true,C.EVENT_TYPE_QUALITY_CHECK_LEVEL_2).size()==1;
		assertTrue(hasOtherQualityLevelEvents) ;
		assertTrue("Es sind QualityEvents nicht vorhanden obwohl es welche geben sollte. ",has2QualityLevelEvents) ;
	
		Event myEvent=getEvents(C.EVENT_TYPE_QUALITY_CHECK_LEVEL_2).get(0);
		assertTrue(myEvent.getSource_file().getPath().equals(lzaPuidDAF2.getPath()));
		
		
		myEvent=getEvents(C.EVENT_TYPE_QUALITY_FAULT_CONVERSION).get(0);
		assertTrue(myEvent.getSource_file().getPath().equals(lzaPuidDAF2.getPath()));
	}
	
	@Test
	public void testQualityLevel1ContainsConversionAndValidationEventsOnSameFiles() throws IOException {
		action.getObject().getLatestPackage().getFiles().add(unknownPuidDAF);
		action.getObject().getLatestPackage().getFiles().add(nonLzaPuidDAF);
		action.getObject().getLatestPackage().getFiles().add(lzaPuidDAF);
		action.getObject().getLatestPackage().getFiles().add(lzaPuidDAF2);
		action.getObject().getLatestPackage().getFiles().add(premisDAF);
		
		//previous validation fails 
		qualityFaultValidationEvent.setSource_file(lzaPuidDAF); 
		action.getObject().getLatestPackage().getEvents().add(qualityFaultValidationEvent);
		
		
		//previous conversion fails
		qualityFaultConversationEvent.setSource_file(lzaPuidDAF); 
		action.getObject().getLatestPackage().getEvents().add(qualityFaultConversationEvent);
		action.implementation();
		
		boolean has2QualityLevelEvents=getEvents(C.EVENT_TYPE_QUALITY_CHECK_LEVEL_1).size()==1;
		boolean hasOtherQualityLevelEvents=getEvents(true,C.EVENT_TYPE_QUALITY_CHECK_LEVEL_1).size()==2;
		assertTrue(hasOtherQualityLevelEvents) ;
		assertTrue("Es sind QualityEvents nicht vorhanden obwohl es welche geben sollte. ",has2QualityLevelEvents) ;
	
		Event myEvent=getEvents(C.EVENT_TYPE_QUALITY_CHECK_LEVEL_1).get(0);
		assertTrue(myEvent.getSource_file().getPath().equals(lzaPuidDAF.getPath()));
		
		myEvent=getEvents(C.EVENT_TYPE_QUALITY_FAULT_CONVERSION).get(0);
		assertTrue(myEvent.getSource_file().getPath().equals(lzaPuidDAF.getPath()));
		
		myEvent=getEvents(C.EVENT_TYPE_QUALITY_FAULT_VALIDATION).get(0);
		assertTrue(myEvent.getSource_file().getPath().equals(lzaPuidDAF.getPath()));
	}

	public void useMinimalQualityPremis(boolean bool) throws IOException{
		if(bool)
			FileUtils.copyFile(new File(action.getLocalNode().getWorkAreaRootPath().toFile().getAbsolutePath(),minimalQualityLevelPremis), premisFile);
		else
			FileUtils.copyFile(new File(action.getLocalNode().getWorkAreaRootPath().toFile().getAbsolutePath(),noMinimalQualityLevelPremis), premisFile);
	}
		/**
	 * Clean up.
	 */
	@After
	public void tearDown() {
		 FileUtils.deleteQuietly(premisFile);
		 
		 //remove all empty sub directories
		 File root=premisFile.getParentFile();
		 while(root.listFiles().length==0){
			 FileUtils.deleteQuietly(root);
			 root=root.getParentFile();
		 }
			 
	}
	
	public List<Event> getEvents(String...strings){
		return getEvents(false,strings);
	}
	
	public List<Event> getEvents(boolean not,String...strings){
		HashSet<String> expectedEvents=new HashSet<String>(Arrays.asList(strings));
		ArrayList<Event> ret=new ArrayList<Event>();
		for(Event e:action.getObject().getLatestPackage().getEvents()){
			if(not){
				if(!expectedEvents.contains(e.getType()))
					ret.add(e);
			}else
				if(expectedEvents.contains(e.getType()))
					ret.add(e);
		}
		return ret;
	}
}
