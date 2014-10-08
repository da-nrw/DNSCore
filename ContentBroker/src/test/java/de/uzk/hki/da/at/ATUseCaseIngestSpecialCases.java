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

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Test;

import de.uzk.hki.da.core.Path;
import de.uzk.hki.da.model.Object;

/**
 * Relates to AK-T/02 Ingest - Sunny Day Szenario (mit besonderen Bedingungen).
 * @author Daniel M. de Oliveira
 */
public class ATUseCaseIngestSpecialCases extends AcceptanceTest{

	private static final String YEAH = "yeah!";
	private String originalName = "ATUseCaseIngest1";
	private Object object;
	
	@After
	public void tearDown(){
		
		FileUtils.deleteQuietly(Path.make(localNode.getWorkAreaRootPath(),"/work/TEST/"+object.getIdentifier()).toFile());

		Path.make(localNode.getIngestAreaRootPath(),"/TEST/AT_CON1.tar").toFile().delete();
		Path.make(localNode.getIngestAreaRootPath(),"/TEST/AT_CON2.tgz").toFile().delete();
		Path.make(localNode.getIngestAreaRootPath(),"/TEST/AT_CON3.zip").toFile().delete();
	}
	
	@Test
	public void testUmlautsInPackageName() throws Exception{
		
		originalName = "ATÜÄÖ";
		object = ath.ingest(originalName);
		System.out.println(YEAH);
	}
	
	@Test
	public void testSpecialCharactersInFileNames() throws Exception{
		
		originalName = "ATSonderzeichen_in_Dateinamen";
		object = ath.ingest(originalName);
		System.out.println(YEAH);
	}
	
	@Test
	public void testUmlautsInFileNames() throws Exception{
		
		originalName = "ATUmlaute_in_Dateinamen";
		object = ath.ingest(originalName);
		System.out.println(YEAH);
	}
	
	@Test
	public void testTARContainer() throws Exception{
		
		originalName = "AT_CON1";
		object = ath.ingest(originalName,"tar",originalName);
		System.out.println(YEAH);
	}
	
	@Test
	public void testTGZContainer() throws Exception{
		
		originalName = "AT_CON2";
		object = ath.ingest(originalName);
		System.out.println(YEAH);
	}
	
	@Test
	public void testZIPContainer() throws Exception{
		
		originalName = "AT_CON3";
		object = ath.ingest(originalName,"zip",originalName);
		System.out.println(YEAH);
	}
	
	@Test
	public void testSpecialCharsInPackageName() throws Exception{
		
		originalName = "AT&Sonderzeichen%in#Paketnamen";
		object = ath.ingest(originalName);
		System.out.println(YEAH);
	}
	
	
	
	@Test
	public void testWhiteSpacesInFileNames() throws Exception{
		
		originalName = "ATLeerzeichen_in_Dateinamen";
		object = ath.ingest(originalName);
		System.out.println(YEAH);
	}
}
