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

import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.model.SystemEvent;
import de.uzk.hki.da.model.User;
import de.uzk.hki.da.service.CSVFileHandler;
import de.uzk.hki.da.service.HibernateUtil;
import de.uzk.hki.da.utils.C;


/**
 * 
 * @author Jens Peters
 * Acceptance Tests for CSV Queries send per file
 *
 */
public class ATCSVQueries extends AcceptanceTest {
	static String ORIGINAL_NAME_ARCHIVED = "ATCSVReportObjectArchived";
	static String ORIGINAL_NAME_ERROR = "ATCSVReportJobInError";
	static String ORIGINAL_NAME_RETRIEVAL = "ATCSVRetrieval";
	
	@BeforeClass
	public static void setUp() throws IOException {
		ath.putAIPToLongTermStorage(ORIGINAL_NAME_ARCHIVED, ORIGINAL_NAME_ARCHIVED, new Date(), 100);
		ath.putSIPtoIngestArea("ATCSVReportJobInError", "tgz", "ATCSVReportJobInError");
		ath.putAIPToLongTermStorage(ORIGINAL_NAME_RETRIEVAL, ORIGINAL_NAME_RETRIEVAL, new Date(), 100);
		
	}
	
	@Test
	public void testCSVReportJobInError ()  throws IOException, InterruptedException {
		ath.waitForJobToBeInErrorStatus(ORIGINAL_NAME_ERROR, C.WORKFLOW_STATUS_DIGIT_USER_ERROR);
		Object object=ath.getObject(ORIGINAL_NAME_ERROR);
		createCSVFile(ORIGINAL_NAME_ERROR);
		File csv = new File(localNode.getUserAreaRootPath()+"/TEST/incoming/"+ORIGINAL_NAME_ERROR+".csv");
		
		assertTrue(csv.exists());
		long lm = csv.lastModified();
		createSystemEvent("CreateStatusReportEvent");
	
		assertTrue(waitUntilFileIsUpdated(csv,lm));
		assertTrue(readCSVFileStatusReporting(ORIGINAL_NAME_ERROR, "identifier",object.getIdentifier()));
		assertTrue(readCSVFileStatusReporting(ORIGINAL_NAME_ERROR, "erfolg","false"));
	}
	
	@Test
	public void testCSVStatusReport () throws IOException, InterruptedException {
		
		createCSVFile(ORIGINAL_NAME_ARCHIVED);
		File csv = new File(localNode.getUserAreaRootPath()+"/TEST/incoming/"+ORIGINAL_NAME_ARCHIVED+".csv");
		
		assertTrue(csv.exists());
		long lm = csv.lastModified();
		createSystemEvent("CreateStatusReportEvent");
	
		assertTrue(waitUntilFileIsUpdated(csv,lm));
		assertTrue(readCSVFileStatusReporting(ORIGINAL_NAME_ARCHIVED, "identifier",ORIGINAL_NAME_ARCHIVED));
		assertTrue(readCSVFileStatusReporting(ORIGINAL_NAME_ARCHIVED, "erfolg","true"));
	}
	
	@Test
	public void testCSVRetrievalRequests () throws IOException, InterruptedException {
		
		createCSVFile(ORIGINAL_NAME_RETRIEVAL);
		File csv = new File(localNode.getUserAreaRootPath()+"/TEST/incoming/"+ORIGINAL_NAME_RETRIEVAL+".csv");
		assertTrue(csv.exists());
		createSystemEvent("CreateRetrievalRequestsEvent");
		ath.waitForJobToBeInStatus(ORIGINAL_NAME_RETRIEVAL, "952");
	}
	
	
	@AfterClass
	public static void tearDown(){
		distributedConversionAdapter.remove("aip/TEST/"+ORIGINAL_NAME_ARCHIVED); 
		distributedConversionAdapter.remove("aip/TEST/"+ORIGINAL_NAME_RETRIEVAL); 
		
		FileUtils.deleteQuietly(new File(localNode.getUserAreaRootPath()+"/TEST/incoming/"+ORIGINAL_NAME_ARCHIVED+".csv"));
		FileUtils.deleteQuietly(new File(localNode.getUserAreaRootPath()+"/TEST/incoming/"+ORIGINAL_NAME_ERROR+".csv"));
		FileUtils.deleteQuietly(new File(localNode.getUserAreaRootPath()+"/TEST/incoming/"+ORIGINAL_NAME_RETRIEVAL+".csv"));
		
		FileUtils.deleteQuietly(new File(localNode.getUserAreaRootPath()+"/TEST/outgoing/"+ORIGINAL_NAME_ARCHIVED+".csv"));
		FileUtils.deleteQuietly(new File(localNode.getUserAreaRootPath()+"/TEST/outgoing/"+ORIGINAL_NAME_ERROR+".csv"));
		FileUtils.deleteQuietly(new File(localNode.getUserAreaRootPath()+"/TEST/outgoing/"+ORIGINAL_NAME_RETRIEVAL+".csv"));
		
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
			if (i>120) {
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
	
	@SuppressWarnings("unchecked")
	private boolean readCSVFileStatusReporting(String origName, String field, String mustcontain) throws IOException {
		CSVFileHandler csf = new CSVFileHandler();
		System.out.println("search CSV Report field " + field + " value " + mustcontain);
		csf.parseFile(new File(localNode.getUserAreaRootPath()+"/TEST/outgoing/"+origName+".csv"));
		for (Map<String, java.lang.Object> csvEntry :csf.getCsvEntries()) {
			if (csvEntry.get("origName").equals(origName))
			if (csvEntry.get(field).equals(mustcontain)) return true;
 		} 
		System.out.println("nothing found in csv reports!");
		return false;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private int createCSVFile(String origName) throws IOException {
		CSVFileHandler csf = new CSVFileHandler();
		ArrayList<Map> csvEntries = new ArrayList();
		Map<String, java.lang.Object> csvEntry = new HashMap<String, java.lang.Object>();
		csf.setEncoding("CP1252");
		csvEntry.put("origName", (java.lang.Object) origName);
		csvEntries.add(csvEntry);
		csf.setCsvEntries(csvEntries);
		csf.persistStates(new File(localNode.getUserAreaRootPath()+"/TEST/incoming/"+origName+".csv"));
		return csvEntries.size();
	}
		
}
