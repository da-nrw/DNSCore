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
import org.junit.BeforeClass;
import org.junit.Test;

import static de.uzk.hki.da.test.TC.*;
import de.uzk.hki.da.util.Path;
import de.uzk.hki.da.utils.CommandLineConnector;
import de.uzk.hki.da.utils.IOTimeoutException;
import de.uzk.hki.da.utils.ProcessInformation;



/**
 * @author Daniel M. de Oliveira
 */
public class JhoveMetadataExtractorTests {

	private static final String VDA3_XML = "vda3.XML";
	private static final String TIMEOUT = "timeout";
	private static final String TMP_OUT_TXT = "/tmp/out.txt";
	private static final Path TEST_DIR = Path.make(TEST_ROOT_FORMAT,"JhoveMetadataExtractor");
	private static final ProcessInformation piRetval0=new ProcessInformation();
	private static final ProcessInformation piRetval1=new ProcessInformation();
	
	CommandLineConnector cli = mock(CommandLineConnector.class);
	JhoveMetadataExtractor jhove = new JhoveMetadataExtractor();

	@BeforeClass
	public static void setUpBeforeClass() {
		piRetval0.setExitValue(0);
		piRetval1.setExitValue(1);
	}
	
	@Before
	public void setUp() throws IOException {
		jhove.setCli(cli);
		ProcessInformation pi=new ProcessInformation();
		pi.setStdOut("Jhove (Rel");
		when(cli.runCmdSynchronously((String[])anyObject(),(File)anyObject(),anyInt())).thenReturn(pi);
		jhove.isConnectable();
	}
	
	
	@Test
	public void connectabilityNotChecked() {
		JhoveMetadataExtractor jhove = new JhoveMetadataExtractor();
		CommandLineConnector cli = mock(CommandLineConnector.class);
		jhove.setCli(cli);
		try {
			jhove.extract(Path.makeFile(TEST_DIR,"notexistent.xml"), 
					new File(TMP_OUT_TXT));
			fail();
		} 
		catch (IllegalStateException expected) {}
		catch (FileNotFoundException expected) {fail();}
		catch (ConnectionException e) {fail();}
		catch (Exception e) {fail();} 
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
		catch (Exception e) {fail();} 
	}
	
	@Test
	public void targetFolderDoesNotExist() {
		try {
			jhove.extract(Path.makeFile(TEST_DIR,VDA3_XML), 
					Path.makeFile(TEST_DIR,"dirNotExists","outputfile.txt"));
			fail();
		} 
		catch (IllegalArgumentException expected) {}
		catch (ConnectionException e) {fail();}
		catch (Exception e) {fail();} 
	}

	@Test
	public void extractSuccessful() throws IOException {
		
		when(cli.runCmdSynchronously((String[])anyObject(),(File)anyObject(),anyInt())).thenReturn(piRetval0);
		
		try {
			jhove.extract(Path.makeFile(TEST_DIR,VDA3_XML), new File(TMP_OUT_TXT));
		} 
		catch (IOException e) { fail(); } 
		catch (ConnectionException e) { fail(); }
	}
	
	@Test
	public void extractNotSuccessfulWithErrorCodes() throws IOException {
		
		when(cli.runCmdSynchronously((String[])anyObject(),(File)anyObject(),anyInt()))
			.thenReturn(piRetval1)
			.thenReturn(piRetval1);
		
		try {
			jhove.extract(Path.makeFile(TEST_DIR,VDA3_XML), new File(TMP_OUT_TXT));
			fail();
		} 
		catch (ConnectionException e) {}
		catch (Exception e) { fail(); } 
	}
	
	@Test
	public void extractSuccessfulAfterRetry() throws IOException {
		
		when(cli.runCmdSynchronously((String[])anyObject(),(File)anyObject(),anyInt()))
			.thenReturn(piRetval1)
			.thenReturn(piRetval0); // let it work with the simple version
		
		try {
			jhove.extract(Path.makeFile(TEST_DIR,VDA3_XML), new File(TMP_OUT_TXT));
		} 
		catch (ConnectionException e) { fail(); }
		catch (Exception e) { fail(); } 
	}
	
	
	
	@Test
	public void timeout() throws IOException {
		
		when(cli.runCmdSynchronously((String[])anyObject(),(File)anyObject(),anyLong()))
			.thenThrow(new IOTimeoutException(TIMEOUT))
			.thenThrow(new IOTimeoutException(TIMEOUT));
		try {
			jhove.extract(Path.makeFile(TEST_DIR,VDA3_XML), new File(TMP_OUT_TXT));
			fail();
		} catch (ConnectionException e) {}
	}
	
	@Test
	public void timeoutNotHappeningWithRetry() throws IOException {
		
		when(cli.runCmdSynchronously((String[])anyObject(),(File)anyObject(),anyLong()))
			.thenThrow(new IOTimeoutException(TIMEOUT))
			.thenReturn(piRetval0); // let it work with the simple version
		try {
			jhove.extract(Path.makeFile(TEST_DIR,VDA3_XML), new File(TMP_OUT_TXT));
			
		} 
		catch (ConnectionException e) {fail();}
		catch (Exception e) { fail(); }  
	}
	
	
	
	@Test
	public void firstTimeoutThenError() throws IOException {
		
		when(cli.runCmdSynchronously((String[])anyObject(),(File)anyObject(),anyLong()))
			.thenThrow(new IOTimeoutException(TIMEOUT))
			.thenReturn(piRetval1);
		try {
			jhove.extract(Path.makeFile(TEST_DIR,VDA3_XML), new File(TMP_OUT_TXT));
			
		} 
		catch (ConnectionException e) {}
		catch (Exception e) { fail(); }  
	}
}
