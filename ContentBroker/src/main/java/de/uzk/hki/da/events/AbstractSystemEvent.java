package de.uzk.hki.da.events;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uzk.hki.da.model.Node;
import de.uzk.hki.da.model.SystemEvent;
import de.uzk.hki.da.model.User;

public abstract class AbstractSystemEvent implements Runnable {
	
	protected Logger logger = LoggerFactory.getLogger( this.getClass().getName() );

	
	protected User owner;

	protected Node node;

	private SystemEvent storedEvent;
	
	public abstract boolean implementation();
	
	@Override
	public void run() {
		try {
			if (!implementation()) logger.debug("Action returned false");
			
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
