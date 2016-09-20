package de.uzk.hki.da.at;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import de.uzk.hki.da.metadata.MetadataHelper;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.model.WorkArea;
import de.uzk.hki.da.repository.RepositoryException;
import de.uzk.hki.da.utils.C;
import de.uzk.hki.da.utils.FolderUtils;
import de.uzk.hki.da.utils.Path;
import de.uzk.hki.da.utils.XMLUtils;

/**
 * 
 * @author Polina Gubaidullina
 *
 */

public class ATMetadataUpdatesDeltaMETS extends AcceptanceTest{

	private static final String ORIG_NAME_ORIG = "ATMetadataUpdatesDeltaMETS";
	private static final String DATA_DANRW_DE = "http://data.danrw.de";
	private static final File retrievalFolder = new File("/tmp/unpackedMetsMods");
	private static Path contractorsPipsPublic;
	private static Object object;
	private static MetadataHelper mh = new MetadataHelper();
	
	@BeforeClass
	public static void setUp() throws IOException, InterruptedException {
		
		contractorsPipsPublic = Path.make(localNode.getWorkAreaRootPath(),WorkArea.PIPS, WorkArea.PUBLIC, C.TEST_USER_SHORT_NAME);
		
		ath.putSIPtoIngestArea(ORIG_NAME_ORIG, "tgz", ORIG_NAME_ORIG);
		ath.awaitObjectState(ORIG_NAME_ORIG,Object.ObjectStatus.ArchivedAndValidAndNotInWorkflow);
		ath.waitForDefinedPublishedState(ORIG_NAME_ORIG);
		ath.putSIPtoIngestArea(ORIG_NAME_ORIG+"_delta_one_file", "tgz", ORIG_NAME_ORIG);
		ath.awaitObjectState(ORIG_NAME_ORIG,Object.ObjectStatus.InWorkflow);
		ath.awaitObjectState(ORIG_NAME_ORIG,Object.ObjectStatus.ArchivedAndValidAndNotInWorkflow);
		ath.waitForDefinedPublishedState(ORIG_NAME_ORIG);
		
		object = ath.getObject(ORIG_NAME_ORIG);
	}
	
	@AfterClass
	public static void tearDownAfterClass() throws IOException{
		FolderUtils.deleteDirectorySafe(retrievalFolder);
		Path.makeFile("tmp",object.getIdentifier()+".pack_2.tar").delete(); // retrieved dip
	}
	
