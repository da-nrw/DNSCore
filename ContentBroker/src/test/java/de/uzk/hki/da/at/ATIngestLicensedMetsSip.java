package de.uzk.hki.da.at;

import static org.junit.Assert.assertEquals;
import org.json.*;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.uzk.hki.da.metadata.MetsLicense;
import de.uzk.hki.da.metadata.MetsParser;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.model.WorkArea;
import de.uzk.hki.da.utils.C;
import de.uzk.hki.da.utils.FolderUtils;
import de.uzk.hki.da.utils.Path;
import de.uzk.hki.da.utils.XMLUtils;

public class ATIngestLicensedMetsSip extends AcceptanceTest {
	
	private static final String sipLicenseInMets = "LicenseInMets";
	private static final String sipLicenseInPremis = "LicenseInPremis";
	private static final String sipLicenseInMetsAndPremis = "LicenseInMetsAndPremis";
	private static final String sipNoLicenseNoPublication = "NoLicenseNoPublication";
	private static final String sipNoLicensePublication = "NoLicensePublication";
	
	private static final String sipPublicMetsLicenseInMets = "PublicMetsLicenseInMets";
	private static final String sipPublicMetsNoLicenseNoPublication = "PublicMetsNoLicenseNoPublication";
	private static final String sipPublicMetsNoLicensePublication = "PublicMetsNoLicensePublication";
	
	
	private static final String sipNoMetsNoLicensePublication = "NoMetadataNoLicense";
	private static final String sipNoMetsLicenseInPremisPublication = "NoMetadataLicense";
	private static final String sipNoMetsNoLicenseNoPublication = "NoMetadataNoLicenseNoPublication";
	
	private static final String sipDeltaLicense = "SipDeltaLicense";
	private static final String sipDeltaLicensePublicMets = "SipDeltaLicensePublicMets";
	
	private static final MetsLicense LICENSE_PREMIS = new MetsLicense("use and reproduction","https://creativecommons.org/licenses/by-sa/4.0/","CC-BY-SA-Lizenz (v4.0)",  "CC v4.0 International Lizenz: Namensnennung - Weitergabe unter gleichen Bedingungen");
	private static final MetsLicense LICENSE_METS = new MetsLicense("use and reproduction","http://creativecommons.org/licenses/by-nc-sa/4.0","CC-BY-NC-SA-Lizenz (4.0)",  "cc-by-nc-sa_4.0");
	private static final MetsLicense LICENSE_PUBLIC_METS = new MetsLicense("use and reproduction","http://creativecommons.org/publicdomain/mark/1.0/","Public Domain Mark 1.0",  "pdm");
	
	Path contractorsPipsPublic = Path.make(localNode.getWorkAreaRootPath(),WorkArea.PIPS, WorkArea.PUBLIC, testContractor.getUsername());
	private static Object object1;
	
	private  String PORTAL_CI_TEST =getTestIndex();
	
	@After
	public void tearDown() {
		setUserPublicMets(false);
	}

	@BeforeClass
	public static void setUp() throws IOException, InterruptedException {
		activateLicenseValidation();
	}
	
	@AfterClass
	public static void tearDownAfterClass() throws IOException{
		deactivateLicenseValidation();
	}
	
	@Test
	public void testLicenseInMets() throws IOException, JDOMException {
		assertEquals("preservationSystem.getLicenseValidationFlag()!=YES", preservationSystem.getLicenseValidationFlag(),C.PRESERVATIONSYS_LICENSE_VALIDATION_YES);
		assertEquals("preservationSystem.getLicenseValidationTestCSNFlag()!=YES", preservationSystem.getLicenseValidationTestCSNFlag(),C.PRESERVATIONSYS_LICENSE_VALIDATION_YES);
		
		ath.putSIPtoIngestArea(sipLicenseInMets, "tgz", sipLicenseInMets);
		ath.awaitObjectState(sipLicenseInMets,Object.ObjectStatus.ArchivedAndValidAndNotInWorkflow);
		ath.waitForDefinedPublishedState(sipLicenseInMets);
		ath.waitForObjectPublishedState(sipLicenseInMets, C.PUBLISHEDFLAG_PUBLIC);
		
		object1=ath.getObject(sipLicenseInMets);
		assertEquals(object1.getLicense_flag(), C.LICENSEFLAG_METS);
		
		checkLicenseInMetadata(object1,LICENSE_METS);
	}
	
