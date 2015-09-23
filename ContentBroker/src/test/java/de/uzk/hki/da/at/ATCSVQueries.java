/*
  DA-NRW Software Suite | ContentBroker
  Copyright (C) 2015 LVR InfoKom

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

/**
 * @author jens Peters
 */
package de.uzk.hki.da.at;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.hibernate.Session;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import de.uzk.hki.da.model.SystemEvent;
import de.uzk.hki.da.model.User;
import de.uzk.hki.da.service.CSVFileHandler;
import de.uzk.hki.da.service.HibernateUtil;


/**
 * 
 * @author Jens Peters
 * Acceptance Tests for CSV Queries 
 *
 */
public class ATCSVQueries extends AcceptanceTest {
	static String ORIGINAL_NAME = "ATCSVQueries";
    
	@BeforeClass
	public static void setUp() throws IOException {
		ath.putAIPToLongTermStorage(ORIGINAL_NAME, ORIGINAL_NAME, new Date(), 100);
	}
	
	@Test
	public void testCSVStatusReport () throws IOException, InterruptedException {
		
		createCSVFileForStatusReporting(ORIGINAL_NAME);
		File csv = new File(localNode.getUserAreaRootPath()+"/TEST/incoming/"+ORIGINAL_NAME+".csv");
		
		assertTrue(csv.exists());
		long lm = csv.lastModified();
		createSystemEvent("CreateStatusReportEvent");
	
		assertTrue(waitUntilFileIsUpdated(csv,lm));
		assertTrue(readCSVFileStatusReporting(ORIGINAL_NAME));	
	}
	
	@Test
	public void testCSVRetrievalRequests () throws IOException, InterruptedException {
		
		createCSVFileForStatusReporting(ORIGINAL_NAME);
		File csv = new File(localNode.getUserAreaRootPath()+"/TEST/incoming/"+ORIGINAL_NAME+".csv");
		assertTrue(csv.exists());
		createSystemEvent("CreateRetrievalRequestsEvent");
		ath.waitForJobToBeInStatus(ORIGINAL_NAME, "952");
	}
	
	
	@AfterClass
	public static void tearDown(){
		distributedConversionAdapter.remove("aip/TEST/"+ORIGINAL_NAME); // TODO does it work?
		FileUtils.deleteQuietly(new File(localNode.getUserAreaRootPath()+"/TEST/incoming/"+ORIGINAL_NAME+".csv"));
	}

	
	//---------------------------------------------------------------------------------
	
	private boolean waitUntilFileIsUpdated( File file, long timeStamp ) throws InterruptedException {
	
		long timeStampOld;
		timeStampOld = timeStamp;
		int i = 0;
		while (timeStampOld==timeStamp) {
			Thread.sleep(1000l);
			timeStamp = file.lastModified();
			i++;
			if (i>60) {
				System.out.println(file + " was NOT changed!");
				return false;
			}
		}
		System.out.println(file + " was changed!");
		return true;
	}
	
	private void createSystemEvent(String eventName) {
		
		try {
		SystemEvent se = new SystemEvent();
		se.setNode(localNode);
		se.setType(eventName);
		User user = new User();
		user.setId(1);
		user.setShort_name("TEST");
		Session session = HibernateUtil.openSession();
		session.beginTransaction();
		se.setOwner(user);
		session.save(se);
		session.getTransaction().commit();
		session.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private boolean readCSVFileStatusReporting(String identifier) throws IOException {
		CSVFileHandler csf = new CSVFileHandler();
		System.out.println("search CSV Report for " + identifier);
		csf.parseFile(new File(localNode.getUserAreaRootPath()+"/TEST/incoming/"+identifier+".csv"));
		for (Map<String, java.lang.Object> csvEntry :csf.getCsvEntries()) {
			if (csvEntry.get("identifier").equals(identifier)) return true;
 		} 
		return false;
	}
	
	private int createCSVFileForStatusReporting(String identifier) throws IOException {
		CSVFileHandler csf = new CSVFileHandler();
		ArrayList<Map> csvEntries = new ArrayList();
		Map<String, java.lang.Object> csvEntry = new HashMap<String, java.lang.Object>();
		csf.setEncoding("CP1252");
		csvEntry.put("origName", (java.lang.Object) ORIGINAL_NAME);
		csvEntries.add(csvEntry);
		csf.setCsvEntries(csvEntries);
		csf.persistStates(new File(localNode.getUserAreaRootPath()+"/TEST/incoming/"+identifier+".csv"));
		return csvEntries.size();
	}
		
}
