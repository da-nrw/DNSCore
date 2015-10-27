package de.uzk.hki.da.core;

import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.MDC;

import de.uzk.hki.da.model.Job;
import de.uzk.hki.da.model.Node;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.service.HibernateUtil;
import de.uzk.hki.da.utils.C;

public class TimeBasedPublicationWorker extends Worker {

	private Node localNode; // Node the ContentBroker actually runs on
	private long pauseForDays; 
	private boolean pollForCi; 
	private long lastTimeDone = 0;
	
	public void init() {
		Session session = HibernateUtil.openSession();
		session.refresh(localNode);
		session.close();
	}

	@Override
	public void scheduleTaskImplementation() {
		try {
			this.scheduleInsertJobs();
		} catch (Exception exc) {
			logger.error("Error executing:", exc);
		}
	}

	protected void scheduleInsertJobs() {

		long oneDay = 86400000;
		long currentTime= (new Date()).getTime();
		if (pollForCi || lastTimeDone + oneDay < currentTime) 
		{
			Session session = HibernateUtil.openSession();
			insertJobs(session);
			session.close();

			lastTimeDone = currentTime;
		}
	}
	
	protected void insertJobs(Session session) {

		String queryStr = "SELECT o FROM Object o "
			+ "where "
			+ "o.published_flag>=0 and "
			+ "(( o.static_nondisclosure_limit < ?1 and "
			+ "	( o.published_flag = ?2 or o.published_flag = ?3)"
			+ "		and o.dynamic_nondisclosure_limit is null) or "
			+ " ( o.static_nondisclosure_limit_institution < ?1 and "
			+ "	( o.published_flag = ?4 or o.published_flag = ?5)"
			+ "		and o.dynamic_nondisclosure_limit_institution is null)) and "
			+ "o.object_state = ?6 and"
			+ "(o.lastPublicationTry is null or o.lastPublicationTry < ?7) and"
			+ "(o.initial_node = ?8)";

		Date today = new Date();
		Date runSinceLastTry = new Date(today.getTime() - pauseForDays * 86400000L);
		Query query = session.createQuery(queryStr);

		query.setParameter("1", new Date());

		query.setParameter("2", C.PUBLISHEDFLAG_NO_PUBLICATION);
		query.setParameter("3", C.PUBLISHEDFLAG_INSTITUTION);
		
		query.setParameter("4", C.PUBLISHEDFLAG_NO_PUBLICATION);
		query.setParameter("5", C.PUBLISHEDFLAG_PUBLIC);
		
		query.setParameter("6",	Object.ObjectStatus.ArchivedAndValidAndNotInWorkflow);
		query.setParameter("7",	runSinceLastTry);

		query.setParameter("8", this.getLocalNode().getName());
		@SuppressWarnings("unchecked")
		List<Object> obbis = query.list();

		if (obbis.isEmpty()) {
			logger.debug("No Time based publishing pending");
			return;
		}

		for (int iii = 0; iii < obbis.size(); iii++) {
			Transaction transi = session.beginTransaction();

			Object obbi = obbis.get(iii);

			obbi.setObject_state(Object.ObjectStatus.InWorkflow);
			obbi.setLastPublicationTry(new Date());
			obbi.setDate_modified(String.valueOf(new Date().getTime()/1000L));
			Job job = new Job();
			job.setObject(obbi);

			logger.debug("Publish " + obbi.getIdentifier() + ": " + obbi.getOrig_name());

			job.setStatus(C.WORKFLOW_STATUS_START___TIME_BASED_PUBLICATION_OBJECT_TO_WORK_AREA_ACTION);
			job.setResponsibleNodeName(this.getLocalNode().getName());

			job.setDate_created(String.valueOf(new Date().getTime() / 1000L));

			session.save(obbi);
			session.save(job);
			transi.commit();
		}
	}

	public Node getLocalNode() {
		return localNode;
	}

	public void setLocalNode(Node localNode) {
		this.localNode = localNode;
	}

	public long getPauseForDays() {
		return pauseForDays;
	}

	public void setPauseForDays(long pauseForDays) {
		this.pauseForDays = pauseForDays;
	}

	public boolean isPollForCi() {
		return pollForCi;
	}

	public void setPollForCi(boolean pollForCi) {
		this.pollForCi = pollForCi;
	}

	@Override
	public void setMDC() {
		MDC.put(WORKER_ID, "timebasedpublication");
	}
}
