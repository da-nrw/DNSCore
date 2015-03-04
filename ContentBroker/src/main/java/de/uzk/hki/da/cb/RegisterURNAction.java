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

import org.apache.commons.lang.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uzk.hki.da.action.AbstractAction;
import de.uzk.hki.da.core.PreconditionsNotMetException;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.model.ObjectPremisXmlReader;


/**
 * Checks if the premis file or mets file delivered with the SIP contains URN information.
 * Otherwise the previously created URN (which can be derived from the object identifier) is used.
 *  
 * @author Thomas Kleinke
 */
public class RegisterURNAction extends AbstractAction {
	
	private static final Logger logger = LoggerFactory.getLogger(RegisterURNAction.class);
	private static final String PREMIS_XML = "premis.xml";
	
	
	@Override
	public void checkConfiguration() {
	}
	

	@Override
	public void checkPreconditions() {
		if (o.getLatest(PREMIS_XML)==null) throw new PreconditionsNotMetException("Must be set: premis.xml");
		if (! premisFile().exists()) throw new PreconditionsNotMetException("Must exist: premis.xml"); 
	}
	
	
	// TODO When implementing METS Urn Extraction, use METSRightSectionXMLReader.java as a starting point
	
	@Override
	public boolean implementation() {
		
		if (o.isDelta())
			logger.info("Object URN: " + o.getUrn());
		else {
			String urn;
	
			String premisUrn = extractURNFromPremisFile();
			if (premisUrn != null)
				urn = premisUrn;
			else {				
				urn = preservationSystem.getUrnNameSpace() + "-" + o.getIdentifier();
			}
			
			logger.info("Object URN: " + urn);
			o.setUrn(urn);
		}	
		
		return true;
	}


	@Override
	public void rollback() {
		throw new NotImplementedException("No rollback implemented for this action");
	}


	/**
	 * @author Thomas Kleinke
	 * @return URN if the SIP premis file contains an URN; otherwise null
	 */
	private String extractURNFromPremisFile() {
		
		Object premisObject = null;
		ObjectPremisXmlReader reader = new ObjectPremisXmlReader();
		try {
			premisObject = reader.deserialize(premisFile());
		} catch (Exception e) {
			// Deserializing the PREMIS file is already checked in unpack-action, where a 
			// user error gets thrown. So we consider this here a 
			// merely technical error.
			throw new RuntimeException("Couldn't deserialize premis file " + premisFile(), e);
		}
		
		String urn = null;
		if (premisObject != null)
			urn = premisObject.getUrn();
		
		if (urn != null && urn.equals(""))
			urn = null;
		
		return urn;
	}
	
	

	private File premisFile() {  
		return o.getLatest(PREMIS_XML).toRegularFile();
	}
	

}
