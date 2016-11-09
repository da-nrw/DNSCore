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

package de.uzk.hki.da.at;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.junit.After;
import org.junit.Test;

import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.repository.RepositoryException;

/**
 * @author Daniel M. de Oliveira
 */
public class ATContractIngestDelta extends AcceptanceTest{

	private static final String OUTPUT_JPG_2 = "/tmp/abc2.jpg";
	private static final String OUTPUT_JPG_1 = "/tmp/abc.jpg";
	private static final String JPG_STREAM_ID = "_c9d29e93707f67182b6542a48214d95c.jpg";
	private static final String DEFAULT_CONTAINER_EXTENSION = "tgz";
	private static final String ORIG_NAME = "ATContractRightDeltas";
	
	@After
	public void tearDown(){
		new File(OUTPUT_JPG_1).delete();
		new File(OUTPUT_JPG_2).delete();
	}
	
	@Test
	public void test() throws IOException, InterruptedException, RepositoryException{
		
		ath.putSIPtoIngestArea(ORIG_NAME+"1", DEFAULT_CONTAINER_EXTENSION, ORIG_NAME);
		Thread.sleep(2000);
		ath.awaitObjectState(ORIG_NAME,Object.ObjectStatus.ArchivedAndValidAndNotInWorkflow);
		ath.waitForDefinedPublishedState(ORIG_NAME);
		Object o=ath.getObject(ORIG_NAME);
		
		FileOutputStream outi;
		
		outi = new FileOutputStream(OUTPUT_JPG_1);
		String collName =  preservationSystem.getOpenCollectionName();
		repositoryFacade.retrieveTo(outi, o.getIdentifier(), collName,	JPG_STREAM_ID);
		outi.close();
		
		ath.waitForObjectToBeIndexed(metadataIndex,getTestIndex(), o.getIdentifier());
		assertTrue(metadataIndex.getIndexedMetadata(getTestIndex(), o.getIdentifier()).contains("Nudelmaschine in Originalverpackung"));
		
		ath.putSIPtoIngestArea(ORIG_NAME+"2", DEFAULT_CONTAINER_EXTENSION, ORIG_NAME);
		ath.awaitObjectState(ORIG_NAME,Object.ObjectStatus.InWorkflow);
		ath.awaitObjectState(ORIG_NAME,Object.ObjectStatus.ArchivedAndValidAndNotInWorkflow);
		ath.waitForDefinedPublishedState(ORIG_NAME);
		o=ath.getObject(ORIG_NAME);

		outi = new FileOutputStream(OUTPUT_JPG_2);
		repositoryFacade.retrieveTo(outi, o.getIdentifier(), collName, JPG_STREAM_ID);
		outi.close();
		
		Thread.sleep(3000);
		assertTrue(metadataIndex.getIndexedMetadata(getTestIndex(), o.getIdentifier()).contains("Nudelmaschine in Originalverpackung"));

		ath.putSIPtoIngestArea(ORIG_NAME+"3", DEFAULT_CONTAINER_EXTENSION, ORIG_NAME);
		ath.awaitObjectState(ORIG_NAME,Object.ObjectStatus.InWorkflow);
		ath.awaitObjectState(ORIG_NAME,Object.ObjectStatus.ArchivedAndValidAndNotInWorkflow);
		ath.waitForObjectPublishedState(ORIG_NAME,0);
		o=ath.getObject(ORIG_NAME);

		boolean exi3 = repositoryFacade.fileExists(o.getIdentifier(), collName, JPG_STREAM_ID);
		assertFalse(exi3);
		
		Thread.sleep(3000);
		assertFalse(metadataIndex.getIndexedMetadata(getTestIndex(), o.getIdentifier()).contains("Nudelmaschine in Originalverpackung"));
	}
}
