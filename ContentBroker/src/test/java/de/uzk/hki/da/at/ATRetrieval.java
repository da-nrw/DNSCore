/*
  DA-NRW Software Suite | ContentBroker
  Copyright (C) 2013 Historisch-Kulturwissenschaftliche Informationsverarbeitung
  Universität zu Köln

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

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.apache.commons.io.FileUtils;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import de.uzk.hki.da.cb.PostRetrievalAction;
import de.uzk.hki.da.model.Job;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.pkg.ArchiveBuilderFactory;
import de.uzk.hki.da.service.HibernateUtil;
import de.uzk.hki.da.utils.C;
import de.uzk.hki.da.utils.FolderUtils;
import gov.loc.repository.bagit.Bag;
import gov.loc.repository.bagit.BagFactory;

/**
 * Relates to AK-T/05 RetrieveObject - Happy Path Scenario.
 * @author Daniel M. de Oliveira
 */
public class ATRetrieval extends AcceptanceTest{
	
	private static final String identifier = "ATRetrieval_identifier";
	static String originalName = "ATRetrieval";
	@BeforeClass
	public static void setUp() {
		removeTMPFiles();
		try {
			ath.putAIPToLongTermStorage(identifier, originalName, new Date(), 100);
		} catch (IOException e) {
			e.printStackTrace();
			fail(e.toString());
		}
	}
	
	@AfterClass
	public static void tearDown(){
		distributedConversionAdapter.remove("aip/TEST/"+identifier); // TODO does it work?
		removeTMPFiles();
	}
	
	private static void removeTMPFiles(){
		new File("/tmp/"+identifier+".tar").delete();
		FolderUtils.deleteQuietlySafe(new File("/tmp/"+identifier));
	}
	
	@Test
	public void testHappyPath() throws Exception{
		ath.createJob(originalName, "900");
		ath.waitForJobToBeInStatus(originalName, "952");
		
		System.out.println(new File(localNode.getUserAreaRootPath()+"/TEST/outgoing/"+identifier+".tar").getAbsolutePath());
		assertTrue(new File(localNode.getUserAreaRootPath()+"/TEST/outgoing/"+identifier+".tar").exists());
		
		FileUtils.moveFileToDirectory(
				new File(localNode.getUserAreaRootPath()+"/TEST/outgoing/"+identifier+".tar"), 
				new File("/tmp"), false);
		//after moving the retrieval-file, PostRetrievalAction(952) have to end the workflow
		Thread.sleep((int)(PostRetrievalAction.PAUSE_DELAY * 1.5));
		ath.awaitObjectState(originalName, Object.ObjectStatus.ArchivedAndValidAndNotInWorkflow); 
		ArchiveBuilderFactory.getArchiveBuilderForFile(new File("/tmp/"+identifier+".tar"))
			.unarchiveFolder(new File("/tmp/"+identifier+".tar"), new File ("/tmp/"));
		
		if (!new File("/tmp/"+identifier+"/data/"+"image/713091.tif").exists()) fail();
		if (!new File("/tmp/"+identifier+"/data/"+"premis.xml").exists()) fail();
		
		if (!bagIsValid(new File("/tmp/"+identifier))) fail();
	}

	private boolean bagIsValid(File file){
		BagFactory bagFactory = new BagFactory();
		Bag bag = bagFactory.createBag(file);
		return bag.verifyValid().isSuccess();
	}	

	@Test
	public void testTimebasedRemoveRetrievalAfter14DayBeforeTimeout() throws Exception {
		int usualRetrievalTime = localNode.getRetrieval_remain_time();

		String createTime = String.valueOf(new Date().getTime() / 1000L - ((usualRetrievalTime * 24 - 12) * 60 * 60));// - 1/2 Day for timeout
		ath.createJob(originalName, "900", createTime);
		ath.waitForJobToBeInStatus(originalName, "952");

		System.out.println(
				new File(localNode.getUserAreaRootPath() + "/TEST/outgoing/" + identifier + ".tar").getAbsolutePath());
		assertTrue(new File(localNode.getUserAreaRootPath() + "/TEST/outgoing/" + identifier + ".tar").exists());

		Thread.sleep((int)(PostRetrievalAction.PAUSE_DELAY * 1.5));
		ath.waitForJobToBeInStatus(originalName, "952");
		assertTrue(new File(localNode.getUserAreaRootPath() + "/TEST/outgoing/" + identifier + ".tar").exists());
		new File(localNode.getUserAreaRootPath() + "/TEST/outgoing/" + identifier + ".tar").delete();
		Thread.sleep((int)(PostRetrievalAction.PAUSE_DELAY * 1.5));
		assertTrue(ath.getJob(originalName) == null);
	}

	
	@Test
	public void testTimebasedRemoveRetrievalAfter14DayAfterTimeout() throws Exception {
		int usualRetrievalTime = localNode.getRetrieval_remain_time();

		String createTime = String.valueOf(new Date().getTime() / 1000L - ((usualRetrievalTime * 24 + 12) * 60 * 60));// +1/2 Day after timeout
		ath.createJob(originalName, "900", createTime);
		//ath.waitForJobToBeInStatus(originalName, "952");

		Thread.sleep((int)(PostRetrievalAction.PAUSE_DELAY * 1.5));
		ath.awaitObjectState(originalName, Object.ObjectStatus.ArchivedAndValidAndNotInWorkflow);
		assertTrue(ath.getJob(originalName) == null);
		assertTrue(!new File(localNode.getUserAreaRootPath() + "/TEST/outgoing/" + identifier + ".tar").exists());

	}
}
