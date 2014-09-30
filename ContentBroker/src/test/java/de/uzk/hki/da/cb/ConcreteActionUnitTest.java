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
package de.uzk.hki.da.cb;

import java.lang.reflect.Field;

import org.junit.Before;

import de.uzk.hki.da.action.AbstractAction;
import de.uzk.hki.da.model.Job;
import de.uzk.hki.da.model.Node;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.model.Package;
import de.uzk.hki.da.model.PreservationSystem;
import de.uzk.hki.da.model.User;
import de.uzk.hki.da.test.TC;

/**
 * Provides the basic framework for effective tests of the business code
 * distributed over the different actions.
 * 
 * Subclasses should use 
 * <pre>
 * "@ActionUnderTest
 * ConcreteAction action = new ConcreteAction();
 * </pre> to mark which action is under test. The action then gets initialized
 * automatically as if it came directly from the ActionFactory.
 * <br>
 * <b>Note</b> that you must not use the JUnit @Before and @After annotations in subclasses.
 * 
 * @author Daniel M. de Oliveira
 *
 */
public class ConcreteActionUnitTest {

	PreservationSystem ps;
	Node n = null;
	Object o;
	Job j;
	private User c;
	
	protected static AbstractAction a;
	

	
	@Before
	public void setUpBeforeActionTest() throws Exception{
		ps = new PreservationSystem();
		ps.setId(1);
		User psadmin = new User();
		psadmin.setShort_name("TEST_PSADMIN");
		psadmin.setEmailAddress("noreply");
		ps.setAdmin(psadmin);
		
		n = new Node();
		n.setName("testnode");
		n.setAdmin(psadmin);
		
		ps.getNodes().add(n);
		
		c = new User();
		c.setShort_name("TEST");
		c.setEmailAddress("noreply");
		
		Package pkg = new Package();
		pkg.setName("1");
		pkg.setId(1);
		pkg.setContainerName("testcontainer.tgz");
		
		o = new Object();
		o.setContractor(c);
		o.setTransientNodeRef(n);
		o.setIdentifier(TC.IDENTIFIER);
		o.getPackages().add(pkg);
		o.reattach();
		o.setUrn("urn");
		
		j = new Job();
		
		ActionUnderTestAnnotationParser parser = new ActionUnderTestAnnotationParser();
		Field f = parser.parse(this.getClass());
		AbstractAction a = (AbstractAction) f.get(this);
		
		a.setObject(o);
		a.setLocalNode(n);
		a.setJob(j);
		a.setPSystem(ps);
	}
}
