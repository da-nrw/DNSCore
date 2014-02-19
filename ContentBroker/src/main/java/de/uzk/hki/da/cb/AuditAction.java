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

	
	private String nodeAdminEmail ="";
	private int minNodes = 3;
	private GridFacade gridRoot;
	private int errorState = 51;
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
		
		
		for (Package pack : object.getPackages()) {
			String pname = pack.getName();
			if (pname.equals("")) pname = "1";

			String logicalPath = "/aip/"+object.getContractor().getShort_name() + "/" +object.getIdentifier() +"/"+
					object.getIdentifier() + ".pack_"+pname+".tar";

			String msg= "";
			if (gridRoot.isValid(logicalPath)) {
				logger.info(logicalPath + " is valid!");
				if (!gridRoot.storagePolicyAchieved(logicalPath, sp)) {
					object.setObject_state(errorState);
					msg += "\nAnzahl der geforderten Replikationen für das Datenpaket "+logicalPath +" auf LZA Medien wurde nicht erreicht! Soll: " + minNodes +"\n";
				}
				if (!msg.equals("")) {
					informNodeAdmin(logicalPath, msg);
				} else {
					logger.debug("settin object state to 100");
					object.setObject_state(100);
				}
				
			} else {
				logger.error(logicalPath + " is invalid!");
				unloadAndRepair(pack, job, object);
				object.setObject_state(errorState);
				msg = "Das gespeicherte Datenpaket \""+ logicalPath + "\" hat mindestens eine kaputte Replikation. " +
				"Bitte prüfen Sie das entsprechende Datenpaket und die Medien auf denen es liegt!\n\n";
			}
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

		String subject = "[DA-NRW] Problem Report für " + logicalPath;
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
	
	
	
	void unloadAndRepair(Package pack, Job job, Object obj) {
		// unload and Deep Check & repair Package on node
		//Todo
	}
	
	public void setObjectState(Job job, int state) {
		
//		if (dao==null) throw new IllegalStateException("centralDatabaseDAO not set");
		
//		HibernateUtil.getThreadBoundSession().refresh(object);
		
		if (object!=null) { // TODO reconsider
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
}
