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

package de.uzk.hki.da.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * The Class CLIConnector.
 */
public class SimplifiedCommandLineConnector {
	
	/** The logger. */
	private static Logger logger = 
			LoggerFactory.getLogger(SimplifiedCommandLineConnector.class);
	
	private CommandLineConnector cl;
	private String errorMessages;
	
	public SimplifiedCommandLineConnector() {
		this.cl = new CommandLineConnector();
	}
	
	public SimplifiedCommandLineConnector(CommandLineConnector cl) {
		this.cl= cl;	
	}
	/**
	 * Execute.
	 *
	 * @param cmd the cmd
	 * @return true, if successful
	 */
	public boolean execute(String cmd[]) {
	
		logger.trace("SimplifiedCommandLineConnector executing conversion command: {}", cmd.toString());
		errorMessages=null;
		ProcessInformation pi = null;
		File redirect = null;
		try {
			redirect = File.createTempFile("Duppes", "log");
			pi = cl.runCmdSynchronously(cmd, null, 0, redirect);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		if (pi.getExitValue()!=0) {
			try {
				BufferedReader bR = new BufferedReader(new FileReader (redirect));
				try {
					int lR = 0;
					errorMessages = new String();
					String ls = System.lineSeparator();
					String line = bR.readLine();
					while (line != null && lR < 50) {
						errorMessages += line + ls;
						lR++;
						line = bR.readLine();
					}
					
					bR.close();
				} catch (IOException e) {
					errorMessages = "Unable to read output";
					try {
						bR.close();
						redirect.delete();
					} catch (IOException exctasy) {
						logger.error("Unable to close output", exctasy);
					}
				}
			} catch (FileNotFoundException exctasy) {
				errorMessages = "Unable to open output";
				logger.error("Unable to open output", exctasy);
			}
			
			logger.error( this.getClass()+": Recieved return code from terminal based command: "+
					pi.getExitValue() );
			logger.error(errorMessages);
			return false;
		}
		
		return true;
	}

	public String getErrorMessages() {
		return errorMessages;
	}
	
}
