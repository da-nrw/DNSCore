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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Daniel M. de Oliveira
 */
class SubformatScanService implements FormatScanService, Connector {

	private Map<String,Set<String>> subformatIdentificationPolicies = new HashMap<String,Set<String>>();
	
	
	/**
	 * @throws IOException 
	 * @throws IllegalStateException if one of the identifiers cannot get instantiated.
	 * @throws InvalidArgumentException if one of the files has no puid.
	 * @param files the subformatIdentifier gets set by this method.
	 * @return files the return value is for convenient mock testing only, since files gets modified as side effect.
	 * @throws 
	 */
	public List<FileWithFileFormat> identify(List<FileWithFileFormat> files) throws IOException{
		
		for (FileWithFileFormat f:files){
			if (f.getFormatPUID()==null||f.getFormatPUID().isEmpty())
				throw new IllegalArgumentException(f+" has no puid");
			
			f.setSubformatIdentifier(identifySubformat(f.toRegularFile(),f.getFormatPUID()));
		}
		return files;
	}


	private String identifySubformat(File f,String puid) throws IOException {
		
		for (String formatIdentifierClassName:subformatIdentificationPolicies.keySet()){
			
			// trigger
			if (subformatIdentificationPolicies.get(
					formatIdentifierClassName).contains(puid)){

				return createSFIInstance(formatIdentifierClassName).identify(f);
			}
		}
		return "";
	}
	
	

	void setSubformatIdentificationPolicies(
			 Map<String,Set<String>> subformatIdentificationPolicies) {
		
		// check if the classes can get instantiated
		for (String sfi:subformatIdentificationPolicies.keySet()) {
			try {
				createSFIInstance(sfi);
			}catch (RuntimeException e) {
				throw new IllegalArgumentException("Error while checking if all of the subformat identifiers can get instantiated",e);
			}
		}
			
		this.subformatIdentificationPolicies = subformatIdentificationPolicies;
		
	}

	
	
	
	@SuppressWarnings("unchecked")
	private FormatIdentificationStrategy createSFIInstance(String className) {
		try {
			FormatIdentificationStrategy sfi=null;
			Class<FormatIdentificationStrategy> c;
			c = (Class<FormatIdentificationStrategy>) Class.forName(className);
			java.lang.reflect.Constructor<FormatIdentificationStrategy> co = c.getConstructor();
			sfi= co.newInstance();
			return sfi;
		}catch(Exception e) {
			throw new RuntimeException("Error creating instance of subformat identifier",e);
		}
	}
	
	
	@Override
	public boolean isConnectable() {
		
		boolean passed=true;
		for (String s:subformatIdentificationPolicies.keySet()) {
			System.out.print("CONNECTIVITY CHECK - "+s+".healthCheck()");
			if (!((Connector)createSFIInstance(s)).isConnectable()) {
				System.out.println(" .... FAIL");
				passed=false;
			}
			else {
				System.out.println(" .... OK");
			}
		}
		return passed;
	}
}
