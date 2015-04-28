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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uzk.hki.da.utils.CommandLineConnector;
import de.uzk.hki.da.utils.ProcessInformation;
import de.uzk.hki.da.utils.StringUtilities;

/**
 * 
 * @author Daniel M. de Oliveira
 *
 */
public class JhoveMetadataExtractor implements MetadataExtractor {

	private static final Logger logger = LoggerFactory.getLogger(JhoveMetadataExtractor.class);
	
	private static final int _6_MINUTES = 3600000; // ms
	private static final String JHOVE_CONF = "conf/jhove.conf";
	private static final long JHOVE_TIMEOUT = _6_MINUTES;
	private static final String jhoveFolder = "jhove";
	private static final String JHOVE_BIN = "jhove";
	private static final String SHELL = "/bin/sh";
	
	private CommandLineConnector cli;
	
	private boolean connectabilityProved=false;
	
	
	
	/**
	 * Scans a file with jhove and extracts technical metadata to a xml file. 
	 * Tries it a second time if the first time fails.
	 * 
	 * @throws ConnectionException when timeout limit reached two times. 
	 * @throws FileNotFoundException 
	 */
	public void extract(File file, File extractedMetadata) throws ConnectionException, FileNotFoundException {
		
		if (!connectabilityProved) throw new IllegalStateException("Make sure you run isExecutable first.");
		
		if (cli==null) throw new IllegalStateException("cli not set");
		if (!file.exists()) 
			throw new FileNotFoundException("Missing file or directory: "+file);
		if (!extractedMetadata.getParentFile().exists())
			throw new IllegalArgumentException("ParentFolder "+extractedMetadata.getParentFile()+" must exist in order to create "+extractedMetadata);
		

		int retval=0;
		try {
			retval=execCMD(jhoveCmd(extractedMetadata, makeFilePath(file)));
		}catch(IOException possibleTimeOut) {
			logger.warn(possibleTimeOut.getMessage());
			retval=1;
		} 
		if (retval==0) return;
		
		logger.info("Problem during extracting technical metadata. Will retry without parsing the whole file.");
		
		retval=0;
		try {
			retval=execCMD(jhoveCmdSkipWholeFileParsing(extractedMetadata, makeFilePath(file)));
		}catch(IOException possibleTimeout) {
			throw new ConnectionException("Call to JHOVE ended with possible timeout (the 2nd time already).",possibleTimeout);
		}

		if (retval==0) return;
		throw new ConnectionException("Recieved return not null return value from jhove.");
	}
	
	


	private String makeFilePath(File file) {
		String filePath;
		filePath=file.getAbsolutePath();
		if (StringUtilities.checkForWhitespace(filePath))
			filePath = "\"" + filePath + "\"";
		return filePath;
	}


	private String[] jhoveCmd(File extractedMetadata, String filePath) {
		return new String[] {
                SHELL, JHOVE_BIN, "-c", JHOVE_CONF, "-h", "XML",
                filePath, "-o", extractedMetadata.getAbsolutePath() };
	}


	private String[] jhoveCmdSkipWholeFileParsing(File extractedMetadata, String filePath) {
		return new String[] {
                SHELL, JHOVE_BIN, "-c", JHOVE_CONF, "-h", "XML", 
                "-s", // skip parsing of the whole file
                filePath, "-o", extractedMetadata.getAbsolutePath() };
	}
	
	
	private int execCMD(String cmd[]) throws ConnectionException, IOException {
		ProcessInformation pi=null;
		pi = cli.runCmdSynchronously(cmd,
                new File(jhoveFolder),JHOVE_TIMEOUT);
		if (pi==null) {
			throw new ConnectionException("Call to JHOVE terminated with empty ProcessInformation");
		}
		if (pi.getExitValue()!=0) {
			logger.debug("StdOut from jhove cmd: "+pi.getStdOut());
			logger.debug("StdErr from jhove cmd: "+pi.getStdErr());
		}
		return pi.getExitValue();
	}

	
	@Override
	public boolean isConnectable() {
		
		System.out.print("INFO: CHECKING - "+this.getClass().getName()+".isConnectable() ....");
		ProcessInformation pi=null;
		try {
			pi = cli.runCmdSynchronously(new String[] {
			        "/bin/sh", "jhove", "-c", JHOVE_CONF, "--version" },
			        new File(jhoveFolder),JHOVE_TIMEOUT);
		} catch (IOException e) {
			return false;
		}
		if (pi.getStdOut().split("\\(Rel")[0].equals("Jhove ")){
			System.out.println(" .... OK");
			connectabilityProved=true;
			return true;
		}else {
			System.out.println(" .... FAIL");
			return false;
		}
	}
	

	public CommandLineConnector getCli() {
		return cli;
	}


	public void setCli(CommandLineConnector cli) {
		this.cli = cli;
	}
}
