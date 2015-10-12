/*
  DA-NRW Software Suite | ContentBroker
  Copyright (C) 2014 Historisch-Kulturwissenschaftliche Informationsverarbeitung
  Universität zu Köln
  Copyright (C) 2015 LVR-Infokom
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

package de.uzk.hki.da.cb;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.jdom.JDOMException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uzk.hki.da.action.AbstractAction;
import de.uzk.hki.da.core.PreconditionsNotMetException;
import de.uzk.hki.da.metadata.MetsMetadataStructure;
import de.uzk.hki.da.model.Document;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.model.ObjectPremisXmlReader;
import de.uzk.hki.da.utils.C;
import de.uzk.hki.da.utils.Path;
import de.uzk.hki.da.utils.StringUtilities;


/**
 * Checks if the PREMIS file or mets file delivered with the SIP contains URN information.
 * Otherwise the previously created URN (which can be derived from the object identifier) is used.
 *  
 * @author Thomas Kleinke
 * @author Daniel M. de Oliveira
 */
public class RegisterURNAction extends AbstractAction {
	
	private static final Logger logger = LoggerFactory.getLogger(RegisterURNAction.class);
	private static final String PREMIS_XML = "premis.xml";
	
	
	@Override
	public void checkConfiguration() {
	}
	

	@Override
	public void checkPreconditions() {
		if (o.getLatest(PREMIS_XML)==null) throw new PreconditionsNotMetException("Must be set: "+PREMIS_XML);
		if (! premisFile().exists()) throw new PreconditionsNotMetException("Must exist: "+PREMIS_XML); 
	}
	
	
	// TODO When implementing METS based URN Extraction, use METSRightSectionXMLReader.java as a starting point
	
	@Override
	public boolean implementation() {
		
		if (o.isDelta()) {
			logger.info("Retaining previous object URN: " + o.getUrn());
			return true;
		}
		
		String premisUrn = extractURNFromPremisFile(premisFile());
		if (StringUtilities.isSet(premisUrn)) {
			o.setUrn(premisUrn);
			logger.info("New user-supplied object URN in premis");
			return true;
		}
		
		String metsUrn = null;
		if(o.getPackage_type()!=null && o.getPackage_type().equals(C.CB_PACKAGETYPE_METS)) {
			logger.debug("Package type: METS. Try to read urn ...");
			File metsFile = Path.makeFile(o.getLatest(o.getMetadata_file()).getRelative_path());
			metsUrn = extractURNFromMetsFile(metsFile);
			if(StringUtilities.isSet(metsUrn)) {
				o.setUrn(metsUrn);
				logger.info("New user-supplied object URN in mets");
				return true;
			}
		}
		
		String urn = preservationSystem.getUrnNameSpace() + "-" + o.getIdentifier();
		logger.info("New system-generated object URN: " + urn);
		o.setUrn(urn);
		return true;
	}


	@Override
	public void rollback() {
		if (!o.isDelta())
			o.setUrn(null);
	}


	/**
	 * @author Thomas Kleinke
	 * @return URN if the SIP premis file contains an URN; otherwise null
	 */
	private static final String extractURNFromPremisFile(File premisFile) {
		
		Object premisObject = null;
		try {
			premisObject = new ObjectPremisXmlReader().deserialize(premisFile);
			if (premisObject == null) throw new Exception("Premis object must not be null after deserialization.");
		} catch (Exception e) {
			// Deserializing the PREMIS file is already checked in unpack-action, where a 
			// user error gets thrown. So we consider this here a 
			// merely technical error.
			throw new RuntimeException("Couldn't deserialize: " + premisFile, e);
		}
		return premisObject.getUrn();
	}
	
	/**
	 * @author Polina Gubaidullina
	 * @return URN if the SIP mets file contains an URN; otherwise null
	 */
	
	private final String extractURNFromMetsFile(File metsFile) {
		String urn = null;
		List<Document> documents = o.getDocuments();
		try {
			Path path = Path.make(wa.dataPath(), o.getLatest(o.getMetadata_file()).getRep_name());
			MetsMetadataStructure mms = new MetsMetadataStructure(path, metsFile, documents);
			urn = mms.getUrn();
			logger.debug("Found urn in mets: "+urn);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return urn;
	}
	
	

	private File premisFile() {  
		return wa.toFile(o.getLatest(PREMIS_XML));
	}
	

}
