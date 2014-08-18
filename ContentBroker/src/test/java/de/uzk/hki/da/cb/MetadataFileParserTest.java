package de.uzk.hki.da.cb;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.io.FileUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xml.sax.SAXException;

import de.uzk.hki.da.metadata.EadMetsMetadataStructure;
import de.uzk.hki.da.metadata.LidoMetadataStructure;
import de.uzk.hki.da.utils.Path;
import de.uzk.hki.da.utils.RelativePath;
import de.uzk.hki.da.utils.XMLUtils;

public class MetadataFileParserTest {
	
	
	private static final Namespace LIDO_NS = Namespace.getNamespace("http://www.lido-schema.org");
	private static final Namespace METS_NS = Namespace.getNamespace("http://www.loc.gov/METS/");
	private static final Namespace XLINK_NS = Namespace.getNamespace("http://www.w3.org/1999/xlink");
	
	private static final Path workAreaRootPathPath = new RelativePath("src/test/resources/cb/MetadataFileParserTest");
	private static String LIDO_FILENAME = "LIDO-Testexport2014-07-04-FML-Auswahl.xml";
	private static String EAD1_FILENAME = "EAD_Export_1.XML";
	private static String EAD2_FILENAME = "EAD_Export_2.XML";
	
//	private static File eadFile1;
	private static File eadFile2;
	private static File lidoFile;
	
	private static EadMetsMetadataStructure eadXML;
	private static LidoMetadataStructure lidoXML;
	
	private static SAXBuilder builder;
	
	@BeforeClass
	public static void setUp() throws IOException, JDOMException, ParserConfigurationException, SAXException, XPathExpressionException {
		FileUtils.copyFileToDirectory(Path.make(workAreaRootPathPath, "data", LIDO_FILENAME).toFile(), Path.make(workAreaRootPathPath, "replacementsTest").toFile());
		FileUtils.copyFileToDirectory(Path.make(workAreaRootPathPath, "data", EAD1_FILENAME).toFile(), Path.make(workAreaRootPathPath, "replacementsTest").toFile());
		FileUtils.copyFileToDirectory(Path.make(workAreaRootPathPath, "data", EAD2_FILENAME).toFile(), Path.make(workAreaRootPathPath, "replacementsTest").toFile());
		
		
		FileUtils.copyFileToDirectory(Path.make(workAreaRootPathPath, "data", "mets_361/mets_2_32044.xml").toFile(), Path.make(workAreaRootPathPath, "replacementsTest", "mets_361").toFile());
		FileUtils.copyFileToDirectory(Path.make(workAreaRootPathPath, "data", "mets_361/mets_2_32045.xml").toFile(), Path.make(workAreaRootPathPath, "replacementsTest", "mets_361").toFile());
		FileUtils.copyFileToDirectory(Path.make(workAreaRootPathPath, "data", "mets_361/mets_2_32046.xml").toFile(), Path.make(workAreaRootPathPath, "replacementsTest", "mets_361").toFile());
		FileUtils.copyFileToDirectory(Path.make(workAreaRootPathPath, "data", "mets_361/mets_2_32047.xml").toFile(), Path.make(workAreaRootPathPath, "replacementsTest", "mets_361").toFile());
		FileUtils.copyFileToDirectory(Path.make(workAreaRootPathPath, "data", "mets_361/mets_2_32048.xml").toFile(), Path.make(workAreaRootPathPath, "replacementsTest", "mets_361").toFile());
		
		
		eadFile2 = Path.make(workAreaRootPathPath, "replacementsTest", EAD2_FILENAME).toFile();
		eadXML = new EadMetsMetadataStructure(eadFile2);
		
		lidoFile = Path.make(workAreaRootPathPath, "replacementsTest", LIDO_FILENAME).toFile();
		lidoXML = new LidoMetadataStructure(lidoFile);
		
		builder = XMLUtils.createNonvalidatingSaxBuilder();
	}
	
	@AfterClass
	public static void cleanUp() {
		Path.makeFile(workAreaRootPathPath, "replacementsTest", LIDO_FILENAME).delete();
		Path.makeFile(workAreaRootPathPath, "replacementsTest", EAD1_FILENAME).delete();
		Path.makeFile(workAreaRootPathPath, "replacementsTest", EAD2_FILENAME).delete();
		
		Path.makeFile(workAreaRootPathPath, "replacementsTest", "mets_361/mets_2_32044.xml").delete();
		Path.makeFile(workAreaRootPathPath, "replacementsTest", "mets_361/mets_2_32045.xml").delete();
		Path.makeFile(workAreaRootPathPath, "replacementsTest", "mets_361/mets_2_32046.xml").delete();
		Path.makeFile(workAreaRootPathPath, "replacementsTest", "mets_361/mets_2_32047.xml").delete();
		Path.makeFile(workAreaRootPathPath, "replacementsTest", "mets_361/mets_2_32048.xml").delete();
		Path.makeFile(workAreaRootPathPath, "replacementsTest", "mets_361").delete();
	}
	
	@Test 
	public void testEADParser() throws JDOMException, IOException, ParserConfigurationException, SAXException {
	}
	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test 
	public void testLidoReplacements() throws IOException, JDOMException{
		HashMap lidoTestReplacements = new HashMap<String, String>();
		lidoTestReplacements.put("LVR_DFG-Alltagskultur_0000050177.tif", "bild1.jpg");
		lidoTestReplacements.put("LVR_DFG-Alltagskultur_0000050178.tif", "bild2.jpg");
		
		lidoXML.setRefResources(lidoTestReplacements);
		
		Document lidoDoc = builder.build(new FileReader(Path.make(workAreaRootPathPath, "replacementsTest", LIDO_FILENAME).toFile()));
		List<String> linkResources = getLidoLinkResources(lidoDoc);
		for(int i=0; i<linkResources.size(); i++) {
			assertEquals("bild"+(i+1)+".jpg", linkResources.get(i));
		}	
	}
	
