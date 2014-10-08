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
import de.uzk.hki.da.core.C;
import de.uzk.hki.da.core.ConfigurationException;
import de.uzk.hki.da.core.UserException;
import de.uzk.hki.da.core.UserException.UserExceptionId;
import de.uzk.hki.da.grid.DistributedConversionAdapter;
import de.uzk.hki.da.model.ConversionInstruction;
import de.uzk.hki.da.model.ConversionInstructionBuilder;
import de.uzk.hki.da.model.ConversionPolicy;
import de.uzk.hki.da.model.DAFile;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.model.ObjectPremisXmlReader;
import de.uzk.hki.da.service.MailContents;


/**
 * Scans the files and builds ConversionInstructions for them if MIGRATION right is granted.
 * If the MIGRATION right is not granted, sets the jobs state to ProcessUserDecisionsAction so that
 * the user can decide how to procede further 
 * 
 * @author Daniel M. de Oliveira
 */
public class ScanAction extends AbstractAction{
	
	private static final String MIGRATION = "MIGRATION";
	private final ConversionInstructionBuilder ciB = new ConversionInstructionBuilder();
	private DistributedConversionAdapter distributedConversionAdapter;
	
	@Override
	public void checkActionSpecificConfiguration() throws ConfigurationException {
		if (distributedConversionAdapter==null) throw new ConfigurationException("distributedConversionAdapter not set");
	}

	@Override
	public void checkSystemStatePreconditions() throws IllegalStateException {
		// Auto-generated method stub
	}

	@Override
	public boolean implementation() throws IOException {
		
		job.getConversion_instructions().addAll(
				generateConversionInstructions(object.getLatestPackage().getFiles()));
		
		Object premisObject = parsePremisToMetadata(object.getDataPath() +"/"+ job.getRep_name()+"a");
		if (!premisObject.grantsRight(MIGRATION))
		{
			logger.info("PREMIS says migration is not granted. Will ask the user what to do next.");
			new MailContents(preservationSystem,localNode).informUserAboutPendingDecision(object); 
			
			// "Manipulate" the end status to point to ProcessUserDecisionsAction
			job.setQuestion(C.QUESTION_MIGRATION_ALLOWED);
			this.setEndStatus(C.WORKFLOW_STATUS_WAIT___PROCESS_FOR_USER_DECISION_ACTION);
		}
		return true;
	}

	
	
	
	@Override
	public void rollback() {
	
		job.getConversion_instructions().clear();
		for (ConversionInstruction ci: job.getConversion_instructions()){
			logger.warn("still exists: "+ci);
		}
		
	}

	
	
	
	/**
	 * @author Daniel M. de Oliveira
	 * @param filesArchival
	 */
	private List<ConversionInstruction> generateConversionInstructions(List<DAFile> filesArchival) {
		
		List<ConversionInstruction> cis = new ArrayList<ConversionInstruction>();
		
		for (DAFile file : filesArchival){
			logger.debug("File: "+file.getRelative_path());
			if(file.getRelative_path().equals("XMP.rdf")) {
				logger.debug("Skipping rdf file");
			} else {
				for	(ConversionPolicy p:
					preservationSystem.getApplicablePolicies(file, false))
					{
					logger.info("Found applicable Policy for FileFormat "+
							p.getSource_format()+" -> "+p.getConversion_routine().getName() + "("+ file.getRelative_path()+ ")");
					
					ConversionInstruction ci = ciB.assembleConversionInstruction(file, p);
					logger.debug("Set source file "+file.getRelative_path());
					ci.setSource_file(file);
					cis.add(ci);
					
					logger.info("Built conversionInstructionForArchival: "+ci.toString());
				}
			}
		}
		
		return cis;
	}
	
	
	
	
	private Object parsePremisToMetadata(String pathToRepresentation) throws IOException {
		logger.debug("reading rights from " + pathToRepresentation + "/premis.xml");
		Object o = null;
				
		try {
			o = new ObjectPremisXmlReader()
			.deserialize(new File(pathToRepresentation + "/premis.xml"));
		} catch (ParseException e1) {
			throw new UserException(UserExceptionId.READ_SIP_PREMIS_ERROR, "Error while parsing premis file", e1);
		} catch (NullPointerException e2) {
			throw new UserException(UserExceptionId.READ_SIP_PREMIS_ERROR, "Error while parsing premis file", e2);
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
