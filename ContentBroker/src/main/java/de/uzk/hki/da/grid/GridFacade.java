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

/**
 * @auhor Jens Peters 27.11.2012
 */
package de.uzk.hki.da.grid;

import java.io.File;
import java.io.IOException;

import de.uzk.hki.da.model.StoragePolicy;


/**
 * The Interface GridFacade.
 *
 * @author Jens Peters
 * The Interface to operate with DataGrid Software Systems in decoupled
 * environments. Seperates the DataGrid operations from the archival workflow done before.
 * Should be used as unique point for Ingestion and Retrieval to the Data Grid.
 */
public interface GridFacade {
	
	/**
	 * Asynchronous call.
	 * Puts a file from the local file system into the grid. 
	 *  
	 * @param file full path to a file location which should be ingested into the grid
	 * @param gridPath address of the target file name in grid (excluding zone prefix). 
	 * 
	 * If @param gridPath parent Folder does not exist, it will be created.
	 * 
	 * @return <strong>true</strong> if file has been successfully copied to the grid cache area,
	 * the file has been successfully registered at the working resource and the replication command has been fired.<br>
	 * <strong>false</strong> if one of the abovementioned conditions has not been met. put can be called again after returning false.
	 * @throws IOException in case grid file already exists and has already lza repls. In this case we interpret the
	 * call of put as beeing malicious. This is the case when calling put after put has been already called and returned true.
	 * 
	 * Store file on the DataGrid, replicate  to repl_dests
	 * Returns true, if the store and delegation of replication process succeeds.
	 * @author: Jens Peters
	 * @author: Daniel M. de Oliveira
	 */
	abstract boolean put( File file, String gridPath, StoragePolicy sp) throws java.io.IOException;
	
	/**
	 * Retrieve file from the DataGrid.
	 *
	 * @param destination full path to a file location the retrieved object gets written to. 
	 * destination should not point to a folder.
	 * @param gridFileAdress address of source file in grid (excluding zone prefix)
	 * 
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @author: Jens Peters
	 */
	abstract void get(File destination, String gridFileAdress) throws java.io.IOException;
	
	
	/**
	 * Determine if a file is valid on the Grid.
	 * The Data Grid SW has to perform the necessary checks to fulfill
	 * this requirement.
	 *
	 * @param gridFileAddress address of source file in grid (excluding zone prefix)
	 * @return true, if is valid
	 * @author: Jens Peters
	 */
	abstract boolean isValid(String gridFileAddress);
	
	/**
	 * Determines if the given storage Policy is being achieved.
	 *
	 * @param address_dest the address_dest
	 * @param minNodes the min nodes
	 * @return true, if successful
	 */
	abstract boolean storagePolicyAchieved(String gridPath, StoragePolicy sp);

	/**
	 * Returnes fileSize of given file.
	 *
	 * @param address_dest the address_dest
	 * @return the file size
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	
	abstract long getFileSize(String address_dest) throws java.io.IOException ; 
}
