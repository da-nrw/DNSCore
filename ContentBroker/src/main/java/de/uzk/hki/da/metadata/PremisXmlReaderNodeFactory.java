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

import nu.xom.*;


/**
 * This custom NodeFactory is used to ignore jhove data when parsing a premis.xml file
 * 
 * @author Thomas Kleinke
 */
public class PremisXmlReaderNodeFactory extends NodeFactory {

	/** The jhove section. */
	boolean jhoveSection = false;

	/* (non-Javadoc)
	 * @see nu.xom.NodeFactory#startMakingElement(java.lang.String, java.lang.String)
	 */
	@Override
	public Element startMakingElement(String name, String namespace) {

		if (name.equals("objectCharacteristicsExtension"))
			jhoveSection = true;

		if (jhoveSection)
			return null;
		else
			return new Element(name, namespace);
	}

	/* (non-Javadoc)
	 * @see nu.xom.NodeFactory#finishMakingElement(nu.xom.Element)
	 */
	@Override
	public Nodes finishMakingElement(Element element) {

		if (element.getLocalName().equals("objectCharacteristics"))
			jhoveSection = false;

		return super.finishMakingElement(element);
	}

	/* (non-Javadoc)
	 * @see nu.xom.NodeFactory#makeText(java.lang.String)
	 */
	@Override
	public Nodes makeText(String data) {

		if (jhoveSection)
			return new Nodes();
		else
			return super.makeText(data);
	}	    
}


