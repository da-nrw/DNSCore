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

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.NotImplementedException;

import de.uzk.hki.da.action.AbstractAction;
import de.uzk.hki.da.core.ConfigurationException;
import de.uzk.hki.da.grid.GridFacade;
import de.uzk.hki.da.model.Job;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.model.Package;
import de.uzk.hki.da.model.StoragePolicy;
import de.uzk.hki.da.service.MailContents;

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

	private int timeOut = 4000;
	
	private GridFacade gridRoot;

	public ArchiveReplicationCheckAction(){SUPPRESS_OBJECT_CONSISTENCY_CHECK=true;}
	
	@Override
	public void checkActionSpecificConfiguration() throws ConfigurationException {
		if (getGridRoot()==null) throw new ConfigurationException("gridRoot not set");
	}

	@Override
	public void checkSystemStatePreconditions() throws IllegalStateException {
		// Auto-generated method stub
	}

	/**
	 * @throws IOException 
	 */
	@Override
	public
	boolean implementation() throws IOException {
		setKILLATEXIT(true);

		StoragePolicy sp = new StoragePolicy(localNode);
		sp.setMinNodes(preservationSystem.getMinRepls());
		do{
			delay();
		}
		while (!gridRoot.storagePolicyAchieved(
				object.getContractor().getShort_name() + 
				"/" + object.getIdentifier() + "/"+ object.getIdentifier() + ".pack_" + object.getLatestPackage().getName() + ".tar", 
				sp));
		
		prepareObjectForObjectDBStorage(object);
		new MailContents(preservationSystem,localNode).sendReciept(job, object);
		
		toCreate = createPublicationJob(job);
		FileUtils.deleteDirectory(object.getPath().toFile());
		return true;
	}
	
	
	@Override
	public void rollback() {
		throw new NotImplementedException("No rollback implemented for this action");
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
				preservationSystem.getPresServer()+" for possible publication of this object.");
		result = new Job (parent, "540");
		result.setResponsibleNodeName(preservationSystem.getPresServer());
		result.setObject(getObject());
		result.setDate_created(String.valueOf(new Date().getTime()/1000L));
		
		return result;
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
	
	
	
	public GridFacade getGridRoot() {
		return gridRoot;
	}


	public void setGridRoot(GridFacade gridRoot) {
		this.gridRoot = gridRoot;
	}
}
	
