package de.uzk.hki.da.cb;



import static de.uzk.hki.da.utils.C.FILE_EXTENSION_TAR;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.FilenameUtils;
import org.hibernate.Session;
import org.jdom.JDOMException;
import org.xml.sax.SAXException;

import de.uzk.hki.da.action.AbstractAction;
import de.uzk.hki.da.core.SubsystemNotAvailableException;
import de.uzk.hki.da.core.UserException;
import de.uzk.hki.da.model.PremisReader;
import de.uzk.hki.da.model.PremisEvent;
import de.uzk.hki.da.model.PremisObject;
import de.uzk.hki.da.model.PremisPackage;
import de.uzk.hki.da.model.WorkArea;
import de.uzk.hki.da.pkg.ArchiveBuilder;
import de.uzk.hki.da.pkg.ArchiveBuilderFactory;
import de.uzk.hki.da.repository.RepositoryException;
import de.uzk.hki.da.service.HibernateUtil;
import de.uzk.hki.da.utils.Path;

public class WritePremisDBAction extends AbstractAction {

	public WritePremisDBAction(String path) {
		this.premisPath = path;
	}
	
	public WritePremisDBAction() {
		FileWriter fw;
		try {
			fw = new FileWriter("/home/julia/Desktop/testDaten/testpremis11234_1.xml");
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write("test WritePremisDBAction ");
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		//this.premis = wa.toFile(o.getLatest(PREMIS_XML));
		//this.premisPath = "/home/julia/Desktop/testDaten/1-2016070651.tar";
	}
	
	@Override
	public void checkConfiguration() {
	}

	@Override
	public void checkPreconditions() {
	}
	
	
	private static final String PREMIS_XML = "premis.xml";
	
	private String premisPath = "";
	private File premis = null;
	
	private static final File targetDir = new File("/tmp/ATPremisDb");
	private File unpackedDIP;
	
	private Path createTmpFolder(){
		Path tmpFolder = Path.make(n.getWorkAreaRootPath(),WorkArea.WORK,
				o.getContractor().getShort_name(), o.getIdentifier(), o.getIdentifier()); 
		tmpFolder.toFile().mkdir();
		return tmpFolder;
	}
	
	@Override
	public boolean implementation() throws IOException {
		FileWriter fw2 = null;
		Path newTar;
		FileWriter fw0 = null;
		try {
			
		/*
		String unpackedObjectPath = unpackedDIP.getAbsolutePath()+"/";

		String folders[] = new File(unpackedObjectPath + "data/").list();
		String repBName="";
		for (String f:folders){
			if (f.contains("+b")) repBName = f;
		}
		*/
		
			fw0 = new FileWriter("/home/julia/Desktop/testDaten/testpremis1234_2.txt");
			BufferedWriter bw0 = new BufferedWriter(fw0);
		File f = null;
	
		if(premisPath != null && !premisPath.equals("")) {
			if(premisPath.toLowerCase().endsWith(".xml")) {
				premis = new File(premisPath);
				bw0.write("Pfad " + premisPath);
				bw0.close();
			} else {
				f = new File(premisPath);
				bw0.write("Pfad " + f.toString() + " " + premisPath);
				bw0.close();
			}
		} else {
			newTar = Path.make(n.getUserAreaRootPath(),o.getContractor().getShort_name(),"outgoing",o.getIdentifier() + FILE_EXTENSION_TAR);
			try {
				f = newTar.toFile();
			} catch (Exception e) {
				
			}
			
			
			bw0.write("Pfad " + newTar.toString());
			bw0.close();
			FileWriter fw1 = new FileWriter("/home/julia/Desktop/testDaten/testpremis1234_1.txt");
			BufferedWriter bw1 = new BufferedWriter(fw1);
			bw1.write("Pfad " + f.toString());
			bw1.close();
		}
		if(f != null) {
					
			ArchiveBuilder builder = ArchiveBuilderFactory.getArchiveBuilderForFile(f); 
				    
			try {
				builder.unarchiveFolder(f, targetDir);
			} catch (Exception e) {
				throw new RuntimeException("couldn't unpack archive " + premisPath + ", " + targetDir, e);
			}
			unpackedDIP = new File(targetDir.getPath() + "/"+ FilenameUtils.removeExtension(f.getName()));
			premis = new File(unpackedDIP, "data/premis.xml");
					
		}
		
		/*
		System.out.println("----- in WritePremisDBAction -----");
		
		this.premis = wa.toFile(o.getLatest(PREMIS_XML));
		
		System.out.println(premis);
		
		File testF = new File("/home/julia/Desktop/testDaten/testpremis1123.xml");
		
		copyFile(premis, testF);
		fw2 = new FileWriter("/home/julia/Desktop/testDaten/testpremis11234.xml");
		BufferedWriter bw2 = new BufferedWriter(fw2);
		bw2.write("test WritePremisDBAction ");
		bw2.write(testF.toString());
		bw2.close();
		} catch (Exception e) {
			FileWriter fw3 = new FileWriter("/home/julia/Desktop/testDaten/testpremis11234_f.xml");
			BufferedWriter bw3 = new BufferedWriter(fw3);
			bw3.write("test WritePremisDBAction ");
			bw3.write(e.getMessage());
			bw3.close();
			
		} finally {
			if(fw2 != null) {
				try {
					fw2.flush();
					fw2.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		
		this.premis = wa.toFile(o.getLatest(PREMIS_XML));
		*/
		
		} catch (Exception e) {
			FileWriter fw3 = new FileWriter("/home/julia/Desktop/testDaten/testpremis1234_f.xml");
			BufferedWriter bw3 = new BufferedWriter(fw3);
			bw3.write("test WritePremisDBAction ");
			e.printStackTrace();
			bw3.write(e.toString() + " " + e.getStackTrace());
			bw3.close();
			
		} finally {
			fw0.close();
		}
		
		PremisReader epr = new PremisReader();
		//ArrayList<PremisEvent> pes = epr.buildPremisEvents(new File(unpackedObjectPath +  "data/" + repBName + "/premis.xml"));
		
		//PremisObject obj = epr.buildPremisObject(new File(unpackedObjectPath +  "data/" + repBName + "/premis.xml"));
		PremisObject obj = epr.buildPremisObject(premis);
		
		
		Session session = HibernateUtil.openSession();
		session.beginTransaction();
			
		List objects = session.createCriteria(PremisObject.class).list();
		
		System.out.println("Objects: " + objects.size());
		Iterator i = objects.iterator();
		while(i.hasNext()) {
			PremisObject pe = (PremisObject) i.next();
			System.out.println(pe.getData_pk() + " " + pe.getIdentifier() + " " + pe.getOrig_name());
		}
		
		String name = obj.getOrig_name();
		
		List objs = session.createSQLQuery("select * from premis_objects where orig_name like :name").addEntity(PremisObject.class).setParameter("name", name).list();
		
		System.out.println("Objects2: " + objs.size());
		Iterator i2 = objs.iterator();
		while(i2.hasNext()) {
			PremisObject pe = (PremisObject) i2.next();
			System.out.println(pe.getData_pk() + " " + pe.getIdentifier() + " " + pe.getOrig_name());
			session.delete(pe);
			System.out.println("gelöscht");
		}

		
		session.getTransaction().commit();
		session.close();
		
		session = HibernateUtil.openSession();
		session.beginTransaction();
		
		
		for (PremisPackage pp: obj.getPackages()) {
			for (PremisEvent pe: pp.getEvents()) {
				if(pe.getType().equals("CONVERT")) {
					session.persist(pe.getSource_file());
					session.persist(pe.getTarget_file());
				}
				session.persist(pe);
			}
			session.persist(pp);
		}
		
		session.persist(obj);
		
		/*
		for(int i = 0; i < 1; i++) {
			PremisObject iter = epr.buildPremisObject(premis);
			iter.setIdentifier(iter.getIdentifier() + i);
			
			for (PremisPackage pp: iter.getPackages()) {
				for (PremisEvent pe: pp.getEvents()) {
					if(pe.getType().equals("CONVERT")) {
						session.persist(pe.getSource_file());
						session.persist(pe.getTarget_file());
					}
					session.persist(pe);
				}
				session.persist(pp);
			}
			
			session.persist(iter);
		}*/
		
		session.getTransaction().commit();
		session.close();
		
		/*
		Session session = HibernateUtil.openSession();
		session.beginTransaction();
		for (PremisEvent pe : pes) {
			System.out.println(pe.getType());
			if(pe.getType().equals("CONVERT") || pe.getType().equals("COPY")) {
				session.persist(pe.getSource_file());
				session.persist(pe.getTarget_file());
			}
			session.persist(pe);
		}
		session.getTransaction().commit();
		session.close();
		*/
		return true;
	}

	@Override
	public void rollback() throws Exception {
		

	}

	
	 private void copyFile(File in, File out) throws IOException { 
	        FileChannel inChannel = null; 
	        FileChannel outChannel = null; 
	        try { 
	            inChannel = new FileInputStream(in).getChannel(); 
	            outChannel = new FileOutputStream(out).getChannel(); 
	            inChannel.transferTo(0, inChannel.size(), outChannel); 
	        } catch (IOException e) { 
	            throw e; 
	        } finally { 
	            try { 
	                if (inChannel != null) 
	                    inChannel.close(); 
	                if (outChannel != null) 
	                    outChannel.close(); 
	            } catch (IOException e) {} 
	        } 
	    } 
	
}
