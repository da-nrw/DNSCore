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


import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static de.uzk.hki.da.test.TC.*;
import de.uzk.hki.da.model.FormatMapping;
import de.uzk.hki.da.model.JHoveParameterMapping;
import de.uzk.hki.da.utils.CommandLineConnector;
import de.uzk.hki.da.utils.IOTimeoutException;
import de.uzk.hki.da.utils.Path;
import de.uzk.hki.da.utils.ProcessInformation;



/**
 * No Test generate Outputfile, becaouse the  CommandLineConnector is mocked, but JhoveMetadataExtractor evaluate output file.
 * The Outputfile is already generated in workspace.
 * 
 * @author Daniel M. de Oliveira
 * @author Trebunski
 */
public class JhoveMetadataExtractorTests {
	private static final String PUID_PDF = "fmt/18";
	private static final String MIME_PDF = "application/pdf";
	private static final String JHOVE_OPT_XML = "-m  PDF-hul";
	
	private static final String TEST_PDF = "ValidPDF.pdf";
	private static final String TIMEOUT = "timeout";
	private static final String TMP_OUT_TXT = "out.txt";
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
		try {
			//private variable in jhove definition with reflections to avoid DB fetches 
			// to avoid java.lang.IllegalStateException: sessionFactory is null in HibernateUtil
			List<JHoveParameterMapping> possibleOptions = new ArrayList<JHoveParameterMapping>();
			possibleOptions.add(new JHoveParameterMapping(MIME_PDF,JHOVE_OPT_XML));
			Field possibleOptionsField;
			possibleOptionsField = jhove.getClass().getDeclaredField("possibleOptions");
			possibleOptionsField.setAccessible(true);
			possibleOptionsField.set(jhove, possibleOptions);			

			List<FormatMapping> pronomMimetypeList = new ArrayList<FormatMapping>();
			FormatMapping fMapping=new FormatMapping();
			fMapping.setPuid(PUID_PDF);
			fMapping.setMime_type(MIME_PDF);
			pronomMimetypeList.add(fMapping);
			Field pronomMimetypeListField = jhove.getClass().getDeclaredField("pronomMimetypeList");
			pronomMimetypeListField.setAccessible(true);
			pronomMimetypeListField.set(jhove, pronomMimetypeList);


		} catch (Exception e1) {
			e1.printStackTrace();
			fail(e1.getMessage());
		}
		
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
					Path.makeFile(TEST_DIR,TMP_OUT_TXT),PUID_PDF);
			fail();
		} 
		catch (IllegalStateException expected) {}
		catch (FileNotFoundException expected) {fail(expected.getMessage());}
		catch (ConnectionException e) {fail(e.getMessage());}
		catch (Exception e) {fail(e.getMessage());} 
	}
	
	
	
	@Test
	public void InputFileDoesNotExist() {
		try {
			jhove.extract(Path.makeFile(TEST_DIR,"notexistent.xml"), 
					Path.makeFile(TEST_DIR,TMP_OUT_TXT),PUID_PDF);
			fail();
		} 
		catch (FileNotFoundException expected) {}
		catch (ConnectionException e) {fail(e.getMessage());}
		catch (Exception e) {fail(e.getMessage());} 
	}
	
	@Test
	public void targetFolderDoesNotExist() {
		try {
			jhove.extract(Path.makeFile(TEST_DIR,TEST_PDF), 
					Path.makeFile(TEST_DIR,"dirNotExists","outputfile.txt"),PUID_PDF);
			fail();
		} 
		catch (IllegalArgumentException expected) {}
		catch (ConnectionException e) {fail(e.getMessage());}
		catch (Exception e) {fail(e.getMessage());} 
	}

	@Test
	public void extractSuccessful() throws IOException {
		
		//CommandLineConnector cli = new CommandLineConnector();
		//jhove.setCli(cli);
		try {
			jhove.extract(Path.makeFile(TEST_DIR,TEST_PDF),Path.makeFile(TEST_DIR,TMP_OUT_TXT),PUID_PDF);
		} 
		catch (IOException e) { fail(e.getMessage()); } 
		catch (ConnectionException e) { fail(e.getMessage()); }
	}

	@Test(expected=ConnectionException.class)
	public void extractNotSuccessfulWithErrorCodes() throws IOException, ConnectionException {
		
		when(cli.runCmdSynchronously((String[])anyObject(),(File)anyObject(),anyInt()))
			.thenReturn(piRetval1)
			.thenReturn(piRetval1);
		
		try {
			jhove.extract(Path.makeFile(TEST_DIR,TEST_PDF), Path.makeFile(TEST_DIR,TMP_OUT_TXT),PUID_PDF);
			fail();
		} 
		catch (ConnectionException e) {throw e;}
		catch (Exception e) { fail(e.getMessage()); } 
		
	}
	
	@Test
	public void extractSuccessfulAfterRetry() throws IOException {
		
		when(cli.runCmdSynchronously((String[])anyObject(),(File)anyObject(),anyInt()))
			.thenReturn(piRetval1)
			.thenReturn(piRetval0); // let it work with the simple version
		
		try {
			jhove.extract(Path.makeFile(TEST_DIR,TEST_PDF), Path.makeFile(TEST_DIR,TMP_OUT_TXT),PUID_PDF);
		} 
	/*	catch(FileNotFoundException e){
			//cli is mocked, jhove is not called and no outputfile will be generated, therefore no outputfile exists, so we expect FileNotFoundException
			if(!e.getMessage().equals(TMP_OUT_TXT+" (No such file or directory)"))
				 fail(e.getMessage());
			}*/
		catch (ConnectionException e) { fail(e.getMessage()); }
		catch (Exception e) { fail(e.getMessage()); } 
	}
	
	
	
	@Test
	public void timeout() throws IOException {

		when(cli.runCmdSynchronously((String[]) anyObject(), (File) anyObject(), anyLong())).thenThrow(new IOTimeoutException(TIMEOUT)).thenThrow(new IOTimeoutException(TIMEOUT));
		try {
			jhove.extract(Path.makeFile(TEST_DIR, TEST_PDF), Path.makeFile(TEST_DIR,TMP_OUT_TXT), PUID_PDF);
			fail();
		} catch (ConnectionException e) {}
	}
	
	@Test
	public void timeoutNotHappeningWithRetry() throws IOException {
		
		when(cli.runCmdSynchronously((String[])anyObject(),(File)anyObject(),anyLong()))
			.thenThrow(new IOTimeoutException(TIMEOUT))
			.thenReturn(piRetval0); // let it work with the simple version
		try {
			jhove.extract(Path.makeFile(TEST_DIR,TEST_PDF), Path.makeFile(TEST_DIR,TMP_OUT_TXT),PUID_PDF);
			
		} catch(FileNotFoundException e){
			//cli is mocked, jhove is not called and no outputfile will be generated, therefore no outputfile exists, so we expect FileNotFoundException
			if(!e.getMessage().equals(TMP_OUT_TXT+" (No such file or directory)"))
				 fail(e.getMessage());
			}
		catch (ConnectionException e) {fail(e.getMessage());}
		catch (Exception e) { fail(e.getMessage()); }  
	}
	
	
	
	@Test
	public void firstTimeoutThenError() throws IOException {
		
		when(cli.runCmdSynchronously((String[])anyObject(),(File)anyObject(),anyLong()))
			.thenThrow(new IOTimeoutException(TIMEOUT))
			.thenReturn(piRetval1);
		try {
			jhove.extract(Path.makeFile(TEST_DIR,TEST_PDF), Path.makeFile(TEST_DIR,TMP_OUT_TXT),PUID_PDF);
			
		} 
		catch (ConnectionException e) {}
		catch (Exception e) { fail(e.getMessage()); }  
	}
	
}
