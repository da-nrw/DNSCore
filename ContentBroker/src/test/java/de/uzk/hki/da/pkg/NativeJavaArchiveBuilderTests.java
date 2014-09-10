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

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.uzk.hki.da.pkg.NativeJavaTarArchiveBuilder;

/**
 * @author Daniel M. de Oliveira
 */
public class NativeJavaArchiveBuilderTests {

	String baseDirPath = "src/test/resources/archivers/NativeJavaArchiveBuilderTests/";
	String sourceDirPath = baseDirPath+"source/";
	String targetDirPath = baseDirPath+"target/";
	String tarPath = baseDirPath+"target/target.tar";

	@Before
	public void setUp(){
		new File(targetDirPath).mkdir();
	}
	
	@After
	public void tearDown() throws IOException{
		new File(tarPath).delete();
		FileUtils.deleteDirectory(new File(targetDirPath));
	}
	
	@Test
	public void testRenameFirstLevelEntry() throws Exception{
		
		NativeJavaTarArchiveBuilder builder = new NativeJavaTarArchiveBuilder();
		builder.setFirstLevelEntryName("target");
		builder.archiveFolder(new File(sourceDirPath), new File(tarPath), true);
		builder.unarchiveFolder(new File(tarPath), new File(targetDirPath));
		
		assertFalse(new File(targetDirPath+"source").exists());
		assertTrue(new File(targetDirPath+"target/pdfSample2.pdf").exists());
		assertTrue(new File(targetDirPath+"target/dip/pdfSample2.pdf").exists());
	}
	
	@Test
	public void dontRestRenameFirstLevelEntry() throws Exception{
		
		NativeJavaTarArchiveBuilder builder = new NativeJavaTarArchiveBuilder();
		builder.archiveFolder(new File(sourceDirPath), new File(tarPath), true);
		builder.unarchiveFolder(new File(tarPath), new File(targetDirPath));
		
		assertFalse(new File(targetDirPath+"target").exists());
		assertTrue(new File(targetDirPath+"source/pdfSample2.pdf").exists());
		assertTrue(new File(targetDirPath+"source/dip/pdfSample2.pdf").exists());
	}
	
	@Test
	public void testResetFirstLevelEntry() throws Exception{
		
		NativeJavaTarArchiveBuilder builder = new NativeJavaTarArchiveBuilder();
		builder.setFirstLevelEntryName("target");
		builder.archiveFolder(new File(sourceDirPath), new File(tarPath), true);
		builder.unarchiveFolder(new File(tarPath), new File(targetDirPath));
		
		assertFalse(new File(targetDirPath+"source").exists());
		assertTrue(new File(targetDirPath+"target/pdfSample2.pdf").exists());
		
		new File(tarPath).delete();
		FileUtils.deleteDirectory(new File(targetDirPath));
		new File(targetDirPath).mkdir();
		
		builder.archiveFolder(new File(sourceDirPath), new File(tarPath), true);
		builder.unarchiveFolder(new File(tarPath), new File(targetDirPath));
		
		assertFalse(new File(targetDirPath+"target").exists());
		assertTrue(new File(targetDirPath+"source/pdfSample2.pdf").exists());
	}
}
