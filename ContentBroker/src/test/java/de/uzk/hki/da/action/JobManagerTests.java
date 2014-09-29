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

import org.hibernate.Session;
import org.junit.BeforeClass;
import org.junit.Test;

import de.uzk.hki.da.core.HibernateUtil;

import de.uzk.hki.da.model.Node; 

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
	
	/**
	 * Sets the up before class.
	 */
	@BeforeClass
	public static void setUpBeforeClass(){
		HibernateUtil.init("src/main/xml/hibernateCentralDB.cfg.xml.inmem");
		Session session = HibernateUtil.openSession();
		session.beginTransaction();
		for (int i=0;i<inserts.length;i++)
			session.createSQLQuery(inserts[i]).executeUpdate();
		session.getTransaction().commit();
		session.close();
	}
	
	@Test
	public void test() {
		Node node = new Node();
		node.setName("testnode");
		
		JobManager jm = new JobManager();
		jm.fetchJobsExecuteActions(node);
		
		
	}
}
