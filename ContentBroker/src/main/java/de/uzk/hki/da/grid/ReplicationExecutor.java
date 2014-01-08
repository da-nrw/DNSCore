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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	
	/** The logger. */
	private static Logger logger = LoggerFactory
			.getLogger(ReplicationExecutor.class);
	
	private int retries=3;
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
	public ReplicationExecutor(IrodsSystemConnector isc, String srcResc, List<String> targetResgroups, String data_name){
		this.isc = isc;
		this.srcResc = srcResc;
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
			int i =0;
		
				while (!replicate(data_name, targetResgroup, srcResc) && i<retries){
					i++;
					try {
						Thread.sleep(10000l);
					} catch (InterruptedException e) {
						logger.error("failed replication thread " + e.getCause());
					}
				}

				// TODO: this has to be send to someone!
				if (i>=retries) logger.error("Failed to replicate :" + data_name + " giving up on node "+ targetResgroup );
		}
		logger.debug("deleting cache on resc " +srcResc );
		isc.trimResource(data_name, srcResc);
}	
	
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

