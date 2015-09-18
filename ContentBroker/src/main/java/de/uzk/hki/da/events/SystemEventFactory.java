package de.uzk.hki.da.events;

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
	
	private String eventsPackage = "de.uzk.hki.da.events.";
	
	public void buildStoredEvents() {
			List<SystemEvent> ses =  getEventsPerNode();
			if (ses!=null) {
			for (SystemEvent se : ses) {
				if (se.getType()==null) continue;
				AbstractSystemEvent ase = null;
				try {
					ase = (AbstractSystemEvent)Class.forName(eventsPackage + se.getType()).newInstance();
				} catch (Exception e) {
					logger.error("could not instantiate " + se.getType());
				}
				injectProperties(ase, se);
				ase.run();
			}
			} else logger.debug("no events to perform");
	}
	
	private void injectProperties(AbstractSystemEvent ase, SystemEvent se) {
		ase.setNode(localNode);
		ase.setOwner(se.getOwner());
		ase.setStoredEvent(se);
	}

	@SuppressWarnings("unchecked")
	private List<SystemEvent>getEventsPerNode() {
		Session session = HibernateUtil.openSession();
		session.beginTransaction();
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
			logger.error("Caught error in getEventsPerNode");
			
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
