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


import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import de.uzk.hki.da.model.DAFile;
import de.uzk.hki.da.model.Node;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.utils.Path;
import de.uzk.hki.da.utils.RelativePath;
import de.uzk.hki.da.utils.TESTHelper;

/**
 * The Class ObjectTest.
 */
public class ObjectTest {
	
	private static final Path workAreaRootPath = new RelativePath("src/test/resources/model/ObjectTests/");
	private static DAFile f1;
	private static DAFile f2;
	private static Object o;
	
	/**
	 * Sets the up before class.
	 *
	 * @throws Exception the exception
	 */
	@Before
	public void setUp() throws Exception {
		Node n = new Node();
		n.setWorkAreaRootPath(Path.make(workAreaRootPath));
		
		o = TESTHelper.setUpObject("123", workAreaRootPath);

		f1 = new DAFile(o.getLatestPackage(),"a","a.txt");
		f2 = new DAFile(o.getLatestPackage(),"b","a.txt");
		
		o.getLatestPackage().getFiles().add(f1);
		o.getLatestPackage().getFiles().add(f2);
	}

	
	
	@Test
	public void testGetLatestReturnsAttachedInstance(){
		
		assertSame(f2,o.getLatest("a.txt"));
	}
	
	
	
	
	
	@Test
	public void testCheckDBtoFSConsistent(){
		
		assertTrue(o.isDBtoFSconsistent());
	}
	
	@Test
	public void testCheckDBtoFSNotConsistent(){
		
		o = TESTHelper.setUpObject("234", workAreaRootPath);
		f1 = new DAFile(o.getLatestPackage(),"a","a.txt");
		f2 = new DAFile(o.getLatestPackage(),"b","a.txt");
		o.getLatestPackage().getFiles().add(f1);
		o.getLatestPackage().getFiles().add(f2);
		assertFalse(o.isDBtoFSconsistent());
	}
	
	
	@Test
	public void testCheckFSToDBConsistent(){
		
		assertTrue(o.isFStoDBconsistent());
	}
	
	@Test
	public void testCheckFSToDBNotConsistent(){
		
		o.getLatestPackage().getFiles().clear();
		f1 = new DAFile(o.getLatestPackage(),"a","a.txt");
		o.getLatestPackage().getFiles().add(f1);
		assertFalse(o.isFStoDBconsistent());
	}
}
