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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.xml.sax.SAXException;

import de.uzk.hki.da.action.AbstractAction;
import de.uzk.hki.da.core.MailContents;
import de.uzk.hki.da.format.MimeTypeDetectionService;
import de.uzk.hki.da.metadata.EadMetsMetadataStructure;
import de.uzk.hki.da.metadata.LidoMetadataStructure;
import de.uzk.hki.da.metadata.MetadataStructure;
import de.uzk.hki.da.metadata.MetsMetadataStructure;
import de.uzk.hki.da.model.DAFile;
import de.uzk.hki.da.model.Event;
import de.uzk.hki.da.model.ObjectPremisXmlReader;
import de.uzk.hki.da.model.Package;
import de.uzk.hki.da.model.PremisLicense;
import de.uzk.hki.da.model.WorkArea;
import de.uzk.hki.da.util.ConfigurationException;
import de.uzk.hki.da.util.FileIdGenerator;
import de.uzk.hki.da.utils.C;
import de.uzk.hki.da.utils.FolderUtils;
import de.uzk.hki.da.utils.FriendlyFilesUtils;
import de.uzk.hki.da.utils.Path;
import de.uzk.hki.da.utils.RelativePath;
import de.uzk.hki.da.utils.XMLUtils;

/**
 * Performs updates to metadata files that are necessary
 * to keep the metadata, especially the paths to referenced
 * files, in sync with the actual package content after
 * conversion actions took place.
 * 
 * Also the transformation to Dublin Core takes place here.
 * 
 * Special actions are taken for XMP and EAD metadata.
 * 
 * @author Polina Gubaidullina
 * @author Sebastian Cuy
 * @author Daniel M. de Oliveira
 *
 *
 */
public class UpdateMetadataAction extends AbstractAction {

	/** The namespaces. */
	private Map<String,String> namespaces;
	/** The xpaths to urls. */
	private Map<String,String> xpathsToUrls = new HashMap<String,String>();
		
	private String[] repNames;	
	private boolean presMode=false;
	private MimeTypeDetectionService mtds = new MimeTypeDetectionService();
	private String absUrlPrefix = "";
	private File metadataFile;
	private HashMap<String, Integer> fileName_convertRewritingCount;
	private Map<DAFile,DAFile> unreferencedConvertedFiles;
	
	@Override
	public void checkConfiguration() {
	}
	

	@Override
	public void checkPreconditions() {
	}

