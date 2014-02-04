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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * The Class CLIConnector.
 */
public class SimplifiedCommandLineConnector {
	
	/** The logger. */
	private static Logger logger = 
			LoggerFactory.getLogger(SimplifiedCommandLineConnector.class);
	
	/**
	 * Execute.
	 *
	 * @param cmd the cmd
	 * @return true, if successful
	 */
	public boolean execute(String cmd[]) {
	
		logger.trace("Executing conversion command: {}", cmd);
		ProcessInformation pi= CommandLineConnector.runCmdSynchronously( cmd );
		if (pi.getExitValue()!=0) {
			logger.error( this.getClass()+": Recieved return code from terminal based command: "+
					pi.getExitValue() );
			logger.error("cli conversion failed!\n\nstdOut: ------ \n\n\n"+
					pi.getStdOut()+"\n\n ----- end of stdOut\n\nstdErr: ------ \n\n\n"+
					pi.getStdErr()+"\n\n ----- end of stdErr");
			return false;
		}
		
		return true;
	}
}
