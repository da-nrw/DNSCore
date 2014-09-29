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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uzk.hki.da.core.HibernateUtil;
import de.uzk.hki.da.model.ConversionInstruction;
import de.uzk.hki.da.model.DAFile;
import de.uzk.hki.da.model.Event;
import de.uzk.hki.da.model.Job;
import de.uzk.hki.da.model.Node;
import de.uzk.hki.da.model.Package;

/**
 * Fetches Jobs from the db.
 * Asks Smart Action factory which of them can be executed.
 * Sets these to a running state and executes them.
 * 
 * @author Daniel M. de Oliveira
 */
public class JobManager {

	private static final String QUERY = "SELECT j FROM Job j LEFT JOIN j.obj as o where j.status like '%0' " +
			"and j.responsibleNodeName=?2 and o.object_state IN (100, 50, 40) order by j.date_modified asc ";
	private static Logger logger = LoggerFactory
			.getLogger(JobManager.class);
	private SmartActionFactory saf;
	
	
	
	
	public void fetchJobsExecuteActions(Node node) {
		
		Set<Job> jobSet = new HashSet<Job>(getJobsFromDB(node));
		
		// get action for jobs with smartactionfactory.createinstances.
		List<AbstractAction> actionInstancesToExecute = saf.createActions(jobSet);
		
		for (AbstractAction toExec:actionInstancesToExecute) {
			Job j = toExec.getJob();
			setJobsRunningStateAndSaveItToDB(j);
			// execute action
			
			// if task executor rejects, ...
		}
		
	}


	/**
	 * set to xx2
	 * @param j
	 */
	private void setJobsRunningStateAndSaveItToDB(Job j) {
		j.setStatus(j.getStatus().substring(0, j.getStatus().length() - 1) + "2");
		Session session = HibernateUtil.openSession();
		session.beginTransaction();
		session.update(j);
		session.getTransaction().commit();
		session.close();
	}
	
	
	@SuppressWarnings("unchecked")
	private List<Job> getJobsFromDB(Node node) {
		
		Session session = HibernateUtil.openSession();
		session.beginTransaction();
		List<Job> joblist=null;
		joblist = session
				.createQuery(QUERY)
						.setParameter("2", node.getName()).setCacheable(false).list();
		

		for (Job j:joblist) {
			// To circumvent lazy initialization issues
			for (@SuppressWarnings("unused") ConversionInstruction ci:j.getConversion_instructions()){}
			for (@SuppressWarnings("unused") Job j1:j.getChildren()){}
			for (Package p:j.getObject().getPackages()){
				for (@SuppressWarnings("unused") DAFile f:p.getFiles()){}
				for (@SuppressWarnings("unused") Event e:p.getEvents()){}
			}
		}
		session.close();
		return joblist;
	}


	public void setSmartActionFactory(SmartActionFactory saf) {
		this.saf=saf;
	}
}