	@Override
	public boolean implementation() throws IOException, JDOMException, ParserConfigurationException, SAXException, NullPointerException {
		
		logger.debug("UpdateMetadataAction ...");
		
		if (this.presMode) {
			if (Boolean.TRUE.equals(o.getContractor().isUsePublicMets())) {
				DAFile srcMetadataFile = o.getLatest(C.PUBLIC_METS);
				if (srcMetadataFile == null){
					logger.debug("Use Public METS: " + C.PUBLIC_METS + " not found. No publication.");
					j.setStatic_nondisclosure_limit(null);
					j.setStatic_nondisclosure_limit_institution(null);
					return true;
				}
				logger.debug("Use Public METS: " + C.PUBLIC_METS);
				copyMetadataFileToNewReps(o.getPackage_type(), C.PUBLIC_METS);
				return true;
			}
		}

		logger.debug("Package type: "+o.getPackage_type());
		logger.debug("Metadata file: "+o.getMetadata_file());
		
		updateAbsUrlPrefix();
		updateRepNames();
		
		this.setMtds(mtds);
		
		String packageType = o.getPackage_type();
		String metadataFileName = o.getMetadata_file();
//		check object & job settings
		if (j==null) throw new ConfigurationException("job");
		if (packageType == null || metadataFileName == null) {
			logger.warn("Could not determine package type. No metadata to update.");
			return true;
		}
		
		logger.debug("Got data from ACS - package_type: {}, metadata_file: {}", packageType, metadataFileName);
		logConvertEventsOnDebugLevel();
		
		List<de.uzk.hki.da.model.Document> documents = o.getDocuments();
			
		if(!"XMP".equals(packageType)) {
			
			String mfPathSrc = o.getLatest(metadataFileName).getPath().toString();
			
			metadataFileName = copyMetadataFileToNewReps(packageType, metadataFileName);
			
			for (String repName : getRepNames()) {
				
				logger.debug("Update path in "+repName+".");
				Map<DAFile,DAFile> replacements = generateReplacementsMap(o.getLatestPackage(), repName, absUrlPrefix);
				unreferencedConvertedFiles = replacements;
				
				if(representationExists(repName)) {
					metadataFile = new RelativePath(repName,metadataFileName).toFile();
					
	                if (!Path.makeFile(wa.dataPath(),metadataFile.getPath()).exists()) throw new FileNotFoundException();
	                
	                logger.debug("Metadata file: "+metadataFile.getAbsolutePath());
	                fileName_convertRewritingCount = new HashMap<String, Integer>();
	                
					if("EAD".equals(packageType)) {
						EadMetsMetadataStructure srcEmms;
						srcEmms = new EadMetsMetadataStructure(wa.dataPath(), new File(mfPathSrc), documents);
						copyMetsFiles(srcEmms, repName);
						EadMetsMetadataStructure emms = new EadMetsMetadataStructure(wa.dataPath(),metadataFile, documents);
						updatePathsInEad(emms, repName, replacements, this.o.getFriendlyFileExtensions());
					} else if ("METS".equals(packageType) && (!replacements.isEmpty() && replacements!=null)) {
						MetsMetadataStructure mms = new MetsMetadataStructure(wa.dataPath(),metadataFile, documents);
						updatePathsInMets(mms, metadataFile, replacements, this.o.getFriendlyFileExtensions());
					} else if("LIDO".equals(packageType) && (!replacements.isEmpty() && replacements!=null)) {
						LidoMetadataStructure lms = new LidoMetadataStructure(wa.dataPath(),metadataFile, documents);
						updatePathsInLido(lms, replacements, this.o.getFriendlyFileExtensions());
					}
				}
				
				if(presMode && "METS".equals(packageType)&&o.getLicense_flag()==C.LICENSEFLAG_PREMIS){//append accessCondition-Element to PIP-Mets 
					logger.debug("Insert License from Premis to Metadata file ");	
					PremisLicense pLicense;
						try {
							MetsMetadataStructure mms = new MetsMetadataStructure(wa.dataPath(),metadataFile, documents);
							pLicense = (new ObjectPremisXmlReader()).deserialize(wa.toFile(o.getLatest(C.PREMIS_XML))).getRights().getPremisLicense();
							logger.debug("Recognized License from Premis: "+pLicense);
							mms.appendAccessCondition(metadataFile, pLicense.getHref(), pLicense.getDisplayLabel(), pLicense.getText());
						} catch (ParseException  e) {
							logger.error("Exception: "+e);
							e.printStackTrace();
							throw new IOException(e);
						}catch (IllegalArgumentException e) {
							logger.error("Exception: "+e);
							e.printStackTrace();
							throw new IOException(e);
						}
						
					
				}
				
				if(!replacements.isEmpty() && replacements!=null) {
					for(String sourceHref : fileName_convertRewritingCount.keySet()) {
						logger.debug((Integer)fileName_convertRewritingCount.get(sourceHref)+" convert replacements for "+sourceHref);
					}
					
					int actualReplacements = fileName_convertRewritingCount.size();
					logger.debug("Successfully replaced references for "+actualReplacements+" files!");
					
					if(!unreferencedConvertedFiles.isEmpty() || unreferencedConvertedFiles!=null) {
						List<String> missingReferences = new ArrayList<String>();
						for(DAFile sourceFile : unreferencedConvertedFiles.keySet()) {
							missingReferences.add(sourceFile.getRelative_path());
						}
						logger.error(missingReferences.size()+" unreferenced file(s) have been converted! Missing reference(s) to "+missingReferences+
 								". Executed conversions: "+unreferencedConvertedFiles);
						new MailContents(preservationSystem,n).missingReferences(o, missingReferences);
					}
				}
			}
		}
		
		return true;
	}

	
	private void updatePathsInEad(EadMetsMetadataStructure emms, String repName, Map<DAFile,DAFile> replacements, String friendlyExts) throws JDOMException, IOException {

		logger.info("Update paths in EAD file "+emms.getMetadataFile().getAbsolutePath());

		HashMap<String, String> eadReplacements = new HashMap<String, String>();
		List<String> eadRefs = emms.getMetsRefsInEad();
		for (Event e:o.getLatestPackage().getEvents()) {
			if(e.getType().equals("COPY") || e.getType().equals("CONVERT")) {
				DAFile sourceFile = e.getSource_file();
				for(String href : eadRefs) {
					File file = XMLUtils.getRelativeFileFromReference(href, emms.getMetadataFile());					
					if(file.getAbsolutePath().contains(File.separator+sourceFile.getRelative_path())) {
						DAFile targetDAFile = e.getTarget_file();
						File targetFile = wa.toFile(targetDAFile);
						String targetPath = href.replace(file.getName(), targetFile.getName());
						String targetValue;
						if(!isPresMode()) {
							targetValue = targetPath;
						} else {
							String newRelPath = FileIdGenerator.getFileId(targetDAFile.getRelative_path());
							targetValue = preservationSystem.getUrisFile() + File.separator + o.getIdentifier() + File.separator + newRelPath;
						}
						eadReplacements.put(href, targetValue);
					}
				}
			}
		}		
		emms.replaceMetsRefsInEad(metadataFile, eadReplacements);
		if(!replacements.isEmpty() && replacements!=null) {
			updatePathsInEADMetsFiles(emms, repName, replacements, friendlyExts);
		}
	}
	
