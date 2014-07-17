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

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.jdom.JDOMException;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

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
 */
public class UpdateMetadataActionXMPTests {

	private static MimeTypeDetectionService mtds;
	private final Path workAreaRootPath = new RelativePath("src/test/resources/cb/UpdateMetadataActionXMPTests/");
	
	@BeforeClass
	public static void mockDca() throws IOException {
		mtds = mock(MimeTypeDetectionService.class);
		when(mtds.detectMimeType((DAFile)anyObject())).thenReturn("image/tiff");
	}
	
	@Before
	public void setUp() throws Exception {
		FileUtils.copyDirectoryToDirectory(new File("src/main/xslt"), new File("conf/"));
	}

	@After
	public void tearDown() throws Exception {
		Path.make(workAreaRootPath,"work/TEST/123/data/dip/public/hasha.xmp").toFile().delete();
		Path.make(workAreaRootPath,"work/TEST/123/data/dip/institution/hasha.xmp").toFile().delete();
		Path.make(workAreaRootPath,"work/TEST/123/data/dip/public/hashb.xmp").toFile().delete();
		Path.make(workAreaRootPath,"work/TEST/123/data/dip/institution/hashb.xmp").toFile().delete();
		Path.make(workAreaRootPath,"work/TEST/123/data/dip/public/XMP.rdf").toFile().delete();
		Path.make(workAreaRootPath,"work/TEST/123/data/dip/institution/XMP.rdf").toFile().delete();
		Path.make(workAreaRootPath,"work/TEST/123/data/dip/public/DC.xml").toFile().delete();
		Path.make(workAreaRootPath,"work/TEST/123/data/dip/institution/DC.xml").toFile().delete();
		Path.make(workAreaRootPath,"work/TEST/123/data/dip/public/a.xmp").toFile().delete();
		Path.make(workAreaRootPath,"work/TEST/123/data/dip/institution/a.xmp").toFile().delete();
		Path.make(workAreaRootPath,"work/TEST/123/data/dip/public/b.xmp").toFile().delete();
		Path.make(workAreaRootPath,"work/TEST/123/data/dip/institution/b.xmp").toFile().delete();
		FileUtils.deleteDirectory(new File("conf/xslt"));
	}

	@Test
	public void test() throws IOException, JDOMException {
		Object obj = TESTHelper.setUpObject("123", workAreaRootPath);
		
		DAFile daf1 = new DAFile(obj.getLatestPackage(),"a","a.txt");
		DAFile daf2 = new DAFile(obj.getLatestPackage(),"b","a.txt");
		DAFile daf3 = new DAFile(obj.getLatestPackage(),"b","a.xmp");
		DAFile daf4 = new DAFile(obj.getLatestPackage(),"dip/institution","hasha.txt");
		DAFile daf5 = new DAFile(obj.getLatestPackage(),"dip/public","hasha.txt");
		DAFile daf6 = new DAFile(obj.getLatestPackage(),"dip/public","hashb.txt");
		DAFile daf7 = new DAFile(obj.getLatestPackage(),"dip/institution","hashb.txt");
		DAFile daf8 = new DAFile(obj.getLatestPackage(),"a","b.txt");
		DAFile daf9 = new DAFile(obj.getLatestPackage(),"b","b.txt");
		DAFile daf10 = new DAFile(obj.getLatestPackage(),"b","b.xmp");
	
		Event evt1  = new Event();
		evt1.setSource_file(daf2);
		evt1.setTarget_file(daf4);
		evt1.setType("CONVERT");
		
		Event evt2  = new Event();
		evt2.setSource_file(daf2);
		evt2.setTarget_file(daf5);
		evt2.setType("CONVERT");
		
		Event evt3  = new Event();
		evt3.setSource_file(daf9);
		evt3.setTarget_file(daf6);
		evt3.setType("CONVERT");
		
		Event evt4  = new Event();
		evt4.setSource_file(daf9);
		evt4.setTarget_file(daf7);
		evt4.setType("CONVERT");
		
		Event evt5  = new Event();
		evt5.setSource_file(daf1);
		evt5.setTarget_file(daf2);
		evt5.setType("CONVERT");
		
		Event evt6  = new Event();
		evt6.setSource_file(daf8);
		evt6.setTarget_file(daf9);
		evt6.setType("CONVERT");
		
		obj.getLatestPackage().getFiles().add(daf1);
		obj.getLatestPackage().getFiles().add(daf2);
		obj.getLatestPackage().getFiles().add(daf3);
		obj.getLatestPackage().getFiles().add(daf4);
		obj.getLatestPackage().getFiles().add(daf5);
		obj.getLatestPackage().getFiles().add(daf6);
		obj.getLatestPackage().getFiles().add(daf7);
		obj.getLatestPackage().getFiles().add(daf8);
		obj.getLatestPackage().getFiles().add(daf9);
		obj.getLatestPackage().getFiles().add(daf10);
		obj.getLatestPackage().getEvents().add(evt1);
		obj.getLatestPackage().getEvents().add(evt2);
		obj.getLatestPackage().getEvents().add(evt3);
		obj.getLatestPackage().getEvents().add(evt4);
		obj.getLatestPackage().getEvents().add(evt5);
		obj.getLatestPackage().getEvents().add(evt6);
		
		// set up object - end
		UpdateMetadataAction action = new UpdateMetadataAction();
		
		HashMap<String,String> xpaths = new HashMap<String,String>();
		xpaths.put("XMP", "//rdf:Description/@rdf:about");
		action.setXpathsToUrls(xpaths);
		
		HashMap<String, String> nsMap = new HashMap<String,String>();
		action.setNamespaces(nsMap);
		
		Job job = new Job(); job.setId(1);
		
		action.setMtds(mtds);
		action.setObject(obj);
		action.setJob(job);
		action.setRepNames(new String[]{"dip/public", "dip/institution"});
		
		obj.setMetadata_file("XMP.rdf");
		obj.setPackage_type("XMP");
		
		Map<String, String> dcMappings = new HashMap<String,String>();
		dcMappings.put("XMP", "conf/xslt/dc/xmp_to_dc.xsl");
		action.setDcMappings(dcMappings);
		
		action.implementation();
		
		assertTrue(new File(workAreaRootPath+"/work/TEST/123/data/dip/public/hasha.xmp").exists());
		assertTrue(new File(workAreaRootPath+"/work/TEST/123/data/dip/institution/hasha.xmp").exists());
		assertTrue(new File(workAreaRootPath+"/work/TEST/123/data/dip/public/hashb.xmp").exists());
		assertTrue(new File(workAreaRootPath+"/work/TEST/123/data/dip/institution/hashb.xmp").exists());
		assertTrue(new File(workAreaRootPath+"/work/TEST/123/data/dip/public/XMP.rdf").exists());
		assertTrue(new File(workAreaRootPath+"/work/TEST/123/data/dip/institution/XMP.rdf").exists());
		assertTrue(new File(workAreaRootPath+"/work/TEST/123/data/dip/public/DC.xml").exists());
		assertTrue(new File(workAreaRootPath+"/work/TEST/123/data/dip/institution/DC.xml").exists());
	}
}
