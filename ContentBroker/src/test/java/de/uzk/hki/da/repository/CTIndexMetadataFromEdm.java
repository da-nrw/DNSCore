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
import de.uzk.hki.da.util.RelativePath;


/**
 * @author Polina Gubaidullina
 * @author Daniel M. de Oliveira
 *
 */
public class CTIndexMetadataFromEdm {

    // index name generated from elasticsearch.indexName + _test for the TEST contractors
	private static final String INDEX_NAME = "portal_ci_test";
	
	File edmFile = new RelativePath("src", "test", "resources", "repository", "CTIndexMetadataFromEdmTests", "edmContent").toFile();
	File metsEdm = new RelativePath("src", "test", "resources", "repository", "CTIndexMetadataFromEdmTests", "metsEdm").toFile();
	File lidoEdm = new RelativePath("src", "test", "resources", "repository", "CTIndexMetadataFromEdmTests", "lidoEdm").toFile();
	File eadEdm = new RelativePath("src", "test", "resources", "repository", "CTIndexMetadataFromEdmTests", "eadEdm").toFile();
	File metsContentEdm = new RelativePath("src", "test", "resources", "repository", "CTIndexMetadataFromEdmTests", "edmContentFromMets").toFile();
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
		
		String metsEdmContent = IOUtils.toString(new FileInputStream(metsEdm), C.ENCODING_UTF_8);
	
		try {
			repo.indexMetadata(INDEX_NAME, "1", metsEdmContent);
		} catch (RepositoryException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		
		try {
			Thread.sleep(4711);
		} catch (InterruptedException e) {}

		assertTrue(repo.getIndexedMetadata(INDEX_NAME, "objectID").contains("\"edm:dataProvider\":\"Universitäts- und Landesbibliothek Münster\""));
		assertTrue(repo.getIndexedMetadata(INDEX_NAME, "objectID").contains("\"dc:title\":[\"und der größeren evangelischen Gemeinde in derselben\",\"Chronik der Stadt Hoerde\"]"));	
		assertTrue(repo.getIndexedMetadata(INDEX_NAME, "objectID").contains("\"dc:date\":[\"2011\",\"1836\"]"));
		assertTrue(repo.getIndexedMetadata(INDEX_NAME, "objectID").contains("\"dc:publisher\":[\"Münster\",\"Hoerde\"]"));
		
		System.out.println("huhu: "+repo.getIndexedMetadata(INDEX_NAME, "objectID*"));
	}	
	
	
	@Test
	public void testLidoEdm() throws FileNotFoundException, IOException {
		
		String lidoEdmContent = IOUtils.toString(new FileInputStream(lidoEdm), C.ENCODING_UTF_8);
	
		try {
			repo.indexMetadata(INDEX_NAME, "1", lidoEdmContent);
		} catch (RepositoryException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		
		try {
			Thread.sleep(4711);
		} catch (InterruptedException e) {}

		System.out.println(repo.getIndexedMetadata(INDEX_NAME, "objectID-f838082dc50949e8b57346d904efdd3d"));
		assertTrue(repo.getIndexedMetadata(INDEX_NAME, "objectID-f838082dc50949e8b57346d904efdd3d")
				.contains("\"dc:title\":[\"Vier Mädchen auf einer Altane\",\"Mädchen auf Altane, Stadt im Hintergrund\"]"));
		assertTrue(repo.getIndexedMetadata(INDEX_NAME, "objectID-f838082dc50949e8b57346d904efdd3d").contains("\"dc:date\":[\"1913\"]"));
	}	
	
	
	
	@Test
	public void eadEdmTest() throws FileNotFoundException, IOException {
		
		String eadEdmContent = IOUtils.toString(new FileInputStream(eadEdm), C.ENCODING_UTF_8);
	
		try {
			repo.indexMetadata(INDEX_NAME, "objectID", eadEdmContent);
		} catch (RepositoryException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		
		try {
			Thread.sleep(4711);
		} catch (InterruptedException e) {}
		

		assertTrue(repo.getIndexedMetadata(INDEX_NAME, "objectID-569c0c3d21aa45b8bb230d4b3bdec00e").contains("\"dc:title\":[\"Jugendherbergsverband, Schriftwechsel\"]"));
		assertTrue(repo.getIndexedMetadata(INDEX_NAME, "objectID-457009589ee34cdcaa71ed56a2dad8a6").contains("\"dc:date\":[\"1937-01-01/1938-12-31\"]"));
		assertTrue(repo.getIndexedMetadata(INDEX_NAME, "objectID-457009589ee34cdcaa71ed56a2dad8a6").contains("\"dc:title\":[\"Volksdeutsches Rundfunkreferat\"]"));	
		assertTrue(repo.getIndexedMetadata(INDEX_NAME, "objectID-457009589ee34cdcaa71ed56a2dad8a6").contains("\"dcterms:isPartOf\""));

	}	

}
