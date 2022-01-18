package de.uzk.hki.da.metadata;

import static org.junit.Assert.assertEquals;
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
public class LidoParserTest {
	private static final Path WORK_AREA_ROOT_PATH = new RelativePath("src/test/resources/metadata/lidoParser/");

	private static File licenseLidoFile = Path.makeFile(WORK_AREA_ROOT_PATH, "LIDO-License.xml");
	private static File noLicenseMultiLidoErrorFile = Path.makeFile(WORK_AREA_ROOT_PATH, "LIDO-InConsistentLicenseMultipleAM.xml");
	private static File noLicenseMultiAMLidoFile = Path.makeFile(WORK_AREA_ROOT_PATH, "LIDO-NoLicenseMultipleAM.xml");
	private static File noLicenseLidoFile = Path.makeFile(WORK_AREA_ROOT_PATH, "LIDO-NoLicense.xml");
	
	private static File metadataLidoFile = Path.makeFile(WORK_AREA_ROOT_PATH, "SchlossNeersenLIDO201911.xml");
	private static File roidkinLidoFile = Path.makeFile(WORK_AREA_ROOT_PATH, "Roidkin.xml");
	
	@Test
	public void testGetIndexInfoFromLvrRoidkin() throws JDOMException, IOException, JaxenException {
		SAXBuilder builder = XMLUtils.createValidatingSaxBuilder();
		FileReader fr1 = new FileReader(roidkinLidoFile);
		Document lidoDoc = builder.build(fr1);
		LidoParser lp = new LidoParser(lidoDoc);

		HashMap<String, HashMap<String, List<String>>> indexInfo = lp.getIndexInfo("Scheissegal was soll das?");
		HashMap.Entry<String, HashMap<String, List<String>>> mapiEnt = indexInfo.entrySet().iterator().next();
		HashMap<String, List<String>> info = mapiEnt.getValue();

		List<String> valus = info.get(C.EDM_TITLE);
		assertTrue(valus.size() == 2);
		assertTrue(valus.contains("418 - Arnsberg"));
		assertTrue(valus.contains("Arnsberg, Ortsansicht, Zeichnung von Renier Roidkin"));

		valus = info.get(C.EDM_SPATIAL);
		assertTrue(valus.size() == 2);
		assertTrue(valus.contains("Arnsberg"));
		assertTrue(valus.contains("Pulheim"));
	
		valus = info.get(C.EDM_DATE);
		assertTrue(valus.size() == 2);
		assertTrue(valus.contains("1720 - 1730"));
		assertTrue(valus.contains("2009-06-26"));

		valus = info.get(C.EDM_RIGHTS);
		assertTrue(valus.size() == 1);
		assertTrue(valus.contains("http://www.deutsche-digitale-bibliothek.de/lizenzen/rv-fz/"));

		valus = info.get(C.DC_RIGHTS_HOLDER);
		assertTrue(valus.size() == 1);
		assertTrue(valus.contains("Landschaftsverband Rheinland"));

		valus = info.get(C.EDM_DATA_PROVIDER);
		assertTrue(valus.size() == 1);
		assertTrue(valus.contains("LVR-Amt für Denkmalpflege im Rheinland DE-2673"));

		valus = info.get(C.EDM_PROVIDER);
		assertTrue(valus.size() == 1);
		assertTrue(valus.contains("Digitales Archiv NRW"));

		valus = info.get(C.DC_RIGHTS);
		assertTrue(valus.size() == 1);
		assertTrue(valus.contains("http://www.deutsche-digitale-bibliothek.de/lizenzen/rv-fz/"));

		valus = info.get(C.EDM_CREATOR);
		assertTrue(valus.size() == 2);
		assertTrue(valus.contains("Renier Roidkin"));
		assertTrue(valus.contains("Roidkin, Renier"));

		valus = info.get(C.EDM_DESCRIPTION);
		assertTrue(valus.size() == 1);
		assertTrue(valus.contains("Schloss und Stadt von Süd-Osten.' '' 'Zeichnung von Renier Roidkin, um 1720/30.' '' 'LVR-ADR, Grafische Sammlung, Skizzenbuch I A, Blattnr. 418.' '' 'Literatur:' '' 'Walther Zimmermann und Heinrich Neu, Das Werk des Malers Renier Roidkin. Ansichten westdeutscher Kirchen, Burgen, Schlösser und Städte aus der ersten Hälfte des 18. Jahrhunderts. Düsseldorf 1939. (=Beiheft 1 der Kunstdenkmäler der Rheinprovinz), Nr. 22."));

		valus = info.get(C.EDM_IDENTIFIER);
		assertTrue(valus.size() == 1);
		assertTrue(valus.contains("418 - Arnsberg DE-2673"));

		valus = info.get(C.EDM_EXTENT);
		assertTrue(valus.size() == 1);
		assertTrue(valus.contains("31,5 x 53 cm"));

		valus = info.get(C.EDM_PROVENANCE);
		assertTrue(valus.size() == 1);
		assertTrue(valus.contains("Ankauf 1938 von dem Brüsseler Antiquitätenhändler Georges Lebrun"));

		valus = info.get(C.EDM_IS_SHOWN_BY);
		assertTrue(valus.size() == 1);
		assertTrue(valus.contains("LVR_ADR_0000076914.tif"));

		valus = info.get(C.EDM_OBJECT);
		assertTrue(valus.size() == 1);
		assertTrue(valus.contains("LVR_ADR_0000076914.tif"));

		valus = info.get(C.EDM_HAS_VIEW);
		assertTrue(valus == null);
	}
		@Test
	public void testGetIndexInfoFromLavMets() throws JDOMException, IOException, JaxenException {
		SAXBuilder builder = XMLUtils.createValidatingSaxBuilder();
		FileReader fr1 = new FileReader(licenseLidoFile);
		Document lidoDoc = builder.build(fr1);
		LidoParser lp = new LidoParser(lidoDoc);
		
		HashMap<String, HashMap<String, List<String>>> indexInfo = lp.getIndexInfo("Test-Object-Id");
		
		assertTrue(indexInfo.entrySet().size()==2);
		
		HashMap<String, List<String>> elements = indexInfo.get("Test-Object-Id-ISIL/lido/Inventarnummer-1");
			
		assertTrue(elements.get(C.EDM_TITLE).size()==1
					&& (elements.get(C.EDM_TITLE).get(0).equals("Nudelmaschine in Originalverpackung")  ));
			
		assertTrue(elements.get(C.EDM_SPATIAL).contains("Overath") 
					&& elements.get(C.EDM_SPATIAL).contains("Italien"));
			
		assertTrue(elements.get(C.EDM_DATE).size()==1 
					&& (elements.get(C.EDM_DATE).get(0).equals("01.01.1970-31.12.1989")));
			
		assertTrue(elements.get(C.EDM_RIGHTS).size()==1 
					&& (elements.get(C.EDM_RIGHTS).get(0).equals("http://creativecommons.org/licenses/by/3.0/de/")));
			
			
			
		HashMap<String, List<String>> elements2 = indexInfo.get("Test-Object-Id-ISIL/lido/Inventarnummer-2");
			
		assertTrue(elements2.get(C.EDM_TITLE).size()==1
					&& (elements2.get(C.EDM_TITLE).get(0).equals("Küchenmaschine")));
			
		assertTrue(elements2.get(C.EDM_SPATIAL).contains("Bergisch Gladbach"));
			
		assertTrue(elements2.get(C.EDM_DATE).size()==2 
					&& (elements2.get(C.EDM_DATE).contains("01.01.1950-31.12.1969")||
							elements2.get(C.EDM_DATE).contains("01.01.1950-31.12.1959")	));
		assertTrue(elements2.get(C.EDM_RIGHTS).size()==1 
					&& (elements2.get(C.EDM_RIGHTS).get(0).equals("http://creativecommons.org/licenses/by/3.0/de/")));		
	}
	
	
	@Test
	public void testDifferentLicenseMultipleAM()throws JDOMException, IOException{
			LidoLicense lidoLicense=new LidoLicense("http://creativecommons.org/licenses/by/3.0/de/","CC BY 3.0 DE");
			SAXBuilder builder = XMLUtils.createValidatingSaxBuilder();
			FileReader fr1 = new FileReader(noLicenseMultiLidoErrorFile);
			Document lidoDoc = builder.build(fr1);
			LidoParser lp = new LidoParser(lidoDoc);
			//md1616184 has accessCondition
			//md1617166 has no accessCondition
			try{
				assertEquals(null,lp.getLicenseForWholeLido()); //throws Exception
			}catch(RuntimeException e){
				assertTrue(e.getMessage().contains("null"));
				assertTrue(e.getMessage().contains(lidoLicense.toString()));
			}

			assertEquals(lidoLicense.getHref(),lp.getIndexInfo("Test-Object-Id").get("Test-Object-Id-ISIL/lido/Inventarnummer-2").get(C.EDM_RIGHTS).get(0));
			assertTrue(lp.getIndexInfo("Test-Object-Id").get("Test-Object-Id-ISIL/lido/Inventarnummer-1").get(C.EDM_RIGHTS).isEmpty());;
		}
		
		
	@Test
	public void testLicenseInMultilevelLido()throws JDOMException, IOException{
		LidoLicense lidoLicense=new LidoLicense("http://creativecommons.org/licenses/by/3.0/de/","CC BY 3.0 DE");
		SAXBuilder builder = XMLUtils.createValidatingSaxBuilder();
		FileReader fr1 = new FileReader(licenseLidoFile);
		Document lidoDoc = builder.build(fr1);
		LidoParser lp = new LidoParser(lidoDoc);
		
		assertEquals(lidoLicense,lp.getLicenseForWholeLido());
		assertEquals(lidoLicense.getHref(),lp.getIndexInfo("Test-Object-Id").get("Test-Object-Id-ISIL/lido/Inventarnummer-1").get(C.EDM_RIGHTS).get(0));
		assertEquals(lidoLicense.getHref(),lp.getIndexInfo("Test-Object-Id").get("Test-Object-Id-ISIL/lido/Inventarnummer-2").get(C.EDM_RIGHTS).get(0));
	}
	
