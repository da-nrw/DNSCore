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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;

import de.uzk.hki.da.core.IngestGate;
import de.uzk.hki.da.core.UserException;
import de.uzk.hki.da.core.UserException.UserExceptionId;
import de.uzk.hki.da.metadata.PremisXmlValidator;
import de.uzk.hki.da.utils.ArchiveBuilder;
import de.uzk.hki.da.utils.ArchiveBuilderFactory;
import de.uzk.hki.da.utils.BagitConsistencyChecker;
import de.uzk.hki.da.utils.ConsistencyChecker;
import de.uzk.hki.da.utils.Path;

/**
 * If there is sufficient space on the WorkArea, fetches the container (named object.package.containername)
 * from the user's (object.contractor) IngestArea space and puts it to work. There the action unpacks the
 * contents and checks the SIP for consistency. Deletes the container after unpacking so that only the unpacked SIP remains. 
 * 
 * Accepted container formats [.tar,.tar.gz,.tgz,.zip].
 * 
 * The package is expected to conform to our SIP-Specification.
 * @see abc <a href="https://github.com/da-nrw/DNSCore/blob/master/ContentBroker/src/main/markdown/sip_specification.md">
 * SIP-Spezifikation
 * </a>
 * 
 * @author Daniel M. de Oliveira
 * @author Sebastian Cuy
 * @author Thomas Kleinke
 * 
 */
public class UnpackAction extends AbstractAction {

	private static final String SIP_SPEC_URL = "https://github.com/da-nrw/DNSCore/blob/master/ContentBroker/src/main/markdown/sip_specification.md";
	private static final String HELP_SUMMARY = "Make sure there exists always only one file with the same document name (which is the file path relative from the SIPs data path, excluding the file extension). "
			+ "For help refer to the SIP-Specification page at "+ SIP_SPEC_URL + ".";
	
	private enum PackageType{ BAGIT, METS }
	
	public UnpackAction(){}
	
	private IngestGate ingestGate;
	
	private String[] sidecarExtensions;
	
	
	boolean implementation() throws IOException{
		if (sidecarExtensions==null) sidecarExtensions = new String[]{};
		
		Path absoluteSIPPath = Path.make(
				localNode.getIngestAreaRootPath(),
				object.getContractor().getShort_name(), 
				object.getLatestPackage().getContainerName());
	
		if (!ingestGate.canHandle(absoluteSIPPath.toFile().length())){
			logger.warn("ResourceMonitor prevents further processing of package due to space limitations. Setting job back to start state.");
			return false;
		}
		
		String sipInForkPath = copySIPToWorkArea(absoluteSIPPath);
		unpack(new File(sipInForkPath),object.getPath().toString());
		
		
		throwUserExceptionIfDuplicatesExist();
		throwUserExceptionIfNotBagitConsistent();
		throwUserExceptionIfNotPremisConsistent();
		
		logger.info("deleting: "+sipInForkPath);
		new File(sipInForkPath).delete();
		
		// Must be the last step in this action
		absoluteSIPPath.toFile().delete();
		return true;
	}	
	


	
	private void throwUserExceptionIfNotPremisConsistent() throws IOException {
		
		try {
			if (!PremisXmlValidator.validatePremisFile(Path.make(object.getDataPath(),"premis.xml").toFile()))
				throw new UserException(UserExceptionId.INVALID_SIP_PREMIS, "PREMIS file is not valid");
		} catch (FileNotFoundException e1) {
			throw new UserException(UserExceptionId.SIP_PREMIS_NOT_FOUND, "Couldn't find PREMIS file", e1);
		}

		
	}




