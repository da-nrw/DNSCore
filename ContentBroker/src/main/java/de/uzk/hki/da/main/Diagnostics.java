/*
  DA-NRW Software Suite | ContentBroker
  Copyright (C) 2014 LVRInfoKom
  Landschaftsverband Rheinland

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

package de.uzk.hki.da.main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import de.uzk.hki.da.core.C;
import de.uzk.hki.da.format.ConfigurableFileFormatFacade;
import de.uzk.hki.da.format.ConnectionException;
import de.uzk.hki.da.format.FFConstants;
import de.uzk.hki.da.format.FidoFormatScanService;
import de.uzk.hki.da.format.FileWithFileFormat;
import de.uzk.hki.da.format.JhoveMetadataExtractor;
import de.uzk.hki.da.format.SimpleFileWithFileFormat;
import de.uzk.hki.da.grid.IrodsGridFacade;
import de.uzk.hki.da.grid.IrodsSystemConnector;
//import de.uzk.hki.da.model.Node;
import de.uzk.hki.da.model.StoragePolicy;
import de.uzk.hki.da.model.SubformatIdentificationStrategyPuidMapping;
import de.uzk.hki.da.model.WorkArea;
import de.uzk.hki.da.repository.Fedora3RepositoryFacade;
import de.uzk.hki.da.repository.RepositoryException;
import de.uzk.hki.da.service.HibernateUtil;
import de.uzk.hki.da.util.Path;
import de.uzk.hki.da.util.RelativePath;
import de.uzk.hki.da.utils.CommandLineConnector;
import de.uzk.hki.da.utils.PropertiesUtils;
import de.uzk.hki.da.utils.StringUtilities;

/**
 * Checks basic connectivity of the application. 
 * 
 * Expects a config.properties file at conf/config.properties
 * Expects a testpackage at conf/testpackage.tgz
 * 
 * @author Daniel M. de Oliveira
 */
public class Diagnostics {

	private static final String TIFF_TESTFILE = "healthCheck.tif";
	private static final String TIFF_TESTFILE_PATH = "conf/healthCheck.tif";
	
	private static final String BEAN_NAME_IRODS_ZONE = "irods.zone";

	private static final String BEAN_NAME_IMPLEMENTATION_GRID = "cb.implementation.grid";

	private static final Logger logger = LoggerFactory.getLogger(Diagnostics.class);
	
	private static final String WARN = "WARN: ";
	private static final String INFO = "INFO: ";
	private static final String ERROR = "ERROR: ";
	private static final String OK = "OK";
	private static final String FAIL = "FAIL";

	private static final String TEST_TGZ = "test.tgz";
	private static final File DIAGNOSTICS_RETRIEVAL_FILE = Path.makeFile("tmp","diagnostics.tgz");
	
	private static final String BEANS_DIAGNOSTICS_IRODS = "classpath*:META-INF/beans-diagnostics.irods.xml";
	private static final String BEANS_DIAGNOSTICS_FEDORA = "classpath*:META-INF/beans-diagnostics.fedora.xml";
	private static final String BEANS_DIAGNOSTICS_ELASTICSEARCH = "classpath*:META-INF/beans-diagnostics.elasticsearch.xml";
	
	private static final String BEAN_NAME_IRODS_GRID_FACADE = "irodsGridFacade";
	private static final String BEAN_NAME_IRODS_SYSTEM_CONNECTOR = "irodsSystemConnector";
	private static final String BEAN_NAME_FEDORA_REPOSITORY_FACADE = "fedoraRepositoryFacade";
//	private static final String BEAN_NAME_METADATA_INDEX_FACADE = "esMetadataIndex";
	
	private static final String PROP_GRID_CACHE_AREA_ROOT_PATH = "localNode.gridCacheAreaRootPath";
	private static final String PROP_USER_AREA_ROOT_PATH = "localNode.userAreaRootPath";
	private static final String PROP_INGEST_AREA_ROOT_PATH = "localNode.ingestAreaRootPath";
	private static final String PROP_WORK_AREA_ROOT_PATH = "localNode.workAreaRootPath";
	private static final String PROP_REPL_DESTINATIONS = "localNode.replDestinations";
	private static final String PROP_IRODS_ZONE = BEAN_NAME_IRODS_ZONE;

	private static final String MSG_USER_AREA_NOT_EXISTS = "ERROR (path configured by localNode.userAreaRootPath does not exist)";
	private static final String MSG_GRID_CACHE_AREA_NOT_EXISTS = "ERROR (path configured by localNode.gridCacheAreaRootPath does not exist)";
	private static final String MSG_WORK_AREA_NOT_EXISTS = "ERROR (path configured by localNode.workAreaRootPath does not exist)";
	private static final String MSG_INGEST_AREA_NOT_EXISTS = "ERROR (path configured by localNode.ingestAreaRootPath does not exist)";

	
	