	@Test
	public void testLicenseInPremis() throws IOException, JDOMException {
		ath.putSIPtoIngestArea(sipLicenseInPremis, "tgz", sipLicenseInPremis);
		ath.awaitObjectState(sipLicenseInPremis,Object.ObjectStatus.ArchivedAndValidAndNotInWorkflow);
		ath.waitForDefinedPublishedState(sipLicenseInPremis);
		ath.waitForObjectPublishedState(sipLicenseInPremis, C.PUBLISHEDFLAG_PUBLIC);
		
		object1=ath.getObject(sipLicenseInPremis);
		assertEquals(object1.getLicense_flag(), C.LICENSEFLAG_PREMIS);
		checkLicenseInMetadata(object1,LICENSE_PREMIS);
	}
	
	@Test
	public void testLicenseInMetsAndPremis() throws IOException, JDOMException, InterruptedException {
		ath.putSIPtoIngestArea(sipLicenseInMetsAndPremis, "tgz", sipLicenseInMetsAndPremis);
		ath.waitForJobToBeInErrorStatus(sipLicenseInMetsAndPremis, C.WORKFLOW_STATUS_DIGIT_USER_ERROR);

		assertEquals(ath.getJob(sipLicenseInMetsAndPremis).getStatus(),"144");
	}
	
	@Test
	public void testNoLicenseNoPublication() throws IOException, JDOMException {
		ath.putSIPtoIngestArea(sipNoLicenseNoPublication, "tgz", sipNoLicenseNoPublication);
		ath.awaitObjectState(sipNoLicenseNoPublication,Object.ObjectStatus.ArchivedAndValidAndNotInWorkflow);
		ath.waitForDefinedPublishedState(sipNoLicenseNoPublication);
		ath.waitForObjectPublishedState(sipNoLicenseNoPublication, C.PUBLISHEDFLAG_INSTITUTION);
		
		object1=ath.getObject(sipNoLicenseNoPublication);
		assertEquals(object1.getLicense_flag(), C.LICENSEFLAG_NO_LICENSE);
	}
	
	@Test
	public void testNoLicensePublication() throws IOException, JDOMException, InterruptedException {
		ath.putSIPtoIngestArea(sipNoLicensePublication, "tgz", sipNoLicensePublication);
		ath.waitForJobToBeInErrorStatus(sipNoLicensePublication, C.WORKFLOW_STATUS_DIGIT_USER_ERROR);
	
		assertEquals(ath.getJob(sipNoLicensePublication).getStatus(),"144");
	}
	
	
	@Test
	public void testPublicMetsLicenseInMets() throws IOException, JDOMException {
		setUserPublicMets(true);
		ath.putSIPtoIngestArea(sipPublicMetsLicenseInMets, "tgz", sipPublicMetsLicenseInMets);
		ath.awaitObjectState(sipPublicMetsLicenseInMets,Object.ObjectStatus.ArchivedAndValidAndNotInWorkflow);
		ath.waitForDefinedPublishedState(sipPublicMetsLicenseInMets);
		ath.waitForObjectPublishedState(sipPublicMetsLicenseInMets, C.PUBLISHEDFLAG_PUBLIC);
		
		object1=ath.getObject(sipPublicMetsLicenseInMets);
		assertEquals(object1.getLicense_flag(), C.LICENSEFLAG_PUBLIC_METS);
		
		checkLicenseInMetadata(object1,LICENSE_PUBLIC_METS);
	}
	
	@Test
	public void testPublicMetsNoLicenseNoPublication() throws IOException, JDOMException, InterruptedException {
		setUserPublicMets(true);
		ath.putSIPtoIngestArea(sipPublicMetsNoLicenseNoPublication, "tgz", sipPublicMetsNoLicenseNoPublication);
		ath.awaitObjectState(sipPublicMetsNoLicenseNoPublication,Object.ObjectStatus.ArchivedAndValidAndNotInWorkflow);
		object1=ath.getObject(sipPublicMetsNoLicenseNoPublication);
		ath.waitForObjectPublishedState(sipPublicMetsNoLicenseNoPublication, C.PUBLISHEDFLAG_INSTITUTION);
		assertEquals(object1.getLicense_flag(), C.LICENSEFLAG_NO_LICENSE);
		assertTrue(object1.getPublished_flag()!=C.PUBLISHEDFLAG_PUBLIC);
	}
	
