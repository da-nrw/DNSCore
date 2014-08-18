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

import java.util.List;

import javax.mail.MessagingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uzk.hki.da.model.Node;
import de.uzk.hki.da.service.Mail;

/**
 * The Class ReplicationExecutor.
 *
 * @author Daniel M. de Oliveira
 * @author Jens Peters
 */
public class ReplicationExecutor extends Thread {

	/** The isc. */
	private IrodsSystemConnector isc;
	
	/** The src resc. */
	private String srcResc; 
	
	/** The target resgroups. */
	private List<String> targetResgroups;
	
	/** The data_name. */
	private String data_name;
	
	/** The node */
	private Node node;
	
	private long timeout = 20000l;
	
	private int retries=3;
	
	public int getRetries() {
		return retries;
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
			.getLogger(ReplicationExecutor.class);
	
	
	/**
	 * Instantiates a new replication executor.
	 *
	 * @param isc the isc
	 * @param srcResc the src resc
	 * @param targetResgroups the target resgroups
	 * @param data_name the data_name
	 * @author Daniel M. de Oliveira
	 * @author Jens Peters
	 */
	public ReplicationExecutor(IrodsSystemConnector isc, Node localnode, List<String> targetResgroups, String data_name){
		this.node = localnode;
		this.isc = isc;
		this.srcResc = localnode.getWorkingResource();
		this.targetResgroups = targetResgroups;
		this.data_name = data_name;
	}
	
	/**
	 * Runs the replication to nodes
	 *
	 * @author Daniel M. de Oliveira
	 * @author Jens Peters
	 */
	@Override
	public void run() {
		logger.trace("run....");
		
		for (String targetResgroup: targetResgroups) {
			logger.info("replicate to " + targetResgroup);
			targetResgroup = targetResgroup.trim();
			if (targetResgroup.startsWith("cp_")) targetResgroup = targetResgroup.substring(3);
			int i =1;
		
			while (!replicate(data_name, targetResgroup, srcResc) && i<retries){
					
					try {
						Thread.sleep(timeout);
					} catch (InterruptedException e) {
						logger.error("failed replication thread " + e.getCause());
					}
					i++;
					
			}
			// We have passed the retries, we try to send Email to node admin
			if (i>=retries) {
				
				String err = "Failed to replicate :" + data_name + " giving up on node "+ targetResgroup; 
				if (node.getAdmin().getEmailAddress()!=null && !node.getAdmin().getEmailAddress().equals("")) {
					try {
						Mail.sendAMail(node.getAdmin().getEmailAddress(), node.getAdmin().getEmailAddress(), err , err);
					} catch (MessagingException ex) {
						logger.error("Sending Email failed " + ex.toString());
					}
				logger.error("Failed to replicate :" + data_name + " giving up on node "+ targetResgroup );
				}
			} 
		}
		logger.debug("deleting cache on resc " +srcResc );
		isc.trimResource(data_name, srcResc);
}	
	
	/**
	 * Trying to Replicate
	 * Exception being catched due to having "normal" error of unreachable nodes.
	 * 
	 * @author Jens Peters
	 * @param data_name
	 * @param targetResgroup
	 * @param srcResc
	 * @return
	 */
	
private boolean replicate (String data_name, String targetResgroup, String srcResc) {
	try {
		if (!isc.isConnected()) isc.connect();
	isc.replicateDaoToResGroupSynchronously(data_name, targetResgroup, srcResc);	
	} catch (Exception e) {
		logger.error("Something went wrong in replicating to " +targetResgroup + " : " + e.getMessage() );
		return false;
	}
	return true;
}
}

