package de.uzk.hki.da.model;

import static org.junit.Assert.*;

import org.junit.Test;

import de.uzk.hki.da.core.Path;
import de.uzk.hki.da.model.Node;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.model.User;
import de.uzk.hki.da.model.WorkArea;

public class WorkAreaTests {

	
	@Test
	public void test(){
		Node n = new Node();
		n.setWorkAreaRootPath(Path.make("/tmp/"));
		
		Object o = new Object();
		o.setIdentifier("identifier");
		
		User c = new User();
		c.setShort_name("TEST");
		o.setContractor(c);
		
		
		WorkArea wa = new WorkArea(n,o);
		
		assertEquals(Path.make("tmp","work","TEST","identifier"),wa.getPath(o));
		
	}
}
