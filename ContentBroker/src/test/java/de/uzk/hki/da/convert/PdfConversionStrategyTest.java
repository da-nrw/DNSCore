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

package de.uzk.hki.da.convert;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.uzk.hki.da.model.ConversionInstruction;
import de.uzk.hki.da.model.ConversionRoutine;
import de.uzk.hki.da.model.DAFile;
import de.uzk.hki.da.model.Node;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.model.WorkArea;
import de.uzk.hki.da.test.TC;
import de.uzk.hki.da.test.TESTHelper;
import de.uzk.hki.da.util.Path;
import de.uzk.hki.da.util.RelativePath;
import de.uzk.hki.da.utils.CommandLineConnector;
import de.uzk.hki.da.utils.ProcessInformation;


/**
 * The Class PdfConversionStrategyTest.
 *
 * @author Jens Peters
 */
public class PdfConversionStrategyTest {

	Path workAreaRootPath=Path.make(TC.TEST_ROOT_CONVERT,"PdfConversionStrategyTests");
	
	/** The cs. */
	PdfConversionStrategy cs = new PdfConversionStrategy();
	
	/** The o. */
	Object o;

	private Node n;
	
	/**
	 * Sets the up.
	 */
	@Before
	public void setUp(){
		
		o = TESTHelper.setUpObject("1", new RelativePath(workAreaRootPath));
		o.getLatestPackage().getFiles().add(new DAFile("rep+a","Pdf.pdf"));
		cs.setObject(o);
		n = new Node();
		n.setWorkAreaRootPath(new RelativePath(workAreaRootPath));
	}
	
	/**
	 * Test pdf conversion.
	 * @throws IOException 
	 */
	@SuppressWarnings("rawtypes")
	@Test
	public void testPdfConversion () throws IOException {
		ConversionInstruction ci = new ConversionInstruction();
		
		ConversionRoutine cr = new ConversionRoutine();
		
		CommandLineConnector cli = mock ( CommandLineConnector.class );
		
		ProcessInformation pi = new ProcessInformation();
		pi.setExitValue(0);
		when(cli.runCmdSynchronously((String[]) anyObject())).thenReturn(pi);
			
//		
//		when(cli.runCmdSynchronously(((String[]) anyObject())).thenAnswer(new Answer () {
//			@Override
//			public Boolean answer(InvocationOnMock invocation) {
//			    java.lang.Object[] args = invocation.getArguments();
//		         String[] cmdarr = (String[]) args[0];
//				
//		         for (String s : cmdarr) {
//		        	 System.out.print(s + " ");
//		         }
//		         return true;
//		    }
//		});		
//			
		cs.setCLIConnector(cli);
		
		cr.setTarget_suffix("pdf");
		ci.setConversion_routine(cr);
		ci.setSource_file(new DAFile("rep+a","Pdf.pdf"));
		ci.setTarget_folder("");
	
		cs.convertFile(new WorkArea(n,o),ci);
		
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
