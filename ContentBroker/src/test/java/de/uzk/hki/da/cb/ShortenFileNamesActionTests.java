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

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.uzk.hki.da.model.Contractor;
import de.uzk.hki.da.model.DAFile;
import de.uzk.hki.da.model.Event;
import de.uzk.hki.da.model.Job;
import de.uzk.hki.da.model.Node;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.model.Package;
import de.uzk.hki.da.utils.RelativePath;


/**
 * The Class ShortenFileNamesActionTests.
 */
public class ShortenFileNamesActionTests {

	/** The base path. */
	private String workAreaRootPath = "src/test/resources/cb/ShortenFileNamesActionTests/";
	
	/** The action. */
	ShortenFileNamesAction action = new ShortenFileNamesAction();
	
	/** The job. */
	Job job;
	
	/** The node. */
	Node node;

	/** The contractor. */
	private Contractor contractor;
	
	/**
	 * Sets the up.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@Before
	public void setUp() throws IOException {
		
		node = new Node();
		node.setWorkAreaRootPath(new RelativePath(workAreaRootPath));
		Node dipNode = new Node(); dipNode.setName("dipNode");
		action.setLocalNode(node);
		
		contractor = new Contractor();
		contractor.setShort_name("TEST");
		
		job = new Job();
		
		FileUtils.copyDirectory(new File(workAreaRootPath+"work/sources/1"), new File(workAreaRootPath+"work/TEST/1"));
		
	}

	/**
	 * Test.
	 *
	 * @throws FileNotFoundException the file not found exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@Test
	public void test() throws FileNotFoundException, IOException {
		
		Package pkg = new Package();
		pkg.setId(1); 
		pkg.setName("1");
		
		Object obj = new Object();
		obj.setContractor(contractor);
		obj.getPackages().add(pkg);
		obj.setIdentifier("1");
		obj.setTransientNodeRef(node);
		obj.reattach();
		
		Event event1 = new Event();
		event1.setType("CONVERT");
		event1.setSource_file(new DAFile(pkg, "rep", "a.txt"));
		event1.setTarget_file(new DAFile(pkg, "dip/public", "a.txt"));
		pkg.getEvents().add(event1);
		Event event2 = new Event();
		event2.setType("CONVERT");
		event2.setSource_file(new DAFile(pkg, "rep", "b.txt"));
		event2.setTarget_file(new DAFile(pkg, "dip/public", "b.txt"));
		Event event3 = new Event();
		pkg.getEvents().add(event2);
		event3.setType("CONVERT");
		event3.setSource_file(new DAFile(pkg, "rep", "a.txt"));
		event3.setTarget_file(new DAFile(pkg, "dip/institution", "a.txt"));
		Event event4 = new Event();
		pkg.getEvents().add(event3);
		event4.setType("CONVERT");
		event4.setSource_file(new DAFile(pkg, "rep", "b.txt"));
		event4.setTarget_file(new DAFile(pkg, "dip/institution", "b.txt"));
		pkg.getEvents().add(event4);
		
		job.setObject(obj);
		action.setObject(obj);
		action.setJob(job);
		action.implementation();
		
		String newFileName = "_a5e54d1fd7bb69a228ef0dcd2431367e.txt";
		assertTrue(new File(workAreaRootPath + "work/TEST/1/data/dip/public/" + newFileName).exists());
		newFileName = "_ce506ace22f28ac2bc4f933d4cf989fd.txt";
		assertTrue(new File(workAreaRootPath + "work/TEST/1/data/dip/public/" + newFileName).exists());
		newFileName = "_a5e54d1fd7bb69a228ef0dcd2431367e.txt";
		assertTrue(new File(workAreaRootPath + "work/TEST/1/data/dip/institution/" + newFileName).exists());
		newFileName = "_ce506ace22f28ac2bc4f933d4cf989fd.txt";
		assertTrue(new File(workAreaRootPath + "work/TEST/1/data/dip/institution/" + newFileName).exists());
		
	}
	
	/**
	 * Tear down.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@After
	public void tearDown() throws IOException {		
		FileUtils.deleteDirectory(new File(workAreaRootPath+"work/TEST/1"));
	}

}
