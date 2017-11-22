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

package de.uzk.hki.da.cb;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.jdom.input.SAXBuilder;

import de.uzk.hki.da.action.AbstractAction;
import de.uzk.hki.da.core.UserException;
import de.uzk.hki.da.core.UserException.UserExceptionId;
import de.uzk.hki.da.metadata.MetadataStructure;
import de.uzk.hki.da.metadata.MetadataStructureFactory;
import de.uzk.hki.da.metadata.MetsLicense;
import de.uzk.hki.da.metadata.MetsParser;
import de.uzk.hki.da.model.DAFile;
import de.uzk.hki.da.model.Document;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.model.ObjectPremisXmlReader;
import de.uzk.hki.da.model.PublicationRight;
import de.uzk.hki.da.model.PublicationRight.Audience;
import de.uzk.hki.da.repository.RepositoryException;
import de.uzk.hki.da.utils.C;
import de.uzk.hki.da.utils.StringUtilities;
import de.uzk.hki.da.utils.XMLUtils;

/**
 * Detects the package type of an object and validates the metadata structure.
 * 
 * The package type can be of the four types
 * <ul>
 * <li>METS
 * <li>EAD
 * <li>XMP
 * <li>LIDO
 * </ul>
 * 
 * When package type XMP is detected, and no XMP.rdf has been found, a XMP.rdf
 * manifest gets generated an put to the temp representation.
 * 
 * @author Daniel M. de Oliveira
 */
public class ValidateMetadataAction extends AbstractAction {
	
	
	private static final String XMP_SIDECAR = "xmp";
	private static final String CB_PACKAGETYPE_NONE = "NONE";
	private static final String PACKAGE_TYPE_FOR_OBJECT_DETERMINDED = "Package type for object determinded: ";
	private String detectedPackageType = CB_PACKAGETYPE_NONE;
	private DAFile detectedMetadataFile;
	private boolean packageTypeInObjectWasSetBeforeRunningAction=false;
	MetadataStructureFactory msf = new MetadataStructureFactory();
	
	@Override
	public void checkConfiguration() {
	}
	

	@Override
	public void checkPreconditions() {
	}
	
