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
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uzk.hki.da.format.PublishXSLTConversionStrategy;
import de.uzk.hki.da.model.ConversionInstruction;
import de.uzk.hki.da.model.ConversionRoutine;
import de.uzk.hki.da.model.DAFile;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.utils.Path;
import de.uzk.hki.da.utils.RelativePath;
import de.uzk.hki.da.utils.TESTHelper;
import de.uzk.hki.da.utils.TC;


/**
 * The Class PublishXsltConversionStrategyTests.
 */
public class PublishXsltConversionStrategyTests {
	
	/** The Constant logger. */
	private final static Logger logger = 
			LoggerFactory.getLogger(PublishXsltConversionStrategyTests.class);
	
	private final static Path workAreaRootPath = 
			Path.make(TC.TEST_ROOT_FORMAT,"XsltConversionStrategyTests");
	
	/** The routine. */
	ConversionRoutine routine;
	
	/** The obj. */
	Object  obj = null;
	
	
	/**
	 * Sets the up.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@Before
	public void setUp() throws IOException {
		
		obj = TESTHelper.setUpObject("1",new RelativePath(workAreaRootPath));
		obj.setIdentifier("1");
//		when(pkg.getNameOfNewestRep()).thenReturn("target");
		
		 routine = new ConversionRoutine(
				 "XSLT_ead_to_dc", null,
				 "de.uzk.hki.da.cb.XSLTConversionStrategy",
				 "conf/xslt/dc/ead_to_dc.xsl", "xml");
	}
	
	/**
	 * Tear down.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@After
	public void tearDown() throws IOException {
		FileUtils.deleteDirectory(Path.makeFile(workAreaRootPath,"work/TEST/1/data/dip/public"));
		FileUtils.deleteDirectory(Path.makeFile(workAreaRootPath,"work/TEST/1/data/dip/institution"));
	}
	
	/**
	 * Test error handling correct.
	 *
	 * @throws FileNotFoundException the file not found exception
	 */
	@Test
	public void testErrorHandlingCorrect() throws FileNotFoundException {
		
		PublishXSLTConversionStrategy strategy = new PublishXSLTConversionStrategy();
		strategy.setObject(obj);
		ConversionInstruction ci = new ConversionInstruction(
				0,
				"", routine, null);
		
		ci.setSource_file(new DAFile(obj.getLatestPackage(),"","ead_correct.xml"));
		strategy.setStylesheet(workAreaRootPath + "/ead_to_dc.xsl");
		strategy.convertFile(ci);
		
		File targetFile = Path.makeFile(workAreaRootPath, 
				"work/TEST/1/data/dip/public/ead_correct_XSLT_ead_to_dc.xml");
		assertTrue(targetFile.exists());
		assertTrue(FileUtils.sizeOf(targetFile) > 0);
		
	}
	
	/**
	 * Test error handling not well formed.
	 *
	 * @throws FileNotFoundException the file not found exception
	 */
	@Test
	public void testErrorHandlingNotWellFormed() throws FileNotFoundException {
		
		try {
		
			PublishXSLTConversionStrategy strategy = new PublishXSLTConversionStrategy();
			strategy.setObject(obj);
			ConversionInstruction ci = new ConversionInstruction(
					0, "", routine, null);
			
			ci.setSource_file(new DAFile(obj.getLatestPackage(),"","ead_not-well-formed.xml"));
			strategy.setStylesheet(workAreaRootPath + "/ead_to_dc.xsl");
			strategy.convertFile(ci);
			
			// should not be reached since exception should be thrown in convertFile
			fail();
			
		} catch (RuntimeException e) {
			logger.info("OK, caught expected exception.");
		}
		
	}
	
	/**
	 * Test error handling not valid.
	 *
	 * @throws FileNotFoundException the file not found exception
	 */
	@Test
	public void testErrorHandlingNotValid() throws FileNotFoundException {
		
		PublishXSLTConversionStrategy strategy = new PublishXSLTConversionStrategy();
		strategy.setObject(obj);
		ConversionInstruction ci = new ConversionInstruction(
				0,
				"", routine, null);
		
		ci.setSource_file(new DAFile(obj.getLatestPackage(),"","ead_not-valid.xml"));
		strategy.setStylesheet(workAreaRootPath + "/ead_to_dc.xsl");
		strategy.convertFile(ci);
		
		File targetFile = Path.makeFile(workAreaRootPath, 
				"work/TEST/1/data/dip/public/ead_not-valid_XSLT_ead_to_dc.xml");
		assertTrue(targetFile.exists());
		assertTrue(FileUtils.sizeOf(targetFile) > 0);
		
	}
	
	/**
	 * Test error handling charset errors.
	 *
	 * @throws FileNotFoundException the file not found exception
	 */
	@Test
	public void testErrorHandlingCharsetErrors() throws FileNotFoundException {
		
		try {
		
			PublishXSLTConversionStrategy strategy = new PublishXSLTConversionStrategy();
			strategy.setObject(obj);
			ConversionInstruction ci = new ConversionInstruction(
					0, 
					"", routine, null);
			
			ci.setSource_file(new DAFile(obj.getLatestPackage(),"","ead_charset-errors.xml"));
			strategy.setStylesheet(workAreaRootPath + "/ead_to_dc.xsl");
			strategy.convertFile(ci);
			
			// should not be reached since exception should be thrown in convertFile
			fail();
			
		} catch (RuntimeException e) {
			logger.info("OK, caught expected exception.");
		}
	}
}
