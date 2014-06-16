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
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import de.uzk.hki.da.model.CentralDatabaseDAO;
import de.uzk.hki.da.model.ConversionInstruction;
import de.uzk.hki.da.model.ConversionRoutine;
import de.uzk.hki.da.model.DAFile;
import de.uzk.hki.da.model.Job;
import de.uzk.hki.da.model.Node;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.model.Package;
import de.uzk.hki.da.service.DistributedConversionHelper;
import de.uzk.hki.da.utils.Path;
import de.uzk.hki.da.utils.RelativePath;
import de.uzk.hki.da.utils.TESTHelper;


/**
 * The Class ConvertActionNodeSplittingTests.
 */
public class ConvertActionNodeSplittingTests {

	/** The local node. */
	private final Node localNode  = new Node("vm1","01-vm1");
	
	/** The friend node. */
	private final Node friendNode = new Node("vm2","01-vm2");
	
	/** The im. */
	private ConversionRoutine im = null;
	
	/** The copy. */
	private ConversionRoutine copy = null;
	
	/** The job. */
	private Job job;
	
	/** The pkg. */
	Package pkg;
	
	/** The all nodes. */
	Set<Node> allNodes = new HashSet<Node>();
	
	/** The convert. */
	ScanAction convert = new ScanAction();
	
	/** The base path. */
	Path basePath = new RelativePath("src/test/resources/cb/GenerateConversionInstructionsTests/");
	
	/** The o. */
	Object o = TESTHelper.setUpObject("1177",basePath);
	
	
	/**
	 * Sets the up.
	 */
	@Before
	public void setUp(){
		
		pkg = mock(Package.class);
		
		CentralDatabaseDAO dao = mock( CentralDatabaseDAO.class );
		
		allNodes.add(localNode);
		allNodes.add(friendNode);
		
		job = new Job();
		job.setId(1);
		job.setResponsibleNodeName("vm1");
		
		convert.setLocalNode(localNode);
		convert.setDao(dao);
		convert.setJob(job);
		
		@SuppressWarnings("serial")
		Set<Node> n1 = new HashSet<Node>(){{}};
		im = new ConversionRoutine(
				"IM",
				n1,
				"de.uzk.hki.da.convert.CLIConversionStrategy",
				"convert input output",
				"png");
		
		@SuppressWarnings("serial")
		Set<Node> n2 = new HashSet<Node>(){{}};
		copy = new ConversionRoutine(
				"COPY",
				n2,
				"de.uzk.hki.da.convert.CLIConversionStrategy",
				"cp input output",
				"*");
	}
	
	/**
	 * We have two ConversionInstruction. One of them (with routine IM) is
	 * to be done as a friend job, the other (with routine COPY) is to be done 
	 * locally.
	 */
	@Test
	public void test(){
		
		ConversionInstruction ci1 = new ConversionInstruction();
		ci1.setTarget_folder("2011+11+01+b");
		ci1.setSource_file(new DAFile(pkg,"2011+11+01+a","premis.xml"));
		ci1.setNode("vm1");
		ci1.setConversion_routine(copy);
		
		ConversionInstruction ci2 = new ConversionInstruction();
		ci2.setTarget_folder("2011+11+01+b");
		ci2.setSource_file(new DAFile(pkg,"2011+11+01+a","140864.tif"));
		ci2.setNode("vm2");
		ci2.setConversion_routine(im);
	
		job.getConversion_instructions().add(ci1);
		job.getConversion_instructions().add(ci2);
		
		List<Job> friendJobs = DistributedConversionHelper.createJobsWhichCantBeDoneLocally(
				job,allNodes,"vm1",o);
		
		assertFalse(friendJobs.isEmpty());
		
		ArrayList<ConversionInstruction> results = 
				new ArrayList<ConversionInstruction>(friendJobs.get(0).getConversion_instructions());
		
		assertEquals("2011+11+01+a/140864.tif",
				results.get(0).getSource_file().getRep_name()+"/"+results.get(0).getSource_file().getRelative_path());
	}	
}
