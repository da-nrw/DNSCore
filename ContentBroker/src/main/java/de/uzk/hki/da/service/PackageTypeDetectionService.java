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
package de.uzk.hki.da.service;

import java.util.List;

import de.uzk.hki.da.model.DAFile;
import de.uzk.hki.da.model.Package;


/**
 * The Class PackageTypeDetectionService.
 */
public class PackageTypeDetectionService {
	
	/** The package type. */
	private String packageType = null;
	
	/** The metadata file. */
	private String metadataFile = null;		
	
	/**
	 * Instantiates a new package type detection service.
	 *
	 * @param pkg the pkg
	 */
	public PackageTypeDetectionService(Package pkg) {
		List<DAFile> files = pkg.getFiles();
		for (DAFile file : files) {
			if ("danrw-fmt/1".equals(file.getFormatPUID())) {
				setMetadataFile(file.getRelative_path());
				setPackageType("METS"); // METS files can be part of EAD packages, so continue
			} else if ("danrw-fmt/2".equals(file.getFormatPUID())) {
				setMetadataFile(file.getRelative_path());
				setPackageType("EAD");
				break; // every package containing an EAD file is of type EAD
			} else if ("danrw-fmt/3".equals(file.getFormatPUID())) {
				setMetadataFile("XMP.rdf");
				setPackageType("XMP");
				break; // every package containing an XMP file is of type XMP
			} else if ("danrw-fmt/4".equals(file.getFormatPUID())) {
				setMetadataFile(file.getRelative_path());
				setPackageType("LIDO");
				break; // every package containing a LIDO file is of type LIDO
			}
		}
	}

	/**
	 * Gets the package type.
	 *
	 * @return the package type
	 */
	public String getPackageType() {
		return packageType;
	}

	/**
	 * Sets the package type.
	 *
	 * @param packageType the new package type
	 */
	public void setPackageType(String packageType) {
		this.packageType = packageType;
	}

	/**
	 * Gets the metadata file.
	 *
	 * @return the metadata file
	 */
	public String getMetadataFile() {
		return metadataFile;
	}

	/**
	 * Sets the metadata file.
	 *
	 * @param metadataFile the new metadata file
	 */
	public void setMetadataFile(String metadataFile) {
		this.metadataFile = metadataFile;
	}

}