	@Test
	public void testPublicMetsNoLicensePublication() throws IOException, JDOMException, InterruptedException {
		setUserPublicMets(true);
		ath.putSIPtoIngestArea(sipPublicMetsNoLicensePublication, "tgz", sipPublicMetsNoLicensePublication);
		ath.waitForJobToBeInErrorStatus(sipPublicMetsNoLicensePublication, C.WORKFLOW_STATUS_DIGIT_USER_ERROR);
		
		assertEquals(ath.getJob(sipPublicMetsNoLicensePublication).getStatus(),"144");
	}
	
	
	@Test
	public void testPublicMetsNoLicensePublicationDeactivatedLicenseValidation() throws IOException, JDOMException, InterruptedException {
		setUserPublicMets(true);
		deactivateLicenseValidation();
		try{
			String sipPublicMetsNoLicensePublicationValidationDeactivated=sipPublicMetsNoLicensePublication+"ValidationDeactivated";
			ath.putSIPtoIngestArea(sipPublicMetsNoLicensePublication, "tgz", sipPublicMetsNoLicensePublicationValidationDeactivated);
			ath.awaitObjectState(sipPublicMetsNoLicensePublicationValidationDeactivated,Object.ObjectStatus.ArchivedAndValidAndNotInWorkflow);
			ath.waitForDefinedPublishedState(sipPublicMetsNoLicensePublicationValidationDeactivated);
			ath.waitForObjectPublishedState(sipPublicMetsNoLicensePublicationValidationDeactivated, C.PUBLISHEDFLAG_PUBLIC);
			
			object1=ath.getObject(sipPublicMetsNoLicensePublicationValidationDeactivated);
			assertEquals(object1.getLicense_flag(), C.LICENSEFLAG_UNDEFINED);
			assertTrue((object1.getPublished_flag() & C.PUBLISHEDFLAG_PUBLIC)!=0);
			assertTrue((object1.getPublished_flag() & C.PUBLISHEDFLAG_INSTITUTION)!=0);
			
			checkLicenseInMetadata(object1,null);
		}finally{
			activateLicenseValidation();
		}
	}
	

	@Test
	public void testNOMetsNoLicensePublication() throws IOException, JDOMException, InterruptedException {
		
		ath.putSIPtoIngestArea(sipNoMetsNoLicensePublication, "tgz", sipNoMetsNoLicensePublication);
		ath.waitForJobToBeInErrorStatus(sipNoMetsNoLicensePublication, C.WORKFLOW_STATUS_DIGIT_USER_ERROR);
		
		assertEquals(ath.getJob(sipNoMetsNoLicensePublication).getStatus(),"144");
	}
	
	@Test
	public void testNOMetsLicenseInPremisPublication() throws IOException, JDOMException, InterruptedException {
		
		ath.putSIPtoIngestArea(sipNoMetsLicenseInPremisPublication, "tgz", sipNoMetsLicenseInPremisPublication);
		ath.waitForJobToBeInErrorStatus(sipNoMetsLicenseInPremisPublication, C.WORKFLOW_STATUS_DIGIT_USER_ERROR);
		
		assertEquals(ath.getJob(sipNoMetsLicenseInPremisPublication).getStatus(),"144");
	}
	
	@Test
	public void testNOMetsNoLicenseNoPublication() throws IOException, JDOMException, InterruptedException {
		
		ath.putSIPtoIngestArea(sipNoMetsNoLicenseNoPublication, "tgz", sipNoMetsNoLicenseNoPublication);
		ath.awaitObjectState(sipNoMetsNoLicenseNoPublication,Object.ObjectStatus.ArchivedAndValidAndNotInWorkflow);
		object1=ath.getObject(sipNoMetsNoLicenseNoPublication);
		ath.waitForObjectPublishedState(sipNoMetsNoLicenseNoPublication, C.PUBLISHEDFLAG_INSTITUTION);
		assertEquals(object1.getLicense_flag(), C.LICENSEFLAG_NO_LICENSE);
		assertTrue(object1.getPublished_flag()!=C.PUBLISHEDFLAG_PUBLIC);
	}
	
	@Test
	public void testNOMetsNoLicenseNoPublicationLicenseValidationDeactivated() throws IOException, JDOMException, InterruptedException {
		
		deactivateLicenseValidation();
		try{
			String sipNoMetsNoLicensePublicationValidationDeactivated=sipNoMetsNoLicensePublication+"ValidationDeactivated";
			//deactivateLicenseValidation
			ath.putSIPtoIngestArea(sipNoMetsNoLicensePublication, "tgz",  sipNoMetsNoLicensePublicationValidationDeactivated);
			ath.awaitObjectState(sipNoMetsNoLicensePublicationValidationDeactivated,Object.ObjectStatus.ArchivedAndValidAndNotInWorkflow);
			object1=ath.getObject(sipNoMetsNoLicensePublicationValidationDeactivated);
			ath.waitForObjectPublishedState(sipNoMetsNoLicensePublicationValidationDeactivated, C.PUBLISHEDFLAG_PUBLIC);
			assertEquals(object1.getLicense_flag(), C.LICENSEFLAG_UNDEFINED); // No license validation, therefore default value 'UNDEFINED'
			assertTrue((object1.getPublished_flag() & C.PUBLISHEDFLAG_PUBLIC)!=0);
			assertTrue((object1.getPublished_flag() & C.PUBLISHEDFLAG_INSTITUTION)!=0);
		}finally{
			activateLicenseValidation();
		}
	}
	
	
	
