package de.uzk.hki.da.at;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import de.uzk.hki.da.metadata.MetsLicense;
import de.uzk.hki.da.metadata.MetsParser;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.model.WorkArea;
import de.uzk.hki.da.utils.C;
import de.uzk.hki.da.utils.Path;
import de.uzk.hki.da.utils.XMLUtils;

public class ATIngestLicensedMetsTypeRestrict extends AcceptanceTest {
	
	private static final String sipLicenseInMets = "PMetsDifferentLicenseTypesOk";
	private static final String sipLicenseInPremis = "MetsAllNoLicenseTypesLicenseInPremis";
	
	private static final String sipLicenseInMetsPartialNoLicense = "PMetsNoLicensePartialTypes";
	private static final String sipDifferentLicenseInMets = "PMetsDifferentLicenseTypesFail";
	private static final String sipNoLicenseNoPublication = "PMetsNoLicensePartialTypesNoPublication";
	
	private static final MetsLicense LICENSE_PREMIS = new MetsLicense("use and reproduction","https://creativecommons.org/licenses/by-sa/4.0/","CC-BY-SA-Lizenz (v4.0)",  "CC v4.0 International Lizenz: Namensnennung - Weitergabe unter gleichen Bedingungen");
	private static final MetsLicense LICENSE_METS = new MetsLicense("use and reproduction","https://creativecommons.org/publicdomain/zero/1.0/","CC0-Lizenz (v1.0)",  "CC0 1.0 Public Domain Dedication");
	
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
		setUserPublicMets(true);
		
		ath.putSIPtoIngestArea(sipLicenseInMets, "tgz", sipLicenseInMets);
		ath.awaitObjectState(sipLicenseInMets,Object.ObjectStatus.ArchivedAndValidAndNotInWorkflow);
		ath.waitForDefinedPublishedState(sipLicenseInMets);
		ath.waitForObjectPublishedState(sipLicenseInMets, C.PUBLISHEDFLAG_PUBLIC);
		
		object1=ath.getObject(sipLicenseInMets);
		assertEquals(object1.getLicense_flag(), C.LICENSEFLAG_PUBLIC_METS);
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
	public void testLicenseInMetsPartialNoLicense() throws IOException, JDOMException, InterruptedException {
		ath.putSIPtoIngestArea(sipLicenseInMetsPartialNoLicense, "tgz", sipLicenseInMetsPartialNoLicense);
		ath.waitForJobToBeInErrorStatus(sipLicenseInMetsPartialNoLicense, C.WORKFLOW_STATUS_DIGIT_USER_ERROR);
		object1=ath.getObject(sipLicenseInMetsPartialNoLicense);
		assertEquals(ath.getJob(sipLicenseInMetsPartialNoLicense).getStatus(),"144");
	}

	@Test
	public void testDifferentMetsLicense() throws IOException, JDOMException, InterruptedException  {
		setUserPublicMets(true);
		ath.putSIPtoIngestArea(sipDifferentLicenseInMets, "tgz", sipDifferentLicenseInMets);

		ath.awaitObjectState(sipDifferentLicenseInMets,Object.ObjectStatus.ArchivedAndValidAndNotInWorkflow);
		object1=ath.getObject(sipDifferentLicenseInMets);
		assertEquals(C.LICENSEFLAG_PUBLIC_METS, object1.getLicense_flag());
	}
	
	@Test
	public void testNoLicenseNoPublication() throws IOException, JDOMException, InterruptedException {
		setUserPublicMets(true);
		ath.putSIPtoIngestArea(sipNoLicenseNoPublication, "tgz", sipNoLicenseNoPublication);
		ath.awaitObjectState(sipNoLicenseNoPublication,Object.ObjectStatus.ArchivedAndValidAndNotInWorkflow);
	
		object1=ath.getObject(sipNoLicenseNoPublication);
		assertEquals(object1.getLicense_flag(), C.LICENSEFLAG_UNDEFINED);
		assertTrue(object1.getPublished_flag()==C.PUBLISHEDFLAG_NO_PUBLICATION);
	}
	
	
	public void checkLicenseInMetadata(Object obj,MetsLicense license) throws IOException, JDOMException{

		SAXBuilder builder = XMLUtils.createValidatingSaxBuilder();
		if(license!=null){
			//testPIPMets
			File metsFile1 = ath.loadDefaultMetsFileFromPip(obj.getIdentifier());
			assertTrue(metsFile1.exists());
			Document metsDoc1 = builder.build(new FileReader(metsFile1));
			MetsParser mp = new MetsParser(metsDoc1);
			MetsLicense lic=mp.getLicensesForWholeMets().get(0);
			assertTrue(lic!=null);	
			assertEquals(lic, license);
		}
		//testEdm
		File edmFile1 = ath.loadFileFromPip(obj.getIdentifier(), "EDM.xml");
		assertTrue(edmFile1.exists());	
		Document edmDoc1 = builder.build(new FileReader(edmFile1));
		@SuppressWarnings("unchecked")
		List<Element> providetCho = edmDoc1.getRootElement().getChildren("ProvidedCHO", C.EDM_NS);
		int numberOfCho=providetCho.size();
		assertEquals("providetCho.size()="+providetCho.size(),providetCho.size(),4);
		for(Element pcho : providetCho) {
			assertTrue(license==null? pcho.getChild("rights", C.DC_NS)==null : pcho.getChild("rights", C.DC_NS).getValue().equals(license.getHref()));
		}
		
		////	testIndex
		String jsonString=metadataIndex.getIndexedMetadata(PORTAL_CI_TEST, obj.getIdentifier());
		JSONObject jsonObj = (new JSONObject(jsonString)).getJSONObject("hits");
		assertEquals(numberOfCho,jsonObj.getInt("total"));
		//jsonObj = jsonObj.getJSONArray("hits").getJSONObject(0);
		JSONArray jsonObjList = jsonObj.getJSONArray("hits");
		for(int i=0;i<jsonObjList.length();i++ ){
			jsonObj=jsonObjList.getJSONObject(i);
			jsonObj = jsonObj.getJSONObject("_source");
			jsonObj = jsonObj.getJSONObject("edm:aggregatedCHO");
			 try{
				 assertTrue(jsonObj.getJSONArray("dc:rights").get(0).toString().equals(license.getHref()));
			 }catch(JSONException e){
				 assertTrue(license==null);
	         }
		}
	}
	
}
