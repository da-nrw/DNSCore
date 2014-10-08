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
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.FileUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xml.sax.SAXException;

import de.uzk.hki.da.core.Path;
import de.uzk.hki.da.ff.MimeTypeDetectionService;
import de.uzk.hki.da.metadata.XMLUtils;
import de.uzk.hki.da.model.DAFile;
import de.uzk.hki.da.model.Event;
import de.uzk.hki.da.model.Job;
import de.uzk.hki.da.model.Node;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.model.PreservationSystem;
import de.uzk.hki.da.test.TC;
import de.uzk.hki.da.test.TESTHelper;


/**
 * The Class UpdateMetadataActionTests.
 */
public class UpdateMetadataActionTests {
	
	private static final String REP_NAME = "rep+a";

	private static MimeTypeDetectionService mtds;
	
	private static final Path workAreaPath = Path.make(TC.TEST_ROOT_CB,"UpdateMetadataActionTests/");
	
	/** The Constant METS_NS. */
	private static final Namespace METS_NS = Namespace.getNamespace("http://www.loc.gov/METS/");
	
	/** The Constant XLINK_NS. */
	private static final Namespace XLINK_NS = Namespace.getNamespace("http://www.w3.org/1999/xlink");
	
	/** The action. */
	private UpdateMetadataAction action;
	
	/** The node. */
	private Node node;
	
	/**
	 * Sets the up.
	 * @throws IOException 
	 */
	
	@BeforeClass
	public static void mockDca() throws IOException {
		mtds = mock(MimeTypeDetectionService.class);
		when(mtds.detectMimeType((DAFile)anyObject())).thenReturn("image/tiff");
	}
	
	@Before
	public void setUp() throws IOException {
		PreservationSystem pSystem = new PreservationSystem();
		pSystem.setUrisFile("http://data.danrw.de/file");
		
		action = new UpdateMetadataAction();		
		node = new Node();
		node.setWorkingResource("vm3");
		node.setWorkAreaRootPath(Path.make(workAreaPath));
		action.setLocalNode(node);
		action.setPSystem(pSystem);
		action.setPresMode(true);
		
		FileUtils.copyDirectoryToDirectory(new File("src/main/xslt"), new File("conf/"));
	}
	
	@After
	public void tearDown () throws IOException {
		new File(workAreaPath + "/work/TEST/23/data/pips/institution/DC.xml").delete();
		new File(workAreaPath + "/work/TEST/23/data/pips/institution/mets.xml").delete();
		new File(workAreaPath + "/work/TEST/23/data/pips/public/DC.xml").delete();
		new File(workAreaPath + "/work/TEST/23/data/pips/public/mets.xml").delete();
		new File(workAreaPath + "/work/TEST/42/data/DC_.xml").delete();
		FileUtils.deleteDirectory(new File(workAreaPath + "/TEST/42/data/pips"));
		FileUtils.deleteDirectory(new File("conf/xslt"));
	}
	
	
	
