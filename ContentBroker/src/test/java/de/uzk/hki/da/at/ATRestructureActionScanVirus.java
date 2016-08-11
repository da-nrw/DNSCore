/*
 DA-NRW Software Suite | ContentBroker
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

package de.uzk.hki.da.at;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.uzk.hki.da.utils.CommandLineConnector;
import de.uzk.hki.da.utils.ProcessInformation;

/**
 * <a href="../../../../src/main/markdown/feature_restructure_action_scan.md">Feature Description</a>
 * 
 * @author Gaby Bender
 */
public class ATRestructureActionScanVirus extends AcceptanceTest{

	private static File sourceDir = new File("src/test/resources/at/");
	private static ProcessInformation pi;
	
	@Before
	public void setUp() throws IOException{	
//		FileUtils.deleteDirectory(targetDir);
	}
	
	@After
	public void tearDown() throws IOException{
//		pi.destroy();
	}

	@Test
	public void testNoVirus() throws IOException {

		File source = new File(sourceDir, "ATRestructureActionScanVirus/noVirus");

 		pi = new CommandLineConnector().runCmdSynchronously(new String[] {
                "clamscan", "-r",
                source.getAbsolutePath()}, 0);
 		
 		assertTrue(pi.getExitValue() == 0);
	}
	
	@Test
	public void testVirus() throws IOException {

		File source = new File(sourceDir, "ATRestructureActionScanVirus/virus");

		pi = new CommandLineConnector().runCmdSynchronously(new String[] {
                "clamscan", "-r",
                source.getAbsolutePath()}, 0);

 		assertTrue(pi.getExitValue() == 1);
	}
	
	
}
