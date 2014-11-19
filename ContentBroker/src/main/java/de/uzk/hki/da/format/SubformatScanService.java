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

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.irods.jargon.core.exception.InvalidArgumentException;

/**
 * @author Daniel M. de Oliveira
 */
class SubformatScanService implements FormatScanService {

	private Map<String,List<String>> subformatIdentificationPolicies = new HashMap<String,List<String>>();
	
	
	/**
	 * @throws IOException 
	 * @throws InvalidArgumentException if one of the files has no puid.
	 * @param files
	 * @return files, for easy mock testing.
	 * @throws 
	 */
	public List<FileWithFileFormat> identify(List<FileWithFileFormat> files) throws InvalidArgumentException, IOException{
		
		for (FileWithFileFormat f:files){
			if (f.getFormatPUID()==null||f.getFormatPUID().isEmpty())
				throw new InvalidArgumentException(f+" has no puid");
			
			for (String formatIdentifierClassName:subformatIdentificationPolicies.keySet()){
				
				if (subformatIdentificationPolicies.get(formatIdentifierClassName).contains(f.getFormatPUID())){
					
					FormatIdentifier fi = null;
					
					try {
						fi = getSFI(formatIdentifierClassName);
					} catch (Exception e) {
						e.printStackTrace();
					}
					f.setSubformatIdentifier(fi.identify(f.toRegularFile()));
					
					
					break;
				}
			}
		}
		
		return files;
	}

	void setSubformatIdentificationPolicies(
			 Map<String,List<String>> subformatIdentificationPolicies) {
		this.subformatIdentificationPolicies = subformatIdentificationPolicies;
	}

	
	
	
	@SuppressWarnings("unchecked")
	private FormatIdentifier getSFI(String className) throws ClassNotFoundException, SecurityException, NoSuchMethodException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException{
		FormatIdentifier sfi=null;

		Class<FormatIdentifier> c;
		c = (Class<FormatIdentifier>) Class.forName(className);
		java.lang.reflect.Constructor<FormatIdentifier> co = c.getConstructor();
		sfi= co.newInstance();

		return sfi;
	}
}
