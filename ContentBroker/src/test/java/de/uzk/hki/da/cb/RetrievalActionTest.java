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

import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.uzk.hki.da.grid.DistributedConversionAdapter;
import de.uzk.hki.da.model.Contractor;
import de.uzk.hki.da.model.Job;
import de.uzk.hki.da.model.Node;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.model.Package;
import de.uzk.hki.da.utils.NativeJavaTarArchiveBuilder;
import de.uzk.hki.da.utils.Path;
import de.uzk.hki.da.utils.RelativePath;


/**
 * The Class RetrievalActionTest.
 *
 * @author Daniel M. de Oliveira
 */
public class RetrievalActionTest {
	
	/** The job. */
	Job job;
	
	/** The fork and transfer path. */
	Path forkAndTransferPath = new RelativePath("src/test/resources/cb/RetrievalActionTests/");
	
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
		
		FileUtils.copyDirectory(Path.makeFile(forkAndTransferPath,"work/csn/Source"), 
				                Path.makeFile(forkAndTransferPath,"work/csn/1")); 
		Node node = new Node();
		node.setWorkAreaRootPath(new RelativePath(forkAndTransferPath));
		node.setUserAreaRootPath(new RelativePath(forkAndTransferPath,"work"));
		
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
		object.getLatestPackage().setTransientBackRefToObject(object);
		object.getLatestPackage().scanRepRecursively("a");
		object.getLatestPackage().scanRepRecursively("b");
		object.getLatestPackage().scanRepRecursively("c");
		object.getLatestPackage().scanRepRecursively("d");
		object.getLatestPackage().scanRepRecursively("e");
		object.getLatestPackage().scanRepRecursively("f");
		
		dca = mock (DistributedConversionAdapter.class);
		
		action = new RetrievalAction();
		action.setSystemFromEmailAddress("noreply@system.de");
		action.setDistributedConversionAdapter(dca);
		action.setObject(object);
		action.setJob(job);
		action.setLocalNode(node);
	}
	
	/**
	 * Tear down.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@After
	public void tearDown () throws IOException {
		
		FileUtils.deleteDirectory(Path.makeFile(forkAndTransferPath,"csn/1"));
		FileUtils.deleteDirectory(Path.makeFile(forkAndTransferPath,"csn/1_"));
		FileUtils.deleteDirectory(Path.makeFile(forkAndTransferPath,"csn/outgoing/1"));
		Path.makeFile(forkAndTransferPath,"csn/outgoing/urn.tar").delete();
		Path.makeFile(forkAndTransferPath,"csn/outgoing/1.tar").delete();
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
		assertFalse(Path.makeFile(forkAndTransferPath,"csn/1_").exists());
		assertTrue( Path.makeFile(forkAndTransferPath,"work/csn/outgoing/1.tar").exists() );
		
		// checking contents of package
		
		NativeJavaTarArchiveBuilder tar = new NativeJavaTarArchiveBuilder();
		tar.unarchiveFolder(Path.makeFile(forkAndTransferPath,"work/csn/outgoing/1.tar"),
				            Path.makeFile(forkAndTransferPath,"work/csn/outgoing/"));
		
		assertTrue(Path.makeFile(forkAndTransferPath,"work/csn/outgoing/1/data/folder1/pic5.txt").exists());
		assertTrue(Path.makeFile(forkAndTransferPath,"work/csn/outgoing/1/data/folder2/pic5.txt").exists());
		assertTrue(Path.makeFile(forkAndTransferPath,"work/csn/outgoing/1/data/pic1.txt").exists());
		assertTrue(Path.makeFile(forkAndTransferPath,"work/csn/outgoing/1/data/pic2.txt").exists());
		assertTrue(Path.makeFile(forkAndTransferPath,"work/csn/outgoing/1/data/pic3.txt").exists());
		assertTrue(Path.makeFile(forkAndTransferPath,"work/csn/outgoing/1/data/pic4.txt").exists());
		
		// check bag
		BagFactory bagFactory = new BagFactory();
		Bag bag = bagFactory.createBag(Path.makeFile(forkAndTransferPath,"work/csn/outgoing/1/"));
		SimpleResult result = bag.verifyValid();
		assertTrue(result.isSuccess());
	}
}
