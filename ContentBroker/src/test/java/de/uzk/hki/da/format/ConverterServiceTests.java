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

import de.uzk.hki.da.format.ConverterService;
import de.uzk.hki.da.model.ConversionInstruction;
import de.uzk.hki.da.model.ConversionRoutine;
import de.uzk.hki.da.model.DAFile;
import de.uzk.hki.da.model.Node;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.utils.Path;
import de.uzk.hki.da.utils.RelativePath;
import de.uzk.hki.da.utils.TESTHelper;
import de.uzk.hki.da.utils.TC;


/**
 * The Class ConverterServiceTests.
 */
public class ConverterServiceTests {

	private final Path workAreaRootPath = Path.make(TC.TEST_ROOT_FORMAT,"ConverterServiceTests");
	
	/** The data path. */
	private final Path dataPath= Path.make(workAreaRootPath,"work/TEST/123/data/");
	
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
		
		o = TESTHelper.setUpObject("123",new RelativePath(workAreaRootPath));
		
		@SuppressWarnings("serial")
		Set<Node> nodes = new HashSet<Node>(){{add(vm3);}};
		ConversionRoutine im = new ConversionRoutine(
				"IM",
				nodes,
				"de.uzk.hki.da.format.CLIConversionStrategy",
				"convert input output",
				"png");
		ConversionRoutine copy = new ConversionRoutine(
				"IM",
				nodes,
				"de.uzk.hki.da.format.CLIConversionStrategy",
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
		Path.makeFile(dataPath,"2011+11+01+b/abc.xml").delete();
		Path.makeFile(dataPath,"2011+11+01+b/140864.png").delete();
		FileUtils.deleteDirectory(Path.makeFile(dataPath,"2011+11+01+b"));
		
	}
	
	
	/**
	 * Test.
	 * @throws IOException 
	 */
	@Test
	public void test() throws IOException{
		
		ConverterService converter = new ConverterService();
		converter.convertBatch(o,conversionInstructions);
		
		assertTrue(Path.makeFile(dataPath,"2011+11+01+b/abc.xml").exists());
		assertTrue(Path.makeFile(dataPath,"2011+11+01+b/140864.png").exists());
	}
}
