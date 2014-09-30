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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Keeps track of all currently running actions (stores references). 
 * @deprecated To be replaced by NewActionRegistry
 * @author Sebastian Cuy
 * @author Daniel M. de Oliveira
 */
public class ActionRegistry {
	
	/** The Constant logger. */
	static final Logger logger = LoggerFactory.getLogger(ActionRegistry.class);
	
	/** The max threads. */
	private Map<String,Integer> maxThreads = new HashMap<String,Integer>();
	
	/** The active threads. */
	private Map<String,Integer> activeThreads = new HashMap<String,Integer>();
	
	/** The action priority. */
	private List<String> actionPriority = new ArrayList<String>();
	
	/** The running actions. */
	private List<AbstractAction> runningActions = new ArrayList<AbstractAction>();
	
	private Map<String,String> blockedBy = new HashMap<String,String>();
	
	
	
	
	
	
	/**
	 * Deregister action.
	 *
	 * @param action the action
	 */
	public synchronized void deregisterAction(AbstractAction action) {
		
		String name = action.getName();
		if (!runningActions.remove(action))
			throw new IllegalStateException("Couldn't remove action cause it was not registered.");
		if (maxThreads.get(name) == null)
			throw new IllegalStateException("No thread limit set for action with name " + name);
		if (activeThreads.get(name) == null || activeThreads.get(name) < 1)
			throw new IllegalStateException("Unable to deregister action: no active threads for this action");
		
		logger.debug("Active Threads for "+name+": "+activeThreads.get(name)+" (ThreadLimit is "+maxThreads.get(name)+") Will decrease number of active threads by one.");
		activeThreads.put(name, activeThreads.get(name) - 1);
	}
	
	/**
	 * Gets the current action descriptions.
	 *
	 * @return the current action descriptions
	 */
	public List<ActionDescription> getCurrentActionDescriptions(){
		
		List<ActionDescription> result = new ArrayList<ActionDescription>();
		for (AbstractAction a:runningActions){
			
			int packageId=-1;
			if (a.getJob().getObject().getLatestPackage()!=null)  {
				packageId=a.getJob().getObject().getLatestPackage().getId();
			}
			ActionDescription ad = new ActionDescription(a.getName(), a.getJob().getId(), packageId);
			ad.setDescription(a.getDescription());
			result.add(ad);
			
		}
		return result;
	}
	
	
	/**
	 * Registers an action at the registry. By registering
	 * one signals that the action is in a running state.
	 * 
	 * Note that you must check prior to calling this method if more threads
	 * of given type are allowed.
	 * 
	 * @param action the action
	 * @author daniel
	 */
	public synchronized void registerAction(AbstractAction action) {
		
		String name = action.getName();
		
		if (maxThreads.get(name) == null){
			logger.error("No thread limit set for action with name "+ name);
			throw new IllegalStateException("No thread limit set for action with name " + name);
		}
		if(activeThreads.get(name) == null)
			activeThreads.put(name, 0);
		
		runningActions.add(action);

		logger.debug("Active Threads for "+name+": "+activeThreads.get(name)+" (ThreadLimit is "+maxThreads.get(name)+") Will increase number of active threads by one.");
		activeThreads.put(name, activeThreads.get(name) + 1);
	}

	
	private boolean actionTypeHasNotReachedMaxThreads(String actionName){
		if (activeThreads.get(actionName) == null || 
				activeThreads.get(actionName) < getMaxThreads().get(actionName)) return true;
		return false;
	}
	
	
	/**
	 * Gets the available job types.
	 *
	 * @return sorted list of names of job types. Sorted by priority. First element has highest priority.
	 */
	public List<String> getAvailableJobTypes() {
		List<String> result = new ArrayList<String>();
		for (String actionName : actionPriority) {
			
			if (getBlockedBy().containsKey(actionName)){
				if (!(actionTypeHasNotReachedMaxThreads(getBlockedBy().get(actionName))))
					continue;
			}

			if (actionTypeHasNotReachedMaxThreads(actionName))
				result.add(actionName);
		}
		return result;
	}
	

	/**
	 * Returns a List of job names whose maxThread value is currently reached.
	 * Can be used for exclusion in DAO-calls.
	 *
	 * @return List of job type names
	 * @author Sebastian Cuy
	 */
	public List<String> getJobTypesWithMaxThreads() {
		List<String> result = new ArrayList<String>();
		for (String key : actionPriority) {
			if (activeThreads.get(key) != null)
				if (activeThreads.get(key) >= getMaxThreads().get(key))
					result.add(key);
		}
		return result;
	}

	/**
	 * Gets the max threads.
	 *
	 * @return the max threads
	 */
	public Map<String,Integer> getMaxThreads() {
		return maxThreads;
	}

	/**
	 * Sets the max threads.
	 *
	 * @param maxThreads the max threads
	 */
	public void setMaxThreads(Map<String,Integer> maxThreads) {
		this.maxThreads = maxThreads;
	}

	/**
	 * Gets the action priority.
	 *
	 * @return the action priority
	 */
	public List<String> getActionPriority() {
		return actionPriority;
	}

	/**
	 * Sets the action priority.
	 *
	 * @param actionPriority the new action priority
	 */
	public void setActionPriority(List<String> actionPriority) {
		this.actionPriority = actionPriority;
	}





	public Map<String,String> getBlockedBy() {
		return blockedBy;
	}





	public void setBlockedBy(Map<String,String> blockedBy) {
		this.blockedBy = blockedBy;
	}
}
