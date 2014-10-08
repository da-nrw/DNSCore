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


/**
 * Implementation note : mark the constructors of the implementations as private in order to enforce 
 * instantiation through the {@link ArchiveBuilderFactory}.
 * @author Daniel M. de Oliveira
 * @author Thomas Kleinke
 */
public interface ArchiveBuilder {
	
	/** 
	 * Expands the contents of an archive/container into the destFolder. The destFolder must exist.
	 * After the unpacking the srcTar still exists on its original place
	 * 
	 * Implementation notes: Make sure that even when the unpacking can't be done and an exception
	 * gets thrown the original file remains in place. There has been a bug like that that under special
	 * occurrences due to implementation issues in ZIPArchiveBuilder that was not the case and the original
	 * file got destroyed. This was not quiet unit-testable. Therefore the comment here.
	 * 
	 * @param srcContainer the container
	 * @param destFolder
	 * @throws Exception
	 */
	public void unarchiveFolder(File srcContainer, File destFolder) throws Exception;
	
	/**
	 * Creates an archive/container file preserving the directory structure of the srcFolder.
	 * 
	 * includeFolder == true:
	 * If the original srcFolder is located at /home/user/folder1, the resulting archive will
	 * contain folder1 as its root level entry and all its subfolders and files will be stored as
	 * entries below that entry.
	 * 
	 * includeFolder == false:
	 * If the original srcFolder is located at /home/user/folder1, the resulting archive will
	 * contain all subfolders and files of folder1 on the first level.
	 * 
	 * @param srcFolder the folder which contains the files that should be stored.
	 * @param container the file the files from srcFolder should be stored into. 
	 * @param includeFolder determines if the srcFolder itself will be archived
	 * @throws Exception
	 */
	public abstract void archiveFolder(File srcFolder, File destFile, boolean includeFolder) throws Exception;
}
