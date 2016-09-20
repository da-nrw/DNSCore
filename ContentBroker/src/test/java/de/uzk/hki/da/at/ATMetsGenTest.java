package de.uzk.hki.da.at;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
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

import de.uzk.hki.da.metadata.MetadataHelper;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.model.WorkArea;
import de.uzk.hki.da.utils.C;
import de.uzk.hki.da.utils.FolderUtils;
import de.uzk.hki.da.utils.Path;
import de.uzk.hki.da.utils.XMLUtils;

public class ATMetsGenTest extends AcceptanceTest{
	private static String origName = "MetsGenTest";
	private static Object object;
	private static Path contractorsPipsPublic;
	private static final File retrievalFolder = new File("/tmp/unpackedMetsMods");
	MetadataHelper mh = new MetadataHelper();
	
	@BeforeClass
	public static void setUp() throws IOException {
		ath.putSIPtoIngestArea(origName, "tgz", origName);
		ath.awaitObjectState(origName,Object.ObjectStatus.ArchivedAndValidAndNotInWorkflow);
		ath.waitForDefinedPublishedState(origName);
		object=ath.getObject(origName);
		ath.waitForObjectToBeIndexed(metadataIndex,object.getIdentifier());
		contractorsPipsPublic = Path.make(localNode.getWorkAreaRootPath(),WorkArea.PIPS, WorkArea.PUBLIC, C.TEST_USER_SHORT_NAME);
	}
	
	@AfterClass
	public static void tearDown() throws IOException{
		FolderUtils.deleteDirectorySafe(retrievalFolder);
		Path.makeFile("tmp",object.getIdentifier()+".pack_1.tar").delete(); // retrieved dip
	}
	
	@Test
	public void testLZA() throws Exception{
		ath.retrieveAIP(object,retrievalFolder,"1");
		System.out.println("object identifier: "+object.getIdentifier());		
		
		Path tmpObjectDirPath = Path.make(retrievalFolder.getAbsolutePath(), "data");	
		File[] tmpObjectSubDirs = new File (tmpObjectDirPath.toString()).listFiles();
		String bRep = "";
		
		for (int i=0; i<tmpObjectSubDirs.length; i++) {
			if(tmpObjectSubDirs[i].getName().contains("+b")) {
				bRep = tmpObjectSubDirs[i].getName();
			}
		}
		
		SAXBuilder builder = XMLUtils.createNonvalidatingSaxBuilder();
		String metsFileName = "mets.xml";
		Document doc = builder.build
				(new FileReader(Path.make(tmpObjectDirPath, bRep, metsFileName).toFile()));
		
		Boolean pic_258098_exists = false;
		Boolean pic_258099_exists = false;
		List<Element> fileElements = mh.getMetsFileElements(doc);
		for(Element e : fileElements) {
			assertTrue(mh.getMimetypeInMets(e).equals("image/png"));
			if(mh.getMetsHref(e).equals("258098.png")) {
				pic_258098_exists = true;
			} else if(mh.getMetsHref(e).equals("258099.png")) {
				pic_258099_exists = true;
			}
		}
		assertTrue(pic_258098_exists && pic_258099_exists);
	}
	
	@Test
	public void testPres() throws JDOMException, FileNotFoundException, IOException {
		
		assertEquals(C.CB_PACKAGETYPE_METS,object.getPackage_type());
		
		SAXBuilder builder = XMLUtils.createNonvalidatingSaxBuilder();
		Document pipMets = builder.build
			(new FileReader(
				Path.make(contractorsPipsPublic, 
					object.getIdentifier(), C.CB_PACKAGETYPE_METS+C.FILE_EXTENSION_XML).toFile()));
		List<Element> fileElements = mh.getMetsFileElements(pipMets);
		Boolean _3b91a3c29a50f62d23bd395e5fa3103c_exists = false;
		Boolean _a39342aa7817e8a8d3fa924b0a0b51fc_exists = false;
		for(Element e : fileElements) {
			assertTrue(mh.getMimetypeInMets(e).equals("image/jpeg"));
			assertTrue(mh.getMetsLoctype(e).equals("URL"));
			if(mh.getMetsHref(e)
					.equals("http://data.danrw.de/file/"+object.getIdentifier()+"/_3b91a3c29a50f62d23bd395e5fa3103c.jpg")) {
				_3b91a3c29a50f62d23bd395e5fa3103c_exists = true;
			} else if(mh.getMetsHref(e).equals("http://data.danrw.de/file/"+object.getIdentifier()+"/_a39342aa7817e8a8d3fa924b0a0b51fc.jpg")) {
				_a39342aa7817e8a8d3fa924b0a0b51fc_exists = true;
			}
		}
		assertTrue(_3b91a3c29a50f62d23bd395e5fa3103c_exists && _a39342aa7817e8a8d3fa924b0a0b51fc_exists);
	}
}