	@Test
	public void testNoLicenseInLido()throws JDOMException, IOException{
		SAXBuilder builder = XMLUtils.createValidatingSaxBuilder();
		FileReader fr1 = new FileReader(noLicenseLidoFile);
		Document lidoDoc = builder.build(fr1);
		LidoParser lp = new LidoParser(lidoDoc);
		
		assertEquals(null,lp.getLicenseForWholeLido());
		assertTrue(lp.getIndexInfo("Test-Object-Id").get("Test-Object-Id-ISIL/lido/Inventarnummer-1").get(C.EDM_RIGHTS).isEmpty());
		assertTrue(lp.getIndexInfo("Test-Object-Id").get("Test-Object-Id-ISIL/lido/Inventarnummer-2").get(C.EDM_RIGHTS).isEmpty());		
	}
	
	@Test
	public void testNoLicenseInLidoMultipleAM()throws JDOMException, IOException{
		SAXBuilder builder = XMLUtils.createValidatingSaxBuilder();
		FileReader fr1 = new FileReader(noLicenseMultiAMLidoFile);
		Document lidoDoc = builder.build(fr1);
		LidoParser lp = new LidoParser(lidoDoc);
		
		assertEquals(null,lp.getLicenseForWholeLido());
		assertTrue(lp.getIndexInfo("Test-Object-Id").get("Test-Object-Id-ISIL/lido/Inventarnummer-1").get(C.EDM_RIGHTS).isEmpty());
		assertTrue(lp.getIndexInfo("Test-Object-Id").get("Test-Object-Id-ISIL/lido/Inventarnummer-2").get(C.EDM_RIGHTS).isEmpty());		
	}
	
	@Test
	public void testExtent()throws JDOMException, IOException{
		SAXBuilder builder = XMLUtils.createValidatingSaxBuilder();
		String objectId="Test-Object-100 - Schloß Neersen";
		LidoLicense lic=new LidoLicense("http://creativecommons.org/licenses/by/4.0/", "CC BY 4.0");
		FileReader fr1 = new FileReader(metadataLidoFile);
		Document lidoDoc = builder.build(fr1);
		LidoParser lp = new LidoParser(lidoDoc);
		HashMap<String, HashMap<String, List<String>>> indexInfo=lp.getIndexInfo("Test-Object");
		assertEquals(lic,lp.getLicenseForWholeLido());
		assertTrue(indexInfo.get(objectId).get(C.EDM_EXTENT).get(0).equals("29 x 41 cm"));
		assertTrue(indexInfo.get(objectId).get(C.EDM_RIGHTS).get(0).equals(lic.getHref()));		
	}
}
