package de.uzk.hki.da.metadata;

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

public class MetsParserTest {
		
	private static final Path WORK_AREA_ROOT_PATH = new RelativePath("src/test/resources/metadata/");
	private static String lavMets = "MetsLav.xml";
	private static String ulbsMets = "MetsUlbms.xml";
	private static File lavMetsFile = Path.makeFile(WORK_AREA_ROOT_PATH, lavMets);
	private static File ulbMetsFile = Path.makeFile(WORK_AREA_ROOT_PATH, ulbsMets);
	
	
	@Test
	public void testGetIndexInfoFromLavMets() throws JDOMException, IOException, JaxenException {
		
		SAXBuilder builder = XMLUtils.createNonvalidatingSaxBuilder();
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
	
		assertTrue(elements.get(C.EDM_HAS_VIEW).size()==8
				&& elements.get(C.EDM_HAS_VIEW).contains("http://data.danrw.de/file/1-2015110919/_6d8889c54cd506f75be230bd630cd70d.jpg")
				&& elements.get(C.EDM_HAS_VIEW).contains("http://data.danrw.de/file/1-2015110919/_03a62f462ff81be6d6db280be6a8e4c6.jpg")
				&& elements.get(C.EDM_HAS_VIEW).contains("http://data.danrw.de/file/1-2015110919/_f1b9c8446da0b8b14c10061a3a81949c.jpg")
				&& elements.get(C.EDM_HAS_VIEW).contains("http://data.danrw.de/file/1-2015110919/_d88477ff445fe329a2dac01d3fddc7a8.jpg")
				&& elements.get(C.EDM_HAS_VIEW).contains("http://data.danrw.de/file/1-2015110919/_2fee28672e542ff0a12556552d059cc6.jpg")
				&& elements.get(C.EDM_HAS_VIEW).contains("_MD5hashes.txt")
				&& elements.get(C.EDM_HAS_VIEW).contains("_ggsg_0001.xml"));
	}
	
	@Test
	public void testGetIndexInfoFromUlbMets() throws JDOMException, IOException {
		
		SAXBuilder builder = XMLUtils.createNonvalidatingSaxBuilder();
		FileReader fr1 = new FileReader(ulbMetsFile);
		Document lavMets = builder.build(fr1);
		MetsParser mp = new MetsParser(lavMets);
		
		HashMap<String, HashMap<String, List<String>>> indexInfo = mp.getIndexInfo("danrw801613");
		assertTrue(indexInfo.entrySet().size()==1);
		HashMap<String, List<String>> elements = indexInfo.get("danrw801613-md801613");
		
		assertTrue(elements.get(C.EDM_TITLE).contains("Text Text// mahels///Titel"));
		
		assertTrue(elements.get(C.EDM_PUBLISHER).size()==2);
		assertTrue(elements.get(C.EDM_PUBLISHER).contains("Grimm] ([Augsburg)")
				&& elements.get(C.EDM_PUBLISHER).contains("ULB (Stadt)"));
		
		assertTrue(elements.get(C.EDM_IDENTIFIER).contains("id42")
				&& elements.get(C.EDM_IDENTIFIER).contains("urn:nbn:de:hbz:42"));
		
		assertTrue(elements.get(C.EDM_CREATOR).contains("Nachname, Vorname"));
		
		assertTrue(elements.get(C.EDM_DATA_PROVIDER).contains("ULB"));
		
		assertTrue(elements.get(C.EDM_OBJECT).contains("image/801622.bmp"));
		
		System.out.println();
	}
}
