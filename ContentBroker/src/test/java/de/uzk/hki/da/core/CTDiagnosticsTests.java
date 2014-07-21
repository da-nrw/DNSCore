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

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.uzk.hki.da.utils.C;
import de.uzk.hki.da.utils.Path;
import de.uzk.hki.da.utils.RelativePath;
import de.uzk.hki.da.utils.TC;

/**
 * 
 * @author Daniel M. de Oliveira
 */
public class CTDiagnosticsTests {

	private static final String CONFIGURE_SH = "configure.sh";
	private static final String FIDO_SH = "fido.sh";
	private static final String FFMPEG_SH = "ffmpeg.sh";
	private static final File TEST_PACKAGE_SRC = Path.makeFile(TC.TEST_ROOT_AT,"AT_CON2.tgz");
	private static final File CI_DATABASE_CFG = new RelativePath("src","main","xml","hibernateCentralDB.cfg.xml.ci").toFile();
	private static final File CONFIGURE_SH_SRC = new RelativePath("src/main/bash/configure.sh").toFile();
	
	
	@Before
	public void setUp() throws IOException{
		C.CONF.toFile().mkdirs();
		FileUtils.copyFile(TC.CONFIG_PROPS_CI, C.CONFIG_PROPS);
		FileUtils.copyFile(TEST_PACKAGE_SRC, C.BASIC_TEST_PACKAGE);
		FileUtils.copyFile(CI_DATABASE_CFG, C.HIBERNATE_CFG);
		
		FileUtils.copyDirectoryToDirectory(new File("../3rdParty/fido"), new File("./"));

		FileUtils.copyFileToDirectory(new File("src/main/resources/healthCheck.avi"), C.CONF.toFile());
		FileUtils.copyFileToDirectory(new File("src/main/resources/healthCheck.tif"), C.CONF.toFile());
		FileUtils.copyFileToDirectory(new File("src/main/bash/fido.sh"), new File("./"));
		Runtime.getRuntime().exec("chmod 777 "+ FIDO_SH);
		FileUtils.copyFile(new File("src/main/bash/ffmpeg.sh.fake"), new File(FFMPEG_SH));
		Runtime.getRuntime().exec("chmod 777 "+FFMPEG_SH);
		FileUtils.copyFileToDirectory(CONFIGURE_SH_SRC, new File("./"));
		Runtime.getRuntime().exec("chmod 777 "+CONFIGURE_SH);
		Runtime.getRuntime().exec("./"+CONFIGURE_SH);
		
	}
	
	@After
	public void tearDown(){
		FileUtils.deleteQuietly(C.CONF.toFile());
		FileUtils.deleteQuietly(new File("fido"));
		FileUtils.deleteQuietly(new File(CONFIGURE_SH));
		FileUtils.deleteQuietly(new File(FIDO_SH));
		FileUtils.deleteQuietly(new File("ffmpeg.sh"));
	}
	
	
	@Test
	public void stubDiagnostics() throws IOException{
		
		assertEquals(new Integer(0),Diagnostics.run());
	}
}
