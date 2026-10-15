package de.uzk.hki.da.metadata;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.jaxen.JaxenException;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.junit.Test;

import de.uzk.hki.da.utils.C;
import de.uzk.hki.da.utils.Path;
import de.uzk.hki.da.utils.RelativePath;
import de.uzk.hki.da.utils.XMLUtils;

/**
 * 
 * @author Eugen Trebunski
 *
 */
public class MetsParserTest {
		
	private static final Path WORK_AREA_ROOT_PATH = new RelativePath("src/test/resources/metadata/");
	private static String lavMets = "MetsLav.xml";
	private static String ulbsMets = "MetsUlbms.xml";
	private static String ulbsMetsPartTitle = "MetsUlbmsPartTitle.xml";
	private static String licenseMets = "MetsLicense.xml";
	private static String httpLicenseMets = "MetsLicenseHttp.xml";
	private static String noLicenseMets = "MetsNoLicense.xml";
	private static File lavMetsFile = Path.makeFile(WORK_AREA_ROOT_PATH, lavMets);
	private static File ulbMetsFile = Path.makeFile(WORK_AREA_ROOT_PATH, ulbsMets);
	private static File ulbMetsFilePartTitle = Path.makeFile(WORK_AREA_ROOT_PATH, ulbsMetsPartTitle);

	private static File licenseMetsFile = Path.makeFile(WORK_AREA_ROOT_PATH, licenseMets);
	private static File httpLicenseMetsFile = Path.makeFile(WORK_AREA_ROOT_PATH, httpLicenseMets);
	private static File differentLicenseMultiMetsFile = Path.makeFile(WORK_AREA_ROOT_PATH, "MetsMultiLevelDifferentLicense.xml");
	private static File sameLicenseMultiMetsFile = Path.makeFile(WORK_AREA_ROOT_PATH, "MetsMultiLevelSameLicense.xml");
	private static File noLicenseMultiMetsFile = Path.makeFile(WORK_AREA_ROOT_PATH, "MetsMultiLevelNoLicense.xml");
	private static File noLicenseMetsFile = Path.makeFile(WORK_AREA_ROOT_PATH, noLicenseMets);
	
	
	@Test
	public void testGetIndexInfoFromLavMets() throws JDOMException, IOException, JaxenException {
		
		SAXBuilder builder = XMLUtils.createValidatingSaxBuilder();
		FileReader fr1 = new FileReader(lavMetsFile);
		Document lavMets = builder.build(fr1);
		MetsParser mp = new MetsParser(lavMets);
		
		assertTrue(mp.getReferences().contains("http://data.danrw.de/file/1-2015110919/_6d8889c54cd506f75be230bd630cd70d.jpg")
				&& mp.getReferences().contains("http://data.danrw.de/file/1-2015110919/_03a62f462ff81be6d6db280be6a8e4c6.jpg")
				&& mp.getReferences().contains("http://data.danrw.de/file/1-2015110919/_f1b9c8446da0b8b14c10061a3a81949c.jpg")
				&& mp.getReferences().contains("http://data.danrw.de/file/1-2015110919/_d88477ff445fe329a2dac01d3fddc7a8.jpg")
				&& mp.getReferences().contains("http://data.danrw.de/file/1-2015110919/_2fee28672e542ff0a12556552d059cc6.jpg")
				&& mp.getReferences().contains("_MD5hashes.txt")
				&& mp.getReferences().contains("_ggsg_0001.xml"));
		
		HashMap<String, HashMap<String, List<String>>> indexInfo = mp.getIndexInfo("Test-Object-Id");
		
		assertTrue(indexInfo.entrySet().size()==1);
		HashMap<String, List<String>> elements = indexInfo.get("Test-Object-Id-dmd00016");
		
		assertTrue(elements.get(C.EDM_TITLE).size()==1 
				&& elements.get(C.EDM_TITLE).get(0).equals("Nr. 1111"));
		
		assertTrue(elements.get(C.EDM_IDENTIFIER).contains("1111") 
				&& elements.get(C.EDM_IDENTIFIER).contains("urn:nbn:de:danrw:de111-11111-1111-11111-1111-1111"));
		
		assertTrue(elements.get(C.EDM_DATA_PROVIDER).size()==1 
				&& (elements.get(C.EDM_DATA_PROVIDER).get(0).equals("Landesarchiv NRW")));
	
	}
	
