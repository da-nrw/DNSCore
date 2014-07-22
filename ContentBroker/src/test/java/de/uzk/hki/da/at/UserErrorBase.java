/*
  DA-NRW Software Suite | ContentBroker
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

package de.uzk.hki.da.at;

import java.io.IOException;

import org.apache.commons.io.FileUtils;

import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.utils.C;
import de.uzk.hki.da.utils.Path;
import de.uzk.hki.da.utils.TC;

/**
 * @author Daniel M. de Oliveira
 */
public class UserErrorBase extends Base{

	private static final int timeout = 20000;
	
	Object ingestAndWaitForErrorState(String originalName,String errorState) throws IOException, InterruptedException{
		return ingestAndWaitForErrorState(originalName, errorState, C.TGZ);
	}
		
	Object ingestAndWaitForErrorState(String originalName,String errorStateLastDigit,String containerSuffix) throws IOException, InterruptedException{
		
		if (!containerSuffix.isEmpty()) containerSuffix="."+containerSuffix;
		
		FileUtils.copyFileToDirectory(Path.makeFile(TC.TEST_ROOT_AT,originalName+containerSuffix), 
				Path.makeFile(localNode.getIngestAreaRootPath(),C.TEST_USER_SHORT_NAME));
		waitForJobToBeInErrorStatus(originalName,errorStateLastDigit,timeout);
		return fetchObjectFromDB(originalName);
	}
}
