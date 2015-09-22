/*
  DA-NRW Software Suite | ContentBroker
  Copyright (C) 2013 Historisch-Kulturwissenschaftliche Informationsverarbeitung
  Universität zu Köln

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

import static de.uzk.hki.da.test.TC.TEST_ROOT_CB;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.uzk.hki.da.core.UserException;
import de.uzk.hki.da.model.Package;
import de.uzk.hki.da.model.WorkArea;
import de.uzk.hki.da.utils.Path;

/**
 * @author Daniel M. de Oliveira
 */
public class DeleteObjectActionTests extends ConcreteActionUnitTest{

	@ActionUnderTest
	DeleteObjectAction action = new DeleteObjectAction();
	
	private Path WORK_AREA_ROOT_PATH = Path.make(TEST_ROOT_CB,"DeleteObjectAction");
	private Path INGEST_AREA_ROOT_PATH = Path.make(TEST_ROOT_CB,"DeleteObjectAction/ingest");
	
	@Before
	public void setUp() throws IOException{
		Path.makeFile(WORK_AREA_ROOT_PATH,"work/TEST/123/data").mkdirs();
		Path.makeFile(WORK_AREA_ROOT_PATH,"work/TEST/abc.txt").createNewFile();
		Path.makeFile(INGEST_AREA_ROOT_PATH,"TEST/abc.txt").createNewFile();
		n.setWorkAreaRootPath(WORK_AREA_ROOT_PATH);
		n.setIngestAreaRootPath(INGEST_AREA_ROOT_PATH);
	}
	
	@After
	public void tearDown() throws IOException{
		FileUtils.deleteDirectory(Path.makeFile(WORK_AREA_ROOT_PATH,WorkArea.WORK,"TEST/identifier/data"));
		Path.makeFile(WORK_AREA_ROOT_PATH,WorkArea.WORK,"TEST/abc.txt").delete();
		Path.makeFile(INGEST_AREA_ROOT_PATH,"TEST/abc.txt").delete();
	}
	
	@Test
	public void cleanWorkArea() throws FileNotFoundException, UserException, IOException{
		o.getLatestPackage().setContainerName("abc.txt");
		action.implementation();
		
		assertFalse(Path.makeFile(WORK_AREA_ROOT_PATH,WorkArea.WORK,"TEST/identifier/").exists());
		assertFalse(Path.makeFile(WORK_AREA_ROOT_PATH,WorkArea.WORK,"TEST/abc.txt").exists());
		assertFalse(new File(INGEST_AREA_ROOT_PATH+"TEST/abc.txt").exists());
	}
	
	
	/**
	 * AbstractAction deletes the object from the DB if DELETEOBJECT is set to true. 
	 * 
	 * @throws FileNotFoundException
	 * @throws UserException
	 * @throws IOException
	 */
	@Test
	public void testSetDeleteObjectFlag() throws FileNotFoundException, UserException, IOException{
		action.implementation();
		assertTrue(action.isDELETEOBJECT());
	}
	
	@Test
	public void testSetDeletePackage() throws FileNotFoundException, UserException, IOException{
		Package p2 = new Package(); p2.setName("2"); p2.setContainerName("testcontainer.tgz");
		o.getPackages().add(p2);
		action.implementation();

		assertFalse(action.isDELETEOBJECT());
		assertEquals(1,o.getPackages().size());
		assertEquals("1",o.getPackages().get(0).getName());
	}
}
