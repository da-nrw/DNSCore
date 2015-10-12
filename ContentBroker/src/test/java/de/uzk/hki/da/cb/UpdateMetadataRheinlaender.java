package de.uzk.hki.da.cb;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.FileUtils;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
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

public class UpdateMetadataRheinlaender extends ConcreteActionUnitTest{
	
	@ActionUnderTest
	UpdateMetadataAction action = new UpdateMetadataAction();
	
	private static final String _TEMP_PIP_REP_PUBLIC = WorkArea.TMP_PIPS+"/public";
	private static final String _TEMP_PIP_REP_INSTITUTION = WorkArea.TMP_PIPS+"/institution";
	private static final String _1_B_REP = "1+b";
	private static MimeTypeDetectionService mtds;
	private static final Namespace METS_NS = Namespace.getNamespace("http://www.loc.gov/METS/");
	private static final Namespace XLINK_NS = Namespace.getNamespace("http://www.w3.org/1999/xlink");
	private static final Path WORK_AREA_ROOT_PATH = new RelativePath("src/test/resources/cb/UpdateMetadataRheinlaender/");
	private Event event2;
	private Event event3;
	private Event event4;
	private Event event5;
	private Event event6;
	
	@Before
	public void setUp() throws IOException{
		mtds = mock(MimeTypeDetectionService.class);
		when(mtds.identify((File)anyObject(),anyBoolean())).thenReturn("image/tiff");
		n.setWorkAreaRootPath(WORK_AREA_ROOT_PATH);
		
		String[] repNames = {"temp_pips/public", "temp_pips/institution"};
		action.setRepNames(repNames);
		
		ps.setUrisFile("http://data.danrw.de/file");
		
		FileUtils.copyFileToDirectory(Path.make(WORK_AREA_ROOT_PATH,"work/src/mets_2_32044.xml").toFile(), Path.make(WORK_AREA_ROOT_PATH,"work/TEST/identifier/data", _1_B_REP).toFile());
		FileUtils.copyFileToDirectory(Path.make(WORK_AREA_ROOT_PATH,"work/src/mets_2_32045.xml").toFile(), Path.make(WORK_AREA_ROOT_PATH,"work/TEST/identifier/data", _1_B_REP).toFile());
		FileUtils.copyFileToDirectory(Path.make(WORK_AREA_ROOT_PATH,"work/src/mets_2_32046.xml").toFile(), Path.make(WORK_AREA_ROOT_PATH,"work/TEST/identifier/data", _1_B_REP).toFile());
		FileUtils.copyFileToDirectory(Path.make(WORK_AREA_ROOT_PATH,"work/src/mets_2_32047.xml").toFile(), Path.make(WORK_AREA_ROOT_PATH,"work/TEST/identifier/data", _1_B_REP).toFile());
		FileUtils.copyFileToDirectory(Path.make(WORK_AREA_ROOT_PATH,"work/src/mets_2_32048.xml").toFile(), Path.make(WORK_AREA_ROOT_PATH,"work/TEST/identifier/data", _1_B_REP).toFile());
		FileUtils.copyFileToDirectory(Path.make(WORK_AREA_ROOT_PATH,"work/src/alvr_nr_4547_aufn_002.tif").toFile(), Path.make(WORK_AREA_ROOT_PATH,"work/TEST/identifier/data", _1_B_REP).toFile());
		FileUtils.copyFileToDirectory(Path.make(WORK_AREA_ROOT_PATH,"work/src/alvr_nr_4547_aufn_003.tif").toFile(), Path.make(WORK_AREA_ROOT_PATH,"work/TEST/identifier/data", _1_B_REP).toFile());
		FileUtils.copyFileToDirectory(Path.make(WORK_AREA_ROOT_PATH,"work/src/alvr_nr_4547_aufn_004.tif").toFile(), Path.make(WORK_AREA_ROOT_PATH,"work/TEST/identifier/data", _1_B_REP).toFile());
		FileUtils.copyFileToDirectory(Path.make(WORK_AREA_ROOT_PATH,"work/src/alvr_nr_4547_aufn_005.tif").toFile(), Path.make(WORK_AREA_ROOT_PATH,"work/TEST/identifier/data", _1_B_REP).toFile());
		FileUtils.copyFileToDirectory(Path.make(WORK_AREA_ROOT_PATH,"work/src/alvr_nr_4547_aufn_006.tif").toFile(), Path.make(WORK_AREA_ROOT_PATH,"work/TEST/identifier/data", _1_B_REP).toFile());
		FileUtils.copyFileToDirectory(Path.make(WORK_AREA_ROOT_PATH,"work/src/EAD_Export.XML").toFile(), Path.make(WORK_AREA_ROOT_PATH,"work/TEST/identifier/data", _1_B_REP).toFile());
		
		DAFile f2 = new DAFile(_1_B_REP,"mets_2_32044.xml");
		o.getLatestPackage().getFiles().add(f2);
		de.uzk.hki.da.model.Document doc2 = new de.uzk.hki.da.model.Document(f2);
		o.addDocument(doc2);
		
		DAFile f3 = new DAFile(_1_B_REP,"mets_2_32045.xml");
		o.getLatestPackage().getFiles().add(f3);
		de.uzk.hki.da.model.Document doc3 = new de.uzk.hki.da.model.Document(f3);
		o.addDocument(doc3);
		
		DAFile f4 = new DAFile(_1_B_REP,"mets_2_32046.xml");
		o.getLatestPackage().getFiles().add(f4);
		de.uzk.hki.da.model.Document doc4 = new de.uzk.hki.da.model.Document(f4);
		o.addDocument(doc4);
		
		DAFile f5 = new DAFile(_1_B_REP,"mets_2_32047.xml");
		o.getLatestPackage().getFiles().add(f5);
		de.uzk.hki.da.model.Document doc5 = new de.uzk.hki.da.model.Document(f5);
		o.addDocument(doc5);
		
		DAFile f6 = new DAFile(_1_B_REP,"mets_2_32048.xml");
		o.getLatestPackage().getFiles().add(f6);
		de.uzk.hki.da.model.Document doc6 = new de.uzk.hki.da.model.Document(f6);
		o.addDocument(doc6);
		
		DAFile f7 = new DAFile(_1_B_REP,"EAD_Export.XML");
		o.getLatestPackage().getFiles().add(f7);
		de.uzk.hki.da.model.Document doc7 = new de.uzk.hki.da.model.Document(f7);
		o.addDocument(doc7);
		
		event2 = new Event();
		event2.setSource_file(new DAFile(_1_B_REP,"alvr_nr_4547_aufn_002.tif"));
		event2.setTarget_file(new DAFile(_TEMP_PIP_REP_PUBLIC,"renamed002.tif"));
		event2.setType("CONVERT");
		o.getLatestPackage().getEvents().add(event2);
		
		event3 = new Event();
		event3.setSource_file(new DAFile(_1_B_REP,"alvr_nr_4547_aufn_003.tif"));
		event3.setTarget_file(new DAFile(_TEMP_PIP_REP_PUBLIC,"renamed003.tif"));
		event3.setType("CONVERT");
		o.getLatestPackage().getEvents().add(event3);
		
		event4 = new Event();
		event4.setSource_file(new DAFile(_1_B_REP,"alvr_nr_4547_aufn_004.tif"));
		event4.setTarget_file(new DAFile(_TEMP_PIP_REP_PUBLIC,"renamed004.tif"));
		event4.setType("CONVERT");
		o.getLatestPackage().getEvents().add(event4);
		
		event5 = new Event();
		event5.setSource_file(new DAFile(_1_B_REP,"alvr_nr_4547_aufn_005.tif"));
		event5.setTarget_file(new DAFile(_TEMP_PIP_REP_PUBLIC,"renamed005.tif"));
		event5.setType("CONVERT");
		o.getLatestPackage().getEvents().add(event5);
		
		event6 = new Event();
		event6.setSource_file(new DAFile(_1_B_REP,"alvr_nr_4547_aufn_006.tif"));
		event6.setTarget_file(new DAFile(_TEMP_PIP_REP_PUBLIC,"renamed006.tif"));
		event6.setType("CONVERT");
		o.getLatestPackage().getEvents().add(event6);

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
		Path.makeFile(WORK_AREA_ROOT_PATH,"work/TEST/identifier/data", _1_B_REP, "alvr_nr_4547_aufn_002.tif").delete();
		Path.makeFile(WORK_AREA_ROOT_PATH,"work/TEST/identifier/data", _1_B_REP, "alvr_nr_4547_aufn_003.tif").delete();
		Path.makeFile(WORK_AREA_ROOT_PATH,"work/TEST/identifier/data", _1_B_REP, "alvr_nr_4547_aufn_004.tif").delete();
		Path.makeFile(WORK_AREA_ROOT_PATH,"work/TEST/identifier/data", _1_B_REP, "alvr_nr_4547_aufn_005.tif").delete();
		Path.makeFile(WORK_AREA_ROOT_PATH,"work/TEST/identifier/data", _1_B_REP, "alvr_nr_4547_aufn_006.tif").delete();
		Path.makeFile(WORK_AREA_ROOT_PATH,"work/TEST/identifier/data", _1_B_REP, "mets_2_32044.xml").delete();
		Path.makeFile(WORK_AREA_ROOT_PATH,"work/TEST/identifier/data", _1_B_REP, "mets_2_32045.xml").delete();
		Path.makeFile(WORK_AREA_ROOT_PATH,"work/TEST/identifier/data", _1_B_REP, "mets_2_32046.xml").delete();
		Path.makeFile(WORK_AREA_ROOT_PATH,"work/TEST/identifier/data", _1_B_REP, "mets_2_32047.xml").delete();
		Path.makeFile(WORK_AREA_ROOT_PATH,"work/TEST/identifier/data", _1_B_REP, "mets_2_32048.xml").delete();
		Path.makeFile(WORK_AREA_ROOT_PATH,"work/TEST/identifier/data", _1_B_REP, "EAD_Export.XML").delete();
		Path.makeFile(WORK_AREA_ROOT_PATH,"work/TEST/identifier/data", _TEMP_PIP_REP_PUBLIC, "mets_2_32044.xml").delete();
		Path.makeFile(WORK_AREA_ROOT_PATH,"work/TEST/identifier/data", _TEMP_PIP_REP_PUBLIC, "mets_2_32045.xml").delete();
		Path.makeFile(WORK_AREA_ROOT_PATH,"work/TEST/identifier/data", _TEMP_PIP_REP_PUBLIC, "mets_2_32046.xml").delete();
		Path.makeFile(WORK_AREA_ROOT_PATH,"work/TEST/identifier/data", _TEMP_PIP_REP_PUBLIC, "mets_2_32047.xml").delete();
		Path.makeFile(WORK_AREA_ROOT_PATH,"work/TEST/identifier/data", _TEMP_PIP_REP_PUBLIC, "mets_2_32048.xml").delete();
		Path.makeFile(WORK_AREA_ROOT_PATH,"work/TEST/identifier/data", _TEMP_PIP_REP_PUBLIC, "EAD.xml").delete();
		Path.makeFile(WORK_AREA_ROOT_PATH,"work/TEST/identifier/data", _TEMP_PIP_REP_INSTITUTION, "mets_2_32044.xml").delete();
		Path.makeFile(WORK_AREA_ROOT_PATH,"work/TEST/identifier/data", _TEMP_PIP_REP_INSTITUTION, "mets_2_32045.xml").delete();
		Path.makeFile(WORK_AREA_ROOT_PATH,"work/TEST/identifier/data", _TEMP_PIP_REP_INSTITUTION, "mets_2_32046.xml").delete();
		Path.makeFile(WORK_AREA_ROOT_PATH,"work/TEST/identifier/data", _TEMP_PIP_REP_INSTITUTION, "mets_2_32047.xml").delete();
		Path.makeFile(WORK_AREA_ROOT_PATH,"work/TEST/identifier/data", _TEMP_PIP_REP_INSTITUTION, "mets_2_32048.xml").delete();
		Path.makeFile(WORK_AREA_ROOT_PATH,"work/TEST/identifier/data", _TEMP_PIP_REP_INSTITUTION, "EAD.xml").delete();
	}

