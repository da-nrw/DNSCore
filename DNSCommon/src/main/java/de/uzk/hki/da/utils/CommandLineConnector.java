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

	private static final int INTERVAL = 100;

	/**
	 * Run cmd synchronously. When timeout is reached, the process gets killed. 
	 *
	 * @param cmd the cmd
	 * @param workingDir the working dir
	 * @param timeout in ms. if set to 0, timeout is automatically set to Long.MAX_VALUE.
	 * @return the process information
	 * 
	 * @throws IOException if the program cannot run for some reason or does not finish in time.
	 * 
	 * @author Daniel M. de Oliveira
	 */
	public ProcessInformation runCmdSynchronously(String cmd[],File workingDir,long timeout) 
			throws IOException{

		if (timeout==0) timeout=Long.MAX_VALUE;
		
		logCmd(cmd, workingDir);
		
		
		Process p = null;
		ProcessInformation pi=null;
		try {
			p=startProcess(cmd, workingDir);
			waitForProcessToTerminate(p,timeout);
			pi=assembleProcessInformation(p);
		}
		finally {
			closeStreams(p);
		}
		return pi;
	}


	/**
	 * Convenience method for {@link #runCmdSynchronously(String[], File)}
	 * @author Daniel M. de Oliveira
	 * @throws IOException 
	 */
	public ProcessInformation runCmdSynchronously(String cmd[]) throws IOException{
		return runCmdSynchronously(cmd, null, 0);
	}


	/**
	 * Convenience method for {@link #runCmdSynchronously(String[], File)}
	 * @author Daniel M. de Oliveira
	 * @throws IOException 
	 */
	public ProcessInformation runCmdSynchronously(String cmd[],long timeout) throws IOException{
		return runCmdSynchronously(cmd, null, timeout);
	}


	/**
	 * Convenience method for {@link #runCmdSynchronously(String[], File)}
	 * @author Daniel M. de Oliveira
	 * @throws IOException 
	 */
	public ProcessInformation runCmdSynchronously(String cmd[],File workingDir) throws IOException{
		return runCmdSynchronously(cmd, workingDir, 0);
	}


	private void logCmd(String[] cmd, File workingDir) {
		if ((workingDir==null)||(workingDir.equals("")))
			Utilities.logger.debug("Running cmd \"{}\"", Arrays.toString(cmd));
		else
			Utilities.logger.debug("Running cmd \"{}\" in working dir \"{}\"", Arrays.toString(cmd), workingDir);
	}
	
	
	private Process startProcess(String[] cmd,File workingDir) throws IOException {
		Process p=null;
		ProcessBuilder pb = new ProcessBuilder(cmd);
		if (workingDir!=null) pb.directory(workingDir);
		p = pb.start(); 
		return p;
	}
	

	private void waitForProcessToTerminate(Process p,long timeout) throws IOException {
		
		int timeElapsed=0;
		while(true) {
			
			try {
				p.exitValue();
				break;
			}catch(IllegalThreadStateException e) {
				
				try {
					Thread.sleep(INTERVAL);
				} catch (InterruptedException e1) {}
				timeElapsed+=INTERVAL;
				
				if (timeElapsed>timeout) {
					p.destroy();
					throw new IOException("Process did not finished. Timeout at "+timeout+".");
				}
			}
		}
	}
	
	private ProcessInformation assembleProcessInformation(Process p) throws IOException {
		ProcessInformation pi= new ProcessInformation();
		pi.setStdErr(convertStream(p.getErrorStream()));
		pi.setStdOut(convertStream(p.getInputStream()));
		pi.setExitValue(p.exitValue());
		return pi;
	}
	
	private void closeStreams(Process p) throws IOException {
		
		if (p != null){
			p.getInputStream().close();
			p.getErrorStream().close();
			p.getOutputStream().close();
		}
	}
	
	
	private String convertStream(InputStream is) throws IOException {
		
		String stdOut="";
		InputStream outStr= is;
		int c2;
		while ((c2= outStr.read()) != -1){
			stdOut+=(char) c2;
		}
		outStr.close();
		return stdOut;
	}
	
}
