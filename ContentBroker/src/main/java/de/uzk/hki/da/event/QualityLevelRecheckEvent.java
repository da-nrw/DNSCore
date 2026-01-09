package de.uzk.hki.da.event;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import de.uzk.hki.da.cb.RetrievePackagesHelper;
import de.uzk.hki.da.model.Job;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.model.PreservationSystem;
import de.uzk.hki.da.service.HibernateUtil;
import de.uzk.hki.da.utils.C;
import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Optional;
import java.util.Date;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;
public class QualityLevelRecheckEvent extends AbstractSystemEvent {
	private static final String WORKFLOW_STATUS_START__QUALITY_LEVEL_RECHECK_ACTION = "8000";
	@Override
	public boolean implementation() {
		Session session = HibernateUtil.openSession();
		
		Transaction tx = session.beginTransaction();

		de.uzk.hki.da.model.Object obj = (Object) session.get(de.uzk.hki.da.model.Object.class, 355); // data_pk
		
		tx.commit();
		String nodeName = getPressNodeName();
		createQualityLevelRecheckJob(obj, nodeName);
		return true;

	}
	
	private boolean createQualityLevelRecheckJob(Object object, String nodeName) { //, String pressNodeName) {
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

			job.setStatus(WORKFLOW_STATUS_START__QUALITY_LEVEL_RECHECK_ACTION);
			job.setResponsibleNodeName(nodeName);
			job.setCreatedAt(new Date());
			job.setModifiedAt(new Date());

			session.save(job);
		}
		session.getTransaction().commit();
		session.close();
		return true;
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