	@Test
	public void testLZA() throws IOException, InterruptedException, RepositoryException, JDOMException{
		ath.retrieveAIP(object,retrievalFolder,"2");
		
		System.out.println("object identifier: "+object.getIdentifier());
		
		Path tmpObjectDirPath = Path.make(retrievalFolder.getAbsolutePath(), "data");	
		File[] tmpObjectSubDirs = new File (tmpObjectDirPath.toString()).listFiles();
		String bRep = "";
		
		for (int i=0; i<tmpObjectSubDirs.length; i++) {
			if(tmpObjectSubDirs[i].getName().contains("+b")) {
				bRep = tmpObjectSubDirs[i].getName();
			}
		}
		
		SAXBuilder builder = XMLUtils.createNonvalidatingSaxBuilder();
		String metsFileName = "export_mets.xml";
		FileReader fr = new FileReader(Path.make(tmpObjectDirPath, bRep, metsFileName).toFile());
		Document doc = builder.build(fr);
		
		List<Element> metsFileElements = mh.getMetsFileElements(doc);
		
		assertTrue(metsFileElements.size()==29);
		
//		orig
		Boolean tif801618Exists = false;
		Boolean tif801619Exists = false;
		Boolean tif801620Exists = false;
		Boolean tif801622Exists = false;
		Boolean tif801624Exists = false;
		Boolean tif801625Exists = false;
		Boolean tif801626Exists = false;
		Boolean tif801627Exists = false;
		Boolean tif801628Exists = false;
		Boolean tif801629Exists = false;
		Boolean tif801630Exists = false;
		Boolean tif801631Exists = false;
		Boolean tif801632Exists = false;
		Boolean tif801633Exists = false;
		Boolean tif801634Exists = false;
		Boolean tif801635Exists = false;
		Boolean tif801636Exists = false;
		Boolean tif801637Exists = false;
		Boolean tif801638Exists = false;
		Boolean tif801639Exists = false;
		Boolean tif801640Exists = false;
		Boolean tif801642Exists = false;
		Boolean tif801643Exists = false;
		Boolean tif801644Exists = false;
		Boolean tif801645Exists = false;
		Boolean tif801648Exists = false;
		Boolean tif801650Exists = false;
		Boolean tif801651Exists = false;
		
//		delta
		Boolean tif801616Exists = false;
		
		for(Element e : metsFileElements) {
			String href = mh.getMetsHref(e);
			
			if(href.equals("image/801618.tif")) {
				tif801618Exists = true;
			} else if(href.equals("image/801619.tif")) {
				tif801619Exists = true;
			} else if(href.equals("image/801620.tif")) {
				tif801620Exists = true;
			} else if(href.equals("image/801622.tif")) {
				tif801622Exists = true;
			} else if (href.equals("image/801624.tif")) {
				tif801624Exists = true;
			} else if(href.equals("image/801625.tif")) {
				tif801625Exists = true;
			} else if (href.equals("image/801626.tif")) {
				tif801626Exists = true;
			} else if (href.equals("image/801627.tif")) {
				tif801627Exists = true;
			} else if (href.equals("image/801628.tif")) {
				tif801628Exists = true;
			} else if (href.equals("image/801629.tif")) {
				tif801629Exists = true;
			} else if (href.equals("image/801630.tif")) {
				tif801630Exists = true;
			} else if (href.equals("image/801631.tif")) {
				tif801631Exists = true;
			} else if (href.equals("image/801632.tif")) {
				tif801632Exists = true;
			} else if (href.equals("image/801633.tif")) {
				tif801633Exists = true;
			} else if (href.equals("image/801634.tif")) {
				tif801634Exists = true;
			} else if (href.equals("image/801635.tif")) {
				tif801635Exists = true;
			} else if (href.equals("image/801636.tif")) {
				tif801636Exists = true;
			} else if (href.equals("image/801637.tif")) {
				tif801637Exists = true;
			} else if (href.equals("image/801638.tif")) {
				tif801638Exists = true;
			} else if (href.equals("image/801639.tif")) {
				tif801639Exists = true;
			} else if (href.equals("image/801640.tif")) {
				tif801640Exists = true;
			} else if (href.equals("image/801642.tif")) {
				tif801642Exists = true;
			} else if (href.equals("image/801643.tif")) {
				tif801643Exists = true;
			} else if (href.equals("image/801644.tif")) {
				tif801644Exists = true;
			} else if (href.equals("image/801645.tif")) {
				tif801645Exists = true;
			} else if (href.equals("image/801648.tif")) {
				tif801648Exists = true;
			}  else if (href.equals("image/801650.tif")) {
				tif801650Exists = true;
			}  else if (href.equals("image/801651.tif")) {
				tif801651Exists = true;
			}  else if (href.equals("image/801616.tif")) {
				tif801616Exists = true;
			} 
			
			assertTrue(mh.getMimetypeInMets(e).equals("image/tiff"));
			assertTrue(mh.getMetsLoctype(e).equals("URL"));
		}
		
		assertTrue(tif801618Exists
				&&tif801619Exists
				&&tif801620Exists
				&&tif801622Exists
				&&tif801624Exists
				&&tif801625Exists
				&&tif801626Exists
				&&tif801627Exists
				&&tif801628Exists
				&&tif801629Exists
				&&tif801630Exists
				&&tif801631Exists
				&&tif801632Exists
				&&tif801633Exists
				&&tif801634Exists
				&&tif801635Exists
				&&tif801636Exists
				&&tif801637Exists
				&&tif801638Exists
				&&tif801639Exists
				&&tif801640Exists
				&&tif801642Exists
				&&tif801643Exists
				&&tif801644Exists
				&&tif801645Exists
				&&tif801648Exists
				&&tif801650Exists
				&&tif801651Exists
				&&tif801616Exists);
		
		fr.close();
		
	}
	
	@Test
	public void testPres() throws FileNotFoundException, JDOMException, IOException{
		
		object = ath.getObject(ORIG_NAME_ORIG);
		
		contractorsPipsPublic = Path.make(localNode.getWorkAreaRootPath(),WorkArea.PIPS, WorkArea.PUBLIC, "TEST");
		
		
		SAXBuilder builder = XMLUtils.createNonvalidatingSaxBuilder();
		FileReader fr = new FileReader(Path.make(contractorsPipsPublic, object.getIdentifier(), C.CB_PACKAGETYPE_METS+C.FILE_EXTENSION_XML).toFile());
		Document doc = builder.build(fr);
		
		List<Element> metsFileElements = mh.getMetsFileElements(doc);
		int danrwRewritings = 0;
		for(Element e : metsFileElements) {
			String href = mh.getMetsHref(e);
			if(href.contains(DATA_DANRW_DE)) {
				danrwRewritings++;
			}
		}
		assertTrue(danrwRewritings==29);	
		fr.close();
	}
}
