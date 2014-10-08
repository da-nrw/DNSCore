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

package de.uzk.hki.da.pkg;


import java.io.File;

import de.uzk.hki.da.utils.CommandLineConnector;
import de.uzk.hki.da.utils.ProcessInformation;





/**
 * 
 *
 */
public class TarArchiveBuilder implements ArchiveBuilder {
	
	public TarArchiveBuilder(){}
	
	/**
	 * Untars an archive to destFolder.
	 * tar is called with the --keep-old-files parameter.
	 * 
	 * @author daniel
	 * @param srcTar
	 * @param destFolder
	 */
	public void unarchiveFolder(File srcTar, File destFolder)
	throws Exception{
		
		ProcessInformation pi = CommandLineConnector.runCmdSynchronously(new String[]{
				"/bin/tar","xf",
				 srcTar.getAbsolutePath(),
				"-C",
				destFolder.getAbsolutePath(),
				"--keep-old-files"
				});
		
		if ((pi == null) || (pi.getExitValue() != 0)) {
			String msg = "Failed to unarchive file " + srcTar + " to folder " + destFolder;
			if (pi != null)
				msg += "\n" + pi.getStdErr();
			throw new RuntimeException(msg);				
		}
	}
	
	
	/**
	 * Tars a whole directory including its structure and its included files.
	 */
	public void archiveFolder(File srcFile, File destFile, boolean includeFolder)
	throws Exception {
		
		if (includeFolder)
		{
			String parent = srcFile.getParentFile().getAbsolutePath();
			ProcessInformation pi = CommandLineConnector.runCmdSynchronously(new String[]{
					"/bin/tar",
					"-C",
					parent,
					"-cf",
					destFile.getAbsolutePath(),
					srcFile.getName()
			});
			
			if ((pi == null) || (pi.getExitValue() != 0)) {
				String msg = "Failed to archive folder " + srcFile + " to file " + destFile;
				if (pi != null)
					msg += "\n" + pi.getStdErr();
				throw new RuntimeException(msg);				
			}
			
		}
		else
		{
			String folder = srcFile.getAbsolutePath();
			ProcessInformation pi = CommandLineConnector.runCmdSynchronously(new String[]{
					"/bin/tar",
					"-C",
					folder,
					"-cf",
					destFile.getAbsolutePath(),
					"."
			});
			
			if ((pi == null) || (pi.getExitValue() != 0)) {
				String msg = "Failed to archive folder " + srcFile + " to file " + destFile;
				if (pi != null)
					msg += "\n" + pi.getStdErr();
				throw new RuntimeException(msg);				
			}
		}
	}
}
