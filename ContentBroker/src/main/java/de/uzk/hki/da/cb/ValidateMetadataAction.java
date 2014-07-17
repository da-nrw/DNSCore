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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.NotImplementedException;

import de.uzk.hki.da.core.UserException;
import de.uzk.hki.da.core.UserException.UserExceptionId;
import de.uzk.hki.da.model.DAFile;
import de.uzk.hki.da.model.Package;
import de.uzk.hki.da.repository.RepositoryException;
import de.uzk.hki.da.utils.C;

/**
 * Detects the package type of an object.
 * It can be one of
 * <li>METS
 * <li>EAD
 * <li>XMP
 * <li>LIDO
 * 
 * @author Daniel M. de Oliveira
 */
public class ValidateMetadataAction extends AbstractAction {
	
	private String packageType;
	private String metadataFile;
	
	@Override
	boolean implementation() throws FileNotFoundException, IOException,
			UserException, RepositoryException {
		
		detect(object.getLatestPackage());
		if (packageType == null || metadataFile == null) {
			logger.warn("Could not determine package type. ");
		} else {
			
			object.setPackage_type(packageType);
			object.setMetadata_file(metadataFile);
		}
		
		return true;
	}

	/**
	 * @throws UserException
	 * @author Daniel M. de Oliveira 
	 */
	private void detect(Package pkg){
		
		if (getFilesWithPUID(pkg.getFiles(), C.EAD_PUID).size()>=2){
			throw new UserException(UserExceptionId.DUPLICATE_METADATA_FILE,"duplicate EAD");
		}
		if (getFilesWithPUID(pkg.getFiles(), C.EAD_PUID).size()==1){
			metadataFile=getFilesWithPUID(pkg.getFiles(), C.EAD_PUID).get(0).getRelative_path();
			packageType=C.EAD;
			return;
		}
		if (getFilesWithPUID(pkg.getFiles(), C.METS_PUID).size()>1){
			throw new UserException(UserExceptionId.DUPLICATE_METADATA_FILE,"duplicate METS");
		}  
		if (getFilesWithPUID(pkg.getFiles(), C.METS_PUID).size()==1){
			metadataFile=getFilesWithPUID(pkg.getFiles(), C.METS_PUID).get(0).getRelative_path();
			packageType=C.METS;
			return;
		}
		// LIDO
		// XMP.rdf
	}
	
	
	private List<DAFile> getFilesWithPUID(List<DAFile> files,String PUID){
		List<DAFile> result = new ArrayList<DAFile>();
		
		for (DAFile f:files){
			if (PUID.equals(f.getFormatPUID())) {
				result.add(f);
			}
		}
		return result;
	}
	
	
	@Override
	void rollback() throws Exception {
		object.setMetadata_file(null);
		object.setPackage_type(null);
	}
}
