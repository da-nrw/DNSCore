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

import de.uzk.hki.da.action.AbstractAction;
import de.uzk.hki.da.core.C;
import de.uzk.hki.da.core.SubsystemNotAvailableException;
import de.uzk.hki.da.format.FileFormatException;
import de.uzk.hki.da.format.FileFormatFacade;
import de.uzk.hki.da.format.FileWithFileFormat;
import de.uzk.hki.da.grid.DistributedConversionAdapter;
import de.uzk.hki.da.model.ConversionInstruction;
import de.uzk.hki.da.model.ConversionInstructionBuilder;
import de.uzk.hki.da.model.ConversionPolicy;
import de.uzk.hki.da.model.DAFile;
import de.uzk.hki.da.model.Package;
import de.uzk.hki.da.util.ConfigurationException;


/**
 * @author Daniel M. de Oliveira
 * @author Sebastian Cuy
 */
public class ScanForPresentationAction extends AbstractAction{
	
	private FileFormatFacade fileFormatFacade;
	private final ConversionInstructionBuilder ciB = new ConversionInstructionBuilder();
	private DistributedConversionAdapter distributedConversionAdapter;
	
	public ScanForPresentationAction(){}
	
	@Override
	public void checkConfiguration() {
		if (distributedConversionAdapter==null) throw new ConfigurationException("distributedConversionAdapter");
		if (fileFormatFacade==null) throw new ConfigurationException("fileFormatFacade");
	}
	

	@Override
	public void checkPreconditions() {
	}
	
	@Override
	public boolean implementation() throws SubsystemNotAvailableException{
		
		List<? extends FileWithFileFormat> fffl=null;
		try {
			fffl = fileFormatFacade.identify(o.getNewestFilesFromAllRepresentations(preservationSystem.getSidecarExtensions()));
		} catch (FileFormatException e) {
			throw new RuntimeException(C.ERROR_MSG_DURING_FILE_FORMAT_IDENTIFICATION,e);
		} catch (IOException e) {
			throw new SubsystemNotAvailableException(e);
		}
		
		@SuppressWarnings("unchecked")
		List<ConversionInstruction> cisPres = generateConversionInstructionsForPresentation(
			o.getLatestPackage(),
			(List<DAFile>) fffl);
		
		
		if (cisPres.size() == 0) logger.trace("no Conversion instructions for Presentation found!");				
		for (ConversionInstruction ci:cisPres) logger.info("Built conversionInstructionForPresentation: "+ci.toString());
		
		j.getConversion_instructions().addAll(cisPres);
		
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
	 * Every file in the files list gets tested with respect to if a ConversionPolicies of contractor PRESENTER will apply to it.
	 * If that is the case a ConversionInstruction gets generated for that file. Based on that information
	 * a format conversion process will later be executed for that file. 
	 * 
	 * @author Sebastian Cuy, Daniel de Oliveira
	 * @param pathToRepresentation physical path to source representation
	 * @param files
	 */
	public List<ConversionInstruction> generateConversionInstructionsForPresentation( 
			Package pkg, List<DAFile> files ){
		
		List<ConversionInstruction> cis = new ArrayList<ConversionInstruction>();
		
		for (DAFile file : files){
		
			// get cps for fileanduser. do with cps: assemble
			
			logger.trace("Generating ConversionInstructions for PRESENTER");
			List<ConversionPolicy> policies = preservationSystem.getApplicablePolicies(file, true);
			if ( o.grantsRight("PUBLICATION")
					&& !wa.toFile(file).getName().toLowerCase().endsWith(".xml")
					&& !wa.toFile(file).getName().toLowerCase().endsWith(".rdf")
					&& !wa.toFile(file).getName().toLowerCase().endsWith(".xmp")
					&& (policies == null || policies.isEmpty()) ) {
				throw new RuntimeException("No policy found for file "+wa.toFile(file).getAbsolutePath()
						+"("+file.getFormatPUID()+")! Package can not be published because it would be incomplete.");
			} else for (ConversionPolicy p : policies)	{
				logger.info("Found applicable Policy for FileFormat "+p.getSource_format()+" -> "+p.getConversion_routine().getName() + "("+ file.getRelative_path()+ ")");
				ConversionInstruction ci = ciB.assembleConversionInstruction(file, p);
				ci.setTarget_folder(ci.getTarget_folder());
				ci.setSource_file(file);
				
				cis.add(ci);
			}
		}
		return cis;
	}
	
	
	
	
	boolean isSemanticsPackage(File packageContent, String origName) {
		if (!new File(packageContent.getAbsolutePath()+"/"+origName).exists()) return false;
		return true;
	}

	boolean isStandardPackage(File packageContent){
		
		boolean is=true;
		if (!new File(packageContent.getAbsolutePath()+"/data").exists()) is=false;
		if (!new File(packageContent.getAbsolutePath()+"/bagit.txt").exists()) is=false;
		if (!new File(packageContent.getAbsolutePath()+"/bag-info.txt").exists()) is=false;
		if (!new File(packageContent.getAbsolutePath()+"/manifest-md5.txt").exists()) is=false;
		if (!new File(packageContent.getAbsolutePath()+"/tagmanifest-md5.txt").exists()) is=false;
		
		return is;
	}
		
	public FileFormatFacade getFormatScanService() {
		return fileFormatFacade;
	}

	public void setFormatScanService(FileFormatFacade fileFormatFacade) {
		this.fileFormatFacade = fileFormatFacade;
	}
	
	public DistributedConversionAdapter getDistributedConversionAdapter() {
		return distributedConversionAdapter;
	}

	public void setDistributedConversionAdapter(
			DistributedConversionAdapter distributedConversionAdapter) {
		this.distributedConversionAdapter = distributedConversionAdapter;
	}

	
}
