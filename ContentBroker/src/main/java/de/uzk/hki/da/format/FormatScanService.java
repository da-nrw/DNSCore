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

import java.io.IOException;
import java.util.List;

import org.irods.jargon.core.exception.InvalidArgumentException;

/**
 * @author Daniel M. de Oliveira
 */
interface FormatScanService {

	/**
	 * @param files
	 * @return files, which allows easier testing.
	 * 
	 * @throws IOException if one or more of the files formats 
	 * could not get determined as a result of IO problems. This can has something to do with 
	 * the files to identify, but also with the helper programs used to identify the files.  
	 * 
	 * @throws InvalidArgumentException 
	 */
	List<FileWithFileFormat> identify(List<FileWithFileFormat> files) throws IOException;
	
	boolean healthCheck();
}