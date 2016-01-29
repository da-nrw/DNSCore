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

import org.jdom.JDOMException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import de.uzk.hki.da.action.AbstractAction;
import de.uzk.hki.da.core.PreconditionsNotMetException;
import de.uzk.hki.da.core.UserException;
import de.uzk.hki.da.repository.RepositoryException;
import de.uzk.hki.da.utils.C;
import de.uzk.hki.da.utils.StringUtilities;

/**
 * Tests if a user has made a choice for a decision request issued automatically
 * by the system.
 * 
 * @author Daniel M. de Oliveira
 */
public class ProcessUserDecisionsAction extends AbstractAction {

	static final Logger logger = LoggerFactory
			.getLogger(ProcessUserDecisionsAction.class);

	@Override
	public void checkConfiguration() {
	}

	@Override
	public void checkPreconditions() {
		if (StringUtilities.isNotSet(j.getAnswer())) {
			throw new PreconditionsNotMetException(
					"Must not be null or empty: j.getAnswer()");
		}
		if (!(j.getAnswer().equals(C.ANSWER_NO)
				|| j.getAnswer().equals(C.ANSWER_YO) || j.getAnswer().equals(
				C.ANSWER_TO)))
			throw new PreconditionsNotMetException(
					"Must be either YES or NO: job.getAnswer().");

	}

	@Override
	public boolean implementation() throws FileNotFoundException, IOException,
			UserException, RepositoryException, JDOMException,
			ParserConfigurationException, SAXException {

		if (j.getQuestion().equals(C.QUESTION_MIGRATION_ALLOWED)) {
			if (j.getAnswer().equals(C.ANSWER_YO)) {
				logger.info("System Question: " + C.QUESTION_MIGRATION_ALLOWED
						+ " User response: " + C.ANSWER_YO);
				this.setEndStatus(C.WORKFLOW_STATUS_START___INGEST_REGISTER_URN_ACTION);
			} else {
				logger.info("System Question: " + C.QUESTION_MIGRATION_ALLOWED
						+ " User response: " + j.getAnswer());
				logger.trace("will delete conversion instructions for long term preservation now");
				j.getConversion_instructions().clear();
				this.setEndStatus(C.WORKFLOW_STATUS_START___INGEST_REGISTER_URN_ACTION);
			}
		} 
		if (j.getQuestion().equals(C.QUESTION_STORE_ALLOWED_IPTC_ERROR)){
			if (j.getAnswer().equals(C.ANSWER_YO)) {
				logger.info("System Question: " + C.QUESTION_STORE_ALLOWED_IPTC_ERROR
						+ " User response: " + C.ANSWER_YO);
				o.getLatestPackage().setPruneExceptions(true);
				j.setQuestion(" ");
				this.setEndStatus(C.WORKFLOW_STATUS_START___RESTART_INGEST_WORKFLOW);
			} else {
				logger.info("System Question: " + C.QUESTION_STORE_ALLOWED_IPTC_ERROR
						+ " User response: " + j.getAnswer());
				this.setEndStatus(C.WORKFLOW_STATUS_START___REMOVE_FROM_WORKFLOW_ACTION);
			}
		}

		return true;
	}

	@Override
	public void rollback() throws Exception {
		// Nothing to do.
	}
}