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

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Calendar;
import java.util.Date;

import org.hibernate.Session;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.uzk.hki.da.core.C;
import de.uzk.hki.da.model.Copy;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.model.ObjectNamedQueryDAO;
import de.uzk.hki.da.service.HibernateUtil;
import de.uzk.hki.da.util.Path;


/**
 * Relates to AK-T Audit 
 * @author Jens Peters
 * @author Daniel M. de Oliveira
 */
public class _ATIntegrityCheck extends AcceptanceTest{
	

	private static final Path archiveStoragePath = Path.make("/ci/archiveStorage/aip/TEST/");
	private Object object = null;
	
	@Before
	public void setUp() throws IOException{
		

	
	}
	
	@After
	public void cleanUp() {
		//TESTHelper.clearDB();
	}
	
	

	
	@Test
	public void localCopyModifiedTest() throws Exception {
	    String ORIGINAL_NAME = "ATIntegrityCheckLocalCopyModified";
	    
		ath.putPackageToIngestArea(ORIGINAL_NAME, "tgz", ORIGINAL_NAME);
		ath.awaitObjectState(ORIGINAL_NAME,Object.ObjectStatus.ArchivedAndValid);
		object=ath.fetchObjectFromDB(ORIGINAL_NAME);
		
	    changeLastCheckedObjectDate(-25);
		
		
		object = new ObjectNamedQueryDAO().getUniqueObject(ORIGINAL_NAME, "TEST");

		setChecksumSecondaryCopy(object.getLatestPackage().getChecksum(),-1);

		// We'll destroy it physically now, if we 're on CI
		// on dev machines FakeGridFacade will find special file in ATUseCaseAudit
		// On other systems (DEV) the fake adapter will do that for us!
		if (System.getProperty("env") != null && System.getProperty("env").equals("ci")) {
			destroyFileInCIEnvironment(object.getIdentifier());
		} else System.out.println(".. not detected CI Environment!");
		
		changeLastCheckedObjectDate(-25);
		assertTrue(waitForObjectInStatus(ORIGINAL_NAME,51));
	}
	
	@Test
	public void remoteCopyDestroyed() throws IOException, InterruptedException {
		String ORIGINAL_NAME = "ATIntegrityRemoteCopyDestroyed";
		
		ath.putPackageToIngestArea(ORIGINAL_NAME, "tgz", ORIGINAL_NAME);
		ath.awaitObjectState(ORIGINAL_NAME,Object.ObjectStatus.ArchivedAndValid);
		object=ath.fetchObjectFromDB(ORIGINAL_NAME);

		changeLastCheckedObjectDate(-25);
		
		object = new ObjectNamedQueryDAO().getUniqueObject(ORIGINAL_NAME, "TEST");
		setChecksumSecondaryCopy("abcedde5",-25);
		assertTrue(waitForObjectInStatus(ORIGINAL_NAME,51));
	}
	
	@Test
	public void allCopiesOKTest() throws Exception {
		String ORIGINAL_NAME = "ATIntegrityCheckAllCopiesOK";
		
		ath.putPackageToIngestArea(ORIGINAL_NAME, "tgz", ORIGINAL_NAME);
		ath.awaitObjectState(ORIGINAL_NAME,Object.ObjectStatus.ArchivedAndValid);
		object=ath.fetchObjectFromDB(ORIGINAL_NAME);
		
		changeLastCheckedObjectDate(-25);
		setChecksumSecondaryCopy(object.getLatestPackage().getChecksum(),-25);
		
		object = new ObjectNamedQueryDAO().getUniqueObject(ORIGINAL_NAME, "TEST");
		assertSame(object.getObject_state(),100);
		
		
		Date old = object.getLast_checked();
		System.out.println("last check was : " + old);
		Date neu = old;
		while (neu.compareTo(old)<=0) {
			object = new ObjectNamedQueryDAO().getUniqueObject(ORIGINAL_NAME, "TEST");
			neu = object.getLast_checked();	
		}
		System.out.println("new check was on : " + neu);
		
		assertSame(object.getObject_state(),100);
		
	}
	
	
	@Test 
	public void allCopiesDestroyed() throws IOException, InterruptedException {
		String ORIGINAL_NAME = "ATIntegrityCheckAllCopiesDestroyed";
		
		ath.putPackageToIngestArea(ORIGINAL_NAME, "tgz", ORIGINAL_NAME);
		ath.awaitObjectState(ORIGINAL_NAME,Object.ObjectStatus.ArchivedAndValid);
		object=ath.fetchObjectFromDB(ORIGINAL_NAME);

		changeLastCheckedObjectDate(-25);
		
		
		object = new ObjectNamedQueryDAO().getUniqueObject(ORIGINAL_NAME, "TEST");
		// We'll destroy it physically now, if we 're on CI
		// on dev machines FakeGridFacade will find special file in ATUseCaseAudit
		// On other systems (DEV) the fake adapter will do that for us!
		if (System.getProperty("env") != null && System.getProperty("env").equals("ci")) {
			destroyFileInCIEnvironment(object.getIdentifier());
		} else System.out.println(".. not detected CI Environment!");
		
		setChecksumSecondaryCopy("abcd77",-25);
		changeLastCheckedObjectDate(-25);
		assertTrue(waitForObjectInStatus(ORIGINAL_NAME,51));
	}
	
