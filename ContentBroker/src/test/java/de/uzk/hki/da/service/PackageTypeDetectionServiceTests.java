package de.uzk.hki.da.service;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import de.uzk.hki.da.model.DAFile;
import de.uzk.hki.da.model.Package;

public class PackageTypeDetectionServiceTests {

	private Package pkg;

	@Before
	public void setUp(){
		DAFile f2 = new DAFile(null,"","mets_2_99.xml"); f2.setFormatPUID("danrw-fmt/1");
		DAFile f3 = new DAFile(null,"","mets_2_998.xml"); f3.setFormatPUID("danrw-fmt/1");
		DAFile f1 = new DAFile(null,"","vda3.XML"); f1.setFormatPUID("danrw-fmt/2");
		pkg = new Package();
		pkg.getFiles().add(f1);
		pkg.getFiles().add(f2);
		pkg.getFiles().add(f3);
	}
	
	@Test
	public void test() {
		PackageTypeDetectionService ptd = new PackageTypeDetectionService(pkg);
		assertEquals("EAD",ptd.getPackageType());
	}
}
