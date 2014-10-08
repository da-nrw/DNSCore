/*
  DA-NRW Software Suite | ContentBroker
  Copyright (C) 2013 Historisch-Kulturwissenschaftliche Informationsverarbeitung
  Universität zu Köln
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

package de.uzk.hki.da.model;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import de.uzk.hki.da.core.Path;
import de.uzk.hki.da.test.TC;
import de.uzk.hki.da.test.TESTHelper;


/**
 * The Class GetNewestFilesFromAllRepresentationsTests.
 * @author Daniel M. de Oliveira
 */
public class GetNewestFilesFromAllRepresentationsTests {

	Path workAreaRootPath = Path.make(TC.TEST_ROOT_MODEL,"Object","GetNewestFiles");
	
	Object o;

	Package p1;
	
	Package p2;
	
	/**
	 * Sets the up.
	 */
	@Before
	public void setUp() {
		o = TESTHelper.setUpObject("1",workAreaRootPath);
		p1 = o.getLatestPackage();
		p2 = new Package();
		p2.setTransientBackRefToObject(o);
		o.getPackages().add(p2);
	}
	

	 /**
	  * @author Daniel M. de Oliveira
	  */
	 @Test
	 public void testOneOverwritesTheOther(){
		 
		 DAFile f1 = new DAFile(p1,"1+a","abc.tif");
		 DAFile f2 = new DAFile(p2,"1+b","abc.jpg");
		 p1.getFiles().add(f1);
		 p2.getFiles().add(f2);
		 
		 assertTrue(o.getNewestFilesFromAllRepresentations("").contains(f2));
		 assertFalse(o.getNewestFilesFromAllRepresentations("").contains(f1));
	 }
	 
	 
	
	/**
	  * @author Daniel M. de Oliveira
	  */
	 @Test
	 public void testOnlyOneGetsOverwritten(){
		 
		 DAFile f1 = new DAFile(p1,"1+a","abc.tif");
		 DAFile f2 = new DAFile(p2,"1+b","abc.jpg");
		 DAFile f3 = new DAFile(p2,"1+a","bcd.jpg");
		 p1.getFiles().add(f1);
		 p2.getFiles().add(f2);
		 p2.getFiles().add(f3);
		 
		 assertTrue(o.getNewestFilesFromAllRepresentations("").contains(f2));
		 assertTrue(o.getNewestFilesFromAllRepresentations("").contains(f3));
		 assertFalse(o.getNewestFilesFromAllRepresentations("").contains(f1));
	 }

	 
	 
	 /**
	  * @author Daniel M. de Oliveira
	  */
	 @Test
	 public void testSidecar(){
		 
		 DAFile f1 = new DAFile(p1,"1+a","abc.tif");
		 DAFile f2 = new DAFile(p2,"1+b","abc.jpg");
		 DAFile f3 = new DAFile(p2,"1+a","abc.xmp");
		 p1.getFiles().add(f1);
		 p2.getFiles().add(f2);
		 p2.getFiles().add(f3);
		 
		 assertTrue(o.getNewestFilesFromAllRepresentations("xmp").contains(f2));
		 assertTrue(o.getNewestFilesFromAllRepresentations("xmp").contains(f3));
		 assertFalse(o.getNewestFilesFromAllRepresentations("xmp").contains(f1));
	 }
}