	@Test
	public void testGetIndexInfoFromUlbMets() throws JDOMException, IOException {
		
		SAXBuilder builder = XMLUtils.createValidatingSaxBuilder();
		FileReader fr1 = new FileReader(ulbMetsFile);
		Document lavMets = builder.build(fr1);
		MetsParser mp = new MetsParser(lavMets);
		
		HashMap<String, HashMap<String, List<String>>> indexInfo = mp.getIndexInfo("danrw801613");
		assertTrue(indexInfo.entrySet().size()==1);
		HashMap<String, List<String>> elements = indexInfo.get("danrw801613-md801613");
		
		assertEquals(elements.get(C.EDM_TITLE).get(0), "nonSortText"+" "+"Text// mahels///Titel");
		
		assertTrue(elements.containsKey(C.EDM_ALTERNATIVE));
		assertEquals(elements.get(C.EDM_ALTERNATIVE).get(0), "Untertitel");
		
		assertTrue(elements.containsKey(C.EDM_TYPE));
		assertEquals(elements.get(C.EDM_TYPE).get(0), C.EDM_TYPE_TEXT);
		assertTrue(elements.containsKey(C.EDM_LANGUAGE)
				&& elements.get(C.EDM_LANGUAGE).contains("ger"));
		
		assertTrue(elements.get(C.EDM_PUBLISHER).size()==2);
		assertTrue(elements.get(C.EDM_PUBLISHER).contains("Grimm]")
				&& elements.get(C.EDM_PUBLISHER).contains("ULB"));
		
		assertTrue(elements.get(C.EDM_IDENTIFIER).contains("id42")
				&& elements.get(C.EDM_IDENTIFIER).contains("urn:nbn:de:hbz:42")
				&& elements.get(C.EDM_IDENTIFIER).contains("urn:nbn:de:hbz:6:1-27094"));
		
		assertTrue(elements.get(C.EDM_CREATOR).contains("Nachname, Vorname"));
		
		assertTrue(elements.get(C.EDM_DATA_PROVIDER).contains("ULB"));
		
		assertTrue(elements.get(C.EDM_OBJECT).contains("image/801622.bmp"));
		
		System.out.println();
	}
	
	@Test
	public void testGetIndexInfoFromUlbMetsPartTitle() throws JDOMException, IOException {
		
		SAXBuilder builder = XMLUtils.createValidatingSaxBuilder();
		FileReader fr1 = new FileReader(ulbMetsFilePartTitle);
		Document lavMets = builder.build(fr1);
		MetsParser mp = new MetsParser(lavMets);
		
		HashMap<String, HashMap<String, List<String>>> indexInfo = mp.getIndexInfo("danrw801613");
		assertTrue(indexInfo.entrySet().size()==1);
		HashMap<String, List<String>> elements = indexInfo.get("danrw801613-md801613");
		System.out.println("##### title: " +elements.get(C.EDM_TITLE).get(0));
		assertEquals(elements.get(C.EDM_TITLE).get(0), "2019," + " " + "Nr." + " " + "05");
				
		assertTrue(elements.get(C.EDM_PUBLISHER).size()==2);
		assertTrue(elements.get(C.EDM_PUBLISHER).contains("Grimm]")
				&& elements.get(C.EDM_PUBLISHER).contains("ULB"));
		
		assertTrue(elements.get(C.EDM_IDENTIFIER).contains("id42")
				&& elements.get(C.EDM_IDENTIFIER).contains("urn:nbn:de:hbz:42")
				&& elements.get(C.EDM_IDENTIFIER).contains("urn:nbn:de:hbz:6:1-27094"));
		
		assertTrue(elements.get(C.EDM_CREATOR).contains("Nachname, Vorname"));
		
		assertTrue(elements.get(C.EDM_DATA_PROVIDER).contains("ULB"));
		
		assertTrue(elements.get(C.EDM_OBJECT).contains("image/801622.bmp"));
		
		System.out.println();
	}
	
