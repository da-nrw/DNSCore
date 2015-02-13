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

/**
 * 
 * @author Jens Peters
 * Deletes objects already retrieved by Contentbroker and read by user (via daweb) 
 * on the given outgoing path
 *
 */

public class PostRetrievalAction extends AbstractAction {

	private int timeOut = 4000;
	private long days = 2;
	
	public PostRetrievalAction(){
		SUPPRESS_OBJECT_CONSISTENCY_CHECK=true;
		setKILLATEXIT(true);
	}
	
	@Override
	public void checkConfiguration() {
	}
	

	@Override
	public void checkPreconditions() {
	}
	
	@Override
	public boolean implementation() {
		
		String csn=o.getContractor().getShort_name();
		String mergeTarName = o.getIdentifier() + ".tar";
		
		String transferAreaRootPath = n.getUserAreaRootPath().toString();
		while (new Date().getTime()/1000L < (Long.parseLong(j.getDate_created())+(86400L*days))){
			delay();
		} 
		if ((new Date().getTime())/1000L > (Long.parseLong(j.getDate_created())+(86400L*days))){
			
			if (transferAreaRootPath!= null && !transferAreaRootPath.equals("")) {
				String webDavOutgoingPath = transferAreaRootPath +"/"+ csn +"/outgoing/";
				
				File toDel =  new File(webDavOutgoingPath + mergeTarName);
				if (toDel.exists()) {
					logger.debug("Deleting  " +webDavOutgoingPath + mergeTarName);
					toDel.delete(); 
				} else logger.info("Called to Delete  " +webDavOutgoingPath + mergeTarName + " but file doesn't exist!");
			} else logger.info("Deletion on webdav folder not possible. Path is not configured. ");
		modifyObject(o);
		} else {
			logger.info("Deletion skipped  " + mergeTarName + " is still to young"); 
			return false;
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
	private void modifyObject(Object obj) {
		o.setObject_state(100);
		o.setDate_modified(String.valueOf(new Date().getTime()));
	}

	@Override
	public void rollback() throws Exception {
	}
}
