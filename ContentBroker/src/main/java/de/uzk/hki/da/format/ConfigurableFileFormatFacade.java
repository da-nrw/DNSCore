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
	
	
	public ConfigurableFileFormatFacade() {}
	
	/**
	 *      formatIdentifierClassName,policyTriggerPUID
	 */
	private Map<String,Set<String>> subformatIdentificationStrategyTriggerMap = new HashMap<String,Set<String>>();

	
	/**
	 * The output of fido typically is a comma separated list of puids for each file. Only the last entry of the list
	 * will be taken.
	 * @throws IOException 
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<FileWithFileFormat> identify(List<? extends FileWithFileFormat> files)
			throws IOException {

		getFormatScanService().identify((List<FileWithFileFormat>) files);
		
		for (String s:subformatIdentificationStrategyTriggerMap.keySet())
			logger.debug("strategy available: "+s);
		
		if (getSubformatScanService()!=null)
			getSubformatScanService().identify((List<FileWithFileFormat>) files);
		
		doCorrections(files);
		return (List<FileWithFileFormat>) files;
	}

	
	
	/**
	 * Compensate for unwanted FIDO behavior.
	 * @param files
	 * @throws IOException
	 */
	private void doCorrections(List<? extends FileWithFileFormat> files) throws IOException{
		for (FileWithFileFormat f:files){
			
			// This is to compensate for a behavior of FIDO where it detects a too specific xml format. 
			if (f.getFormatPUID().equals(FFConstants.DROID_XML_PUID)) {
				f.setFormatPUID(FFConstants.XML_PUID);
				f.setSubformatIdentifier(
						new XMLSubformatIdentifier().identify(f.toRegularFile()));
			}else
			if (f.getFormatPUID().equals(FFConstants.XMP_PUID)) {
				f.setFormatPUID(FFConstants.XML_PUID);
				f.setSubformatIdentifier(FFConstants.SUBFORMAT_IDENTIFIER_XMP);
			}
		}
	}
	
	
	/**
	 * @throws ConnectionException 
	 * 
	 */
	@Override
	public boolean extract(File file, File extractedMetadata) throws ConnectionException {
		try {
			getMetadataExtractor().extract(file, extractedMetadata);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
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
		this.metadataExtractor = metadataExtractor;
	}


	@Override
	public FormatScanService getSubformatScanService() {
		return subformatScanService;
	}


	@Override
	public void setSubformatScanService(FormatScanService subformatScanService) {
		this.subformatScanService = subformatScanService;
	}



}
