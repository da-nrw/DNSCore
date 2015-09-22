package de.uzk.hki.da.cb;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyObject;
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

public class UpdateMetadataActionLIDOTests extends ConcreteActionUnitTest{
	
	@ActionUnderTest
	UpdateMetadataAction action = new UpdateMetadataAction();
	
	private static final String _TEMP_PIP_REP_PUBLIC = WorkArea.TMP_PIPS+"/public";
	private static final String _TEMP_PIP_REP_INSTITUTION = WorkArea.TMP_PIPS+"/institution";
	private static final String _1_B_REP = "1+b";
	private static final Namespace LIDO_NS = Namespace.getNamespace("http://www.lido-schema.org");
	private static MimeTypeDetectionService mtds;
	private static final Path WORK_AREA_ROOT_PATH = new RelativePath("src/test/resources/cb/UpdateMetadataActionLIDOTests/");

	private Event event1;
	private Event event2;
	private Event event3;
	
	@Before
	public void setUp() throws IOException {
		n.setWorkAreaRootPath(WORK_AREA_ROOT_PATH);
		mtds = mock(MimeTypeDetectionService.class);
		when(mtds.identify((File)anyObject())).thenReturn("image/tiff");
		ps.setUrisFile("http://data.danrw.de/file");
		
		String[] repNames = {"temp_pips/public", "temp_pips/institution"};
		action.setRepNames(repNames);

		FileUtils.copyFileToDirectory(Path.make(WORK_AREA_ROOT_PATH,"work/src/LIDO-Testexport2014-07-04-FML-Auswahl.xml").toFile(), Path.make(WORK_AREA_ROOT_PATH,"work/TEST/identifier/data",_1_B_REP).toFile());
		DAFile f1 = new DAFile(_1_B_REP,"LIDO-Testexport2014-07-04-FML-Auswahl.xml");
		de.uzk.hki.da.model.Document doc1 = new de.uzk.hki.da.model.Document(f1);
		o.addDocument(doc1);
		o.getLatestPackage().getFiles().add(f1);
		
		DAFile f2 = new DAFile(_1_B_REP,"LVR_DFG-Alltagskultur_0000050177.tif");
		de.uzk.hki.da.model.Document doc2 = new de.uzk.hki.da.model.Document(f2);
		o.addDocument(doc2);
		
		event1 = new Event();
		event1.setSource_file(f2);
		event1.setTarget_file(new DAFile(_TEMP_PIP_REP_PUBLIC,"renamed0000050177.tif"));
		event1.setType("CONVERT");
		
		DAFile f4 = new DAFile(_1_B_REP,"LVR_DFG-Alltagskultur_0000050178.tif");
		de.uzk.hki.da.model.Document doc4 = new de.uzk.hki.da.model.Document(f4);
		o.addDocument(doc4);
		
		event2 = new Event();
		event2.setSource_file(new DAFile(_1_B_REP,"LVR_DFG-Alltagskultur_0000050178.tif"));
		event2.setTarget_file(new DAFile(_TEMP_PIP_REP_PUBLIC,"renamed0000050178.tif"));
		event2.setType("CONVERT");
		
		DAFile f5 = new DAFile(_1_B_REP,"Test.tif");
		de.uzk.hki.da.model.Document doc5 = new de.uzk.hki.da.model.Document(f5);
		o.addDocument(doc5);
		
		event3 = new Event();
		event3.setSource_file(new DAFile(_1_B_REP,"lvr_dfg-alltagskultur_0000050178"));
		event3.setTarget_file(new DAFile(_TEMP_PIP_REP_PUBLIC,"renamed0000050178_1.tif"));
		event3.setType("CONVERT");
		
		o.getLatestPackage().getEvents().add(event1);
		o.getLatestPackage().getEvents().add(event2);
		o.getLatestPackage().getEvents().add(event3);

		o.setPackage_type("LIDO");
		o.setMetadata_file("LIDO-Testexport2014-07-04-FML-Auswahl.xml");
		
		HashMap<String,String> xpaths = new HashMap<String,String>();
		xpaths.put("LIDO", "//lido:linkResource");
		action.setXpathsToUrls(xpaths);
		HashMap<String, String> nsMap = new HashMap<String,String>();
		nsMap.put("lido", LIDO_NS.getURI());
		action.setNamespaces(nsMap);
		action.setMtds(mtds);
		action.setPresMode(true);
	}
	
	@After 
	public void tearDown(){
		Path.makeFile(WORK_AREA_ROOT_PATH,"work/TEST/identifier/data",_1_B_REP,"LIDO-Testexport2014-07-04-FML-Auswahl.xml").delete();
		Path.makeFile(WORK_AREA_ROOT_PATH,"work/TEST/identifier/data",_TEMP_PIP_REP_PUBLIC,"LIDO.xml").delete();
		Path.makeFile(WORK_AREA_ROOT_PATH,"work/TEST/identifier/data",_TEMP_PIP_REP_INSTITUTION,"LIDO.xml").delete();
	}
	
	@Test
	public void test() throws IOException, JDOMException, ParserConfigurationException, SAXException {
		action.implementation();
		
		SAXBuilder builder = XMLUtils.createNonvalidatingSaxBuilder();
		Document doc = builder.build(new FileReader(Path.make(WORK_AREA_ROOT_PATH,"work/TEST/identifier/data",_TEMP_PIP_REP_PUBLIC,"LIDO.xml").toFile()));
		assertEquals("http://data.danrw.de/file/identifier/renamed0000050177.tif", getLIDOURL(doc));
		
	}
	
	@Test
	public void testRollback() throws Exception {
		action.implementation();
		assertTrue(Path.makeFile(WORK_AREA_ROOT_PATH,"work/TEST/identifier/data",_TEMP_PIP_REP_PUBLIC,"LIDO.xml").exists());
		assertTrue(Path.makeFile(WORK_AREA_ROOT_PATH,"work/TEST/identifier/data",_TEMP_PIP_REP_INSTITUTION,"LIDO.xml").exists());
		
		action.rollback();
		assertFalse(Path.makeFile(WORK_AREA_ROOT_PATH,"work/TEST/identifier/data",_TEMP_PIP_REP_PUBLIC,"LIDO.xml").exists());
		assertFalse(Path.makeFile(WORK_AREA_ROOT_PATH,"work/TEST/identifier/data",_TEMP_PIP_REP_INSTITUTION,"LIDO.xml").exists());
		
		action.implementation();
		assertTrue(Path.makeFile(WORK_AREA_ROOT_PATH,"work/TEST/identifier/data",_TEMP_PIP_REP_PUBLIC,"LIDO.xml").exists());
		assertTrue(Path.makeFile(WORK_AREA_ROOT_PATH,"work/TEST/identifier/data",_TEMP_PIP_REP_INSTITUTION,"LIDO.xml").exists());
	}
		
	private String getLIDOURL(Document doc){
		
		return doc.getRootElement()
				.getChild("lido", LIDO_NS)
				.getChild("administrativeMetadata", LIDO_NS)
				.getChild("resourceWrap", LIDO_NS)
				.getChild("resourceSet", LIDO_NS)
				.getChild("resourceRepresentation", LIDO_NS)
				.getChild("linkResource", LIDO_NS)
				.getValue();
	}

}

