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
					Session session = HibernateUtil.openSession();
					session.beginTransaction();
					session.delete(storedEvent);
					session.getTransaction().commit();
					session.close();
					logger.debug("StoredEvent successfully removed");
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
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
