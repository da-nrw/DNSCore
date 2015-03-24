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
package de.uzk.hki.da.core;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

import de.uzk.hki.da.grid.IrodsGridFacade;
import de.uzk.hki.da.grid.IrodsSystemConnector;
import de.uzk.hki.da.model.Node;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.model.Package;
import de.uzk.hki.da.model.PreservationSystem;
import de.uzk.hki.da.model.StoragePolicy;
import de.uzk.hki.da.model.User;


/**
 * The Class IntegrityScannerWorkerTest.
 *
 * @author Jens Peters
 * @author Daniel M. de Oliveira
 */
public class IntegrityScannerWorkerTest {
	
	/** The base path. */
	String basePath = "src/test/resources/integrity/IntegrityScanner/";
	
	/** The urn. */
	 static String urn = "123456";
	
	/** The package1 path. */
	String package1Path = "TEST/"+urn+"/"+urn+".pack_1.tar";
	
	/** The package2 path. */
	String package2Path = "TEST/"+urn+"/"+urn+".pack_2.tar";

	
	StoragePolicy sp ;
	IrodsGridFacade gc;
	static Node node;
	
	User user;
	
	PreservationSystem pSystem;	
	
	Object obj;
	
	IntegrityScannerWorker worker;
	
	
	
	/**
	 * Sets the up before class.
	 */
	@Before
	public  void setUp() {	
			user = new User();
			user.setEmailAddress("noreply");
			user.setId(1);
			node = new Node(); 
			node.setId(1);
			node.setAdmin(user);

			pSystem = new PreservationSystem();
			pSystem.setMinRepls(3);
			pSystem.setId(1);
			pSystem.setAdmin(user);

			worker = new IntegrityScannerWorker();
			obj = new Object();
			
			Package pack1 = new Package();
			pack1.setName("1");
			Package pack2 = new Package();
			pack2.setName("2");
			
			obj.getPackages().add(pack1);
			obj.getPackages().add(pack2);
			obj.setObject_state(66);
			obj.setIdentifier(urn);
			obj.setContractor(new User("TEST","",""));
			worker.setLocalNodeId("1");
			worker.setpSystem(pSystem);;
			worker.setSleepFor(100L);
			worker.setNode(node);
	}	
	
	/**
	 * Test object integrity not achieved cause package2 is broken.
	 */
	@Test
	public void testObjectIntegrityNotAchievedCausePackage2IsBroken() {

		
		 gc = mock(IrodsGridFacade.class);
		IrodsSystemConnector irods = mock (IrodsSystemConnector.class);
		when (irods.getZone()).thenReturn("c-i");
		when (gc.getirodsSystemConnector()).thenReturn(irods);
		
		when (gc.isValid(package1Path)).thenReturn(true);
		when (gc.storagePolicyAchieved(anyString(),(StoragePolicy) anyObject())).thenReturn(true);
		when (gc.isValid(package2Path)).thenReturn(false);
	
		worker.setGridFacade(gc);
		assertEquals(51,worker.checkObjectValidity(obj));
	}

	
	
	
	/**
	 * Test object integrity achieved.
	 */
	@Test
	public void testObjectIntegrityAchieved() {
		obj.setObject_state(100);
		gc = mock(IrodsGridFacade.class);
		IrodsSystemConnector irods = mock (IrodsSystemConnector.class);

		when (irods.getZone()).thenReturn("c-i");
		

		when (gc.getirodsSystemConnector()).thenReturn(irods);
		
		when (gc.isValid(package1Path)).thenReturn(true);
		when (gc.storagePolicyAchieved(anyString(),(StoragePolicy)anyObject())).thenReturn(true);
		when (gc.isValid(package2Path)).thenReturn(true);
		worker.setGridFacade(gc);
		
		assertEquals(100,worker.checkObjectValidity(obj));
	}
}
