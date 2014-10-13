/*
  DA-NRW Software Suite | ContentBroker
  Copyright (C) 2014 LVRInfoKom
  LandSchaftsverband Rheinland

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
import java.io.FileReader;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uzk.hki.da.cb.SendToPresenterAction;
import de.uzk.hki.da.core.Path;

/**
 * @author Sebastian Cuy
 * @author Daniel M. de Oliveira
 */
public class DCReader {

	static final Logger logger = LoggerFactory.getLogger(SendToPresenterAction.class);
	
	public String getPackageTypeFromDC(Path dipPathPublic, Path dipPathInstitution) {
			
		String packageType = null;
		File dcFile = Path.makeFile(dipPathPublic,"DC.xml");
		if (!dcFile.exists())
			dcFile = Path.makeFile(dipPathInstitution,"DC.xml");
		if (dcFile.exists()) {
			SAXBuilder builder = XMLUtils.createNonvalidatingSaxBuilder();
			Document doc;
			try {
				doc = builder.build(new FileReader(dcFile));
				Element formatEl = doc.getRootElement().getChild("format",
						Namespace.getNamespace("http://purl.org/dc/elements/1.1/"));
				if (formatEl == null) {
					logger.warn("No format element found in DC, unable to determine package type!");
				} else {
					packageType = formatEl.getTextNormalize();
				}
			} catch (Exception e) {
				logger.error("Error while parsing DC, unable to determine package type.", e);
			}
		} else {
			logger.warn("No DC file found, unable to determine package type!");
		}
		return packageType;
	}	
}