	@Test
	public void test() throws IOException, JDOMException, ParserConfigurationException, SAXException {
		action.implementation();
		
		SAXBuilder builder = XMLUtils.createNonvalidatingSaxBuilder();
		Document doc = builder.build(new FileReader(Path.make(WORK_AREA_ROOT_PATH, "work/TEST/identifier/data", _TEMP_PIP_REP_PUBLIC, "mets_2_32044.xml").toFile()));
		assertEquals("http://data.danrw.de/file/identifier/renamed002.tif", getURL(doc));
		
		Document doc3 = builder.build(new FileReader(Path.make(WORK_AREA_ROOT_PATH,"work/TEST/identifier/data", _TEMP_PIP_REP_PUBLIC, "mets_2_32045.xml").toFile()));
		assertEquals("http://data.danrw.de/file/identifier/renamed003.tif", getURL(doc3));
		
		Document doc4 = builder.build(new FileReader(Path.make(WORK_AREA_ROOT_PATH,"work/TEST/identifier/data", _TEMP_PIP_REP_PUBLIC, "mets_2_32046.xml").toFile()));
		assertEquals("http://data.danrw.de/file/identifier/renamed004.tif", getURL(doc4));
		
		Document doc5 = builder.build(new FileReader(Path.make(WORK_AREA_ROOT_PATH,"work/TEST/identifier/data", _TEMP_PIP_REP_PUBLIC, "mets_2_32047.xml").toFile()));
		assertEquals("http://data.danrw.de/file/identifier/renamed005.tif", getURL(doc5));
		
		Document doc6 = builder.build(new FileReader(Path.make(WORK_AREA_ROOT_PATH,"work/TEST/identifier/data", _TEMP_PIP_REP_PUBLIC, "mets_2_32048.xml").toFile()));
		assertEquals("http://data.danrw.de/file/identifier/renamed006.tif", getURL(doc6));
	}
	
