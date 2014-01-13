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

package de.uzk.hki.da.core;

import org.hibernate.classic.Session;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import de.uzk.hki.da.db.CentralDatabaseDAO;
import de.uzk.hki.da.db.HibernateUtil;
import de.uzk.hki.da.model.Node;


/**
 * The Class ContentBrokerTests.
 */
public class ContentBrokerTests {

	/** The base dir path. */
	private String baseDirPath = "src/test/resources/core/ContentBrokerTests/";
	
	/** The content broker. */
	private ContentBroker contentBroker;
	private static CentralDatabaseDAO dao;
	
	/**
	 * Sets the up.
	 */
	@BeforeClass
	public static void setUp(){
		HibernateUtil.init("src/main/conf/hibernateCentralDb.cfg.xml.inmem");
		Node node = new Node(131614,"da-nrw-vm3.hki.uni-koeln.de");

		Session session = HibernateUtil.openSession();
		session.beginTransaction();
		session.save(node);
		session.getTransaction().commit();
		session.close();
		
		System.out.println(node.getId());
		
		
	}
	
	/**
	 * Test schedule task.
	 */
	@Test
	public void testScheduleTask() {
		
		FileSystemXmlApplicationContext context = new FileSystemXmlApplicationContext(baseDirPath+"spring-context.xml");
		contentBroker = (ContentBroker) context.getBean("contentBroker");

		dao = new CentralDatabaseDAO();
		contentBroker.getActionFactory().setDao(dao);
		contentBroker.scheduleTaskWithoutCatchingExceptions();
		contentBroker.scheduleTaskWithoutCatchingExceptions();
		contentBroker.scheduleTaskWithoutCatchingExceptions();
		contentBroker.scheduleTaskWithoutCatchingExceptions();
		contentBroker.scheduleTaskWithoutCatchingExceptions();
		
		context.close();
	}

}
