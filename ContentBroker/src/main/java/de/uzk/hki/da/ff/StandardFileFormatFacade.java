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

package de.uzk.hki.da.ff;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.irods.jargon.core.exception.InvalidArgumentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uzk.hki.da.model.SubformatIdentificationPolicy;
import de.uzk.hki.da.utils.CommandLineConnector;
import de.uzk.hki.da.utils.ProcessInformation;
import de.uzk.hki.da.utils.Utilities;

/**
 * Implementation for file identification: FIDO.
 * Implementation for basic metadata extraction: JHOVE.
 * 
 * @author Daniel M. de Oliveira
 */
public class StandardFileFormatFacade implements FileFormatFacade{

	static final Logger logger = LoggerFactory.getLogger(StandardFileFormatFacade.class);
	
	private FidoFormatScanService pronomFormatScanService;
	private SubformatScanService subformatScanService;
	
	private static final String JHOVE_CONF = "conf/jhove.conf";
	private String jhoveFolder = "jhove";
	
	private List<SubformatIdentificationPolicy> subformatIdentificationPolicies =
			new ArrayList<SubformatIdentificationPolicy>();
	
	/**
	 * The output of fido typically is a comma separated list of puids for each file. Only the last entry of the list
	 * will be taken.
	 * @throws IOException 
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<FileWithFileFormat> identify(List<? extends FileWithFileFormat> files)
			throws IOException {

		pronomFormatScanService = new FidoFormatScanService();
		pronomFormatScanService.identify((List<FileWithFileFormat>) files);
		
		for (SubformatIdentificationPolicy p:subformatIdentificationPolicies)
			logger.debug("policy available: "+p);
		
		subformatScanService = new SubformatScanService();
		subformatScanService.setSecondStageScanPolicies(subformatIdentificationPolicies);
		
		
		try {
			subformatScanService.identify((List<FileWithFileFormat>) files);
		} catch (InvalidArgumentException e) {
			throw new RuntimeException("all files must have a PUID by now");
		}
		
		
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
	 * 
	 */
	@Override
	public void extract(File file, File extractedMetadata) throws IOException {
		if (!file.exists()) throw new FileNotFoundException("File to extract Metadata from doesn't exist! ("+file+")");
		
		
		
		String filePath = file.getAbsolutePath();
		
		if (Utilities.checkForWhitespace(filePath))
		{
			filePath = "\"" + filePath + "\"";
		}

		ProcessInformation pi = CommandLineConnector.runCmdSynchronously(new String[] {
                "/bin/sh", "jhove", "-c", JHOVE_CONF, "-h", "XML",
                filePath, "-o", extractedMetadata.getAbsolutePath() },
                new File(jhoveFolder));
				
		if ((pi == null) || (pi.getExitValue() != 0)) {
            if (pi != null)
            	logger.warn(pi.getStdErr());
            logger.warn("Jhove error. Will try again without complete file parsing.");
            
            pi = CommandLineConnector.runCmdSynchronously(new String[] {
                    "/bin/sh", "jhove", "-c", JHOVE_CONF, "-h", "XML", "-s",
                    filePath, "-o", extractedMetadata.getAbsolutePath() },
                    new File(jhoveFolder));
            
            if ((pi == null) || (pi.getExitValue() != 0)) {
                if (pi != null)
                	logger.error(pi.getStdErr());
                throw new RuntimeException("Jhove error");
            }
		}
	}



	/**
	 * @param subformatIdentifier fully qualified java class name a 
	 * class which can be used to identify subformats for a range of puids.
	 */
	@Override
	public void registerSubformatIdentificationMethod(String puids,
			String subformatIdentifier) {
		
		SubformatIdentificationPolicy p = new SubformatIdentificationPolicy();
		p.setPUID(puids);
		p.setFormatIdentifierScriptName(subformatIdentifier);
		
		subformatIdentificationPolicies.add(p);
	}
}
