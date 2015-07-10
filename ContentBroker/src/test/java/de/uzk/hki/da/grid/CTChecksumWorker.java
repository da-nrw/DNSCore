package de.uzk.hki.da.grid;
/*
DA-NRW Software Suite | ContentBroker
Copyright (C) 2014 LVRInfoKom
Landschaftsverband Rheinland

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
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

	Node node;
	String origName = "ATUseCaseChecksumWorker"; 
	String data_name = "123456.pack_1.tar";
	String coll = "zoneA/cn/aip/TEST/123456";
	String dao = coll + "/" + data_name;
	IrodsCommandLineConnector iclc;
	String fedprefix = "federated";
	String fedcoll = fedprefix + "/" + coll;
	String feddao =  fedcoll + "/" + data_name;
	String md5sum = "";

	String zone = "c-i";
	File tempTest;
	
	private static String tmpDir = "/tmp/forkDir/";
	
	@Before
	public void before() throws IOException {
		HibernateUtil.init("src/main/xml/hibernateCentralDB.cfg.xml.inmem");
		

		node = new Node();
		node.setName("localnode");
		tempTest = createTestFile();
		md5sum = MD5Checksum.getMD5checksumForLocalFile(tempTest);
	}
	
	private int storeCopy(String checksum, Date date) {
		Copy copy;
		copy = new Copy();
		copy.setPath(dao);
		if (checksum!=null) copy.setChecksum(checksum);
		if (date!=null) copy.setChecksumDate(date);
		node.getCopies().add(copy);
		Session session = HibernateUtil.openSession();
		session.beginTransaction();
		session.save(copy);
		session.save(node);
		session.getTransaction().commit();
		session.close();
		return copy.getId();
	}
	
	@Test
	public void reComputationOfNewChecksumAfterSomeTime() throws IOException, InterruptedException {
		Calendar now = Calendar.getInstance();
		now.add(Calendar.DAY_OF_YEAR, -32);
		Date initchecksumdate;
		int id = storeCopy("abcdef5", now.getTime());
		initchecksumdate = now.getTime();

		iclc = new IrodsCommandLineConnector();
		iclc.mkCollection("/"+zone+"/"+fedcoll);
		iclc.put(tempTest,"/"+zone+"/"+feddao);
		Thread.sleep(3000);
		assertTrue(iclc.exists("/"+zone+"/"+feddao));
		ChecksumWorker cw = new ChecksumWorker();
		setAllowedTimeAndNumOfCopyjobsForChecksumWorker(cw);
		cw.setSecondaryCopyPrefix(fedprefix);
		IrodsSystemConnector isc = new IrodsSystemConnector("","");
		isc.setZone("c-i");
		gf.setIrodsSystemConnector(isc);
		cw.setGridFacade(gf);
		cw.setNode(node);
		cw.scheduleTask();
		Session session = HibernateUtil.openSession();
		session.beginTransaction();
		
		Copy recopy = (Copy) session.get(Copy.class,id);
		assertEquals(md5sum,recopy.getChecksum());
		assertNotNull(recopy.getChecksumDate());
		assertTrue(recopy.getChecksumDate().after(initchecksumdate));
		session.close();
		
	}
	
	@Test
	public void noReComputationOfNewChecksumAfterLessTime() throws IOException, InterruptedException {
		Calendar now = Calendar.getInstance();
		now.add(Calendar.DAY_OF_YEAR, -10);
		Date initchecksumdate;
		int id = storeCopy("abcdef5", now.getTime());
		initchecksumdate = now.getTime();

		iclc = new IrodsCommandLineConnector();
		iclc.mkCollection("/"+zone+"/"+fedcoll);
		iclc.put(tempTest,"/"+zone+"/"+feddao);
		Thread.sleep(3000);
		assertTrue(iclc.exists("/"+zone+"/"+feddao));
		ChecksumWorker cw = new ChecksumWorker();
		setAllowedTimeAndNumOfCopyjobsForChecksumWorker(cw);
		cw.setSecondaryCopyPrefix(fedprefix);
		IrodsSystemConnector isc = new IrodsSystemConnector("","");
		isc.setZone("c-i");
		gf.setIrodsSystemConnector(isc);
		cw.setGridFacade(gf);
		cw.setNode(node);
		cw.scheduleTask();
		Session session = HibernateUtil.openSession();
		session.beginTransaction();
		
		Copy recopy = (Copy) session.get(Copy.class,id);
		assertEquals("abcdef5",recopy.getChecksum());
		assertNotNull(recopy.getChecksumDate());
		assertTrue(recopy.getChecksumDate().after(initchecksumdate));
		session.close();
		
	}
	
	@Test
	public void initialComputationOfChecksum() throws IOException, InterruptedException {
		Date initchecksumdate = new Date();;
		
		int id = storeCopy(md5sum, null);
		iclc = new IrodsCommandLineConnector();
		iclc.mkCollection("/"+zone+"/"+fedcoll);
		iclc.put(tempTest,"/"+zone+"/"+feddao);
		Thread.sleep(3000);
		assertTrue(iclc.exists("/"+zone+"/"+feddao));
		ChecksumWorker cw = new ChecksumWorker();
		setAllowedTimeAndNumOfCopyjobsForChecksumWorker(cw);
		cw.setSecondaryCopyPrefix(fedprefix);
		IrodsSystemConnector isc = new IrodsSystemConnector("","");
		isc.setZone("c-i");
		gf.setIrodsSystemConnector(isc);
		cw.setGridFacade(gf);
		cw.setNode(node);
		cw.scheduleTask();	
		Session session = HibernateUtil.openSession();
		session.beginTransaction();
		
		Copy recopy = (Copy) session.get(Copy.class,id);
		assertEquals(md5sum,recopy.getChecksum());
		assertNotNull(recopy.getChecksumDate());
		assertTrue(recopy.getChecksumDate().after(initchecksumdate));
		session.close();
		
	}
	
	@After
	public void cleanup() {
		iclc.remove("/"+zone+"/"+feddao);
	}
	
	private File createTestFile() throws IOException {
		new File(tmpDir).mkdir();
		File temp = new File(tmpDir + "/" + data_name);
		FileWriter writer = new FileWriter(temp ,false);
		writer.write("Hallo Wie gehts?");
		writer.close();
		return temp;
	}
	
	private void setAllowedTimeAndNumOfCopyjobsForChecksumWorker(ChecksumWorker cw) {
		cw.setTrustChecksumForDays(30);
		cw.setStartTime(0);
		cw.setEndTime(24);
		cw.setAllowedNumOfCopyjobs(100);
	}
	

}
