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

import static de.uzk.hki.da.core.C.FILE_EXTENSION_XML;
import static org.junit.Assert.assertFalse;
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
import de.uzk.hki.da.model.WorkArea;
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

	private static final String PREMIS = "premis";
	private static final String _140849_TIF = "140849.tif";
	private static final Path TESTDIR = new RelativePath(TC.TEST_ROOT_CB, "FetchPIPsAction");
	private static final String UNDERSCORE = "_";
	private static DistributedConversionAdapter distributedConversionAdapter;
	
	
	@Before
	public void setUp() throws IOException {
		n.setWorkAreaRootPath(TESTDIR);
		
		distributedConversionAdapter = mock(FakeDistributedConversionAdapter.class);
		FileUtils.copyDirectory(Path.makeFile( TESTDIR, WorkArea.PIPS+UNDERSCORE ), Path.makeFile( TESTDIR, WorkArea.PIPS ));
		
		action.setDistributedConversionAdapter(distributedConversionAdapter);
	}
	
	
	
	@After
	public void cleanUp() {
		FileUtils.deleteQuietly(Path.makeFile( TESTDIR, WorkArea.PIPS ));
	}
	
	
	
	@Test
	public void overwriteExistingPIPs() throws FileNotFoundException, IOException{
		assertFalse(makeMetadataFile(PREMIS,WorkArea.PUBLIC).exists());
		assertFalse(makeMetadataFile(PREMIS,WorkArea.WA_INSTITUTION ).exists());
		action.implementation();
		assertTrue(makeMetadataFile(PREMIS,WorkArea.PUBLIC).exists());
		assertTrue(makeMetadataFile(PREMIS,WorkArea.WA_INSTITUTION ).exists());
	}
	
	@Test
	public void movePIPs() throws FileNotFoundException, IOException {
		action.implementation();
		assertFalse(makePIPSourceFolder(WorkArea.PUBLIC).exists());
		assertFalse(makePIPSourceFolder(WorkArea.WA_INSTITUTION).exists());
		assertTrue(makePIPFolder(WorkArea.PUBLIC).exists());
		assertTrue(makePIPFolder(WorkArea.WA_INSTITUTION).exists());
	}
	
	@Test
	public void rollback() throws Exception {
		simulateImpl();
		action.rollback();
		assertTrue(makeFileInSrcFolder(_140849_TIF, WorkArea.PUBLIC).exists());
		assertTrue(makeFileInSrcFolder(_140849_TIF, WorkArea.WA_INSTITUTION).exists());
		assertTrue(makeFileInSrcFolder(PREMIS+FILE_EXTENSION_XML, WorkArea.PUBLIC).exists());
		assertTrue(makeFileInSrcFolder(PREMIS+FILE_EXTENSION_XML, WorkArea.WA_INSTITUTION).exists());
		assertFalse(makePIPFolder(WorkArea.PUBLIC).exists());
		assertFalse(makePIPFolder(WorkArea.WA_INSTITUTION).exists());
	}


	private void simulateImpl() throws IOException {
		FileUtils.deleteDirectory(makePIPFolder(WorkArea.PUBLIC));
		FileUtils.deleteDirectory(makePIPFolder(WorkArea.WA_INSTITUTION));
		FileUtils.moveDirectory(
				makePIPSourceFolder(WorkArea.PUBLIC), 
				makePIPFolder(WorkArea.PUBLIC));
		FileUtils.moveDirectory(
				makePIPSourceFolder(WorkArea.WA_INSTITUTION), 
				makePIPFolder(WorkArea.WA_INSTITUTION));
		FileUtils.deleteDirectory(makePIPSourceFolder(WorkArea.PUBLIC));
		FileUtils.deleteDirectory(makePIPSourceFolder(WorkArea.WA_INSTITUTION));
	}
	
	
	
	
	public File makeMetadataFile(String fileName,String pipType) {
		return Path.makeFile(n.getWorkAreaRootPath(),WorkArea.PIPS,pipType,o.getContractor().getShort_name(),
				o.getIdentifier(),fileName+FILE_EXTENSION_XML);
	}
	
	public File makeFileInSrcFolder(String fileName,String pipType) {
		return Path.makeFile(n.getWorkAreaRootPath(),WorkArea.PIPS,pipType,o.getContractor().getShort_name(),
				o.getIdentifier()+UNDERSCORE+o.getPackages().get(0).getName(),fileName);
	}
	
	private File makePIPFolder(String pipType) {
		return Path.makeFile(n.getWorkAreaRootPath(),WorkArea.PIPS,pipType,o.getContractor().getShort_name(),o.getIdentifier());
	}
	
	private File makePIPSourceFolder(String pipType) {
		return Path.makeFile(n.getWorkAreaRootPath(),WorkArea.PIPS,pipType,o.getContractor().getShort_name(),o.getIdentifier()+UNDERSCORE+o.getLatestPackage().getId());
	}
}
