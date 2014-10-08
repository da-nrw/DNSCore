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

import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.uzk.hki.da.core.Path;
import de.uzk.hki.da.format.DocxConversionStrategy;
import de.uzk.hki.da.test.TC;



/**
 * The Class DocxConversionStrategyTests.
 *
 * @author Jens Peters
 * 
 * USES Webservice at URL
 */
public class CTDocxConversionStrategyTest {
	
	/** The base path. */
	Path basePath=Path.make(TC.TEST_ROOT_FORMAT,"DocxConversionStrategyTests");
	
	/** The cs. */
	DocxConversionStrategy cs = new DocxConversionStrategy();
	
	/** The url. */
	String url = "http://server/Handler.ashx";
	
	/**
	 * Sets the up.
	 * @throws IOException 
	 */
	@Before
	public void setUp() throws IOException{
		/*
		FileUtils.copyFile(new File("src/main/conf/PDFA_def.ps"),new File("conf/PDFA_def.ps"));
		
		o = TESTHelper.setUpObject("1", basePath);
		o.reattach();
		cs.setObject(o);
		new File(basePath + "TEST/1/data/rep+b").mkdirs();*/
        	
	}
	
	
	
	
	/**
	 * Test docx converison.
	 *
	 * @throws FileNotFoundException the file not found exception
	 */
	@Test
	public void testDocxConverison () throws FileNotFoundException {
		/*
		ConversionInstruction ci = new ConversionInstruction();
		
		ConversionRoutine cr = new ConversionRoutine();
		cr.setParams( url );
		
		
		HttpFileTransmissionClient httpclient = new HttpFileTransmissionClient();
		httpclient.setUrl(url);
		
		/*  Mocking httpclient is not useful
		HttpFileTransmissionClient httpclient = mock( HttpFileTransmissionClient.class );
		when( httpclient.postFileAndReadResponse((File)anyObject(),(File)anyObject()) ).thenAnswer(new Answer<java.lang.Object> () {
			public File answer(InvocationOnMock invocation) {
				
				File f = new File(basePath + "TEST/1/data/rep+b/Docx.pdf");
		        try {
		        	f.mkdirs();
		        	f.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
				return f;
		    }
		});
			
		cs.setHttpclient(httpclient); 
		
		
		
		SimplifiedCommandLineConnector cli = new SimplifiedCommandLineConnector(); 
		cs.setCLIConnector(cli);
		
		cr.setTarget_suffix("pdf");
		ci.setConversion_routine(cr);
		ci.setSource_file(new DAFile(o.getLatestPackage(),"rep+a","Docx.docx"));
		ci.setTarget_folder("");
		
		cs.convertFile(ci);
		
		assertTrue(new File(basePath + "TEST/1/data/rep+b/Docx.pdf").exists());*/
	}
	
	/**
	 * Cleanup.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@After
	public void cleanup() throws IOException {
		/*new File(basePath + "TEST/1/data/rep+b/Docx.pdf").delete();
		new File(basePath + "TEST/1/data/rep+b/_Docx.pdf").delete();
		new File("conf/PDFA_def.ps").delete();*/
	}
	


}
