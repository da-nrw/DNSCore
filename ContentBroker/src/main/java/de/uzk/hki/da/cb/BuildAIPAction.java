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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uzk.hki.da.utils.BagitUtils;

/**
 * <ol>
 * <li>Copies derivates for presentation out of the package
 * <li>Deletes unnecessary representations which in case of deltas have been added.
 * <li>Adds bagit files
 * </ol>
 * @author Daniel M. de Oliveira
 */
public class BuildAIPAction extends AbstractAction {

	static final Logger logger = LoggerFactory.getLogger(BuildAIPAction.class);
	
	@Override
	boolean implementation() {

		String relativePathOfSource = object.getContractor().getShort_name()+"/" + object.getIdentifier() +"/";
		String physicalPackagePathOfSource = localNode.getWorkAreaRootPath() + "/work/" + relativePathOfSource;
		
		
		
		logger.info ( "Preparing AIP at \"" + physicalPackagePathOfSource +"\" for archival." );
		deleteOldPremisFile();
		deleteUnnecessaryReps(physicalPackagePathOfSource,job.getRep_name());		
		BagitUtils.buildBagit ( physicalPackagePathOfSource );
		
		return true;
	}

	@Override
	void rollback() throws Exception {
		
		logger.debug("Deleting bagit files from source");
		try{
		
			new File(object.getPath()+"/bag-info.txt").delete();
			new File(object.getPath()+"/bagit.txt").delete();
			new File(object.getPath()+"/manifest-md5.txt").delete();
			new File(object.getPath()+"/tagmanifest-md5.txt").delete();
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
	void deleteUnnecessaryReps(String physicalPackagePathOfSource,String repName){
		String children[] = new File(physicalPackagePathOfSource + "data").list();
		for (int i=0;i<children.length;i++){
			if (!children[i].contains(repName) &&
					new File(physicalPackagePathOfSource+"data/"+children[i]).isDirectory()) {
				try {
					FileUtils.deleteDirectory(new File(physicalPackagePathOfSource+"data/"+children[i]));
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

		File oldPremis = new File(object.getDataPath() + "premis_old.xml");
		
		logger.debug("Deleting " + oldPremis.getAbsolutePath());
				
		oldPremis.delete();
	}
}
