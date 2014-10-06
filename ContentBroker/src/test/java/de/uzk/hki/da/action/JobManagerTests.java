/*
  DA-NRW Software Suite | ContentBroker
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

package de.uzk.hki.da.action;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.hibernate.Session;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import de.uzk.hki.da.cb.NullAction;
import de.uzk.hki.da.core.HibernateUtil;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import de.uzk.hki.da.model.Job;
import de.uzk.hki.da.model.Node; 
import de.uzk.hki.da.test.TESTHelper;

/**
 * @author Daniel M. de Oliveira
 */
public class JobManagerTests {
	
	/** The Constant inserts. */
	private static final String inserts[] = new String[]{
		"INSERT INTO nodes (urn_index) VALUES (1)",
		"INSERT INTO users (short_name,forbidden_nodes,email_contact,id) " +
			"VALUES ('TEST_2','','da-nrw-notifier@uni-koeln.de',1)",
		"INSERT INTO objects (data_pk,initial_node,urn,orig_name,date_created,date_modified,zone," +
					"published_flag,user_id,object_state) VALUES (1,'testnode'," +
					"'urn+nbn+de+danrw-1-2012113022773','test_object_double','1354276007948','1354276113286','da-nrw','0',1,'100')",
		"INSERT INTO queue (id,objects_id,status,initial_node) VALUES (1,1,'110','testnode')",
	};
	
	@AfterClass
	public static void tearDownAfterClass() {
		
		TESTHelper.clearDB();
	}
	
	/**
	 * Sets the up before class.
	 */
	@BeforeClass
	public static void setUpBeforeClass(){
		
		TESTHelper.clearDB();
		
		HibernateUtil.init("src/main/xml/hibernateCentralDB.cfg.xml.inmem");
		Session session = HibernateUtil.openSession();
		session.beginTransaction();
		for (int i=0;i<inserts.length;i++)
			session.createSQLQuery(inserts[i]).executeUpdate();
		session.getTransaction().commit();
		session.close();
	}
	
	@Test
	public void test() throws InterruptedException {
		
		SmartActionFactory saf = prepare();
		
		Node node = new Node();
		node.setName("testnode");
		
		JobManager jm = new JobManager();
		jm.setSmartActionFactory(saf);
		jm.fetchJobsExecuteActions(node);
		
		Thread.sleep(100);
		Session session = HibernateUtil.openSession();
		session.beginTransaction();
		Job j = (Job) session.get(Job.class,1);
		session.close();
		
		assertEquals("112",j.getStatus());
		
	}

	
	/**
	 * Like the createActionsMethod, return Action with are initialized, which means in this case that
	 * theiry job field is set with the jobs from the params.
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private SmartActionFactory prepare() {
		SmartActionFactory saf = mock(SmartActionFactory.class);
		when(saf.createActions((Set<Job>) anyObject())).thenAnswer(new Answer<List<AbstractAction>>() {
			    @Override
			    public List<AbstractAction> answer(InvocationOnMock invocation) throws Throwable {
			      Object[] args = invocation.getArguments();
			      Set<Job> jobs = ((Set<Job>) args[0]);
			      
			      List<AbstractAction> list = new ArrayList<AbstractAction>();
			      for (Job j:jobs) {
			    	  AbstractAction a = new NullAction();
			    	  a.setJob(j);
			    	  list.add(a);
			      }
			      
			      return list;
			    }
			  });
		return saf;
	}
}
