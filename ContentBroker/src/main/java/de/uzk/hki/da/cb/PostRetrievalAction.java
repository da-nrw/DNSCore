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

import de.uzk.hki.da.action.AbstractAction;
import de.uzk.hki.da.core.PreconditionsNotMetException;
import de.uzk.hki.da.utils.C;
import de.uzk.hki.da.utils.Path;
import de.uzk.hki.da.utils.StringUtilities;

/**
 * 
 * @author Jens Peters
 * Deletes objects already retrieved by Contentbroker and read by user (via daweb) 
 * on the given outgoing path
 *
 */

public class PostRetrievalAction extends AbstractAction {

	private static final String OUTGOING = "outgoing";
	private int timeOut = 20000;
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
		
		if (StringUtilities.isNotSet(n.getUserAreaRootPath()))
			throw new PreconditionsNotMetException("Must be set: n.getUserAreaRootPath");
		if (! Path.makeFile(n.getUserAreaRootPath(),o.getContractor().getShort_name(),OUTGOING).exists())
			throw new PreconditionsNotMetException("Must exist: "+Path.makeFile(n.getUserAreaRootPath(),o.getContractor().getShort_name(),OUTGOING));
		
	}
	
	@Override
	public boolean implementation() {
		logger.debug("PostRetrievalAction called! ");
		Path outgoingFolder = Path.make(n.getUserAreaRootPath(), o.getContractor().getShort_name(), OUTGOING);
		File toDel = Path.makeFile(outgoingFolder, o.getIdentifier() + C.FILE_EXTENSION_TAR);
		if (!toDel.exists()) {// For the use case the File has been
								// moved/deleted manually
			logger.debug("Retrieval is already deleted by other software, modify Object status");
			modifyObject(o);
		} else {
			if (System.currentTimeMillis() / 1000 <= (Long.parseLong(j.getDate_created()) + (86400L * days))) {
				delay();
				logger.debug("CB not yet able to delete that item yet!");
				return false;
			} else {
				if (toDel.exists()) {
					logger.debug("Deleting  " + toDel);
					toDel.delete();
				} else
					logger.warn("Called delete but cannot execute. File does not exist: " + toDel);
				modifyObject(o);
			}
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
		// Do nothing.
	}
}
