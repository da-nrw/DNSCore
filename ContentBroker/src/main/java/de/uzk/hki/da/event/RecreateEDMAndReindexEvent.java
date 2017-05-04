package de.uzk.hki.da.event;

import java.util.Date;
import java.util.List;

import org.hibernate.Session;

import de.uzk.hki.da.model.Job;
import de.uzk.hki.da.model.Node;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.model.PreservationSystem;
import de.uzk.hki.da.service.HibernateUtil;
import de.uzk.hki.da.utils.C;

public class RecreateEDMAndReindexEvent extends AbstractSystemEvent {
		
	
	public RecreateEDMAndReindexEvent(){
		setkILLATEXIT(true); 
		//Set FALSE for advanced job queue strategies, e.g. limited amount per event execution. It might be useful to recall the implementation method until all objects are reindexed 
	}
	
	@Override
	public boolean implementation() {
		List<Object> obj = fetchPublishedObjectsForEventContractor();
		if (obj==null)
			return true;
		String pnodeName=getPressNodeName();
		for (Object o: obj) {
			logger.debug(" Found Object ID : "  + o.getIdentifier());
			createRecreateEDMJob(o,pnodeName);
		}
		
		return true;
	}
	
	private void createRecreateEDMJob(Object object, String pressNodeName) {
		Session session = HibernateUtil.openSession();
		session.beginTransaction();
		session.refresh(object);
		if (object.getObject_state() != Object.ObjectStatus.ArchivedAndValidAndNotInWorkflow) {
			logger.info(" Object " + object.getIdentifier()	+ " is again in workflow, maybe caused by delta, reindex action can not be inialized! ");
		} else {
			object.setObject_state(Object.ObjectStatus.InWorkflow);
			session.saveOrUpdate(object);

			Job job = new Job();
			job.setObject(object);

			job.setStatus(C.WORKFLOW_STATUS_START___SEND_TO_PRESENTER_ACTION);
			job.setResponsibleNodeName(pressNodeName);
			job.setCreatedAt(new Date());
			job.setModifiedAt(new Date());

			session.save(job);
		}
		session.getTransaction().commit();
		session.close();

	}

	@SuppressWarnings("unchecked")
	private synchronized List<Object> fetchPublishedObjectsForEventContractor() {
		Session session = HibernateUtil.openSession();
		session.beginTransaction();
		logger.debug("Fetch Objects for user : " + owner.getId());
		List<Object> objlist=null;
		try{				
			objlist = session
					.createQuery("SELECT o FROM Object o  where o.user.id = ?1 and o.object_state =?2 and (o.published_flag = ?3 or o.published_flag = ?4) ORDER BY o.modifiedAt")
					.setParameter("1", owner.getId()).setParameter("2", Object.ObjectStatus.ArchivedAndValidAndNotInWorkflow)
					.setParameter("3", C.PUBLISHEDFLAG_PUBLIC).setParameter("4", C.PUBLISHEDFLAG_INSTITUTION).setCacheable(false).list();
			
			if ((objlist == null) || (objlist.isEmpty())){
				logger.trace("no objects found for USER : " +  owner.getId());
				session.close();
				return null;	
			}
			session.close();
		}catch(Exception e){
			session.close();
			logger.error("Caught error in fetchPublishedObjectsForContractorOfType for " + owner.getId() );
			throw new RuntimeException(e.getMessage(), e);
		}
		return objlist;
	}
	
	@SuppressWarnings("unchecked")
	private synchronized String getPressNodeName() {
		Session session = HibernateUtil.openSession();
		session.beginTransaction();
		logger.debug("Get PressNodeName from PreservationSystem ");
		List<PreservationSystem> presslist=null;
		try{				
			presslist = session
					.createQuery("SELECT p FROM PreservationSystem p").setReadOnly(true).list();
			
			if ((presslist == null) || (presslist.isEmpty()) || presslist.size()!=1){
				logger.trace("PreservationSystem is not distinct");
				session.close();
				return null;	
			}
			session.close();
		}catch(Exception e){
			session.close();
			logger.error("Caught error in getPressNodeName for "+e.getMessage() );
			throw new RuntimeException(e.getMessage(), e);
		}
		return presslist.get(0).getPresServer();
	}
}
