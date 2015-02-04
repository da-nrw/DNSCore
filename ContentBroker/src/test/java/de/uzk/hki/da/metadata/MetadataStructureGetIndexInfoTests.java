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

import de.uzk.hki.da.core.C;
import de.uzk.hki.da.model.Document;
import de.uzk.hki.da.util.Path;
import de.uzk.hki.da.util.RelativePath;

public class MetadataStructureGetIndexInfoTests {
	
	private static final String DATA_DANRW_DE = "http://data.danrw.de";
	private static final Path workAreaRootPathPath = new RelativePath("src/test/resources/metadata/MetadataStructureGetIndexInfoTests/");
	private static HashMap<String, HashMap<String, List<String>>> indexInfo = new HashMap<String, HashMap<String,List<String>>>();
	private static HashMap<String, List<String>> content = new HashMap<String, List<String>>();
	private String objectID = "objectID";
	
	@BeforeClass
	public static void createTargetDir() {
		Path.makeFile(workAreaRootPathPath, "target").mkdirs();
	}
	
	@Test
	public void testLIDO() throws FileNotFoundException, JDOMException, IOException {
		
		File lidoFile = Path.make(workAreaRootPathPath,"MetadataStructureGetIndexInfoTestsLIDO.xml").toFile();
		
		List<Document> docs = new ArrayList<Document>();
		MetadataStructure lms = new LidoMetadataStructure(lidoFile, docs);
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
		lms.toEDM(indexInfo, Path.makeFile(workAreaRootPathPath, "target", "lidoToEdm.xml"),  DATA_DANRW_DE);
	}
	
	@Test
	public void testMETS() throws FileNotFoundException, JDOMException, IOException {
		
		File metsFile = Path.make(workAreaRootPathPath,"MetadataStructureGetIndexInfoTestsMETS.xml").toFile();
		
		List<Document> docs = new ArrayList<Document>();
		MetadataStructure mms = new MetsMetadataStructure(metsFile, docs);
		indexInfo = mms.getIndexInfo(objectID);
		
		content = indexInfo.get(objectID);
		
		assertTrue(content.get(C.EDM_TITLE).contains("Chronik der Stadt Hoerde")
				&&content.get(C.EDM_TITLE).contains("und der größeren evangelischen Gemeinde in derselben"));
			
		
		assertTrue(content.get(C.EDM_DATE).contains("1836")
				&&content.get(C.EDM_DATE).contains("2011"));
		
		assertTrue(content.get(C.EDM_PUBLISHER).contains("Hoerde")
				&&content.get(C.EDM_PUBLISHER).contains("Münster"));
		
		mms.toEDM(indexInfo, Path.makeFile(workAreaRootPathPath, "target", "metsToEdm.xml"),  DATA_DANRW_DE);
	}
	
	@Test
	public void testEAD() throws FileNotFoundException, JDOMException, IOException, ParserConfigurationException, SAXException {
		
		File eadFile = Path.make(workAreaRootPathPath,"MetadataStructureGetIndexInfoTestsEAD.xml").toFile();
		
		List<Document> docs = new ArrayList<Document>();
		MetadataStructure ems = new EadMetsMetadataStructure(eadFile, docs);
		
		indexInfo = ems.getIndexInfo(objectID);
		
		String parent = "";
		for(String id : indexInfo.keySet()) {
			HashMap<String, List<String>> content = indexInfo.get(id);
			if(content.get(C.EDM_TITLE).contains("Pressemitteilungen, Amerikadienst")) {
				
				assertTrue(
						content.get(C.EDM_DATE).contains("1938-01-01/1938-12-31"));
				assertTrue(content.get(C.EDM_IDENTIFIER).size()==3&&
						content.get(C.EDM_IDENTIFIER).contains("altsignatur: XIV 34")&&
						content.get(C.EDM_IDENTIFIER).contains("v.num: 4667")&&
						content.get(C.EDM_IDENTIFIER).contains("Bestellnummer: 4667"));
				
				parent = content.get(C.EDM_IS_PART_OF).get(0);
			}
			if(content.get(C.EDM_TITLE).contains("Hans Abel")) {
				assertTrue(
						content.get(C.EDM_DATE).contains("0000/0000"));
				assertTrue(content.get(C.EDM_IDENTIFIER).size()==3&&
						content.get(C.EDM_IDENTIFIER).contains("altsignatur: ")&&
						content.get(C.EDM_IDENTIFIER).contains("v.num: 2")&&
						content.get(C.EDM_IDENTIFIER).contains("Bestellnummer: 4547_Blatt_002"));
				assertTrue(
						content.get(C.EDM_HAS_VIEW).contains("mets_2_32045.xml"));
			}
		}
		assertTrue(indexInfo.get(parent).get(C.EDM_TITLE).contains("14. Verschiedenes"));
		
		ems.toEDM(indexInfo, Path.makeFile(workAreaRootPathPath, "target", "eadToEdm.xml"), DATA_DANRW_DE);
	}
	
	@AfterClass 
	public static void tearDown(){
		Path.makeFile(workAreaRootPathPath, "target", "edmToEdm.xml").delete();
		Path.makeFile(workAreaRootPathPath, "target","lidoToEdm.xml").delete();
		Path.makeFile(workAreaRootPathPath, "target", "metsToEdm.xml").delete();
		Path.makeFile(workAreaRootPathPath, "target").delete();
	}
}
