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
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import de.uzk.hki.da.test.TC;
import de.uzk.hki.da.util.Path;
import de.uzk.hki.da.utils.CommandLineConnector;
import de.uzk.hki.da.utils.ProcessInformation;



/**
 * @author Daniel M. de Oliveira
 */
public class JhoveMetadataExtractorTests {

	CommandLineConnector cli = mock(CommandLineConnector.class);
	JhoveMetadataExtractor jhove = new JhoveMetadataExtractor();

	@Before
	public void setUp() {
		jhove.setCli(cli);
	}
	
	@Test
	public void extractSuccessful() throws IOException {
		
		ProcessInformation pi=new ProcessInformation();
		pi.setExitValue(0);
		when(cli.runCmdSynchronously((String[])anyObject(),(File)anyObject(),anyInt())).thenReturn(pi);
		
		try {
			assertTrue(jhove.extract(Path.makeFile(TC.TEST_ROOT_FORMAT,"JhoveMetadataExtractor","vda3.XML"), new File("")));
		} 
		catch (IOException e) { fail(); } 
		catch (ConnectionException e) { fail(); }
	}
	
	@Test
	public void extractNotSuccessful() throws IOException {
		
		ProcessInformation pi=new ProcessInformation();
		pi.setExitValue(1);
		when(cli.runCmdSynchronously((String[])anyObject(),(File)anyObject(),anyInt())).thenReturn(pi);
		
		try {
			assertFalse(jhove.extract(Path.makeFile(TC.TEST_ROOT_FORMAT,"JhoveMetadataExtractor","vda3.XML"), new File("")));
		} 
		catch (IOException e) { fail(); } 
		catch (ConnectionException e) { fail(); }
	}
	
	@Test
	public void cliConnectionError() throws IOException {
		
		ProcessInformation pi=new ProcessInformation();
		pi.setExitValue(1);
		when(cli.runCmdSynchronously((String[])anyObject(),(File)anyObject(),anyLong())).thenThrow(new IOException("io"));
		try {
			jhove.extract(Path.makeFile(TC.TEST_ROOT_FORMAT,"JhoveMetadataExtractor","vda3.XML"), new File(""));
			fail();
		} catch (ConnectionException e) {}
	}
	
	@Test
	public void extractSuccessfulAfterTryingThe2ndTime() throws IOException {
		
		ProcessInformation pi=new ProcessInformation();
		pi.setExitValue(1);
		ProcessInformation pi2=new ProcessInformation();
		pi2.setExitValue(0);
		when(cli.runCmdSynchronously((String[])anyObject(),(File)anyObject(),anyInt())).thenReturn(pi).thenReturn(pi2);
		
		try {
			assertTrue(jhove.extract(Path.makeFile(TC.TEST_ROOT_FORMAT,"JhoveMetadataExtractor","vda3.XML"), new File("")));
		} 
		catch (IOException e) { fail(); } 
		catch (ConnectionException e) { fail(); }
	}
	
	@Test
	public void extractNotSuccessfulAfterTryingThe2ndTime() throws IOException {
		
		ProcessInformation pi=new ProcessInformation();
		pi.setExitValue(1);
		ProcessInformation pi2=new ProcessInformation();
		pi2.setExitValue(1);
		when(cli.runCmdSynchronously((String[])anyObject(),(File)anyObject(),anyInt())).thenReturn(pi).thenReturn(pi2);
		
		try {
			assertFalse(jhove.extract(Path.makeFile(TC.TEST_ROOT_FORMAT,"JhoveMetadataExtractor","vda3.XML"), new File("")));
		} 
		catch (IOException e) { fail(); } 
		catch (ConnectionException e) { fail(); }
	}
	
	@Test
	public void cliConnectionErrorAfterTryingThe2ndTime() throws IOException {
		
		ProcessInformation pi=new ProcessInformation();
		pi.setExitValue(1);
		when(cli.runCmdSynchronously((String[])anyObject(),(File)anyObject(),anyLong())).thenReturn(pi).thenThrow(new IOException("io"));
		try {
			jhove.extract(Path.makeFile(TC.TEST_ROOT_FORMAT,"JhoveMetadataExtractor","vda3.XML"), new File(""));
			fail();
		} catch (ConnectionException e) {}
	}
	
	
	
}
