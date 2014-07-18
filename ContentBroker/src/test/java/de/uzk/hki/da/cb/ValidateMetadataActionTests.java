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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import de.uzk.hki.da.core.UserException;
import de.uzk.hki.da.model.DAFile;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.model.Package;
import de.uzk.hki.da.repository.RepositoryException;
import de.uzk.hki.da.utils.C;
import de.uzk.hki.da.utils.Path;
import de.uzk.hki.da.utils.TESTHelper;
import de.uzk.hki.da.utils.TC;

/**
 * @author Daniel M. de Oliveira
 */
public class ValidateMetadataActionTests {

	private static final String METADATA = "METADATA";
	private static final String REP_B = "rep+b";
	private static final String REP_A = "rep+a";
	private static final String EAD_XML = "EAD.XML";
	private static final String METS_2_998_XML = "mets_2_998.xml";
	private static final String VDA03_XML = "vda03.xml";
	private static final String METS_2_99_XML = "mets_2_99.xml";
	private static final String IDENTIFIER = "identifier";
	private static final Path WORK_AREA_ROOT = Path.make(TC.TEST_ROOT_CB,"ValidateMetadataActionTests");
	private static final String XMP1_XML = "xmp1.xmp";
	private static final String LIDO_XML = "lido1.xml";
	
	
	private Object object;
	
