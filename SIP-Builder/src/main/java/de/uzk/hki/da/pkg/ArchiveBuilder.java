/*
  DA-NRW Software Suite | SIP-Builder
  Copyright (C) 2014 Historisch-Kulturwissenschaftliche Informationsverarbeitung
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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import de.uzk.hki.da.sb.ProgressManager;
import de.uzk.hki.da.sb.SIPFactory;
import de.uzk.hki.da.sb.SIPFactory.SipBuildingProcess;

/**
 * Creates archive files (tar and tgz)
 * 
 * @author Thomas Kleinke
 */
public class ArchiveBuilder {
	
	private ProgressManager progressManager;
	private int jobId;
	private SIPFactory.SipBuildingProcess sipBuildingProcess;

	/**
	 * Create an archive file out of the given source folder
	 * 
	 * @param srcFolder The folder to archive
	 * @param destFile The archive file to build
	 * @param includeFolder Indicates if the source folder will be included to the archive file on
	 * the first level or not
	 * @param compress Indicates if the archive file will be compressed (tgz file) or not (tar file)
	 * @return false if the SIP creation process was aborted during the archive file creation process, otherwise true
	 * @throws Exception
	 */
	public boolean archiveFolder(File srcFolder, File destFile, boolean includeFolder, boolean compress)
			throws Exception {

		FileOutputStream fOut = null;
		GzipCompressorOutputStream gzOut = null;
		TarArchiveOutputStream tOut = null;

		try {
			fOut = new FileOutputStream(destFile);
			
			if (compress) {
				gzOut = new GzipCompressorOutputStream(fOut);
				tOut = new TarArchiveOutputStream(gzOut, "UTF-8");
			} else
				tOut = new TarArchiveOutputStream(fOut, "UTF-8");

			tOut.setLongFileMode(TarArchiveOutputStream.LONGFILE_GNU);
			tOut.setBigNumberMode(2);

			if (includeFolder) {
				if (!addFileToArchive(tOut,srcFolder,""))
					return false;
			}
			else
			{
				File children[] = srcFolder.listFiles();

				for (int i = 0; i < children.length; i++) {
					if (!addFileToArchive(tOut,children[i],""))
						return false;
				}
			}
		}
		finally
		{
			tOut.finish();
			tOut.close();
			if (gzOut != null)
				gzOut.close();
			fOut.close();
		}

		return true;
	}

	/**
	 * Adds the given file to the archive
	 * 
	 * @param tOut The tar archive output stream
	 * @param file The file to add
	 * @param base The relative path to the file inside the archive (without the file name)
	 * @throws IOException
	 */
	private boolean addFileToArchive(TarArchiveOutputStream tOut, File file, String base) throws IOException{

		if (sipBuildingProcess.isAborted())
			return false;
		
		String entryName = base + file.getName();

		TarArchiveEntry entry = (TarArchiveEntry) tOut.createArchiveEntry(file, entryName);
		tOut.putArchiveEntry(entry);

		if (file.isFile()){
			FileInputStream fis = new FileInputStream(file);
			IOUtils.copy(fis, tOut);
			tOut.closeArchiveEntry();
			fis.close();
			
			progressManager.archiveProgress(jobId, FileUtils.sizeOf(file));
		}

		if (file.isDirectory()){
			tOut.closeArchiveEntry();

			File children[] = file.listFiles();
			if (children == null)
				return true;

			for (int i = 0; i < children.length; i++) {

				if (!addFileToArchive(tOut, children[i], entryName + "/"))
					return false;
			}
		}
		
		return true;
	}
	
	/**
	 * Unpacks the contents of the given archive into the given folder
	 * 
	 * @param srcFile The archive container file
	 * @param destFolder The destination folder
	 * @param uncompress Indicates if the archive file is compressed (tgz file) or not (tar file)
	 * @throws Exception
	 */
	public void unarchiveFolder(File srcFile, File destFolder, boolean uncompress) throws Exception {
		
		FileInputStream fIn = new FileInputStream(srcFile);
		BufferedInputStream in = new BufferedInputStream (fIn); 
		
		TarArchiveInputStream tIn = null;
		GzipCompressorInputStream gzIn = null;
		
		if (uncompress) {
			gzIn = new GzipCompressorInputStream(in);
			tIn = new TarArchiveInputStream(gzIn);
		}
		else
			tIn = new TarArchiveInputStream(fIn);

		TarArchiveEntry entry;
		do{
			entry = tIn.getNextTarEntry();
			
			if (entry == null)
				break;
			
			File entryFile = new File(destFolder.getAbsolutePath() + "/" + entry.getName());
			
			if (entry.isDirectory()) 
				entryFile.mkdirs();
			else {
				new File(entryFile.getAbsolutePath().substring(0, entryFile.getAbsolutePath().lastIndexOf('/'))).mkdirs();

				FileOutputStream out = new FileOutputStream(entryFile);
				IOUtils.copy(tIn, out);
				out.close();
			}
		} while(true);
		
		tIn.close();
		if (gzIn != null)
			gzIn.close();
		in.close();
		fIn.close();
	}
	
	public void setProgressManager(ProgressManager progressManager) {
		this.progressManager = progressManager;
	}

	public void setJobId(int jobId) {
		this.jobId = jobId;
	}

	public void setSipBuildingProcess(SIPFactory.SipBuildingProcess sipBuildingProcess) {
		this.sipBuildingProcess = sipBuildingProcess;
	}

}
