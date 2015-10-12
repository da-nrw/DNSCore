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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.irods.jargon.core.exception.InvalidArgumentException;
import org.junit.Before;
import org.junit.Test;

import de.uzk.hki.da.model.DAFile;
import de.uzk.hki.da.model.Node;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.model.WorkArea;
import de.uzk.hki.da.test.TESTHelper;
import de.uzk.hki.da.utils.Path;
import de.uzk.hki.da.utils.RelativePath;

/**
 * @author Daniel M. de Oliveira
 */
public class SubformatScanServiceTests {

	private SubformatScanService sfs = new SubformatScanService();
	private WorkArea wa;
	private Path workAreaRootPath=new RelativePath("src/test/resources");
	private Object o;
	
	
	@Before
	public void setUp(){
		
		o = TESTHelper.setUpObject("identifier", workAreaRootPath);
		
		Node n=new Node();
		n.setWorkAreaRootPath(workAreaRootPath);
		wa=new WorkArea(n, o);
		
		
		
		// This is what is configured in the database.
		Map<String,Set<String>> subformatIdentificationPolicies = new HashMap<String,Set<String>>();
		
		subformatIdentificationPolicies.put("de.uzk.hki.da.format.FakeCompressionIdentifier", 
				new HashSet<String>(Arrays.asList(FFConstants.FMT_353) ));
		
		sfs.setSubformatIdentificationPolicies(subformatIdentificationPolicies);
	}
	
	
	@Test
	public void testIdentify() throws InvalidArgumentException, IOException{
		
		
		
		DAFile f = new DAFile("",FFConstants.TIF);
		f.setFormatPUID(FFConstants.FMT_353);
		
		List<FileWithFileFormat> files = new ArrayList<FileWithFileFormat>();
		files.add(f);
		sfs.identify(wa.dataPath(),files,false);
		
		assertEquals(FFConstants.LZW,f.getSubformatIdentifier());
	}
	
	
	@Test
	public void testPUIDNotSet() throws IOException{
		DAFile f = new DAFile(null,FFConstants.TIF);
		
		List<FileWithFileFormat> files = new ArrayList<FileWithFileFormat>();
		files.add(f);
		try {
			sfs.identify(wa.dataPath(),files,false);
			fail();
		} catch (IllegalArgumentException e){}
	}
}
