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

package de.uzk.hki.da.pkg;

import java.io.File;

public class ArchiveBuilderFactory {

	/**
	 * Gives you the appropriate ArchiveBuilder implementation 
	 * based on the file extension of the file that should be compressed/uncompressed.
	 * <ul>
	 * <li>TarBuilder for .tar
	 * <li>TarGZBuilder for .tar.gz or .tgz
	 * <li>ZipBuilder for .zip
	 * </ul>
	 * Can deal with uppercase extensions also.
	 * @author Daniel M. de Oliveira
	 * @param file
	 * @return the appropriate builder or null if none found.
	 */
	public static ArchiveBuilder getArchiveBuilderForFile(File file){
		
		String filename = file.getName().toLowerCase();
		
		if ((filename.endsWith(".tgz"))
				||(filename).endsWith("tar.gz")) 
			return new TarGZArchiveBuilder();
		
		if (filename.endsWith(".zip"))
			return new ZipArchiveBuilder();
		
		if (filename.endsWith(".tar")){
			return new NativeJavaTarArchiveBuilder();
		}
		
		return null;	
	}
}
