package de.uzk.hki.da.metadata;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xml.sax.SAXException;

import de.uzk.hki.da.model.Document;
import de.uzk.hki.da.model.PreservationSystem;
import de.uzk.hki.da.utils.C;
import de.uzk.hki.da.utils.Path;
import de.uzk.hki.da.utils.RelativePath;
import de.uzk.hki.da.utils.XMLUtils;

/**
 * 
 * @author Eugen Trebunski
 * @author Daniel M. de Oliveira
 *
 */
public class MetadataStructureGetIndexInfoTests {
	
	private static final Path basePath = new RelativePath("src/test/resources/metadata/MetadataStructureGetIndexInfoTests/");
	private static HashMap<String, HashMap<String, List<String>>> indexInfo = new HashMap<String, HashMap<String,List<String>>>();
	private static HashMap<String, List<String>> content = new HashMap<String, List<String>>();
	private static HashMap<String, List<String>> content1 = new HashMap<String, List<String>>();
	private String objectID = "objectID";
	private static PreservationSystem pSystem;
	private final String urn = "urn:nbn:de:danrw-2-20150415425545";
	
	@BeforeClass
	public static void createTargetDir() {
		Path.makeFile(basePath, "target").mkdirs();
		
		pSystem = new PreservationSystem();
		pSystem.setUrisFile("http://data.danrw.de/file");
		pSystem.setUrisCho("http://data.danrw.de/cho");
		pSystem.setUrisAggr("http://data.danrw.de/aggregation");
	}
	
	
	// unklar wieso die Stelle bei LidoParser::getIndexInfo() auskommentiert worden ist
	/*
	@Test
	public void testLIDO() throws FileNotFoundException, JDOMException, IOException {
		
		File lidoFile = Path.make("MetadataStructureGetIndexInfoTestsLIDO.xml").toFile();
		
		List<Document> docs = new ArrayList<Document>();
		MetadataStructure lms = new LidoMetadataStructure(Path.make(basePath),lidoFile, docs);
		indexInfo = lms.getIndexInfo(objectID);
		
		for(String id : indexInfo.keySet()) {
			HashMap<String, List<String>> content = indexInfo.get(id);

			if(content.get(C.EDM_TITLE).contains("Poltrona di Proust")) {
				assertTrue(
						content.get(C.EDM_PUBLISHER).get(0).equals("Mailand, Italien"));
				assertTrue(content.get(C.EDM_DATE_ISSUED).size()==2&&
						content.get(C.EDM_DATE_ISSUED).contains("1978 (Herstellungsjahr: 1980/1990)")&&
						content.get(C.EDM_DATE_ISSUED).contains("1978-1990"));
				
			}
			if(content.get(C.EDM_TITLE).contains("Prämienschein Konzentrationslager Floßenbürg 009739")) {
				assertTrue(
						content.get(C.EDM_PUBLISHER).get(0).equals("Floßenbürg, Deutschland"));
				assertTrue(
						content.get(C.EDM_DATE_ISSUED).size()==2
						&&content.get(C.EDM_DATE_ISSUED).contains("o. D.")
						&&content.get(C.EDM_DATE_ISSUED).contains("1943-1945"));
			}
			
		}
		lms.toEDM(indexInfo, Path.makeFile(basePath, "target", "lidoToEdm.xml"), pSystem, objectID, urn);
	}
	*/
	
	@Test
	public void testMETS() throws FileNotFoundException, JDOMException, IOException {
		
		File metsFile = Path.make("MetadataStructureGetIndexInfoTestsMETS.xml").toFile();
		
		List<Document> docs = new ArrayList<Document>();
		MetsMetadataStructure mms = new MetsMetadataStructure(Path.make(basePath),metsFile, docs);
		
		assertTrue(mms.getUrn("urn+nbn+de+hbz+6+1-3602").equals("urn:nbn:de:hbz:6:1-3602"));
		
		indexInfo = mms.getIndexInfo(objectID);
		
		content = indexInfo.get(objectID+"-md258094");
		

		assertEquals(content.get(C.EDM_TITLE).get(0), "Chronik der Stadt Hoerde");
		assertTrue(content.get(C.EDM_ALTERNATIVE).contains("und der größeren evangelischen Gemeinde in derselben"));
			
		assertTrue(content.get(C.EDM_EXTENT).get(0).equals("VI, 115 S."));
		assertTrue(content.get(C.EDM_DATE_ISSUED).contains("1836")
				&&content.get(C.EDM_DATE_CREATED).contains("2011"));
		
		assertTrue(content.get(C.EDM_PUBLISHER).contains("Universitäts- und Landesbibliothek"));
		assertTrue(!content.get(C.EDM_PUBLISHER).contains("Hoerde"));
		
		assertTrue(content.get(C.EDM_CREATOR).contains("Schulte, Friedrich W."));
		
		content1 = indexInfo.get(objectID+"-md1616184");
		
		mms.toEDM(indexInfo, Path.makeFile(basePath, "target", "metsToEdm.xml"), pSystem, objectID, urn);
	}
	
