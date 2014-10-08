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
package de.uzk.hki.da.service;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.uzk.hki.da.core.URNCheckDigitGenerator;


/**
 * The Class URNCheckDigitGeneratorTests.
 */
public class URNCheckDigitGeneratorTests {

	/**
	 * Testcheck digit generation.
	 */
	@Test
	public void testcheckDigitGeneration() {
		
		URNCheckDigitGenerator generator = new URNCheckDigitGenerator();
		assertEquals("5", generator.checkDigit("urn:nbn:de:gbv:089-332175294"));
		assertEquals("2", generator.checkDigit("urn:nbn:de:danrw-1-2013102517743"));
		
	}
	
}
