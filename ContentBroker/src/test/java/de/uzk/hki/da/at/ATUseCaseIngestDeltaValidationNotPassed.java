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

import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import de.uzk.hki.da.core.C;
import de.uzk.hki.da.core.Path;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.test.TC;

/**
 * @author Daniel M. de Oliveira
 */
public class ATUseCaseIngestDeltaValidationNotPassed extends AcceptanceTest {
	                                            
	private static final String ORIG_NAME =    "ATUseCaseIngestDeltaDuplicateEAD";
	private static final String IDENTIFIER =   "ATUseCaseIngestDeltaDuplicateEADIdentifier";
	private static final String CONTAINER_NAME = ORIG_NAME+"."+C.FILE_EXTENSION_TGZ;

	Object object = null;
	
	@Before
	public void setUp() throws IOException{

		object = ath.putPackageToStorage(IDENTIFIER,ORIG_NAME,CONTAINER_NAME,null,100);
		FileUtils.copyFile(Path.makeFile(TC.TEST_ROOT_AT,CONTAINER_NAME), 
				Path.makeFile(localNode.getIngestAreaRootPath(),C.TEST_USER_SHORT_NAME,CONTAINER_NAME));
	}
	
	@Test
	public void testRejectDuplicateEADFiles() throws IOException, InterruptedException{
		ath.ingestAndWaitForErrorState(ORIG_NAME, C.WORKFLOW_STATE_DIGIT_USER_ERROR);
	}
}
