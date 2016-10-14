package de.uzk.hki.da.at;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.apache.commons.io.FileUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.uzk.hki.da.model.Job;
import de.uzk.hki.da.model.Node;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.model.PendingMail;
import de.uzk.hki.da.model.User;
import de.uzk.hki.da.pkg.ArchiveBuilderFactory;
import de.uzk.hki.da.service.HibernateUtil;
import de.uzk.hki.da.utils.C;

public class ATPublicMets extends AcceptanceTest {
	private static final String NO_NAME = "ATPublicMetsNo";
	private static final String YES_NAME = "ATPublicMetsYes";
	private static final String YES_NAME2 = "ATPublicMetsYes2";

	@Before
	public void setUp() throws IOException {
	}

	@After
	public void tearDown() {
		Boolean oldUsePublicMets = this.setUserPublicMets(null);
	}

	@Test
	public void testUserNoSipYes() throws Exception {
		ath.putSIPtoIngestArea(YES_NAME2, C.FILE_EXTENSION_TGZ, YES_NAME2);
		ath.waitForJobToBeInErrorStatus(YES_NAME2, "4");
	}

	@Test
	public void testUserYesSipYes() throws Exception {
		Boolean oldUsePublicMets = this.setUserPublicMets(Boolean.TRUE);

		ath.putSIPtoIngestArea(YES_NAME, C.FILE_EXTENSION_TGZ, YES_NAME);
		ath.awaitObjectState(YES_NAME, Object.ObjectStatus.ArchivedAndValidAndNotInWorkflow);

		this.setUserPublicMets(oldUsePublicMets);
		Object object = ath.getObject(YES_NAME);
		assertTrue(object.getPublished_flag() == 1);
	}

	@Test
	public void testUserYesSipNo() throws Exception {
		Boolean oldUsePublicMets = this.setUserPublicMets(Boolean.TRUE);

		ath.putSIPtoIngestArea(NO_NAME, C.FILE_EXTENSION_TGZ, NO_NAME);
		ath.awaitObjectState(NO_NAME, Object.ObjectStatus.ArchivedAndValidAndNotInWorkflow);

		this.setUserPublicMets(oldUsePublicMets);
		Object object = ath.getObject(NO_NAME);
		assertTrue(object.getPublished_flag() == 0);
	}

	public Boolean setUserPublicMets(Boolean usePublicMets) {
		Session session = HibernateUtil.openSession();
		Transaction transaction = session.beginTransaction();
		Query query = session.createQuery("SELECT u FROM User u where username = 'TEST'");

		@SuppressWarnings("unchecked")
		List<User> users = query.list();
		User testUser = users.get(0);
		Boolean oldUsePublicMets = testUser.isUsePublicMets();
		testUser.setUsePublicMets(usePublicMets);
		session.save(testUser);
		transaction.commit();
		session.close();

		return oldUsePublicMets;
	}
}
