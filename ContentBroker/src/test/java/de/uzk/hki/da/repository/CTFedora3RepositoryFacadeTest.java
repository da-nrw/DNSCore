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

package de.uzk.hki.da.repository;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;

import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.uzk.hki.da.core.C;
import de.uzk.hki.da.repository.Fedora3RepositoryFacade;
import de.uzk.hki.da.repository.RepositoryException;
import de.uzk.hki.da.utils.PasswordUtils;

/**
 * @author Daniel M. de Oliveira
 */
public class CTFedora3RepositoryFacadeTest {

	private static final String TEST = "TEST";
	private static final String OBJECTS_URL = "http://www.danrw.de/objects/";
	private static final String OBJECT_ID = "1-120";
	private static final String COLL_NAME = "collection-open";
	private Fedora3RepositoryFacade fedora;

	
	@Before
	public void setUp(){
		try {
			fedora = new Fedora3RepositoryFacade("http://localhost:8080/fedora", "fedoraAdmin", PasswordUtils.decryptPassword("BYi/MFjKDFd5Dpe52PSUoA=="), null);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
	
	@After
	public void tearDown() throws RepositoryException{
		fedora.purgeObjectIfExists(OBJECT_ID, COLL_NAME);
	}
	
	
	@Test
	public void test() throws RepositoryException, IOException{
		
		String content=null;
		FileInputStream fileInputStream = new FileInputStream(new File("src/test/resources/ct/Fedora3RepositoryFacadeTest/ead.xml"));
		content = IOUtils.toString(fileInputStream, C.ENCODING_UTF_8);
		fileInputStream.close();
		
		fedora.createObject(OBJECT_ID, COLL_NAME, TEST);
		
		fedora.createMetadataFile(OBJECT_ID, COLL_NAME, "ead123.xml", content, "label", "text/xml");
	
	}

	
	@Test
	public void testAddRelationship() throws RepositoryException{
		
		fedora.createObject(OBJECT_ID, COLL_NAME, TEST);
		fedora.addRelationship(OBJECT_ID, COLL_NAME, C.OWL_SAMEAS, OBJECTS_URL+OBJECT_ID);
		
	}

	
	// to recreate an error which happened during refactoring of atusecaseingestdelta
	@Test
	public void testAddRelationshipWithMalformedURL(){
		
		try{
			fedora.createObject(OBJECT_ID, COLL_NAME, TEST);
			fedora.addRelationship(OBJECT_ID, COLL_NAME, C.OWL_SAMEAS,null); // it seems that null is a problem
			fail();
		}catch(RepositoryException e){}
	}
}