	@Test
	public void testReadAccessConditionNonExist()throws JDOMException, IOException{
		SAXBuilder builder = XMLUtils.createValidatingSaxBuilder();
		FileReader fr1 = new FileReader(noLicenseMetsFile);
		Document lavMets = builder.build(fr1);
		MetsParser mp = new MetsParser(lavMets);
		
		assertEquals(null,mp.getLicensesForWholeMets().get(0));
		assertTrue(mp.getIndexInfo("Test-Object-Id").get("Test-Object-Id-md2684319").get(C.EDM_RIGHTS).isEmpty());
		assertTrue(mp.getIndexInfo("Test-Object-Id").get("Test-Object-Id-md2684319").get(C.DC_RIGHTS).isEmpty());
	}
	
	@Test
	public void testReadAccessConditionExist()throws JDOMException, IOException{
		MetsLicense mLicense=new MetsLicense("use and reproduction","https://creativecommons.org/publicdomain/mark/1.0/","Public Domain Mark 1.0","pdm");
		SAXBuilder builder = XMLUtils.createValidatingSaxBuilder();
		FileReader fr1 = new FileReader(licenseMetsFile);
		Document lavMets = builder.build(fr1);
		MetsParser mp = new MetsParser(lavMets);
		
		assertEquals(mLicense,mp.getLicensesForWholeMets().get(0));
		assertEquals(MetsParser.PDM_MARK_URI,mp.getIndexInfo("Test-Object-Id").get("Test-Object-Id-md1937630").get(C.EDM_RIGHTS).get(0));
		assertEquals(mLicense.getDisplayLabel(),mp.getIndexInfo("Test-Object-Id").get("Test-Object-Id-md1937630").get(C.DC_RIGHTS).get(0));
	}
	
	@Test
	public void testReadHttpAccessConditionExists()throws JDOMException, IOException{
		MetsLicense mLicense=new MetsLicense("use and reproduction","http://creativecommons.org/publicdomain/mark/1.0/","Public Domain Mark 1.0","pdm");
		SAXBuilder builder = XMLUtils.createValidatingSaxBuilder();
		FileReader fr1 = new FileReader(httpLicenseMetsFile);
		Document lavMets = builder.build(fr1);
		MetsParser mp = new MetsParser(lavMets);
		
		assertEquals(mLicense,mp.getLicensesForWholeMets().get(0));
		assertEquals(MetsParser.PDM_MARK_URI,mp.getIndexInfo("Test-Object-Id").get("Test-Object-Id-md1937630").get(C.EDM_RIGHTS).get(0));
		assertEquals(mLicense.getDisplayLabel(),mp.getIndexInfo("Test-Object-Id").get("Test-Object-Id-md1937630").get(C.DC_RIGHTS).get(0));
	}
	
	
		@Test
	public void testReadAccessConditionInMultilevelMETSDifferent()throws JDOMException, IOException{
			MetsLicense mLicense=new MetsLicense("use and reproduction","https://creativecommons.org/publicdomain/mark/1.0/","Public Domain Mark 1.0","pdm");
			SAXBuilder builder = XMLUtils.createValidatingSaxBuilder();
			FileReader fr1 = new FileReader(differentLicenseMultiMetsFile);
			Document lavMets = builder.build(fr1);
			MetsParser mp = new MetsParser(lavMets);
			//md1616184 has accessCondition
			//md1617166 has no accessCondition
			try{
				assertEquals(mLicense,mp.getLicensesForWholeMets().get(0)); //throws Exception
			}catch(RuntimeException e){
				assertTrue(e.getMessage().contains("null"));
				assertTrue(e.getMessage().contains(mLicense.toString()));
			}

assertEquals(MetsParser.PDM_MARK_URI,mp.getIndexInfo("Test-Object-Id").get("Test-Object-Id-md1616184").get(C.EDM_RIGHTS).get(0));
		assertTrue(mp.getIndexInfo("Test-Object-Id").get("Test-Object-Id-md1617166").get(C.EDM_RIGHTS).isEmpty());;
			
			assertEquals(mLicense.getDisplayLabel(),mp.getIndexInfo("Test-Object-Id").get("Test-Object-Id-md1616184").get(C.DC_RIGHTS).get(0));
			assertTrue(mp.getIndexInfo("Test-Object-Id").get("Test-Object-Id-md1617166").get(C.DC_RIGHTS).isEmpty());;
		
		}
		
