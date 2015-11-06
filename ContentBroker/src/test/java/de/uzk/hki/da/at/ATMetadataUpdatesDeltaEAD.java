package de.uzk.hki.da.at;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
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
import de.uzk.hki.da.repository.RepositoryException;
import de.uzk.hki.da.utils.C;
import de.uzk.hki.da.utils.Path;
import de.uzk.hki.da.utils.XMLUtils;

/**
 * 
 * @author Polina Gubaidullina
 *
 */

public class ATMetadataUpdatesDeltaEAD extends AcceptanceTest{

	private static final String ORIG_NAME_ORIG = "ATMetadataUpdatesDeltaEAD";
	private static Path contractorsPipsPublic;
	private static Object o;
	private static final File retrievalFolder = new File("/tmp/unpackedDIP");
	private MetadataHelper mh = new MetadataHelper();
	private static final String EAD_XML = "EAD.xml";
	
	
	@BeforeClass
	public static void setUp() throws IOException, InterruptedException {
		
		contractorsPipsPublic = Path.make(localNode.getWorkAreaRootPath(),WorkArea.PIPS, WorkArea.PUBLIC, C.TEST_USER_SHORT_NAME);
		
		ath.putSIPtoIngestArea(ORIG_NAME_ORIG+"_orig","tgz", ORIG_NAME_ORIG);
		ath.awaitObjectState(ORIG_NAME_ORIG,Object.ObjectStatus.ArchivedAndValidAndNotInWorkflow);
		ath.waitForDefinedPublishedState(ORIG_NAME_ORIG);
		ath.putSIPtoIngestArea(ORIG_NAME_ORIG+"_delta", "tgz", ORIG_NAME_ORIG);
		ath.awaitObjectState(ORIG_NAME_ORIG,Object.ObjectStatus.InWorkflow);
		ath.awaitObjectState(ORIG_NAME_ORIG,Object.ObjectStatus.ArchivedAndValidAndNotInWorkflow);
		ath.waitForDefinedPublishedState(ORIG_NAME_ORIG);
		
		o=ath.getObject(ORIG_NAME_ORIG);
	}
	
	@AfterClass
	public static void tearDownAfterClass() throws IOException{
		FileUtils.deleteDirectory(retrievalFolder);
		Path.makeFile("tmp",o.getIdentifier()+".pack_2.tar").delete(); // retrieved dip
	}
	
	@Test
	public void testLZA() throws IOException, InterruptedException, RepositoryException, JDOMException{

		ath.retrieveAIP(o, retrievalFolder, "2");
		Object lzaObject = ath.getObject(o.getOrig_name());
		System.out.println("object identifier: "+lzaObject.getIdentifier());
		
		Path tmpObjectDirPath = Path.make(retrievalFolder.getAbsolutePath(), "data");	
		File[] tmpObjectSubDirs = new File (tmpObjectDirPath.toString()).listFiles();
		String bRep = "";
		
		for (int i=0; i<tmpObjectSubDirs.length; i++) {
			if(tmpObjectSubDirs[i].getName().contains("+b")) {
				bRep = tmpObjectSubDirs[i].getName();
			}
		}
		
		SAXBuilder builder = XMLUtils.createNonvalidatingSaxBuilder();
		FileReader fr1 = new FileReader(Path.make(tmpObjectDirPath, bRep, "mets_2_32044.xml").toFile());
		Document doc1 = builder.build(fr1);
		List<Element> metsFileElements1 = mh.getMetsFileElements(doc1);
		Element fileElement1 = metsFileElements1.get(0);
		assertTrue(mh.getMetsHref(fileElement1).equals("Picture1.tif"));
		assertTrue(mh.getMimetypeInMets(fileElement1).equals("image/tiff"));
		fr1.close();

		FileReader fr2 = new FileReader(Path.make(tmpObjectDirPath, bRep, "mets_2_32045.xml").toFile());
		Document doc2 = builder.build(fr2);
		List<Element> metsFileElements2 = mh.getMetsFileElements(doc2);
		Element fileElement2 = metsFileElements2.get(0);
		assertTrue(mh.getMetsHref(fileElement2).equals("Picture2.tif"));
		assertTrue(mh.getMimetypeInMets(fileElement2).equals("image/tiff"));
		fr2.close();
		
		FileReader fr3 = new FileReader(Path.make(tmpObjectDirPath, bRep, "mets_2_32046.xml").toFile());
		Document doc3 = builder.build(fr3);
		List<Element> metsFileElements3 = mh.getMetsFileElements(doc3);
		Element fileElement3 = metsFileElements3.get(0);
		assertTrue(mh.getMetsHref(fileElement3).equals("Picture3.tif"));
		assertTrue(mh.getMimetypeInMets(fileElement3).equals("image/tiff"));
		fr3.close();
		
		FileReader fr4 = new FileReader(Path.make(tmpObjectDirPath, bRep, "mets_2_32047.xml").toFile());
		Document doc4 = builder.build(fr4);
		List<Element> metsFileElements4 = mh.getMetsFileElements(doc4);
		Element fileElement4 = metsFileElements4.get(0);
		assertTrue(mh.getMetsHref(fileElement4).equals("Picture4.tif"));
		assertTrue(mh.getMimetypeInMets(fileElement4).equals("image/tiff"));
		fr4.close();
		
		FileReader fr5 = new FileReader(Path.make(tmpObjectDirPath, bRep, "mets_2_32048.xml").toFile());
		Document doc5 = builder.build(fr5);
		List<Element> metsFileElements5 = mh.getMetsFileElements(doc5);
		Element fileElement5 = metsFileElements5.get(0);
		assertTrue(mh.getMetsHref(fileElement5).equals("Picture5.tif"));
		assertTrue(mh.getMimetypeInMets(fileElement5).equals("image/tiff"));
		fr4.close();
	}
	
