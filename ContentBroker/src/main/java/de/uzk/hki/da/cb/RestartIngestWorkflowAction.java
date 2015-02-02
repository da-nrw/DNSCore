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

import static de.uzk.hki.da.cb.ArchiveReplicationCheckAction.clearNonpersistentObjectProperties;
import static de.uzk.hki.da.cb.RestructureAction.makeRepOfSIPContent;
import static de.uzk.hki.da.cb.RestructureAction.revertToSIPContent;
import static de.uzk.hki.da.cb.BuildAIPAction.deleteBagitFiles;
import static de.uzk.hki.da.core.C.*;
import static de.uzk.hki.da.utils.StringUtilities.*;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import de.uzk.hki.da.action.AbstractAction;
import de.uzk.hki.da.util.ConfigurationException;
import de.uzk.hki.da.util.Path;


/**
 * Resets job in ingest workflow back to the start status of the ingest workflow.
 * Suited to reset jobs from between 12x and 36x, which is a portion of the ingest workflow (beans-workflow.ingest.xml).
 * Removes all artifacts generated during the ingest workflow and puts the contents of only the newest a representation back
 * to the data folder which is by definition similar to the state of the unpacked SIP.
 * 
 * @author Thomas Kleinke
 * @author Daniel M. de Oliveira
 */
public class RestartIngestWorkflowAction extends AbstractAction {

	private static final String A = "a";
	private static final String UNDERSCORE = "_";

	public RestartIngestWorkflowAction(){SUPPRESS_OBJECT_CONSISTENCY_CHECK = true;}

	
	@Override
	public void checkActionSpecificConfiguration() throws ConfigurationException {}

	
	@Override
	public void checkSystemStatePreconditions() throws IllegalStateException {}

	
	@Override
	public boolean implementation() throws IOException {

		if (!o.isDelta())
			o.setUrn(null);
		deleteBagitFiles(o.getPath());
		
		revertToSIPContent(o.getPath(), o.getDataPath(), j.getRep_name());
		deleteTemporaryPIPs();
		
		clearNonpersistentObjectProperties(o);
		j.getConversion_instructions().clear();
		return true;
	}
	
	
	@Override
	public void rollback() throws Exception {
		if (isNotSet(j.getRep_name())) throw new IllegalStateException("Rep name not set.");
		
		if (	thereIsNoARepresentation()
				&&(o.getDataPath().toFile().exists())
				&&dataIsOnlySubfolderOfObject()) {
			
			makeRepOfSIPContent(o.getPath(), o.getDataPath(), j.getRep_name());
		} 
		else {
			throw new RuntimeException("Rollback not possible.");
		}
	}

	
	
	private boolean dataIsOnlySubfolderOfObject() {
		String subfolders[] = o.getPath().toFile().list();
		if (subfolders.length!=1) return false;
		if (!subfolders[0].equals(WA_DATA)) return false;
		return true;
	}


	private boolean thereIsNoARepresentation() {
		return (!Path.makeFile(o.getDataPath(),j.getRep_name()+A).exists());
	}


	/**
	 * @author Thomas Kleinke
	 */
	private void deleteTemporaryPIPs() throws IOException {
		
		if (makePIPSourceFolder(WA_PUBLIC).exists())
			FileUtils.deleteDirectory(makePIPSourceFolder(WA_PUBLIC));
		if (makePIPSourceFolder(WA_INSTITUTION).exists())
			FileUtils.deleteDirectory(makePIPSourceFolder(WA_INSTITUTION));
	}
	
	private File makePIPSourceFolder(String pipType) {
		return Path.makeFile(n.getWorkAreaRootPath(),WA_PIPS,pipType,o.getContractor().getShort_name(),o.getIdentifier()+UNDERSCORE+o.getLatestPackage().getId());
	}
}
