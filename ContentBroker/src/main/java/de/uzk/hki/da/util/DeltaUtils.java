/**
 * 
 */
package de.uzk.hki.da.util;

import java.io.File;
import java.util.List;

import org.hibernate.Session;

import de.uzk.hki.da.model.Copy;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.model.ObjectPremisXmlReader;
import de.uzk.hki.da.model.Package;
import de.uzk.hki.da.service.HibernateUtil;
import de.uzk.hki.da.utils.C;

/**
 * 
 */
public class DeltaUtils {
	
	/**
	 * 
	 * @param o: das Object aus der DB
	 * @return
	 */
	public static boolean checkPublished(Object o, File premisFile) {
		
		// Prüfen, ob das Delta auch publiziert werden darf
		
//		File premisFile = premisFile();
		Object premisObject = null;
		try {
			premisObject = new ObjectPremisXmlReader().deserialize(premisFile);
			if (premisObject == null)
				throw new Exception("Premis object must not be null after deserialization.");
		} catch (Exception e) {
			// Deserializing the PREMIS file is already checked in
			// unpack-action, where a
			// user error gets thrown. So we consider this here a
			// merely technical error.
			throw new RuntimeException("Couldn't deserialize: " + premisFile, e);
		}
		 if ( premisObject.getPublished_flag() != C.PUBLISHEDFLAG_PUBLIC) {
			 return false;
		 }
		
		return true;
	}
}
