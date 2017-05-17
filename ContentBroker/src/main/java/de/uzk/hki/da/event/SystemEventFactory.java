/*
  DA-NRW Software Suite | ContentBroker
  Copyright (C) 2015 LVRInfoKom
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

/**
 * @author jens Peters
 * Factory for building events 
 */

package de.uzk.hki.da.event;

import java.util.List;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uzk.hki.da.model.Node;
import de.uzk.hki.da.model.SystemEvent;
import de.uzk.hki.da.service.HibernateUtil;

public class SystemEventFactory  {

	protected Logger logger = LoggerFactory.getLogger( this.getClass().getName() );
	
	private Node localNode;
	
	private String eventsPackage = "de.uzk.hki.da.event.";
	
	public void buildStoredEvents() {
		List<SystemEvent> ses = getEventsPerNode();
		if (ses != null) {
			for (SystemEvent se : ses) {
				if (se.getType() == null)
					continue;
				AbstractSystemEvent ase = null;
				try {
					ase = (AbstractSystemEvent) Class.forName(eventsPackage + se.getType()).newInstance();
					injectProperties(ase, se);
					ase.run();
				} catch (Exception e) {
					logger.error("could not instantiate " + se.getType());
				}
			}
		} else
			logger.debug("no events to perform");
	}
	
	private void injectProperties(AbstractSystemEvent ase, SystemEvent se) {
		ase.setNode(localNode);
		ase.setOwner(se.getOwner());
		ase.setStoredEvent(se);
	}

	@SuppressWarnings("unchecked")
	private List<SystemEvent>getEventsPerNode() {
		Session session = HibernateUtil.openSession();
		List<SystemEvent> events=null;
		try{				
			events = session
					.createQuery("from SystemEvent e where e.node.id = ?1")
					.setParameter("1", localNode.getId()).setCacheable(false).list();

			if ((events == null) || (events.isEmpty())){
				logger.trace("no systemevents found for " + localNode.getName());
				session.close();
				return null;
			}
				
			session.close();
			
		}catch(Exception e){
			session.close();
			logger.error("Caught error in getEventsPerNode id: " + localNode.getId() + " " + e.getMessage(),e);
			
			throw new RuntimeException(e.getMessage(), e);
		}
		return events;
}

	public Node getLocalNode() {
		return localNode;
	}

	public void setLocalNode(Node localNode) {
		this.localNode = localNode;
	}	
	
}
