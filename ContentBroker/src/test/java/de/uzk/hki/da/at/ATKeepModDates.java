package de.uzk.hki.da.at;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.apache.commons.io.FileUtils;
import org.hibernate.Session;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.uzk.hki.da.model.Job;
import de.uzk.hki.da.model.Node;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.pkg.ArchiveBuilderFactory;
import de.uzk.hki.da.service.HibernateUtil;
import de.uzk.hki.da.utils.C;
import de.uzk.hki.da.utils.FolderUtils;

public class ATKeepModDates extends AcceptanceTest {
	private static final String ORIG_NAME = "ATKeepModDates";
	private String idiName;
	
	@Before
	public void setUp() throws IOException {
		ath.putSIPtoIngestArea(ORIG_NAME, C.FILE_EXTENSION_TGZ,
				ORIG_NAME);
	}

	@After
	public void tearDown(){
		distributedConversionAdapter.remove("aip/TEST/"+idiName); 
		new File("/tmp/"+idiName+".tar").delete();
		FolderUtils.deleteQuietlySafe(new File("/tmp/"+idiName));
	}

	@Test
	public void test() throws Exception{
		ath.awaitObjectState(ORIG_NAME,Object.ObjectStatus.ArchivedAndValidAndNotInWorkflow);

		// ath.createJob(ORIG_NAME, "900");

		Object o = ath.getObject(ORIG_NAME);
		idiName = o.getIdentifier(); 
		
		Session session = HibernateUtil.openSession();
		session.beginTransaction();
		
		Node node = (Node) session.load(Node.class, localNode .getId());
		
		Job j = new Job();
		j.setResponsibleNodeName(node.getName());
		j.setObject(o);
		j.setDate_created(String.valueOf(new Date().getTime()/1000L));
		j.setDate_modified(String.valueOf(new Date().getTime()/1000L));
		j.setQuestion("RETRIEVE:1");
		j.setStatus("900");
	
		session.save(j);
		
		session.getTransaction().commit();
		session.close();

		ath.waitForJobToBeInStatus(ORIG_NAME, "952");

		String archName = localNode.getUserAreaRootPath()+"/TEST/outgoing/"+idiName+".tar";
		assertTrue(new File(archName).exists());
		
		
		FileUtils.moveFileToDirectory(
				new File(archName), 
				new File("/tmp"), false);
		
		ArchiveBuilderFactory.getArchiveBuilderForFile(new File("/tmp/"+idiName+".tar"))
			.unarchiveFolder(new File("/tmp/"+idiName+".tar"), new File ("/tmp/"));

		String repName = "";
		String dataDir = "/tmp/" + idiName +"/data/";
		
		String[] repos  = new File(dataDir).list();
		for (String reppiNam: repos){
			if (reppiNam.endsWith("a")){
				repName = reppiNam;
				break;
			}
		}
		
		long moddi;
		Date modDate;
		String dayStr;
		SimpleDateFormat dateForm = new SimpleDateFormat("dd.MM.yyyy");
		dateForm.setTimeZone(TimeZone.getTimeZone("GMT"));

		String testDir = "/tmp/" + idiName +"/data/" + repName + "/";

		moddi = new File(testDir + "West_mets.xml").lastModified();
		modDate = new Date(moddi);
		dayStr = dateForm.format(modDate);
		assertEquals("23.12.1978",dayStr);

		moddi = new File(testDir + "unter1/Pest16.txt").lastModified();
		modDate = new Date(moddi);
		dayStr = dateForm.format(modDate);
		assertEquals("04.08.2015",dayStr);

		moddi = new File(testDir + "unter1/Pest17.bmp").lastModified();
		modDate = new Date(moddi);
		dayStr = dateForm.format(modDate);
		assertEquals("25.10.2012",dayStr);

		moddi = new File(testDir + "unter1/unter2/U.tif").lastModified();
		modDate = new Date(moddi);
		dayStr = dateForm.format(modDate);
		assertEquals("25.11.2014",dayStr);

		moddi = new File(testDir + "unter1/unter2/M00000.jpg").lastModified();
		modDate = new Date(moddi);
		dayStr = dateForm.format(modDate);
		assertEquals("21.10.2015",dayStr);
	}
}
