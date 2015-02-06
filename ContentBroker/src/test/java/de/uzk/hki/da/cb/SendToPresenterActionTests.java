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

import static de.uzk.hki.da.core.C.*;
import static de.uzk.hki.da.test.TC.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.uzk.hki.da.core.PreconditionsNotMetException;
import de.uzk.hki.da.model.WorkArea;
import de.uzk.hki.da.repository.RepositoryException;
import de.uzk.hki.da.repository.RepositoryFacade;
import de.uzk.hki.da.util.Path;

/**
 * @author Daniel M. de Oliveira
 */
public class SendToPresenterActionTests extends ConcreteActionUnitTest{

	@ActionUnderTest
	SendToPresenterAction action = new SendToPresenterAction();

	
	private static final String UNDERSCORE = "_";
	private static final Path WORKAREAROOTPATH = Path.make(TEST_ROOT_CB,"SendToPresenterAction");
	
	private final RepositoryFacade repositoryFacade = mock(RepositoryFacade.class);

	@Before
	public void setUp() throws IOException{
		
		n.setWorkAreaRootPath(WORKAREAROOTPATH);
		action.setRepositoryFacade(repositoryFacade);
		
		Map<String,String> viewerUrls = new HashMap<String,String>();
		viewerUrls.put(CB_PACKAGETYPE_EAD, "http://data.danrw.de/ead-viewer/#/browse?src=");
		action.setViewerUrls(viewerUrls);
		
		Set<String> fileFilter = new HashSet<String>();
		action.setFileFilter(fileFilter);
		
		Set<String> testContractors = new HashSet<String>();
		action.setTestContractors(testContractors);
		
		FileUtils.copyDirectory(Path.makeFile(WORKAREAROOTPATH,WA_PIPS+UNDERSCORE), Path.makeFile(WORKAREAROOTPATH,WA_PIPS));
		
		o.setPackage_type(CB_PACKAGETYPE_EAD);
		
		action.setWorkArea(new WorkArea(n,o));
	}
	
	private File makeMetadataFile(String fileName,String pipType) {
		return Path.makeFile(n.getWorkAreaRootPath(),WA_PIPS,pipType,o.getContractor().getShort_name(),o.getIdentifier(),fileName+FILE_EXTENSION_XML);
	}
	
	
	@After
	public void tearDown() throws IOException {
		makeMetadataFile(METADATA_STREAM_ID_EPICUR,WA_PUBLIC).delete();
		FileUtils.deleteDirectory(Path.makeFile(WORKAREAROOTPATH,WA_PIPS));
	}
	
	
	@Test
	public void endWorkflowWhenNothingToIndex() throws IOException {
		o.setPackage_type(null);
		action.implementation();
		assertTrue(action.isKILLATEXIT());
	}
	
	// if no public DIP is created EDM creation and ES indexing is skipped
	@Test
	public void endWorkflowWhenPublicPIPWasNotSuccessfullyIngested() throws IOException {
		FileUtils.deleteDirectory(Path.makeFile(WORKAREAROOTPATH,WA_PIPS,WA_PUBLIC,o.getContractor().getShort_name(),o.getIdentifier()));
		action.implementation();
		assertTrue(action.isKILLATEXIT());
	}
	
	@Test
	public void continueWhenNothingToIndex() throws IOException {
		
		action.implementation();
		assertFalse(action.isKILLATEXIT());
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
		assertTrue(makeMetadataFile(METADATA_STREAM_ID_EPICUR,WA_PUBLIC).exists());
	}
	
	
	@Test 
	public void preconditionsThrowErrorUrnNotSet() throws IOException{
		o.setUrn(null);
		try {
			action.implementation();
			fail();
		} catch (PreconditionsNotMetException e) {}
	}
	
	@Test 
	public void preconditionsThrowErrorClosedCollectionNotSet() throws IOException{
		ps.setOpenCollectionName(null);
		try {
			action.implementation();
			fail();
		} catch (IllegalStateException e) {}
	}
	
	@Test 
	public void preconditionsThrowErrorOpenCollectionSet() throws IOException{
		ps.setClosedCollectionName(null);
		try {
			action.implementation();
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
		assertFalse(makeMetadataFile(METADATA_STREAM_ID_EPICUR,WA_PUBLIC).exists());
	}
	
	@Test
	public void addURNToDC() throws IOException {
		action.implementation(); 
		FileInputStream in = new FileInputStream(makeMetadataFile(METADATA_STREAM_ID_DC, WA_PUBLIC));
		String dcContent = IOUtils.toString(in, ENCODING_UTF_8);
		assertTrue(dcContent.contains("<DC:identifier xmlns:DC=\"http://purl.org/dc/elements/1.1/\">urn</DC:identifier>"));
	}
	
}
