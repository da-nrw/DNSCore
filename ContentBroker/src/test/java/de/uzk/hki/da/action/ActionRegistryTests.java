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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import de.uzk.hki.da.action.AbstractAction;
import de.uzk.hki.da.action.ActionDescription;
import de.uzk.hki.da.action.ActionRegistry;
import de.uzk.hki.da.cb.NullAction;
import de.uzk.hki.da.cb.TarAction;
import de.uzk.hki.da.model.Job;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.model.Package;



/**
 * The Class ActionRegistryTests.
 * @author Daniel M. de Oliveira
 * @author Jens Peters
 */
public class ActionRegistryTests {
	
	/** The context. */
	FileSystemXmlApplicationContext context = null;

	/** The registry. */
	ActionRegistry registry;
	
	/**
	 * Sets the up.
	 * @throws IOException 
	 */
	@Before
	public void setUp() throws IOException{
		FileUtils.copyFile(new File("src/main/conf/config.properties.dev"), 
				new File("conf/config.properties"));
		
		context = new FileSystemXmlApplicationContext(
				"src/test/resources/action/ActionRegistryTests/action-definitions.xml" );
		registry = (ActionRegistry) context.getBean("actionRegistry");
	}
	
	@After 
	public void tearDown(){
		new File("conf/config.properties").delete();
	}
	
	
	/**
	 * Test report.
	 */
	@Test
	public void testReport(){
		Object o = new Object();
		Package p = new Package();
		p.setName("1");
		o.getPackages().add(p);
		
		
		Job job = new Job();
		job.setObject(o);
		job.setId(1);
		AbstractAction tarAction = (AbstractAction) context.getBean("IngestTarAction");
		tarAction.setJob(job);
		registry.registerAction(tarAction);
		
		List<ActionDescription> list = registry.getCurrentActionDescriptions();
		assertEquals("IngestTarAction",list.get(0).getActionType());
	}
	
	/**
	 * Test register actions.
	 */
	@Test
	public void testRegisterActions() {
		
		AbstractAction tarAction = (AbstractAction) context.getBean("IngestTarAction");
		AbstractAction sendToPresenterAction = (AbstractAction) context.getBean("SendToPresenterAction");
		
		registry.registerAction(tarAction);
		registry.registerAction(tarAction);
		registry.deregisterAction(tarAction);
		registry.registerAction(tarAction);
		registry.registerAction(sendToPresenterAction);
		assertTrue(registry.getJobTypesWithMaxThreads().contains("IngestTarAction"));
		assertTrue(registry.getJobTypesWithMaxThreads().contains("SendToPresenterAction"));
	}
	
	/**
	 * Test unknown action.
	 */
	@Test
	public void testUnknownAction() {
		
		AbstractAction action = new TarAction();
		
		try {
			registry.registerAction(action);
			fail();
		} catch (IllegalStateException e) {
			System.out.println("Caught expected Exception: " + e.getMessage());
		}
	}
	
	/**
	 * Test active threads lower than zero.
	 */
	@Test
	public void testActiveThreadsLowerThanZero() {
		
		AbstractAction tarAction = (AbstractAction) context.getBean("IngestTarAction");
		
		try {
			registry.registerAction(tarAction);
			registry.deregisterAction(tarAction);
			registry.deregisterAction(tarAction);
			fail();
		} catch (IllegalStateException e) {
			System.out.println("Caught expected Exception: " + e.getMessage());
		}
		
	}
	
	
	
	/**
	 */
	@Test
	public void testGetLowerPriorizedActionIfMaxThreads() {
		
		AbstractAction action = (NullAction) context.getBean("BlockingAction");
		registry.registerAction(action);

		AbstractAction action2 = (NullAction) context.getBean("IngestTarAction");
		registry.registerAction(action2);
		
		List<String> list = registry.getAvailableJobTypes();
		assertEquals("SendToPresenterAction",list.get(0));
	}
	
	
	/**
	 * 
	 */
	@Test
	public void testOneActionsIsBlockedByAnother(){
		AbstractAction blocking = (NullAction) context.getBean("BlockingAction");
		registry.registerAction(blocking);
		
		assertEquals("IngestTarAction",registry.getAvailableJobTypes().get(0));
	}
	
	
	
	/**
	 * 
	 */
	@Test
	public void testAnotherActionIsBlocked(){
		
		registry.registerAction((NullAction) context.getBean("BlockingAction"));
		registry.registerAction((NullAction) context.getBean("IngestTarAction"));
		registry.registerAction((NullAction) context.getBean("SendToPresenterAction"));
		registry.registerAction((NullAction) context.getBean("AnotherBlockingAction"));
		
		assertTrue(registry.getAvailableJobTypes().isEmpty());
	}
	
	
	
	
	
	
	
	/**
	 * Test job with max threads.
	 */
	@Test
	public void testJobWithMaxThreads() {	
		
		AbstractAction tarAction = (AbstractAction) context.getBean("IngestTarAction");
		registry.registerAction(tarAction);
		List<String> list = registry.getJobTypesWithMaxThreads();
		assertEquals("IngestTarAction",list.get(0));
	}
	
}
