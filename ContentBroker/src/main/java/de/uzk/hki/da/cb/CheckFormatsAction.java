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

package de.uzk.hki.da.cb;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;

import de.uzk.hki.da.action.AbstractAction;
import de.uzk.hki.da.core.C;
import de.uzk.hki.da.core.SubsystemNotAvailableException;
import de.uzk.hki.da.format.ConnectionException;
import de.uzk.hki.da.format.FileFormatException;
import de.uzk.hki.da.format.FileFormatFacade;
import de.uzk.hki.da.format.FileWithFileFormat;
import de.uzk.hki.da.model.DAFile;
import de.uzk.hki.da.model.Package;
import de.uzk.hki.da.util.ConfigurationException;
import de.uzk.hki.da.util.Path;
import de.uzk.hki.da.utils.CommaSeparatedList;
import de.uzk.hki.da.utils.MD5Checksum;

/**
 * Executes a file format identification and technical metadata extraction 
 * on all files of the object.
 * 
 * For every file a corresponding file which contains the metadata is created, 
 * following this a naming convention shown by this example:
 * 
 * <br>
 * <strong>File:</strong> WorkAreaRootPath/work/csn/oid/data/repname/sub/a.jpg
 * <br>
 * <strong>Metadata file:</strong> WorkAreaRootPath/work/csn/oid/data/jhove_temp/repname/md5hashed(sub/a.jpg)
 * 
 * 
 * @author Daniel M. de Oliveira
 */
public class CheckFormatsAction extends AbstractAction {


	private FileFormatFacade fileFormatFacade;


	@Override
	public void checkConfiguration() {
		if (fileFormatFacade==null) throw new ConfigurationException("fileFormatFacade");
	}
	

	@Override
	public void checkPreconditions() {
	}

	@Override
	public boolean implementation() throws IOException, SubsystemNotAvailableException {
		
		identifyFileFormatsOfAllFilesOfObject();
		createChecksumsForAllFiles();
		
		// TODO remove. send via communicator. this should not be saved to object this early. 
		o.setMost_recent_formats(getFormatsAsCommaSeparatedString(getNewestFilesOfObject()));
		o.setMostRecentSecondaryAttributes(getSubformatsAsCommaSeparatedString(getNewestFilesOfObject()).toString());
		o.setOriginal_formats(getFormatsOfAllOriginalFilesAsCommaSeparatedString(getAllFilesOfObject()));
		
		attachJhoveInfoToAllFiles(getAllFilesOfObject());
		return true;
	}
	
	
	private void createChecksumsForAllFiles() throws IOException {
		
		for (Package p:o.getPackages()){
			
			for (DAFile daf:p.getFiles()){
				
				String md5 = MD5Checksum.getMD5checksumForLocalFile(wa.toFile(daf));
				daf.setChksum(md5);
			}
		}
		
		
	}
	
	
	private void identifyFileFormatsOfAllFilesOfObject() throws SubsystemNotAvailableException{
		List<FileWithFileFormat> allFiles = new ArrayList<FileWithFileFormat>();
		
		for (Package p:o.getPackages()){
				allFiles.addAll(p.getFiles());
		}
		
		try {
			allFiles = getFileFormatFacade().identify(wa.dataPath(),allFiles);
		} catch (FileFormatException e) {
			throw new RuntimeException(C.ERROR_MSG_DURING_FILE_FORMAT_IDENTIFICATION,e);
		} catch (IOException e) {
			throw new SubsystemNotAvailableException(e);
		}
		
		for (FileWithFileFormat f:allFiles){
			if (f.getFormatPUID()==null) throw new RuntimeException("file \""+f+"\" has no format puid");
		}
	}
	
	/**
	 * Scans every file in files with jhove and sets the pathToJhoveOutput
	 * property accordingly
	 * 
	 * @param files
	 * @throws IOException 
	 * @throws SubsystemNotAvailableException 
	 */
	private void attachJhoveInfoToAllFiles(List<DAFile> files) throws IOException, SubsystemNotAvailableException {
		for (DAFile f : files) {
			// dir
			String dir = Path.make(wa.dataPath(),C.JHOVE_TEMP,f.getRep_name()).toString();
			String fileName = DigestUtils.md5Hex(f.getRelative_path());
			
			if (!new File(dir).exists()) new File(dir).mkdirs();
			
			File target = Path.makeFile(dir,fileName);
			logger.debug("will write jhove output to: "+target);
			try {
				if (!fileFormatFacade.extract(wa.toFile(f), target)) 
					throw new RuntimeException("Unknown error during metadata file extraction.");
			} catch (ConnectionException e) {
				throw new SubsystemNotAvailableException("fileFormatFacade.extract() could not connect.",e);
			}
		}
	}

	private List<DAFile> getNewestFilesOfObject(){
		return o.getNewestFilesFromAllRepresentations(preservationSystem.getSidecarExtensions());
	}

	private List<DAFile> getAllFilesOfObject(){
		List<DAFile> allDAFiles = new ArrayList<DAFile>();
		for (Package p:o.getPackages()){
				allDAFiles.addAll(p.getFiles());
		}
		return allDAFiles;
	}
	
	private String getFormatsAsCommaSeparatedString(List<DAFile> files) {
		
		Set<String> mostRecentFormats = new HashSet<String>();
		for (DAFile f:files){
			mostRecentFormats.add(f.getFormatPUID());
		}
		return new CommaSeparatedList(new ArrayList<String>(mostRecentFormats)).toString();
	}
	
	
	private String getSubformatsAsCommaSeparatedString(List<DAFile> files) {
		
		Set<String> mostRecentSubformats = new HashSet<String>();
		for (DAFile f:files){
			if (!f.getSubformatIdentifier().isEmpty())
				mostRecentSubformats.add(f.getSubformatIdentifier());
		}
		return new CommaSeparatedList(new ArrayList<String>(mostRecentSubformats)).toString();
	}

	private String getFormatsOfAllOriginalFilesAsCommaSeparatedString(List<DAFile> files) {
		return new CommaSeparatedList(new ArrayList<String>(getPUIDsForAllRepAFiles(files))).toString();
	}
	
	
	private Set<String> getPUIDsForAllRepAFiles(List<DAFile> allFiles) {
		Set<String> originalFormatsSet = new HashSet<String>();
		for (DAFile f : allFiles) {
			if (f.getRep_name().endsWith("a"))
				originalFormatsSet.add(f.getFormatPUID());
		}
		return originalFormatsSet;
	}
	

	
	
	
	
	
	
	
	
	@Override
	public void rollback() throws Exception {
		FileUtils.deleteQuietly(Path.makeFile(wa.dataPath(),C.JHOVE_TEMP));
	}


	public FileFormatFacade getFileFormatFacade() {
		return fileFormatFacade;
	}

	public void setFileFormatFacade(FileFormatFacade fileFormatFacade) {
		this.fileFormatFacade = fileFormatFacade;
	}
}
