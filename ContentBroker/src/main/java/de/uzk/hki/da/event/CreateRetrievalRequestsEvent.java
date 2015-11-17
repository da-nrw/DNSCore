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
 * Creates Retrieval requests based on SystemEvent
 * @author Jens Peters
 * 
 *
 */
public class CreateRetrievalRequestsEvent extends AbstractSystemEvent {

	private Path pathToReportIncoming() {
		return Path.make(
				node.getUserAreaRootPath(),
				owner.getShort_name(),
				"incoming"
				);
	}
	
	private Path pathToReportOutgoing() {
		return Path.make(
				node.getUserAreaRootPath(),
				owner.getShort_name(),
				"outgoing"
				);
	}
	FilenameFilter csvFilter = new FilenameFilter() {

	public boolean accept(File dir, String name) {
		return (name.toLowerCase().endsWith(".csv"));
	}
	};

	@Override
	public boolean implementation()  {
		File[] files = pathToReportIncoming().toFile().listFiles(csvFilter);
		logger.debug("looking in " + pathToReportIncoming().toFile());
		if (files!=null)
		for (int i=0;i<files.length;i++){
			if (Path.makeFile(pathToReportIncoming(),files[i].getName()).isDirectory())
				continue;
			try {
			CSVQueryHandler sr = new CSVQueryHandler(node.getName(),owner.getId());
			logger.debug("working on " + Path.makeFile(pathToReportIncoming(),files[i].getName()));
			sr.generateRetrievalRequests(Path.makeFile(pathToReportIncoming(),files[i].getName()),Path.makeFile(pathToReportOutgoing(),files[i].getName()));
			} catch (org.supercsv.exception.SuperCsvException ex) {
				logger.error("Found parsing exception in "+ files[i].getName() + " " + ex.getMessage());
			}
		}
	
		return true;
	}

}
