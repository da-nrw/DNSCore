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

import de.uzk.hki.da.core.C;
import de.uzk.hki.da.metadata.MetadataHelper;
import de.uzk.hki.da.metadata.XMLUtils;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.util.Path;

/**
 * 
 * @author Polina Gubaidullina
 *
 */

public class ATMetadataUpdatesEAD extends AcceptanceTest{
	
	private static final String URL = "URL";
	private static Path contractorsPipsPublic;
	private static String origName = "ATMetadataUpdatesEAD";
	private static Object o;
	private static final String EAD_XML = "EAD.xml";
	private static final File retrievalFolder = new File("/tmp/unpackedDIP");
	private MetadataHelper mh = new MetadataHelper();
	
	@BeforeClass
	public static void setUp() throws IOException {
		ath.putSIPtoIngestArea(origName, "tgz", origName);
		ath.awaitObjectState(origName,Object.ObjectStatus.ArchivedAndValidAndNotInWorkflow);
		ath.waitForDefinedPublishedState(origName);
		o=ath.getObject(origName);
		ath.waitForObjectToBeIndexed(metadataIndex,o.getIdentifier());
		
		contractorsPipsPublic = Path.make(localNode.getWorkAreaRootPath(),C.WA_PIPS, C.WA_PUBLIC, C.TEST_USER_SHORT_NAME);
	}
	
	@AfterClass
	public static  void tearDown() throws IOException{
		FileUtils.deleteDirectory(retrievalFolder);
		Path.makeFile("tmp",o.getIdentifier()+".pack_1.tar").delete(); // retrieved dip
	}
	
	@Test
	public void testLZA() throws FileNotFoundException, JDOMException, IOException {
		
		ath.retrieveAIP(o, retrievalFolder, "1");
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
		Document doc1 = builder.build
				(new FileReader(Path.make(tmpObjectDirPath, bRep, "mets 2_32044.xml").toFile()));
		List<Element> metsFileElements1 = mh.getMetsFileElements(doc1);
		Element fileElement1 = metsFileElements1.get(0);
		assertTrue(mh.getMetsHref(fileElement1).equals("Picture1.tif"));
		assertTrue(mh.getMimetypeInMets(fileElement1).equals("image/tiff"));
		System.out.println("Loctype: "+mh.getMetsHref(fileElement1));

		Document doc2 = builder.build
				(new FileReader(Path.make(tmpObjectDirPath, bRep, "mets_2_32045.xml").toFile()));
		List<Element> metsFileElements2 = mh.getMetsFileElements(doc2);
		Element fileElement2 = metsFileElements2.get(0);
		assertTrue(mh.getMetsHref(fileElement2).equals("Picture2.tif"));
		assertTrue(mh.getMimetypeInMets(fileElement2).equals("image/tiff"));
		
		Document doc3 = builder.build
				(new FileReader(Path.make(tmpObjectDirPath, bRep, "mets_2_32046.xml").toFile()));
		List<Element> metsFileElements3 = mh.getMetsFileElements(doc3);
		Element fileElement3 = metsFileElements3.get(0);
		assertTrue(mh.getMetsHref(fileElement3).equals("Picture3.tif"));
		assertTrue(mh.getMimetypeInMets(fileElement3).equals("image/tiff"));
		
		Document doc4 = builder.build
				(new FileReader(Path.make(tmpObjectDirPath, bRep, "mets_2_32047.xml").toFile()));
		List<Element> metsFileElements4 = mh.getMetsFileElements(doc4);
		Element fileElement4 = metsFileElements4.get(0);
		assertTrue(mh.getMetsHref(fileElement4).equals("Picture4.tif"));
		assertTrue(mh.getMimetypeInMets(fileElement4).equals("image/tiff"));
		
		Document doc5 = builder.build
				(new FileReader(Path.make(tmpObjectDirPath, bRep, "mets_2_32048.xml").toFile()));
		List<Element> metsFileElements5 = mh.getMetsFileElements(doc5);
		Element fileElement5 = metsFileElements5.get(0);
		assertTrue(mh.getMetsHref(fileElement5).equals("Picture5.tif"));
		assertTrue(mh.getMimetypeInMets(fileElement5).equals("image/tiff"));
		
	}
	
	
	@Test
	public void testPres() throws FileNotFoundException, JDOMException, IOException {
		
		FileReader frMets = new FileReader(Path.make(contractorsPipsPublic, o.getIdentifier(), "mets_2_32044.xml").toFile());
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

		List<String> metsReferences = mh.getMetsRefsInEad(eadDoc);
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
	}
	