		@Test
		public void testReadAccessConditionInMultilevelMETSNo()throws JDOMException, IOException{
			MetsLicense mLicense=new MetsLicense("use and reproduction","https://creativecommons.org/publicdomain/mark/1.0/","Public Domain Mark 1.0","pdm");
			SAXBuilder builder = XMLUtils.createValidatingSaxBuilder();
			FileReader fr1 = new FileReader(noLicenseMultiMetsFile);
			Document lavMets = builder.build(fr1);
			MetsParser mp = new MetsParser(lavMets);
			
			assertEquals(null,mp.getLicensesForWholeMets().get(0));
			assertTrue(mp.getIndexInfo("Test-Object-Id").get("Test-Object-Id-md1616184").get(C.EDM_RIGHTS).isEmpty());
			assertTrue(mp.getIndexInfo("Test-Object-Id").get("Test-Object-Id-md1617166").get(C.EDM_RIGHTS).isEmpty());
			assertTrue(mp.getIndexInfo("Test-Object-Id").get("Test-Object-Id-md1616184").get(C.DC_RIGHTS).isEmpty());
			assertTrue(mp.getIndexInfo("Test-Object-Id").get("Test-Object-Id-md1617166").get(C.DC_RIGHTS).isEmpty());
		}
		
	
	
	@Test
	public void testReadAccessConditionInMultilevelMETSSame()throws JDOMException, IOException{
		MetsLicense mLicense=new MetsLicense("use and reproduction","https://creativecommons.org/publicdomain/mark/1.0/","Public Domain Mark 1.0","pdm");
		SAXBuilder builder = XMLUtils.createValidatingSaxBuilder();
		FileReader fr1 = new FileReader(sameLicenseMultiMetsFile);
		Document lavMets = builder.build(fr1);
		MetsParser mp = new MetsParser(lavMets);
		
		assertEquals(mLicense,mp.getLicensesForWholeMets().get(0));
		assertEquals(MetsParser.PDM_MARK_URI,mp.getIndexInfo("Test-Object-Id").get("Test-Object-Id-md1616184").get(C.EDM_RIGHTS).get(0));
		assertEquals(MetsParser.PDM_MARK_URI,mp.getIndexInfo("Test-Object-Id").get("Test-Object-Id-md1617166").get(C.EDM_RIGHTS).get(0));
		assertEquals(mLicense.getDisplayLabel(),mp.getIndexInfo("Test-Object-Id").get("Test-Object-Id-md1616184").get(C.DC_RIGHTS).get(0));
		assertEquals(mLicense.getDisplayLabel(),mp.getIndexInfo("Test-Object-Id").get("Test-Object-Id-md1617166").get(C.DC_RIGHTS).get(0));
	}

