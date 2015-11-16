package de.uzk.hki.da.core;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.mail.MessagingException;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.MDC;

import de.uzk.hki.da.model.Node;
import de.uzk.hki.da.model.PendingMail;
import de.uzk.hki.da.service.HibernateUtil;
import de.uzk.hki.da.service.Mail;

public class MailWorker extends Worker {

	private Node localNode; // Node the ContentBroker actually runs on
	private long lastTimeDone = 0;
	private String smtpHost;
	
	public void init() {
		Session session = HibernateUtil.openSession();
		session.refresh(localNode);
		session.close();
	}

	@Override
	public void scheduleTaskImplementation() {
		try {
			this.sceduleFetchMails();
		} catch (Exception exc) {
			logger.error("Error executing:", exc);
		}
	}

	private void sceduleFetchMails() throws MessagingException {
		long oneDay = 86400000;
		long currentTime = (new Date()).getTime();
		boolean alsoPooled = false;

		if (lastTimeDone + oneDay < currentTime) {
			alsoPooled = true;
			lastTimeDone = currentTime;
		}
		Session session = HibernateUtil.openSession();
		fetchMails(session, alsoPooled);
		session.close();
	}

	private void fetchMails(Session session, boolean alsoPooled) throws MessagingException {
		Query query;
		if (alsoPooled) {
			query = session.createQuery("SELECT o FROM PendingMail o where nodeName = ?1");
		} else {
			query = session.createQuery("SELECT o FROM PendingMail o where nodeName = ?1 and pooled = ?2");
			query.setParameter("2", false);
		}
		query.setParameter("1", localNode.getName());

		@SuppressWarnings("unchecked")
		List<PendingMail> dbMails = query.list();
		if (dbMails.isEmpty()) {
			logger.debug("No mails pending");
			return;
		}

		ArrayList<PendingMail> mailsSent = new ArrayList<PendingMail>();
		ArrayList<PendingMail> mailsFailed = new ArrayList<PendingMail>();

		TreeMap<String, ArrayList<PendingMail>> reportMap = new TreeMap<String, ArrayList<PendingMail>>();
		for (int iii = 0; iii < dbMails.size(); iii++) {
			PendingMail pMail = dbMails.get(iii);

			if (pMail.isPooled()) {
				String toAddr = pMail.getToAddress();
				ArrayList<PendingMail> reportMails = reportMap.get(toAddr);
				if (reportMails == null) {
					reportMails = new ArrayList<PendingMail>();
					reportMap.put(toAddr, reportMails);
				}
				reportMails.add(pMail);
			} else {
				boolean succeeded = sendMail(pMail);
				if (succeeded) {
					mailsSent.add(pMail);
				} else {
					mailsFailed.add(pMail);
				}
			}
		}

		for (Entry<String, ArrayList<PendingMail>> reportEntry : reportMap.entrySet()) {
			String toAddr = reportEntry.getKey();
			ArrayList<PendingMail> reportMails = reportEntry.getValue();
			sendPooledTo(toAddr, reportMails, mailsSent, mailsFailed);
		}

		Transaction trans = session.beginTransaction();
		for (int iii = 0; iii < mailsFailed.size(); iii++) {
			PendingMail pMail = mailsFailed.get(iii);
			pMail.setRetries(pMail.getRetries() + 1);
			pMail.setLastTry(new Date());
			session.save(pMail);
		}
		for (int iii = 0; iii < mailsSent.size(); iii++) {
			PendingMail pMail = mailsSent.get(iii);
			session.delete(pMail);
		}
		trans.commit();
	}

	public void sendPooledTo(String toAddr, ArrayList<PendingMail> toMails, ArrayList<PendingMail> mailsSent, ArrayList<PendingMail> mailsFailed) {

		TreeMap<String, ArrayList<PendingMail>> reportMap = new TreeMap<String, ArrayList<PendingMail>>();
		for (int iii = 0; iii < toMails.size(); iii++) {
			PendingMail pMail = toMails.get(iii);

			String fromAddr = pMail.getFromAddress();
			ArrayList<PendingMail> reportMails = reportMap.get(fromAddr);
			if (reportMails == null) {
				reportMails = new ArrayList<PendingMail>();
				reportMap.put(fromAddr, reportMails);
			}
			reportMails.add(pMail);
		}

		for (Entry<String, ArrayList<PendingMail>> reportEntry : reportMap.entrySet()) {
			String fromAddr = reportEntry.getKey();
			ArrayList<PendingMail> reportMails = reportEntry.getValue();
			sendPooledFromTo(toAddr, fromAddr, reportMails, mailsSent, mailsFailed);
		}
	}

	protected void sendPooledFromTo(String toAddr, String fromAddr, ArrayList<PendingMail> fromToMails, ArrayList<PendingMail> mailsSent, ArrayList<PendingMail> mailsFailed) {
		String str = new String();
		for (int iii = 0; iii < fromToMails.size(); iii++) {
			PendingMail pMail = fromToMails.get(iii);
			str += pMail.getSubject() + "\n";
			str += pMail.getMessage() + "\n\n";
		}

		try {
			Mail.sendMail(smtpHost, fromAddr, toAddr, "[DA-NRW] Report", str);
			mailsSent.addAll(fromToMails);
		} catch (Exception exc) {
			mailsFailed.addAll(fromToMails);
			logger.error("Error executing:", exc);
		}
	}

	protected boolean sendMail(PendingMail pMail) {
		boolean ret = false;
		try {
			Mail.sendMail(smtpHost, pMail.getFromAddress(), pMail.getToAddress(), pMail.getSubject(), pMail.getMessage());
			ret = true;
		} catch (Exception exc) {
			logger.error("Error executing:", exc);
		}
		return ret;
	}

	public Node getLocalNode() {
		return localNode;
	}

	public void setLocalNode(Node localNode) {
		this.localNode = localNode;
	}

	public String getSmtpHost() {
		return smtpHost;
	}

	public void setSmtpHost(String smtpHost) {
		this.smtpHost = smtpHost;
	}

	@Override
	public void setMDC() {
		MDC.put(WORKER_ID, "mailworker");
	}
}
