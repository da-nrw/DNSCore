package de.uzk.hki.da.event;

import static de.uzk.hki.da.utils.C.PUBLISHEDFLAG_INSTITUTION;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.Session;

import de.uzk.hki.da.model.Job;
import de.uzk.hki.da.model.Node;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.model.PreservationSystem;
import de.uzk.hki.da.model.Role;
import de.uzk.hki.da.service.HibernateUtil;
import de.uzk.hki.da.utils.C;

public class RecreateEDMAndReindexEvent extends AbstractSystemEvent {
	
	static final int OBJECTAMOUNT_FOR_EXECTUION=60;
	static final int WAIT_TIME=10; //sec
	
	public RecreateEDMAndReindexEvent(){
		setkILLATEXIT(true); 
		//Set FALSE for advanced job queue strategies, e.g. limited amount per event execution. 
		//It might be useful to recall the implementation method until all objects are reindexed 
	}
	
	/**
	 * Method (means whole Event execution) is blocking until all objects are reindexed
	 * 
	 */
	@Override
	public boolean implementation() { 
		List<Object> obj= fetchPublishedObjectsForEventContractor();
		if (obj==null){
			return true;
		}
		String pnodeName=getPressNodeName();
		
		//objInWorkflow is used to monitor objects in workflow and control the reindex process to prevent overflow
		List<Object> objInWorkflow=new ArrayList<Object>();
		
		for(int currentIndex=0;currentIndex<obj.size();  ){
			
			refreshObjectList(objInWorkflow);
			objInWorkflow=getOnlyInWorkflowObjectList(objInWorkflow);
			//if to many old objects are processing now, wait until enough objects are finished
			logger.debug("Objects in Workflow: "+objInWorkflow.size()+(objInWorkflow.size()<OBJECTAMOUNT_FOR_EXECTUION/2?"<":">=")+(OBJECTAMOUNT_FOR_EXECTUION/2));
			if(objInWorkflow.size()<OBJECTAMOUNT_FOR_EXECTUION/2){
				int currentIndexEnd=Math.min(obj.size(),currentIndex+OBJECTAMOUNT_FOR_EXECTUION);
				for (int i=currentIndex;i<currentIndexEnd;i++ ) {
					Object o=obj.get(i);
					logger.debug("Found Object("+i+"/"+obj.size()+") ID : "  + o.getIdentifier());
					if(createRecreateEDMJob(o,pnodeName))
						objInWorkflow.add(o);
				}
				currentIndex=currentIndexEnd;
			}
			waitTime();
		}

		return true;
	}
	
	private void waitTime() {
		try {
			logger.debug("Event wait "+WAIT_TIME);
			Thread.sleep(WAIT_TIME*1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}

	/**
	 * Get sublist of all Object, which are for now in worflow
	 * 
	 * @param obj
	 * @return
	 */
	private synchronized List<Object> getOnlyInWorkflowObjectList(List<Object> obj){
		List<Object> ret=new ArrayList<Object>();
		for (Object o: obj) {
			if(o.getObject_state()==Object.ObjectStatus.InWorkflow)
				ret.add(o);
		}
		return ret;
	}

	/**
	 * Synchronize objects and persistence copies
	 * @param obj
	 */
	private synchronized void refreshObjectList(List<Object> obj){
		Session session = HibernateUtil.openSession();
		session.beginTransaction();
		for (Object o: obj) {
			session.refresh(o);
		}
		session.getTransaction().commit();
		session.close();
	}
	
	/**
	 * Method set Object state to {@link de.uzk.hki.da.model.Object.ObjectStatus InWorkflow} and initiate {@link C.WORKFLOW_STATUS_START___SEND_TO_PRESENTER_ACTION SendToPresenterAction}
	 * 
	 * Returns false if Object is not in ArchivedAndValidAndNotInWorkflow state.
	 * 
	 * @param object
	 * @param pressNodeName
	 * @return
	 */
	private boolean createRecreateEDMJob(Object object, String pressNodeName) {
		Session session = HibernateUtil.openSession();
		session.beginTransaction();
		session.refresh(object);
		if (object.getObject_state() != Object.ObjectStatus.ArchivedAndValidAndNotInWorkflow) {
			logger.info(" Object " + object.getIdentifier()	+ " is again in workflow, maybe caused by delta, reindex action can not be initialized! ");
			session.close();
			return false;
		} else {
			logger.info(" Object " + object.getIdentifier()	+ " put in workflow, and reindex action is initialized! ");
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
		return true;
	}

	@SuppressWarnings("unchecked")
	private synchronized List<Object> fetchPublishedObjectsForEventContractor() {
		
		
		Session session = HibernateUtil.openSession();
		session.beginTransaction();
		
		logger.debug("Fetch Objects for user : " + owner.getId());
		List<Object> objlist=null;
		try{				
			boolean isAdmin=false;
			//Hibernate.initialize(owner);
			session.refresh(owner); //LazyInitializationException
			for(Role r:owner.getRoles()){
				if(r.getAuthority().equals("ROLE_PSADMIN"))
					isAdmin=true;
			}
			logger.debug("user : " + owner.getId()+" is "+(isAdmin?" ":" not ")+"Admin");
			
			if(isAdmin){
				objlist = execQueryGetObjectsForAdmin(session);
			}else{
				objlist = execQueryGetObjectsForOwner(session);
			}
			
			logger.debug("for user(" + owner.getId()+") "+objlist.size()+" Objects are fetched");
			
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
	
	private synchronized List<Object> execQueryGetObjectsForAdmin(Session session){
		return session
				.createQuery("SELECT o FROM Object o where o.object_state =?2 and (o.published_flag = ?3 or o.published_flag = ?4 or o.published_flag = ?5) ORDER BY o.modifiedAt")
				.setParameter("2", Object.ObjectStatus.ArchivedAndValidAndNotInWorkflow)
				.setParameter("3", C.PUBLISHEDFLAG_PUBLIC).setParameter("4", C.PUBLISHEDFLAG_INSTITUTION).setParameter("5", C.PUBLISHEDFLAG_PUBLIC+C.PUBLISHEDFLAG_INSTITUTION)
				.setCacheable(false).list();
	}
	
	private synchronized List<Object> execQueryGetObjectsForOwner(Session session){
		return session
				.createQuery("SELECT o FROM Object o where o.user.id = ?1 and o.object_state =?2 and (o.published_flag = ?3 or o.published_flag = ?4) ORDER BY o.modifiedAt")
				.setParameter("1", owner.getId()).setParameter("2", Object.ObjectStatus.ArchivedAndValidAndNotInWorkflow)
				.setParameter("3", C.PUBLISHEDFLAG_PUBLIC).setParameter("4", C.PUBLISHEDFLAG_INSTITUTION).setParameter("5", C.PUBLISHEDFLAG_PUBLIC+C.PUBLISHEDFLAG_INSTITUTION)
				.setCacheable(false).list();
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
