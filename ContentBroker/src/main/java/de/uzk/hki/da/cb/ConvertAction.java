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
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uzk.hki.da.convert.ConverterService;
import de.uzk.hki.da.core.ConfigurationException;
import de.uzk.hki.da.grid.DistributedConversionAdapter;
import de.uzk.hki.da.model.DAFile;
import de.uzk.hki.da.model.Event;
import de.uzk.hki.da.model.Job;



/**
 * Executes local ConversionInstructions and waits for ConversionInstructions which
 * were to be done on other Nodes.
 * @author Daniel M. de Oliveira
 *
 */
public class ConvertAction extends AbstractAction {
	
	static final Logger logger = LoggerFactory.getLogger(ConvertAction.class);
	private DistributedConversionAdapter distributedConversionAdapter;
	
	private List<Event> localConversionEvents;
	
	public ConvertAction(){}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public boolean implementation() throws IOException {
		if (distributedConversionAdapter==null) throw new ConfigurationException("distributedConversionAdapter not set");
		object.reattach();
		
		if (job.getConversion_instructions().size()==0)
			logger.warn("No Conversion Instruction could be found for job with id: "+job.getId());
		
		localConversionEvents = new ConverterService().convertBatch(
					object.getLatest("premis.xml").toRegularFile(),
					object, 
					new ArrayList(job.getConversion_instructions()));
		
		logger.debug("listing file instances attached to latest package");
		for (DAFile f:object.getLatestPackage().getFiles()){
			logger.debug(f.toString());
		}
		
		// COMMENTED OUT ON PURPOSE - DO NOT DELETE - distributed conversion
//		distributedConversionAdapter.register(
//				"fork/"+object.getContractor().getShort_name()+"/"+object.getIdentifier()+"/data",
//				object.getDataPath()
//				);
//		if (job.getChildren().size() > 0)
//			waitForFriendJobsToBeReady(job.getChildren());
//		distributedConversionAdapter.replicateToTocalNode(
//				"fork/"+object.getContractor().getShort_name()+"/"+object.getIdentifier()+"/data"
//				);
		
		// TODO trim the stuff down to only our resource
		
		// COMMENTED OUT ON PURPOSE - DO NOT DELETE - distributed conversion
//		Session session = HibernateUtil.openSession();
//		session.beginTransaction();
//		dao.refreshJob(job); // To avoid concurrent modification of the files collection.
//		session.close();
//		
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


	void waitForFriendJobsToBeReady(List<Job> friendJobs){

		logger.info("Checking status of friend jobs");
		while (true){
			boolean allJobsReady = true;
			
			for (Job j:friendJobs){
				j = dao.refreshJob(j); // little trick to get the tests work ( j = )
				logger.info("Job on "+j.getInitial_node()+" is in status "+j.getStatus());
				if (j.getStatus().equals("581")) throw new RuntimeException(
						"Error in friend job encountered");
				if (!j.getStatus().equals("590")) allJobsReady = false;
			}
			
			if (allJobsReady){
				logger.info("Friends Jobs are ready");
				break;
			}
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				throw new RuntimeException("Problem with Thread.sleep() encountered");
			}
		}
	}

	
	/**
	 * @author Thomas Kleinke
	 */
	@Override
	void rollback() throws IOException {
		
		if (new File(object.getDataPath() + "dip").exists())
			FileUtils.deleteDirectory(new File(object.getDataPath() + "dip"));
		
		String latestARep = object.getNameOfNewestARep();
		String repName = latestARep.replace("a", "b");
		if (object.getNameOfNewestBRep().equals(repName))
			FileUtils.deleteDirectory(new File(object.getDataPath() + repName));

		if (localConversionEvents != null) {
			for (Event e : localConversionEvents) {
				object.getLatestPackage().getEvents().remove(e);
				object.getLatestPackage().getFiles().remove(e.getTarget_file());
			}
		}
	}

	public DistributedConversionAdapter getDistributedConversionAdapter() {
		return distributedConversionAdapter;
	}

	public void setDistributedConversionAdapter(
			DistributedConversionAdapter distributedConversionAdapter) {
		this.distributedConversionAdapter = distributedConversionAdapter;
	}
}
