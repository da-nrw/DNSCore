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

import static org.junit.Assert.*;

import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.utils.Path;
import de.uzk.hki.da.utils.RelativePath;

/**
 * Relates to AK-T/02 Ingest - Alternative Szenarien.
 * @author Daniel M. de Oliveira
 *
 */
public class ATUseCaseIngestValidationNotPassed extends Base{

	@Before
	public void setUp() throws IOException{
		setUpBase();
	}
	
	@After
	public void tearDown(){
		clearDB();
		cleanStorage();
	}
	
	private Object ingestAndWaitForErrorState(String originalName,String errorState) throws IOException, InterruptedException{
		return ingestAndWaitForErrorState(originalName, errorState, "tgz");
	}
		
	private Object ingestAndWaitForErrorState(String originalName,String errorState,String containerSuffix) throws IOException, InterruptedException{
		
		if (!containerSuffix.isEmpty()) containerSuffix="."+containerSuffix;
		
		FileUtils.copyFileToDirectory(new RelativePath("src/test/resources/at/",originalName+containerSuffix).toFile(), 
				Path.make(localNode.getIngestAreaRootPath(),"/TEST").toFile());
		waitForJobToBeInStatus(originalName,errorState,2000);
		return fetchObjectFromDB(originalName);
	}
	
	@Test
	public void testFirst_tagmanifest1ZeichenChanged() throws Exception{
		
		ingestAndWaitForErrorState("ATErsteZeile_tagmanifest1Zeichengeaendert", "114");
		System.out.println("yeah!");
	}
	
	@Test
	public void testManifestMd5_2filesChanged() throws Exception{
		
		ingestAndWaitForErrorState("ATManifestMd5_2filesgeaendert", "114");
		System.out.println("yeah!");
	}
	
	@Test
	public void testOneFileDeleted() throws Exception{
		
		ingestAndWaitForErrorState("ATEineDatei_geloescht", "114");
		System.out.println("yeah!");
	}
	
	@Test
	public void testInvalidPremis() throws Exception{
		
		ingestAndWaitForErrorState("ATInvalidPremis", "114","zip");
		System.out.println("yeah!");
	}
	
	@Test
	public void testDuplicateMetadataFiles() throws IOException, InterruptedException{
		
		Object object = ingestAndWaitForErrorState("ATDuplicateMetadataFiles","134");
		System.out.println("yeah!");
		
		assertEquals(null,object.getPackage_type());
		assertEquals(null,object.getMetadata_file());
	}
	
	@Test
	public void testDuplicateDocumentName() throws IOException, InterruptedException{
		ingestAndWaitForErrorState("ATDuplicateDocumentName","114");
		System.out.println("yeah!");
	}
	
	
	
	
}
