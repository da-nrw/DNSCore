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

import de.uzk.hki.da.action.AbstractAction;
import de.uzk.hki.da.core.C;
import de.uzk.hki.da.core.ConfigurationException;
import de.uzk.hki.da.core.UserException;
import de.uzk.hki.da.repository.RepositoryException;
import de.uzk.hki.da.utils.Utilities;

/**
 * Tests if a user has made a choice for a decision request issued automatically by the system.
 * 
 * @author Daniel M. de Oliveira
 */
public class ProcessUserDecisionsAction extends AbstractAction{

	static final Logger logger = LoggerFactory.getLogger(ProcessUserDecisionsAction.class);
	
	@Override
	public void checkActionSpecificConfiguration() throws ConfigurationException {
		// Auto-generated method stub
	}

	@Override
	public void checkSystemStatePreconditions() throws IllegalStateException {
		// Auto-generated method stub
	}

	@Override
	public boolean implementation() throws FileNotFoundException, IOException,
			UserException, RepositoryException, JDOMException,
			ParserConfigurationException, SAXException {
		
		if (Utilities.isNotSet(job.getAnswer())){
			throw new IllegalStateException("job.getAnswer() must not be null or empty.");
		}
		
		else if (job.getAnswer().equals(C.ANSWER_YO)){
			logger.info("System Question: "+C.QUESTION_MIGRATION_ALLOWED+" User response: "+C.ANSWER_YO);
		} 
		else {
			logger.info("System Question: "+C.QUESTION_MIGRATION_ALLOWED+" User response: "+C.ANSWER_NO);
			logger.trace("will delete conversion instructions for long term preservation now");
			job.getConversion_instructions().clear();
		}
		this.setEndStatus(C.WORKFLOW_STATUS_START___INGEST_REGISTER_URN_ACTION);
		return true;
	}

	
	
	
	
	@Override
	public void rollback() throws Exception {
		throw new NotImplementedException("rollback not yet implemented");
	}
}