	private void copyMetsFiles(EadMetsMetadataStructure emms, String repName) throws IOException{
		
		List<String> metse = emms.getMetsRefsInEad();
		
		for (int mmm = 0; mmm < metse.size(); mmm++) {
			String mets = metse.get(mmm);
			String normMets = FilenameUtils.normalize(mets);
			if (normMets != null){
				mets = normMets; 
			}
			
			File srcFile = new File(wa.dataPath() + "/" + o.getLatest(mets).getPath());
			File dstFile = new File(wa.dataPath() + "/" + repName + "/" + mets);
			FileUtils.copyFile(srcFile, dstFile);

			DAFile dstDaFile = new DAFile(repName, mets);
			DAFile srcDaFile = o.getLatest(mets);
			dstDaFile.setFormatPUID(srcDaFile.getFormatPUID());
			o.getLatestPackage().getFiles().add(dstDaFile);
			
			Event e = new Event();
			e.setSource_file(srcDaFile);
			e.setTarget_file(dstDaFile);
			e.setType("COPY");
			e.setDate(new Date());
			e.setAgent_type("NODE");
			e.setAgent_name(n.getName());							
			o.getLatestPackage().getEvents().add(e);
			
			logger.debug("Copied metadata file \"{}\" to \"{}\"", srcDaFile.toString(), dstDaFile);
		}
	}
	
