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

package de.uzk.hki.da.model;

import static org.junit.Assert.fail;

import org.hibernate.Session;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import de.uzk.hki.da.core.HibernateUtil;
import de.uzk.hki.da.model.CentralDatabaseDAO;
import de.uzk.hki.da.model.ConversionInstruction;
import de.uzk.hki.da.model.ConversionRoutine;
import de.uzk.hki.da.model.Job;
 

/**
 * The Class CentralDatabaseDAOTests.
 */
public class CentralDatabaseDAOTests {
	
	/** The dao. */
	private static CentralDatabaseDAO dao = new CentralDatabaseDAO();
	
	/** The Constant inserts. */
	private static final String inserts[] = new String[]{
		"INSERT INTO nodes (urn_index) VALUES (1)",
		"INSERT INTO conversion_routines (name,type,params,target_suffix) " +
			"VALUES ('abc','de.uzk.hki.da.cb.CLIConversionStrategy','convert input output','png')",
		"INSERT INTO conversion_routines (name,type,params,target_suffix) " +
			"VALUES ('def','de.uzk.hki.da.cb.CLIConversionStrategy','convert input output','png')",
		"INSERT INTO contractors (short_name,forbidden_nodes,email_contact,id) " +
			"VALUES ('TEST_1','','da-nrw-notifier@uni-koeln.de','1')",
		"INSERT INTO contractors (short_name,forbidden_nodes,email_contact,id) " +
			"VALUES ('TEST_2','','da-nrw-notifier@uni-koeln.de','2')",
		"INSERT INTO objects (data_pk,initial_node,urn,orig_name,date_created,date_modified,zone," +
			"published_flag,contractor_id,object_state) VALUES ('109733','da-nrw-vm3.hki.uni-koeln.de'," +
			"'urn+nbn+de+danrw-1-2012113022771','test_object','1354276007948','1354276113286','da-nrw','0','1','100')",
		"INSERT INTO objects (data_pk,initial_node,urn,orig_name,date_created,date_modified,zone," +
			"published_flag,contractor_id,object_state) VALUES ('109734','da-nrw-vm3.hki.uni-koeln.de'," +
			"'urn+nbn+de+danrw-1-2012113022772','test_object_double','1354276007948','1354276113286','da-nrw','0','1','100')",
		"INSERT INTO objects (data_pk,initial_node,urn,orig_name,date_created,date_modified,zone," +
			"published_flag,contractor_id,object_state) VALUES ('109735','da-nrw-vm3.hki.uni-koeln.de'," +
			"'urn+nbn+de+danrw-1-2012113022773','test_object_double','1354276007948','1354276113286','da-nrw','0','1','100')"
	};
	
	/**
	 * Sets the up before class.
	 */
	@BeforeClass
	public static void setUpBeforeClass(){
		HibernateUtil.init("src/main/xml/hibernateCentralDB.cfg.xml.inmem");
		Session session = HibernateUtil.openSession();
		session.beginTransaction();
		
		// necesarry because some previously written test left garbage behind
		session.createSQLQuery("DELETE FROM conversion_routines_nodes").executeUpdate(); 
		session.createSQLQuery("DELETE FROM nodes").executeUpdate();
		
		for (int i=0;i<inserts.length;i++)
			session.createSQLQuery(inserts[i]).executeUpdate();
		
		session.getTransaction().commit();
		session.close();
	}
	
	/**
	 * Tear down after class.
	 */
	@AfterClass
	public static void tearDownAfterClass(){}
	
	
	/**
	 * Test add conversion instruction with invalid routine.
	 */
	@Test
	public void testAddConversionInstructionWithInvalidRoutine(){
		Session session = HibernateUtil.openSession();
		session.beginTransaction();
		
		Job job = new Job("testContractor", "testNode");
		session.save(job);
		
		ConversionRoutine routine = new ConversionRoutine();
		routine.setId(444);
		
		ConversionInstruction ci = new ConversionInstruction(
				job.getId(),  "target", 
				routine, "");
		
		try{
			session.save(ci);
			fail();
		}catch(Exception e)
		{                 }
		
		session.close();
		
	}
	
}
