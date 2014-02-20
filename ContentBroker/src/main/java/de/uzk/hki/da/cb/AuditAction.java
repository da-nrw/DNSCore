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

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import javax.mail.MessagingException;

import org.apache.commons.lang.NotImplementedException;

import de.uzk.hki.da.grid.GridFacade;
import de.uzk.hki.da.model.Job;
import de.uzk.hki.da.model.Node;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.model.Package;
import de.uzk.hki.da.model.StoragePolicy;
import de.uzk.hki.da.service.Mail;

/**
 * Audit Action.
 * Asks Storage Layer about the data integrity of an Object 
 * informs Node Admin if an error is found
 *  
 * @author Jens Peters
 */

public class AuditAction extends AbstractAction {

	
	private String nodeAdminEmail;
	private int minNodes = 3;
	private GridFacade gridRoot;
	private int errorState = 51;
	private String systemName;
	/*
	 * 
	 * (non-Javadoc)
	 * @see de.uzk.hki.da.cb.AbstractAction#implementation()
	 */
	@Override
	boolean implementation() {
		setKILLATEXIT(true);
		
		Properties properties = null;
		InputStream in;
		try {
		
			in = new FileInputStream("conf/config.properties");
			properties = new Properties();
			properties.load(in);

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		
		setObjectState(job,50);
		
		StoragePolicy sp = new StoragePolicy(localNode);
		sp.setMinNodes(Integer.parseInt(((String)properties.getProperty("cb.min_repls"))));
		
		String msg= "";
		// TODO: refactor to same implementation IntegrityScanner uses
		boolean completelyValid = true; 
		for (Package pack : object.getPackages()) {
			String pname = pack.getName();
			if (pname.equals("")) pname = "1";
			String logicalPath = "/aip/"+object.getContractor().getShort_name() + "/" +object.getIdentifier() +"/"+
					object.getIdentifier() + ".pack_"+pname+".tar";
			
			if (!gridRoot.isValid(logicalPath)) {
				msg+="SEVERE FAULT " + logicalPath + " is not valid, Checksum could not be verified on all systems!\n";
				 completelyValid = false;
				continue;
			}
			if (!gridRoot.storagePolicyAchieved(logicalPath, sp)) {
				msg+="FAULT " + logicalPath + " has not achieved its replication policy!\n";
				completelyValid = false;
				continue;
			} 
		}
		if (completelyValid) {
			logger.debug("Object checked OK, setting object state to 100");
			object.setObject_state(100);
		} else {
			object.setObject_state(errorState);
			logger.error("Object " + object.getIdentifier()  + " has following errors :" +  msg);
			unloadAndRepair(object);
		}		
		return true;
	}

	@Override
	void rollback() throws Exception {
		throw new NotImplementedException("No rollback implemented for this action");
	}
	
	/**
	 * Informs the Node Admin about the problems being found
	 * 
	 * @param logicalPath
	 * @param msg
	 * @author Jens Peters
	 */
	
	void informNodeAdmin(String logicalPath, String msg) {
		// send Mail to Admin with Package in Error

		String subject = "[" + systemName + "] Problem Report für " + logicalPath;
		if (nodeAdminEmail != null && !nodeAdminEmail.equals("")) {
			try {
				Mail.sendAMail(nodeAdminEmail, subject, msg);
			} catch (MessagingException e) {
				logger.error("Sending email problem report for " + logicalPath + "failed");
			}
		} else {
			logger.warn("Node Admin has no valid Email address!");
		}
	}
	
	
	
	void unloadAndRepair( Object obj) {
		// TODO TBD unload and Deep Check & repair Package on node
	}
	
	public void setObjectState(Job job, int state) {	
		if (object!=null) { 
			object.setObject_state(state);
		}
		
	}

	/**
	 * @param nodeAdminEmail the nodeAdminEmail to set
	 */
	public void setNodeAdminEmail(String nodeAdminEmail) {
		this.nodeAdminEmail = nodeAdminEmail;
	}

	/**
	 * @return the nodeAdminEmail
	 */
	public String getNodeAdminEmail() {
		return nodeAdminEmail;
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

	public GridFacade getGridRoot() {
		return gridRoot;
	}

	public void setGridRoot(GridFacade gridRoot) {
		this.gridRoot = gridRoot;
	}

	/**
	 * @return the zoneName
	 */
	public String getSystemName() {
		return systemName;
	}

	/**
	 * @param zoneName the zoneName to set
	 */
	public void setSystemName(String zoneName) {
		this.systemName = zoneName;
	}
}
