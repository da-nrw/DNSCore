package de.uzk.hki.da.metadata;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.jdom.JDOMException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xml.sax.SAXException;

import de.uzk.hki.da.model.Document;
import de.uzk.hki.da.model.PreservationSystem;
import de.uzk.hki.da.utils.C;
import de.uzk.hki.da.utils.Path;
import de.uzk.hki.da.utils.RelativePath;

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
				assertTrue(content.get(C.EDM_DATE).size()==2&&
						content.get(C.EDM_DATE).contains("1978 (Herstellungsjahr: 1980/1990)")&&
						content.get(C.EDM_DATE).contains("1978-1990"));
				
			}
			if(content.get(C.EDM_TITLE).contains("Prämienschein Konzentrationslager Floßenbürg 009739")) {
				assertTrue(
						content.get(C.EDM_PUBLISHER).get(0).equals("Floßenbürg, Deutschland"));
				assertTrue(
						content.get(C.EDM_DATE).size()==2
						&&content.get(C.EDM_DATE).contains("o. D.")
						&&content.get(C.EDM_DATE).contains("1943-1945"));
			}
			
		}
		lms.toEDM(indexInfo, Path.makeFile(basePath, "target", "lidoToEdm.xml"), pSystem, objectID, urn);
	}
	
	@Test
	public void testMETS() throws FileNotFoundException, JDOMException, IOException {
		
		File metsFile = Path.make("MetadataStructureGetIndexInfoTestsMETS.xml").toFile();
		
		List<Document> docs = new ArrayList<Document>();
		MetsMetadataStructure mms = new MetsMetadataStructure(Path.make(basePath),metsFile, docs);
		
		assertTrue(mms.getUrn().equals("urn:nbn:de:hbz:6:1-3602"));
		
		indexInfo = mms.getIndexInfo(objectID);
		
		content = indexInfo.get(objectID+"-md258094");
		
		assertTrue(content.get(C.EDM_TITLE).contains("Chronik der Stadt Hoerde")
				&&content.get(C.EDM_TITLE).contains("und der größeren evangelischen Gemeinde in derselben"));
			
		
		assertTrue(content.get(C.EDM_DATE).contains("1836")
				&&content.get(C.EDM_DATE).contains("2011"));
		
		assertTrue(content.get(C.EDM_PUBLISHER).contains("Hoerde")
				&&content.get(C.EDM_PUBLISHER).contains("Universitäts- und Landesbibliothek (Münster)"));
		
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
		
		assertTrue(mms.getUrn().equals("urn:nbn:de:hbz:6:1-65526"));
		
		content = indexInfo.get(objectID+"-md1617166");
		
		assertTrue(content.get(C.EDM_TITLE).contains("[Atlas von Europa]"));	
		assertTrue(content.get(C.EDM_DATE).contains("1794")
				&&content.get(C.EDM_DATE).contains("2012"));
		assertTrue(content.get(C.EDM_PUBLISHER).contains("Otto (Wien)")
				&&content.get(C.EDM_PUBLISHER).contains("Univ.- und Landesbibliothek (Münster)"));

		assertTrue(content.get(C.EDM_HAS_PART).size()==1 && content.get(C.EDM_HAS_PART).contains("objectID-md1616184"));
		
		content1 = indexInfo.get(objectID+"-md1616184");
		assertTrue(content1.get(C.EDM_IS_PART_OF).size()==1 && content1.get(C.EDM_IS_PART_OF).contains("objectID-md1617166"));
		assertTrue(content1.get(C.EDM_IS_SHOWN_BY).contains("image/1616186.tif"));
		assertTrue(content1.get(C.EDM_OBJECT).contains("image/1616186.tif"));
		assertTrue(content1.get(C.EDM_HAS_VIEW).contains("image/1616186.tif") && content1.get(C.EDM_HAS_VIEW).contains("image/1616187.tif"));
		
		mms.toEDM(indexInfo, Path.makeFile(basePath, "target", "multilevelMetsToEdm.xml"), pSystem, objectID, urn);
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
						content.get(C.EDM_DATE).contains("1938-01-01/1938-12-31"));
				assertTrue(content.get(C.EDM_IDENTIFIER).size()==3&&
						content.get(C.EDM_IDENTIFIER).contains("XIV 34")&&
						content.get(C.EDM_IDENTIFIER).contains("4667")&&
						content.get(C.EDM_IDENTIFIER).contains("4667"));
				
				parent = content.get(C.EDM_IS_PART_OF).get(0);
			}
			if(content.get(C.EDM_TITLE).contains("Hans Abel")) {
				assertTrue(
						content.get(C.EDM_DATE).contains("0000/0000"));
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
	
	@AfterClass 
	public static void tearDown(){
		Path.makeFile(basePath, "target", "eadToEdm.xml").delete();
		Path.makeFile(basePath, "target","lidoToEdm.xml").delete();
		Path.makeFile(basePath, "target", "metsToEdm.xml").delete();
		Path.makeFile(basePath, "target", "multilevelMetsToEdm.xml").delete();
		Path.makeFile(basePath, "target", "new_ddb_ead_to_edm.xml").delete();
		Path.makeFile(basePath, "target").delete();
	}
}
