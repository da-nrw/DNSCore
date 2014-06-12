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
package de.uzk.hki.da.at;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import gov.loc.repository.bagit.Bag;
import gov.loc.repository.bagit.BagFactory;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.uzk.hki.da.utils.ArchiveBuilderFactory;

/**
 * Relates to AK-T/05 RetrieveObject - Happy Path Scenario.
 * @author Daniel M. de Oliveira
 */
public class ATUseCaseRetrieval extends Base{
	
	// SETUP
	
	@Before()
	public void setUp() throws IOException{
		setUpBase();
//		gridFacade.put(
//				new File("src/test/resources/at/ATUseCaseRetrieval.pack_1.tar"),
//				"TEST/ID-ATUseCaseRetrieval/ID-ATUseCaseRetrieval.pack_1.tar",new StoragePolicy(new Node()));
	}
	
	@After
	public void tearDown(){
		distributedConversionAdapter.remove("aip/TEST/ID-ATUseCaseRetrieval"); // TODO does it work?
		new File("/tmp/ID-ATUseCaseRetrieval.tar").delete();
		FileUtils.deleteQuietly(new File("/tmp/ID-ATUseCaseRetrieval"));
		
		cleanStorage();
		clearDB();
	}
	
	// TEST
	
	@Test
	public void testHappyPath() throws Exception{
		
		String name = "ATUseCaseRetrieval";
		createObjectAndJob(name,"900");
		waitForJobToBeInStatus(name, "950", 2000);
		
		System.out.println(new File(localNode.getUserAreaRootPath()+"/TEST/outgoing/ID-ATUseCaseRetrieval.tar").getAbsolutePath());
		assertTrue(new File(localNode.getUserAreaRootPath()+"/TEST/outgoing/ID-ATUseCaseRetrieval.tar").exists());
		
		FileUtils.moveFileToDirectory(
				new File(localNode.getUserAreaRootPath()+"/TEST/outgoing/ID-ATUseCaseRetrieval.tar"), 
				new File("/tmp"), false);
		
		ArchiveBuilderFactory.getArchiveBuilderForFile(new File("/tmp/ID-ATUseCaseRetrieval.tar"))
			.unarchiveFolder(new File("/tmp/ID-ATUseCaseRetrieval.tar"), new File ("/tmp/"));
		
		if (!new File("/tmp/ID-ATUseCaseRetrieval/data/"+"image/713091.tif").exists()) fail();
		if (!new File("/tmp/ID-ATUseCaseRetrieval/data/"+"premis.xml").exists()) fail();
		
		if (!checkBag(new File("/tmp/ID-ATUseCaseRetrieval"))) fail();
	}
	
	// ----------
	
	/**
	 * Check bag.
	 *
	 * @param file the file
	 * @return true, if successful
	 */
	private boolean checkBag(File file){
		BagFactory bagFactory = new BagFactory();
		Bag bag = bagFactory.createBag(file);
		return bag.verifyValid().isSuccess();
	}	
	
}
