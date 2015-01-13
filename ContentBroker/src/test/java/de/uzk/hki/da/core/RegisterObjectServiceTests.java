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
import static org.junit.Assert.fail;

import java.util.Date;

import org.hibernate.Session;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import de.uzk.hki.da.model.Node;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.model.PreservationSystem;
import de.uzk.hki.da.model.User;
import de.uzk.hki.da.service.HibernateUtil;
import de.uzk.hki.da.utils.Utilities;


/**
 * The Class URNGeneratorTests.
 * @author: Daniel M. de Oliveira
 */
public class RegisterObjectServiceTests {

	private static Node node;
	private static User contractor;
	private static PreservationSystem pSystem;
	private static RegisterObjectService registerObjectService;
	private static int urnIndex=92;

	/**
	 * TODO Up until now i couldn't find a sample test suite of valid URNs.
	 * Tests URNCheckDigitGenerator.checkDigit
	 */
	@Test
	public void testCheckDigit(){
		String base = "urn:nbn:de:gbv:089-332175294";
		assertEquals(new URNCheckDigitGenerator().checkDigit( base ),"5");
	}
	
	/**
	 * Sets the up.
	 */
	@BeforeClass
	public static void setUp(){
		HibernateUtil.init("src/main/xml/hibernateCentralDB.cfg.xml.inmem");
		Session session = HibernateUtil.openSession();
		session.getTransaction().begin();
		session.createSQLQuery("DELETE FROM nodes").executeUpdate();
		session.createSQLQuery("DELETE FROM preservation_system").executeUpdate();
		
		node = new Node("vm1","vm1-01");
		node.setUrn_index(urnIndex);
		
		contractor = new User();
		contractor.setShort_name("TEST");
		session.save(contractor);
		session.flush();
		
		pSystem = new PreservationSystem();
		pSystem.setUrnNameSpace("urn:nbn:de:danrw");
		pSystem.setMinRepls(1);
		pSystem.setAdmin(contractor);

		session.save(node);
		session.save(pSystem);
		
		session.getTransaction().commit();
		session.close();

		registerObjectService = new RegisterObjectService();
		registerObjectService.setLocalNodeId(new Integer(node.getId()).toString());
		registerObjectService.setPreservationSystemId(new Integer(pSystem.getId()).toString());
		registerObjectService.init();
	}
	
	/**
	 * Tear down.
	 */
	@AfterClass
	public static void tearDown() {
		Session session = HibernateUtil.openSession();
		session.getTransaction().begin();
		session.createSQLQuery("DELETE FROM nodes").executeUpdate();
		session.createSQLQuery("DELETE FROM preservation_system").executeUpdate();
		session.getTransaction().commit();
		session.close();
	}
	
	


	
	
	/**
	 * This test excludes checking the checkdigit.
	 * For a test of the checkdigit please refer to {@link #testCheckDigit()} 
	 * 
	 * Tests URNGenerator.generateURNForNode
	 * @author: Daniel M. de Oliveira
	 */
	@Test
	public void generateIdentifier(){
		Object object = registerObjectService.registerObject("containerName", contractor);
		assertEquals(createIdentifier(urnIndex+1),object.getIdentifier());
	}
	
	
	@Test
	public void generateExistingIdentifier() {
		Session session = HibernateUtil.openSession();
		session.getTransaction().begin();
		node.setUrn_index(urnIndex);
		session.update(node);
		Object o = new Object();
		
		o.setIdentifier(createIdentifier(urnIndex+1));
		session.save(o);
		session.getTransaction().commit();
		session.close();
		
		try {
			registerObjectService.registerObject("containerName", contractor);
			fail();
		}catch(IllegalStateException e){
			System.out.println("Excpected error. "+e);
		}
	}

	
	
	private String createIdentifier(Integer urnIndex) {
		String urnWithoutCheckDigit=pSystem.getUrnNameSpace()+"-"+node.getId()+"-"+ Utilities.todayAsSimpleIsoDate(new Date())+urnIndex;
		String urn=urnWithoutCheckDigit+new URNCheckDigitGenerator().checkDigit(urnWithoutCheckDigit);
		String identifier=urn.replace(pSystem.getUrnNameSpace()+"-", "");
		return identifier;
	}
}
