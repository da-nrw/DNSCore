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

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.hibernate.classic.Session;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.uzk.hki.da.core.HibernateUtil;
import de.uzk.hki.da.model.CentralDatabaseDAO;
import de.uzk.hki.da.model.Contractor;
import de.uzk.hki.da.model.ConversionRoutine;
import de.uzk.hki.da.model.Node;
import de.uzk.hki.da.model.SecondStageScanPolicy;
import de.uzk.hki.da.service.DistributedConversionHelper;


/**
 * CLASS UNDER TEST: FormatScanService.selectProcessingNodesForInformationPackage()
 * @author Daniel M. de Oliveira
 *
 */
public class SelectProcessingNodesTests {
	
	private ScanAction scan = new ScanAction();
	
	Node vm1 = new Node("vm1","01-vm1");
	Node vm2 = new Node("vm2","01-vm2");
	Node vm3 = new Node("vm3","01-vm3");
	
	private CentralDatabaseDAO dummyDao;
	
	
	/**
	 * Helper that prepares a enw CentralDatabaseDAOInterface mock
	 * and prepares the four calls to the mock in ScannerFacade.init()
	 */
	void prepareCentralDatabaseDAOInterfaceMock(){
		dummyDao = mock( CentralDatabaseDAO.class );
		when( dummyDao.getContractor((Session) anyObject(), anyString())).
			thenReturn( new Contractor("PRESENTER","","") )
			.thenReturn( new Contractor("DEFAULT","","") );
			
		when( dummyDao.getSecondStageScanPolicies((Session)anyObject())).thenReturn( new ArrayList<SecondStageScanPolicy>() );
		
	}
	
	/**
	 * Sets the up before class.
	 */
	@BeforeClass
	public static void setUpBeforeClass(){
		HibernateUtil.init("conf/hibernateCentralDB.cfg.xml.inmem");
	}
	
	/**
	 * Sets the up.
	 */
	@Before
	public void setUp() {
		
		prepareCentralDatabaseDAOInterfaceMock();
	}


	/**
	 * When two different ConversionRoutines are required to fulfill on job,
	 * but both of them are available on the initial node, we expect that
	 * only the initial node is selected as the processing node of choice for both
	 * ConversionRoutines.
	 *
	 * @author Daniel M. de Oliveira
	 */
	@Test
	public void testTwoRoutinesOnInitalNode(){
		
			
		@SuppressWarnings("serial")
		Set<Node> nodesForRoutine1 = new HashSet<Node>(){{add(vm1);add(vm3);}};
		ConversionRoutine routine1 = new ConversionRoutine("IM-PNG",
				nodesForRoutine1,"","","");
		@SuppressWarnings("serial")
		Set<Node> nodesForRoutine2 = new HashSet<Node>(){{add(vm2);add(vm1);}};
		ConversionRoutine routine2 = new ConversionRoutine("IM-TIF",
				nodesForRoutine2,"","","");
		Set<ConversionRoutine> set= new HashSet<ConversionRoutine>();
		set.add(routine1);
		set.add(routine2);

		scan.setDao( dummyDao );

		Map<ConversionRoutine,Node> conversionRoutinesSelectedNodes = DistributedConversionHelper
			.selectProcessingNodes( new Node("vm1"), set);

		assertTrue( conversionRoutinesSelectedNodes.size() == 2 );
		assertTrue( conversionRoutinesSelectedNodes.get(routine1).equals("vm1"));
		assertTrue( conversionRoutinesSelectedNodes.get(routine2).equals("vm1"));
	}



	/**
	 * The ConversionRoutines selected for a job is not available because
	 * no node has been chosen on which it can run. This can happen when policies
	 * reference certain routines, but the routines don't exist any longer on any node.
	 * 
	 * @author Daniel M. de Oliveira
	 */
	@Test
	public void testNoNodeDefinedForRoutine(){

		Set<ConversionRoutine> set = new HashSet<ConversionRoutine>();
		set.add( new ConversionRoutine( "NO_NODE", new HashSet<Node>(),"","",""));
		
		scan.setDao(dummyDao);

		try {
			DistributedConversionHelper.selectProcessingNodes( new Node("vm1"), set);
			fail();
		}
		catch (IllegalStateException e)	{ }
	}




