package de.uzk.hki.da.at;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import de.uzk.hki.da.event.QualityLevelRecheckEvent;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.service.HibernateUtil;
import de.uzk.hki.da.utils.C;
import de.uzk.hki.da.utils.FolderUtils;
import de.uzk.hki.da.utils.XMLUtils;

/**
 * Acceptance Tests fuer das QualityLevelRecheck-Feature.
 *
 * Fachliches Modell:
 *
 * - Jeder normale Ingest / jedes Delta erzeugt ein neues Package. - Jeder
 * QualityLevelRecheck erzeugt ebenfalls ein neues Package. - Representation A
 * eines Recheck-Packages darf KEINE Nutzdaten enthalten. Erlaubt sind nur: -
 * premis.xml (Pflicht) - export_mets.xml (optional) - Representation B darf
 * neben Metadaten auch neu erzeugte Derivate enthalten. - premis.xml in
 * Representation A muss ein QUALITY_LEVEL_RECHECK Event enthalten.
 *
 * Testdaten:
 *
 * ATQualityLevelRecheck_COMPLETE Vollstaendiges Test-SIP.
 *
 * ATQualityLevelRecheck_EMPTY Leeres Test-SIP.
 *
 * ATQualityLevelRecheck_NEWTIF Test-SIP mit neuem TIFF.
 *
 * ATQualityLevelRecheck_ONEFILE Kleines SIP mit genau einer Nutzdatei.
 */
public class ATQualityLevelRecheck extends AcceptanceTest {

	private static final String SOURCE_PACKAGE_COMPLETE = "ATQualityLevelRecheck_COMPLETE";

	private static final String SOURCE_PACKAGE_EMPTY = "ATQualityLevelRecheck_EMPTY";

	private static final String SOURCE_PACKAGE_NEWTIF = "ATQualityLevelRecheck_NEWTIF";

	private static final String SOURCE_PACKAGE_ONEFILE = "ATQualityLevelRecheck_ONEFILE";

	private static final String PREMIS_EVENT_QUALITY_LEVEL_RECHECK = "QUALITY_LEVEL_RECHECK";

	private static final String PREMIS_FILE = "premis.xml";

	private static final String EXPORT_METS_FILE = "export_mets.xml";

	private static final int GOOD_QUALITY_LEVEL_1 = 4;
	private static final int GOOD_QUALITY_LEVEL_2 = 5;
	private static final int BAD_QUALITY_LEVEL = 1;

	private static final File IMAGEMAGICK_DISABLED_FLAG = new File("/tmp/dnscore-imagemagick-disabled");

	@Before
	public void beforeTest() throws Exception {
		enableImageMagick();
	}

	@After
	public void afterTest() throws Exception {
		enableImageMagick();
	}

	/*
	 * ===================================================================== 1.
	 * Basisfaelle
	 * =====================================================================
	 * WORKS (REMINDER)
	 */
	@Ignore
	@Test
	public void completeInitialWithImageMagickGetsGoodQualityLevel() throws Exception {

		String originalName = uniqueName("CompleteInitial_Good");

		enableImageMagick();

		Object object = ingestInitial(SOURCE_PACKAGE_COMPLETE, originalName);

		assertGoodQualityLevel(object);
		assertPackageCount(object, 1);
	}
	@Ignore
	@Test
	public void oneFileInitialWithoutImageMagickGetsBadQualityLevel() throws Exception {

		String originalName = uniqueName("OneFileInitial_Bad");

		disableImageMagick();

		Object object = ingestInitial(SOURCE_PACKAGE_ONEFILE, originalName);

		assertBadQualityLevel(object);
		assertPackageCount(object, 1);
	}

