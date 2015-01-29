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
import org.junit.Test;
import org.xml.sax.SAXException;

import de.uzk.hki.da.model.Document;
import de.uzk.hki.da.util.Path;
import de.uzk.hki.da.util.RelativePath;

public class MetadataStructureGetIndexInfoTests {
	
	private static final Path workAreaRootPathPath = new RelativePath("src/test/resources/metadata/MetadataStructureGetIndexInfoTests/");
	private static HashMap<String, HashMap<String, List<String>>> indexInfo = new HashMap<String, HashMap<String,List<String>>>();
	private static HashMap<String, List<String>> content = new HashMap<String, List<String>>();
	
	@Test
	public void testLIDO() throws FileNotFoundException, JDOMException, IOException {
		
		File lidoFile = Path.make(workAreaRootPathPath,"MetadataStructureGetIndexInfoTestsLIDO.xml").toFile();
		
		List<Document> docs = new ArrayList<Document>();
		MetadataStructure lms = new LidoMetadataStructure(lidoFile, docs);
		indexInfo = lms.getIndexInfo();
		
		for(String id : indexInfo.keySet()) {
			HashMap<String, List<String>> content = indexInfo.get(id);

			if(content.get("title").contains("Poltrona di Proust")) {
				assertTrue(
						content.get("publisher").get(0).equals("Mailand, Italien"));
				assertTrue(content.get("date").size()==2&&
						content.get("date").contains("1978 (Herstellungsjahr: 1980/1990)")&&
						content.get("date").contains("1978-1990"));
				
			}
			if(content.get("title").contains("Prämienschein Konzentrationslager Floßenbürg 009739")) {
				assertTrue(
						content.get("publisher").get(0).equals("Floßenbürg, Deutschland"));
				assertTrue(
						content.get("date").size()==2
						&&content.get("date").contains("o. D.")
						&&content.get("date").contains("1943-1945"));
			}
			
		}
	}
	
	@Test
	public void testMETS() throws FileNotFoundException, JDOMException, IOException {
		
		File metsFile = Path.make(workAreaRootPathPath,"MetadataStructureGetIndexInfoTestsMETS.xml").toFile();
		
		List<Document> docs = new ArrayList<Document>();
		MetadataStructure mms = new MetsMetadataStructure(metsFile, docs);
		indexInfo = mms.getIndexInfo();
		
		content = indexInfo.get("");
		
		assertTrue(content.get("title").contains("Chronik der Stadt Hoerde")
				&&content.get("title").contains("und der größeren evangelischen Gemeinde in derselben"));
			
		assertTrue(content.get("dataProvider").contains("Universitäts- und Landesbibliothek Münster"));
		
		assertTrue(content.get("date").contains("1836")
				&&content.get("date").contains("2011"));
		
		assertTrue(content.get("publisher").contains("Hoerde")
				&&content.get("publisher").contains("Münster"));
	}
	
	@Test
	public void testEAD() throws FileNotFoundException, JDOMException, IOException, ParserConfigurationException, SAXException {
		
		File eadFile = Path.make(workAreaRootPathPath,"MetadataStructureGetIndexInfoTestsEAD.xml").toFile();
		
		List<Document> docs = new ArrayList<Document>();
		MetadataStructure ems = new EadMetsMetadataStructure(eadFile, docs);
		
		indexInfo = ems.getIndexInfo();
		
		String parent = "";
		for(String id : indexInfo.keySet()) {
			HashMap<String, List<String>> content = indexInfo.get(id);
			if(content.get("title").contains("Pressemitteilungen, Amerikadienst")) {
				
				assertTrue(
						content.get("date").contains("1938-01-01/1938-12-31"));
				assertTrue(content.get("identifier").size()==3&&
						content.get("identifier").contains("altsignatur: XIV 34")&&
						content.get("identifier").contains("v.num: 4667")&&
						content.get("identifier").contains("Bestellnummer: 4667"));
				
				parent = content.get("isPartOf").get(0);
			}
		}
		assertTrue(indexInfo.get(parent).get("title").contains("14. Verschiedenes"));
	}

}
