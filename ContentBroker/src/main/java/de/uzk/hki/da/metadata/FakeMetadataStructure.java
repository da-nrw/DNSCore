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

package de.uzk.hki.da.metadata;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.jdom.JDOMException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uzk.hki.da.model.Document;
import de.uzk.hki.da.utils.Path;

/**
 * @author Polina Gubaidullina
 */

public class FakeMetadataStructure extends MetadataStructure{

	/** The logger. */
	public Logger logger = LoggerFactory
			.getLogger(FakeMetadataStructure.class);
	
	public FakeMetadataStructure(Path workPath,File metadataFile, List<Document> documents)
			throws FileNotFoundException, JDOMException, IOException {
		super(workPath, metadataFile, documents);
		logger.debug("Create fake metadata structure.");
	}

	@Override
	public boolean isValid() {
		return true;
	}

	@Override
	public File getMetadataFile() {
		return null;
	}

	@Override
	public HashMap<String, HashMap<String, List<String>>> getIndexInfo(
			String objectId) {
		// TODO Auto-generated method stub
		return null;
	}
}
