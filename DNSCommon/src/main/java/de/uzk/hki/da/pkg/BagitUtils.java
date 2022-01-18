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
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.loc.repository.bagit.creator.BagCreator;
import gov.loc.repository.bagit.domain.Bag;
import gov.loc.repository.bagit.hash.StandardSupportedAlgorithms;
import gov.loc.repository.bagit.hash.SupportedAlgorithm;
import gov.loc.repository.bagit.reader.BagReader;
import gov.loc.repository.bagit.verify.BagVerifier;

/**
 * Builds a BagIt around a folder which contains only one subfolder (data) which itself can contain various data.
 *
 * @param packagePath can be absolute or relative
 * @author Daniel M. de Oliveira
 */
public class BagitUtils {
	
	/** The Constant logger. */
	static final Logger logger = LoggerFactory.getLogger(BagitUtils.class);
	
	public static final SupportedAlgorithm DEFAULT_BAGIT_ALGORITHM=StandardSupportedAlgorithms.SHA512;
	
	public static final RegexFileFilter MANIFEST_FILE_FILTER=new RegexFileFilter("manifest\\-\\w*\\.txt");
	public static final RegexFileFilter TAG_MANIFEST_FILE_FILTER=new RegexFileFilter("tagmanifest\\-\\w*\\.txt");

	public static class RegexFileFilter implements FileFilter{

		final Pattern p;

		public RegexFileFilter(String regex) {
			super();
			p = Pattern.compile(regex);
		}

		public boolean accept(File file) {
			boolean ret=p.matcher(file.getName()).matches();
			return ret;
		}
	}
	
	public static void buildBagit(String packagePath){
		
		if (new File(packagePath + "/" + "bagit.txt").exists())
			new File(packagePath + "/" + "bagit.txt").delete();
		if (new File(packagePath + "/" + "bag-info.txt").exists())
			new File(packagePath + "/" + "bag-info.txt").delete();
		
		File[] fileToDel=new File(packagePath).listFiles(MANIFEST_FILE_FILTER);
		for(File f:fileToDel)
			f.delete();
		fileToDel=new File(packagePath).listFiles(TAG_MANIFEST_FILE_FILTER);
		for(File f:fileToDel)
			f.delete();


		Bag bag;
		try {
			bag = BagCreator.bagInPlace(Paths.get(packagePath), Arrays.asList(DEFAULT_BAGIT_ALGORITHM), false);
		} catch (Exception e) {
			//e.printStackTrace();
			throw new RuntimeException("BagIt couldn't create Bag: "+e.toString());
		}

		BagVerifier sut = new BagVerifier();
		try {
			sut.isValid(bag, false);
		} catch (Exception e) {
			//e.printStackTrace();
			throw new RuntimeException("BagIt couldn't be validated after its creation: "+e.toString());
		}
	}
	
	public static boolean isBagItStyle(File packagePath){
		if (new File(packagePath, "bagit.txt").exists() &&
				new File(packagePath, "bag-info.txt").exists() &&
				packagePath.listFiles(TAG_MANIFEST_FILE_FILTER).length!=0 &&
				packagePath.listFiles(MANIFEST_FILE_FILTER).length!=0)
			return true;

		return false;

	}
	
	/**
	 * @param folder The folder to check
	 * @return true if the BagIt metadata is valid, otherwise false
	 */
	public static boolean bagIsValid(File folder) throws IOException{
		return bagIsValid(folder.toURI().getPath());
	}

	
	
	/**
	 * @param folder The folder to check
	 * @return true if the BagIt metadata is valid, otherwise false
	 */
	public static boolean bagIsValid(String unpackedObjectPath) throws IOException{
		BagReader reader = new BagReader();

		Bag bagVer;
		BagVerifier sut = new BagVerifier();
		try {
			bagVer = reader.read(Paths.get(unpackedObjectPath));
			sut.isValid(bagVer, false);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} 
		return true;
	}

}
