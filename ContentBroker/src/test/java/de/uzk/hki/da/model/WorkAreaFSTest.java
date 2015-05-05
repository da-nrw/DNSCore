/*
  DA-NRW Software Suite | ContentBroker
  Copyright (C) 2014 Historisch-Kulturwissenschaftliche Informationsverarbeitung
  Universität zu Köln
  Copyright (C) 2015 LVR-InfoKom
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


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import de.uzk.hki.da.core.C;
import de.uzk.hki.da.test.TC;
import de.uzk.hki.da.test.TESTHelper;
import de.uzk.hki.da.util.Path;

/**
 * @author Daniel M. de Oliveira
 */
public class WorkAreaFSTest {
	
	private static final String identifier = "identifier";
	private static final String _1_B_REP = "2000_00_00+00_00+b";
	private static final String _1_A_REP = "2000_00_00+00_00+a";
	private static final Path workAreaRootPath = Path.make(TC.TEST_ROOT_MODEL,"WorkAreaFS");
	private static DAFile f1;
	private static DAFile f2;
	private static Object o;
	private static WorkArea wa;
	private Node n;
	
	/**
	 * Sets the up before class.
	 *
	 * @throws Exception the exception
	 */
	@Before
	public void setUp() throws Exception {
		n = new Node();
		n.setWorkAreaRootPath(Path.make(workAreaRootPath));
		
		o = TESTHelper.setUpObject(identifier, workAreaRootPath);

		f1 = new DAFile(_1_A_REP,"a.txt");
		f2 = new DAFile(_1_B_REP,"a.txt");
		
		o.getLatestPackage().getFiles().add(f1);
		o.getLatestPackage().getFiles().add(f2);
		
		wa = new WorkArea(n,o);
	}

	
	
	
	
	
	
	@Test
	public void testCheckDBtoFSConsistent(){
		
		assertTrue(wa.isDBtoFSconsistent());
	}
	
	@Test
	public void testCheckDBtoFSNotConsistent(){
		
		o = TESTHelper.setUpObject("234", workAreaRootPath);
		f1 = new DAFile(_1_A_REP,"a.txt");
		f2 = new DAFile(_1_B_REP,"a.txt");
		o.getLatestPackage().getFiles().add(f1);
		o.getLatestPackage().getFiles().add(f2);
		wa=new WorkArea(n,o);
		assertFalse(wa.isDBtoFSconsistent());
	}
	
	
	@Test
	public void testCheckFSToDBConsistent(){
		
		assertTrue(wa.isFStoDBconsistent());
	}
	
	/**
	 * There are more files on the file system than there are in the database. 
	 */
	@Test
	public void testCheckFSToDBNotConsistent(){
		
		o.getLatestPackage().getFiles().clear();
		f1 = new DAFile(_1_A_REP,"a.txt");
		o.getLatestPackage().getFiles().add(f1);
		assertFalse(wa.isFStoDBconsistent());
	}
	
	
	
	/**
	 * Ignore it if more than the entries under data are present on the FS, but some of them are not named with the 
	 * proper representation name patterns for lza reps.
	 */
	@Test
	public void testDontConsiderNonRepEntriesOnFS(){
		
		o = TESTHelper.setUpObject("123dip", workAreaRootPath);
		f1 = new DAFile(_1_A_REP,"a.txt");
		f2 = new DAFile(_1_B_REP,"a.txt");
		o.getLatestPackage().getFiles().add(f1);
		o.getLatestPackage().getFiles().add(f2);
		
		assertTrue(wa.isFStoDBconsistent());
	}
	
	
	
	/**
	 * Ignore testing for files whose representation is not one of the lza reps. 
	 */
	@Test
	public void testDontConsiderNonRepEntriesInDB(){
		
		DAFile f3 = new DAFile("dip","a.txt");
		o.getLatestPackage().getFiles().add(f3);
		
		assertTrue(wa.isDBtoFSconsistent());
	}
	
	
	
	
	
	
	@Test
	public void testGetPathNewestRep(){
		
		assertEquals(
				_1_B_REP,
				o.getNameOfLatestBRep()
				);
	}
	
}
