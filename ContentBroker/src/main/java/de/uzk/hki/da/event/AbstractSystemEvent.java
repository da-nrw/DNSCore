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
 * The abstract class for SystemEvents
 */
package de.uzk.hki.da.event;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uzk.hki.da.model.Node;
import de.uzk.hki.da.model.SystemEvent;
import de.uzk.hki.da.model.User;
import de.uzk.hki.da.service.HibernateUtil;

public abstract class AbstractSystemEvent implements Runnable {
	
	protected Logger logger = LoggerFactory.getLogger( this.getClass().getName() );

	protected boolean kILLATEXIT = true;
	
	protected User owner;

	protected Node node;

	private SystemEvent storedEvent;
	
	public abstract boolean implementation();
	
	@Override
	public void run() {
		try {
			if (!implementation()) logger.debug("StoredEvent returned false");
			else {
				if (kILLATEXIT) {
					Session session = null;
					try {
					session = HibernateUtil.openSession();
					session.beginTransaction();
					session.delete(storedEvent);
					session.getTransaction().commit();
					session.close();
					logger.debug("StoredEvent successfully removed");
					} catch (Exception e) {
						logger.error("Error while deleting " + storedEvent.getType());
						if (session!=null) session.close();	
					}
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
	}
	public boolean iskILLATEXIT() {
		return kILLATEXIT;
	}

	public void setkILLATEXIT(boolean kILLATEXIT) {
		this.kILLATEXIT = kILLATEXIT;
	}

	public Node getNode() {
		return node;
	}

	public void setNode(Node node) {
		this.node = node;
	}

	public User getOwner() {
		return owner;
	}

	public void setOwner(User owner) {
		this.owner = owner;
	}

	public SystemEvent getStoredEvent() {
		return storedEvent;
	}

	public void setStoredEvent(SystemEvent storedEvent) {
		this.storedEvent = storedEvent;
	}
}
