package de.uzk.hki.da.metadata;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import org.jaxen.JaxenException;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.junit.Test;

import de.uzk.hki.da.utils.Path;
import de.uzk.hki.da.utils.RelativePath;
import de.uzk.hki.da.utils.XMLUtils;

public class EadParserTests {
	
	private static final Path WORK_AREA_ROOT_PATH = new RelativePath("src/test/resources/metadata/");
	private static String ddbEad = "NewDDBEad.XML";
	private static File ddbEadFile = Path.makeFile(WORK_AREA_ROOT_PATH, ddbEad);
	
	private static File publishableEadFile = Path.makeFile(
			WORK_AREA_ROOT_PATH, "NewDDBEadPublishable.XML");
	private static File notPublishableEadFile = Path.makeFile(
			WORK_AREA_ROOT_PATH, "NewDDBEadNotPublishable.XML");
	private static File noDigitalObjectsEadFile = Path.makeFile(
			WORK_AREA_ROOT_PATH, "NewDDBEadNoDigitalObjects.XML");
	
	
	@Test
	public void testParsingReferencesFromEad() throws JDOMException, IOException, JaxenException {

		String metsReference1 = "mets_1_280920.xml";
		String metsReference2 = "mets_2_32042.xml";
		
		boolean metsReference1exists = false;
		boolean metsReference2exists = false;
		
		SAXBuilder builder = XMLUtils.createValidatingSaxBuilder();
		FileReader fr1 = new FileReader(ddbEadFile);
		Document ddbEadDoc = builder.build(fr1);
		EadParser ep = new EadParser(ddbEadDoc);
		List<String> references = ep.getReferences();
		
		assertTrue(references.size()==2);
		
		for(String r : references) {
			if(r.equals(metsReference1)) {
				metsReference1exists = true;
			} else if(r.equals(metsReference2)) {
				metsReference2exists = true;
			}
		}
		assertTrue(metsReference1exists&&metsReference2exists);
	}	

	@Test
	public void testPublishableEadIsPublishable()
			throws JDOMException, IOException, JaxenException {
		SAXBuilder builder = XMLUtils.createValidatingSaxBuilder();
		FileReader fr = new FileReader(publishableEadFile);
		Document doc = builder.build(fr);
		EadParser ep = new EadParser(doc);

		assertTrue(ep.isPublishable());

		List<EadLicenseCheck> checks = ep.getLicensesForWholeEAD();
		assertTrue(checks.size() == 2);
		for (EadLicenseCheck check : checks) {
			assertTrue(EadLicenseCheck.isValidForPublication(check));
		}
	}

	@Test
	public void testNotPublishableEadIsNotPublishable()
			throws JDOMException, IOException, JaxenException {
		SAXBuilder builder = XMLUtils.createValidatingSaxBuilder();
		FileReader fr = new FileReader(notPublishableEadFile);
		Document doc = builder.build(fr);
		EadParser ep = new EadParser(doc);

		assertFalse(ep.isPublishable());

		List<EadLicenseCheck> checks = ep.getLicensesForWholeEAD();
		assertTrue(checks.size() == 2);

		boolean sawUnpublishableUnit = false;
		for (EadLicenseCheck check : checks) {
			if (!EadLicenseCheck.isValidForPublication(check)) {
				sawUnpublishableUnit = true;
				assertTrue("B 100/2".equals(check.getUnitId()));
			}
		}
		assertTrue(sawUnpublishableUnit);
	}

	@Test
	public void testEadWithoutDigitalObjectsIsNotPublishable()
			throws JDOMException, IOException, JaxenException {
		SAXBuilder builder = XMLUtils.createValidatingSaxBuilder();
		FileReader fr = new FileReader(noDigitalObjectsEadFile);
		Document doc = builder.build(fr);
		EadParser ep = new EadParser(doc);

		assertTrue(ep.getLicensesForWholeEAD().isEmpty());
		assertFalse(ep.isPublishable());
	}

	@Test
	public void testHasInvalidLicenseForPublication()
			throws JDOMException, IOException, JaxenException {
		SAXBuilder builder = XMLUtils.createValidatingSaxBuilder();

		FileReader publishableFr = new FileReader(publishableEadFile);
		EadParser publishable = new EadParser(builder.build(publishableFr));
		assertFalse(publishable.hasInvalidLicenseForPublication());

		FileReader notPublishableFr = new FileReader(notPublishableEadFile);
		EadParser notPublishable = new EadParser(builder.build(notPublishableFr));
		assertTrue(notPublishable.hasInvalidLicenseForPublication());

		FileReader noDigitalObjectsFr = new FileReader(noDigitalObjectsEadFile);
		EadParser noDigitalObjects = new EadParser(
				builder.build(noDigitalObjectsFr));
		assertFalse(noDigitalObjects.hasInvalidLicenseForPublication());
	}

	@Test
	public void testLicenseValidator() {
		assertTrue(LicenseValidator.isValidForPublication(
				"https://creativecommons.org/publicdomain/zero/1.0/"));
		assertTrue(LicenseValidator.isValidForPublication(
				"http://creativecommons.org/licenses/by/4.0"));
		assertTrue(LicenseValidator.isValidForPublication(
				"http://creativecommons.org/licenses/by-nc-sa/3.0/"));
		assertFalse(LicenseValidator.isValidForPublication(
				"http://rightsstatements.org/vocab/InC/1.0/"));
		assertFalse(LicenseValidator.isValidForPublication(null));
		assertFalse(LicenseValidator.isValidForPublication(""));
	}
	
//	@Test
//	public void testReferences() throws JDOMException, IOException {
//		SAXBuilder builder = XMLUtils.createNonvalidatingSaxBuilder();
//		FileReader fr1 = new FileReader(ddbEadFile);
//		Document ddbEadDoc = builder.build(fr1);
//		EadParser ep = new EadParser(ddbEadDoc);
//		List<String> references = ep.getReferences();
//		for(String ref : references) {
//			ep.getMetsFileFromPIPHref(ref);
//		}
//		
//	}
}
