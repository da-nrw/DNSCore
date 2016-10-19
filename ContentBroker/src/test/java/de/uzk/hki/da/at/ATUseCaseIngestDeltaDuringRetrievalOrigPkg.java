package de.uzk.hki.da.at;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.hibernate.Session;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import de.uzk.hki.da.model.Job;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.service.HibernateUtil;
import de.uzk.hki.da.utils.FolderUtils;

public class ATUseCaseIngestDeltaDuringRetrievalOrigPkg extends AcceptanceTest{

	private static String ORIG_NAME = "ATDeltaDuringRetrivalOrigPkg";
	private static Object object;
	private static final File retrievalFolder = new File("/tmp/unpackedDIP");
	
	@BeforeClass
	public static void setUp() throws IOException {
		
		ath.putSIPtoIngestArea(ORIG_NAME+"_orig", "tgz", ORIG_NAME);
		ath.awaitObjectState(ORIG_NAME, Object.ObjectStatus.ArchivedAndValidAndNotInWorkflow);
		object=ath.getObject(ORIG_NAME);
		
		object.setObject_state(50);
		Job job = new Job();
		job.setStatus("900");
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
		FolderUtils.deleteDirectorySafe(retrievalFolder);
	}
	
	@Test
	public void test() throws IOException, InterruptedException {
		
		ath.putSIPtoIngestArea(ORIG_NAME+"_delta", "tgz", ORIG_NAME);
		ath.waitForJobToBeInStatus(ORIG_NAME, "952");
		object=ath.getObject(ORIG_NAME);
		
//		Delete Job & Set object state
// 		normally this is being done by PostRetrievalAction checking for the createddate of Retrieval job
// 		The Job is in operation by PostRetrieval action, therefore we have to delete the job by hand!
		Session session = HibernateUtil.openSession();
		session.beginTransaction();
		session.createSQLQuery("DELETE FROM queue WHERE status='952'").executeUpdate(); 
		session.createSQLQuery("UPDATE objects SET object_state='100' WHERE object_state='50'").executeUpdate();
		session.getTransaction().commit();
		session.close();
		
		ath.putSIPtoIngestArea(ORIG_NAME+"_orig", "tgz", ORIG_NAME);
		ath.awaitObjectState(ORIG_NAME, Object.ObjectStatus.ArchivedAndValidAndNotInWorkflow);
		
//		object = ath.ingest(ORIG_NAME);
	
//		FolderUtils.deleteQuietlySafe(new File("src/test/resources/at/"+ORIG_NAME+".tgz"));
	}
}
