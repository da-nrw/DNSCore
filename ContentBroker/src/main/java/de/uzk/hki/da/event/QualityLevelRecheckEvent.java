package de.uzk.hki.da.event;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import de.uzk.hki.da.cb.RetrievePackagesHelper;
import de.uzk.hki.da.core.RegisterObjectService;
import de.uzk.hki.da.model.Job;
import de.uzk.hki.da.model.Node;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.model.PreservationSystem;
import de.uzk.hki.da.model.User;
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

	/** The register object service. */

	@Override
	public boolean implementation() {
	    String nodeName = getPressNodeName();
	    int objectId = 4861; // TODO: parametrisieren
	    
	    
	    
	   
	        //obj = registerObjectService.registerObject(origName, contractor);
	        
	        	
	        // optional: wenn du harte Zustände willst
	        

	    return insertJobIntoQueueAndSetWorkFlowState(nodeName,objectId);

	        
	        
	}

	private boolean insertJobIntoQueueAndSetWorkFlowState(String nodeName, int objectId) {
		Session session = HibernateUtil.openSession();
		Transaction tx = session.beginTransaction();
		try {
			Object obj = (Object) session.get(Object.class, objectId);
			if (obj == null)
				return false;
			if (obj.getObject_state() != Object.ObjectStatus.ArchivedAndValidAndNotInWorkflow) {
				logger.info("Object {} not eligible for recheck, state={}", obj.getIdentifier(), obj.getObject_state());
				tx.commit();
				return false;
			}
			session.saveOrUpdate(obj);
			obj.setObject_state(Object.ObjectStatus.InWorkflow);

			Job job = new Job();
			job.setObject(obj);
			job.setStatus(WORKFLOW_STATUS_START__QUALITY_LEVEL_RECHECK_ACTION);
			job.setResponsibleNodeName(nodeName);
			job.setCreatedAt(new Date());
			job.setModifiedAt(new Date());
			session.save(job);
			session.getTransaction().commit();

			return true;
		} catch (Exception e) {
			tx.rollback();
			throw e;
		} finally {
			session.close();
		}
		
	}

	@SuppressWarnings("unchecked")
	private synchronized String getPressNodeName() {
		Session session = HibernateUtil.openSession();
		session.beginTransaction();
		logger.debug("Get PressNodeName from PreservationSystem ");
		List<PreservationSystem> presslist = null;
		try {
			presslist = session.createQuery("SELECT p FROM PreservationSystem p").setReadOnly(true).list();

			if ((presslist == null) || (presslist.isEmpty()) || presslist.size() != 1) {
				logger.trace("PreservationSystem is not distinct");
				session.close();
				return null;
			}
			session.close();
		} catch (Exception e) {
			session.close();
			logger.error("Caught error in getPressNodeName for " + e.getMessage());
			throw new RuntimeException(e.getMessage(), e);
		}
		return presslist.get(0).getPresServer();
	}



}
