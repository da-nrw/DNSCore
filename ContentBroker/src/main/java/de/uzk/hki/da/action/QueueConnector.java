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

import java.util.Date;
import java.util.List;

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
import de.uzk.hki.da.model.PreservationSystem;

/**
 * @author Daniel M. de Oliveira
 */
public class QueueConnector {

	private static final Logger logger = LoggerFactory.getLogger(QueueConnector.class);
	
	/**
	 * XXX locking synchronized, against itself and against get object need audit
	 * 
	 * IMPORTANT NOTE: Fetch objects from queue opens a new session.
	 *
	 * @param status the status
	 * @param workingStatus typically a numerical value with three digits, stored as string.
	 * working status third digit typically is 2 by convention.
	 * @param node the node
	 * @return the job
	 * @author Daniel M. de Oliveira
	 */
	@SuppressWarnings({ "unchecked", "unused" })
	public Job fetchJobFromQueue(String status, String workingStatus, Node node, PreservationSystem pSystem) {
		Session session = HibernateUtil.openSession();
		session.beginTransaction();
		logger.trace("Fetch job for node name " + node.getName());
		List<Job> joblist=null;
		try{
			session.refresh(node);
			session.refresh(pSystem);
			
			joblist = session
					.createQuery("SELECT j FROM Job j LEFT JOIN j.obj as o where j.status=?1 and "
							+ "j.responsibleNodeName=?2 and o.object_state IN (100, 50, 40) and o.orig_name!=?3 order by j.date_modified asc ")
					.setParameter("1", status).setParameter("2", node.getName()).setParameter("3","integrationTest").setCacheable(false).setMaxResults(1).list();

			if ((joblist == null) || (joblist.isEmpty())){
				logger.trace("no job found for status {}.",status);
				session.close();
				return null;
			}
			
			Job job = joblist.get(0);
			
			// To circumvent lazy initialization issues
			for (ConversionInstruction ci:job.getConversion_instructions()){}
			for (Job j:job.getChildren()){}
			for (Package p:job.getObject().getPackages()){
				for (DAFile f:p.getFiles()){}
				for (Event e:p.getEvents()){}
			}
			// -
			logger.debug("fetched job with id {} and status {}", job.getId(), job.getStatus());

			job.setStatus(workingStatus);
			job.setDate_modified(String.valueOf(new Date().getTime()/1000L));
			session.merge(job);
			session.getTransaction().commit();
			session.close();
			
			logger.debug("fetched job with id {} and set status to {}", job.getId(), job.getStatus());
		
		}catch(Exception e){
			session.close();
			logger.error("Caught error in fetchJobFromQueue");
			
			throw new RuntimeException(e.getMessage(), e);
		}
		return joblist.get(0);
	}
}
