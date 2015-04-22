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
import org.junit.BeforeClass;
import org.junit.Test;

import de.uzk.hki.da.core.C;
import de.uzk.hki.da.metadata.MetadataHelper;
import de.uzk.hki.da.metadata.XMLUtils;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.util.Path;

public class ATUseCaseIngestArchivDuisburg extends AcceptanceTest{
	private static final String URL = "URL";
	private static Path contractorsPipsPublic;
	private static String origName = "Archiv_Duisburg_mini";
	private static Object object;
	private static final String EAD_XML = "EAD.xml";
	private MetadataHelper mh = new MetadataHelper();
	
	@BeforeClass
	public static void setUp() throws IOException, InterruptedException {
		ath.putPackageToIngestArea(origName, "tgz", origName);
		ath.awaitObjectState(origName,Object.ObjectStatus.ArchivedAndValid);
		ath.waitForObjectToBePublished(origName);
		object=ath.fetchObjectFromDB(origName);
		
		contractorsPipsPublic = Path.make(localNode.getWorkAreaRootPath(),C.WA_PIPS, C.WA_PUBLIC, C.TEST_USER_SHORT_NAME);
	}
	
	
	@Test
	public void testFileIdGenInPres() throws FileNotFoundException, JDOMException, IOException {
		
		SAXBuilder builder = XMLUtils.createNonvalidatingSaxBuilder();
		Document doc = builder.build
				(new FileReader(Path.make(contractorsPipsPublic, object.getIdentifier(), "1175", "mets_1175.xml").toFile()));
		List<Element> metsFileElements = mh.getMetsFileElements(doc);
		
		Element fileElement = metsFileElements.get(0);
		
		String metsURL = mh.getMetsHref(fileElement);
		assertTrue(metsURL.startsWith("http://data.danrw.de/file/"+object.getIdentifier()) && metsURL.endsWith(".jpg"));
		assertEquals(URL, mh.getMetsLoctype(fileElement));
		assertEquals(C.MIMETYPE_IMAGE_JPEG, mh.getMetsMimetype(fileElement));
		
		SAXBuilder eadSaxBuilder = XMLUtils.createNonvalidatingSaxBuilder();
		Document eadDoc = eadSaxBuilder.build(new FileReader(Path.make(contractorsPipsPublic, object.getIdentifier(), EAD_XML).toFile()));

		List<String> metsReferences = mh.getMetsRefsInEad(eadDoc);
		assertTrue(metsReferences.size()==2);
		for(String metsRef : metsReferences) {
			if(metsRef.contains("mets_1175.xml")) {
				assertTrue(metsRef.equals("http://data.danrw.de/file/"+ object.getIdentifier() +"/_1175-mets_1175.xml"));
			} else {
				assertTrue(metsRef.equals("http://data.danrw.de/file/"+ object.getIdentifier() +"/_1176-mets_1176.xml"));
			}
		}
	}
	
	@Test
	public void testPipFileList() {
		assertTrue(Path.makeFile(contractorsPipsPublic, object.getIdentifier(), EAD_XML).exists());
		assertTrue(!Path.makeFile(contractorsPipsPublic, object.getIdentifier(), "EAD_FB_Standesamt.xml").exists());
	}
}
