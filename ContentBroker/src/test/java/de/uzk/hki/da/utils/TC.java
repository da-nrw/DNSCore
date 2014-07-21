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
 * Test constants.
 * @author Daniel M. de Oliveira
 */
public class TC {

	public static final Path TEST_ROOT = new RelativePath("src","test","resources");
	public static final Path TEST_ROOT_FORMAT = Path.make(TEST_ROOT,"format");
	public static final Path TEST_ROOT_AT = Path.make(TEST_ROOT,"at");
	public static final Path TEST_ROOT_CB = Path.make(TEST_ROOT,"cb");
	
	public static final File CONFIG_PROPS_CI = new RelativePath("src","main","conf","config.properties.ci").toFile();
}
