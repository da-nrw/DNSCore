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
import java.util.Date;

import org.apache.commons.lang.NotImplementedException;

import de.uzk.hki.da.action.AbstractAction;
import de.uzk.hki.da.core.ConfigurationException;

/**
 * 
 * @author Jens Peters
 * Deletes objects already retrieved by Contentbroker and read by user (via daweb) 
 * on the given outgoing path
 *
 */

public class PostRetrievalAction extends AbstractAction {

	public PostRetrievalAction(){SUPPRESS_OBJECT_CONSISTENCY_CHECK = true;}
	
	@Override
	public void checkActionSpecificConfiguration() throws ConfigurationException {
		// Auto-generated method stub
	}

	@Override
	public void checkSystemStatePreconditions() throws IllegalStateException {
		// Auto-generated method stub
	}

	@Override
	public boolean implementation() {
		setKILLATEXIT(true);
		
		String csn=object.getContractor().getShort_name();
		String mergeTarName = object.getIdentifier() + ".tar";
		
		String transferAreaRootPath = localNode.getUserAreaRootPath().toString();
		if ((new Date().getTime())/1000L > (Long.parseLong(job.getDate_created())+(86400L*1))){
			
			if (transferAreaRootPath!= null && !transferAreaRootPath.equals("")) {
			String webDavOutgoingPath = transferAreaRootPath +"/"+ csn +"/outgoing/";
			
			File toDel =  new File(webDavOutgoingPath + mergeTarName);
			if (toDel.exists()) {
				logger.debug("Deleting  " +webDavOutgoingPath + mergeTarName);
				toDel.delete(); 
			} else logger.info("Called to Delete  " +webDavOutgoingPath + mergeTarName + " but file doesn't exist!");
		} else logger.info("Deletion on webdav folder not possible. Path is not configured. ");
		} else {
			logger.info("Deletion skipped  " + mergeTarName + " is still to young"); 
			return false;
		}
		
		return true;
	}

	@Override
	public void rollback() throws Exception {
		throw new NotImplementedException("No rollback implemented for this action");
	}
}
