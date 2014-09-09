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
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uzk.hki.da.model.DAFile;
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
	private FidoFormatScanService fidoFormatScanService;
	private static final String JHOVE_CONF = "conf/jhove.conf";
	private String jhoveFolder = "jhove";
	
	@Override
	public List<FileWithFileFormat> identify(List<FileWithFileFormat> files)
			throws FileNotFoundException {
		if (fidoFormatScanService==null) throw new IllegalStateException("fidoFormatScanService must not be null");
		
		return fidoFormatScanService.identify(files);
	}

	public FidoFormatScanService getFidoFormatScanService() {
		return fidoFormatScanService;
	}

	public void setFidoFormatScanService(FidoFormatScanService fidoFormatScanService) {
		this.fidoFormatScanService = fidoFormatScanService;
	}
	
	/**
	 * Extract.
	 *
	 * @param file the file
	 * @param jobId the job id
	 * @return the string
	 * @throws Exception the exception
	 */
	@Override
	public void extract(File file, File extractedMetadata) throws IOException {
		if (!file.exists()) throw new FileNotFoundException("File to extract Metadata from doesn't exist! ("+file+")");
		
		
		
		String filePath = file.getAbsolutePath();
//		
//		String path = filePath.replace('/', '_').replace('.', '_');
//		String outputFileName = DigestUtils.md5Hex(path) + ".xml";
//		String outputFolderName = new File(jhoveFolder).getAbsolutePath() + "/temp/" + jobId + "/";
//		
//		if (!new File(outputFolderName).exists())
//			new File(outputFolderName).mkdirs();
//		String outputFilePath = outputFolderName + outputFileName;
//
		
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
}
