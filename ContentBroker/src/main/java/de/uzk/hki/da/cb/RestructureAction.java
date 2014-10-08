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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.NotImplementedException;
import org.hibernate.Session;

import de.uzk.hki.da.action.AbstractAction;
import de.uzk.hki.da.core.C;
import de.uzk.hki.da.core.ConfigurationException;
import de.uzk.hki.da.core.HibernateUtil;
import de.uzk.hki.da.core.IngestGate;
import de.uzk.hki.da.core.Path;
import de.uzk.hki.da.core.UserException;
import de.uzk.hki.da.ff.FileFormatException;
import de.uzk.hki.da.ff.FileFormatFacade;
import de.uzk.hki.da.ff.IFileWithFileFormat;
import de.uzk.hki.da.ff.ISubformatIdentificationPolicy;
import de.uzk.hki.da.grid.GridFacade;
import de.uzk.hki.da.model.SecondStageScanPolicy;
import de.uzk.hki.da.repository.RepositoryException;

/**
 * <li>Creates a new Representation and copies the contents of the submission into it.
 * <li>Tests if it is a delta package (detected through orig_name=already existing orig_name of an object).
 * <li>If that's the case, the previous representations of the original packages get loaded, so that all 
 * reps including the new one are accessible under fork/[csn]/[orig_name]/data/[repnames]
 * 
 * @author Daniel M. de Oliveira
 */
public class RestructureAction extends AbstractAction{
	
	private FileFormatFacade fileFormatFacade;
	private IngestGate ingestGate;
	private GridFacade gridRoot;
	
	public RestructureAction(){
		SUPPRESS_OBJECT_CONSISTENCY_CHECK = true;
	}
	
	@Override
	public void checkActionSpecificConfiguration() throws ConfigurationException {
		if (getGridRoot()==null) throw new ConfigurationException("gridRoot not set");
		if (getFileFormatFacade()==null) throw new ConfigurationException("fileFormatFacade not set");
	}

	@Override
	public void checkSystemStatePreconditions() throws IllegalStateException {
		// Auto-generated method stub
	}

	@Override
	public boolean implementation() throws FileNotFoundException, IOException,
			UserException, RepositoryException {
		
		FileUtils.moveDirectory(object.getDataPath().toFile(), 
				new File(object.getPath()+"/sipData"));
		object.getDataPath().toFile().mkdirs();

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
			
			logger.info("object already exists. Moving existing packages to work area.");
			try {
				retrievePackagesHelper.loadPackages(object, false);
				logger.info("Packages of object \""+object.getIdentifier()+
						"\" are now available on cache resource at: " + Path.make(object.getPath(),"existingAIPs"));
				FileUtils.copyFile(Path.makeFile(object.getPath("newest"),"premis.xml"),
						Path.makeFile(object.getDataPath(),"premis_old.xml"));
			} catch (IOException e) {
				throw new RuntimeException("error while trying to get existing packages from lza area",e);
			}
		}
		
		
		String repName;
		try {
			repName = transduceDateFolderContentsToNewRep(object.getPath().toString());
		} catch (IOException e) {		
			throw new RuntimeException("problems during creating new representation",e);
		}
		object.getLatestPackage().scanRepRecursively(repName+"a");
		job.setRep_name(repName);

		
		object.reattach();
		logger.debug("scanning files with format identifier(s)");
		Session session = HibernateUtil.openSession();
		List<SecondStageScanPolicy> policies = 
				preservationSystem.getSubformatIdentificationPolicies();
		session.close();


		List<ISubformatIdentificationPolicy> polys = new ArrayList<ISubformatIdentificationPolicy>();
		for (SecondStageScanPolicy s:policies)
			polys.add((ISubformatIdentificationPolicy) s);
		getFileFormatFacade().setSubformatIdentificationPolicies(polys);


		
		List<IFileWithFileFormat> scannedFiles = null;
		try {
			scannedFiles = fileFormatFacade.identify(object.getNewestFilesFromAllRepresentations(preservationSystem.getSidecarExtensions()));
		} catch (FileFormatException e) {
			throw new RuntimeException(C.ERROR_MSG_DURING_FILE_FORMAT_IDENTIFICATION,e);
		}
		for (IFileWithFileFormat f:scannedFiles){
			logger.debug(f+":"+f.getFormatPUID());
		}

		
		
		
		logger.debug("Create new b representation "+repName+"b");
		Path.makeFile(object.getDataPath(), repName+"b").mkdir();

		Path.makeFile(object.getDataPath(),"jhove_temp").mkdirs();
		return true;
	}

	@Override
	public void rollback() throws Exception {
		throw new NotImplementedException("rollback for this action not implemented yet");
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
	    
		
	
	    FileUtils.moveDirectory(new File(physicalPathToAIP+"/sipData"), 
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
	
	public FileFormatFacade getFileFormatFacade() {
		return fileFormatFacade;
	}

	public void setFileFormatFacade(FileFormatFacade fileFormatFacade) {
		this.fileFormatFacade = fileFormatFacade;
	}
}
