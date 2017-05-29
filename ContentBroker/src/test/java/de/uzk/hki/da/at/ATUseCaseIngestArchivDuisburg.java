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

import de.uzk.hki.da.metadata.EadParser;
import de.uzk.hki.da.metadata.MetadataHelper;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.model.WorkArea;
import de.uzk.hki.da.utils.C;
import de.uzk.hki.da.utils.Path;
import de.uzk.hki.da.utils.XMLUtils;

public class ATUseCaseIngestArchivDuisburg extends AcceptanceTest{
	private static final String URL = "URL";
	private static String origName = "Archiv_Duisburg_mini";
	private static Object object;
	private static final String EAD_XML = "EAD.xml";
	private MetadataHelper mh = new MetadataHelper();
	
	@BeforeClass
	public static void setUp() throws IOException, InterruptedException {
		ath.putSIPtoIngestArea(origName, "tgz", origName);
		ath.awaitObjectState(origName,Object.ObjectStatus.ArchivedAndValidAndNotInWorkflow);
		ath.waitForDefinedPublishedState(origName);
		object=ath.getObject(origName);
	}
	
	
	@Test
	public void testFileIdGenInPres() throws FileNotFoundException, JDOMException, IOException {
		
		SAXBuilder builder = XMLUtils.createNonvalidatingSaxBuilder();
		Document doc = builder.build(new FileReader(ath.loadFileFromPip(object.getIdentifier(), "1175/mets_1175.xml")));
		List<Element> metsFileElements = mh.getMetsFileElements(doc);
		
		Element fileElement = metsFileElements.get(0);
		
		String metsURL = mh.getMetsHref(fileElement);
		assertTrue(metsURL.startsWith(preservationSystem.getUrisFile()+"/"+object.getIdentifier()) && metsURL.endsWith(".jpg"));
		assertEquals(URL, mh.getMetsLoctype(fileElement));
		assertEquals(C.MIMETYPE_IMAGE_JPEG, mh.getMimetypeInMets(fileElement));
		
		SAXBuilder eadSaxBuilder = XMLUtils.createNonvalidatingSaxBuilder();
		Document eadDoc = eadSaxBuilder.build(new FileReader(ath.loadFileFromPip(object.getIdentifier(), EAD_XML)));
		EadParser ep = new EadParser(eadDoc);
		
		List<String> metsReferences = ep.getReferences();
		assertTrue(metsReferences.size()==2);
		for(String metsRef : metsReferences) {
			if(metsRef.contains("mets_1175.xml")) {
				assertTrue(metsRef.equals(preservationSystem.getUrisFile()+"/"+ object.getIdentifier() +"/_1175-mets_1175.xml"));
			} else {
				assertTrue(metsRef.equals(preservationSystem.getUrisFile()+"/"+ object.getIdentifier() +"/_1176-mets_1176.xml"));
			}
		}
	}
	
	@Test
	public void testPipFileList() throws IOException {
		assertTrue(EAD_XML+" not exists",ath.loadFileFromPip(object.getIdentifier(), EAD_XML).exists());
		assertTrue("EAD_FB_Standesamt.xml"+" exists",!ath.loadFileFromPip(object.getIdentifier(),"EAD_FB_Standesamt.xml").exists());
	}
}
