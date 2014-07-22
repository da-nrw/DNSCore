/*
  DA-NRW Software Suite | ContentBroker
  Copyright (C) 2014 LVRInfoKom
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

package de.uzk.hki.da.core;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.uzk.hki.da.utils.C;
import de.uzk.hki.da.utils.CommandLineConnector;
import de.uzk.hki.da.utils.Path;
import de.uzk.hki.da.utils.RelativePath;
import de.uzk.hki.da.utils.TC;

/**
 * 
 * @author Daniel M. de Oliveira
 */
public class CTDiagnosticsTests {

	// set path to logback xml before anything else happens
	static { 
		System.setProperty("logback.configurationFile", "src/main/xml/logback.xml.debug");
	}
	
	private static final File FIDO_DIR = new File("fido");
	private static final File JHOVE_DIR = new File("jhove");
	private static final File FFMPEG_SH_FAKE_SRC = new File("src/main/bash/ffmpeg.sh.fake");
	private static final String CHMOD_777 = "chmod 777 ";
	private static final File CONFIGURE_SH = new File("configure.sh");
	private static final File FIDO_SH = new File("fido.sh");
	private static final File FFMPEG_SH = new File("ffmpeg.sh");
	private static final File TEST_PACKAGE_SRC = Path.makeFile(TC.TEST_ROOT_AT,"AT_CON2.tgz");
	private static final File CI_DATABASE_CFG = new RelativePath("src","main","xml","hibernateCentralDB.cfg.xml.ci").toFile();
	
	
	@Before
	public void setUp() throws IOException{
		
		CommandLineConnector.runCmdSynchronously(new String[] {
                "src/main/bash/collect.sh", "./" });
		
		FileUtils.copyFile(TC.CONFIG_PROPS_CI, C.CONFIG_PROPS);
		FileUtils.copyFile(TEST_PACKAGE_SRC, C.BASIC_TEST_PACKAGE);
		FileUtils.copyFile(CI_DATABASE_CFG, C.HIBERNATE_CFG);

		FileUtils.copyFile(FFMPEG_SH_FAKE_SRC, FFMPEG_SH);
		Runtime.getRuntime().exec(CHMOD_777+FFMPEG_SH);
		
		Runtime.getRuntime().exec("./"+CONFIGURE_SH);
	}

	
	
	
	
	@After
	public void tearDown(){
		FileUtils.deleteQuietly(C.CONF.toFile());
		FileUtils.deleteQuietly(FIDO_DIR);
		FileUtils.deleteQuietly(JHOVE_DIR);
		FileUtils.deleteQuietly(CONFIGURE_SH);
		FileUtils.deleteQuietly(FIDO_SH);
		FileUtils.deleteQuietly(FFMPEG_SH);
	}
	
	
	@Test
	public void stubDiagnostics() throws IOException{
		
		assertEquals(new Integer(0),Diagnostics.run());
	}
}
