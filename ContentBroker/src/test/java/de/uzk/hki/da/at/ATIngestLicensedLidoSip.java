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
import org.jdom.input.SAXBuilder;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.uzk.hki.da.metadata.LidoLicense;
import de.uzk.hki.da.metadata.LidoParser;
import de.uzk.hki.da.metadata.MetsLicense;
import de.uzk.hki.da.metadata.MetsParser;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.model.WorkArea;
import de.uzk.hki.da.utils.C;
import de.uzk.hki.da.utils.FolderUtils;
import de.uzk.hki.da.utils.Path;
import de.uzk.hki.da.utils.XMLUtils;

public class ATIngestLicensedLidoSip extends AcceptanceTest {
	
	private static final String sipLicenseInLido = "ATLidoSipLicenseInLido";
	private static final String sipLicenseInPremis = "ATLidoSipLicenseInPremis";
	private static final String sipLicenseInMetsAndPremis = "ATLidoSipLicenseInLidoAndPremis";
	private static final String sipNoLicenseNoPublication = "ATLidoSipNoLicenseNoPublication";
	private static final String sipNoLicensePublication = "ATLidoSipNoLicensePublication";
	
	private static final String sipLicenseInPremisMultipleAM = "ATLidoSipLicenseInPremisMultipleEmptyAM";
	private static final String sipNoLicenseMultipleAMError = "ATLidoSipNoLicenseMultipleAMError";
	private static final String sipLicenseInPremisMultipleAMError = "ATLidoSipLicenseInPremisMultipleAMError";
	
	private static final String sipDeltaLicense = "SipDeltaLicense";
	
	private static final LidoLicense LICENSE_PREMIS = new LidoLicense("https://creativecommons.org/licenses/by-sa/4.0/", "CC-BY-SA-Lizenz (v4.0)");
	private static final LidoLicense LICENSE_LIDO = new LidoLicense("http://creativecommons.org/licenses/by/3.0/de/",  "CC BY 3.0 DE");

	
	Path contractorsPipsPublic = Path.make(localNode.getWorkAreaRootPath(),WorkArea.PIPS, WorkArea.PUBLIC, testContractor.getUsername());
	private static Object object1;
	
	private  String PORTAL_CI_TEST =getTestIndex();
	
