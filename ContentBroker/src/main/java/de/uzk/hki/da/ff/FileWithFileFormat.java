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
package de.uzk.hki.da.ff;

import java.io.File;

/**
 * The simplest possible implementation of FileWithFileFormat.
 * 
 * @author Daniel M. de Oliveira
 */
public class FileWithFileFormat implements IFileWithFileFormat {

	File file;
	String formatPUID;
	String secondary;

	public FileWithFileFormat(File f){
		this.file=f;
	}
	
	@Override
	public String getFormatPUID() {
		return formatPUID;
	}

	@Override
	public void setFormatPUID(String formatPUID) {
		this.formatPUID = formatPUID;
	}

	@Override
	public String getFormatSecondaryAttribute() {
		return secondary;
	}

	@Override
	public void setFormatSecondaryAttribute(String formatSecondaryAttribute) {
		this.secondary = formatSecondaryAttribute;
		
	}

	@Override
	public File toRegularFile() {

		return file;
	}

}
