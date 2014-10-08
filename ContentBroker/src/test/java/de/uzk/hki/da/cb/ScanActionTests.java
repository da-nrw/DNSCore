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

package de.uzk.hki.da.cb;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.uzk.hki.da.core.C;
import de.uzk.hki.da.core.Path;
import de.uzk.hki.da.core.RelativePath;
import de.uzk.hki.da.grid.DistributedConversionAdapter;
import de.uzk.hki.da.model.ConversionInstruction;
import de.uzk.hki.da.model.ConversionPolicy;
import de.uzk.hki.da.model.ConversionRoutine;
import de.uzk.hki.da.model.DAFile;
import de.uzk.hki.da.model.Node;
import de.uzk.hki.da.model.PreservationSystem;



/**
 * Tests the ScanAction.
 * 
 * @author Daniel M. de Oliveira
 */
public class ScanActionTests extends ConcreteActionUnitTest{

	private static final Path workAreaRootPath = new RelativePath("src/test/resources/cb/ScanActionTests/");
	
	private static final String TIFF_TESTFILE = "140849.tif";
	private static final String TIF_PUID = "fmt/353";
	private static final String REPNAME = "2011_11_01+00_01+";

	private static final File premisFile = Path.makeFile(workAreaRootPath,"work","TEST","identifier","data","2011_11_01+00_01+a","premis.xml");
	
	@ActionUnderTest
	ScanAction action = new ScanAction();

	
	/**
	 * Sets the up before class.
	 * @throws IOException 
	 */
	@Before
	public void setUp() throws IOException{
		
		
		j.setRep_name(REPNAME);
		
		DAFile file = new DAFile(o.getLatestPackage(),REPNAME+"a",TIFF_TESTFILE);
		file.setFormatPUID(TIF_PUID);
		List<DAFile> files = new ArrayList<DAFile>(); files.add(file);
		o.getLatestPackage().getFiles().addAll(files);

		
		Set<Node> nodes = new HashSet<Node>(); nodes.add(n);
		ConversionRoutine toPng = new ConversionRoutine(
				"TOPNG", 
				"de.uzk.hki.da.cb.CLIConversionStrategy",
				"cp input output","bmp");
		ConversionPolicy policy = new ConversionPolicy(
				TIF_PUID,
				toPng
				);
		List<ConversionPolicy> policies = new ArrayList<ConversionPolicy>();
		List<ConversionPolicy> noPolicies = new ArrayList<ConversionPolicy>();
		policies.add(policy);

		PreservationSystem pSystem = mock (PreservationSystem.class);
		
		when(pSystem.getApplicablePolicies((DAFile) anyObject(), (Boolean)anyObject())).thenReturn(policies).thenReturn(noPolicies);
		when(pSystem.getAdmin()).thenReturn(o.getContractor()); // quick fix
		action.setDistributedConversionAdapter(mock (DistributedConversionAdapter.class));
		action.setPSystem(pSystem);
		n.setWorkAreaRootPath(workAreaRootPath);

		
		FileUtils.copyFile(Path.makeFile(workAreaRootPath,"premis.xml_MIGRATION_NOTIFY"), 
				premisFile);
	}
	
	@After
	public void tearDown() {
		premisFile.delete();
	}
	
	
	@Test
	public void testMigrationToConfirm() throws IOException {
		premisFile.delete();
		FileUtils.copyFile(Path.makeFile(workAreaRootPath,"premis.xml_MIGRATION_CONFIRM"),
				premisFile);
		
		action.implementation();
		assertEquals(C.QUESTION_MIGRATION_ALLOWED,j.getQuestion());
		assertEquals(C.WORKFLOW_STATUS_WAIT___PROCESS_FOR_USER_DECISION_ACTION,
				action.getEndStatus());
	}
	
	@Test
	public void testMigrationJustNotify() throws IOException {
		
		action.implementation();
		assertEquals("",j.getQuestion());
		assertEquals(null,action.getEndStatus());
	}
	
	
	
	/**
	 * Conversion instructions get created properly.
	 * @throws IOException 
	 */
	@Test 
	public void conversionInstructionsGetCreatedProperly() throws IOException{
		action.implementation();
		
		ConversionInstruction[] instrs = j.getConversion_instructions().toArray(new ConversionInstruction[0]);
		
		System.out.println(instrs[0]);
		
		assertEquals(REPNAME+"a/"+TIFF_TESTFILE,instrs[0].getSource_file().getRep_name()+"/"+instrs[0].getSource_file().getRelative_path());
		assertEquals("",instrs[0].getTarget_folder());
		assertEquals("TOPNG",instrs[0].getConversion_routine().getName());
	}
}
