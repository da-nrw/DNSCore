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
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.uzk.hki.da.core.C;
import de.uzk.hki.da.repository.Fedora3RepositoryFacade;
import de.uzk.hki.da.repository.RepositoryException;
import de.uzk.hki.da.repository.RepositoryFacade;
import de.uzk.hki.da.test.TC;
import de.uzk.hki.da.util.Path;


/**
 * @author Daniel M. de Oliveira
 */
public class CreateEDMActionTests extends ConcreteActionUnitTest{

	@ActionUnderTest
	CreateEDMAction action = new CreateEDMAction();

	private static final String METS_MODS_TO_EDM_XSL = "src/main/xslt/edm/mets-mods_to_edm.xsl";
	private static final String EAD_TO_EDM_XSL = "src/main/xslt/edm/ead_to_edm.xsl";
	private static final Path WORK_AREA_ROOT_PATH = Path.make(TC.TEST_ROOT_CB,"CreateEDMAction");
	private static final String METADATAFILENAME = C.CB_PACKAGETYPE_EAD+C.FILE_EXTENSION_XML;
	
	
	@Before
	public void setUp() throws FileNotFoundException, RepositoryException, IOException {
		n.setWorkAreaRootPath(WORK_AREA_ROOT_PATH);
		o.setPackage_type(C.CB_PACKAGETYPE_EAD);
		
		RepositoryFacade repo = mock(Fedora3RepositoryFacade.class);
		
		Map<String,String> edmMappings = new HashMap<String,String>();
		edmMappings.put(C.CB_PACKAGETYPE_EAD, EAD_TO_EDM_XSL);
		edmMappings.put(C.CB_PACKAGETYPE_METS,METS_MODS_TO_EDM_XSL);
		action.setEdmMappings(edmMappings);
		action.setRepositoryFacade(repo);
		
		FileUtils.copyDirectory(Path.makeFile(WORK_AREA_ROOT_PATH,"_"+C.WA_PIPS),Path.makeFile(WORK_AREA_ROOT_PATH,C.WA_PIPS));
	}
	
	@After
	public void tearDown() {
		FileUtils.deleteQuietly(Path.makeFile(WORK_AREA_ROOT_PATH,C.WA_PIPS));
	}
	
	@Test
	public void missingPackageType() throws IOException, RepositoryException {
		o.setPackage_type(null);
		try {
			action.checkSystemStatePreconditions();
			fail();
		} catch (IllegalStateException e) {
			assertTrue(e.getMessage().contains("package type"));
		}
	}
	

	@Test
	public void missingMetadataFileInPublicPIP() throws IOException, RepositoryException {
		FileUtils.deleteQuietly(Path.makeFile(WORK_AREA_ROOT_PATH,C.WA_PIPS,
				C.WA_PUBLIC,o.getContractor().getShort_name(),o.getIdentifier(),METADATAFILENAME));
		try {
			action.implementation();
			fail();
		} catch (RuntimeException e) {
			assertTrue(e.getMessage().contains(METADATAFILENAME));
		}
	}
	
	@Test
	public void test() throws IOException, RepositoryException{
		
		action.implementation();
	}
}
