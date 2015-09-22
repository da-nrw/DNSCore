package de.uzk.hki.da.at;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.jdom.xpath.XPath;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.model.WorkArea;
import de.uzk.hki.da.utils.C;
import de.uzk.hki.da.utils.Path;
import de.uzk.hki.da.utils.XMLUtils;

/**
 * 
 * @author Polina Gubaidullina
 *
 */

public class ATMetadataWithRelativeReferencesEad extends AcceptanceTest {

	private static final Namespace METS_NS = Namespace.getNamespace("http://www.loc.gov/METS/");
	private static final Namespace XLINK_NS = Namespace.getNamespace("http://www.w3.org/1999/xlink");
	private String EAD_XPATH_EXPRESSION = "//daoloc/@href";
	private static final String URL = "URL";
	private static Path contractorsPipsPublic;
	private static String origName = "ATMetadataWithRelativeReferencesEad";
	private static Object o;
	private static final String EAD_XML = "EAD.xml";
	private static final File retrievalFolder = new File("/tmp/unpackedDIP");
	
	@BeforeClass
	public static void setUp() throws IOException {
		
		ath.putSIPtoIngestArea(origName, "tgz", origName);
		ath.awaitObjectState(origName,Object.ObjectStatus.ArchivedAndValidAndNotInWorkflow);
		ath.waitForDefinedPublishedState(origName);
		o=ath.getObject(origName);
		
		contractorsPipsPublic = Path.make(localNode.getWorkAreaRootPath(),WorkArea.PIPS, WorkArea.PUBLIC, C.TEST_USER_SHORT_NAME);
	}
	
	@AfterClass
	public static void tearDown() throws IOException{
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
		
		SAXBuilder builder1 = XMLUtils.createNonvalidatingSaxBuilder();
		
		Document doc0 = builder1.build
				(new FileReader(Path.make(tmpObjectDirPath, bRep, "EAD_Export.XML").toFile()));
		List<String> metsReferences = getMetsRefsInEad(doc0);
		for(String metsRef : metsReferences) {
			assertTrue(metsRef.contains("./mets_361/mets_2_3204"));
		}
		
		SAXBuilder builder = XMLUtils.createNonvalidatingSaxBuilder();
		
		Document doc1 = builder.build
				(new FileReader(Path.make(tmpObjectDirPath, bRep, "mets_361", "mets_2_32044.xml").toFile()));
		assertTrue(getMetsURL(doc1).equals("Picture01.tif"));
		assertTrue(getMetsMimetype(doc1).equals("image/tiff"));
		
		Document doc2 = builder.build
				(new FileReader(Path.make(tmpObjectDirPath, bRep, "mets_361", "mets_2_32045.xml").toFile()));
		assertTrue(getMetsURL(doc2).equals("./Picture02.tif"));
		assertTrue(getMetsMimetype(doc2).equals("image/tiff"));
		
		Document doc3 = builder.build
				(new FileReader(Path.make(tmpObjectDirPath, bRep, "mets_361", "mets_2_32046.xml").toFile()));
		assertTrue(getMetsURL(doc3).equals("./Picture03.tif"));
		assertTrue(getMetsMimetype(doc3).equals("image/tiff"));
		
		Document doc4 = builder.build
				(new FileReader(Path.make(tmpObjectDirPath, bRep, "mets_361", "mets_2_32047.xml").toFile()));
		assertTrue(getMetsURL(doc4).equals("./Picture04.tif"));
		assertTrue(getMetsMimetype(doc4).equals("image/tiff"));
		
		Document doc5 = builder.build
				(new FileReader(Path.make(tmpObjectDirPath, bRep, "mets_361", "mets_2_32048.xml").toFile()));
		assertTrue(getMetsURL(doc5).equals("./Picture05.tif"));
		assertTrue(getMetsMimetype(doc5).equals("image/tiff"));
		
	}
	
	@Test
	public void testPres() throws FileNotFoundException, JDOMException, IOException {
		
		SAXBuilder builder = XMLUtils.createNonvalidatingSaxBuilder();
		Document doc = builder.build
				(new FileReader(Path.make(contractorsPipsPublic, o.getIdentifier(), "mets_361", "mets_2_32044.xml").toFile()));
		assertTrue(getMetsURL(doc).contains("http://data.danrw.de"));
		assertEquals(URL, getLoctype(doc));
		assertEquals(C.MIMETYPE_IMAGE_JPEG, getMetsMimetype(doc));
		
		SAXBuilder eadSaxBuilder = XMLUtils.createNonvalidatingSaxBuilder();
		Document eadDoc = eadSaxBuilder.build(new FileReader(Path.make(contractorsPipsPublic, o.getIdentifier(), EAD_XML).toFile()));
		
		List<String> metsReferences = getMetsRefsInEad(eadDoc);
		assertTrue(metsReferences.size()==5);
		for(String metsRef : metsReferences) {
			if(metsRef.contains("mets_2_32044.xml")) {
				assertTrue(metsRef.equals("http://data.danrw.de/file/"+o.getIdentifier()+"/mets_361-mets_2_32044.xml"));
			} else if(metsRef.contains("mets_2_32045.xml")) {
				assertTrue(metsRef.equals("http://data.danrw.de/file/"+o.getIdentifier()+"/mets_361-mets_2_32045.xml"));
			} else if(metsRef.contains("mets_2_32046.xml")) {
				assertTrue(metsRef.equals("http://data.danrw.de/file/"+o.getIdentifier()+"/mets_361-mets_2_32046.xml"));
			} else if(metsRef.contains("mets_2_32047.xml")) {
				assertTrue(metsRef.equals("http://data.danrw.de/file/"+o.getIdentifier()+"/mets_361-mets_2_32047.xml"));
			} else {
				assertTrue(metsRef.equals("http://data.danrw.de/file/"+o.getIdentifier()+"/mets_361-mets_2_32048.xml"));
			}
		}
	}
	
	private String getMetsURL(Document doc){
		
		return doc.getRootElement()
				.getChild("fileSec", METS_NS)
				.getChild("fileGrp", METS_NS)
				.getChild("file", METS_NS)
				.getChild("FLocat", METS_NS)
				.getAttributeValue("href", XLINK_NS);
	}
	
	private String getMetsMimetype(Document doc){
		
		return doc.getRootElement()
				.getChild("fileSec", METS_NS)
				.getChild("fileGrp", METS_NS)
				.getChild("file", METS_NS)
				.getAttributeValue("MIMETYPE");
	}
	
	private String getLoctype(Document doc){
		return doc.getRootElement()
				.getChild("fileSec", C.METS_NS)
				.getChild("fileGrp", C.METS_NS)
				.getChild("file", C.METS_NS)
				.getChild("FLocat", C.METS_NS)
				.getAttributeValue("LOCTYPE");
	}
	
	private List<String> getMetsRefsInEad(Document eadDoc) throws JDOMException, IOException {
		
		List<String> metsReferences = new ArrayList<String>();
	
		XPath xPath = XPath.newInstance(EAD_XPATH_EXPRESSION);
		
		@SuppressWarnings("rawtypes")
		List allNodes = xPath.selectNodes(eadDoc);
		
		for (java.lang.Object node : allNodes) {
			Attribute attr = (Attribute) node;
			String href = attr.getValue();
			metsReferences.add(href);
		}
		return metsReferences;
	}	
}
