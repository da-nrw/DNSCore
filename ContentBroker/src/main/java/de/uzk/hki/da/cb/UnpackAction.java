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
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uzk.hki.da.core.ConfigurationException;
import de.uzk.hki.da.core.IngestGate;
import de.uzk.hki.da.core.UserException;
import de.uzk.hki.da.grid.GridFacade;
import de.uzk.hki.da.metadata.PremisXmlValidator;
import de.uzk.hki.da.service.PremisCreator;
import de.uzk.hki.da.service.PremisCreator.IdentifyPackageException;
import de.uzk.hki.da.service.RetrievePackagesHelper;
import de.uzk.hki.da.core.UserException.UserExceptionId;
import de.uzk.hki.da.utils.ArchiveBuilder;
import de.uzk.hki.da.utils.ArchiveBuilderFactory;
import de.uzk.hki.da.utils.BagitConsistencyChecker;
import de.uzk.hki.da.utils.ConsistencyChecker;
import de.uzk.hki.da.utils.MetsConsistencyChecker;

/**
 * Does the following steps during the (early) ingest stage of a package:
 * <ol>
 * <li>Looks in the homedir of the delivering user if it can find a file [orig_name].[containersuffix] there. If thats the case (which is the iput
 * standard case), moves it to the fork directory. In case of a prior revert or an ingest through the staging area the package 
 * is expected to be already in fork. So, if there is no package in home, fork gets scanned for appropriate packages.
 * 
 * <li>Unzips the container and checks it against METS or bagIt checksums for consistency. 
 * <li>If it is a METS style package, generates a premis file from the METS rights section.
 * <li>Creates a new Representation and copies the contents of the submission into it.
 * <li>Test if it is a delta package (detected through orig_name=already existing orig_name of an object).
 * <li>If that's the case, the previous representations of the original packages get loaded, so that all 
 * reps including the new one are accessible under fork/[csn]/[orig_name]/data/[repnames]
 * <li>Deletes container file after successful unpacking.
 * </ol>
 * 
 * Accepted container formats [.tar,.tar.gz,.tgz,.zip].
 * 
 * The package is expected to conform to our SIP-Spezifikation.
 * @see abc <a href="http://da-nrw.hki.uni-koeln.de/projects/danrwpublic/wiki/SIP-Spezifikation">
 * SIP-Spezifikation
 * </a>
 * 
 * @author Daniel M. de Oliveira
 * @author Sebastian Cuy
 * @author Thomas Kleinke
 * 
 */
public class UnpackAction extends AbstractAction {

	private enum PackageType{ BAGIT, METS }
	
	static final Logger logger = LoggerFactory.getLogger(UnpackAction.class);
	
	private List<IOFileFilter> unwantedFilesFilters;
	
	public UnpackAction(){}
	
	private IngestGate ingestGate;
	
