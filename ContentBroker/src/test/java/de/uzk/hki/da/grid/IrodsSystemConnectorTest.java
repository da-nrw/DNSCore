/*
  DA-NRW Software Suite | ContentBroker
  Copyright (C) 2013 Historisch-Kulturwissenschaftliche Informationsverarbeitung
  Universität zu Köln

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
  @Author Jens Peters
*/

package de.uzk.hki.da.grid;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.irods.jargon.core.pub.DataObjectAO;
import org.irods.jargon.core.pub.domain.DataObject;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;


/**
 * The Class IrodsSystemConnectorTest.
 */
public class IrodsSystemConnectorTest {
	
	/** The irods system connector. */
	public static IrodsSystemConnector irodsSystemConnector;

	/** The localfilename. */
	public static String localfilename = "testiRODS.txt";
	
	/** The localpath. */
	public static String localpath = "/tmp/";
	
	/** The file. */
	public static File file;
	
	/** The localfilenameget. */
	public static String localfilenameget = "testiRODS.txt.GET";
	
	/** The test coll. */
	public static String testColl = "testColl";

	/**
	 * Sets the up before class.
	 *
	 * @throws Exception the exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		// Pam 9L7pa7U6IItXHYjeEpAmgg==
		// normal WpXlLLg3a4/S/iYrs6UhtQ==
		irodsSystemConnector = new IrodsSystemConnector("rods", "WpXlLLg3a4/S/iYrs6UhtQ==", "da-nrw-vm3.hki.uni-koeln.de", "da-nrw", "01-da-nrw-vm3.hki.uni-koeln.de");
		irodsSystemConnector.connect();
		file = new File(localpath + localfilename);
		 FileWriter writer = new FileWriter(file ,false);
	     writer.write("Hallo Wie gehts?");
	     writer.close();
	    
	   irodsSystemConnector.createCollection("/da-nrw/home/rods/" + testColl);
	   irodsSystemConnector.put(file, "/da-nrw/home/rods/"+ testColl);
	}

	/**
	 * Tear down after class.
	 *
	 * @throws Exception the exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		try {
		irodsSystemConnector.removeCollection("/da-nrw/home/rods/" + testColl);
		 file = new File(localpath + localfilename);
		 file.delete();
		} catch (Exception e ) {
		irodsSystemConnector.logoff();
		}
	}

	/**
	 * Sets the up.
	 *
	 * @throws Exception the exception
	 */
	@Before
	public void setUp() throws Exception {
		
	}

	/**
	 * Tear down.
	 *
	 * @throws Exception the exception
	 */
	@After
	public void tearDown() throws Exception {
		
	}
	
	/**
	 * Gets the dao.
	 *
	 * @return the dao
	 */
	@Test
	public void getDao() {
		DataObjectAO dao = irodsSystemConnector.getDataObjectAO();
		assertNotNull(dao);
	}
	
	/**
	 * Gets the resc loc for resc.
	 *
	 * @return the resc loc for resc
	 */
	@Test
	public void getRescLocForResc() {
		assertEquals("da-nrw-vm3.hki.uni-koeln.de", irodsSystemConnector.getRescLocForRescName("01-da-nrw-vm3.hki.uni-koeln.de"));
		
	}
	
	/**
	 * Adds the avu metadata data object.
	 */
	@Test
	public void addAVUMetadataDataObject() {
		try {
		irodsSystemConnector.removeFile("/da-nrw/home/rods/"+testColl +"/"+ localfilename);
		irodsSystemConnector.put(file, "/da-nrw/home/rods/"+testColl);
		irodsSystemConnector.addAVUMetadataDataObject("/da-nrw/home/rods/"+testColl +"/"+ localfilename , "TestName", "1223");
		String avu =irodsSystemConnector.getAVUMetadataDataObjectValue("/da-nrw/home/rods/"+testColl +"/"+ localfilename, "TestName");
		if (!avu.equals("1223")) fail();
		} catch (Exception e ) {
			e.printStackTrace();
			fail();
		}
		}
	
	/**
	 * Added avu metadata twice must complain.
	 */
	@Test
	public void addedAVUMetadataTwiceMustComplain() {
		try {
		irodsSystemConnector.removeFile("/da-nrw/home/rods/"+testColl +"/"+ localfilename);
		irodsSystemConnector.put(file, "/da-nrw/home/rods/"+testColl);
		irodsSystemConnector.addAVUMetadataDataObject("/da-nrw/home/rods/"+testColl +"/"+ localfilename , "TestName", "1223");
		irodsSystemConnector.addAVUMetadataDataObject("/da-nrw/home/rods/"+testColl +"/"+ localfilename , "TestName", "1223");
		
		} catch (Exception e ) {
			
			return;
		}
		fail();
		}
	

