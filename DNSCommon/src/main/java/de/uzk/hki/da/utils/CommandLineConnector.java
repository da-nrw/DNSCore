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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Arrays;

import de.uzk.hki.da.utils.LinuxEnvironmentUtils;



/**
 * The Class CommandLineConnector.
 *
 * @author Daniel M. de Oliveira
 */

public class CommandLineConnector {

	/**
	 * Run cmd synchronously.
	 *
	 * @param cmd the cmd
	 * @param workingDir the working dir
	 * @return the process information
	 */
	public static ProcessInformation runCmdSynchronously(String cmd[],File workingDir){
		
		String stdErr="";
		String stdOut="";
		
		
		// TODO log message without delimiter
		if ((workingDir==null)||(workingDir.equals("")))
			Utilities.logger.debug("Running cmd \"{}\"", Arrays.toString(cmd));
		else
			Utilities.logger.debug("Running cmd \"{}\" in working dir \"{}\"", Arrays.toString(cmd), workingDir);
		
		
		Process p = null;
		ProcessInformation pi= new ProcessInformation();
		try{
			ProcessBuilder pb = new ProcessBuilder(cmd);
			if (workingDir!=null) pb.directory(workingDir);
			
			p = pb.start();
			
			InputStream errStr= p.getErrorStream();
			int c1;
			while ((c1= errStr.read()) != -1){
				stdErr+=(char) c1;
			}
			pi.setStdErr(stdErr);
			errStr.close();
			
			InputStream outStr= p.getInputStream();
			int c2;
			while ((c2= outStr.read()) != -1){
				stdOut+=(char) c2;
			}
			pi.setStdOut(stdOut);
			outStr.close();
	
			p.waitFor();
			pi.setExitValue(p.exitValue());
		}
		catch( FileNotFoundException e){			
			Utilities.logger.error("File not found in runShellCommand",e);
			throw new RuntimeException(e);
		}	
		catch (Exception e){		
			Utilities.logger.error("Error in runShellCommand",e);
			throw new RuntimeException(e);
		}
		finally {
			if (p != null)
				LinuxEnvironmentUtils.closeStreams(p);
		}
		return pi;
	}

	/**
	 * Run cmd synchronously.
	 *
	 * @param cmd the cmd
	 * @return the process information
	 * @author Daniel M. de Oliveira
	 */
	public static ProcessInformation runCmdSynchronously(String cmd[]){
		return runCmdSynchronously(cmd, null);
	}

}
