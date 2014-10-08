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

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.NotImplementedException;

import de.uzk.hki.da.action.AbstractAction;
import de.uzk.hki.da.core.C;
import de.uzk.hki.da.core.ConfigurationException;
import de.uzk.hki.da.core.Path;

/**
 * Resets job in ingest workflow back to 120.
 * Suited to reset jobs from between 12x and 36x.
 * 
 * @author Thomas Kleinke
 * @author Daniel M. de Oliveira
 */
public class RestartIngestWorkflowAction extends AbstractAction {

	public RestartIngestWorkflowAction(){SUPPRESS_OBJECT_CONSISTENCY_CHECK = true;}
	
	@Override
	public void checkActionSpecificConfiguration() throws ConfigurationException {
		// Auto-generated method stub
	}

	@Override
	public void checkSystemStatePreconditions() throws IllegalStateException {
		// Auto-generated method stub
	}

	@Override
	public boolean implementation() throws IOException {
		
		if (!object.isDelta())
			object.setUrn(null);
		
		revertDataFolderToSIPContent();		
		
		deletePIPS();
		
		object.getLatestPackage().getEvents().clear();
		object.getLatestPackage().getFiles().clear();
		job.getConversion_instructions().clear();

		return true;
	}
	
	@Override
	public void rollback() throws Exception {
		throw new NotImplementedException("No rollback implemented for this action");
	}

	/**
	 * Leaves only the representation with the SIP content. 
	 *
	 * @author Daniel M. de Oliveira
	 * @throws IOException 
	 */
	private void revertDataFolderToSIPContent() throws IOException {

		List<String> filesC = Arrays.asList(object.getDataPath().toFile().list());
		Collections.sort(filesC,Collections.reverseOrder());
		String newestRep = filesC.iterator().next();
		if (newestRep.endsWith("+b")){
			FileUtils.deleteDirectory(Path.makeFile(object.getDataPath(),newestRep));
			newestRep = newestRep.replace("+b", "+a");
		}
		
		File sipContent = Path.makeFile(object.getDataPath(),newestRep);
		File sipTemp = Path.makeFile(object.getPath(),"___sipContent");
		
		FileUtils.moveDirectory(sipContent, sipTemp);
		FileUtils.deleteDirectory(object.getDataPath().toFile());
		FileUtils.moveDirectory(sipTemp, object.getDataPath().toFile());
	}
	
	/**
	 * 
	 * Deletes previously created dips
	 * 
	 * @author Thomas Kleinke
	 * @throws IOException 
	 */
	private void deletePIPS() throws IOException {
		File publicDipFolder = Path.makeFile(localNode.getWorkAreaRootPath(),C.WA_PIPS,C.WA_PUBLIC,
			object.getContractor().getShort_name(),object.getIdentifier() + "_" + object.getLatestPackage().getId());
		File institutionDipFolder = Path.makeFile(localNode.getWorkAreaRootPath(),C.WA_PIPS,C.WA_INSTITUTION,
				object.getContractor().getShort_name(),object.getIdentifier() + "_" + object.getLatestPackage().getId());
		
		if (publicDipFolder.exists())
			FileUtils.deleteDirectory(publicDipFolder);
		if (institutionDipFolder.exists())
			FileUtils.deleteDirectory(institutionDipFolder);
	}
}
