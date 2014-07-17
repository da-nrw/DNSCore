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

import de.uzk.hki.da.format.PdfConversionStrategy;
import de.uzk.hki.da.model.ConversionInstruction;
import de.uzk.hki.da.model.ConversionRoutine;
import de.uzk.hki.da.model.DAFile;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.utils.Path;
import de.uzk.hki.da.utils.RelativePath;
import de.uzk.hki.da.utils.SimplifiedCommandLineConnector;
import de.uzk.hki.da.utils.TESTHelper;
import de.uzk.hki.da.utils.TC;


/**
 * The Class PdfConversionStrategyTest.
 *
 * @author Jens Peters
 */
public class PdfConversionStrategyTest {

	Path workAreaRootPath=Path.make(TC.TEST_ROOT_FORMAT,"PdfConversionStrategyTests");
	
	/** The cs. */
	PdfConversionStrategy cs = new PdfConversionStrategy();
	
	/** The o. */
	Object o;
	
	/**
	 * Sets the up.
	 */
	@Before
	public void setUp(){
		
		o = TESTHelper.setUpObject("1", new RelativePath(workAreaRootPath));
		
		cs.setObject(o);
	}
	
	/**
	 * Test pdf conversion.
	 *
	 * @throws FileNotFoundException the file not found exception
	 */
	@SuppressWarnings("rawtypes")
	@Test
	public void testPdfConversion () throws FileNotFoundException {
		ConversionInstruction ci = new ConversionInstruction();
		
		ConversionRoutine cr = new ConversionRoutine();
		
		SimplifiedCommandLineConnector cli = mock ( SimplifiedCommandLineConnector.class );
					
		when(cli.execute((String[]) anyObject())).thenAnswer(new Answer () {
			public Boolean answer(InvocationOnMock invocation) {
			    java.lang.Object[] args = invocation.getArguments();
		         String[] cmdarr = (String[]) args[0];
				
		         for (String s : cmdarr) {
		        	 System.out.print(s + " ");
		         }
		         return true;
		    }
		});		
			
		cs.setCLIConnector(cli);
		
		cr.setTarget_suffix("pdf");
		ci.setConversion_routine(cr);
		ci.setSource_file(new DAFile(o.getLatestPackage(),"rep+a","Pdf.pdf"));
		ci.setTarget_folder("");
	
		cs.convertFile(ci);
		
	}
	
	/**
	 * Cleanup.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@After
	public void cleanup() throws IOException {
		new File(workAreaRootPath + "work/data/rep+b/Pdf.pdf").delete();
	}


}