	@Test
	public void testLicenseDeltaPublicMets() throws IOException, JDOMException, InterruptedException {

		//Ingest 1 sipPublicMetsLicenseInMets
		setUserPublicMets(true);
		
		ath.putSIPtoIngestArea(sipPublicMetsLicenseInMets, "tgz", sipDeltaLicensePublicMets);
		ath.awaitObjectState(sipDeltaLicensePublicMets,Object.ObjectStatus.InWorkflow);
		ath.awaitObjectState(sipDeltaLicensePublicMets,Object.ObjectStatus.ArchivedAndValidAndNotInWorkflow);
		ath.waitForDefinedPublishedState(sipDeltaLicensePublicMets);
		ath.waitForObjectPublishedState(sipDeltaLicensePublicMets, C.PUBLISHEDFLAG_PUBLIC);
		
		object1=ath.getObject(sipDeltaLicensePublicMets);
		assertEquals(object1.getLicense_flag(), C.LICENSEFLAG_PUBLIC_METS);
		
		checkLicenseInMetadata(object1,LICENSE_PUBLIC_METS);
		
		//Ingest 2 sipPublicMetsNoLicenseNoPublication
		ath.putSIPtoIngestArea(sipPublicMetsNoLicenseNoPublication, "tgz", sipDeltaLicensePublicMets);
		ath.awaitObjectState(sipDeltaLicensePublicMets,Object.ObjectStatus.InWorkflow);
		ath.awaitObjectState(sipDeltaLicensePublicMets,Object.ObjectStatus.ArchivedAndValidAndNotInWorkflow);
		object1=ath.getObject(sipDeltaLicensePublicMets);
		ath.waitForObjectPublishedState(sipDeltaLicensePublicMets, C.PUBLISHEDFLAG_INSTITUTION);
		assertEquals(object1.getLicense_flag(), C.LICENSEFLAG_NO_LICENSE);
		assertTrue(object1.getPublished_flag()!=C.PUBLISHEDFLAG_PUBLIC);

		
		//Ingest 3 sipPublicMetsLicenseInMets
		ath.putSIPtoIngestArea(sipPublicMetsLicenseInMets, "tgz", sipDeltaLicensePublicMets);
		ath.awaitObjectState(sipDeltaLicensePublicMets,Object.ObjectStatus.InWorkflow);
		ath.awaitObjectState(sipDeltaLicensePublicMets,Object.ObjectStatus.ArchivedAndValidAndNotInWorkflow);
		ath.waitForDefinedPublishedState(sipDeltaLicensePublicMets);
		ath.waitForObjectPublishedState(sipDeltaLicensePublicMets, C.PUBLISHEDFLAG_PUBLIC);
		
		object1=ath.getObject(sipDeltaLicensePublicMets);
		assertEquals(object1.getLicense_flag(), C.LICENSEFLAG_PUBLIC_METS);
		
		checkLicenseInMetadata(object1,LICENSE_PUBLIC_METS);
		
		setUserPublicMets(false);
		
	}

