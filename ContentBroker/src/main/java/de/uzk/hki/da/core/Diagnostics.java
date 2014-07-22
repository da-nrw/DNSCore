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

package de.uzk.hki.da.core;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import de.uzk.hki.da.format.CLIFormatIdentifier;
import de.uzk.hki.da.format.JhoveScanService;
import de.uzk.hki.da.grid.IrodsGridFacade;
import de.uzk.hki.da.grid.IrodsSystemConnector;
import de.uzk.hki.da.model.DAFile;
import de.uzk.hki.da.model.Node;
import de.uzk.hki.da.model.StoragePolicy;
import de.uzk.hki.da.repository.Fedora3RepositoryFacade;
import de.uzk.hki.da.repository.RepositoryException;
import de.uzk.hki.da.utils.C;
import de.uzk.hki.da.utils.Path;
import de.uzk.hki.da.utils.RelativePath;
import de.uzk.hki.da.utils.Utilities;

/**
 * Checks basic connectivity of the application. 
 * 
 * Expects a config.properties file at conf/config.properties
 * Expects a testpackage at conf/testpackage.tgz
 * 
 * @author Daniel M. de Oliveira
 */
public class Diagnostics {

	private static final Logger logger = LoggerFactory.getLogger(Diagnostics.class);
	
	private static final String WARN = "WARN: ";

	private static final String TEST_TGZ = "test.tgz";
	private static final File DIAGNOSTICS_RETRIEVAL_FILE = Path.makeFile("tmp","diagnostics.tgz");
	
	private static final String BEANS_DIAGNOSTICS_IRODS = "classpath*:META-INF/beans-diagnostics.irods.xml";
	private static final String BEANS_DIAGNOSTICS_IDENTIFIER = "classpath*:META-INF/beans-diagnostics.identifier.xml";
	private static final String BEANS_DIAGNOSTICS_FEDORA = "classpath*:META-INF/beans-diagnostics.fedora.xml";
	
	private static final String BEAN_NAME_IRODS_GRID_FACADE = "irodsGridFacade";
	private static final String BEAN_NAME_IRODS_SYSTEM_CONNECTOR = "irodsSystemConnector";
	private static final String BEAN_NAME_PRONOM_FORMAT_IDENTIFIER = "pronomFormatIdentifier";
	private static final String BEAN_NAME_VIDEO_CODEC_FORMAT_IDENTIFIER = "videoCodecFormatIdentifier";
	private static final String BEAN_NAME_FEDORA_REPOSITORY_FACADE = "fedoraRepositoryFacade";
	
	private static final String PROP_GRID_CACHE_AREA_ROOT_PATH = "localNode.gridCacheAreaRootPath";
	private static final String PROP_USER_AREA_ROOT_PATH = "localNode.userAreaRootPath";
	private static final String PROP_INGEST_AREA_ROOT_PATH = "localNode.ingestAreaRootPath";
	private static final String PROP_WORK_AREA_ROOT_PATH = "localNode.workAreaRootPath";
	private static final String PROP_REPL_DESTINATIONS = "localNode.replDestinations";
	private static final String PROP_IRODS_ZONE = "irods.zone";

	private static final String MSG_USER_AREA_NOT_EXISTS = WARN+"path localNode.userAreaRootPath points to not exists";
	private static final String MSG_GRID_CACHE_AREA_NOT_EXISTS = WARN+"path localNode.gridCacheAreaRootPath points to not exists";
	private static final String MSG_WORK_AREA_NOT_EXISTS = WARN+"path localNode.workAreaRootPath not exists";
	private static final String MSG_INGEST_AREA_NOT_EXISTS = WARN+"path localNode.ingestAreaRootPath points to not exists";

	
	
	
	public static Integer run() {
	
		System.out.println("Smoke test the application");
		int errorCount = 0;
		
		Properties properties = null;
		try {
			properties = Utilities.read(C.CONFIG_PROPS);
		} catch (IOException e) {
			System.out.println(WARN+"error while reading "+C.CONFIG_PROPS);
			return 1;
		}
		
		logger.info("Setting up HibernateUtil ..");
		try {
			HibernateUtil.init(C.HIBERNATE_CFG.getAbsolutePath());
		} catch (Exception e) {
			System.out.println("Cannot instantiate database!");
			return 1;
		}
		
		
		errorCount+=checkJhove();
		errorCount+=checkPaths(properties);
		errorCount+=checkIrods(properties);
		errorCount+=checkFormatIdentifiers();
		errorCount+=checkFedora(properties);
		
		
		if (errorCount==0) System.out.println("There were no errors.");
		else System.out.println("There where "+errorCount+" errors.");
		return errorCount;
	}

	
	
	private static int checkJhove() {
		
		System.out.print("CHECKING JHOVE: ");
		JhoveScanService jhove = new JhoveScanService();
		jhove.setJhoveFolder("jhove");
		try {
			jhove.extract(new File("conf/healthCheck.tif"), 1);
			System.out.println("OK");
		} catch (IOException e) {
			System.out.println(WARN+" jhove scan service doesnt work");
			return 1;
		}
		return 0;
	}