	/*
	 * ===================================================================== 2.
	 * Recheck direkt nach Erstingest
	 * =====================================================================
	 */
	@Ignore
	@Test
	public void badCompleteInitialThenGoodRecheckRecovers() throws Exception {

		String originalName = uniqueName("BadComplete_GoodRecheck");

		disableImageMagick();

		Object object = ingestInitial(SOURCE_PACKAGE_COMPLETE, originalName);

		assertBadQualityLevel(object);
		assertPackageCount(object, 1);

		enableImageMagick();

		object = recheck(originalName);

		assertGoodQualityLevel(object);
		assertPackageCount(object, 2);

		assertQualityLevelRecheckPackage(object, 2);
	}
	@Ignore
	@Test
	public void goodOneFileInitialThenBadRecheckBecomesBad() throws Exception {

		String originalName = uniqueName("GoodOneFile_BadRecheck");

		enableImageMagick();

		Object object = ingestInitial(SOURCE_PACKAGE_ONEFILE, originalName);

		assertGoodQualityLevel(object);
		assertPackageCount(object, 1);

		disableImageMagick();

		object = recheck(originalName);

		assertBadQualityLevel(object);
		assertPackageCount(object, 2);

		assertQualityLevelRecheckPackage(object, 2);
	}

	/*
	 * ===================================================================== 3. Ein
	 * Delta zwischen Erstingest und Recheck
	 * =====================================================================
	 */
	@Test
	public void goodCompleteThenBadNewTifDeltaThenGoodRecheckRecovers() throws Exception {

		String originalName = uniqueName("Complete_BadNewTif_GoodRecheck");

		enableImageMagick();

		Object object = ingestInitial(SOURCE_PACKAGE_COMPLETE, originalName);

		assertGoodQualityLevel(object);
		assertPackageCount(object, 1);

		disableImageMagick();

		object = ingestDelta(SOURCE_PACKAGE_NEWTIF, originalName);

		assertBadQualityLevel(object);
		assertPackageCount(object, 2);

		enableImageMagick();

		object = recheck(originalName);

		assertGoodQualityLevel(object);
		assertPackageCount(object, 3);

		assertQualityLevelRecheckPackage(object, 3);
	}
	@Ignore
	@Test
	public void badOneFileThenGoodCompleteDeltaThenGoodRecheck() throws Exception {

		String originalName = uniqueName("BadOneFile_GoodCompleteDelta_Recheck");

		disableImageMagick();

		Object object = ingestInitial(SOURCE_PACKAGE_ONEFILE, originalName);

		assertBadQualityLevel(object);
		assertPackageCount(object, 1);

		enableImageMagick();

		object = ingestDelta(SOURCE_PACKAGE_COMPLETE, originalName);

		assertGoodQualityLevel(object);
		assertPackageCount(object, 2);

		object = recheck(originalName);

		assertGoodQualityLevel(object);
		assertPackageCount(object, 3);

		assertQualityLevelRecheckPackage(object, 3);
	}

	/*
	 * ===================================================================== 4.
	 * Mehrere Deltas vor dem Recheck
	 * =====================================================================
	 */
	@Test
	public void multipleDifferentBadDeltasThenGoodRecheckRecovers() throws Exception {

		String originalName = uniqueName("MultipleDifferentBadDeltas_Recheck");

		enableImageMagick();

		Object object = ingestInitial(SOURCE_PACKAGE_COMPLETE, originalName);

		assertGoodQualityLevel(object);
		assertPackageCount(object, 1);

		disableImageMagick();

		object = ingestDelta(SOURCE_PACKAGE_NEWTIF, originalName);

		assertBadQualityLevel(object);
		assertPackageCount(object, 2);

		object = ingestDelta(SOURCE_PACKAGE_ONEFILE, originalName);

		assertBadQualityLevel(object);
		assertPackageCount(object, 3);

		object = ingestDelta(SOURCE_PACKAGE_COMPLETE, originalName);

		assertBadQualityLevel(object);
		assertPackageCount(object, 4);

		enableImageMagick();

		object = recheck(originalName);

		assertGoodQualityLevel(object);
		assertPackageCount(object, 5);

		assertQualityLevelRecheckPackage(object, 5);
	}
	@Test
	public void mixedGoodAndBadDeltaHistoryThenGoodRecheckEndsGood() throws Exception {

		String originalName = uniqueName("MixedDeltaHistory_Recheck");

		enableImageMagick();

		Object object = ingestInitial(SOURCE_PACKAGE_ONEFILE, originalName);

		assertGoodQualityLevel(object);
		assertPackageCount(object, 1);

		disableImageMagick();

		object = ingestDelta(SOURCE_PACKAGE_NEWTIF, originalName);

		assertBadQualityLevel(object);
		assertPackageCount(object, 2);

		enableImageMagick();

		object = ingestDelta(SOURCE_PACKAGE_COMPLETE, originalName);

		assertGoodQualityLevel(object);
		assertPackageCount(object, 3);

		disableImageMagick();

		object = ingestDelta(SOURCE_PACKAGE_ONEFILE, originalName);

		assertBadQualityLevel(object);
		assertPackageCount(object, 4);

		enableImageMagick();

		object = recheck(originalName);

		assertGoodQualityLevel(object);
		assertPackageCount(object, 5);

		assertQualityLevelRecheckPackage(object, 5);
	}

