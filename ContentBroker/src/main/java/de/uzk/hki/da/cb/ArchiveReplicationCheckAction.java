/*
  DA-NRW Software Suite | ContentBroker
  Copyright (C) 2013 Historisch-Kulturwissenschaftliche Informationsverarbeitung
  Universität zu Köln

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

package de.uzk.hki.da.cb;

import java.util.Date;

import javax.mail.MessagingException;

import org.hibernate.classic.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uzk.hki.da.core.ConfigurationException;
import de.uzk.hki.da.db.HibernateUtil;
import de.uzk.hki.da.grid.GridFacade;
import de.uzk.hki.da.model.Job;
import de.uzk.hki.da.model.Node;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.model.StoragePolicy;
import de.uzk.hki.da.service.Mail;

/**
 * Checks if the minimum number of replications of an AIP, as specified by minNodes, is available on any
 * set of available resources as specified in the local node's repl_destinations.
 * 
 * The AIP must be located at the logical path <code>[zonePath]/aip/[csn]/[objectIdentifier]/[objectIdentifier].pack_[pkgname].tar</code>.
 * 
 * @author Jens Peters
 * @author Daniel M. de Oliveira
 * 
 */
public class ArchiveReplicationCheckAction extends AbstractAction{

	static final Logger logger = LoggerFactory.getLogger(ArchiveReplicationCheckAction.class);
	
	private int minNodes = 3;
	private int timeOut = 4000;
	
	private Node dipNode;
	
	private GridFacade gridRoot;
	
	
	/**
	 * @return false if minimum number of replications could not be detected.
	 */
	@Override
	public
	boolean implementation() {
		if (getGridRoot()==null) throw new ConfigurationException("gridRoot not set");
		setKILLATEXIT(true);
		object.reattach();
		
		String aip = "/aip/" + object.getContractor().getShort_name() + 
				"/" + object.getIdentifier() + "/"+ object.getIdentifier() + ".pack_" + object.getLatestPackage().getName() + ".tar";
		
		StoragePolicy sp = new StoragePolicy(localNode);
		sp.setMinNodes(minNodes);
		if (!gridRoot.storagePolicyAchieved(aip, sp)){
			delay();
			return false;
		}
		
		
		setObjectStored(object);
		
		sendReciept(job, object);
		object.getPackages().get(object.getPackages().size()-1).getFiles().clear();
		object.getPackages().get(object.getPackages().size()-1).getEvents().clear();
		
		createPublicationJob();
		
		return true;
	}
	
	
	private void delay(){
		try {
			Thread.sleep(timeOut); // to prevent unnecessary small intervals when checking
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	

	/**
	 * @author Daniel M. de Oliveira Jens Peters
	 */
	private void createPublicationJob(){
		
		logger.info("Creating child job with state 540 on "+   getDipNode().getName()+" for possible publication of this object.");
		Job child = new Job (job, "540");
		child.setInitial_node(getDipNode().getName());
		child.setObject(getObject());
		child.setDate_created(String.valueOf(new Date().getTime()/1000L));
		
		Session session = HibernateUtil.openSession();
		session.beginTransaction();
		session.save(child);
		session.getTransaction().commit();
		session.close();
	}
	
	
	
	
	public Node getDipNode() {
		return dipNode;
	}


	public void setDipNode(Node dipNode) {
		this.dipNode = dipNode;
	}
	
	/**
	 * @param minNodes
	 *            the minNodes to set
	 */
	public void setMinNodes(int minNodes) {
		this.minNodes = minNodes;
	}

	/**
	 * @return the minNodes
	 */
	public int getMinNodes() {
		return minNodes;
	}

	/**
	 * Defines the length of the interval at which the function checks the state
	 * of the replication.
	 * 
	 * @param timeOut
	 */
	public void setTimeOut(int timeOut) {
		this.timeOut = timeOut;
	}
	
	/** 
	 * @author Jens Peters
	 * Stores Object DB entry in the database. The entry is deduced from the queue entry which
	 * is not persistent. The method checks if the Object DB entry already exists, and
	 * adds new Package if that's the case. 
	 */
	private void setObjectStored(Object obj) {

		// THE OBJECT Is set to the full archived valid state now!
		// since it has sucessfully been been created before. 
		// The Object is stored in archived and replicated state 100!
		obj.setObject_state(100);
		
		obj.setDate_modified(String.valueOf(new Date().getTime()));
		obj.setStatic_nondisclosure_limit((Date) actionCommunicatorService.extractDataObject(job.getId(), 
											"static_nondisclosure_limit"));
		obj.setDynamic_nondisclosure_limit((String) actionCommunicatorService.extractDataObject(job.getId(),
											"dynamic_nondisclosure_limit"));
	}
	
	
	/**
	 * @author Jens Peters
	 * Sends an Reciept to the deliverer of package
	 */
	public boolean sendReciept(Job job, Object obj){
		if (dao==null) throw new IllegalStateException("centralDatabaseDAO not set");
		
		String objectIdentifier=obj.getIdentifier();
		String email = obj.getContractor().getEmail_contact();
		String subject;
		String msg;
		if (obj.hasDeltas())
		{
			subject = "[DA-NRW] Einlieferungsbeleg für Ihr Delta zum Objekt " + objectIdentifier;
			msg = "Ihrem archivierten Objekt mit dem Identifier " + objectIdentifier + " und der URN " + obj.getUrn() +
					" wurde erfolgreich ein Delta mit dem Paketnamen \"" + object.getOrig_name() + "\" hinzugefügt.";
		}
		else
		{
			subject = "[DA-NRW] Einlieferungsbeleg für " + objectIdentifier;
			msg = "Ihr eingeliefertes Paket mit dem Namen \""+ object.getOrig_name() + "\" wurde erfolgreich im DA-NRW archiviert.\n\n" +
			"Identifier: " + objectIdentifier + "\n" +
			"URN: " + obj.getUrn();
		}
		
		logger.debug(subject);
		logger.debug("");
		logger.debug(msg);
		
		if (email!=null) {
		try {
			Mail.sendAMail(email, subject, msg);
		} catch (MessagingException e) {
			logger.error("Sending email reciept for " + objectIdentifier + " failed",e);
			return false;
		}
		} else logger.info(obj.getContractor().getShort_name() + " has no valid Email Adress!");
		
		return true;
	}

	@Override
	void rollback() {}


	public GridFacade getGridRoot() {
		return gridRoot;
	}


	public void setGridRoot(GridFacade gridRoot) {
		this.gridRoot = gridRoot;
	}
}
	
