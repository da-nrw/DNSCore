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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.uzk.hki.da.action.AbstractAction;
import de.uzk.hki.da.core.C;
import de.uzk.hki.da.core.ConfigurationException;
import de.uzk.hki.da.core.UserException;
import de.uzk.hki.da.core.UserException.UserExceptionId;
import de.uzk.hki.da.ff.FFConstants;
import de.uzk.hki.da.metadata.MetadataStructure;
import de.uzk.hki.da.metadata.MetadataStructureFactory;
import de.uzk.hki.da.metadata.XmpCollector;
import de.uzk.hki.da.model.DAFile;
import de.uzk.hki.da.model.Event;
import de.uzk.hki.da.repository.RepositoryException;

/**
 * Detects the package type of an object and validates the metadata structure.
 * 
 * The package type can be of the four types
 * <ul>
 * <li>METS
 * <li>EAD
 * <li>XMP
 * <li>LIDO
 * </ul>
 * 
 * When package type XMP is detected, and no XMP.rdf has been found, a XMP.rdf
 * manifest gets generated an put to the temp representation.
 * 
 * @author Daniel M. de Oliveira
 */
public class ValidateMetadataAction extends AbstractAction {
	
	private static final String PACKAGE_TYPE_FOR_OBJECT_DETERMINDED = "Package type for object determinded: ";
	private String detectedPackageType = "NONE";
	private DAFile detectedMetadataFile;
	private boolean packageTypeInObjectWasSetBeforeRunningAction=false;
	MetadataStructureFactory msf = new MetadataStructureFactory();
	
	
	@Override
	public void checkActionSpecificConfiguration() throws ConfigurationException {
		// Auto-generated method stub
		
	}

	@Override
	public void checkSystemStatePreconditions() throws IllegalStateException {
		// Auto-generated method stub
		
	}

	@Override
	public boolean implementation() throws FileNotFoundException, IOException,
			UserException, RepositoryException {
		
		detect();
		throwExceptionIfPackageTypeCollision();
		
		logger.info(PACKAGE_TYPE_FOR_OBJECT_DETERMINDED+detectedPackageType);
		if (detectedPackageType.equals("NONE")){
			return true;
		}
		
		object.setPackage_type(detectedPackageType);
		
		if(!object.isDelta()) {
			logger.debug("Validate package...");
			MetadataStructure ms = createMetadataStructure();
			if (!ms.isValid()){
				throw new UserException(UserExceptionId.INCONSISTENT_PACKAGE, 
						"Package of type "+detectedPackageType+" is not consistent");
			}
		} else {
			logger.debug("DELTA: Skipping validation...");
		}
		
		object.setMetadata_file(detectedMetadataFile.getRelative_path());

		return true;
	}
	
	
	private MetadataStructure createMetadataStructure() {
		MetadataStructure ms=null;
		try {
			if(object.getPackage_type().equals(C.CB_PACKAGETYPE_XMP)) {
				collectXMP();
			}
			File d = detectedMetadataFile.toRegularFile();
			List<DAFile> newestFiles = object.getNewestFilesFromAllRepresentations(detectedPackageType);
			ms = msf.create(detectedPackageType, d, newestFiles);
		} catch (Exception e){
			throw new RuntimeException("problem occured during creation of metadata structure",e);
		}
		return ms;
	}
	
	/**
	 * if something else has been detected in a previous SIP.
	 */
	private void throwExceptionIfPackageTypeCollision() {
		if (!(object.getPackage_type()==null||object.getPackage_type().isEmpty())){
			packageTypeInObjectWasSetBeforeRunningAction=true;
			if ((!detectedPackageType.equals(object.getPackage_type()))
					||(!detectedMetadataFile.getRelative_path().equals(object.getMetadata_file()))){
				throw new RuntimeException("COLLISION");
			}
		}
	}
	

	@Override
	public void rollback() throws Exception {
		if (!packageTypeInObjectWasSetBeforeRunningAction){
			object.setMetadata_file(null);
			object.setPackage_type(null);
		}
	}

