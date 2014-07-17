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

import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.utils.C;
import de.uzk.hki.da.utils.Path;
import de.uzk.hki.da.utils.TC;

/**
 * Relates to AK-T/02 Ingest - Alternative Szenarien.
 * @author Daniel M. de Oliveira
 *
 */
public class ATUseCaseIngestValidationNotPassed extends Base{

	private static final String AT_EINE_DATEI_GELOESCHT = "ATEineDatei_geloescht";
	private static final String AT_DUPLICATE_METADATA_FILES = "ATDuplicateMetadataFiles";
	private static final String AT_INVALID_PREMIS = "ATInvalidPremis";
	private static final String AT_ERSTE_ZEILE_TAGMANIFEST1_ZEICHENGEAENDERT = "ATErsteZeile_tagmanifest1Zeichengeaendert";
	private static final String AT_MANIFEST_MD5_2FILESGEAENDERT = "ATManifestMd5_2filesgeaendert";
	private static final String YEAH = "yeah!";
	private static final int timeout = 20000;

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
		return ingestAndWaitForErrorState(originalName, errorState, C.TGZ);
	}
		
	private Object ingestAndWaitForErrorState(String originalName,String errorStateLastDigit,String containerSuffix) throws IOException, InterruptedException{
		
		if (!containerSuffix.isEmpty()) containerSuffix="."+containerSuffix;
		
		FileUtils.copyFileToDirectory(Path.makeFile(TC.TEST_ROOT_AT,originalName+containerSuffix), 
				Path.makeFile(localNode.getIngestAreaRootPath(),TC.TEST));
		waitForJobToBeInErrorStatus(originalName,errorStateLastDigit,timeout);
		return fetchObjectFromDB(originalName);
	}
	
	@Test
	public void testFirst_tagmanifest1ZeichenChanged() throws Exception{
		
		ingestAndWaitForErrorState(AT_ERSTE_ZEILE_TAGMANIFEST1_ZEICHENGEAENDERT, C.USER_ERROR_STATE_DIGIT);
		System.out.println(YEAH);
	}
	
	@Test
	public void testManifestMd5_2filesChanged() throws Exception{
		
		ingestAndWaitForErrorState(AT_MANIFEST_MD5_2FILESGEAENDERT, C.USER_ERROR_STATE_DIGIT);
		System.out.println(YEAH);
	}
	
	@Test
	public void testOneFileDeleted() throws Exception{
		
		ingestAndWaitForErrorState(AT_EINE_DATEI_GELOESCHT, C.USER_ERROR_STATE_DIGIT);
		System.out.println(YEAH);
	}
	
	@Test
	public void testInvalidPremis() throws Exception{
		
		ingestAndWaitForErrorState(AT_INVALID_PREMIS, C.USER_ERROR_STATE_DIGIT,C.ZIP);
		System.out.println(YEAH);
	}
	
	@Test
	public void testDuplicateMetadataFiles() throws IOException, InterruptedException{
		
		Object object = ingestAndWaitForErrorState(AT_DUPLICATE_METADATA_FILES,C.USER_ERROR_STATE_DIGIT);
		System.out.println(YEAH);
		
		assertEquals(null,object.getPackage_type());
		assertEquals(null,object.getMetadata_file());
	}
	
	@Test
	public void testDuplicateDocumentName() throws IOException, InterruptedException{
		ingestAndWaitForErrorState("ATDuplicateDocumentName",C.USER_ERROR_STATE_DIGIT);
		System.out.println(YEAH);
	}
	
	
	
	
}
