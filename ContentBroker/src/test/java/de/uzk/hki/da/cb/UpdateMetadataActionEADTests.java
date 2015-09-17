/*
  DA-NRW Software Suite | ContentBroker
  Copyright (C) 2013 Historisch-Kulturwissenschaftliche Informationsverarbeitung
  Universität zu Köln

  This program is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.

  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.

  You should have received a copy of the GNU General Public License
  along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package de.uzk.hki.da.cb;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyObject;
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

/**
 * @author Daniel M. de Oliveira
 * @author jpeters
 * @author Polina Gubaidullina
 */
public class UpdateMetadataActionEADTests extends ConcreteActionUnitTest{
	
	@ActionUnderTest
	UpdateMetadataAction action = new UpdateMetadataAction();
	
	private static final String _TEMP_PIP_REP_PUBLIC = WorkArea.TMP_PIPS+"/public";
	private static final String _TEMP_PIP_REP_INSTITUTION = WorkArea.TMP_PIPS+"/institution";
	private static final String _1_B_REP = "1+b";
	private static MimeTypeDetectionService mtds;
	private static final Namespace METS_NS = Namespace.getNamespace("http://www.loc.gov/METS/");
	private static final Namespace XLINK_NS = Namespace.getNamespace("http://www.w3.org/1999/xlink");
	private String EAD_XPATH_EXPRESSION = "//daoloc/@href";
	private static final Path WORK_AREA_ROOT_PATH = new RelativePath("src/test/resources/cb/UpdateMetadataActionEADTests/");
	private Event event1;
	private Event event2;
	DAFile f4;
	
	@Before
	public void setUp() throws IOException, JDOMException, ParserConfigurationException, SAXException{
		n.setWorkAreaRootPath(WORK_AREA_ROOT_PATH);
		mtds = mock(MimeTypeDetectionService.class);
		when(mtds.identify((File)anyObject())).thenReturn("image/tiff");
		
		String[] repNames = {"temp_pips/public", "temp_pips/institution"};
		action.setRepNames(repNames);

		FileUtils.copyFileToDirectory(Path.make(WORK_AREA_ROOT_PATH,"work/src/mets_2_99.xml").toFile(), Path.make(WORK_AREA_ROOT_PATH,"work/TEST/identifier/data",_1_B_REP).toFile());
		FileUtils.copyFileToDirectory(Path.make(WORK_AREA_ROOT_PATH,"work/src/vda3.XML").toFile(), Path.make(WORK_AREA_ROOT_PATH,"work/TEST/identifier/data",_1_B_REP).toFile());
		DAFile f1 = new DAFile(_1_B_REP,"mets_2_99.xml");
		de.uzk.hki.da.model.Document doc1 = new de.uzk.hki.da.model.Document(f1);
		o.addDocument(doc1);
		o.getLatestPackage().getFiles().add(f1);
		
		DAFile f2 = new DAFile(_1_B_REP,"ALVR_Nr_4547_Aufn_067.tif");
		de.uzk.hki.da.model.Document doc2 = new de.uzk.hki.da.model.Document(f2);
		o.addDocument(doc2);
		o.getLatestPackage().getFiles().add(f2);
		
		DAFile f3 = new DAFile(_1_B_REP,"vda3.XML");
		de.uzk.hki.da.model.Document doc3 = new de.uzk.hki.da.model.Document(f3);
		o.addDocument(doc3);
		o.getLatestPackage().getFiles().add(f3);
		
		f4 = new DAFile(_1_B_REP,"alvr_Nr_4547_Aufn_067.tif");
		de.uzk.hki.da.model.Document doc4 = new de.uzk.hki.da.model.Document(f4);
		o.addDocument(doc4);
		o.getLatestPackage().getFiles().add(f4);
		
		event1 = new Event();
		event1.setSource_file(f2);
		event1.setTarget_file(new DAFile(_TEMP_PIP_REP_PUBLIC,"renamed067.tif"));
		event1.setType("CONVERT");
		o.getLatestPackage().getEvents().add(event1);
		
		event2 = new Event();
		event2.setSource_file(f2);
		event2.setTarget_file(new DAFile(_TEMP_PIP_REP_INSTITUTION,"renamed067.tif"));
		event2.setType("CONVERT");
		o.getLatestPackage().getEvents().add(event2);
		
		o.setPackage_type("EAD");
		o.setMetadata_file("vda3.XML");
		
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
		ps.setUrisFile("http://data.danrw.de/file");
	}
	