	@Test
	public void testSubOrderTitleFromStructMapLabel() throws JDOMException, IOException {
		SAXBuilder builder = XMLUtils.createValidatingSaxBuilder();
		FileReader fr1 = new FileReader(Path.makeFile(WORK_AREA_ROOT_PATH, "MetsHierarchy.xml"));
		Document metsDoc = builder.build(fr1);
		MetsParser mp = new MetsParser(metsDoc);
		
		HashMap<String, HashMap<String, List<String>>> indexInfo = mp.getIndexInfo("obj");
		// root div level: no CONTENTIDS, title comes from titleInfo
		assertEquals("Haupttitel", indexInfo.get("obj-mdRoot").get(C.EDM_TITLE).get(0));
		// sub order div level: identified via CONTENTIDS, title from div@LABEL
		assertEquals("Kind-Titel", indexInfo.get("obj-mdSub").get(C.EDM_TITLE).get(0));
	}

	@Test
	public void testImageTeaserAsIsShownBy() throws JDOMException, IOException {
		SAXBuilder builder = XMLUtils.createValidatingSaxBuilder();
		FileReader fr1 = new FileReader(Path.makeFile(WORK_AREA_ROOT_PATH, "MetsImageTeaser.xml"));
		Document metsDoc = builder.build(fr1);
		MetsParser mp = new MetsParser(metsDoc);
		
		HashMap<String, HashMap<String, List<String>>> indexInfo = mp.getIndexInfo("obj");
		// volume div (identified via DMDID, its CONTENTIDS differs from the root URN)
		// -> isShownBy = IMG_TEASER href of its own teaser
		assertTrue(indexInfo.get("obj-mdVolume").get(C.EDM_IS_SHOWN_BY).contains("http://example.org/webcache/304/11589950"));
		// periodical div has its own IMG_TEASER fptr -> its own teaser is used
		assertTrue(indexInfo.get("obj-mdPeriodical").get(C.EDM_IS_SHOWN_BY).contains("http://example.org/webcache/304/11545390"));
	}

	@Test
	public void testImageTeaserMissingFptrOmitsIsShownBy() throws JDOMException, IOException {
		SAXBuilder builder = XMLUtils.createValidatingSaxBuilder();
		FileReader fr1 = new FileReader(Path.makeFile(WORK_AREA_ROOT_PATH, "MetsImageTeaserNoTeaserFptr.xml"));
		Document metsDoc = builder.build(fr1);
		MetsParser mp = new MetsParser(metsDoc);
		
		HashMap<String, HashMap<String, List<String>>> indexInfo = mp.getIndexInfo("obj");
		// volume div has no IMG_TEASER fptr although the METS uses IMG_TEASER files
		// -> edm:isShownBy is omitted
		assertTrue(indexInfo.get("obj-mdVolume").get(C.EDM_IS_SHOWN_BY) == null
				|| indexInfo.get("obj-mdVolume").get(C.EDM_IS_SHOWN_BY).isEmpty());
		// edm:object remains untouched (still the default first reference from the TEASER grp)
		assertTrue(indexInfo.get("obj-mdVolume").get(C.EDM_OBJECT).contains("http://example.org/webcache/304/11589950"));
	}

	@Test
	public void testIndexInfoFilteredByContentIdsUrn() throws JDOMException, IOException {
		SAXBuilder builder = XMLUtils.createValidatingSaxBuilder();
		FileReader fr1 = new FileReader(Path.makeFile(WORK_AREA_ROOT_PATH, "MetsImageTeaser.xml"));
		Document metsDoc = builder.build(fr1);
		MetsParser mp = new MetsParser(metsDoc);
		
		// volume div identified via CONTENTIDS matching the urn -> only this section is exported
		HashMap<String, HashMap<String, List<String>>> indexInfo = mp.getIndexInfo("obj", "urn:nbn:de:hbz:061:1-1024024");
		assertEquals(1, indexInfo.size());
		assertTrue(indexInfo.containsKey("obj-mdVolume"));
		assertFalse(indexInfo.containsKey("obj-mdPeriodical"));
	}

