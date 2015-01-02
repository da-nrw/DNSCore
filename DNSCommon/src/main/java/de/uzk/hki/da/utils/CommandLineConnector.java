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
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;



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
	 * 
	 * @throws IOException if the program cannot run for some reason or timeout reached.
	 * 
	 * @author Daniel M. de Oliveira
	 */
	public static ProcessInformation runCmdSynchronously(String cmd[],File workingDir,long timeout) 
			throws IOException{

		if (timeout==0) timeout=Long.MAX_VALUE;
		
		if ((workingDir==null)||(workingDir.equals("")))
			Utilities.logger.debug("Running cmd \"{}\"", Arrays.toString(cmd));
		else
			Utilities.logger.debug("Running cmd \"{}\" in working dir \"{}\"", Arrays.toString(cmd), workingDir);
		
		
		Process p = null;
		ProcessInformation pi= new ProcessInformation();
		try {
			ProcessBuilder pb = new ProcessBuilder(cmd);
			if (workingDir!=null) pb.directory(workingDir);
			p = pb.start(); 
			
			waitForProcessToTerminate(p,timeout);
			
			redirectStreams(pi,p);
			pi.setExitValue(p.exitValue());
		}
		finally {
			closeStreams(p);
		}
		return pi;
	}

	private static void waitForProcessToTerminate(Process p,long timeout) throws IOException {
		
		int timeElapsed=0;
		while(true) {
			
			try {
				p.exitValue();
				break;
			}catch(IllegalThreadStateException e) {
				
				try {
					Thread.sleep(100);
				} catch (InterruptedException e1) {}
				timeElapsed+=100;
				
				if (timeElapsed>timeout) {
					p.destroy();
					throw new IOException("timeout reached");
				}
			}
		}
	}
	
	private static void closeStreams(Process p) throws IOException {
		
		if (p != null){
			p.getInputStream().close();
			p.getErrorStream().close();
			p.getOutputStream().close();
		}
	}
	
	private static void redirectStreams(ProcessInformation pi,Process p) throws IOException {
		String stdErr="";
		String stdOut="";
		
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
	}
	
	
	/**
	 * Convenience method for {@link #runCmdSynchronously(String[], File)}
	 * @author Daniel M. de Oliveira
	 * @throws IOException 
	 */
	public static ProcessInformation runCmdSynchronously(String cmd[]) throws IOException{
		return runCmdSynchronously(cmd, null, 0);
	}

}
