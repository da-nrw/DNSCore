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
import org.junit.Before;
import org.junit.Test;

import ch.qos.logback.classic.Logger;
import de.uzk.hki.da.core.Path;
import de.uzk.hki.da.metadata.MetadataHelper;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.repository.RepositoryException;

/**
 * 
 * @author Polina Gubaidullina
 *
 */

public class ATUseCaseIngestDeltaMETS extends AcceptanceTest{

	private static final int _1_MINUTE = 60000;
	private static final String ORIG_NAME_ORIG = "ATUseCaseIngestDeltaMETS";
	private static final File retrievalFolder = new File("/tmp/unpackedMetsMods");
	private static Path testContractorPipsPublic;
	private static Object object;
	private static MetadataHelper mh = new MetadataHelper();
	
	@Before
	public void setUp() throws IOException, InterruptedException {
		FileUtils.copyFileToDirectory(new File("src/test/resources/at/"+ORIG_NAME_ORIG+"_orig/"+ORIG_NAME_ORIG+".tgz"), new File("src/test/resources/at"));
		ath.ingest(ORIG_NAME_ORIG);
		FileUtils.deleteQuietly(new File("src/test/resources/at/"+ORIG_NAME_ORIG+".tgz"));
		
		Thread.sleep(_1_MINUTE); // to prevent the repnames to match the ones of the previous package
		FileUtils.copyFileToDirectory(new File("src/test/resources/at/"+ORIG_NAME_ORIG+"_delta_oneFile/"+ORIG_NAME_ORIG+".tgz"), new File("src/test/resources/at"));
		object = ath.ingest(ORIG_NAME_ORIG);
		FileUtils.deleteQuietly(new File("src/test/resources/at/"+ORIG_NAME_ORIG+".tgz"));
	}
	
	@Test
	public void testLZA() throws IOException, InterruptedException, RepositoryException, JDOMException{
		
		System.out.println("LZA");

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
		String metsFileName = "export_mets.xml";
		Document doc = builder.build
				(new FileReader(Path.make(tmpObjectDirPath, bRep, metsFileName).toFile()));
		
		List<Element> metsFileElements = mh.getMetsFileElements(doc);
		
		for(Element e : metsFileElements) {
			System.out.println("href: "+mh.getMetsHref(e));
			System.out.println("mimetype: "+mh.getMetsMimetype(e));
			System.out.println("loctype: "+mh.getMetsLoctype(e));
		}
	}
//	
//	@Test
//	public void testPres() throws FileNotFoundException, JDOMException, IOException{
//		
//		SAXBuilder builder = new SAXBuilder();
//		Document doc = builder.build
//				(new FileReader(Path.make(testContractorPipsPublic, object.getIdentifier(), "LIDO.xml").toFile()));
//		
//		List<String> lidoUrls =  mh.getLIDOURL(doc);
//		int danrwRewritings = 0;
//		for(String url : lidoUrls) {
//			if(url.contains(DATA_DANRW_DE)) {
//				danrwRewritings++;
//			}
//		}
//		assertTrue(danrwRewritings==2);		
//	}
}
