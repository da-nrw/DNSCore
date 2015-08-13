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

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.uzk.hki.da.core.C;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.util.Path;

/**
 * @author Daniel M. de Oliveira
 */
public class ATMigrationRight extends AcceptanceTest {

	private static final File UNPACKED_DIP = new File("/tmp/MigrationUnpacked");
	private static final String ORIG_NAME = "ATMigrationRightAllowed";
	private static final String ORIG_NAME_NOTALLOWED = "ATMigrationRightNotAllowed";
	private static Object o;
	
	@Before
	public void setUp() throws IOException{
		ath.putSIPtoIngestArea(ORIG_NAME, C.FILE_EXTENSION_TGZ, ORIG_NAME);		
		ath.putSIPtoIngestArea(ORIG_NAME_NOTALLOWED, C.FILE_EXTENSION_TGZ, ORIG_NAME_NOTALLOWED);
	}
	
	@After
	public void tearDown(){
		FileUtils.deleteQuietly(UNPACKED_DIP);
	}
	
	@Test
	public void testMigrationNotAllowed() throws IOException, InterruptedException {
		ath.waitForJobToBeInStatus(ORIG_NAME_NOTALLOWED, C.WORKFLOW_STATUS_WAIT___PROCESS_FOR_USER_DECISION_ACTION);
		o=ath.getObject(ORIG_NAME_NOTALLOWED);
	}
	
	
	@Test
	public void test() throws IOException{
		ath.awaitObjectState(ORIG_NAME,Object.ObjectStatus.ArchivedAndValidAndNotInWorkflow);
		o=ath.getObject(ORIG_NAME);
		ath.retrieveAIP(o, UNPACKED_DIP, "1");
		
		String brep="";
		File[] fList = Path.makeFile(UNPACKED_DIP.toString(),"data").listFiles();
		for (File file : fList){
			if (file.getAbsolutePath().endsWith("+b")){
				brep=FilenameUtils.getBaseName(file.getAbsolutePath());
				System.out.println(":"+brep);
			}
		}

		assertTrue(Path.makeFile(UNPACKED_DIP.toString(),"data",brep,"image42.tif").exists());
		
	}
}
