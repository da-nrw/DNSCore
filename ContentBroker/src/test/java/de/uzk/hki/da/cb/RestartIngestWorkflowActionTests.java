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

import static de.uzk.hki.da.core.C.TEST_USER_SHORT_NAME;
import static de.uzk.hki.da.core.C.WA_DATA;
import static de.uzk.hki.da.core.C.WA_INSTITUTION;
import static de.uzk.hki.da.core.C.WA_PIPS;
import static de.uzk.hki.da.core.C.WA_PUBLIC;
import static de.uzk.hki.da.core.C.WA_WORK;
import static de.uzk.hki.da.test.TC.TEST_ROOT_CB;
import static de.uzk.hki.da.test.TC.URN;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.uzk.hki.da.model.ConversionInstruction;
import de.uzk.hki.da.model.DAFile;
import de.uzk.hki.da.model.Event;
import de.uzk.hki.da.util.Path;

/**
 * Tests RestartIngestWorkflowAction
 * 
 * @author Daniel M. de Oliveira
 */
public class RestartIngestWorkflowActionTests extends ConcreteActionUnitTest{
	
	private static final String REP_NAME = "2012_12_01+12_01_12+";
	private static final String UNDERSCORE = "_";
	private final Path WORK_AREA_ROOT_PATH = Path.make(
			TEST_ROOT_CB,"RestartIngestWorkflowAction");
	private final Path contractorFolder = Path.make(
			WORK_AREA_ROOT_PATH,WA_WORK,TEST_USER_SHORT_NAME);
	private final Path pipsFolder = Path.make(
			WORK_AREA_ROOT_PATH,WA_PIPS);

	@ActionUnderTest
	RestartIngestWorkflowAction action = new RestartIngestWorkflowAction();
	
	@Before
	public void setUp() throws IOException{
		FileUtils.copyDirectory(
				Path.makeFile(WORK_AREA_ROOT_PATH,WA_WORK+UNDERSCORE), 
				Path.makeFile(WORK_AREA_ROOT_PATH,WA_WORK));
		
		FileUtils.copyDirectory(Path.makeFile(WORK_AREA_ROOT_PATH,UNDERSCORE+WA_PIPS), 
				pipsFolder.toFile());
		
		n.setWorkAreaRootPath(WORK_AREA_ROOT_PATH);
		j.setRep_name(REP_NAME);
	}

	@After
	public void tearDown() throws IOException{
		FileUtils.deleteDirectory(Path.makeFile(WORK_AREA_ROOT_PATH,WA_WORK));
		FileUtils.deleteDirectory(pipsFolder.toFile());
	}
	
	@Test
	public void leaveOnlySIPContents() throws IOException{
		action.implementation();
		assertTrue(Path.makeFile(contractorFolder,o.getIdentifier(),WA_DATA,"contentLatest.txt").exists());
	}
	
	
	
	
	@Test
	public void emptyPIPFolders() throws IOException{
		action.implementation();
		assertFalse(makePIPSourceFolder(WA_INSTITUTION).exists());
		assertFalse(makePIPSourceFolder(WA_PUBLIC).exists());
	}
	
	
	private File makePIPSourceFolder(String pipType) {
		return Path.makeFile(n.getWorkAreaRootPath(),WA_PIPS,pipType,o.getContractor().getShort_name(),o.getIdentifier()+UNDERSCORE+o.getLatestPackage().getId());
	}
	
	@Test
	public void resetURNIfNotDelta() throws IOException{
		// just to prevent changes in ActionTest will influence the test result
		assertEquals(URN, o.getUrn()); 
		
		action.implementation();
		assertEquals(null, o.getUrn());
	}
	
	
	@Test
	public void rollback() throws Exception {
		action.implementation();
		action.rollback();
	}
	
	@Test
	public void rollbackNotPossibleTempLeftOver() throws IOException {
		action.implementation();
		Path.makeFile(o.getPath(),"_temp").mkdirs();
		try {
			action.rollback();
			fail();
		} catch (Exception expected) {}
	}
	
	@Test
	public void rollbackNotPossibleARepExists() throws IOException {
		
		try {
			action.rollback();
			fail();
		} catch (Exception expected) {}
	}
	
	@Test
	public void rollbackNotPossibleNoRepName() throws IOException {
		
		j.setRep_name(null);
		try {
			action.rollback();
			fail();
		} catch (Exception expected) {}
	}
	
	
	
	
	
	@Test
	public void clearEventsAndFiles() throws IOException{
		DAFile src = new DAFile(null,"","");
		DAFile trg = new DAFile(null,"","");
		o.getLatestPackage().getFiles().add(src);
		o.getLatestPackage().getFiles().add(trg);
		Event e = new Event();
		e.setSource_file(src);
		e.setTarget_file(trg);
		o.getLatestPackage().getEvents().add(e);
		ConversionInstruction ci = new ConversionInstruction();
		j.getConversion_instructions().add(ci);
		
		action.implementation();
		assertTrue(o.getLatestPackage().getEvents().isEmpty());
		assertTrue(o.getLatestPackage().getFiles().isEmpty());
		assertTrue(j.getConversion_instructions().isEmpty());
	}
}
