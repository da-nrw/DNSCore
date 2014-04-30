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

import static org.junit.Assert.*;

import java.io.File;
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
import org.junit.Test;

import de.uzk.hki.da.core.ActionCommunicatorService;
import de.uzk.hki.da.model.DAFile;
import de.uzk.hki.da.model.Event;
import de.uzk.hki.da.model.Job;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.service.UpdateMetadataService;
import de.uzk.hki.da.utils.TESTHelper;

/**
 * @author Daniel M. de Oliveira
 */
public class UpdateMetadataActionEADTests {

	private static final Namespace METS_NS = Namespace.getNamespace("http://www.loc.gov/METS/");
	private static final Namespace XLINK_NS = Namespace.getNamespace("http://www.w3.org/1999/xlink");
	private static final String workAreaRootPathPath = "src/test/resources/cb/UpdateMetadataActionEADTests/";
	private static final UpdateMetadataAction action = new UpdateMetadataAction();
	
	@Before
	public void setUp() throws IOException{
		Object obj = TESTHelper.setUpObject("42",workAreaRootPathPath);

		FileUtils.copyFileToDirectory(new File(workAreaRootPathPath+"work/src/mets_2_99.xml"), new File(workAreaRootPathPath+"work/TEST/42/data/a/"));
		FileUtils.copyFileToDirectory(new File(workAreaRootPathPath+"work/src/vda3.XML"), new File(workAreaRootPathPath+"work/TEST/42/data/a/"));
		DAFile f1 = new DAFile(obj.getLatestPackage(),"a","mets_2_99.xml");
		obj.getLatestPackage().getFiles().add(f1);
		DAFile f3 = new DAFile(obj.getLatestPackage(),"a","vda3.XML");
		obj.getLatestPackage().getFiles().add(f3);
		
		Event e1 = new Event();
		e1.setSource_file(new DAFile(obj.getLatestPackage(),"a","ALVR_Nr_4547_Aufn_067.tif"));
		e1.setTarget_file(new DAFile(obj.getLatestPackage(),"b","renamed067.tif"));
		e1.setType("CONVERT");
		obj.getLatestPackage().getEvents().add(e1);
		
		
		
		Job job = new Job(); job.setObject(obj); job.setId(1);
		ActionCommunicatorService acs = new ActionCommunicatorService();
		acs.addDataObject(1, "package_type", "EAD");
		acs.addDataObject(1, "metadata_file", "vda3.XML");
		
		UpdateMetadataService service = new UpdateMetadataService();
		HashMap<String,String> xpaths = new HashMap<String,String>();
		xpaths.put("METS", "//mets:FLocat/@xlink:href");
		xpaths.put("EAD", "//daoloc/@href");
		service.setXpathsToUrls(xpaths);
		HashMap<String, String> nsMap = new HashMap<String,String>();
		nsMap.put("mets", METS_NS.getURI());
		nsMap.put("xlink", XLINK_NS.getURI());
		service.setNamespaces(nsMap);
		action.setUpdateMetadataService(service);
		Map<String, String> dcMappings = new HashMap<String,String>();
		dcMappings.put("EAD", "conf/xslt/dc/ead_to_dc.xsl");
		action.setDcMappings(dcMappings);
		
		action.setActionCommunicatorService(acs);
		action.setObject(obj);
		action.setJob(job);
	}
	
	@After 
	public void tearDown(){
		new File(workAreaRootPathPath+"work/TEST/42/data/a/mets_2_99.xml").delete();
		new File(workAreaRootPathPath+"work/TEST/42/data/a/vda3.XML").delete();
		new File(workAreaRootPathPath+"work/TEST/42/data/b/mets_2_99.xml").delete();
		new File(workAreaRootPathPath+"work/TEST/42/data/b/vda3.XML").delete();
	}
	
	
	@Test
	public void test() throws IOException, JDOMException {
		
		action.implementation();
		
		SAXBuilder builder = new SAXBuilder();
		Document doc = builder.build(new FileReader(new File(workAreaRootPathPath + "work/TEST/42/data/b/mets_2_99.xml")));

		String url = doc.getRootElement()
				.getChild("fileSec", METS_NS)
				.getChild("fileGrp", METS_NS)
				.getChild("file", METS_NS)
				.getChild("FLocat", METS_NS)
				.getAttributeValue("href", XLINK_NS);
		
		assertEquals("renamed067.tif", url);
	}

}
