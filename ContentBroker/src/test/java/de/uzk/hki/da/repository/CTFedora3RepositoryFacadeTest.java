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

import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;

import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static de.uzk.hki.da.core.C.*;
import static de.uzk.hki.da.test.TC.*;
import de.uzk.hki.da.util.Path;
import de.uzk.hki.da.utils.PasswordUtils;

/**
 * @author Daniel M. de Oliveira
 */
public class CTFedora3RepositoryFacadeTest {

	private static final String TEST = "TEST";
	private static final Path TEST_DIR = Path.make(TEST_ROOT_REPOSITORY,"Fedora3RepositoryFacade");
	private static final String OBJECTS_URL = "http://www.danrw.de/objects/";
	private static final String COLL_NAME = "collection-open";
	private Fedora3RepositoryFacade fedora;
	private static final File abc = new File("/tmp/adc.txt");
	private static final File abd = new File("/tmp/abd.txt");

	
	@Before
	public void setUp() throws IOException{
		try {
			
			fedora = new Fedora3RepositoryFacade(
					"http://localhost:8080/fedora", 
					"fedoraAdmin", 
					PasswordUtils.decryptPassword("BYi/MFjKDFd5Dpe52PSUoA=="), null);
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		
		 if (!abc.exists()) abc.createNewFile();
		 if (!abd.exists()) abd.createNewFile();
	}
	
	@After
	public void tearDown() throws RepositoryException{
		fedora.purgeObjectIfExists(IDENTIFIER, COLL_NAME);
		abc.delete();
		abd.delete();
	}
	
	
	@Test
	public void test() throws RepositoryException, IOException{
		
		String content=null;
		FileInputStream fileInputStream = new FileInputStream(Path.makeFile(TEST_DIR,"ead.xml"));
		content = IOUtils.toString(fileInputStream, ENCODING_UTF_8);
		fileInputStream.close();
		
		fedora.createObject(IDENTIFIER, COLL_NAME, TEST);
	}

	
	@Test
	public void testAddRelationship() throws RepositoryException{
		
		fedora.createObject(IDENTIFIER, COLL_NAME, TEST);
		fedora.addRelationship(IDENTIFIER, COLL_NAME, OWL_SAMEAS, OBJECTS_URL+IDENTIFIER);
		
	}

	
	// to recreate an error which happened during refactoring of atusecaseingestdelta
	@Test
	public void testAddRelationshipWithMalformedURL(){
		
		try{
			fedora.createObject(IDENTIFIER, COLL_NAME, TEST);
			fedora.addRelationship(IDENTIFIER, COLL_NAME, OWL_SAMEAS,null); // it seems that null is a problem
			fail();
		}catch(RepositoryException e){}
	}
	
	
	// to show that duplicate ingest in createEDMAction is possible
	@Test
	public void overwritingFileDatastreamPossible() throws IOException {
		
		try{
			fedora.createObject(IDENTIFIER, COLL_NAME, TEST);
			fedora.ingestFile(IDENTIFIER, COLL_NAME, "abc", abc, "a label", "text/xml");
			fedora.ingestFile(IDENTIFIER, COLL_NAME, "abc", abd, "a label", "text/xml");
		}catch(RepositoryException e){}
	}
	
	
}
