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

import java.io.IOException;
import java.io.InputStream;


/**
 * 
 * @author Christian Weitz
 * @author Thomas Kleinke
 */

public class LinuxEnvironmentUtils {
	public static String getContentBrokerPID() {
		
		ProcessBuilder builder = new ProcessBuilder("/bin/sh", "-c", "ps -ef | grep ContentBroker.jar | grep -v grep | awk '{ print $2 }'");
		Process process = null;
		try {
			process = builder.start();
			
			InputStream output = process.getInputStream();
			int c;
			String pid = "";
			while ((c = output.read()) != -1) {
				pid+=(char) c;
			}
			output.close();
			
			return pid.trim();
		} catch (IOException e) {
			
			e.printStackTrace();
		} finally {
		
			if (process != null)
				LinuxEnvironmentUtils.closeStreams(process);
		}
		
		return null;
	}

	/**
	 * 
	 * @author Christian Weitz
	 * @author Thomas Kleinke
	 */
	public static int getOpenFilesNumber() {
		
		String pid = "";
		Process process = null;
		try {
			pid = getContentBrokerPID();
			
			if (pid == null)
				return -1;
	
			Utilities.logger.debug("Running cmd: /bin/sh -c ls /proc/" + pid + "/fd | wc -w");
			ProcessBuilder builder = new ProcessBuilder("/bin/sh", "-c", "ls /proc/" + pid + "/fd | wc -w");
			process = builder.start();
		
			InputStream output = process.getInputStream();
			int c;
			String openFilesNum = "";
			while ((c = output.read()) != -1) {
				openFilesNum+=(char) c;
			}
			output.close();
					
			return Integer.parseInt(openFilesNum.trim());
			
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (process != null)
					LinuxEnvironmentUtils.closeStreams(process);
			}
	
		return -1;
	}

	/**
	 * 
	 * @author Christian Weitz
	 * @author Thomas Kleinke
	 */
	public static void closeStreams(Process process) {
		try {
			process.getInputStream().close();
			process.getOutputStream().close();
			process.getErrorStream().close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String logHeapSpaceInformation() {
		
		String output;
		Runtime runtime = Runtime.getRuntime();
		
		output = "Used Memory: " + (runtime.totalMemory() - runtime.freeMemory()) / (1024*1024) + " MB\n";
	    output += "Free Memory: " + runtime.freeMemory() / (1024*1024) + "MB \n";
	    output += "Total available Memory:" + runtime.totalMemory() / (1024*1024) + "MB \n";
	    output += "Max Memory:" + runtime.maxMemory() / (1024*1024) + "MB \n";
		
	    return output;
	}

}
