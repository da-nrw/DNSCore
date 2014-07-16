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

package de.uzk.hki.da.format;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uzk.hki.da.utils.CommaSeparatedList;
import de.uzk.hki.da.utils.CommandLineConnector;
import de.uzk.hki.da.utils.ProcessInformation;



/**
 * Depends on a script that returns a comma separated list of puids for any given file.
 */
public class CLIFormatIdentifier implements FormatIdentifier{

	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(CLIFormatIdentifier.class);
	
	/** The conversion script. */
	private File conversionScript = null;
	
	/** The health check test file. */
	private File healthCheckTestFile = null;
	
	/** The health check expected outcome. */
	private String healthCheckExpectedOutcome = null;
	
	
	/**
	 * Instantiates a new cLI format identifier.
	 */
	public CLIFormatIdentifier(){
	}
	
	
	
	
	/* (non-Javadoc)
	 * @see de.uzk.hki.da.format.FormatIdentifier#identify(java.io.File)
	 */
	public Set<String> identify(File file) {
		
		if (!file.exists()) throw new Error("File doesn't exist");

		if (!conversionScript.exists()) throw new IllegalStateException(
				"ConversionScript doesn't exist: "+conversionScript.getAbsolutePath());
		
		ProcessInformation pi= CommandLineConnector.runCmdSynchronously( new String[]{
				
				conversionScript.getAbsolutePath(),
				file.getAbsolutePath()
		});
		
		
		if (pi.getExitValue()!=0){
			logger.warn("stdout from identification: "+pi.getStdErr());
			logger.warn("FormatIdentifier with exit value: " + pi.getExitValue());
			
			return new HashSet<String>();
		}
		
		logger.debug("stdout from identification: "+pi.getStdOut());
		Set<String> fileFormatsIdentifiers= new HashSet<String>(new CommaSeparatedList(pi.getStdOut()).toList());
		return fileFormatsIdentifiers;
	}
	
	
	/**
	 * Gets the conversion script.
	 *
	 * @return the conversion script
	 */
	public File getConversionScript(){
		return this.conversionScript;
	}
	
	
	/**
	 * Sets the conversion script.
	 *
	 * @param conversionScript the new conversion script
	 */
	public void setConversionScript(File conversionScript){
		this.conversionScript= conversionScript;
	}
	
	
	/**
	 * This method must guarantee that a tif file placed at conf/healthCheck.tif
	 * gets properly recognized by using conversionScript.
	 *
	 * @return true, if successful
	 */
	public boolean healthCheck() {
		
		if (conversionScript==null) throw new IllegalStateException("conversionScript not set");
		if (!conversionScript.exists()) throw new IllegalStateException(
				"ConversionScript doesn't exist: "+conversionScript.getAbsolutePath());
		if (!healthCheckTestFile.exists())
			throw new IllegalStateException("Test file "+healthCheckTestFile+" does not exist");
		
		
		ProcessInformation pi= CommandLineConnector.runCmdSynchronously( new String[]{
				
				conversionScript.getAbsolutePath(),
				healthCheckTestFile.getAbsolutePath()
		});
		
		logger.debug(pi.getStdOut());
		logger.debug(pi.getStdErr());
		
		if (pi.getStdOut().equals(healthCheckExpectedOutcome)) return true;
		return false;
	}


	/**
	 * Gets the health check test file.
	 *
	 * @return the health check test file
	 */
	public File getHealthCheckTestFile() {
		return healthCheckTestFile;
	}


	/**
	 * Sets the health check test file.
	 *
	 * @param healthCheckTestFile the new health check test file
	 */
	public void setHealthCheckTestFile(File healthCheckTestFile) {
		this.healthCheckTestFile = healthCheckTestFile;
	}


	/**
	 * Gets the health check expected outcome.
	 *
	 * @return the health check expected outcome
	 */
	public String getHealthCheckExpectedOutcome() {
		return healthCheckExpectedOutcome;
	}


	/**
	 * Sets the health check expected outcome.
	 *
	 * @param healthCheckExpectedOutcome the new health check expected outcome
	 */
	public void setHealthCheckExpectedOutcome(String healthCheckExpectedOutcome) {
		this.healthCheckExpectedOutcome = healthCheckExpectedOutcome;
	}

}
