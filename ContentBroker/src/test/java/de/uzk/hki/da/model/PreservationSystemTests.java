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

package de.uzk.hki.da.model;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.classic.Session;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.uzk.hki.da.core.HibernateUtil;



/**
 * The Class PreservationSystemTests.
 */

/**
 * @author Daniel M. de Oliveira
 *
 */
public class PreservationSystemTests {

	/**
	 * Sets the up before class.
	 *
	 * @throws Exception the exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * Tear down after class.
	 *
	 * @throws Exception the exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	private PreservationSystem preservationSystem;
	
	/** The file. */
	private DAFile file;
	
	/** The default c. */
	private User defaultC;
	
	/** The pres c. */
	private User presC;

	/**
	 * Sets the up.
	 *
	 * @throws Exception the exception
	 */
	@Before
	public void setUp() throws Exception {
	
		HibernateUtil.init("src/main/xml/hibernateCentralDB.cfg.xml.inmem");
		
		ConversionRoutine routine = new ConversionRoutine();
		routine.setName("COPY");
		
		// Conversion Policies
		List<ConversionPolicy> policies = new ArrayList<ConversionPolicy>();
		ConversionPolicy one = new ConversionPolicy();
		one.setId(0);
		one.setSource_format("fmt/10");
		one.setConversion_routine(routine);
		ConversionPolicy two = new ConversionPolicy();
		two.setId(1);
		two.setSource_format("fmt/10");
		two.setConversion_routine(routine);
		policies.add(one);
		policies.add(two);
		
		
		List<ConversionPolicy> policies2 = new ArrayList<ConversionPolicy>();
		ConversionPolicy three = new ConversionPolicy();
		three.setId(2);
		three.setSource_format("fmt/10");
		three.setConversion_routine(routine);
		ConversionPolicy four = new ConversionPolicy();
		four.setId(3);
		four.setSource_format("fmt/10");
		four.setConversion_routine(routine);
		policies2.add(three);
		policies2.add(four);

		
		defaultC = new User();
		defaultC.setShort_name("DEFAULT");
		defaultC.setConversion_policies(policies);
		
		presC = new User();
		presC.setShort_name("PRESENTER");
		presC.setConversion_policies(policies2);
		
		CentralDatabaseDAO dao = mock(CentralDatabaseDAO.class);
		when(dao.getContractor((Session) anyObject(), anyString())).thenReturn(presC).thenReturn(defaultC);
//		when(dao.getConversionPoliciesForContractor((Contractor) anyObject())).
//			thenReturn(policies).thenReturn(policies2);
		preservationSystem = new PreservationSystem();
		preservationSystem.initialize(dao);
		
		// creating a valid file
		file = new DAFile(null,"","");
		file.setFormatPUID("fmt/10");
		
		
	}

	/**
	 * Tear down.
	 *
	 * @throws Exception the exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test file has no file format.
	 */
	@Test
	public void testFileHasNoFileFormat(){
		
		file = new DAFile(null,"","");
		file.setFormatPUID("");
		
		List<ConversionPolicy> policies =
				preservationSystem.getApplicablePolicies(file, defaultC.getShort_name());
		assertTrue(policies.isEmpty());
				
	}
	
	/**
	 * Test success scenario.
	 */
	@Test
	public void testSuccessScenario() {

		List<ConversionPolicy> policies = 
				preservationSystem.getApplicablePolicies(file,defaultC.getShort_name());
		assertTrue(0==policies.get(0).getId());
		assertTrue(1==policies.get(1).getId());
		
		List<ConversionPolicy> policies2 = 
				preservationSystem.getApplicablePolicies(file,presC.getShort_name());
		assertTrue(2==policies2.get(0).getId());
		assertTrue(3==policies2.get(1).getId());
	}
	
	/**
	 * Test no policies for contractor.
	 */
	@Test
	public void testNoPoliciesForContractor(){
		List<ConversionPolicy> policies = 
				preservationSystem.getApplicablePolicies(file,"NOT_EXISTENT");
		assertTrue(policies.isEmpty());
	}
	
	

}
