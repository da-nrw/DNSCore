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

import static de.uzk.hki.da.core.C.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.junit.Test;

import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.repository.RepositoryException;
import de.uzk.hki.da.util.Path;

/**
 * @author Daniel M. de Oliveira
 */
public class ATUseCaseTimeBasedPublication extends AcceptanceTest{

	private static final String XLINK_NAMESPACE = "http://www.w3.org/1999/xlink";
	private static final String METS_NAMESPACE = "http://www.loc.gov/METS/";

	private static final String ORIG_NAME_PREFIX =  "ATUCTimeBasedPubl";
	private Namespace mETS_NS;
	private Namespace xLINK_NS;
	
	/**
	 * @author ???
	 * 
	 * @throws InterruptedException
	 * @throws IOException
	 * @throws JDOMException 
	 * @throws RepositoryException 
	 */
	
	@Test
	public void testUpdateUrls() throws InterruptedException, IOException, JDOMException, RepositoryException{
		
		String name = "UpdateUrls";
		ath.createObjectAndJob(ORIG_NAME_PREFIX+name,WORKFLOW_STATUS_START___TIME_BASED_PUBLICATION_OBJECT_TO_WORK_AREA_ACTION,"METS","mets.xml");

		ath.waitForJobsToFinish(ORIG_NAME_PREFIX+name);
		Object object = ath.fetchObjectFromDB(ORIG_NAME_PREFIX+name);
		assertNotNull(object);
		
		assertNotNull(repositoryFacade.retrieveFile(object.getIdentifier(), preservationSystem.getOpenCollectionName(), "_0c32b463b540e3fee433961ba5c491d6.jpg"));
		assertNotNull(repositoryFacade.retrieveFile(object.getIdentifier(), preservationSystem.getClosedCollectionName(), "_0c32b463b540e3fee433961ba5c491d6.jpg"));
		
		
		File publFile = Path.makeFile(localNode.getWorkAreaRootPath(),
				WA_PIPS,WA_PUBLIC,object.getContractor().getShort_name(),
				object.getIdentifier(),CB_PACKAGETYPE_METS+FILE_EXTENSION_XML);
		assertTrue(publFile.exists());
				
		File instFile = Path.makeFile(localNode.getWorkAreaRootPath(),
				WA_PIPS,WA_INSTITUTION,object.getContractor().getShort_name(),
				object.getIdentifier(),CB_PACKAGETYPE_METS+FILE_EXTENSION_XML);
		assertTrue(instFile.exists());
		assertEquals(PUBLISHEDFLAG_PUBLIC+
				PUBLISHEDFLAG_INSTITUTION, object.getPublished_flag());
		
		mETS_NS = Namespace.getNamespace(METS_NAMESPACE);
		xLINK_NS = Namespace.getNamespace(XLINK_NAMESPACE);
		
		SAXBuilder builder = new SAXBuilder();
		Document publDoc = builder.build(new FileInputStream(publFile));
		assertEquals("_0c32b463b540e3fee433961ba5c491d6.jpg", getUrl(publDoc));
		Document instDoc = builder.build(new FileInputStream(instFile));
		assertEquals("_0c32b463b540e3fee433961ba5c491d6.jpg", getUrl(instDoc));
	}

	private String getUrl(Document doc) {
		return doc.getRootElement()
				.getChild("fileSec", mETS_NS)
				.getChild("fileGrp", mETS_NS)
				.getChild("file", mETS_NS)
				.getChild("FLocat", mETS_NS)
				.getAttributeValue("href", xLINK_NS);
	}
	
	@Test
	public void testPublishInstOnly() throws InterruptedException, IOException, RepositoryException{
		
		String name = "InstOnly";
		ath.createObjectAndJob(ORIG_NAME_PREFIX+name,WORKFLOW_STATUS_START___TIME_BASED_PUBLICATION_OBJECT_TO_WORK_AREA_ACTION);
		ath.waitForJobsToFinish(ORIG_NAME_PREFIX+name);
		Object object = ath.fetchObjectFromDB(ORIG_NAME_PREFIX+name);
		assertNotNull(object);
		assertNull(repositoryFacade.retrieveFile(object.getIdentifier(), preservationSystem.getOpenCollectionName(), "_0c32b463b540e3fee433961ba5c491d6.jpg"));
		assertNotNull(repositoryFacade.retrieveFile(object.getIdentifier(), preservationSystem.getClosedCollectionName(), "_0c32b463b540e3fee433961ba5c491d6.jpg"));
		assertEquals(PUBLISHEDFLAG_INSTITUTION,object.getPublished_flag());
	}
	
