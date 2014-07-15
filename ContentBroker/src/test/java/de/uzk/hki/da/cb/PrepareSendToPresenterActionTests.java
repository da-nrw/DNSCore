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
import org.junit.Test;

import de.uzk.hki.da.grid.DistributedConversionAdapter;
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
 * The Class PrepareSendToPresenterActionTests.
 */
public class PrepareSendToPresenterActionTests {

	/** The base path. */
	private String workingAreaRoot = "src/test/resources/cb/PrepareSendToPresenterActionTests";
	
	/** The action. */
	PrepareSendToPresenterAction action = new PrepareSendToPresenterAction();
	
	/** The irods. */
	IrodsSystemConnector irods;
	
	/** The dao. */
	CentralDatabaseDAO dao;
	
	/** The job. */
	Job job;
	
	/** The node. */
	Node node;
	
	/** The public file. */
	private File publicFile = new File(workingAreaRoot+"/pips/public/TEST/identifier_1_1/a.txt");
	
	/** The institution file. */
	private File institutionFile = new File(workingAreaRoot+"/pips/institution/TEST/identifier_1_1/a.txt");

	/** The contractor. */
	private Contractor contractor;
	
	/**
	 * Sets the up.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@Before
	public void setUp() throws IOException {
		action.setDistributedConversionAdapter(mock (DistributedConversionAdapter.class));
		action.setDao(mock(CentralDatabaseDAO.class));

		node = new Node(); 
		node.setWorkAreaRootPath(new RelativePath(workingAreaRoot));
		Node dipNode = new Node(); dipNode.setName("dipNode");
		action.setLocalNode(node);

		contractor = new Contractor();
		contractor.setShort_name("TEST");
		
		job = new Job();
		
		new File(workingAreaRoot+"/pips/institution").mkdirs();
		new File(workingAreaRoot+"/pips/public").mkdirs();
		
		FileUtils.copyDirectory(new File(workingAreaRoot+"/sources/1"), new File(workingAreaRoot+"/work/TEST/identifier_1"));
		FileUtils.copyDirectory(new File(workingAreaRoot+"/sources/2"), new File(workingAreaRoot+"/work/TEST/identifier_2"));
	}
	
	/**
	 * Publish everything.
	 * @throws IOException 
	 */
	@Test
	public void publishEverything() throws IOException {
		Package pkg = new Package(); pkg.setId(1); 
		pkg.setName("1");
		Object  obj = new Object(); obj.getPackages().add(pkg);  obj.setContractor(contractor); obj.setIdentifier("identifier_1");
		job.setObject(obj);
		obj.setTransientNodeRef(node);
		action.setJob(job);
		action.setObject(obj);
		action.implementation();
		
//		assertTrue (new File(basePath+"dip/urn_1/thumbnail/a.txt").exists() );
		assertTrue (publicFile.exists() );
		assertTrue (institutionFile.exists() );
		
		// TODO create random files previously and test for non existence of everything except dip/public and dip/institution here.
	}
	
	/**
	 * Publish nothing.
	 * @throws IOException 
	 */
	@Test
	public void publishNothing() throws IOException {
		Package pkg = new Package(); pkg.setId(2); 
		pkg.setName("2");
		Object  obj = new Object(); obj.getPackages().add(pkg); obj.setContractor(contractor); obj.setIdentifier("identifier_2");
		job.setObject(obj);
		obj.setTransientNodeRef(node);
		action.setObject(obj);
		action.setJob(job);
		action.implementation();
		
		assertFalse (publicFile.exists() );
		assertFalse (institutionFile.exists() );
	}
	
	
	
	/**
	 * Tear down.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@After
	public void tearDown() throws IOException {
		
		FileUtils.deleteDirectory(new File(workingAreaRoot+"/work/TEST/identifier_1"));
		FileUtils.deleteDirectory(new File(workingAreaRoot+"/work/TEST/identifier_2"));
		FileUtils.deleteDirectory(new File(workingAreaRoot+"/pips/institution"));
		FileUtils.deleteDirectory(new File(workingAreaRoot+"/pips/public"));
	}
	
	
}
