/*
  DA-NRW Software Suite | SIP-Builder
  Copyright (C) 2014 Historisch-Kulturwissenschaftliche Informationsverarbeitung
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

package de.uzk.hki.da.sb;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Can be used to write log messages into the SIP-Builder's log file
 * 
 * @author Thomas Kleinke
 */
public class Logger {

	private File logfile;
	private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

	public Logger(String dataPath) {
		
		logfile = new File(dataPath + "/sipbuilder_errors.log");
	}

	/**
	 * Writes a message into the log file
	 * 
	 * @param message The message to write
	 */
	public void log(String message) {
		
		try {
			PrintWriter writer = new PrintWriter(
								 new BufferedWriter(
								 new FileWriter(logfile, true)));
			writer.println(dateFormat.format(new Date()) + " " + message + "\n");
			writer.close();
		} catch (IOException e) {
			throw new RuntimeException("Failed to create log file " + logfile.getAbsolutePath(), e);
		}
	}
	
	/**
	 * Writes a message into the log file and appends the stack trace of an exception
	 * 
	 * @param message The message to write
	 * @param exception The exception
	 */
	public void log(String message, Exception exception) {
		
		try {
			PrintWriter writer = new PrintWriter(
								 new BufferedWriter(
								 new FileWriter(logfile, true)));
			writer.println(dateFormat.format(new Date()) + " " + message);
			exception.printStackTrace(writer);						
			writer.print("\n");
			writer.close();
		} catch (IOException e) {
			throw new RuntimeException("Failed to create log file " + logfile.getAbsolutePath(), e);
		}
	}
}
