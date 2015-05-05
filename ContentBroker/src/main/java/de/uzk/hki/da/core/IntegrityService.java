/*
  DA-NRW Software Suite | ContentBroker
  Copyright (C) 2015 LVR  InfoKom

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
package de.uzk.hki.da.core;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.mail.MessagingException;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uzk.hki.da.grid.GridFacade;
import de.uzk.hki.da.model.Copy;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.model.Package;
import de.uzk.hki.da.service.HibernateUtil;
import de.uzk.hki.da.service.Mail;
import de.uzk.hki.da.utils.StringUtilities;


/**
 * Basic Functions for object/package checking
 * @author Jens Peters
 *
 */
public class IntegrityService {
	
	
	
	private GridFacade gridFacade;

	private static final Logger logger = LoggerFactory.getLogger(IntegrityService.class);
	
	/**
	 * Determines which of the objects that the local node is responsible for 
	 * (since it holds the primary copies of them) is the one which
	 * has not been checked for the longest period of time. 
	 * 
	 * @return the next object that needs audit. null if there is no object in the database which meets the criteria.
	 * 
	 * @author Jens Peters
	 * @author Daniel M. de Oliveira
	 * 
	 */
	public synchronized Object fetchObjectForAudit(String nodeName) {
		
		Session session = null;
		try {
			session = HibernateUtil.openSession();
			session.beginTransaction();

			Calendar now = Calendar.getInstance();
			now.add(Calendar.HOUR_OF_DAY, -24);
		
			@SuppressWarnings("rawtypes")
			List l = null;
			l = session.createQuery("from Object o where o.initial_node = ?1 and o.last_checked < ?2 and "
					+ "o.object_state != ?3 and o.object_state != ?4 and o.object_state >= 50"
					+ "order by o.last_checked asc")
					.setParameter("1", nodeName)
					.setTimestamp("2",now.getTime())
					.setParameter("3", Object.ObjectStatus.InWorkflow) // don't consider objects under work
					.setParameter("4", Object.ObjectStatus.UnderAudit) //           ||
							.setReadOnly(true).list();
			
			Object objectToAudit = (Object) l.get(0);
			for (Package p: objectToAudit.getPackages()) {
				for (@SuppressWarnings("unused") Copy c: p.getCopies()) {
				}
			}
			// lock object
			objectToAudit.setObject_state(Object.ObjectStatus.UnderAudit);
			session.update(objectToAudit);
			session.getTransaction().commit();
			session.close();
			
			return objectToAudit;
		
		} catch (IndexOutOfBoundsException e){
			if (session!=null) session.close();
			return null;
		}
	}	
	
	
	/**
	 * Updates the object state, sets the current time, and updates
	 * the database object accordingly
	 * @param object
	 * @param auditResult
	 */
	public synchronized void updateObject(Object object,Integer auditResult){
		
		object.setLast_checked(new Date());
		object.setObject_state(auditResult);
		Session session = HibernateUtil.openSession();
		session.beginTransaction();
		session.update(object);
		session.getTransaction().commit();
		session.close();
	}	
	
	
	/**
	 * Send email.
	 *
	 * @param obj the obj
	 */
	public void sendEmail(Object obj, String from, String to) {
		// send Mail to Admin with Package in Error
		logger.debug("Trying to send email");
		String subject = "[" + "da-nrw".toUpperCase() +  "] Problem Report für " + obj.getIdentifier();
		if (to!= null && !to.equals("")) {
			try {
				Mail.sendAMail( from , to, subject, "Es gibt ein Problem mit dem Objekt an Ihrem Knoten " + obj.getContractor().getShort_name()+ "/" + obj.getIdentifier());
			} catch (MessagingException e) {
				logger.error("Sending email problem report for " + obj.getIdentifier() + "failed");
			}
		} else {
			logger.error("Node Admin has no valid Email address!");
		}
	}
	
	/**
	 * Checks Object validity.
	 * True: object is valid on all nodes
	 * False: object is invalid at least on one node 
	 * @author Jens Peters
	 * @param obj
	 * @param minRepls
	 * @return
	 */
	
	public boolean checkObject(Object obj, int minRepls) {
		Integer auditResult = checkObjectValidity(obj, minRepls);
		updateObject(obj,auditResult);
		if (auditResult==Object.ObjectStatus.ArchivedAndValidAndNotInWorkflow) return true;
		else return false;
	}


	/**
	 * @author Jens Peters
	 * @author Daniel M. de Oliveira
	 * @param obj the obj
	 * @return the new object state. Either archivedAndValidState or errorState.
	 */
	private int checkObjectValidity(Object obj, int minRepls) {
		if (minRepls ==0) throw new IllegalStateException("minNodes not set correctly!");
		Calendar olderThan = Calendar.getInstance();
		olderThan.add(Calendar.DAY_OF_YEAR, -365);
		
		logger.debug("Check Object "+ obj.getIdentifier());
		boolean completelyValid = true;		
		if (obj.getContractor()==null) {
			String err= "Could not determine valid Contractor for object " + obj.getIdentifier();
			logger.error(err);
			return Object.ObjectStatus.Error;
		}
		if (obj.getLast_checked().before(olderThan.getTime())) {
			String err= obj.getIdentifier() + " is assumed to invalid because last check is too old, was " +obj.getLast_checked();
			logger.error(err);
			return Object.ObjectStatus.Error;
		}
		String dao_base = C.WA_AIP + "/"+obj.getContractor().getShort_name()+"/"+obj.getIdentifier()+"/"+obj.getIdentifier()+".pack_";
		for (Package pack : obj.getPackages()) {
			String dao = dao_base + pack.getName()+".tar"; 
			logger.debug("Checking: " + dao );
			logger.debug("Original Checksum in Package Table is " + pack.getChecksum());
			int copies = 0;
			String checksum_now_pc = gridFacade.reComputeAndGetChecksumInCustody(dao);
			if (StringUtilities.isNotSet(checksum_now_pc) || (!checksum_now_pc.equals(pack.getChecksum()))) {
				String err= "PRIMARY COPY in ERROR " + obj.getIdentifier();
				logger.error(err);
				return Object.ObjectStatus.Error;
			} 
			copies = 1;
			for (Copy copy : pack.getCopies() ) {
				if (StringUtilities.isNotSet(copy.getChecksum()) || !copy.getChecksum().equals(pack.getChecksum())) {
					String err= "SECONDARY COPY in ERROR "+ obj.getIdentifier();
					logger.error(err);
					completelyValid = false;
				} else {
			
					if (copy.getChecksumDate().before(olderThan.getTime())) {
						logger.error("SECONDARY COPY was last checked on " + copy.getChecksumDate() + " which is assumed to be not valid!");
						completelyValid = false;
					}
					else copies ++;
				}
			}
			if (copies<minRepls) {
				logger.error("MinRepls count not reached for " + obj.getIdentifier());
				completelyValid = false; 
			}
				
		}
		if (completelyValid) return Object.ObjectStatus.ArchivedAndValidAndNotInWorkflow;
		else return Object.ObjectStatus.Error;
	}
	public GridFacade getGridFacade() {
		return gridFacade;
	}


	public void setGridFacade(GridFacade gridFacade) {
		this.gridFacade = gridFacade;
	}

}
