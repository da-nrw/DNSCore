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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.uzk.hki.da.core.C;
import de.uzk.hki.da.core.UserException;
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
 * The Class TiffConversionStrategyTests.
 */
public class TiffConversionStrategyTests {
	
	private static final String TIFF_CONVERSION_STRATEGY_TESTS = "TiffConversionStrategyTests";

	Path workAreaRootPath=Path.make(TC.TEST_ROOT_CONVERT,TIFF_CONVERSION_STRATEGY_TESTS);
	Path contractorFolder=Path.make(workAreaRootPath,"work",C.TEST_USER_SHORT_NAME);
	
	private Object o;

	private Node n;

	@Before
	public void setUp(){
		
		o = TESTHelper.setUpObject("1", new RelativePath(workAreaRootPath));
		
		o.reattach();
		DAFile f = new DAFile(o.getLatestPackage(),"rep+b","CCITT_1.TIF");
		DAFile f2 = new DAFile(o.getLatestPackage(),"rep+b","CCITT_1.UNCOMPRESSED.TIF");
		o.getLatestPackage().getFiles().add(f);
		o.getLatestPackage().getFiles().add(f2);
		Path.makeFile(contractorFolder,"1/data","rep+b").mkdirs();
		
		n = new Node();
		n.setWorkAreaRootPath(workAreaRootPath);
	}

	@After
	public void tearDown() throws IOException {
		Path.makeFile(contractorFolder,"1/data/rep+b/CCITT_1.TIF").delete();
		Path.makeFile(contractorFolder,"1/data/rep+b/CCITT_1_UNCOMPRESSED.TIF").delete();
		FileUtils.deleteQuietly(Path.makeFile(contractorFolder,"1/data/rep+b"));
		org.apache.commons.io.FileUtils.deleteDirectory(Path.makeFile(contractorFolder,"/1/data/rep+b/subfolder"));
	}
	
	
	/**
	 * Test subfolder creation.
	 */
	@Test
	public void testSubfolderCreation () {

		TiffConversionStrategy cs = new TiffConversionStrategy();
		cs.setObject(o);
		cs.setCLIConnector(new CommandLineConnector());
		ConversionInstruction ci = new ConversionInstruction();
		ConversionRoutine cr = new ConversionRoutine();
		ci.setConversion_routine(cr);
		ci.setSource_file(new DAFile(o.getLatestPackage(),"rep+a","CCITT_1.TIF"));
		ci.setTarget_folder("subfolder");
		
		cs.convertFile(new WorkArea(n,o),ci);
		
		assertTrue(Path.makeFile(workAreaRootPath,"work/TEST/1/data/rep+b/subfolder/CCITT_1.TIF").exists());
	}
	
	/**
	 * Test conversion compressed tiff.
	 */
	@Test
	public void testConversionCompressedTiff () {

		TiffConversionStrategy cs = new TiffConversionStrategy();
		cs.setCLIConnector(new CommandLineConnector());
		cs.setObject(o);
		ConversionInstruction ci = new ConversionInstruction();
		ConversionRoutine cr = new ConversionRoutine();
		ci.setConversion_routine(cr);
		ci.setSource_file(new DAFile(o.getLatestPackage(),"rep+a","CCITT_1.TIF"));
		ci.setTarget_folder("");
		
		cs.convertFile(new WorkArea(n,o),ci);
		
		assertTrue(Path.makeFile(workAreaRootPath,"work/TEST/1/data/rep+b/CCITT_1.TIF").exists());
	}
	
	
	/**
	 * Test conversion un compressed tiff.
	 */
	@Test
	public void testConversionUnCompressedTiff () {

		TiffConversionStrategy cs = new TiffConversionStrategy();
		cs.setObject(o);
		cs.setCLIConnector(new CommandLineConnector());
		ConversionInstruction ci = new ConversionInstruction();
		ConversionRoutine cr = new ConversionRoutine();
		ci.setConversion_routine(cr);
		ci.setSource_file(new DAFile(o.getLatestPackage(),"rep+a","CCITT_1_UNCOMPRESSED.TIF"));
		ci.setTarget_folder("");
		
		cs.convertFile(new WorkArea(n,o),ci);
		
		assertFalse(Path.makeFile(workAreaRootPath,"work/TEST/1/data/rep+b/CCITT_1_UNCOMPRESSED.TIF").exists());
	}
	
