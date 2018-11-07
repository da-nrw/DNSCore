/*
  DA-NRW Software Suite | ContentBroker
  Copyright (C) 2015 LVRInfoKom
  Landschaftsverband Rheinland

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

/**
 * Creates user defined status report based on given CSV File stored in incoming folder
 * @author jens Peters
 */
package de.uzk.hki.da.event;

import java.io.File;
import java.io.FilenameFilter;

import de.uzk.hki.da.service.CSVQueryHandler;
import de.uzk.hki.da.utils.Path;

/**
 * 
 * @author Eugen Trebunski
 * Creates status reports based on CSV files found in incoming folder
 * 
 * Example Event creation by SQL
 * insert into systemevent (id,user_id, type,node_id) values (757,1,'AutomaticStatusReportEvent',2);
 *
 */
public class AutomaticStatusReportEvent extends CreateStatusReportEvent {
	public static final int DELAY_TIME = 60;

	public AutomaticStatusReportEvent(){
		setkILLATEXIT(false);
	}
	
	@Override
	public boolean implementation()  {
		File[] files = pathToReportIncoming().toFile().listFiles(csvFilter);
		logger.debug("looking in " + pathToReportIncoming().toFile());
		
		if (files!=null && files.length>0){
			logger.debug(files.length+" Reportfile(s) found, wait " + DELAY_TIME + " sek ");
			delay();

			for (int i = 0; i < files.length; i++) {
				if (!Path.makeFile(pathToReportIncoming(), files[i].getName()).exists() // If Inp-Fil is already served
						|| Path.makeFile(pathToReportIncoming(), files[i].getName()).isDirectory())
					continue;
				try {
					CSVQueryHandler sr = new CSVQueryHandler(node.getName(), owner.getId());
					logger.debug("working on " + Path.makeFile(pathToReportIncoming(), files[i].getName()));
					sr.generateReportBasedOnFile(Path.makeFile(pathToReportIncoming(), files[i].getName()),
							Path.makeFile(pathToReportOutgoing(), files[i].getName()));
				} catch (org.supercsv.exception.SuperCsvException ex) {
					logger.error("Found parsing exception in " + files[i].getName() + " " + ex.getMessage());
				}
			}
		}
		return true;
	}
	
	private void delay(){
		try {
			Thread.sleep(DELAY_TIME*1000); 
		} catch (InterruptedException e) {
			e.printStackTrace();
			logger.debug("Exception in PostRetrievalAction: " + e.getMessage());
		}
	}
}