	/*
	 * ===================================================================== 5.
	 * Recheck -> neues Delta -> weiterer Recheck
	 * =====================================================================
	 */
	@Test
	public void recheckThenDifferentBadDeltasThenFurtherRechecksRecoverAgain() throws Exception {

		String originalName = uniqueName("Recheck_Delta_Recheck_Delta_Recheck");

		disableImageMagick();

		Object object = ingestInitial(SOURCE_PACKAGE_COMPLETE, originalName);

		assertBadQualityLevel(object);
		assertPackageCount(object, 1);

		enableImageMagick();

		object = recheck(originalName);

		assertGoodQualityLevel(object);
		assertPackageCount(object, 2);
		assertQualityLevelRecheckPackage(object, 2);

		disableImageMagick();

		object = ingestDelta(SOURCE_PACKAGE_NEWTIF, originalName);

		assertBadQualityLevel(object);
		assertPackageCount(object, 3);

		enableImageMagick();

		object = recheck(originalName);

		assertGoodQualityLevel(object);
		assertPackageCount(object, 4);
		assertQualityLevelRecheckPackage(object, 4);

		disableImageMagick();

		object = ingestDelta(SOURCE_PACKAGE_ONEFILE, originalName);

		assertBadQualityLevel(object);
		assertPackageCount(object, 5);

		enableImageMagick();

		object = recheck(originalName);

		assertGoodQualityLevel(object);
		assertPackageCount(object, 6);
		assertQualityLevelRecheckPackage(object, 6);
	}

	/*
	 * ===================================================================== 6.
	 * Mehrere Rechecks ohne Delta
	 * =====================================================================
	 */
	@Ignore
	@Test
	public void repeatedRechecksCreatePackagesAndFollowImageMagickAvailability() throws Exception {

		String originalName = uniqueName("RepeatedRechecks");

		disableImageMagick();

		Object object = ingestInitial(SOURCE_PACKAGE_ONEFILE, originalName);

		assertBadQualityLevel(object);
		assertPackageCount(object, 1);

		enableImageMagick();

		object = recheck(originalName);

		assertGoodQualityLevel(object);
		assertPackageCount(object, 2);
		assertQualityLevelRecheckPackage(object, 2);

		disableImageMagick();

		object = recheck(originalName);

		assertBadQualityLevel(object);
		assertPackageCount(object, 3);
		assertQualityLevelRecheckPackage(object, 3);

		enableImageMagick();

		object = recheck(originalName);

		assertGoodQualityLevel(object);
		assertPackageCount(object, 4);
		assertQualityLevelRecheckPackage(object, 4);
	}

