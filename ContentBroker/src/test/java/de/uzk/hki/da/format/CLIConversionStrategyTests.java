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

import java.io.FileNotFoundException;

import org.junit.After;
import org.junit.Test;

import de.uzk.hki.da.core.Path;
import de.uzk.hki.da.core.RelativePath;
import de.uzk.hki.da.model.ConversionInstruction;
import de.uzk.hki.da.model.ConversionRoutine;
import de.uzk.hki.da.model.DAFile;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.test.TC;
import de.uzk.hki.da.test.TESTHelper;
import de.uzk.hki.da.utils.SimplifiedCommandLineConnector;



/**
 * The Class CLIConversionStrategyTests.
 */
public class CLIConversionStrategyTests {
	
	private Path workAreaRootPath = Path.make(TC.TEST_ROOT_FORMAT,"CLIConversionStrategyTests");
	
	/**
	 * Tear down.
	 */
	@After
	public void tearDown() {
		Path.makeFile(workAreaRootPath,"work/TEST/1233/data/2012_12_12+12_12+b/Bild 4-1.jpg").delete();
		Path.makeFile(workAreaRootPath,"work/TEST/1244/data/2011+11+01+b/140849.png").delete();
		Path.makeFile(workAreaRootPath,"work/TEST/1255/data/2012_12_12+12_12+b/3512.pdf").delete();
	}
	
	
	
	/**
	 * Addresses a bug that caused trouble while executing conversions
	 * when files to be scanned and converted contained
	 * whitespaces.
	 *
	 * @throws FileNotFoundException the file not found exception
	 * @author Daniel M. de Oliveira
	 */
	@Test
	public void convertFileContainingWhitespaces() throws FileNotFoundException{
		
		ConversionRoutine conversionRoutineCopy= new ConversionRoutine("COPY","",
				"cp input output","*");
		
		Object o = TESTHelper.setUpObject("1233",new RelativePath(workAreaRootPath));
		
		CLIConversionStrategy strat= new CLIConversionStrategy();
		strat.setCLIConnector(new SimplifiedCommandLineConnector());
		strat.setParam("cp input output");
		strat.setObject(o);
		ConversionInstruction ci= new ConversionInstruction();
		
		DAFile f = new DAFile(o.getLatestPackage(),"2012_12_12+12_12+a","Bild 4-1.jpg");
		o.getLatestPackage().getFiles().add(f);
		ci.setSource_file(f);
		
		ci.setTarget_folder("");
		ci.setConversion_routine(conversionRoutineCopy);
		ci.setAdditional_params("");
		
		strat.convertFile(ci);
		
		assertTrue(Path.makeFile(workAreaRootPath,"work/TEST/1233/data/2012_12_12+12_12+b/Bild 4-1.jpg").exists());
	}
	
	/**
	 * Test resolve additional params.
	 *
	 * @throws FileNotFoundException the file not found exception
	 */
	@Test
	public void testResolveAdditionalParams() throws FileNotFoundException{
		

		ConversionRoutine conversionRoutineResize=  new ConversionRoutine("RESIZE","",
				"convert -resize {institution.width}x{institution.height} input output","png");
		
		Object o = TESTHelper.setUpObject("1244",new RelativePath(workAreaRootPath));
		DAFile source = new DAFile(o.getLatestPackage(),"2011+11+01+a","140849.tif");
		o.getLatestPackage().getFiles().add(source);
		
		CLIConversionStrategy strat= new CLIConversionStrategy();
		strat.setCLIConnector(new SimplifiedCommandLineConnector());
		strat.setParam("convert -resize {institution.width}x{institution.height} input output");
		strat.setObject(o);
		
		ConversionInstruction ci= new ConversionInstruction();
		
		ci.setSource_file(source);
		
		ci.setTarget_folder("");
		ci.setConversion_routine(conversionRoutineResize);
		ci.setAdditional_params("640,480");
		
		strat.convertFile(ci);
		
		assertTrue(Path.makeFile(workAreaRootPath,"work/TEST/1244/data/2011+11+01+b/140849.png").exists());
		
		
	}
	
	
	
	
	
	/**
	 * This corresponds to bug #381 found in version 0.2.4. The "output" parameter of
	 * the params field could not be substituted properly by the output file name.
	 * 
	 * The problem could be reproduced and was caused by "-sOutput=output", where "output" after
	 * the "=" could not be replaced due to tokenization issues in CLIConversionStrategy.
	 *
	 * @throws FileNotFoundException the file not found exception
	 * @author Daniel M. de Oliveira
	 */
	@Test
	public void testOutputParameterAfterEqualsSign() throws FileNotFoundException{
		
		Object o = TESTHelper.setUpObject("1255",new RelativePath(workAreaRootPath));
		DAFile source =new DAFile(o.getLatestPackage(),"2012_12_12+12_12+a","3512.pdf");
		o.getLatestPackage().getFiles().add(new DAFile(o.getLatestPackage(),"2012_12_12+12_12+a","3512.pdf"));
		
		CLIConversionStrategy strat= new CLIConversionStrategy();
		strat.setObject(o);
		ConversionRoutine conversionRoutinePdfToPdfA=  new ConversionRoutine("PDF2PDFA","",
			"gs -dPDFA -dBATCH -dNOPAUSE -sDEVICE=pdfwrite -sProcessColorModel=DeviceGray -sOutputFile=output input","pdf");
		
	
		ConversionInstruction ci= new ConversionInstruction();
		
		ci.setSource_file(source);
		
		ci.setTarget_folder("");
		ci.setConversion_routine(conversionRoutinePdfToPdfA);
		
		strat.setCLIConnector(new SimplifiedCommandLineConnector());
		strat.setParam("gs -dPDFA -dBATCH -dNOPAUSE -sDEVICE=pdfwrite -sProcessColorModel=DeviceGray -sOutputFile=output input");
		strat.convertFile(ci);
		
		assertTrue(Path.makeFile(workAreaRootPath,"work/TEST/1255/data/2012_12_12+12_12+b/3512.pdf").exists());
	}
}