	@Override
	public boolean implementation() throws FileNotFoundException, IOException,
			UserException, RepositoryException {
		
		detect();
		throwExceptionIfPackageTypeCollision();
		
		logger.info(PACKAGE_TYPE_FOR_OBJECT_DETERMINDED+detectedPackageType);
		if (detectedPackageType.equals(CB_PACKAGETYPE_NONE)){
			try {
				if( !canIgnoreLicenseValidation())
					checkLicenses();
			} catch (UserException e) {
				logger.debug("Fehler bei Lizenzauswertung: "+e.getMessage());
				throw e;
			}catch (Exception e) {
				logger.debug("Fehler bei Lizenzauswertung: "+e.getMessage());
				throw new UserException(UserExceptionId.INVALID_LICENSE_DATA,"Fehler bei Lizenzenauswertung: "+e,"Fehler bei Lizenzenauswertung");
			}
			return true;
		}
		
		o.setPackage_type(detectedPackageType);
		
		
		logger.debug("Validate package...");
		MetadataStructure ms = createMetadataStructure();
		if (!ms.isValid()){
			throw new UserException(UserExceptionId.INCONSISTENT_PACKAGE, 
					"Metadaten nicht konsistent. Metadatentyp: "+detectedPackageType);
		}
		
		o.setMetadata_file(detectedMetadataFile.getRelative_path());
		try {
			if( !canIgnoreLicenseValidation())
				checkLicenses();
		} catch (UserException e) {
			logger.debug("Fehler bei Lizenzauswertung: "+e.getMessage());
			throw e;
		}catch (Exception e) {
			logger.debug("Fehler bei Lizenzauswertung: "+e.getMessage());
			throw new UserException(UserExceptionId.INVALID_LICENSE_DATA,"Fehler bei Lizenzenauswertung: "+e,"Fehler bei Lizenzenauswertung");
		}
		return true;
	}
	
	
	private void checkLicenses() throws Exception {
		boolean wantPublication = false;
		boolean hasPremisLicense = false;
		boolean hasMetsLicense = false;
		boolean hasPublicMetsLicense = false;
		boolean usePublicMets = false;
		//validate premis
		Object premisObject = parsePremisToMetadata(wa.toFile(o.getLatest(C.PREMIS_XML)));
		for (PublicationRight pr : premisObject.getRights().getPublicationRights())
			if (pr.getAudience().equals(Audience.PUBLIC))
				wantPublication = true;

		if (!detectedPackageType.equals(C.CB_PACKAGETYPE_METS) && wantPublication)
			throw new UserException(UserExceptionId.INVALID_LICENSE_DATA,
					"Publikation ist nur mithilfe von METS-Metadaten erlaubt.","Publikation ist nur mithilfe von METS-Metadaten erlaubt.");

		if (premisObject.getRights().getPremisLicense() != null)
			hasPremisLicense = true;
		
		if(hasPremisLicense){
			try{
				new URL(premisObject.getRights().getPremisLicense().getHref());
			}catch(MalformedURLException e){
				throw new UserException(UserExceptionId.INVALID_LICENSE_DATA,
						"Invalide Lizenz: publicationLicense-Element in der Premis hat ein ungueltiges href-Attribut("+premisObject.getRights().getPremisLicense().getHref()+")",
						"Invalide Lizenz: publicationLicense-Element in der Premis hat ein ungueltiges href-Attribut("+premisObject.getRights().getPremisLicense().getHref()+")");
			}
		}

		//validate mets
		if (detectedPackageType.equals(C.CB_PACKAGETYPE_METS)){
			List<DAFile> metsFiles = getFilesOfMetadataType(C.SUBFORMAT_IDENTIFIER_METS);
			MetsLicense licenseMetsFile =null;
			MetsLicense licensePublicMetsFile = null;
			for (DAFile f : metsFiles) {//over all mets-files (max 2), amount checked by previous actions
				SAXBuilder builder = XMLUtils.createNonvalidatingSaxBuilder();
				MetsParser mp = new MetsParser(builder.build(wa.toFile(f).getAbsolutePath()));
				logger.debug("Check license in mets file: "+f.getRelative_path());
				if(f.getRelative_path().equalsIgnoreCase(C.PUBLIC_METS)){
					usePublicMets=true;
					licensePublicMetsFile=mp.getLicenseForWholeMets();
					hasPublicMetsLicense=(licensePublicMetsFile!=null);
				}else{
					try{
						licenseMetsFile=mp.getLicenseForWholeMets();
					}catch(Exception e){
						logger.error(e.getMessage());
						//bei public-mets-csn ist eine invalide Angabe der Lizenz in der export_mets akzeptabel
						if(!o.getContractor().isUsePublicMets())
							throw e;
					}
					hasMetsLicense=(licenseMetsFile!=null);
				}
			}
			
			if(hasPublicMetsLicense){
				try{
					new URL(licensePublicMetsFile.getHref());
				}catch(MalformedURLException e){
					throw new UserException(UserExceptionId.INVALID_LICENSE_DATA,
							"Invalide Lizenzangaben in "+C.PUBLIC_METS+": accessCondition-Element hat ein ungueltiges href-Attribut("+e.getMessage()+")",
							"Invalide Lizenzangaben in "+C.PUBLIC_METS+": accessCondition-Element hat ein ungueltiges href-Attribut("+licensePublicMetsFile.getHref()+")");
				}
			}else if(!hasPublicMetsLicense && hasMetsLicense){
				try{
					new URL(licenseMetsFile.getHref());
				}catch(MalformedURLException e){
					throw new UserException(UserExceptionId.INVALID_LICENSE_DATA,
							"Invalide Lizenzangaben in mets metadaten: accessCondition-Element hat ein ungueltiges href-Attribut("+e.getMessage()+")",
							"Invalide Lizenzangaben in mets metadaten: accessCondition-Element hat ein ungueltiges href-Attribut("+licenseMetsFile.getHref()+")");
				}
			}
			if ((licenseMetsFile!=null && !licenseMetsFile.equals(licensePublicMetsFile)) ||
					(licensePublicMetsFile!=null && !licensePublicMetsFile.equals(licenseMetsFile))) // mets and public mets are different
				logger.warn("Lizenzangaben in den METS-Metadaten sind unterschiedlich: e.g.:" + licenseMetsFile+" "	+ licensePublicMetsFile);
		}
		//check license compatibility
		logger.debug("Detected license information wantPublication:"+wantPublication+", hasPremisLicense:"+hasPremisLicense+", hasMetsLicense:"+hasMetsLicense+", usePublicMets:"+usePublicMets+", hasPublicMetsLicense:"+hasPublicMetsLicense);
		
		if(usePublicMets && wantPublication && !hasPublicMetsLicense)
			throw new UserException(UserExceptionId.INVALID_LICENSE_DATA,
					"Keine Lizenzangaben in der Public-METS-Metadatei vorhanden.",
					"Keine Lizenzangaben in der Public-METS-Metadatei vorhanden.");
		if (hasPremisLicense && (hasMetsLicense || hasPublicMetsLicense))
			throw new UserException(UserExceptionId.INVALID_LICENSE_DATA,
					"Lizenzangaben in den METS-Metadaten und in der Premis-Datei vorhanden.",
					"Lizenzangaben in den METS-Metadaten und in der Premis-Datei vorhanden.");

		if (wantPublication && !hasPremisLicense && !(hasMetsLicense || hasPublicMetsLicense))
			throw new UserException(UserExceptionId.INVALID_LICENSE_DATA,
					"Keine Lizenzangaben für eine Publikation vorhanden.",
					"Keine Lizenzangaben für eine Publikation vorhanden.");
		

		if(hasPremisLicense)
			o.setLicense_flag(C.LICENSEFLAG_PREMIS);
		else if(hasMetsLicense && !usePublicMets)
			o.setLicense_flag(C.LICENSEFLAG_METS);
		else if(hasPublicMetsLicense && o.getContractor().isUsePublicMets())
			o.setLicense_flag(C.LICENSEFLAG_PUBLIC_METS);
		else if(!hasPremisLicense && !hasMetsLicense && !hasPublicMetsLicense)
			o.setLicense_flag(C.LICENSEFLAG_NO_LICENSE);
		else
			throw new UserException(UserExceptionId.INVALID_LICENSE_DATA,"Invalide Lizenzangaben.","Invalide Lizenzangaben.");
		logger.debug("Object License_flag is setted to: "+o.getLicense_flag());
	}

