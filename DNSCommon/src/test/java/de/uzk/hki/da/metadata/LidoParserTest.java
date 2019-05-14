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
	
	
	@Test
	public void testGetIndexInfoFromLavMets() throws JDOMException, IOException, JaxenException {
		SAXBuilder builder = XMLUtils.createNonvalidatingSaxBuilder();
		FileReader fr1 = new FileReader(licenseLidoFile);
		Document lidoDoc = builder.build(fr1);
		LidoParser lp = new LidoParser(lidoDoc);
		
		HashMap<String, HashMap<String, List<String>>> indexInfo = lp.getIndexInfo("Test-Object-Id");
		
		assertTrue(indexInfo.entrySet().size()==2);
		
		HashMap<String, List<String>> elements = indexInfo.get("Test-Object-Id-ISIL/lido/Inventarnummer-1");
			
		assertTrue(elements.get(C.EDM_TITLE).size()==1
					&& (elements.get(C.EDM_TITLE).get(0).equals("Nudelmaschine in Originalverpackung")  ));
			
		assertTrue(elements.get(C.EDM_PUBLISHER).contains("Overath") 
					&& elements.get(C.EDM_PUBLISHER).contains("Italien"));
			
		assertTrue(elements.get(C.EDM_DATE_ISSUED).size()==1 
					&& (elements.get(C.EDM_DATE_ISSUED).get(0).equals("01.01.1970-31.12.1989")));
			
		assertTrue(elements.get(C.EDM_RIGHTS).size()==1 
					&& (elements.get(C.EDM_RIGHTS).get(0).equals("http://creativecommons.org/licenses/by/3.0/de/")));
			
			
			
		HashMap<String, List<String>> elements2 = indexInfo.get("Test-Object-Id-ISIL/lido/Inventarnummer-2");
			
		assertTrue(elements2.get(C.EDM_TITLE).size()==1
					&& (elements2.get(C.EDM_TITLE).get(0).equals("Küchenmaschine")));
			
		assertTrue(elements2.get(C.EDM_PUBLISHER).contains("Bergisch Gladbach"));
			
		assertTrue(elements2.get(C.EDM_DATE_ISSUED).size()==2 
					&& (elements2.get(C.EDM_DATE_ISSUED).contains("01.01.1950-31.12.1969")||
							elements2.get(C.EDM_DATE_ISSUED).contains("01.01.1950-31.12.1959")	));
		assertTrue(elements2.get(C.EDM_RIGHTS).size()==1 
					&& (elements2.get(C.EDM_RIGHTS).get(0).equals("http://creativecommons.org/licenses/by/3.0/de/")));		
	}
	
	
	@Test
	public void testDifferentLicenseMultipleAM()throws JDOMException, IOException{
			LidoLicense lidoLicense=new LidoLicense("http://creativecommons.org/licenses/by/3.0/de/","CC BY 3.0 DE");
			SAXBuilder builder = XMLUtils.createNonvalidatingSaxBuilder();
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
		SAXBuilder builder = XMLUtils.createNonvalidatingSaxBuilder();
		FileReader fr1 = new FileReader(licenseLidoFile);
		Document lidoDoc = builder.build(fr1);
		LidoParser lp = new LidoParser(lidoDoc);
		
		assertEquals(lidoLicense,lp.getLicenseForWholeLido());
		assertEquals(lidoLicense.getHref(),lp.getIndexInfo("Test-Object-Id").get("Test-Object-Id-ISIL/lido/Inventarnummer-1").get(C.EDM_RIGHTS).get(0));
		assertEquals(lidoLicense.getHref(),lp.getIndexInfo("Test-Object-Id").get("Test-Object-Id-ISIL/lido/Inventarnummer-2").get(C.EDM_RIGHTS).get(0));
	}
	
	@Test
	public void testNoLicenseInLido()throws JDOMException, IOException{
		SAXBuilder builder = XMLUtils.createNonvalidatingSaxBuilder();
		FileReader fr1 = new FileReader(noLicenseLidoFile);
		Document lidoDoc = builder.build(fr1);
		LidoParser lp = new LidoParser(lidoDoc);
		
		assertEquals(null,lp.getLicenseForWholeLido());
		assertTrue(lp.getIndexInfo("Test-Object-Id").get("Test-Object-Id-ISIL/lido/Inventarnummer-1").get(C.EDM_RIGHTS).isEmpty());
		assertTrue(lp.getIndexInfo("Test-Object-Id").get("Test-Object-Id-ISIL/lido/Inventarnummer-2").get(C.EDM_RIGHTS).isEmpty());		
	}
	
	@Test
	public void testNoLicenseInLidoMultipleAM()throws JDOMException, IOException{
		SAXBuilder builder = XMLUtils.createNonvalidatingSaxBuilder();
		FileReader fr1 = new FileReader(noLicenseMultiAMLidoFile);
		Document lidoDoc = builder.build(fr1);
		LidoParser lp = new LidoParser(lidoDoc);
		
		assertEquals(null,lp.getLicenseForWholeLido());
		assertTrue(lp.getIndexInfo("Test-Object-Id").get("Test-Object-Id-ISIL/lido/Inventarnummer-1").get(C.EDM_RIGHTS).isEmpty());
		assertTrue(lp.getIndexInfo("Test-Object-Id").get("Test-Object-Id-ISIL/lido/Inventarnummer-2").get(C.EDM_RIGHTS).isEmpty());		
	}
}
