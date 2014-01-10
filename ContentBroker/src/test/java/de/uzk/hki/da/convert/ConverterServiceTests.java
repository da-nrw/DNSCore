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
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.uzk.hki.da.model.ConversionInstruction;
import de.uzk.hki.da.model.ConversionRoutine;
import de.uzk.hki.da.model.DAFile;
import de.uzk.hki.da.model.Node;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.utils.TESTHelper;


/**
 * The Class ConverterServiceTests.
 */
public class ConverterServiceTests {

	/** The base path. */
	private final String basePath = "src/test/resources/convert/ConverterServiceTests/";
	
	/** The data path. */
	private final String dataPath=basePath+"TEST/123/data/";
	
	/** The conversion instructions. */
	private final List<ConversionInstruction> conversionInstructions = new ArrayList<ConversionInstruction>();
	
	/** The o. */
	private Object  o   = null;
	
	
	
	
	/**
	 * Sets the up.
	 */
	@Before
	public void setUp(){
		final Node vm3 = new Node("vm3","01-vm3");
		
		o = TESTHelper.setUpObject("123",basePath);
		
		@SuppressWarnings("serial")
		Set<Node> nodes = new HashSet<Node>(){{add(vm3);}};
		ConversionRoutine im = new ConversionRoutine(
				"IM",
				nodes,
				"de.uzk.hki.da.convert.CLIConversionStrategy",
				"convert input output",
				"png");
		ConversionRoutine copy = new ConversionRoutine(
				"IM",
				nodes,
				"de.uzk.hki.da.convert.CLIConversionStrategy",
				"cp input output",
				"*");

		
		ConversionInstruction ci1 = new ConversionInstruction();
		ci1.setTarget_folder("");
		ci1.setSource_file(new DAFile(o.getLatestPackage(),"2011+11+01+a","abc.xml"));
		ci1.setNode("vm3");
		ci1.setConversion_routine(copy);
		
		ConversionInstruction ci2 = new ConversionInstruction();
		ci2.setTarget_folder("");
		ci2.setSource_file(new DAFile(o.getLatestPackage(),"2011+11+01+a","140864.tif"));
		ci2.setNode("vm3");
		ci2.setConversion_routine(im);
		
		conversionInstructions.add(ci1);
		conversionInstructions.add(ci2);
	}
	
	/**
	 * Tear down.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@After
	public void tearDown() throws IOException{
		new File(dataPath+"2011+11+01+b/abc.xml").delete();
		new File(dataPath+"2011+11+01+b/140864.png").delete();
		FileUtils.deleteDirectory(new File(dataPath+"2011+11+01+b"));
		
	}
	
	
	/**
	 * Test.
	 * @throws IOException 
	 */
	@Test
	public void test() throws IOException{
		
		ConverterService converter = new ConverterService();
		converter.convertBatch(o,conversionInstructions);
		
		assertTrue(new File(dataPath+"2011+11+01+b/abc.xml").exists());
		assertTrue(new File(dataPath+"2011+11+01+b/140864.png").exists());
	}
}