	/*
	 * ===================================================================== 7.
	 * EMPTY-Sonderfaelle
	 * =====================================================================
	 */
	@Ignore
	@Test
	public void emptyDeltaBetweenBadHistoryAndGoodRecheckIsHandled() throws Exception {

		String originalName = uniqueName("BadHistory_EmptyDelta_Recheck");

		disableImageMagick();

		Object object = ingestInitial(SOURCE_PACKAGE_COMPLETE, originalName);

		assertBadQualityLevel(object);
		assertPackageCount(object, 1);

		enableImageMagick();

		object = ingestDelta(SOURCE_PACKAGE_EMPTY, originalName);

		/*
		 * Fuer EMPTY wird hier bewusst kein konkretes Quality-Level vorausgesetzt. Der
		 * Test prueft die Verarbeitbarkeit und den anschliessenden Recheck.
		 */
		assertNotNull(object);
		assertPackageCount(object, 2);

		object = recheck(originalName);

		assertGoodQualityLevel(object);
		assertPackageCount(object, 3);

		assertQualityLevelRecheckPackage(object, 3);
	}
	@Ignore
	@Test
	public void emptyInitialRecheckCreatesMetadataOnlyRepresentations() throws Exception {

		String originalName = uniqueName("EmptyInitial_Recheck");

		enableImageMagick();

		Object object = ingestInitial(SOURCE_PACKAGE_EMPTY, originalName);

		assertNotNull(object);
		assertPackageCount(object, 1);

		object = recheck(originalName);

		assertPackageCount(object, 2);

		File unpackedPackage = retrievePackage(object, 2);

		try {

			assertRepresentationContainsOnlyMetadata(unpackedPackage, "a", true);

			File representationA = findRepresentationFolder(unpackedPackage, "a");

			assertPremisContainsEvent(new File(representationA, PREMIS_FILE), PREMIS_EVENT_QUALITY_LEVEL_RECHECK);

			/*
			 * EMPTY: B soll ebenfalls nur Metadaten enthalten.
			 */
			assertRepresentationContainsOnlyMetadata(unpackedPackage, "b", true);

		} finally {

			FolderUtils.deleteDirectorySafe(unpackedPackage);
		}
	}
	
	@Test
	public void badDeltaThenGoodDeltaThenBadRecheckThenBadDeltaThenGoodRecheck() throws Exception {

		String originalName = uniqueName("BadDelta_GoodDelta_BadRecheck_BadDelta_GoodRecheck");

		/*
		 * pack_1: Guter Ausgangszustand mit COMPLETE.
		 */
		enableImageMagick();

		Object object = ingestInitial(SOURCE_PACKAGE_COMPLETE, originalName);

		assertGoodQualityLevel(object);
		assertPackageCount(object, 1);

		/*
		 * pack_2: NEWTIF kommt rein, waehrend ImageMagick fehlt.
		 */
		disableImageMagick();

		object = ingestDelta(SOURCE_PACKAGE_NEWTIF, originalName);

		assertBadQualityLevel(object);
		assertPackageCount(object, 2);

		/*
		 * pack_3: ImageMagick ist wieder da. Bewusst anderes Delta: ONEFILE.
		 */
		enableImageMagick();

		object = ingestDelta(SOURCE_PACKAGE_ONEFILE, originalName);

		assertGoodQualityLevel(object);
		assertPackageCount(object, 3);

		/*
		 * pack_4: Jetzt faellt ImageMagick wieder aus und wir machen bewusst einen
		 * SCHLECHTEN QualityLevelRecheck.
		 */
		disableImageMagick();

		object = recheck(originalName);

		assertBadQualityLevel(object);
		assertPackageCount(object, 4);

		assertQualityLevelRecheckPackage(object, 4);

		/*
		 * pack_5: Noch waehrend ImageMagick fehlt kommt ein weiteres, diesmal anderes
		 * Delta rein.
		 */
		object = ingestDelta(SOURCE_PACKAGE_COMPLETE, originalName);

		assertBadQualityLevel(object);
		assertPackageCount(object, 5);

		/*
		 * pack_6: ImageMagick kommt zurueck und ein zweiter Recheck soll die komplette
		 * Historie wieder auf GOOD bringen.
		 */
		enableImageMagick();

		object = recheck(originalName);

		assertGoodQualityLevel(object);
		assertPackageCount(object, 6);

		assertQualityLevelRecheckPackage(object, 6);
	}
	/*
	 * ===================================================================== 8.
	 * Expliziter Konvertierungspfad
	 * =====================================================================
	 */
	
