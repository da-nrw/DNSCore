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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.hibernate.Session;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import de.uzk.hki.da.cb.AbstractAction;
import de.uzk.hki.da.model.CentralDatabaseDAO;
import de.uzk.hki.da.model.Contractor;
import de.uzk.hki.da.model.Job;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.model.Package;
import de.uzk.hki.da.model.Node;
import de.uzk.hki.da.utils.Path;



/**
 * The Class ActionFactoryTests.
 */
public class ActionFactoryTests {
	
	/** The base dir path. */
	String baseDirPath="src/test/resources/core/ActionFactoryTests/";
	
	/** The factory. */
	private ActionFactory factory;
	
	/** The c. */
	private Contractor c = new Contractor();
	
	private static int nodeId;
	
	@BeforeClass
	public static void beforeClass() {
		HibernateUtil.init("src/main/xml/hibernateCentralDB.cfg.xml.inmem");
		
		Session session = HibernateUtil.openSession();
		session.beginTransaction();
		
		Node node = new Node("localnode");
		session.save(node);
		session.getTransaction().commit();	
		nodeId = node.getId();
		session.close();		
		
		
	}
	
	/**
	 * Sets the up.
	 */
	@Before
	public void setUp() {
		c.setShort_name("csn");
		
		CentralDatabaseDAO dao = new CentralDatabaseDAO();

		FileSystemXmlApplicationContext context = new FileSystemXmlApplicationContext(baseDirPath+"action-definitions.xml");
		factory = new ActionFactory();
		factory.setApplicationContext(context);
		factory.setDao(dao);
		factory.setActionRegistry((ActionRegistry)context.getBean("actionRegistry"));
	}
	
	/**
	 * Test build next action.
	 */
	@Test
	public void testBuildNextAction() {
		
		CentralDatabaseDAO dummyDao = mock(CentralDatabaseDAO.class);

		Job j = new Job("localnode", "450"); 
		Object o = new Object();
		Package p = new Package();
		o.getPackages().add(p);
		j.setObject(o);
		
		when(dummyDao.fetchJobFromQueue(anyString(),anyString(),(Node)anyObject())).
			thenReturn(j);
		
		factory.setDao(dummyDao);	
		factory.setLocalNode(new Node());
		
		AbstractAction a = factory.buildNextAction();
		assertNotNull(a);
		assertEquals("450", a.getStartStatus());
		assertEquals("460", a.getEndStatus());
		
		assertNotNull(a.getDao());
//		assertEquals("csn", a.getJob().getObject().getContractor().getShort_name()); XXX used?
		assertNotNull(a.getActionMap());
		
	}
	
	/**
	 * Test no job found.
	 */
	@Test
	public void testNoJobFound(){
		
		CentralDatabaseDAO dummyDao = mock(CentralDatabaseDAO.class);

		when(dummyDao.fetchJobFromQueue(anyString(),anyString(),(Node)anyObject())).
			thenReturn(null);
		
		Node node = new Node("localnode");
		node.setId(nodeId);
		factory.setDao(dummyDao);
		factory.setLocalNode(node);
		
		AbstractAction a = factory.buildNextAction();
		assertNull(a);
	}
	
	
	
	
}
