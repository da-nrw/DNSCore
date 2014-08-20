/*
  DA-NRW Software Suite | ContentBroker
  Copyright (C) 2014 LVRInfoKom
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

package de.uzk.hki.da.cb;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang.NotImplementedException;
import org.jdom.JDOMException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import de.uzk.hki.da.core.ConfigurationException;
import de.uzk.hki.da.core.UserException;
import de.uzk.hki.da.repository.RepositoryException;
import de.uzk.hki.da.utils.C;

/**
 * Tests if a user has made a choice for a decision request issued automatically by the system.
 * 
 * @author Daniel M. de Oliveira
 */
public class ProcessUserDecisionsAction extends AbstractAction{

	static final Logger logger = LoggerFactory.getLogger(ProcessUserDecisionsAction.class);
	
	@Override
	void checkActionSpecificConfiguration() throws ConfigurationException {
		// Auto-generated method stub
	}

	@Override
	void checkSystemStatePreconditions() throws IllegalStateException {
		// Auto-generated method stub
	}

	@Override
	boolean implementation() throws FileNotFoundException, IOException,
			UserException, RepositoryException, JDOMException,
			ParserConfigurationException, SAXException {
		
		if (job.getAnswer().isEmpty()) {
			return false;
		} 
		else if (job.getAnswer().equals(C.YES)){
		} 
		else {
			job.getConversion_instructions().clear();
		}
		this.setEndStatus(C.INGEST_REGISTER_URN_ACTION_START_STATUS);
		return false;
	}

	@Override
	void rollback() throws Exception {
		throw new NotImplementedException("rollback not yet implemented");
	}
}