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
package de.uzk.hki.da.format;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.uzk.hki.da.utils.Path;
import de.uzk.hki.da.utils.SimplifiedCommandLineConnector;
import de.uzk.hki.da.utils.CommandLineConnector;
import de.uzk.hki.da.utils.ProcessInformation;
import de.uzk.hki.da.utils.TC;

/**
 * @author Daniel M. de Oliveira
 */
public class FidoCLITest {

	Path BASE_DIR = Path.make(TC.TEST_ROOT_FORMAT,"FidoCLITest");
	
	@Before
	public void setUp() throws IOException{
		FileUtils.copyFileToDirectory(new File("src/main/bash/configure.sh"), new File("./"));
		FileUtils.copyFileToDirectory(new File("src/main/bash/fido.sh"), new File("./"));
		FileUtils.copyFile(new File("src/main/conf/config.properties.dev"), new File("./conf/config.properties"));
		FileUtils.copyDirectoryToDirectory(new File("../3rdParty/fido"), new File("./"));
		new File("fido.sh").setExecutable(true);
		new File("configure.sh").setExecutable(true);
		new SimplifiedCommandLineConnector().execute(new String[]{"./configure.sh"});
	}
	
	@After 
	public void tearDown() throws IOException{
		new File("fido.sh").delete();
		new File("configure.sh").delete();
		new File("conf/config.properties").delete();
		FileUtils.deleteDirectory(new File("./fido/"));
	}
	
	@Test
	public void testMETS1(){
		ProcessInformation pi = CommandLineConnector.runCmdSynchronously(new String[]{"./fido.sh",
				BASE_DIR+"/mets_2_99.xml"});
		assertTrue(pi.getStdOut().endsWith("danrw-fmt/1"));
	}
	
	@Test
	public void testMETS2(){
		ProcessInformation pi = CommandLineConnector.runCmdSynchronously(new String[]{"./fido.sh",
				BASE_DIR+"/mets_2_998.xml"});
		assertTrue(pi.getStdOut().endsWith("danrw-fmt/1"));
	}
	
	@Test
	public void testEAD(){
		ProcessInformation pi = CommandLineConnector.runCmdSynchronously(new String[]{"./fido.sh",
				BASE_DIR+"/vda3.XML"});
		System.out.println(pi.getStdOut());
		assertTrue(pi.getStdOut().endsWith("danrw-fmt/2"));
	}
	
	/**
	 * @author Jens Peters
	 */
	@Test
	public void testEADFindbuchAusTestLVR(){
		ProcessInformation pi = CommandLineConnector.runCmdSynchronously(new String[]{"./fido.sh",
				BASE_DIR+"/Findbuch_EAD.xml"});
		System.out.println(pi.getStdOut());
		assertTrue(pi.getStdOut().endsWith("danrw-fmt/2"));
	}
}