	@Test 
	public void testMetsReplacementsInEadStructure() throws IOException, JDOMException {
		
		String mimetype = "image/jpeg";
		String loctype = "url";
		
		File metsFile1 = Path.make(workAreaRootPathPath, "replacementsTest", "/mets_361/mets_2_32044.xml").toFile();
		eadXML.makeReplacementsInMetsFile(metsFile1, mimetype, loctype, "mets_361/ALVR_Nr_4547_Aufn_002.tif", "https://bla/ALVR_Nr_4547_Aufn_002.jpg");
		Document metsDoc1 = builder.build(new FileReader(metsFile1));
		assertEquals( "https://bla/ALVR_Nr_4547_Aufn_002.jpg", getMetsURL(metsDoc1));
		assertEquals( "url", getMetsLoctype(metsDoc1));
		assertEquals( "image/jpeg", getMetsMimetype(metsDoc1));
		
		File metsFile2 = Path.make(workAreaRootPathPath, "replacementsTest", "/mets_361/mets_2_32045.xml").toFile();
		eadXML.makeReplacementsInMetsFile(metsFile2, mimetype, loctype, "mets_361/ALVR_Nr_4547_Aufn_003.tif", "https://bla/ALVR_Nr_4547_Aufn_003.jpg");
		Document metsDoc2 = builder.build(new FileReader(metsFile2));
		assertEquals( "https://bla/ALVR_Nr_4547_Aufn_003.jpg", getMetsURL(metsDoc2));
		assertEquals( "url", getMetsLoctype(metsDoc2));
		assertEquals( "image/jpeg", getMetsMimetype(metsDoc2));
		
		File metsFile3 = Path.make(workAreaRootPathPath, "replacementsTest", "/mets_361/mets_2_32046.xml").toFile();
		eadXML.makeReplacementsInMetsFile(metsFile3, mimetype, loctype, "mets_361/ALVR_Nr_4547_Aufn_004.tif", "https://bla/ALVR_Nr_4547_Aufn_004.jpg");
		Document metsDoc3 = builder.build(new FileReader(metsFile3));
		assertEquals( "https://bla/ALVR_Nr_4547_Aufn_004.jpg", getMetsURL(metsDoc3));
		assertEquals( "url", getMetsLoctype(metsDoc3));
		assertEquals( "image/jpeg", getMetsMimetype(metsDoc3));
		
		File metsFile4 = Path.make(workAreaRootPathPath, "replacementsTest", "/mets_361/mets_2_32047.xml").toFile();
		eadXML.makeReplacementsInMetsFile(metsFile4, mimetype, loctype, "mets_361/ALVR_Nr_4547_Aufn_005.tif", "https://bla/ALVR_Nr_4547_Aufn_005.jpg");
		Document metsDoc4 = builder.build(new FileReader(metsFile4));
		assertEquals( "https://bla/ALVR_Nr_4547_Aufn_005.jpg", getMetsURL(metsDoc4));
		assertEquals( "url", getMetsLoctype(metsDoc4));
		assertEquals( "image/jpeg", getMetsMimetype(metsDoc4));
		
		File metsFile5 = Path.make(workAreaRootPathPath, "replacementsTest", "/mets_361/mets_2_32048.xml").toFile();
		eadXML.makeReplacementsInMetsFile(metsFile5, mimetype, loctype, "mets_361/ALVR_Nr_4547_Aufn_006.tif", "https://bla/ALVR_Nr_4547_Aufn_006.jpg");
		Document metsDoc5 = builder.build(new FileReader(metsFile5));
		assertEquals( "https://bla/ALVR_Nr_4547_Aufn_006.jpg", getMetsURL(metsDoc5));
		assertEquals( "url", getMetsLoctype(metsDoc5));
		assertEquals( "image/jpeg", getMetsMimetype(metsDoc5));
	}
	
	public List<String> getLidoLinkResources(Document doc) {
		
		List<String> linkResources = new ArrayList<String>();
		
		@SuppressWarnings("unchecked")
		List<Element> lidoElements = doc.getRootElement().getChildren();
		for(Element element : lidoElements) {
			if(element.getName().equalsIgnoreCase("lido")) {
				String currentLinkResource = element
				.getChild("administrativeMetadata", LIDO_NS)
				.getChild("resourceWrap", LIDO_NS)
				.getChild("resourceSet", LIDO_NS)
				.getChild("resourceRepresentation", LIDO_NS)
				.getChild("linkResource", LIDO_NS)
				.getValue();
				linkResources.add(currentLinkResource);
			}
		}
		return linkResources;
	}
	
	private String getMetsURL(Document doc){
		return doc.getRootElement()
				.getChild("fileSec", METS_NS)
				.getChild("fileGrp", METS_NS)
				.getChild("file", METS_NS)
				.getChild("FLocat", METS_NS)
				.getAttributeValue("href", XLINK_NS);
	}
	
	private String getMetsLoctype(Document doc){
		return doc.getRootElement()
				.getChild("fileSec", METS_NS)
				.getChild("fileGrp", METS_NS)
				.getChild("file", METS_NS)
				.getChild("FLocat", METS_NS)
				.getAttributeValue("LOCTYPE");
	}
	
	private String getMetsMimetype(Document doc){
		return doc.getRootElement()
				.getChild("fileSec", METS_NS)
				.getChild("fileGrp", METS_NS)
				.getChild("file", METS_NS)
				.getAttributeValue("MIMETYPE");
	}
}
