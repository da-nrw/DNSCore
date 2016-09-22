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
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import de.uzk.hki.da.metadata.MetadataHelper;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.model.WorkArea;
import de.uzk.hki.da.utils.C;
import de.uzk.hki.da.utils.Path;
import de.uzk.hki.da.utils.XMLUtils;

/**
 * 
 * @author Polina Gubaidullina
 *
 */

public class ATMetadataUpdatesLIDO extends AcceptanceTest{

	private static final String DATA_DANRW_DE = "http://data.danrw.de";
	private static final String origName = "ATMetadataUpdatesLIDO";
	private static final File retrievalFolder = new File("/tmp/LIDOunpacked");
	private static Object object;
	private static Path contractorsPipsPublic;
	private static MetadataHelper mh = new MetadataHelper();
	
	@BeforeClass
	public static void setUp() throws IOException, InterruptedException{
		
		ath.putSIPtoIngestArea(origName, "tgz", origName);
		ath.awaitObjectState(origName,Object.ObjectStatus.ArchivedAndValidAndNotInWorkflow);
		ath.waitForDefinedPublishedState(origName);
		object=ath.getObject(origName);
		ath.waitForObjectToBeIndexed(metadataIndex,getTestIndex(),object.getIdentifier());
		
		contractorsPipsPublic = Path.make(localNode.getWorkAreaRootPath(),WorkArea.PIPS, WorkArea.PUBLIC, C.TEST_USER_SHORT_NAME);
	}
	
	@AfterClass
	public static void tearDown() throws IOException{
		FileUtils.deleteDirectory(retrievalFolder);
		Path.makeFile("tmp",object.getIdentifier()+".pack_1.tar").delete(); // retrieved dip
	}
	
	@Test
	public void testLZA() throws FileNotFoundException, JDOMException, IOException {
		
		ath.retrieveAIP(object,retrievalFolder,"1");
		System.out.println("object identifier: "+object.getIdentifier());
		
		Path tmpObjectDirPath = Path.make(retrievalFolder.getAbsolutePath(), "data");	
		File[] tmpObjectSubDirs = new File (tmpObjectDirPath.toString()).listFiles();
		String bRep = "";
		
		for (int i=0; i<tmpObjectSubDirs.length; i++) {
			if(tmpObjectSubDirs[i].getName().contains("+b")) {
				bRep = tmpObjectSubDirs[i].getName();
			}
		}
		
		SAXBuilder builder = XMLUtils.createNonvalidatingSaxBuilder();
		String LidoFileName = "LIDO-Testexport2014-07-04-FML-Auswahl.xml";
		Document doc = builder.build
				(new FileReader(Path.make(tmpObjectDirPath, bRep, LidoFileName).toFile()));
		
		List<String> lidoUrls =  mh.getLIDOURL(doc);
		
		Boolean pic1Exists = false;
		Boolean pic2Exists = false;
		
		for(String url : lidoUrls) {
			if(url.equals("Picture1.tif")) {
				pic1Exists = true;
			}
			if(url.equals("Picture2.tif")) {
				pic2Exists = true;
			}
		}
		
		assertTrue(pic1Exists);
		assertTrue(pic2Exists);

	}
	
