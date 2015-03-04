/*
  DA-NRW Software Suite | ContentBroker
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

package de.uzk.hki.da.cb;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import de.uzk.hki.da.model.DAFile;
import de.uzk.hki.da.test.TC;
import de.uzk.hki.da.util.Path;

/**
 * @author Daniel M. de Oliveira
 */
public class RegisterURNActionTest extends ConcreteActionUnitTest {
	
	@ActionUnderTest
	RegisterURNAction action = new RegisterURNAction();
	
	private static final Path WORK_AREA_ROOT_PATH = Path.make(TC.TEST_ROOT_CB,"RegisterURNAction"); 
	
	
	@Before
	public void setUp() {
		n.setWorkAreaRootPath(WORK_AREA_ROOT_PATH);
		
		DAFile premis = new DAFile(o.getLatestPackage(),"2012_12_12+12_12_12+a","premis.xml");
		o.getLatestPackage().getFiles().add(premis);
	}
	
	@Test
	public void newIdentifier() {
		o.setUrn(null);
		action.implementation();
		assertEquals(o.getUrn(),ps.getUrnNameSpace()+"-"+o.getIdentifier());
	}
}
