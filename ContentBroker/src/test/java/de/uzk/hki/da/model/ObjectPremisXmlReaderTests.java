package de.uzk.hki.da.model;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;

import org.junit.Test;

import de.uzk.hki.da.utils.Path;
import de.uzk.hki.da.utils.RelativePath;



public class ObjectPremisXmlReaderTests {

	private static final Path WORK_AREA_ROOT_PATH = new RelativePath("src/test/resources/model/ObjectPremisXmlReader/");
	private File premis = Path.makeFile(WORK_AREA_ROOT_PATH, "premis.xml");
	
	@Test
	public void parsePremisTest() {
		
		Object o = null;
		
		try {
			o = new ObjectPremisXmlReader()
			.deserialize(premis);
		} catch (NullPointerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (!o.grantsRight("MIGRATION")) {
			System.out.println("");
		}
	}
	
}
