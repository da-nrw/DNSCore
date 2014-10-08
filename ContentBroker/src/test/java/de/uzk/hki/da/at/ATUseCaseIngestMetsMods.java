/*
  DA-NRW Software Suite | ContentBroker
  Copyright (C) 2014 LVRInfoKom
  Landschaftsverband Rheinland

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

package de.uzk.hki.da.at;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.jdom.xpath.XPath;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.uzk.hki.da.core.C;
import de.uzk.hki.da.core.Path;
import de.uzk.hki.da.model.Object;



/**
 * @author Polina Gubaidullina
 * @author Daniel M. de Oliveira
 */

public class ATUseCaseIngestMetsMods extends AcceptanceTest{
	
	private static final Namespace METS_NS = Namespace.getNamespace("http://www.loc.gov/METS/");
	private static final Namespace XLINK_NS = Namespace.getNamespace("http://www.w3.org/1999/xlink");
	private static final String PORTAL_CI_TEST = "portal_ci_test";
	private static final File retrievalFolder = new File("/tmp/unpackedMetsMods");
	private static Path testContractorPipsPublic;
	private static final String origName = "ATUseCaseUpdateMetadataLZA_METS";
	private static Object object;
	private String METS_XPATH_EXPRESSION = 		"//mets:file";
	private static Document metsDoc;
	
	
	@Before
	public void setUpBeforeClass() throws IOException{
		object = ath.ingest(origName);
	}
	
	@After
	public void tearDownAfterClass() throws IOException{
		FileUtils.deleteDirectory(retrievalFolder);
	}
	
	@Test
	public void testLZA() throws Exception{
		ath.retrievePackage(object,retrievalFolder,"1");
		System.out.println("object identifier: "+object.getIdentifier());		
		
		Path tmpObjectDirPath = Path.make(retrievalFolder.getAbsolutePath(), "data");	
		File[] tmpObjectSubDirs = new File (tmpObjectDirPath.toString()).listFiles();
		String bRep = "";
		
		for (int i=0; i<tmpObjectSubDirs.length; i++) {
			if(tmpObjectSubDirs[i].getName().contains("+b")) {
				bRep = tmpObjectSubDirs[i].getName();
			}
		}
		
		SAXBuilder builder = new SAXBuilder();
		String metsFileName = "export_mets.xml";
		Document doc = builder.build
				(new FileReader(Path.make(tmpObjectDirPath, bRep, metsFileName).toFile()));
		checkReferencesAndMimetype(doc, ".tif", "image/tiff", null);
	}
	
	@Test
	public void testPres() throws JDOMException, FileNotFoundException, IOException {
		testContractorPipsPublic = Path.make(localNode.getWorkAreaRootPath(),C.WA_PIPS, C.WA_PUBLIC, "TEST");
		
		assertEquals(C.CB_PACKAGETYPE_METS,object.getPackage_type());
		
		metsDoc = new SAXBuilder().build
			(new FileReader(
				Path.make(testContractorPipsPublic, 
					object.getIdentifier(), C.CB_PACKAGETYPE_METS+C.FILE_EXTENSION_XML).toFile()));
		
		checkReferencesAndMimetype(metsDoc, "http://data.danrw.de/", C.MIMETYPE_IMAGE_JPEG, "URL");
	}
	
	@Test
	public void checkIndex(){
		testContractorPipsPublic = Path.make(localNode.getWorkAreaRootPath(),C.WA_PIPS, C.WA_PUBLIC, "TEST");
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {}
		String abc = repositoryFacade.getIndexedMetadata(PORTAL_CI_TEST, object.getIdentifier()+"-md801613");
		assertTrue(abc.contains("ULB (Stadt) [Electronic ed.]"));
	}
	
	public void checkReferencesAndMimetype(Document doc, String href, String mimetype, String loctype) throws JDOMException, FileNotFoundException, IOException {
		XPath xPath = XPath.newInstance(METS_XPATH_EXPRESSION);
		
		@SuppressWarnings("rawtypes")
		List allNodes = xPath.selectNodes(doc);
		
		for (java.lang.Object node : allNodes) {
			Element fileElement = (Element) node;
			Attribute attr = fileElement.getChild("FLocat", METS_NS).getAttribute("href", XLINK_NS);
			Attribute attrLoctype = fileElement.getChild("FLocat", METS_NS).getAttribute("LOCTYPE");
			Attribute attrMT = fileElement.getAttribute("MIMETYPE");
			assertTrue(attr.getValue().contains(href));
			assertTrue(attrMT.getValue().equals(mimetype));
			if(loctype!=null) {
				assertTrue(attrLoctype.getValue().equals(loctype));
			}
		}
	}
}
