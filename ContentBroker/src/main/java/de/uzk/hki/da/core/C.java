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

import org.jdom.Namespace;

import de.uzk.hki.da.ff.FFConstants;

/**
 * @author Daniel M. de Oliveira & the DA-NRW team
 */
public class C {

	// file extensions
	public static final String FILE_EXTENSION_ZIP = "zip";
	public static final String FILE_EXTENSION_TGZ = "tgz";
	public static final String FILE_EXTENSION_XML = ".xml";
	public static final String FILE_EXTENSION_JPG = ".jpg";
	public static final String FILE_EXTENSION_TAR = ".tar";

	
	
	public static final String MIMETYPE_IMAGE_JPEG = "image/jpeg";

	
	
	// Presentation Metadata related 
	public static final String CB_PACKAGETYPE_XMP  = FFConstants.SUBFORMAT_IDENTIFIER_XMP;
	public static final String CB_PACKAGETYPE_LIDO = FFConstants.SUBFORMAT_IDENTIFIER_LIDO;
	public static final String CB_PACKAGETYPE_METS = FFConstants.SUBFORMAT_IDENTIFIER_METS;
	public static final String CB_PACKAGETYPE_EAD  = FFConstants.SUBFORMAT_IDENTIFIER_EAD;     
		/** common metadata file for all XMP type packages */
	public static final String XMP_METADATA_FILE = "XMP.rdf";
		/** Fedora datastream id for EDM. */
	public static final String EDM_METADATA_STREAM_ID = "EDM"; 
	

	// Systems communication
	public static final String COMMAND_STOP_FACTORY = "STOP_FACTORY";
	public static final String COMMAND_START_FACTORY = "START_FACTORY";
	public static final String COMMAND_SHOW_DESCRIPTION = "SHOW_DESCRIPTION";
	public static final String COMMAND_SHOW_VERSION = "SHOW_VERSION";
	public static final String COMMAND_SHOW_ACTIONS = "SHOW_ACTIONS";
	public static final String COMMAND_SHOW_ACTION = "SHOW_ACTION";
	public static final String COMMAND_GRACEFUL_SHUTDOWN = "GRACEFUL_SHUTDOWN";
	public static final String QUESTION_MIGRATION_ALLOWED = "MIGRATION_ALLOWED?";
	public static final String ANSWER_YO = "YES";
	public static final String ANSWER_NO = "NO";
	public static final String IRODS_START_DELAYED = "START_DELAYED";
	public static final String IRODS_STOP_DELAYED = "STOP_DELAYED";
	public static final String QUEUE_TO_SERVER = "CB.SYSTEM";
	public static final String QUEUE_TO_CLIENT = "CB.CLIENT";
	public static final String QUEUE_TO_IRODS_SERVER = "IRODS.SYSTEM";
	
	// Premis related
	public static final String EVENT_TYPE_CREATE = "CREATE";
	public static final String EVENT_TYPE_COPY = "COPY";
	public static final String EVENT_TYPE_CONVERT = "CONVERT";
	
	// Error messages
	public static final String ERROR_MSG_ROLLBACK_NOT_IMPLEMENTED = "rollback not implemented yet.";
	public static final String ERROR_MSG_NOTCONFIGURED = " not configured properly.";
	public static final String ERROR_MSG_DURING_FILE_FORMAT_IDENTIFICATION = "Error during file format identification";

	// WorkArea organization
	public static final String WA_DIP = "dip";
	public static final String WA_AIP = "aip";
	public static final String WA_DATA = "data";
	public static final String WA_WORK = "work";
	public static final String WA_PIPS = "pips";
	public static final String WA_PUBLIC = "public";
	public static final String WA_INSTITUTION = "institution";

	//
	public static final String TEST_USER_SHORT_NAME = "TEST";

	// File system
	public static final Path CONF = new RelativePath("conf");
	public static final String PREMIS_XSD_PATH = "conf/premis.xsd";
	public static final String CONFIG_PROPS = "conf/config.properties";
	public static final File HIBERNATE_CFG = new RelativePath(CONF,"hibernateCentralDB.cfg.xml").toFile();
	public static final File XLINK_XSD = new File("src/main/xsd/xlink.xsd");
	public static final File PREMIS_XSD = new File("src/main/xsd/premis.xsd");
	public static final String XLINK_XSD_PATH = "conf/xlink.xsd";
	public static final String FIDO_GLUE_SCRIPT = "fido.sh";
	public static final Path FIDO_INSTALLATION = new RelativePath("fido");
	public static final String CONFIGURE_SCRIPT = "configure.sh"; 
	

	// Xml
	public static final String OWL_SAMEAS = "http://www.w3.org/2002/07/owl#sameAs";
	public static final String XSI_NS = "http://www.w3.org/2001/XMLSchema-instance";
	public static final String CONTRACT_NS = "http://www.danrw.de/contract/v1";
	public static final String CONTRACT_V1_URL = "http://www.danrw.de/contract/v1";
	public static final String CONTRACT_V1_SCHEMA_LOCATION = "http://www.danrw.de/schemas/contract/v1/danrw-contract-1.xsd";
	public static final Namespace METS_NS = Namespace.getNamespace("http://www.loc.gov/METS/");
	public static final Namespace XLINK_NS = Namespace.getNamespace("http://www.w3.org/1999/xlink");
	
	// Other
	public static final String LOCAL_NODE_BEAN_NAME = "localNode";
	public static final File BASIC_TEST_PACKAGE = Path.makeFile(CONF,"basic_test_package.tgz");
	public static final String OAI_DANRW_DE = "oai:danrw.de:";
	
	//
	public static final String ENCODING_UTF_8 = "UTF-8";
	
	// Action organization
	public static final String WORKFLOW_STATE_DIGIT_USER_ERROR="4";
	public static final String WORKFLOW_STATE_DIGIT_ERROR_PROPERLY_HANDLED = "1";
	public static final String WORKFLOW_STATE_DIGIT_ERROR_NOT_PROPERLY_HANDLED = "3";
	public static final String WORKFLOW_STATUS_START___INGEST_REGISTER_URN_ACTION = "150";
	public static final String WORKFLOW_STATUS_WAIT___PROCESS_FOR_USER_DECISION_ACTION = "645";
	
	
	
}
