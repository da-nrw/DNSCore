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
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.uzk.hki.da.grid.IrodsSystemConnector;
import de.uzk.hki.da.model.CentralDatabaseDAO;
import de.uzk.hki.da.model.Contractor;
import de.uzk.hki.da.model.Job;
import de.uzk.hki.da.model.Node;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.model.Package;
import de.uzk.hki.da.utils.Path;
import de.uzk.hki.da.utils.RelativePath;


/**
 * The Class BuildAIPActionTests.
 */
public class BuildAIPActionTests {

	static Path workAreaRootPath = new RelativePath("src/test/resources/cb/BuildAIPActionTests/");
	
	/** The backup package path. */
	static Path backupPackagePath = Path.make(workAreaRootPath,"work/csn/95949_/");
	
	/** The package fork path. */
	static Path packageForkPath = Path.make(workAreaRootPath,"work/csn/95949/");
	
	/** The job. */
	static Job job = new Job("csn","vm3");
	
	/** The irods. */
	static IrodsSystemConnector irods = mock (IrodsSystemConnector.class);
	
	/** The dao. */
	static CentralDatabaseDAO dao = mock (CentralDatabaseDAO.class);
	
	/** The action. */
	static BuildAIPAction action = new BuildAIPAction();
	
	/** The node. */
	static Node node = new Node(); 
	
	/** The rep name. */
	static String repName = "2012_01_01+12_12+";
	
	/**
	 * Sets the up before class.
	 */
	@BeforeClass
	public static void setUpBeforeClass(){
		node.setWorkingResource("vm3");
		node.setWorkAreaRootPath(new RelativePath(workAreaRootPath));
		
		
		Package pkg = new Package();
		pkg.setName("2");
		pkg.setId(95949);
		
		Contractor contractor = new Contractor();
		contractor.setShort_name("csn");
		
		Object obj = new Object();
		obj.getPackages().add(pkg);
		obj.setContractor(contractor);
		obj.setIdentifier("95949");
		obj.setTransientNodeRef(node);
		
		job.setObject(obj);
		job.setRep_name(repName);

		action.setObject(obj);
		action.setDao(dao);
		action.setLocalNode(node);
		action.setJob(job);
		
	}
	
	/**
	 * Sets the up.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@Before
	public void setUp() throws IOException{
		FileUtils.copyDirectory(backupPackagePath.toFile(), packageForkPath.toFile());
	}
	
	/**
	 * Tear down.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@After
	public void tearDown() throws IOException{
		if (packageForkPath.toFile().exists()) FileUtils.deleteDirectory(packageForkPath.toFile()); 
	}
	
	/**
	 * Proper bag creation.
	 */
	@Test 
	public void properBagCreation(){
		
	}
	
	/**
	 * Irods collection removal.
	 */
	@Test
	public void irodsCollectionRemoval(){
		
	}
	
	/**
	 * Test only newest reps survive.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void testOnlyNewestRepsSurvive() throws Exception{
		action.implementation();
		
		String children[] = new File(packageForkPath+"/data").list(); 
		
		for (int i=0;i<children.length;i++){
			if (!children[i].contains(repName) && !children[i].contains("premis")) fail();
		}
		
	}
	
	/**
	 * Test replace premis file.
	 */
	@Test
	public void testDeleteOldPremisFile() {
		action.implementation();
		
		assertFalse(new File(packageForkPath + "data/premis_old.xml").exists());
	}
	
	/**
	 * Delete bag infos when rolling back.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void deleteBagInfosWhenRollingBack() throws Exception{		
		action.implementation();
		action.rollback();
		
		assertFalse(new File(packageForkPath + "bag-info.txt" ).exists());
		assertFalse(new File(packageForkPath + "bagit.txt" ).exists());
		assertFalse(new File(packageForkPath + "manifest-md5.txt" ).exists());
		assertFalse(new File(packageForkPath + "tagmanifest-md5.txt" ).exists());
	}
}
