/*

  DA-NRW Software Suite | ContentBroker
  Copyright (C) 2014 LVRInfoKom
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import de.uzk.hki.da.cb.AbstractAction;
import de.uzk.hki.da.cb.NullAction;

/**
 * @author Daniel M. de Oliveira
 */
public class NewActionRegistryTests {

	private static final String ACTION_NAME = "NullAction";
	private NewActionRegistry registry;
	private AbstractAction action1;
	private AbstractAction action2;
	private AbstractAction action3;

	@Before
	public void setUp(){
		registry = new NewActionRegistry();
		
		Map<String,Integer> maxThreads = new HashMap<String,Integer>();
		maxThreads.put("NullAction", 3);
		registry.setMaxThreads(maxThreads);
		
		action1 = new NullAction();
		action1.setName(ACTION_NAME);
		action2 = new NullAction();
		action2.setName(ACTION_NAME);
		action3 = new NullAction();
		action3.setName(ACTION_NAME);
	}
	
	@Test
	public void testSimpleRegistration() {
		
		registry.register(action1);
		
		assertSame(action1,registry.getRunningActions().get(0));
	}
	
	@Test
	public void testNoNameSet() {
		
		action1.setName(null);
		try {
			registry.register(action1);
			fail();
		} catch (IllegalArgumentException e) {}
	}
	
	
	@Test
	public void maxThreadsNotDefinedForAction(){
		action1.setName("anActionTypeWhereThreadsHasNotBeenDefinedForPreviously");
		try {
			registry.register(action1);
			fail();
		} catch (IllegalStateException e) {}
	}
	
	@Test
	public void testRegisterTwoActionsOfSameType() {
		
		registry.register(action1);
		registry.register(action2);
		
		assertEquals(2,registry.getRunningActions().size());
	}
	
	@Test
	public void testDontRegisterSameActionTwice() {

		registry.register(action1);
		try {
			registry.register(action1);
			fail();
		} catch (IllegalArgumentException e) {}
	}
	
	@Test
	public void deregisterOne() {

		registry.register(action1);
		registry.register(action2);
		registry.deregister(action1);
		
		assertEquals(1,registry.getRunningActions().size());
	}
	
	@Test
	public void testDeristerOneWhichIsNotPresent(){
		
		try{ 
			registry.deregister(action1);
			fail();
		} catch (Exception e) {}
	}
	
	
	
	@Test
	public void testListRemainingThreads() {
		
		registry.register(action1);
		assertEquals(new Integer(2),
				registry.calculateRemainingThreadsForNamedActions().get(ACTION_NAME));
		
		registry.register(action2);
		assertEquals(new Integer(1),
				registry.calculateRemainingThreadsForNamedActions().get(ACTION_NAME));
		
		registry.register(action3);
		assertEquals(null,
				registry.calculateRemainingThreadsForNamedActions().get(ACTION_NAME));
	}
	
}
