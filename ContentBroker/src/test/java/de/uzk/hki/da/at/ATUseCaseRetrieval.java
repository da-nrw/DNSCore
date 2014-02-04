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
import org.hibernate.classic.Session;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.uzk.hki.da.core.HibernateUtil;
import de.uzk.hki.da.model.Node;
import de.uzk.hki.da.model.StoragePolicy;
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
		gridFacade.put(
				new File("src/test/resources/at/ATUseCaseRetrieval.pack_1.tar"),
				"/aip/TEST/RetrievalObject/RetrievalObject.pack_1.tar",new StoragePolicy(new Node()));
	}
	
	@After
	public void tearDown() throws IOException{
		distributedConversionAdapter.remove("aip/TEST/RetrievalObject"); // TODO does it work?
		new File("/tmp/RetrievalObject.tar").delete();
		FileUtils.deleteDirectory(new File("/tmp/RetrievalObject"));
		
		cleanStorage();
		clearDB();
	}
	
	// TEST
	
	@Test
	public void testHappyPath() throws Exception{
		
		createObjectAndRetrievalJob();
		waitForJobToBeInStatus("OriginalName", "950", 2000);
		
		System.out.println(new File(userAreaRootPath+"TEST/outgoing/RetrievalObject.tar").getAbsolutePath());
		assertTrue(new File(userAreaRootPath+"TEST/outgoing/RetrievalObject.tar").exists());
		
		FileUtils.moveFileToDirectory(
				new File(userAreaRootPath+"TEST/outgoing/RetrievalObject.tar"), 
				new File("/tmp"), false);
		
		ArchiveBuilderFactory.getArchiveBuilderForFile(new File("/tmp/RetrievalObject.tar"))
			.unarchiveFolder(new File("/tmp/RetrievalObject.tar"), new File ("/tmp/"));
		
		if (!new File("/tmp/RetrievalObject/data/"+"image/713091.tif").exists()) fail();
		if (!new File("/tmp/RetrievalObject/data/"+"premis.xml").exists()) fail();
		
		if (!checkBag(new File("/tmp/RetrievalObject"))) fail();
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
	
	/**
	 * @deprecated code duplication with pipgen
	 */
	private void createObjectAndRetrievalJob(){
		Session session = HibernateUtil.openSession();
		session.beginTransaction();
		session.createSQLQuery("INSERT INTO objects (data_pk,identifier,orig_name,contractor_id,object_state,published_flag) "
				+"VALUES (1,'RetrievalObject','OriginalName',1,'100',0);").executeUpdate();
		session.createSQLQuery("INSERT INTO packages (id,name) VALUES (1,'1');").executeUpdate();
		session.createSQLQuery("INSERT INTO objects_packages (objects_data_pk,packages_id) VALUES (1,1);").executeUpdate();
		session.createSQLQuery("INSERT INTO queue (id,status,objects_id,initial_node) VALUES (1,'900',1,"+
				"'"+nodeName+"');").executeUpdate();
		session.getTransaction().commit();
		session.close();	
	}
	
	
}
