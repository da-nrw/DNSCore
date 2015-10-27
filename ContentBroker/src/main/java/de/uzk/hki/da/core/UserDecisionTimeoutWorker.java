package de.uzk.hki.da.core;

import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.MDC;

import de.uzk.hki.da.model.Job;
import de.uzk.hki.da.model.Node;
import de.uzk.hki.da.service.HibernateUtil;
import de.uzk.hki.da.utils.C;

/**
 * @author Josef Hammer
 */
public class UserDecisionTimeoutWorker extends Worker {

	private Node localNode; // Node the ContentBroker actually runs on

	public void init() {
		Session session = HibernateUtil.openSession();
		session.refresh(localNode);
		session.close();
	}

	@Override
	public void scheduleTaskImplementation() {
		try {
			Session session = HibernateUtil.openSession();
			this.updateTimedOut(session);
			session.close();
		} catch (Exception exc) {
			logger.error("Error executing:", exc);
		}
	}

	protected void updateTimedOut(Session session) {
		logger.debug("updateTimedOut");


		String dateBefore = String.valueOf(new Date().getTime() / 1000L - 86400 * 30);

		String queryStr = "SELECT j FROM Job j LEFT JOIN j.obj o "
				+ "where j.status=?1 and j.date_modified < ?2 and j.responsibleNodeName=?3";

		String waitUserDecStatus = C.WORKFLOW_STATUS_WAIT___PROCESS_FOR_USER_DECISION_ACTION;
		Query query = session.createQuery(queryStr);
		query.setParameter("1", waitUserDecStatus);
		query.setParameter("2", dateBefore);
		query.setParameter("3", this.localNode.getName());

		@SuppressWarnings("unchecked")
		List<Job> jobList = query.list();

		if (jobList.isEmpty()){
			return;
		}

		String newStatus = switchStatus(waitUserDecStatus, C.WORKFLOW_STATUS_DIGIT_WAITING);
		Transaction transi = session.beginTransaction();

		for (int iii = 0; iii < jobList.size(); iii++) {
			Job jobbi = jobList.get(iii);
			
			jobbi.setStatus(newStatus);
			jobbi.setAnswer(C.ANSWER_TO);
			jobbi.setDate_modified(String.valueOf(new Date().getTime() / 1000L));
			session.save(jobbi);

			logger.debug("found identifier: " + jobbi.getObject().getIdentifier()
					+ " origname: " + jobbi.getObject().getOrig_name() 
					+ " newStatus: " + jobbi.getStatus());
		}
		transi.commit();
	}

	private String switchStatus(String startStatus,String digit) {
		return startStatus.substring(0,startStatus.length()-1) + digit;
	}

	@Override
	public void setMDC() {
		MDC.put(WORKER_ID, "decisiontimeout");
	}

	public Node getLocalNode() {
		return localNode;
	}

	public void setLocalNode(Node localNode) {
		this.localNode = localNode;
	}

}
