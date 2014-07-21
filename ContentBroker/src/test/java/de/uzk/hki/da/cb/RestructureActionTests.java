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

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.uzk.hki.da.core.UserException;
import de.uzk.hki.da.format.FormatScanService;
import de.uzk.hki.da.grid.GridFacade;
import de.uzk.hki.da.grid.IrodsGridFacade;
import de.uzk.hki.da.model.DAFile;
import de.uzk.hki.da.model.Job;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.repository.RepositoryException;
import de.uzk.hki.da.utils.C;
import de.uzk.hki.da.utils.Path;
import de.uzk.hki.da.utils.TC;
import de.uzk.hki.da.utils.TESTHelper;

/**
 * @author Daniel M. de Oliveira
 */
public class RestructureActionTests {

	private static final String IDENTIFIER = "identifier";
	private static final Path WORK_AREA_ROOT = Path.make(TC.TEST_ROOT_CB,"RestructureActionTests");
	private static final Path TEST_CONTRACTOR_WORK_FOLDER = Path.make(WORK_AREA_ROOT,"work","TEST");
	private RestructureAction action;
	private Job job;
	private Object object;
	
	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws IOException{
		FileUtils.copyDirectory(Path.makeFile(TEST_CONTRACTOR_WORK_FOLDER,IDENTIFIER+"_"), Path.makeFile(TEST_CONTRACTOR_WORK_FOLDER,IDENTIFIER));
		
		object = TESTHelper.setUpObject(IDENTIFIER, WORK_AREA_ROOT);
		
		job = new Job();
		job.setObject(object);
		
		GridFacade grid = mock(IrodsGridFacade.class);
		action = new RestructureAction();
		action.setGridRoot(grid);
		action.setObject(object);
		action.setJob(job);
		
		
		
		FormatScanService ffs = mock(FormatScanService.class);
	
		DAFile file = new DAFile(object.getLatestPackage(),"rep+a","140849.tif");
		file.setFormatPUID("fmt/353");
		List<DAFile> files = new ArrayList<DAFile>(); files.add(file);
		when( ffs.identify((List<DAFile>)anyObject()) ).thenReturn(files);
		action.setFormatScanService(ffs);
		
	}
	
	@After
	public void tearDown(){
		FileUtils.deleteQuietly(Path.makeFile(TEST_CONTRACTOR_WORK_FOLDER,IDENTIFIER));
	}
	
	@Test
	public void test() throws FileNotFoundException, UserException, IOException, RepositoryException{
		
		action.implementation();
		
		assertTrue(Path.makeFile(TEST_CONTRACTOR_WORK_FOLDER,IDENTIFIER,C.DATA,job.getRep_name()+"a").exists());
		assertTrue(Path.makeFile(TEST_CONTRACTOR_WORK_FOLDER,IDENTIFIER,C.DATA,job.getRep_name()+"a","vda3.XML").exists());
	}
	
	@Test
	public void scanPuids() throws FileNotFoundException, UserException, IOException, RepositoryException{

		action.implementation();
		// TODO test retrieved format identifiers.
	}
	
	
	
}
