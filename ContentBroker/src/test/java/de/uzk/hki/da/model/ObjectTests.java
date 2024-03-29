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
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import de.uzk.hki.da.test.TC;
import de.uzk.hki.da.test.TESTHelper;
import de.uzk.hki.da.utils.Path;


/**
 * @author Daniel M. de Oliveira
 */
public class ObjectTests {
	
	private static final String A_TXT = "a.txt";
	private static final String PREMIS = "premis.xml";
	private static final String identifier = "123";
	private static final String _1_B_REP = "2000_00_00+00_00+b";
	private static final String _1_A_REP = "2000_00_00+00_00+a";
	private static final Path workAreaRootPath = Path.make(TC.TEST_ROOT_MODEL,"ObjectTests");
	private static DAFile f1;
	private static DAFile f2;
	private static Object o;
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

		f1 = new DAFile(_1_A_REP,A_TXT);
		f2 = new DAFile(_1_B_REP,A_TXT);
		
		o.getLatestPackage().getFiles().add(f1);
		o.getLatestPackage().getFiles().add(f2);
	}
	
	
	
	@Test
	public void testGetPathNewestRepNoRepsPresent(){
		o.getLatestPackage().getFiles().clear();

		try{
			o.getNameOfLatestBRep();
			fail();
		}catch(IllegalStateException e){
		}
	}
	
	@Test
	public void testGetLatestReturnsAttachedInstance(){
		
		assertEquals(f2,o.getLatest(A_TXT));
	}
	
	
	
	
	@Test 
	public void testGetLatest(){

		DAFile premis = new DAFile(_1_A_REP,PREMIS);
		o.getLatestPackage().getFiles().add(premis);
		
		assertEquals(
				premis,
				o.getLatest(PREMIS)
				);
	}
	
	
	
	
	@Test 
	public void testGetLatestNotPresent(){

		assertEquals(
				null,
				o.getLatest(PREMIS)
				);
	}
}
