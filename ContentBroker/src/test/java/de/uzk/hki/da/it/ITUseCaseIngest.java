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
*/

package de.uzk.hki.da.it;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.hibernate.classic.Session;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import de.uzk.hki.da.db.HibernateUtil;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.utils.MD5Checksum;



/**
 * Relates to AK-T/02 Ingest.
 * @author Jens Peters
 */
public class ITUseCaseIngest extends ITIngestBase{
    
	/**
	 * Sets the up before class.
	 *
	 * @throws Exception the exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		HibernateUtil.init(hibernateConfigFilePath);
		context = new FileSystemXmlApplicationContext("conf/beans.xml");
		setUpNode();
		
	}
	
	/**
	 * Sets the up.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@Before
	public void setUp() throws IOException{
		setupSysConnector();
		prepareSIPAndInsertJobAndObject("integrationTest");
	}

	
	
	
	
	/**
	 * Tear down.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@After
	public void tearDown() throws IOException{
		
		irodsSystemConnector.removeCollectionAndEatException("/da-nrw/aip/TEST/" + object.getIdentifier());
		irodsSystemConnector.logoff();
		
		FileUtils.deleteDirectory(new File("/tmp/integrationTest"));
		new File("/data/danrw/ingest/TEST/integrationTest.tgz").delete();
		FileUtils.deleteDirectory(new File("/data/danrw/storage/fs/fork/TEST/it+ingestBase+identifier/"));

		Session session = HibernateUtil.openSession();
		session.beginTransaction();
		session.delete(object);
		session.getTransaction().commit();
		session.close();
	}
	
	
	
	
	/**
	 * Test ingest.
	 * @throws Exception the exception
	 */
	@Test
	public void testIngest() throws Exception {
		
		stepThroughIngestWorkflow();
		String packageRelativePath = "TEST/"+object.getIdentifier()+"/"+object.getIdentifier()+".pack_1.tar";
		
		// checking properties of grid
		checkingGridProperties(packageRelativePath);
		
		if (!new File(aipResourceVaultPath+"aip/"+packageRelativePath)
		.exists()) fail("AIP did not show up in the AIP folder on faked long term resource");
		if (!MD5Checksum.getMD5checksumForLocalFile(
					new File(aipResourceVaultPath+"aip/"+packageRelativePath))
					.equals(irodsSystemConnector.getAVUMetadataDataObjectValue("/da-nrw/aip/"+packageRelativePath, "chksum"))) fail("Checksum ICAT value vs. Local File are not equal!");
		
		Object obj = getUniqueObjectForObjectIdentifier(object.getIdentifier());
		
		assertThatWorkingAreaHasBeenLeftInACleanState(obj, packageRelativePath);
		
		// check files
		assertThatGridAndDatabaseChecksumAreSet(obj,packageRelativePath);
	}
	
	
	
	
	
	

	/**
	 * Checking grid properties.
	 * @param packageRelativePath the package relative path
	 */
	private void checkingGridProperties(String packageRelativePath) {
		
		if (!irodsSystemConnector.getAVUMetadataDataObjectValue("/da-nrw/aip/"+packageRelativePath, "replicated").equals("1")) fail("Metadata Value replicated not recorded!");
		if (irodsSystemConnector.getAVUMetadataDataObjectValue("/da-nrw/aip/"+packageRelativePath, "chksum").equals("")) fail("Metadata Value checksum not recorded!");
		if (irodsSystemConnector.getChecksum("/da-nrw/aip/"+packageRelativePath).equals("")) fail("Dao Value checksum not recorded!");
	}

	
	/**
	 * Assert that working area has been left in a clean state.
	 * @param obj the obj
	 * @param packageRelativePath the package relative path
	 */
	private void assertThatWorkingAreaHasBeenLeftInACleanState(Object obj, String packageRelativePath){
		
		assertFalse(fileExistsLogicallyOrPhysically("fork/TEST/"+obj.getOrig_name()+".zip"));
		assertFalse(fileExistsLogicallyOrPhysically("fork/TEST/"+packageRelativePath));
		assertFalse(collectionExistsLogicallyOrPhysically("fork/TEST/"+obj.getIdentifier()));
		assertFalse(new File(cacheResourceVaultPath+"aip/TEST/"+packageRelativePath)
			.exists()); // AIP Package still in Cache Folder - cleanup error, Do we have configured less than 3 copies on the grid?
	}
	
	
	
	/**
	 * @param packageRelativePath
	 * @author Jens Peters
	 */
	private void assertThatGridAndDatabaseChecksumAreSet(Object o,String packageRelativePath) 
	{
		
		assertThat(o.getLatestPackage().getChecksum()).isNotEmpty();
		assertThat(o.getLatestPackage().getChecksum()).isEqualTo(irodsSystemConnector.getChecksum("/da-nrw/aip/"+packageRelativePath));
		
	}
}
