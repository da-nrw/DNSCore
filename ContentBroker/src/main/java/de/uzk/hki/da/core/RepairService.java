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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.mail.MessagingException;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uzk.hki.da.grid.IrodsCommandLineConnector;
import de.uzk.hki.da.model.Copy;
import de.uzk.hki.da.model.Node;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.model.Package;
import de.uzk.hki.da.model.PreservationSystem;
import de.uzk.hki.da.model.WorkArea;
import de.uzk.hki.da.service.HibernateUtil;
import de.uzk.hki.da.service.Mail;
import de.uzk.hki.da.utils.StringUtilities;


/**
 * Basic Functions for object/package checking
 * @author Jens Peters
 *
 */
public class RepairService {
	
	private static final Logger logger = LoggerFactory.getLogger(RepairService.class);
	private String zoneName;
	private String replDestinations;
	private Node node;
	private PreservationSystem pSystem;
	
	public RepairService(PreservationSystem pSystem, Node node, String zoneName, String replDestinations){
		this.pSystem = pSystem;
		this.node = node;
		this.zoneName = zoneName;
		this.replDestinations = replDestinations;
	}
	
	public synchronized Object fetchObjectForAudit() {
		
		Session session = null;
		Object objectToAudit = null;
		try {
			String nodeName = this.node.getName();
			
			session = HibernateUtil.openSession();

			Calendar now = Calendar.getInstance();
			now.add(Calendar.HOUR_OF_DAY, -24);
		
			try {
				Query query = session.createQuery("from Object o where o.initial_node = ?1 and o.last_checked < ?2 and "
						+ "o.object_state = ?3 " + "order by o.last_checked asc");
				query.setParameter("1", nodeName);
				query.setTimestamp("2", now.getTime());
				query.setParameter("3", Object.ObjectStatus.ArchivedAndValidAndNotInWorkflow);

				@SuppressWarnings("unchecked")
				List<Object> obbiList = query.list();

				if (obbiList.size() > 0) {
					objectToAudit = (Object) obbiList.get(0);

					for (Package p : objectToAudit.getPackages()) {
						p.getCopies().size();
					}

					objectToAudit.setObject_state(Object.ObjectStatus.UnderAudit);
					Transaction trans = session.beginTransaction();
					session.save(objectToAudit);
					trans.commit();
				}				
			} finally {
				session.close();
			}
			
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
	public synchronized void unlockObject(Object object){
		
		object.setLast_checked(new Date());
		Session session = HibernateUtil.openSession();
		session.beginTransaction();
		session.update(object);
		object.setObject_state(Object.ObjectStatus.ArchivedAndValidAndNotInWorkflow);
		session.getTransaction().commit();
		session.close();
	}	
	
	
	/**
	 * Send email.
	 *
	 * @param obj the obj
	 */
	public void sendEmail(Object obj, String msgText) {
		// send Mail to Admin with Package in Error
		logger.debug("Trying to send email");
		String subject = "[" + "da-nrw".toUpperCase() +  "] Problem Report für " + obj.getIdentifier();
		String to = node.getAdmin().getEmailAddress();
		if (to!= null && !to.equals("")) {
			try {
				Mail.queueMail(node.getName(), 
						pSystem.getAdmin().getEmailAddress(),
						to, 	
						subject, 
						msgText,
						node.getAdmin().isMailsPooled());
			} catch (MessagingException e) {
				logger.error("Sending email problem report for " + obj.getIdentifier() + "failed");
			}
		} else {
			logger.error("Node Admin has no valid Email address!");
		}
	}
	
	protected void incrementRepair(Package pack) {
		if (pack.getRepair() == null){
			pack.setRepair(1);
		} else {
			pack.setRepair(pack.getRepair() + 1);
		}
	}

	protected void incrementRepair(Copy cop) {
		if (cop.getRepair() == null){
			cop.setRepair(1);
		} else {
			cop.setRepair(cop.getRepair() + 1);
		}
	}

	protected void repairLocalPackage(Object object, Package pack, String daoBase, Copy intactCopy) {
		IrodsCommandLineConnector iclc = new IrodsCommandLineConnector();
		String source = "/" + intactCopy.getNode().getIdentifier() + "/federated" 
				+ "/" + this.zoneName + "/" + daoBase + intactCopy.getPackName() + ".tar";; 
		String corruptedOld = "/" + this.zoneName + "/" + daoBase + pack.getName()+".tar";
		this.incrementRepair(pack);
		String target = "/" + this.zoneName + "/" + daoBase + pack.getName()+".tar";
		String resc = this.replDestinations;
		
		logger.debug("Local Package corrupt: " + corruptedOld);
		logger.debug("Will be replaced with: " + source);
		logger.debug("          New Name is: " + target);
		
		iclc.rsync(source, target, resc);
		String checksum = iclc.computeChecksumForce(target);
		pack.setChecksum(checksum);

		String msg = "Defektes Paket: " + corruptedOld 
				+ " ersetzt durch " + source
				+ ", neuer Name ist " + target;
		this.sendEmail(object, msg);
	}
	
	protected void repairCopyFromLocal(Object object, Copy cop, Package pack, String daoBase){
		IrodsCommandLineConnector iclc = new IrodsCommandLineConnector();

		String source = "/" + this.zoneName + "/" + daoBase + pack.getName()+".tar";
		String corruptedOld = "/" + cop.getNode().getIdentifier() + "/federated/" + 
								this.zoneName + "/" + daoBase + cop.getPackName() + ".tar";

		this.incrementRepair(cop);
		String copyPath = this.zoneName + "/" + daoBase + cop.getPackName() + ".tar";  
		String target = "/" + cop.getNode().getIdentifier() + "/federated/" + copyPath; 
		String resc = this.replDestinations;
		
		logger.debug("Foreign Package corrupt: " + corruptedOld);
		logger.debug("  Will be replaced with: " + source);
		logger.debug("            New Name is: " + target);
		
		iclc.rsync(source, target, resc);
		cop.setChecksum(pack.getChecksum());
		cop.setPath(copyPath);
		
		String msg = "Defektes Paket: " + corruptedOld 
				+ " ersetzt durch " + source
				+ ", neuer Name ist " + target;
		this.sendEmail(object, msg);
	}
	
	protected boolean estimatePackageChecksumms(Object object, Package pack, String daoBase) {
		IrodsCommandLineConnector iclc = new IrodsCommandLineConnector();

		boolean completelyValid = true;		
		
		String dao = "/" + this.zoneName + "/" + daoBase + pack.getName()+".tar"; 
		logger.debug("Checking: " + dao );
		

		String localChecksum = "";
		try {
			localChecksum = iclc.computeChecksumForce(dao);
		} catch (Exception exc) {
		}
				
		TreeMap<String, ArrayList<Copy>> replChecks = new TreeMap<String, ArrayList<Copy>>();  
		for (Copy copy : pack.getCopies() ) {
			String checksum = copy.getChecksum();
			ArrayList<Copy> copyList = replChecks.get(checksum);
			if (copyList == null){
				copyList = new ArrayList<Copy>();
				replChecks.put(checksum, copyList);
			}
			copyList.add(copy);
		}

		logger.debug("Old (DB) Checksum is: " + pack.getChecksum());
		logger.debug("Computed Checksum is: " + localChecksum);
		if (StringUtilities.isNotSet(pack.getChecksum()) || (!pack.getChecksum().equals(localChecksum))) {
			completelyValid = false;
		}

		for (Map.Entry<String, ArrayList<Copy>> replCheck : replChecks.entrySet()) {
			String checksum = replCheck.getKey();
			ArrayList<Copy> copyList = replCheck.getValue();
			for (int nnn = 0; nnn < copyList.size(); nnn++) {
				String nodeIdent = copyList.get(nnn).getNode().getIdentifier();
				logger.debug("  Remote Checksum is: " + checksum + " on " + nodeIdent);
			}
			if (StringUtilities.isNotSet(pack.getChecksum()) || (!pack.getChecksum().equals(checksum))) {
				completelyValid = false;
			}
		}
		
		if (completelyValid){
			return true;
		}

		String votedChecksum = null;
		int maxCheckMatch = 0;
		for (Map.Entry<String, ArrayList<Copy>> replCheck : replChecks.entrySet()) {
			String checksum = replCheck.getKey();
			if (StringUtilities.isSet(checksum)) {
				ArrayList<Copy> copyList = replCheck.getValue();
				if (maxCheckMatch < copyList.size()) {
					votedChecksum = checksum; 
					maxCheckMatch = copyList.size();
				}
				else if (maxCheckMatch == copyList.size()){
					if (checksum.equals(localChecksum) || checksum.equals(pack.getChecksum())){
						votedChecksum = checksum; 
					}
				}
			}
		}
		
		if (maxCheckMatch < 2){
			if (localChecksum.equals(pack.getChecksum())){
				votedChecksum = localChecksum; 
			} else if (!localChecksum.equals(votedChecksum) && !pack.getChecksum().equals(votedChecksum)){
				String msg = "Package " + pack.getName() + " kann nicht maschinell repariert werden, " 
						   + " da nicht ermittelt werden kann, welche Kopie gültig ist.";
				this.sendEmail(object, msg);

				logger.error("Package " + pack.getName()  + " kann nicht maschinell repariert werden");
				return false;
			}
		}
		
		if (!localChecksum.equals(votedChecksum)){
			Copy intactCopy = replChecks.get(votedChecksum).get(0);
			this.repairLocalPackage(object, pack, daoBase, intactCopy);
		} else if (!pack.getChecksum().equals(votedChecksum)){
			pack.setChecksum(votedChecksum);
		}

		for (Map.Entry<String, ArrayList<Copy>> replCheck : replChecks.entrySet()) {
			String checksum = replCheck.getKey();
			if (!votedChecksum.equals(checksum)) {
				ArrayList<Copy> copyList = replCheck.getValue();
				for (int nnn = 0; nnn < copyList.size(); nnn++) {
					Copy cop = copyList.get(nnn);
					if (votedChecksum.equals(pack.getChecksum())){
						this.repairCopyFromLocal(object, cop, pack, daoBase);
					}
				}
			}
		}

		Session session = HibernateUtil.openSession();
		session.update(pack);

		Transaction transi =session.beginTransaction();		
		
		session.save(pack);
		transi.commit();
		session.close();
		
		return false;
	}
	
	public boolean estimateChecksumms(Object obj) {
		Calendar olderThan = Calendar.getInstance();
		olderThan.add(Calendar.DAY_OF_YEAR, -365);
		
		logger.debug("Check Object "+ obj.getIdentifier());
		boolean completelyValid = true;		
		if (obj.getContractor()==null) {
			String err= "Could not determine valid Contractor for object " + obj.getIdentifier();
			logger.error(err);
			return false;
		}
		String daoBase = WorkArea.AIP + "/"+obj.getContractor().getShort_name()+"/"+obj.getIdentifier()+"/"+obj.getIdentifier()+".pack_";

		for (Package pack : obj.getPackages()) {
			completelyValid &= this.estimatePackageChecksumms(obj, pack, daoBase);
		}

		return completelyValid;
	}
}
