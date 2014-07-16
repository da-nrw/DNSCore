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

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Test;

import de.uzk.hki.da.core.UserException;
import de.uzk.hki.da.repository.RepositoryException;
import de.uzk.hki.da.utils.Path;
import de.uzk.hki.da.utils.TESTHelper;
import de.uzk.hki.da.utils.TestConstants;
import de.uzk.hki.da.model.DAFile;
import de.uzk.hki.da.model.Object;

/**
 * @author Daniel M. de Oliveira
 */
public class ValidateMetadataActionTests {

	private static final String IDENTIFIER = "identifier";
	private static final Path WORK_AREA_ROOT = Path.make(TestConstants.TEST_ROOT_CB,"ValidateMetadataActionTests");


	@Test
	public void testRejectPackageWithDuplicateEADFile() throws FileNotFoundException, UserException, IOException, RepositoryException{
		
//		ValidateMetadataAction action = new ValidateMetadataAction();
//		action.implementation();
	}
	
	
	@Test
	public void testDetectEAD() throws FileNotFoundException, UserException, IOException, RepositoryException{
		
		DAFile f1 = new DAFile(null,"rep+a","vda03.xml");
		f1.setFormatPUID("danrw-fmt/2");
		DAFile f2 = new DAFile(null,"","mets_2_99.xml"); f2.setFormatPUID("danrw-fmt/1");
		DAFile f3 = new DAFile(null,"","mets_2_998.xml"); f3.setFormatPUID("danrw-fmt/1");
		
		
		Object object = TESTHelper.setUpObject(IDENTIFIER,WORK_AREA_ROOT);
		object.getLatestPackage().getFiles().add(f1);
		object.getLatestPackage().getFiles().add(f2);
		object.getLatestPackage().getFiles().add(f3);
		
		ValidateMetadataAction action = new ValidateMetadataAction();
		action.setObject(object);
		action.implementation();
		
		assertEquals("vda03.xml",object.getMetadata_file());
		assertEquals("EAD",object.getPackage_type());
		
	}
	
	
	
	
	
}
