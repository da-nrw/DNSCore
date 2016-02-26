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

import de.uzk.hki.da.action.AbstractAction;
import de.uzk.hki.da.core.MailContents;
import de.uzk.hki.da.grid.GridFacade;
import de.uzk.hki.da.model.Copy;
import de.uzk.hki.da.model.Job;
import de.uzk.hki.da.model.Node;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.model.Package;
import de.uzk.hki.da.model.StoragePolicy;
import de.uzk.hki.da.util.ConfigurationException;
import de.uzk.hki.da.utils.C;
import de.uzk.hki.da.utils.Path;
import de.uzk.hki.da.utils.RelativePath;
import de.uzk.hki.da.utils.StringUtilities;

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

	public ArchiveReplicationCheckAction(){
		SUPPRESS_OBJECT_CONSISTENCY_CHECK=true;
		setKILLATEXIT(true);
	}
	
	@Override
	public void checkConfiguration() {
		if (getGridRoot()==null) throw new ConfigurationException("gridRoot");
	}
	

	@Override
	public void checkPreconditions() {
	}
	
	
	
	@Override
	public
	boolean implementation() throws IOException {

		StoragePolicy sp = new StoragePolicy();
		sp.setMinNodes(preservationSystem.getMinRepls());
		do{
			delay();
		}
		while (!gridRoot.storagePolicyAchieved(
				aipPath().toString(), 
				sp, o.getLatestPackage().getChecksum(), n.getCooperatingNodes()));
		
		if (StringUtilities.isSet(preservationSystem.getPresServer())) {
				toCreate=createPublicationJob(j,o,preservationSystem.getPresServer());
		} else {
			gridRoot.remove("/" + n.getIdentifier()+"/pips/public/");
			gridRoot.remove("/" + n.getIdentifier()+"/pips/institution/");
		}
		setObjectArchived();

		logger.debug("Delete object "+wa.objectPath().toFile()+" from WorkArea.");
		FileUtils.deleteDirectory(wa.objectPath().toFile());
		logger.debug("Delete object "+wa.replPath().toFile()+" from WorkArea.");
		FileUtils.deleteDirectory(wa.replPath().toFile());
		String filename = o.getIdentifier() + ".pack_" + o.getLatestPackage().getName() + ".tar";
		Path replFilePath = Path.make(n.getIdentifier(), "repl", o.getContractor().getShort_name(), filename);
		logger.debug("Delete object "+replFilePath.toString()+" from WorkArea.");
		if(!gridRoot.remove(replFilePath.toString())) {
			logger.error("Unable to remove the aip file from local repl directory");
		}
		
		new MailContents(preservationSystem,n).sendReciept(j, o);
		logger.debug("Successfully sent email.");
		return true;
	}	
	
	@Override
	public void rollback() {
		
		toCreate=null;
		unsetObjectArchived();
	}
	
	

	/**
	 * @author Daniel M. de Oliveira 
	 * @author Jens Peters
	 */
	static Job createPublicationJob(Job parent,Object o,String presServerName){
		
		Job result = new Job();
		
		result = new Job (parent, C.WORKFLOW_STATUS_START___FETCH_PIPS_ACTION);
		result.setResponsibleNodeName(presServerName);
		result.setObject(o);
		result.setDate_created(String.valueOf(new Date().getTime()/1000L));
		
		return result;
	}

	
	
	
	static void clearNonpersistentObjectProperties(Object o) {
		
		o.getDocuments().clear();
		for (Package pkg : o.getPackages()){
			pkg.getEvents().clear();
			pkg.getFiles().clear();
	
		}
	}

	
	
	
	/** 
	 * Cleans object db entry from data which should not get persisted and
	 * adds data which should get persisted. 
	 * Sets archive state of object to 100 (archived according to storage policy).
	 * 
	 * @author Jens Peters
	 * @author Daniel M. de Oliveira
	 */
	private void setObjectArchived() {
		
		clearNonpersistentObjectProperties(o);
		
		for (Node cn:n.getCooperatingNodes()) {
			logger.debug("Cooperating node "+cn);
			Copy copy = new Copy();
			copy.setPath(n.getIdentifier()+"/aip/"+aipPath().toString());
			cn.setCopyToSave(copy);
		}
		
		
		o.setLast_checked(new Date());
		o.setDate_modified(String.valueOf(new Date().getTime()));

		o.setStatic_nondisclosure_limit(j.getStatic_nondisclosure_limit());
		o.setDynamic_nondisclosure_limit(j.getDynamic_nondisclosure_limit());
		
		o.setStatic_nondisclosure_limit_institution(j.getStatic_nondisclosure_limit_institution());
		o.setDynamic_nondisclosure_limit_institution(j.getDynamic_nondisclosure_limit_institution());

		o.setObject_state(Object.ObjectStatus.InWorkflow); // object stays in workflow for publication
		o.setPublished_flag(C.PUBLISHEDFLAG_UNDEFINED);
	}

	
	
	
	private void unsetObjectArchived() {
		
		for (Node cn:n.getCooperatingNodes()) {
			cn.setCopyToSave(null);
		}
	
		o.setObject_state(Object.ObjectStatus.InWorkflow);
		o.setLast_checked(null);
		o.setDate_modified(null);
		o.setStatic_nondisclosure_limit(null);
		o.setDynamic_nondisclosure_limit(null);
	}
	
	
	
	
	private void delay(){
		try {
			Thread.sleep(timeOut); // to prevent unnecessary small intervals when checking
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private Path aipPath() {
		return new RelativePath(o.getContractor().getShort_name(), o.getIdentifier(), 
				o.getIdentifier() + ".pack_" + o.getLatestPackage().getName() + C.FILE_EXTENSION_TAR);
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

	public GridFacade getGridRoot() {
		return gridRoot;
	}


	public void setGridRoot(GridFacade gridRoot) {
		this.gridRoot = gridRoot;
	}
}
	
