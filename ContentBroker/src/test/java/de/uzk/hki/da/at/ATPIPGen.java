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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;

import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.junit.Test;

import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.repository.RepositoryException;

/**
 * @author Daniel M. de Oliveira
 */
public class ATPIPGen extends AcceptanceTest{

	private static final String XLINK_NAMESPACE = "http://www.w3.org/1999/xlink";
	private static final String METS_NAMESPACE = "http://www.loc.gov/METS/";

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
		ath.createObjectAndJob("ATPIPGen"+name,"700","METS","mets.xml");

		ath.waitForJobsToFinish("ATPIPGen"+name);
		Object object = ath.fetchObjectFromDB("ATPIPGen"+name);
		
		assertNotNull(repositoryFacade.retrieveFile(object.getIdentifier(), "collection-open", "_0c32b463b540e3fee433961ba5c491d6.jpg"));
		assertNotNull(repositoryFacade.retrieveFile(object.getIdentifier(), "collection-closed", "_0c32b463b540e3fee433961ba5c491d6.jpg"));
		InputStream metsStreamPublic = repositoryFacade.retrieveFile(object.getIdentifier(), "collection-open", "METS");
		assertNotNull(metsStreamPublic);
		assertTrue(metsStreamPublic.toString().length() > 0);
		InputStream metsStreamClosed = repositoryFacade.retrieveFile(object.getIdentifier(), "collection-closed", "METS");
		assertNotNull(metsStreamClosed);
		
		Namespace METS_NS = Namespace.getNamespace(METS_NAMESPACE);
		Namespace XLINK_NS = Namespace.getNamespace(XLINK_NAMESPACE);
		
		SAXBuilder builder = new SAXBuilder();
		Document doc = builder.build(metsStreamPublic);

		System.out.println("doc: " + doc);
		
		String url = doc.getRootElement()
				.getChild("fileSec", METS_NS)
				.getChild("fileGrp", METS_NS)
				.getChild("file", METS_NS)
				.getChild("FLocat", METS_NS)
				.getAttributeValue("href", XLINK_NS);
		
		assertEquals("_0c32b463b540e3fee433961ba5c491d6.jpg", url);
		
		doc = builder.build(metsStreamClosed);

		url = doc.getRootElement()
				.getChild("fileSec", METS_NS)
				.getChild("fileGrp", METS_NS)
				.getChild("file", METS_NS)
				.getChild("FLocat", METS_NS)
				.getAttributeValue("href", XLINK_NS);
		
		assertEquals("_0c32b463b540e3fee433961ba5c491d6.jpg", url);
		
	}
	
	@Test
	public void testPublishInstOnly() throws InterruptedException, IOException, RepositoryException{
		
		String name = "InstOnly";
		ath.createObjectAndJob("ATPIPGen"+name,"700");
		ath.waitForJobsToFinish("ATPIPGen"+name);
		Object object = ath.fetchObjectFromDB("ATPIPGen"+name);
		
		assertNull(repositoryFacade.retrieveFile(object.getIdentifier(), "collection-open", "_0c32b463b540e3fee433961ba5c491d6.jpg"));
		assertNotNull(repositoryFacade.retrieveFile(object.getIdentifier(), "collection-closed", "_0c32b463b540e3fee433961ba5c491d6.jpg"));
		
	}
	
	@Test
	public void testNoPubWithLawSet() throws InterruptedException, IOException, RepositoryException{
		
		String name = "NoPubWithLawSet";
		ath.createObjectAndJob("ATPIPGen"+name,"700");
		ath.waitForJobsToFinish("ATPIPGen"+name);
		Object object = ath.fetchObjectFromDB("ATPIPGen"+name);
		
		assertFalse(repositoryFacade.objectExists(object.getIdentifier(), "collection-open"));
		
	}
	
	@Test
	public void testNoPubWithStartDateSet() throws InterruptedException, IOException, RepositoryException{
		
		String name = "NoPubWithStartDateSet";
		ath.createObjectAndJob("ATPIPGen"+name,"700");
		ath.waitForJobsToFinish("ATPIPGen"+name);
		Object object = ath.fetchObjectFromDB("ATPIPGen"+name);
		
		assertFalse(repositoryFacade.objectExists(object.getIdentifier(), "collection-open"));
		
	}
	
	
	@Test
	public void testPublishNothing() throws InterruptedException, IOException, RepositoryException{
		
		String name = "PublishNothing";
		ath.createObjectAndJob("ATPIPGen"+name,"700");
		ath.waitForJobsToFinish("ATPIPGen"+name);
		Object object = ath.fetchObjectFromDB("ATPIPGen"+name);
		
		assertFalse(repositoryFacade.objectExists(object.getIdentifier(), "collection-open"));
		assertFalse(repositoryFacade.objectExists(object.getIdentifier(), "collection-closed"));
		
	}
	
	@Test
	public void testPublishAll() throws InterruptedException, IOException, RepositoryException{
		
		String name = "AllPublic";
		ath.createObjectAndJob("ATPIPGen"+name,"700");
		ath.waitForJobsToFinish("ATPIPGen"+name);
		Object object = ath.fetchObjectFromDB("ATPIPGen"+name);
		
		assertTrue(repositoryFacade.objectExists(object.getIdentifier(), "collection-open"));
		assertTrue(repositoryFacade.objectExists(object.getIdentifier(), "collection-closed"));
		
	}
	
}
