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

package de.uzk.hki.da.ff;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.irods.jargon.core.exception.InvalidArgumentException;
import org.junit.Before;
import org.junit.Test;

import de.uzk.hki.da.ff.IFileWithFileFormat;
import de.uzk.hki.da.ff.FileWithFileFormat;
import de.uzk.hki.da.ff.SecondaryFormatScan;
import de.uzk.hki.da.model.DAFile;
import de.uzk.hki.da.model.SecondStageScanPolicy;

/**
 * @author Daniel M. de Oliveira
 */
public class SecondaryFormatScanTests {

	SecondaryFormatScan sfs = new SecondaryFormatScan();
	
	@Before
	public void setUp(){
		
		// This is what is configured in the database.
		
		SecondStageScanPolicy scan  = new SecondStageScanPolicy();
		scan.setAllowedValues("lzw,ccitt");
		scan.setPUID("fmt/353");
		scan.setFormatIdentifierScriptName("de.uzk.hki.da.ff.FakeCompressionIdentifier");

		SecondStageScanPolicy scan2  = new SecondStageScanPolicy();
		scan2.setPUID("fmt/101");
		scan2.setAllowedValues("EAD,METS");
		scan2.setFormatIdentifierScriptName("script:src/test/resources/format/SecondaryFormatScanTests/abs.sh");
		
		List<ISubformatIdentificationPolicy> secondStageScanPolicies = new ArrayList<ISubformatIdentificationPolicy>();
		secondStageScanPolicies.add((ISubformatIdentificationPolicy)scan);
		secondStageScanPolicies.add((ISubformatIdentificationPolicy)scan2);
		sfs.setSecondStageScanPolicies(secondStageScanPolicies);
		
	}
	
	@Test 
	public void testFormatIdentifierUnkown(){
		// maybe test somewhere up
	}
	
	@Test
	public void testIdentifiedByJavaClass() throws InvalidArgumentException{
		DAFile f = new DAFile(null,null,"tif");
		f.setFormatPUID("fmt/353");
		
		List<IFileWithFileFormat> files = new ArrayList<IFileWithFileFormat>();
		files.add(f);
		sfs.identify(files);
		
		assertEquals("lzw",f.getFormatSecondaryAttribute());
	}
	
	@Test
	public void testIdentifiedByScript() throws InvalidArgumentException{
		FileWithFileFormat f = new FileWithFileFormat(new File("src/test/resources/format/SecondaryFormatScanTests/xml"));
		f.setFormatPUID("fmt/101");
		
		List<IFileWithFileFormat> files = new ArrayList<IFileWithFileFormat>();
		files.add(f);
		sfs.identify(files);
		
		assertEquals("EAD",f.getFormatSecondaryAttribute());
	}
	
	@Test
	public void testPUIDNotSet(){
		DAFile f = new DAFile(null,null,"tif");
		
		List<IFileWithFileFormat> files = new ArrayList<IFileWithFileFormat>();
		files.add(f);
		try {
			sfs.identify(files);
			fail();
		} catch (InvalidArgumentException e){}
	}
}
