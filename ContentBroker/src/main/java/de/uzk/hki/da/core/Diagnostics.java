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
import de.uzk.hki.da.grid.IrodsGridFacade;
import de.uzk.hki.da.grid.IrodsSystemConnector;
import de.uzk.hki.da.model.Node;
import de.uzk.hki.da.model.StoragePolicy;
import de.uzk.hki.da.utils.C;
import de.uzk.hki.da.utils.Path;
import de.uzk.hki.da.utils.RelativePath;
import de.uzk.hki.da.utils.Utilities;

/**
 * Allows checking if the application will run prior to execution of the ContentBroker in "normal" execution mode.
 * Expects a config.properties file at conf/config.properties
 * Expects a testpackage at conf/testpackage.tgz
 * 
 * @author Daniel M. de Oliveira
 */
public class Diagnostics {

	private static final Logger logger = LoggerFactory.getLogger(Diagnostics.class);
	
	private static final String TEST_TGZ = "test.tgz";
	private static final File DIAGNOSTICS_RETRIEVAL_FILE = Path.makeFile("tmp","diagnostics.tgz");
	private static final String DIAGNOSTICS_IRODS = "classpath*:META-INF/beans-diagnostics.irods.xml";
	private static final String DIAGNOSTICS_IDENTIFIER = "classpath*:META-INF/beans-diagnostics.identifier.xml";
	private static final String WARN = "WARN: ";
	private static final String MSG_GRID_CACHE_AREA_NOT_EXISTS = WARN+"path localNode.gridCacheAreaRootPath points to not exists";
	private static final String MSG_WORK_AREA_NOT_EXISTS = WARN+"path localNode.workAreaRootPath not exists";
	private static final String MSG_INGEST_AREA_NOT_EXISTS = WARN+"path localNode.ingestAreaRootPath points to not exists";
	private static final String MSG_USER_AREA_NOT_EXISTS = WARN+"path localNode.userAreaRootPath points to not exists";
	private static final String GRID_CACHE_AREA_ROOT_PATH = "localNode.gridCacheAreaRootPath";
	
	private static final String IRODS_GRID_FACADE_BEAN_NAME = "irodsGridFacade";
	private static final String IRODS_SYSTEM_CONNECTOR_BEAN_NAME = "irodsSystemConnector";
	private static final String BEAN_NAME_PRONOM_FORMAT_IDENTIFIER = "pronomFormatIdentifier";
	private static final String BEAN_NAME_VIDEO_CODEC_FORMAT_IDENTIFIER = "videoCodecFormatIdentifier";
	
	private static final String PROP_USER_AREA_ROOT_PATH = "localNode.userAreaRootPath";
	private static final String PROP_INGEST_AREA_ROOT_PATH = "localNode.ingestAreaRootPath";
	private static final String PROP_WORK_AREA_ROOT_PATH = "localNode.workAreaRootPath";
	private static final String PROP_REPL_DESTINATIONS = "localNode.replDestinations";
	private static final String PROP_IRODS_ZONE = "irods.zone";


	
	
	
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
		
		
		errorCount+=checkPaths(properties);
		errorCount+=checkIrods(properties);
		errorCount+=checkFormatIdentifiers();
		
		
		if (errorCount==0) System.out.println("There were no errors.");
		else System.out.println("There where "+errorCount+" errors.");
		return errorCount;
	}

	
	
	private static int checkFormatIdentifiers() {

		int errorCount=0;
		AbstractApplicationContext context =
				new ClassPathXmlApplicationContext(DIAGNOSTICS_IDENTIFIER);
		
		CLIFormatIdentifier pronomFormatIdentifier = (CLIFormatIdentifier) context.getBean(BEAN_NAME_PRONOM_FORMAT_IDENTIFIER);
		if (!pronomFormatIdentifier.healthCheck()){
			errorCount++;
			System.out.println(WARN+"pronomFormatIdentifier health check not passed.");
		}
		
		CLIFormatIdentifier videoCodecFormatIdentifier = (CLIFormatIdentifier) context.getBean(BEAN_NAME_VIDEO_CODEC_FORMAT_IDENTIFIER);
		if (!videoCodecFormatIdentifier.healthCheck()){
			errorCount++;
			System.out.println(WARN+"videoFormatIdentifier health check not passed.");
		}
		
		context.close();
		return errorCount;
	}


	private static int checkIrods(Properties properties){
		int errorCount = 0;
		
		AbstractApplicationContext context =
				new ClassPathXmlApplicationContext(DIAGNOSTICS_IRODS);
		
		Node node = (Node) context.getBean(C.LOCAL_NODE_BEAN_NAME);
		StoragePolicy sp = new StoragePolicy(node);
		sp.setMinNodes(1);
		List<String> replDestinations = new ArrayList<String>();
		replDestinations.add((String)properties.get(PROP_REPL_DESTINATIONS));
		
		sp.setDestinations(replDestinations);
		
		
		
		IrodsSystemConnector irods = (IrodsSystemConnector) context.getBean(IRODS_SYSTEM_CONNECTOR_BEAN_NAME);
		try{
			irods.connect();
			irods.removeFileAndEatException(Path.make((String) properties.getProperty(PROP_IRODS_ZONE),C.AIP,C.TEST,TEST_TGZ).toString());
			irods.logoff();
		}
		catch(Exception e){
			errorCount++;
			System.out.println(WARN+"cannot connect to irods via irodsSystemConnector and delete test file. "+e.getMessage());
		}
		
		
		
		IrodsGridFacade irodsGridFacade = (IrodsGridFacade) context.getBean(IRODS_GRID_FACADE_BEAN_NAME);
		try {
			irodsGridFacade.put( C.BASIC_TEST_PACKAGE, new RelativePath(C.TEST,TEST_TGZ).toString(), sp);
		} catch (Exception e) {
			errorCount++;
			System.out.println(WARN+"cannot put file via irodsGridFacade");
		}
		
		if (DIAGNOSTICS_RETRIEVAL_FILE.exists()) DIAGNOSTICS_RETRIEVAL_FILE.delete();
		try {
			irodsGridFacade.get(DIAGNOSTICS_RETRIEVAL_FILE, new RelativePath(C.TEST,TEST_TGZ).toString());
		} catch (Exception e) {
			errorCount++;
			System.out.println(WARN+"connot retrieve file via irodsGridFacade");
		}
		if (DIAGNOSTICS_RETRIEVAL_FILE.exists()) DIAGNOSTICS_RETRIEVAL_FILE.delete();
		
		context.close();
		return errorCount;
	}
	
	
	
	private static int checkPaths(Properties properties){
		
		int errorCount = 0;
		
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
		if (!new File(properties.getProperty(GRID_CACHE_AREA_ROOT_PATH)).exists()) {
			System.out.println(MSG_GRID_CACHE_AREA_NOT_EXISTS);
			errorCount++;
		}
		
		return errorCount;
	}

}
