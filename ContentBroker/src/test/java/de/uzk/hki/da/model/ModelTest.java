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

import org.junit.Test;


/**
 * The Class ModelTest.
 */
public class ModelTest {
//	private static CentralDatabaseManagerDebugExtension debugDAO = new CentralDatabaseManagerDebugExtension();
	
	/** The debug dao. */
	
	/**
	 * Model test.
	 */
	@Test
	/** 
	 * Makes sure that if one changes the field package of Job the change gets propagated
	 * to the packages table.
	 * @author Daniel
	 */
	public void modelTest(){
		
//		HibernateUtil.init("conf/hibernateCentralDbWithInmem.cfg.xml");
//		Session session = HibernateUtil.getThreadBoundSession();
//		session.beginTransaction();
//		Job job = new Job("urn", "csn", "vm3");
//		session.save(job);
//		
//		Package pkg = new Package();
//		object.setPackage(pkg);
//		session.save(pkg);
//		
//		job.getPackage().setName("_1");
//
//		assertEquals( "_1", pkg.getName() );
//		
//		debugDAO.showPackages();
//		session.getTransaction().commit();
	}
}
