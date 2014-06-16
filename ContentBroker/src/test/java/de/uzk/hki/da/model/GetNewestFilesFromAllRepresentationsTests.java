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

package de.uzk.hki.da.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.uzk.hki.da.utils.Path;
import de.uzk.hki.da.utils.RelativePath;
import de.uzk.hki.da.utils.TESTHelper;


/**
 * The Class GetNewestFilesFromAllRepresentationsTests.
 */
public class GetNewestFilesFromAllRepresentationsTests {

	Path workAreaRootPath = new RelativePath("src/test/resources/model/Object/GetNewestFiles/");
	
	/** The o. */
	Object o;
	
	/**
	 * Sets the up.
	 */
	@Before
	public void setUp() {
		o = TESTHelper.setUpObject("1",workAreaRootPath);
	}
	
	
	/**
	 * Test ianus pkg.
	 *
	 * @author Daniel M. de Oliveira
	 */
	@Test
	public void testIanusPkg(){
		Node n = new Node(); n.setWorkAreaRootPath(Path.make(workAreaRootPath));
		Package pkg = new Package(); pkg.setId(2); 
		pkg.setName("2");
		o.setIdentifier("2");
		o.getPackages().clear();
		o.getPackages().add(pkg);
		pkg.setTransientBackRefToObject(o);
		o.getLatestPackage().scanRepRecursively("2013_06_19+10_26+a");
		o.getLatestPackage().scanRepRecursively("2013_06_19+10_26+b");
		
		List<DAFile> fileList = o.getNewestFilesFromAllRepresentations("xmp;xml");
		
		assertTrue(fileList.contains(new DAFile(null,"2013_06_19+10_26+b","ead_XSLT_ead_to_dc.xml")));
		
		assertTrue(fileList.contains(new DAFile(null,"2013_06_19+10_26+b","ALVR_Nr_4547_Aufn_002.xml")));
		assertTrue(fileList.contains(new DAFile(null,"2013_06_19+10_26+b","ALVR_Nr_4547_Aufn_003.xml")));
		assertTrue(fileList.contains(new DAFile(null,"2013_06_19+10_26+b","ALVR_Nr_4547_Aufn_004.xml")));
		
		assertTrue(fileList.contains(new DAFile(null,"2013_06_19+10_26+b","ALVR_Nr_4547_Aufn_004_XSLT_mets_mods_to_dc.xml")));
		assertTrue(fileList.contains(new DAFile(null,"2013_06_19+10_26+b","ALVR_Nr_4547_Aufn_003_XSLT_mets_mods_to_dc.xml")));
		assertTrue(fileList.contains(new DAFile(null,"2013_06_19+10_26+b","ALVR_Nr_4547_Aufn_002_XSLT_mets_mods_to_dc.xml")));
		assertTrue(fileList.contains(new DAFile(null,"2013_06_19+10_26+a","ead.xml")));
		
		assertTrue(fileList.contains(new DAFile(null,"2013_06_19+10_26+a","premis.xml")));
		
		assertTrue(fileList.contains(new DAFile(null,"2013_06_19+10_26+b","ALVR_Nr_4547_Aufn_002.tif")));
		assertTrue(fileList.contains(new DAFile(null,"2013_06_19+10_26+b","ALVR_Nr_4547_Aufn_003.tif")));
		assertTrue(fileList.contains(new DAFile(null,"2013_06_19+10_26+b","ALVR_Nr_4547_Aufn_004.tif")));
	}
	
	
	
	/**
	 * TODO the method seems not to be working as expected since we expect only
	 * one document testImage_2 to be existent and it should be in the latest folder.
	 *
	 * @return the newest file for document
	 */
	@Test
	public void getNewestFileForDocument(){
		
		o.getLatestPackage().scanRepRecursively("2012_11_05+12_49+a");
		o.getLatestPackage().scanRepRecursively("2012_11_05+12_49+b");
		o.getLatestPackage().scanRepRecursively("2012_11_05+13_32+a");
		o.getLatestPackage().scanRepRecursively("2012_11_05+13_32+b");
		List<DAFile> fileList = o.getNewestFilesFromAllRepresentations("xmp");
	
		boolean justOne = true;
		for (DAFile f:fileList){
			if (f.getRelative_path().endsWith("testImage_2.tif")){
				if (!justOne) fail();
				assertEquals("testImage_2.tif",f.getRelative_path());
				assertEquals("2012_11_05+13_32+b",f.getRep_name());
				justOne = false;
			}
		}
	}
	
	
	/**
	 * Should create a list of the newest (last modified) files for each Document from all
	 * representation folders.
	 *
	 * @return the newest files from all representations
	 */
	 @Test
	 public void getNewestFilesFromAllRepresentations()
	 {
		 
		 o.getLatestPackage().scanRepRecursively("2012_11_05+12_49+a");
		 o.getLatestPackage().scanRepRecursively("2012_11_05+12_49+b");
		 o.getLatestPackage().scanRepRecursively("2012_11_05+13_32+a");
		 o.getLatestPackage().scanRepRecursively("2012_11_05+13_32+b");
		 
		 List<DAFile> fileList = o.getNewestFilesFromAllRepresentations("xmp;xml;txt");
		 String[][] correctRelativePaths = new String[][] { {"testImage_1.tif", "2012_11_05+12_49+b"},
				 											{"testImage_2.tif", "2012_11_05+13_32+b"},
				 											{"testImage_3.tif", "2012_11_05+12_49+b"},
				 											{"folder_1/testImage_4.tif", "2012_11_05+12_49+b"},
				 											{"folder_1/testImage_5.tif", "2012_11_05+12_49+b"},
				 											{"folder_2/testImage_4.tif", "2012_11_05+12_49+b"},
				 											{"folder_2/testImage_5.tif", "2012_11_05+12_49+b"},
				 											{"testImage_1.xmp", "2012_11_05+12_49+b"},
				 											{"testImage_1.txt", "2012_11_05+12_49+b"} };
		 for (DAFile f : fileList)
		 {
			 System.out.println("Name: " + f.toRegularFile().getName());
			 System.out.println("Relative path: " + f.getRelative_path());
			 System.out.println("Representation name: " + f.getRep_name());
			 System.out.println("");
			 
			 boolean validPath = false;
			 for (int i = 0; i < 9; i++)
			 {
				 if (correctRelativePaths[i][0].equals(f.getRelative_path())
						 && correctRelativePaths[i][1].equals(f.getRep_name()))
				 {
					 correctRelativePaths[i][0] = "";
					 correctRelativePaths[i][1] = "";
					 validPath = true;
					 break;
				 }
			 }
			 
			 assertTrue(validPath);
		 }
		
		 assertEquals(9, fileList.size());
	 }
	 
	 
	 
	

}
