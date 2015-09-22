package de.uzk.hki.da.service;
/*
DA-NRW Software Suite | ContentBroker
Copyright (C) 2015 LVRInfoKom
Landschaftsverband Rheinland

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

/**
* @author jens Peters
* creates Report as CSV File about state of jobs and objects 
*/

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uzk.hki.da.model.Copy;
import de.uzk.hki.da.model.Job;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.model.Package;
import de.uzk.hki.da.utils.C;

/**
 * 
 * @author Jens Peters
 * Class for handling operations stored in CSV files
 *
 */
public class CSVQueryHandler {
	
	CSVFileHandler csvFileHandler = null;
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private String nodeName;
	
	private int userId;
	
	public CSVQueryHandler(String nodeName, int userId) {
		csvFileHandler = new CSVFileHandler();
		csvFileHandler.setEncoding("CP1252");
		this.nodeName = nodeName;
		this.userId = userId;
	}
	
	@SuppressWarnings("serial")
	public void generateRetrievalRequests(File csvFile){
		try {
			csvFileHandler.parseFile(csvFile);
			Object o = null;
			for (Map<String, java.lang.Object> csvEntry : csvFileHandler.getCsvEntries()) {
				logger.debug("Evaluating " + csvEntry.get("origName"));
				String origName = String.valueOf(csvEntry.get("origName"));
				o = fetchObject(origName);
				if (o != null) {
					createRetievalJob(o);
				}
			}
		}catch (IOException e) {
			logger.error("catched " + e.toString() + " while working with "
					+ csvFile.getAbsolutePath());
			throw new RuntimeException(
					"CSV File operations not possible "
							+ csvFile.getAbsolutePath(), e) {
			};
		}
	}

	@SuppressWarnings("serial")
	public void generateReportBasedOnFile(File csvFile) {
		logger.error("generating Report file " + csvFile);
		try {
			csvFileHandler.parseFile(csvFile);
			evalStates();
			csvFileHandler.persistStates(csvFile);
		} catch (IOException e) {
			logger.error("catched " + e.toString() + " while working with "
					+ csvFile.getAbsolutePath());
			throw new RuntimeException(
					"CSV File operations not possible "
							+ csvFile.getAbsolutePath(), e) {
			};
		}
	}
	

	private void createRetievalJob(Object o){
		if (fetchJob (o.getOrig_name(),o.getIdentifier())== null) {
		Session session = HibernateUtil.openSession();
		session.beginTransaction();
		Job result = new Job();
		result = new Job (nodeName,C.WORKFLOW_STATUS_START___RETRIEVAL_ACTION);
		result.setResponsibleNodeName(nodeName);
		result.setObject(o);
		result.setDate_created(String.valueOf(new Date().getTime()/1000L));
		session.save(result);
		session.getTransaction().commit();
		session.close();
		logger.debug("created successfully retieval job!");
		}
	}

	@SuppressWarnings("unchecked")
	private synchronized Job fetchJob(String origName, String identifier ) {
		Session session = HibernateUtil.openSession();
		session.beginTransaction();
		logger.debug("Fetch job origName: " + origName + " identifier: " + identifier);
		List<Job> joblist=null;
		try{				
			joblist = session
					.createQuery("SELECT j FROM Job j LEFT JOIN j.obj as o where "
							+ " o.orig_name=?1 and o.identifier=?2 and o.user.id = ?3 ")
					.setParameter("1", origName).setParameter("2", identifier).setParameter("3", userId).setCacheable(false).setMaxResults(1).list();
			
			if ((joblist == null) || (joblist.isEmpty())){
				logger.trace("no job found for " + origName + " identifier: " + identifier);
				session.close();
				return null;
			}
			
			Job job = joblist.get(0);
			
			session.close();
			
			logger.debug("Fetched job of object "+job.getObject().getIdentifier());
		
		}catch(Exception e){
			session.close();
			logger.error("Caught error in fetchJobFromQueue");
			
			throw new RuntimeException(e.getMessage(), e);
		}
		return joblist.get(0);
}


private synchronized Object fetchObject(String origName) {
	logger.debug("Fetch object origName: " + origName);
	Session session = null;
	try {
		session = HibernateUtil.openSession();
		session.beginTransaction();

		@SuppressWarnings("rawtypes")
		List l = null;
		l = session.createQuery("from Object o where o.orig_name = ?1 and o.user.id = ?2 ")
				.setParameter("1", origName).setParameter("2", userId).setReadOnly(true).list();
	
		if ((l == null) || (l.isEmpty())){
			logger.debug("no object found for " + origName );
			session.close();
			return null;
		}
		Object objectToFetch = (Object) l.get(0);
		for (Package p : objectToFetch.getPackages()) {
			for (@SuppressWarnings("unused")
			Copy c : p.getCopies()) {
			}
		}
		session.close();

		return objectToFetch;

	} catch (Exception e) {
		if (session != null)
			session.close();
		logger.error("Caught error in fetchObjectFromQueue");
		throw new RuntimeException(e.getMessage(), e);
	}
}
	private void evalStates() {
		Object o = null;
		String identifier = null;
		for (Map<String, java.lang.Object> csvEntry : csvFileHandler.getCsvEntries()) {
			logger.debug("Evaluating " + csvEntry.get("origName"));
			String origName = String.valueOf(csvEntry.get("origName"));
			o = fetchObject(origName);
			String text = Object.ObjectStatus.NotKnownText;
			boolean jobState = false; 
			if (o != null) {
				if (o.getObject_state() == Object.ObjectStatus.ArchivedAndValidAndNotInWorkflow) {
					text = Object.ObjectStatus.ArchivedAndValidAndNotInWorkflowText;
				} else if (o.getObject_state() == Object.ObjectStatus.InitState) {
					text = Object.ObjectStatus.InitStateText;
					jobState = true;
				} else if (o.getObject_state() == Object.ObjectStatus.UnderAudit) {
					text = Object.ObjectStatus.UnderAuditText;
				} else if (o.getObject_state() == Object.ObjectStatus.InWorkflow) {
					text = Object.ObjectStatus.InWorkflowText + "";
					jobState = true;
				} else if (o.getObject_state() == Object.ObjectStatus.Error) {
					text = Object.ObjectStatus.ErrorText;
				}
				identifier = o.getIdentifier();
				// we've got jobs here, we look for the state! 
				if (jobState && identifier != null) {
					Job job = fetchJob(origName, identifier);
					csvEntry.put("statuscode", (java.lang.Object) job.getStatus());
				}
			}
			csvEntry.put("bemerkung", (java.lang.Object) text);
			
			if (identifier != null) {
				csvEntry.put("identifier", (java.lang.Object) identifier);
			}
			logger.debug(text);
		}
	}
}
