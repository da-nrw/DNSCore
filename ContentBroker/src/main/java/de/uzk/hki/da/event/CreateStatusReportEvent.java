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
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.jdom.JDOMException;
import org.xml.sax.SAXException;

import de.uzk.hki.da.action.AbstractAction;
import de.uzk.hki.da.core.SubsystemNotAvailableException;
import de.uzk.hki.da.core.UserException;
import de.uzk.hki.da.model.User;
import de.uzk.hki.da.repository.RepositoryException;
import de.uzk.hki.da.service.CSVStatusReport;
import de.uzk.hki.da.util.Path;

public class CreateStatusReportEvent extends AbstractSystemEvent {

	private Path pathToReportInUserArea() {
		return Path.make(
				node.getUserAreaRootPath(),
				owner.getShort_name(),
				"incoming"
				);
	}
	FilenameFilter csvFilter = new FilenameFilter() {

	public boolean accept(File dir, String name) {
		return (name.endsWith(".csv")
				||name.endsWith(".CSV"));
	}
	};

	@Override
	public boolean implementation()  {
		File[] files = pathToReportInUserArea().toFile().listFiles(csvFilter);
		logger.debug("looking in " + pathToReportInUserArea().toFile());
		if (files!=null)
		for (int i=0;i<files.length;i++){
			if (Path.makeFile(pathToReportInUserArea(),files[i].getName()).isDirectory())
				continue;
			CSVStatusReport sr = new CSVStatusReport();
			logger.debug("working on " + Path.makeFile(pathToReportInUserArea(),files[i].getName()));
			sr.generateReportBasedOnFile(Path.makeFile(pathToReportInUserArea(),files[i].getName()));
		}
	
		return true;
	}

}
