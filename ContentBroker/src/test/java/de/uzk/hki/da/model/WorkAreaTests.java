/*ileleee
 DA-NRW Software Suite | ContentBroker
 Copyright (C) 2014 Historisch-Kulturwissenschaftliche Informationsverarbeitung
 Universität zu Köln
 Copyright (C) 2015 LVR-Infokom
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

package de.uzk.hki.da.model;

import static org.junit.Assert.*;
import static de.uzk.hki.da.core.C.*;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.uzk.hki.da.model.Node;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.model.User;
import de.uzk.hki.da.model.WorkArea;
import de.uzk.hki.da.test.TC;
import de.uzk.hki.da.util.Path;

/**
 * @author Daniel M. de Oliveira
 */
public class WorkAreaTests {

	
	private static final Path WORK_AREA_ROOT_PATH = Path.make(TC.TEST_ROOT_MODEL,"WorkArea");
	private static final Path WORK_AREA_ROOT_PATH_TMP = Path.make(TC.TEST_ROOT_MODEL,"WorkArea_");
	Object o = new Object();
	Node n = new Node();
	private WorkArea wa;
	
	@Before
	public void setUp() throws IOException {
		FileUtils.copyDirectory(WORK_AREA_ROOT_PATH_TMP.toFile(), WORK_AREA_ROOT_PATH.toFile());
		
		o.setIdentifier(TC.IDENTIFIER);
		n.setWorkAreaRootPath(WORK_AREA_ROOT_PATH);
		
		User c = new User();
		c.setShort_name(TEST_USER_SHORT_NAME);
		o.setContractor(c);

		wa = new WorkArea(n,o);
	}
	
	@After 
	public void tearDown() throws IOException {
		FileUtils.deleteDirectory(WORK_AREA_ROOT_PATH.toFile());
	}
	
	

	@Test
	public void waDoesNotExist() throws IOException {
		FileUtils.deleteDirectory(WORK_AREA_ROOT_PATH.toFile());
		try {
			new WorkArea(n,o);
			fail();
		}catch(Exception expected) {}
	}
	
	@Test
	public void missingContractorDirPublicPIPs() throws IOException {
		FileUtils.deleteDirectory(Path.makeFile(WORK_AREA_ROOT_PATH,WA_PIPS,WA_PUBLIC,o.getContractor().getShort_name()));
		try {
			new WorkArea(n,o);
			fail();
		}catch(Exception expected) {}
	}
	
	@Test
	public void missingContractorDirInstitutionPIPs() throws IOException {
		FileUtils.deleteDirectory(Path.makeFile(WORK_AREA_ROOT_PATH,WA_PIPS,WA_INSTITUTION,o.getContractor().getShort_name()));
		try {
			new WorkArea(n,o);
			fail();
		}catch(Exception expected) {}	
	}
	
	@Test
	public void missingWorkingDirContractorObjects() throws IOException {
		FileUtils.deleteDirectory(Path.makeFile(WORK_AREA_ROOT_PATH,WA_WORK,o.getContractor().getShort_name()));
		try {
			new WorkArea(n,o);
			fail();
		}catch(Exception expected) {}
	}
	
	
	@Test
	public void pipFolder() {
		Path rp = wa.pipFolder(WA_PUBLIC);
		assertEquals(Path.make(WORK_AREA_ROOT_PATH,WA_PIPS,WA_PUBLIC,o.getContractor().getShort_name(),o.getIdentifier()),rp);
		Path ri = wa.pipFolder(WA_INSTITUTION);
		assertEquals(Path.make(WORK_AREA_ROOT_PATH,WA_PIPS,WA_INSTITUTION,o.getContractor().getShort_name(),o.getIdentifier()),ri);
	}
	
	@Test
	public void metadataFile() {
		final String metadataFileName = "metadata";
		File r = wa.metadataStream(WA_PUBLIC,metadataFileName);
		assertEquals(Path.makeFile(WORK_AREA_ROOT_PATH,WA_PIPS,WA_PUBLIC,o.getContractor().getShort_name(),o.getIdentifier(),metadataFileName+FILE_EXTENSION_XML),r);
	}
	
	@Test
	public void toFile() {
		final String repName="rep+a";
		final String relativePath="sub/file.txt"; 
		DAFile daf = new DAFile(null,repName,relativePath);
		File r = wa.toFile(daf);
		assertEquals(Path.makeFile(WORK_AREA_ROOT_PATH,WA_WORK,o.getContractor().getShort_name(),o.getIdentifier(),repName,relativePath),r);
	}
	
}
