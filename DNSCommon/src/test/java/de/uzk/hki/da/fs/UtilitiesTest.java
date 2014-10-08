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

package de.uzk.hki.da.fs;


import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import de.uzk.hki.da.utils.FolderUtils;
import de.uzk.hki.da.utils.Utilities;


/**
 * The Class UtilitiesTest.
 */
public class UtilitiesTest {

	/**
	 * Compare two equal folders.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@Test
	public void compareTwoEqualFolders() throws IOException{
		
		assertTrue(FolderUtils.compareFolders(
				new File("src/test/resources/fs/folderComparison/folder1"),
				new File("src/test/resources/fs/folderComparison/folder2")));
	}
	
	/**
	 * The two folders have the same files as far as the filenames concern. However the
	 * contents of the two contained pdf have been exchanged. So the test has to return
	 * false.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@Test
	public void compareTwoUnequalFolders() throws IOException{
		
		assertFalse(FolderUtils.compareFolders(
				new File("src/test/resources/fs/folderComparison/folder1"),
				new File("src/test/resources/fs/folderComparison/folder3")));
	}
	
	
	/**
	 * This test asserts that the compareFolders for
	 * two unequal folders (different structure) returns false.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@Test
	public void compareTwoUnequalFolders2() throws IOException{
		
		assertFalse(FolderUtils.compareFolders(
				new File("src/test/resources/fs/folderComparison/folder1"),
				new File("src/test/resources/fs/folderComparison/folder3")));
	}
	
	/**
	 * compares to equal folder structures which are placed in different locations on
	 * the file system.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@Test
	public void comparisonOnDifferentLocations() throws IOException{
		assertTrue(FolderUtils.compareFolders(
				new File("src/test/resources/fs/folderComparison/folder1"),
				new File("src/test/resources/fs/folderComparison/alternativeLoc/folder2")));
	}
	
	/**
	 * test written for a bugfix in compareFolders. The bug was that folders on some locations
	 * got listings of contained files that differed in their order. Though the compared folders
	 * were equal, compareFolders returned false
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@Test
	public void copyToTmpAndCompare() throws IOException{
		FileUtils.copyDirectoryToDirectory(
				new File("src/test/resources/fs/folderComparison/folder1"), 
				new File("/tmp/"));
		
		
		assertTrue(FolderUtils.compareFolders(
				new File("src/test/resources/fs/folderComparison/folder1"),
				new File("/tmp/folder1")));
	}
	
	
	/**
	 * Test check for whitespace.
	 */
	@Test
	public void testCheckForWhitespace() {
		
		String testString = "XYZ 123467";
		assertTrue(Utilities.checkForWhitespace(testString));
		
		testString = "XYZ123467";
		assertFalse(Utilities.checkForWhitespace(testString));
	}	
}
