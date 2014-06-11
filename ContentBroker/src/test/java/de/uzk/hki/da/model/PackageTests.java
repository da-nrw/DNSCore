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
package de.uzk.hki.da.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.utils.Path;
import de.uzk.hki.da.utils.RelativePath;

import java.util.List;

import org.junit.Before;
import org.junit.Test;


/**
 * The Class PackageTests.
 */
public class PackageTests {

	String workAreaRootPath = "src/test/resources/model/PackageTests/";
	
	/** The n. */
	Node node;
	
	/**
	 * Sets the up.
	 */
	@Before
	public void setUp() {
		node = new Node();
		node.setWorkAreaRootPath(new RelativePath(workAreaRootPath));
	}
	
//	@Test
//	public void testGetLatestFile() {
//		
//		Package pkg = new Package();
//		pkg.setContractorShortName("TEST");
//		pkg.setId(2);
//		pkg.setLocalNode(n);
//		
//		DAFile f = pkg.getLatest("premis.xml");
//		
//		assertThat( f.getRelative_path(), is ( equalTo("premis.xml")  ));
//		assertThat( f.getRepName(), is (equalTo("2012+12+12+b" ) ));
//	}
	
	/**
 * Test.
 */
@Test
	public void test() {
		
		String repName = "test";
		Object object = new Object();
		Contractor contractor = new Contractor();
		contractor.setShort_name("TEST");
		Package pkg = new Package();
		pkg.setId(1);
		pkg.setName("1");
		object.getPackages().add(pkg);
		object.setContractor(contractor);
		object.setIdentifier("1");
		object.setTransientNodeRef(node);
		object.reattach();
		
		List<DAFile> files = pkg.scanRepRecursively(
				repName);

		int checksPerformed = 0;
		for (DAFile file:files){
		
			if (file.getRelative_path().contains("abc")){
				assertEquals("abc.txt",file.getRelative_path());
				assertEquals("test",
						file.getRep_name());
				checksPerformed++;
			}
			if (file.getRelative_path().contains("cde")){
				assertEquals("sub/cde.txt",file.getRelative_path());
				assertEquals("test",
						file.getRep_name());
				checksPerformed++;
			}
		
		}
		if (checksPerformed!=2) fail("not enough checksPerformed");
	}
}
