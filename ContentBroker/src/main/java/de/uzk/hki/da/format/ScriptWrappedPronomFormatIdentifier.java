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
 * Connects to an external application to determine the PUID of files.
 * 
 * @author Daniel M. de Oliveira
 */
public class ScriptWrappedPronomFormatIdentifier implements FormatIdentificationStrategy {

	private static final Logger logger = LoggerFactory.getLogger(ScriptWrappedPronomFormatIdentifier.class);

	private File conversionScript = null;
	
	ScriptWrappedPronomFormatIdentifier(File cs){
		this.conversionScript=cs;
	}
	
	/**
	 * Lets the fido program determine a set of pronom identifiers, 
	 * from which the last one is chosen and returned.
	 * 
	 * @param fff
	 * @return PUID or UNDEFINED if fido cannot determine the file format. 
	 */
	@Override
	public
	String identify(File file){
		if (!conversionScript.exists()) throw new IllegalStateException(
				"ConversionScript doesn't exist: "+conversionScript.getAbsolutePath());
		
		
		if (!file.exists()) throw new Error("File doesn't exist");

		
		ProcessInformation pi = CommandLineConnector.runCmdSynchronously( new String[]{
				
				conversionScript.getAbsolutePath(),
				file.getAbsolutePath()
		});
		
		
		if (pi.getExitValue()!=0){
			logger.warn("stdout from identification: "+pi.getStdErr());
			logger.warn("FormatIdentifier with exit value: " + pi.getExitValue());
			
			return "UNDEFINED";
		}
		
		logger.debug("stdout from identification: "+pi.getStdOut());
		Set<String> fileFormatsIdentifiers= new HashSet<String>(new CommaSeparatedList(pi.getStdOut()).toList());
		
		String result="";
		for (String r:fileFormatsIdentifiers){
			result=r;
		}
		return result;
	}

	@Override
	public boolean healthCheck() {
		// TODO Auto-generated method stub
		return false;
	}
}