	@Test
	public void testPres() throws FileNotFoundException, JDOMException, IOException{
		
		FileReader frLido = new FileReader(Path.make(contractorsPipsPublic, object.getIdentifier(), "LIDO.xml").toFile());
		SAXBuilder lidoSaxBuilder = XMLUtils.createNonvalidatingSaxBuilder();
		Document doc = lidoSaxBuilder.build(frLido);
		
		List<String> lidoUrls =  mh.getLIDOURL(doc);
		int danrwRewritings = 0;
		for(String url : lidoUrls) {
			if(url.contains(DATA_DANRW_DE)) {
				danrwRewritings++;
			}
		}
		assertTrue(danrwRewritings==2);	
		frLido.close();
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testEdmAndIndex() throws FileNotFoundException, JDOMException, IOException {
		
		FileReader frLido = new FileReader(Path.make(contractorsPipsPublic, object.getIdentifier(),"EDM.xml").toFile());
		SAXBuilder lidoSaxBuilder = XMLUtils.createNonvalidatingSaxBuilder();
		Document doc = lidoSaxBuilder.build(frLido);
	
		List<Element> providetCho = doc.getRootElement().getChildren("ProvidedCHO", C.EDM_NS);
		Boolean testProvidetCho1Exists = false;
		Boolean testProvidetCho2Exists = false;
		for(Element pcho : providetCho) {
			if(pcho.getAttributeValue("about", C.RDF_NS).contains(object.getIdentifier()+"-ISIL/lido/Inventarnummer-1")) {
				testProvidetCho1Exists = true;
				assertTrue(pcho.getChild("title", C.DC_NS).getValue().equals("Nudelmaschine in Originalverpackung"));
				assertTrue(pcho.getChild("date", C.DC_NS).getValue().equals("01.01.1970-31.12.1989"));
				assertTrue(pcho.getChild("hasType", C.EDM_NS).getValue().equals("is root element"));
			} else if(pcho.getAttributeValue("about", C.RDF_NS).contains(object.getIdentifier()+"-ISIL/lido/Inventarnummer-2")){
				testProvidetCho2Exists = true;
				assertTrue(pcho.getChild("title", C.DC_NS).getValue().equals("Küchenmaschine"));
				assertTrue(pcho.getChild("date", C.DC_NS).getValue().contains("01.01.1950-31.12.1959"));
				assertTrue(pcho.getChild("hasType", C.EDM_NS).getValue().equals("is root element"));
			}
			
			List<Element> identifier = pcho.getChildren("identifier", C.DC_NS);
			Boolean objIdExists = false;
			Boolean urnExists = false;
			for(Element id : identifier) {
				if(id.getValue().equals(object.getUrn())) {
					urnExists = true;
				} else if(id.getValue().equals(object.getIdentifier())) {
					objIdExists = true;
				}
			}
			assertTrue(objIdExists && urnExists);
		}
		
		List<Element> aggregation = doc.getRootElement().getChildren("Aggregation", C.ORE_NS);
		Boolean testAggr1Exists = false;
		Boolean testAggr2Exists = false;
		for(Element a : aggregation) {
			if(a.getAttributeValue("about", C.RDF_NS).contains(object.getIdentifier()+"-ISIL/lido/Inventarnummer-1")) {
				testAggr1Exists = true;
				assertTrue(a.getChild("isShownBy", C.EDM_NS).getAttributeValue("resource", C.RDF_NS)
						.contains("http://data.danrw.de/file/"+object.getIdentifier()+"/_c8079103e5eecf45d2978a396e1839a9.jpg"));
				assertTrue(a.getChild("object", C.EDM_NS).getAttributeValue("resource", C.RDF_NS)
						.contains("http://data.danrw.de/file/"+object.getIdentifier()+"/_c8079103e5eecf45d2978a396e1839a9.jpg"));
			} else if(a.getAttributeValue("about", C.RDF_NS).contains(object.getIdentifier()+"-ISIL/lido/Inventarnummer-2")){
				testAggr2Exists = true;
				assertTrue(a.getChild("isShownBy", C.EDM_NS).getAttributeValue("resource", C.RDF_NS)
						.contains("http://data.danrw.de/file/"+object.getIdentifier()+"/_c3836acf068a9b227834e0adda226ac2.jpg"));
				assertTrue(a.getChild("object", C.EDM_NS).getAttributeValue("resource", C.RDF_NS)
						.contains("http://data.danrw.de/file/"+object.getIdentifier()+"/_c3836acf068a9b227834e0adda226ac2.jpg"));
			}
		}
		
		assertTrue(testProvidetCho1Exists&&testProvidetCho2Exists);
		assertTrue(testAggr1Exists&&testAggr2Exists);
		
//		testIndex
		assertTrue(metadataIndex.getIndexedMetadata(getTestIndex(), object.getIdentifier()+"-ISIL/lido/Inventarnummer-1").contains("Nudelmaschine in Originalverpackung"));
		
		frLido.close();
	}
}
	
	