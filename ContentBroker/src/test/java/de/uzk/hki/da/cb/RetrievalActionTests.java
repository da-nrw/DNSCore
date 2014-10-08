/*

  DA-NRW Software Suite | ContentBroker
  Copyright (C) 2013 Historisch-Kulturwissenschaftliche Informationsverarbeitung
  Universität zu Köln
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
import gov.loc.repository.bagit.Bag;
import gov.loc.repository.bagit.BagFactory;
import gov.loc.repository.bagit.utilities.SimpleResult;

import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.uzk.hki.da.core.C;
import de.uzk.hki.da.core.Path;
import de.uzk.hki.da.model.Package;
import de.uzk.hki.da.pkg.NativeJavaTarArchiveBuilder;
import de.uzk.hki.da.test.TC;


/**
 * The Class RetrievalActionTest.
 *
 * @author Daniel M. de Oliveira
 */
public class RetrievalActionTests extends ConcreteActionUnitTest{
	
	private static final Path userAreaRootPath = Path.make(TC.TEST_ROOT_CB,"RetrievalActionTests","user");
	private static final Path workAreaRootPath = Path.make(TC.TEST_ROOT_CB,"RetrievalActionTests","work");
	private static final Path outgoingFolder = Path.make(userAreaRootPath,C.TEST_USER_SHORT_NAME,"outgoing");
	private static final Path container = Path.make(outgoingFolder,TC.IDENTIFIER+C.FILE_EXTENSION_TAR);
	
	@ActionUnderTest
	RetrievalAction action = new RetrievalAction();
	

	
	/**
	 */
	@Before
	public void setUp() throws Exception {
	
		Package pkg1 = o.getLatestPackage();
		Package pkg2 = new Package();
		pkg2.setName("2");
		Package pkg3 = new Package();
		pkg3.setName("3");
		o.getPackages().add(pkg2);
		o.getPackages().add(pkg3);
		
		pkg2.setTransientBackRefToObject(o);
		pkg3.setTransientBackRefToObject(o);
		o.reattach();

		n.setWorkAreaRootPath(workAreaRootPath);
		n.setUserAreaRootPath(userAreaRootPath);
	
		FileUtils.copyDirectory(Path.makeFile(workAreaRootPath,"work",o.getContractor().getShort_name(),"_"+TC.IDENTIFIER), 
				                Path.makeFile(workAreaRootPath,"work",o.getContractor().getShort_name(),TC.IDENTIFIER)); 
		Path.makeFile(outgoingFolder).mkdirs();
		
		
		pkg1.scanRepRecursively("1+a");
		pkg1.scanRepRecursively("1+b");
		pkg2.scanRepRecursively("2+a");
		pkg2.scanRepRecursively("2+b");
		pkg3.scanRepRecursively("3+a");
		pkg3.scanRepRecursively("3+b");
	}

	
	/**
	 * Tear down.
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@After
	public void tearDown () throws IOException {
		FileUtils.deleteDirectory(Path.makeFile(workAreaRootPath,C.WA_WORK,o.getContractor().getShort_name(),TC.IDENTIFIER));
		FileUtils.deleteDirectory(Path.makeFile(outgoingFolder,TC.IDENTIFIER));
		Path.makeFile(outgoingFolder,TC.IDENTIFIER+C.FILE_EXTENSION_TAR).delete();
		FileUtils.deleteDirectory(userAreaRootPath.toFile());
	}
	
	
	
	@Test
	public void testContainerAndBag() throws Exception{

		action.implementation();
		assertFalse(Path.makeFile(workAreaRootPath,o.getContractor().getShort_name(),TC.IDENTIFIER+"_").exists());
		assertTrue( Path.makeFile(outgoingFolder,TC.IDENTIFIER+C.FILE_EXTENSION_TAR).exists() );
		unpack();
		
		BagFactory bagFactory = new BagFactory();
		Bag bag = bagFactory.createBag(Path.makeFile(outgoingFolder,TC.IDENTIFIER));
		SimpleResult result = bag.verifyValid();
		assertTrue(result.isSuccess());
	}
	
	
	
	
	/**
	 * Test.
	 * @throws Exception the exception
	 */
	@Test
	public void testNormalRetrieval() throws Exception {
		
		action.implementation();
		unpack();
		
		assertTrue(Path.makeFile(outgoingFolder,TC.IDENTIFIER,"data/folder1/pic5.txt").exists());
		assertTrue(Path.makeFile(outgoingFolder,TC.IDENTIFIER,"data/folder2/pic5.txt").exists());
		assertTrue(Path.makeFile(outgoingFolder,TC.IDENTIFIER,"data/pic1.txt").exists());
		assertTrue(Path.makeFile(outgoingFolder,TC.IDENTIFIER,"data/pic2.txt").exists());
		assertTrue(Path.makeFile(outgoingFolder,TC.IDENTIFIER,"data/pic3.txt").exists());
		assertTrue(Path.makeFile(outgoingFolder,TC.IDENTIFIER,"data/pic4.txt").exists());
		
		
	}

	
	@Test
	public void testSpecialRetrieval() throws Exception{
		
		action.getJob().setQuestion("RETRIEVE:1,3");
		action.implementation();
		
		unpack();
		assertTrue(Path.makeFile(outgoingFolder,TC.IDENTIFIER,"data/1+a/pic1.txt").exists());
		assertTrue(Path.makeFile(outgoingFolder,TC.IDENTIFIER,"data/1+b/pic2.txt").exists());
		assertTrue(Path.makeFile(outgoingFolder,TC.IDENTIFIER,"data/1+b/premis.xml").exists());
		assertTrue(Path.makeFile(outgoingFolder,TC.IDENTIFIER,"data/3+a/pic1.txt").exists());
		assertTrue(Path.makeFile(outgoingFolder,TC.IDENTIFIER,"data/3+b/folder1/pic5.txt").exists());
		assertTrue(Path.makeFile(outgoingFolder,TC.IDENTIFIER,"data/3+b/folder2/pic5.txt").exists());
		assertTrue(Path.makeFile(outgoingFolder,TC.IDENTIFIER,"data/3+b/pic3.txt").exists());
		assertTrue(Path.makeFile(outgoingFolder,TC.IDENTIFIER,"data/3+b/premis.xml").exists());

		assertFalse(Path.makeFile(outgoingFolder,TC.IDENTIFIER,"data/2+a/pic3.txt").exists());
		assertFalse(Path.makeFile(outgoingFolder,TC.IDENTIFIER,"data/2+b/pic4.txt").exists());
	}
	
	
	/**
	 * @throws IOException 
	 */
	@Test
	public void testCleanup() throws IOException{
		action.implementation();
		
		assertFalse(Path.makeFile(workAreaRootPath,C.WA_WORK,o.getContractor().getShort_name(),TC.IDENTIFIER).exists());
	}
	

	@Test 
	public void testRollback() throws IOException{
		action.implementation();
		action.rollback();
		assertFalse(container.toFile().exists());
	}
	
	


	private static void unpack() throws Exception{
		NativeJavaTarArchiveBuilder tar = new NativeJavaTarArchiveBuilder();
		tar.unarchiveFolder(Path.makeFile(outgoingFolder,TC.IDENTIFIER+C.FILE_EXTENSION_TAR),
				            outgoingFolder.toFile());
	}
}
