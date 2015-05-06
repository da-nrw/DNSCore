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

import static de.uzk.hki.da.core.C.QUESTION_MIGRATION_ALLOWED;
import static de.uzk.hki.da.core.C.WORKFLOW_STATUS_WAIT___PROCESS_FOR_USER_DECISION_ACTION;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.uzk.hki.da.action.AbstractAction;
import de.uzk.hki.da.core.MailContents;
import de.uzk.hki.da.core.PreconditionsNotMetException;
import de.uzk.hki.da.grid.DistributedConversionAdapter;
import de.uzk.hki.da.model.ConversionInstruction;
import de.uzk.hki.da.model.ConversionInstructionBuilder;
import de.uzk.hki.da.model.ConversionPolicy;
import de.uzk.hki.da.model.DAFile;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.model.ObjectPremisXmlReader;
import de.uzk.hki.da.util.ConfigurationException;


/**
 * Scans the files and builds ConversionInstructions for them if MIGRATION right is granted.
 * If the MIGRATION right is not granted, sets the jobs state to ProcessUserDecisionsAction so that
 * the user can decide how to procede further 
 * 
 * @author Daniel M. de Oliveira
 */
public class ScanAction extends AbstractAction{
	
	private static final String PREMIS_XML = "premis.xml";
	private static final String XMP_RDF = "XMP.rdf";
	private static final String MIGRATION = "MIGRATION";
	private final ConversionInstructionBuilder ciB = new ConversionInstructionBuilder();
	private DistributedConversionAdapter distributedConversionAdapter;
	

	@Override
	public void checkConfiguration() {
		if (distributedConversionAdapter==null) throw new ConfigurationException("distributedConversionAdapter");
	}
	

	@Override
	public void checkPreconditions() {
		if (o.getLatest(PREMIS_XML)==null) throw new PreconditionsNotMetException("Must exist: "+PREMIS_XML);
	    if (!wa.toFile(o.getLatest(PREMIS_XML)).exists()) throw new PreconditionsNotMetException("Must exist: "+PREMIS_XML);
	}

	@Override
	public boolean implementation() throws IOException {
		
		j.getConversion_instructions().addAll(
				generateConversionInstructions(o.getLatestPackage().getFiles()));
		
		Object premisObject = parsePremisToMetadata(wa.toFile(o.
				getLatest(PREMIS_XML)));
		if (!premisObject.grantsRight(MIGRATION))
		{
			logger.info("PREMIS says migration is not granted. Will ask the user what to do next.");
			new MailContents(preservationSystem,n).informUserAboutPendingDecision(o); 
			
			// "Manipulate" the end status to point to ProcessUserDecisionsAction
			j.setQuestion(QUESTION_MIGRATION_ALLOWED);
			this.setEndStatus(WORKFLOW_STATUS_WAIT___PROCESS_FOR_USER_DECISION_ACTION);
		}
		
		return true;
	}

	
	
	
	@Override
	public void rollback() {
	
		j.getConversion_instructions().clear();
		for (ConversionInstruction ci: j.getConversion_instructions()){
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
			if(file.getRelative_path().equals(XMP_RDF)) {
				logger.debug("Skipping rdf file");
			} else {
				for	(ConversionPolicy p:
					preservationSystem.getApplicablePolicies(file, false))
					{
					logger.info("Found applicable Policy for FileFormat "+
							p.getSource_format()+" -> "+p.getConversion_routine().getName() + "("+ file.getRelative_path()+ ")");
					
					ConversionInstruction ci = ciB.assembleConversionInstruction(wa,file, p);
					logger.debug("Set source file "+file.getRelative_path());
					ci.setSource_file(file);
					cis.add(ci);
					
					logger.info("Built conversionInstructionForArchival: "+ci.toString());
				}
			}
		}
		
		return cis;
	}
	
	
	
	
	private Object parsePremisToMetadata(File premis) throws IOException {
		Object o = null;
				
		try {
			o = new ObjectPremisXmlReader()
			.deserialize(premis);
		} catch (Exception e) {
			// do not throw userexception here since ability to deserialize should already have been checked in UnpackAction.
			throw new RuntimeException("Error while deserializing PREMIS", e);
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
