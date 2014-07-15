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

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.uzk.hki.da.core.HibernateUtil;
import de.uzk.hki.da.model.CentralDatabaseDAO;
import de.uzk.hki.da.model.Job;
import de.uzk.hki.da.model.Node;


/**
 * This test is intended to ensure that the children and the 
 * conversion instructions get refreshed to
 * reflect the actual database entries. This is important for the 
 * ConvertCheckAction because it waits
 * for the states to change. 
 * 
 * If this test here hangs, 
 * this comes equal to a fail (but this is only one way it can fail).
 * 
 * @Under_test ConvertAction
 * @author Daniel M. de Oliveira
 *
 */
public class ProperRefreshOfJobChildrenTest extends Thread{

	/** The dao. */
	static CentralDatabaseDAO dao = new CentralDatabaseDAO();
	
	/** The action. */
	static ConvertAction action = new ConvertAction();
	
	/** The thisthread. */
	static ProperRefreshOfJobChildrenTest thisthread = null;
	
	/** The Constant inserts. */
	private static final String inserts[] = new String[]{
		"INSERT INTO contractors (id,short_name) VALUES (1,'TEST')",
		"INSERT INTO objects (data_pk,identifier,object_state,published_flag) VALUES (1,'ID','0',1)",
		"INSERT INTO packages (id,name) VALUES (1,'1')",
		"INSERT INTO queue (id,objects_id,contractor_id,status,initial_node) VALUES (1,1,1,'240','vm1')",
		"INSERT INTO queue (id,objects_id,contractor_id,status,initial_node,parent_id) VALUES (2,1,1,'580','vm2',1)",
		"INSERT INTO conversion_queue (id,job_id) VALUES (1,2)",
		};
		
	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	public void run(){
		try {Thread.sleep(4000);} catch (InterruptedException e){}
		// 'test:urn'
		Session baseSession = HibernateUtil.openSession();
		baseSession.beginTransaction();
		baseSession.createSQLQuery("UPDATE queue SET status='590' WHERE id=2").executeUpdate();
		baseSession.getTransaction().commit();
		baseSession.close();
		
		
	}
	
	/**
	 * Sets the up before class.
	 *
	 * @throws Exception the exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		HibernateUtil.init("src/main/xml/hibernateCentralDB.cfg.xml.inmem");
		Session baseSession = HibernateUtil.openSession();
		baseSession.beginTransaction();
		
		for (int i=0;i<inserts.length;i++){
			baseSession.createSQLQuery(inserts[i]).executeUpdate();
		}
		
		baseSession.getTransaction().commit();
		baseSession.close();
		
		thisthread = new ProperRefreshOfJobChildrenTest();
		(thisthread).start();
	}
	

	/**
	 * Tear down after class.
	 *
	 * @throws Exception the exception
	 */
	@SuppressWarnings("deprecation")
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		
		thisthread.stop();
		
		Session session = HibernateUtil.openSession();
		session.beginTransaction();
		
		// necesarry because some previously written test left garbage behind
		session.createSQLQuery("DELETE FROM conversion_queue").executeUpdate(); 
		session.createSQLQuery("DELETE FROM queue").executeUpdate(); 
		session.createSQLQuery("DELETE FROM contractors").executeUpdate(); 
		session.createSQLQuery("DELETE FROM objects").executeUpdate();
		
		session.getTransaction().commit();
		session.close();
		
		
	}

	/**
	 * Sets the up.
	 *
	 * @throws Exception the exception
	 */
	@Before
	public void setUp() throws Exception {

		Node node = new Node(); node.setWorkingResource("vm3");
		Session session = HibernateUtil.openSession();
		session.beginTransaction();
		action.setJob((Job) session.get(Job.class,1));
		session.close();
		action.setLocalNode(node);
		action.setDao(dao);
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
	 * Test.
	 */
	@Test
	public void test() {
		
		List<Job> friendJobs = new ArrayList<Job>();
		Session session = HibernateUtil.openSession();
		session.beginTransaction();
		friendJobs.add((Job) session.get(Job.class,2));
		session.close();
		
		action.waitForFriendJobsToBeReady(friendJobs);
	}
}
