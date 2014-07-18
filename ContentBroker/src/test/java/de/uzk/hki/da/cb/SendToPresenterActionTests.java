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

import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Matchers.anyObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

import de.uzk.hki.da.metadata.DCReader;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.repository.RepositoryException;
import de.uzk.hki.da.repository.RepositoryFacade;
import de.uzk.hki.da.utils.Path;
import de.uzk.hki.da.utils.RelativePath;
import de.uzk.hki.da.utils.TESTHelper;

/**
 * @author Daniel M. de Oliveira
 */
public class SendToPresenterActionTests {

	private SendToPresenterAction action;
	private DCReader dcReader;
	private Object object;

	@Before
	public void setUp(){
		object = TESTHelper.setUpObject("id1", new RelativePath("src/test/resources/cb/SendToPresenterActionTests"));
		object.setUrn("urn");
		
		action = new SendToPresenterAction();
		action.setObject(object);
		action.setLocalNode(object.getTransientNodeRef());
		RepositoryFacade repositoryFacade = mock(RepositoryFacade.class);
		action.setRepositoryFacade(repositoryFacade);
		
		dcReader = mock(DCReader.class);
		when(dcReader.getPackageTypeFromDC((Path)anyObject(),(Path)anyObject())).thenReturn("EAD");
		action.setDcReader(dcReader);
		
		Map<String,String> viewerUrls = new HashMap<String,String>();
		viewerUrls.put("EAD", "http://data.danrw.de/ead-viewer/#/browse?src=");
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
	
	@Test
	public void testEADPackage(){
		try {
			action.implementation();
		} catch (IOException e) {
			fail();
		}
	}
	
	// happened during refactoring of atusecaseingestdelta
	@Test 
	public void testThrowErrorWhenTryingToExecuteWithoutURNSet(){
		object.setUrn(null);
		try {
			action.implementation();
			fail();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	
	
	
}
