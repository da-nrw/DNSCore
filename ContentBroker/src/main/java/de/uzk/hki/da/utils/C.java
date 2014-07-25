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

package de.uzk.hki.da.utils;

import java.io.File;

/**
 * @author Daniel M. de Oliveira
 */
public class C {

	public static final File XLINK_XSD = new File("src/main/xsd/xlink.xsd");
	public static final File PREMIS_XSD = new File("src/main/xsd/premis.xsd");
	public static final String ERROR_ROLLBACK_NOT_IMPLEMENTED = "rollback not implemented yet.";
	public static final String ERROR_NOTCONFIGURED = " not configured properly.";
	public static final String METS_PUID = "danrw-fmt/1";
	public static final String EAD_PUID = "danrw-fmt/2";
	public static final String EAD = "EAD";
	public static final String XMP = "XMP";
	public static final String LIDO = "LIDO";
	public static final String XMP_RDF = "XMP.rdf";
	public static final String METS = "METS";
	public static final String LIDO_PUID = "danrw-fmt/4";
	public static final String XMP_PUID = "danrw-fmt/3";
	public static final String USER_ERROR_STATE_DIGIT="4";
	public static final String ZIP = "zip";
	public static final String TGZ = "tgz";
	public static final String DIP = "dip";
	public static final String AIP = "aip";
	public static final String DATA = "data";
	public static final String WORK = "work";
	public static final Path CONF = new RelativePath("conf");
	public static final String OWL_SAMEAS = "http://www.w3.org/2002/07/owl#sameAs";
	public static final File CONFIG_PROPS = new RelativePath(CONF,"config.properties").toFile();
	public static final File HIBERNATE_CFG = new RelativePath(CONF,"hibernateCentralDB.cfg.xml").toFile();
	public static final String TEST_USER_SHORT_NAME = "TEST";
	public static final String LOCAL_NODE_BEAN_NAME = "localNode";
	public static final File BASIC_TEST_PACKAGE = Path.makeFile(CONF,"basic_test_package.tgz");
	public static final String STOP_FACTORY = "STOP_FACTORY";
	public static final String START_FACTORY = "START_FACTORY";
	public static final String SHOW_DESCRIPTION = "SHOW_DESCRIPTION";
	public static final String SHOW_VERSION = "SHOW_VERSION";
	public static final String SHOW_ACTIONS = "SHOW_ACTIONS";
	public static final String GRACEFUL_SHUTDOWN = "GRACEFUL_SHUTDOWN";
	public static final String QUEUE_TO_SERVER = "CB.SYSTEM";
	public static final String QUEUE_TO_CLIENT = "CB.CLIENT";
	
	
}
