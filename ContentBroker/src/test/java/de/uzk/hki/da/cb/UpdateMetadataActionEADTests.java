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
import java.io.IOException;
import java.util.HashMap;

import org.apache.commons.io.FileUtils;
import org.jdom.Namespace;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.uzk.hki.da.core.ActionCommunicatorService;
import de.uzk.hki.da.model.DAFile;
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
	private static final String basePath = "src/test/resources/cb/UpdateMetadataActionEADTests/";
	private static final UpdateMetadataAction action = new UpdateMetadataAction();
	
	@Before
	public void setUp() throws IOException{
		Object obj = TESTHelper.setUpObject("42",basePath);

		FileUtils.copyFileToDirectory(new File(basePath+"src/mets_2_99.xml"), new File(basePath+"TEST/42/data/a/"));
		FileUtils.copyFileToDirectory(new File(basePath+"src/mets_2_998.xml"), new File(basePath+"TEST/42/data/a/"));
		FileUtils.copyFileToDirectory(new File(basePath+"src/vda3.XML"), new File(basePath+"TEST/42/data/a/"));
		DAFile f1 = new DAFile(obj.getLatestPackage(),"a","mets_2_99.xml");
		obj.getLatestPackage().getFiles().add(f1);
		DAFile f2 = new DAFile(obj.getLatestPackage(),"a","mets_2_998.xml");
		obj.getLatestPackage().getFiles().add(f2);
		DAFile f3 = new DAFile(obj.getLatestPackage(),"a","vda3.XML");
		obj.getLatestPackage().getFiles().add(f3);
		
		
		
		Job job = new Job(); job.setObject(obj); job.setId(1);
		ActionCommunicatorService acs = new ActionCommunicatorService();
		acs.addDataObject(1, "package_type", "EAD");
		acs.addDataObject(1, "metadata_file", "vda3.XML");
		
		
		
		UpdateMetadataService service = new UpdateMetadataService();
		HashMap<String,String> xpaths = new HashMap<String,String>();
		xpaths.put("METS", "//mets:FLocat/@xlink:href");
		service.setXpathsToUrls(xpaths);
		
		HashMap<String, String> nsMap = new HashMap<String,String>();
		nsMap.put("mets", METS_NS.getURI());
		nsMap.put("xlink", XLINK_NS.getURI());
		service.setNamespaces(nsMap);
		
		action.setUpdateMetadataService(service);
		
		
		action.setActionCommunicatorService(acs);
		action.setObject(obj);
		action.setJob(job);
	}
	
	@After 
	public void tearDown(){
		new File(basePath+"TEST/42/data/a/mets_2_99.xml").delete();
		new File(basePath+"TEST/42/data/a/mets_2_998.xml").delete();
		new File(basePath+"TEST/42/data/a/vda3.XML").delete();
		new File(basePath+"TEST/42/data/b/mets_2_99.xml").delete();
		new File(basePath+"TEST/42/data/b/mets_2_998.xml").delete();
		new File(basePath+"TEST/42/data/b/vda3.XML").delete();
	}
	
	
	@Test
	public void test() throws IOException {
		
		
//		action.implementation();
	}

}
