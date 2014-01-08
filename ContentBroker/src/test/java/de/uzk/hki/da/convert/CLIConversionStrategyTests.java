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

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;

import org.junit.After;
import org.junit.Test;

import de.uzk.hki.da.model.ConversionInstruction;
import de.uzk.hki.da.model.ConversionRoutine;
import de.uzk.hki.da.model.DAFile;
import de.uzk.hki.da.model.Node;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.utils.TESTHelper;



/**
 * The Class CLIConversionStrategyTests.
 */
public class CLIConversionStrategyTests {
	
	/** The base path. */
	private String basePath = "src/test/resources/convert/CLIConversionStrategyTests/";
	
	/**
	 * Tear down.
	 */
	@After
	public void tearDown() {
		new File(basePath+"TEST/1233/data/2012_12_12+12_12+b/Bild 4-1.jpg").delete();
		new File(basePath+"TEST/1244/data/2011+11+01+b/140849.png").delete();
		new File(basePath+"TEST/1255/data/2012_12_12+12_12+b/3512.pdf").delete();
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
		
		ConversionRoutine conversionRoutineCopy= new ConversionRoutine("COPY",new HashSet<Node>(),"",
				"cp input output","*");
		
		Object o = TESTHelper.setUpObject("1233",basePath);
		
		CLIConversionStrategy strat= new CLIConversionStrategy();
		strat.setCLIConnector(new CLIConnector());
		strat.setParam("cp input output");
		strat.setObject(o);
		ConversionInstruction ci= new ConversionInstruction();
		
		DAFile f = new DAFile(o.getLatestPackage(),"2012_12_12+12_12+a","Bild 4-1.jpg");
		ci.setSource_file(f);
		
		ci.setTarget_folder("");
		ci.setConversion_routine(conversionRoutineCopy);
		ci.setAdditional_params("");
		
		strat.convertFile(ci);
		
		assertTrue(new File(basePath+"TEST/1233/data/2012_12_12+12_12+b/Bild 4-1.jpg").exists());
	}
	
	/**
	 * Test resolve additional params.
	 *
	 * @throws FileNotFoundException the file not found exception
	 */
	@Test
	public void testResolveAdditionalParams() throws FileNotFoundException{
		

		ConversionRoutine conversionRoutineResize=  new ConversionRoutine("RESIZE",new HashSet<Node>(),"",
				"convert -resize {institution.width}x{institution.height} input output","png");
		
		Object o = TESTHelper.setUpObject("1244",basePath);
		
		CLIConversionStrategy strat= new CLIConversionStrategy();
		strat.setCLIConnector(new CLIConnector());
		strat.setParam("convert -resize {institution.width}x{institution.height} input output");
		strat.setObject(o);
		
		ConversionInstruction ci= new ConversionInstruction();
		
		ci.setSource_file(new DAFile(o.getLatestPackage(),"2011+11+01+a","140849.tif"));
		
		ci.setTarget_folder("");
		ci.setConversion_routine(conversionRoutineResize);
		ci.setAdditional_params("640,480");
		
		strat.convertFile(ci);
		
		assertTrue(new File(basePath+"TEST/1244/data/2011+11+01+b/140849.png").exists());
		
		
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
		
		Object o = TESTHelper.setUpObject("1255",basePath);
		
		CLIConversionStrategy strat= new CLIConversionStrategy();
		strat.setObject(o);
		ConversionRoutine conversionRoutinePdfToPdfA=  new ConversionRoutine("PDF2PDFA",new HashSet<Node>(),"",
			"gs -dPDFA -dBATCH -dNOPAUSE -sDEVICE=pdfwrite -sProcessColorModel=DeviceGray -sOutputFile=output input","pdf");
		
	
		ConversionInstruction ci= new ConversionInstruction();
		
		ci.setSource_file(new DAFile(o.getLatestPackage(),"2012_12_12+12_12+a","3512.pdf"));
		
		ci.setTarget_folder("");
		ci.setConversion_routine(conversionRoutinePdfToPdfA);
		
		strat.setCLIConnector(new CLIConnector());
		strat.setParam("gs -dPDFA -dBATCH -dNOPAUSE -sDEVICE=pdfwrite -sProcessColorModel=DeviceGray -sOutputFile=output input");
		strat.convertFile(ci);
		
		assertTrue(new File(basePath+"TEST/1255/data/2012_12_12+12_12+b/3512.pdf").exists());
	}
	
	/*
	@Test
	public void convertFileContainingSpecialCharacters(){
		
		ConversionRoutine conversionRoutineCopy= new ConversionRoutine("COPY",new HashSet<Node>(),"",
				"cp input output","*","");
		
		String dataPath="src/test/resources/cb/CLIConversionStrategyTests/SpecialCharacters_SIP";
		
		CLIConversionStrategy strat= new CLIConversionStrategy();
		strat.setParam("cp input output");
		
		ConversionInstruction ci= new ConversionInstruction();
		ci.setFile_name("input/Bild4-1{[xyz]}$!*?§.jpg");
		ci.setTarget_folder("output");
		ci.setConversion_routine(conversionRoutineCopy);
		ci.setPhysicalPackagePath(dataPath);
		ci.setAdditional_params("");
		
		strat.convertFile(ci);
		
		assertTrue(new File(dataPath+"/data/output/Bild4-1{[xyz]}$!*?§.jpg").exists());
	
		// cleanup
		new File(dataPath+"/data/output/Bild4-1{[xyz]}$!*?§.jpg").delete();
	}*/
}
