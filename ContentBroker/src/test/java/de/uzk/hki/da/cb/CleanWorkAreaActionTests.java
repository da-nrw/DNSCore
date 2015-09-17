/*
  DA-NRW Software Suite | ContentBroker
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

package de.uzk.hki.da.cb;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import de.uzk.hki.da.grid.IrodsDistributedConversionAdapter;
import de.uzk.hki.da.test.TC;
import de.uzk.hki.da.utils.Path;

/**
 * @author Daniel M. de Oliveira
 */
public class CleanWorkAreaActionTests extends ConcreteActionUnitTest {

	@ActionUnderTest 
	CleanWorkAreaAction action = new CleanWorkAreaAction();

	private static final Path WORK_AREA_ROOT_PATH = Path.make(TC.TEST_ROOT_CB, "CleanWorkAreaAction"); 
	
	@Before
	public void setUp() {
		action.setDistributedConversionAdapter(mock(IrodsDistributedConversionAdapter.class));
		n.setWorkAreaRootPath(WORK_AREA_ROOT_PATH);
	}
	
	@Test
	public void implementation() throws IOException {
		action.implementation();
		assertTrue(action.getToCreate()!=null);
	}
	
	@Test
	public void rollback() throws Exception {
		action.implementation();
		action.rollback();
		assertTrue(action.getToCreate()==null);
	}
}
