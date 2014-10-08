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
package de.uzk.hki.da.at;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.hibernate.classic.Session;
import org.junit.After;
import org.junit.Before;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import de.uzk.hki.da.core.C;
import de.uzk.hki.da.core.HibernateUtil;
import de.uzk.hki.da.core.Path;
import de.uzk.hki.da.grid.DistributedConversionAdapter;
import de.uzk.hki.da.grid.GridFacade;
import de.uzk.hki.da.model.Node;
import de.uzk.hki.da.model.PreservationSystem;
import de.uzk.hki.da.model.User;
import de.uzk.hki.da.repository.RepositoryFacade;
import de.uzk.hki.da.test.TESTHelper;
import de.uzk.hki.da.utils.Utilities;

/**
 * @author Daniel M. de Oliveira
 */
public class AcceptanceTest {
	
	protected static Node localNode;
	protected static GridFacade gridFacade;
	protected static RepositoryFacade repositoryFacade;
	protected static DistributedConversionAdapter distributedConversionAdapter;
	protected static User testContractor;
	protected static PreservationSystem preservationSystem;
	
	protected static AcceptanceTestHelper ath = null;
	
	
	/**
	 * @param gridImplBeanName bean name 
	 * @param dcaImplBeanName distributed conversion adapter beanName
	 * @return
	 */
	
	private static void instantiateGrid(Properties properties) {
		
		String gridImplBeanName = properties.getProperty("cb.implementation.grid");
		String dcaImplBeanName  = properties.getProperty("cb.implementation.distributedConversion");
		
		if (gridImplBeanName==null) gridImplBeanName="fakeGridFacade";
		if (dcaImplBeanName==null) dcaImplBeanName="fakeDistributedConversionAdapter";
		
		AbstractApplicationContext context =
				new FileSystemXmlApplicationContext("conf/beans.xml");
		
		gridFacade = (GridFacade) context.getBean(gridImplBeanName);
		distributedConversionAdapter = (DistributedConversionAdapter) context.getBean(dcaImplBeanName);
		context.close();
	}
	
	private static void instantiateNode() {
		
		AbstractApplicationContext context = 
				new FileSystemXmlApplicationContext("conf/beans.xml");
		localNode = (Node) context.getBean("localNode");
		
		Session session = HibernateUtil.openSession();
		session.beginTransaction();
		session.refresh(localNode);
		session.close();
		
		context.close();
	}
	
	private static void instantiateRepository(Properties properties) {
		
		String repImplBeanName=properties.getProperty("cb.implementation.repository");
		if (repImplBeanName==null) repImplBeanName="fakeRepositoryFacade";
		
		AbstractApplicationContext context =
				new FileSystemXmlApplicationContext("conf/beans.xml");
		repositoryFacade = (RepositoryFacade) context.getBean(repImplBeanName);
		context.close();
	}
	
	
	@Before
	public void setUpAcceptanceTest() throws IOException{
		
		HibernateUtil.init("conf/hibernateCentralDB.cfg.xml");
		
		instantiateNode();
		if (localNode==null) throw new IllegalStateException("localNode could not be instantiated");
	
		System.out.println("localnode: "+localNode.getName());
		
		Properties properties = Utilities.read(new File("conf/config.properties"));
		instantiateGrid(properties);
		if (gridFacade==null) throw new IllegalStateException("gridFacade could not be instantiated");
		
		instantiateRepository(properties);
		if (repositoryFacade==null) throw new IllegalStateException("repositoryFacade could not be instantiated");
	
		Session session = HibernateUtil.openSession();
		session.beginTransaction();
		testContractor = getContractor(session, "TEST");
	
		preservationSystem = (PreservationSystem) session.get(PreservationSystem.class, 1);
		session.close();
		
		ath = new AcceptanceTestHelper(gridFacade,localNode,testContractor);
	}

	/**
	 * Gets the contractor.
	 *
	 * @param contractorShortName the contractor short name
	 * @return null if no contractor for short name could be found
	 */
	private User getContractor(Session session, String contractorShortName) {
	
		@SuppressWarnings("rawtypes")
		List list;	
		list = session.createQuery("from User where short_name=?1")
	
				.setParameter("1",contractorShortName).setReadOnly(true).list();
		
		if (list.isEmpty())
			return null;
	
		return (User) list.get(0);
	}
	
	
	
	@After
	public void tearDownAcceptanceTest(){
		TESTHelper.clearDB();
		cleanStorage();
	}
	

	private void cleanStorage(){
		FileUtils.deleteQuietly(Path.makeFile(localNode.getWorkAreaRootPath(),"work","TEST"));
		FileUtils.deleteQuietly(Path.make(localNode.getIngestAreaRootPath(),"TEST").toFile());
		FileUtils.deleteQuietly(Path.makeFile(localNode.getGridCacheAreaRootPath(),C.WA_AIP,C.TEST_USER_SHORT_NAME));
		FileUtils.deleteQuietly(Path.make(localNode.getWorkAreaRootPath(),"pips","institution","TEST").toFile());
		FileUtils.deleteQuietly(Path.make(localNode.getWorkAreaRootPath(),"pips","public","TEST").toFile());
		FileUtils.deleteQuietly(Path.make(localNode.getUserAreaRootPath(),"/TEST/outgoing").toFile());
		
		distributedConversionAdapter.remove("work/TEST");
		distributedConversionAdapter.remove("aip/TEST");
		distributedConversionAdapter.remove("pips/institution/TEST");
		distributedConversionAdapter.remove("pips/public/TEST");
		
		distributedConversionAdapter.create("work/TEST");
		distributedConversionAdapter.create("aip/TEST");
		distributedConversionAdapter.create("pips/institution/TEST");
		distributedConversionAdapter.create("pips/public/TEST");
		
		Path.make(localNode.getUserAreaRootPath(),"/TEST/outgoing").toFile().mkdirs();
		Path.makeFile(localNode.getGridCacheAreaRootPath(),"aip",C.TEST_USER_SHORT_NAME).mkdirs();
		Path.make(localNode.getIngestAreaRootPath(),"/TEST").toFile().mkdirs();
		Path.make(localNode.getWorkAreaRootPath(),"/work/TEST").toFile().mkdirs();
		Path.make(localNode.getWorkAreaRootPath(),"/pips/public/TEST").toFile().mkdirs();
		Path.make(localNode.getWorkAreaRootPath(),"/pips/institution/TEST").toFile().mkdirs();
	}
	
	

	
}
