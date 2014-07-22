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
package de.uzk.hki.da.cb;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.NotImplementedException;

import de.uzk.hki.da.core.UserException;
import de.uzk.hki.da.utils.Path;

/**
 * For a given object 
 * <li>deletes the object folder from the UserArea
 * <li>deletes any existing packages belonging to the Object from the IngestArea
 * @author Daniel M. de Oliveira
 */
public class DeleteObjectAction extends AbstractAction {

	@Override
	boolean implementation() throws FileNotFoundException, IOException,
			UserException {

		if (object.getPackages().size()==1){
			logger.info("Deleting object from database");
			DELETEOBJECT=true;
		}
		else 
		if (object.getPackages().size()>1){
			object.getPackages().remove(object.getLatestPackage());
		}
		
		logger.info("Deleting object from WorkArea: "+object.getPath());
		FileUtils.deleteDirectory(object.getPath().toFile());
		
		File fileInWorkArea = Path.makeFile(
				object.getTransientNodeRef().getWorkAreaRootPath(),"work",
				object.getContractor().getShort_name(),object.getLatestPackage().getContainerName());
		if (fileInWorkArea.exists()) {
			logger.info("Delete latest package from WorkArea: " + fileInWorkArea );
			fileInWorkArea.delete();
		}
		
		File fileInIngestArea = Path.makeFile(
				object.getTransientNodeRef().getIngestAreaRootPath(),
				object.getContractor().getShort_name(),object.getLatestPackage().getContainerName());
		if (fileInIngestArea.exists()) {
			logger.info("Delete latest package from WorkArea: " + fileInIngestArea );
			fileInIngestArea.delete();
		}
		
		
		return true;
	}

	@Override
	void rollback() throws Exception {
		throw new NotImplementedException("No rollback implemented for this action");
	}

}
