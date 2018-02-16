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
import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import de.uzk.hki.da.action.ActionFactory;
import de.uzk.hki.da.grid.DistributedConversionAdapter;
import de.uzk.hki.da.grid.GridFacade;
import de.uzk.hki.da.grid.IrodsCommandLineConnector;
import de.uzk.hki.da.model.Node;
import de.uzk.hki.da.model.PreservationSystem;
import de.uzk.hki.da.model.StoragePolicy;
import de.uzk.hki.da.model.User;
import de.uzk.hki.da.model.WorkArea;
import de.uzk.hki.da.repository.ElasticsearchMetadataIndex;
import de.uzk.hki.da.repository.MetadataIndex;
import de.uzk.hki.da.repository.RepositoryFacade;
import de.uzk.hki.da.service.HibernateUtil;
import de.uzk.hki.da.test.TESTHelper;
import de.uzk.hki.da.utils.C;
import de.uzk.hki.da.utils.FolderUtils;
import de.uzk.hki.da.utils.Path;
import de.uzk.hki.da.utils.PropertiesUtils;
import gov.loc.repository.bagit.domain.Bag;
import gov.loc.repository.bagit.reader.BagReader;
import gov.loc.repository.bagit.verify.BagVerifier;

/**
 * @author Daniel M. de Oliveira
 */
public class AcceptanceTest {
	private static final String CONF_BEANS_XML = "conf/beans.xml";
	private static String CI_WORKING_RESOURCE = "ciWorkingResource";
	private static String CI_ARCHIVE_RESOURCE = "ciArchiveRescGroup";
	private static String CI_ARCHIVE_STORAGE = "/ci/archiveStorage/aip/TEST/";
	private static final String BEAN_NAME_FAKE_REPOSITORY_FACADE = "fakeRepositoryFacade";
	private static final String BEAN_NAME_FAKE_METADATA_INDEX = "fakeMetadataIndex";
	protected static Node localNode;
	protected static GridFacade gridFacade;
	protected static RepositoryFacade repositoryFacade;
	protected static MetadataIndex metadataIndex;
	protected static DistributedConversionAdapter distributedConversionAdapter;
	protected static User testContractor;
	protected static PreservationSystem preservationSystem;
	protected static AcceptanceTestHelper ath = null;
	protected static StoragePolicy sp;
	private static String testIndex;
	
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
		
		if(properties.getProperty("localNode.workingResource")!=null) 
			CI_WORKING_RESOURCE=properties.getProperty("localNode.workingResource");
		if(properties.getProperty("localNode.replDestinations")!=null) 
			CI_ARCHIVE_RESOURCE=properties.getProperty("localNode.replDestinations");
		
		AbstractApplicationContext context =
				new FileSystemXmlApplicationContext(CONF_BEANS_XML);
		
