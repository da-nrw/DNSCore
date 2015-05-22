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

package de.uzk.hki.da.grid;

import javax.mail.MessagingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.uzk.hki.da.model.StoragePolicy;
import de.uzk.hki.da.service.Mail;

/**
 * The Class FederationExecutor.
 *
 * @author Jens Peters
 */
public class FederationExecutor extends Thread {

	/** The isc. */
	private IrodsSystemConnector isc;

	/** The data_name. */
	private String data_name;
	
	private String destResc;
	
	private long timeout = 1200000l;
	
	private int retries=3;
	
	private StoragePolicy sp;
	
	public int getRetries() {
		return retries;
	}

	public String getDestResc() {
		return destResc;
	}

	public void setDestResc(String destResc) {
		this.destResc = destResc;
	}

	public void setRetries(int retries) {
		this.retries = retries;
	}

	public long getTimeout() {
		return timeout;
	}

	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}

	/** The logger. */
	private static Logger logger = LoggerFactory
			.getLogger(FederationExecutor.class);
	
	
	/**
	 * Instantiates a new Federation executor.
	 *
	 * @param isc the isc
	 * @param srcResc the src resc
	 * @param data_name the data_name
	 * @author Jens Peters
	 */
	public FederationExecutor(IrodsSystemConnector isc, StoragePolicy sp, String data_name){
		this.isc = isc;
		this.sp = sp;
		this.data_name = data_name;
		this.destResc = sp.getCommonStorageRescName();
	}
	
	/**
	 * Runs the Federation to nodes
	 *
	 * @author Jens Peters
	 */
	@Override
	public void run() {
		logger.trace("run....");
		if (destResc==null) {
			logger.error("dest Resc is null!");
			throw new RuntimeException("federation destResc is null");
		}
		if (sp.getAdminEmail()==null) {
			logger.error("node Admin is null!");
			throw new RuntimeException("node Admin is null!");
		}
		int i = 0;
		while (i<retries && !federate(data_name)){
				try {
					logger.debug("Going to sleep for " + timeout + " millis");
					Thread.sleep(timeout);
				} catch (InterruptedException e) {
					logger.error("failed wakeup federation thread " + e.getCause());
				}
				i++;
		}
		// We have passed the retries, we try to send Email to node admin
		if (i==retries) {
			String err = "Failed to federate :" + data_name + " giving up on node " + sp.getNodeName() + " , cooperateing servers offline?"; 
			if (sp.getAdminEmail()!=null && !sp.getAdminEmail().equals("")) {
				try {
					Mail.sendAMail(sp.getAdminEmail(), sp.getAdminEmail(), err , err);
				} catch (MessagingException ex) {
					logger.error("Sending Email failed " + ex.toString());
				}
			logger.error(err);
			}
		} 
}	
	/**
	 * Trying to federate
	 * Exception being catched due to having "normal" error of unreachable nodes.
	 * 
	 * @author Jens Peters
	 * @param data_name
	 * @return
	 */
	
private boolean federate (String data_name) {
	boolean ret = false;
	try {
		isc.establishConnect();

		ret = isc.federateDataObjectToConnectedZones(data_name, destResc, 3);
		isc.logoff();
	} catch (Exception e) {
		logger.error("Something went wrong calling federation rule " + e.getMessage() );
	} finally {
		isc.logoff();
	}
	return ret;
}
}

