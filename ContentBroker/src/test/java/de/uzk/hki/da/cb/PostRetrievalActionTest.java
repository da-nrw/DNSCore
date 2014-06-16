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

/**
 * @author: Jens Peters
 */

import static org.mockito.Mockito.mock;

import java.util.GregorianCalendar;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.uzk.hki.da.core.HibernateUtil;
import de.uzk.hki.da.grid.IrodsSystemConnector;
import de.uzk.hki.da.model.CentralDatabaseDAO;
import de.uzk.hki.da.model.Contractor;
import de.uzk.hki.da.model.Job;
import de.uzk.hki.da.model.Node;
import de.uzk.hki.da.utils.Path;


/**
 * The Class PostRetrievalActionTest.
 */
public class PostRetrievalActionTest {

	/** The dao. */
	private static CentralDatabaseDAO dao = new CentralDatabaseDAO();
	
	/** The irods. */
	IrodsSystemConnector irods;
	
	/** The node. */
	Node node;
	
	/**
	 * Sets the up before class.
	 *
	 * @throws Exception the exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

		HibernateUtil.init("conf/hibernateCentralDbWithInmem.cfg.xml");
		
	}

	/**
	 * Tear down after class.
	 *
	 * @throws Exception the exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * Sets the up.
	 *
	 * @throws Exception the exception
	 */
	@Before
	public void setUp() throws Exception {
		 irods = mock (IrodsSystemConnector.class);
		 node = new Node();
			node.setUserAreaRootPath(Path.make("/tmp/webdav"));
			
	}

	/**
	 * Tear down.
	 *
	 * @throws Exception the exception
	 */
	@After
	public void tearDown() throws Exception {
	}
	
	/**
	 * Post retrieval.
	 */
	@Test 
	public void postRetrieval() {
		Contractor c = new Contractor();
		c.setShort_name("TEST");
		
		Job job = new Job();
		GregorianCalendar gc = new GregorianCalendar();
			System.out.println("is now: " + gc.getTime());
		gc.set(GregorianCalendar.HOUR, gc.get(GregorianCalendar.HOUR)-25);
		System.out.println("read: " + gc.getTime());
		job.setDate_created(String.valueOf(Math.round(gc.getTimeInMillis()/1000L)));
		PostRetrievalAction action = new PostRetrievalAction();
		action.setJob(job);
		
		action.setLocalNode(node);
		action.setDao(dao);
		action.implementation();
		
	}

}
