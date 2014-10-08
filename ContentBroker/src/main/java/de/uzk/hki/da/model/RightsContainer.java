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

package de.uzk.hki.da.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


/**
 * Acts as a container for preservation metadata. Used by JAXB in MetsRightsSectionXmlReader.
 * @author Sebastian Cuy
 */
@XmlRootElement(name="preservationMetadata")
public class RightsContainer {
	
	/** The rights. */
	private List<RightsStatement> rights;
	
	/**
	 * Instantiates a new rights container.
	 */
	private RightsContainer() {
		rights = new ArrayList<RightsStatement>();
	}
	
	/**
	 * Gets the rights.
	 *
	 * @return the rights
	 */
	@XmlElement(name="right")
	public List<RightsStatement> getRights() {
		return rights;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer("PreservationMetadata[");
		buf.append("\n\tRightsStatements: ");
		for (RightsStatement right : getRights()) buf.append(right);
		buf.append("\n]");
		return buf.toString();
	}
}
