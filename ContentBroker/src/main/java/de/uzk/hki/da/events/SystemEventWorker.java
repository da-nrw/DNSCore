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

/**
 * The package integrity.
 */
package de.uzk.hki.da.events;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.type.IntegerType;
import org.hibernate.type.LongType;
import org.slf4j.MDC;

import de.uzk.hki.da.core.Worker;
import de.uzk.hki.da.grid.GridFacade;
import de.uzk.hki.da.model.Copy;
import de.uzk.hki.da.model.Node;
import de.uzk.hki.da.model.PreservationSystem;
import de.uzk.hki.da.service.HibernateUtil;


/**
 * starts System events
 * copies
 * @author Jens Peters
 *
 */
public class SystemEventWorker extends Worker{

	/** The local node id. */
	private int localNodeId;
	
	private Node node;
	
	public Node getNode() {
		return node;
	}

	public void setNode(Node node) {
		this.node = node;
	}

	public void init(){
		node = new Node(); 
		node.setId(localNodeId);
		Session session = HibernateUtil.openSession();
		session.beginTransaction();
		session.refresh(node);
		session.getTransaction().commit();
		session.close();
	}
	
	@Override
	public void setMDC() {
		MDC.put(WORKER_ID, "events");
	} 
	/**
	 * @author Jens Peters
	 */
	@Override
	public void scheduleTaskImplementation(){
		SystemEventFactory se = new SystemEventFactory();
		se.setLocalNode(node);
		se.buildStoredEvents();
	}
	
}
