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


import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.NotImplementedException;

import de.uzk.hki.da.action.AbstractAction;
import de.uzk.hki.da.grid.GridFacade;
import de.uzk.hki.da.model.StoragePolicy;
import de.uzk.hki.da.util.ConfigurationException;
import de.uzk.hki.da.util.Path;
import de.uzk.hki.da.utils.MD5Checksum;

/**
 * Does the decoupled and time based Archive Replication to the given minimum number of required nodes. 
 * @author Jens Peters
 */
public class ArchiveReplicationAction extends AbstractAction {
	
	public ArchiveReplicationAction(){SUPPRESS_OBJECT_CONSISTENCY_CHECK=true;}
	
	private GridFacade gridRoot;
	
	private int timeOut = 4000;
	
	@Override
	public void checkConfiguration() {
		if (gridRoot==null) throw new ConfigurationException("gridRoot");
	}

	@Override
	public void checkPreconditions() {
	}
	
	@Override
	public
	boolean implementation() {
		
		String filename = o.getIdentifier() + ".pack_" + o.getLatestPackage().getName() + ".tar";
		Path target = Path.make(o.getContractor().getShort_name(), o.getIdentifier(), filename);
		StoragePolicy sp = new StoragePolicy();
		sp.setGridCacheAreaRootPath(n.getGridCacheAreaRootPath().toString());
		sp.setMinNodes(preservationSystem.getMinRepls());
		sp.setForbiddenNodes(o.getContractor().getForbidden_nodes());
		sp.setReplDestinations(n.getReplDestinations());
		sp.setAdminEmail(n.getAdmin().getEmailAddress());
		sp.setNodeName(n.getName());
		sp.setCommonStorageRescName(n.getReplDestinations());
		Path aipFilePath = Path.make(n.getWorkAreaRootPath(), "work", o.getContractor().getShort_name(), filename);
		Path replFilePath = Path.make(n.getWorkAreaRootPath(), "repl", o.getContractor().getShort_name(), filename);
		try {
			
			File sourceFile = new File(aipFilePath.toString());
			
			String sourceFileMd5Checksum = MD5Checksum.getMD5checksumForLocalFile(new File(aipFilePath.toString()));
			logger.debug("Source file md5 checksum: "+sourceFileMd5Checksum);
			
			File replFile = new File(replFilePath.toString());
			FileUtils.copyFile(sourceFile, replFile);
			String replFileMd5Checksum = MD5Checksum.getMD5checksumForLocalFile(replFile);
			logger.debug("Replication file md5 checksum: "+replFileMd5Checksum);
			
			if(!sourceFileMd5Checksum.equals(replFileMd5Checksum)) {
				throw new RuntimeException("Unable to copy the object file to destination "+replFile.getPath());
			}
			
			if (!gridRoot.put(replFile, 
						target.toString(), sp, o.getLatestPackage().getChecksum())) {
				delay(); 
				return false;
			}
			
			gridRoot.distribute(n, replFile, target.toString(), sp);
			
			new File(aipFilePath.toString()).delete();
		} catch (IOException e) {
			throw new RuntimeException("Put to IRODS Datagrid failed! " + e.getMessage());
		}
		return true;
	}
	
	private void delay(){
		try {
			Thread.sleep(timeOut); // to prevent unnecessary small intervals when checking
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	@Override
	public void rollback() {
		throw new NotImplementedException("No rollback implemented for this action");
	}
	
	
	public GridFacade getGridRoot() {
		return gridRoot;
	}

	public void setGridRoot(GridFacade gridRoot) {
		this.gridRoot = gridRoot;
	}

	

	
}