	/**
	 * Test write package type to dc.
	 *
	 * @throws FileNotFoundException the file not found exception
	 * @throws JDOMException the jDOM exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@Test
	public void testWritePackageTypeToDC() throws FileNotFoundException, JDOMException, IOException {
		
		Object o = TESTHelper.setUpObject("42",workAreaPath);
		
		o.getLatestPackage().getFiles().add(new DAFile(o.getLatestPackage(), "pips/public", "DC.xml"));
		o.getLatestPackage().getFiles().add(new DAFile(o.getLatestPackage(), "pips/institution", "DC.xml"));
		
		Job job = new Job();
		job.setRep_name("rep42");
		job.setObject(o);
		
		action.setMtds(mtds);
		action.setObject(o);
		action.setJob(job);
		
		action.setRepNames(new String[]{"pips/public", "pips/institution"});

		String dcPath = o.getDataPath() +"/"+ "test_dc.xml";
		File dcFile = new File(dcPath);
		File publicDcFile = new File(o.getDataPath() + "/pips/public/DC.xml");
		File instDcFile = new File(o.getDataPath() + "/pips/institution/DC.xml");
		FileUtils.copyFile(dcFile, publicDcFile);
		FileUtils.copyFile(dcFile, instDcFile);
		
		action.writePackageTypeToDC("TEST");
		
		SAXBuilder builder = XMLUtils.createNonvalidatingSaxBuilder();
		Document doc = builder.build(new FileReader(publicDcFile));
		Element child = doc.getRootElement()
				.getChild("format", Namespace.getNamespace("http://purl.org/dc/elements/1.1/"));
		
		assertNotNull(child);
		assertEquals("TEST", child.getText());
		
		doc = builder.build(new FileReader(instDcFile));
		child = doc.getRootElement().getChild("format", Namespace.getNamespace("http://purl.org/dc/elements/1.1/"));
		
		assertNotNull(child);
		assertEquals("TEST", child.getText());
		
		publicDcFile.delete();
		instDcFile.delete();
		
	}
	
	/**
	 * Test update paths in metadata.
	 *
	 * @throws FileNotFoundException the file not found exception
	 * @throws JDOMException the jDOM exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws SAXException 
	 * @throws ParserConfigurationException 
	 */
	@Test
	public void testUpdatePathsInMetadata() throws FileNotFoundException, JDOMException, IOException, ParserConfigurationException, SAXException {
		
		Object obj = TESTHelper.setUpObject("23",workAreaPath);

		DAFile t1 = new DAFile(obj.getLatestPackage(), "pips/public", "Ye_old_duckroll.jpg");
		DAFile t2 = new DAFile(obj.getLatestPackage(), "pips/institution", "Ye_old_duckroll.jpg");
		DAFile s = new DAFile(obj.getLatestPackage(), REP_NAME, "tif/enne09=v0001.tif");
		DAFile m = new DAFile(obj.getLatestPackage(), REP_NAME, "mets.xml");
		
		Event event = new Event();
		event.setType("CONVERT");
		event.setSource_file(s);
		event.setTarget_file(t1);
		
		Event event2 = new Event();
		event2.setType("CONVERT");
		event2.setSource_file(s);
		event2.setTarget_file(t2);
		
		obj.getLatestPackage().getEvents().add(event);		
		obj.getLatestPackage().getEvents().add(event2);
		
		obj.getLatestPackage().getFiles().add(t1);
		obj.getLatestPackage().getFiles().add(t2);
		obj.getLatestPackage().getFiles().add(s);
		obj.getLatestPackage().getFiles().add(m);
		
		obj.setOrig_name("test");
		
		Job job = new Job();
		job.setObject(obj);
		job.setRep_name("rep42");

		String metsPath = Path.make( obj.getDataPath(), REP_NAME,"mets.xml").toString();
		File metsFile = new File(metsPath);
		File publicMetsFile = new File(obj.getDataPath() + "/pips/public/mets.xml");
		File instMetsFile = new File(obj.getDataPath() + "/pips/institution/mets.xml");
		FileUtils.copyFile(metsFile, publicMetsFile);
		FileUtils.copyFile(metsFile, instMetsFile);
		
		action.setMtds(mtds);
		action.setObject(obj);
		action.setJob(job);
		
		obj.setMetadata_file("mets.xml");
		obj.setPackage_type("METS");
		
		HashMap<String,String> xpaths = new HashMap<String,String>();
		xpaths.put("METS", "//mets:file");
		action.setXpathsToUrls(xpaths);
		
		HashMap<String, String> nsMap = new HashMap<String,String>();
		nsMap.put("mets", METS_NS.getURI());
		nsMap.put("xlink", XLINK_NS.getURI());
		action.setNamespaces(nsMap);
		
		action.setRepNames(new String[]{"pips/public", "pips/institution"});
		Map<String, String> dcMappings = new HashMap<String,String>();
		dcMappings.put("METS", "conf/xslt/dc/mets-mods_to_dc.xsl");
		action.setDcMappings(dcMappings);
		
		action.implementation();
		
		SAXBuilder builder = XMLUtils.createNonvalidatingSaxBuilder();
		Document doc = builder.build(new FileReader(new File(workAreaPath + "/work/TEST/23/data/pips/public/mets.xml")));

		String url = doc.getRootElement()
				.getChild("fileSec", METS_NS)
				.getChild("fileGrp", METS_NS)
				.getChild("file", METS_NS)
				.getChild("FLocat", METS_NS)
				.getAttributeValue("href", XLINK_NS);
		
		assertEquals("http://data.danrw.de/file/23/Ye_old_duckroll.jpg", url);
		
		doc = builder.build(new FileReader(new File(workAreaPath + "/work/TEST/23/data/pips/institution/mets.xml")));

		url = doc.getRootElement()
				.getChild("fileSec", METS_NS)
				.getChild("fileGrp", METS_NS)
				.getChild("file", METS_NS)
				.getChild("FLocat", METS_NS)
				.getAttributeValue("href", XLINK_NS);
		
		assertEquals("http://data.danrw.de/file/23/Ye_old_duckroll.jpg", url);
		
		publicMetsFile.delete();
		instMetsFile.delete();
		
	}
	
}
