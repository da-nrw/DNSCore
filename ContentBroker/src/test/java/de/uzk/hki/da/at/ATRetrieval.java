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

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import gov.loc.repository.bagit.Bag;
import gov.loc.repository.bagit.BagFactory;

import java.io.File;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import de.uzk.hki.da.pkg.ArchiveBuilderFactory;

/**
 * Relates to AK-T/05 RetrieveObject - Happy Path Scenario.
 * @author Daniel M. de Oliveira
 */
public class ATRetrieval extends AcceptanceTest{
	
	private static final String identifier = "ATRetrieval_identifier";
	
	@BeforeClass
	public static void setUp() {
	}
	
	
	@AfterClass
	public static void tearDown(){
		distributedConversionAdapter.remove("aip/TEST/"+identifier); // TODO does it work?
		new File("/tmp/"+identifier+".tar").delete();
		FileUtils.deleteQuietly(new File("/tmp/"+identifier));
	}
	
	@Test
	public void testHappyPath() throws Exception{
		
		String originalName = "ATRetrieval";
		
		ath.putPackageToStorage(identifier, originalName, new Date(), 100);
		ath.createJob(originalName, "900");
		ath.waitForJobToBeInStatus(originalName, "952");
		
		System.out.println(new File(localNode.getUserAreaRootPath()+"/TEST/outgoing/"+identifier+".tar").getAbsolutePath());
		assertTrue(new File(localNode.getUserAreaRootPath()+"/TEST/outgoing/"+identifier+".tar").exists());
		
		FileUtils.moveFileToDirectory(
				new File(localNode.getUserAreaRootPath()+"/TEST/outgoing/"+identifier+".tar"), 
				new File("/tmp"), false);
		
		ArchiveBuilderFactory.getArchiveBuilderForFile(new File("/tmp/"+identifier+".tar"))
			.unarchiveFolder(new File("/tmp/"+identifier+".tar"), new File ("/tmp/"));
		
		if (!new File("/tmp/"+identifier+"/data/"+"image/713091.tif").exists()) fail();
		if (!new File("/tmp/"+identifier+"/data/"+"premis.xml").exists()) fail();
		
		if (!bagIsValid(new File("/tmp/"+identifier))) fail();
	}

	private boolean bagIsValid(File file){
		BagFactory bagFactory = new BagFactory();
		Bag bag = bagFactory.createBag(file);
		return bag.verifyValid().isSuccess();
	}	
	
}
