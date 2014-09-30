/*
  DA-NRW Software Suite | ContentBroker
  Copyright (C) 2014 LVRInfoKom
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

package de.uzk.hki.da.service;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.mail.MessagingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uzk.hki.da.action.AbstractAction;
import de.uzk.hki.da.core.UserException;
import de.uzk.hki.da.model.Job;
import de.uzk.hki.da.model.Node;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.model.PreservationSystem;

/**
 * Encapsulates the content of business code related emails.
 * @author Daniel M. de Oliveira
 * @author Jens Peters
 * @author Thomas Kleinke
 */
public class MailContents {

	private static final String DA_NRW = "da-nrw";

	private static final Logger logger = LoggerFactory.getLogger(MailContents.class);
	
	private PreservationSystem preservationSystem;
	private Node localNode;
	
	
	/**
	 * @param preservationSystem
	 * @param localNode
	 */
	public MailContents(PreservationSystem preservationSystem,Node localNode){
		if (preservationSystem==null) throw new IllegalArgumentException("preservation system must not be null");
		if (preservationSystem.getAdmin()==null) throw new IllegalArgumentException("preservation system must have an admin");
		if (preservationSystem.getAdmin().getEmailAddress()==null||preservationSystem.getAdmin().getEmailAddress().isEmpty()) throw new IllegalStateException("preservation systems admin must have an email address");
		if (localNode==null) throw new IllegalArgumentException("local node must not be null");
		if (localNode.getAdmin()==null) throw new IllegalArgumentException("local node must have an admin");
		if (localNode.getAdmin().getEmailAddress()==null||localNode.getAdmin().getEmailAddress().isEmpty()) throw new IllegalArgumentException("local nodes admin must have an email address");
		this.preservationSystem = preservationSystem;
		this.localNode = localNode;
	}
	
	private void checkObject(Object object){
		if (object==null) throw new IllegalArgumentException("object must not be null");
		if (object.getIdentifier()==null) throw new IllegalArgumentException("obj identifier must not be null");
		if (object.getContractor()==null) throw new IllegalArgumentException("obj has not contractor");
		if (object.getContractor().getEmailAddress()==null||object.getContractor().getEmailAddress().isEmpty()) throw new IllegalArgumentException("objs contractor has no email adress");
	}
	
	
	public void informUserAboutPendingDecision(Object obj){
		checkObject(obj);
		
		String subject = "[" + DA_NRW.toUpperCase() + "] Decision required.";
		String msg = "Please look at daweb and make your decision for object " + obj.getIdentifier();
		
		try {
			Mail.sendAMail(preservationSystem.getAdmin().getEmailAddress(), obj.getContractor().getEmailAddress(), subject, msg);
		} catch (MessagingException e) {
			logger.error("Sending email problem report for " +  obj.getIdentifier() + " failed");
		}
	}
	
	
	/**
	 * Informs the Node Admin about the problems being found
	 * 
	 * @param logicalPath
	 * @param msg
	 * @author Jens Peters
	 */
	public void auditInformNodeAdmin(Object obj, String msg) {
		// send Mail to Admin with Package in Error
		checkObject(obj);

		String subject = "[" + DA_NRW.toUpperCase() + "] Problem Report für " + obj.getIdentifier();
		try {
			Mail.sendAMail(preservationSystem.getAdmin().getEmailAddress(), localNode.getAdmin().getEmailAddress(), subject, msg);
		} catch (MessagingException e) {
			logger.error("Sending email problem report for " +  obj.getIdentifier() + " failed");
		}
	}
	
	
	/**
	 * @author Jens Peters
	 * @param email
	 * @param objectIdentifier
	 * @param csn
	 */
	public void retrievalReport(Object object){
		checkObject(object);
		
		String subject = "Retrieval Report für " + object.getIdentifier();
		String msg = "Ihr angefordertes Objekt mit dem Namen \""+ object.getIdentifier() + "\" wurde unter Ihrem Outgoing Ordner unter " 
				+ object.getContractor().getShort_name() + "/outgoing/ abgelegt und steht jetzt"
				+ " zum Retrieval bereit!\n\n";
		try {
			Mail.sendAMail(preservationSystem.getAdmin().getEmailAddress(),object.getContractor().getEmailAddress(), subject, msg);
		} catch (MessagingException e) {
			logger.error("Sending email retrieval reciept for " + object.getIdentifier() + "failed", e);
		}
	}
	
