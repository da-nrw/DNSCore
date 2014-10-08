package de.uzk.hki.da.utils;


import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.uzk.hki.da.model.RightsSectionURNMetsXmlReader;

public class BomRemoverTest {

	static File fileBom;
	static File tempBom;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		fileBom = new File ("src/test/resources/utils/BomRemover/00000115.mods.xml");
		tempBom = new File ("src/test/resources/utils/BomRemover/00000115.temp.xml");
		FileUtils.copyFile(fileBom, tempBom);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
		tempBom.delete();
	}

	@Test
	public void testBomRemove() throws IOException {
	
		BomRemover bom = new BomRemover();
		assertTrue(bom.startsWithBOM(tempBom));
		bom.stripBomFrom(tempBom);
		assertFalse(bom.startsWithBOM(tempBom));
		RightsSectionURNMetsXmlReader metsUrnReader = new RightsSectionURNMetsXmlReader();
		try {
			String urn = metsUrnReader.readURN(tempBom);
			assertEquals(null,urn);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	
	}
	
	
}
