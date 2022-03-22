package de.uzk.hki.da.at;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.uzk.hki.da.model.Copy;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.model.WorkArea;
import de.uzk.hki.da.service.HibernateUtil;
import de.uzk.hki.da.utils.Path;

public class ATRepair extends AcceptanceTest {
	final int MAX_RETRY=200;
	final static SimpleDateFormat DATE_FORM = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	protected String zoneName = "c-i";
	@Before
	public void setUp() throws IOException {
	}

	@After
	public void tearDown(){
	}

	public void writeOnFile(String fileName, boolean append) {
		try {

			File file = Path.makeFile(fileName);
			RandomAccessFile ranAcc = null;
			try {
				ranAcc = new RandomAccessFile(file, "rw");
				if (append) {
					ranAcc.seek(file.length());
				} else {
					ranAcc.seek(222);
				}
				System.out.println("Write to: " + fileName + " append: " + append);
				ranAcc.writeBytes("Kaputnik");
			} catch (Exception ex) {
				fail("writing to file " + file + " failed" + "\n" + ex.getMessage() + "\n" + ex.toString());
			} finally {
				try {
					if (ranAcc != null)
						ranAcc.close();
				} catch (Exception ex) {
					fail(ex.getMessage() + "\n" + ex.toString());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage() + "\n" + e.toString());
		}
	}

	@Test
	public void testLocal() {
		try {
			Object object = null;

			String ORIGINAL_NAME = "ATIntegrityCheckLocalCopyModified";

			ath.putSIPtoIngestArea(ORIGINAL_NAME, "tgz", ORIGINAL_NAME);
			ath.awaitObjectState(ORIGINAL_NAME, Object.ObjectStatus.ArchivedAndValidAndNotInWorkflow);
			object = ath.getObject(ORIGINAL_NAME);

			String fileName = getLocalFileName(object);
			writeOnFile(fileName, true);
			this.waitLocalRepair(object);
			
			fileName = getLocalFileName(object);
			writeOnFile(fileName, false);
			this.waitLocalRepair(object);
			
			this.localRemove(object);
			this.waitLocalRepair(object);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage() + "\n" + e.toString());
		}
	}

	String getLocalFileName(Object object) {
		Path archiveStoragePath = Path.make(getCI_ARCHIVE_STORAGE());

		String indy = object.getIdentifier();

		String storeName = indy + "/" + indy + ".pack_";
		String packName = object.getLatestPackage().getName();
		String fileName = archiveStoragePath + "/" + storeName + packName + ".tar";
		return fileName;
	}	
	