	@Test
	public void testLicenseDelta() throws IOException, JDOMException, InterruptedException {
		//Ingest 1 sipLicenseInMets
		ath.putSIPtoIngestArea(sipLicenseInMets, "tgz", sipDeltaLicense);
		
		ath.awaitObjectState(sipDeltaLicense,Object.ObjectStatus.ArchivedAndValidAndNotInWorkflow);
		ath.waitForDefinedPublishedState(sipDeltaLicense);
		ath.waitForObjectPublishedState(sipDeltaLicense, C.PUBLISHEDFLAG_PUBLIC);
		
		object1=ath.getObject(sipDeltaLicense);
		assertEquals(object1.getLicense_flag(), C.LICENSEFLAG_METS);
		
		checkLicenseInMetadata(object1,LICENSE_METS);

		//Ingest 2 sipLicenseInPremis
		ath.putSIPtoIngestArea(sipLicenseInPremis, "tgz", sipDeltaLicense);
		ath.awaitObjectState(sipDeltaLicense,Object.ObjectStatus.InWorkflow);
		ath.awaitObjectState(sipDeltaLicense,Object.ObjectStatus.ArchivedAndValidAndNotInWorkflow);
		ath.waitForDefinedPublishedState(sipDeltaLicense);
		ath.waitForObjectPublishedState(sipDeltaLicense, C.PUBLISHEDFLAG_PUBLIC);
		
		object1=ath.getObject(sipDeltaLicense);
		assertEquals(object1.getLicense_flag(), C.LICENSEFLAG_PREMIS);
		checkLicenseInMetadata(object1,LICENSE_PREMIS);

		//Ingest 3 sipNoLicenseNoPublication
		ath.putSIPtoIngestArea(sipNoLicenseNoPublication, "tgz", sipDeltaLicense);
		ath.awaitObjectState(sipDeltaLicense,Object.ObjectStatus.InWorkflow);
		ath.awaitObjectState(sipDeltaLicense,Object.ObjectStatus.ArchivedAndValidAndNotInWorkflow);
		ath.waitForDefinedPublishedState(sipDeltaLicense);
		ath.waitForObjectPublishedState(sipDeltaLicense, C.PUBLISHEDFLAG_INSTITUTION);
		
		object1=ath.getObject(sipDeltaLicense);
		assertEquals(object1.getLicense_flag(), C.LICENSEFLAG_NO_LICENSE);
		
	}

	public void checkLicenseInMetadata(Object obj,MetsLicense license) throws IOException, JDOMException{

		SAXBuilder builder = XMLUtils.createValidatingSaxBuilder();
		if(license!=null){
			//testPIPMets
			File metsFile1 = ath.loadDefaultMetsFileFromPip(obj.getIdentifier());
			assertTrue(metsFile1.exists());
			Document metsDoc1 = builder.build(new FileReader(metsFile1));
			MetsParser mp = new MetsParser(metsDoc1);
			MetsLicense lic=mp.getLicenseForWholeMets();
			assertTrue(lic!=null);	
			assertEquals(lic, license);
		}
		
		//testEdm
		File edmFile1 = ath.loadFileFromPip(obj.getIdentifier(), "EDM.xml");
		assertTrue(edmFile1.exists());	
		Document edmDoc1 = builder.build(new FileReader(edmFile1));
	
		@SuppressWarnings("unchecked")
		List<Element> providetCho = edmDoc1.getRootElement().getChildren("ProvidedCHO", C.EDM_NS);
		assertTrue(providetCho.size()==1);
		for(Element pcho : providetCho) {
			assertTrue(license==null? pcho.getChild("rights", C.DC_NS)==null : pcho.getChild("rights", C.DC_NS).getValue().equals(license.getHref()));
		}
		
		Element edmLicElem=edmDoc1.getRootElement().getChild("Aggregation",C.ORE_NS).getChild("rights", C.EDM_NS);
		//((Attribute)edmDoc1.getRootElement().getChild("Aggregation",C.ORE_NS).getChild("rights", C.EDM_NS).getAttributes().get(0)).getValue()
		if(license==null ^ edmLicElem==null){
			assertTrue(false);
		}
		if(license!=null){
			String	edmLicString=((Attribute)edmLicElem.getAttributes().get(0)).getValue();
			assertTrue (edmLicString.equals(license.getHref()));
		}
		
		////	testIndex
		String jsonString=metadataIndex.getIndexedMetadata(PORTAL_CI_TEST, obj.getIdentifier());
		JSONObject jsonObj = (new JSONObject(jsonString)).getJSONObject("hits");
		assertEquals(1,jsonObj.getInt("total"));
		
		jsonObj = jsonObj.getJSONArray("hits").getJSONObject(0);
		jsonObj = jsonObj.getJSONObject("_source");
		JSONObject jsonObjCHO = jsonObj.getJSONObject("edm:aggregatedCHO");
		 try{
			 assertTrue(jsonObjCHO.getJSONArray("dc:rights").get(0).toString().equals(license.getHref()));		
		 }catch(JSONException e){ assertTrue(license==null);  }
		 try{
			 JSONObject esLicObj=jsonObj.getJSONArray("edm:rights").getJSONObject(0);
			 String esLicStr= esLicObj.getString("@id");
			 assertTrue(esLicStr.equals(license.getHref()));
		 }catch(JSONException e){
			 assertTrue(license==null);
         }

	}
}
