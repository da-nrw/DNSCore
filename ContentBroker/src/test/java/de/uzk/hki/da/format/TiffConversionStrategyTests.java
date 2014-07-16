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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.uzk.hki.da.format.TiffConversionStrategy;
import de.uzk.hki.da.model.ConversionInstruction;
import de.uzk.hki.da.model.ConversionRoutine;
import de.uzk.hki.da.model.DAFile;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.utils.Path;
import de.uzk.hki.da.utils.RelativePath;
import de.uzk.hki.da.utils.TESTHelper;
import de.uzk.hki.da.utils.TestConstants;


/**
 * The Class TiffConversionStrategyTests.
 */
public class TiffConversionStrategyTests {
	
	Path workAreaRootPath=Path.make(TestConstants.TEST_ROOT_FORMAT,"TiffConversionStrategyTests");
	
	/** The cs. */
	TiffConversionStrategy cs = new TiffConversionStrategy();
	
	/** The o. */
	private Object o;

	
	/**
	 * Sets the up.
	 */
	@Before
	public void setUp(){
		
		o = TESTHelper.setUpObject("1", new RelativePath(workAreaRootPath));
		cs.setObject(o);
		o.reattach();
	}
	
	/**
	 * Test subfolder creation.
	 */
	@Test
	public void testSubfolderCreation () {
		ConversionInstruction ci = new ConversionInstruction();
		ConversionRoutine cr = new ConversionRoutine();
		ci.setConversion_routine(cr);
		ci.setSource_file(new DAFile(o.getLatestPackage(),"rep+a","CCITT_1.TIF"));
		ci.setTarget_folder("subfolder");
		
		cs.convertFile(ci);
		
		assertTrue(Path.makeFile(workAreaRootPath,"work/TEST/1/data/rep+b/subfolder/CCITT_1.TIF").exists());
	}
	
	/**
	 * Test conversion compressed tiff.
	 */
	@Test
	public void testConversionCompressedTiff () {
		ConversionInstruction ci = new ConversionInstruction();
		ConversionRoutine cr = new ConversionRoutine();
		ci.setConversion_routine(cr);
		ci.setSource_file(new DAFile(o.getLatestPackage(),"rep+a","CCITT_1.TIF"));
		ci.setTarget_folder("");
		
		cs.convertFile(ci);
		
		assertTrue(Path.makeFile(workAreaRootPath,"work/TEST/1/data/rep+b/CCITT_1.TIF").exists());
	}
	
	
	/**
	 * Test conversion un compressed tiff.
	 */
	@Test
	public void testConversionUnCompressedTiff () {
		ConversionInstruction ci = new ConversionInstruction();
		ConversionRoutine cr = new ConversionRoutine();
		ci.setConversion_routine(cr);
		ci.setSource_file(new DAFile(o.getLatestPackage(),"rep+a","CCITT_1_UNCOMPRESSED.TIF"));
		ci.setTarget_folder("");
		
		cs.convertFile(ci);
		
		assertFalse(Path.makeFile(workAreaRootPath,"work/TEST/1/data/rep+b/CCITT_1_UNCOMPRESSED.TIF").exists());
	}
	
	/**
	 * Test identify return code on problematic Tiff containing "EXIF IFD" and "GPS IFD" Tags)
	 */
	@Test
	public void testConversionProblematicTiff () {
		ConversionInstruction ci = new ConversionInstruction();
		ConversionRoutine cr = new ConversionRoutine();
		ci.setConversion_routine(cr);
		ci.setSource_file(new DAFile(o.getLatestPackage(),"rep+a","0001_L.TIF"));
		ci.setTarget_folder("");
		
		cs.convertFile(ci);
		
		assertFalse(new File(workAreaRootPath + "work/TEST/1/data/rep+b/0001_L.TIF").exists());
	}
	
	/**
	 * Test return code on Tiff, which isn't a picture at all. (Assume it has passed the fido checks 
	 * done before)
	 */
	@Test
	public void testConversionBuggyTiff () {
		ConversionInstruction ci = new ConversionInstruction();
		ConversionRoutine cr = new ConversionRoutine();
		ci.setConversion_routine(cr);
		ci.setSource_file(new DAFile(o.getLatestPackage(),"rep+a","notanytiff.tif"));
		ci.setTarget_folder("");
		
		try {
		cs.convertFile(ci);
		assertFalse(true);
		} catch (Exception e) {
			
		}
		assertFalse(new File(workAreaRootPath + "work/TEST/1/data/rep+b/notanytiff.tif").exists());
		
	}
	
	
	/**
	 * Cleanup.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@After
	public void cleanup() throws IOException {
		new File(workAreaRootPath + "work/TEST/1/data/rep+b/CCITT_1.TIF").delete();
		new File(workAreaRootPath + "work/TEST/1/data/rep+b/CCITT_1_UNCOMPRESSED.TIF").delete();
		new File(workAreaRootPath + "work/TEST/1/data/rep+b/0001_L.TIF").delete();
		org.apache.commons.io.FileUtils.deleteDirectory(new File(workAreaRootPath + "work/TEST/1/data/rep+b/subfolder"));
	}
	


}
