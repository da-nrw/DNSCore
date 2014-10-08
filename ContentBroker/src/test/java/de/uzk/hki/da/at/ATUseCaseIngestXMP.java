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
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.uzk.hki.da.core.C;
import de.uzk.hki.da.core.Path;
import de.uzk.hki.da.model.Object;

/**
 * @author Polina Gubaidullina
 */
public class ATUseCaseIngestXMP extends AcceptanceTest{

	private static final Namespace RDF_NS = Namespace.getNamespace("http://www.w3.org/1999/02/22-rdf-syntax-ns#");
	private static final String origName = "ATUseCaseUpdateMetadataLZA_XMP";
	private static Object object;
	private static Path contractorsPipsPublic;
	private static final File retrievalFolder = new File("/tmp/XMPunpacked");
	
	@Before
	public void setUp() throws IOException{
		object = ath.ingest(origName);
	}
	
	
	@After
	public void tearDown() throws IOException{
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
		
		String xmpFileName = "XMP.rdf";
		
		SAXBuilder builder = new SAXBuilder();
		Document doc = builder.build
				(new FileReader(Path.make(tmpObjectDirPath, bRep, xmpFileName).toFile()));
		assertTrue(getURL(doc).equals("LVR_ILR_0000008126.tif"));
	}
	
	@Test
	public void testPres() throws FileNotFoundException, JDOMException, IOException {
		contractorsPipsPublic = Path.make(localNode.getWorkAreaRootPath(),C.WA_PIPS, C.WA_PUBLIC, C.TEST_USER_SHORT_NAME);
		SAXBuilder builder = new SAXBuilder();
		Document doc = builder.build
				(new FileReader(Path.make(contractorsPipsPublic, object.getIdentifier(), "XMP.rdf").toFile()));
		assertTrue(getURL(doc).contains("http://data.danrw.de/file/"+object.getIdentifier()) && (getURL(doc).endsWith(".jpg")));
	}
	
	@Test
	public void testIndex() throws JDOMException, FileNotFoundException, IOException {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {}
		assertTrue(repositoryFacade.getIndexedMetadata("portal_ci_test", object.getIdentifier()+"-1").
				contains("Dieser Brauch zum Sankt Martinstag"));
	}
	
	public String getURL(Document doc) {
		return doc.getRootElement()
				.getChild("Description", RDF_NS)
				.getAttributeValue("about", RDF_NS);
	}
}
