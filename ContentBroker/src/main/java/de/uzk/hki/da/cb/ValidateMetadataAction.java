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

import org.apache.commons.lang.NotImplementedException;

import de.uzk.hki.da.core.ConfigurationException;
import de.uzk.hki.da.core.UserException;
import de.uzk.hki.da.repository.RepositoryException;
import de.uzk.hki.da.service.PackageTypeDetectionService;
import de.uzk.hki.da.utils.FilesAndConstants;

/**
 * Detects the package type of an object
 * @author Daniel M. de Oliveira
 */
public class ValidateMetadataAction extends AbstractAction {
	
	private PackageTypeDetectionService ptds = null;
	
	@Override
	boolean implementation() throws FileNotFoundException, IOException,
			UserException, RepositoryException {
//		if (ptds==null) throw new ConfigurationException("ptds "+FilesAndConstants.ERROR_NOTCONFIGURED);
//		if (ptds==null) return true;
		
		PackageTypeDetectionService ptds = new PackageTypeDetectionService(object.getLatestPackage());
		
		String packageType = ptds.getPackageType();
		String metadataFile = ptds.getMetadataFile();
		if (packageType == null || metadataFile == null) {
			logger.warn("Could not determine package type. ");
		} else {
			
			object.setPackage_type(packageType);
			object.setMetadata_file(metadataFile);
		}
		
		// scan all the newest files
		return true;
	}

	@Override
	void rollback() throws Exception {
		throw new NotImplementedException(FilesAndConstants.ERROR_ROLLBACK_NOT_IMPLEMENTED);
	}

//	public PackageTypeDetectionService getPtds() {
//		return ptds;
//	}
//
//	public void setPtds(PackageTypeDetectionService ptds) {
//		this.ptds = ptds;
//	}
}
