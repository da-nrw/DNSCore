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

package de.uzk.hki.da.cb;

import java.io.File;
import java.util.List;

import org.apache.commons.lang.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uzk.hki.da.action.AbstractAction;
import de.uzk.hki.da.core.ConfigurationException;
import de.uzk.hki.da.core.UserException;
import de.uzk.hki.da.core.UserException.UserExceptionId;
import de.uzk.hki.da.ff.FFConstants;
import de.uzk.hki.da.model.DAFile;
import de.uzk.hki.da.model.RightsSectionURNMetsXmlReader;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.model.ObjectPremisXmlReader;


/**
 * Checks if the premis file or mets file delivered with the SIP contains URN information.
 * Otherwise the previously created URN (which can be derived from the object identifier) is used.
 *  
 * @author Thomas Kleinke
 */
public class RegisterURNAction extends AbstractAction {
	
	static final Logger logger = LoggerFactory.getLogger(RegisterURNAction.class);
	
	/**
	 * @author Thomas Kleinke
	 * @return URN if the SIP premis file contains an URN; otherwise null
	 */
	private String extractURNFromPremisFile() {
		
//		File premisFile = new File(object.getDataPath() + "/"+ object.getNameOfNewestRep() + "/" + "premis.xml");
		File premisFile = object.getLatest("premis.xml").toRegularFile();
		
		
		Object premisObject = null;
		ObjectPremisXmlReader reader = new ObjectPremisXmlReader();
		try {
			premisObject = reader.deserialize(premisFile);
		} catch (Exception e) {
			throw new UserException(UserExceptionId.READ_SIP_PREMIS_ERROR,
					"Couldn't deserialize premis file " + premisFile.getAbsolutePath(), e);
		}
		
		String urn = null;
		if (premisObject != null)
			urn = premisObject.getUrn();
		
		if (urn != null && urn.equals(""))
			urn = null;
		
		return urn;
	}
	
	/**
	 * @author Thomas Kleinke
	 * @return URN if the mets file contains an URN; otherwise null
	 */
	private String extractURNFromMetsFile() {
		
		DAFile metsFile = null;		
		
		List<DAFile> files = object.getLatestPackage().getFiles();
		for (DAFile file : files) {
			if(!file.getRelative_path().contains("XMP.rdf")) {
				if (file.getFormatPUID().equals(FFConstants.METS_PUID))
				metsFile = file;
			}
			
		}

		if (metsFile != null) {
			String urn = null;
			RightsSectionURNMetsXmlReader metsUrnReader = new RightsSectionURNMetsXmlReader();
			try {
				urn = metsUrnReader.readURN(metsFile.toRegularFile());
			} catch (Exception e) {
				Exception causeEx = (Exception) e.getCause();
				while (causeEx.getCause() != null) {
					causeEx = (Exception) causeEx.getCause();					
				};
				throw new UserException(UserExceptionId.READ_METS_ERROR, "Failed to read URN from mets file " +
					metsFile.toRegularFile().getAbsolutePath(), causeEx.getMessage(), e);
			}
			
			return urn;
		}

		return null;
	}
	

	@Override
	public void checkActionSpecificConfiguration() throws ConfigurationException {
		// Auto-generated method stub
	}

	@Override
	public void checkSystemStatePreconditions() throws IllegalStateException {
		if (preservationSystem.getUrnNameSpace()==null) throw new IllegalStateException("URN NameSpace parameter not set!");
	}

	@Override
	public boolean implementation() {
		
		if (object.isDelta())
			logger.info("Object URN: " + object.getUrn());
		else {
			String urn;
	
			String premisUrn = extractURNFromPremisFile();
			if (premisUrn != null)
				urn = premisUrn;
			else {				
				String metsUrn = extractURNFromMetsFile();
				
				if (metsUrn != null)
					urn = metsUrn;
				else				
					urn = preservationSystem.getUrnNameSpace() + "-" + object.getIdentifier();
			}
			
			logger.info("Object URN: " + urn);
			object.setUrn(urn);
		}	
		
		return true;
	}

	@Override
	public void rollback() {
		throw new NotImplementedException("No rollback implemented for this action");
	}
}
