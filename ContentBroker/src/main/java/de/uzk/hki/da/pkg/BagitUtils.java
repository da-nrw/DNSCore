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

import gov.loc.repository.bagit.Bag;
import gov.loc.repository.bagit.BagFactory;
import gov.loc.repository.bagit.PreBag;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Builds a BagIt around a folder which contains only one subfolder (data) which itself can contain various data.
 *
 * @param packagePath can be absolute or relative
 * @author Daniel M. de Oliveira
 */
public class BagitUtils {
	
	/** The Constant logger. */
	static final Logger logger = LoggerFactory.getLogger(BagitUtils.class);
	
	public static void buildBagit(String packagePath){
		
		if (new File(packagePath + "/" + "bagit.txt").exists())
			new File(packagePath + "/" + "bagit.txt").delete();
		if (new File(packagePath + "/" + "bag-info.txt").exists())
			new File(packagePath + "/" + "bag-info.txt").delete();
		if (new File(packagePath + "/" + "manifest-md5.txt").exists())
			new File(packagePath + "/" + "manifest-md5.txt").delete();
		if (new File(packagePath + "/" + "tagmanifest-md5.txt").exists())
			new File(packagePath + "/" + "tagmanifest-md5.txt").delete();
		
		BagFactory bagFactory = new BagFactory();
		
		PreBag preBag = bagFactory.createPreBag(new File(packagePath));
		preBag.makeBagInPlace(BagFactory.LATEST, false); 
		
		Bag bag = bagFactory.createBag(new File(packagePath));
		if(!bag.verifyValid().isSuccess()) throw new RuntimeException("BagIt couldn't be validated after its creation");
		try {
			bag.close();
		} catch (IOException e) {
			logger.error("Error closing Bag at " + packagePath + " " + e.getStackTrace());
		}
	}

}