		gridFacade = (GridFacade) context.getBean(gridImplBeanName);
		distributedConversionAdapter = (DistributedConversionAdapter) context.getBean(dcaImplBeanName);
		context.close();
	}
	
	private static void instantiateNode() {
		
		AbstractApplicationContext context = 
				new FileSystemXmlApplicationContext(CONF_BEANS_XML);
		localNode = (Node) context.getBean("localNode");
		
		Session session = HibernateUtil.openSession();
		session.beginTransaction();
		session.refresh(localNode);
		session.close();
		
		context.close();
	}
	
	private static void instantiateRepository(Properties properties) {
		
		String repImplBeanName=properties.getProperty("cb.implementation.repository");
		if (repImplBeanName==null) repImplBeanName=BEAN_NAME_FAKE_REPOSITORY_FACADE;
		
		AbstractApplicationContext context =
				new FileSystemXmlApplicationContext(CONF_BEANS_XML);
		repositoryFacade = (RepositoryFacade) context.getBean(repImplBeanName);
		context.close();
	}
	
	private static void instantiateMetadataIndex(Properties properties) {
		String indexImplBeanName=properties.getProperty("cb.implementation.index");
		testIndex=properties.getProperty("elasticsearch.index")+MetadataIndex.TEST_INDEX_SUFFIX;;
		if (indexImplBeanName==null) indexImplBeanName=BEAN_NAME_FAKE_METADATA_INDEX;
		AbstractApplicationContext context =
				new FileSystemXmlApplicationContext(CONF_BEANS_XML);
		metadataIndex = (MetadataIndex) context.getBean(indexImplBeanName);
		context.close();
	}
	
	
	/**
	 * The StoragePolicy is normally configured in the app,
	 * but for Packages regarding UC such as retrieval and audit
	 * a valid StoragePolicy has to be configured.
	 * @author Jens Peters
	 */
	private static void instantiateStoragePolicy() {
		sp = new StoragePolicy();
		sp.setMinNodes(1);
		sp.setWorkingResource(CI_WORKING_RESOURCE);
		sp.setReplDestinations(CI_ARCHIVE_RESOURCE);
		sp.setAdminEmail("noreply");
		sp.setGridCacheAreaRootPath(localNode.getGridCacheAreaRootPath().toString());
		sp.setCommonStorageRescName(CI_ARCHIVE_RESOURCE);
	}
	
	
	/**
	 * Gets the contractor.
	 *
	 * @param contractorShortName the contractor short name
	 * @return null if no contractor for short name could be found
	 */
	public static User getContractor(Session session, String contractorShortName) {
	
		@SuppressWarnings("rawtypes")
		List list;	
		list = session.createQuery("from User where short_name=?1")
				.setParameter("1",contractorShortName).setReadOnly(true).list();
		
		if (list.isEmpty())
			return null;
	
		return (User) list.get(0);
	}
	
	public static Boolean setUserPublicMets(Boolean usePublicMets) {
		Session session = HibernateUtil.openSession();
		Transaction transaction = session.beginTransaction();
		Query query = session.createQuery("SELECT u FROM User u where username = '"+testContractor.getUsername()+"'");

		@SuppressWarnings("unchecked")
		List<User> users = query.list();
		User testUser = users.get(0);
		Boolean oldUsePublicMets = testUser.isUsePublicMets();
		testUser.setUsePublicMets(usePublicMets);
		session.save(testUser);
		transaction.commit();
		session.close();

		return oldUsePublicMets;
	}
	
	
	
	@BeforeClass
	public static void setUpAcceptanceTest() throws IOException{
		
		HibernateUtil.init("conf/hibernateCentralDB.cfg.xml");
		
		instantiateNode();
		if (localNode==null) throw new IllegalStateException("localNode could not be instantiated");
	
		System.out.println("localnode: "+localNode.getName());
		
		Properties properties = PropertiesUtils.read(new File("conf/config.properties"));
		
		instantiateGrid(properties);
		if (gridFacade==null) throw new IllegalStateException("gridFacade could not be instantiated");
		
		instantiateRepository(properties);
		if (repositoryFacade==null) throw new IllegalStateException("repositoryFacade could not be instantiated");
		
		instantiateMetadataIndex(properties);
		if (metadataIndex==null) throw new IllegalStateException("metadataIndex could not be instantiated");
	
		Session session = HibernateUtil.openSession();
		session.beginTransaction();
		
		if(properties.getProperty("regression.archiveStorage")!=null)
			CI_ARCHIVE_STORAGE=properties.getProperty("regression.archiveStorage"); 
		
		if(properties.getProperty("regression.TestCSN")!=null){
			String csn=properties.getProperty("regression.TestCSN");
			testContractor = getContractor(session, csn);
			if(testContractor==null)
				throw new IllegalStateException("regression.TestCSN: "+csn+" is not defined in DNS (No etry in DBMS) ");
			
		}else
			testContractor = getContractor(session, C.TEST_USER_SHORT_NAME);
		
		if(testContractor==null)
			throw new IllegalStateException("regression.TestCSN: "+testContractor+" is not defined in DNS (No etry in DBMS) ");
		
		preservationSystem = (PreservationSystem) session.get(PreservationSystem.class, 1);
		ciPathConsistencyTest();
		session.close();
		instantiateStoragePolicy();
		ath = new AcceptanceTestHelper(gridFacade,localNode,testContractor,sp,preservationSystem);
		if(properties.getProperty("localNode.logFolder")!=null) 
			ath.setLogPath(properties.getProperty("localNode.logFolder"));
		
		if(properties.getProperty("regression.fedoraUrlTemplateForDownload")!=null) 
			ath.setFedoraUrlTemplate(properties.getProperty("regression.fedoraUrlTemplateForDownload"));
		
		if(properties.getProperty("regression.maxWaitTime")!=null) {
			int maxTimeout=Integer.parseInt(properties.getProperty("regression.maxWaitTime"));
        	if(maxTimeout<1 || maxTimeout>30)
        		throw new IllegalStateException("Parameter max-waittime have to be between 1-30 minutes");
			ath.setTIMEOUT(maxTimeout*60*1000);
		}
		
		
		
//		new CommandLineConnector().runCmdSynchronously(new String[] {"src/main/bash/rebuildIndex.sh"});
		//If the previous test execution not cleaned 
			cleanStorage();
			clearDB();
			deactivateLicenseValidation();
	}
	
	public static void setLicenseInPreservationSystem(PreservationSystem preservationSystem ,int lflag){
			Session session = HibernateUtil.openSession();
			Transaction transaction = session.beginTransaction();
			PreservationSystem preservationSystemPersist = (PreservationSystem) session.get(PreservationSystem.class,preservationSystem.getId());
			//System.out.println("AcceptanceTest::setLicenseInPreservationSystem: "+preservationSystem.getLicenseValidationTestCSNFlag()+" "+lflag);
			preservationSystemPersist.setLicenseValidationTestCSNFlag(lflag);
			preservationSystem.setLicenseValidationTestCSNFlag(lflag);
			session.save(preservationSystemPersist);
			//session.update(preservationSystem);
			transaction.commit();
			session.close();
	}
	
	public static void ciPathConsistencyTest(){
		//check if testContractor is contained in the testContractors-Set
		AbstractApplicationContext context = new FileSystemXmlApplicationContext(CONF_BEANS_XML);
		Set<String> testContractors=(Set<String>) context.getBean("testContractors");
		
		if(!testContractors.contains(testContractor.getUsername())){
			testContractor=null;// Prevent removing wrong data in @AfterClass 
			throw new IllegalStateException("regression.TestCSN: "+testContractor+" is not in Set of testContractors: "+testContractors.toString());
		}
		
		
		//test path CI_ARCHIVE_STORAGE, it have to contain testContractor-CSN
		String[] tmpArr=CI_ARCHIVE_STORAGE.replace('/', ' ').trim().split(" ");
		if(!tmpArr[tmpArr.length-1].equals(testContractor.getUsername()))
			throw new IllegalStateException("Der Pfad: "+CI_ARCHIVE_STORAGE+" scheint nicht von dem User: "+testContractor+" zu sein");
		
	}
	synchronized static protected void activateLicenseValidation(){
		setLicenseInPreservationSystem(preservationSystem,C.PRESERVATIONSYS_LICENSE_VALIDATION_YES);
	}
	
	synchronized static protected void deactivateLicenseValidation(){
		setLicenseInPreservationSystem(preservationSystem,C.PRESERVATIONSYS_LICENSE_VALIDATION_NO);
	}
	
	synchronized static protected boolean activateMetsUrnForTestCSN(boolean useMetsURN){
		Session session = HibernateUtil.openSession();
		Transaction transaction = session.beginTransaction();
		Query query = session.createQuery("SELECT u FROM User u where username = '"+testContractor.getUsername()+"'");

		@SuppressWarnings("unchecked")
		List<User> users = query.list();
		User testUser = users.get(0);
		Boolean oldUseMetsUrn = testUser.isUseMetsUrn();
		testUser.setUseMetsUrn(useMetsURN);
		session.save(testUser);
		transaction.commit();
		session.close();

		return oldUseMetsUrn;
	
	}
	
	

	@AfterClass
	public static void tearDownAcceptanceTest() throws IOException{
//		new CommandLineConnector().runCmdSynchronously(new String[] {"src/main/bash/rebuildIndex.sh"});
		activateLicenseValidation();
		
		cleanStorage();
		//If the at tests are running on systems with important data, then hard reset of the db is not allowed
		if(System.getProperty(AcceptanceTestHelper.NO_DIRTY_CLEANUP_AFTER_EACH_TEST_PROPERTY)!=null){
			clearDB();
		}else{
			TESTHelper.dirtyClearDB();
		}
	}
	

	private static void cleanStorage(){
		System.out.println("cleanStorage()");
		FolderUtils.deleteQuietlySafe(Path.makeFile(localNode.getWorkAreaRootPath(),"work",testContractor.getUsername()));
		FolderUtils.deleteQuietlySafe(Path.makeFile(localNode.getWorkAreaRootPath(),"repl",testContractor.getUsername()));
		FolderUtils.deleteQuietlySafe(Path.makeFile(localNode.getIngestAreaRootPath(),testContractor.getUsername()));
		FolderUtils.deleteQuietlySafe(Path.makeFile(localNode.getGridCacheAreaRootPath(),WorkArea.AIP,testContractor.getUsername()));
		FolderUtils.deleteQuietlySafe(Path.makeFile(localNode.getWorkAreaRootPath(),"pips","institution",testContractor.getUsername()));
		FolderUtils.deleteQuietlySafe(Path.makeFile(localNode.getWorkAreaRootPath(),"pips","public",testContractor.getUsername()));
		FolderUtils.deleteQuietlySafe(Path.makeFile(localNode.getUserAreaRootPath(),testContractor.getUsername(),"outgoing"));
		
	
		IrodsCommandLineConnector icl = new IrodsCommandLineConnector();
		icl.remove("/"+localNode.getIdentifier() + "/work/"+testContractor.getUsername());
		icl.remove("/"+localNode.getIdentifier() + "/aip/"+testContractor.getUsername());
		icl.remove("/"+localNode.getIdentifier() + "/repl/"+testContractor.getUsername());
		icl.remove("/"+localNode.getIdentifier() + "/pips/institution/"+testContractor.getUsername());
		icl.remove("/"+localNode.getIdentifier() + "/pips/public/"+testContractor.getUsername());
		
		icl.mkCollection("/"+localNode.getIdentifier() + "/work/"+testContractor.getUsername());
		icl.mkCollection("/"+localNode.getIdentifier() + "/aip/"+testContractor.getUsername());
		icl.mkCollection("/"+localNode.getIdentifier() + "/repl/"+testContractor.getUsername());
		icl.mkCollection("/"+localNode.getIdentifier() + "/pips/institution/"+testContractor.getUsername());
		icl.mkCollection("/"+localNode.getIdentifier() + "/pips/public/"+testContractor.getUsername());
		
		/**distributedConversionAdapter.remove("work/TEST");
		distributedConversionAdapter.remove("aip/TEST");
		distributedConversionAdapter.remove("pips/institution/TEST");
		distributedConversionAdapter.remove("pips/public/TEST");
		
		distributedConversionAdapter.create("work/TEST");
		distributedConversionAdapter.create("aip/TEST");
		distributedConversionAdapter.create("pips/institution/TEST");
		distributedConversionAdapter.create("pips/public/TEST");
		*/
		Path.makeFile(localNode.getUserAreaRootPath(),testContractor.getUsername(),"outgoing").mkdirs();
		Path.makeFile(localNode.getGridCacheAreaRootPath(),"aip",testContractor.getUsername()).mkdirs();
		Path.makeFile(localNode.getIngestAreaRootPath(),testContractor.getUsername()).mkdirs();
		Path.make(localNode.getWorkAreaRootPath(),"work",testContractor.getUsername()).toFile().mkdirs();
		Path.make(localNode.getWorkAreaRootPath(),"repl",testContractor.getUsername()).toFile().mkdirs();
		Path.makeFile(localNode.getWorkAreaRootPath(),"pips","public",testContractor.getUsername()).mkdirs();
		Path.makeFile(localNode.getWorkAreaRootPath(),"pips","institution",testContractor.getUsername()).mkdirs();
	}
	
	private static void clearDB() {
		System.out.println("clearDB()");
		TESTHelper.clearDBOnlyTestUser(testContractor,localNode);
	}

	public static String getTestIndex() {
		return testIndex;
	}

	public static Node getLocalNode() {
		return localNode;
	}

	public static String getCI_ARCHIVE_STORAGE() {
		return CI_ARCHIVE_STORAGE;
	}
	
	
	public static boolean bagIsValid(String unpackedObjectPath) throws IOException{
		BagReader reader = new BagReader();
		
		Bag bagVer;
		BagVerifier sut = new BagVerifier();
		try {
			bagVer = reader.read(Paths.get(unpackedObjectPath));
			sut.isValid(bagVer, false);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} 
		return true;
	}
	
	
}
