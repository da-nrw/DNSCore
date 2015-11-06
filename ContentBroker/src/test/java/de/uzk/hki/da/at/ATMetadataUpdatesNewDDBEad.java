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
		ath.waitForObjectToBeIndexed(metadataIndex,o.getIdentifier());
		
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
}
