/*
  DA-NRW Software Suite | ContentBroker
  Copyright (C) 2013 Historisch-Kulturwissenschaftliche Informationsverarbeitung
  Universität zu Köln, 2014 LVR InfoKom

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
package de.uzk.hki.da.at;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Calendar;

import org.apache.commons.io.FileUtils;
import org.hibernate.classic.Session;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.uzk.hki.da.core.C;
import de.uzk.hki.da.core.HibernateUtil;
import de.uzk.hki.da.core.Path;
import de.uzk.hki.da.model.Object;


/**
 * Relates to AK-T Audit 
 * @author Jens Peters
 * 
 */
public class ATUseCaseAudit extends AcceptanceTest{
	
	private static final String ORIGINAL_NAME = "ATUseCaseAudit";
	private static final String CONTAINER_NAME = ORIGINAL_NAME+"."+C.FILE_EXTENSION_TGZ;
	private static final String IDENTIFIER =   "ATUseCaseAuditIdentifier";
	private static final Path archiveStoragePath = Path.make("/ci/archiveStorage/aip/TEST/");
	private Object object = null;
	
	@Before
	public void setUp() throws IOException{
		
		// set object to older creationdate than one day
		Calendar now = Calendar.getInstance();
		now.add(Calendar.HOUR_OF_DAY, -25);
		
		object = ath.putPackageToStorage(IDENTIFIER,ORIGINAL_NAME,CONTAINER_NAME,now.getTime(),100 );
	}
	
	@After
	public void tearDown(){ 
		try{
			Path.makeFile(localNode.getIngestAreaRootPath(),C.TEST_USER_SHORT_NAME,CONTAINER_NAME).delete();
			Path.makeFile("tmp",object.getIdentifier()+".pack_1.tar").delete();
			FileUtils.deleteDirectory(Path.makeFile("tmp",object.getIdentifier()+".pack_1"));
		}catch(Exception e){
			System.out.println(e.getMessage());
		}
	}
	
	@Test
	public void testHappyPath() throws Exception {
		
		Session session = HibernateUtil.openSession();
		session.beginTransaction();
		object = ath.getUniqueObject(session, ORIGINAL_NAME, "TEST");
		session.close();
		// We'll destroy it now, if we 're on CI
		// on dev machines FakeGridFacade will find special file in ATUseCaseAudit
		if (System.getProperty("env").equals("ci"))
			destroyFileInCIEnvironment(object.getIdentifier());
		assertTrue(waitForObjectInStatus(51));
	}
		
	private void destroyFileInCIEnvironment(String identifier) {
		
		File file = Path.makeFile(archiveStoragePath,identifier,identifier+".pack_1.tar");
		if (!file.exists()) {	
			fail(file  + " does not exist!" );
		}
		Writer writer = null;
		try {
		    writer = new BufferedWriter(new OutputStreamWriter(
		          new FileOutputStream(file), "utf-8"));
		    writer.write("Something");
		} catch (IOException ex) {
		  fail("writing to file");
		} finally {
		   try {writer.close();} catch (Exception ex) { fail();}
		}
		
	}

	private boolean waitForObjectInStatus(int status) throws InterruptedException {	
		while (true){
			Thread.sleep(6000);
			Session session = HibernateUtil.openSession();
			session.beginTransaction();
			Object object = ath.getUniqueObject(session, ORIGINAL_NAME, C.TEST_USER_SHORT_NAME);
			session.close();		
			if (object!=null){
				
				System.out.println("waiting for Object to be in state " + status + ". Is: "+object.getObject_state() + " last checked " + object.getLast_checked() );
				if (object.getObject_state()==status){
					System.out.println("reached state : " + status );
					return true;
			}
		} else return false;
			
				
		}	
		}
}
	
	
