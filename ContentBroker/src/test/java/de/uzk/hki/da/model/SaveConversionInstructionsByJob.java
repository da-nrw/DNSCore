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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.hibernate.Session;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.uzk.hki.da.service.HibernateUtil;


/**
 * The Class SaveConversionInstructionsByJob.
 */
public class SaveConversionInstructionsByJob {

	/** The id. */
	int id;
	
	/**
	 * Sets the up.
	 */
	@Before
	public void setUp(){
		HibernateUtil.init("src/main/conf/hibernateCentralDB.cfg.xml.inmem");
		Session session = HibernateUtil.openSession();
		session.beginTransaction();
		
		Job job = new Job();
		
		
		ConversionInstruction ci = new ConversionInstruction();
		ci.setTarget_folder("abc");
		job.getConversion_instructions().add(ci);

		session.save(job);      
		id = job.getId();
		session.getTransaction().commit();
		session.close();
	}
	
	/**
	 * Tear down.
	 */
	@After
	public void tearDown(){
		
	}
	
	/**
	 * Test orphan removal.
	 */
	@Test
	public void testOrphanRemoval(){
		Session session = HibernateUtil.openSession();
		session.beginTransaction();
		
		Job job = (Job) session.get(Job.class,id);
		
		job.getConversion_instructions().clear();
		
		session.getTransaction().commit();
		session.close();
		
		session = HibernateUtil.openSession();
		session.beginTransaction();
		
		job = (Job) session.get(Job.class,id);
		
		assertTrue(job.getConversion_instructions().isEmpty());
		
		session.close();
	}
	
	/**
	 * Test automatic insertion.
	 */
	@Test
	public void testAutomaticInsertion() {
		Session session = HibernateUtil.openSession();
		session.beginTransaction();
		Job job = (Job) session.get(Job.class,id);
		assertFalse(job.getConversion_instructions().isEmpty());
		session.close();
		
		
		ConversionInstruction getBack = new ArrayList<ConversionInstruction>(job.getConversion_instructions()).get(0);
		assertEquals("abc", getBack.getTarget_folder());
		
	}
	
	

}
