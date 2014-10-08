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

package de.uzk.hki.da.cb;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.uzk.hki.da.core.Path;
import de.uzk.hki.da.core.RelativePath;
import de.uzk.hki.da.grid.DistributedConversionAdapter;
import de.uzk.hki.da.grid.FakeDistributedConversionAdapter;
import de.uzk.hki.da.model.User;
import de.uzk.hki.da.model.Node;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.model.Package;

/**
 * @author Polina Gubaidullina
 */
public class FetchPIPsActionTest{
	
	static FetchPIPsAction action = new FetchPIPsAction();

	private static DistributedConversionAdapter distributedConversionAdapter;
	
	private static Node localNode = new Node();
	
	private static Object object = new Object();
	
	private static Package testPackage = new Package();
	
	private static List<Package> packages = new ArrayList<Package>(); 
	
	String sourceDIPName;
	
	
	private static Path testDir = new RelativePath("src", "test", "resources", "cb", "FetchPIPsActionTest");
	private static Path sourcePIPsPath = Path.make(testDir, "sourceDir"); 
	private static Path institutionPartialPath = Path.make("pips", "institution", "TEST");
	private static Path publicPartialPath = Path.make("pips", "public", "TEST");
	private static Path workAreaRootPartialPath = Path.make(testDir, "work");
	private static String packageName = "1";
	private static String objectId = "1";
	
	@AfterClass
	public static void cleanUp() {
		FileUtils.deleteQuietly(Path.make(workAreaRootPartialPath, institutionPartialPath).toFile());
		FileUtils.deleteQuietly(Path.make(workAreaRootPartialPath, publicPartialPath).toFile());
	}
	
	@BeforeClass
	public static void initObject() {
		distributedConversionAdapter = mock(FakeDistributedConversionAdapter.class);
		localNode.setWorkAreaRootPath(workAreaRootPartialPath);
		User contractor = new User();
		contractor.setShort_name("TEST");
		object.setContractor(contractor);
		object.setIdentifier(objectId);
		testPackage.setId(1);
		testPackage.setName(packageName);
		packages.add(testPackage);
		object.setPackages(packages);
		try {
			FileUtils.copyDirectory(Path.make(sourcePIPsPath, institutionPartialPath).toFile(), Path.make(workAreaRootPartialPath, institutionPartialPath).toFile());
			FileUtils.copyDirectory(Path.make(sourcePIPsPath, publicPartialPath).toFile(), Path.make(workAreaRootPartialPath, publicPartialPath).toFile());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Before
	public void initImpementation() throws FileNotFoundException, IOException {
		action.setLocalNode(localNode);
		action.setObject(object);
		action.setDistributedConversionAdapter(distributedConversionAdapter);
		action.implementation();
	}
	
	
	@Test
	public void testRenamePIPs(){
		assertTrue(Path.makeFile(workAreaRootPartialPath, publicPartialPath, objectId).exists());
		assertTrue(Path.makeFile(workAreaRootPartialPath, institutionPartialPath, objectId).exists());
	}
}
