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

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import de.uzk.hki.da.cb.AbstractAction;
import de.uzk.hki.da.cb.NullAction;
import de.uzk.hki.da.model.CentralDatabaseDAO;
import de.uzk.hki.da.model.Job;

/**
 * @author Daniel M. de Oliveira
 */
public class NewActionFactoryTests {

	@Test
	public void testSimulateThatSecondActionIsNotAvailabeBecauseThreadLimitReached(){
	
		CentralDatabaseDAO dao = mock(CentralDatabaseDAO.class);
		NewActionRegistry ar = mock(NewActionRegistry.class);
		NewActionFactory af = new NewActionFactory(dao,ar);
		
		List<Job> jobList= new ArrayList<Job>();
		Job j1 = new Job();
		j1.setStatus("110");
		Job j2 = new Job();
		j2.setStatus("120");
		jobList.add(j1);
		jobList.add(j2);
		when (dao.getPendingJobsOfLocalNode()).thenReturn(jobList);

		AbstractAction actionType1 = new NullAction(); 
		actionType1.setStartStatus("110");
		actionType1.setName("FirstAction");
		AbstractAction actionType2 = new NullAction();
		actionType2.setStartStatus("120");
		actionType2.setName("SecondAction");

		
		Map<AbstractAction,Integer> remainingThreads = new HashMap<AbstractAction,Integer>();
		remainingThreads.put(actionType1, new Integer(1));
		when (ar.calculateActionTypeRemainingThreads()).thenReturn(remainingThreads);
		
		List<AbstractAction> results = af.createActions();
		assertEquals("FirstAction",results.get(0).getName());
	}
	
	
	@Test
	public void testNewInstanceIsCreated() {
		// make sure new instances instead old instances or example (types) are used
	}
}