	/**
	 * Modify avu metadata data object.
	 */
	@Test
	public void modifyAVUMetadataDataObject() {
		try {
		irodsSystemConnector.removeFile("/da-nrw/home/rods/"+testColl +"/"+ localfilename);
		irodsSystemConnector.put(file, "/da-nrw/home/rods/"+testColl);
		irodsSystemConnector.saveOrUpdateAVUMetadataDataObject("/da-nrw/home/rods/"+testColl +"/"+ localfilename , "TestName", "1223");
		String avu =irodsSystemConnector.getAVUMetadataDataObjectValue("/da-nrw/home/rods/"+testColl +"/"+ localfilename, "TestName");
		if (!avu.equals("1223")) fail();
		
		irodsSystemConnector.saveOrUpdateAVUMetadataDataObject("/da-nrw/home/rods/"+testColl +"/"+ localfilename , "TestName", "1223556");
		
	 avu =irodsSystemConnector.getAVUMetadataDataObjectValue("/da-nrw/home/rods/"+testColl +"/"+ localfilename, "TestName");
		if (!avu.equals("1223556")) fail();
		
		
		
		} catch (Exception e ) {
			e.printStackTrace();
			fail();
		}
		}
	
	/**
	 * Irods i get.
	 */
	@Test
	public void irodsIGet() {
		
		File getFile = new File(localpath + localfilenameget);
		if (getFile.exists()) getFile.delete();
	  irodsSystemConnector.get("/da-nrw/home/rods/" + testColl +"/"+localfilename, new File(localpath + localfilenameget));
	  assertTrue(getFile.exists());
	  
	}
	
	/**
	 * Irods rename file.
	 */
	@Test
	public void irodsRenameFile() {
		try {
		irodsSystemConnector.put(file, "/da-nrw/home/rods/"+ testColl +"/test2.txt");
		irodsSystemConnector.renameDataObject("/da-nrw/home/rods/"+ testColl + "/test2.txt", "/da-nrw/home/rods/"+testColl+"/testRename.txt");
		
		} catch (Exception e ) {
	
			e.printStackTrace();
			fail();
		}
	}

	/**
	 * Irods rename collection.
	 */
	@Test
	public void irodsRenameCollection() {
		try {

		assertTrue(irodsSystemConnector.isConnected());
		irodsSystemConnector.createCollection("/da-nrw/home/rods/" + testColl + "/testColl1");
		irodsSystemConnector.renameCollection("/da-nrw/home/rods/" + testColl+"/testColl1", "/da-nrw/home/rods/"+testColl +"/testColl2");
		}catch (Exception e ) {	
			e.printStackTrace();
			fail();
		}
	}
	
	/**
	 * Checks if is connected.
	 */
	@Test
	public void isConnected() {
	
		//assertTrue(irodsSystemConnector.isConnected());
		//irodsSystemConnector.logoff();
		//assertFalse(irodsSystemConnector.isConnected());
		//irodsSystemConnector.connect();
		//assertTrue(irodsSystemConnector.isConnected());		
	}
	
	/**
	 * Generate checksum.
	 */
	@Test
	public void generateChecksum() {
		irodsSystemConnector.put(file, "/da-nrw/home/rods/"+ testColl +"/test3.txt");
		String cs = irodsSystemConnector.computeChecksum("/da-nrw/home/rods/"+ testColl +"/test3.txt");
		
		assertTrue("Length of checksum is 0", cs.length()>0);
}
	
	/**
	 * Builds the tar.
	 */
	@SuppressWarnings("deprecation")
	@Test
	public void buildTar() {
		irodsSystemConnector.createCollection("/da-nrw/home/rods/" + testColl + "/testColl2");
		irodsSystemConnector.put(file, "/da-nrw/home/rods/"+ testColl +"/testColl2/test3.txt");
		irodsSystemConnector.buildTar("/da-nrw/home/rods/"+ testColl +"/test.tar", "/da-nrw/home/rods/" + testColl + "/testColl2", "01-da-nrw-vm3.hki.uni-koeln.de");
		assertTrue(irodsSystemConnector.fileExists("/da-nrw/home/rods/"+ testColl +"/test.tar"));
	}
	
	/**
	 * Gets the repls for file.
	 *
	 * @return the repls for file
	 */
	@Test
	public void getReplsForFile() {
		List<DataObject> dao = irodsSystemConnector.getReplicationsForFile("/da-nrw/home/rods/"+ testColl, file.getName());
		assertTrue(dao.size()==1);
		
		
	}
	
	@Test
	public void executeRuleIfNotActivated() throws IOException {
	/*	if (!irodsSystemConnector.isRuleActivated("performFederation")) System.out.println("nein");
		Map<String, String> map = new HashMap<String, String>();
		map.put("*contractor", "\"TEST\"");

		irodsSystemConnector.executeRuleFromFile(new File("src/test/resources/grid/test.r"),map );
	*/
	}
	
	
}
