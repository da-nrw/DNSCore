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

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.uzk.hki.da.grid.DistributedConversionAdapter;
import de.uzk.hki.da.model.CentralDatabaseDAO;
import de.uzk.hki.da.model.Contractor;
import de.uzk.hki.da.model.Job;
import de.uzk.hki.da.model.Node;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.model.Package;
import de.uzk.hki.da.utils.Path;
import de.uzk.hki.da.utils.RelativePath;


/**
 * The Class TarActionTests.
 *
 * @author Daniel M. de Oliveira
 */
public class TarActionTests {

	static String workAreaRootPath = "src/test/resources/cb/TarActionTests/Implementation/";
	
	/** The backup package path. */
	static String backupPackagePath = workAreaRootPath+"work/csn/95949_/";
	
	/** The package fork path. */
	static String packageForkPath = workAreaRootPath+"work/csn/95949/";
	
	/** The unpacked package path. */
	static String unpackedPackagePath = workAreaRootPath+"work/csn/95949_unpacked/";
	
	/** The target tar file. */
	static File targetTarFile = new File(workAreaRootPath+"work/csn/95949.pack_2.tar");
	
	/** The job. */
	static Job job = new Job("csn","vm3");
	
	
	/** The dao. */
	static CentralDatabaseDAO dao = mock (CentralDatabaseDAO.class);
	
	/** The action. */
	static TarAction action = new TarAction();
	
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
		
		Contractor contractor = new Contractor();
		contractor.setShort_name("csn");
		Object o = new Object();
		o.setContractor(contractor);
		Package pkg = new Package();
		pkg.setName("2");
		o.getPackages().add(pkg);
		o.setIdentifier("95949");
		o.setTransientNodeRef(node);
		
		job.setRep_name(repName);
		job.setObject(o);

		action.setObject(o);
		action.setDao(dao);
		action.setDistributedConversionAdapter(mock(DistributedConversionAdapter.class));
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
		FileUtils.copyDirectory(new File(backupPackagePath), new File(packageForkPath));
	}
	
	/**
	 * Tear down.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@After
	public void tearDown() throws IOException{
		if (targetTarFile.exists()){targetTarFile.delete();}
		
		if (new File(packageForkPath).exists()) FileUtils.deleteDirectory(new File(packageForkPath)); 
		if (new File(unpackedPackagePath).exists()) FileUtils.deleteDirectory(new File(unpackedPackagePath));
		
	}
	
	/**
	 * Test tar creation.
	 * @throws IOException 
	 */
	@Test
	public void testTarCreation() throws IOException{
		action.implementation();
		assertTrue(targetTarFile.exists());
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
	 * In case the package still exists in the fork directory, we can safely remove any
	 * (possibly partially) created tar.
	 * @author Daniel M. de Oliveira
	 * @throws IOException 
	 */
	@Test
	public void rollback() throws IOException {
		action.implementation();
		assertTrue(targetTarFile.exists());
		
		action.rollback();
		assertFalse(targetTarFile.exists());
	}
}
