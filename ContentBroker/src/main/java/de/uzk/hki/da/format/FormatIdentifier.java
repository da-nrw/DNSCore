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

package de.uzk.hki.da.format;

import java.io.File;
import java.util.Set;


/**
 * The Interface FormatIdentifier.
 *
 * @author Daniel M. de Oliveira
 */
public interface FormatIdentifier {

	/**
	 * Delivers a comma separated list of file format identifiers.
	 * The identifier type of the identifiers depends on the specific
	 * implementation.
	 *
	 * @param file the file
	 * @return the sets the
	 */
	public Set<String> identify(File file);
	
	
	/**
	 * Implementations of FormatIdentifier check themselves if they work properly in
	 * their current environments. This check is necessary because they normally depend
	 * on external services like FIDO, ffmpeg etc.
	 *
	 * @return true, if successful
	 */
	public boolean healthCheck();
}
