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

package de.uzk.hki.da.format;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import de.uzk.hki.da.core.Path;
import de.uzk.hki.da.core.RelativePath;
import de.uzk.hki.da.format.DocxConversionStrategy;
import de.uzk.hki.da.model.ConversionInstruction;
import de.uzk.hki.da.model.ConversionRoutine;
import de.uzk.hki.da.model.DAFile;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.test.TC;
import de.uzk.hki.da.test.TESTHelper;
import de.uzk.hki.da.utils.SimplifiedCommandLineConnector;
import de.uzk.hki.da.webservice.HttpFileTransmissionClient;



/**
 * The Class DocxConversionStrategyTests.
 *
 * @author Jens Peters
 * 
 * USES Webservice at URL
 */
public class DocxConversionStrategyTests {
	
	Path basePath= Path.make(TC.TEST_ROOT_FORMAT,"DocxConversionStrategyTests");
	
	/** The cs. */
	DocxConversionStrategy cs = new DocxConversionStrategy();
	
	/** The url. */
	String url = "http://da-nrw-winvm1.hki.uni-koeln.de/Handler.ashx";
	
	/** The o. */
	private Object o;
	
	/**
	 * Sets the up.
	 * @throws IOException 
	 */
	@Before
	public void setUp() throws IOException{
		
		o = TESTHelper.setUpObject("1", new RelativePath(basePath));
		cs.setObject(o);
		Path.make(o.getPath("newest")+"/_Docx.pdf").toFile().createNewFile();
	}
	
	
	
	
	/**
	 * Test docx converison.
	 *
	 * @throws FileNotFoundException the file not found exception
	 */
	@Test
	public void testDocxConverison () throws FileNotFoundException {
		ConversionInstruction ci = new ConversionInstruction();
		
		ConversionRoutine cr = new ConversionRoutine();
		cr.setParams( url );
		
		
		
		HttpFileTransmissionClient httpclient = mock( HttpFileTransmissionClient.class );
		when( httpclient.postFileAndReadResponse((File)anyObject(),(File)anyObject()) ).thenAnswer(new Answer<java.lang.Object> () {
			@Override
			public File answer(InvocationOnMock invocation) {
				
				File f = new File(basePath + "TEST/1/data/rep+b/Docx.pdf");
		        try {
					f.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
				return f;
		    }
		});	cs.setHttpclient(httpclient);
		
		
		
		SimplifiedCommandLineConnector cli = mock ( SimplifiedCommandLineConnector.class );
		when(cli.execute((String[]) anyObject())).thenAnswer(new Answer<java.lang.Object> () {
			@Override
			public Boolean answer(InvocationOnMock invocation) {
			    java.lang.Object[] args = invocation.getArguments();
		         String[] cmdarr = (String[]) args[0];
				
		         for (String s : cmdarr) {
		        	 System.out.print(s + " ");
		         }
		         return true;
		    }
		});	cs.setCLIConnector(cli);
		
		
		
		
		
			
			
		cr.setTarget_suffix("pdf");
		ci.setConversion_routine(cr);
		ci.setSource_file(new DAFile(o.getLatestPackage(),"rep+a","Docx.docx"));
		ci.setTarget_folder("");
		
		cs.convertFile(ci);
		
		assertTrue(new File(basePath + "TEST/1/data/rep+b/Docx.pdf").exists());
	}
	
	/**
	 * Cleanup.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@After
	public void cleanup() throws IOException {
		new File(basePath + "TEST/1/data/rep+b/Docx.pdf").delete();
		
	}
	


}