	/**
	 * Searches for duplicate document names. Normally duplicates are bad.
	 * <br>
	 * However, duplicates can be ok, if there are only two files sharing a document name and
	 * one of them is a sidecar file (which can be identified if it has one of the allowed sidecarExtensions).
	 * 
	 * @author Daniel M. de Oliveira
	 * @throws UserException if more there are files which share a document name.
	 */
	private void throwUserExceptionIfDuplicatesExist() {
		
		// document name <-> list of the files sharing the same document name  
		Map<String,List<File>> duplicates = 
				purgeUnicates(generateDocumentsToFilesMap());

		String errorMsg = ""; int errs = 0;
		for (String duplicate : duplicates.keySet()){
			
			boolean isOKWhenSidecarFilesAreSubtracted = false;
			for (File file:duplicates.get(duplicate)){
				if (hasSidecarExtension(file)&&(duplicates.get(duplicate).size()-1)==1) {
					isOKWhenSidecarFilesAreSubtracted=true;
					break;
				}
			}
			if (!isOKWhenSidecarFilesAreSubtracted){
				errorMsg+="More than one file found for the document named \"";
				errorMsg+= duplicate;
				errorMsg+="\".\n";
				errs++;
			}
		}

		if (errs!=0){
			errorMsg+= HELP_SUMMARY+" Found errors: "+errs;
			throw new UserException(UserException.UserExceptionId.DUPLICATE_DOCUMENT_NAMES, errorMsg);
		}
	}

	
	/**
	 * @param file
	 * @return
	 * @author Daniel M. de Oliveira
	 */
	private boolean hasSidecarExtension(File file){
		for (int i=0;i<sidecarExtensions.length;i++){
			if (FilenameUtils.getExtension(file.toString()).equals(sidecarExtensions[i])){
				return true;
			}
		}
		return false;
	}
	
	
	