	/**
	 * @return errorCount
	 */
	public static Integer run() {
	
		System.out.println("::::::::::::::::::::::::::::::::::");
		System.out.println("::: Smoke test the application :::");
		System.out.println("::::::::::::::::::::::::::::::::::");
		int errorCount = 0;
		
		Properties properties = null;
		try {
			properties = PropertiesUtils.read(new File(C.CONFIG_PROPS));
		} catch (IOException e) {
			System.out.println(WARN+"error while reading "+C.CONFIG_PROPS);
			return 1;
		}
		
		System.out.print(INFO+"CHECKING - DATABASE CONNECTIVITY ... ");
		try {
			HibernateUtil.init(C.HIBERNATE_CFG.getAbsolutePath());
			Session session = HibernateUtil.openSession();
			session.createSQLQuery("select * from users;").list();
			session.close();
		} catch (Exception e) {
			System.out.println(ERROR+"CANNOT CONNECT TO DATABASE ");
			e.printStackTrace();
			return 1;
		}
		System.out.println(OK);
		
		
		errorCount+=checkPaths(properties);
		errorCount+=checkIrods(properties);
		errorCount+=checkFormatFacade(properties);
		errorCount+=checkFedora(properties);
		errorCount+=checkElasticsearch(properties);
		
		
		if (errorCount==0) System.out.println("There were no errors.");
		else System.out.println("There where "+errorCount+" errors.");
		return errorCount;
	}

	
	



	private static int checkFedora(Properties properties) {
		
		if (((String)properties.get("fedora.user"))==null||((String)properties.get("fedora.user")).isEmpty()){
			System.out.println(INFO+"WILL NOT CHECK FEDORA ! ! ! fedora.user is empty.");
			return 0;
		}
		
		
		int errorCount=0;
		AbstractApplicationContext context =
				new ClassPathXmlApplicationContext(BEANS_DIAGNOSTICS_FEDORA);
		Fedora3RepositoryFacade fedora = (Fedora3RepositoryFacade) context.getBean(BEAN_NAME_FEDORA_REPOSITORY_FACADE);		
		context.close();
		
		System.out.print(INFO+"CHECKING - FEDORA CONNECTIVITY .... ");
		try {
			fedora.purgeObjectIfExists("abc", "coll1");
			fedora.createObject("abc", "coll1", "TEST");
			fedora.purgeObjectIfExists("abc", "coll1");
			System.out.println(OK);
		} catch (RepositoryException e) {
			errorCount++;
			System.out.println(WARN+"connection to fedora cannot be established");
		}
		
		return errorCount;
	}
	
	
	private static int checkElasticsearch(Properties properties) {
		
		if (((String)properties.get("elasticsearch.index"))==null){
			System.out.println(INFO+"WILL NOT CHECK ELASTICSEARCH ! ! ! elasticsearch.index is empty.");
			return 0;
		}
		
		
		int errorCount=0;
		AbstractApplicationContext context =
				new ClassPathXmlApplicationContext(BEANS_DIAGNOSTICS_ELASTICSEARCH);
//		ElasticsearchMetadataIndex es = (ElasticsearchMetadataIndex) context.getBean(BEAN_NAME_METADATA_INDEX_FACADE);		
		context.close();
		
		System.out.print(INFO+"CHECKING - ELASTICSEARCH CONNECTIVITY .... ");
//		try {
//		
//		String getIndexedMetadata(String indexName, String objectId);
		
//			String portal = properties.getProperty("elasticsearch.index");
//			
//			Map<String,Object> data;
//			data = new HashMap<String,Object>();
//			data.put("@title","The Godfather");
//			data.put("@director","Francis Ford Coppola");
//			data.put("@year","1972");
//			
//			es.indexMetadata(portal, "test_object_1", "test_collection", data);
//			System.out.println(OK);
//		} catch (MetadataIndexException e) {
//			errorCount++;
//			System.out.println(WARN+"connection to elasticsearch cannot be established: "+e);
//		}
		
		return errorCount;
	}
	
	
	private static int checkFormatFacade(Properties properties) {
		
		
		if (!StringUtilities.isNotSet(((String)properties.get("cb.implementation.fileFormatFacade")))){
			System.out.println(INFO+"WILL NOT CHECK FILEFORMATFACADE ! ! ! fileFormatFacade is set to \"fakeFileFormatFacade\"");
			return 0;
		}

		int errorCount = 0;
		ConfigurableFileFormatFacade sfff = new ConfigurableFileFormatFacade();
		sfff.setFormatScanService(new FidoFormatScanService());
		JhoveMetadataExtractor meta = new JhoveMetadataExtractor();
		meta.setCli(new CommandLineConnector());
		sfff.setMetadataExtractor(meta);
		
		System.out.println(INFO+"CHECKING - ConfigurableFileFormatFacade.connectivityCheck() ... ");
		if (!standardFileFormatFacadeHealthSubformatsPassedCheckPassed(sfff))
			errorCount+=1;
		
		System.out.print(INFO+"CHECKING - ConfigurableFileFormatFacade.identify() ... ");
		if (!standardFileFormatFacadeFidoWorkingProperly(sfff)) {
			errorCount+=1;
			System.out.println(FAIL);
		}
		else
			System.out.println(OK);
		
		System.out.print(INFO+"CHECKING - ConfigurableFileFormatFacade.extract() ... ");
		if (!standardFileFormatFacadeJhoveWorkingProperly(sfff)) {
			errorCount+=1;
			System.out.println(FAIL);
		}
		else
			System.out.println(OK);

		return errorCount;
	}
	
