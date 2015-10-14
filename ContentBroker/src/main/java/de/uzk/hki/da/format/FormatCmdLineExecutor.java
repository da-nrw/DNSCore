/*
  DA-NRW Software Suite | ContentBroker
  Copyright (C) 2015 LVRInfoKom
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

/**
 * @author jens Peters
 * Warapper executes CommandLine operations on formats,
 * allows to prune certain known errors
 */
package de.uzk.hki.da.format;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uzk.hki.da.core.UserException;
import de.uzk.hki.da.utils.CommandLineConnector;
import de.uzk.hki.da.utils.ProcessInformation;

public class FormatCmdLineExecutor {

	private String stdOut = "";
	
	private String stdErr = "";
	
	private int exitValue;
	private List<FormatCmdLineError> knownErrors;
	
	private boolean pruneExceptions = false;
	
	private FormatCmdLineError error;
	
	private static final Logger logger = LoggerFactory.getLogger(FormatCmdLineExecutor.class);
	
	private CommandLineConnector clc;
	
	public FormatCmdLineExecutor(CommandLineConnector clc, KnownFormatCmdLineErrors knownErrors) {
		this.clc = clc;
		if (knownErrors!=null)
		this.knownErrors = knownErrors.getFormatCmdLineErrors();
	}
	
	public boolean execute(String [] cmd) {
		ProcessInformation pi;
		try {
			pi =  clc.runCmdSynchronously(cmd);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return parse(pi);
	}
	
	private boolean parse(ProcessInformation pi) {
		stdOut = pi.getStdOut();
		stdErr = pi.getStdErr();
		if (stdOut!=null) stdOut = stdOut.trim();
		if (stdErr!=null) stdErr = stdErr.trim();
		exitValue = pi.getExitValue();
		if (exitValue != 0) {
			if (knownErrors!=null) 
			for (FormatCmdLineError ke : knownErrors) {
				if (stdErr.matches(ke.getErrOutContainsRegex())) {
					if (pruneExceptions) {
						error = ke;
						return true;
					} else throwCorrespondingExceptionIfNotPruned(ke);
				} 
			}
			
			throw new RuntimeException("Error: " + stdErr);
		} else return true;
	}
	
	public List<FormatCmdLineError> getKnownErrors() {
		return knownErrors;
	}

	public void setKnownErrors(List<FormatCmdLineError> knownErrors) {
		this.knownErrors = knownErrors;
	}

	private void throwCorrespondingExceptionIfNotPruned(FormatCmdLineError ke) {
		if (!pruneExceptions) {
			throw new UserException(ke.getUserExceptionId(), ke.getErrorText());
		} else logger.error(ke.getUserExceptionId() + " " + ke.getErrorText() + " recieved, was pruned!");
	}

	public String getStdOut() {
		return stdOut;
	}

	public String getStdErr() {
		return stdErr;
	}

	public boolean isPruneExceptions() {
		return pruneExceptions;
	}

	public void setPruneExceptions(boolean pruneExceptions) {
		this.pruneExceptions = pruneExceptions;
	}

	public int getExitValue() {
		return exitValue;
	}

	public FormatCmdLineError getError() {
		return error;
	}

}
