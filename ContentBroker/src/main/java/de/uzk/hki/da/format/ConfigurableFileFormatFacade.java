/*
  DA-NRW Software Suite | ContentBroker
  Copyright (C) 2014 LVRInfoKom
  Landschaftsverband Rheinland

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

package de.uzk.hki.da.format;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uzk.hki.da.utils.C;
import de.uzk.hki.da.utils.Path;

/**
 * Implementation for file identification: FIDO.
 * Implementation for basic metadata extraction: JHOVE.
 * 
 * @author Daniel M. de Oliveira
 */
public class ConfigurableFileFormatFacade implements FileFormatFacade{

	private static final Logger logger = LoggerFactory.getLogger(ConfigurableFileFormatFacade.class);
	
	private FormatScanService formatScanService;
	private FormatScanService subformatScanService;
	private MetadataExtractor metadataExtractor;
	
	private KnownFormatCmdLineErrors knownFormatCommandLineErrors;
	
	public ConfigurableFileFormatFacade() {}
	
	/**
	 *      formatIdentifierClassName,policyTriggerPUID
	 */
	private Map<String,Set<String>> subformatIdentificationStrategyTriggerMap = new HashMap<String,Set<String>>();

	
	/**
	 * The output of fido typically is a comma separated list of puids for each file. Only the last entry of the list
	 * will be taken.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<FileWithFileFormat> identify(Path workPath,List<? extends FileWithFileFormat> files,boolean pruneExceptions)
			throws IOException, FileNotFoundException {
		
		for (FileWithFileFormat f:files) {
			if (!(Path.makeFile(workPath,f.getPath()).exists()))
				throw new FileNotFoundException("Missing file: "+Path.makeFile(workPath,f.getPath()));
		}
		getFormatScanService().setKnownFormatCmdLineErrors(knownFormatCommandLineErrors);
		getFormatScanService().identify(workPath,(List<FileWithFileFormat>) files,pruneExceptions);
		
		for (String s:subformatIdentificationStrategyTriggerMap.keySet())
			logger.debug("strategy available: "+s);
		
		if (getSubformatScanService()!=null) {
			getSubformatScanService().setKnownFormatCmdLineErrors(knownFormatCommandLineErrors);
			getSubformatScanService().identify(workPath,(List<FileWithFileFormat>) files,pruneExceptions);
		}
		doCorrections(workPath,files,pruneExceptions);
		return (List<FileWithFileFormat>) files;
	}

	
	
	/**
	 * Compensate for unwanted FIDO behavior.
	 * @param files
	 * @throws IOException
	 */
	private void doCorrections(Path workPath,List<? extends FileWithFileFormat> files,boolean pruneExceptions) throws IOException{
		for (FileWithFileFormat f:files){
			
			// This is to compensate for a behavior of FIDO where it detects a too specific xml format. 
			if (f.getFormatPUID().equals(FFConstants.DROID_XML_PUID)
			 || f.getFormatPUID().equals(FFConstants.DROID_XML_PUID2)) {
				f.setFormatPUID(FFConstants.XML_PUID);
				f.setSubformatIdentifier(
						new XMLSubformatIdentifier().identify(Path.makeFile(workPath,f.getPath()),pruneExceptions));
			}else
			if (f.getFormatPUID().equals(FFConstants.XMP_PUID)) {
				f.setFormatPUID(FFConstants.XML_PUID);
				f.setSubformatIdentifier(C.SUBFORMAT_IDENTIFIER_XMP);
			}
		}
	}
	
	
	/**
	 * @throws ConnectionException 
	 * @throws IOException 
	 * 
	 */
	@Override
	public boolean extract(File file, File extractedMetadata) throws ConnectionException {
		try {
			getMetadataExtractor().extract(file, extractedMetadata);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			// This is to resemble the exact same behavior 
			// of how it was before the distinction of IOTimeoutException and IOException 
			// in CommandLineConnector and JhoveMetadataExtractor and adresses cases 
			// in which binaries are missing. For an example see de.uzk.hki.da.format.CTFileFormatFacadeExtractTests#binaryNotPresent().
			throw new ConnectionException(e.getMessage());
		}
		return true;
	}
	
	

	

	/**
	 * @param subformatIdentificationStrategyName fully quallyfied java class name of the piece of 
	 * code which is used for subformat identification for files of format <i>formatPuid</i>. The class 
	 * must be of a type which implements {@link FormatIdentifier}.
	 */
	@Override
	public void registerSubformatIdentificationStrategyPuidMapping(
			String subformatIdentificationStrategyName,String puid) {
		
		if (subformatIdentificationStrategyTriggerMap.containsKey(subformatIdentificationStrategyName)) {
			subformatIdentificationStrategyTriggerMap.get(subformatIdentificationStrategyName).add(puid);
		}else{
			Set<String> puids = new HashSet<String>();
			puids.add(puid);
			subformatIdentificationStrategyTriggerMap.put(subformatIdentificationStrategyName, puids);
		}
		
		if (subformatScanService instanceof SubformatScanService)
			((SubformatScanService) subformatScanService).setSubformatIdentificationPolicies(subformatIdentificationStrategyTriggerMap);
	}



	@Override
	public boolean connectivityCheck() {
		
		if (getSubformatScanService()==null) // no strategypuidmapping registered yet 
			return ( metadataExtractor.isConnectable() 
					&&getFormatScanService().isConnectable() ); 
		else
			return (metadataExtractor.isConnectable()
					&&getFormatScanService().isConnectable()
					&&getSubformatScanService().isConnectable());
	}


	@Override
	public FormatScanService getFormatScanService() {
		if (formatScanService!=null) formatScanService.setKnownFormatCmdLineErrors(knownFormatCommandLineErrors);
		return formatScanService;
	}


	@Override
	public void setFormatScanService(FormatScanService formatScanService) {
		this.formatScanService = formatScanService;
	}


	@Override
	public MetadataExtractor getMetadataExtractor() {
		return metadataExtractor;
	}


	@Override
	public void setMetadataExtractor(MetadataExtractor metadataExtractor) {
		if (!metadataExtractor.isConnectable()) throw new RuntimeException("cannot connect");
		this.metadataExtractor = metadataExtractor;
	}


	@Override
	public FormatScanService getSubformatScanService() {
		if (subformatScanService!=null) subformatScanService.setKnownFormatCmdLineErrors(knownFormatCommandLineErrors);
		return subformatScanService;
	}


	@Override
	public void setSubformatScanService(FormatScanService subformatScanService) {
		this.subformatScanService = subformatScanService;
	}



	public KnownFormatCmdLineErrors getKnownFormatCommandLineErrors() {
		return knownFormatCommandLineErrors;
	}



	public void setKnownFormatCommandLineErrors(
			KnownFormatCmdLineErrors knownFormatCommandLineErrors) {
		this.knownFormatCommandLineErrors = knownFormatCommandLineErrors;
	}



}
