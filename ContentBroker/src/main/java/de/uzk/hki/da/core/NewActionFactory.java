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
import java.util.List;

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

	public AbstractAction createAction() {
		
		List<Job> jobsInStartState = dao.getJobForLocalNodeAndInStartState();
		for (Job j:jobsInStartState)
			logger.debug(j.toString());
		List<AbstractAction> jobsWhichCanBeExecuted = calculateActionTypesWhichHaveNotReachedThreadLimit(jobsInStartState);

		// filter the types by a set of rules
		
		// create !new! instances for the result list
		
		return jobsWhichCanBeExecuted.get(0);
	}

	private List<AbstractAction> calculateActionTypesWhichHaveNotReachedThreadLimit(
			List<Job> jobsInStartState) {
		
		List<AbstractAction> resultList = new ArrayList<AbstractAction>();
		
		for (AbstractAction actionType:ar.calculateActionTypeRemainingThreads().keySet()) { 
			
			Integer remainingThreadsForType = ar.calculateActionTypeRemainingThreads().get(actionType);
			
			// choose from the set of jobs for that type as many as the thread limit allows
			for (Job j:jobsInStartState) {
				if (!j.getStatus().equals(actionType.getStartStatus())) continue;
				
				System.out.println("actionType which has not reached limit:"+actionType.getName()+","+actionType.getStartStatus());
				
				resultList.add(actionType);
				remainingThreadsForType--;
				if (remainingThreadsForType==0) break;
			}
		}
			
		return resultList;
	}
	
	
}
