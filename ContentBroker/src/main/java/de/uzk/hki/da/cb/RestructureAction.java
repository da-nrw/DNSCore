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
package de.uzk.hki.da.cb;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.lang.NotImplementedException;

import de.uzk.hki.da.core.ConfigurationException;
import de.uzk.hki.da.core.IngestGate;
import de.uzk.hki.da.core.UserException;
import de.uzk.hki.da.format.FormatScanService;
import de.uzk.hki.da.grid.GridFacade;
import de.uzk.hki.da.model.DAFile;
import de.uzk.hki.da.repository.RepositoryException;
import de.uzk.hki.da.service.RetrievePackagesHelper;
import de.uzk.hki.da.utils.Path;

/**
 * <li>Creates a new Representation and copies the contents of the submission into it.
 * <li>Tests if it is a delta package (detected through orig_name=already existing orig_name of an object).
 * <li>If that's the case, the previous representations of the original packages get loaded, so that all 
 * reps including the new one are accessible under fork/[csn]/[orig_name]/data/[repnames]
 * 
 * @author Daniel M. de Oliveira
 */
public class RestructureAction extends AbstractAction{
	
	private String sidecarExtensions="";	
	private FormatScanService formatScanService;
	private IngestGate ingestGate;
	private List<IOFileFilter> unwantedFilesFilters;
	private GridFacade gridRoot;
	
	@Override
	boolean implementation() throws FileNotFoundException, IOException,
			UserException, RepositoryException {
		if (getGridRoot()==null) throw new ConfigurationException("gridRoot not set");
		if (getFormatScanService()==null) throw new ConfigurationException("formatScanService not set");
		
		deleteUnwantedFiles(object.getPath().toFile()); // unwanted content can be configured in beans-actions.xml
		
		String repName;
		try {
			repName = transduceDateFolderContentsToNewRep(object.getPath().toString());
		} catch (IOException e) {		
			throw new RuntimeException("problems during creating new representation",e);
		}
		object.getLatestPackage().scanRepRecursively(repName+"a");
		logger.debug("REPNAME: " + repName);
		job.setRep_name(repName);
		
		if (object.isDelta()) {
			
			RetrievePackagesHelper retrievePackagesHelper = new RetrievePackagesHelper(getGridRoot());

			try {
				if (!getIngestGate().canHandle(retrievePackagesHelper.getObjectSize(object, job ))){
					logger.info("no disk space available at working resource. will not fetch new data.");
					return false;
				}
			} catch (IOException e) {
				throw new RuntimeException("Failed to determine object size for object " + object.getIdentifier(), e);
			}
			
			object.getDataPath().toFile().mkdirs();
			logger.info("object already exists. Moving existing packages to work area.");
			try {
				retrievePackagesHelper.loadPackages(object, false);
				logger.info("Packages of object \""+object.getIdentifier()+
						"\" are now available on cache resource at: " + Path.make(object.getPath(),"existingAIPs"));
				FileUtils.copyFile(Path.makeFile(object.getDataPath(),object.getNameOfNewestBRep(),"premis.xml"),
						Path.makeFile(object.getDataPath(),"premis_old.xml"));
			} catch (IOException e) {
				throw new RuntimeException("error while trying to get existing packages from lza area",e);
			}
		}
		
		
		object.reattach();
		logger.debug("scanning files with format identifier(s)");
		List<DAFile> scannedFiles = formatScanService.identify(object.getNewestFilesFromAllRepresentations(sidecarExtensions));
		for (DAFile f:scannedFiles){
			logger.debug(f+":"+f.getFormatPUID());
		}
		return true;
	}

	@Override
	void rollback() throws Exception {
		throw new NotImplementedException("rollback for this action not implemented yet");
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

	public FormatScanService getFormatScanService() {
		return formatScanService;
	}

	public void setFormatScanService(FormatScanService formatScanService) {
		this.formatScanService = formatScanService;
	}

	public String getSidecarExtensions() {
		return sidecarExtensions;
	}

	public void setSidecarExtensions(String sidecarExtensions) {
		this.sidecarExtensions = sidecarExtensions;
	}
}