	public void localRemove(Object object ) {
		try {
			String fileName = this.getLocalFileName(object);
			File file = Path.makeFile(fileName);
			System.out.println("Try to delete: " + file + " file is exists: " + file.exists());
			file.delete();
			System.out.println("Deleted: " + file + " file is exists: " + file.exists());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage() + "\n" + e.toString());
		}
	}
	
	public void waitLocalRepair(Object object) {
		try {
			Session session = HibernateUtil.openSession();
			try {
				session.refresh(object);
				de.uzk.hki.da.model.Package pack = object.getPackages().get(0);

				int oldRepair = 0;
				if (pack.getRepair() != null) {
					oldRepair = pack.getRepair();
				}
				
				Calendar daysAgo = Calendar.getInstance();
				daysAgo.add(Calendar.DATE, -405);
				Transaction tx = session.beginTransaction();
				Date daysAgoD = daysAgo.getTime();
				object.setLast_checked(daysAgoD);
				session.save(object);
				tx.commit();

				int knockOut = 0;
				int newRepair = 0;
				do {
					System.out.println(
							"Wait local package be repaired. " + knockOut + " " + DATE_FORM.format(new Date()));
					Thread.sleep(2000);
					session.refresh(pack);
					if (pack.getRepair() != null) {
						newRepair = pack.getRepair();
					}
					knockOut++;
					if (knockOut > MAX_RETRY) {
						String msg = "Local package not repaired. Failed: " + object.getIdentifier();
						System.out.println(msg);
						fail(msg);
					}

				} while (newRepair != oldRepair + 1);
				String fileName = getLocalFileName(object);
				assertTrue(new File(fileName).exists());
				System.out.println("Local package " + fileName + " " + newRepair + " times repaired!");
			} finally {
				session.close();
			}

		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage() + "\n" + e.toString());
		}
	}
	
	@Test
	public void remoteTest() {
		try {
			Object object = null;

			String ORIGINAL_NAME = "ATIntegrityRemoteCopyDestroyed";

			ath.putSIPtoIngestArea(ORIGINAL_NAME, "tgz", ORIGINAL_NAME);
			ath.awaitObjectState(ORIGINAL_NAME, Object.ObjectStatus.ArchivedAndValidAndNotInWorkflow);
			object = ath.getObject(ORIGINAL_NAME);
			Session session = HibernateUtil.openSession();
			session.refresh(object);
			List<Copy> copyList = object.getLatestPackage().getCopies();
			if (copyList.size() < 1) {
				return;
			}

			Copy copy = copyList.get(0);
			this.remoteRemove(object, copy);
			this.waitRemoteRepair(session, object, copy);

			String fileName = this.getRemoteFileName(object, copy);
			writeOnFile(fileName, true);
			this.waitRemoteRepair(session, object, copy);
			
			fileName = this.getRemoteFileName(object, copy);
			writeOnFile(fileName, false);
			this.waitRemoteRepair(session, object, copy);
		
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage() + "\n" + e.toString());
		}
	}

	public void remoteRemove(Object object, Copy copy) {
		try {
			String fileName = getRemoteFileName(object, copy);
			File file = new File(fileName);
			System.out.println("Try to delete: " + file + " file is exists: " + file.exists());
			file.delete();
			System.out.println("Deleted: " + file + " file is exists: " + file.exists());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage() + "\n" + e.toString());
		}
	}

	public String getRemoteFileName(Object object, Copy copy) {
		String indy = object.getIdentifier();
		String federated = "/ci/archiveStorage/CN/federated/" + localNode.getIdentifier() + "/" + WorkArea.AIP + "/"
				+ object.getContractor().getShort_name();
		String tarName = indy + "/" + indy + ".pack_" + copy.getPackName() + ".tar";
		String fileName = federated + "/" + tarName;
		return fileName;
	}
	
	public void waitRemoteRepair(Session session, Object object, Copy copy) {
		try {
			Calendar daysAgo = Calendar.getInstance();
			daysAgo.add(Calendar.DATE, -405);

			Transaction tx = session.beginTransaction();
			String oldCS = copy.getChecksum();
			copy.setChecksumDate(daysAgo.getTime());
			session.save(copy);
			tx.commit();

			String newCS;

			int knockOut = 0;
			do {
				System.out.println("Wait Recomputing of Checksum. " + knockOut + " " + DATE_FORM.format(new Date()));
				Thread.sleep(2000);
				session.refresh(copy);
				newCS = copy.getChecksum();
				knockOut++;
				if (knockOut > MAX_RETRY) {
					String msg = "Recomputing of Checksum not performed. Failed: " + object.getIdentifier();
					System.out.println(msg);
					fail(msg);
				}

			} while (oldCS.equals(newCS));
			System.out.println("Checksum recomputed!");

			tx = session.beginTransaction();
			object.setLast_checked(daysAgo.getTime());
			session.save(object);
			tx.commit();

			int oldRepair = 0;
			if (copy.getRepair() != null) {
				oldRepair = copy.getRepair();
			}
			
			knockOut = 0;
			int newRepair = 0;
			do {
				System.out.println(
						"Wait remote package to be repaired. " + knockOut + " " + DATE_FORM.format(new Date()));
				Thread.sleep(2000);
				session.refresh(copy);
				if (copy.getRepair() != null) {
					newRepair = copy.getRepair();
				}
				knockOut++;
				if (knockOut > MAX_RETRY) {
					String msg = "Foreign package not repaired. Failed: " + object.getIdentifier();
					System.out.println(msg);
					fail(msg);
				}
			} while (newRepair != oldRepair + 1);
			String fileName = this.getRemoteFileName(object, copy);
			assertTrue(new File(fileName).exists());
			System.out.println("Remote package " + fileName + " " + newRepair + " times repaired!");

		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage() + "\n" + e.toString());
		}
	}

	public void testInitialSetOfChecksum() throws IOException {
		Object object = null;
		
		String ORIGINAL_NAME = "ATInitialSetOfChecksum";
	    
		ath.putSIPtoIngestArea(ORIGINAL_NAME, "tgz", ORIGINAL_NAME);
		ath.awaitObjectState(ORIGINAL_NAME,Object.ObjectStatus.ArchivedAndValidAndNotInWorkflow);
		object=ath.getObject(ORIGINAL_NAME);
		assertTrue(checkCopies(object));
	}

	private boolean checkCopies(Object object) {
		Session session = HibernateUtil.openSession();
		session.beginTransaction();
		session.refresh(object);

		de.uzk.hki.da.model.Package pack = object.getLatestPackage();
		String packCS = pack.getChecksum();
		for (Copy copy : object.getLatestPackage().getCopies()) {
			copy.getId();
			String copCS = copy.getChecksum();

			if (!packCS.equals(copCS)) {
				return false;
			}
		}
		return true;
	}
}
