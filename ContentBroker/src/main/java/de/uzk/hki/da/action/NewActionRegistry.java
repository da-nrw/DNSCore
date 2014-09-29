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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.irods.jargon.core.exception.InvalidArgumentException;

/**
 * Purpose: Maintain an overview of which actions instances are running and how many
 * of each type are allowed to run.
 * 
 * Rewrite of the classic ActionRegistry
 * @author Daniel M. de Oliveira
 */
public class NewActionRegistry {

	private List<AbstractAction> runningActionInstances = new ArrayList<AbstractAction>();
	private Map<AbstractAction, Integer> actionTypeThreadsAllowed = new HashMap<AbstractAction, Integer>();
	
	
	
	/**
	 * Register an instance of an action as running.
	 *  
	 * @param actionInstance
	 * @throws InvalidArgumentException if the client tries to register the same action instance more than once.
	 * @throws IllegalStateException if thread limit is not configured for the type of the action.
	 */
	public void register(AbstractAction actionInstance) {
		if (actionInstance.getName()==null) 
			throw new IllegalArgumentException("actions name must be set");
		
		boolean alreadyRegistered=false;
		for (AbstractAction a:runningActionInstances)
			if (a==actionInstance) alreadyRegistered=true;
		if (alreadyRegistered) throw new IllegalArgumentException("action must not get registered twice");
		
		boolean threadsAllowedForActionTypeIsConfigured=false;
		for (AbstractAction a:actionTypeThreadsAllowed.keySet()) {
			if (a.getClass().getName().equals(actionInstance.getClass().getName())){
				threadsAllowedForActionTypeIsConfigured=true;
				break;
			}
		}
		if (!threadsAllowedForActionTypeIsConfigured)
			throw new IllegalStateException("thread limit not configured for action of type "+actionInstance.getClass().getName());
		
		runningActionInstances.add(actionInstance);
	}

	
	
	public List<AbstractAction> getRunningActionsInstances() {
		return runningActionInstances;
	}

	
	/**
	 * Removes an action instance from the list of running actions.
	 * 
	 * @param actionInstanceToRemove
	 */
	public void deregister(AbstractAction actionInstanceToRemove) {
		if (!runningActionInstances.contains(actionInstanceToRemove))
			throw new IllegalStateException("action cannot be deristered since it never was registered.");
		
		runningActionInstances.remove(actionInstanceToRemove);
	}

	
	
	/**
	 * @param maxThreads |action name, number of allowed instances for action with name|
	 */
	public void setActionTypeThreadsAllowed(Map<AbstractAction, Integer> maxThreads) {
		this.actionTypeThreadsAllowed = maxThreads;
	}

	
	/**
	 * @return Map of action type to number of remaining threads.
	 */
	public Map<AbstractAction, Integer> calculateActionTypeRemainingThreads() {
		
		Map<AbstractAction, Integer> threadsTotal = new HashMap<AbstractAction, Integer>();
		for (AbstractAction a:runningActionInstances) {
			if (threadsTotal.get(a)==null)
				threadsTotal.put(a, 1);
			else
				threadsTotal.put(a, threadsTotal.get(a)+1);
		}
		
		Map<AbstractAction,Integer> threadsRemaining = new HashMap<AbstractAction, Integer>();
		for (AbstractAction n:threadsTotal.keySet()) {

			int remaining = actionTypeThreadsAllowed.get(n)-threadsTotal.get(n);
			if (remaining>0) threadsRemaining.put(n,remaining);
		}
		
		return threadsRemaining;
	}
}
