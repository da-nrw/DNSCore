package de.uzk.hki.da.cb;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.uzk.hki.da.model.Contractor;
import de.uzk.hki.da.model.DAFile;
import de.uzk.hki.da.model.Node;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.model.Package;

public class Testutf8 {

	private String basedir="src/test/resources/testutf8";
	private Package p;
	
	@Before
	public void setUp() throws Exception {
		Node n = new Node();
		n.setWorkAreaRootPath(basedir);
		Object o = new Object();
		Contractor c = new Contractor();
		c.setShort_name("TEST");
		o.setContractor(c);
		o.setTransientNodeRef(n);
		o.setIdentifier("123");
		p = new Package();
		o.getPackages().add(p);
		p.setTransientBackRefToObject(o);
		
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		p.scanRepRecursively("a"); 
		for (DAFile f:p.getFiles()) {
			assertTrue(f.toRegularFile().exists());
			File file = new File(basedir +"/TEST/123/data/a/" + f.toRegularFile().getName());
			assertTrue(file.exists());
		} 
		}

}
