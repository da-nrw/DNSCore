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

import de.uzk.hki.da.core.HibernateUtil;
import de.uzk.hki.da.core.IngestGate;
import de.uzk.hki.da.core.Path;
import de.uzk.hki.da.core.UserException;
import de.uzk.hki.da.ff.FileFormatException;
import de.uzk.hki.da.ff.FileFormatFacade;
import de.uzk.hki.da.ff.IFileWithFileFormat;
import de.uzk.hki.da.ff.StandardFileFormatFacade;
import de.uzk.hki.da.grid.FakeGridFacade;
import de.uzk.hki.da.model.DAFile;
import de.uzk.hki.da.model.Job;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.model.Package;
import de.uzk.hki.da.model.PreservationSystem;
import de.uzk.hki.da.repository.RepositoryException;
import de.uzk.hki.da.test.TC;
import de.uzk.hki.da.test.TESTHelper;

/**
 * @author Daniel M. de Oliveira
 */
public class RestructureActionTests {

	private static final String IDENTIFIER = "identifier";
	private static final Path WORK_AREA_ROOT = Path.make(TC.TEST_ROOT_CB,"RestructureActionTests");
	private static final Path TEST_CONTRACTOR_WORK_FOLDER = Path.make(WORK_AREA_ROOT,"work","TEST");
	private static final Path DATA_FOLDER = Path.make(TEST_CONTRACTOR_WORK_FOLDER,IDENTIFIER,"data");
	private RestructureAction action;
	private Job job;
	private Object object;
	private FakeGridFacade grid;
	private Package p1;
	
	
	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws IOException, FileFormatException{
		HibernateUtil.init("src/main/xml/hibernateCentralDB.cfg.xml.inmem");
		
		PreservationSystem pSystem = new PreservationSystem();
		
		FileUtils.copyDirectory(Path.makeFile(TEST_CONTRACTOR_WORK_FOLDER,IDENTIFIER+"_"), 
				Path.makeFile(TEST_CONTRACTOR_WORK_FOLDER,IDENTIFIER));
		Path.makeFile(TEST_CONTRACTOR_WORK_FOLDER,IDENTIFIER,"loadedAIPs").mkdirs();

		object = TESTHelper.setUpObject(IDENTIFIER, WORK_AREA_ROOT);
		p1 = object.getLatestPackage();
		
		
		job = new Job();
		job.setObject(object);
		
		grid = new FakeGridFacade();
		grid.setGridCacheAreaRootPath("/tmp/");
		
		action = new RestructureAction();
		action.setGridRoot(grid);
		action.setObject(object);
		action.setJob(job);
		action.setPSystem(pSystem);
		
		IngestGate gate = mock(IngestGate.class);
		when(gate.canHandle((Long)anyObject())).thenReturn(true);
		action.setIngestGate(gate);

		FileFormatFacade ffs = mock(StandardFileFormatFacade.class);
	
		DAFile file = new DAFile(object.getLatestPackage(),"rep+a","140849.tif");
		file.setFormatPUID("fmt/353");
		List<IFileWithFileFormat> files = new ArrayList<IFileWithFileFormat>(); files.add(file);
		when( ffs.identify((List<IFileWithFileFormat>)anyObject()) ).thenReturn(files);
		action.setFileFormatFacade(ffs);
		
		
	}
	
	@After
	public void tearDown(){
		FileUtils.deleteQuietly(Path.makeFile(TEST_CONTRACTOR_WORK_FOLDER,IDENTIFIER));
	}
	
	@Test
	public void test() throws FileNotFoundException, UserException, IOException, RepositoryException{
		
		action.implementation();
		
		assertTrue(Path.makeFile(DATA_FOLDER,job.getRep_name()+"a").exists());
		assertTrue(Path.makeFile(DATA_FOLDER,job.getRep_name()+"a","vda3.XML").exists());
		assertTrue(Path.makeFile(DATA_FOLDER,"jhove_temp").exists());
		
		assertTrue(p1.getFiles().contains(new DAFile(null,job.getRep_name()+"a","vda3.XML")));
	}
	
	
	@Test
	public void testDelta() throws FileNotFoundException, UserException, IOException, RepositoryException{
		
		grid.put(Path.makeFile(TEST_CONTRACTOR_WORK_FOLDER,"identifier.pack_1.tar"), "TEST/identifier/identifier.pack_1.tar", null);
		
		Package p2 = new Package();
		p2.setName("2");
		object.getPackages().add(p2);
		p2.setTransientBackRefToObject(object);
		
		action.implementation();
		
		assertTrue(Path.makeFile(DATA_FOLDER,job.getRep_name()+"a").exists());
		assertTrue(Path.makeFile(DATA_FOLDER,job.getRep_name()+"a","vda3.XML").exists());
		assertTrue(Path.makeFile(DATA_FOLDER,"2014_09_12+11_32+a","premis.xml").exists());
		assertTrue(Path.makeFile(DATA_FOLDER,"2014_09_12+11_32+b","premis.xml").exists());
		assertTrue(Path.makeFile(DATA_FOLDER,"jhove_temp").exists());
		
		
		for (Package p:object.getPackages()){
			System.out.println(p.getName());
			for (DAFile f:p.getFiles())
				System.out.println(":"+f);
		}
		assertTrue(p2.getFiles().contains(new DAFile(null,job.getRep_name()+"a","vda3.XML")));
		assertTrue(p1.getFiles().contains(new DAFile(null,"2014_09_12+11_32+a","premis.xml")));
		assertTrue(p1.getFiles().contains(new DAFile(null,"2014_09_12+11_32+b","premis.xml")));
		assertTrue(p1.getFiles().contains(new DAFile(null,"2014_09_12+11_32+a","SIP-Builder Anleitung.pdf")));
		
	}
	
	
	
	
	
}
