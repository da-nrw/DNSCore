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
package de.uzk.hki.da.format;

import java.io.File;

import de.uzk.hki.da.core.UserException;
import de.uzk.hki.da.core.UserException.UserExceptionId;
import de.uzk.hki.da.utils.Path;
import de.uzk.hki.da.utils.RelativePath;

/**
 * The simplest possible implementation of FileWithFileFormat.
 * 
 * @author Daniel M. de Oliveira
 */
public class SimpleFileWithFileFormat implements FileWithFileFormat {

	File file;
	String formatPUID;
	String secondary;
	UserExceptionId userExceptionId;

	public SimpleFileWithFileFormat(File f){
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
	public String getSubformatIdentifier() {
		return secondary;
	}

	@Override
	public void setSubformatIdentifier(String subformatIdentifier) {
		this.secondary = subformatIdentifier;
	}

	@Override 
	public Path getPath() {
		return new RelativePath(file.getPath().toString());
	}

	@Override
	public UserExceptionId getUserExceptionId() {
		return userExceptionId;
	}

	@Override
	public void setUserExceptionId(UserExceptionId e) {
		userExceptionId =e;
		
	}
}
