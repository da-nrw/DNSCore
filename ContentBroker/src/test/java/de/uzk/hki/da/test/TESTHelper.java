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
package de.uzk.hki.da.test;
import java.util.List;

import org.hibernate.Session;

import de.uzk.hki.da.at.AcceptanceTest;
import de.uzk.hki.da.model.Node;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.model.Package;
import de.uzk.hki.da.model.PreservationSystem;
import de.uzk.hki.da.model.User;
import de.uzk.hki.da.service.HibernateUtil;
import de.uzk.hki.da.utils.Path;


/**
 * The Class TESTHelper.
 *
 * @author Daniel M. de Oliveira
 */
public class TESTHelper {

	/**
	 * Sets up the object.
	 *
	 * @param pkgId the pkg id
	 * @param workAreaRootPath the base path
	 * @return the object
	 */
	public static Object setUpObject(String identifier,Path workAreaRootPath){
		
		return setUpObject(identifier,workAreaRootPath,workAreaRootPath,workAreaRootPath);
	}
	
	/**
	 * @deprecated use ActionTest as a framework for the only caller retrievalactiontests.
	 *  
	 * @param workAreaRootPath
	 * @param ingestAreaRootPath
	 * @param userAreaRootPath
	 * @return
	 */
	public static PreservationSystem setUpPS(
			Path workAreaRootPath,
			Path ingestAreaRootPath,
			Path userAreaRootPath){
		
		PreservationSystem ps = new PreservationSystem();
		ps.setId(1);
		User psadmin = new User();
		psadmin.setShort_name("TEST_PSADMIN");
		psadmin.setEmailAddress("noreply");
		ps.setAdmin(psadmin);
		
		Node node = new Node(); 
		node.setName("testnode");
		node.setWorkAreaRootPath(workAreaRootPath);
		node.setIngestAreaRootPath(ingestAreaRootPath);
		node.setUserAreaRootPath(userAreaRootPath);
		node.setAdmin(psadmin);
		
		ps.getNodes().add(node);
		return ps;
	}
	
	
	/**
	 * Sets up the object.
	 *
	 * @param pkgId the pkg id
	 * @param workAreaRootPath the base path
	 * @return the object
	 */
	public static Object setUpObject(String identifier,
			Path workAreaRootPath,
			Path ingestAreaRootPath,
			Path userAreaRootPath){
		
		Node node = new Node(); 
		node.setName("testnode");
		node.setWorkAreaRootPath(workAreaRootPath);
		node.setIngestAreaRootPath(ingestAreaRootPath);
		node.setUserAreaRootPath(userAreaRootPath);
		
		User contractor = new User();
		contractor.setShort_name("TEST");
		contractor.setEmailAddress("noreply");
		
		Package pkg = new Package();
		pkg.setName("1");
		pkg.setId(1);
		pkg.setContainerName("testcontainer.tgz");
		
		Object o = new Object();
		o.setContractor(contractor);
		o.setIdentifier(identifier);
		o.getPackages().add(pkg);
		
		return o;
	}

	public static void clearDBOnlyTestUser() {
		Session session = HibernateUtil.openSession();
		session.beginTransaction();
		final User contract = AcceptanceTest.getContractor(session, "TEST");

		List joblist = session.createQuery("SELECT j FROM Job j left join j.obj as o where o.user = :CSN")
				.setParameter("CSN", contract).list();
		for (java.lang.Object o : joblist)
			session.delete(o);
		
		List objlist = session.createQuery(" FROM Object where user = :CSN").setParameter("CSN", contract).list();
		for (java.lang.Object o : objlist)
			session.delete(o);

		List copyJoblist = session
				.createQuery("FROM CopyJob as cj where cj.source_node_identifier like concat('%',:CSNPATH,'%')")
				.setParameter("CSNPATH", "/aip/TEST/").list();
		for (java.lang.Object o : copyJoblist)
			session.delete(o);

		session.getTransaction().commit();
		session.close();
	}

	public static void dirtyClearDB() {
		Session session = HibernateUtil.openSession();
		session.beginTransaction();

		session.createSQLQuery("DELETE FROM events").executeUpdate();
		session.createSQLQuery("DELETE FROM conversion_queue").executeUpdate();
		session.createSQLQuery("DELETE from documents").executeUpdate();
		session.createSQLQuery("DELETE from dafile_knownerror").executeUpdate();
		session.createSQLQuery("DELETE FROM dafiles").executeUpdate();
		session.createSQLQuery("DELETE FROM queue").executeUpdate();
		session.createSQLQuery("DELETE from messages").executeUpdate();
		session.createSQLQuery("DELETE FROM objects_packages").executeUpdate();
		session.createSQLQuery("DELETE FROM copies").executeUpdate();
		session.createSQLQuery("DELETE FROM copyjob").executeUpdate();
		session.createSQLQuery("DELETE FROM packages").executeUpdate();
		session.createSQLQuery("DELETE FROM objects").executeUpdate();

		session.getTransaction().commit();
		session.close();
	}

}