	/**
	 * purges documentsToFiles and returns the reference
	 * @param
	 * @return the reference to the param  
	 * @author Daniel M. de Oliveira
	 */
	private Map<String, List<File>> purgeUnicates(Map<String,List<File>> documentsToFiles){

		List<String> unicates = new ArrayList<String>();
		
		for (String document:documentsToFiles.keySet())
			if (documentsToFiles.get(document).size()==1)
				unicates.add(document);
		
		for (String unicate:unicates)
			documentsToFiles.remove(unicate);
		return documentsToFiles;
	}
	
	
	/**
	 * @return
	 * @author Daniel M. de Oliveira
	 */
	private Map<String,List<File>> generateDocumentsToFilesMap(){
		
		Map<String,List<File>> documentsToFiles = new HashMap<String,List<File>>();
		
		Collection<File> files = FileUtils.listFiles(object.getDataPath().toFile(), TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
		for (File file : files) {
			String document = 
					file.getAbsolutePath().replace(object.getDataPath().toFile().getAbsolutePath(),"");
			document = document.substring(1);
			document = FilenameUtils.removeExtension(document);
			
			if (!documentsToFiles.keySet().contains(document)){
				
				List<File> filesList = new ArrayList<File>();
				filesList.add(file);
				documentsToFiles.put(document,filesList);
			} else {
				documentsToFiles.get(document).add(file);
			}
		}
		
		return documentsToFiles;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * Moves the SIP from ingest area to work area.
	 * 
	 * @author Thomas Kleinke
	 * @return path to SIP in work area
	 */
	private String copySIPToWorkArea(Path ingestFilePath) {
		
		File ingestFile = ingestFilePath.toFile();
		File destFile = Path.make(localNode.getWorkAreaRootPath(),"work",object.getContractor().getShort_name(), 
				  FilenameUtils.getName(ingestFilePath.toString())).toFile();
		
		if (!ingestFile.exists())
			throw new RuntimeException("Package file " + ingestFile.getAbsolutePath() + " does not exist");
		
		try {
			FileUtils.copyFile(ingestFile, destFile);
		} catch (IOException e) {
			throw new RuntimeException("File " + ingestFile.getAbsolutePath() + " could not be moved to " +
					destFile.getAbsolutePath(), e);
		}
		
		if (!destFile.exists())
			throw new RuntimeException("File " + destFile.getAbsolutePath() + " does not exist");
					
		return destFile.getAbsolutePath();
	}	
	
	
	
	
	
	
	/**
	 * Creates a folder at targetFolderPath and expands the contents of sourceFilePath into it.
	 * @param sourceFilePath
	 * @param targetFolderPath
	 * @throws RuntimeException if the folder at targetFolderPath already exists or the file at 
	 * sourceFilePath doesn't exist or the archive couldn't be unpacked.
	 */
	private void unpack(File sourceFile, String targetFolderPath){
		
		File targetFolder = new File(targetFolderPath);
		
		if (targetFolder.exists()) throw new RuntimeException("Path the SIP should be " +
				"extracted to ("+targetFolderPath+") already exists. Please clean up the fork directory and rerun the package.");
		else {
			targetFolder.mkdir();
		}
		
		if (!sourceFile.exists())
			throw new RuntimeException("container at "+ sourceFile + " doesn't exist");
		
		ArchiveBuilder builder = ArchiveBuilderFactory.getArchiveBuilderForFile(sourceFile);
		try {
			builder.unarchiveFolder(sourceFile, targetFolder);
		} catch (Exception e) {
			throw new RuntimeException("couldn't unpack archive", e);
		}

		File[] files = targetFolder.listFiles();
		if (files.length == 1) {
			File[] folderFiles = files[0].listFiles();

			for (File f : folderFiles) {
				if (f.isFile()) {
					try {
						FileUtils.moveFileToDirectory(f, targetFolder, false);
					} catch (IOException e) {
						throw new RuntimeException("couldn't move file " + f.getAbsolutePath() +
								" to folder " + targetFolderPath, e);
					}
				}
				if (f.isDirectory()) {
					try {
						FileUtils.moveDirectoryToDirectory(f, targetFolder, false);
					} catch (IOException e) {
						throw new RuntimeException("couldn't move folder " + f.getAbsolutePath() +
								" to folder " + targetFolderPath, e);
					}
				}
			}
			
			try {
				FileUtils.deleteDirectory(files[0]);
			} catch (IOException e) {
				throw new RuntimeException("couldn't delete folder " + files[0].getAbsolutePath());
			}
		}		
	}

	
	
	/**
	 * 
	 * @author Daniel M. de Oliveira
	 * @param packageInForkAbsolutePath
	 * @return
	 * @throws RuntimeException
	 */
	private PackageType throwUserExceptionIfNotBagitConsistent(){
		
		PackageType pType = null;
		pType = determinePackageType(object.getPath().toFile());

		if (pType == null)
			throw new UserException(UserExceptionId.UNKNOWN_PACKAGE_TYPE, "Package type couldn't be determined");

		ConsistencyChecker checker = new BagitConsistencyChecker(object.getPath().toString());
		
		try{
			if (!checker.checkPackage())
				throw new UserException(UserExceptionId.INCONSISTENT_PACKAGE,
						"Consistency checker detected inconsistent package!\n" + checker.getMessages(),
						checker.getMessages());			
		} catch (UserException e) { 
			throw e;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		return pType;
	}	
	
	

	/**
	 * Determines whether the package is of type BAGIT or PREMIS
	 * @author Daniel M. de Oliveira
	 * @param package PATH
	 * @return Either PackageType.METS or PackageType.BAGIT or null if package type can't be determined.
	 * @throws RuntimeException if cannot determine package type.
	 */
	PackageType determinePackageType(File pkg_path){
		logger.debug("determine package type for "+pkg_path);
		String files[] = pkg_path.list();
		for (String f:files){
			logger.debug("-- "+f);
		}
		
		if (isStandardPackage(pkg_path)) {
			logger.debug("Package is BagIt style, baby!");
		} else {
			return null;
		}
		return PackageType.BAGIT;
	}
	
	
	boolean isStandardPackage(File packageContent){
		
		boolean is=true;
		if (!new File(packageContent.getAbsolutePath()+"/data").exists()) is=false;
		if (!new File(packageContent.getAbsolutePath()+"/bagit.txt").exists()) is=false;
		if (!new File(packageContent.getAbsolutePath()+"/bag-info.txt").exists()) is=false;
		
		return is;
	}

	
	


	

	@Override
	void rollback() throws IOException {
		FileUtils.deleteDirectory(Path.make(object.getPath()).toFile());
		
		new File(localNode.getWorkAreaRootPath() + object.getContractor().getShort_name() + "/" + 
				object.getLatestPackage().getContainerName()).delete();
		
		object.getLatestPackage().getFiles().clear();
		job.setRep_name("");
	}

	public IngestGate getIngestGate() {
		return ingestGate;
	}

	public void setIngestGate(IngestGate ingestGate) {
		this.ingestGate = ingestGate;
	}

	public String getSidecarExtensions() {
		return sidecarExtensions.toString();
	}

	public void setSidecarExtensions(String sidecarFiles) {
		if (sidecarFiles.contains(","))
			this.sidecarExtensions = sidecarFiles.split(",");
		else
			this.sidecarExtensions = sidecarFiles.split(";");
	}
}
