/*
  DA-NRW Software Suite | ContentBroker
  Copyright (C) 2015 LVR-InfoKom
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.jdom.JDOMException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.uzk.hki.da.model.DAFile;
import de.uzk.hki.da.model.Package;
import de.uzk.hki.da.test.TC;
import de.uzk.hki.da.util.Path;

/**
 * @author Daniel M. de Oliveira
 */
public class RegisterURNActionTest extends ConcreteActionUnitTest {
	
	@ActionUnderTest
	RegisterURNAction action = new RegisterURNAction();
	
	private static final Path WORK_AREA_ROOT_PATH = Path.make(TC.TEST_ROOT_CB,"RegisterURNAction"); 
	private DAFile premis = null;
	
	@Before
	public void setUp() throws IOException {
		n.setWorkAreaRootPath(WORK_AREA_ROOT_PATH);
		
		premis = new DAFile("2012_12_12+12_12_12+a","premis.xml");
		o.getLatestPackage().getFiles().add(premis);
		
		FileUtils.copyFile(Path.makeFile(WORK_AREA_ROOT_PATH,"premis.xml.1"), wa.toFile(premis));
		o.setUrn(null);
		o.setPackage_type("NONE");
	}

	@After
	public void tearDown() {
		wa.toFile(premis).delete();
	}
	
	
	@Test
	public void newIdentifier() throws FileNotFoundException, JDOMException, IOException {
		action.implementation();
		assertEquals(ps.getUrnNameSpace()+"-"+o.getIdentifier(),o.getUrn());
	}
	
	
	@Test
	public void identifierFromPREMIS() throws IOException, JDOMException {
		FileUtils.copyFile(Path.makeFile(WORK_AREA_ROOT_PATH,"premis.xml.urn"), wa.toFile(premis));
	
		action.implementation();
		assertEquals("urn:nbn:de:xyz-1-20131008367735",o.getUrn());
	}
	
	@Test
	public void dontOverrideURNWhenDeltaAndURNInPremis() throws IOException {
		FileUtils.copyFile(Path.makeFile(WORK_AREA_ROOT_PATH,"premis.xml.urn"), wa.toFile(premis));
		
		Package pkg = new Package();
		pkg.setName("2");
		o.getPackages().add(pkg);
		String prv_urn = "previous_urn";
		o.setUrn(prv_urn);
		
		action.implementation();
		assertEquals("previous_urn",o.getUrn());
	}
	
	
	@Test
	public void dontOverrideURNWhenDelta() {
		Package pkg = new Package();
		pkg.setName("2");
		o.getPackages().add(pkg);
		String prv_urn = "previous_urn";
		o.setUrn(prv_urn);
		
		action.implementation();
		assertEquals(prv_urn,o.getUrn());
	}
	
	
	@Test
	public void rollbackWhenNotDelta() {
		action.implementation();
		
		action.rollback();
		assertTrue(o.getUrn()==null);
	}
	
	
	@Test
	public void rollbackWhenDelta() {
		
		Package pkg = new Package();
		pkg.setName("2");
		o.getPackages().add(pkg);
		
		String prv_urn = "previous_urn";
		o.setUrn(prv_urn);
		action.implementation();
		
		action.rollback();
		assertEquals(prv_urn,o.getUrn());
	}
	
	
	
}