	@Test
	public void testPres() throws FileNotFoundException, JDOMException, IOException {
		
		SAXBuilder builder = XMLUtils.createNonvalidatingSaxBuilder();
		FileReader fr1 = new FileReader(Path.make(contractorsPipsPublic, o.getIdentifier(), "mets_2_32044.xml").toFile());
		Document doc1 = builder.build(fr1);
		List<Element> metsFileElements1 = mh.getMetsFileElements(doc1);
		Element fileElement1 = metsFileElements1.get(0);
		String metsURL1 = mh.getMetsHref(fileElement1);
		assertTrue(metsURL1.equals("http://data.danrw.de/file/"+o.getIdentifier()+"/_c3836acf068a9b227834e0adda226ac2.jpg"));
		assertEquals("URL", mh.getMetsLoctype(fileElement1));
		assertEquals(C.MIMETYPE_IMAGE_JPEG, mh.getMimetypeInMets(fileElement1));
		fr1.close();
		
		FileReader fr2 = new FileReader(Path.make(contractorsPipsPublic, o.getIdentifier(), "mets_2_32045.xml").toFile());
		Document doc2 = builder.build(fr2);
		List<Element> metsFileElements2 = mh.getMetsFileElements(doc2);
		Element fileElement2 = metsFileElements2.get(0);
		String metsURL2 = mh.getMetsHref(fileElement2);
		assertTrue(metsURL2.equals("http://data.danrw.de/file/"+o.getIdentifier()+"/_c8079103e5eecf45d2978a396e1839a9.jpg"));
		assertEquals("URL", mh.getMetsLoctype(fileElement2));
		assertEquals(C.MIMETYPE_IMAGE_JPEG, mh.getMimetypeInMets(fileElement2));
		fr2.close();
		
		FileReader fr3 = new FileReader(Path.make(contractorsPipsPublic, o.getIdentifier(), "mets_2_32046.xml").toFile());
		Document doc3 = builder.build(fr3);
		List<Element> metsFileElements3 = mh.getMetsFileElements(doc3);
		Element fileElement3 = metsFileElements3.get(0);
		String metsURL3 = mh.getMetsHref(fileElement3);
		assertTrue(metsURL3.equals("http://data.danrw.de/file/"+o.getIdentifier()+"/_fa55eb875c9ad7ceedb0f61868daf0e4.jpg"));
		assertEquals("URL", mh.getMetsLoctype(fileElement3));
		assertEquals(C.MIMETYPE_IMAGE_JPEG, mh.getMimetypeInMets(fileElement3));
		fr3.close();
		
		FileReader fr4 = new FileReader(Path.make(contractorsPipsPublic, o.getIdentifier(), "mets_2_32047.xml").toFile());
		Document doc4 = builder.build(fr4);
		List<Element> metsFileElements4 = mh.getMetsFileElements(doc4);
		Element fileElement4 = metsFileElements4.get(0);
		String metsURL4 = mh.getMetsHref(fileElement4);
		assertTrue(metsURL4.equals("http://data.danrw.de/file/"+o.getIdentifier()+"/_a66c85bf5ddf7683f7999cb4a20bfd61.jpg"));
		assertEquals("URL", mh.getMetsLoctype(fileElement4));
		assertEquals(C.MIMETYPE_IMAGE_JPEG, mh.getMimetypeInMets(fileElement4));
		fr4.close();
		
		FileReader fr5 = new FileReader(Path.make(contractorsPipsPublic, o.getIdentifier(), "mets_2_32048.xml").toFile());
		Document doc5 = builder.build(fr5);
		List<Element> metsFileElements5 = mh.getMetsFileElements(doc5);
		Element fileElement5 = metsFileElements5.get(0);
		String metsURL5 = mh.getMetsHref(fileElement5);
		assertTrue(metsURL5.equals("http://data.danrw.de/file/"+o.getIdentifier()+"/_12b1c1ce98f2726c6d9c91d0e589979d.jpg"));
		assertEquals("URL", mh.getMetsLoctype(fileElement5));
		assertEquals(C.MIMETYPE_IMAGE_JPEG, mh.getMimetypeInMets(fileElement5));
		fr5.close();
		
		
		FileReader fr = new FileReader(Path.make(contractorsPipsPublic, o.getIdentifier(), EAD_XML).toFile());
		SAXBuilder eadSaxBuilder = XMLUtils.createNonvalidatingSaxBuilder();
		Document eadDoc = eadSaxBuilder.build(fr);
		EadParser ep = new EadParser(eadDoc);
		
		List<String> metsReferences = ep.getReferences();
		assertTrue(metsReferences.size()==5);
		for(String metsRef : metsReferences) {
			if(metsRef.contains("mets_2_32044.xml")) {
				assertTrue(metsRef.equals("http://data.danrw.de/file/"+ o.getIdentifier() +"/mets_2_32044.xml"));
			} else if(metsRef.contains("mets_2_32045.xml")) {
				assertTrue(metsRef.equals("http://data.danrw.de/file/"+ o.getIdentifier() +"/mets_2_32045.xml"));
			} else if(metsRef.contains("mets_2_32046.xml")) {
				assertTrue(metsRef.equals("http://data.danrw.de/file/"+ o.getIdentifier() +"/mets_2_32046.xml"));
			} else if(metsRef.contains("mets_2_32047.xml")) {
				assertTrue(metsRef.equals("http://data.danrw.de/file/"+ o.getIdentifier() +"/mets_2_32047.xml"));
			} else {
				assertTrue(metsRef.equals("http://data.danrw.de/file/"+ o.getIdentifier() +"/mets_2_32048.xml"));
			}
		}
		fr.close();
	}
}
