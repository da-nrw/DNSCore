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

package de.uzk.hki.da.core;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.uzk.hki.da.utils.C;
import de.uzk.hki.da.utils.Path;
import de.uzk.hki.da.utils.TC;

/**
 * 
 * @author Daniel M. de Oliveira
 */
public class CTDiagnosticsTests {

	private static final File TEST_PACKAGE_SRC = Path.makeFile(TC.TEST_ROOT_AT,"AT_CON2.tgz");
	
	
	@Before
	public void setUp() throws IOException{
		C.CONF.toFile().mkdirs();
		FileUtils.copyFile(TC.CONFIG_PROPS_CI, C.CONFIG_PROPS);
		FileUtils.copyFile(TEST_PACKAGE_SRC, C.BASIC_TEST_PACKAGE);
	}
	
	@After
	public void tearDown(){
		FileUtils.deleteQuietly(C.CONF.toFile());
	}
	
	
	@Test
	public void stubDiagnostics() throws IOException{
		
		assertEquals(new Integer(0),Diagnostics.run());
	}
}
