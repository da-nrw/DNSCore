package de.uzk.hki.da.event;

import java.util.List;

import org.hibernate.Session;

import de.uzk.hki.da.model.Job;
import de.uzk.hki.da.service.HibernateUtil;
import de.uzk.hki.da.utils.C;

public class AutomaticCheckIPTCErrorEvent extends AbstractSystemEvent {

	String statusUserDecision = "640";
	String question = C.QUESTION_STORE_ALLOWED_IPTC_ERROR;
	
	public AutomaticCheckIPTCErrorEvent(){
		setkILLATEXIT(false);
	}
	
	@Override
	public boolean implementation() {
		List<Job> jobs = fetchJobsForContractorOfType( owner.getId() );
		if (jobs!=null)
		for (Job j: jobs) {
			logger.debug(" Found JOB ID : "  + j.getId() + " STATUS: " + j.getStatus() );
			updateJobAnswer(j);
		}
		
		return true;
	}
	
	private void updateJobAnswer(Job j) {
		Session session = HibernateUtil.openSession();
		session.beginTransaction();
		session.refresh(j);
		logger.info(" set " + C.QUESTION_STORE_ALLOWED_IPTC_ERROR + " " + C.ANSWER_YO);
		j.setQuestion(C.QUESTION_STORE_ALLOWED_IPTC_ERROR);
		j.setStatus(statusUserDecision);
		j.setAnswer(C.ANSWER_YO);
		session.update(j);
		session.getTransaction().commit();
		session.close();
		
	}

	@SuppressWarnings("unchecked")
	private synchronized List<Job> fetchJobsForContractorOfType(int userId) {
		Session session = HibernateUtil.openSession();
		session.beginTransaction();
		logger.debug("Fetch jobs for user : " + userId + " of type " + question );
		List<Job> joblist=null;
		try{				
			joblist = session
					.createQuery("SELECT j FROM Job j LEFT JOIN j.obj as o where "
							+ " j.question = ?1 and o.user.id = ?2 ")
					.setParameter("1", question).setParameter("2", userId).setCacheable(false).list();
			
			if ((joblist == null) || (joblist.isEmpty())){
				logger.trace("no jobs found for USER : " +  userId+ " QUESTION: " + question);
				session.close();
				return null;	
			}
			session.close();
			logger.debug("Fetched jobs of USER "+userId);
		}catch(Exception e){
			session.close();
			logger.error("Caught error in fetchJobsFromQueue for " + userId + " QUESTION " + question);
			throw new RuntimeException(e.getMessage(), e);
		}
		return joblist;
}

	
	

}
