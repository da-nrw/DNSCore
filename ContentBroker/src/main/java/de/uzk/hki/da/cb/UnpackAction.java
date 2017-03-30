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
import org.xml.sax.SAXException;

import de.uzk.hki.da.action.AbstractAction;
import de.uzk.hki.da.core.IngestGate;
import de.uzk.hki.da.core.PreconditionsNotMetException;
import de.uzk.hki.da.core.UserException;
import de.uzk.hki.da.core.UserException.UserExceptionId;
import de.uzk.hki.da.model.ObjectPremisXmlReader;
import de.uzk.hki.da.model.PremisXmlValidator;
import de.uzk.hki.da.pkg.ArchiveBuilder;
import de.uzk.hki.da.pkg.ArchiveBuilderFactory;
import de.uzk.hki.da.pkg.BagitConsistencyChecker;
import de.uzk.hki.da.pkg.ConsistencyChecker;
import de.uzk.hki.da.util.ConfigurationException;
import de.uzk.hki.da.utils.FolderUtils;
import de.uzk.hki.da.utils.Path;
import de.uzk.hki.da.utils.SidecarUtils;

/**
 * If there is sufficient space on the WorkArea, fetches the container (named object.package.containername)
 * from the user's (object.contractor) IngestArea space and puts it to work. There the action unpacks the
 * contents and checks the SIP for consistency. Deletes the container after proving that it is valid so that
 * the original SIP remains. If the package has proven valid, then the original SIP on the IngestArea gets 
 * removed. 
 * 
 * Accepted container formats [.tar,.tar.gz,.tgz,.zip].
 * 
 * The package is expected to conform to our SIP-Specification.
 * @see abc <a href="https://github.com/da-nrw/DNSCore/blob/master/ContentBroker/src/main/markdown/specification_sip.de.md">
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
	private static final String PREMIS_XML = "premis.xml";
	
	public UnpackAction(){SUPPRESS_OBJECT_CONSISTENCY_CHECK=true;}
	
	private IngestGate ingestGate;
	
	@Override
	public void checkConfiguration() {
		if (ingestGate==null) throw new ConfigurationException("ingestGate");
	}
	

	@Override
	public void checkPreconditions() {
		if (!sipContainerOnIngestArea().exists()) throw new PreconditionsNotMetException("Missing file: "+sipContainerOnIngestArea());
		if (wa.objectPath().toFile().exists()) throw new PreconditionsNotMetException("Should not exist: "+wa.objectPath());
	}

	@Override
	public boolean implementation() throws IOException{
		long size = 0L;
		if (sipContainerOnIngestAreaIsDir())
		 size = FileUtils.sizeOfDirectory(sipContainerOnIngestArea());
		else size = sipContainerOnIngestArea().length();
		
		if (!ingestGate.canHandle(size)){
//			JmsMessage jms = new JmsMessage(C.QUEUE_TO_CLIENT,C.QUEUE_TO_SERVER,o.getIdentifier() + " - Please check WorkArea space limitations: " + ingestGate.getFreeDiskSpacePercent() +" % free needed " );
//			super.getJmsMessageServiceHandler().sendJMSMessage(jms);	
			logger.warn("ResourceMonitor prevents further processing of package due to space limitations. Setting job back to start state.");
			return false;
		}
		
		
		wa.ingestSIP(sipContainerOnIngestArea());
		if (!sipContainerOnIngestAreaIsDir()) {
			unpack(wa.sipFile());
			expandDirInto();
			wa.sipFile().delete();
		} else {
			moveSipDir();
			expandDirInto();
		}
		
		throwUserExceptionIfNotBagitConsistent();
		throwUserExceptionIfDuplicatesExist();
		throwUserExceptionIfNotPremisConsistent();
		
		// Is the last step of action because it should only happen after validity has been proven. 
		logger.info("Removing SIP from IngestArea");
		if (!sipContainerOnIngestAreaIsDir()) {
		sipContainerOnIngestArea().delete();
		} else FolderUtils.deleteDirectorySafe(sipContainerOnIngestArea());
		return true;
	}	
	
	private void moveSipDir() throws IOException {
		FileUtils.moveDirectoryToDirectory(wa.sipFile(), wa.objectPath().toFile(), true);
	}
	
	private File sipContainerOnIngestArea() {
		return sipContainerInIngestAreaPath().toFile();
	}
	
	private boolean sipContainerOnIngestAreaIsDir() {
		return sipContainerInIngestAreaPath().toFile().isDirectory();
	}
	
	
	private Path sipContainerInIngestAreaPath() {
		return Path.make(
				n.getIngestAreaRootPath(),
				o.getContractor().getShort_name(), 
				o.getLatestPackage().getContainerName());
	}


	
	@Override
	public void rollback() throws IOException {
		
		FolderUtils.deleteDirectorySafe(wa.objectPath().toFile());
		if (!sipContainerOnIngestAreaIsDir())
		wa.sipFile().delete();
		
		o.getLatestPackage().getFiles().clear();
		j.setRep_name("");
	}




	private void throwUserExceptionIfNotPremisConsistent() throws IOException {
		
		try {
			if (!PremisXmlValidator.validatePremisFile(Path.make(wa.dataPath(),PREMIS_XML).toFile()))
				throw new UserException(UserExceptionId.INVALID_SIP_PREMIS, "PREMIS Datei nicht valide.");
		} catch (FileNotFoundException e1) {
			throw new UserException(UserExceptionId.SIP_PREMIS_NOT_FOUND, "PREMIS Datei nicht gefunden.", e1);
		} catch (SAXException e) {
			logger.error(e.getMessage());
			throw new UserException(UserExceptionId.INVALID_SIP_PREMIS, "PREMIS Datei nicht valide.: "+e.getMessage());
		}		
		try {
			//just test: parse values and do xml to object mapping
			new ObjectPremisXmlReader().deserialize(Path.makeFile(wa.dataPath(),PREMIS_XML));
		} catch (Exception e) {
			throw new UserException(UserExceptionId.READ_SIP_PREMIS_ERROR,
					"Konnte PREMIS Datei nicht erfolgreich einlesen.", e);
		}
	}




	/**
	 * Searches for duplicate document names. Normally duplicates are bad.
	 * <br>
	 * However, duplicates can be ok, if there are only two files sharing a document name and
	 * one of them is a sidecar file (which can be identified if it has one of the allowed sidecarExtensions).
	 * 
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
				if (SidecarUtils.hasSidecarExtension(file.getAbsolutePath(),preservationSystem.getSidecarExtensions())&&(duplicates.get(duplicate).size()-1)==1) {
					isOKWhenSidecarFilesAreSubtracted=true;
					break;
				}
			}
			if (!isOKWhenSidecarFilesAreSubtracted){
				errorMsg+="Mehr als ein Dokument gefunden mit dem Namen \"";
				errorMsg+= duplicate;
				errorMsg+="\".\n";
				errs++;
			}
		}

		if (errs!=0){
			errorMsg+= HELP_SUMMARY+" Gefundene Fehler: "+errs;
			throw new UserException(UserException.UserExceptionId.DUPLICATE_DOCUMENT_NAMES, errorMsg);
		}
	}

	
	
	/**
	 * purges documentsToFiles and returns the reference
	 * @param
	 * @return the reference to the param  
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
	 */
	private Map<String,List<File>> generateDocumentsToFilesMap(){
		
		Map<String,List<File>> documentsToFiles = new HashMap<String,List<File>>();
		
		Collection<File> files = FileUtils.listFiles(wa.dataPath().toFile(), TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
		for (File file : files) {
			String document = 
					file.getAbsolutePath().replace(wa.dataPath().toFile().getAbsolutePath(),"");
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
	
	private void expandDirInto() {

		File[] files = wa.objectPath().toFile().listFiles();
		if (files.length == 1) {
			File[] folderFiles = files[0].listFiles();

			for (File f : folderFiles) {
				if (f.isFile()) {
					try {
						FileUtils.moveFileToDirectory(f, wa.objectPath().toFile(), false);
					} catch (IOException e) {
						throw new RuntimeException("couldn't move file " + f.getAbsolutePath() +
								" to folder " + wa.objectPath().toFile(), e);
					}
				}
				if (f.isDirectory()) {
					try {
						FileUtils.moveDirectoryToDirectory(f, wa.objectPath().toFile(), false);
					} catch (IOException e) {
						throw new RuntimeException("couldn't move folder " + f.getAbsolutePath() +
								" to folder " + wa.objectPath().toFile(), e);
					}
				}
			}
			
			try {
				FolderUtils.deleteDirectorySafe(files[0]);
			} catch (IOException e) {
				throw new RuntimeException("couldn't delete folder " + files[0].getAbsolutePath());
			}
		}		

	}
	/**
	 * Creates a folder at targetFolderPath and expands the contents of sourceFilePath into it.
	 * @param sourceFilePath
	 * @param targetFolderPath
	 * @throws RuntimeException if the folder at targetFolderPath already exists or the file at 
	 * sourceFilePath doesn't exist or the archive couldn't be unpacked.
	 */
	private void unpack(File sourceFile){
		
		wa.objectPath().toFile().mkdir();
		
		
		if (!sourceFile.exists())
			throw new RuntimeException("container at "+ sourceFile + " doesn't exist");
		
		ArchiveBuilder builder = ArchiveBuilderFactory.getArchiveBuilderForFile(sourceFile);
		try {
			builder.unarchiveFolder(sourceFile, wa.objectPath().toFile());
		} catch (Exception e) {
			throw new RuntimeException("couldn't unpack archive", e);
		}
	}

	
	
	/**
	 * 
	 * @param packageInForkAbsolutePath
	 * @return
	 * @throws RuntimeException
	 */
	private void throwUserExceptionIfNotBagitConsistent(){
		
		if (! isBagItPackage(wa.objectPath().toFile()))
			throw new UserException(UserExceptionId.NOT_A_BAGIT_PACKAGE, "Paket entspricht nicht der BagIt Struktur.");

		ConsistencyChecker checker = new BagitConsistencyChecker(wa.objectPath().toString());
		
		try{
			if (!checker.checkPackage())
				throw new UserException(UserExceptionId.INCONSISTENT_PACKAGE,
						"Inkonsistentes Paket!\n" + checker.getMessages(),
						checker.getMessages());			
		} catch (UserException e) { 
			throw e;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}	
	
	

	/**
	 * Check if package is premis.
	 * 
	 * @param package PATH
	 * @return Either PackageType.METS or PackageType.BAGIT or null if package type can't be determined.
	 * @throws RuntimeException if cannot determine package type.
	 */
	private boolean isBagItPackage(File pkg_path){
		logger.debug("determine package type for "+pkg_path);
		String files[] = pkg_path.list();
		for (String f:files){
			logger.debug("-- "+f);
		}
		
		if (isStandardPackage(pkg_path)) {
			logger.debug("Package is BagIt style, baby!");
		} else {
			return false;
		}
		return true;
	}
	
	
	boolean isStandardPackage(File packageContent){
		
		boolean is=true;
		if (!new File(packageContent.getAbsolutePath()+"/data").exists()) is=false;
		if (!new File(packageContent.getAbsolutePath()+"/bagit.txt").exists()) is=false;
		if (!new File(packageContent.getAbsolutePath()+"/bag-info.txt").exists()) is=false;
		
		return is;
	}

	

	public IngestGate getIngestGate() {
		return ingestGate;
	}

	public void setIngestGate(IngestGate ingestGate) {
		this.ingestGate = ingestGate;
	}
}
