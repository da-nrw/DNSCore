/*
  DA-NRW Software Suite | SIP-Builder
  Copyright (C) 2014 Historisch-Kulturwissenschaftliche Informationsverarbeitung
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

package de.uzk.hki.da.sb;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import de.uzk.hki.da.metadata.ContractRightsTests;
import de.uzk.hki.da.metadata.ContractSettingsTests;

/**
 * The collection of all our unit tests which should run pre and post commit.
 */
@RunWith(Suite.class)
@SuiteClasses({
	ArchiveBuilderTests.class,
	ContractRightsTests.class,
	ContractSettingsTests.class,
	CopyUtilityTests.class,
	SIPFactoryTests.class
})
public class SimpleSuite {

}
