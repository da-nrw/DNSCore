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

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Test;

import de.uzk.hki.da.core.Path;
import de.uzk.hki.da.model.DAFile;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.test.TC;
import de.uzk.hki.da.test.TESTHelper;

/**
 * @author Daniel M. de Oliveira
 */
public class XMPCollectorTests {

	private static final Path WORK_AREA_ROOT_PATH = Path.make(TC.TEST_ROOT_METADATA,"XmpCollectorTests","WorkArea");
	
	DAFile target = null;
	
	
	@After
	public void tearDown(){
		if (target!=null&&target.toRegularFile().exists()) target.toRegularFile().delete();
	}
	
	@Test
	public void test(){
		Object object = TESTHelper.setUpObject("identifier", WORK_AREA_ROOT_PATH);
		
		DAFile xmp = new DAFile(object.getLatestPackage(),"1+a","abc.xmp");
		target = new DAFile(object.getLatestPackage(),"1+a","target.rdf");
		
		List<DAFile> xmps = new ArrayList<DAFile>();
		xmps.add(xmp);
		
		XmpCollector.collect(xmps, target.toRegularFile());
		assertTrue(target.toRegularFile().exists());
		
	}
}