	@After
	public void tearDown() {
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
	public void testLicenseInLido() throws IOException, JDOMException {
		assertTrue("preservationSystem.getLicenseValidationFlag()==0", preservationSystem.getLicenseValidationFlag()!=C.PRESERVATIONSYS_LICENSE_VALIDATION_NO);
		ath.putSIPtoIngestArea(sipLicenseInLido, "tgz", sipLicenseInLido);
		ath.awaitObjectState(sipLicenseInLido,Object.ObjectStatus.ArchivedAndValidAndNotInWorkflow);
		ath.waitForDefinedPublishedState(sipLicenseInLido);
		ath.waitForObjectPublishedState(sipLicenseInLido, C.PUBLISHEDFLAG_PUBLIC);
		
		object1=ath.getObject(sipLicenseInLido);
		assertEquals(object1.getLicense_flag(), C.LICENSEFLAG_LIDO);
		
		checkLicenseInMetadata(object1,LICENSE_LIDO);
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
	public void testLicenseInPremisMultipleAM() throws IOException, JDOMException {
		ath.putSIPtoIngestArea(sipLicenseInPremisMultipleAM, "tgz", sipLicenseInPremisMultipleAM);
		ath.awaitObjectState(sipLicenseInPremisMultipleAM,Object.ObjectStatus.ArchivedAndValidAndNotInWorkflow);
		ath.waitForDefinedPublishedState(sipLicenseInPremisMultipleAM);
		ath.waitForObjectPublishedState(sipLicenseInPremisMultipleAM, C.PUBLISHEDFLAG_PUBLIC);
		
		object1=ath.getObject(sipLicenseInPremisMultipleAM);
		assertEquals(object1.getLicense_flag(), C.LICENSEFLAG_PREMIS);
		checkLicenseInMetadata(object1,LICENSE_PREMIS);
	}
	
	@Test
	public void testLicenseInLidoAndPremis() throws IOException, JDOMException, InterruptedException {
		ath.putSIPtoIngestArea(sipLicenseInMetsAndPremis, "tgz", sipLicenseInMetsAndPremis);
		ath.waitForJobToBeInErrorStatus(sipLicenseInMetsAndPremis, C.WORKFLOW_STATUS_DIGIT_USER_ERROR);

		assertEquals(ath.getJob(sipLicenseInMetsAndPremis).getStatus(),"144");
	}
	
	@Test
	public void testNoLicenseMultipleAMError() throws IOException, JDOMException, InterruptedException {
		ath.putSIPtoIngestArea(sipNoLicenseMultipleAMError, "tgz", sipNoLicenseMultipleAMError);
		ath.waitForJobToBeInErrorStatus(sipNoLicenseMultipleAMError, C.WORKFLOW_STATUS_DIGIT_USER_ERROR);

		assertEquals(ath.getJob(sipNoLicenseMultipleAMError).getStatus(),"144");
	}
	
	@Test
	public void testLicenseinPremisMultipleAMError() throws IOException, JDOMException, InterruptedException {
		ath.putSIPtoIngestArea(sipLicenseInPremisMultipleAMError, "tgz", sipLicenseInPremisMultipleAMError);
		ath.waitForJobToBeInErrorStatus(sipLicenseInPremisMultipleAMError, C.WORKFLOW_STATUS_DIGIT_USER_ERROR);

		assertEquals(ath.getJob(sipLicenseInPremisMultipleAMError).getStatus(),"144");
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
	public void testLicenseDelta() throws IOException, JDOMException, InterruptedException {
		//Ingest 1 sipLicenseInMets
		ath.putSIPtoIngestArea(sipLicenseInLido, "tgz", sipDeltaLicense);
		
		ath.awaitObjectState(sipDeltaLicense,Object.ObjectStatus.ArchivedAndValidAndNotInWorkflow);
		ath.waitForDefinedPublishedState(sipDeltaLicense);
		ath.waitForObjectPublishedState(sipDeltaLicense, C.PUBLISHEDFLAG_PUBLIC);
		
		object1=ath.getObject(sipDeltaLicense);
		assertEquals(object1.getLicense_flag(), C.LICENSEFLAG_LIDO);
		
		checkLicenseInMetadata(object1,LICENSE_LIDO);

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

	public void checkLicenseInMetadata(Object obj,LidoLicense license) throws IOException, JDOMException{

		SAXBuilder builder = XMLUtils.createValidatingSaxBuilder();
		//testPIPMets
		File lidoFile1 = ath.loadDefaultLidoFileFromPip(obj.getIdentifier());
		assertTrue(lidoFile1.exists());
		Document lidoDoc1 = builder.build(new FileReader(lidoFile1));
		LidoParser mp = new LidoParser(lidoDoc1);
		LidoLicense lic=mp.getLicenseForWholeLido();
		assertTrue(lic!=null);	
		assertEquals(lic, license);
		
		//testEdm
		File edmFile1 = ath.loadFileFromPip(obj.getIdentifier(), "EDM.xml");
		assertTrue(edmFile1.exists());	
		Document edmDoc1 = builder.build(new FileReader(edmFile1));
	
		@SuppressWarnings("unchecked")
		List<Element> providetCho = edmDoc1.getRootElement().getChildren("ProvidedCHO", C.EDM_NS);
		assertTrue(providetCho.size()==2);
		for(Element pcho : providetCho) {
			assertTrue(pcho.getChild("rights", C.DC_NS).getValue().equals(license.getHref()));
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
		assertEquals(2,jsonObj.getInt("total"));
		
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
