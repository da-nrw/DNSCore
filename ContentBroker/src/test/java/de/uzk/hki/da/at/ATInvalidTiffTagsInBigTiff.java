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

import java.io.IOException;

import org.hibernate.Session;
import org.junit.Test;

import de.uzk.hki.da.model.Job;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.service.HibernateUtil;
import de.uzk.hki.da.utils.C;


/**
 * Relates to https://github.com/da-nrw/DNSCore/blob/master/ContentBroker/src/main/markdown/feature_tiff_problem_detection.md
 * 
 * @author Jens Peters
 */
public class ATInvalidTiffTagsInBigTiff extends AcceptanceTest{
	
	final String PROCESS_MOCK_USER_DECISION = "640";

	@Test 
	public void testInvalidTiffTagsDetectUserException() throws IOException, InterruptedException {
		
		String ORIGINAL_NAME = "ATInvalidTiffTagsInBigTiff";
	    ath.putSIPtoIngestArea(ORIGINAL_NAME, "tgz", ORIGINAL_NAME);
		ath.waitForJobToBeInErrorStatus(ORIGINAL_NAME, "4");
	
	}
	@Test 
	public void testInvalidTiffTagsPrunedByUser() throws IOException, InterruptedException {
		
		String ORIGINAL_NAME = "ATInvalidTiffTagsInBigTiff";
	    ath.putSIPtoIngestArea(ORIGINAL_NAME, "tgz", ORIGINAL_NAME);
		ath.waitForJobToBeInErrorStatus(ORIGINAL_NAME, "4");
		Job job = ath.getJob(ORIGINAL_NAME);
		modifyPackageDataFromOutside(job);
		ath.awaitObjectState(ORIGINAL_NAME, Object.ObjectStatus.ArchivedAndValidAndNotInWorkflow);
	
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
	
	
