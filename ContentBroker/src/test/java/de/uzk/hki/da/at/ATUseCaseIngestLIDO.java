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
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import de.uzk.hki.da.core.C;
import de.uzk.hki.da.metadata.MetadataHelper;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.util.Path;

/**
 * 
 * @author Polina Gubaidullina
 *
 */

public class ATUseCaseIngestLIDO extends AcceptanceTest{

	private static final String DATA_DANRW_DE = "http://data.danrw.de";
	private static final String origName = "ATUseCaseUpdateMetadataLZA_LIDO";
	private static final File retrievalFolder = new File("/tmp/LIDOunpacked");
	private static Object object;
	private static Path contractorsPipsPublic;
	private static MetadataHelper mh = new MetadataHelper();
	
	@BeforeClass
	public static void setUp() throws IOException{
		object = ath.ingest(origName);
		contractorsPipsPublic = Path.make(localNode.getWorkAreaRootPath(),C.WA_PIPS, C.WA_PUBLIC, C.TEST_USER_SHORT_NAME);
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
		
		SAXBuilder builder = new SAXBuilder();
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
		
		SAXBuilder builder = new SAXBuilder();
		
		Document doc = builder.build
				(new FileReader(Path.make(contractorsPipsPublic, object.getIdentifier(), "LIDO.xml").toFile()));
		
		List<String> lidoUrls =  mh.getLIDOURL(doc);
		int danrwRewritings = 0;
		for(String url : lidoUrls) {
			if(url.contains(DATA_DANRW_DE)) {
				danrwRewritings++;
			}
		}
		
		assertTrue(danrwRewritings==2);		
	}
	
	@Test
	public void testIndex() {
		assertTrue(repositoryFacade.getIndexedMetadata("portal_ci_test", "Inventarnummer").contains("\"edm:provider\":\"DA-NRW - Digitales Archiv Nordrhein-Westfalen\""));
	}
}
	
