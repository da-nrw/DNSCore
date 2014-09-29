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
import de.uzk.hki.da.model.CentralDatabaseDAO;
import de.uzk.hki.da.model.ConversionInstruction;
import de.uzk.hki.da.model.DAFile;
import de.uzk.hki.da.model.DAOException;
import de.uzk.hki.da.model.Event;
import de.uzk.hki.da.model.Job;
import de.uzk.hki.da.model.Node;
import de.uzk.hki.da.model.Package;
import de.uzk.hki.da.model.PreservationSystem;

/**
 * Fetches Jobs from the db.
 * Asks Smart Action factory which of them can be executed.
 * Sets these to a running state and executes them.
 * 
 * @author Daniel M. de Oliveira
 */
public class JobManager {

	private static Logger logger = LoggerFactory
			.getLogger(JobManager.class);
	
	public void fetchJobsExecuteActions(Node node) {
		
		Session session = HibernateUtil.openSession();
		session.beginTransaction();
		List<Job> joblist=null;
		joblist = session
				.createQuery("SELECT j FROM Job j LEFT JOIN j.obj as o where j.status like '%0' and "
						+ "j.responsibleNodeName=?2 and o.object_state IN (100, 50, 40) order by j.date_modified asc ")
						.setParameter("2", node.getName()).setCacheable(false).list();
		

		for (Job j:joblist) {
			// To circumvent lazy initialization issues
			logger.debug(":"+j);
			for (ConversionInstruction ci:j.getConversion_instructions()){}
			for (Job j1:j.getChildren()){}
			for (Package p:j.getObject().getPackages()){
				for (DAFile f:p.getFiles()){}
				for (Event e:p.getEvents()){}
			}
		}
		
		session.close();
	}
}
