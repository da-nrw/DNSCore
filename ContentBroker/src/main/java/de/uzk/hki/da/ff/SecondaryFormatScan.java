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

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.irods.jargon.core.exception.InvalidArgumentException;

import de.uzk.hki.da.model.SubformatIdentificationPolicy;

/**
 * @author Daniel M. de Oliveira
 */
public class SecondaryFormatScan {

	List<SubformatIdentificationPolicy> secondStageScanPolicies = null;
	
	
	/**
	 * @throws IOException 
	 * @throws InvalidArgumentException if one of the files has no puid.
	 * @param files
	 * @return files, for easy mock testing.
	 * @throws 
	 */
	List<IFileWithFileFormat> identify(List<IFileWithFileFormat> files) throws InvalidArgumentException, IOException{

		
		for (IFileWithFileFormat f:files){
			if (f.getFormatPUID()==null||f.getFormatPUID().isEmpty())
				throw new InvalidArgumentException(f+" has no puid");
			
			for (SubformatIdentificationPolicy p:secondStageScanPolicies){
				if (f.getFormatPUID().equals(p.getPUID())){
					
					SecondaryFormatIdentifier fi = null;

					try {
						fi = getSFI(p.getFormatIdentifierScriptName());
					} catch (Exception e) {
						e.printStackTrace();
					}
					f.setFormatSecondaryAttribute(fi.identify(f.toRegularFile()));
						
						
					break;
				}
			}
		}
		
		return files;
	}

	void setSecondStageScanPolicies(
			List<SubformatIdentificationPolicy> secondStageScanPolicies) {
		this.secondStageScanPolicies = secondStageScanPolicies;
	}

	
	
	
	@SuppressWarnings("unchecked")
	private SecondaryFormatIdentifier getSFI(String className) throws ClassNotFoundException, SecurityException, NoSuchMethodException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException{
		SecondaryFormatIdentifier sfi=null;

		Class<SecondaryFormatIdentifier> c;
		c = (Class<SecondaryFormatIdentifier>) Class.forName(className);
		java.lang.reflect.Constructor<SecondaryFormatIdentifier> co = c.getConstructor();
		sfi= co.newInstance();

		return sfi;
	}
}