	private GridFacade gridRoot;
	
	
	boolean implementation(){
		if (getGridRoot()==null) throw new ConfigurationException("gridRoot not set");
		
		String absoluteSIPPath = localNode.getIngestAreaRootPath() + object.getContractor().getShort_name() + 
				"/" + object.getLatestPackage().getContainerName();
	
		if (!ingestGate.canHandle(new File(absoluteSIPPath).length())){
			logger.warn("ResourceMonitor prevents further processing of package due to space limitations.");
			return false;
		}
		
		String sipInForkPath = copySIPToWorkArea(absoluteSIPPath);
		
		unpack(new File(sipInForkPath),object.getPath());
		
		deleteUnwantedFiles(new File(object.getPath())); // unwanted content can be configured in beans-actions.xml
		
		PackageType pType = checkConsistency(object.getPath());
		if (pType==PackageType.METS)
			convertMETStoPREMIS(object.getPath());
		else
			try {
				if (!PremisXmlValidator.validatePremisFile(new File(object.getDataPath() + "premis.xml")))
					throw new UserException(UserExceptionId.INVALID_SIP_PREMIS, "PREMIS file is not valid");
			} catch (FileNotFoundException e1) {
				throw new UserException(UserExceptionId.SIP_PREMIS_NOT_FOUND, "Couldn't find PREMIS file", e1);
			}
			catch (IOException e2) {
				throw new RuntimeException("Failed to read PREMIS file for validation", e2);
			}		
		
		String repName;
		try {
			repName = transduceDateFolderContentsToNewRep(object.getPath());
		} catch (IOException e) {		
			throw new RuntimeException("problems during creating new representation");
		}
		object.getLatestPackage().scanRepRecursively(repName+"a");
		logger.debug("REPNAME: " + repName);
		job.setRep_name(repName);
		
		if (object.hasDeltas()) {
			
			RetrievePackagesHelper retrievePackagesHelper = new RetrievePackagesHelper(getGridRoot());

			try {
				if (!ingestGate.canHandle(retrievePackagesHelper.getObjectSize(object, job ))){
					logger.info("no disk space available at working resource. will not fetch new data.");
					return false;
				}
			} catch (IOException e) {
				throw new RuntimeException("Failed to determine object size for object " + object.getIdentifier(), e);
			}
			
			new File(object.getDataPath()).mkdirs();
			logger.info("object already exists. Moving existing packages to work area.");
			try {
				retrievePackagesHelper.loadPackages(object, false);
				logger.info("Packages of object \""+object.getIdentifier()+
						"\" are now available on cache resource at: " + object.getPath()+"existingAIPs");
				FileUtils.copyFile(new File(object.getDataPath() + object.getNameOfNewestBRep() + "/premis.xml"),
						 new File(object.getDataPath() + "premis_old.xml"));
			} catch (IOException e) {
				throw new RuntimeException("error while trying to get existing packages from lza area",e);
			}
		}
		
		logger.info("deleting: "+sipInForkPath);
		new File(sipInForkPath).delete();
		
		// Must be the last step in this action
		new File(absoluteSIPPath).delete();
				
		return true;
	}	
	