	/**
	 * @author Jens Peters
	 * Sends an Reciept to the deliverer of package
	 */
	public boolean sendReciept(Job job, Object obj){
		checkObject(obj);
		
		String subject;
		String msg;
		if (obj.isDelta())
		{
			subject = "[DA-NRW] Einlieferungsbeleg für Ihr Delta zum Objekt " + obj.getIdentifier();
			msg = "Ihrem archivierten Objekt mit dem Identifier " + obj.getIdentifier() + " und der URN " + obj.getUrn() +
					" wurde erfolgreich ein Delta mit dem Paketnamen \"" + obj.getOrig_name() + "\" hinzugefügt.";
		}
		else
		{
			subject = "[DA-NRW] Einlieferungsbeleg für " + obj.getIdentifier();
			msg = "Ihr eingeliefertes Paket mit dem Namen \""+ obj.getOrig_name() + "\" wurde erfolgreich im DA-NRW archiviert.\n\n" +
			"Identifier: " + obj.getIdentifier() + "\n" +
			"URN: " + obj.getUrn();
		}
		
		logger.debug(subject);
		logger.debug("");
		logger.debug(msg);
		
		try {
			Mail.sendAMail(preservationSystem.getAdmin().getEmailAddress(), obj.getContractor().getEmailAddress(), subject, msg);
		} catch (MessagingException e) {
			logger.error("Sending email reciept for " + obj.getIdentifier() + " failed",e);
			return false;
		}
		
		return true;
	}

	/**
	 * Creates report about the error
	 * Sends Email to the Admin
	 * @author Jpeters
	 */
	public void abstractActionCreateAdminReport(Exception e,Object object,AbstractAction action) {

		String errorStatus = action.getStartStatus().substring(0,action.getStartStatus().length()-1) + "1";
		String email = localNode.getAdmin().getEmailAddress();
		String subject = "Fehlerreport für " + object.getIdentifier() + " : Status (" + errorStatus + ")" ;
		String msg = e.getMessage();
		msg +="\n\n";
		StringWriter s = new StringWriter();
	    e.printStackTrace(new PrintWriter(s));
	    msg += s.toString();
		
		if (email!=null && !email.equals("")) {
		try {
			Mail.sendAMail(preservationSystem.getAdmin().getEmailAddress(), email, subject, msg);
		} catch (MessagingException ex) {
			logger.error("Sending email reciept for " + object.getIdentifier() + " failed",ex);
		}
		} else logger.info(localNode.getName() + " has no valid email address!");
	}
	
	
	
	/**
	 * Creates report about the error
	 * Sends e-mail to the User
	 * @author Thomas Kleinke
	 */
	public void userExceptionCreateUserReport(UserExceptionManager uem,UserException e,Object object) {
		if (object==null) throw new IllegalArgumentException("object must not be null");
		
		String email = object.getContractor().getEmailAddress();
		String subject = "Fehlerreport für " + object.getIdentifier();
		String message = uem.getMessage(e.getUserExceptionId());
		
		message = message.replace("%OBJECT_IDENTIFIER", object.getIdentifier())
			 .replace("%CONTAINER_NAME", object.getLatestPackage().getContainerName())
			 .replace("%ERROR_INFO", e.getErrorInfo());
				
		
		logger.debug("Sending mail to: " + email + "\n" + subject + "\n" + message);
		
		if (email == null){
			logger.warn(object.getContractor().getShort_name() + " has no valid email address!");		
			return;
		}
		try {
			Mail.sendAMail(preservationSystem.getAdmin().getEmailAddress(),email, subject, message);
		} catch (MessagingException ex) {
			logger.error("Sending email reciept for " + object.getIdentifier() + " failed", ex);
		}
	}	
}