	private void updatePathsInEADMetsFiles(EadMetsMetadataStructure emms, String repName, Map<DAFile,DAFile> replacements, String friendlyExts) 
			throws IOException, JDOMException {
		List<MetsMetadataStructure> mmsList = emms.getMetsMetadataStructures();
		for (MetsMetadataStructure mms : mmsList) {
			logger.info("Update paths in METS file "+mms.getMetadataFile().getAbsolutePath());
			updatePathsInMets(mms, mms.getMetadataFile(), replacements, friendlyExts);
		}
	}
	
	
	@SuppressWarnings("rawtypes")
	private void updatePathsInMets(MetsMetadataStructure mms, File metsFile, Map<DAFile,DAFile> replacements, String friendlyExts) 
			throws IOException, JDOMException {
		String targetPath = "";
		List<Element> metsFileElemens = mms.getFileElements();
		for(Element metsFileElement : metsFileElemens) {
			String href = mms.getHref(metsFileElement);
			logger.info("Reference: "+href);
			DAFile targetDAFile = null;
			File file = XMLUtils.getRelativeFileFromReference(href, metsFile);
			Boolean fileExists = false;
			String mimetype = "";
			Iterator it = replacements.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry entry = (Map.Entry)it.next();
				DAFile sourceFile = (DAFile)entry.getKey();
				if(file.getAbsolutePath().contains(File.separator+sourceFile.getRelative_path())) {
					fileExists = true;
					targetDAFile = (DAFile)entry.getValue();
					logger.info("DAFile "+sourceFile+" has been converted to "+targetDAFile+"! Rewrite the reference ...");
					mimetype = targetDAFile.getMimeType();
					targetPath = href.replace(file.getName(), wa.toFile(targetDAFile).getName());
					addRefToFileNameConvertRewritingCountMap(href);
					if(unreferencedConvertedFiles.get(sourceFile)!=null) {
						unreferencedConvertedFiles.remove(sourceFile);
					}
					break;
				} 
			}
			if(!fileExists && o.isDelta() && !isPresMode()) {
				List<String> referenceWithMimetype = getCorrReferencesAndMimetypeInDelta(mms, href, friendlyExts);
				if(!referenceWithMimetype.isEmpty()&&referenceWithMimetype!=null) {
					targetPath = referenceWithMimetype.get(0);
					mimetype = referenceWithMimetype.get(1);
					fileExists = true;
				}
			}
			if(fileExists) {
				String targetValue;
				String loctype = null;
				if(!isPresMode()) {
					targetValue = targetPath;
				} else {
					targetValue = preservationSystem.getUrisFile() + File.separator + o.getIdentifier() + File.separator + targetDAFile.getRelative_path();
					loctype = "URL";
				}
				logger.debug("New mets replacement: "+href+" by "+targetValue);
				mms.makeReplacementsHrefInMetsFile(metsFile, href, targetValue, mimetype, loctype);
			} else {
				logger.error("No dafile found for href "+href);
			}
		}
	}
	
	@SuppressWarnings("rawtypes")
	private void updatePathsInLido(LidoMetadataStructure lms, Map<DAFile,DAFile> replacements, String friendlyExts) 
			throws IOException {
		logger.info("Update paths in LIDO file "+lms.getMetadataFile().getAbsolutePath());
		HashMap<String, String> lidoReplacements = new HashMap<String, String>();
		List<String> lidoRefs = lms.getReferences();
		String targetPath = "";
		for(String href : lidoRefs) {
			logger.debug("Reference: "+href);
			File file = XMLUtils.getRelativeFileFromReference(href, lms.getMetadataFile());
			DAFile targetDAFile = null;
			Boolean fileExists = false;
			Iterator it = replacements.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry entry = (Map.Entry)it.next();
		        DAFile sourceFile = (DAFile)entry.getKey();
				if(file.getAbsolutePath().contains(File.separator+sourceFile.getRelative_path())) {
					fileExists = true;
					targetDAFile = (DAFile)entry.getValue();
					logger.info("DAFile "+sourceFile+" has been converted to "+targetDAFile+"! Rewrite the reference ...");
					targetPath = href.replace(file.getName(), wa.toFile(targetDAFile).getName());
					addRefToFileNameConvertRewritingCountMap(href);
					if(unreferencedConvertedFiles.get(sourceFile)!=null) {
						unreferencedConvertedFiles.remove(sourceFile);
					}
					break;
				}
			}
			if(!fileExists && o.isDelta() && !isPresMode()) {
				List<String> references = getCorrReferencesAndMimetypeInDelta(lms, href, friendlyExts);
				if(!references.isEmpty() && references.get(0)!=null) {
					targetPath = references.get(0);
					fileExists = true;
				}
			}
			if(fileExists) {
				String targetValue;
				if(!isPresMode()) {
					targetValue = targetPath;
				} else {
					targetValue = preservationSystem.getUrisFile() + File.separator + o.getIdentifier() + File.separator + targetDAFile.getRelative_path();
				}
				logger.debug("New lido replacement: "+href+" by "+targetValue);
				lidoReplacements.put(href, targetValue);
			}
		}
		lms.replaceRefResources(lidoReplacements);
	}
	
	private void addRefToFileNameConvertRewritingCountMap (String href) {
		int count = 0;
		if(fileName_convertRewritingCount.get(href)!=null) {
			count = fileName_convertRewritingCount.get(href);
		} 
		fileName_convertRewritingCount.put(href, count+1);
	}
	
	private List<String> getCorrReferencesAndMimetypeInDelta(MetadataStructure ms, String href, String friendlyExts) {
		logger.debug("File not found. Completing reference "+href+" in delta ...");
		List<String> reference = new ArrayList<String>();
		DAFile sourceFile = ms.getReferencedDafile(ms.getMetadataFile(), href, o.getDocuments());
		String sourceFileName = wa.toFile(sourceFile).getName();
		if (FriendlyFilesUtils.isFriendlyFile(sourceFileName, friendlyExts)){
			return reference;
		}
		
		List<DAFile> newestFiles = o.getNewestFilesFromAllRepresentations(friendlyExts);
		for(DAFile dafile : newestFiles) {
			if (!FriendlyFilesUtils.isFriendlyFile(dafile.getRelative_path(), friendlyExts)
			 && FilenameUtils.getBaseName(wa.toFile(dafile).getName()).equals(FilenameUtils.getBaseName(sourceFileName))
			 &&(!(wa.toFile(dafile).getName().equals(sourceFileName)))) {
				logger.debug("calculated reference "+dafile.getRelative_path());
				reference.add(dafile.getRelative_path());
				String mimetype = "";
				try {
					mimetype = mtds.identify(wa.toFile(dafile),false);
				} catch (IOException e) {
					e.printStackTrace();
				}
				reference.add(mimetype);
				break;
			}
		}
		return reference;
	}
	
	private Map<DAFile,DAFile> generateReplacementsMap(Package pkg,String repName,String absUrlPrefix) throws IOException{
		
		logger.debug("Generate replacements");
		//  relativePath,DAFile
		//  Mimetype, DAFile
		Map<DAFile,DAFile> replacements = new HashMap<DAFile,DAFile>();
		// collect paths to be replaced in map
		for (Event e:pkg.getEvents()) {
			logger.debug("Event type: "+e.getType());
			if (!"CONVERT".equals(e.getType())) {
				continue;
			} 
			DAFile targetFile = e.getTarget_file();
			DAFile sourceFile = e.getSource_file();
			if (!targetFile.getRep_name().equals(repName)) {
				continue;
			}
			targetFile.setMimeType(getMtds().identify(wa.toFile(targetFile),false));
			replacements.put(sourceFile, targetFile);
		}
		logger.info("Planned replacements: {}", replacements);
		return replacements;
	}
	
	/**
	 * @param packageType
	 * @param metadataFileName
	 * @return
	 * @throws IOException
	 */
	private String copyMetadataFileToNewReps(String packageType,
			String metadataFileName) throws IOException {
		// copy other metadata to rep(s)
		
		DAFile srcMetadataFile = o.getLatest(metadataFileName);
		
		for (String repName : getRepNames()) {
			// rename metadatafile for presentation
			
			logger.debug("metadataFileName="+metadataFileName);
			if (repName.startsWith(WorkArea.TMP_PIPS)) {
				metadataFileName = packageType + ".xml";
				logger.debug("metadataFileName="+metadataFileName);
				//+ extension;
			}
			
			File destFile = new File(wa.dataPath() + "/" + repName + "/" + metadataFileName);
			FileUtils.copyFile(wa.toFile(srcMetadataFile), destFile);
			DAFile destMetadataFile = new DAFile(repName, metadataFileName);
			destMetadataFile.setFormatPUID(srcMetadataFile.getFormatPUID());
			o.getLatestPackage().getFiles().add(destMetadataFile);
			
			Event e = new Event();
			e.setSource_file(srcMetadataFile);
			e.setTarget_file(destMetadataFile);
			e.setType("COPY");
			e.setDate(new Date());
			e.setAgent_type("NODE");
			e.setAgent_name(n.getName());							
			o.getLatestPackage().getEvents().add(e);
			
			logger.debug("Copied metadata file \"{}\" to \"{}\"", srcMetadataFile.toString(), destMetadataFile);
			
//			object.setMetadata_file(metadataFileName);
			
			// copy METS-Files if present in EAD-package
//			if ("EAD".equals(packageType)) {
//				copyXMLsToNewRepresentation(e.getSource_file(), repName);
//			}
		}
		return metadataFileName;
	}

	
	private void logConvertEventsOnDebugLevel() {
		logger.debug("Showing events for pkg");
		
		for (Event e:o.getLatestPackage().getEvents()){			
			if (e.getType().equals("CONVERT")){
				logger.debug("Detail:"+e.getDetail());
				logger.debug("Source:"+e.getSource_file().toString());
				logger.debug("Target:"+e.getTarget_file().toString());
			}
		}
	}
	
	private boolean representationExists(String repName) {
		boolean repExists = false;
		String repPath = Path.make(wa.dataPath(),repName).toString();
		File repDir = new File(repPath);
		if(repDir.exists()) {
			repExists = true;
		} else {
			logger.error("Representation "+repName+" does not exist!");
		}
		return repExists;
	}

	@Override
	public void rollback() throws Exception {
		
		String metadataFileName = o.getMetadata_file();
		File mf = wa.toFile(o.getLatest(metadataFileName));
		String mfPath= o.getLatest(metadataFileName).getPath().toString();
		
		String packageType = o.getPackage_type();
		List<de.uzk.hki.da.model.Document> documents = o.getDocuments();
		for(String repName : getRepNames()) {
			if(packageType.equals(C.CB_PACKAGETYPE_EAD)) {
				
				EadMetsMetadataStructure ead = new EadMetsMetadataStructure(wa.dataPath(),
						new File(mfPath), documents);
				
				for(String ref : ead.getMetsRefsInEad()) {
					String metsRelPath = ead.getReferencedDafile(mf, ref, documents).getRelative_path();
					FolderUtils.deleteQuietlySafe(wa.toFile(repName, metsRelPath));
				}
			}
			if (repName.startsWith(WorkArea.TMP_PIPS)) {
				metadataFileName = packageType + ".xml";
			}
			FolderUtils.deleteQuietlySafe(Path.makeFile(wa.dataPath(), repName, metadataFileName));
			
			for(File file : Path.makeFile(wa.dataPath(), repName).listFiles()) {
				if(file.isDirectory() && file.listFiles().length==0) {
					FolderUtils.deleteDirectorySafe(file);
				}
			}
		}
	}
	

	/**
	 * Get the names of the representations the action
	 * should work on.
	 * @return an array of representation names
	 */
	public String[] getRepNames() {
		return repNames;
	}

	/**
	 * Set the names of the representations the action
	 * should work on.
	 * @param an array of representation names
	 */
	public void setRepNames(String[] repNames) {
		this.repNames = repNames;
	}

	
	
	/**
	 * Gets the xpaths to urls.
	 *
	 * @return the xpaths to urls
	 */
	public Map<String,String> getXpathsToUrls() {
		return xpathsToUrls;
	}

	/**
	 * Sets the xpaths to urls.
	 *
	 * @param xpathsToUrls the xpaths to urls
	 */
	public void setXpathsToUrls(Map<String,String> xpathsToUrls) {
		this.xpathsToUrls = xpathsToUrls;
	}

	/**
	 * Gets the namespaces.
	 *
	 * @return the namespaces
	 */
	public Map<String,String> getNamespaces() {
		return namespaces;
	}

	/**
	 * Sets the namespaces.
	 *
	 * @param namespaces the namespaces
	 */
	public void setNamespaces(Map<String,String> namespaces) {
		this.namespaces = namespaces;
	}

	public MimeTypeDetectionService getMtds() {
		return mtds;
	}

	public void setMtds(MimeTypeDetectionService mtds) {
		this.mtds = mtds;
	}
	
	public boolean isPresMode() {
		return presMode;
	}

	public void setPresMode(boolean presMode) {
		this.presMode = presMode;
	}

	public void updateAbsUrlPrefix() {
		if (presMode){
			if (preservationSystem.getUrisFile() != null && !preservationSystem.getUrisFile().isEmpty()) {
				absUrlPrefix = preservationSystem.getUrisFile() 
						+ "/" + j.getObject().getIdentifier() + "/";
				logger.debug(":::::::::::::::::::::::::::::: Presentation ::::::::::::::::::::::::::::::");
			} else {
				logger.debug(":::::::::::::::::::::::::::::: LZA ::::::::::::::::::::::::::::::");
			}
		}
	}
	
	public void updateRepNames() {
		if (repNames == null || repNames.length == 0) {
			repNames = new String[]{ o.getNameOfLatestBRep() };
		}
	}
}
