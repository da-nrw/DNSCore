package de.uzk.hki.da.metadata;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;

import org.junit.*;

import de.uzk.hki.da.model.ObjectPremisXmlReader;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.model.Package;

public class PremisTest {
	
	@Test
	public void premisParsen() throws IOException, ParseException {
		ObjectPremisXmlReader premis = new ObjectPremisXmlReader();
		Object o = premis.deserialize(new File("src/test/resources/metadata/premistest.xml"));
		
		Package p = o.getPackages().get(0);
		
		
		try {
			Thread.sleep(5000);
		} catch (Exception e) {
			
		}
		
		System.out.println(p.getId());
		System.out.println(p.getChecksum());
		System.out.println(p.getEvents().get(0).getIdentifier());
		
		assertTrue(true);
	}
}
