/**
  DA-NRW Software Suite | ContentBroker
  Copyright (C) 2013 Historisch-Kulturwissenschaftliche Informationsverarbeitung
  Universität zu Köln
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

package de.uzk.hki.da.utils;

import org.apache.commons.io.FilenameUtils;

public class SidecarUtils {

	
	/**
	 * TODO remove duplicate from UnpackAction
	 * @param file
	 * @return
	 * @author Daniel M. de Oliveira
	 */
	public static boolean hasSidecarExtension(String filename,String sidecarExts){

		String[] sidecarExtensions;
		if (sidecarExts.contains(","))
			sidecarExtensions = sidecarExts.split(",");
		else
			sidecarExtensions = sidecarExts.split(";");
		
		for (int i=0;i<sidecarExtensions.length;i++){
			if (FilenameUtils.getExtension(filename).toLowerCase().equals(sidecarExtensions[i].toLowerCase())){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Checks if is sidecar file.
	 *
	 * @param filename the filename
	 * @param extensionList the extension list
	 * @return true, if is sidecar file
	 * @author Thomas Kleinke
	 */
	public static boolean isSidecarFile(String filename, String extensionList) {
		
		int position = 0;
		while (position < extensionList.length())
		{
			int index = extensionList.indexOf(';', position);
			if (index == -1)
				index = extensionList.length();
			String extension = extensionList.substring(position, index).toLowerCase();
			position = index + 1;
			
			if (filename.toLowerCase().endsWith("." + extension))
				return true;			
		}
		
		return false;
	}
}
