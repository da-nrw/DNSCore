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
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.BeforeClass;
import org.junit.Test;

import de.uzk.hki.da.grid.DistributedConversionAdapter;
import de.uzk.hki.da.model.CentralDatabaseDAO;
import de.uzk.hki.da.model.Contractor;
import de.uzk.hki.da.model.ConversionInstruction;
import de.uzk.hki.da.model.ConversionPolicy;
import de.uzk.hki.da.model.ConversionRoutine;
import de.uzk.hki.da.model.DAFile;
import de.uzk.hki.da.model.Job;
import de.uzk.hki.da.model.Node;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.model.PreservationSystem;
import de.uzk.hki.da.utils.Path;
import de.uzk.hki.da.utils.RelativePath;
import de.uzk.hki.da.utils.TESTHelper;



/**
 * The Class ScanActionTests.
 */
public class ScanActionTests {

	private static final Path workAreaRootPath = new RelativePath("src/test/resources/cb/ScanActionTests/");
	
	/** The Constant action. */
	private static final ScanAction action = new ScanAction();
	
	
	/** The Constant job. */
	private static final Job job = new Job();
	
	
	/**
	 * Sets the up before class.
	 *
	 * @throws FileNotFoundException the file not found exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws FileNotFoundException{
		
		Object obj = TESTHelper.setUpObject("1234",workAreaRootPath);
		
		job.setObject(obj);
		job.setRep_name("2011_11_01+00_01+");
		
		DAFile file = new DAFile(obj.getLatestPackage(),"2011_11_01+00_01+a","140849.tif");
		file.setFormatPUID("fmt/353");
		
		List<DAFile> files = new ArrayList<DAFile>(); files.add(file);
		obj.getLatestPackage().getFiles().addAll(files);
		
		Node localNode = new Node("vm2","01-vm2");
		localNode.setWorkAreaRootPath(Path.make(workAreaRootPath));
		action.setLocalNode(localNode);
		
		Set<Node> nodes = new HashSet<Node>(); nodes.add(localNode);
		ConversionRoutine toPng = new ConversionRoutine(
				"TOPNG", nodes,
				"de.uzk.hki.da.cb.CLIConversionStrategy",
				"cp input output","bmp");
		ConversionPolicy policy = new ConversionPolicy(
				new Contractor("DEFAULT","",""),
				"fmt/353",
				toPng,
				null,
				"");
		List<ConversionPolicy> policies = new ArrayList<ConversionPolicy>();
		List<ConversionPolicy> noPolicies = new ArrayList<ConversionPolicy>();
		policies.add(policy);

		PreservationSystem pres = mock ( PreservationSystem.class );
		when(pres.getApplicablePolicies((DAFile) anyObject(), anyString())).thenReturn(policies).thenReturn(noPolicies);
		action.setPreservationSystem(pres);
		
		action.setObject(obj);
		action.setDistributedConversionAdapter(mock (DistributedConversionAdapter.class));
		action.setDao(mock ( CentralDatabaseDAO.class ));
		action.setJob(job);
	}
	
	
	/**
	 * Conversion instructions get created properly.
	 * @throws IOException 
	 */
	@Test 
	public void conversionInstructionsGetCreatedProperly() throws IOException{
		
		action.implementation();
		
		Job job = action.getJob();
		
		ConversionInstruction[] instrs = job.getConversion_instructions().toArray(new ConversionInstruction[0]);
		
		System.out.println(instrs[0]);
		
		assertEquals("2011_11_01+00_01+a/140849.tif",instrs[0].getSource_file().getRep_name()+"/"+instrs[0].getSource_file().getRelative_path());
		assertEquals("",instrs[0].getTarget_folder());
		assertEquals("TOPNG",instrs[0].getConversion_routine().getName());
	}
}
