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
import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.*;
import org.apache.commons.io.FileUtils;

import de.uzk.hki.da.pkg.ArchiveBuilder;
import de.uzk.hki.da.pkg.ArchiveBuilderFactory;
import de.uzk.hki.da.pkg.NativeJavaTarArchiveBuilder;
import de.uzk.hki.da.utils.FolderUtils;


/**
 * The Class ArchiveBuilderTests.
 *
 * @author Daniel M. de Oliveira
 * @author Thomas Kleinke
 */
public class ArchiveBuilderTests {
	
	/** The Constant baseDirPath. */
	private static final String baseDirPath="src/test/resources/utils/archiveBuilding/";
	
	/** The Constant srcFolder. */
	private static final File srcFolder = new File(baseDirPath + "folder2");
	
	/** The Constant tempFolderPath. */
	private static final String tempFolderPath= baseDirPath + "temp";
	
	/** The Constant destFolder. */
	private static final File destFolder = new File( baseDirPath + "temp/folder2" );
	
	/** The Constant tarFile. */
	private static final File tarFile = new File( baseDirPath + "folder2.tar" );
	
	/** The Constant zipFile. */
	private static final File zipFile = new File( baseDirPath + "folder2.zip" );
	
	/** The Constant tarGZFile. */
	private static final File tarGZFile = new File( baseDirPath + "folder2.tgz");
	
	/** The containers. */
	private static List<File> containers;
	
	/**
	 * Sets the up before class.
	 */
	@BeforeClass
	public static void setUpBeforeClass(){
		containers = new ArrayList<File>();
		containers.add(tarFile);
		containers.add(zipFile);
		containers.add(tarGZFile);
	}
	
	/**
	 * Tear down.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@After
	public void tearDown() throws IOException{
		
		if (zipFile.exists()) zipFile.delete();
		if (tarFile.exists()) tarFile.delete();
		if (tarGZFile.exists()) tarGZFile.delete();
		
		if (destFolder.exists()) FileUtils.deleteDirectory(destFolder);
	}
	
	
	/**
	 * Test source container must still exist after unpacking.
	 *
	 * @throws Exception the exception
	 * @author Daniel M. de Oliveira
	 */
	@Test
	public void testSourceContainerMustStillExistAfterUnpacking() throws Exception{
		
		for (File file:containers){
			
			ArchiveBuilder b = ArchiveBuilderFactory.getArchiveBuilderForFile(file);
			b.archiveFolder(srcFolder, file, false);
			
			destFolder.mkdir();
			b.unarchiveFolder(file, destFolder);
			
			assertTrue(file.exists());
			
			FileUtils.deleteDirectory(destFolder);
		}
	}
		
	
	
	
	/**
	 * Test compress and uncompress with all builders without folder inclusion.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void testCompressAndUncompressWithAllBuildersWithoutFolderInclusion() throws Exception{
		
		for (File file:containers){
			
			ArchiveBuilder b = ArchiveBuilderFactory.getArchiveBuilderForFile(file);
			b.archiveFolder(srcFolder, file, false);
			
			assertTrue(file.exists());
			
			destFolder.mkdir();
			
			b.unarchiveFolder(file , destFolder);
			
			assertTrue(destFolder.exists());
			assertTrue (FolderUtils.compareFolders(srcFolder, destFolder));
			
			FileUtils.deleteDirectory(destFolder);
		}
	}
	
	/**
	 * Test compress and uncompress with all builders with folder inclusion.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void testCompressAndUncompressWithAllBuildersWithFolderInclusion() throws Exception{
		
		for (File file:containers){
			
			ArchiveBuilder b = ArchiveBuilderFactory.getArchiveBuilderForFile(file);
			b.archiveFolder(srcFolder, file, true);
			
			assertTrue(file.exists());
			
			b.unarchiveFolder(file, new File(tempFolderPath));
			
			assertTrue(destFolder.exists());
			assertTrue (FolderUtils.compareFolders(srcFolder, destFolder));
			
			FileUtils.deleteDirectory(destFolder);
		}
	}
	
	/**
	 * Test native java tar without folder inclusion.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void testNativeJavaTarWithoutFolderInclusion() throws Exception {
		
		destFolder.mkdir();
		
		NativeJavaTarArchiveBuilder tb = new NativeJavaTarArchiveBuilder();
		tb.archiveFolder(srcFolder, tarFile, false);
		tb.unarchiveFolder(tarFile, destFolder);
		
		assertTrue(FolderUtils.compareFolders(srcFolder, destFolder));
	}
	
	/**
	 * Test native java tar with folder inclusion.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void testNativeJavaTarWithFolderInclusion() throws Exception {
		
		destFolder.mkdir();
		
		NativeJavaTarArchiveBuilder tb = new NativeJavaTarArchiveBuilder();
		tb.archiveFolder(srcFolder, tarFile, true);
		tb.unarchiveFolder(tarFile, new File(tempFolderPath));
		
		assertTrue((destFolder).exists());
		assertTrue(FolderUtils.compareFolders(srcFolder, destFolder));			
	}
}
