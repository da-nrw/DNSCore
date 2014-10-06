/*
  DA-NRW Software Suite | ContentBroker
  
  Copyright (C) 2014 Historisch-Kulturwissenschaftliche Informationsverarbeitung
  Universität zu Köln
  
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
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.Before;
import org.junit.Test;

import de.uzk.hki.da.cb.NullAction;
import de.uzk.hki.da.core.UserException;
import de.uzk.hki.da.core.UserException.UserExceptionId;
import de.uzk.hki.da.model.Job;
import de.uzk.hki.da.model.Node;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.model.Package;
import de.uzk.hki.da.model.PreservationSystem;
import de.uzk.hki.da.model.User;
import de.uzk.hki.da.service.UserExceptionManager;

/**
 * 
 * @author Daniel M. de Oliveira
 *
 */
public class AbstractActionTests {

	Session mockSession = null;
	
	@Before
	public void setUp(){
		mockSession = mock(Session.class);
		Transaction mockTransaction = mock(Transaction.class);
		when(mockSession.getTransaction()).thenReturn(mockTransaction);
	}
	
	
	
	private void setCommonProperties(
			AbstractAction action, 
			String startStatus,String endStatus){
		
		Package pkg = new Package();
		pkg.setName("1");
		pkg.setContainerName("CONTAINER");
		
		UserExceptionManager userExceptionManager = mock(UserExceptionManager.class);
		when(userExceptionManager.getMessage((UserExceptionId) anyObject())).thenReturn("Ihr eingeliefertes Paket mit dem Namen %CONTAINER_NAME konnte im DA NRW nicht archiviert werden.\n\nGrund: Package ist nicht konsistent!\n\nMeldung:\n%ERROR_INFO\nEs ist wahrscheinlich, dass Fehler bei der Übertragung aufgetreten sind. Bitte versuchen Sie eine erneute Ablieferung.");
		action.setUserExceptionManager(userExceptionManager);
		
		action.setActionMap(mock(ActionRegistry.class));
		Job job = new Job();
		action.setJob(job);
		User c = new User(); c.setShort_name("TEST"); c.setEmailAddress("useremail");
		Object object = new Object();
		object.setIdentifier("ID");
		object.setContractor(c);
		object.getPackages().add(pkg);
		action.setObject(object);
		action.setStartStatus(startStatus);
		action.setEndStatus(endStatus);
		action.SUPPRESS_OBJECT_CONSISTENCY_CHECK=true;
		PreservationSystem ps = new PreservationSystem(); ps.setAdmin(c);
		action.setPSystem(ps);
		Node node = new Node(); node.setAdmin(c);
		action.setLocalNode(node);
	}
	
	
	@Test
	public void testImplementationSuccesful() {
		SuccessfulAction action = new SuccessfulAction();
		action.setSession(mockSession);
		setCommonProperties(action, "190", "200");
		
		action.run();
		
		verify(mockSession,times(1)).update(action.getJob());
		verify(mockSession,times(1)).update(action.getObject());
		assertEquals("200",action.getJob().getStatus());
	}
	
	@Test
	public void testImplementationExecutionAborted() {
		ExecutionAbortedAction action = new ExecutionAbortedAction();
		action.setSession(mockSession);
		setCommonProperties(action, "190", "200");
		
		action.run();
		
		verify(mockSession,times(1)).update(action.getJob());
		verify(mockSession,times(1)).update(action.getObject());
		assertEquals("190",action.getJob().getStatus());
	}

	@Test
	public void testImplementationThrowsUserException(){
		UserExceptionAction action = new UserExceptionAction();
		action.setSession(mockSession);
		setCommonProperties(action, "190", "200");
		
		action.run();
		
		verify(mockSession,times(1)).update(action.getJob());
		assertEquals("194",action.getJob().getStatus());
	}
	
	
	
	class SuccessfulAction extends NullAction{
		@Override
		public boolean implementation() {
			return true;
		}
	}
	
	class ExecutionAbortedAction extends NullAction{
		@Override
		public boolean implementation() {
			return false;
		}
	}
	
	class UserExceptionAction extends NullAction{
		@Override
		public boolean implementation() {
			throw new UserException(UserExceptionId.INCONSISTENT_PACKAGE,"ERROR","ERROR");
		}
	}
}
