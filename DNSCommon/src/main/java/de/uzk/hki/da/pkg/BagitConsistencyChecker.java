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

package de.uzk.hki.da.pkg;

import gov.loc.repository.bagit.Bag;
import gov.loc.repository.bagit.BagFactory;
import gov.loc.repository.bagit.utilities.SimpleResult;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * The Class BagitConsistencyChecker.
 */
public class BagitConsistencyChecker implements ConsistencyChecker {
	
	/** The logger. */
	private static Logger logger = LoggerFactory.getLogger(BagitConsistencyChecker.class);
	
	/** The package path. */
	String packagePath;
	
	/** The messages. */
	List<String> messages;

	/**
	 * Instantiates a new bagit consistency checker.
	 *
	 * @param packagePath the package path
	 */
	public BagitConsistencyChecker(String packagePath) {
		this.packagePath = packagePath;
		messages = new ArrayList<String>();
	}

	/* (non-Javadoc)
	 * @see de.uzk.hki.da.utils.ConsistencyChecker#checkPackage()
	 */
	public boolean checkPackage() {
		
		logger.debug("Starting BagIt consistency check.");
		
		BagFactory bagFactory = new BagFactory();
		Bag bag = bagFactory.createBag(new File(packagePath));
		SimpleResult result = bag.verifyValid();
		messages = result.getMessages();
		
		logger.debug("verifyPayloadManifest returned: " + bag.verifyPayloadManifests().isSuccess());
		logger.debug("verifyComplete returned: " + bag.verifyComplete().isSuccess());
		logger.debug("verifyTagManifests returned: " + bag.verifyTagManifests().isSuccess());
		logger.debug("verifyValid returned: " + bag.verifyValid().isSuccess());
		
		return result.isSuccess();
	}

	/* (non-Javadoc)
	 * @see de.uzk.hki.da.utils.ConsistencyChecker#getMessages()
	 */
	public List<String> getMessages() {
		return messages;
	}

}
