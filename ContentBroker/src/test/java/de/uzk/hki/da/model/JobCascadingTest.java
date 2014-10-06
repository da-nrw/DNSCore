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

import org.hibernate.Session;
import org.junit.Test;

import de.uzk.hki.da.core.HibernateUtil;


/**
 * The Class JobCascadingTest.
 */
public class JobCascadingTest {

	/**
	 * Test.
	 */
	@Test
	public void test(){
		HibernateUtil.init("src/main/xml/hibernateCentralDB.cfg.xml.inmem");
		Session session = HibernateUtil.openSession();
		session.beginTransaction();
		
			Job job = new Job();
			session.save(job);
			int id = job.getId();
		
		session.getTransaction().commit();
		session.close();
		
		session = HibernateUtil.openSession();
		session.beginTransaction();
		
			Job ret = (Job) session.get(Job.class,id);
			Job child = new Job();
		
			ret.getChildren().add(child);
		
		session.getTransaction().commit();
		session.close();
		
		session = HibernateUtil.openSession();
		session.beginTransaction();

		session.getTransaction().commit();
		session.close();
	}
}
