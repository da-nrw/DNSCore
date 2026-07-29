package de.uzk.hki.da.at;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.uzk.hki.da.grid.IrodsCommandLineConnector;
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

	@Test
	public void localCopyModifiedTest() {
		try {
			Object object = null;

			String ORIGINAL_NAME = "ATIntegrityCheckLocalCopyModified";

			ath.putSIPtoIngestArea(ORIGINAL_NAME, "tgz", ORIGINAL_NAME);
			ath.awaitObjectState(ORIGINAL_NAME, Object.ObjectStatus.ArchivedAndValidAndNotInWorkflow);
			object = ath.getObject(ORIGINAL_NAME);

			Path archiveStoragePath = Path.make(getCI_ARCHIVE_STORAGE());

			String indy =object.getIdentifier();
			File file = Path.makeFile(archiveStoragePath, indy, indy+".pack_1.tar");

			Writer writer = null;
			try {
				System.out.println("Try to modify: "+file +" file is exists: "+file.exists());
				writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "utf-8"));
				writer.write("Kaputnik");
			} catch (Exception ex) {
				fail("writing to file " + file + " failed"+ "\n" +
						ex.getMessage() + "\n" + ex.toString());
			} finally {
				try {
					if(writer!=null)
						writer.close();
				} catch (Exception ex) {
					fail(ex.getMessage() + "\n" + ex.toString());
				}
			}

			
			Session session = HibernateUtil.openSession();

			try {
				session.refresh(object);
				de.uzk.hki.da.model.Package pack = object.getPackages().get(0);

				Calendar daysAgo = Calendar.getInstance();
				daysAgo.add(Calendar.DATE, -405);
				Transaction tx = session.beginTransaction();
				Date daysAgoD = daysAgo.getTime();
				object.setLast_checked(daysAgoD);
				session.save(object);
				tx.commit();

				int knockOut = 0;
				Integer repair = 0;
				do {
					System.out.println("Wait local package be repaired. " + knockOut+ " "+DATE_FORM.format(new Date()));
					Thread.sleep(2000);
					session.refresh(pack);
					repair = pack.getRepair();
					knockOut++;
					if (knockOut > MAX_RETRY) {
						String msg = "Local package not repaired. Failed: " + file;
						System.out.println(msg);
						fail(msg);
					}

				} while (repair == null);
				System.out.println("Local package repaired!");
			} finally {
				session.close();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage()+"\n"+e.toString());
		}
	}

	
	@Test
	public void remoteCopyDeletedTest() {
		try {
			Object object = null;

			String ORIGINAL_NAME = "ATIntegrityRemoteCopyDestroyed";

			ath.putSIPtoIngestArea(ORIGINAL_NAME, "tgz", ORIGINAL_NAME);
			ath.awaitObjectState(ORIGINAL_NAME, Object.ObjectStatus.ArchivedAndValidAndNotInWorkflow);
			object = ath.getObject(ORIGINAL_NAME);

			Session session = HibernateUtil.openSession();

			try {
				Transaction tx = session.beginTransaction();
				session.refresh(object);

				List<Copy> copyList = object.getLatestPackage().getCopies();
				if (copyList.size() < 1) {
					return;
				}

				Copy copy = copyList.get(0);
				String oldCS = copy.getChecksum();

				String daoBase = WorkArea.AIP + "/" + object.getContractor().getShort_name() + "/" + object.getIdentifier() + "/" + object.getIdentifier() + ".pack_";

				String foreignName = "/" + copy.getNode().getIdentifier() + "/federated" 
									+ "/" + localNode.getIdentifier() + "/" 
									+ daoBase + copy.getPackName() + ".tar";;

				System.out.println("Will destroy: " + foreignName);

				IrodsCommandLineConnector iclc = new IrodsCommandLineConnector();
				iclc.remove(foreignName);

				Calendar daysAgo = Calendar.getInstance();
				daysAgo.add(Calendar.DATE, -405);

				copy.setChecksumDate(daysAgo.getTime());
				session.save(copy);
				tx.commit();

				String newCS;
				
				int knockOut = 0; 
				do {
					System.out.println("Wait Recomputing of Checksum. " + knockOut+" "+DATE_FORM.format(new Date()));
					Thread.sleep(2000);
					session.refresh(copy);
					newCS = copy.getChecksum();
					knockOut++;
					if (knockOut > MAX_RETRY){
						String msg = "Recomputing of Checksum not performed. Failed: " + foreignName;
						System.out.println(msg);
						fail(msg);
					}
						
				} while (oldCS.equals(newCS));
				System.out.println("Checksum recomputed!");

				tx = session.beginTransaction();
				object.setLast_checked(daysAgo.getTime());
				session.save(object);
				tx.commit();
				
				knockOut = 0;
				do {
					System.out.println("Wait remote package to be repaired. " + knockOut+" "+DATE_FORM.format(new Date()));
					Thread.sleep(2000);
					session.refresh(copy);
					newCS = copy.getChecksum();
					knockOut++;
					if (knockOut > MAX_RETRY){
						String msg = "Foreign package not repaired. Failed: " + foreignName;
						System.out.println(msg);
						fail(msg);
					}
						
				} while (!oldCS.equals(newCS));
				System.out.println("Remote package repaired!");

			} finally {
				session.close();
			}
			} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage()+"\n"+e.toString());
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
