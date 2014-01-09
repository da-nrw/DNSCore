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
package de.uzk.hki.da.service;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.uzk.hki.da.grid.FakeGridFacade;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.model.Package;
import de.uzk.hki.da.utils.TESTHelper;

/**
 * @author Daniel M. de Oliveira
 */
public class RetrievePackagesHelperTest {

	private String basePath = "src/test/resources/service/RetrievePackagesHelperTest/";
	private Object object;	
	
	@Before
	public void setUp(){
		new File(basePath+"work/TEST/id/data").mkdir();
		
		object = TESTHelper.setUpObject("id", basePath+"work/");
		
		Package p2 = new Package(); p2.setName("2");
		Package p3 = new Package(); p3.setName("3");
		object.getPackages().add(p2);
		object.getPackages().add(p3);
	}
	
	@After 
	public void tearDown() throws IOException{
		FileUtils.deleteDirectory(new File(basePath+"work/TEST/id/existingAIPs"));
		FileUtils.deleteDirectory(new File(basePath+"work/TEST/id/data"));
	}
	
	
	@Test
	public void test() throws IOException{
		
		FakeGridFacade grid = new FakeGridFacade();
		grid.setGridCacheAreaRootPath(basePath+"grid/");
		
		new RetrievePackagesHelper().copyPackagesFromLZAToWorkArea(object, grid, true);
		new RetrievePackagesHelper().unpackExistingPackages(object);
		
		String outputPath = basePath + "work/TEST/id/";
		
		assertTrue(new File(outputPath + "data/a/pic1.txt").exists());
		assertTrue(new File(outputPath + "data/b/pic2.txt").exists());
		assertTrue(new File(outputPath + "data/c/pic3.txt").exists());
		assertTrue(new File(outputPath + "data/d/pic4.txt").exists());
		assertTrue(new File(outputPath + "data/e/pic1.txt").exists());
		assertTrue(new File(outputPath + "data/f/folder1/pic5.txt").exists());
		assertTrue(new File(outputPath + "data/f/folder2/pic5.txt").exists());
		assertTrue(new File(outputPath + "data/f/pic3.txt").exists());
		assertFalse(new File(outputPath + "existingAIPs").exists());
	}
}
