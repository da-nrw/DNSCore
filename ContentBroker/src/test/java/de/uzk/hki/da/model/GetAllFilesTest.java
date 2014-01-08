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

import static org.fest.assertions.Assertions.assertThat;

import java.util.List;

import org.junit.Before;
import org.junit.Test;





/**
 * The Class GetAllFilesTest.
 */
public class GetAllFilesTest {

	/** The working area root path. */
	private String workingAreaRootPath = "src/test/resources/model/Object/GetAllFiles";
	
	/** The object. */
	private Object object;
	
	/**
	 * Sets the up.
	 */
	@Before
	public void setUp(){

		Contractor contractor = new Contractor();
		contractor.setShort_name("TEST");
		
		Node localNode = new Node();
		localNode.setWorkAreaRootPath(workingAreaRootPath);
		
		object = new Object();
		object.setContractor(contractor);
		object.setIdentifier("2");
		object.setTransientNodeRef(localNode);
		
		Package pkg = new Package();
		pkg.setName("2");
		pkg.setId(2);
		object.getPackages().add(pkg);
	}
	
	/**
	 * Test.
	 */
	@Test
	public void test(){
		
		List<DAFile> allFiles = object.getAllFiles();
		 
		assertThat(allFiles).contains(new DAFile(null,"2011_11_11+11_11+a","_2.jpg"));
		assertThat(allFiles).contains(new DAFile(null,"2011_11_11+11_11+b","_2.tif"));
		
		
	}
}
