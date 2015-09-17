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
import org.junit.Test;

import de.uzk.hki.da.model.Copy;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.model.ObjectNamedQueryDAO;
import de.uzk.hki.da.service.HibernateUtil;
import de.uzk.hki.da.utils.Path;


/**
 * Relates to AK-T Audit 
 * @author Jens Peters
 * @author Daniel M. de Oliveira
 */
public class ATIntegrityCheck extends AcceptanceTest{
	

	private static final Path archiveStoragePath = Path.make("/ci/archiveStorage/aip/TEST/");
	
	@Test
	public void localCopyModifiedTest()  {
		try {
			
			Object object = null;
		
	    String ORIGINAL_NAME = "ATIntegrityCheckLocalCopyModified";
	    
		ath.putSIPtoIngestArea(ORIGINAL_NAME, "tgz", ORIGINAL_NAME);
		ath.awaitObjectState(ORIGINAL_NAME,Object.ObjectStatus.ArchivedAndValidAndNotInWorkflow);
		object=ath.getObject(ORIGINAL_NAME);
		
	    changeLastCheckedObjectDate(object, -25);
		
		
		object = new ObjectNamedQueryDAO().getUniqueObject(ORIGINAL_NAME, "TEST");

		setChecksumSecondaryCopy(object, object.getLatestPackage().getChecksum(),-1);
		Thread.sleep(2000L);
		// We'll destroy it physically now, if we 're on CI
		// on dev machines FakeGridFacade will find special file in ATUseCaseAudit
		// On other systems (DEV) the fake adapter will do that for us!
		if (System.getProperty("env") != null && System.getProperty("env").equals("ci")) {
			destroyFileInCIEnvironment(object.getIdentifier());
		} else System.out.println(".. not detected CI Environment!");
		
		changeLastCheckedObjectDate(object, -25);
		Thread.sleep(2000L);
		
		ath.awaitObjectState(ORIGINAL_NAME,Object.ObjectStatus.Error);
		object = new ObjectNamedQueryDAO().getUniqueObject(ORIGINAL_NAME, "TEST");
		assertSame(object.getObject_state(), Object.ObjectStatus.Error);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void remoteCopyDestroyed()  {
try {
		String ORIGINAL_NAME = "ATIntegrityRemoteCopyDestroyed";
		Object object = null;
		ath.putSIPtoIngestArea(ORIGINAL_NAME, "tgz", ORIGINAL_NAME);
		ath.awaitObjectState(ORIGINAL_NAME,Object.ObjectStatus.ArchivedAndValidAndNotInWorkflow);
		object=ath.getObject(ORIGINAL_NAME);

		changeLastCheckedObjectDate(object, -25);
		Thread.sleep(2000L);
		object = new ObjectNamedQueryDAO().getUniqueObject(ORIGINAL_NAME, "TEST");
		setChecksumSecondaryCopy(object, "abcedde5",-31);
		//assertTrue(waitForObjectInStatus(ORIGINAL_NAME,Object.ObjectStatus.Error));
		ath.awaitObjectState(ORIGINAL_NAME,Object.ObjectStatus.Error);
		object = new ObjectNamedQueryDAO().getUniqueObject(ORIGINAL_NAME, "TEST");
		assertSame(Integer.valueOf(object.getObject_state()), Integer.valueOf(Object.ObjectStatus.Error));
} catch (Exception e) {
	e.printStackTrace();
}
}
	
	@Test
	public void allCopiesOKTest()  {
		try {
		String ORIGINAL_NAME = "ATIntegrityCheckAllCopiesOK";
		Object object = null;
		ath.putSIPtoIngestArea(ORIGINAL_NAME, "tgz", ORIGINAL_NAME);
		ath.awaitObjectState(ORIGINAL_NAME,Object.ObjectStatus.ArchivedAndValidAndNotInWorkflow);
		object=ath.getObject(ORIGINAL_NAME);
		
		changeLastCheckedObjectDate(object, -25);
		setChecksumSecondaryCopy(object, object.getLatestPackage().getChecksum(),-31);
		Thread.sleep(2000L);
		assertSame(Integer.valueOf(object.getObject_state()),Integer.valueOf(Object.ObjectStatus.ArchivedAndValidAndNotInWorkflow));
		
		waitForObjectChecked(object, ORIGINAL_NAME);
		object = new ObjectNamedQueryDAO().getUniqueObject(ORIGINAL_NAME, "TEST");
		assertSame(Integer.valueOf(object.getObject_state()),Integer.valueOf(Object.ObjectStatus.ArchivedAndValidAndNotInWorkflow));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test 
	public void allCopiesDestroyed() {
		try {
		String ORIGINAL_NAME = "ATIntegrityCheckAllCopiesDestroyed";
		Object object = null;
		ath.putSIPtoIngestArea(ORIGINAL_NAME, "tgz", ORIGINAL_NAME);
		ath.awaitObjectState(ORIGINAL_NAME,Object.ObjectStatus.ArchivedAndValidAndNotInWorkflow);
		object=ath.getObject(ORIGINAL_NAME);

		changeLastCheckedObjectDate(object, -25);
		Thread.sleep(2000L);
		
		object = new ObjectNamedQueryDAO().getUniqueObject(ORIGINAL_NAME, "TEST");
		// We'll destroy it physically now, if we 're on CI
		// on dev machines FakeGridFacade will find special file in ATUseCaseAudit
		// On other systems (DEV) the fake adapter will do that for us!
		if (System.getProperty("env") != null && System.getProperty("env").equals("ci")) {
			destroyFileInCIEnvironment(object.getIdentifier());
		} else System.out.println(".. not detected CI Environment!");
		
		setChecksumSecondaryCopy(object, "abcd77",-31);
		changeLastCheckedObjectDate(object, -25);
		//assertTrue(waitForObjectInStatus(ORIGINAL_NAME,Object.ObjectStatus.Error));
		ath.awaitObjectState(ORIGINAL_NAME,Object.ObjectStatus.Error);
		object = new ObjectNamedQueryDAO().getUniqueObject(ORIGINAL_NAME, "TEST");
		assertSame(Integer.valueOf(object.getObject_state()), Integer.valueOf(Object.ObjectStatus.Error));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test 
	public void secondaryCopiesTooOld() {
		try {
		
		String ORIGINAL_NAME = "ATIntegritySecondaryCopiesCheckTooOld";
		Object object = null;
		ath.putSIPtoIngestArea(ORIGINAL_NAME, "tgz", ORIGINAL_NAME);
		ath.awaitObjectState(ORIGINAL_NAME,Object.ObjectStatus.ArchivedAndValidAndNotInWorkflow);
		object=ath.getObject(ORIGINAL_NAME);

		setChecksumSecondaryCopy(object, object.getLatestPackage().getChecksum(),-8761);
		
		assertSame(Integer.valueOf(Object.ObjectStatus.ArchivedAndValidAndNotInWorkflow),Integer.valueOf(object.getObject_state()));
		Thread.sleep(2000L);
		object = new ObjectNamedQueryDAO().getUniqueObject(ORIGINAL_NAME, "TEST");;
		changeLastCheckedObjectDate(object, -25);
		
		//assertTrue(waitForObjectInStatus(ORIGINAL_NAME,Object.ObjectStatus.Error));
		ath.awaitObjectState(ORIGINAL_NAME,Object.ObjectStatus.Error);
		object = new ObjectNamedQueryDAO().getUniqueObject(ORIGINAL_NAME, "TEST");
		assertSame(Integer.valueOf(object.getObject_state()), Integer.valueOf(Object.ObjectStatus.Error));
		} catch (Exception e) {
			e.printStackTrace();
		}
		}
	@Test 
	public void primaryCopyTooOld()  {
		try {
			String ORIGINAL_NAME = "ATIntegrityCheckPrimaryCopyTooOld";
		
		Object object = null;
		ath.putSIPtoIngestArea(ORIGINAL_NAME, "tgz", ORIGINAL_NAME);
		ath.awaitObjectState(ORIGINAL_NAME,Object.ObjectStatus.ArchivedAndValidAndNotInWorkflow);
		object=ath.getObject(ORIGINAL_NAME);

		assertSame(Integer.valueOf(Object.ObjectStatus.ArchivedAndValidAndNotInWorkflow),Integer.valueOf(object.getObject_state()));
		object = new ObjectNamedQueryDAO().getUniqueObject(ORIGINAL_NAME, "TEST");;
		changeLastCheckedObjectDate(object, -8761);
		Thread.sleep(2000L);
		//assertTrue(waitForObjectInStatus(ORIGINAL_NAME,Object.ObjectStatus.Error));
		ath.awaitObjectState(ORIGINAL_NAME,Object.ObjectStatus.Error);
		object = new ObjectNamedQueryDAO().getUniqueObject(ORIGINAL_NAME, "TEST");
		assertSame(Integer.valueOf(object.getObject_state()), Integer.valueOf(Object.ObjectStatus.Error));
		} catch (Exception e) {
		e.printStackTrace();
	}
	}
	
	
	private void waitForObjectChecked(Object object,String ORIGINAL_NAME) {
		Date old = object.getLast_checked();
		System.out.println("last check was : " + old);
		Date neu = old;
		while (neu.compareTo(old)<=0) {
			object = new ObjectNamedQueryDAO().getUniqueObject(ORIGINAL_NAME, "TEST");
			neu = object.getLast_checked();	
		}
		System.out.println("new check was on : " + neu + " object state " + object.getObject_state());
	
	}
	
	private void setChecksumSecondaryCopy(Object object, String checksum,int minusDaysInPast) {
		Session session = HibernateUtil.openSession();
		session.beginTransaction();
		// replace proxies by real objects
		session.refresh(object);
		for (Copy rec : object.getLatestPackage().getCopies()) rec.getId();
		
		// Simulate checksumming done by foreign nodes
		Copy copy = object.getLatestPackage().getCopies().iterator().next();
		
		copy.setChecksum(checksum);
		
		// set object to older creationdate than one day
		Calendar now = Calendar.getInstance();
		now.add(Calendar.DAY_OF_YEAR, minusDaysInPast);
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
	
	
	private Date changeLastCheckedObjectDate(Object object, int minusHoursInPast) throws IOException{
		
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
}
	
	
