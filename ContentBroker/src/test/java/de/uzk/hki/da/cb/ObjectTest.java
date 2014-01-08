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

//jp
import org.junit.BeforeClass;
import org.junit.Test;


/**
 * The Class ObjectTest.
 */
public class ObjectTest {
	
	/** The object identifier. */
	public static String objectIdentifier = ":ABCD:CONTRACTOR3:";

	/**
	 * Sets the up before class.
	 *
	 * @throws Exception the exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
//		HibernateUtil.init("conf/hibernateCentralDbWithInmem.cfg.xml");
//		dao.clearAll();
//
//		dao.addContractor(new Contractor(
//				"KCG","","info@hki.uni-koeln.de"));
	}

	
	// TODO StoreObjectActionShould be under test
	/**
	 * Creates the object.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void createObject() throws Exception {
		
//		Job job= dao.createJob(new Job(urn, "KCG", "vm1"));
//		debugDao.showJobs();
//		Package pkg = dao.createPackage(new Package("1", job.getUrn()));
//		job.setPackage(pkg);
//		System.out.println(".."); debugDao.showJobs();
//		Contractor contractor = dao.getContractor("KCG");
//		
//		Object obj = dao.updateObjectFromJob( job, contractor );
//		
//		Assert.assertEquals(obj.getContractor().getShort_name(), "KCG");
//		Assert.assertEquals(urn, obj.getUrn());
//		Assert.assertEquals(urn, obj.getPackages().get(0).getObject_urn());
//		System.out.println("Object state: " + obj.getObject_state() );
		
	}
	

	
	/*
	@Test
	public void addObject() throws Exception {
		Thread.sleep(1000);
		Job job= dao.createJob(urn,"KCG", "vm1");
		dao.updateObjectFromJob(urn);
		Object obj = dao.getObject(urn);
		Assert.assertEquals(obj.getContractor().getShort_name(), "KCG");
		Assert.assertEquals(urn, obj.getUrn());
		Assert.assertEquals(urn, obj.getPackages().get(1).getObject_urn());


		for ( Iterator<de.uzk.hki.da.db.Package> pack= obj.getPackages().iterator(); pack.hasNext(); )
		 System.out.println("Package: " + pack.next().getName() );

	}*/
	


}
