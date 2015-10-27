package de.uzk.hki.da.at;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.uzk.hki.da.model.Job;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.service.HibernateUtil;
import de.uzk.hki.da.utils.C;
import de.uzk.hki.da.utils.Path;

/**
 * @author Josef Hammer
 */
public class ATMigrationDecisionTimeout extends AcceptanceTest {
	private static final String ORIG_NAME_NOTALLOWED = "ATMigrationDecisionTimeout";
	private static final File UNPACKED_TMP = new File("/tmp/DecisionUnpacked");

	@Before
	public void setUp() throws IOException {
		ath.putSIPtoIngestArea(ORIG_NAME_NOTALLOWED, C.FILE_EXTENSION_TGZ,
				ORIG_NAME_NOTALLOWED);
	}

	@After
	public void tearDown() {
		FileUtils.deleteQuietly(UNPACKED_TMP);
	}

	@Test
	public void testMigrationTimeout() throws IOException, InterruptedException {

		ath.waitForJobToBeInStatus(ORIG_NAME_NOTALLOWED,
				C.WORKFLOW_STATUS_WAIT___PROCESS_FOR_USER_DECISION_ACTION);

		Job jobbi = ath.getJob(ORIG_NAME_NOTALLOWED);

		String notRealyLongAgo = String
				.valueOf(new Date().getTime() / 1000L - 86400 * 30);

		Session session = HibernateUtil.openSession();
		session.refresh(jobbi);

		Object obbi = jobbi.getObject();
		Transaction trans = session.beginTransaction();
		jobbi.setDate_modified(notRealyLongAgo);
		session.save(jobbi);
		trans.commit();
		session.close();

		ath.awaitObjectState(ORIG_NAME_NOTALLOWED,
				Object.ObjectStatus.ArchivedAndValidAndNotInWorkflow);

		ath.retrieveAIP(obbi, UNPACKED_TMP, "1");

		String aDir = "", bDir = "";
		File[] fList = Path.makeFile(UNPACKED_TMP.toString(), "data")
				.listFiles();
		for (File file : fList) {
			if (file.getAbsolutePath().endsWith("+a")) {
				aDir = FilenameUtils.getBaseName(file.getAbsolutePath());
			} else if (file.getAbsolutePath().endsWith("+b")) {
				bDir = FilenameUtils.getBaseName(file.getAbsolutePath());
			}
		}

		File orgFile = Path.makeFile(UNPACKED_TMP.toString(), "data", aDir,	"v.bmp");
		assertTrue(orgFile.exists());

		File migFile = Path.makeFile(UNPACKED_TMP.toString(), "data", bDir,	"v.tif");
		assertTrue(!migFile.exists());
	}
}