	/**
	 * Test one routine on initial node.
	 *
	 * @author Daniel M. de Oliveira
	 */
	@Test
	public void testOneRoutineOnInitialNode(){

		@SuppressWarnings("serial")
		Set<Node> nodesForRoutine = new HashSet<Node>(){{add(vm1);}};
		ConversionRoutine routine = new ConversionRoutine("IM-PNG",
				nodesForRoutine,"","","");
		Set<ConversionRoutine> set = new HashSet<ConversionRoutine>();
		set.add( routine );
		
		scan.setDao(dummyDao);

		assertTrue(
				(((
						DistributedConversionHelper.
				selectProcessingNodes(new Node("vm1"),set).get(routine)
				.equals("vm1"))))
				);
	}




	/**
	 * Test two conversion routine on dedicated nodes.
	 *
	 * @author Daniel M. de Oliveira
	 */
	@Test
	public void testTwoConversionRoutineOnDedicatedNodes(){

		@SuppressWarnings("serial")
		Set<Node> nodesForRoutine1 = new HashSet<Node>(){{add(vm1);add(vm3);}};
		ConversionRoutine routine1 = new ConversionRoutine("IM-PNG",
				nodesForRoutine1,"","","");
		@SuppressWarnings("serial")
		Set<Node> nodesForRoutine2 = new HashSet<Node>(){{add(vm3);}};
		ConversionRoutine routine2 = new ConversionRoutine("IM-TIF",
				nodesForRoutine2,"","","");

		Set<ConversionRoutine> set = new HashSet<ConversionRoutine>();
		set.add(routine1);
		set.add(routine2);

		scan.setDao(dummyDao);

		Map<ConversionRoutine,Node> conversionRoutinesSelectedNodes=
				(DistributedConversionHelper.selectProcessingNodes(new Node("vm1"),set));

		boolean vm2appeared=false;
		boolean vm3appeared=false;
		for (ConversionRoutine routine:conversionRoutinesSelectedNodes.keySet()){

			if (conversionRoutinesSelectedNodes.get(routine).equals("vm1")) vm3appeared=true;
			if (conversionRoutinesSelectedNodes.get(routine).equals("vm3")) vm2appeared=true;
		}

		assertTrue(vm2appeared && vm3appeared);
	}




	/**
	 * Test resolve list of working resources.
	 *
	 * @author Daniel M. de Oliveira
	 */
	@Test
	public void testResolveListOfWorkingResources(){

		@SuppressWarnings("serial")
		Set<Node> nodesForRoutine1 = new HashSet<Node>(){{add(vm1);}};
		ConversionRoutine routine1 = new ConversionRoutine("IM-PNG",
				nodesForRoutine1,"","","");
		@SuppressWarnings("serial")
		Set<Node> nodesForRoutine2 = new HashSet<Node>(){{add(vm1);}};
		ConversionRoutine routine2 = new ConversionRoutine("IM-TIF",
				nodesForRoutine2,"","","");
		@SuppressWarnings("serial")
		Set<Node> nodesForRoutine3 = new HashSet<Node>(){{add(vm2);}};
		ConversionRoutine routine3 = new ConversionRoutine("IM-JPG",
				nodesForRoutine3,"","","");
		@SuppressWarnings("serial")
		Set<Node> nodesForRoutine4 = new HashSet<Node>(){{add(vm2);}};
		ConversionRoutine routine4 = new ConversionRoutine("IM-BMP",
				nodesForRoutine4,"","","");

		Map<ConversionRoutine,Node> conversionRoutinesSelectedNodes=
				new HashMap<ConversionRoutine,Node>();

		conversionRoutinesSelectedNodes.put(routine1, vm1);
		conversionRoutinesSelectedNodes.put(routine2, vm1);
		conversionRoutinesSelectedNodes.put(routine3, vm2);
		conversionRoutinesSelectedNodes.put(routine4, vm2);

		if (!( "01-vm2,01-vm1".equals(DistributedConversionHelper.listOfWorkingResources(conversionRoutinesSelectedNodes.values())))
		|| ( "01-vm1,01-vm2".equals(DistributedConversionHelper.listOfWorkingResources(conversionRoutinesSelectedNodes.values()))))
			fail();

	}

}
