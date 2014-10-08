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
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import de.uzk.hki.da.action.AbstractAction;
import de.uzk.hki.da.core.ConfigurationException;
import de.uzk.hki.da.format.ConverterService;
import de.uzk.hki.da.grid.DistributedConversionAdapter;
import de.uzk.hki.da.model.DAFile;
import de.uzk.hki.da.model.Event;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.model.ObjectPremisXmlReader;



/**
 * Executes local ConversionInstructions and waits for ConversionInstructions which
 * were to be done on other Nodes.
 * @author Daniel M. de Oliveira
 *
 */
public class ConvertAction extends AbstractAction {
	
	private DistributedConversionAdapter distributedConversionAdapter;
	
	private List<Event> localConversionEvents;
	
	public ConvertAction(){}
	
	@Override
	public void checkActionSpecificConfiguration() throws ConfigurationException {
		if (distributedConversionAdapter==null) throw new ConfigurationException("distributedConversionAdapter not set");
	}

	@Override
	public void checkSystemStatePreconditions() throws IllegalStateException {
		// Auto-generated method stub
		
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public boolean implementation() throws IOException {
		
		if (job.getConversion_instructions().size()==0)
			logger.warn("No Conversion Instruction could be found for job with id: "+job.getId());
		
		Object premisObject = parsePremisToMetadata(object.getLatest("premis.xml").toRegularFile().getAbsolutePath());
		object.setRights(premisObject.getRights());
		
		localConversionEvents = new ConverterService().convertBatch(
					object, 
					new ArrayList(job.getConversion_instructions()));
		
		logger.debug("listing file instances attached to latest package");
		for (DAFile f:object.getLatestPackage().getFiles()){
			logger.debug(f.toString());
		}
		
		for (Event e:localConversionEvents){
			object.getLatestPackage().getEvents().add(e);
			object.getLatestPackage().getFiles().add(e.getTarget_file());
		}
		
		job.getConversion_instructions().clear();
		job.getChildren().clear();
		
		// This is a hack because redmine #309 caused problems due to long replication times
		job.setRepl_destinations( localNode.getWorkingResource() );
		
		
		return true;
	}

	/**
	 * @author Thomas Kleinke
	 */
	@Override
	public void rollback() throws IOException {
		
		if (localConversionEvents != null) {
			for (Event e : localConversionEvents) {
				e.getTarget_file().toRegularFile().delete();
				
				object.getLatestPackage().getEvents().remove(e);
				object.getLatestPackage().getFiles().remove(e.getTarget_file());
			}
		}
		
		logger.info("@Admin: You can safely roll back this job to status "+this.getStartStatus()+" now.");
	}

	// TODO remove code duplication with scan action
	private Object parsePremisToMetadata(String pathToPremis) throws IOException {
		logger.debug("reading rights from " + pathToPremis);
		Object o = null;
				
		try {
			o = new ObjectPremisXmlReader()
			.deserialize(new File(pathToPremis));
		} catch (ParseException e) {
			throw new RuntimeException("error while parsing premis file",e);
		}
		
		return o;
	}
	
	

	public DistributedConversionAdapter getDistributedConversionAdapter() {
		return distributedConversionAdapter;
	}

	public void setDistributedConversionAdapter(
			DistributedConversionAdapter distributedConversionAdapter) {
		this.distributedConversionAdapter = distributedConversionAdapter;
	}
}
