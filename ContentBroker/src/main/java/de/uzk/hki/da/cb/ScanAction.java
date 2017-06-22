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

import static de.uzk.hki.da.utils.C.QUESTION_MIGRATION_ALLOWED;
import static de.uzk.hki.da.utils.C.WORKFLOW_STATUS_WAIT___PROCESS_FOR_USER_DECISION_ACTION;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TreeSet;

import org.apache.commons.io.FilenameUtils;

import de.uzk.hki.da.action.AbstractAction;
import de.uzk.hki.da.core.MailContents;
import de.uzk.hki.da.core.PreconditionsNotMetException;
import de.uzk.hki.da.grid.DistributedConversionAdapter;
import de.uzk.hki.da.metadata.EadMetsMetadataStructure;
import de.uzk.hki.da.model.ConversionInstruction;
import de.uzk.hki.da.model.ConversionInstructionBuilder;
import de.uzk.hki.da.model.ConversionPolicy;
import de.uzk.hki.da.model.DAFile;
import de.uzk.hki.da.model.Event;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.model.ObjectPremisXmlReader;
import de.uzk.hki.da.util.ConfigurationException;
import de.uzk.hki.da.utils.C;
import de.uzk.hki.da.utils.FriendlyFilesUtils;


/**
 * Scans the files and builds ConversionInstructions for them if MIGRATION right is granted.
 * If the MIGRATION right is not granted, sets the jobs state to ProcessUserDecisionsAction so that
 * the user can decide how to procede further 
 * 
 * @author Daniel M. de Oliveira
 */
public class ScanAction extends AbstractAction{
	
	private static final String PREMIS_XML = "premis.xml";
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
		
		List<ConversionInstruction> cis;
		cis = generateConversionInstructions(o.getLatestPackage().getFiles());
		j.getConversion_instructions().addAll(cis);
		
		Object premisObject = parsePremisToMetadata(wa.toFile(o.getLatest(PREMIS_XML)));
		
		o.setDdbExclusion(premisObject.ddbExcluded());
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
		
		TreeSet<String> neverConverted = this.neverConverted();
		for (DAFile file : filesArchival) {
			String relPath = file.getRelative_path();
			if (neverConverted.contains(relPath)) {
				logger.debug("Skipping file: " + relPath);
			} else {
				List<ConversionPolicy> convPolicy = preservationSystem.getApplicablePolicies(file, false);
				if (convPolicy.size() < 1) {
					logger.debug("No policy: " + relPath);
				} else {
					if (FriendlyFilesUtils.isFriendlyFile(relPath, o.getFriendlyFileExtensions())) {
						this.suppressedEvent(file);
						logger.debug("Friendly file: " + relPath);
					} else {
						for (ConversionPolicy p : convPolicy) {
							ConversionInstruction ci = ciB.assembleConversionInstruction(wa, file, p);
							ci.setSource_file(file);
							cis.add(ci);

							logger.debug(ci.toString());
						}
					}
				}
			}
		}
		
		return cis;
	}
	
	protected void suppressedEvent(DAFile srcDaFile) {
		Event e = new Event();
		e.setSource_file(srcDaFile);
		e.setType(C.EVENT_TYPE_CONVERSION_SUPRESSED);
		e.setDate(new Date());
		e.setAgent_type("NODE");
		e.setAgent_name(n.getName());
		o.getLatestPackage().getEvents().add(e);
	}
	
	protected TreeSet<String> neverConverted(){
		TreeSet<String> ret = new TreeSet<String>();
		ret.add(PREMIS_XML);

		if (o.getMetadata_file() != null) {
			ret.add(o.getMetadata_file());
			String packageType = o.getPackage_type();

			if ("EAD".equals(packageType)) {
				String mfPathSrc = o.getLatest(o.getMetadata_file()).getPath().toString();
				EadMetsMetadataStructure emms = null;
				try {
					emms = new EadMetsMetadataStructure(wa.dataPath(), new File(mfPathSrc), o.getDocuments());
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (emms != null) {
					List<String> metse = emms.getMetsRefsInEad();
					for (int mmm = 0; mmm < metse.size(); mmm++) {
						String mets = metse.get(mmm);
						String normMets = FilenameUtils.normalize(mets);
						if (normMets != null){
							mets = normMets; 
						}
						ret.add(mets);
					}
				}
			}
		}
		
		return ret;
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
