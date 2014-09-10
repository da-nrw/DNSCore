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
import static org.junit.Assert.*;

import org.junit.Test;

import de.uzk.hki.da.pkg.ConsistencyChecker;
import de.uzk.hki.da.pkg.MetsConsistencyChecker;



/**
 * The Class MetsConsistencyCheckerTest.
 */
public class MetsConsistencyCheckerTest {

	/** The Constant VALID_PACKAGE. */
	private static final String VALID_PACKAGE = "src/test/resources/cb/CheckConsistencyTests/mets_package_valid";
	
	/** The Constant INVALID_PACKAGE. */
	private static final String INVALID_PACKAGE = "src/test/resources/cb/CheckConsistencyTests/mets_package_invalid";
	
	/**
	 * Test check.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void testCheck() throws Exception {
		
		ConsistencyChecker checker = new MetsConsistencyChecker(VALID_PACKAGE);
		assertTrue(checker.checkPackage());
	
		checker = new MetsConsistencyChecker(INVALID_PACKAGE);
		assertFalse(checker.checkPackage());
		
		System.out.println("Messages:");
		for (String file : checker.getMessages()) {
			System.out.println(file);
		}
		
	}
}
