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

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.uzk.hki.da.model.Object;

/**
 * Relates to AK-T/02 Ingest - Alternative Szenarien.
 * @author Daniel M. de Oliveira
 *
 */
public class ATUseCaseIngestValidationNotPassed extends Base{

	private String originalName = "ATUseCaseIngest1";
	private String containerName = originalName+".tgz";
	private Object object;
	
	@Before
	public void setUp() throws IOException{
		setUpBase();
	}
	
	@After
	public void tearDown() throws IOException{
		clearDB();
		cleanStorage();
	}
	
	
	@Test
	public void testFirst_tagmanifest1ZeichenChanged() throws Exception{
		
		originalName = "ATErsteZeile_tagmanifest1Zeichengeaendert";
		containerName = originalName+".tgz";
				
		FileUtils.copyFileToDirectory(new File("src/test/resources/at/"+containerName), 
				new File(ingestAreaRootPath+"TEST"));
		waitForJobToBeInStatus(originalName,"114",2000);
		object = fetchObjectFromDB(originalName);
		System.out.println("objectIdentifier: "+object.getIdentifier());
		
		System.out.println("yeah!");
	}
	
	
	@Test
	public void testManifestMd5_2filesChanged() throws Exception{
		
		originalName = "ATManifestMd5_2filesgeaendert";
		containerName = originalName+".tgz";
				
		FileUtils.copyFileToDirectory(new File("src/test/resources/at/"+containerName), 
				new File(ingestAreaRootPath+"TEST"));
		waitForJobToBeInStatus(originalName,"114",2000);
		object = fetchObjectFromDB(originalName);
		System.out.println("objectIdentifier: "+object.getIdentifier());
		
		System.out.println("yeah!");
	}
	
	
	
	
	@Test
	public void testOneFileDeleted() throws Exception{
		
		originalName = "ATEineDatei_geloescht";
		containerName = originalName+".tgz";
				
		FileUtils.copyFileToDirectory(new File("src/test/resources/at/"+containerName), 
				new File(ingestAreaRootPath+"TEST"));
		waitForJobToBeInStatus(originalName,"114",2000);
		object = fetchObjectFromDB(originalName);
		System.out.println("objectIdentifier: "+object.getIdentifier());
		
		System.out.println("yeah!");
	}
	
	
	
	
	@Test
	public void testInvalidPremis() throws Exception{
		
		originalName = "ATInvalidPremis";
		containerName = originalName+".zip";
				
		FileUtils.copyFileToDirectory(new File("src/test/resources/at/"+containerName), 
				new File(ingestAreaRootPath+"TEST"));
		waitForJobToBeInStatus(originalName,"114",2000);
		object = fetchObjectFromDB(originalName);
		System.out.println("objectIdentifier: "+object.getIdentifier());
		
		System.out.println("yeah!");
	}
	
	
	
	
	
}
