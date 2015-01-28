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

import static de.uzk.hki.da.core.C.WA_INSTITUTION;
import static de.uzk.hki.da.core.C.WA_PIPS;
import static de.uzk.hki.da.core.C.WA_PUBLIC;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.uzk.hki.da.grid.DistributedConversionAdapter;
import de.uzk.hki.da.grid.FakeDistributedConversionAdapter;
import de.uzk.hki.da.test.TC;
import de.uzk.hki.da.util.Path;
import de.uzk.hki.da.util.RelativePath;

/**
 * @author Polina Gubaidullina
 * @author Daniel M. de Oliveira
 */
public class FetchPIPsActionTest extends ConcreteActionUnitTest {
	

	@ActionUnderTest
	static FetchPIPsAction action = new FetchPIPsAction();

	private static final Path TESTDIR = new RelativePath(TC.TEST_ROOT_CB, "FetchPIPsAction");
	private static final String UNDERSCORE = "_";
	private static DistributedConversionAdapter distributedConversionAdapter;
	
	
	@Before
	public void setUp() throws IOException {
		n.setWorkAreaRootPath(TESTDIR);
		
		distributedConversionAdapter = mock(FakeDistributedConversionAdapter.class);
		FileUtils.copyDirectory(Path.makeFile( TESTDIR, WA_PIPS+UNDERSCORE ), Path.makeFile( TESTDIR, WA_PIPS ));
		
		action.setDistributedConversionAdapter(distributedConversionAdapter);
	}
	
	
	
	@After
	public void cleanUp() {
		FileUtils.deleteQuietly(Path.makeFile( TESTDIR, WA_PIPS ));
	}
	
	
	
	@Test
	public void renamePIPs() throws FileNotFoundException, IOException{
		action.implementation();
		assertTrue(makePIPFolder( WA_PUBLIC ).exists());
		assertTrue(makePIPFolder( WA_INSTITUTION ).exists());
	}
	
	
	
	private File makePIPFolder(String pipType) {
		return Path.makeFile(n.getWorkAreaRootPath(),WA_PIPS,pipType,o.getContractor().getShort_name(),o.getIdentifier());
	}
}
