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

package de.uzk.hki.da.archivers;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class NativeJavaTarArchiveBuilder implements ArchiveBuilder {

	public NativeJavaTarArchiveBuilder(){}
		
	static final Logger logger = LoggerFactory.getLogger(NativeJavaTarArchiveBuilder.class);
	
	@Override	
	public void unarchiveFolder(File srcTar, File destFolder) throws Exception {
		
		FileInputStream fin = new FileInputStream(srcTar);
		BufferedInputStream in = new BufferedInputStream (fin); 
		
		
		TarArchiveInputStream tIn = new TarArchiveInputStream(in);
		
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
		in.close();
		fin.close();
	}
	
	@Override
	public void archiveFolder(File srcFolder, File destFile, boolean includeFolder)
			throws Exception {
		
		FileOutputStream fOut = null;
		BufferedOutputStream bOut = null;
		TarArchiveOutputStream tOut = null;
		
		try {
			fOut = new FileOutputStream(destFile);
			bOut = new BufferedOutputStream(fOut);
			tOut = new TarArchiveOutputStream(bOut);
			
			tOut.setLongFileMode(TarArchiveOutputStream.LONGFILE_GNU);
			tOut.setBigNumberMode(2);
			
			if (includeFolder)
				addFileToTar(tOut,srcFolder,"");
			else
			{
				File children[] = srcFolder.listFiles();
				for (int i = 0; i < children.length; i++) {
					addFileToTar(tOut,children[i],"");
				}
			}
		} finally
		{
			tOut.finish();
			
			tOut.close();
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
	private void addFileToTar(TarArchiveOutputStream tOut, File file, String base) throws IOException{
		
		String entryName = base + file.getName();
		logger.debug("addFileToTar: "+entryName);
		
		TarArchiveEntry entry = (TarArchiveEntry) tOut.createArchiveEntry(file,entryName);
		tOut.putArchiveEntry(entry);
		
		if (file.isFile()){
			
			FileInputStream fileInputStream = new FileInputStream(file);
			IOUtils.copy(fileInputStream, tOut);
			fileInputStream.close();
			tOut.closeArchiveEntry();
			
		}
		
		if (file.isDirectory()){
			
			
			tOut.closeArchiveEntry();
			
			File children[] = file.listFiles();
			if (children==null) return;
			
			for (int i = 0; i < children.length; i++) {
				addFileToTar(tOut,children[i],entryName+"/");
			}
		}
	}
		
}
