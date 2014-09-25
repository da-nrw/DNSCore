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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.irods.jargon.core.exception.InvalidArgumentException;

import de.uzk.hki.da.cb.AbstractAction;

/**
 * Purpose: Maintain an overview of which actions are running and how many
 * of each type are allowed to run.
 * 
 * Rewrite of the classic ActionRegistry
 * @author Daniel M. de Oliveira
 */
public class NewActionRegistry {

	private List<AbstractAction> runningActions = new ArrayList<AbstractAction>();
	private Map<String, Integer> threadsAllowed = new HashMap<String, Integer>();
	
	
	
	/**
	 * Register an action as running. 
	 * @param action
	 * @throws InvalidArgumentException 
	 */
	public void register(AbstractAction action) {
		if (action.getName()==null) 
			throw new IllegalArgumentException("actions name must be set");
		if (!threadsAllowed.keySet().contains(action.getName())) 
			throw new IllegalStateException("maxThread not set for action type");
		if (runningActions.contains(action)) throw new IllegalArgumentException("action must not get registered twice");
		
		runningActions.add(action);
	}

	
	
	public List<AbstractAction> getRunningActions() {
		return runningActions;
	}

	
	
	public void deregister(AbstractAction actionToRemove) {
		if (!runningActions.contains(actionToRemove))
			throw new IllegalStateException("action cannot be deristered since it never was registered.");
		
		runningActions.remove(actionToRemove);
	}

	
	
	/**
	 * @param maxThreads |action name, number of allowed instances for action with name|
	 */
	public void setMaxThreads(Map<String, Integer> maxThreads) {
		this.threadsAllowed = maxThreads;
	}

	
	
	public Map<String, Integer> calculateRemainingThreadsForNamedActions() {
		
		Map<String, Integer> threadsTotal = new HashMap<String, Integer>();
		for (AbstractAction a:runningActions) {
			if (threadsTotal.get(a.getName())==null)
				threadsTotal.put(a.getName(), 1);
			else
				threadsTotal.put(a.getName(), threadsTotal.get(a.getName())+1);
		}
		
		Map<String,Integer> threadsRemaining = new HashMap<String, Integer>();
		for (String n:threadsTotal.keySet()) {
			int remaining = threadsAllowed.get(n)-threadsTotal.get(n);
			if (remaining>0) threadsRemaining.put(n,remaining);
		}
		
		return threadsRemaining;
	}
}