	public static Object parsePremisToMetadata(File premis) throws IOException {
		Object o = null;

		try {
			o = new ObjectPremisXmlReader().deserialize(premis);
		} catch (Exception e) {
			// do not throw userexception here since ability to deserialize
			// should already have been checked in UnpackAction.
			throw new RuntimeException("Error while deserializing PREMIS", e);
		}
		return o;
	}

	private MetadataStructure createMetadataStructure() {
		MetadataStructure ms=null;
		try {
			List<Document> documents = o.getDocuments();
			ms = msf.create(wa.dataPath(),detectedPackageType, detectedMetadataFile.getPath().toFile(), documents);
		} catch (Exception e){
			throw new RuntimeException("problem occured during creation of metadata structure",e);
		}
		return ms;
	}
	
	/**
	 * if something else has been detected in a previous SIP.
	 */
	private void throwExceptionIfPackageTypeCollision() {
		if (StringUtilities.isSet(o.getPackage_type())){
			packageTypeInObjectWasSetBeforeRunningAction=true;
			if ((!detectedPackageType.equals(o.getPackage_type()))
					||(!detectedMetadataFile.getRelative_path().equals(o.getMetadata_file()))){
				throw new RuntimeException("COLLISION");
			}
		}
	}
	

	@Override
	public void rollback() throws Exception {
		if (!packageTypeInObjectWasSetBeforeRunningAction){
			o.setMetadata_file(null);
			o.setPackage_type(null);
		}
	}

