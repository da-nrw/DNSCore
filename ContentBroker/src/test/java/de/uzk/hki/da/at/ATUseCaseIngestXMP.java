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

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import de.uzk.hki.da.core.C;
import de.uzk.hki.da.metadata.XMLUtils;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.util.Path;

/**
 * @author Polina Gubaidullina
 */
public class ATUseCaseIngestXMP extends AcceptanceTest{

	private static final Namespace RDF_NS = Namespace.getNamespace("http://www.w3.org/1999/02/22-rdf-syntax-ns#");
	private static final String origName = "ATUseCaseUpdateMetadataLZA_XMP";
	private static Object object;
	private static Path contractorsPipsPublic;
	private static final File retrievalFolder = new File("/tmp/XMPunpacked");
	
	@BeforeClass
	public static void setUp() throws IOException{
		object = ath.ingest(origName);
	}
	
	
	@AfterClass
	public static void tearDown() throws IOException{
		FileUtils.deleteDirectory(retrievalFolder);
	}
	
	@Test
	public void testLZA() throws FileNotFoundException, JDOMException, IOException {
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
		
		String xmpFileName = "XMP.xml";
		
		SAXBuilder builder = XMLUtils.createNonvalidatingSaxBuilder();
		Document doc = builder.build
				(new FileReader(Path.make(tmpObjectDirPath, bRep, xmpFileName).toFile()));
		assertTrue(getURL(doc).equals("LVR_ILR_0000008126.tif"));
	}
	
	@Test
	public void testPres() throws FileNotFoundException, JDOMException, IOException {
		contractorsPipsPublic = Path.make(localNode.getWorkAreaRootPath(),C.WA_PIPS, C.WA_PUBLIC, C.TEST_USER_SHORT_NAME);
		SAXBuilder builder = XMLUtils.createNonvalidatingSaxBuilder();
		Document doc = builder.build
				(new FileReader(Path.make(contractorsPipsPublic, object.getIdentifier(), "XMP.xml").toFile()));
		assertTrue(getURL(doc).contains("http://data.danrw.de/file/"+object.getIdentifier()) && (getURL(doc).endsWith(".jpg")));
	}
	
	@Test
	public void testIndex() throws JDOMException, FileNotFoundException, IOException {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {}
		assertTrue(metadataIndex.getIndexedMetadata("portal_ci_test", object.getIdentifier()+"-1").
				contains("Dieser Brauch zum Sankt Martinstag"));
	}
	
	public String getURL(Document doc) {
		return doc.getRootElement()
				.getChild("Description", RDF_NS)
				.getAttributeValue("about", RDF_NS);
	}
	
	@Test
	public void testEdmAndIndex() throws FileNotFoundException, JDOMException, IOException {
		SAXBuilder builder = XMLUtils.createNonvalidatingSaxBuilder();
		Document doc = builder.build
				(new FileReader(Path.make(contractorsPipsPublic, object.getIdentifier(), "EDM.xml").toFile()));
		String fullId = doc.getRootElement()
					.getChild("ProvidedCHO", C.EDM_NS)
					.getAttributeValue("about", C.RDF_NS);
		assertTrue(fullId.equals("http://data.danrw.de/cho/"+object.getIdentifier()+"-1"));
		String title = doc.getRootElement().getChild("ProvidedCHO", C.EDM_NS).getChild("title", C.DC_NS).getValue();
		assertTrue(title.equals("Martinsfeuer"));

//		testIndex
		assertTrue(metadataIndex.getIndexedMetadata("portal_ci_test", object.getIdentifier()+"-1").
				contains("Dieser Brauch zum Sankt Martinstag"));
	}
}
