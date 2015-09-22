/*
  DA-NRW Software Suite | ContentBroker
  Copyright (C) 2014 LVR-InfoKom
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

package de.uzk.hki.da.utils;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * @author Daniel M. de Oliveira
 */
public class StringUtilitiesTest {
	
	@Test
	public void isNotSet() {
		assertTrue(StringUtilities.isNotSet(null));
		assertTrue(StringUtilities.isNotSet(""));
		assertFalse(StringUtilities.isNotSet("abc"));
		assertFalse(StringUtilities.isNotSet(new Boolean(true)));
	}
	
	@Test
	public void isSet() {
		assertFalse(StringUtilities.isSet(null));
		assertFalse(StringUtilities.isSet(""));
		assertTrue(StringUtilities.isSet("abc"));
		assertTrue(StringUtilities.isSet(new Boolean(true)));
	}
}
