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

package de.uzk.hki.da.action;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import de.uzk.hki.da.action.AbstractAction;
import de.uzk.hki.da.action.NewActionRegistry;
import de.uzk.hki.da.cb.NullAction;

/**
 * @author Daniel M. de Oliveira
 */
public class NewActionRegistryTests {

	private static final String ACTION_NAME = "NullAction";
	private NewActionRegistry registry;
	private AbstractAction actionInstance1;
	private AbstractAction actionInstance2;
	private AbstractAction actionInstance3;
	private NullAction actionType1;

	@Before
	public void setUp(){
		registry = new NewActionRegistry();
		
		Map<AbstractAction,Integer> maxThreads = new HashMap<AbstractAction,Integer>();
		actionType1 = new NullAction();
		actionType1.setName(ACTION_NAME);
		maxThreads.put(actionType1, 3);
		registry.setActionTypeThreadsAllowed(maxThreads);
		
		
		actionInstance1 = new NullAction();
		actionInstance1.setName(ACTION_NAME);
		actionInstance2 = new NullAction();
		actionInstance2.setName(ACTION_NAME);
		actionInstance3 = new NullAction();
		actionInstance3.setName(ACTION_NAME);
	}
	
	@Test
	public void testSimpleRegistration() {
		
		registry.register(actionInstance1);
		
		assertSame(actionInstance1,registry.getRunningActionsInstances().get(0));
	}
	
	@Test
	public void testNoNameSet() {
		
		actionInstance1.setName(null);
		try {
			registry.register(actionInstance1);
			fail();
		} catch (IllegalArgumentException e) {}
	}
	
	
	@Test
	public void maxThreadsNotDefinedForAction(){
		registry.setActionTypeThreadsAllowed(new HashMap<AbstractAction,Integer>());
		try {
			registry.register(actionInstance1);
			fail();
		} catch (IllegalStateException e) {}
	}
	
	@Test
	public void testRegisterTwoActionsOfSameType() {
		
		registry.register(actionInstance1);
		registry.register(actionInstance2);
		
		assertEquals(2,registry.getRunningActionsInstances().size());
	}
	
	@Test
	public void testDontRegisterSameActionTwice() {

		registry.register(actionInstance1);
		try {
			registry.register(actionInstance1);
			fail();
		} catch (IllegalArgumentException e) {}
	}
	
	@Test
	public void deregisterOne() {

		registry.register(actionInstance1);
		registry.register(actionInstance2);
		registry.deregister(actionInstance1);
		
		assertEquals(1,registry.getRunningActionsInstances().size());
	}
	
	@Test
	public void testDeregisterOneWhichIsNotPresent(){
		
		try{ 
			registry.deregister(actionInstance1);
			fail();
		} catch (Exception e) {}
	}
	
	
	
	@Test
	public void testListRemainingThreads() {
		
		registry.register(actionInstance1);
		assertEquals(new Integer(2),
				registry.calculateActionTypeRemainingThreads().get(actionType1));
		
		registry.register(actionInstance2);
		assertEquals(new Integer(1),
				registry.calculateActionTypeRemainingThreads().get(actionType1));
		
		registry.register(actionInstance3);
		assertEquals(null,
				registry.calculateActionTypeRemainingThreads().get(actionType1));
	}
	
}
