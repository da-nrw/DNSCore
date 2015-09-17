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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOCase;
import org.apache.commons.io.filefilter.WildcardFileFilter;
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
import de.uzk.hki.da.metadata.XMPMetadataStructure;
import de.uzk.hki.da.metadata.XmpCollector;
import de.uzk.hki.da.model.DAFile;
import de.uzk.hki.da.model.Event;
import de.uzk.hki.da.model.Package;
import de.uzk.hki.da.model.WorkArea;
import de.uzk.hki.da.util.ConfigurationException;
import de.uzk.hki.da.util.FileIdGenerator;
import de.uzk.hki.da.utils.C;
import de.uzk.hki.da.utils.Path;
import de.uzk.hki.da.utils.RelativePath;

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
	public boolean implementation() throws IOException, JDOMException, ParserConfigurationException, SAXException {
		
		logger.debug("UpdateMetadataAction ...");
		
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
			
			metadataFileName = copyMetadataFileToNewReps(packageType,
					metadataFileName);
			
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
						EadMetsMetadataStructure emms = new EadMetsMetadataStructure(wa.dataPath(),metadataFile, documents);
						updatePathsInEad(emms, repName, replacements);
					} else if ("METS".equals(packageType) && (!replacements.isEmpty() && replacements!=null)) {
						MetsMetadataStructure mms = new MetsMetadataStructure(wa.dataPath(),metadataFile, documents);
						updatePathsInMets(mms, metadataFile, replacements);
					} else if("LIDO".equals(packageType) && (!replacements.isEmpty() && replacements!=null)) {
						LidoMetadataStructure lms = new LidoMetadataStructure(wa.dataPath(),metadataFile, documents);
						updatePathsInLido(lms, replacements);
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
		
	
		if ("XMP".equals(packageType)){
			
			collectXMP();
			
			for (String repName : getRepNames()) {
				if(representationExists(repName)) {
					logger.debug("representation: "+repName);
					Map<DAFile,DAFile> replacements = generateReplacementsMap(o.getLatestPackage(), repName, absUrlPrefix);
					logger.debug("Search for metadata file "+Path.make(wa.dataPath(),repName,metadataFileName));
					metadataFile = new RelativePath(repName,metadataFileName).toFile();
					if (!Path.makeFile(wa.dataPath(),metadataFile.getPath()).exists()) throw new FileNotFoundException();
		            XMPMetadataStructure xms = new XMPMetadataStructure(wa.dataPath(),metadataFile, documents);
					updatePathsInRDF(xms, replacements);
				}
			}
		}
		
		return true;
	}
	
	private void updatePathsInRDF(XMPMetadataStructure xms, Map<DAFile,DAFile> replacements) throws IOException {
		logger.debug("Update paths in XMP file "+xms.getMetadataFile().getAbsolutePath());
		Map<String, String> replacementsMap = new HashMap<String, String>();
		for(DAFile sourceFile : replacements.keySet()) {
			DAFile targetFile = (DAFile)replacements.get(sourceFile);
			String targetValue;
			if(!isPresMode()) {
				targetValue = targetFile.getRelative_path();
			} else {
				targetValue = preservationSystem.getUrisFile() + File.separator + o.getIdentifier() + File.separator + targetFile.getRelative_path();
			}
			replacementsMap.put(sourceFile.getRelative_path(), targetValue);
		}
		xms.makeReplacementsInRDf(replacementsMap);	
	}
	
	private void updatePathsInEad(EadMetsMetadataStructure emms, String repName, Map<DAFile,DAFile> replacements) throws JDOMException, IOException {
		logger.info("Update paths in EAD file "+emms.getMetadataFile().getAbsolutePath());
		HashMap<String, String> eadReplacements = new HashMap<String, String>();
		List<String> eadRefs = emms.getMetsRefsInEad();
		for (Event e:o.getLatestPackage().getEvents()) {
			if(e.getType().equals("COPY") || e.getType().equals("CONVERT")) {
				DAFile sourceFile = e.getSource_file();
				for(String href : eadRefs) {
					File file = emms.getCanonicalFileFromReference(href, emms.getMetadataFile());					
					if(file.getAbsolutePath().contains(sourceFile.getRelative_path())) {
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
			updatePathsInEADMetsFiles(emms, repName, replacements);
		}
	}
	
	
	private void updatePathsInEADMetsFiles(EadMetsMetadataStructure emms, String repName, Map<DAFile,DAFile> replacements) throws IOException, JDOMException {
		List<MetsMetadataStructure> mmsList = emms.getMetsMetadataStructures();
		for (MetsMetadataStructure mms : mmsList) {
			logger.info("Update paths in METS file "+mms.getMetadataFile().getAbsolutePath());
			updatePathsInMets(mms, mms.getMetadataFile(), replacements);
		}
	}
	
	
	@SuppressWarnings("rawtypes")
	private void updatePathsInMets(MetsMetadataStructure mms, File metsFile, Map<DAFile,DAFile> replacements) throws IOException, JDOMException {
		String targetPath = "";
		List<Element> metsFileElemens = mms.getFileElements();
		for(Element metsFileElement : metsFileElemens) {
			String href = mms.getHref(metsFileElement);
			logger.info("Reference: "+href);
			DAFile targetDAFile = null;
			File file = mms.getCanonicalFileFromReference(href, metsFile);
			Boolean fileExists = false;
			String mimetype = "";
			Iterator it = replacements.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry entry = (Map.Entry)it.next();
				DAFile sourceFile = (DAFile)entry.getKey();
				if(file.getAbsolutePath().contains(sourceFile.getRelative_path())) {
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
				List<String> referenceWithMimetype = getCorrReferencesAndMimetypeInDelta(mms, href, "");
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
				mms.makeReplacementsInMetsFile(metsFile, href, targetValue, mimetype, loctype);
			} else {
				logger.error("No dafile found for href "+href);
			}
		}
	}
	
	@SuppressWarnings("rawtypes")
	private void updatePathsInLido(LidoMetadataStructure lms, Map<DAFile,DAFile> replacements) throws IOException {
		logger.info("Update paths in LIDO file "+lms.getMetadataFile().getAbsolutePath());
		HashMap<String, String> lidoReplacements = new HashMap<String, String>();
		List<String> lidoRefs = lms.getLidoLinkResources();
		String targetPath = "";
		for(String href : lidoRefs) {
			logger.debug("Reference: "+href);
			File file = lms.getCanonicalFileFromReference(href, lms.getMetadataFile());
			DAFile targetDAFile = null;
			Boolean fileExists = false;
			Iterator it = replacements.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry entry = (Map.Entry)it.next();
		        DAFile sourceFile = (DAFile)entry.getKey();
				if(file.getAbsolutePath().contains(sourceFile.getRelative_path())) {
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
				List<String> references = getCorrReferencesAndMimetypeInDelta(lms, href, "");
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
	
	private List<String> getCorrReferencesAndMimetypeInDelta(MetadataStructure ms, String href, String sidecarExts) {
		logger.debug("File not found. Completing reference "+href+" in delta ...");
		List<String> reference = new ArrayList<String>();
		DAFile sourceFile = ms.getReferencedDafile(metadataFile, href, o.getDocuments());
		List<DAFile> newestFiles = o.getNewestFilesFromAllRepresentations(sidecarExts);
		for(DAFile dafile : newestFiles) {
			if(FilenameUtils.getBaseName(wa.toFile(dafile).getName()).equals(FilenameUtils.getBaseName(wa.toFile(sourceFile).getName()))&&(
					!(wa.toFile(dafile).getName().equals(wa.toFile(sourceFile).getName())))) {
				logger.debug("calculated reference "+dafile.getRelative_path());
				reference.add(dafile.getRelative_path());
				String mimetype = "";
				try {
					mimetype = mtds.identify(wa.toFile(dafile));
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
			targetFile.setMimeType(getMtds().identify(wa.toFile(targetFile)));
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
			if ("EAD".equals(packageType)) {
				copyXMLsToNewRepresentation(e.getSource_file(), repName);
			}
		}
		return metadataFileName;
	}

	
	
	/**
	 * @param srcFile
	 * @param repName
	 * @throws IOException
	 */
	
	private void copyXMLsToNewRepresentation(DAFile srcDAFile, String repName) 
			throws IOException {
		
		File srcFile = wa.toFile(srcDAFile);
		
		Iterator<File> xmlFiles = FileUtils.iterateFiles(
				srcFile.getParentFile(), new WildcardFileFilter("*.xml", IOCase.INSENSITIVE), null);

//		Implementierung für beliebige Baumtiefe steht noch aus!
		
		File[] subDirs = srcFile.getParentFile().listFiles();
		
		for(int file=-1; file<subDirs.length; file++) {
			File destDir = new File(wa.dataPath() +"/"+ repName);
			
			if(file>-1) {
				File currentFile = subDirs[file];
				if(currentFile.isDirectory()) {
					destDir = new File(Path.make(wa.dataPath(), repName, currentFile.getName()).toString());
					xmlFiles = FileUtils.iterateFiles(
							currentFile, new WildcardFileFilter("*.xml", IOCase.INSENSITIVE), null);
				}
			}
			
			int count=0;
			while (xmlFiles.hasNext()) {
				count++;
				
				File xmlFile = xmlFiles.next();
				
				if(!xmlFile.getName().equals(o.getMetadata_file())) {
					FileUtils.copyFileToDirectory(xmlFile, destDir);
					logger.debug("Copy "+xmlFile.getAbsolutePath()+" to "+destDir.getAbsolutePath());
					
					DAFile daFile = null;
					Event e = new Event();							
					for (Package p : o.getPackages()) {
						for (DAFile f : p.getFiles()) {
							if (xmlFile.getAbsolutePath()
									.equals(wa.toFile(f).getAbsolutePath())) {
								e.setSource_file(f);
								daFile = new DAFile(repName, f.getRelative_path());
								daFile.setFormatPUID(f.getFormatPUID());
							}
						}
					}	
					
					o.getLatestPackage().getFiles().add(daFile);
					
					e.setTarget_file(daFile);
					e.setType("COPY");
					e.setDate(new Date());
					e.setAgent_type("NODE");
					e.setAgent_name(n.getName());							
					o.getLatestPackage().getEvents().add(e);
				}
			}
			logger.debug("Copied "+count+ " *.xml files to new representation (package is of type EAD)");	
		}
	}
	
	/**
	 * Copy xmp sidecar files and collect them into one "XMP manifest"
	 * @author Sebastian Cuy
	 * @author Daniel M. de Oliveira
	 * @author Thomas Kleinke
	 * @throws IOException
	 */
	private void collectXMP() throws IOException {
		logger.debug("collectXMP");
		Map<DAFile,DAFile> copyCommands = new HashMap<DAFile,DAFile>();
		for (String repName : getRepNames()) {
			logger.debug("looking for xmp files in rep {}", repName);
			String repPath = Path.make(wa.dataPath(),repName).toString();
			File repDir = new File(repPath);
			if (!repDir.exists()) {
				logger.info("representation directory {} does not exist. Skipping ...", repPath);
				continue;
			}
			
			List<DAFile> newestFiles = o.getNewestFilesFromAllRepresentations("xmp");
			List<DAFile> newestXmpFiles = new ArrayList<DAFile>();
			for (DAFile dafile : newestFiles) {
				if (dafile.getRelative_path().toLowerCase().endsWith(".xmp"))
					newestXmpFiles.add(dafile);
			}
			
			logger.debug("found {} xmp files", newestXmpFiles.size());
			
			for (DAFile sidecarSourceFile : newestXmpFiles) {
				if (Arrays.asList(repNames).contains(sidecarSourceFile.getRep_name())) continue;
				logger.debug("Found xmp sidecar: {}", sidecarSourceFile);

				String xmpTargetPath = determineTargetRelativePathWithoutExtension(sidecarSourceFile); 
				
				if (xmpTargetPath.equals(""))					
					continue;
				
				xmpTargetPath += ".xmp";
				
				DAFile sidecarTargetFile = new DAFile(repName, xmpTargetPath);
				
				copyCommands.put(sidecarTargetFile, sidecarSourceFile);
			}
			logger.debug("collecting files in path: {}", repPath);
			
			XmpCollector.collect(wa,newestXmpFiles, new File(repPath + "/XMP.xml"));
			DAFile xmpFile = new DAFile(repName,"XMP.xml");
			o.getLatestPackage().getFiles().add(xmpFile);
			o.getLatestPackage().getEvents().add(createCreateEvent(xmpFile));
			
		}
		
		// run copy commands
		for (DAFile sidecarTargetFile : copyCommands.keySet()) {
			DAFile sidecarSourceFile = copyCommands.get(sidecarTargetFile);
			
			logger.debug("Copying {} to {}", sidecarSourceFile, sidecarTargetFile);
			FileUtils.copyFile(wa.toFile(sidecarSourceFile), wa.toFile(sidecarTargetFile));
			sidecarTargetFile.setFormatPUID(sidecarSourceFile.getFormatPUID());
			
			o.getLatestPackage().getFiles().add(sidecarTargetFile);
			o.getLatestPackage().getEvents().add(
					createCopyEvent(sidecarSourceFile, sidecarTargetFile));
		}
		
	}
	
	private String determineTargetRelativePathWithoutExtension(DAFile sidecarSourceFile) {
		String relativePath = "";
		for (Event evt:o.getLatestPackage().getEvents()){
			if (evt.getType().equals("CONVERT")&&
					FilenameUtils.removeExtension(wa.toFile(evt.getSource_file()).getAbsolutePath()).
						equals(FilenameUtils.removeExtension(wa.toFile(sidecarSourceFile).getAbsolutePath()))){
				relativePath = FilenameUtils.removeExtension(evt.getTarget_file().getRelative_path());
				break;
			}
		}
		
		if (relativePath.equals(""))
			logger.debug("No CONVERT event found for " + FilenameUtils.removeExtension(wa.toFile(sidecarSourceFile).getName()));
		
		return relativePath;
	}
	
	private Event createCopyEvent(DAFile sidecarSourceFile,
			DAFile sidecarTargetFile) {
		Event e = new Event();							
		e.setTarget_file(sidecarTargetFile);
		e.setSource_file(sidecarSourceFile);
		e.setType("COPY");
		e.setDate(new Date());
		e.setAgent_type("NODE");
		e.setAgent_name(n.getName());
		return e;
	}
	
	private Event createCreateEvent(DAFile targetFile) {
		
		Event e = new Event();
		e.setTarget_file(targetFile);
		e.setType("CREATE");
		e.setDate(new Date());
		e.setAgent_type("NODE");
		e.setAgent_name(n.getName());
		return e;
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
					FileUtils.deleteQuietly(wa.toFile(repName, metsRelPath));
				}
			}
			if (repName.startsWith(WorkArea.TMP_PIPS)) {
				metadataFileName = packageType + ".xml";
			}
			FileUtils.deleteQuietly(Path.makeFile(wa.dataPath(), repName, metadataFileName));
			
			for(File file : Path.makeFile(wa.dataPath(), repName).listFiles()) {
				if(file.isDirectory() && file.listFiles().length==0) {
					FileUtils.deleteDirectory(file);
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