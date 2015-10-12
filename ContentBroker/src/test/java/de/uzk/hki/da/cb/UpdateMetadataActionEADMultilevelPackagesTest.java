package de.uzk.hki.da.cb;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.FileUtils;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.jdom.xpath.XPath;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

import de.uzk.hki.da.format.MimeTypeDetectionService;
import de.uzk.hki.da.model.DAFile;
import de.uzk.hki.da.model.Event;
import de.uzk.hki.da.model.WorkArea;
import de.uzk.hki.da.utils.Path;
import de.uzk.hki.da.utils.RelativePath;
import de.uzk.hki.da.utils.XMLUtils;

public class UpdateMetadataActionEADMultilevelPackagesTest extends ConcreteActionUnitTest{
	
	@ActionUnderTest
	UpdateMetadataAction action = new UpdateMetadataAction();
	
	private static final String _TEMP_PIP_REP_PUBLIC = WorkArea.TMP_PIPS+"/public";
	private static final String _TEMP_PIP_REP_INSTITUTION = WorkArea.TMP_PIPS+"/institution";
	private static final String _1_B_REP = "1+b";
	private static MimeTypeDetectionService mtds;
	private static final Namespace METS_NS = Namespace.getNamespace("http://www.loc.gov/METS/");
	private static final Namespace XLINK_NS = Namespace.getNamespace("http://www.w3.org/1999/xlink");
	private static final Path WORK_AREA_ROOT_PATH = new RelativePath("src/test/resources/cb/UpdateMetadataActionEADMultilevelPackagesTest/");
	private String EAD_XPATH_EXPRESSION = "//daoloc/@href";
	private Event event2;
	private Event event21;
	private Event event3;
	private Event event31;
	private Event event4;
	private Event event41;
	private Event event5;
	private Event event51;
	private Event event6;
	private Event event61;
	
	
	@Before
	public void setUp() throws IOException, JDOMException, ParserConfigurationException, SAXException{
		
		n.setWorkAreaRootPath(WORK_AREA_ROOT_PATH);
		mtds = mock(MimeTypeDetectionService.class);
		when(mtds.identify((File)anyObject(),anyBoolean())).thenReturn("image/tiff");
		ps.setUrisFile("http://data.danrw.de/file");
		
		String[] repNames = {"temp_pips/public", "temp_pips/institution"};
		action.setRepNames(repNames);
		
		FileUtils.copyFileToDirectory(Path.make(WORK_AREA_ROOT_PATH,"work/src/mets_361/mets_2_32044.xml").toFile(), Path.make(WORK_AREA_ROOT_PATH,"work/TEST/identifier/data",_1_B_REP,"mets_361/").toFile());
		System.out.println("COPY "+Path.make(WORK_AREA_ROOT_PATH,"work/src/mets_361/mets_2_32044.xml").toFile()+"; "+Path.make(WORK_AREA_ROOT_PATH,"work/TEST/identifier/data",_1_B_REP,"mets_361/").toFile());
		FileUtils.copyFileToDirectory(Path.make(WORK_AREA_ROOT_PATH,"work/src/mets_361/mets_2_32045.xml").toFile(), Path.make(WORK_AREA_ROOT_PATH,"work/TEST/identifier/data",_1_B_REP,"mets_361/").toFile());
		FileUtils.copyFileToDirectory(Path.make(WORK_AREA_ROOT_PATH,"work/src/mets_361/mets_2_32046.xml").toFile(), Path.make(WORK_AREA_ROOT_PATH,"work/TEST/identifier/data",_1_B_REP,"mets_361/").toFile());
		FileUtils.copyFileToDirectory(Path.make(WORK_AREA_ROOT_PATH,"work/src/mets_361/mets_2_32047.xml").toFile(), Path.make(WORK_AREA_ROOT_PATH,"work/TEST/identifier/data",_1_B_REP,"mets_361/").toFile());
		FileUtils.copyFileToDirectory(Path.make(WORK_AREA_ROOT_PATH,"work/src/mets_361/mets_2_32048.xml").toFile(), Path.make(WORK_AREA_ROOT_PATH,"work/TEST/identifier/data",_1_B_REP,"mets_361/").toFile());
		FileUtils.copyFileToDirectory(Path.make(WORK_AREA_ROOT_PATH,"work/src/mets_361/ALVR_Nr_4547_Aufn_002.tif").toFile(), Path.make(WORK_AREA_ROOT_PATH,"work/TEST/identifier/data",_1_B_REP,"mets_361/").toFile());
		FileUtils.copyFileToDirectory(Path.make(WORK_AREA_ROOT_PATH,"work/src/mets_361/ALVR_Nr_4547_Aufn_003.tif").toFile(), Path.make(WORK_AREA_ROOT_PATH,"work/TEST/identifier/data",_1_B_REP,"mets_361/").toFile());
		FileUtils.copyFileToDirectory(Path.make(WORK_AREA_ROOT_PATH,"work/src/mets_361/ALVR_Nr_4547_Aufn_004.tif").toFile(), Path.make(WORK_AREA_ROOT_PATH,"work/TEST/identifier/data",_1_B_REP,"mets_361/").toFile());
		FileUtils.copyFileToDirectory(Path.make(WORK_AREA_ROOT_PATH,"work/src/mets_361/ALVR_Nr_4547_Aufn_005.tif").toFile(), Path.make(WORK_AREA_ROOT_PATH,"work/TEST/identifier/data",_1_B_REP,"mets_361/").toFile());
		FileUtils.copyFileToDirectory(Path.make(WORK_AREA_ROOT_PATH,"work/src/mets_361/ALVR_Nr_4547_Aufn_006.tif").toFile(), Path.make(WORK_AREA_ROOT_PATH,"work/TEST/identifier/data",_1_B_REP,"mets_361/").toFile());
		FileUtils.copyFileToDirectory(Path.make(WORK_AREA_ROOT_PATH,"work/src/EAD_Export.XML").toFile(), Path.make(WORK_AREA_ROOT_PATH,"work/TEST/identifier/data",_1_B_REP,"").toFile());
		
		DAFile f2 = new DAFile(_1_B_REP,"mets_361/mets_2_32044.xml");
		o.getLatestPackage().getFiles().add(f2);
		de.uzk.hki.da.model.Document doc2 = new de.uzk.hki.da.model.Document(f2);
		o.addDocument(doc2);
		
		DAFile f3 = new DAFile(_1_B_REP,"mets_361/mets_2_32045.xml");
		o.getLatestPackage().getFiles().add(f3);
		de.uzk.hki.da.model.Document doc3 = new de.uzk.hki.da.model.Document(f3);
		o.addDocument(doc3);
		
		DAFile f4 = new DAFile(_1_B_REP,"mets_361/mets_2_32046.xml");
		o.getLatestPackage().getFiles().add(f4);
		de.uzk.hki.da.model.Document doc4 = new de.uzk.hki.da.model.Document(f4);
		o.addDocument(doc4);
		
		DAFile f5 = new DAFile(_1_B_REP,"mets_361/mets_2_32047.xml");
		o.getLatestPackage().getFiles().add(f5);
		de.uzk.hki.da.model.Document doc5 = new de.uzk.hki.da.model.Document(f5);
		o.addDocument(doc5);
		
		DAFile f6 = new DAFile(_1_B_REP,"mets_361/mets_2_32048.xml");
		o.getLatestPackage().getFiles().add(f6);
		de.uzk.hki.da.model.Document doc6 = new de.uzk.hki.da.model.Document(f6);
		o.addDocument(doc6);
		
		DAFile f7 = new DAFile(_1_B_REP,"EAD_Export.XML");
		o.getLatestPackage().getFiles().add(f7);
		de.uzk.hki.da.model.Document doc7 = new de.uzk.hki.da.model.Document(f7);
		o.addDocument(doc7);
		
		event2 = new Event();
		event2.setSource_file(new DAFile(_1_B_REP,"mets_361/ALVR_Nr_4547_Aufn_002.tif"));
		event2.setTarget_file(new DAFile(_TEMP_PIP_REP_PUBLIC,"renamed002.tif"));
		event2.setType("CONVERT");
		o.getLatestPackage().getEvents().add(event2);
		
		event21 = new Event();
		event21.setSource_file(new DAFile(_1_B_REP,"mets_361/ALVR_Nr_4547_Aufn_002.tif"));
		event21.setTarget_file(new DAFile(_TEMP_PIP_REP_INSTITUTION,"renamed002.tif"));
		event21.setType("CONVERT");
		o.getLatestPackage().getEvents().add(event21);
		
		event3 = new Event();
		event3.setSource_file(new DAFile(_1_B_REP,"mets_361/ALVR_Nr_4547_Aufn_003.tif"));
		event3.setTarget_file(new DAFile(_TEMP_PIP_REP_PUBLIC,"renamed003.tif"));
		event3.setType("CONVERT");
		o.getLatestPackage().getEvents().add(event3);
		
		event31 = new Event();
		event31.setSource_file(new DAFile(_1_B_REP,"mets_361/ALVR_Nr_4547_Aufn_003.tif"));
		event31.setTarget_file(new DAFile(_TEMP_PIP_REP_INSTITUTION,"renamed003.tif"));
		event31.setType("CONVERT");
		o.getLatestPackage().getEvents().add(event31);
		
		event4 = new Event();
		event4.setSource_file(new DAFile(_1_B_REP,"mets_361/ALVR_Nr_4547_Aufn_004.tif"));
		event4.setTarget_file(new DAFile(_TEMP_PIP_REP_PUBLIC,"renamed004.tif"));
		event4.setType("CONVERT");
		o.getLatestPackage().getEvents().add(event4);
		
		event41 = new Event();
		event41.setSource_file(new DAFile(_1_B_REP,"mets_361/ALVR_Nr_4547_Aufn_004.tif"));
		event41.setTarget_file(new DAFile(_TEMP_PIP_REP_INSTITUTION,"renamed004.tif"));
		event41.setType("CONVERT");
		o.getLatestPackage().getEvents().add(event41);
		
		event5 = new Event();
		event5.setSource_file(new DAFile(_1_B_REP,"mets_361/ALVR_Nr_4547_Aufn_005.tif"));
		event5.setTarget_file(new DAFile(_TEMP_PIP_REP_PUBLIC,"renamed005.tif"));
		event5.setType("CONVERT");
		o.getLatestPackage().getEvents().add(event5);
		
		event51 = new Event();
		event51.setSource_file(new DAFile(_1_B_REP,"mets_361/ALVR_Nr_4547_Aufn_005.tif"));
		event51.setTarget_file(new DAFile(_TEMP_PIP_REP_INSTITUTION,"renamed005.tif"));
		event51.setType("CONVERT");
		o.getLatestPackage().getEvents().add(event51);
		
		event6 = new Event();
		event6.setSource_file(new DAFile( _1_B_REP,"mets_361/ALVR_Nr_4547_Aufn_006.tif"));
		event6.setTarget_file(new DAFile( _TEMP_PIP_REP_PUBLIC,"renamed006.tif"));
		event6.setType("CONVERT");
		o.getLatestPackage().getEvents().add(event6);
		
		event61 = new Event();
		event61.setSource_file(new DAFile( _1_B_REP,"mets_361/ALVR_Nr_4547_Aufn_006.tif"));
		event61.setTarget_file(new DAFile( _TEMP_PIP_REP_INSTITUTION,"renamed006.tif"));
		event61.setType("CONVERT");
		o.getLatestPackage().getEvents().add(event61);
		
		o.setPackage_type("EAD");
		o.setMetadata_file("EAD_Export.XML");

		HashMap<String,String> xpaths = new HashMap<String,String>();
		xpaths.put("METS", "//mets:file");
		xpaths.put("EAD", "//daoloc/@href");
		action.setXpathsToUrls(xpaths);
		HashMap<String, String> nsMap = new HashMap<String,String>();
		nsMap.put("mets", METS_NS.getURI());
		nsMap.put("xlink", XLINK_NS.getURI());
		action.setNamespaces(nsMap);
		
		action.setMtds(mtds);
		action.setPresMode(true);

	}