	@Test
	public void testMultilevelMETS() throws FileNotFoundException, JDOMException, IOException {
		
		File metsFile = Path.make("export_mets.xml").toFile();
		
		List<Document> docs = new ArrayList<Document>();
		MetsMetadataStructure mms = new MetsMetadataStructure(Path.make(basePath),metsFile, docs);
		indexInfo = mms.getIndexInfo(objectID);
		
		assertTrue(mms.getUrn("urn+nbn+de+hbz+6+1-65526").equals("urn:nbn:de:hbz:6:1-65526"));
		
		content = indexInfo.get(objectID+"-md1617166");
		
		assertTrue(content.get(C.EDM_TITLE).contains("[Atlas von Europa]"));	
		assertTrue(content.get(C.EDM_DATE_ISSUED).contains("1794")
				&&content.get(C.EDM_DATE_CREATED).contains("2012"));
		assertTrue(content.get(C.EDM_PUBLISHER).contains("Otto")
				&&content.get(C.EDM_PUBLISHER).contains("Univ.- und Landesbibliothek"));

		assertTrue(content.get(C.EDM_HAS_PART).size()==1 && content.get(C.EDM_HAS_PART).contains("objectID-md1616184"));
		
		content1 = indexInfo.get(objectID+"-md1616184");
		assertTrue(content1.get(C.EDM_IS_PART_OF).size()==1 && content1.get(C.EDM_IS_PART_OF).contains("urn:nbn:de:hbz:6:1-65526"));
		assertTrue(content1.get(C.EDM_IS_SHOWN_BY).contains("image/1616186.tif"));
		assertTrue(content1.get(C.EDM_OBJECT).contains("image/1616186.tif"));
		assertTrue(content1.get(C.EDM_HAS_VIEW).contains("image/1616186.tif") && content1.get(C.EDM_HAS_VIEW).contains("image/1616187.tif"));
		
		mms.toEDM(indexInfo, Path.makeFile(basePath, "target", "multilevelMetsToEdm.xml"), pSystem, objectID, urn);
		
		try {
			SAXBuilder builder = XMLUtils.createValidatingSaxBuilder();
			org.jdom.Document edmDoc = builder.build(Path.makeFile(basePath, "target", "multilevelMetsToEdm.xml"));
			List<Element> providedCHOs = (List<Element>) edmDoc.getRootElement().getChildren(C.EDM_PROVIDED_CHO, C.EDM_NS);
			for (Element providedCho : providedCHOs) {
				String about = providedCho.getAttributeValue("about", C.RDF_NS);
				if (about != null && about.endsWith("md1616184")) {
					assertEquals("urn:nbn:de:hbz:6:1-65526",
							providedCho.getChild("isPartOf", C.DCTERMS_NS).getAttributeValue("resource", C.RDF_NS));
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	@Test
	public void testMultilevelMETSappendAccessCondition() throws FileNotFoundException, JDOMException, IOException {
		MetsLicense testLicense=new MetsLicense("use and reproduction","https://creativecommons.org/publicdomain/mark/1.0/","Public Domain Mark 1.0","pdm");
		File metsFile = Path.make("export_mets.xml").toFile();
		
		//file for modifications
		File metsTMP=Path.make("/export_metsTEST.xml").toFile();
		File metsTMPFullPath= Path.make(Path.make(basePath),metsTMP.getAbsolutePath()).toFile();
		FileUtils.copyFile(Path.make(Path.make(basePath),metsFile.getAbsolutePath()).toFile(), metsTMPFullPath);
		try{
			List<Document> docs = new ArrayList<Document>();
			MetsMetadataStructure mms = new MetsMetadataStructure(Path.make(basePath),metsFile, docs);
			mms.appendAccessCondition(metsTMP, testLicense.getHref(), testLicense.getDisplayLabel(), testLicense.getText());
			
			//read the written result
			SAXBuilder builder = XMLUtils.createValidatingSaxBuilder();
			FileReader fr1 = new FileReader(metsTMPFullPath);
			org.jdom.Document metsDoc = builder.build(fr1);
			MetsParser mp = new MetsParser(metsDoc);
			
			assertEquals(testLicense,mp.getLicensesForWholeMets().get(0));
assertEquals(MetsParser.PDM_MARK_URI,mp.getIndexInfo("Test-Object-Id").get("Test-Object-Id-md1616184").get(C.EDM_RIGHTS).get(0));
		assertEquals(MetsParser.PDM_MARK_URI,mp.getIndexInfo("Test-Object-Id").get("Test-Object-Id-md1617166").get(C.EDM_RIGHTS).get(0));
			
			
		}finally{
			FileUtils.deleteQuietly(metsTMPFullPath);
		}
	}
	
	@Test
	public void testDataProvider() throws FileNotFoundException, JDOMException, IOException {
		File metsFile = Path.make(Path.make(basePath),"202305SuperiorMets.xml").toFile();

		try{
			SAXBuilder builder = XMLUtils.createValidatingSaxBuilder();
			FileReader fr1 = new FileReader(metsFile);
			org.jdom.Document metsDoc = builder.build(fr1);
			MetsParser mp = new MetsParser(metsDoc);
			
			assertEquals(1,mp.getDataProvider("amd2083410").size());
			assertEquals("Universitäts- und Landesbibliothek Düsseldorf",mp.getIndexInfo("Test-Object-Id").get("Test-Object-Id-md2083410").get(C.EDM_DATA_PROVIDER).get(0));
			//md2083410   md2083068   md2089438  md2089038	
		}finally{
			
		}
	}
	
	@Test
	public void testSimpleMETSappendAccessCondition() throws FileNotFoundException, JDOMException, IOException {
		MetsLicense testLicense=new MetsLicense("use and reproduction","https://creativecommons.org/publicdomain/mark/1.0/","Public Domain Mark 1.0","pdm");
		File metsFile = Path.make("simpleMetsNoLicense.xml").toFile();
		
		//file for modifications
		File metsTMP=Path.make("/simpleMetsNoLicenseTEST.xml").toFile();
		File metsTMPFullPath= Path.make(Path.make(basePath),metsTMP.getAbsolutePath()).toFile();
		FileUtils.copyFile(Path.make(Path.make(basePath),metsFile.getAbsolutePath()).toFile(), metsTMPFullPath);
		try{
			List<Document> docs = new ArrayList<Document>();
			MetsMetadataStructure mms = new MetsMetadataStructure(Path.make(basePath),metsFile, docs);
			mms.appendAccessCondition(metsTMP, testLicense.getHref(), testLicense.getDisplayLabel(), testLicense.getText());
			
			//read the written result
			SAXBuilder builder = XMLUtils.createValidatingSaxBuilder();
			FileReader fr1 = new FileReader(metsTMPFullPath);
			org.jdom.Document metsDoc = builder.build(fr1);
			MetsParser mp = new MetsParser(metsDoc);
			
			assertEquals(testLicense,mp.getLicensesForWholeMets().get(0));
			assertEquals(MetsParser.PDM_MARK_URI,mp.getIndexInfo("Test-Object-Id").get("Test-Object-Id-md2684319").get(C.EDM_RIGHTS).get(0));			
		}finally{
			FileUtils.deleteQuietly(metsTMPFullPath);
		}

	}
	
	@Test
	public void testToEdmPdmMarkRightsNormalizedToHttps() throws FileNotFoundException, JDOMException, IOException {
		MetsLicense testLicense=new MetsLicense("use and reproduction","https://creativecommons.org/publicdomain/mark/1.0/","Public Domain Mark 1.0","pdm");
		File metsFile = Path.make("export_mets.xml").toFile();
		
		File metsTMP=Path.make("/export_metsRightsTEST.xml").toFile();
		File metsTMPFullPath= Path.make(Path.make(basePath),metsTMP.getAbsolutePath()).toFile();
		FileUtils.copyFile(Path.make(Path.make(basePath),metsFile.getAbsolutePath()).toFile(), metsTMPFullPath);
		File edmFile = Path.makeFile(basePath, "target", "metsRightsToEdm.xml");
		try{
			List<Document> docs = new ArrayList<Document>();
			MetsMetadataStructure mms = new MetsMetadataStructure(Path.make(basePath),metsFile, docs);
			mms.appendAccessCondition(metsTMP, testLicense.getHref(), testLicense.getDisplayLabel(), testLicense.getText());
			
			SAXBuilder builder = XMLUtils.createValidatingSaxBuilder();
			FileReader fr1 = new FileReader(metsTMPFullPath);
			MetsParser mp = new MetsParser(builder.build(fr1));
			FakeMetadataStructure fake = new FakeMetadataStructure(Path.make(basePath), new File("fake.xml"), new ArrayList<Document>());
			fake.toEDM(mp.getIndexInfo("Test-Object-Id"), edmFile, pSystem, "Test-Object-Id", "urn:nbn:de:hbz:6:1-65526");
			
			org.jdom.Document edmDoc = builder.build(edmFile);
			List<Element> aggregations = (List<Element>) edmDoc.getRootElement().getChildren("Aggregation", C.ORE_NS);
			assertTrue(!aggregations.isEmpty());
			for (Element aggregation : aggregations) {
				Element rights = aggregation.getChild("rights", C.EDM_NS);
				assertTrue(rights != null);
				assertEquals(MetsParser.PDM_MARK_URI, rights.getAttributeValue("resource", C.RDF_NS));
			}
		} finally {
			FileUtils.deleteQuietly(metsTMPFullPath);
			FileUtils.deleteQuietly(edmFile);
		}
	}
	
	@Test
	public void testToEdmHttpRightsNormalizedToHttps() throws FileNotFoundException, JDOMException, IOException {
		MetsLicense testLicense=new MetsLicense("use and reproduction","http://creativecommons.org/licenses/by-nc-sa/4.0","CC-BY-NC-SA-Lizenz (4.0)","cc-by-nc-sa_4.0");
		File metsFile = Path.make("export_mets.xml").toFile();
		
		File metsTMP=Path.make("/export_metsHttpRightsTEST.xml").toFile();
		File metsTMPFullPath= Path.make(Path.make(basePath),metsTMP.getAbsolutePath()).toFile();
		FileUtils.copyFile(Path.make(Path.make(basePath),metsFile.getAbsolutePath()).toFile(), metsTMPFullPath);
		File edmFile = Path.makeFile(basePath, "target", "metsHttpRightsToEdm.xml");
		try{
			List<Document> docs = new ArrayList<Document>();
			MetsMetadataStructure mms = new MetsMetadataStructure(Path.make(basePath),metsFile, docs);
			mms.appendAccessCondition(metsTMP, testLicense.getHref(), testLicense.getDisplayLabel(), testLicense.getText());
			
			SAXBuilder builder = XMLUtils.createValidatingSaxBuilder();
			FileReader fr1 = new FileReader(metsTMPFullPath);
			MetsParser mp = new MetsParser(builder.build(fr1));
			
			//the license in the Mets-File itself is not modified
			assertEquals(testLicense,mp.getLicensesForWholeMets().get(0));
			
			FakeMetadataStructure fake = new FakeMetadataStructure(Path.make(basePath), new File("fake.xml"), new ArrayList<Document>());
			fake.toEDM(mp.getIndexInfo("Test-Object-Id"), edmFile, pSystem, "Test-Object-Id", "urn:nbn:de:hbz:6:1-65526");
			
			org.jdom.Document edmDoc = builder.build(edmFile);
			List<Element> aggregations = (List<Element>) edmDoc.getRootElement().getChildren("Aggregation", C.ORE_NS);
			assertTrue(!aggregations.isEmpty());
			for (Element aggregation : aggregations) {
				Element rights = aggregation.getChild("rights", C.EDM_NS);
				assertTrue(rights != null);
				assertEquals("https://creativecommons.org/licenses/by-nc-sa/4.0", rights.getAttributeValue("resource", C.RDF_NS));
			}
		} finally {
			FileUtils.deleteQuietly(metsTMPFullPath);
			FileUtils.deleteQuietly(edmFile);
		}
	}
	

		
	@Test
	public void testEAD() throws FileNotFoundException, JDOMException, IOException, ParserConfigurationException, SAXException {
		
		File eadFile = Path.make("MetadataStructureGetIndexInfoTestsEAD.xml").toFile();
		
		List<Document> docs = new ArrayList<Document>();
		MetadataStructure ems = new EadMetsMetadataStructure(Path.make(basePath),eadFile, docs);
		
		indexInfo = ems.getIndexInfo(objectID);
		
		String parent = "";
		for(String id : indexInfo.keySet()) {
			HashMap<String, List<String>> content = indexInfo.get(id);
			if(content.get(C.EDM_TITLE).contains("Pressemitteilungen, Amerikadienst")) {
				
				assertTrue(
						content.get(C.EDM_DATE_CREATED).contains("1938-01-01/1938-12-31"));
				assertTrue(content.get(C.EDM_IDENTIFIER).size()==3&&
						content.get(C.EDM_IDENTIFIER).contains("XIV 34")&&
						content.get(C.EDM_IDENTIFIER).contains("4667")&&
						content.get(C.EDM_IDENTIFIER).contains("4667"));
				
				parent = content.get(C.EDM_IS_PART_OF).get(0);
			}
			if(content.get(C.EDM_TITLE).contains("Hans Abel")) {
				assertTrue(
						content.get(C.EDM_DATE_CREATED).contains("0000/0000"));
				assertTrue(content.get(C.EDM_IDENTIFIER).size()==2&&
						content.get(C.EDM_IDENTIFIER).contains("2")&&
						content.get(C.EDM_IDENTIFIER).contains("4547_Blatt_002"));
				assertTrue(
						content.get(C.EDM_IS_SHOWN_BY).contains("mets_2_32045.xml"));
			}
		}
		assertTrue(indexInfo.get(parent).get(C.EDM_TITLE).contains("14. Verschiedenes"));
		
		ems.toEDM(indexInfo, Path.makeFile(basePath, "target", "eadToEdm.xml"), pSystem, objectID, urn);
	}
	
	@Test
	public void testNewDdbEad() throws JDOMException, IOException, ParserConfigurationException, SAXException {
		File eadFile = Path.make("new_ddb_ead.xml").toFile();
		
		List<Document> docs = new ArrayList<Document>();
		MetadataStructure ems = new EadMetsMetadataStructure(Path.make(basePath),eadFile, docs);
		
		indexInfo = ems.getIndexInfo(objectID);
		
		String parentID1_Bestandsname = "1";
		String parentID2_Bestandsname = "2";
		String parentID3_Bestandsname = "3";
		
		for(String id : indexInfo.keySet()) {
			HashMap<String, List<String>> content = indexInfo.get(id);
			
			if(content.get(C.EDM_TITLE)!=null && content.get(C.EDM_TITLE).contains("_Bestandsname")) {
				assertTrue(content.get(C.EDM_HAS_PART).size()==3);
			}
			
			if(content.get(C.EDM_TITLE)!=null && content.get(C.EDM_IDENTIFIER).contains("4 Rheinländer in aller Welt, Adressen")) {
				parentID1_Bestandsname = content.get(C.EDM_IS_PART_OF).get(0);
			}
			
			if(content.get(C.EDM_TITLE)!=null && content.get(C.EDM_IDENTIFIER).contains("Klassifikationsgruppe 1")) {
				parentID2_Bestandsname = content.get(C.EDM_IS_PART_OF).get(0);
				assertTrue(content.get(C.EDM_HAS_PART).size()==1);
			}
			
			if(content.get(C.EDM_TITLE)!=null && content.get(C.EDM_IDENTIFIER).contains("Systematikgruppe 1")) {
				parentID3_Bestandsname = content.get(C.EDM_IS_PART_OF).get(0);
			}

		}
		assertTrue(parentID1_Bestandsname.equals(parentID2_Bestandsname) && parentID2_Bestandsname.equals(parentID3_Bestandsname));
		ems.toEDM(indexInfo, Path.makeFile(basePath, "target", "new_ddb_ead_to_edm.xml"), pSystem, objectID, urn);
	}
	
	
	
	@Test
	public void testToEdmDoesNotDuplicateUrnInIdentifiers() throws FileNotFoundException, JDOMException, IOException {
		HashMap<String, HashMap<String, List<String>>> indexInfo = new HashMap<String, HashMap<String, List<String>>>();
		HashMap<String, List<String>> dmdSecInfo = new HashMap<String, List<String>>();
		List<String> identifiers = new ArrayList<String>();
		identifiers.add(urn);
		dmdSecInfo.put(C.EDM_IDENTIFIER, identifiers);
		indexInfo.put(objectID+"-mdTest", dmdSecInfo);

		File edmFile = Path.makeFile(basePath, "target", "metsToEdmNoDupUrn.xml");
		FakeMetadataStructure mms = new FakeMetadataStructure(Path.make(basePath), new File("fake.xml"), new ArrayList<Document>());
		mms.toEDM(indexInfo, edmFile, pSystem, objectID, urn);
		
		try {
			SAXBuilder builder = XMLUtils.createValidatingSaxBuilder();
			org.jdom.Document edmDoc = builder.build(edmFile);
			List<Element> providedCHOs = (List<Element>) edmDoc.getRootElement().getChildren("ProvidedCHO", C.EDM_NS);
			List<Element> identifiersInEdm = (List<Element>) providedCHOs.get(0).getChildren("identifier", C.DC_NS);
			int urnCount = 0;
			for (Element identifier : identifiersInEdm) {
				if (urn.equals(identifier.getValue())) {
					urnCount++;
				}
			}
			assertEquals(1, urnCount);
		} finally {
			FileUtils.deleteQuietly(edmFile);
		}
	}

	@Test
	public void testToEdmIsPartOfLiteralAndUri() throws FileNotFoundException, JDOMException, IOException {
		HashMap<String, HashMap<String, List<String>>> indexInfo = new HashMap<String, HashMap<String, List<String>>>();
		HashMap<String, List<String>> literalNode = new HashMap<String, List<String>>();
		List<String> literal = new ArrayList<String>();
		literal.add(C.EDM_IS_PART_OF_LITERAL+"Kriegsbriefe");
		literalNode.put(C.EDM_IS_PART_OF, literal);
		indexInfo.put(objectID+"-mdLiteral", literalNode);
		
		HashMap<String, List<String>> uriNode = new HashMap<String, List<String>>();
		List<String> uri = new ArrayList<String>();
		uri.add("https://kalliope-verbund.info/DE-611-BF-41207");
		uriNode.put(C.EDM_IS_PART_OF, uri);
		indexInfo.put(objectID+"-mdUri", uriNode);
		
		File edmFile = Path.makeFile(basePath, "target", "isPartOfLiteralAndUriEdm.xml");
		FakeMetadataStructure mms = new FakeMetadataStructure(Path.make(basePath), new File("fake.xml"), new ArrayList<Document>());
		mms.toEDM(indexInfo, edmFile, pSystem, objectID, urn);
		
		try {
			SAXBuilder builder = XMLUtils.createValidatingSaxBuilder();
			org.jdom.Document edmDoc = builder.build(edmFile);
			List<Element> providedCHOs = (List<Element>) edmDoc.getRootElement().getChildren("ProvidedCHO", C.EDM_NS);
			for (Element providedCho : providedCHOs) {
				String about = providedCho.getAttributeValue("about", C.RDF_NS);
				if (about != null && about.endsWith("mdLiteral")) {
					Element isPartOf = providedCho.getChild("isPartOf", C.DCTERMS_NS);
					assertEquals("Kriegsbriefe", isPartOf.getValue());
					assertEquals(null, isPartOf.getAttributeValue("resource", C.RDF_NS));
				}
				if (about != null && about.endsWith("mdUri")) {
					Element isPartOf = providedCho.getChild("isPartOf", C.DCTERMS_NS);
					assertEquals("https://kalliope-verbund.info/DE-611-BF-41207",
							isPartOf.getAttributeValue("resource", C.RDF_NS));
					assertEquals("", isPartOf.getValue());
				}
			}
		} finally {
			FileUtils.deleteQuietly(edmFile);
		}
	}

	@Test
	public void testToEdmAggregationAboutEqualsAggregatedCho() throws FileNotFoundException, JDOMException, IOException {
		HashMap<String, HashMap<String, List<String>>> indexInfo = new HashMap<String, HashMap<String, List<String>>>();
		indexInfo.put(objectID+"-mdFirst", new HashMap<String, List<String>>());
		indexInfo.put(objectID+"-mdSecond", new HashMap<String, List<String>>());

		File edmFile = Path.makeFile(basePath, "target", "aggregationAboutToEdm.xml");
		FakeMetadataStructure mms = new FakeMetadataStructure(Path.make(basePath), new File("fake.xml"), new ArrayList<Document>());
		mms.toEDM(indexInfo, edmFile, pSystem, objectID, urn);

		try {
			SAXBuilder builder = XMLUtils.createValidatingSaxBuilder();
			org.jdom.Document edmDoc = builder.build(edmFile);
			Set<String> choUris = new HashSet<String>();
			for (Element providedCho : (List<Element>) edmDoc.getRootElement().getChildren("ProvidedCHO", C.EDM_NS)) {
				choUris.add(providedCho.getAttributeValue("about", C.RDF_NS));
			}
			List<Element> aggregations = (List<Element>) edmDoc.getRootElement().getChildren("Aggregation", C.ORE_NS);
			assertEquals(2, aggregations.size());
			for (Element aggregation : aggregations) {
				String about = aggregation.getAttributeValue("about", C.RDF_NS);
				assertTrue(about.startsWith(pSystem.getUrisCho()+"/"));
				assertEquals(about, aggregation.getChild("aggregatedCHO", C.EDM_NS).getAttributeValue("resource", C.RDF_NS));
				assertTrue(choUris.contains(about));
			}
		} finally {
			FileUtils.deleteQuietly(edmFile);
		}
	}

	@Test
	public void testToEdmImageTeaserAsIsShownBy() throws FileNotFoundException, JDOMException, IOException {
		File metsFile = Path.make("MetsImageTeaser.xml").toFile();
		
		List<Document> docs = new ArrayList<Document>();
		MetsMetadataStructure mms = new MetsMetadataStructure(Path.make(basePath), metsFile, docs);
		HashMap<String, HashMap<String, List<String>>> teaserIndexInfo = mms.getIndexInfo(objectID);
		
		File edmFile = Path.makeFile(basePath, "target", "imageTeaserToEdm.xml");
		FakeMetadataStructure fake = new FakeMetadataStructure(Path.make(basePath), new File("fake.xml"), new ArrayList<Document>());
		fake.toEDM(teaserIndexInfo, edmFile, pSystem, objectID, urn);
		
		try {
			SAXBuilder builder = XMLUtils.createValidatingSaxBuilder();
			org.jdom.Document edmDoc = builder.build(edmFile);
			List<Element> aggregations = (List<Element>) edmDoc.getRootElement().getChildren("Aggregation", C.ORE_NS);
			assertTrue(!aggregations.isEmpty());
			for (Element aggregation : aggregations) {
				String about = aggregation.getAttributeValue("about", C.RDF_NS);
				// volume div (CONTENTIDS != root URN) -> its own IMG_TEASER becomes edm:isShownBy
				if (about != null && about.endsWith(objectID + "-mdVolume")) {
					assertEquals("http://example.org/webcache/304/11589950",
							aggregation.getChild("isShownBy", C.EDM_NS).getAttributeValue("resource", C.RDF_NS));
				}
				// periodical div -> its own IMG_TEASER becomes edm:isShownBy
				if (about != null && about.endsWith(objectID + "-mdPeriodical")) {
					assertEquals("http://example.org/webcache/304/11545390",
							aggregation.getChild("isShownBy", C.EDM_NS).getAttributeValue("resource", C.RDF_NS));
				}
			}
		} finally {
			FileUtils.deleteQuietly(edmFile);
		}
	}

	@AfterClass 
	public static void tearDown(){
		Path.makeFile(basePath, "target", "eadToEdm.xml").delete();
		Path.makeFile(basePath, "target","lidoToEdm.xml").delete();
		Path.makeFile(basePath, "target", "metsToEdm.xml").delete();
		Path.makeFile(basePath, "target", "multilevelMetsToEdm.xml").delete();
		Path.makeFile(basePath, "target", "new_ddb_ead_to_edm.xml").delete();
		Path.makeFile(basePath, "target", "imageTeaserToEdm.xml").delete();
		Path.makeFile(basePath, "target").delete();
	}
}
