package de.uzk.hki.da.at;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import de.uzk.hki.da.metadata.EadParser;
import de.uzk.hki.da.metadata.MetadataHelper;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.model.WorkArea;
import de.uzk.hki.da.utils.C;
import de.uzk.hki.da.utils.Path;
import de.uzk.hki.da.utils.XMLUtils;

public class ATMetadataUpdatesNewDDBEad extends AcceptanceTest{
	
	private static final String origName = "NewDDBEad";
	private static Object o;
	private static Path contractorsPipsPublic;
	private MetadataHelper mh = new MetadataHelper();
	private static final String URL = "URL";
	private static final String EAD_XML = "EAD.xml";
	
	@BeforeClass
	public static void setUp() throws IOException, InterruptedException {
		ath.putSIPtoIngestArea(origName, "tgz", origName);
		ath.awaitObjectState(origName,Object.ObjectStatus.ArchivedAndValidAndNotInWorkflow);
		ath.waitForDefinedPublishedState(origName);
		o=ath.getObject(origName);
		ath.waitForObjectToBeIndexed(metadataIndex,getTestIndex(),o.getIdentifier());
		
		contractorsPipsPublic = Path.make(localNode.getWorkAreaRootPath(),WorkArea.PIPS, WorkArea.PUBLIC, C.TEST_USER_SHORT_NAME);
	}
	
	@AfterClass
	public static void tearDownAfterClass() throws IOException{
	}
	
	@Test
	public void testPres() throws FileNotFoundException, JDOMException, IOException {
		
		FileReader frMets = new FileReader(Path.make(contractorsPipsPublic, o.getIdentifier(), "mets_1_280920.xml").toFile());
		SAXBuilder builder = XMLUtils.createNonvalidatingSaxBuilder();
		Document doc = builder.build(frMets);
		List<Element> metsFileElements = mh.getMetsFileElements(doc);
		Element fileElement = metsFileElements.get(0);
		String metsURL = mh.getMetsHref(fileElement);
		assertTrue(metsURL.startsWith("http://data.danrw.de/file/"+o.getIdentifier()) && metsURL.endsWith(".jpg"));
		assertEquals(URL, mh.getMetsLoctype(fileElement));
		assertEquals(C.MIMETYPE_IMAGE_JPEG, mh.getMimetypeInMets(fileElement));
		frMets.close();
		
		FileReader frEad = new FileReader(Path.make(contractorsPipsPublic, o.getIdentifier(), EAD_XML).toFile());
		SAXBuilder eadSaxBuilder = XMLUtils.createNonvalidatingSaxBuilder();
		Document eadDoc = eadSaxBuilder.build(frEad);
		EadParser ep = new EadParser(eadDoc);
		
		List<String> metsReferences = ep.getReferences();
		assertTrue(metsReferences.size()==2);
		boolean mets1refExists = false;
		boolean mets2refExists = false;
		for(String metsRef : metsReferences) {
			if(metsRef.contains("mets_1_280920.xml")) {
				mets1refExists = true;
				assertTrue(metsRef.equals("http://data.danrw.de/file/"+ o.getIdentifier() +"/mets_1_280920.xml"));
			} else if(metsRef.contains("mets_2_32042.xml")) {
				mets2refExists = true;
				assertTrue(metsRef.equals("http://data.danrw.de/file/"+ o.getIdentifier() +"/mets_2_32042.xml"));
			}
		}
		assertTrue(mets1refExists&&mets2refExists);
	}
	
	@Test
	public void testEdmAndIndex() throws FileNotFoundException, JDOMException, IOException {
		
		FileReader frEdm = new FileReader(Path.make(contractorsPipsPublic, o.getIdentifier(), "EDM.xml").toFile());
		SAXBuilder builder = XMLUtils.createNonvalidatingSaxBuilder();
		Document doc = builder.build(frEdm);
		@SuppressWarnings("unchecked")
		List<Element> providetCho = doc.getRootElement().getChildren("ProvidedCHO", C.EDM_NS);
		String firstID = "";
		String secondID = "";
		for(Element pcho : providetCho) {
			Element title = pcho.getChild("title", C.DC_NS);
			if(title!=null) {
				if(pcho.getChild("title", C.DC_NS).getValue().equals("Titel 1. Ebene")) {
					assertTrue(pcho.getChild("date", C.DC_NS).getValue().equals("2000-01-01/2005-12-31"));
					firstID = pcho.getAttributeValue("about", C.RDF_NS);
					assertTrue(pcho.getChildren("hasPart", C.DCTERMS_NS).size()==1);
				} else if(pcho.getChild("title", C.DC_NS).getValue().equals("Titel 2. Ebene")) {
					assertTrue(pcho.getChild("date", C.DC_NS).getValue().equals("2000-01-01/2005-12-31"));
					secondID = pcho.getAttributeValue("about", C.RDF_NS);
				}
			}
		}
		
		int referencedImages = 0;
		@SuppressWarnings("unchecked")
		List<Element> aggregationElements = doc.getRootElement().getChildren("Aggregation", C.ORE_NS);
		boolean mets1Ref1 = false;
		boolean mets1Ref2 = false;
		boolean mets1Ref3 = false;
		for(Element a : aggregationElements) {
			if(a.getAttributeValue("about", C.RDF_NS).replace("aggregation", "cho").equals(firstID)) {
				assertTrue(a.getChild("isShownBy", C.EDM_NS).getAttributeValue("resource", C.RDF_NS).endsWith(".jpg"));
				@SuppressWarnings("unchecked")
				List<Element> hasViewList = a.getChildren("hasView", C.EDM_NS);
				assertTrue(hasViewList.size()==3);
				referencedImages = referencedImages+3;
				for(Element e : hasViewList) {
					String ref = e.getAttributeValue("resource", C.RDF_NS);
					if(ref.endsWith("_05c3fc64901b048dded574aa3accf104.jpg")) {
						mets1Ref1 = true;
					} else if(ref.endsWith("_8f911ff6c422c2bf8564d98228b364d5.jpg")) {
						mets1Ref2 = true;
					} else if(ref.endsWith("_c5b1707b33c7191cd87b6f5fba37c0b8.jpg")) {
						mets1Ref3 = true;
					}
				}
			} else if(a.getAttributeValue("about", C.RDF_NS).replace("aggregation", "cho").equals(secondID)) {
				assertTrue(a.getChild("isShownBy", C.EDM_NS).getAttributeValue("resource", C.RDF_NS).endsWith("_4c3932a266662288a30c0f078f973837.jpg"));
				referencedImages++;
			} 
		}
		assertTrue(referencedImages==4);
		assertTrue(mets1Ref1 && mets1Ref2 && mets1Ref3);
				
//			testIndex
		String cho = "/cho/";
		String ID = firstID.substring(firstID.lastIndexOf(cho)+cho.length());
		assertTrue(metadataIndex.getIndexedMetadata(getTestIndex(), ID).contains("\"dc:date\":[\"2000-01-01/2005-12-31\"]"));
		
		frEdm.close();
	}
}
