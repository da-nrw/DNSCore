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

package de.uzk.hki.da.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;

import org.junit.Before;
import org.junit.Test;


/**
 * The Class ActionCommunicatorServiceTests.
 *
 * @author Christian Weitz
 * @author Thomas Kleinke
 */
public class ActionCommunicatorServiceTests {

	/** The acs. */
	private ActionCommunicatorService acs;
	
	/** The input message1. */
	private String inputMessage1;
	
	/** The input message2. */
	private String inputMessage2;
	
	/** The input message3. */
	private String inputMessage3;
	
	/** The id1. */
	private int id1;
	
	/** The id2. */
	private int id2;
	
	/**
	 * Sets the up.
	 */
	@Before
	public void setUp() {
		
		acs = new ActionCommunicatorService();
		
		inputMessage1 = "1234567 Test";
		inputMessage2 = "7654321 Test Test";
		inputMessage3 = "7654567 Test Test Test";
		id1 = 1234567;
		id2 = 7654321;
	}
	
	/**
	 * Test object storage.
	 */
	@Test
	public void testObjectStorage() {
		
		acs.addDataObject(id1, "inputMessage1", inputMessage1);
		String outputMessage = (String) acs.extractDataObject(id1, "inputMessage1");
		
		assertEquals(inputMessage1, outputMessage);
		assertEquals(acs.extractDataObject(id1, "inputMessage1"), null);
	}
	
	/**
	 * Test serialization.
	 */
	@Test
	public void testSerialization() {
		
		acs.addDataObject(id1, "inputMessage1", inputMessage1);
		acs.addDataObject(id2, "inputMessage2", inputMessage2);
		acs.addDataObject(id2, "inputMessage3", inputMessage3);
		
		acs.serialize();
		acs.deserialize();
		
		String outputMessage1 = (String) acs.extractDataObject(id1, "inputMessage1");
		String outputMessage2 = (String) acs.extractDataObject(id2, "inputMessage2");
		String outputMessage3 = (String) acs.extractDataObject(id2, "inputMessage3");
		
		assertEquals(inputMessage1, outputMessage1);
		assertEquals(inputMessage2, outputMessage2);
		assertEquals(inputMessage3, outputMessage3);
		
		if (new File("actionCommunicatorService.recovery").exists())
			fail();
	}

}
