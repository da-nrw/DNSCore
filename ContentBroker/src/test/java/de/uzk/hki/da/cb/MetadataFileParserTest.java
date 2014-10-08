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

import de.uzk.hki.da.core.Path;
import de.uzk.hki.da.core.RelativePath;
import de.uzk.hki.da.metadata.EadMetsMetadataStructure;
import de.uzk.hki.da.metadata.LidoMetadataStructure;
import de.uzk.hki.da.metadata.MetsMetadataStructure;
import de.uzk.hki.da.metadata.XMLUtils;
import de.uzk.hki.da.model.DAFile;

public class MetadataFileParserTest {
	
	
	private static final Namespace LIDO_NS = Namespace.getNamespace("http://www.lido-schema.org");
	private static final Namespace METS_NS = Namespace.getNamespace("http://www.loc.gov/METS/");
	private static final Namespace XLINK_NS = Namespace.getNamespace("http://www.w3.org/1999/xlink");
	
	private static final Path workAreaRootPathPath = new RelativePath("src/test/resources/cb/MetadataFileParserTest");
	private static String LIDO_FILENAME = "LIDO-Testexport2014-07-04-FML-Auswahl.xml";
	private static String EAD1_FILENAME = "EAD_Export_1.XML";
	private static String EAD2_FILENAME = "EAD_Export_2.XML";
	private static List<DAFile> dafiles;
	
//	private static File eadFile1;
	private static File eadFile2;
	private static File lidoFile;
	
	private static EadMetsMetadataStructure eadStructure;
	private static LidoMetadataStructure lidoStructure;
	
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
		eadStructure = new EadMetsMetadataStructure(eadFile2, dafiles);
		
		lidoFile = Path.make(workAreaRootPathPath, "replacementsTest", LIDO_FILENAME).toFile();
		lidoStructure = new LidoMetadataStructure(lidoFile, dafiles);
		
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
		
		lidoStructure.replaceRefResources(lidoTestReplacements);
		
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
		
		for(MetsMetadataStructure mms : eadStructure.getMetsMetadataStructures()) {
			File metsFile = mms.getMetadataFile();
			
			if(metsFile.getName().equals("mets_2_32044.xml")) {
				mms.makeReplacementsInMetsFile(mms.getMetadataFile(), "mets_361/ALVR_Nr_4547_Aufn_002.tif", "https://bla/ALVR_Nr_4547_Aufn_002.jpg", mimetype, loctype);
				Document metsDoc = builder.build(new FileReader(mms.getMetadataFile()));
				assertEquals( "https://bla/ALVR_Nr_4547_Aufn_002.jpg", getMetsURL(metsDoc));
				assertEquals( "url", getMetsLoctype(metsDoc));
				assertEquals( "image/jpeg", getMetsMimetype(metsDoc));
			} else if(metsFile.getName().equals("mets_2_32045.xml")) {
				mms.makeReplacementsInMetsFile(mms.getMetadataFile(), "mets_361/ALVR_Nr_4547_Aufn_003.tif", "https://bla/ALVR_Nr_4547_Aufn_003.jpg", mimetype, loctype);
				Document metsDoc = builder.build(new FileReader(mms.getMetadataFile()));
				assertEquals( "https://bla/ALVR_Nr_4547_Aufn_003.jpg", getMetsURL(metsDoc));
				assertEquals( "url", getMetsLoctype(metsDoc));
				assertEquals( "image/jpeg", getMetsMimetype(metsDoc));
			} else if(metsFile.getName().equals("mets_2_32046.xml")) {
				mms.makeReplacementsInMetsFile(mms.getMetadataFile(), "mets_361/ALVR_Nr_4547_Aufn_004.tif", "https://bla/ALVR_Nr_4547_Aufn_004.jpg", mimetype, loctype);
				Document metsDoc = builder.build(new FileReader(mms.getMetadataFile()));
				assertEquals( "https://bla/ALVR_Nr_4547_Aufn_004.jpg", getMetsURL(metsDoc));
				assertEquals( "url", getMetsLoctype(metsDoc));
				assertEquals( "image/jpeg", getMetsMimetype(metsDoc));
			} else if(metsFile.getName().equals("mets_2_32047.xml")) {
				mms.makeReplacementsInMetsFile(mms.getMetadataFile(), "mets_361/ALVR_Nr_4547_Aufn_005.tif", "https://bla/ALVR_Nr_4547_Aufn_005.jpg", mimetype, loctype);
				Document metsDoc = builder.build(new FileReader(mms.getMetadataFile()));
				assertEquals( "https://bla/ALVR_Nr_4547_Aufn_005.jpg", getMetsURL(metsDoc));
				assertEquals( "url", getMetsLoctype(metsDoc));
				assertEquals( "image/jpeg", getMetsMimetype(metsDoc));
			} else {
				mms.makeReplacementsInMetsFile(mms.getMetadataFile(), "mets_361/ALVR_Nr_4547_Aufn_006.tif", "https://bla/ALVR_Nr_4547_Aufn_006.jpg", mimetype, loctype);
				Document metsDoc = builder.build(new FileReader(mms.getMetadataFile()));
				assertEquals( "https://bla/ALVR_Nr_4547_Aufn_006.jpg", getMetsURL(metsDoc));
				assertEquals( "url", getMetsLoctype(metsDoc));
				assertEquals( "image/jpeg", getMetsMimetype(metsDoc));
			}
		}
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
