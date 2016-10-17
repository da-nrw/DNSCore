package de.uzk.hki.da.cb;

import static de.uzk.hki.da.utils.C.FILE_EXTENSION_TAR;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.fest.util.Files;
import org.hibernate.Session;

import de.uzk.hki.da.action.AbstractAction;
import de.uzk.hki.da.model.Job;
import de.uzk.hki.da.model.PremisReader;
import de.uzk.hki.da.model.PremisEvent;
import de.uzk.hki.da.model.PremisObject;
import de.uzk.hki.da.model.PremisPackage;
import de.uzk.hki.da.model.WorkArea;
import de.uzk.hki.da.pkg.ArchiveBuilder;
import de.uzk.hki.da.pkg.ArchiveBuilderFactory;
import de.uzk.hki.da.service.HibernateUtil;
import de.uzk.hki.da.utils.Path;

public class WritePremisDBAction extends AbstractAction {

	public WritePremisDBAction(String path) {
		this.premisPath = path;
	}

	public WritePremisDBAction() {
		SUPPRESS_OBJECT_CONSISTENCY_CHECK=true;
		setKILLATEXIT(true);
		// this.premis = wa.toFile(o.getLatest(PREMIS_XML));
	}

	@Override
	public void checkConfiguration() {
	}

	@Override
	public void checkPreconditions() {
		if (!wa.objectPath().toFile().exists()) throw new IllegalStateException("object data path on fs doesn't exist on fs");
		if (!wa.toFile(o.getLatest(PREMIS_XML)).exists()) throw new RuntimeException("CRITICAL ERROR: premis file could has not been found");
	}

	private static final String PREMIS_XML = "premis.xml";

	private String premisPath = "";
	private File premis = null;

	private static final File targetDir = new File("/tmp/ATPremisDb");
	private File unpackedDIP;

	private Path createTmpFolder() {
		Path tmpFolder = Path.make(n.getWorkAreaRootPath(), WorkArea.WORK, o
				.getContractor().getShort_name(), o.getIdentifier(), o
				.getIdentifier());
		tmpFolder.toFile().mkdir();
		return tmpFolder;
	}

	@Override
	public boolean implementation() throws IOException {
		
		Path newTar;

		Path tempFolder = createTmpFolder(); //
		File dest = moveNewestPremisToDIP(tempFolder); //
		
		try {

			File f = null;

			if (premisPath != null && !premisPath.equals("")) {
				if (premisPath.toLowerCase().endsWith(".xml")) {
					premis = new File(premisPath);
				} else {
					f = new File(premisPath);
				}
			} else {
				if(dest != null) { //
					premis = dest; //
				}
				else {
					newTar = Path.make(n.getUserAreaRootPath(), o.getContractor()
						.getShort_name(), "outgoing", o.getIdentifier()
						+ FILE_EXTENSION_TAR);
					f = newTar.toFile();
				}
			}
			if (f != null) {

				// warten, bis das DIP bereit liegt
				long startTime = System.currentTimeMillis();
				long curTime = System.currentTimeMillis();
				while(!f.exists() && Math.abs(curTime-startTime) < 180000) { // 3 Min
					Thread.sleep(1000);
				}
				
				ArchiveBuilder builder = ArchiveBuilderFactory
						.getArchiveBuilderForFile(f);

				try {
					builder.unarchiveFolder(f, targetDir);
				} catch (Exception e) {
					throw new RuntimeException("couldn't unpack archive "
							+ premisPath + ", " + targetDir, e);
				}
				unpackedDIP = new File(targetDir.getPath() + "/"
						+ FilenameUtils.removeExtension(f.getName()));
				premis = new File(unpackedDIP, "data/" + PREMIS_XML);
				
				try {
					Files.delete(f);
				} catch (Exception e) {
					
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		PremisReader epr = new PremisReader();
		
		PremisObject obj = epr.buildPremisObject(premis);
		
		Session session = openSession();
		//	Daten zu alter Premis löschen
		try {
			session.beginTransaction();

			List objects = session.createCriteria(PremisObject.class).list();

			
			String identifier = obj.getIdentifier();
			System.out.println("identifier: " + identifier);
			List objs = session.createSQLQuery(
							"select * from queue where status like '1000' and objects_id = (select data_pk from objects where identifier like :identifier)")
					.addEntity(Job.class).setParameter("identifier", identifier).list(); // '952', '991', '992', 

			System.out.println("Jobs: " + objs.size()); //
		
			Iterator i = objs.iterator();
			while (i.hasNext()) {
				Job j = (Job) i.next();
				System.out.println("delete job id: " + j.getId() + ", obj: " + j.getObject());
				session.delete(j);
			}
			
			
			String name = obj.getOrig_name();

			System.out.println("name: " + name);
			
			List objs2 = session
					.createSQLQuery(
							"select * from premis_objects where orig_name like :name")
					.addEntity(PremisObject.class).setParameter("name", name)
					.list();

			System.out.println("Objects2: " + objs2.size());
			Iterator i2 = objs2.iterator();
			while (i2.hasNext()) {
				PremisObject pe = (PremisObject) i2.next();
				System.out.println(pe.getData_pk() + " " + pe.getIdentifier()
						+ " " + pe.getOrig_name());
				session.delete(pe);
				System.out.println("deleted");
			}

			session.getTransaction().commit();
		} catch (Exception e) {
		} finally {
			session.close();
		}

		Session session2 = HibernateUtil.openSession();
		// Daten der neuen Premis persistieren
		try {
			session2.beginTransaction();

			for (PremisPackage pp : obj.getPackages()) {
				for (PremisEvent pe : pp.getEvents()) {
					if (pe.getType().equals("CONVERT")) {
						session2.persist(pe.getSource_file());
						session2.persist(pe.getTarget_file());
					}
					session2.persist(pe);
				}
				session2.persist(pp);
			}

			session2.persist(obj);

			session2.getTransaction().commit();
		} catch (Exception e) {

		} finally {
			session2.close();
		}

		cleanupFS();
		o.setObject_state(100);
		return true;
	}
	
	private File moveNewestPremisToDIP(Path tempFolder) throws IOException {
		File dest = Path.makeFile(tempFolder,WorkArea.DATA,PREMIS_XML);
		FileUtils.copyFile(wa.toFile(o.getLatest(PREMIS_XML)), dest);
		return dest;
	}

	private void cleanupFS() throws IOException{
		
		// cleanup
		
		FileUtils.deleteDirectory(wa.objectPath().toFile());
	}
	
	@Override
	public void rollback() throws Exception {

	}

}
