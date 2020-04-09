package de.uzk.hki.da.at;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.IOException;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.Test;

import de.uzk.hki.da.model.Job;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.service.HibernateUtil;
import de.uzk.hki.da.utils.C;

public class ATUseCaseDeleteObjectFromWorkflow extends AcceptanceTest{
	private static final String AT_DeleteObject = "ATDeleteObject";
	private static final String AT_DeleteDelta = "ATDeleteDelta";
	private static final String AT_UncompletedReferences_METS = "ATDetectUncompletedReferencesMets";
	private static final String ATMailQueue = "ATMailQueue";
	
	
	@Test
	public void test() throws InterruptedException, IOException {
		ath.putSIPtoIngestArea( AT_UncompletedReferences_METS, "tgz", AT_DeleteObject);
		ath.waitForJobToBeInErrorStatus(AT_DeleteObject, C.WORKFLOW_STATUS_DIGIT_USER_ERROR);
		assertNotNull(ath.getObject(AT_DeleteObject));

		Job jobbi = ath.getJob(AT_DeleteObject);

		Session session = HibernateUtil.openSession();
		session.refresh(jobbi);

		Transaction trans = session.beginTransaction();
		jobbi.setStatus("800");
		session.save(jobbi);
		trans.commit();
		session.close();
		
		Object obj = null;
		for (int iii = 0; iii < 30; iii++) {
			Thread.sleep(1000);
			obj = ath.getObject(AT_DeleteObject);
			if (obj == null) {
				break;
			}
			System.out.println("ATUseCaseDeleteObjectFromWorkflow: Awaiting object deletion");
		}
			
		assertNull(obj);
	}

	@Test
	public void testDelta() throws InterruptedException, IOException {
		ath.putSIPtoIngestArea( ATMailQueue, "tgz", AT_DeleteDelta);
		ath.awaitObjectState(AT_DeleteDelta,Object.ObjectStatus.ArchivedAndValidAndNotInWorkflow);
		
		ath.putSIPtoIngestArea( AT_UncompletedReferences_METS, "tgz", AT_DeleteDelta);
		ath.waitForJobToBeInErrorStatus(AT_DeleteDelta, C.WORKFLOW_STATUS_DIGIT_USER_ERROR);
		
		assertNotNull(ath.getObject(AT_DeleteDelta));

		Job jobbi = ath.getJob(AT_DeleteDelta);

		Session session = HibernateUtil.openSession();
		session.refresh(jobbi);

		Transaction trans = session.beginTransaction();
		jobbi.setStatus("800");
		session.save(jobbi);
		trans.commit();
		session.close();
		
		ath.awaitObjectState(AT_DeleteDelta,Object.ObjectStatus.ArchivedAndValidAndNotInWorkflow);
		Object obbi = ath.getObject(AT_DeleteDelta);
		assertNotNull(obbi);
	}
}
