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

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.loc.repository.bagit.domain.Bag;
import gov.loc.repository.bagit.reader.BagReader;
import gov.loc.repository.bagit.verify.BagVerifier;



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
		BagVerifier sut = new BagVerifier();
		BagReader reader = new BagReader();
		Bag bagVer;
		try {
			bagVer = reader.read(Paths.get(packagePath));
			sut.isValid(bagVer, false);
			//sut.isComplete(bagVer, false); isValid do it already

		} catch (Exception e) {
			e.printStackTrace();
			messages = Arrays.asList(e.getMessage());
			logger.debug("BagIt verification failed: " + e.toString());
			return false;
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see de.uzk.hki.da.utils.ConsistencyChecker#getMessages()
	 */
	public List<String> getMessages() {
		return messages;
	}

}
