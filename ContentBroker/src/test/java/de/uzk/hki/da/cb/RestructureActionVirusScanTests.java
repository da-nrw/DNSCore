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
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uzk.hki.da.core.UserException;
import de.uzk.hki.da.core.UserException.UserExceptionId;
import de.uzk.hki.da.format.ConfigurableFileFormatFacade;
import de.uzk.hki.da.format.FileFormatException;
import de.uzk.hki.da.format.FileFormatFacade;
import de.uzk.hki.da.format.FileWithFileFormat;
import de.uzk.hki.da.grid.FakeGridFacade;
import de.uzk.hki.da.model.DAFile;
import de.uzk.hki.da.service.HibernateUtil;
import de.uzk.hki.da.test.TC;
import de.uzk.hki.da.utils.Path;
 
/**
 * @author Gaby Bender
 *
 */
public class RestructureActionVirusScanTests extends ConcreteActionUnitTest{

	@ActionUnderTest
	RestructureAction action = new RestructureAction();
	
	/** The Constant logger. */
	static final Logger logger = LoggerFactory.getLogger(RestructureActionVirusScanTests.class);

	private static final String SIP_INPUT = "SipInput";
	private static final Path WORK_AREA_ROOT_PATH = Path.make(TC.TEST_ROOT_CB,"VirusScanActionTests");
	private static final Path TEST_CONTRACTOR_WORK_FOLDER = Path.make(WORK_AREA_ROOT_PATH,"work","TEST", "clamAV");
	
	private FakeGridFacade grid;
	
	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws IOException, FileFormatException{
		HibernateUtil.init("src/main/xml/hibernateCentralDB.cfg.xml.inmem");
		
		FileFormatFacade ffs = mock(ConfigurableFileFormatFacade.class);
		
		DAFile file = new DAFile("rep+a","140849.tif");
		file.setFormatPUID("fmt/353");
		List<FileWithFileFormat> files = new ArrayList<FileWithFileFormat>(); files.add(file);
		when( ffs.identify((Path)anyObject(),(List<FileWithFileFormat>)anyObject(),anyBoolean()) ).thenReturn(files);
		action.setFileFormatFacade(ffs);
		o.getContractor().setUseVirusScan(true);

	}
	
	
	@After
	public void tearDown(){
//		FolderUtils.deleteQuietlySafe(Path.makeFile(TEST_CONTRACTOR_WORK_FOLDER,SIP_INPUT));
	}
	
	@Test
	public void scanClamAvNok() throws Exception  {
		
		prepareScan( "virus");
		
		o.setIdentifier("clamAV/" + "virus/" + SIP_INPUT);
		boolean assertType = true;
		
		try {
			action.implementation();
			if (o.getLatestPackage().getEvents().size() > 0) {
				logger.error("Es sind folgende Events aufgetreten: " + o.getLatestPackage().getEvents());
				assertType = false;
			}
		} catch (UserException ue) {
			UserExceptionId id = ue.getUserExceptionId();
			assertType = false;
			switch (id) {
			case VIRUS_DETECTED:
				assertType = true;
			default:
				break;
			}
		}
		assertTrue( assertType);
	}

	
	@Test
	public void scanClamAvOk() throws Exception{
		
		prepareScan("noVirus");
		boolean assertType;
		o.setIdentifier("clamAV/" + "noVirus/" + SIP_INPUT);
		if (!action.implementation() ) { 
			if ( o.getLatestPackage().getEvents().size() > 0) {
				logger.error("Es sind folgende Events aufgetreten: " + o.getLatestPackage().getEvents());
			}
			assertType = false;
		} else {
			assertType = true;
		}
		
		assertTrue(assertType);
	}
	
	/**
	 * @author Gaby Bender
	 * @throws IOException
	 */
	private void prepareScan(String virus) throws IOException {
		
		FileUtils.copyDirectory(Path.makeFile(TEST_CONTRACTOR_WORK_FOLDER, virus, SIP_INPUT +"_"), 
				Path.makeFile(TEST_CONTRACTOR_WORK_FOLDER, SIP_INPUT));

		n.setWorkAreaRootPath(WORK_AREA_ROOT_PATH);
		action.setLocalNode(n);
		grid = new FakeGridFacade();
		grid.setGridCacheAreaRootPath("/tmp/");
		action.setGridRoot(grid);
	}
}
