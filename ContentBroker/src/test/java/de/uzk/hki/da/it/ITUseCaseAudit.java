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

package de.uzk.hki.da.it;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.hibernate.classic.Session;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import de.uzk.hki.da.db.HibernateUtil;
import de.uzk.hki.da.model.Contractor;
import de.uzk.hki.da.model.Job;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.model.Package;


/**
 * The Class ITUseCaseAudit.
 *
 * @author Jens Peters
 */

public class ITUseCaseAudit extends ITBase{

	
	/** The object identifier. */
	private final String objectIdentifier = "integrationTestAudit";

	/** The job. */
	private Job job;
	
	/** The object. */
	private Object object;
	
	/** The pkg. */
	private Package pkg;
	
	/**
	 * Sets the up before class.
	 *
	 * @throws Exception the exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
		HibernateUtil.init(hibernateConfigFilePath);
		
		context = new FileSystemXmlApplicationContext("conf/beans.xml");
		
		Session session = HibernateUtil.openSession();
		session.beginTransaction();
		setUpNode();
		session.close();
	}

	
	
	/**
	 * Sets the up.
	 *
	 * @throws Exception the exception
	 */
	@Before
	public void setUp() throws Exception {
		setupSysConnector();
		irodsSystemConnector.createCollection("/da-nrw/aip/TEST/integrationTestAudit");
	}

	/**
	 * Creates the pkg variable which is used in every test and saves it to the db.
	 *
	 * @param urn the urn
	 * @param orig_name the orig_name
	 */
	private void setUpObjectPackageAndJob(String urn,String orig_name){
		
		Session session = HibernateUtil.openSession();
		session.beginTransaction();
		
		pkg = new Package();
		pkg.setName("1"); 
		
		object = new Object();
		object.setIdentifier(urn);
		object.setOrig_name(urn);
		object.setContractor(dao.getContractor(HibernateUtil.getThreadBoundSession(), "TEST"));
		object.getPackages().add(pkg);
		
		Contractor c = new Contractor();
		c.setId(1); // TEST
		
		job = new Job();
		job.setObject(object);
		job.setInitial_node(nameOfOurIntegrationTestNode);
		job.setStatus("5000");
		
		session.save(job);
		session.getTransaction().commit();
		session.close();
	}
	
	
	/**
	 * Tear down.
	 *
	 * @throws Exception the exception
	 */
	@After
	public void tearDown() throws Exception {
		
		irodsSystemConnector.removeFileAndEatException("/da-nrw/aip/TEST/integrationTestAudit/integrationTestAudit.pack_1.tar");
		irodsSystemConnector.logoff();
		
		Session session = HibernateUtil.openSession();
		session.beginTransaction();
		session.delete(object);
		session.getTransaction().commit();
		session.close();
	}
	
	/**
	 * Test audit ok.
	 *
	 * @author Jens Peters
	 */
	@Test
	public void testAuditOK() {
		setUpObjectPackageAndJob(objectIdentifier, objectIdentifier);
		
		iputPackage("/data/danrw/testPackages/integrationTestAudit.pack_1.tar", "/da-nrw/aip/TEST/integrationTestAudit/integrationTestAudit.pack_1.tar");
		irodsSystemConnector.computeChecksum("/da-nrw/aip/TEST/integrationTestAudit/integrationTestAudit.pack_1.tar");

		irodsSystemConnector.replicateDaoToResGroupSynchronously("/da-nrw/aip/TEST/integrationTestAudit/integrationTestAudit.pack_1.tar", "hbz");
		irodsSystemConnector.replicateDaoToResGroupSynchronously("/da-nrw/aip/TEST/integrationTestAudit/integrationTestAudit.pack_1.tar", "lvr");
		job = connectAndRunAction("AuditAction");
		assertEquals(new File(aipResourceVaultPath+"aip/TEST/integrationTestAudit/integrationTestAudit.pack_1.tar").exists(),true);

		Object obj = getUniqueObjectForObjectIdentifier(object.getIdentifier());
		assertEquals(100,obj.getObject_state());
	}
	
	
	
	
	/**
	 * 
	 * @author Daniel M. de Oliveira
	 * @throws IOException 
	 */
	@Test
	public void testObjectHasNoChecksum() throws IOException{
		setUpObjectPackageAndJob(objectIdentifier, objectIdentifier);
		
		iputPackage("/data/danrw/testPackages/integrationTestAudit.pack_1.tar", "/da-nrw/aip/TEST/integrationTestAudit");
		
	    job = connectAndRunAction("AuditAction");
	    Object obj = getUniqueObjectForObjectIdentifier(object.getIdentifier());
	    
	    assertEquals(60,obj.getObject_state());
	}
	
	
	
	/**
	 * Test audit nok.
	 *
	 * @throws Exception the exception
	 * @author Jens Peters
	 */
	@Test
	public void testAuditNOK() throws Exception {
		setUpObjectPackageAndJob(objectIdentifier, objectIdentifier);
		
		iputPackage("/data/danrw/testPackages/integrationTestAudit.pack_1.tar", "/da-nrw/aip/TEST/integrationTestAudit");
		irodsSystemConnector.computeChecksum("/da-nrw/aip/TEST/integrationTestAudit/integrationTestAudit.pack_1.tar");
		
		irodsSystemConnector.replicateDaoToResGroupSynchronously("/da-nrw/aip/TEST/integrationTestAudit/integrationTestAudit.pack_1.tar", "hbz");
		irodsSystemConnector.replicateDaoToResGroupSynchronously("/da-nrw/aip/TEST/integrationTestAudit/integrationTestAudit.pack_1.tar", "lvr");
		File destFile = new File(aipResourceVaultPath+"aip/TEST/integrationTestAudit/integrationTestAudit.pack_1.tar");
		assertEquals(destFile.exists(),true);
		FileWriter writer = new FileWriter(destFile ,false);
	    writer.write("Hallo Wie gehts?");
	    writer.close();
	    job = connectAndRunAction("AuditAction");
	    Object obj = getUniqueObjectForObjectIdentifier(object.getIdentifier());
	    
	    assertEquals(60,obj.getObject_state());
	}

	
	

}
