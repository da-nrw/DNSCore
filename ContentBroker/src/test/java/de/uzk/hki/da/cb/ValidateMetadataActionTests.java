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
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.jdom.JDOMException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xml.sax.SAXException;

import de.uzk.hki.da.core.C;
import de.uzk.hki.da.core.Path;
import de.uzk.hki.da.core.UserException;
import de.uzk.hki.da.ff.FFConstants;
import de.uzk.hki.da.metadata.FakeMetadataStructure;
import de.uzk.hki.da.metadata.MetadataStructureFactory;
import de.uzk.hki.da.model.DAFile;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.model.Package;
import de.uzk.hki.da.repository.RepositoryException;
import de.uzk.hki.da.test.TC;
import de.uzk.hki.da.test.TESTHelper;

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
	private static final Path WORK_AREA_ROOT = Path.make(TC.TEST_ROOT_CB,"ValidateMetadataActionTests","work");
	private static final String XMP1_XML = "xmp1.xmp";
	private static final String LIDO_XML = "lido1.xml";
	private static final String LIDO2_XML = "lido2.xml";
	
	private static MetadataStructureFactory msf;
	
	private Object object;
	
	ValidateMetadataAction action = new ValidateMetadataAction();
	
	DAFile f_ead1 = new DAFile(null,REP_A,VDA03_XML);
	DAFile f_ead2 = new DAFile(null,REP_B,EAD_XML);
	DAFile f_mets1 = new DAFile(null,"",METS_2_99_XML); 
	DAFile f_mets2 = new DAFile(null,"",METS_2_998_XML);
	DAFile f_xmp1 = new DAFile(null,"1+a",XMP1_XML);
	DAFile f_lido1 = new DAFile(null,"",LIDO_XML);
	DAFile f_lido2 = new DAFile(null,"",LIDO2_XML);

	
//	@SuppressWarnings("static-access")
	@SuppressWarnings("unchecked")
	@BeforeClass
	public static void mockDca() throws IOException, JDOMException, ParserConfigurationException, SAXException {
		msf = mock(MetadataStructureFactory.class);
		File file = null;
		List<DAFile> dafiles = null;
		when(msf.create((String)anyObject(),(File)anyObject(), (List<DAFile>)anyObject())).thenReturn(new FakeMetadataStructure(file, dafiles));	
	}
	
	@Before
	public void setUp(){
		
		object = TESTHelper.setUpObject(IDENTIFIER,WORK_AREA_ROOT);
		
		f_ead1.setPackage(object.getLatestPackage());
		f_ead2.setPackage(object.getLatestPackage());
		f_mets1.setPackage(object.getLatestPackage());
		f_mets2.setPackage(object.getLatestPackage());
		f_xmp1.setPackage(object.getLatestPackage());
		f_lido1.setPackage(object.getLatestPackage());
		f_lido2.setPackage(object.getLatestPackage());
		
		action.setMsf(msf);
		action.setObject(object);

		f_ead1.setFormatSecondaryAttribute(FFConstants.SUBFORMAT_IDENTIFIER_EAD);
		f_ead2.setFormatSecondaryAttribute(FFConstants.SUBFORMAT_IDENTIFIER_EAD);
		f_mets1.setFormatSecondaryAttribute(FFConstants.SUBFORMAT_IDENTIFIER_METS);
		f_mets2.setFormatSecondaryAttribute(FFConstants.SUBFORMAT_IDENTIFIER_METS);
		f_xmp1.setFormatSecondaryAttribute(FFConstants.SUBFORMAT_IDENTIFIER_XMP);
		f_lido1.setFormatSecondaryAttribute(FFConstants.SUBFORMAT_IDENTIFIER_LIDO);
		f_lido2.setFormatSecondaryAttribute(FFConstants.SUBFORMAT_IDENTIFIER_LIDO);
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
			assertTrue(e.getMessage().contains(C.CB_PACKAGETYPE_EAD));
		}
	}
	
	@Test
	public void testDetectEAD() throws FileNotFoundException, UserException, IOException, RepositoryException{
		
		object.getLatestPackage().getFiles().add(f_ead1);
		object.getLatestPackage().getFiles().add(f_mets1);
		object.getLatestPackage().getFiles().add(f_mets2);
		
		action.implementation();
		
		assertEquals(C.CB_PACKAGETYPE_EAD,object.getPackage_type());
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
			assertTrue(e.getMessage().contains(C.CB_PACKAGETYPE_METS));
		}
	}
	
	@Test 
	public void testDetectMETS() throws FileNotFoundException, UserException, IOException, RepositoryException{
		
		object.getLatestPackage().getFiles().add(f_mets1);
		
		action.implementation();
		
		assertEquals(C.CB_PACKAGETYPE_METS,object.getPackage_type());
		assertEquals(METS_2_99_XML,object.getMetadata_file());
	}

	@Test
	public void testLido() throws FileNotFoundException, UserException, IOException, RepositoryException{
		
		object.getLatestPackage().getFiles().add(f_lido1);
		
		action.implementation();
		
		assertEquals(C.CB_PACKAGETYPE_LIDO,object.getPackage_type());
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
			assertTrue(e.getMessage().contains(C.CB_PACKAGETYPE_LIDO));
		}
	}
	
	
	@Test
	public void testXMPWithoutRDF() throws FileNotFoundException, UserException, IOException, RepositoryException{
		
		object.getLatestPackage().getFiles().add(f_xmp1);
		
		action.implementation();
		
		assertEquals(C.CB_PACKAGETYPE_XMP,object.getPackage_type());
		assertEquals(C.XMP_METADATA_FILE,object.getMetadata_file());
	}
	

	@Test
	public void testRollback() throws Exception{
		
		object.setMetadata_file(VDA03_XML);
		object.setPackage_type(C.CB_PACKAGETYPE_XMP);
		
		action.rollback();
		
		assertEquals(null, object.getMetadata_file());
		assertEquals(null, object.getPackage_type());
	}
	
	@Test
	public void testDetectedPackageTypeCollidesWithPackageTypeOfObject() throws FileNotFoundException, UserException, IOException, RepositoryException{
		
		object.getLatestPackage().getFiles().add(f_mets1);
		
		object.setMetadata_file(VDA03_XML);
		object.setPackage_type(C.CB_PACKAGETYPE_EAD);
		
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
		object.setPackage_type(C.CB_PACKAGETYPE_EAD);
		
		object.getLatestPackage().getFiles().add(f_mets1);
		
		try{
			action.implementation();
		}catch(RuntimeException e){}
		
		action.rollback();
		
		assertEquals(VDA03_XML,object.getMetadata_file());
		assertEquals(C.CB_PACKAGETYPE_EAD,object.getPackage_type());
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
	
	
	@Test
	public void testRejectDuplicateEADWhichComesWithDelta() throws FileNotFoundException, IOException, RepositoryException{
		
		object.getLatestPackage().getFiles().add(f_ead1);
		Package pkg2 = new Package();
		pkg2.setName("2");
		pkg2.getFiles().add(f_ead2);
		object.getPackages().add(pkg2);
		
		try{
			action.implementation();
			fail();
		}catch(UserException e){
			System.out.println(e.getMessage());
			assertTrue(e.getMessage().contains(C.CB_PACKAGETYPE_EAD));
		}
	}
}