	/**
	 * @throws UserException If more than one metadata file was found.
	 * @author Daniel M. de Oliveira 
	 */
	private void detect(){
		
		if (getFilesOfMetadataType(C.SUBFORMAT_IDENTIFIER_EAD).size()>=2){
			throw new UserException(UserExceptionId.DUPLICATE_METADATA_FILE,"Mehr als eine Metadatendatei vorhanden vom Typ: EAD");
		}
		if (getFilesOfMetadataType(C.SUBFORMAT_IDENTIFIER_LIDO).size()>1){
			throw new UserException(UserExceptionId.DUPLICATE_METADATA_FILE,"Mehr als eine Metadatendatei vorhanden vom Typ: LIDO");
		}

		int ptypeCount=0;
		
		List<DAFile> metaFiles = getFilesOfMetadataType(C.SUBFORMAT_IDENTIFIER_EAD);
		if (metaFiles.size() > 1){
			throw new UserException(UserExceptionId.DUPLICATE_METADATA_FILE,"Mehr als eine Metadatendatei vorhanden vom Typ: EAD");
		} else if (metaFiles.size() == 1){
			detectedMetadataFile=metaFiles.get(0);
			detectedPackageType=C.CB_PACKAGETYPE_EAD;
			ptypeCount++;
		} else {
			metaFiles = getFilesOfMetadataType(C.SUBFORMAT_IDENTIFIER_METS);		

			if (Boolean.TRUE.equals(o.getContractor().isUsePublicMets()) ){
				for (int iii=metaFiles.size()-1; iii>=0; iii--){
					DAFile metaFile = metaFiles.get(iii);

					if (metaFile.getRelative_path().equalsIgnoreCase(C.PUBLIC_METS)){
						metaFiles.remove(iii);
						break;
					}
				}
			}
			
			if (metaFiles.size() > 1) {
				throw new UserException(UserExceptionId.DUPLICATE_METADATA_FILE,"Mehr als eine Metadatendatei vorhanden vom Typ: METS");
			}  else if (metaFiles.size() == 1) {
				detectedMetadataFile=metaFiles.get(0);
				detectedPackageType=C.CB_PACKAGETYPE_METS;
				ptypeCount++;
			}  
		}  
				
		if ((getFilesOfMetadataType(C.SUBFORMAT_IDENTIFIER_LIDO)).size()==1){
			detectedMetadataFile=getFilesOfMetadataType(C.SUBFORMAT_IDENTIFIER_LIDO).get(0);
			detectedPackageType=C.CB_PACKAGETYPE_LIDO;
			ptypeCount++;
		}
		
		if (ptypeCount>1)
			throw new UserException(UserExceptionId.DUPLICATE_METADATA_FILE,"Mehr als eine Metadatendatei vorhanden vom Typ: METADATA");
	}
	
	
	/**
	 * Considers only files which are not located in subfolders.
	 * 
	 * @param metadataFormatIdentifier
	 * @return
	 */
	private List<DAFile> getFilesOfMetadataType(String metadataFormatIdentifier){
		List<DAFile> result = new ArrayList<DAFile>();
		for (DAFile f:o.getNewestFilesFromAllRepresentations(XMP_SIDECAR)){
			if (f.getRelative_path().contains(C.FS_SEPARATOR)) continue;
			
			if (metadataFormatIdentifier.equals(f.getSubformatIdentifier()))
				result.add(f);
		}
		return result;
	}
	
	
	public void setMsf(MetadataStructureFactory msf) {
		this.msf = msf;
	}
}