	@Test 
	public void secondaryCopiesTooOld() throws IOException, InterruptedException {
		String ORIGINAL_NAME = "ATIntegritySecondaryCopiesCheckTooOld";
		
		ath.putPackageToIngestArea(ORIGINAL_NAME, "tgz", ORIGINAL_NAME);
		ath.awaitObjectState(ORIGINAL_NAME,Object.ObjectStatus.ArchivedAndValid);
		object=ath.fetchObjectFromDB(ORIGINAL_NAME);

		setChecksumSecondaryCopy(object.getLatestPackage().getChecksum(),-8761);
		assertSame(100,object.getObject_state());
		object = new ObjectNamedQueryDAO().getUniqueObject(ORIGINAL_NAME, "TEST");;
		changeLastCheckedObjectDate(-25);
		assertTrue(waitForObjectInStatus(ORIGINAL_NAME,51));
	}
	@Test 
	public void primaryCopyTooOld() throws IOException, InterruptedException {
		String ORIGINAL_NAME = "ATIntegrityCheckPrimaryCopyTooOld";
		
		ath.putPackageToIngestArea(ORIGINAL_NAME, "tgz", ORIGINAL_NAME);
		ath.awaitObjectState(ORIGINAL_NAME,Object.ObjectStatus.ArchivedAndValid);
		object=ath.fetchObjectFromDB(ORIGINAL_NAME);

		assertSame(100,object.getObject_state());
		object = new ObjectNamedQueryDAO().getUniqueObject(ORIGINAL_NAME, "TEST");;
		changeLastCheckedObjectDate(-8761);
		assertTrue(waitForObjectInStatus(ORIGINAL_NAME,51));
	}
	
	
	
	//----------------------------------------------------
	
	private void setChecksumSecondaryCopy(String checksum,int minusHoursInPast) {
		Session session = HibernateUtil.openSession();
		session.beginTransaction();
		// replace proxies by real objects
		session.refresh(object);
		for (Copy rec : object.getLatestPackage().getCopies()) {}
		
		assertTrue(object.getLatestPackage().getCopies().size()>0);
		
		// Simulate checksumming done by foreign nodes
		Copy copy = object.getLatestPackage().getCopies().iterator().next();
		
		copy.setChecksum(checksum);
		
		// set object to older creationdate than one day
		Calendar now = Calendar.getInstance();
		now.add(Calendar.HOUR_OF_DAY, minusHoursInPast);
		copy.setChecksumDate(now.getTime());
		
		
		session.update(copy);
		session.getTransaction().commit();
		session.close();
		}
	
	
	private void destroyFileInCIEnvironment(String identifier) {

		System.out.println("Trying to destroy file on the archive storage path now!");
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
		  fail("writing to file " + file + " failed");
		} finally {
		   try {writer.close();} catch (Exception ex) { fail();}
		}
		
	}
	
	
	private Date changeLastCheckedObjectDate(int minusHoursInPast) throws IOException{
		
		Session session = HibernateUtil.openSession();
		session.beginTransaction();
		Calendar now = Calendar.getInstance();
		now.add(Calendar.HOUR_OF_DAY, minusHoursInPast);
		object.setLast_checked(now.getTime());
		session.update(object);
		session.getTransaction().commit();
		session.close();
		return now.getTime();
	}
	

	private boolean waitForObjectInStatus( String orgName , int status) throws InterruptedException {	
		while (true){
			Thread.sleep(6000);
			Object object = new ObjectNamedQueryDAO().getUniqueObject(orgName, C.TEST_USER_SHORT_NAME);
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
	
	
