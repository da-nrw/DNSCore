/*
  DA-NRW Software Suite | ContentBroker
  Copyright (C) 2014 LVR-Infokom
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

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static de.uzk.hki.da.core.C.*;
import de.uzk.hki.da.core.IngestGate;
import de.uzk.hki.da.grid.FakeGridFacade;
import de.uzk.hki.da.grid.GridFacade;
import de.uzk.hki.da.service.JmsMessageServiceHandler;
import de.uzk.hki.da.test.TC;
import de.uzk.hki.da.util.Path;

/**
 * @author Daniel M. de Oliveira
 */
public class ObjectToWorkAreaActionTests extends ConcreteActionUnitTest{

	private static final String UNDERSCORE = "_";

	@ActionUnderTest
	ObjectToWorkAreaAction action = new ObjectToWorkAreaAction();
	
	private static final Path WORK_AREA_ROOT_PATH = Path.make(TC.TEST_ROOT_CB,"ObjectToWorkAreaAction");

	private IngestGate ig;

	private GridFacade grid;
	
	// the test file WORK_AREA_ROOT_PATH/work/TEST/identifier/loadedAIPs/identifer.pack_1.tar 
	// is placed on purpose onto the right place to simulate the behavior of grid.get().
	
	@Before
	public void setUp() throws IOException {
		grid = mock(FakeGridFacade.class);
		action.setGridFacade(grid);
		action.setJmsMessageServiceHandler(mock(JmsMessageServiceHandler.class));
		n.setWorkAreaRootPath(WORK_AREA_ROOT_PATH);
		ig = mock(IngestGate.class);
		action.setIngestGate(ig);
		when(ig.canHandle((Long)anyObject())).thenReturn(true);
		FileUtils.copyDirectory(Path.makeFile(WORK_AREA_ROOT_PATH,WA_WORK+UNDERSCORE), Path.makeFile(WORK_AREA_ROOT_PATH,WA_WORK));
	}
	
	@After
	public void tearDown() throws IOException {
		FileUtils.deleteDirectory(Path.makeFile(WORK_AREA_ROOT_PATH,WA_WORK));
	}
	
	
	@Test
	public void implementation() {
		action.implementation();
		assertTrue(Path.makeFile(o.getDataPath()).exists());
		assertTrue(Path.makeFile(o.getDataPath(),"2014_07_18+11_38+a").exists());
		assertTrue(Path.makeFile(o.getDataPath(),"2014_07_18+11_38+b").exists());
	}
	
	@Test
	public void noSpaceLeft() {
		when(ig.canHandle((Long)anyObject())).thenReturn(false);
		assertFalse(action.implementation());
	}
	
	@Test
	public void errorWhileRetrievingFile() throws IOException {
		doThrow(new IOException("io")).when(grid).get((File)anyObject(), anyString());
		try {
			action.implementation();
			fail();
		}catch(RuntimeException expected) {}
	}
	
	@Test
	public void rollback() throws Exception {
		action.implementation();
		action.rollback();
		assertFalse(Path.makeFile(o.getDataPath()).exists());
	}
}
