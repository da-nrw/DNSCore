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
import org.junit.BeforeClass;
import org.junit.Test;
import org.xml.sax.SAXException;

import de.uzk.hki.da.format.MimeTypeDetectionService;
import de.uzk.hki.da.metadata.XMLUtils;
import de.uzk.hki.da.model.DAFile;
import de.uzk.hki.da.model.Event;
import de.uzk.hki.da.model.Job;
import de.uzk.hki.da.model.Node;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.model.PreservationSystem;
import de.uzk.hki.da.model.User;
import de.uzk.hki.da.test.TESTHelper;
import de.uzk.hki.da.util.Path;
import de.uzk.hki.da.util.RelativePath;

/**
 * @author Daniel M. de Oliveira
 * @author jpeters
 * @author Polina Gubaidullina
 */
public class UpdateMetadataActionEADTests {
	
	private static final String _1_B_REP = "1+b";
	private static final String _1_A_REP = "1+a";
	private static MimeTypeDetectionService mtds;
	private static final Namespace METS_NS = Namespace.getNamespace("http://www.loc.gov/METS/");
	private static final Namespace XLINK_NS = Namespace.getNamespace("http://www.w3.org/1999/xlink");
	private String EAD_XPATH_EXPRESSION = "//daoloc/@href";
	private static final Path workAreaRootPathPath = new RelativePath("src/test/resources/cb/UpdateMetadataActionEADTests/");
	private static final UpdateMetadataAction action = new UpdateMetadataAction();
	private Event event;
	private Object object;
	private PreservationSystem pSystem;
	private Node n;
	DAFile f4;
	
	@BeforeClass
	public static void mockDca() throws IOException {
		mtds = mock(MimeTypeDetectionService.class);
		when(mtds.identify((File)anyObject())).thenReturn("image/tiff");
	}
	
	@Before
	public void setUp() throws IOException, JDOMException, ParserConfigurationException, SAXException{
		
		pSystem = new PreservationSystem();
		pSystem.setId(1);
		pSystem.setMinRepls(0);
		User psadmin = new User();
		psadmin.setShort_name("TEST_PSADMIN");
		psadmin.setEmailAddress("noreply");
		pSystem.setAdmin(psadmin);
		pSystem.setUrisFile("http://data.danrw.de/file");
		
		n = new Node();
		n.setName("testnode");
		n.setAdmin(psadmin);
		pSystem.getNodes().add(n);
		
		action.setLocalNode(n);
		action.setPSystem(pSystem);
		
		object = TESTHelper.setUpObject("42",workAreaRootPathPath);

		FileUtils.copyFileToDirectory(Path.make(workAreaRootPathPath,"work/src/mets_2_99.xml").toFile(), Path.make(workAreaRootPathPath,"work/TEST/42/data",_1_A_REP).toFile());
		FileUtils.copyFileToDirectory(Path.make(workAreaRootPathPath,"work/src/vda3.XML").toFile(), Path.make(workAreaRootPathPath,"work/TEST/42/data",_1_A_REP).toFile());
		DAFile f1 = new DAFile(object.getLatestPackage(),_1_A_REP,"mets_2_99.xml");
		de.uzk.hki.da.model.Document doc1 = new de.uzk.hki.da.model.Document(f1);
		object.addDocument(doc1);
		object.getLatestPackage().getFiles().add(f1);
		
		DAFile f2 = new DAFile(object.getLatestPackage(),_1_A_REP,"ALVR_Nr_4547_Aufn_067.tif");
		de.uzk.hki.da.model.Document doc2 = new de.uzk.hki.da.model.Document(f2);
		object.addDocument(doc2);
		object.getLatestPackage().getFiles().add(f2);
		
		DAFile f3 = new DAFile(object.getLatestPackage(),_1_A_REP,"vda3.XML");
		de.uzk.hki.da.model.Document doc3 = new de.uzk.hki.da.model.Document(f3);
		object.addDocument(doc3);
		object.getLatestPackage().getFiles().add(f3);
		
		f4 = new DAFile(object.getLatestPackage(),_1_A_REP,"alvr_Nr_4547_Aufn_067.tif");
		de.uzk.hki.da.model.Document doc4 = new de.uzk.hki.da.model.Document(f4);
		object.addDocument(doc4);
		object.getLatestPackage().getFiles().add(f4);
		
		event = new Event();
		event.setSource_file(f2);
		event.setTarget_file(new DAFile(object.getLatestPackage(),_1_B_REP,"renamed067.tif"));
		event.setType("CONVERT");
		object.getLatestPackage().getEvents().add(event);
		
		Job job = new Job(); job.setObject(object); job.setId(1);
		object.setPackage_type("EAD");
		object.setMetadata_file("vda3.XML");
		
		HashMap<String,String> xpaths = new HashMap<String,String>();
		xpaths.put("METS", "//mets:file");
		xpaths.put("EAD", "//daoloc/@href");
		action.setXpathsToUrls(xpaths);
		HashMap<String, String> nsMap = new HashMap<String,String>();
		nsMap.put("mets", METS_NS.getURI());
		nsMap.put("xlink", XLINK_NS.getURI());
		action.setNamespaces(nsMap);
		
		action.setMtds(mtds);
		action.setJob(job);
		action.setPSystem(pSystem);
		action.setPresMode(true);
	
	}
	
