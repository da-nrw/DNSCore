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

import java.io.IOException;
import java.util.Date;

import javax.mail.MessagingException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.NotImplementedException;

import de.uzk.hki.da.core.ConfigurationException;
import de.uzk.hki.da.grid.GridFacade;
import de.uzk.hki.da.model.Job;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.model.Package;
import de.uzk.hki.da.model.StoragePolicy;
import de.uzk.hki.da.service.Mail;

/**
 * Checks if the minimum number of replications of an AIP, as specified by minNodes, is available on any
 * set of available resources as specified in the local node's repl_destinations.
 * 
 * The AIP must be located at the logical path <code>/aip/[csn]/[objectIdentifier]/[objectIdentifier].pack_[pkgname].tar</code>.
 * 
 * @author Jens Peters
 * @author Daniel M. de Oliveira
 * 
 */
public class ArchiveReplicationCheckAction extends AbstractAction{

	private int minNodes = 3;
	private int timeOut = 4000;
	
	private String presentationRepositoryNodeName;
	
	private GridFacade gridRoot;

	/**
	 * @throws IOException 
	 */
	@Override
	public
	boolean implementation() throws IOException {
		if (getGridRoot()==null) throw new ConfigurationException("gridRoot not set");
		setKILLATEXIT(true);

		StoragePolicy sp = new StoragePolicy(localNode);
		sp.setMinNodes(minNodes);
		do{
			delay();
		}
		while (!gridRoot.storagePolicyAchieved(
				object.getContractor().getShort_name() + 
				"/" + object.getIdentifier() + "/"+ object.getIdentifier() + ".pack_" + object.getLatestPackage().getName() + ".tar", 
				sp));
		
		prepareObjectForObjectDBStorage(object);
		sendReciept(job, object);
		
		toCreate = createPublicationJob(job);
		FileUtils.deleteDirectory(object.getPath().toFile());
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
	 * @author Daniel M. de Oliveira 
	 * @author Jens Peters
	 */
	private Job createPublicationJob(Job parent){
		
		Job result = new Job();
		
		logger.info("Creating child job with state 540 on "+ 
				getPresentationRepositoryNodeName()+" for possible publication of this object.");
		result = new Job (parent, "540");
		result.setResponsibleNodeName(getPresentationRepositoryNodeName());
		result.setObject(getObject());
		result.setDate_created(String.valueOf(new Date().getTime()/1000L));
		
		return result;
	}
	
	/**
	 * @author Daniel M. de Oliveira
	 * @return
	 */
	public String getPresentationRepositoryNodeName() {
		return presentationRepositoryNodeName;
	}


	public void setPresentationRepositoryNodeName(String presentationRepositoryNodeName) {
		this.presentationRepositoryNodeName = presentationRepositoryNodeName;
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
	 * Cleans object db entry from data which should not get persisted and
	 * adds data which should get persisted. 
	 * Sets archive state of object to 100 (archived according to storage policy).
	 * 
	 * @author Jens Peters
	 * @author Daniel M. de Oliveira
	 */
	private void prepareObjectForObjectDBStorage(Object obj) {

		for (Package pkg : obj.getPackages()){
			pkg.getEvents().clear();
			pkg.getFiles().clear();
		}
		
		obj.setObject_state(100);
		obj.setDate_modified(String.valueOf(new Date().getTime()));
		obj.setStatic_nondisclosure_limit(job.getStatic_nondisclosure_limit());
		obj.setDynamic_nondisclosure_limit(job.getDynamic_nondisclosure_limit());
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
		if (obj.isDelta())
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
			Mail.sendAMail(getSystemFromEmailAdress(), email, subject, msg);
		} catch (MessagingException e) {
			logger.error("Sending email reciept for " + objectIdentifier + " failed",e);
			return false;
		}
		} else logger.info(obj.getContractor().getShort_name() + " has no valid Email Adress!");
		
		return true;
	}

	@Override
	void rollback() {
		throw new NotImplementedException("No rollback implemented for this action");
	}


	public GridFacade getGridRoot() {
		return gridRoot;
	}


	public void setGridRoot(GridFacade gridRoot) {
		this.gridRoot = gridRoot;
	}

}
	
