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

package de.uzk.hki.da.action;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import de.uzk.hki.da.core.HibernateUtil;
import de.uzk.hki.da.model.Job;
import de.uzk.hki.da.model.Node;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.model.Package;
import de.uzk.hki.da.model.PreservationSystem;
import de.uzk.hki.da.model.User;
import de.uzk.hki.da.service.UserExceptionManager;



/**
 * The Class ActionFactoryTests.
 */
public class ActionFactoryTests {
	
	/** The base dir path. */
	String baseDirPath="src/test/resources/action/ActionFactoryTests/";
	
	/** The factory. */
	private ActionFactory factory;
	
	/** The c. */
	private User c = new User();
	
	private static int nodeId;
	
	@BeforeClass
	public static void beforeClass() {
		HibernateUtil.init("src/main/xml/hibernateCentralDB.cfg.xml.inmem");
	}
	
	/**
	 * Sets the up.
	 */
	@Before
	public void setUp() {
		c.setShort_name("csn");
		c.setEmailAddress("useremail");
		
		FileSystemXmlApplicationContext context = new FileSystemXmlApplicationContext(baseDirPath+"action-definitions.xml");
		factory = new ActionFactory();
		factory.setApplicationContext(context);
		factory.setActionRegistry((ActionRegistry)context.getBean("actionRegistry"));
		PreservationSystem ps = new PreservationSystem(); ps.setId(1); ps.setMinRepls(1);
		User psadmin = new User(); psadmin.setUsername("psadmin");
		ps.setAdmin(psadmin);
		factory.setPreservationSystem(ps);
		factory.setUserExceptionManager(new UserExceptionManager());
		
	}
	
	/**
	 * Test build next action.
	 */
	@Test
	public void testBuildNextAction() {
		
		QueueConnector queueConnector = mock(QueueConnector.class);

		Job j = new Job("localnode", "450"); 
		Object o = new Object();
		o.setIdentifier("identifier");
		o.setContractor(c);
		Package p = new Package(); p.setName("1"); p.setContainerName("cname");
		o.getPackages().add(p);
		j.setObject(o);
		
		when(queueConnector.fetchJobFromQueue(anyString(),anyString(),(Node)anyObject(),(PreservationSystem)anyObject())).
			thenReturn(j);
		
		factory.setQueueConnector(queueConnector);	
		factory.setLocalNode(new Node());
		
		AbstractAction a = factory.buildNextAction();
		assertNotNull(a);
		assertEquals("450", a.getStartStatus());
		assertEquals("460", a.getEndStatus());
		
//		assertEquals("csn", a.getJob().getObject().getContractor().getShort_name()); XXX used?
		assertNotNull(a.getActionMap());
	}
	
	/**
	 * Test no job found.
	 */
	@Test
	public void testNoJobFound(){
		
		QueueConnector queueConnector = mock(QueueConnector.class);

		when(queueConnector.fetchJobFromQueue(anyString(),anyString(),(Node)anyObject(),(PreservationSystem)anyObject())).
			thenReturn(null);
		
		Node node = new Node("localnode");
		node.setId(nodeId);
		factory.setQueueConnector(queueConnector);
		factory.setLocalNode(node);
		
		AbstractAction a = factory.buildNextAction();
		assertNull(a);
	}
	
	
	
	
}