	@After 
	public void tearDown(){
		Path.makeFile(workAreaRootPathPath,"work/TEST/42/data",_1_A_REP,"mets_2_99.xml").delete();
		Path.makeFile(workAreaRootPathPath,"work/TEST/42/data",_1_A_REP,"vda3.XML").delete();
		Path.makeFile(workAreaRootPathPath,"work/TEST/42/data",_1_B_REP,"mets_2_99.xml").delete();
		Path.makeFile(workAreaRootPathPath,"work/TEST/42/data",_1_B_REP,"vda3.XML").delete();
	}
	
	
	@Test
	public void test() throws IOException, JDOMException, ParserConfigurationException, SAXException {
		
		action.setObject(object);
		action.implementation();
		
		SAXBuilder builder = new SAXBuilder();
		Document doc = builder.build(new FileReader(Path.make(workAreaRootPathPath,"work/TEST/42/data",_1_B_REP,"mets_2_99.xml").toFile()));

		assertEquals("http://data.danrw.de/file/42/renamed067.tif", getURL(doc));
		assertEquals("image/tiff", getMimetypeInMets(doc));
		assertEquals("URL", getLoctypeInMets(doc));
	}
	
	@Test
	public void checkReplacementsInEad() throws FileNotFoundException, JDOMException, IOException, ParserConfigurationException, SAXException {
		
		action.setObject(object);
		action.implementation();
		
		SAXBuilder eadSaxBuilder = XMLUtils.createNonvalidatingSaxBuilder();
		Document eadDoc = eadSaxBuilder.build(new FileReader(Path.make(workAreaRootPathPath,"work/TEST/42/data",_1_B_REP,"vda3.XML").toFile()));

		List<String> eadRefs = getMetsRefsInEad(eadDoc);
		for(String ref : eadRefs) {
			assertEquals("http://data.danrw.de/file/42/mets_2_99.xml", ref);
		}
	}
	
//	@Test
//	public void upperLowerCaseMismatch() throws IOException, JDOMException, ParserConfigurationException, SAXException {
//		
//		event.setSource_file(f4);
//		
//		try{
//			action.setObject(object);
//			action.implementation();
//			fail();
//		} catch(Error e){
//			System.out.println(e.getMessage());
//			assertTrue(e.getMessage().equals(
//					"1 unreferenced file(s) have been converted! Missing reference(s) to [alvr_Nr_4547_Aufn_067.tif]. "
//					+ "Executed conversions: {[1+a]/[alvr_Nr_4547_Aufn_067.tif]=[1+b]/[renamed067.tif]}"));
//		}
//	}
	
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
