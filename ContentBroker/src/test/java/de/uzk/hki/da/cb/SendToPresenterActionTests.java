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

package de.uzk.hki.da.cb;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static de.uzk.hki.da.core.C.*;
import de.uzk.hki.da.core.C;
import de.uzk.hki.da.repository.RepositoryException;
import de.uzk.hki.da.repository.RepositoryFacade;
import static de.uzk.hki.da.test.TC.*;
import de.uzk.hki.da.util.Path;

/**
 * @author Daniel M. de Oliveira
 */
public class SendToPresenterActionTests extends ConcreteActionUnitTest{

	@ActionUnderTest
	SendToPresenterAction action = new SendToPresenterAction();
	
	
	private static final Path WORKAREAROOTPATH = Path.make(TEST_ROOT_CB,"SendToPresenterAction");
	
	private final RepositoryFacade repositoryFacade = mock(RepositoryFacade.class);

	@Before
	public void setUp(){
		
		n.setWorkAreaRootPath(WORKAREAROOTPATH);
		action.setRepositoryFacade(repositoryFacade);
		
		Map<String,String> viewerUrls = new HashMap<String,String>();
		viewerUrls.put(CB_PACKAGETYPE_EAD, "http://data.danrw.de/ead-viewer/#/browse?src=");
		action.setViewerUrls(viewerUrls);
		
		Set<String> fileFilter = new HashSet<String>();
		action.setFileFilter(fileFilter);
		
		try {
			String fakeDCFile = "<root xmlns:dc=\"http://www.w3schools.com/furniture\">\n"+
				"<dc:title>VDA - Forschungsstelle Rheinlländer in aller Welt: Bezirksstelle West des Vereins für das Deutschtum im Ausland</dc:title>\n"+
				"</root>";
			InputStream in = IOUtils.toInputStream(fakeDCFile, "UTF-8");
			when(repositoryFacade.retrieveFile((String) anyObject(), (String) anyObject(), (String)anyObject())).thenReturn(in);
		} catch (RepositoryException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Set<String> testContractors = new HashSet<String>();
		action.setTestContractors(testContractors);
	}
	
	private File makeMetadataFile(String fileName,String pipType) {
		return Path.makeFile(n.getWorkAreaRootPath(),WA_PIPS,pipType,o.getContractor().getShort_name(),o.getIdentifier(),fileName+C.FILE_EXTENSION_XML);
	}
	
	
	@After
	public void tearDown() {
		makeMetadataFile("epicur",WA_PUBLIC).delete();
	}
	
	@Test
	public void implementationEADPackage(){
		try {
			action.implementation();
			assertSame(1,o.getPublished_flag());
		} catch (IOException e) {
			fail();
		}
	}
	
	@Test
	public void createXepicur() throws IOException {
		action.implementation();
		assertTrue(makeMetadataFile("epicur",WA_PUBLIC).exists());
	}
	
	
	@Test 
	public void preconditionsThrowErrorUrnNotSet(){
		o.setUrn(null);
		try {
			action.checkSystemStatePreconditions();
			fail();
		} catch (IllegalStateException e) {}
	}
	
	@Test 
	public void preconditionsThrowErrorClosedCollectionNotSet(){
		ps.setOpenCollectionName(null);
		try {
			action.checkSystemStatePreconditions();
			fail();
		} catch (IllegalStateException e) {}
	}
	
	@Test 
	public void preconditionsThrowErrorOpenCollectionSet(){
		ps.setClosedCollectionName(null);
		try {
			action.checkSystemStatePreconditions();
			fail();
		} catch (IllegalStateException e) {}
	}
	
	
	@Test
	public void rollbackPurgeObject() throws RepositoryException {
		action.rollback();
		verify(repositoryFacade).purgeObjectIfExists(o.getIdentifier(), ps.getOpenCollectionName());
		verify(repositoryFacade).purgeObjectIfExists(o.getIdentifier(), ps.getClosedCollectionName());
	}
	
	@Test
	public void rollbackResetPublishedFlag() {
		o.setPublished_flag(3);
		action.rollback();
		assertSame(0,o.getPublished_flag());
	}
	
	@Test
	public void rollbackDeleteXepicur() throws IOException { 
		action.implementation(); 
		// xepicur proven to be created by Test createXepicur()
		action.rollback();
		assertFalse(makeMetadataFile("epicur",WA_PUBLIC).exists());
	}
	
	
}