	@After 
	public void tearDown(){
		Path.makeFile(WORK_AREA_ROOT_PATH,"work/TEST/identifier/data",_1_B_REP,"mets_361/ALVR_Nr_4547_Aufn_002.tif").delete();
		Path.makeFile(WORK_AREA_ROOT_PATH,"work/TEST/identifier/data",_1_B_REP,"mets_361/ALVR_Nr_4547_Aufn_003.tif").delete();
		Path.makeFile(WORK_AREA_ROOT_PATH,"work/TEST/identifier/data",_1_B_REP,"mets_361/ALVR_Nr_4547_Aufn_004.tif").delete();
		Path.makeFile(WORK_AREA_ROOT_PATH,"work/TEST/identifier/data",_1_B_REP,"mets_361/ALVR_Nr_4547_Aufn_005.tif").delete();
		Path.makeFile(WORK_AREA_ROOT_PATH,"work/TEST/identifier/data",_1_B_REP,"mets_361/ALVR_Nr_4547_Aufn_006.tif").delete();
		Path.makeFile(WORK_AREA_ROOT_PATH,"work/TEST/identifier/data",_1_B_REP,"mets_361/mets_2_32044.xml").delete();
		Path.makeFile(WORK_AREA_ROOT_PATH,"work/TEST/identifier/data",_1_B_REP,"mets_361/mets_2_32045.xml").delete();
		Path.makeFile(WORK_AREA_ROOT_PATH,"work/TEST/identifier/data",_1_B_REP,"mets_361/mets_2_32046.xml").delete();
		Path.makeFile(WORK_AREA_ROOT_PATH,"work/TEST/identifier/data",_1_B_REP,"mets_361/mets_2_32047.xml").delete();
		Path.makeFile(WORK_AREA_ROOT_PATH,"work/TEST/identifier/data",_1_B_REP,"mets_361/mets_2_32048.xml").delete();
		Path.makeFile(WORK_AREA_ROOT_PATH,"work/TEST/identifier/data",_1_B_REP,"mets_361/").delete();
		Path.makeFile(WORK_AREA_ROOT_PATH,"work/TEST/identifier/data",_1_B_REP,"EAD_Export.XML").delete();
		
		Path.makeFile(WORK_AREA_ROOT_PATH,"work/TEST/identifier/data",_TEMP_PIP_REP_PUBLIC,"mets_361/mets_2_32044.xml").delete();
		Path.makeFile(WORK_AREA_ROOT_PATH,"work/TEST/identifier/data",_TEMP_PIP_REP_PUBLIC,"mets_361/mets_2_32045.xml").delete();
		Path.makeFile(WORK_AREA_ROOT_PATH,"work/TEST/identifier/data",_TEMP_PIP_REP_PUBLIC,"mets_361/mets_2_32046.xml").delete();
		Path.makeFile(WORK_AREA_ROOT_PATH,"work/TEST/identifier/data",_TEMP_PIP_REP_PUBLIC,"mets_361/mets_2_32047.xml").delete();
		Path.makeFile(WORK_AREA_ROOT_PATH,"work/TEST/identifier/data",_TEMP_PIP_REP_PUBLIC,"mets_361/mets_2_32048.xml").delete();
		Path.makeFile(WORK_AREA_ROOT_PATH,"work/TEST/identifier/data",_TEMP_PIP_REP_PUBLIC,"mets_361/").delete();
		Path.makeFile(WORK_AREA_ROOT_PATH,"work/TEST/identifier/data",_TEMP_PIP_REP_PUBLIC,"EAD.xml").delete();
		
		Path.makeFile(WORK_AREA_ROOT_PATH,"work/TEST/identifier/data",_TEMP_PIP_REP_INSTITUTION,"mets_361/mets_2_32044.xml").delete();
		Path.makeFile(WORK_AREA_ROOT_PATH,"work/TEST/identifier/data",_TEMP_PIP_REP_INSTITUTION,"mets_361/mets_2_32045.xml").delete();
		Path.makeFile(WORK_AREA_ROOT_PATH,"work/TEST/identifier/data",_TEMP_PIP_REP_INSTITUTION,"mets_361/mets_2_32046.xml").delete();
		Path.makeFile(WORK_AREA_ROOT_PATH,"work/TEST/identifier/data",_TEMP_PIP_REP_INSTITUTION,"mets_361/mets_2_32047.xml").delete();
		Path.makeFile(WORK_AREA_ROOT_PATH,"work/TEST/identifier/data",_TEMP_PIP_REP_INSTITUTION,"mets_361/mets_2_32048.xml").delete();
		Path.makeFile(WORK_AREA_ROOT_PATH,"work/TEST/identifier/data",_TEMP_PIP_REP_INSTITUTION,"mets_361/").delete();
		Path.makeFile(WORK_AREA_ROOT_PATH,"work/TEST/identifier/data",_TEMP_PIP_REP_INSTITUTION,"EAD.xml").delete();
	}

