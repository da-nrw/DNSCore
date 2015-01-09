package de.uzk.hki.da.at;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.hibernate.classic.Session;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import de.uzk.hki.da.model.Job;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.service.HibernateUtil;

public class ATUseCaseIngestDeltaDuringRetrivalOrigPkg extends AcceptanceTest{

	private static final int _1_MINUTE = 60000;
	private static String ORIG_NAME = "ATUseCaseIngestDeltaDuringRetrivalOrigPkg";
	private static Object object;
	private static final File retrievalFolder = new File("/tmp/unpackedDIP");
	
	@BeforeClass
	public static void setUp() throws IOException {
		
		FileUtils.copyFileToDirectory(new File("src/test/resources/at/"+ORIG_NAME+"_orig/"+ORIG_NAME+".tgz"), new File("src/test/resources/at"));
		object = ath.ingest(ORIG_NAME);
		FileUtils.deleteQuietly(new File("src/test/resources/at/"+ORIG_NAME+".tgz"));
		
		object.setObject_state(50);
		Job job = new Job();
		job.setStatus("950");
		job.setObject(object);
		job.setResponsibleNodeName(object.getInitial_node());
		job.setDate_created(String.valueOf(new Date().getTime()/1000L));
		job.setDate_modified(String.valueOf(new Date().getTime()/1000L));

		Session session = HibernateUtil.openSession();
		session.beginTransaction();
		session.saveOrUpdate(object);
		session.save(job);
		session.getTransaction().commit();
		session.close();
	}
	
	@AfterClass
	public static  void tearDown() throws IOException{
		FileUtils.deleteDirectory(retrievalFolder);
	}
	
	@Test
	public void test() throws IOException, InterruptedException {
		
		Thread.sleep(_1_MINUTE);

		FileUtils.copyFileToDirectory(new File("src/test/resources/at/"+ORIG_NAME+"_delta/"+ORIG_NAME+".tgz"), new File("src/test/resources/at"));
		object = ath.ingestAndWaitForJobInState(ORIG_NAME, "950");
		
//		Delete Job & Set object state
		Session session = HibernateUtil.openSession();
		session.beginTransaction();
		session.createSQLQuery("DELETE FROM queue WHERE status='950'").executeUpdate(); 
		session.createSQLQuery("UPDATE objects SET object_state='100' WHERE object_state='50'").executeUpdate();
		session.getTransaction().commit();
		session.close();
		
		object = ath.ingest(ORIG_NAME);
	
		FileUtils.deleteQuietly(new File("src/test/resources/at/"+ORIG_NAME+".tgz"));
	}
}
