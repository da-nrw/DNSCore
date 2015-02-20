package de.uzk.hki.da.at;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import de.uzk.hki.da.core.C;
import de.uzk.hki.da.metadata.MetadataHelper;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.util.Path;

public class ATUseCaseIngestArchivDuisburg extends AcceptanceTest{
	private static final String URL = "URL";
	private static Path contractorsPipsPublic;
	private static String origName = "Archiv_Duisburg_mini";
	private static Object object;
	private static final String EAD_XML = "EAD.xml";
	private static final File retrievalFolder = new File("/tmp/unpackedDIP");
	private MetadataHelper mh = new MetadataHelper();
	
	@BeforeClass
	public static void setUp() throws IOException {
		object = ath.ingest(origName);
		contractorsPipsPublic = Path.make(localNode.getWorkAreaRootPath(),C.WA_PIPS, C.WA_PUBLIC, C.TEST_USER_SHORT_NAME);
	}
	
	@AfterClass
	public static void tearDown() throws IOException{
		FileUtils.deleteDirectory(retrievalFolder);
	}
	
	@Test
	public void testEdmAndIndex() throws FileNotFoundException, JDOMException, IOException {
//		FileUtils.copyFileToDirectory(Path.make(contractorsPipsPublic, object.getIdentifier(), "EDM.xml").toFile(), Path.makeFile("tmp"));
		SAXBuilder builder = new SAXBuilder();
		Document doc = builder.build
				(new FileReader(Path.make(contractorsPipsPublic, object.getIdentifier(), "EDM.xml").toFile()));
		@SuppressWarnings("unchecked")
		List<Element> providetCho = doc.getRootElement().getChildren("ProvidedCHO", C.EDM_NS);
		Boolean testProvidetChoExists = false;
		String testId = "";
		for(Element pcho : providetCho) {
//			System.out.println("ID: "+pcho.getAttributeValue("about", C.RDF_NS));
			List<Element> elements = pcho.getChildren();
			for(Element e : elements) {
//				System.out.println(e.getName()+": "+e.getValue());
			}
		}
	}
}