	@Test
	public void newTifRecheckWithImageMagickCreatesPayloadInRepresentationB() throws Exception {

		String originalName = uniqueName("NewTif_Conversion_Recheck");

		disableImageMagick();

		Object object = ingestInitial(SOURCE_PACKAGE_NEWTIF, originalName);

		assertBadQualityLevel(object);
		assertPackageCount(object, 1);

		enableImageMagick();

		object = recheck(originalName);

		assertGoodQualityLevel(object);
		assertPackageCount(object, 2);

		File unpackedPackage = retrievePackage(object, 2);

		try {

			assertRepresentationContainsOnlyMetadata(unpackedPackage, "a", true);

			File representationA = findRepresentationFolder(unpackedPackage, "a");

			assertPremisContainsEvent(new File(representationA, PREMIS_FILE), PREMIS_EVENT_QUALITY_LEVEL_RECHECK);

			File representationB = findRepresentationFolder(unpackedPackage, "b");

			assertTrue("premis.xml fehlt in Representation B", new File(representationB, PREMIS_FILE).isFile());

			/*
			 * NEWTIF ist als gezielter Konvertierungsfall gedacht.
			 */
			assertRepresentationContainsPayload(representationB);

		} finally {

			FolderUtils.deleteDirectorySafe(unpackedPackage);
		}
	}

	/*
	 * ===================================================================== Helper
	 * =====================================================================
	 */

	private String uniqueName(String scenario) {

		return "ATQualityLevelRecheck_" + scenario + "_" + UUID.randomUUID().toString();
	}

	private Object ingestInitial(String sourcePackage, String originalName) throws IOException {

		return ingestPackage(sourcePackage, originalName);
	}

	private Object ingestDelta(String sourcePackage, String originalName) throws Exception {

		Object before = ath.getObject(originalName);

		assertNotNull(before);

		int expectedPackageCount = before.getPackages().size() + 1;

		System.out.println("Ingesting DELTA " + sourcePackage + " as " + originalName);

		ath.putSIPtoIngestArea(sourcePackage, C.FILE_EXTENSION_TGZ, originalName);

		/*
		 * Ganz wichtig: Erst warten, bis das neue Package ueberhaupt registriert wurde.
		 *
		 * Sonst kann awaitObjectState(100) sofort auf dem alten Zustand zurueckkehren.
		 */
		awaitPackageCount(originalName, expectedPackageCount);

		/*
		 * Jetzt warten wir auf den Abschluss DIESES Workflows.
		 */
		ath.awaitObjectState(originalName, Object.ObjectStatus.ArchivedAndValidAndNotInWorkflow);

		Object after = ath.getObject(originalName);

		assertNotNull(after);

		assertPackageCount(after, expectedPackageCount);

		return after;
	}

	private Object ingestPackage(String sourcePackage, String originalName) throws IOException {

		System.out.println("Ingesting " + sourcePackage + " as " + originalName);

		ath.putSIPtoIngestArea(sourcePackage, C.FILE_EXTENSION_TGZ, originalName);

		ath.awaitObjectState(originalName, Object.ObjectStatus.ArchivedAndValidAndNotInWorkflow);

		Object object = ath.getObject(originalName);

		assertNotNull("Objekt wurde nach dem Ingest nicht gefunden: " + originalName, object);

		return object;
	}

	private void awaitPackageCount(String originalName, int expectedPackageCount) throws Exception {

		long timeout = System.currentTimeMillis() + 120000;

		while (System.currentTimeMillis() < timeout) {

			Object object = ath.getObject(originalName);

			if (object != null && object.getPackages().size() >= expectedPackageCount) {

				return;
			}

			Thread.sleep(500);
		}

		throw new AssertionError(
				"Timeout: Package-Anzahl " + expectedPackageCount + " wurde fuer " + originalName + " nicht erreicht");
	}

