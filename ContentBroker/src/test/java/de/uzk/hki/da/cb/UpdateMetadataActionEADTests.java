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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.uzk.hki.da.core.UserException;
import de.uzk.hki.da.model.DAFile;
import de.uzk.hki.da.model.Event;
import de.uzk.hki.da.model.Job;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.service.MimeTypeDetectionService;
import de.uzk.hki.da.utils.Path;
import de.uzk.hki.da.utils.RelativePath;
import de.uzk.hki.da.utils.TESTHelper;

/**
 * @author Daniel M. de Oliveira
 * @author jpeters
 */
public class UpdateMetadataActionEADTests {
	
	private static MimeTypeDetectionService mtds;
	private static final Namespace METS_NS = Namespace.getNamespace("http://www.loc.gov/METS/");
	private static final Namespace XLINK_NS = Namespace.getNamespace("http://www.w3.org/1999/xlink");
	private static final Path workAreaRootPathPath = new RelativePath("src/test/resources/cb/UpdateMetadataActionEADTests/");
	private static final UpdateMetadataAction action = new UpdateMetadataAction();
	private Event event;
	private Object object;
	
	@BeforeClass
	public static void mockDca() throws IOException {
		mtds = mock(MimeTypeDetectionService.class);
		when(mtds.detectMimeType((DAFile)anyObject())).thenReturn("image/tiff");
	}
	
	@Before
	public void setUp() throws IOException{
		object = TESTHelper.setUpObject("42",workAreaRootPathPath);

		FileUtils.copyFileToDirectory(Path.make(workAreaRootPathPath,"work/src/mets_2_99.xml").toFile(), Path.make(workAreaRootPathPath,"work/TEST/42/data/a/").toFile());
		FileUtils.copyFileToDirectory(Path.make(workAreaRootPathPath,"work/src/vda3.XML").toFile(), Path.make(workAreaRootPathPath,"work/TEST/42/data/a/").toFile());
		DAFile f1 = new DAFile(object.getLatestPackage(),"a","mets_2_99.xml");
		object.getLatestPackage().getFiles().add(f1);
		DAFile f3 = new DAFile(object.getLatestPackage(),"a","vda3.XML");
		object.getLatestPackage().getFiles().add(f3);
		
		event = new Event();
		event.setSource_file(new DAFile(object.getLatestPackage(),"a","ALVR_Nr_4547_Aufn_067.tif"));
		event.setTarget_file(new DAFile(object.getLatestPackage(),"b","renamed067.tif"));
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
		action.setAbsUrlPrefix("http://data.danrw.de/file");
		Map<String, String> dcMappings = new HashMap<String,String>();
		dcMappings.put("EAD", "conf/xslt/dc/ead_to_dc.xsl");
		action.setDcMappings(dcMappings);
		
		action.setMtds(mtds);
		action.setObject(object);
		action.setJob(job);
	}
	
	@After 
	public void tearDown(){
		Path.makeFile(workAreaRootPathPath,"work/TEST/42/data/a/mets_2_99.xml").delete();
		Path.makeFile(workAreaRootPathPath,"work/TEST/42/data/a/vda3.XML").delete();
		Path.makeFile(workAreaRootPathPath,"work/TEST/42/data/b/mets_2_99.xml").delete();
		Path.makeFile(workAreaRootPathPath,"work/TEST/42/data/b/vda3.XML").delete();
	}
	
	
	@Test
	public void test() throws IOException, JDOMException {
		
		action.implementation();
		
		SAXBuilder builder = new SAXBuilder();
		Document doc = builder.build(new FileReader(Path.make(workAreaRootPathPath,"work/TEST/42/data/b/mets_2_99.xml").toFile()));

		assertEquals("http://data.danrw.de/file/42/renamed067.tif", getURL(doc));
		System.out.println("DC: "+action.getDcMappings());
	}
	
	
	
	@Test
	public void upperLowerCaseMismatch() throws IOException, JDOMException {
		event.setSource_file(new DAFile(object.getLatestPackage(),"a","alvr_Nr_4547_Aufn_067.tif"));
		
		try{
			action.implementation();
			fail();
		}catch(UserException e){
			assertTrue(e.getMessage().contains("but only"));
		}
	}
	
	private String getURL(Document doc){
		
		return doc.getRootElement()
				.getChild("fileSec", METS_NS)
				.getChild("fileGrp", METS_NS)
				.getChild("file", METS_NS)
				.getChild("FLocat", METS_NS)
				.getAttributeValue("href", XLINK_NS);
	}
}
