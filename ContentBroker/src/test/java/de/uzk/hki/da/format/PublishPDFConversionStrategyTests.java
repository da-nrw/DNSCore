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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.uzk.hki.da.core.C;
import de.uzk.hki.da.core.Path;
import de.uzk.hki.da.core.RelativePath;
import de.uzk.hki.da.format.PublishPDFConversionStrategy;
import de.uzk.hki.da.model.ConversionInstruction;
import de.uzk.hki.da.model.ConversionRoutine;
import de.uzk.hki.da.model.DAFile;
import de.uzk.hki.da.model.Event;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.model.PublicationRight;
import de.uzk.hki.da.model.TextRestriction;
import de.uzk.hki.da.model.PublicationRight.Audience;
import de.uzk.hki.da.test.TC;
import de.uzk.hki.da.test.TESTHelper;


/**
 * The Class PublishPDFConversionStrategyTests.
 */
public class PublishPDFConversionStrategyTests {
	
	Path workAreaRootPath= Path.make(TC.TEST_ROOT_FORMAT,"PublishPDFConversionStrategyTests");
	Path dataPath = Path.make(workAreaRootPath,"work/TEST/1",C.WA_DATA);
	Path dipPath = Path.make(dataPath,C.WA_DIP);
	
	/** The cs. */
	PublishPDFConversionStrategy cs = new PublishPDFConversionStrategy();
	
	/** The cr. */
	ConversionRoutine cr;
	
	/** The o. */
	Object o;

	
	@Before
	public void setUp(){
		dipPath.toFile().mkdirs();
	}
	
	@After
	public void tearDown() throws IOException {
		FileUtils.deleteQuietly(dipPath.toFile());
	}
	
	/**
	 * Test.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@Test
	public void test() throws IOException {
		
		o = TESTHelper.setUpObject("1", new RelativePath(workAreaRootPath));
		PublicationRight right = new PublicationRight();
		right.setAudience(Audience.PUBLIC);
		right.setTextRestriction(new TextRestriction());
		right.getTextRestriction().setCertainPages(new int[] { 1, 2, 7, 10, 12, 14, 15});
		o.getRights().getPublicationRights().add(right);
		
		
		DAFile sourceFile = new DAFile(o.getLatestPackage(),"a","filename.pdf");
		
		ConversionInstruction ci = new ConversionInstruction();
		ci.setSource_file(sourceFile);
		ci.setTarget_folder("target/");
		
		ConversionRoutine cr = new ConversionRoutine();
		cr.setTarget_suffix("pdf");
		ci.setConversion_routine(cr);
		
		cs.setObject(o);
		List<Event> events = cs.convertFile(ci);
	
		File targetFile = events.get(0).getTarget_file().toRegularFile();
		assertTrue(targetFile.exists());
		assertEquals("filename.pdf", targetFile.getName());
		
		// contract states that the PDF should have 7 pages
		PDDocument targetDoc = PDDocument.load(targetFile);
		assertEquals(7, targetDoc.getDocumentCatalog().getAllPages().size());
		
		targetFile = events.get(1).getTarget_file().toRegularFile();
		assertTrue(targetFile.exists());
		assertEquals("filename.pdf", targetFile.getName());
		
		// contract has no restrictions for institution, PDF should have
		targetDoc = PDDocument.load(targetFile);
		assertEquals(227, targetDoc.getDocumentCatalog().getAllPages().size());
		
	}

}
