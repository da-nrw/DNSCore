/*
  DA-NRW Software Suite | ContentBroker
  Copyright (C) 2013 Historisch-Kulturwissenschaftliche Informationsverarbeitung
  Universität zu Köln
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

package de.uzk.hki.da.model;

import static org.junit.Assert.fail;

import org.hibernate.Session;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import de.uzk.hki.da.core.HibernateUtil;
import de.uzk.hki.da.test.TESTHelper;

/**
 * The Class ModelTest.
 */
public class ModelTest {
	
	private static final String inserts[] = new String[]{
		"INSERT INTO nodes (urn_index) VALUES (1)",
		"INSERT INTO conversion_routines (name,type,params,target_suffix) " +
			"VALUES ('abc','de.uzk.hki.da.cb.CLIConversionStrategy','convert input output','png')",
		"INSERT INTO conversion_routines (name,type,params,target_suffix) " +
			"VALUES ('def','de.uzk.hki.da.cb.CLIConversionStrategy','convert input output','png')",
		"INSERT INTO users (short_name,forbidden_nodes,email_contact,id) " +
			"VALUES ('TEST_1','','da-nrw-notifier@uni-koeln.de','1')",
		"INSERT INTO users (short_name,forbidden_nodes,email_contact,id) " +
			"VALUES ('TEST_2','','da-nrw-notifier@uni-koeln.de','2')",
	};
	
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
	
	@AfterClass
	public static void tearDownAfterClass(){
		TESTHelper.clearDB();
		Session session = HibernateUtil.openSession();
		session.beginTransaction();
		session.createSQLQuery("DELETE FROM users").executeUpdate();
		session.createSQLQuery("DELETE FROM conversion_routines").executeUpdate();
		session.createSQLQuery("DELETE FROM nodes").executeUpdate();
		session.getTransaction().commit();
		session.close();
	}

	
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
