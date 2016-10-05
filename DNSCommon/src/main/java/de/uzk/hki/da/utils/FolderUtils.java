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
 * @param folder
 *            the folder
 * @return the list
 * @author Thomas Kleinke
 */

public class FolderUtils {
	public static final long TIME_TO_END_FILE_OPERATION = 5000;

	public static List<String> listFolderContents(File folder) {
		String[] fileNames = folder.list();

		List<String> fileList = new ArrayList<String>();

		for (String fileName : fileNames) {
			if (new File(folder.getAbsolutePath() + "/" + fileName).isDirectory()) {
				List<String> folderFileNames = listFolderContents(new File(folder.getAbsoluteFile() + "/" + fileName));
				for (String folderFileName : folderFileNames)
					fileList.add(fileName + "/" + folderFileName);
			} else
				fileList.add(fileName);
		}

		return fileList;
	}

	/**
	 * Compares two folders for equality. Compares the folderstructure and the
	 * compares the bitwise equality of the included files.
	 *
	 * @param lhs
	 *            relative path to the first folder
	 * @param rhs
	 *            relative path to the second folder
	 * @return true if lhs equals rhs
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static boolean compareFolders(File lhs, File rhs) throws IOException {

		String rhsParentPath = rhs.getAbsolutePath();
		String lhsParentPath = lhs.getAbsolutePath();

		String lhsChildren[] = lhs.list();
		String rhsChildren[] = rhs.list();

		// Sometimes the order of the listings are not equal even though the
		// files contained are
		Arrays.sort(lhsChildren);
		Arrays.sort(rhsChildren);

		boolean filesAreEqual = true;
		for (int i = 0; i < lhsChildren.length; i++) {

			File lhsf = new File(lhsParentPath + "/" + lhsChildren[i]);
			File rhsf = new File(rhsParentPath + "/" + rhsChildren[i]);

			if (lhsf.isFile()) {

				if (!FileUtils.contentEquals(lhsf, rhsf))
					filesAreEqual = false;
			}

			if (lhsf.isDirectory()) {
				if (!compareFolders(lhsf, rhsf))
					filesAreEqual = false;
			}
		}
		return filesAreEqual;
	}

	/**
	 * Method for execute NFS-aware Directory deletion.
	 * 
	 * @param directory
	 * @throws IOException
	 */
	public static void deleteDirectorySafe(File directory) throws IOException {
		boolean successful = false;
		for (int i = 1; i < 15 & !successful; i++) {
			successful = true;
			try {
				FileUtils.deleteDirectory(directory);
			} catch (IOException e) {
				successful = false;
				System.out.println("FolderUtils::deleteDirectorySafe(): delete " + directory + " fails: " + i+" x "+TIME_TO_END_FILE_OPERATION);
				waitToCompleteNFSAwareFileOperation();
				// Zweiter versuch nach einer Pause
				// FileUtils.deleteDirectory(directory);
			}
		}

	}

	public static boolean deleteQuietlySafe(File file) {
		boolean result = FileUtils.deleteQuietly(file);
		if (!result) {
			waitToCompleteNFSAwareFileOperation();
			result = FileUtils.deleteQuietly(file);
		}
		return result;
	}

	public static void waitToCompleteNFSAwareFileOperation() {
		try {
			Thread.sleep(TIME_TO_END_FILE_OPERATION);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
