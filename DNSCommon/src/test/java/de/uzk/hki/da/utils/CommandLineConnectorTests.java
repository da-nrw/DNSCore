/*
 DA-NRW Software Suite | ContentBroker
 Copyright (C) 2013 Historisch-Kulturwissenschaftliche Informationsverarbeitung
 Universität zu Köln
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

import java.io.File;
import java.io.IOException;

import org.junit.Test;

/**
 * @author Daniel M. de Oliveira
 */
public class CommandLineConnectorTests {

	private static final String testfolder="src/test/resources/utils/CommandLineConnectorTests/";
	
	@Test
	public void programCannotRun() {
		
		try {
			CommandLineConnector.runCmdSynchronously(new String[] {"/hello"});
			fail();
		} catch (IOException expected) {}
	}
	
	@Test 
	public void validStringOnStdOut() {
		try {
			ProcessInformation pi=CommandLineConnector.runCmdSynchronously(new String[] {testfolder+"printhallo.sh"});
			assertEquals("hallo\n",pi.getStdOut());
		} catch (IOException e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testTimeout() {
		try {
			CommandLineConnector.runCmdSynchronously(new String[] {testfolder+"sleep20seconds.sh"},1000);
			fail();
		} catch (IOException expected) {}
	}
	
	@Test
	public void testWorkingDir() {
		try {
			CommandLineConnector.runCmdSynchronously(new String[] {"printhallo.sh"},new File(testfolder));
			fail();
		} catch (IOException e) {}
	}
}
