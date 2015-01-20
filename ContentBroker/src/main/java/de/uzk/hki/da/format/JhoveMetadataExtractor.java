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

import de.uzk.hki.da.utils.CommandLineConnector;
import de.uzk.hki.da.utils.ProcessInformation;
import de.uzk.hki.da.utils.Utilities;

/**
 * 
 * @author Daniel M. de Oliveira
 *
 */
public class JhoveMetadataExtractor implements Connector {

	private static final String JHOVE_CONF = "conf/jhove.conf";
	private static final long jhoveTimeout = 100000;
	private static final String jhoveFolder = "jhove";
	private static final String JHOVE_BIN = "jhove";
	private static final String SHELL = "/bin/sh";
	
	private CommandLineConnector cli;
	
	/**
	 * @throws ConnectionException 
	 * 
	 */
	public boolean extract(File file, File extractedMetadata) throws IOException, ConnectionException {
		if (cli==null) throw new IllegalStateException("cli not set");
		if (!file.exists()) throw new FileNotFoundException("File to extract Metadata from doesn't exist! ("+file+")");
		String filePath = file.getAbsolutePath();
		if (Utilities.checkForWhitespace(filePath))
			filePath = "\"" + filePath + "\"";

		String CMD[] = new String[] {
                SHELL, JHOVE_BIN, "-c", JHOVE_CONF, "-h", "XML",
                filePath, "-o", extractedMetadata.getAbsolutePath() };
		String CMD2[] = new String[] {
                SHELL, JHOVE_BIN, "-c", JHOVE_CONF, "-h", "XML", 
                "-s", // skip parsing of the whole file
                filePath, "-o", extractedMetadata.getAbsolutePath() };
		
		if (execCMD(CMD) != 0) {
			if (execCMD(CMD2) !=0 )
				return false;
			return true;
		}
		
		return true;
	}
	
	
	private int execCMD(String cmd[]) throws ConnectionException {
		ProcessInformation pi=null;
		try {
			pi = cli.runCmdSynchronously(cmd,
	                new File(jhoveFolder),jhoveTimeout);
		}catch(IOException e) {
			throw new ConnectionException("Call to JHOVE was not successful.");
		}
		if (pi==null) {
			throw new ConnectionException("Call to JHOVE terminated with empty ProcessInformation");
		}
		return pi.getExitValue();
	}

	
	@Override
	public boolean isConnectable() {
		
		System.out.print("CONNECTIVITY CHECK - StandardFileFormatFacade - JHOVE");
		ProcessInformation pi=null;
		try {
			pi = cli.runCmdSynchronously(new String[] {
			        "/bin/sh", "jhove", "-c", JHOVE_CONF, "--version" },
			        new File(jhoveFolder),jhoveTimeout);
		} catch (IOException e) {
			return false;
		}
		if (pi.getStdOut().split("\\(Rel")[0].equals("Jhove ")){
			System.out.println(" .... OK");
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
