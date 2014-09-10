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

package de.uzk.hki.da.core;

import java.io.File;

import org.junit.Test;

import de.uzk.hki.da.pkg.BagitUtils;


/**
 * The Class BagitUtilsTests.
 */
public class BagitUtilsTests {

	/**
	 * Builds the bagit.
	 */
	@Test
	public void buildBagit(){
		String packagePath = "src/test/resources/utils/BagitUtilsTests/pack";
		new File ( packagePath + "/" + "bagit.txt" ).delete();
		new File ( packagePath + "/" + "bag-info.txt" ).delete();
		new File ( packagePath + "/" + "manifest-md5.txt" ).delete();
		new File ( packagePath + "/" + "tagmanifest-md5.txt" ).delete();

		BagitUtils.buildBagit(packagePath);
		
		assert(new File ( packagePath + "/" + "bagit.txt" ).exists());
		assert(new File ( packagePath + "/" + "bag-info.txt" ).exists());
		assert(new File ( packagePath + "/" + "manifest-md5.txt" ).exists());
		assert(new File ( packagePath + "/" + "tagmanifest-md5.txt" ).exists());
	}	
}
