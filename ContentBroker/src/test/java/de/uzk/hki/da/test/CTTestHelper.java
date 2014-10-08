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

package de.uzk.hki.da.test;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import de.uzk.hki.da.core.C;
import de.uzk.hki.da.utils.CommandLineConnector;

/**
 * @author Daniel M. de Oliveira
 */
public class CTTestHelper {

	public static void prepareWhiteBoxTest() throws IOException{
		CommandLineConnector.runCmdSynchronously(new String[] {
                "src/main/bash/collect.sh", "./" });
		
		new File("conf").mkdir();
		FileUtils.copyFile(new File(TC.CONFIG_PROPS_CI), new File(C.CONFIG_PROPS));
		FileUtils.copyFile(new File("src/main/resources/healthCheck.tif"), new File("conf/healthCheck.tif"));
		FileUtils.copyFile(new File(TC.FIDO_SH_SRC), new File(C.FIDO_GLUE_SCRIPT));
		FileUtils.copyDirectory(new File("../3rdParty/fido/fido"), C.FIDO_INSTALLATION.toFile());
		FileUtils.copyFile(new File("src/main/bash/configure.sh"), new File(C.CONFIGURE_SCRIPT));
		
		new File(C.FIDO_GLUE_SCRIPT).setExecutable(true);
		new File(C.CONFIGURE_SCRIPT).setExecutable(true);
		
		Runtime.getRuntime().exec("./"+C.CONFIGURE_SCRIPT);
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
		}
	}
	
	public static void cleanUpWhiteBoxTest(){
		FileUtils.deleteQuietly(C.FIDO_INSTALLATION.toFile());
		FileUtils.deleteQuietly(new File(C.FIDO_GLUE_SCRIPT));
		FileUtils.deleteQuietly(C.CONF.toFile());
		FileUtils.deleteQuietly(new File("jhove"));
		FileUtils.deleteQuietly(new File(C.CONFIGURE_SCRIPT));
		FileUtils.deleteQuietly(new File("ContentBroker_start.sh.template"));
		FileUtils.deleteQuietly(new File("ContentBroker_stop.sh.template"));
		FileUtils.deleteQuietly(new File("cbTalk.sh"));
		FileUtils.deleteQuietly(new File("ffmpeg.sh"));
	}
}
