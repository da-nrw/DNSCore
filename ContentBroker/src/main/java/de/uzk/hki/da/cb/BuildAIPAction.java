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
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import de.uzk.hki.da.action.AbstractAction;
import de.uzk.hki.da.core.ConfigurationException;
import de.uzk.hki.da.core.Path;
import de.uzk.hki.da.core.RelativePath;
import de.uzk.hki.da.pkg.BagitUtils;

/**
 * <ol>
 * <li>Copies derivates for presentation out of the package
 * <li>Deletes unnecessary representations which in case of deltas have been added.
 * <li>Adds bagit files
 * </ol>
 * @author Daniel M. de Oliveira
 */
public class BuildAIPAction extends AbstractAction {

	@Override
	public void checkActionSpecificConfiguration() throws ConfigurationException {
		// Auto-generated method stub
	}

	@Override
	public boolean implementation() {

		Path relativePathOfSource = new RelativePath(object.getContractor().getShort_name(),object.getIdentifier());
		Path physicalPackagePathOfSource = Path.make(localNode.getWorkAreaRootPath(),"work",relativePathOfSource);
		
		logger.info ( "Preparing AIP at \"" + physicalPackagePathOfSource +"\" for archival." );
		deleteOldPremisFile();
		deleteUnnecessaryReps(physicalPackagePathOfSource,job.getRep_name());		
		BagitUtils.buildBagit ( physicalPackagePathOfSource.toString() );
		
		return true;
	}

	@Override
	public void rollback() throws Exception {
		
		logger.debug("Deleting bagit files from source");
		try{
		
			Path.make(object.getPath(),"bag-info.txt").toFile().delete();
			Path.make(object.getPath(),"bagit.txt").toFile().delete();
			Path.make(object.getPath(),"manifest-md5.txt").toFile().delete();
			Path.make(object.getPath(),"tagmanifest-md5.txt").toFile().delete();
		}catch(Exception e){
			logger.error("Couldn't delete bagit files");
		}
	}
	
	
	
	
	
	
	/**
	 * Only the reps with the submitted files and the one with the conversion are to be 
	 * packaged into the final container. This plays a role in case of deltas where prior 
	 * to conversions all previous reps of an object get loaded onto the local disk.
	 * Logs on info level an entry for each representation destroyed.
	 * @author Daniel M. de Oliveira
	 */
	void deleteUnnecessaryReps(Path physicalPackagePathOfSource,String repName){
		
		String children[] = Path.make(physicalPackagePathOfSource,"data").toFile().list();
		for (int i=0;i<children.length;i++){
			if (!children[i].contains(repName) &&
					Path.make(physicalPackagePathOfSource,"data",children[i]).toFile().isDirectory()) {
				try {
					FileUtils.deleteDirectory(Path.make(physicalPackagePathOfSource,"data",children[i]).toFile());
				} catch (IOException e) {
					throw new RuntimeException("Couldn't delete folder: "+children[i]);
				}
				logger.info("Deleting previosly loaded representation: {}",children[i]);
			}
		}
	}
	
	
	/** 
	 * Deletes premis_old.xml if it exists (which is the case if the package is a delta package).
	 * 
	 *  @author Thomas Kleinke
	 */
	void deleteOldPremisFile() {

		File oldPremis = Path.make(object.getDataPath(),"premis_old.xml").toFile();
		
		logger.debug("Deleting " + oldPremis.getAbsolutePath());
				
		oldPremis.delete();
	}

	@Override
	public void checkSystemStatePreconditions() throws IllegalStateException {
		// TODO Auto-generated method stub
		
	}
}
