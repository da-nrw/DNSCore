package de.uzk.hki.da.grid;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

import org.hibernate.Session;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.uzk.hki.da.core.ChecksumWorker;
import de.uzk.hki.da.model.Copy;
import de.uzk.hki.da.model.Node;
import de.uzk.hki.da.service.HibernateUtil;
import de.uzk.hki.da.utils.MD5Checksum;

public class CTChecksumWorker {

	IrodsGridFacade gf = new IrodsGridFacade();
	Copy copy;
	String origName = "ATUseCaseChecksumWorker"; 
	String data_name = "123456.pack_1.tar";
	String coll = "cn/aip/TEST/123456";
	String dao = coll + "/" + data_name;
	IrodsCommandLineConnector iclc;
	String fedprefix = "federated";
	String fedcoll = fedprefix + "/" + coll;
	String feddao =  fedcoll + "/" + data_name;
	String md5sum = "";
	Date initchecksumdate;
	
	Node node;
	private static String tmpDir = "/tmp/forkDir/";
	
	@Before
	public void before() throws IOException {
		HibernateUtil.init("src/main/xml/hibernateCentralDB.cfg.xml.inmem");
		node = new Node();
		
		copy = new Copy();
		copy.setPath(dao);
		initchecksumdate=new Date();
		copy.setChecksumDate(initchecksumdate);
		node.setName("localnode");
		
		node.getCopies().add(copy);
		Session session = HibernateUtil.openSession();
		session.beginTransaction();
		session.save(copy);
		session.save(node);
		session.getTransaction().commit();
		session.close();
	}
	
	@Test
	public void testScheduleTask() throws IOException {
		
		iclc = new IrodsCommandLineConnector();
		
		File tempTest = createTestFile();
		md5sum = MD5Checksum.getMD5checksumForLocalFile(tempTest);
	
		iclc.mkCollection(fedcoll);
		iclc.put(tempTest, feddao);
		
		ChecksumWorker cw = new ChecksumWorker();
		cw.setSecondaryCopyPrefix(fedprefix);
		IrodsSystemConnector isc = new IrodsSystemConnector("","");
		isc.setZone("c-i");
		gf.setIrodsSystemConnector(isc);
		cw.setGridFacade(gf);
		cw.setNode(node);
		cw.scheduleTask();
		
		
		Session session = HibernateUtil.openSession();
		session.beginTransaction();
		
		Copy recopy = (Copy) session.get(Copy.class,copy.getId());

		assertEquals(md5sum,recopy.getChecksum());
		assertNotNull(recopy.getChecksumDate());
		assertTrue(recopy.getChecksumDate().after(initchecksumdate));
		session.close();
		
	}
	
	@After
	public void cleanup() {
		iclc.remove(feddao);
	}
	
	private File createTestFile() throws IOException {
		new File(tmpDir).mkdir();
		File temp = new File(tmpDir + "/" + data_name);
		FileWriter writer = new FileWriter(temp ,false);
		writer.write("Hallo Wie gehts?");
		writer.close();
		return temp;
	}
	

}
