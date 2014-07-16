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
import java.util.List;

import org.apache.commons.lang.NotImplementedException;

import de.uzk.hki.da.core.UserException;
import de.uzk.hki.da.model.DAFile;
import de.uzk.hki.da.model.Package;
import de.uzk.hki.da.repository.RepositoryException;
import de.uzk.hki.da.utils.FilesAndConstants;

/**
 * Detects the package type of an object
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

	
	private void detect(Package pkg){
		
		List<DAFile> files = pkg.getFiles();
		for (DAFile file : files) {
			if ("danrw-fmt/1".equals(file.getFormatPUID())) {
				metadataFile=file.getRelative_path();
				packageType="METS"; // METS files can be part of EAD packages, so continue
			} else if ("danrw-fmt/2".equals(file.getFormatPUID())) {
				metadataFile=file.getRelative_path();
				packageType="EAD";
				break; // every package containing an EAD file is of type EAD
			} else if ("danrw-fmt/3".equals(file.getFormatPUID())) {
				metadataFile="XMP.rdf";
				packageType="XMP";
				break; // every package containing an XMP file is of type XMP
			} else if ("danrw-fmt/4".equals(file.getFormatPUID())) {
				metadataFile=file.getRelative_path();
				packageType="LIDO";
				break; // every package containing a LIDO file is of type LIDO
			}
		}
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
