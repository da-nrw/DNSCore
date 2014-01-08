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
package de.uzk.hki.da.service;


import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.uzk.hki.da.db.CentralDatabaseDAO;
import de.uzk.hki.da.grid.IrodsSystemConnector;
import de.uzk.hki.da.model.Job;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.utils.TESTHelper;


/**
 * The Class RetrievePackagesHelperTest.
 */
public class RetrievePackagesHelperTest {

	/** The base path. */
	String basePath = "src/test/resources/cb/RetrievePackagesHelperTest/";
	
	/** The irods. */
	IrodsSystemConnector irods;
	
	/** The dao. */
	CentralDatabaseDAO dao;
	
	/** The urn. */
	String urn = "urn+nbn+de+danrw-1-2012091718884";
	
	/**
	 * Sets the up.
	 *
	 * @throws Exception the exception
	 */
	@Before
	public void setUp() throws Exception {
		 irods = mock (IrodsSystemConnector.class);
		 dao = mock(CentralDatabaseDAO.class);
		 
		 new File(basePath+"csn/1/data/existingAIPs").mkdirs();
		 FileUtils.copyFile(new File(basePath+"source/pack1.tar"), new File(basePath+"TEST/1/existingAIPs/urn.pack_1.tar"));
		 FileUtils.copyFile(new File(basePath+"source/pack2.tar"), new File(basePath+"TEST/1/existingAIPs/urn.pack_2.tar"));
		 FileUtils.copyFile(new File(basePath+"source/pack3.tar"), new File(basePath+"TEST/1/existingAIPs/urn.pack_3.tar"));
	}
	
	/**
	 * Tear down.
	 */
	@After
	public void tearDown() {
		try {
			FileUtils.deleteDirectory(new File(basePath+"TEST/1"));
			FileUtils.deleteDirectory(new File(basePath+"csn"));
		} catch (IOException e) {
			throw new RuntimeException("Couldn't delete directory");
		}
	}
	
	
	/**
	 * Test unpack.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void testUnpack() throws Exception{
		
		Object o = TESTHelper.setUpObject("1",basePath);
		
		Job job = new Job();
		job.setObject(o);
		
		new RetrievePackagesHelper().unpackExistingPackages(o);
		
		
		String outputPath = basePath + "/TEST/1/";
		
		assertTrue(new File(outputPath + "data/a/pic1.txt").exists());
		assertTrue(new File(outputPath + "data/b/pic2.txt").exists());
		assertTrue(new File(outputPath + "data/c/pic3.txt").exists());
		assertTrue(new File(outputPath + "data/d/pic4.txt").exists());
		assertTrue(new File(outputPath + "data/e/pic1.txt").exists());
		assertTrue(new File(outputPath + "data/f/folder1/pic5.txt").exists());
		assertTrue(new File(outputPath + "data/f/folder2/pic5.txt").exists());
		assertTrue(new File(outputPath + "data/f/pic3.txt").exists());
		assertFalse(new File(outputPath + "existingAIPs").exists());
	}
}
