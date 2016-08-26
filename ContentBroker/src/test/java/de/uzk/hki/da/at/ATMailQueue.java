package de.uzk.hki.da.at;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.junit.Before;
import org.junit.Test;

import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.model.PendingMail;
import de.uzk.hki.da.service.HibernateUtil;
import de.uzk.hki.da.utils.C;

public class ATMailQueue extends AcceptanceTest {
	private static final String ORIG_NAME = "ATMailQueue";

	@Before
	public void setUp() throws IOException {
		ath.putSIPtoIngestArea(ORIG_NAME, C.FILE_EXTENSION_TGZ,
				ORIG_NAME);
	}

	@Test
	public void test() throws IOException{
		ath.awaitObjectState(ORIG_NAME,Object.ObjectStatus.ArchivedAndValidAndNotInWorkflow);
		
		Session session = HibernateUtil.openSession();

		String msgBegin = "Ihr eingeliefertes Paket mit dem Namen \"ATMailQueue.tgz\" wurde erfolgreich im DA-NRW archiviert%";

		Query query = session.createQuery("SELECT o FROM PendingMail o where message like ?1");

		query.setParameter("1", msgBegin);

		@SuppressWarnings("unchecked")
		List<PendingMail> dbMails = query.list();
		
		assertTrue(dbMails.size() > 0);
		
	}
}
