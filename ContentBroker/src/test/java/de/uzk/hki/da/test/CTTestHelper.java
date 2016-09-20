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

import de.uzk.hki.da.utils.C;
import de.uzk.hki.da.utils.CommandLineConnector;
import de.uzk.hki.da.utils.FolderUtils;

/**
 * @author Daniel M. de Oliveira
 */
public class CTTestHelper {

	public static void prepareWhiteBoxTest() throws IOException{
		new  CommandLineConnector().runCmdSynchronously(new String[] {
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
			Thread.sleep(2000);
		} catch (InterruptedException e) {
		}
	}
	
	public static void cleanUpWhiteBoxTest(){
		FolderUtils.deleteQuietlySafe(C.FIDO_INSTALLATION.toFile());
		FolderUtils.deleteQuietlySafe(new File(C.FIDO_GLUE_SCRIPT));
		FolderUtils.deleteQuietlySafe(C.CONF.toFile());
		FolderUtils.deleteQuietlySafe(new File("jhove"));
		FolderUtils.deleteQuietlySafe(new File(C.CONFIGURE_SCRIPT));
		FolderUtils.deleteQuietlySafe(new File("ContentBroker_start.sh.template"));
		FolderUtils.deleteQuietlySafe(new File("ContentBroker_stop.sh.template"));
		FolderUtils.deleteQuietlySafe(new File("cbTalk.sh"));
		FolderUtils.deleteQuietlySafe(new File("ffmpeg.sh"));
		FolderUtils.deleteQuietlySafe(new File("systemRules"));
		FolderUtils.deleteQuietlySafe(new File("activemq-data"));
	}
}