	@Test
	public void testMetsFiles() throws IOException, JDOMException, ParserConfigurationException, SAXException {
		
		action.implementation();
		
		SAXBuilder builder = XMLUtils.createNonvalidatingSaxBuilder();
		Document doc = builder.build(new FileReader(Path.make(WORK_AREA_ROOT_PATH,"work/TEST/identifier/data",_TEMP_PIP_REP_PUBLIC,"mets_361/mets_2_32044.xml").toFile()));
		assertEquals("http://data.danrw.de/file/identifier/renamed002.tif", getURL(doc));
		assertEquals("URL", getLoctype(doc));
		
		Document doc3 = builder.build(new FileReader(Path.make(WORK_AREA_ROOT_PATH,"work/TEST/identifier/data",_TEMP_PIP_REP_PUBLIC,"mets_361/mets_2_32045.xml").toFile()));
		assertEquals("http://data.danrw.de/file/identifier/renamed003.tif", getURL(doc3));
		assertEquals("URL", getLoctype(doc3));
		
		Document doc4 = builder.build(new FileReader(Path.make(WORK_AREA_ROOT_PATH,"work/TEST/identifier/data",_TEMP_PIP_REP_PUBLIC,"mets_361/mets_2_32046.xml").toFile()));
		assertEquals("http://data.danrw.de/file/identifier/renamed004.tif", getURL(doc4));
		assertEquals("URL", getLoctype(doc4));
		
		Document doc5 = builder.build(new FileReader(Path.make(WORK_AREA_ROOT_PATH,"work/TEST/identifier/data",_TEMP_PIP_REP_PUBLIC,"mets_361/mets_2_32047.xml").toFile()));
		assertEquals("http://data.danrw.de/file/identifier/renamed005.tif", getURL(doc5));
		assertEquals("URL", getLoctype(doc5));
		
		Document doc6 = builder.build(new FileReader(Path.make(WORK_AREA_ROOT_PATH,"work/TEST/identifier/data",_TEMP_PIP_REP_PUBLIC,"mets_361/mets_2_32048.xml").toFile()));
		assertEquals("http://data.danrw.de/file/identifier/renamed006.tif", getURL(doc6));
		assertEquals("URL", getLoctype(doc6));
	}

