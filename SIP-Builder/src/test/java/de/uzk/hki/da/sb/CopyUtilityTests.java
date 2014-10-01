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
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.uzk.hki.da.pkg.CopyUtility;
import de.uzk.hki.da.sb.SIPFactory.SipBuildingProcess;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Class under test: CopyUtility
 * 
 * @author Thomas Kleinke
 */
public class CopyUtilityTests {

	File sourceFolder = new File("src/test/resources/CopyUtilityTests/source");
	File destinationFolder = new File("src/test/resources/CopyUtilityTests/destination");
	CopyUtility copyUtility;

	@Before
	public void setUp() {
		
		destinationFolder.mkdir();
		
		SipBuildingProcess process = mock(SipBuildingProcess.class);
		when(process.isAborted()).thenReturn(false);
		
		copyUtility = new CopyUtility();
		copyUtility.setSipBuildingProcess(process);
	}
	
	/**
	 * @throws IOException
	 */
	@After
	public void tearDown() throws IOException {
	
		FileUtils.deleteDirectory(destinationFolder);	
	}
			
	/**
	 * Method under test: CopyUtility.copyDirectory()
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCopyDirectory() throws IllegalArgumentException, IOException {
		
		copyUtility.copyDirectory(sourceFolder, destinationFolder);
		
		assertTrue(new File(destinationFolder, "document.pdf").exists());
		assertTrue(new File(destinationFolder, "image.tif").exists());
		assertTrue(new File(destinationFolder, "text.txt").exists());
		assertTrue(new File(destinationFolder, "subfolder/image_2.tif").exists());
	}
	
	/**
	 * Method under test: CopyUtility.copyDirectory()
	 * 
	 * @throws Exception
	 */
	@Test
	public void testIgnoreFileExtensions() throws IllegalArgumentException, IOException {
		
		List<String> forbiddenFileExtensions = new ArrayList<String>();
		forbiddenFileExtensions.add("pdf");
		forbiddenFileExtensions.add("txt");
		
		copyUtility.copyDirectory(sourceFolder, destinationFolder, forbiddenFileExtensions);
		
		assertFalse(new File(destinationFolder, "document.pdf").exists());
		assertTrue(new File(destinationFolder, "image.tif").exists());
		assertFalse(new File(destinationFolder, "text.txt").exists());
		assertTrue(new File(destinationFolder, "subfolder/image_2.tif").exists());
	}
}