	private Object recheck(String originalName) throws Exception {

		triggerQualityLevelRecheck(originalName);

		ath.awaitObjectState(originalName, Object.ObjectStatus.ArchivedAndValidAndNotInWorkflow);

		Object object = ath.getObject(originalName);

		assertNotNull("Objekt wurde nach QualityLevelRecheck nicht gefunden: " + originalName, object);

		return object;
	}

	private void triggerQualityLevelRecheck(String originalName) throws Exception {

		Object object = ath.getObject(originalName);

		assertNotNull("Objekt fuer QualityLevelRecheck nicht gefunden: " + originalName, object);

		Session session = HibernateUtil.openSession();

		Transaction tx = session.beginTransaction();

		try {

			Object managedObject = (Object) session.get(Object.class, object.getData_pk());

			assertNotNull("Objekt konnte nicht aus Hibernate geladen werden", managedObject);

			managedObject.setQualityLevelRecheck_flag(true);

			tx.commit();

		} catch (Exception e) {

			tx.rollback();
			throw e;

		} finally {

			session.close();
		}

		QualityLevelRecheckEvent event = new QualityLevelRecheckEvent();

		boolean queued = event.implementation();

		assertTrue("QualityLevelRecheckEvent hat keinen Job erzeugt fuer " + originalName, queued);
	}

	private void assertGoodQualityLevel(Object object) {

		int qualityLevel = object.getQuality_flag();

		assertTrue(
				"Unerwartetes Quality Level: " + qualityLevel + " (erwartet " + GOOD_QUALITY_LEVEL_1 + " oder "
						+ GOOD_QUALITY_LEVEL_2 + ")",
				qualityLevel == GOOD_QUALITY_LEVEL_1 || qualityLevel == GOOD_QUALITY_LEVEL_2);
	}

	private void assertBadQualityLevel(Object object) {

		assertEquals("Unerwartetes Quality Level bei nicht verfuegbarem ImageMagick", BAD_QUALITY_LEVEL,
				object.getQuality_flag());
	}

	private void assertPackageCount(Object object, int expectedPackageCount) {

		assertEquals("Unerwartete Package-Anzahl", expectedPackageCount, object.getPackages().size());
	}

	/**
	 * Standardpruefung fuer jedes Recheck-Package.
	 */
	private void assertQualityLevelRecheckPackage(Object object, int packageNumber) throws Exception {

		File unpackedPackage = retrievePackage(object, packageNumber);

		try {

			/*
			 * A darf beim Recheck keine Nutzdaten enthalten.
			 */
			assertRepresentationContainsOnlyMetadata(unpackedPackage, "a", true);

			File representationA = findRepresentationFolder(unpackedPackage, "a");

			assertPremisContainsEvent(new File(representationA, PREMIS_FILE), PREMIS_EVENT_QUALITY_LEVEL_RECHECK);

			/*
			 * B kann Nutzdaten enthalten oder leer bleiben, muss aber seine
			 * PREMIS-Metadaten besitzen.
			 */
			File representationB = findRepresentationFolder(unpackedPackage, "b");

			assertTrue("premis.xml fehlt in Representation B", new File(representationB, PREMIS_FILE).isFile());

		} finally {

			FolderUtils.deleteDirectorySafe(unpackedPackage);
		}
	}

	/**
	 * Erlaubt ausschliesslich premis.xml und optional export_mets.xml.
	 */
	private void assertRepresentationContainsOnlyMetadata(File unpackedPackage, String representationSuffix,
			boolean requirePremis) {

		File representation = findRepresentationFolder(unpackedPackage, representationSuffix);

		File[] files = representation.listFiles();

		assertNotNull("Representation " + representationSuffix + " konnte nicht gelesen werden", files);

		Set<String> actualFiles = new HashSet<String>();

		for (File file : files) {

			assertTrue("In Representation " + representationSuffix + " darf kein Unterverzeichnis liegen: "
					+ file.getAbsolutePath(), file.isFile());

			actualFiles.add(file.getName());
		}

		Set<String> allowedFiles = new HashSet<String>();

		allowedFiles.add(PREMIS_FILE);

		allowedFiles.add(EXPORT_METS_FILE);

		assertTrue("Unerwartete Dateien in Representation " + representationSuffix + ": " + actualFiles,
				allowedFiles.containsAll(actualFiles));

		if (requirePremis) {

			assertTrue("premis.xml fehlt in Representation " + representationSuffix, actualFiles.contains(PREMIS_FILE));
		}
	}

