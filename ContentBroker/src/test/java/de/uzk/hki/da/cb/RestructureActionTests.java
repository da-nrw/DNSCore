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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyBoolean;
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

import de.uzk.hki.da.core.IngestGate;
import de.uzk.hki.da.core.SubsystemNotAvailableException;
import de.uzk.hki.da.core.UserException;
import de.uzk.hki.da.format.ConfigurableFileFormatFacade;
import de.uzk.hki.da.format.FileFormatException;
import de.uzk.hki.da.format.FileFormatFacade;
import de.uzk.hki.da.format.FileWithFileFormat;
import de.uzk.hki.da.grid.FakeGridFacade;
import de.uzk.hki.da.model.DAFile;
import de.uzk.hki.da.model.Package;
import de.uzk.hki.da.repository.RepositoryException;
import de.uzk.hki.da.service.HibernateUtil;
import de.uzk.hki.da.service.JmsMessageServiceHandler;
import de.uzk.hki.da.test.TC;
import de.uzk.hki.da.utils.Path;

/**
 * @author Daniel M. de Oliveira
 */
public class RestructureActionTests extends ConcreteActionUnitTest{

	@ActionUnderTest
	RestructureAction action = new RestructureAction();

	private static final String IDENTIFIER = "identifier";
	private static final Path WORK_AREA_ROOT_PATH = Path.make(TC.TEST_ROOT_CB,"RestructureActionTests");
	private static final Path TEST_CONTRACTOR_WORK_FOLDER = Path.make(WORK_AREA_ROOT_PATH,"work","TEST");
	private static final Path DATA_FOLDER = Path.make(TEST_CONTRACTOR_WORK_FOLDER,IDENTIFIER,"data");
	
	private FakeGridFacade grid;
	private IngestGate gate;
	private Package pkg1;
	private Package pkg2;
	
	
	
	
	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws IOException, FileFormatException{
		HibernateUtil.init("src/main/xml/hibernateCentralDB.cfg.xml.inmem");
		
		FileUtils.copyDirectory(Path.makeFile(TEST_CONTRACTOR_WORK_FOLDER,IDENTIFIER+"_"), 
				Path.makeFile(TEST_CONTRACTOR_WORK_FOLDER,IDENTIFIER));
		
		n.setWorkAreaRootPath(WORK_AREA_ROOT_PATH);
		
		action.setLocalNode(n);
		grid = new FakeGridFacade();
		grid.setGridCacheAreaRootPath("/tmp/");
		action.setGridRoot(grid);
	
		

		gate = mock(IngestGate.class);
		when(gate.canHandle((Long)anyObject())).thenReturn(true);
		JmsMessageServiceHandler jms = mock(JmsMessageServiceHandler.class);
		action.setJmsMessageServiceHandler(jms);
		action.setIngestGate(gate);
		
		
		FileFormatFacade ffs = mock(ConfigurableFileFormatFacade.class);
	
		DAFile file = new DAFile("rep+a","140849.tif");
		file.setFormatPUID("fmt/353");
		List<FileWithFileFormat> files = new ArrayList<FileWithFileFormat>(); files.add(file);
		when( ffs.identify((Path)anyObject(),(List<FileWithFileFormat>)anyObject(),anyBoolean()) ).thenReturn(files);
		action.setFileFormatFacade(ffs);
	}
	
	
	
	
	@After
	public void tearDown(){
		FileUtils.deleteQuietly(Path.makeFile(TEST_CONTRACTOR_WORK_FOLDER,IDENTIFIER));
	}
	
	
	
	
	@Test
	public void implementation() throws FileNotFoundException, UserException, IOException, RepositoryException, SubsystemNotAvailableException{
		
		action.implementation();
		
		assertTrue(Path.makeFile(DATA_FOLDER,j.getRep_name()+"a").exists());
		assertTrue(Path.makeFile(DATA_FOLDER,j.getRep_name()+"a","vda3.XML").exists());
		assertTrue(Path.makeFile(DATA_FOLDER,"jhove_temp").exists());
		
		assertTrue(o.getLatestPackage().getFiles().contains(new DAFile(j.getRep_name()+"a","vda3.XML")));
	}
	
	
	
	
	@Test
	public void rollbackToPreviousState() throws Exception {
		
		action.implementation();
		action.rollback();
		
		assertTrue(dataFolderIsInOriginalState());
		
	}
	
	
	@Test
	public void dontMoveAnythingWhenNoSpaceForDeltaPackages() 
			throws FileNotFoundException, UserException, IOException, RepositoryException, SubsystemNotAvailableException {

		putAIPAndPrepareForDeltaIngest();
		
		when(gate.canHandle((Long)anyObject())).thenReturn(false);
		assertFalse(action.implementation());
		
		assertTrue(dataFolderIsInOriginalState());
	}
	
	
	
	@Test
	public void testDelta() throws FileNotFoundException, UserException, IOException, RepositoryException, SubsystemNotAvailableException{
		
		putAIPAndPrepareForDeltaIngest();
		
		action.implementation();
		
		assertTrue(Path.makeFile(DATA_FOLDER,j.getRep_name()+"a").exists());
		assertTrue(Path.makeFile(DATA_FOLDER,j.getRep_name()+"a","vda3.XML").exists());
		assertTrue(Path.makeFile(DATA_FOLDER,"2014_09_12+11_32+a","premis.xml").exists());
		assertTrue(Path.makeFile(DATA_FOLDER,"2014_09_12+11_32+b","premis.xml").exists());
		assertTrue(Path.makeFile(DATA_FOLDER,"jhove_temp").exists());
		
		
		for (Package p:o.getPackages()){
			System.out.println(p.getName());
			for (DAFile f:p.getFiles())
				System.out.println(":"+f);
		}
		assertTrue(pkg2.getFiles().contains(new DAFile(j.getRep_name()+"a","vda3.XML")));
		assertTrue(pkg1.getFiles().contains(new DAFile("2014_09_12+11_32+a","premis.xml")));
		assertTrue(pkg1.getFiles().contains(new DAFile("2014_09_12+11_32+b","premis.xml")));
		assertTrue(pkg1.getFiles().contains(new DAFile("2014_09_12+11_32+a","SIP-Builder Anleitung.pdf")));
		
	}
	
	
	
	
	@Test
	public void rollbackToPreviousStateWithDelta() throws Exception {
		
		putAIPAndPrepareForDeltaIngest();
		
		action.implementation();
		action.rollback();
		
		assertTrue(dataFolderIsInOriginalState());
	}




	private void putAIPAndPrepareForDeltaIngest() throws IOException {
		
		grid.put(Path.makeFile(TEST_CONTRACTOR_WORK_FOLDER,"identifier.pack_1.tar"), "TEST/identifier/identifier.pack_1.tar", null, null);
		
		pkg1 = o.getLatestPackage();
		pkg2 = new Package();
		pkg2.setName("2");
		o.getPackages().add(pkg2);
	}




	private boolean dataFolderIsInOriginalState() {
		if (! Path.makeFile(DATA_FOLDER,"vda3.XML").exists()) return false;
		String files[] = Path.makeFile(DATA_FOLDER).list();
		if (! (files.length==1)) return false;
		return true;
	}
	
	
	
	
}
