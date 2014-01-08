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

package de.uzk.hki.da.cb;
import java.io.FileNotFoundException;
import java.io.IOException;



public class PreprocessForPresentationAction extends AbstractAction {

	@Override
	boolean implementation() throws FileNotFoundException, IOException {
		object.reattach();
		
//		String packageType = (String) actionCommunicatorService.getDataObject(job.getId(), "package_type");
		
		// collect xmp files into one "XMP manifest"
		// is now done in UpdateMetadataAction, TODO remove action from beans
		/*if ("XMP".equals(packageType)) {
			String publicPath = job.getPackage().getDataPath() + "dip/public/";
			logger.debug("collecting files in public path: {}", publicPath);
			XmpCollector.collect(new File(publicPath), new File(publicPath + "XMP.rdf"));
			String instPath = job.getPackage().getDataPath() + "dip/institution/";
			logger.debug("collecting files in institution path: {}", instPath);
			XmpCollector.collect(new File(instPath), new File(instPath + "XMP.rdf"));
		}*/
		
		return true;
		
	}

	@Override
	void rollback() throws Exception {
		
	}

}