	/**
	 * Fuer NEWTIF: Neben premis.xml / export_mets.xml muss in B mindestens eine
	 * weitere Datei vorhanden sein.
	 */
	private void assertRepresentationContainsPayload(File representation) {

		File[] files = representation.listFiles();

		assertNotNull("Representation B konnte nicht gelesen werden", files);

		boolean payloadFound = false;

		for (File file : files) {

			if (!file.isFile()) {
				continue;
			}

			String fileName = file.getName();

			if (!PREMIS_FILE.equals(fileName) && !EXPORT_METS_FILE.equals(fileName)) {

				payloadFound = true;
				break;
			}
		}

		assertTrue("Representation B enthaelt keine erzeugte Nutzdatei / kein Derivat", payloadFound);
	}

	private void assertPremisContainsEvent(File premisFile, String expectedEventType) {

		assertTrue("premis.xml fehlt: " + premisFile, premisFile.isFile());

		SAXBuilder builder = XMLUtils.createValidatingSaxBuilder();

		Document doc;

		try {

			doc = builder.build(premisFile);

		} catch (Exception e) {

			throw new RuntimeException("PREMIS konnte nicht gelesen werden: " + premisFile, e);
		}

		Element rootElement = doc.getRootElement();

		Namespace ns = rootElement.getNamespace();

		@SuppressWarnings("unchecked")
		List<Element> eventElements = rootElement.getChildren("event", ns);

		boolean found = false;

		for (Element event : eventElements) {

			String eventType = event.getChildText("eventType", ns);

			if (expectedEventType.equals(eventType)) {

				found = true;
				break;
			}
		}

		assertTrue("PREMIS enthaelt kein Event vom Typ " + expectedEventType, found);
	}

	private File retrievePackage(Object object, int packageNumber) throws IOException {

		File targetFolder = new File("/tmp/ATQualityLevelRecheck-" + object.getIdentifier() + "-pack_" + packageNumber);

		ath.retrieveAIP(object, targetFolder, String.valueOf(packageNumber));

		return targetFolder;
	}

	private File findRepresentationFolder(File unpackedPackage, String suffix) {

		File dataFolder = new File(unpackedPackage, "data");

		assertTrue("data-Verzeichnis fehlt: " + dataFolder, dataFolder.isDirectory());

		File[] representations = dataFolder.listFiles();

		assertNotNull("data-Verzeichnis konnte nicht gelesen werden", representations);

		File found = null;

		for (File file : representations) {

			if (file.isDirectory() && file.getName().endsWith(suffix)) {

				assertTrue("Mehr als eine Representation endet auf " + suffix, found == null);

				found = file;
			}
		}

		assertNotNull("Keine Representation mit Suffix " + suffix + " gefunden", found);

		return found;
	}

	private void disableImageMagick() throws IOException {

		if (IMAGEMAGICK_DISABLED_FLAG.exists()) {
			return;
		}

		if (!IMAGEMAGICK_DISABLED_FLAG.createNewFile()) {

			throw new IOException("ImageMagick-Flag konnte nicht erstellt werden: " + IMAGEMAGICK_DISABLED_FLAG);
		}
	}

	private void enableImageMagick() throws IOException {

		if (!IMAGEMAGICK_DISABLED_FLAG.exists()) {
			return;
		}

		if (!IMAGEMAGICK_DISABLED_FLAG.delete()) {

			throw new IOException("ImageMagick-Flag konnte nicht entfernt werden: " + IMAGEMAGICK_DISABLED_FLAG);
		}
	}
}