	private static boolean standardFileFormatFacadeHealthSubformatsPassedCheckPassed(ConfigurableFileFormatFacade sfff) {
		
		Session session = HibernateUtil.openSession();
		session.beginTransaction();

		for (SubformatIdentificationStrategyPuidMapping sfiP:getSecondStageScanPolicies(session)) {
			sfff.registerSubformatIdentificationStrategyPuidMapping(sfiP.getSubformatIdentificationStrategyName(),sfiP.getFormatPuid());
		}
		session.close();
		if (!sfff.connectivityCheck()) return false;
		return true;
	}
	
	private static boolean standardFileFormatFacadeJhoveWorkingProperly(ConfigurableFileFormatFacade sfff) {
		String TIFF_TESTFILE_TEMPPATH="/tmp/abc";
		
//		FileFormatFacade jhove = new ConfigurableFileFormatFacade();
		try {
			sfff.extract(new File(TIFF_TESTFILE_PATH), new File(TIFF_TESTFILE_TEMPPATH));
		} catch (ConnectionException e) {
			return false;
		} catch (Exception e) {
			return false;
		}
		return true;
	}
	
	private static boolean standardFileFormatFacadeFidoWorkingProperly(ConfigurableFileFormatFacade sfff) {
		
		List<FileWithFileFormat> files = new ArrayList<FileWithFileFormat>();
		FileWithFileFormat ffff = new SimpleFileWithFileFormat(new File(TIFF_TESTFILE));
		files.add(ffff);
		
		try {
			sfff.identify(new RelativePath("conf"),files);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if (!files.get(0).getFormatPUID().equals(FFConstants.FMT_353))
			return false;
		return true;
	}
	

	/**
	 * TODO remove Duplication with actionfactory.
	 * Gets the second stage scan policies.
	 *
	 * @return the second stage scan policies
	 */
	private static List<SubformatIdentificationStrategyPuidMapping> getSecondStageScanPolicies(Session session) {
		@SuppressWarnings("unchecked")
		List<SubformatIdentificationStrategyPuidMapping> l = session
				.createQuery("from SubformatIdentificationStrategyPuidMapping").list();

		return l;
	}
	
	
	private static int checkIrods(Properties properties){
		
		if (((String)properties.get(BEAN_NAME_IRODS_ZONE))==null||((String)properties.get(BEAN_NAME_IRODS_ZONE)).isEmpty()){
			System.out.println("WARN: WILL NOT CHECK IRODS ! ! ! "+BEAN_NAME_IRODS_ZONE+" is empty");
			return 0;
		}
		
		System.out.print(INFO+"CHECKING - IRODS CONNECTION .... ");

		AbstractApplicationContext context =
				new ClassPathXmlApplicationContext(BEANS_DIAGNOSTICS_IRODS);
		IrodsSystemConnector irods = (IrodsSystemConnector) context.getBean(BEAN_NAME_IRODS_SYSTEM_CONNECTOR);
		
		int errorCount = 0;
		if (!irods.connect()){
			System.out.println(ERROR+"COULD NOT CONNECT TO IRODS");
			errorCount++;
			context.close();
			return errorCount;
		}
		
		irods.removeFileAndEatException(Path.make(properties.getProperty(PROP_IRODS_ZONE),WorkArea.AIP,C.TEST_USER_SHORT_NAME,TEST_TGZ).toString());
		irods.logoff();
		
		
		// Check iRODS grid facade
		if (((String)properties.get(BEAN_NAME_IMPLEMENTATION_GRID))==null||((String)properties.get(BEAN_NAME_IMPLEMENTATION_GRID)).isEmpty()){
			System.out.println(WARN+"CONNECTION ESTABLISHED. CHECKING FOR GRID FACADE SKIPPED (REASON: "+BEAN_NAME_IMPLEMENTATION_GRID+" is empty. This is ok for presentation only nodes.)");
			context.close();
			return errorCount;
		}

		System.out.println(OK);
		

		IrodsGridFacade irodsGridFacade = (IrodsGridFacade) context.getBean(BEAN_NAME_IRODS_GRID_FACADE);
		
		
//		Node node = (Node) context.getBean(C.LOCAL_NODE_BEAN_NAME);
		StoragePolicy sp = new StoragePolicy();
		sp.setMinNodes(1);
		sp.setGridCacheAreaRootPath((String)properties.get(PROP_GRID_CACHE_AREA_ROOT_PATH));
	
		sp.setReplDestinations((String)properties.get(PROP_REPL_DESTINATIONS));
		context.close();
		
		
		System.out.print(INFO+"CHECKING - GridFacade.put() .... ");
		
		SimpleDateFormat df = new SimpleDateFormat( "yyyy-MM-dd+HH-mm-ss" );
		String testPkgName="smoke_test."+df.format(new Date())+".tgz";
		
		try {
			
			boolean returnValue = irodsGridFacade.put( C.BASIC_TEST_PACKAGE, 
					new RelativePath(C.TEST_USER_SHORT_NAME,testPkgName).toString(), sp);
			if (returnValue==false){
				errorCount++;
				System.out.println(WARN+"put returned false.");
			}else
				System.out.println(OK);
			
		} catch (Exception e) {
			errorCount++;
			System.out.println(ERROR+"cannot put file via irodsGridFacade");
			e.printStackTrace();
		}
		System.out.print(INFO+"CHECKING - GridFacade.isValid() ... ");
		if (!irodsGridFacade.isValid(new RelativePath(C.TEST_USER_SHORT_NAME,testPkgName).toString())) {
				errorCount++;
				System.out.println(WARN+" GridFacade.isValid() returned false.");
		}else
				System.out.println(OK);
	
		
		System.out.print(INFO+"CHECKING - GridFacade.get() .... ");
		if (DIAGNOSTICS_RETRIEVAL_FILE.exists()) DIAGNOSTICS_RETRIEVAL_FILE.delete();
		try {
			irodsGridFacade.get(DIAGNOSTICS_RETRIEVAL_FILE, new RelativePath(C.TEST_USER_SHORT_NAME,testPkgName).toString());
			System.out.println(OK);
		} catch (Exception e) {
			errorCount++;
			System.out.println(ERROR+"cannot retrieve file via irodsGridFacade");
			e.printStackTrace();
		}
		if (DIAGNOSTICS_RETRIEVAL_FILE.exists()) DIAGNOSTICS_RETRIEVAL_FILE.delete();
		
		return errorCount;
	}
	
	
	
	private static int checkPaths(Properties properties){
		
		int errorCount = 0;
		
		System.out.print(INFO+"CHECKING - LOCAL NODE PATHS ... ");
		
		if ((properties.getProperty(PROP_USER_AREA_ROOT_PATH)==null) ||
			((String)properties.getProperty(PROP_USER_AREA_ROOT_PATH)).isEmpty())
			
			logger.warn(INFO+PROP_USER_AREA_ROOT_PATH+" is empty. will not check for path");
		else {
			if (!new File(properties.getProperty(PROP_USER_AREA_ROOT_PATH)).exists()){
				System.out.println(MSG_USER_AREA_NOT_EXISTS);
				errorCount++;
			}
		}
		
		if ((properties.getProperty(PROP_INGEST_AREA_ROOT_PATH)==null) ||
				((String)properties.getProperty(PROP_INGEST_AREA_ROOT_PATH)).isEmpty())
				
				logger.warn(INFO+PROP_INGEST_AREA_ROOT_PATH+" is empty. will not check for path");
		else {
			if (!new File(properties.getProperty(PROP_INGEST_AREA_ROOT_PATH)).exists()){
				System.out.println(MSG_INGEST_AREA_NOT_EXISTS);
				errorCount++;
			}
		}
		
		if ((properties.getProperty(PROP_GRID_CACHE_AREA_ROOT_PATH)==null) ||
				((String)properties.getProperty(PROP_GRID_CACHE_AREA_ROOT_PATH)).isEmpty())
		
			logger.warn(INFO+PROP_INGEST_AREA_ROOT_PATH+" is empty. will not check for path");
		else{
			if (!new File(properties.getProperty(PROP_GRID_CACHE_AREA_ROOT_PATH)).exists()) {
				System.out.println(MSG_GRID_CACHE_AREA_NOT_EXISTS);
				errorCount++;
			}
		}
		
		if (!new File(properties.getProperty(PROP_WORK_AREA_ROOT_PATH)).exists()) {
			System.out.println(MSG_WORK_AREA_NOT_EXISTS);
			errorCount++;
		}
		if (errorCount==0) System.out.println(OK);
		
		return errorCount;
	}

}
