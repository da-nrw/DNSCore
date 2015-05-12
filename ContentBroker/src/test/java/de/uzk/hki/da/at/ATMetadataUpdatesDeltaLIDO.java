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
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import de.uzk.hki.da.core.C;
import de.uzk.hki.da.metadata.MetadataHelper;
import de.uzk.hki.da.metadata.XMLUtils;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.model.WorkArea;
import de.uzk.hki.da.repository.RepositoryException;
import de.uzk.hki.da.util.Path;

/**
 * 
 * @author Polina Gubaidullina
 *
 */

public class ATMetadataUpdatesDeltaLIDO extends AcceptanceTest{
	
	private static final String ORIG_NAME_ORIG = "ATMetadataUpdatesDeltaLIDO";
	private static final String DATA_DANRW_DE = "http://data.danrw.de";
	private static Object object;
	private static final File retrievalFolder = new File("/tmp/LIDOunpacked");
	private static Path contractorsPipsPublic;
	private static MetadataHelper mh = new MetadataHelper();
	
	@BeforeClass
	public static void setUp() throws IOException, InterruptedException {
		
		ath.putSIPtoIngestArea(ORIG_NAME_ORIG+"_orig", "tgz", ORIG_NAME_ORIG);
		ath.awaitObjectState(ORIG_NAME_ORIG,Object.ObjectStatus.ArchivedAndValidAndNotInWorkflow);
		ath.putSIPtoIngestArea(ORIG_NAME_ORIG+"_delta", "tgz", ORIG_NAME_ORIG);
		ath.awaitObjectState(ORIG_NAME_ORIG,Object.ObjectStatus.InWorkflow);
		ath.awaitObjectState(ORIG_NAME_ORIG,Object.ObjectStatus.ArchivedAndValidAndNotInWorkflow);
		
		ath.waitForDefinedPublishedState(ORIG_NAME_ORIG);
		object = ath.getObject(ORIG_NAME_ORIG);
		
		contractorsPipsPublic = Path.make(localNode.getWorkAreaRootPath(),WorkArea.PIPS, WorkArea.PUBLIC, C.TEST_USER_SHORT_NAME);
	}
	
	@AfterClass
	public static void tearDown() throws IOException{
		FileUtils.deleteDirectory(retrievalFolder);
	}

	@Test
	public void testLZA() throws IOException, InterruptedException, RepositoryException, JDOMException{

		ath.retrieveAIP(object,retrievalFolder,"2");
		
		Path tmpObjectDirPath = Path.make(retrievalFolder.getAbsolutePath(), "data");	
		File[] tmpObjectSubDirs = new File (tmpObjectDirPath.toString()).listFiles();
		String bRep = "";
		
		for (int i=0; i<tmpObjectSubDirs.length; i++) {
			if(tmpObjectSubDirs[i].getName().contains("+b")) {
				bRep = tmpObjectSubDirs[i].getName();
			}
		}
		
		SAXBuilder builder = XMLUtils.createNonvalidatingSaxBuilder();
		String LidoFileName = "LIDO-Testexport2014-07-04-FML-Auswahl.xml";
		Document doc = builder.build
				(new FileReader(Path.make(tmpObjectDirPath, bRep, LidoFileName).toFile()));
		
		List<String> lidoUrls =  mh.getLIDOURL(doc);
		
		Boolean pic1Exists = false;
		Boolean pic2Exists = false;
		
		for(String url : lidoUrls) {
			if(url.equals("Picture1.tif")) {
				pic1Exists = true;
			}
			if(url.equals("Picture2.tif")) {
				pic2Exists = true;
			}
		}
		
		assertTrue(pic1Exists);
		assertTrue(pic2Exists);
	}
	
	@Test
	public void testPres() throws FileNotFoundException, JDOMException, IOException{
		
		SAXBuilder builder = XMLUtils.createNonvalidatingSaxBuilder();
		Document doc = builder.build
				(new FileReader(Path.make(contractorsPipsPublic, object.getIdentifier(), "LIDO.xml").toFile()));
		
		List<String> lidoUrls =  mh.getLIDOURL(doc);
		int danrwRewritings = 0;
		for(String url : lidoUrls) {
			if(url.contains(DATA_DANRW_DE)) {
				danrwRewritings++;
			}
		}
		assertTrue(danrwRewritings==2);		
	}
}
