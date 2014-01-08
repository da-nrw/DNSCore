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


/**
 * The Class ProcessInformation.
 */
public class ProcessInformation {

	/** The std err. */
	private String stdErr;
	
	/** The std out. */
	private String stdOut;
	
	/** The exit value. */
	private int exitValue;
	
	/**
	 * Gets the std err.
	 *
	 * @return the std err
	 */
	public String getStdErr() {
		return stdErr;
	}
	
	/**
	 * Sets the std err.
	 *
	 * @param stdErr the new std err
	 */
	public void setStdErr(String stdErr) {
		this.stdErr = stdErr;
	}
	
	/**
	 * Gets the std out.
	 *
	 * @return the std out
	 */
	public String getStdOut() {
		return stdOut;
	}
	
	/**
	 * Sets the std out.
	 *
	 * @param stdOut the new std out
	 */
	public void setStdOut(String stdOut) {
		this.stdOut = stdOut;
	}
	
	/**
	 * Gets the exit value.
	 *
	 * @return the exit value
	 */
	public int getExitValue() {
		return exitValue;
	}
	
	/**
	 * Sets the exit value.
	 *
	 * @param exitValue the new exit value
	 */
	public void setExitValue(int exitValue) {
		this.exitValue = exitValue;
	}
}
