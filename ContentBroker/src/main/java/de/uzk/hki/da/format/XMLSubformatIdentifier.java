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
import java.io.IOException;

import de.uzk.hki.da.utils.CommandLineConnector;
import de.uzk.hki.da.utils.XMLUtils;

/**
 * DNSCore supports four metadata structures that enable proper publication via the presentation repository. 
 * Each of these structures is based on a specific xml metadata format (EAD,METS,LIDO,XMP). 
 * This class provides a method to test xml files if they conform to one of these metadata format standards.  
 * 
 * @author Daniel M. de Oliveira
 */
public class XMLSubformatIdentifier implements FormatIdentifier, Connector{
	
	/**
	 * @param f
	 * @return
	 * @throws IOException 
	 */
	@Override
	public String identify(File f,boolean pruneExceptions) throws IOException{
		return XMLUtils.identifyMetadataType(f);
	}

	@Override
	public boolean isConnectable() {
		return true; // no external connectors used.
	}

	@Override
	public void setCliConnector(CommandLineConnector cli) {

		
	}

	@Override
	public CommandLineConnector getCliConnector() {
		return null;
	}
}