	@Test
	public void testEdmAndIndex() throws FileNotFoundException, JDOMException, IOException {
		
		FileReader frEdm = new FileReader(Path.make(contractorsPipsPublic, o.getIdentifier(), "EDM.xml").toFile());
		SAXBuilder builder = XMLUtils.createNonvalidatingSaxBuilder();
		Document doc = builder.build(frEdm);
		@SuppressWarnings("unchecked")
		List<Element> providetCho = doc.getRootElement().getChildren("ProvidedCHO", C.EDM_NS);
		Boolean testProvidetChoExists = false;
		Boolean bundesleitungUndBezirksverbaendeExists = false;
		String testId = "";
		for(Element pcho : providetCho) {
			if(pcho.getChild("title", C.DC_NS).getValue().equals("Schriftwechsel Holländisch Limburg")) {
				testProvidetChoExists = true;
				assertTrue(pcho.getChild("date", C.DC_NS).getValue().equals("1938-01-01/1939-12-31"));
				testId = pcho.getAttributeValue("about", C.RDF_NS);
			}
		}
		
		Boolean rootElementExists = false;
		for(Element pcho : providetCho) {
			
			if(pcho.getAttributeValue("about", C.RDF_NS).equals("http://data.danrw.de/cho/"+o.getIdentifier())) {
				assertTrue(pcho.getChild("title", C.DC_NS).getValue()
						.equals("VDA - Forschungsstelle Rheinlländer in aller Welt: Bezirksstelle West des Vereins für das Deutschtum im Ausland"));
				assertTrue(pcho.getChild("hasType", C.EDM_NS).getValue().equals("is root element"));
				rootElementExists = true;
			} else {
				assertTrue(pcho.getChild("hasType", C.EDM_NS)==null);
			}
			
			if(pcho.getChild("title", C.DC_NS).getValue().equals("01. Bundesleitung und Bezirksverbände")) {
				bundesleitungUndBezirksverbaendeExists = true;
				assertTrue(pcho.getChild("isPartOf", C.DCTERMS_NS).getAttributeValue("resource", C.RDF_NS).equals("http://data.danrw.de/cho/"+o.getIdentifier()));
				assertTrue(pcho.getChildren("hasPart", C.DCTERMS_NS).size()==39);
			} else if(pcho.getChild("title", C.DC_NS).getValue().equals("02. Finanzsachen")) {
				assertTrue(pcho.getChildren("hasPart", C.DCTERMS_NS).size()==13);
			} else if(pcho.getChild("title", C.DC_NS).getValue().equals("03. Personelles")) {
				assertTrue(pcho.getChildren("hasPart", C.DCTERMS_NS).size()==5);
			} else if(pcho.getChild("title", C.DC_NS).getValue().equals("04. Rheinländer in aller Welt, Adressen")) {
				assertTrue(pcho.getChildren("hasPart", C.DCTERMS_NS).size()==19);
			} else if(pcho.getChild("title", C.DC_NS).getValue().equals("05. VDA Berlin, Volksdeutsche Mittelstelle")) {
				assertTrue(pcho.getChildren("hasPart", C.DCTERMS_NS).size()==19);
			} else if(pcho.getChild("title", C.DC_NS).getValue().equals("VDA - Forschungsstelle Rheinlländer in aller Welt: Bezirksstelle West des Vereins für das Deutschtum im Ausland")) {
				assertTrue(pcho.getChildren("hasPart", C.DCTERMS_NS).size()==30);
			} else if(pcho.getChild("title", C.DC_NS).getValue().equals("Anschriften A-B")) {
				assertTrue(pcho.getChildren("hasPart", C.DCTERMS_NS).size()==5);
			} 
			List<Element> identifier = pcho.getChildren("identifier", C.DC_NS);
			Boolean objIdExists = false;
			Boolean urnExists = false;
			for(Element id : identifier) {
				if(id.getValue().equals(o.getUrn())) {
					urnExists = true;
				} else if(id.getValue().equals(o.getIdentifier())) {
					objIdExists = true;
				}
			}
			assertTrue(objIdExists && urnExists);
		}
		assertTrue(rootElementExists);
		assertTrue(testProvidetChoExists);
		assertTrue(bundesleitungUndBezirksverbaendeExists);
		
//			testIndex
		String cho = "/cho/";
		String ID = testId.substring(testId.lastIndexOf(cho)+cho.length());
		assertTrue(metadataIndex.getIndexedMetadata("portal_ci_test", ID).contains("\"dc:date\":[\"1938-01-01/1939-12-31\"]"));
		
		frEdm.close();
	}
}