	@Test
	public void testNoPubWithLawSetForAudiencePublic() throws InterruptedException, IOException, RepositoryException{
		
		String name = "NoPubWithLawSet";
		ath.createObjectAndJob(ORIG_NAME_PREFIX+name,WORKFLOW_STATUS_START___TIME_BASED_PUBLICATION_OBJECT_TO_WORK_AREA_ACTION);
		ath.waitForJobsToFinish(ORIG_NAME_PREFIX+name);
		Object object = ath.fetchObjectFromDB(ORIG_NAME_PREFIX+name);
		assertNotNull(object);
		assertFalse(repositoryFacade.objectExists(object.getIdentifier(), preservationSystem.getOpenCollectionName()));
		assertEquals(PUBLISHEDFLAG_INSTITUTION, object.getPublished_flag());
		
	}
	
	@Test
	public void testNoPubWithStartDateSetForAudiencePublic() throws InterruptedException, IOException, RepositoryException{
		
		String name = "NoPubWithStartDateSet";
		ath.createObjectAndJob(ORIG_NAME_PREFIX+name,WORKFLOW_STATUS_START___TIME_BASED_PUBLICATION_OBJECT_TO_WORK_AREA_ACTION);
		ath.waitForJobsToFinish(ORIG_NAME_PREFIX+name);
		Object object = ath.fetchObjectFromDB(ORIG_NAME_PREFIX+name);
		assertNotNull(object);
		assertFalse(repositoryFacade.objectExists(object.getIdentifier(), preservationSystem.getOpenCollectionName()));
		assertEquals(PUBLISHEDFLAG_INSTITUTION, object.getPublished_flag());
		
	}
	
	
	@Test
	public void testPublishNothing() throws InterruptedException, IOException, RepositoryException{
		
		String name = "PublishNothing";
		ath.createObjectAndJob(ORIG_NAME_PREFIX+name,WORKFLOW_STATUS_START___TIME_BASED_PUBLICATION_OBJECT_TO_WORK_AREA_ACTION);
		ath.waitForJobsToFinish(ORIG_NAME_PREFIX+name);
		Object object = ath.fetchObjectFromDB(ORIG_NAME_PREFIX+name);
		assertNotNull(object);
		
		assertFalse(repositoryFacade.objectExists(object.getIdentifier(), preservationSystem.getOpenCollectionName()));
		assertFalse(repositoryFacade.objectExists(object.getIdentifier(), preservationSystem.getClosedCollectionName()));
		assertEquals(PUBLISHEDFLAG_NO_PUBLICATION, object.getPublished_flag());
		
	}
	
	@Test
	public void testPublishAll() throws InterruptedException, IOException, RepositoryException{
		
		String name = "AllPublic";
		ath.createObjectAndJob(ORIG_NAME_PREFIX+name,WORKFLOW_STATUS_START___TIME_BASED_PUBLICATION_OBJECT_TO_WORK_AREA_ACTION);
		ath.waitForJobsToFinish(ORIG_NAME_PREFIX+name);
		Object object = ath.fetchObjectFromDB(ORIG_NAME_PREFIX+name);
		assertNotNull(object);
		assertTrue(repositoryFacade.objectExists(object.getIdentifier(), preservationSystem.getOpenCollectionName()));
		assertTrue(repositoryFacade.objectExists(object.getIdentifier(), preservationSystem.getClosedCollectionName()));
		assertEquals(PUBLISHEDFLAG_PUBLIC+
				PUBLISHEDFLAG_INSTITUTION, object.getPublished_flag());
	}
	
}
