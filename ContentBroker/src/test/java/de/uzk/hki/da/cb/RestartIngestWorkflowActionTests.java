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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.uzk.hki.da.core.C;
import de.uzk.hki.da.core.Path;
import de.uzk.hki.da.core.RelativePath;
import de.uzk.hki.da.model.ConversionInstruction;
import de.uzk.hki.da.model.DAFile;
import de.uzk.hki.da.model.Event;
import de.uzk.hki.da.test.TC;

/**
 * Tests RestartIngestWorkflowAction
 * 
 * @author Daniel M. de Oliveira
 */
public class RestartIngestWorkflowActionTests extends ConcreteActionUnitTest{
	
	private final Path workAreaRootPath = new RelativePath(
			TC.TEST_ROOT_CB,"RestartIngestWorkflowActionTests");
	private final Path contractorFolder = Path.make(
			workAreaRootPath,C.WA_WORK,C.TEST_USER_SHORT_NAME);
	private final Path objectFolder = Path.make(
			contractorFolder,TC.IDENTIFIER);
	private final Path pipsFolder = Path.make(
			workAreaRootPath,C.WA_PIPS);

	@ActionUnderTest
	RestartIngestWorkflowAction action = new RestartIngestWorkflowAction();
	
	@Before
	public void setUp() throws IOException{
		FileUtils.copyDirectory(
				Path.makeFile(contractorFolder,"_"+TC.IDENTIFIER), 
				new File(objectFolder.toString()));
		
		FileUtils.copyDirectory(Path.makeFile(workAreaRootPath,"_"+C.WA_PIPS), 
				pipsFolder.toFile());
		
		n.setWorkAreaRootPath(workAreaRootPath);

	}

	@After
	public void tearDown() throws IOException{
		FileUtils.deleteDirectory(objectFolder.toFile());
		FileUtils.deleteDirectory(pipsFolder.toFile());
	}
	
	@Test
	public void leaveOnlySIPContents() throws IOException{
		action.implementation();
		assertTrue(Path.makeFile(objectFolder,"data","contentLatest.txt").exists());
	}
	
	@Test
	public void emptyPIPFolders() throws IOException{
		action.implementation();
		assertFalse(Path.makeFile(pipsFolder,C.WA_INSTITUTION,o.getContractor().getShort_name(),TC.IDENTIFIER+"_1").exists());
		assertFalse(Path.makeFile(pipsFolder,C.WA_PUBLIC,o.getContractor().getShort_name(),TC.IDENTIFIER+"_1").exists());
	}
	
	
	@Test
	public void resetURNIfNotDelta() throws IOException{
		// just to prevent changes in ActionTest will influence the test result
		assertEquals("urn", o.getUrn()); 
		
		action.implementation();
		assertEquals(null, o.getUrn());
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
