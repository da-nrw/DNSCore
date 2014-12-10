package de.uzk.hki.da.at;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.junit.BeforeClass;
import org.junit.Test;

import de.uzk.hki.da.core.C;
import de.uzk.hki.da.core.Path;
import de.uzk.hki.da.metadata.MetadataHelper;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.repository.RepositoryException;

/**
 * 
 * @author Polina Gubaidullina
 *
 */

public class ATUseCaseIngestDeltaLIDO extends AcceptanceTest{
	
	private static final int _1_MINUTE = 60000;
	private static final String ORIG_NAME_ORIG = "ATUseCaseIngestDeltaLIDO";
	private static final String DATA_DANRW_DE = "http://data.danrw.de";
	private static Object object;
	private static final File retrievalFolder = new File("/tmp/LIDOunpacked");
	private static Path contractorsPipsPublic;
	private static MetadataHelper mh = new MetadataHelper();
	
	@BeforeClass
	public static void setUp() throws IOException, InterruptedException {
		FileUtils.copyFileToDirectory(new File("src/test/resources/at/"+ORIG_NAME_ORIG+"_orig/"+ORIG_NAME_ORIG+".tgz"), new File("src/test/resources/at"));
		ath.ingest(ORIG_NAME_ORIG);
		FileUtils.deleteQuietly(new File("src/test/resources/at/"+ORIG_NAME_ORIG+".tgz"));
		
		Thread.sleep(_1_MINUTE); // to prevent the repnames to match the ones of the previous package
		FileUtils.copyFileToDirectory(new File("src/test/resources/at/"+ORIG_NAME_ORIG+"_delta/"+ORIG_NAME_ORIG+".tgz"), new File("src/test/resources/at"));
		object = ath.ingest(ORIG_NAME_ORIG);
		FileUtils.deleteQuietly(new File("src/test/resources/at/"+ORIG_NAME_ORIG+".tgz"));
		
		contractorsPipsPublic = Path.make(localNode.getWorkAreaRootPath(),C.WA_PIPS, C.WA_PUBLIC, C.TEST_USER_SHORT_NAME);
	}

	@Test
	public void testLZA() throws IOException, InterruptedException, RepositoryException, JDOMException{
		
		System.out.println("LZA ...");
		
		ath.retrievePackage(object,retrievalFolder,"2");
		System.out.println("object identifier: "+object.getIdentifier());
		
		Path tmpObjectDirPath = Path.make(retrievalFolder.getAbsolutePath(), "data");	
		File[] tmpObjectSubDirs = new File (tmpObjectDirPath.toString()).listFiles();
		String bRep = "";
		
		for (int i=0; i<tmpObjectSubDirs.length; i++) {
			if(tmpObjectSubDirs[i].getName().contains("+b")) {
				bRep = tmpObjectSubDirs[i].getName();
			}
		}
		
		SAXBuilder builder = new SAXBuilder();
		String LidoFileName = "LIDO-Testexport2014-07-04-FML-Auswahl.xml";
		Document doc = builder.build
				(new FileReader(Path.make(tmpObjectDirPath, bRep, LidoFileName).toFile()));
		
		List<String> lidoUrls =  mh.getLIDOURL(doc);
		
		for(String url : lidoUrls) {
			System.out.println("URL: "+url);
		}
	}
	
	@Test
	public void testPres() throws FileNotFoundException, JDOMException, IOException{
		
		System.out.println("Presentation ...");
		
		File[] files = (Path.makeFile(contractorsPipsPublic, object.getIdentifier()).listFiles());
		for(File f : files) {
			System.out.println(f.getName());
		}
		
		SAXBuilder builder = new SAXBuilder();
		
		Document doc = builder.build
				(new FileReader(Path.make(contractorsPipsPublic, object.getIdentifier(), "LIDO.xml").toFile()));
		
		List<String> lidoUrls =  mh.getLIDOURL(doc);
		int danrwRewritings = 0;
		for(String url : lidoUrls) {
			System.out.println("URL: "+url);
			if(url.contains(DATA_DANRW_DE)) {
				danrwRewritings++;
			}
		}
		
		assertTrue(danrwRewritings==2);		
	}
}
