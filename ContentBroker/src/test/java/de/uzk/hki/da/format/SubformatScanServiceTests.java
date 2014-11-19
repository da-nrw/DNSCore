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

package de.uzk.hki.da.format;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.irods.jargon.core.exception.InvalidArgumentException;
import org.junit.Before;
import org.junit.Test;

import de.uzk.hki.da.core.Path;
import de.uzk.hki.da.model.DAFile;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.test.TESTHelper;

/**
 * @author Daniel M. de Oliveira
 */
public class SubformatScanServiceTests {

	SubformatScanService sfs = new SubformatScanService();
	
	@Before
	public void setUp(){
		
		// This is what is configured in the database.
		Map<String,List<String>> subformatIdentificationPolicies = new HashMap<String,List<String>>();
		
		subformatIdentificationPolicies.put("de.uzk.hki.da.format.FakeCompressionIdentifier", 
				Arrays.asList(FFConstants.FMT_353));
		
		sfs.setSubformatIdentificationPolicies(subformatIdentificationPolicies);
	}
	
	
	@Test
	public void testIdentify() throws InvalidArgumentException, IOException{
		
		Object o = TESTHelper.setUpObject("identifier", Path.make("src/test/resources"));
		
		DAFile f = new DAFile(o.getLatestPackage() ,null,FFConstants.TIF);
		f.setFormatPUID(FFConstants.FMT_353);
		
		List<FileWithFileFormat> files = new ArrayList<FileWithFileFormat>();
		files.add(f);
		sfs.identify(files);
		
		assertEquals(FFConstants.LZW,f.getSubformatIdentifier());
	}
	
	
	@Test
	public void testPUIDNotSet() throws IOException{
		DAFile f = new DAFile(null,null,FFConstants.TIF);
		
		List<FileWithFileFormat> files = new ArrayList<FileWithFileFormat>();
		files.add(f);
		try {
			sfs.identify(files);
			fail();
		} catch (InvalidArgumentException e){}
	}
}
