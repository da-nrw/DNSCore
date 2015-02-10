/*
 DA-NRW Software Suite | ContentBroker
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
import org.junit.BeforeClass;
import org.junit.Test;

import de.uzk.hki.da.core.C;
import de.uzk.hki.da.model.Job;
import de.uzk.hki.da.test.TC;
import de.uzk.hki.da.util.Path;

/**
 * Relates to AK-T/02 Ingest - Alternative Szenarien.
 * @author Daniel M. de Oliveira
 *
 */
public class ATUseCaseIngestValidationNotPassed extends AcceptanceTest{

	private static final String AT_DUPLICATE_DOCUMENT_NAME = "ATDuplicateDocumentName";
	private static final String AT_EINE_DATEI_GELOESCHT = "ATEineDatei_geloescht";
	private static final String AT_DUPLICATE_METADATA_FILES = "ATDuplicateMetadataFiles";
	private static final String AT_INVALID_PREMIS = "ATInvalidPremis";
	private static final String AT_ERSTE_ZEILE_TAGMANIFEST1_ZEICHENGEAENDERT = "ATErsteZeile_tagmanifest1Zeichengeaendert";
	private static final String AT_MANIFEST_MD5_2FILESGEAENDERT = "ATManifestMd5_2filesgeaendert";
	
	private static final String ORIG_NAME =    "ATUseCaseIngestDeltaDuplicateEAD";
	private static final String IDENTIFIER =   "ATUseCaseIngestDeltaDuplicateEADIdentifier";
	private static final String CONTAINER_NAME = ORIG_NAME+"."+C.FILE_EXTENSION_TGZ;

	@BeforeClass
	public static void putPackages() throws IOException{
		
		ath.putPackageToStorage(IDENTIFIER,ORIG_NAME,CONTAINER_NAME,null,100);
		FileUtils.copyFile(Path.makeFile(TC.TEST_ROOT_AT,CONTAINER_NAME), 
				Path.makeFile(localNode.getIngestAreaRootPath(),C.TEST_USER_SHORT_NAME,CONTAINER_NAME));
		
		
		ath.putPackageToIngestArea(ORIG_NAME,"tgz",
				ORIG_NAME);
		
		
		ath.putPackageToIngestArea(AT_ERSTE_ZEILE_TAGMANIFEST1_ZEICHENGEAENDERT,"tgz",
				AT_ERSTE_ZEILE_TAGMANIFEST1_ZEICHENGEAENDERT);
		ath.putPackageToIngestArea(AT_MANIFEST_MD5_2FILESGEAENDERT,"tgz",
				AT_MANIFEST_MD5_2FILESGEAENDERT);
		ath.putPackageToIngestArea(AT_EINE_DATEI_GELOESCHT,"tgz",
				AT_EINE_DATEI_GELOESCHT);
		ath.putPackageToIngestArea(AT_INVALID_PREMIS,"zip",
				AT_INVALID_PREMIS);
		ath.putPackageToIngestArea(AT_DUPLICATE_METADATA_FILES,"tgz",
				AT_DUPLICATE_METADATA_FILES);
		ath.putPackageToIngestArea(AT_DUPLICATE_DOCUMENT_NAME,"tgz",
				AT_DUPLICATE_DOCUMENT_NAME);
	}
	
	@Test
	public void testFirst_tagmanifest1ZeichenChanged() throws Exception{
		ath.waitForJobToBeInErrorStatus(AT_ERSTE_ZEILE_TAGMANIFEST1_ZEICHENGEAENDERT,C.WORKFLOW_STATUS_DIGIT_USER_ERROR);
	}
	
	@Test
	public void testManifestMd5_2filesChanged() throws Exception{
		ath.waitForJobToBeInErrorStatus(AT_MANIFEST_MD5_2FILESGEAENDERT,C.WORKFLOW_STATUS_DIGIT_USER_ERROR);
	}
	
	@Test
	public void testOneFileDeleted() throws Exception{
		ath.waitForJobToBeInErrorStatus(AT_EINE_DATEI_GELOESCHT,C.WORKFLOW_STATUS_DIGIT_USER_ERROR);
	}
	
	@Test
	public void testInvalidPremis() throws Exception{
		ath.waitForJobToBeInErrorStatus(AT_INVALID_PREMIS,C.WORKFLOW_STATUS_DIGIT_USER_ERROR);
	}
	
	@Test
	public void testDuplicateMetadataFiles() throws IOException, InterruptedException{
		Job job = ath.waitForJobToBeInErrorStatus(AT_DUPLICATE_METADATA_FILES,C.WORKFLOW_STATUS_DIGIT_USER_ERROR);
		assertEquals(null,job.getObject().getPackage_type());
		assertEquals(null,job.getObject().getMetadata_file());
	}
	
	@Test
	public void testDuplicateDocumentName() throws IOException, InterruptedException{
		ath.waitForJobToBeInErrorStatus(AT_DUPLICATE_DOCUMENT_NAME,C.WORKFLOW_STATUS_DIGIT_USER_ERROR);
	}
	
	@Test
	public void testDeltaRejectDuplicateEADFiles() throws IOException, InterruptedException{
		ath.waitForJobToBeInErrorStatus(ORIG_NAME,C.WORKFLOW_STATUS_DIGIT_USER_ERROR);
	}
}
