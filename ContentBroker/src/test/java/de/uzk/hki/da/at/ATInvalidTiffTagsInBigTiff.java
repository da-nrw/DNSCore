/*
  DA-NRW Software Suite | ContentBroker
  Copyright (C) 2013 Historisch-Kulturwissenschaftliche Informationsverarbeitung
  Universität zu Köln, 2014 LVR InfoKom

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
package de.uzk.hki.da.at;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.hibernate.Session;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.junit.After;
import org.junit.Test;

import de.uzk.hki.da.model.Job;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.service.HibernateUtil;
import de.uzk.hki.da.utils.C;
import de.uzk.hki.da.utils.Path;
import de.uzk.hki.da.utils.XMLUtils;


/**
 * Relates to https://github.com/da-nrw/DNSCore/blob/master/ContentBroker/src/main/markdown/feature_tiff_problem_detection.md
 * 
 * @author Jens Peters
 */
public class ATInvalidTiffTagsInBigTiff extends PREMISBase{
	
	final String PROCESS_MOCK_USER_DECISION = "640";
	String ORIGINAL_NAME = "ATInvalidTiffTagsInBigTiff";
	
	private static final File unpackedDIP = new File("/tmp/ATInvalidTiffTagsInBigTiff");
	Object retrievedObject = null;
	@Test 
	public void testInvalidTiffTagsDetectUserException() throws InterruptedException, IOException {
		String destName = "InvalidTiffTagsDetectUserException";
		
	    ath.putSIPtoIngestArea(ORIGINAL_NAME, "tgz", destName);
		ath.waitForJobToBeInErrorStatus(destName, "4");
	}
	@After
	public void tearDown() throws IOException{
		//FileUtils.deleteDirectory(unpackedDIP);
		//Path.makeFile("tmp",retrievedObject.getIdentifier()+".pack_1.tar").delete(); // retrieved dip
	}
	@Test 
	public void testInvalidTiffTagsPrunedByUser() throws IOException, InterruptedException {
		String destName = "InvalidTiffTagsPrunedByUser";
	    ath.putSIPtoIngestArea(ORIGINAL_NAME, "tgz", destName);
		ath.waitForJobToBeInErrorStatus(destName, "4");
		Job job = ath.getJob(destName);
		modifyPackageDataFromOutside(job);
		ath.awaitObjectState(destName, Object.ObjectStatus.ArchivedAndValidAndNotInWorkflow);
		Object obj = ath.getObject(destName);
		assertSame(obj.getObject_state(),Object.ObjectStatus.ArchivedAndValidAndNotInWorkflow);
	}
	@Test 
	public void testPremisContainsMarkers() throws IOException, InterruptedException {
		String destName = "InvalidTiffTagsPremisContainsMarkers";
	    ath.putSIPtoIngestArea(ORIGINAL_NAME, "tgz", destName);
		ath.waitForJobToBeInErrorStatus(destName, "4");
		Job job = ath.getJob(destName);
		modifyPackageDataFromOutside(job);
		ath.awaitObjectState(destName, Object.ObjectStatus.ArchivedAndValidAndNotInWorkflow);
		retrievedObject = ath.getObject(destName);
		ath.retrieveAIP(retrievedObject,unpackedDIP,"1");
		String unpackedObjectPath = unpackedDIP.getAbsolutePath()+"/";
		
		String folders[] = new File(unpackedObjectPath + "data/").list();
		String repAName="";
		String repBName="";
		for (String f:folders){
			if (f.contains("+a")) repAName = f;
			if (f.contains("+b")) repBName = f;
		}
		verifyPREMISContainsSpecifiedElements(unpackedObjectPath,retrievedObject,repAName,repBName);
	}
	
	@SuppressWarnings("unchecked")
	private void verifyPREMISContainsSpecifiedElements(
			String unpackedObjectPath,
			Object object,
			String repAName,
			String repBName
			) {
		assertTrue(new File(unpackedObjectPath + "data/" +  repBName + "/premis.xml").exists());
		String objectIdentifier = object.getIdentifier();
		
		SAXBuilder builder = XMLUtils.createNonvalidatingSaxBuilder();
		Document doc;
		try {
			doc = builder.build(new File(unpackedObjectPath +  "data/" + repBName + "/premis.xml"));
		} catch (Exception e) {
			throw new RuntimeException("Failed to read premis file", e);
		}
		
		Element rootElement = doc.getRootElement();
		Namespace ns = rootElement.getNamespace();
		
		List<Element> objectElements = rootElement.getChildren("object", ns);
		
		int checkedObjects = 0;
		for (Element e:objectElements){
			String identifierText = e.getChild("objectIdentifier",ns).getChildText("objectIdentifierValue",ns);
			
			if (identifierText.equals(objectIdentifier)) {
				List<Element> identifierEls = e.getChildren("objectIdentifier", ns);
				assertEquals(object.getUrn(), identifierEls.get(1).getChildText("objectIdentifierValue", ns)); // TODO shouldn't it be the unique object identifier?
				String originalName = e.getChildText("originalName", ns);
				assertEquals(object.getOrig_name(),originalName);
				checkedObjects++;
			}
			
			if (identifierText.equals(objectIdentifier + ".pack_1.tar")) {
				assertThat(e.getChildText("originalName",ns)).isEqualTo(retrievedObject.getOrig_name()+ ".tgz");
				checkedObjects++;
			}
						
			if (identifierText.contains("a/268754.tif")){
				verifyPREMISFileObjectHasCertainSubElements(ns, e, "268754.tif", "fmt/353");
				checkedObjects++;
			}
		}
		System.out.println("jjja§ " + checkedObjects);
		assertThat(checkedObjects).isEqualTo(3);	
		List<Element> eventElements = rootElement.getChildren("event", ns);
		int checkedEvents = 0;
		for (Element e:eventElements){
			String eventType = e.getChildText("eventType", ns);
			
			if (eventType.equals("INGEST")){
				String eventDetail = e.getChildText("eventDetail",ns);
				if ( eventDetail.contains("PRUNE")){
					checkedEvents++;
				}
				}
		}
		assertThat(checkedEvents).isEqualTo(1);
	}
	
	/**
	 * In real world scenario, this is being done with Da-Web
	 * @author Jens Peters
	 * @param job
	 */
	private void modifyPackageDataFromOutside(Job job) {
		Session session = HibernateUtil.openSession();
		session.beginTransaction();
		session.refresh(job);
		System.out.println(" set " + C.QUESTION_STORE_ALLOWED_IPTC_ERROR + " " + C.ANSWER_YO);
		job.setQuestion(C.QUESTION_STORE_ALLOWED_IPTC_ERROR);
		job.setStatus(PROCESS_MOCK_USER_DECISION);
		job.setAnswer(C.ANSWER_YO);
		session.update(job);
		session.getTransaction().commit();
		session.close();
	}
}
	
	
