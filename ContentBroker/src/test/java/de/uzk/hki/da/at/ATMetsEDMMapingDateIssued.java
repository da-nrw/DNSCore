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
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.uzk.hki.da.metadata.MetsParser;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.model.WorkArea;
import de.uzk.hki.da.utils.C;
import de.uzk.hki.da.utils.FolderUtils;
import de.uzk.hki.da.utils.Path;
import de.uzk.hki.da.utils.XMLUtils;

public class ATMetsEDMMapingDateIssued extends AcceptanceTest {
	
	private static final String sip = "ATMetsEDMMapingDateIssued";
	
	Path contractorsPipsPublic = Path.make(localNode.getWorkAreaRootPath(),WorkArea.PIPS, WorkArea.PUBLIC, testContractor.getUsername());
	private static Object object1;
	
	private  String PORTAL_CI_TEST =getTestIndex();
	
	
	@BeforeClass
	public static void setUp() throws IOException, InterruptedException {
		
		ath.putSIPtoIngestArea(sip, "tgz", sip);
		ath.awaitObjectState(sip,Object.ObjectStatus.ArchivedAndValidAndNotInWorkflow);
		ath.waitForDefinedPublishedState(sip);
		ath.waitForObjectPublishedState(sip, C.PUBLISHEDFLAG_PUBLIC);
		
		object1=ath.getObject(sip);
	}
	
	@AfterClass
	public static void tearDownAfterClass() throws IOException{
	}

	@Test
	public void testPIPEdm1() throws IOException, JDOMException {

		SAXBuilder builder = XMLUtils.createValidatingSaxBuilder();
		
		File edmFile1 = ath.loadFileFromPip(object1.getIdentifier(), "EDM.xml");

		for(int i=0;i<30 && !edmFile1.exists();i++){
			FolderUtils.waitToCompleteNFSAwareFileOperation();
			System.out.println("Target("+edmFile1+") file is not created yet, wait: "+i);
		}
		assertTrue(edmFile1.exists());	
		Document edmDoc1 = builder.build
				(new FileReader(edmFile1));
	
		@SuppressWarnings("unchecked")
		List<Element> providetCho = edmDoc1.getRootElement().getChildren("ProvidedCHO", C.EDM_NS);
		for(Element pcho : providetCho) {
			assertTrue(pcho.getChild("hasType", C.EDM_NS).getValue().equals("is root element"));
			assertTrue(pcho.getChild("title", C.DC_NS).getValue().equals("EDM Mapping-Test Brief an Mutter, Möhn, Schwester, Robert Etmund, Sophie Trinchen [Katharina Bauchmüller] etc."));
			
			@SuppressWarnings("unchecked")
			List<Element> extent = pcho.getChildren("extent", C.DCTERMS_NS);
			assertEquals(extent.size(),1);
			assertEquals(extent.get(0).getValue(),"1 Plakat : sw ; 32 x 17 cm");
			
			@SuppressWarnings("unchecked")
			List<Element> dateIssued = pcho.getChildren("issued", C.DCTERMS_NS);
			assertTrue(dateIssued.size()==3);
			boolean dateIssued1 = false;
			boolean dateIssued2 = false;
			boolean dateIssued3 = false;
			for(Element cont : dateIssued) {
				if(cont.getValue().equals("1819")) {
					dateIssued1 = true;
				} else if(cont.getValue().equals("1984")) {
					dateIssued2 = true;
				} else if(cont.getValue().equals("1988")) {
					dateIssued3 = true;
				}
			}
			assertTrue(dateIssued1&&dateIssued2&&dateIssued3);	
		}
////	testIndex
		String jsonString=metadataIndex.getIndexedMetadata(PORTAL_CI_TEST, object1.getIdentifier());
		JSONObject jsonObj = (new JSONObject(jsonString)).getJSONObject("hits");
		assertEquals(1,jsonObj.getInt("total"));
		
		jsonObj = jsonObj.getJSONArray("hits").getJSONObject(0);
		assertTrue(jsonObj.getString("_id").contains("Jh-Dussel"));
		jsonObj = jsonObj.getJSONObject("_source");
		jsonObj = jsonObj.getJSONObject("edm:aggregatedCHO");
		
		JSONArray jsondateIssuedArray=jsonObj.getJSONArray("dcterms:issued");
		assertEquals(jsondateIssuedArray.length(),3);
		boolean dateIssued1 = false;
		boolean dateIssued2 = false;
		boolean dateIssued3 = false;
		for(int i =0;i<jsondateIssuedArray.length();i++) {
			if(jsondateIssuedArray.getString(i).equals("1819")) {
				dateIssued1 = true;
			} else if(jsondateIssuedArray.getString(i).equals("1984")) {
				dateIssued2 = true;
			} else if(jsondateIssuedArray.getString(i).equals("1988")) {
				dateIssued3 = true;
			}
		}
		assertTrue(dateIssued1&&dateIssued2&&dateIssued3);	
		
		JSONArray jsonDateCreatedArray=jsonObj.getJSONArray("dcterms:created");
		assertEquals(jsonDateCreatedArray.length(),1);
		assertEquals(jsonDateCreatedArray.getString(0),"2012");
		
		JSONArray jsonExtentArray=jsonObj.getJSONArray("dcterms:extent");
		assertEquals(jsonExtentArray.length(),1);
		assertEquals(jsonExtentArray.getString(0),"1 Plakat : sw ; 32 x 17 cm");
	}
}
