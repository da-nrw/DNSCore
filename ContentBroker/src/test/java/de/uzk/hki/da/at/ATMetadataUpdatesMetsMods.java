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

import org.apache.commons.io.FilenameUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.uzk.hki.da.metadata.MetadataHelper;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.model.WorkArea;
import de.uzk.hki.da.utils.C;
import de.uzk.hki.da.utils.FolderUtils;
import de.uzk.hki.da.utils.Path;
import de.uzk.hki.da.utils.XMLUtils;



/**
 * @author Eugen Trebunski
 * @author Polina Gubaidullina
 * @author Daniel M. de Oliveira
 */

public class ATMetadataUpdatesMetsMods extends AcceptanceTest{
	
	private String PORTAL_CI_TEST = getTestIndex();
	private static final File retrievalFolder = new File("/tmp/unpackedMetsMods");
	private static Path contractorsPipsPublic;
	private static final String origName = "ATMetadataUpdates_METS";
	private static Object object;
	private static Document metsDoc;
	MetadataHelper mh = new MetadataHelper();
	
	
	@BeforeClass
	public static void setUp() throws IOException{
		ath.putSIPtoIngestArea(origName, "tgz", origName);
		ath.awaitObjectState(origName,Object.ObjectStatus.ArchivedAndValidAndNotInWorkflow);
		ath.waitForDefinedPublishedState(origName);
		object=ath.getObject(origName);
		ath.waitForObjectToBeIndexed(metadataIndex,getTestIndex(),object.getIdentifier());
		contractorsPipsPublic = Path.make(localNode.getWorkAreaRootPath(),WorkArea.PIPS, WorkArea.PUBLIC, C.TEST_USER_SHORT_NAME);
	}
	
	@AfterClass
	public static void tearDown() throws IOException{
		FolderUtils.deleteDirectorySafe(retrievalFolder);
		Path.makeFile("tmp",object.getIdentifier()+".pack_1.tar").delete(); // retrieved dip
	}
	
	@Test
	public void testLZA() throws Exception{
		ath.retrieveAIP(object,retrievalFolder,"1");	
		
		Path tmpObjectDirPath = Path.make(retrievalFolder.getAbsolutePath(), "data");	
		File[] tmpObjectSubDirs = new File (tmpObjectDirPath.toString()).listFiles();
		String bRep = "";
		
		for (int i=0; i<tmpObjectSubDirs.length; i++) {
			if(tmpObjectSubDirs[i].getName().contains("+b")) {
				bRep = tmpObjectSubDirs[i].getName();
			}
		}
		
		SAXBuilder builder = XMLUtils.createNonvalidatingSaxBuilder();
		String metsFileName = "export_mets.xml";
		Document doc = builder.build
				(new FileReader(Path.make(tmpObjectDirPath, bRep, metsFileName).toFile()));
		List<Element> elements = mh.getMetsFileElements(doc);
		for(Element e : elements) {
			assertTrue(mh.getMetsHref(e).contains(".tif"));
			assertTrue(mh.getMimetypeInMets(e).equals("image/tiff"));
		}
	}
	
	@Test
	public void testPres() throws JDOMException, FileNotFoundException, IOException {
		
		assertEquals(C.CB_PACKAGETYPE_METS,object.getPackage_type());
		
		SAXBuilder builder = XMLUtils.createNonvalidatingSaxBuilder();
		metsDoc = builder.build
			(new FileReader(
				Path.make(contractorsPipsPublic, 
					object.getIdentifier(), C.CB_PACKAGETYPE_METS+C.FILE_EXTENSION_XML).toFile()));
		List<Element> elements = mh.getMetsFileElements(metsDoc);
		for(Element e : elements) {
			assertTrue(mh.getMetsHref(e).contains("http://data.danrw.de/"));
			assertTrue(mh.getMimetypeInMets(e).equals(C.MIMETYPE_IMAGE_JPEG));
			assertTrue(mh.getMetsLoctype(e).equals("URL"));
			assertTrue(Path.make(contractorsPipsPublic, 
					object.getIdentifier(), FilenameUtils.getName(mh.getMetsHref(e))).toFile().exists());
			
		
		}
	}
	
	@Test
	public void testEdmAndIndex() throws FileNotFoundException, JDOMException, IOException {

		SAXBuilder builder = XMLUtils.createNonvalidatingSaxBuilder();
		Document doc = builder.build
				(new FileReader(Path.make(contractorsPipsPublic, object.getIdentifier(), "EDM.xml").toFile()));
		@SuppressWarnings("unchecked")
		List<Element> providetCho = doc.getRootElement().getChildren("ProvidedCHO", C.EDM_NS);
		Boolean testProvidetChoExists = false;
		for(Element pcho : providetCho) {
			if(pcho.getChild("title", C.DC_NS).getValue().equals("nonSortText"+" "+"Text// mahels///Titel"+" : "+"Untertitel")) {
				testProvidetChoExists = true;
				assertTrue(pcho.getChild("date", C.DC_NS).getValue().equals("1523"));
				assertTrue(pcho.getChild("hasType", C.EDM_NS).getValue().equals("is root element"));
			}
			@SuppressWarnings("unchecked")
			List<Element> identifier = pcho.getChildren("identifier", C.DC_NS);
			Boolean objIdExists = false;
			Boolean urnExists = false;
			Boolean hbzIdnExists = false;
			Boolean metsUrnExists = false;
			int urnCount = 0;
			for(Element id : identifier) {
				if(id.getValue().equals(object.getUrn())) {
					urnExists = true;
					urnCount++;
				} else if(id.getValue().equals(object.getIdentifier())) {
					objIdExists = true;
				} else if(id.getValue().equals("id42")) {
					hbzIdnExists = true;
				} 
			}
			if(urnCount==2) {
				metsUrnExists = true;
			}
			assertTrue(objIdExists);
			assertTrue(urnExists);
			assertTrue(hbzIdnExists);
			assertTrue(metsUrnExists);
		}
		
		assertTrue(testProvidetChoExists);
		assertTrue(doc.getRootElement().getChild("Aggregation", C.ORE_NS).getChild("isShownBy", C.EDM_NS).getAttributeValue("resource", C.RDF_NS)
				.contains("http://data.danrw.de/file/"+object.getIdentifier()+"/_bee84f142bba34a1036ecc4667b54615.jpg"));
		assertTrue(doc.getRootElement().getChild("Aggregation", C.ORE_NS).getChild("object", C.EDM_NS).getAttributeValue("resource", C.RDF_NS)
				.contains("http://data.danrw.de/file/"+object.getIdentifier()+"/_bee84f142bba34a1036ecc4667b54615.jpg"));
		
//		testIndex
		assertTrue(metadataIndex.getIndexedMetadata(PORTAL_CI_TEST, object.getIdentifier()+"-md801613").contains("nonSortText Text// mahels///Titel : Untertitel"));
	}
	
	@Test
	public void testUserSuppliedUrn() {
		assertTrue(object.getUrn().equals("urn:nbn:de:hbz:42"));
	}
}
