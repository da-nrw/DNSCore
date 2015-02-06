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

import static de.uzk.hki.da.cb.ArchiveReplicationCheckAction.*;

import java.io.IOException;

import org.apache.commons.io.FileUtils;

import de.uzk.hki.da.action.AbstractAction;
import de.uzk.hki.da.grid.DistributedConversionAdapter;
import de.uzk.hki.da.util.ConfigurationException;

/**
 * @author ???
 */
public class CleanWorkAreaAction extends AbstractAction{

	private DistributedConversionAdapter distributedConversionAdapter;
	
	public CleanWorkAreaAction() {setKILLATEXIT(true);}
	
	@Override
	public void checkActionSpecificConfiguration() throws ConfigurationException {
		if (distributedConversionAdapter==null) throw new ConfigurationException("distributedConversionAdapter not set");
	}

	
	@Override
	public boolean implementation() throws IOException {
		
		// to prevent leftover files from irods collection removal we delete the dirs on the filesystem first.
		FileUtils.deleteDirectory(o.getPath().toFile());
		
		clearNonpersistentObjectProperties(o);
		toCreate=createPublicationJob(j,o,preservationSystem.getPresServer());
		return true;
	}
	
	@Override
	public void rollback() throws Exception {
		toCreate=null;
	}

	
	public DistributedConversionAdapter getDistributedConversionAdapter() {
		return distributedConversionAdapter;
	}


	public void setDistributedConversionAdapter(
			DistributedConversionAdapter distributedConversionAdapter) {
		this.distributedConversionAdapter = distributedConversionAdapter;
	}
}
