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

package de.uzk.hki.da.repository;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

import de.uzk.hki.da.core.C;
import de.uzk.hki.da.core.RelativePath;


/**
 * @author Polina Gubaidullina
 * @author Daniel M. de Oliveira
 *
 */
public class CTIndexMetadataFromEdm {

    // index name generated from elasticsearch.indexName + _test for the TEST contractors
	private static final String INDEX_NAME = "portal_ci_test";
	
	File edmFile = new RelativePath("src", "test", "resources", "repository", "CTIndexMetadataFromEdmTests", "edmContent").toFile();
	File edmFromMetsFile = new RelativePath("src", "test", "resources", "repository", "CTIndexMetadataFromEdmTests", "edmContentFromMets").toFile();
	private Fedora3RepositoryFacade repo;
	
	
	@Before 
	public void setUp() throws MalformedURLException {
		repo = new Fedora3RepositoryFacade("http://localhost:8080/fedora", "fedoraAdmin", "BYi/MFjKDFd5Dpe52PSUoA==", 
				new RelativePath("src", "main", "resources", "frame.jsonld").toString());
		ElasticsearchMetadataIndex esmi = new ElasticsearchMetadataIndex(); 
		esmi.setCluster("cluster_ci");
		String[] hosts={"localhost"};
		esmi.setHosts(hosts);
		repo.setMetadataIndex(esmi);
		
	}
	
	
	@Test
	public void testMetsEdm() throws FileNotFoundException, IOException {
		
		String edmContent = IOUtils.toString(new FileInputStream(edmFromMetsFile), C.ENCODING_UTF_8);
	
		try {
			repo.indexMetadata(INDEX_NAME, "1", edmContent);
		} catch (RepositoryException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		
		try {
			Thread.sleep(4711);
		} catch (InterruptedException e) {}
		
		assertTrue(repo.getIndexedMetadata(INDEX_NAME, "Inventarnummer").contains("\"edm:provider\":\"DA-NRW - Digitales Archiv Nordrhein-Westfalen\""));
	}	
	
	
	
	@Test
	public void test() throws FileNotFoundException, IOException {
		
		String edmContent = IOUtils.toString(new FileInputStream(edmFile), C.ENCODING_UTF_8);
	
		try {
			repo.indexMetadata(INDEX_NAME, "1", edmContent);
		} catch (RepositoryException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		
		try {
			Thread.sleep(4711);
		} catch (InterruptedException e) {}
		
		assertTrue(repo.getIndexedMetadata(INDEX_NAME, "Inventarnummer").contains("\"edm:provider\":\"DA-NRW - Digitales Archiv Nordrhein-Westfalen\""));
	}	

}