	/**
	 * Test identify return code on problematic Tiff containing "EXIF IFD" and "GPS IFD" Tags)
	 */
	@Test
	public void testConversionProblematicTiff () {

		TiffConversionStrategy cs = new TiffConversionStrategy();
		cs.setObject(o);
		cs.setCLIConnector(new CommandLineConnector());
		ConversionInstruction ci = new ConversionInstruction();
		ConversionRoutine cr = new ConversionRoutine();
		ci.setConversion_routine(cr);
		ci.setSource_file(new DAFile(o.getLatestPackage(),"rep+a","0001_L.TIF"));
		ci.setTarget_folder("");
		
		cs.convertFile(new WorkArea(n,o),ci);
		
		assertFalse(new File(workAreaRootPath + "work/TEST/1/data/rep+b/0001_L.TIF").exists());
	}
	
	/**
	 * Test return code on Tiff, which isn't a picture at all. (Assume it has passed the fido checks 
	 * done before)
	 */
	@Test
	public void testConversionBuggyTiff () {

		TiffConversionStrategy cs = new TiffConversionStrategy();
		cs.setObject(o);
		cs.setCLIConnector(new CommandLineConnector());
		ConversionInstruction ci = new ConversionInstruction();
		ConversionRoutine cr = new ConversionRoutine();
		ci.setConversion_routine(cr);
		ci.setSource_file(new DAFile(o.getLatestPackage(),"rep+a","notanytiff.tif"));
		ci.setTarget_folder("");
		
		try {
		cs.convertFile(new WorkArea(n,o),ci);
		assertFalse(true);
		} catch (Exception e) {
			
		}
		assertFalse(new File(workAreaRootPath + "work/TEST/1/data/rep+b/notanytiff.tif").exists());
		
	}
	
	/**
	 * Test if UserException is thrown on Tiff containing RichIPTC Images
	 * depends on specific version of IM, therefore we need to mock the commandLine 
	 * converter (not each IM behaves the same:-)
	 * @throws IOException 
	 */
	@Test
	public void testUserExceptionOnIPTCField() throws IOException {
		
		ProcessInformation pi = new ProcessInformation();
		
		pi.setExitValue(1);
		
		pi.setStdOut("");
		pi.setStdErr("wrong data type 2 for \"RichTIFFIPTC\"; tag ignored");
		CommandLineConnector cli = mock ( CommandLineConnector.class );
		
		String cmdIdentify[] = new String[] {
				"identify", "-format", "'%C'", new File(workAreaRootPath + "/work/TEST/1/data/rep+a/0001_L.TIF").getAbsolutePath()
		};

		when(cli.runCmdSynchronously(cmdIdentify)).thenReturn(pi);
		
		TiffConversionStrategy cs = new TiffConversionStrategy();
		cs.setCLIConnector(cli);
		cs.setObject(o);
		ConversionInstruction ci = new ConversionInstruction();
		ConversionRoutine cr = new ConversionRoutine();
		ci.setConversion_routine(cr);
		ci.setSource_file(new DAFile(o.getLatestPackage(),"rep+a","0001_L.TIF"));
		ci.setTarget_folder("");
		try {
		cs.convertFile(new WorkArea(n,o),ci);
		assertTrue(false);
		} catch (UserException e) {
			;
			assertTrue(true);
		} catch (Exception e) {
			e.printStackTrace();
			assertFalse(true);
			
		}
		assertFalse(new File(workAreaRootPath + "work/TEST/1/data/rep+b/0001_L.TIF").exists());
		
	}
	
	


}