	ValidateMetadataAction action = new ValidateMetadataAction();

	
	DAFile f_ead1 = new DAFile(null,REP_A,VDA03_XML);
	DAFile f_ead2 = new DAFile(null,REP_B,EAD_XML);
	DAFile f_mets1 = new DAFile(null,"",METS_2_99_XML); 
	DAFile f_mets2 = new DAFile(null,"",METS_2_998_XML);
	DAFile f_xmp1 = new DAFile(null,"",XMP1_XML);
	DAFile f_lido1 = new DAFile(null,"",LIDO_XML);
	DAFile f_lido2 = new DAFile(null,"",LIDO_XML);

	
	
	
	@Before
	public void setUp(){
		object = TESTHelper.setUpObject(IDENTIFIER,WORK_AREA_ROOT);
		action.setObject(object);

		f_ead1.setFormatPUID(C.EAD_PUID);
		f_ead2.setFormatPUID(C.EAD_PUID);
		f_mets1.setFormatPUID(C.METS_PUID);
		f_mets2.setFormatPUID(C.METS_PUID);
		f_xmp1.setFormatPUID(C.XMP_PUID);
		f_lido1.setFormatPUID(C.LIDO_PUID);
		f_lido2.setFormatPUID(C.LIDO_PUID);
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
			System.out.println(e.getMessage());
			assertTrue(e.getMessage().contains(C.EAD));
		}
	}
	
	@Test
	public void testDetectEAD() throws FileNotFoundException, UserException, IOException, RepositoryException{
		
		object.getLatestPackage().getFiles().add(f_ead1);
		object.getLatestPackage().getFiles().add(f_mets1);
		object.getLatestPackage().getFiles().add(f_mets2);
		
		action.implementation();
		
		assertEquals(C.EAD,object.getPackage_type());
		assertEquals(VDA03_XML,object.getMetadata_file());
	}

	@Test
	public void testMoreThanOneMETSAndNoEAD() throws FileNotFoundException, UserException, IOException, RepositoryException{

		object.getLatestPackage().getFiles().add(f_mets1);
		object.getLatestPackage().getFiles().add(f_mets2);
		
		try{
			action.implementation();
			fail();
		} catch (UserException e){
			assertTrue(e.getMessage().contains(C.METS));
		}
	}
	
	@Test 
	public void testDetectMETS() throws FileNotFoundException, UserException, IOException, RepositoryException{
		
		object.getLatestPackage().getFiles().add(f_mets1);
		
		action.implementation();
		
		assertEquals(C.METS,object.getPackage_type());
		assertEquals(METS_2_99_XML,object.getMetadata_file());
	}

	@Test
	public void testLido() throws FileNotFoundException, UserException, IOException, RepositoryException{
		
		object.getLatestPackage().getFiles().add(f_lido1);
		
		action.implementation();
		
		assertEquals(C.LIDO,object.getPackage_type());
		assertEquals(LIDO_XML,object.getMetadata_file());
	}
	
	@Test
	public void testRejectPackageWithDuplicateLIDOFile() throws FileNotFoundException, UserException, IOException, RepositoryException{
		
		object.getLatestPackage().getFiles().add(f_lido1);
		object.getLatestPackage().getFiles().add(f_lido2);
		
		try{
			action.implementation();
			fail();
		}catch(UserException e){
			System.out.println(e.getMessage());
			assertTrue(e.getMessage().contains(C.LIDO));
		}
	}
	
	@Test
	public void testXMP() throws FileNotFoundException, UserException, IOException, RepositoryException{
		
		object.getLatestPackage().getFiles().add(f_xmp1);
		
		action.implementation();
		
		assertEquals(C.XMP,object.getPackage_type());
		assertEquals(C.XMP_RDF,object.getMetadata_file());
	}
	

	@Test
	public void testRollback() throws Exception{
		
		object.setMetadata_file(VDA03_XML);
		object.setPackage_type(C.EAD);
		
		action.rollback();
		
		assertEquals(null, object.getMetadata_file());
		assertEquals(null, object.getPackage_type());
	}
	
	@Test
	public void testDetectedPackageTypeCollidesWithPackageTypeOfObject() throws FileNotFoundException, UserException, IOException, RepositoryException{
		
		object.getLatestPackage().getFiles().add(f_mets1);
		
		object.setMetadata_file(VDA03_XML);
		object.setPackage_type(C.EAD);
		
		try { 
			action.implementation();
			fail();
		} catch (RuntimeException e){
			assertEquals("COLLISION",e.getMessage());
		}
	}

	
	
	@Test 
	public void testRollbackMustNotDeletePreviouslyExistentPackageType() throws Exception{
		
		object.setMetadata_file(VDA03_XML);
		object.setPackage_type(C.EAD);
		
		object.getLatestPackage().getFiles().add(f_mets1);
		
		try{
			action.implementation();
		}catch(RuntimeException e){}
		
		action.rollback();
		
		assertEquals(VDA03_XML,object.getMetadata_file());
		assertEquals(C.EAD,object.getPackage_type());
	}
	
	
	@Test
	public void testRejectLIDOAndXMP() throws FileNotFoundException, IOException, RepositoryException{
		object.getLatestPackage().getFiles().add(f_xmp1);
		object.getLatestPackage().getFiles().add(f_lido1);
		
		try{
			action.implementation();
			fail();
		}catch(UserException e){
			System.out.println(e.getMessage());
			assertTrue(e.getMessage().contains(METADATA));
		}
	}
	
	@Test
	public void testRejectMETSAndXMP() throws FileNotFoundException, IOException, RepositoryException{
		object.getLatestPackage().getFiles().add(f_mets1);
		object.getLatestPackage().getFiles().add(f_lido1);
		
		try{
			action.implementation();
			fail();
		}catch(UserException e){
			System.out.println(e.getMessage());
			assertTrue(e.getMessage().contains(METADATA));
		}
	}
	
	@Test
	public void testRejectEADAndXMP() throws FileNotFoundException, IOException, RepositoryException{
		object.getLatestPackage().getFiles().add(f_ead1);
		object.getLatestPackage().getFiles().add(f_xmp1);
		
		try{
			action.implementation();
			fail();
		}catch(UserException e){
			System.out.println(e.getMessage());
			assertTrue(e.getMessage().contains(METADATA));
		}
	}
	
	@Test
	public void testRejectEADAndLIDO() throws FileNotFoundException, IOException, RepositoryException{
		object.getLatestPackage().getFiles().add(f_ead1);
		object.getLatestPackage().getFiles().add(f_lido1);
		
		try{
			action.implementation();
			fail();
		}catch(UserException e){
			System.out.println(e.getMessage());
			assertTrue(e.getMessage().contains(METADATA));
		}
	}
	
	@Test
	public void testRejectMETSAndLIDO() throws FileNotFoundException, IOException, RepositoryException{
		object.getLatestPackage().getFiles().add(f_mets1);
		object.getLatestPackage().getFiles().add(f_lido1);
		
		try{
			action.implementation();
			fail();
		}catch(UserException e){
			System.out.println(e.getMessage());
			assertTrue(e.getMessage().contains(METADATA));
		}
	}
	
	@Test
	public void testRejectEADwithMETSAndLIDO() throws FileNotFoundException, IOException, RepositoryException{
		object.getLatestPackage().getFiles().add(f_ead1);
		object.getLatestPackage().getFiles().add(f_mets1);
		object.getLatestPackage().getFiles().add(f_lido1);
		
		try{
			action.implementation();
			fail();
		}catch(UserException e){
			System.out.println(e.getMessage());
			assertTrue(e.getMessage().contains(METADATA));
		}
	}

	@Test
	public void testRejectEADwithMETSAndXMP() throws FileNotFoundException, IOException, RepositoryException{
		object.getLatestPackage().getFiles().add(f_ead1);
		object.getLatestPackage().getFiles().add(f_mets1);
		object.getLatestPackage().getFiles().add(f_xmp1);
		
		try{
			action.implementation();
			fail();
		}catch(UserException e){
			System.out.println(e.getMessage());
			assertTrue(e.getMessage().contains(METADATA));
		}
	}
	
	
	// TODO cannot test this before getNewestFilesFromAllRepresentations is not separated from looking onto the actual file system.
	@Test
	public void testConsiderPreviousPackagesWhenDetectingMetadataFiles() throws FileNotFoundException, IOException, RepositoryException{
		
//		object.getLatestPackage().getFiles().add(f_ead1);
//		Package pkg2 = new Package();
//		pkg2.setName("2");
//		pkg2.getFiles().add(f_ead2);
//		object.getPackages().add(pkg2);
//		
//		try{
//			action.implementation();
//			fail();
//		}catch(UserException e){
//			System.out.println(e.getMessage());
//			assertTrue(e.getMessage().contains(C.EAD));
//		}
	}
	
	
	
	
	
}
