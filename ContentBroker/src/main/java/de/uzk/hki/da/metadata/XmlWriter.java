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

package de.uzk.hki.da.metadata;

import java.io.File;
import java.io.IOException;


/**
 * Interface for classes that serialize preservation 
 * metadata given as java objects into different formats.
 * @author scuy
 */
public interface XmlWriter {
	
	/**
	 * Writes the given metadata into an XML file.
	 *
	 * @param metadata preservation metadata as a java object tree
	 * @param uri URI of the file that should be written to
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void serialize(RightsContainer metadata, String uri) throws IOException;
	
	/**
	 * Writes the given metadata into an XML file.
	 *
	 * @param metadata preservation metadata as a java object tree
	 * @param file the file that should be written to
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void serialize(RightsContainer metadata, File file) throws IOException;

}
