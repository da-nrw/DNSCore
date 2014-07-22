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
package de.uzk.hki.da.integrity;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.uzk.hki.da.core.HibernateUtil;
import de.uzk.hki.da.grid.IrodsGridFacade;
import de.uzk.hki.da.grid.IrodsSystemConnector;
import de.uzk.hki.da.model.CentralDatabaseDAO;
import de.uzk.hki.da.model.Contractor;
import de.uzk.hki.da.model.Node;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.model.Package;
import de.uzk.hki.da.model.StoragePolicy;


/**
 * The Class IntegrityScannerWorkerTest.
 *
 * @author Jens Peters
 * @author Daniel M. de Oliveira
 */
public class IntegrityScannerWorkerTest {
	
	/** The base path. */
	String basePath = "src/test/resources/integrity/IntegrityScanner/";
	
	/** The worker. */
	IntegrityScannerWorker worker = new IntegrityScannerWorker();
	
	/** The dao. */
	CentralDatabaseDAO dao;
	
	/** The urn. */
	String urn = "123456";
	
	/** The package1 path. */
	String package1Path = "TEST/"+urn+"/"+urn+".pack_1.tar";
	
	/** The package2 path. */
	String package2Path = "TEST/"+urn+"/"+urn+".pack_2.tar";
	
	/** The obj. */
	Object obj;
	
	StoragePolicy sp ;
	
	
	/**
	 * Sets the up.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@Before
	public void setUp() throws IOException{
		
		dao = mock(CentralDatabaseDAO.class);
		obj = new Object();
		
		Package pack1 = new Package();
		pack1.setName("1");
		Package pack2 = new Package();
		pack2.setName("2");
		
		obj.getPackages().add(pack1);
		obj.getPackages().add(pack2);
		obj.setObject_state(66);
		obj.setIdentifier(urn);
		obj.setContractor(new Contractor("TEST","",""));
		Node node = new Node("test");
		sp = new StoragePolicy(node);
		worker.setMinNodes(3);
	}
	
	
	
	
	/**
	 * Sets the up before class.
	 */
	@BeforeClass
	public static void setUpBeforeClass() {
//		HibernateUtil.init("src/main/xml/hibernateCentralDB.cfg.xml.inmem");
	}
	
	/**
	 * Tear down.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@After 
	public void tearDown() throws IOException {
			// TODO clear the inmem object db
	}
	
	
	
	
	/**
	 * Test object integrity not achieved cause package2 is broken.
	 */
	@Test
	public void testObjectIntegrityNotAchievedCausePackage2IsBroken() {

		IrodsGridFacade gc = mock(IrodsGridFacade.class);
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
	
		IrodsGridFacade gc = mock(IrodsGridFacade.class);
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
