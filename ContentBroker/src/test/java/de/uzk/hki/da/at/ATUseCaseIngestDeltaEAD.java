package de.uzk.hki.da.at;

import static org.junit.Assert.assertTrue;

import java.io.File;
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

import de.uzk.hki.da.core.Path;
import de.uzk.hki.da.metadata.MetadataHelper;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.repository.RepositoryException;

/**
 * 
 * @author Polina Gubaidullina
 *
 */

public class ATUseCaseIngestDeltaEAD extends AcceptanceTest{

	private static final int _1_MINUTE = 60000;
	private static final String ORIG_NAME_ORIG = "ATUseCaseIngestDeltaEAD";
	private static Object object;
	private static final File retrievalFolder = new File("/tmp/unpackedDIP");
	private MetadataHelper mh = new MetadataHelper();
	
	
	@Before
	public void setUp() throws IOException, InterruptedException {
		FileUtils.copyFileToDirectory(new File("src/test/resources/at/"+ORIG_NAME_ORIG+"_orig/"+ORIG_NAME_ORIG+".tgz"), new File("src/test/resources/at"));
		ath.ingest(ORIG_NAME_ORIG);
		FileUtils.deleteQuietly(new File("src/test/resources/at/"+ORIG_NAME_ORIG+".tgz"));
		
		Thread.sleep(_1_MINUTE); // to prevent the repnames to match the ones of the previous package
		FileUtils.copyFileToDirectory(new File("src/test/resources/at/"+ORIG_NAME_ORIG+"_delta/"+ORIG_NAME_ORIG+".tgz"), new File("src/test/resources/at"));
		object = ath.ingest(ORIG_NAME_ORIG);
		FileUtils.deleteQuietly(new File("src/test/resources/at/"+ORIG_NAME_ORIG+".tgz"));
	}
	
	@Test
	public void test() {
		System.out.println("Test");
	}

//	@Test
//	public void testLZA() throws IOException, InterruptedException, RepositoryException, JDOMException{
//		Object lzaObject = ath.retrievePackage(object, retrievalFolder, "2");
//		System.out.println("object identifier: "+lzaObject.getIdentifier());
//		
//		Path tmpObjectDirPath = Path.make(retrievalFolder.getAbsolutePath(), "data");	
//		File[] tmpObjectSubDirs = new File (tmpObjectDirPath.toString()).listFiles();
//		String bRep = "";
//		
//		for (int i=0; i<tmpObjectSubDirs.length; i++) {
//			if(tmpObjectSubDirs[i].getName().contains("+b")) {
//				bRep = tmpObjectSubDirs[i].getName();
//			}
//		}
//		
//		SAXBuilder builder = new SAXBuilder();
//		Document doc1 = builder.build
//				(new FileReader(Path.make(tmpObjectDirPath, bRep, "mets_2_32044.xml").toFile()));
//		List<Element> metsFileElements1 = mh.getMetsFileElements(doc1);
//		Element fileElement1 = metsFileElements1.get(0);
//		assertTrue(mh.getMetsHref(fileElement1).equals("Picture1.tif"));
//		assertTrue(mh.getMetsMimetype(fileElement1).equals("image/tiff"));
//
//		Document doc2 = builder.build
//				(new FileReader(Path.make(tmpObjectDirPath, bRep, "mets_2_32045.xml").toFile()));
//		List<Element> metsFileElements2 = mh.getMetsFileElements(doc2);
//		Element fileElement2 = metsFileElements2.get(0);
//		assertTrue(mh.getMetsHref(fileElement2).equals("Picture2.tif"));
//		assertTrue(mh.getMetsMimetype(fileElement2).equals("image/tiff"));
//		
//		Document doc3 = builder.build
//				(new FileReader(Path.make(tmpObjectDirPath, bRep, "mets_2_32046.xml").toFile()));
//		List<Element> metsFileElements3 = mh.getMetsFileElements(doc3);
//		Element fileElement3 = metsFileElements3.get(0);
//		assertTrue(mh.getMetsHref(fileElement3).equals("Picture3.tif"));
//		assertTrue(mh.getMetsMimetype(fileElement3).equals("image/tiff"));
//		
//		Document doc4 = builder.build
//				(new FileReader(Path.make(tmpObjectDirPath, bRep, "mets_2_32047.xml").toFile()));
//		List<Element> metsFileElements4 = mh.getMetsFileElements(doc4);
//		Element fileElement4 = metsFileElements4.get(0);
//		assertTrue(mh.getMetsHref(fileElement4).equals("Picture4.tif"));
//		assertTrue(mh.getMetsMimetype(fileElement4).equals("image/tiff"));
//		
//		Document doc5 = builder.build
//				(new FileReader(Path.make(tmpObjectDirPath, bRep, "mets_2_32048.xml").toFile()));
//		List<Element> metsFileElements5 = mh.getMetsFileElements(doc5);
//		Element fileElement5 = metsFileElements5.get(0);
//		assertTrue(mh.getMetsHref(fileElement5).equals("Picture5.tif"));
//		assertTrue(mh.getMetsMimetype(fileElement5).equals("image/tiff"));
//	}
}