	@Test
	public void testIndexInfoWithoutMatchingContentIdsKeepsAllSections() throws JDOMException, IOException {
		SAXBuilder builder = XMLUtils.createValidatingSaxBuilder();
		FileReader fr1 = new FileReader(Path.makeFile(WORK_AREA_ROOT_PATH, "MetsImageTeaser.xml"));
		Document metsDoc = builder.build(fr1);
		MetsParser mp = new MetsParser(metsDoc);
		
		// no div matches the urn -> previous behavior: all sections are kept
		HashMap<String, HashMap<String, List<String>>> indexInfo = mp.getIndexInfo("obj", "urn:nbn:de:hbz:061:1-999999");
		assertEquals(2, indexInfo.size());
		assertTrue(indexInfo.containsKey("obj-mdVolume"));
		assertTrue(indexInfo.containsKey("obj-mdPeriodical"));
	}

	@Test
	public void testIndexInfoLanguageOfSubOrderWithoutModsLanguage() throws JDOMException, IOException {
		SAXBuilder builder = XMLUtils.createValidatingSaxBuilder();
		FileReader fr1 = new FileReader(Path.makeFile(WORK_AREA_ROOT_PATH, "MetsImageTeaser.xml"));
		Document metsDoc = builder.build(fr1);
		MetsParser mp = new MetsParser(metsDoc);

		HashMap<String, HashMap<String, List<String>>> indexInfo = mp.getIndexInfo("obj");
		// sub order with edm:type=TEXT but no mods:language -> "und" (undetermined)
		assertEquals("und", indexInfo.get("obj-mdVolume").get(C.EDM_LANGUAGE).get(0));
		// root carries its own language from mods:language -> code
		assertTrue(indexInfo.get("obj-mdPeriodical").get(C.EDM_LANGUAGE).contains("ger"));

		// same rule applies when filtered by CONTENTIDS urn (the exported single section)
		HashMap<String, HashMap<String, List<String>>> filtered = mp.getIndexInfo("obj", "urn:nbn:de:hbz:061:1-1024024");
		assertEquals("und", filtered.get("obj-mdVolume").get(C.EDM_LANGUAGE).get(0));
	}

	@Test
	public void testIndexInfoIsPartOfUsesSuperiorUrn() throws JDOMException, IOException {
		SAXBuilder builder = XMLUtils.createValidatingSaxBuilder();
		FileReader fr1 = new FileReader(Path.makeFile(WORK_AREA_ROOT_PATH, "MetsImageTeaser.xml"));
		Document metsDoc = builder.build(fr1);
		MetsParser mp = new MetsParser(metsDoc);

		HashMap<String, HashMap<String, List<String>>> indexInfo = mp.getIndexInfo("obj");
		// sub order: dcterms:isPartOf = URN of superior dmdSec (via outermost enclosing div)
		assertTrue(indexInfo.get("obj-mdVolume").get(C.EDM_IS_PART_OF).contains("urn:nbn:de:hbz:061:1-1012347"));
		// root level: no enclosing div and no relatedItem series -> no isPartOf
		assertTrue(indexInfo.get("obj-mdPeriodical").get(C.EDM_IS_PART_OF) == null);
	}

	@Test
	public void testIndexInfoIsPartOfFromRelatedItemSeries() throws JDOMException, IOException {
		SAXBuilder builder = XMLUtils.createValidatingSaxBuilder();
		FileReader fr1 = new FileReader(licenseMetsFile);
		Document metsDoc = builder.build(fr1);
		MetsParser mp = new MetsParser(metsDoc);

		HashMap<String, HashMap<String, List<String>>> indexInfo = mp.getIndexInfo("obj");
		// single div + relatedItem type=series without urn/uri identifier -> the series title
		// is taken over as literal dcterms:isPartOf (see Anleitung 3.1.9 Abs. 4-5)
		assertEquals(C.EDM_IS_PART_OF_LITERAL+"Kriegsbriefe",
				indexInfo.get("obj-md1937630").get(C.EDM_IS_PART_OF).get(0));
	}

	
}
