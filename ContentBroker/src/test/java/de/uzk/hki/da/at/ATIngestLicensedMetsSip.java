package de.uzk.hki.da.at;

import static org.junit.Assert.assertEquals;
import org.json.*;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
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
		assertTrue("preservationSystem.getLicenseValidationFlag()==0", preservationSystem.getLicenseValidationFlag()!=C.PRESERVATIONSYS_LICENSE_VALIDATION_NO);
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

		SAXBuilder builder = XMLUtils.createNonvalidatingSaxBuilder();
		//testPIPMets
		File metsFile1 = ath.loadDefaultMetsFileFromPip(obj.getIdentifier());
		assertTrue(metsFile1.exists());
		Document metsDoc1 = builder.build(new FileReader(metsFile1));
		MetsParser mp = new MetsParser(metsDoc1);
		MetsLicense lic=mp.getLicenseForWholeMets();
		assertTrue(lic!=null);	
		assertEquals(lic, license);
		
		//testEdm
		File edmFile1 = ath.loadFileFromPip(obj.getIdentifier(), "EDM.xml");
		assertTrue(edmFile1.exists());	
		Document edmDoc1 = builder.build(new FileReader(edmFile1));
	
		@SuppressWarnings("unchecked")
		List<Element> providetCho = edmDoc1.getRootElement().getChildren("ProvidedCHO", C.EDM_NS);
		assertTrue(providetCho.size()==1);
		for(Element pcho : providetCho) {
			assertTrue(pcho.getChild("rights", C.DC_NS).getValue().equals(license.getHref()));
		}
		
		////	testIndex
		String jsonString=metadataIndex.getIndexedMetadata(PORTAL_CI_TEST, obj.getIdentifier());
		JSONObject jsonObj = (new JSONObject(jsonString)).getJSONObject("hits");
		assertEquals(1,jsonObj.getInt("total"));
		
		jsonObj = jsonObj.getJSONArray("hits").getJSONObject(0);
		//assertTrue(jsonObj.getString("_id").contains("Jh-Dussel"));
		jsonObj = jsonObj.getJSONObject("_source");
		jsonObj = jsonObj.getJSONObject("edm:aggregatedCHO");
		assertTrue(jsonObj.getJSONArray("dc:rights").get(0).toString().equals(license.getHref()));
	}
}
