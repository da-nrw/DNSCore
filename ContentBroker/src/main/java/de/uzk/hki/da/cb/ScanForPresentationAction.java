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
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import de.uzk.hki.da.core.ConfigurationException;
import de.uzk.hki.da.format.FormatScanService;
import de.uzk.hki.da.grid.DistributedConversionAdapter;
import de.uzk.hki.da.model.ConversionInstruction;
import de.uzk.hki.da.model.ConversionInstructionBuilder;
import de.uzk.hki.da.model.ConversionPolicy;
import de.uzk.hki.da.model.DAFile;
import de.uzk.hki.da.model.Package;
import de.uzk.hki.da.model.PreservationSystem;


/**
 * @author Daniel M. de Oliveira
 * @author Sebastian Cuy
 */
public class ScanForPresentationAction extends AbstractAction{
	
	private FormatScanService formatScanService;
	private PreservationSystem preservationSystem;
	private final ConversionInstructionBuilder ciB = new ConversionInstructionBuilder();
	private String sidecarExtensions;
	private DistributedConversionAdapter distributedConversionAdapter;
	
	public ScanForPresentationAction(){}
	
	@Override
	boolean implementation() throws FileNotFoundException {
		if (distributedConversionAdapter==null) throw new ConfigurationException("distributedConversionAdapter not set");
		if (formatScanService==null) throw new ConfigurationException("formatScanService not set");
		if (preservationSystem==null) // So we can prevent the preservationSystem to be instantiated in unit tests.
			preservationSystem = new PreservationSystem(dao);
		
		// check if object package type is set
		
		
		List<DAFile> newestFiles = object.getNewestFilesFromAllRepresentations(sidecarExtensions);
		if (newestFiles.size() == 0)
			throw new RuntimeException("No files found!");
		newestFiles = formatScanService.identify(newestFiles);
		
		List<ConversionInstruction> cisPres = generateConversionInstructionsForPresentation(
			object.getLatestPackage(),
			newestFiles);
		if (cisPres.size() == 0) logger.trace("no Conversion instructions for Presentation found!");				
		for (ConversionInstruction ci:cisPres) logger.info("Built conversionInstructionForPresentation: "+ci.toString());
		
		job.getConversion_instructions().addAll(cisPres);
		
		return true;
	}
	
	
	
	/**
	 * this is just for testing purposes
	 * @param sys
	 */
	void setPreservationSystem(PreservationSystem sys){
		preservationSystem = sys;
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
		if (preservationSystem==null) throw new IllegalStateException("preservationSystem not set");
		
		List<ConversionInstruction> cis = new ArrayList<ConversionInstruction>();
		
		for (DAFile file : files){
		
			// get cps for fileanduser. do with cps: assemble
			
			logger.trace("Generating ConversionInstructions for PRESENTER");
			List<ConversionPolicy> policies = preservationSystem.getApplicablePolicies(file, "PRESENTER");
			if ( object.grantsRight("PUBLICATION")
					&& !file.toRegularFile().getName().toLowerCase().endsWith(".xml")
					&& !file.toRegularFile().getName().toLowerCase().endsWith(".rdf")
					&& !file.toRegularFile().getName().toLowerCase().endsWith(".xmp")
					&& (policies == null || policies.isEmpty()) ) {
				throw new RuntimeException("No policy found for file "+file.toRegularFile().getAbsolutePath()
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
		
	public FormatScanService getFormatScanService() {
		return formatScanService;
	}

	public void setFormatScanService(FormatScanService formatScanService) {
		this.formatScanService = formatScanService;
	}
	
	public void setSidecarExtensions(String sidecarExtensions) {
		this.sidecarExtensions = sidecarExtensions;
	}

	public String getSidecarExtensions() {
		return sidecarExtensions;
	}

	@Override
	void rollback() {
		
		job.getConversion_instructions().clear();
		for (ConversionInstruction ci: job.getConversion_instructions()){
			logger.warn("still exists: "+ci);
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
