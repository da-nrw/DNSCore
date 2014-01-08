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
import java.text.ParseException;
import java.io.Reader;

import de.uzk.hki.da.model.Object;


/**
 * Interface for classes that deserialize preservation 
 * metadata encoded in different XML formats.
 * @author scuy
 */
public interface XmlReader {
	
	/**
	 * Reads the XML-Representation of a set of preservation
	 * metadata and creates the corresponding native object model representation of the ContentBroker.
	 *
	 * @param uri URI of the XML file
	 * @return preservation metadata as a Java object tree
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws ParseException the parse exception
	 */
	public Object deserialize(String uri) throws IOException, ParseException;
	
	/**
	 * Reads the XML-Representation of a set of preservation
	 * metadata and creates the corresponding native object model representation of the ContentBroker.
	 *
	 * @param file the XML file
	 * @return preservation metadata as a Java object tree
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws ParseException the parse exception
	 */
	public Object deserialize(File file) throws IOException, ParseException;
	
	/**
	 * Reads the XML-Representation of a set of preservation
	 * metadata and creates the corresponding native object model representation of the ContentBroker.
	 *
	 * @param reader a Reader representing the XML data
	 * @return preservation metadata as a Java object tree
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws ParseException the parse exception
	 */
	public Object deserialize(Reader reader) throws IOException, ParseException;

}
