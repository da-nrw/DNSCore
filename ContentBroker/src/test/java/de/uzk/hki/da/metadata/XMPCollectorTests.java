/*
  DA-NRW Software Suite | ContentBroker
  Copyright (C) 2014 LVRInfoKom
  Landschaftsverband Rheinland

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

package de.uzk.hki.da.metadata;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.uzk.hki.da.model.DAFile;
import de.uzk.hki.da.model.Node;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.model.WorkArea;
import de.uzk.hki.da.test.TC;
import de.uzk.hki.da.test.TESTHelper;
import de.uzk.hki.da.util.Path;

/**
 * @author Daniel M. de Oliveira
 */
public class XMPCollectorTests {

	private static final Path WORK_AREA_ROOT_PATH = Path.make(TC.TEST_ROOT_METADATA,"XmpCollectorTests","WorkArea");
	
	DAFile target = null;

	private Object o;

	private WorkArea wa;
	
	@Before
	public void setUp() {
		o = TESTHelper.setUpObject("identifier", WORK_AREA_ROOT_PATH);
		Node n = new Node();
		n.setWorkAreaRootPath(WORK_AREA_ROOT_PATH);
		wa = new WorkArea(n,o);
	}
	
	
	@After
	public void tearDown(){
		if (target!=null&&wa.toFile(target).exists()) wa.toFile(target).delete();
	}
	
	@Test
	public void test() throws IOException{
		
		DAFile xmp = new DAFile("1+a","abc.xmp");
		target = new DAFile("1+a","target.rdf");
		
		List<DAFile> xmps = new ArrayList<DAFile>();
		xmps.add(xmp);
		
		XmpCollector.collect(wa,xmps,wa.toFile(target));
		assertTrue(wa.toFile(target).exists());
		
	}
}
