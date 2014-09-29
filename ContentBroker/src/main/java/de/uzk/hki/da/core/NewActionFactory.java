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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uzk.hki.da.cb.AbstractAction;
import de.uzk.hki.da.model.CentralDatabaseDAO;
import de.uzk.hki.da.model.Job;

/**
 * @author Daniel M. de Oliveira
 */
public class NewActionFactory {

	
	private static final Logger logger = LoggerFactory.getLogger(NewActionFactory.class);
	
	
	private CentralDatabaseDAO dao;
	private NewActionRegistry ar;
	
	NewActionFactory(CentralDatabaseDAO dao,NewActionRegistry ar){
		this.dao = dao;
		this.ar  = ar;
	}

	public List<AbstractAction> createActions() {
		
		Map<Job,AbstractAction> jobsWhereThreadLimitNotReached = 
				sortOutJobsWhereThreadLimitReached(dao.getPendingJobsOfLocalNode());

		// filter the types by a set of rules

		for (Job j:jobsWhereThreadLimitNotReached.keySet())
			logger.debug("actionType which has not reached limit:"+
					jobsWhereThreadLimitNotReached.get(j).getName()+","+jobsWhereThreadLimitNotReached.get(j).getStartStatus());
		
		
		// create !new! instances for the result list
		List<AbstractAction> instantiatedActions = new ArrayList<AbstractAction>();
		for (Job j:jobsWhereThreadLimitNotReached.keySet())
			instantiatedActions.add(jobsWhereThreadLimitNotReached.get(j));
		
		return instantiatedActions;
	}

	private Map<Job,AbstractAction> sortOutJobsWhereThreadLimitReached(
			List<Job> jobsInStartState) {
		
		Map<Job,AbstractAction> resultList = new HashMap<Job,AbstractAction>();
		
		for (AbstractAction actionType:ar.calculateActionTypeRemainingThreads().keySet()) { 
			
			Integer remainingThreadsForType = ar.calculateActionTypeRemainingThreads().get(actionType);
			
			// choose from the set of jobs for that type as many as the thread limit allows
			for (Job j:jobsInStartState) {
				if (!j.getStatus().equals(actionType.getStartStatus())) continue;
				
				resultList.put(j,actionType);
				remainingThreadsForType--;
				if (remainingThreadsForType==0) break;
			}
		}
			
		return resultList;
	}
	
	
}
