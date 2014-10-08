/*
  DA-NRW Software Suite | ContentBroker
  Copyright (C) 2014 LVRInfoKom
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
package de.uzk.hki.da.ff;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uzk.hki.da.utils.CommaSeparatedList;
import de.uzk.hki.da.utils.CommandLineConnector;
import de.uzk.hki.da.utils.ProcessInformation;

/**
 * @author Daniel M. de Oliveira
 */
public class CLISecondaryFormatIdentifier implements SecondaryFormatIdentifier{

	private static final Logger logger = LoggerFactory.getLogger(CLISecondaryFormatIdentifier.class);
	
	private String scriptName = null;
	
	@Override
	public String identify(IFileWithFileFormat fff) {
		
		File conversionScript = new File(scriptName);
		
		File file = fff.toRegularFile();
		
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
			
			return "";
		}
		
		logger.debug("stdout from identification: "+pi.getStdOut());
		Set<String> fileFormatsIdentifiers= new HashSet<String>(new CommaSeparatedList(pi.getStdOut()).toList());

		return fileFormatsIdentifiers.iterator().next();
	}

	public String getScriptName() {
		return scriptName;
	}

	public void setScriptName(String scriptName) {
		this.scriptName = scriptName;
	}

}
