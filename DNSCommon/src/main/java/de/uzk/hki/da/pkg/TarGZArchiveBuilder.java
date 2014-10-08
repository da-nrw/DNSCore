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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TarGZArchiveBuilder implements ArchiveBuilder {

	TarGZArchiveBuilder(){}
	
	static final Logger logger = LoggerFactory.getLogger(TarGZArchiveBuilder.class);
	
	public void unarchiveFolder(File srcTar, File destFolder) throws Exception {
		
		FileInputStream fin = new FileInputStream(srcTar);
		BufferedInputStream in = new BufferedInputStream (fin); 
		
		
		GzipCompressorInputStream gzIn = new GzipCompressorInputStream(in);
		TarArchiveInputStream tIn = new TarArchiveInputStream(gzIn);

		TarArchiveEntry entry;
		do{
			entry = tIn.getNextTarEntry();
			if (entry==null) break;
			logger.debug(entry.getName());
			
			File entryFile = new File(destFolder.getAbsolutePath()+"/"+entry.getName());
			if (entry.isDirectory()) 
				entryFile.mkdirs();
			else {
				new File(entryFile.getAbsolutePath().substring(0, entryFile.getAbsolutePath().lastIndexOf('/'))).mkdirs();

				FileOutputStream out = new FileOutputStream(entryFile);
				IOUtils.copy(tIn, out);
				out.close();
			}
		}while(true);
		
		tIn.close();
		gzIn.close();
		in.close();
		fin.close();
	}

	public void archiveFolder(File srcFolder, File destFile, boolean includeFolder)
			throws Exception {

		FileOutputStream fOut = null;
		BufferedOutputStream bOut = null;
		GzipCompressorOutputStream gzOut = null;
		TarArchiveOutputStream tOut = null;
		
		try {
			fOut = new FileOutputStream(destFile);
			bOut = new BufferedOutputStream(fOut);
			gzOut = new GzipCompressorOutputStream(bOut);
			tOut = new TarArchiveOutputStream(gzOut);
			
			tOut.setLongFileMode(TarArchiveOutputStream.LONGFILE_GNU);
			tOut.setBigNumberMode(2);
			
			if (includeFolder)
				addFileToTarGZ(tOut,srcFolder,"");
			else
			{
				File children[] = srcFolder.listFiles();
				for (int i = 0; i < children.length; i++) {
					addFileToTarGZ(tOut,children[i],"");
				}
			}
		} finally
		{
			tOut.finish();
			
			tOut.close();
			gzOut.close();
			bOut.close();
			fOut.close();
		}
	}
	
	/**
	 * @param tOut
	 * @param the actual file that should be added
	 * @param base
	 * @throws IOException
	 */
	private void addFileToTarGZ(TarArchiveOutputStream tOut, File file, String base) throws IOException{
		
		String entryName = base + file.getName();
		logger.debug("addFileToTarGZ: "+entryName);
		
		TarArchiveEntry entry = (TarArchiveEntry) tOut.createArchiveEntry(file,entryName);
		tOut.putArchiveEntry(entry);
		
		if (file.isFile()){
			
			FileInputStream fis = new FileInputStream(file);
			IOUtils.copy(fis, tOut);
			tOut.closeArchiveEntry();
			fis.close();
		}
		
		if (file.isDirectory()){
			
			
			tOut.closeArchiveEntry();
			
			File children[] = file.listFiles();
			if (children==null) return;
			
			for (int i = 0; i < children.length; i++) {

				addFileToTarGZ(tOut,children[i],entryName+"/");
			}
		}
	}
	
}
