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

public class ATMetadataTitleAndRoleTerm extends AcceptanceTest {
	
	private static final String sip = "ATMetadataRoleTermNonSortTitle";
	private static final String urn2 = "urn+nbn+de+danrw+de2189-89532c28-d082-4c38-8783-21b9019225988";
	private static final String urn3 = "urn+nbn+de+danrw+de2189-0c6ab310-f2f6-4f66-80e2-a138bd4db6938";
	
	Path contractorsPipsPublic = Path.make(localNode.getWorkAreaRootPath(),WorkArea.PIPS, WorkArea.PUBLIC, C.TEST_USER_SHORT_NAME);
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

		SAXBuilder builder = XMLUtils.createNonvalidatingSaxBuilder();
		File edmFile1 = Path.make(contractorsPipsPublic, object1.getIdentifier(), "EDM.xml").toFile();
		Document edmDoc1 = builder.build
				(new FileReader(edmFile1));
		for(int i=0;i<30 && !edmFile1.exists();i++){
			FolderUtils.waitToCompleteNFSAwareFileOperation();
			System.out.println("Target("+edmFile1+") file is not created yet, wait: "+i);
		}
		assertTrue(edmFile1.exists());
		
//		File edmFile2 = Path.make(contractorsPipsPublic, object2.getIdentifier(), "EDM.xml").toFile();
//		Document edmDoc2 = builder.build
//				(new FileReader(edmFile2));
//		
//		File edmFile3 = Path.make(contractorsPipsPublic, object3.getIdentifier(), "EDM.xml").toFile();
//		Document edmDoc3 = builder.build
//				(new FileReader(edmFile3));		
	
		assertTrue(edmFile1.exists());
		@SuppressWarnings("unchecked")
		List<Element> providetCho = edmDoc1.getRootElement().getChildren("ProvidedCHO", C.EDM_NS);
		for(Element pcho : providetCho) {
			assertTrue(pcho.getChild("hasType", C.EDM_NS).getValue().equals("is root element"));
			assertTrue(pcho.getChild("title", C.DC_NS).getValue().equals("Brief an Mutter, Möhn, Schwester, Robert Etmund, Sophie Trinchen [Katharina Bauchmüller] etc."));
			
			@SuppressWarnings("unchecked")
			List<Element> creator = pcho.getChildren("creator", C.DC_NS);
			assertEquals(creator.size(),1);
			assertEquals(creator.get(0).getValue(),"Verfasser: Bauchmüller, Heinrich");
			
			@SuppressWarnings("unchecked")
			List<Element> contributor = pcho.getChildren("contributor", C.DC_NS);
			assertTrue(contributor.size()==3);
			boolean contributor1 = false;
			boolean contributor2 = false;
			boolean contributor3 = false;
			for(Element cont : contributor) {
				if(cont.getValue().equals("Adressatin: Bauchmüller, Katharina")) {
					contributor1 = true;
				} else if(cont.getValue().equals("Adressat: Bauchmüller, Robert")) {
					contributor2 = true;
				} else if(cont.getValue().equals("Urheber: Preußen / Ministerium der Geistlichen, Unterrichts- und Medizinalangelegenheiten")) {
					contributor3 = true;
				}
			}
			assertTrue(contributor1&&contributor2&&contributor3);	
		}
////	testIndex
		String jsonString=metadataIndex.getIndexedMetadata(PORTAL_CI_TEST, object1.getIdentifier());
		JSONObject jsonObj = (new JSONObject(jsonString)).getJSONObject("hits");
		assertEquals(1,jsonObj.getInt("total"));
		
		jsonObj = jsonObj.getJSONArray("hits").getJSONObject(0);
		assertTrue(jsonObj.getString("_id").contains("Jh-Dussel"));
		jsonObj = jsonObj.getJSONObject("_source");
		jsonObj = jsonObj.getJSONObject("edm:aggregatedCHO");
		
		JSONArray jsonContributorArray=jsonObj.getJSONArray("dc:contributor");
		assertEquals(jsonContributorArray.length(),3);
		boolean contributor1 = false;
		boolean contributor2 = false;
		boolean contributor3 = false;
		for(int i =0;i<jsonContributorArray.length();i++) {
			if(jsonContributorArray.getString(i).equals("Adressatin: Bauchmüller, Katharina")) {
				contributor1 = true;
			} else if(jsonContributorArray.getString(i).equals("Adressat: Bauchmüller, Robert")) {
				contributor2 = true;
			} else if(jsonContributorArray.getString(i).equals("Urheber: Preußen / Ministerium der Geistlichen, Unterrichts- und Medizinalangelegenheiten")) {
				contributor3 = true;
			}
		}
		assertTrue(contributor1&&contributor2&&contributor3);	
		
		JSONArray jsonCreatorArray=jsonObj.getJSONArray("dc:creator");
		assertEquals(jsonCreatorArray.length(),1);
		assertEquals(jsonCreatorArray.getString(0),"Verfasser: Bauchmüller, Heinrich");
		
		
		JSONArray jsonTitelArray=jsonObj.getJSONArray("dc:title");
		assertEquals(jsonTitelArray.length(),1);
		assertEquals(jsonTitelArray.getString(0),"Brief an Mutter, Möhn, Schwester, Robert Etmund, Sophie Trinchen [Katharina Bauchmüller] etc.");


	}
}
