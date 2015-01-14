/*
  DA-NRW Software Suite | ContentBroker
  Copyright (C) 2013 Historisch-Kulturwissenschaftliche Informationsverarbeitung
  Universität zu Köln
  Copyright (C) 2014 LVR-InfoKom
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

package de.uzk.hki.da.core;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Set;

import org.hibernate.Session;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import de.uzk.hki.da.model.User;
import de.uzk.hki.da.service.HibernateUtil;

/**
 * @author Daniel M. de Oliveira
 */
public class IngestAreaScannerWorkerTests {

	String basePath = "src/test/resources/core/IngestAreaScannerWorker/";
	String ingestAreaRootPath = basePath+"ingest/";

	private static User user1;
	private static User user2;
	
	@BeforeClass
	public static void setUpBeforeClass() throws IOException{
		
		HibernateUtil.init("src/main/xml/hibernateCentralDB.cfg.xml.inmem");
		Session session = HibernateUtil.openSession();
		session.getTransaction().begin();
		user1 = new User(); user1.setShort_name("USER1"); session.save(user1);
		user2 = new User(); user2.setShort_name("USER2"); session.save(user2);
		session.getTransaction().commit();
		session.close();
	
	}

	
	@AfterClass
	public static void tearDown() throws IOException{
		Session session = HibernateUtil.openSession();
		session.getTransaction().begin();
		session.createQuery("DELETE FROM User").executeUpdate();
		session.getTransaction().commit();
		session.close();
	}

	@Test
	public void initialization(){

		IngestAreaScannerWorker scanner = new IngestAreaScannerWorker();
		scanner.setIngestAreaRootPath(ingestAreaRootPath);
		Set<User> contractorsWhoseFoldersGetScanned = scanner.init();
		
		assertTrue(contractorsWhoseFoldersGetScanned.contains(user1));
		assertTrue(contractorsWhoseFoldersGetScanned.contains(user2));

		assertSame(contractorsWhoseFoldersGetScanned.size(),2); 
		// which means a) ignoring USER3 because he is not in the DB and b) ignoring files below ingestAreaRootPath.
	}
	
}
