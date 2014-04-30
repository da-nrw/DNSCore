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
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.uzk.hki.da.core.ActionCommunicatorService;
import de.uzk.hki.da.core.IngestGate;
import de.uzk.hki.da.grid.GridFacade;
import de.uzk.hki.da.grid.IrodsGridFacade;
import de.uzk.hki.da.model.CentralDatabaseDAO;
import de.uzk.hki.da.model.Contractor;
import de.uzk.hki.da.model.Job;
import de.uzk.hki.da.model.Node;
import de.uzk.hki.da.model.Package;
import de.uzk.hki.da.model.Object;


/**
 * The Class UnpackActionBagitAndDeltaTests.
 */
public class UnpackActionBagitAndDeltaTests {

	/** The data path. */
	String workAreaRootPath = "src/test/resources/cb/UnpackActionTests/";
	
	/** The base path. */
	String ingestPath = workAreaRootPath + "ingest/csn/";
	
	/** The work path. */
	String csnPath = workAreaRootPath + "work/csn/";

	/** The grid. */
	GridFacade grid = mock(IrodsGridFacade.class);
	
	/** The dao. */
	CentralDatabaseDAO dao = mock(CentralDatabaseDAO.class);
	
	/** The bal. */
	IngestGate gate = new IngestGate();
	
	/** The node. */
	Node node = new Node();
	
	/** The job. */
	Job job = new Job("csn","vm3");
	
	/** The action. */
	UnpackAction action = new UnpackAction();
	
	/** The pkg. */
	Package pkg = new Package();
	
	/** The acs. */
	ActionCommunicatorService acs = new ActionCommunicatorService();

	/**
	 * Sets the up.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@Before
	public void setUp() throws IOException{

		FileUtils.copyFileToDirectory(new File("src/main/resources/premis.xsd"), new File("conf/"));
		FileUtils.copyFileToDirectory(new File("src/main/resources/xlink.xsd"), new File("conf/"));
		
		Contractor c = new Contractor();
		c.setShort_name("csn");

		node.setWorkingResource("vm3");
		node.setWorkAreaRootPath(workAreaRootPath);
		node.setIngestAreaRootPath(workAreaRootPath + "ingest/");

		gate.setWorkAreaRootPath(workAreaRootPath);
		gate.setFreeDiskSpacePercent(5);
		gate.setFileSizeFactor(3);

		pkg.setName("1");	

		action.setNode(node);
		action.setDao(dao);
		action.setGridRoot(grid);

		Object o = new Object();
		o.setContractor(c);
		o.setIdentifier("identifier");
		o.getPackages().add(pkg);
		o.setTransientNodeRef(node);
		o.reattach();

		
		job.setObject(o);
		
		action.setObject(o);
		action.setJob(job);
		action.setIngestGate(gate);
		action.setActionCommunicatorService(acs);

		FileUtils.copyFile(new File(ingestPath+"/bagitPackage.tgz_"),new File(ingestPath+"/bagitPackage.tgz"));
	}

	/**
	 * Tear down.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@After
	public void tearDown() throws IOException{
		new File("conf/premis.xsd").delete();
		new File("conf/xlink.xsd").delete();
		
		FileUtils.deleteDirectory(new File(csnPath+"identifier"));
		if (new File(ingestPath+"/bagitPackage.tgz").exists()) new File(ingestPath+"/bagitPackage.tgz").delete();
		if (new File(csnPath+"/bagitPackage.tgz").exists()) new File(csnPath+"/bagitPackage.tgz").delete();
	}

	/**
	 * Test unpack std package.
	 */
	@Test
	public void testUnpackStdPackage(){

		job.getObject().getLatestPackage().setId(10020);
		job.getObject().getLatestPackage().setContainerName("bagitPackage.tgz");
		action.implementation();

		String repName = job.getRep_name();
		String packagePath = csnPath + "identifier/";

		assertTrue(new File(packagePath+"data").exists());
		assertTrue(new File(packagePath+"bagit.txt").exists());
		assertTrue(new File(packagePath+"manifest-md5.txt").exists());
		assertTrue(new File(packagePath+"bag-info.txt").exists());
		assertTrue(new File(packagePath+"tagmanifest-md5.txt").exists());
		assertTrue(new File(packagePath+"data/"+repName+"a/140849.tif").exists());
		assertTrue(new File(packagePath+"data/"+repName+"a/premis.xml").exists());
	}

	/**
	 * Test delete source package.
	 */
	@Test
	public void testDeleteSourcePackage(){
		job.getObject().getLatestPackage().setId(10020);
		job.getObject().getLatestPackage().setContainerName("bagitPackage.tgz");
		action.implementation();
		assertFalse(new File(csnPath + "bagitPackage.tgz").exists());
	} 

	/**
	 * Throw exception when zi pdoesnt exist.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@Test
	public void throwExceptionWhenZIPdoesntExist() throws IOException{

//		job.setOrig_name("56789");

		try{		
			action.implementation();
			fail();
		}
		catch(Exception e){
			action.rollback();
			System.out.println("Exception caught as expected: "+e);	
		}		
	}

}
