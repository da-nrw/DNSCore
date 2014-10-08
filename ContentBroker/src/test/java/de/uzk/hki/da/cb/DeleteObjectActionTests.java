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

import de.uzk.hki.da.core.RelativePath;
import de.uzk.hki.da.core.UserException;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.model.Package;
import de.uzk.hki.da.test.TESTHelper;

/**
 * @author Daniel M. de Oliveira
 */
public class DeleteObjectActionTests {

	private String workAreaRootPath = "src/test/resources/cb/DeleteObjectActionTests/";
	private String ingestAreaRootPath = "src/test/resources/cb/DeleteObjectActionTests/ingest/";
	private DeleteObjectAction action;
	
	@Before
	public void setUp() throws IOException{
		new File(workAreaRootPath+"work/TEST/123/data").mkdirs();
		new File(workAreaRootPath+"work/TEST/abc.txt").createNewFile();
		new File(ingestAreaRootPath+"TEST/abc.txt").createNewFile();
	}
	
	@After
	public void tearDown() throws IOException{
		FileUtils.deleteDirectory(new File(workAreaRootPath+"work/TEST/123/data"));
		new File(workAreaRootPath+"work/TEST/abc.txt").delete();
		new File(ingestAreaRootPath+"TEST/abc.txt").delete();
	}
	
	@Test
	public void cleanWorkArea() throws FileNotFoundException, UserException, IOException{
		Object o = TESTHelper.setUpObject("123", new RelativePath(workAreaRootPath));
		o.getTransientNodeRef().setIngestAreaRootPath(new RelativePath(ingestAreaRootPath));
		o.getLatestPackage().setContainerName("abc.txt");
		
		action = new DeleteObjectAction();
		action.setObject(o);
		action.implementation();
		
		assertFalse(new File(workAreaRootPath+"work/TEST/123/").exists());
		assertFalse(new File(workAreaRootPath+"work/TEST/abc.txt").exists());
		assertFalse(new File(ingestAreaRootPath+"TEST/abc.txt").exists());
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
		action = new DeleteObjectAction();
		action.setObject(TESTHelper.setUpObject("123", new RelativePath(workAreaRootPath), new RelativePath(ingestAreaRootPath),new RelativePath(ingestAreaRootPath)));
		action.implementation();

		assertTrue(action.DELETEOBJECT);
	}
	
	@Test
	public void testSetDeletePackage() throws FileNotFoundException, UserException, IOException{
		action = new DeleteObjectAction();
		Object o = TESTHelper.setUpObject("123", new RelativePath(workAreaRootPath), new RelativePath(ingestAreaRootPath), new RelativePath(ingestAreaRootPath));
		Package p2 = new Package(); p2.setName("2"); p2.setContainerName("testcontainer.tgz");
		o.getPackages().add(p2);
		action.setObject(o);
		action.implementation();

		assertFalse(action.DELETEOBJECT);
		assertEquals(1,o.getPackages().size());
		assertEquals("1",o.getPackages().get(0).getName());
	}
}