	/**
	 * @throws UserException If more than one metadata file was found.
	 * @author Daniel M. de Oliveira 
	 */
	private void detect(){
		
		if (getFilesOfMetadataType(FFConstants.SUBFORMAT_IDENTIFIER_EAD).size()>=2){
			throw new UserException(UserExceptionId.DUPLICATE_METADATA_FILE,"duplicate EAD");
		}
		if (getFilesOfMetadataType(FFConstants.SUBFORMAT_IDENTIFIER_LIDO).size()>1){
			throw new UserException(UserExceptionId.DUPLICATE_METADATA_FILE,"duplicate LIDO");
		}

		int ptypeCount=0;
		
		if (getFilesOfMetadataType(FFConstants.SUBFORMAT_IDENTIFIER_EAD).size()==1){
			detectedMetadataFile=getFilesOfMetadataType(FFConstants.SUBFORMAT_IDENTIFIER_EAD).get(0);
			detectedPackageType=C.CB_PACKAGETYPE_EAD;
			ptypeCount++;
		}
		
		if ((getFilesOfMetadataType(FFConstants.SUBFORMAT_IDENTIFIER_EAD).size()!=1)&&
				getFilesOfMetadataType(FFConstants.SUBFORMAT_IDENTIFIER_METS).size()>1){
			throw new UserException(UserExceptionId.DUPLICATE_METADATA_FILE,"duplicate METS");
		}  
				
		if (getFilesOfMetadataType(FFConstants.SUBFORMAT_IDENTIFIER_METS).size()==1){
			detectedMetadataFile=getFilesOfMetadataType(FFConstants.SUBFORMAT_IDENTIFIER_METS).get(0);
			detectedPackageType=C.CB_PACKAGETYPE_METS;
			ptypeCount++;
		}
		
		if ((getFilesOfMetadataType(FFConstants.SUBFORMAT_IDENTIFIER_XMP)).size()>=1){
			detectedMetadataFile=new DAFile(object.getLatestPackage(),
					object.getPath("newest").getLastElement(),C.XMP_METADATA_FILE);
			detectedPackageType=C.CB_PACKAGETYPE_XMP;
			ptypeCount++;
		}
		
		if ((getFilesOfMetadataType(FFConstants.SUBFORMAT_IDENTIFIER_LIDO)).size()==1){
			detectedMetadataFile=getFilesOfMetadataType(FFConstants.SUBFORMAT_IDENTIFIER_LIDO).get(0);
			detectedPackageType=C.CB_PACKAGETYPE_LIDO;
			ptypeCount++;
		}
		
		if (ptypeCount>1)
			throw new UserException(UserExceptionId.DUPLICATE_METADATA_FILE,"duplicate METADATA");
	}
	
	
	private List<DAFile> getFilesOfMetadataType(String metadataFormatIdentifier){
		List<DAFile> result = new ArrayList<DAFile>();

		for (DAFile f:object.getNewestFilesFromAllRepresentations("xmp")){
			
			if (metadataFormatIdentifier.equals(f.getFormatSecondaryAttribute())) {
				result.add(f);
			}
		}
		return result;
	}
	
	
	public void setMsf(MetadataStructureFactory msf) {
		this.msf = msf;
	}
	
	/**
	 * Copy xmp sidecar files and collect them into one "XMP manifest"
	 * @author Sebastian Cuy
	 * @author Daniel M. de Oliveira
	 * @author Thomas Kleinke
	 * @throws IOException
	 */
	private void collectXMP() throws IOException {
		
		logger.debug("collectXMP");
		
		String repPath = object.getPath("newest").toString();
			
		List<DAFile> newestFiles = object.getNewestFilesFromAllRepresentations("xmp");
		List<DAFile> newestXmpFiles = new ArrayList<DAFile>();
		for (DAFile dafile : newestFiles) {
			if (dafile.getRelative_path().toLowerCase().endsWith(".xmp"))
				newestXmpFiles.add(dafile);
		}
			
		logger.debug("found {} xmp files", newestXmpFiles.size());
		File rdfFile = new File(repPath + "/XMP.rdf");
		XmpCollector.collect(newestXmpFiles, rdfFile);	
		logger.debug("collecting files in path: {}", repPath);
		DAFile xmpFile = new DAFile(object.getLatestPackage(),object.getPath("newest").getLastElement(),"XMP.rdf");
		object.getLatestPackage().getFiles().add(xmpFile);
		object.getLatestPackage().getEvents().add(createCreateEvent(xmpFile));		
	}
	
	private Event createCreateEvent(DAFile targetFile) {
		
		Event e = new Event();
		e.setTarget_file(targetFile);
		e.setType("CREATE");
		e.setDate(new Date());
		e.setAgent_type("NODE");
		e.setAgent_name(object.getTransientNodeRef().getName());
		return e;
	}
	
}
