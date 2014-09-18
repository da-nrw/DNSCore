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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Test;

import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.repository.RepositoryException;

/**
 * @author Daniel M. de Oliveira
 */
public class ATUseCaseIngestDeltaContract extends AcceptanceTest{

	private static final String OUTPUT_JPG_2 = "/tmp/abc2.jpg";
	private static final String OUTPUT_JPG_1 = "/tmp/abc.jpg";
	private static final int _1_MINUTE = 60000;
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
		
		Object 
		o = ath.ingest(ORIG_NAME+"1",DEFAULT_CONTAINER_EXTENSION,ORIG_NAME);

		InputStream is = repositoryFacade.retrieveFile(o.getIdentifier(), preservationSystem.getOpenCollectionName(), 
				JPG_STREAM_ID);
		assertNotNull(is);
		IOUtils.copy(is,new FileOutputStream(OUTPUT_JPG_1));
		
		Thread.sleep(_1_MINUTE); // to prevent the repnames to match the ones of the previous package
		
		o = ath.ingest(ORIG_NAME+"2",DEFAULT_CONTAINER_EXTENSION,ORIG_NAME);

		InputStream is2 = repositoryFacade.retrieveFile(o.getIdentifier(), preservationSystem.getOpenCollectionName(), 
				JPG_STREAM_ID);
		assertNotNull(is2);
		IOUtils.copy(is2,new FileOutputStream(OUTPUT_JPG_2));

		Thread.sleep(_1_MINUTE); // to prevent the repnames to match the ones of the previous package
		
		o = ath.ingest(ORIG_NAME+"3",DEFAULT_CONTAINER_EXTENSION,ORIG_NAME);

		InputStream is3 = repositoryFacade.retrieveFile(o.getIdentifier(), preservationSystem.getOpenCollectionName(), 
				JPG_STREAM_ID);
		
		assertNull(is3);
	}
}
