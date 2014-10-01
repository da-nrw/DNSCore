/*
  DA-NRW Software Suite | SIP-Builder
  Copyright (C) 2014 Historisch-Kulturwissenschaftliche Informationsverarbeitung
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

package de.uzk.hki.da.sb;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.uzk.hki.da.pkg.ArchiveBuilder;
import de.uzk.hki.da.sb.SIPFactory.SipBuildingProcess;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Class under test: ArchiveBuilder
 * 
 * @author Thomas Kleinke
 */
public class ArchiveBuilderTests {

	File sourceFolder = new File("src/test/resources/ArchiveBuilderTests/source");
	File destinationFolder = new File("src/test/resources/ArchiveBuilderTests/destination");
	ArchiveBuilder archiveBuilder;

	@Before
	public void setUp() {
		
		destinationFolder.mkdir();
		
		SipBuildingProcess process = mock(SipBuildingProcess.class);
		when(process.isAborted()).thenReturn(false);
		
		ProgressManager progressManager = mock(ProgressManager.class);
		
		archiveBuilder = new ArchiveBuilder();
		archiveBuilder.setSipBuildingProcess(process);
		archiveBuilder.setProgressManager(progressManager);
	}
	
	/**
	 * @throws IOException
	 */
	@After
	public void tearDown() throws IOException {
	
		FileUtils.deleteDirectory(destinationFolder);	
	}
			
	/**
	 * Methods under test: ArchiveBuilder.archiveFolder() and ArchiveBuilder.unarchiveFolder()
	 * 
	 * @throws Exception
	 */
	@Test
	public void testArchiveTar() throws Exception {
		
		File destFile = new File(destinationFolder, "target.tar");
		
		archiveBuilder.archiveFolder(sourceFolder, destFile, true, false);
		
		assertTrue(destFile.exists());
		
		archiveBuilder.unarchiveFolder(destFile, destinationFolder, false);
		
		assertTrue(new File(destinationFolder, "source/test1.txt").exists());
		assertTrue(new File(destinationFolder, "source/test2.txt").exists());
		assertTrue(new File(destinationFolder, "source/test3.txt").exists());
	}
	
	/**
	 * Methods under test: ArchiveBuilder.archiveFolder() and ArchiveBuilder.unarchiveFolder()
	 * 
	 * @throws Exception
	 */
	@Test
	public void testArchiveTgz() throws Exception {
		
		File destFile = new File(destinationFolder, "target.tgz");
		
		archiveBuilder.archiveFolder(sourceFolder, destFile, true, true);
		
		assertTrue(destFile.exists());
		
		archiveBuilder.unarchiveFolder(destFile, destinationFolder, true);
		
		assertTrue(new File(destinationFolder, "source/test1.txt").exists());
		assertTrue(new File(destinationFolder, "source/test2.txt").exists());
		assertTrue(new File(destinationFolder, "source/test3.txt").exists());
	}
}
