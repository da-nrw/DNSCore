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
import de.uzk.hki.da.model.DAFile;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.model.Package;
import de.uzk.hki.da.utils.Path;
import de.uzk.hki.da.utils.RelativePath;
import de.uzk.hki.da.utils.TESTHelper;

/**
 * @author Daniel M. de Oliveira
 */
public class RetrievePackagesHelperTest {

	private Path workAreaRootPathPath = new RelativePath("src/test/resources/service/RetrievePackagesHelperTest/");
	private Object object;	
	
	@Before
	public void setUp(){
		Path.make(workAreaRootPathPath,"work/TEST/id/data").toFile().mkdir();
		
		object = TESTHelper.setUpObject("id", workAreaRootPathPath);
		
		Package p2 = new Package(); p2.setName("2");
		Package p3 = new Package(); p3.setName("3");
		object.getPackages().add(p2);
		object.getPackages().add(p3);
	}
	
	@After 
	public void tearDown() throws IOException{
		FileUtils.deleteDirectory(Path.make(workAreaRootPathPath,"work/TEST/id/loadedAIPs").toFile());
		FileUtils.deleteDirectory(Path.make(workAreaRootPathPath,"work/TEST/id/data").toFile());
	}
	
	
	@Test
	public void test() throws IOException{
		
		FakeGridFacade grid = new FakeGridFacade();
		grid.setGridCacheAreaRootPath(workAreaRootPathPath+"/grid/");
		
		new RetrievePackagesHelper(grid).loadPackages(object, true);
		
		String outputPath = workAreaRootPathPath + "/work/TEST/id/";
		
		assertTrue(new File(outputPath + "data/a/pic1.txt").exists());
		assertTrue(new File(outputPath + "data/b/pic2.txt").exists());
		assertTrue(new File(outputPath + "data/c/pic3.txt").exists());
		assertTrue(new File(outputPath + "data/d/pic4.txt").exists());
		assertTrue(new File(outputPath + "data/e/pic1.txt").exists());
		assertTrue(new File(outputPath + "data/f/folder1/pic5.txt").exists());
		assertTrue(new File(outputPath + "data/f/folder2/pic5.txt").exists());
		assertTrue(new File(outputPath + "data/f/pic3.txt").exists());
		assertFalse(new File(outputPath + "existingAIPs").exists());
		
		boolean checked1=false;
		boolean checked2=false;
		boolean checked3=false;
		for (Package p:object.getPackages()){
			System.out.println("Object has Package "+p.getName());
			
			if (p.getName().equals("1")){
				assertTrue(p.getFiles().contains(new DAFile(null,"a","pic1.txt")));
				assertTrue(p.getFiles().contains(new DAFile(null,"b","pic2.txt")));
				assertFalse(p.getFiles().contains(new DAFile(null,"c","pic3.txt")));
				assertFalse(p.getFiles().contains(new DAFile(null,"d","pic4.txt")));
				assertFalse(p.getFiles().contains(new DAFile(null,"e","pic1.txt")));
				assertFalse(p.getFiles().contains(new DAFile(null,"f","folder1/pic5.txt")));
				assertFalse(p.getFiles().contains(new DAFile(null,"f","folder2/pic5.txt")));
				assertFalse(p.getFiles().contains(new DAFile(null,"f","pic3.txt")));
				checked1=true;
			}
			if (p.getName().equals("2")){
				assertFalse(p.getFiles().contains(new DAFile(null,"a","pic1.txt")));
				assertFalse(p.getFiles().contains(new DAFile(null,"b","pic2.txt")));
				assertTrue(p.getFiles().contains(new DAFile(null,"c","pic3.txt")));
				assertTrue(p.getFiles().contains(new DAFile(null,"d","pic4.txt")));
				assertFalse(p.getFiles().contains(new DAFile(null,"e","pic1.txt")));
				assertFalse(p.getFiles().contains(new DAFile(null,"f","folder1/pic5.txt")));
				assertFalse(p.getFiles().contains(new DAFile(null,"f","folder2/pic5.txt")));
				assertFalse(p.getFiles().contains(new DAFile(null,"f","pic3.txt")));
				checked2=true;
				
			}
			if (p.getName().equals("3")){
				assertFalse(p.getFiles().contains(new DAFile(null,"a","pic1.txt")));
				assertFalse(p.getFiles().contains(new DAFile(null,"b","pic2.txt")));
				assertFalse(p.getFiles().contains(new DAFile(null,"c","pic3.txt")));
				assertFalse(p.getFiles().contains(new DAFile(null,"d","pic4.txt")));
				assertTrue(p.getFiles().contains(new DAFile(null,"e","pic1.txt")));
				assertTrue(p.getFiles().contains(new DAFile(null,"f","folder1/pic5.txt")));
				assertTrue(p.getFiles().contains(new DAFile(null,"f","folder2/pic5.txt")));
				assertTrue(p.getFiles().contains(new DAFile(null,"f","pic3.txt")));
				checked3=true;
			}
		}
		assertTrue(checked1);
		assertTrue(checked2);
		assertTrue(checked3);
	}
}
