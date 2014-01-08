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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import gov.loc.repository.bagit.Bag;
import gov.loc.repository.bagit.BagFactory;
import gov.loc.repository.bagit.utilities.SimpleResult;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.uzk.hki.da.archivers.NativeJavaTarArchiveBuilder;
import de.uzk.hki.da.grid.DistributedConversionAdapter;
import de.uzk.hki.da.model.Contractor;
import de.uzk.hki.da.model.Job;
import de.uzk.hki.da.model.Node;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.model.Package;


/**
 * The Class RetrievalActionTest.
 *
 * @author Daniel M. de Oliveira
 */
public class RetrievalActionTest {
	
	/** The job. */
	Job job;
	
	/** The fork and transfer path. */
	String forkAndTransferPath = "src/test/resources/cb/RetrievalActionTests/";
	
	/** The object identifier. */
	String objectIdentifier = "1";
	
	/** The action. */
	RetrievalAction action;
	
	/** The irods. */
	DistributedConversionAdapter dca;
	
	/**
	 * Sets the up.
	 *
	 * @throws Exception the exception
	 */
	@Before
	public void setUp() throws Exception {
		
		FileUtils.copyDirectory(new File(forkAndTransferPath+"csn/Source"), 
				                new File(forkAndTransferPath+"csn/1")); 
		Node node = new Node();
		node.setWorkAreaRootPath(forkAndTransferPath);
		node.setUserAreaRootPath(forkAndTransferPath);
		
		Contractor contractor = new Contractor(); 
		contractor.setShort_name("csn"); 
		contractor.setEmail_contact("abc@hki.uni-koeln.de");
		Object object = new Object(); 
		object.setContractor(contractor); 
		object.setIdentifier(objectIdentifier);
		
		Package pkg = new Package(); pkg.setId(1); 
		pkg.setName("1");
		object.getPackages().add(pkg); 
		Job job = new Job(); 
		object.setIdentifier(objectIdentifier);
		job.setObject(object);
		object.setTransientNodeRef(node);
		object.reattach();
		
		dca = mock (DistributedConversionAdapter.class);

		action = new RetrievalAction();
		action.setDistributedConversionAdapter(dca);
		action.setObject(object);
		action.setJob(job);
		action.setLocalNode(node);
		action.setIrodsZonePath("da-nrw");
	}
	
	/**
	 * Tear down.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@After
	public void tearDown () throws IOException {
		
		FileUtils.deleteDirectory(new File(forkAndTransferPath+"csn/1"));
		FileUtils.deleteDirectory(new File(forkAndTransferPath+"csn/1_"));
		FileUtils.deleteDirectory(new File(forkAndTransferPath+"csn/outgoing/1"));
		new File(forkAndTransferPath+"csn/outgoing/urn.tar").delete();
		new File(forkAndTransferPath + "csn/outgoing/1.tar").delete();
	}
	
	
	/**
	 * Test.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void test() throws Exception {
		
		// action.setIrodsSystemConnector(irods);
		action.setSidecarExtensions("xmp");
		
		action.implementation();
		assertFalse(new File(forkAndTransferPath+"csn/1_").exists());
		assertTrue( new File(forkAndTransferPath+"csn/outgoing/1.tar").exists() );
		
		// checking contents of package
		
		NativeJavaTarArchiveBuilder tar = new NativeJavaTarArchiveBuilder();
		tar.unarchiveFolder(new File(forkAndTransferPath+"csn/outgoing/1.tar"),
				            new File(forkAndTransferPath+"csn/outgoing/"));
		
		assertTrue(new File(forkAndTransferPath+"csn/outgoing/1/data/folder1/pic5.txt").exists());
		assertTrue(new File(forkAndTransferPath+"csn/outgoing/1/data/folder2/pic5.txt").exists());
		assertTrue(new File(forkAndTransferPath+"csn/outgoing/1/data/pic1.txt").exists());
		assertTrue(new File(forkAndTransferPath+"csn/outgoing/1/data/pic2.txt").exists());
		assertTrue(new File(forkAndTransferPath+"csn/outgoing/1/data/pic3.txt").exists());
		assertTrue(new File(forkAndTransferPath+"csn/outgoing/1/data/pic4.txt").exists());
		
		// check bag
		BagFactory bagFactory = new BagFactory();
		Bag bag = bagFactory.createBag(new File(forkAndTransferPath+"csn/outgoing/1/"));
		SimpleResult result = bag.verifyValid();
		assertTrue(result.isSuccess());
	}
}
