/*
  DA-NRW Software Suite | ContentBroker
  Copyright (C) 2013 Historisch-Kulturwissenschaftliche Informationsverarbeitung
  Universität zu Köln
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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author Daniel M. de Oliveira
 */
public class PropertiesUtils {

	/**
	 * Parses the arguments.
	 *
	 * @param args the args
	 * @param props the props
	 */
	public static void parseArguments(String[] args,Properties props) {
		if (props==null){
			StringUtilities.logger.error("props is null in parseArguments");
			return;
		}
		
		for (String arg : args) {
			if (arg.startsWith("--")) {
				arg = arg.substring(2);
				if (arg.contains("=")) {
					String[] split = arg.split("=");
					props.put(split[0], split[1]);
				} else {
					props.put(arg, arg);
				}
			}
		}
	}

	/**
	 * @param propertiesFile
	 * @return properties
	 * @throws IOException
	 * @throws FileNotFoundException if propertiesfile does not exist
	 * @author Daniel M. de Oliveira
	 */
	public static Properties read(File propertiesFile) throws IOException{
		if (!propertiesFile.exists()) throw new FileNotFoundException(propertiesFile+" does not exist");
		InputStream in = new FileInputStream(propertiesFile);
		Properties props = new Properties();
		props.load(in);
		return props;
	}

}