	/**
	 * Moves the SIP from ingest area to work area.
	 * 
	 * @author Thomas Kleinke
	 * @return path to SIP in work area
	 */
	private String copySIPToWorkArea(String ingestFilePath) {
		
		File ingestFile = new File(ingestFilePath);
		File destFile = new File(localNode.getWorkAreaRootPath() + "work/" + object.getContractor().getShort_name() + "/" + 
				  FilenameUtils.getName(ingestFilePath));
		
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
	 * 
	 * @author Daniel M. de Oliveira
	 * @param packageInForkAbsolutePath
	 * @return
	 * @throws RuntimeException
	 */
	private PackageType checkConsistency(String packageInForkAbsolutePath){
		
		PackageType pType = null;
		pType = determinePackageType(new File(packageInForkAbsolutePath));

		if (pType == null)
			throw new UserException(UserExceptionId.UNKNOWN_PACKAGE_TYPE, "Package type couldn't be determined");

		ConsistencyChecker checker = null;
		if (pType==PackageType.METS) {
			normalizeMetsPackage(new File(packageInForkAbsolutePath));
			checker = new MetsConsistencyChecker(packageInForkAbsolutePath + "/data");
		}else{
			checker = new BagitConsistencyChecker(packageInForkAbsolutePath);
		}
		
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
	
	private void convertMETStoPREMIS(String unpackedSIPPath){
		
		logger.info("The delivered package is a mets style package. Converting Rights " +
				"statement METS -> PREMIS");

		PremisCreator premisCreator = new PremisCreator();
		try {
			premisCreator.createPremisFromMets(
					unpackedSIPPath+"/data/export_mets.xml",
					unpackedSIPPath+"/data/premis.xml",
					object.getContractor());
		} catch (IdentifyPackageException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Takes a SIP style package that contains its files directly under data and moves this files
	 * to a newly created subfolder of data which is named like yyyy_MM_dd+HH_mm+a (java simple date format notation).
	 * 
	 * @author Daniel M. de Oliveira
	 * @param job
	 * @param physicalPathToAIP
	 * @return the representations
	 * @throws IOException 
	 */
	public String transduceDateFolderContentsToNewRep(String physicalPathToAIP) throws IOException{
		logger.trace("createFirstRepresentation(job,"+physicalPathToAIP+")");
		
		Date dNow = new Date( );
	    SimpleDateFormat ft = new SimpleDateFormat ("yyyy'_'MM'_'dd'+'HH'_'mm'+'");
	    String repName = ft.format(dNow);
	    
		FileUtils.moveDirectory(new File(physicalPathToAIP+"/data"), 
				new File(physicalPathToAIP+"/temp"));
	
	    new File(physicalPathToAIP+"/data").mkdir();
	    FileUtils.moveDirectory(new File(physicalPathToAIP+"/temp"), 
	    		new File(physicalPathToAIP+"/data/"+repName+"a"));
	    
	    return repName;
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
	
	public void deleteUnwantedFiles(File pkg) {

		if(unwantedFilesFilters == null || unwantedFilesFilters.isEmpty()) {
			logger.warn("unwantedFilesFilters is not set. No cleanup will be performed after unpacking.");
			return;
		}

		for (IOFileFilter filter : unwantedFilesFilters) {
			
			Collection<File> files = FileUtils.listFilesAndDirs(pkg, filter, TrueFileFilter.INSTANCE);
			for (File file : files) {
				if( filter.accept(file)) {
					logger.warn("deleted unwanted file: {}", file.getAbsolutePath());
					FileUtils.deleteQuietly(file);
				}
			}
		}

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
		
		boolean isSemPackage = false;
		if (isStandardPackage(pkg_path)) {
			logger.debug("Package is BagIt style, baby!");
		} else if (isSemanticsPackage(pkg_path)) {
			logger.debug("Package is METS style, baby!");
			isSemPackage = true;
		} else {
			return null;
		}
		return (isSemPackage ? PackageType.METS : PackageType.BAGIT);
	}
	
	void normalizeMetsPackage(File packageName){
		
		String children[] = packageName.list();
		String source= packageName.getAbsolutePath() + "/" + children[0];
		String target= packageName.getAbsolutePath() + "/data";
		new File(source).renameTo(new File(target));
		logger.debug("renaming METS package from "+source+" to "+target);
	}
	
	boolean isSemanticsPackage(File packageContent) {
		String children[] = (new File(packageContent.getAbsolutePath())).list();
		logger.debug("Absolute path has children: " + Arrays.toString(children));
		if (children.length == 1) {
			logger.debug("Testing if path exists" + packageContent.getAbsolutePath() + "/" + children[0] + "/export_mets.xml");
			logger.debug("Result: " + new File(packageContent.getAbsolutePath() + "/" + children[0] + "/export_mets.xml").exists());
			if (new File(packageContent.getAbsolutePath() + "/" + children[0] + "/export_mets.xml").exists()) {
				return true;
			}
		}
		return false;
	}

	boolean isStandardPackage(File packageContent){
		
		boolean is=true;
		if (!new File(packageContent.getAbsolutePath()+"/data").exists()) is=false;
		if (!new File(packageContent.getAbsolutePath()+"/bagit.txt").exists()) is=false;
		if (!new File(packageContent.getAbsolutePath()+"/bag-info.txt").exists()) is=false;
		
		return is;
	}

	public List<IOFileFilter> getUnwantedFilesFilters() {
		return unwantedFilesFilters;
	}
		
	/**
	 * Sets a list of unix-like patterns which denote files and directories
	 * that will be deleted after unpacking the SIP.
	 * Allowed wildcards are "*" and "?".
	 * @param unwantedFiles
	 */
	public void setUnwantedFilesFilters(List<IOFileFilter> unwantedFilesFilters) {
		this.unwantedFilesFilters = unwantedFilesFilters;
	}

	@Override
	void rollback() throws IOException {
		FileUtils.deleteDirectory(new File(object.getPath()));
		
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

	public GridFacade getGridRoot() {
		return gridRoot;
	}

	public void setGridRoot(GridFacade gridRoot) {
		this.gridRoot = gridRoot;
	}
}
