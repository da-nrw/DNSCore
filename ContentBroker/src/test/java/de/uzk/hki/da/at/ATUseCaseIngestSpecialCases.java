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
 * Relates to AK-T/02 Ingest - Sunny Day Szenario (mit besonderen Bedingungen).
 * @author Daniel M. de Oliveira
 *
 */
public class ATUseCaseIngestSpecialCases extends Base{

	private String originalName = "ATUseCaseIngest1";
	private String containerName = originalName+".tgz";
	private Object object;
	
	@Before
	public void setUp() throws IOException{
		setUpBase();
	}
	
	@After
	public void tearDown() throws IOException{
		
		FileUtils.deleteDirectory(new File(workAreaRootPath+"TEST/"+object.getIdentifier()));
		
		new File(ingestAreaRootPath+"TEST/AT_CON1.tar").delete();
		new File(ingestAreaRootPath+"TEST/AT_CON2.tgz").delete();
		new File(ingestAreaRootPath+"TEST/AT_CON3.zip").delete();
		
		clearDB();
		cleanStorage();
	}
	
	@Test
	public void testUmlautsInPackageName() throws Exception{
		
		originalName = "ATÜÄÖ";
		containerName = originalName+".tgz";
				
		FileUtils.copyFileToDirectory(new File("src/test/resources/at/"+containerName), 
				new File(ingestAreaRootPath+"TEST"));
		waitForJobToBeInStatus(originalName,"540",2000);
		object = fetchObjectFromDB(originalName);
		System.out.println("objectIdentifier: "+object.getIdentifier());
		
		System.out.println("yeah!");
	}
	
	@Test
	public void testSpecialCharactersInFileNames() throws Exception{
		
		originalName = "ATSonderzeichen_in_Dateinamen";
		containerName = originalName+".tgz";
				
		FileUtils.copyFileToDirectory(new File("src/test/resources/at/"+containerName), 
				new File(ingestAreaRootPath+"TEST"));
		waitForJobToBeInStatus(originalName,"540",2000);
		object = fetchObjectFromDB(originalName);
		System.out.println("objectIdentifier: "+object.getIdentifier());
		
		System.out.println("yeah!");
	}
	
	@Test
	public void testUmlautsInFileNames() throws Exception{
		
		originalName = "ATUmlaute_in_Dateinamen";
		containerName = originalName+".tgz";
				
		FileUtils.copyFileToDirectory(new File("src/test/resources/at/"+containerName), 
				new File(ingestAreaRootPath+"TEST"));
		waitForJobToBeInStatus(originalName,"540",2000);
		object = fetchObjectFromDB(originalName);
		System.out.println("objectIdentifier: "+object.getIdentifier());
		
		System.out.println("yeah!");
	}
	
	@Test
	public void testTARContainer() throws Exception{
		
		originalName = "AT_CON1";
		containerName = originalName+".tar";
				
		FileUtils.copyFileToDirectory(new File("src/test/resources/at/"+containerName), 
				new File(ingestAreaRootPath+"TEST"));
		waitForJobToBeInStatus(originalName,"540",2000);
		object = fetchObjectFromDB(originalName);
		System.out.println("objectIdentifier: "+object.getIdentifier());
		
		System.out.println("yeah!");
	}
	
	@Test
	public void testTGZContainer() throws Exception{
		
		originalName = "AT_CON2";
		ingest(originalName);
		
		object = fetchObjectFromDB(originalName);
		System.out.println("objectIdentifier: "+object.getIdentifier());
		
		System.out.println("yeah!");
	}
	
	@Test
	public void testZIPContainer() throws Exception{
		
		originalName = "AT_CON3";
		containerName = originalName+".zip";
				
		FileUtils.copyFileToDirectory(new File("src/test/resources/at/"+containerName), 
				new File(ingestAreaRootPath+"TEST"));
		waitForJobToBeInStatus(originalName,"540",2000);
		object = fetchObjectFromDB(originalName);
		System.out.println("objectIdentifier: "+object.getIdentifier());
		
		System.out.println("yeah!");
	}
	
	@Test
	public void testSpecialCharsInPackageName() throws Exception{
		
		originalName = "AT&Sonderzeichen%in#Paketnamen";
		ingest(originalName);
		
		
		object = fetchObjectFromDB(originalName);
		System.out.println("objectIdentifier: "+object.getIdentifier());
		
		System.out.println("yeah!");
	}
	
	
	
	@Test
	public void testWhiteSpacesInFileNames() throws Exception{
		
		originalName = "ATLeerzeichen_in_Dateinamen";
		ingest(originalName);
		
		object = fetchObjectFromDB(originalName);
		System.out.println("objectIdentifier: "+object.getIdentifier());
		
		System.out.println("yeah!");
	}
}
