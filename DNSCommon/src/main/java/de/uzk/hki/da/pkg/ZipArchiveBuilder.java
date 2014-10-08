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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uzk.hki.da.utils.CommandLineConnector;
import de.uzk.hki.da.utils.ProcessInformation;


public class ZipArchiveBuilder implements ArchiveBuilder {

	protected Logger logger = LoggerFactory.getLogger(this.getClass().getName());
	
	public final String unzipPath="/usr/bin/jar";
	
	ZipArchiveBuilder(){
		// TODO check if (!new File(unzipPath).exists()) throw new IllegalStateException(unzipPath+" not existing");
	}
	
	/**
	 * WARN this is actually implemented with /usr/bin/jar instead of /usr/bin/unzip.
	 * See implementation notes at {@link de.uzk.hki.da.pkg.ArchiveBuilder#unarchiveFolder(File, File)}.
	 */
	public void unarchiveFolder(File srcTar, File destFolder)
	throws Exception{

		logger.debug("moving {} to folder {}",srcTar,destFolder);
        FileUtils.copyFileToDirectory(
                    srcTar,
                    destFolder,false );

        ProcessInformation pi = CommandLineConnector.runCmdSynchronously(new String[] {
                "/usr/bin/jar", "-xf", FilenameUtils.getName(srcTar.getAbsolutePath()) },
                destFolder);
        
        if ((pi==null)||(pi.getExitValue()!=0)){
            if (pi!=null) logger.error(pi.getStdErr());
            throw new RuntimeException("Couldnt unpack package");
        }

        new File(destFolder.getAbsolutePath() + "/" + FilenameUtils.getName(srcTar.getAbsolutePath())).delete();
	}
	
	public void archiveFolder(File srcFolder, File destFile, boolean includeFolder)
	throws Exception {

		FileOutputStream fileWriter = new FileOutputStream(destFile);
		ZipOutputStream zip = new ZipOutputStream(fileWriter);

		addFolderToArchive("", srcFolder, zip, includeFolder);
	
		zip.close();
		fileWriter.close();
	}

	private void addFileToArchive(String path, File srcFile, ZipOutputStream zip, boolean includeFolder)
	throws Exception {

		if (srcFile.isDirectory()) {
			addFolderToArchive(path, srcFile, zip, includeFolder);
		} else {
			byte[] buf = new byte[1024];
			int len;
			FileInputStream in = new FileInputStream(srcFile);
			zip.putNextEntry(new ZipEntry(path + "/" + srcFile.getName()));
			while ((len = in.read(buf)) > 0) {
				zip.write(buf, 0, len);
			}
			in.close();
		}
		
		zip.flush();
		
	}

	private void addFolderToArchive(String path, File srcFolder, ZipOutputStream zip, boolean includeFolder)
	throws Exception {
		
		for (String fileName : srcFolder.list()) {
			if (path.equals("")) {
				if (includeFolder)
					addFileToArchive(srcFolder.getName(), new File(srcFolder + "/" + fileName), zip, includeFolder);
				else
					addFileToArchive(".", new File(srcFolder + "/" + fileName), zip, includeFolder);
			} else {
				addFileToArchive(path + "/" + srcFolder.getName(), new File(srcFolder + "/" + fileName), zip, includeFolder);
			}
		}
		
	}

}
