/*
  DA-NRW Software Suite | ContentBroker
  Copyright (C) 2015 LVRInfoKom
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

package de.uzk.hki.da.format;

import java.io.File;
import java.io.IOException;

import de.uzk.hki.da.utils.CommandLineConnector;


/**
 * @author Daniel M. de Oliveira
 */
public interface FormatIdentifier {

	/**
	 * @param f
	 * @return a format identifier if a format has been detected. Empty string if nothing has been detected.
	 * @throws IOException signals errors that happen during the process of reading the file.
	 */
	public String identify(File f, boolean pruneExceptions) throws IOException;
	
	/**
	 * Mainly for testing purposes to inject mocked cli
	 * @author Jens Peters
	 * @param cli
	 */
	public void setCliConnector(CommandLineConnector cli);
	

	/**
	 * Mainly for testing purposes to inject mocked cli
	 * @author Jens Peters
	 * @param cli
	 */
	public CommandLineConnector getCliConnector();
}