	@Test
	public void testRollback() throws Exception {
		action.implementation();
		assertTrue(Path.makeFile(WORK_AREA_ROOT_PATH,"work/TEST/identifier/data",_TEMP_PIP_REP_PUBLIC,"EAD.xml").exists());
		assertTrue(Path.makeFile(WORK_AREA_ROOT_PATH,"work/TEST/identifier/data",_TEMP_PIP_REP_PUBLIC,"mets_2_32044.xml").exists());
		assertTrue(Path.makeFile(WORK_AREA_ROOT_PATH,"work/TEST/identifier/data",_TEMP_PIP_REP_PUBLIC,"mets_2_32045.xml").exists());
		assertTrue(Path.makeFile(WORK_AREA_ROOT_PATH,"work/TEST/identifier/data",_TEMP_PIP_REP_PUBLIC,"mets_2_32046.xml").exists());
		assertTrue(Path.makeFile(WORK_AREA_ROOT_PATH,"work/TEST/identifier/data",_TEMP_PIP_REP_PUBLIC,"mets_2_32047.xml").exists());
		assertTrue(Path.makeFile(WORK_AREA_ROOT_PATH,"work/TEST/identifier/data",_TEMP_PIP_REP_PUBLIC,"mets_2_32048.xml").exists());
		assertTrue(Path.makeFile(WORK_AREA_ROOT_PATH,"work/TEST/identifier/data",_TEMP_PIP_REP_INSTITUTION,"EAD.xml").exists());
		assertTrue(Path.makeFile(WORK_AREA_ROOT_PATH,"work/TEST/identifier/data",_TEMP_PIP_REP_INSTITUTION,"mets_2_32044.xml").exists());
		assertTrue(Path.makeFile(WORK_AREA_ROOT_PATH,"work/TEST/identifier/data",_TEMP_PIP_REP_INSTITUTION,"mets_2_32045.xml").exists());
		assertTrue(Path.makeFile(WORK_AREA_ROOT_PATH,"work/TEST/identifier/data",_TEMP_PIP_REP_INSTITUTION,"mets_2_32046.xml").exists());
		assertTrue(Path.makeFile(WORK_AREA_ROOT_PATH,"work/TEST/identifier/data",_TEMP_PIP_REP_INSTITUTION,"mets_2_32047.xml").exists());
		assertTrue(Path.makeFile(WORK_AREA_ROOT_PATH,"work/TEST/identifier/data",_TEMP_PIP_REP_INSTITUTION,"mets_2_32048.xml").exists());
		
		action.rollback();
		assertFalse(Path.makeFile(WORK_AREA_ROOT_PATH,"work/TEST/identifier/data",_TEMP_PIP_REP_PUBLIC,"EAD.xml").exists());
		assertFalse(Path.makeFile(WORK_AREA_ROOT_PATH,"work/TEST/identifier/data",_TEMP_PIP_REP_PUBLIC,"mets_2_32044.xml").exists());
		assertFalse(Path.makeFile(WORK_AREA_ROOT_PATH,"work/TEST/identifier/data",_TEMP_PIP_REP_PUBLIC,"mets_2_32045.xml").exists());
		assertFalse(Path.makeFile(WORK_AREA_ROOT_PATH,"work/TEST/identifier/data",_TEMP_PIP_REP_PUBLIC,"mets_2_32046.xml").exists());
		assertFalse(Path.makeFile(WORK_AREA_ROOT_PATH,"work/TEST/identifier/data",_TEMP_PIP_REP_PUBLIC,"mets_2_32047.xml").exists());
		assertFalse(Path.makeFile(WORK_AREA_ROOT_PATH,"work/TEST/identifier/data",_TEMP_PIP_REP_PUBLIC,"mets_2_32048.xml").exists());
		assertFalse(Path.makeFile(WORK_AREA_ROOT_PATH,"work/TEST/identifier/data",_TEMP_PIP_REP_INSTITUTION,"EAD.xml").exists());
		assertFalse(Path.makeFile(WORK_AREA_ROOT_PATH,"work/TEST/identifier/data",_TEMP_PIP_REP_INSTITUTION,"mets_2_32044.xml").exists());
		assertFalse(Path.makeFile(WORK_AREA_ROOT_PATH,"work/TEST/identifier/data",_TEMP_PIP_REP_INSTITUTION,"mets_2_32045.xml").exists());
		assertFalse(Path.makeFile(WORK_AREA_ROOT_PATH,"work/TEST/identifier/data",_TEMP_PIP_REP_INSTITUTION,"mets_2_32046.xml").exists());
		assertFalse(Path.makeFile(WORK_AREA_ROOT_PATH,"work/TEST/identifier/data",_TEMP_PIP_REP_INSTITUTION,"mets_2_32047.xml").exists());
		assertFalse(Path.makeFile(WORK_AREA_ROOT_PATH,"work/TEST/identifier/data",_TEMP_PIP_REP_INSTITUTION,"mets_2_32048.xml").exists());
		
		action.implementation();
		assertTrue(Path.makeFile(WORK_AREA_ROOT_PATH,"work/TEST/identifier/data",_TEMP_PIP_REP_PUBLIC,"EAD.xml").exists());
		assertTrue(Path.makeFile(WORK_AREA_ROOT_PATH,"work/TEST/identifier/data",_TEMP_PIP_REP_PUBLIC,"mets_2_32044.xml").exists());
		assertTrue(Path.makeFile(WORK_AREA_ROOT_PATH,"work/TEST/identifier/data",_TEMP_PIP_REP_PUBLIC,"mets_2_32045.xml").exists());
		assertTrue(Path.makeFile(WORK_AREA_ROOT_PATH,"work/TEST/identifier/data",_TEMP_PIP_REP_PUBLIC,"mets_2_32046.xml").exists());
		assertTrue(Path.makeFile(WORK_AREA_ROOT_PATH,"work/TEST/identifier/data",_TEMP_PIP_REP_PUBLIC,"mets_2_32047.xml").exists());
		assertTrue(Path.makeFile(WORK_AREA_ROOT_PATH,"work/TEST/identifier/data",_TEMP_PIP_REP_PUBLIC,"mets_2_32048.xml").exists());
		assertTrue(Path.makeFile(WORK_AREA_ROOT_PATH,"work/TEST/identifier/data",_TEMP_PIP_REP_INSTITUTION,"EAD.xml").exists());
		assertTrue(Path.makeFile(WORK_AREA_ROOT_PATH,"work/TEST/identifier/data",_TEMP_PIP_REP_INSTITUTION,"mets_2_32044.xml").exists());
		assertTrue(Path.makeFile(WORK_AREA_ROOT_PATH,"work/TEST/identifier/data",_TEMP_PIP_REP_INSTITUTION,"mets_2_32045.xml").exists());
		assertTrue(Path.makeFile(WORK_AREA_ROOT_PATH,"work/TEST/identifier/data",_TEMP_PIP_REP_INSTITUTION,"mets_2_32046.xml").exists());
		assertTrue(Path.makeFile(WORK_AREA_ROOT_PATH,"work/TEST/identifier/data",_TEMP_PIP_REP_INSTITUTION,"mets_2_32047.xml").exists());
		assertTrue(Path.makeFile(WORK_AREA_ROOT_PATH,"work/TEST/identifier/data",_TEMP_PIP_REP_INSTITUTION,"mets_2_32048.xml").exists());
	}
	
	private String getURL(Document doc){
		return doc.getRootElement()
				.getChild("fileSec", METS_NS)
				.getChild("fileGrp", METS_NS)
				.getChild("file", METS_NS)
				.getChild("FLocat", METS_NS)
				.getAttributeValue("href", XLINK_NS);
	}
}

