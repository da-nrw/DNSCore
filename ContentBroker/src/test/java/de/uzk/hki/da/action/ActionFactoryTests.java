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

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import de.uzk.hki.da.cb.NullAction;
import de.uzk.hki.da.core.SubsystemNotAvailableException;
import de.uzk.hki.da.core.UserExceptionManager;
import de.uzk.hki.da.model.Job;
import de.uzk.hki.da.model.Node;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.model.Package;
import de.uzk.hki.da.model.PreservationSystem;
import de.uzk.hki.da.model.JobNamedQueryDAO;
import de.uzk.hki.da.model.User;
import de.uzk.hki.da.service.HibernateUtil;
import de.uzk.hki.da.util.ConfigurationException;
import de.uzk.hki.da.util.Path;



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
	
	private static final JobNamedQueryDAO queueConnector = mock(JobNamedQueryDAO.class);

	private PreservationSystem ps;

	private FileSystemXmlApplicationContext context;
	
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
		c.setEmailAddress("noreply");
		
		context = mock(FileSystemXmlApplicationContext.class);
		AbstractAction action = new NullAction(); action.setStartStatus("450"); action.setName("tarAction"); action.setEndStatus("460");
		when(context.getBean(anyString())).thenReturn(action);
		
		factory = new ActionFactory();
		factory.setApplicationContext(context);
		factory.setActionRegistry((ActionRegistry)new FileSystemXmlApplicationContext(baseDirPath+"action-definitions.xml").getBean("actionRegistry"));
		ps = new PreservationSystem(); ps.setId(1); ps.setMinRepls(1); 
		ps.setUrnNameSpace("urn"); ps.setUrisCho("abc"); ps.setUrisFile("abc"); ps.setUrisLocal("abc");
		ps.setUrisAggr("abc");
		User psadmin = new User(); psadmin.setUsername("psadmin"); psadmin.setEmailAddress("abc");
		ps.setAdmin(psadmin);
		factory.setPreservationSystem(ps);
		factory.setUserExceptionManager(new UserExceptionManager());
		factory.setQueueConnector(queueConnector);	
		
	}
	
	private Job makeGoodJob() { 
		Job j=new Job("localnode", "450"); 
		Object o = new Object();
		o.setIdentifier("identifier");
		o.setContractor(c);
		Package p = new Package(); p.setName("1"); p.setContainerName("cname");
		o.getPackages().add(p);
		j.setObject(o);
		Node n= new Node();
		n.setWorkAreaRootPath(Path.make("/tmp"));
		factory.setLocalNode(n);
		return j;
	}
	
	
	/**
	 * Test build next action.
	 */
	@Test
	public void testBuildNextAction() {
		
		when(queueConnector.fetchJobFromQueue(anyString(),anyString(),(Node)anyObject())).
			thenReturn(makeGoodJob());
		
		AbstractAction a = factory.buildNextAction();
		assertNotNull(a);
		assertEquals("450", a.getStartStatus());
		assertEquals("460", a.getEndStatus());
		assertNotNull(a.getActionMap());
	}
	
	@Test
	public void badSystemState() {

		Job j=makeGoodJob();
		j.getObject().setContractor(null);
		when(queueConnector.fetchJobFromQueue(anyString(),anyString(),(Node)anyObject())).
			thenReturn(j);
		factory.buildNextAction();
		verify(queueConnector,times(1)).updateJobStatus(j, "455");
	}
	
	@Test
	public void badConfiguration() {
		AbstractAction action = new ConfigurationExceptionAction(); action.setStartStatus("450"); action.setName("tarAction"); action.setEndStatus("460");
		when(context.getBean(anyString())).thenReturn(action);
		
		Job j=makeGoodJob();
		when(queueConnector.fetchJobFromQueue(anyString(),anyString(),(Node)anyObject())).
			thenReturn(j);
		assertTrue(factory.buildNextAction()==null);
		verify(queueConnector,times(1)).updateJobStatus(j, "450");
	}
	
	
	
	@Test
	public void systemStateBad() {
		assertFalse(factory.paused());
		AbstractAction action=factory.buildNextAction();
		assertTrue(action==null);
		assertTrue(factory.paused());
	}
	
	
	
	
	/**
	 * Test no job found.
	 */
	@Test
	public void testNoJobFound(){
		
		when(queueConnector.fetchJobFromQueue(anyString(),anyString(),(Node)anyObject())).
			thenReturn(null);
		
		Node node = new Node("localnode");
		node.setId(nodeId);
		factory.setQueueConnector(queueConnector);
		factory.setLocalNode(node);
		
		AbstractAction a = factory.buildNextAction();
		assertNull(a);
	}
	
	
	class ConfigurationExceptionAction extends NullAction{
		@Override
		public void checkConfiguration() {
			throw new ConfigurationException("Bad configuration");
		}
	}
}
