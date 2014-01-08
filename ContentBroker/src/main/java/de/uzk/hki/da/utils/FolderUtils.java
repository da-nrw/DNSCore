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
package de.uzk.hki.da.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
/**
 * List folder contents.
 *
 * @param folder the folder
 * @return the list
 * @author Thomas Kleinke
 */

public class FolderUtils {

	public static List<String> listFolderContents(File folder)
	{
		String[] fileNames = folder.list();
		
		List<String> fileList = new ArrayList<String>();
		
		for (String fileName : fileNames)
		{
			if (new File(folder.getAbsolutePath() + "/" + fileName).isDirectory())
			{
				List<String> folderFileNames = listFolderContents(new File(folder.getAbsoluteFile() + "/" + fileName));
				for (String folderFileName : folderFileNames)
					fileList.add(fileName + "/" + folderFileName);
			}
			else
				fileList.add(fileName);
		}
		
		return fileList;
	}

	/**
	 * Compares two folders for equality. Compares the folderstructure and
	 * the compares the bitwise equality of the included files.
	 *
	 * @param lhs relative path to the first folder
	 * @param rhs relative path to the second folder
	 * @return true if lhs equals rhs
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static boolean compareFolders(File lhs, File rhs) throws IOException {
		
		String rhsParentPath= rhs.getAbsolutePath();
		String lhsParentPath= lhs.getAbsolutePath();
		
		String lhsChildren[]= lhs.list();
		String rhsChildren[]= rhs.list();
		
		// Sometimes the order of the listings are not equal even though the files contained are
		Arrays.sort(lhsChildren);
		Arrays.sort(rhsChildren);
				
		boolean filesAreEqual= true;
		for (int i=0;i<lhsChildren.length;i++){
		
			File lhsf= new File(lhsParentPath+"/"+lhsChildren[i]);
			File rhsf= new File(rhsParentPath+"/"+rhsChildren[i]);
			
			if (lhsf.isFile()){
				
				if (!FileUtils.contentEquals(lhsf, rhsf))
					filesAreEqual=false;
			}
			
			if (lhsf.isDirectory()){
				if (!compareFolders(
						lhsf,
						rhsf)
						) filesAreEqual=false;
				}
		}	
		return filesAreEqual;
	}

}
