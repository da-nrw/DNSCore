/*
  DA-NRW Software Suite | ContentBroker
  Copyright (C) 2014 LVRInfoKom
  Landschaftsverband Rheinland

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

package de.uzk.hki.da.format;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Matchers.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import static de.uzk.hki.da.test.TC.*;
import de.uzk.hki.da.util.Path;
import de.uzk.hki.da.utils.CommandLineConnector;
import de.uzk.hki.da.utils.ProcessInformation;



/**
 * @author Daniel M. de Oliveira
 */
public class JhoveMetadataExtractorTests {

	private static final String TMP_OUT_TXT = "/tmp/out.txt";

	private static final Path TEST_DIR = Path.make(TEST_ROOT_FORMAT,"JhoveMetadataExtractor");
	
	CommandLineConnector cli = mock(CommandLineConnector.class);
	JhoveMetadataExtractor jhove = new JhoveMetadataExtractor();

	@Before
	public void setUp() {
		jhove.setCli(cli);
	}
	
	
	@Test
	public void InputFileDoesNotExist() {
		try {
			jhove.extract(Path.makeFile(TEST_DIR,"notexistent.xml"), 
					new File(TMP_OUT_TXT));
			fail();
		} 
		catch (FileNotFoundException expected) {}
		catch (ConnectionException e) {fail();}
		catch (IOException e) {fail();} 
	}
	
	@Test
	public void targetFolderDoesNotExist() {
		try {
			jhove.extract(Path.makeFile(TEST_DIR,"vda3.XML"), 
					Path.makeFile(TEST_DIR,"dirNotExists","outputfile.txt"));
			fail();
		} 
		catch (FileNotFoundException expected) {}
		catch (ConnectionException e) {fail();}
		catch (IOException e) {fail();} 
	}
	
	
	
	@Test
	public void extractSuccessful() throws IOException {
		
		ProcessInformation pi=new ProcessInformation();
		pi.setExitValue(0);
		when(cli.runCmdSynchronously((String[])anyObject(),(File)anyObject(),anyInt())).thenReturn(pi);
		
		try {
			jhove.extract(Path.makeFile(TEST_DIR,"vda3.XML"), new File(TMP_OUT_TXT));
		} 
		catch (IOException e) { fail(); } 
		catch (ConnectionException e) { fail(); }
	}
	
	@Test
	public void extractNotSuccessfulWithErrorCodes() throws IOException {
		
		ProcessInformation pi=new ProcessInformation();
		pi.setExitValue(1);
		ProcessInformation pi2=new ProcessInformation();
		pi2.setExitValue(1);
		when(cli.runCmdSynchronously((String[])anyObject(),(File)anyObject(),anyInt()))
			.thenReturn(pi)
			.thenReturn(pi2);
		
		try {
			jhove.extract(Path.makeFile(TEST_DIR,"vda3.XML"), new File(TMP_OUT_TXT));
			fail();
		} 
		catch (IOException e) { fail(); } 
		catch (ConnectionException e) {}
	}
	
	@Test
	public void extractSuccessfulAfterRetry() throws IOException {
		
		ProcessInformation pi=new ProcessInformation();
		pi.setExitValue(1);
		ProcessInformation pi2=new ProcessInformation();
		pi2.setExitValue(0);
		when(cli.runCmdSynchronously((String[])anyObject(),(File)anyObject(),anyInt()))
			.thenReturn(pi)
			.thenReturn(pi2); // let it work with the simple version
		
		try {
			jhove.extract(Path.makeFile(TEST_DIR,"vda3.XML"), new File(TMP_OUT_TXT));
		} 
		catch (IOException e) { fail(); } 
		catch (ConnectionException e) { fail(); }
	}
	
	
	
	@Test
	public void timeout() throws IOException {
		
		when(cli.runCmdSynchronously((String[])anyObject(),(File)anyObject(),anyLong()))
			.thenThrow(new IOException("timeout"))
			.thenThrow(new IOException("timeout"));
		try {
			jhove.extract(Path.makeFile(TEST_DIR,"vda3.XML"), new File(TMP_OUT_TXT));
			fail();
		} catch (ConnectionException e) {}
	}
	
	@Test
	public void timeoutNotHappeningWithRetry() throws IOException {
		
		ProcessInformation pi=new ProcessInformation();
		pi.setExitValue(0);
		when(cli.runCmdSynchronously((String[])anyObject(),(File)anyObject(),anyLong()))
			.thenThrow(new IOException("timeout"))
			.thenReturn(pi); // let it work with the simple version
		try {
			jhove.extract(Path.makeFile(TEST_DIR,"vda3.XML"), new File(TMP_OUT_TXT));
			
		} 
		catch (IOException e) { fail(); }  
		catch (ConnectionException e) {fail();}
	}
}