	@Test
	public void testEadFile() throws FileNotFoundException, JDOMException, IOException, ParserConfigurationException, SAXException {
		
		action.implementation();
		
		SAXBuilder eadSaxBuilder = XMLUtils.createNonvalidatingSaxBuilder();
		
		Document eadDocPub = eadSaxBuilder.build(new FileReader(Path.make(WORK_AREA_ROOT_PATH,"work/TEST/identifier/data",_TEMP_PIP_REP_PUBLIC,"EAD.xml").toFile()));
		List<String> eadRefs = getMetsRefsInEad(eadDocPub);
		for(String ref : eadRefs) {
			assertTrue(ref.contains("http://data.danrw.de/file/identifier/mets_361-mets_2_320"));
		}
		
		Document eadDocInst = eadSaxBuilder.build(new FileReader(Path.make(WORK_AREA_ROOT_PATH,"work/TEST/identifier/data",_TEMP_PIP_REP_INSTITUTION,"EAD.xml").toFile()));
		List<String> eadRefsInst = getMetsRefsInEad(eadDocInst);
		for(String ref : eadRefsInst) {
			assertTrue(ref.contains("http://data.danrw.de/file/identifier/mets_361-mets_2_320"));
		}
	}
	
	@Test
	public void testRollback() throws Exception {
		action.implementation();
		assertTrue(Path.makeFile(WORK_AREA_ROOT_PATH,"work/TEST/identifier/data",_TEMP_PIP_REP_PUBLIC,"mets_361/mets_2_32044.xml").exists());
		assertTrue(Path.makeFile(WORK_AREA_ROOT_PATH,"work/TEST/identifier/data",_TEMP_PIP_REP_INSTITUTION,"mets_361/mets_2_32044.xml").exists());
		assertTrue(Path.makeFile(WORK_AREA_ROOT_PATH,"work/TEST/identifier/data",_TEMP_PIP_REP_PUBLIC,"EAD.xml").exists());
		assertTrue(Path.makeFile(WORK_AREA_ROOT_PATH,"work/TEST/identifier/data",_TEMP_PIP_REP_INSTITUTION,"EAD.xml").exists());
		
		action.rollback();
		assertFalse(Path.makeFile(WORK_AREA_ROOT_PATH,"work/TEST/identifier/data",_TEMP_PIP_REP_PUBLIC,"mets_361/mets_2_32044.xml").exists());
		assertFalse(Path.makeFile(WORK_AREA_ROOT_PATH,"work/TEST/identifier/data",_TEMP_PIP_REP_INSTITUTION,"mets_361/mets_2_32044.xml").exists());
		assertFalse(Path.makeFile(WORK_AREA_ROOT_PATH,"work/TEST/identifier/data",_TEMP_PIP_REP_PUBLIC,"EAD.xml").exists());
		assertFalse(Path.makeFile(WORK_AREA_ROOT_PATH,"work/TEST/identifier/data",_TEMP_PIP_REP_INSTITUTION,"EAD.xml").exists());
		assertFalse(Path.makeFile(WORK_AREA_ROOT_PATH,"work/TEST/identifier/data",_TEMP_PIP_REP_PUBLIC,"mets_361").exists());
		
		action.implementation();
		assertTrue(Path.makeFile(WORK_AREA_ROOT_PATH,"work/TEST/identifier/data",_TEMP_PIP_REP_PUBLIC,"mets_361/mets_2_32044.xml").exists());
		assertTrue(Path.makeFile(WORK_AREA_ROOT_PATH,"work/TEST/identifier/data",_TEMP_PIP_REP_INSTITUTION,"mets_361/mets_2_32044.xml").exists());
		assertTrue(Path.makeFile(WORK_AREA_ROOT_PATH,"work/TEST/identifier/data",_TEMP_PIP_REP_PUBLIC,"EAD.xml").exists());
		assertTrue(Path.makeFile(WORK_AREA_ROOT_PATH,"work/TEST/identifier/data",_TEMP_PIP_REP_INSTITUTION,"EAD.xml").exists());
	}
	
	private String getURL(Document doc){
		
		return doc.getRootElement()
				.getChild("fileSec", METS_NS)
				.getChild("fileGrp", METS_NS)
				.getChild("file", METS_NS)
				.getChild("FLocat", METS_NS)
				.getAttributeValue("href", XLINK_NS);
	}
	
	private String getLoctype(Document doc){
		
		return doc.getRootElement()
				.getChild("fileSec", METS_NS)
				.getChild("fileGrp", METS_NS)
				.getChild("file", METS_NS)
				.getChild("FLocat", METS_NS)
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