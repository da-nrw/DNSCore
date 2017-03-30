/*
  DA-NRW Software Suite | ContentBroker
  Copyright (C) 2013 Historisch-Kulturwissenschaftliche Informationsverarbeitung
  Universität zu Köln, 2014 LVR InfoKom

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

/**
 * Uses iRODs irsync to copy files to secondary nodes
 * 
 * @author Jens Peters
 */
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uzk.hki.da.model.CopyJob;

public class IrodsCopyExecutor implements JobExecutor {

	private IrodsCommandLineConnector iclc = null;
		
	/** The logger. */
	private static Logger logger = LoggerFactory
			.getLogger(IrodsCopyExecutor.class);
	
	public IrodsCopyExecutor() {
		iclc = new IrodsCommandLineConnector();
	}
	
	private String dirPrefix;
			
	public boolean execute(CopyJob cj ) {
			logger.debug("starting Syncing ... ");
			if (cj==null) {
				logger.error("CopyJob is null");
				return false;
			}
			
			if (dirPrefix== null) {
				logger.error("dirPrefix is null");
				return false;
			}
			
			String targetDir =  FilenameUtils.getFullPath("/" +cj.getDest_name() + "/"+dirPrefix + cj.getAipGridPath());
			if (!iclc.exists(targetDir)){
				logger.info("Creating " + targetDir + " now");
				iclc.mkCollection(targetDir);
			}
			logger.debug("iRSYNC "+cj.getSource()+" & "+targetDir + " with " + cj.getParams() );
			try {
			String out = iclc.rsync(cj.getSource(), targetDir, cj.getParams());
			logger.debug(out);
			} catch (RuntimeException irex) {
				logger.error(irex.getMessage());
				return false;
			}
			return true;
	}

	public String getDirPrefix() {
		return dirPrefix;
	}

	public void setDirPrefix(String dirPrefix) {
		this.dirPrefix = dirPrefix;
	}
	

}
