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
}