	@After 
	public void tearDown(){
		Path.makeFile(WORK_AREA_ROOT_PATH,"work/TEST/identifier/data",_1_B_REP,"mets_2_99.xml").delete();
		Path.makeFile(WORK_AREA_ROOT_PATH,"work/TEST/identifier/data",_1_B_REP,"vda3.XML").delete();
		Path.makeFile(WORK_AREA_ROOT_PATH,"work/TEST/identifier/data", _TEMP_PIP_REP_PUBLIC,"mets_2_99.xml").delete();
		Path.makeFile(WORK_AREA_ROOT_PATH,"work/TEST/identifier/data",_TEMP_PIP_REP_PUBLIC,"EAD.xml").delete();
		Path.makeFile(WORK_AREA_ROOT_PATH,"work/TEST/identifier/data",_TEMP_PIP_REP_INSTITUTION,"mets_2_99.xml").delete();
		Path.makeFile(WORK_AREA_ROOT_PATH,"work/TEST/identifier/data",_TEMP_PIP_REP_INSTITUTION,"EAD.xml").delete();
	}
	
	@Test
	public void test() throws IOException, JDOMException, ParserConfigurationException, SAXException {
		
		action.implementation();
		
		SAXBuilder builder = XMLUtils.createNonvalidatingSaxBuilder();
		Document doc = builder.build(new FileReader(Path.make(WORK_AREA_ROOT_PATH,"work/TEST/identifier/data",_TEMP_PIP_REP_PUBLIC,"mets_2_99.xml").toFile()));

		assertEquals("http://data.danrw.de/file/identifier/renamed067.tif", getURL(doc));
		assertEquals("image/tiff", getMimetypeInMets(doc));
		assertEquals("URL", getLoctypeInMets(doc));
	}
	
	@Test
	public void checkReplacementsInEad() throws FileNotFoundException, JDOMException, IOException, ParserConfigurationException, SAXException {
		action.implementation();
		SAXBuilder eadSaxBuilder = XMLUtils.createNonvalidatingSaxBuilder();
		Document eadDoc = eadSaxBuilder.build(new FileReader(Path.make(WORK_AREA_ROOT_PATH,"work/TEST/identifier/data",_TEMP_PIP_REP_PUBLIC,"EAD.xml").toFile()));

		List<String> eadRefs = getMetsRefsInEad(eadDoc);
		for(String ref : eadRefs) {
			assertEquals("http://data.danrw.de/file/identifier/mets_2_99.xml", ref);
		}
	}
	
	@Test
	public void testRollback() throws Exception {
		action.implementation();
		assertTrue(Path.makeFile(WORK_AREA_ROOT_PATH,"work/TEST/identifier/data",_TEMP_PIP_REP_PUBLIC,"mets_2_99.xml").exists());
		assertTrue(Path.makeFile(WORK_AREA_ROOT_PATH,"work/TEST/identifier/data",_TEMP_PIP_REP_INSTITUTION,"mets_2_99.xml").exists());
		assertTrue(Path.makeFile(WORK_AREA_ROOT_PATH,"work/TEST/identifier/data",_TEMP_PIP_REP_PUBLIC,"EAD.xml").exists());
		assertTrue(Path.makeFile(WORK_AREA_ROOT_PATH,"work/TEST/identifier/data",_TEMP_PIP_REP_INSTITUTION,"EAD.xml").exists());
		
		action.rollback();
		assertFalse(Path.makeFile(WORK_AREA_ROOT_PATH,"work/TEST/identifier/data",_TEMP_PIP_REP_PUBLIC,"mets_2_99.xml").exists());
		assertFalse(Path.makeFile(WORK_AREA_ROOT_PATH,"work/TEST/identifier/data",_TEMP_PIP_REP_INSTITUTION,"mets_2_99.xml").exists());
		assertFalse(Path.makeFile(WORK_AREA_ROOT_PATH,"work/TEST/identifier/data",_TEMP_PIP_REP_PUBLIC,"EAD.xml").exists());
		assertFalse(Path.makeFile(WORK_AREA_ROOT_PATH,"work/TEST/identifier/data",_TEMP_PIP_REP_INSTITUTION,"EAD.xml").exists());
		
		action.implementation();
		assertTrue(Path.makeFile(WORK_AREA_ROOT_PATH,"work/TEST/identifier/data",_TEMP_PIP_REP_PUBLIC,"mets_2_99.xml").exists());
		assertTrue(Path.makeFile(WORK_AREA_ROOT_PATH,"work/TEST/identifier/data",_TEMP_PIP_REP_INSTITUTION,"mets_2_99.xml").exists());
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
	
	private String getLoctypeInMets(Document doc) {
		return doc.getRootElement()
				.getChild("fileSec", METS_NS)
				.getChild("fileGrp", METS_NS)
				.getChild("file", METS_NS)
				.getChild("FLocat", METS_NS)
				.getAttributeValue("LOCTYPE");
	}
	
	private String getMimetypeInMets(Document doc) {
		return doc.getRootElement()
				.getChild("fileSec", METS_NS)
				.getChild("fileGrp", METS_NS)
				.getChild("file", METS_NS)
				.getAttribute("MIMETYPE")
				.getValue();
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
