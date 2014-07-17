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

import org.junit.Before;
import org.junit.Test;

import de.uzk.hki.da.core.UserException;
import de.uzk.hki.da.repository.RepositoryException;
import de.uzk.hki.da.utils.C;
import de.uzk.hki.da.utils.Path;
import de.uzk.hki.da.utils.TESTHelper;
import de.uzk.hki.da.utils.TestConstants;
import de.uzk.hki.da.model.DAFile;
import de.uzk.hki.da.model.Object;

/**
 * @author Daniel M. de Oliveira
 */
public class ValidateMetadataActionTests {

	private static final String METS_2_99_XML = "mets_2_99.xml";
	private static final String IDENTIFIER = "identifier";
	private static final Path WORK_AREA_ROOT = Path.make(TestConstants.TEST_ROOT_CB,"ValidateMetadataActionTests");
	private Object object;
	ValidateMetadataAction action = new ValidateMetadataAction();

	DAFile f_ead1 = new DAFile(null,"rep+a","vda03.xml");
	DAFile f_ead2 = new DAFile(null,"rep+b","EAD.XML");
	DAFile f_mets1 = new DAFile(null,"",METS_2_99_XML); 
	DAFile f_mets2 = new DAFile(null,"","mets_2_998.xml");

	
	@Before
	public void setUp(){
		object = TESTHelper.setUpObject(IDENTIFIER,WORK_AREA_ROOT);
		action.setObject(object);

		f_ead1.setFormatPUID(C.EAD_PUID);
		f_ead2.setFormatPUID(C.EAD_PUID);
		f_mets1.setFormatPUID(C.METS_PUID);
		f_mets2.setFormatPUID(C.METS_PUID);
	}
	
	
	@Test
	public void testRejectPackageWithDuplicateEADFile() throws FileNotFoundException, UserException, IOException, RepositoryException{
		
		object.getLatestPackage().getFiles().add(f_ead1);
		object.getLatestPackage().getFiles().add(f_ead2);
		object.getLatestPackage().getFiles().add(f_mets1);
		object.getLatestPackage().getFiles().add(f_mets2);
		
		try{
			action.implementation();
			fail();
		}catch(UserException e){
			
		}
	}
	
	@Test
	public void testDetectEAD() throws FileNotFoundException, UserException, IOException, RepositoryException{
		
		object.getLatestPackage().getFiles().add(f_ead1);
		object.getLatestPackage().getFiles().add(f_mets1);
		object.getLatestPackage().getFiles().add(f_mets2);
		
		action.implementation();
		
		assertEquals("EAD",object.getPackage_type());
		assertEquals("vda03.xml",object.getMetadata_file());
	}

	@Test
	public void testMoreThanOneMETSAndNoEAD() throws FileNotFoundException, UserException, IOException, RepositoryException{

		object.getLatestPackage().getFiles().add(f_mets1);
		object.getLatestPackage().getFiles().add(f_mets2);
		
		try{
			action.implementation();
			fail();
		} catch (UserException e){
			
		}
	}
	
	@Test 
	public void testDetectMETS() throws FileNotFoundException, UserException, IOException, RepositoryException{
		
		object.getLatestPackage().getFiles().add(f_mets1);
		
		action.implementation();
		
		assertEquals(C.METS,object.getPackage_type());
		assertEquals(METS_2_99_XML,object.getMetadata_file());
	}
}