	private static int checkFedora(Properties properties) {
		
		if (((String)properties.get("fedora.user"))==null||((String)properties.get("fedora.user")).isEmpty()){
			System.out.println("WARN: WILL NOT CHECK FEDORA ! ! ! fedora.user is empty");
			return 0;
		}
		
		
		int errorCount=0;
		AbstractApplicationContext context =
				new ClassPathXmlApplicationContext(BEANS_DIAGNOSTICS_FEDORA);
		Fedora3RepositoryFacade fedora = (Fedora3RepositoryFacade) context.getBean(BEAN_NAME_FEDORA_REPOSITORY_FACADE);		
		context.close();
		
		System.out.print("CHECKING FEDORA CONNECTIVITY: ");
		try {
			fedora.purgeObjectIfExists("abc", "coll1");
			fedora.createObject("abc", "coll1", "TEST");
			fedora.purgeObjectIfExists("abc", "coll1");
			System.out.println("OK");
		} catch (RepositoryException e) {
			errorCount++;
			System.out.println(WARN+"connection to fedora cannot be established");
		}
		
		return errorCount;
	}
	
	
	
	
	private static int checkFormatIdentifiers() {
		
		int errorCount=0;
		AbstractApplicationContext context =
				new ClassPathXmlApplicationContext(BEANS_DIAGNOSTICS_IDENTIFIER);
		CLIFormatIdentifier pronomFormatIdentifier = (CLIFormatIdentifier) context.getBean(BEAN_NAME_PRONOM_FORMAT_IDENTIFIER);
		CLIFormatIdentifier videoCodecFormatIdentifier = (CLIFormatIdentifier) context.getBean(BEAN_NAME_VIDEO_CODEC_FORMAT_IDENTIFIER);
		context.close();

		
		System.out.print("CHECKING PRONOM FORMAT IDENTIFIER: ");
		if (!pronomFormatIdentifier.healthCheck()){
			errorCount++;
			System.out.println(WARN+"pronomFormatIdentifier health check not passed.");
		}else System.out.println("OK");

		
		System.out.print("CHECKING VIDEO FORMAT IDENTIFIER: ");
		if (!videoCodecFormatIdentifier.healthCheck()){
			errorCount++;
			System.out.println(WARN+"videoFormatIdentifier health check not passed.");
		}else System.out.println("OK");
		
		return errorCount;
	}


	private static int checkIrods(Properties properties){
		
		if (((String)properties.get("irods.zone"))==null||((String)properties.get("irods.zone")).isEmpty()){
			System.out.println("WARN: WILL NOT CHECK IRODS ! ! ! irods.zone is empty");
			return 0;
		}
		
		
		
		
		int errorCount = 0;
		
		AbstractApplicationContext context =
				new ClassPathXmlApplicationContext(BEANS_DIAGNOSTICS_IRODS);
		IrodsSystemConnector irods = (IrodsSystemConnector) context.getBean(BEAN_NAME_IRODS_SYSTEM_CONNECTOR);
		IrodsGridFacade irodsGridFacade = (IrodsGridFacade) context.getBean(BEAN_NAME_IRODS_GRID_FACADE);
		Node node = (Node) context.getBean(C.LOCAL_NODE_BEAN_NAME);
		context.close();
		
		StoragePolicy sp = new StoragePolicy(node);
		sp.setMinNodes(1);
		List<String> replDestinations = new ArrayList<String>();
		replDestinations.add((String)properties.get(PROP_REPL_DESTINATIONS));
		
		sp.setDestinations(replDestinations);
		
		System.out.print("CHECKING IRODS CONNECTION: ");
		try{
			irods.connect();
			irods.removeFileAndEatException(Path.make((String) properties.getProperty(PROP_IRODS_ZONE),C.AIP,C.TEST_USER_SHORT_NAME,TEST_TGZ).toString());
			irods.logoff();
			System.out.println("OK");
		}
		catch(Exception e){
			errorCount++;
			System.out.println(WARN+"cannot connect to irods via irodsSystemConnector and delete test file. "+e.getMessage());
		}
		
		System.out.print("CHECKING GRID FACADE PUT: ");
		try {
			irodsGridFacade.put( C.BASIC_TEST_PACKAGE, new RelativePath(C.TEST_USER_SHORT_NAME,TEST_TGZ).toString(), sp);
			System.out.println("OK");
		} catch (Exception e) {
			errorCount++;
			System.out.println(WARN+"cannot put file via irodsGridFacade");
		}
		
		System.out.print("CHECKING GRID FACADE GET: ");
		if (DIAGNOSTICS_RETRIEVAL_FILE.exists()) DIAGNOSTICS_RETRIEVAL_FILE.delete();
		try {
			irodsGridFacade.get(DIAGNOSTICS_RETRIEVAL_FILE, new RelativePath(C.TEST_USER_SHORT_NAME,TEST_TGZ).toString());
			System.out.println("OK");
		} catch (Exception e) {
			errorCount++;
			System.out.println(WARN+"connot retrieve file via irodsGridFacade");
		}
		if (DIAGNOSTICS_RETRIEVAL_FILE.exists()) DIAGNOSTICS_RETRIEVAL_FILE.delete();
		
		return errorCount;
	}
	
	
	
	private static int checkPaths(Properties properties){
		
		int errorCount = 0;
		
		System.out.print("CHECKING LOCAL NODE PATHS: ");
		if (!new File(properties.getProperty(PROP_USER_AREA_ROOT_PATH)).exists()){
			System.out.println(MSG_USER_AREA_NOT_EXISTS);
			errorCount++;
		}
		if (!new File(properties.getProperty(PROP_INGEST_AREA_ROOT_PATH)).exists()) {
			System.out.println(MSG_INGEST_AREA_NOT_EXISTS);
			errorCount++;
		}
		if (!new File(properties.getProperty(PROP_WORK_AREA_ROOT_PATH)).exists()) {
			System.out.println(MSG_WORK_AREA_NOT_EXISTS);
			errorCount++;
		}
		if (!new File(properties.getProperty(PROP_GRID_CACHE_AREA_ROOT_PATH)).exists()) {
			System.out.println(MSG_GRID_CACHE_AREA_NOT_EXISTS);
			errorCount++;
		}
		System.out.println("OK");
		
		return errorCount;
	}

}
