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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
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
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.commons.io.input.BOMInputStream;
import org.apache.commons.lang.NotImplementedException;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.xml.sax.SAXException;

import de.uzk.hki.da.action.AbstractAction;
import de.uzk.hki.da.core.MailContents;
import de.uzk.hki.da.format.MimeTypeDetectionService;
import de.uzk.hki.da.metadata.EadMetsMetadataStructure;
import de.uzk.hki.da.metadata.LidoMetadataStructure;
import de.uzk.hki.da.metadata.MetsMetadataStructure;
import de.uzk.hki.da.metadata.XMPMetadataStructure;
import de.uzk.hki.da.metadata.XmpCollector;
import de.uzk.hki.da.metadata.XsltEDMGenerator;
import de.uzk.hki.da.model.DAFile;
import de.uzk.hki.da.model.Event;
import de.uzk.hki.da.model.Package;
import de.uzk.hki.da.util.ConfigurationException;
import de.uzk.hki.da.util.Path;

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
	private boolean writePackageTypeToDC = false;	
	private String[] repNames;	
	private boolean presMode=false;
	private Map<String,String> dcMappings = new HashMap<String,String>();
	private MimeTypeDetectionService mtds = new MimeTypeDetectionService();
	private String absUrlPrefix = "";
	private File metadataFile;
	private HashMap<String, Integer> fileName_convertRewritingCount;
	private Map<DAFile,DAFile> unreferencedConvertedFiles;
	

	@Override
	public void checkActionSpecificConfiguration() throws ConfigurationException {
		// Auto-generated method stub
	}

	@Override
	public void checkSystemStatePreconditions() throws IllegalStateException {
		// Auto-generated method stub
	}

	@Override
	public boolean implementation() throws IOException, JDOMException, ParserConfigurationException, SAXException {
		
		logger.debug("UpdateMetadataAction ...");
		
		logger.debug("Package type: "+object.getPackage_type());
		logger.debug("Metadata file: "+object.getMetadata_file());
		
		updateAbsUrlPrefix();
		updateRepNames();
		
		this.setMtds(mtds);
		
		String packageType = object.getPackage_type();
		String metadataFileName = object.getMetadata_file();
		
//		check object & job settings
		if (job==null) throw new ConfigurationException("job not set");
		if (packageType == null || metadataFileName == null) {
			logger.warn("Could not determine package type. No metadata to update.");
			return true;
		}
		
		logger.debug("Got data from ACS - package_type: {}, metadata_file: {}", packageType, metadataFileName);
		logConvertEventsOnDebugLevel();
		
		List<de.uzk.hki.da.model.Document> documents = object.getDocuments();
			
		if(!"XMP".equals(packageType)) {
			
			metadataFileName = copyMetadataFileToNewReps(packageType,
					metadataFileName);
			
			for (String repName : getRepNames()) {
				
				logger.debug("Update path in "+repName+".");
				Map<DAFile,DAFile> replacements = generateReplacementsMap(object.getLatestPackage(), repName, absUrlPrefix);
				unreferencedConvertedFiles = replacements;
				
				if(representationExists(repName)) {
					metadataFile = Path.makeFile(object.getLatestPackage().getTransientBackRefToObject().getDataPath(),repName,metadataFileName);
	                if (!metadataFile.exists()) throw new FileNotFoundException();
	                logger.debug("Metadata file: "+metadataFile.getAbsolutePath());
	                fileName_convertRewritingCount = new HashMap<String, Integer>();
	                
					if("EAD".equals(packageType)) {
						EadMetsMetadataStructure emms = new EadMetsMetadataStructure(metadataFile, documents);
						updatePathsInEad(emms, repName, replacements);
					} else if ("METS".equals(packageType) && (!replacements.isEmpty() && replacements!=null)) {
						MetsMetadataStructure mms = new MetsMetadataStructure(metadataFile, documents);
						updatePathsInMets(mms, metadataFile, replacements);
					} else if("LIDO".equals(packageType) && (!replacements.isEmpty() && replacements!=null)) {
						LidoMetadataStructure lms = new LidoMetadataStructure(metadataFile, documents);
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
						new MailContents(preservationSystem,localNode).missingReferences(object, missingReferences);
					}
				}
			}
		}
		
	
		if ("XMP".equals(packageType)){
			
			collectXMP();
			
			for (String repName : getRepNames()) {
				if(representationExists(repName)) {
					logger.debug("representation: "+repName);
					Map<DAFile,DAFile> replacements = generateReplacementsMap(object.getLatestPackage(), repName, absUrlPrefix);
					logger.debug("Search for metadata file "+Path.make(object.getLatestPackage().getTransientBackRefToObject().getDataPath(),repName,metadataFileName));
					metadataFile = Path.makeFile(object.getLatestPackage().getTransientBackRefToObject().getDataPath(),repName,metadataFileName);
					if (!metadataFile.exists()) throw new FileNotFoundException();
		            XMPMetadataStructure xms = new XMPMetadataStructure(metadataFile, documents);
					updatePathsInRDF(xms, replacements);
				}
			}
		}
		
		copyDCdatastreamFromMetadata(packageType, metadataFileName);
		if (isWritePackageTypeToDC())
			writePackageTypeToDC(packageType);
		
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
				targetValue = preservationSystem.getUrisFile() + File.separator + object.getIdentifier() + File.separator + targetFile.getRelative_path();
			}
			replacementsMap.put(sourceFile.getRelative_path(), targetValue);
		}
		xms.makeReplacementsInRDf(replacementsMap);	
	}
	
	private void updatePathsInEad(EadMetsMetadataStructure emms, String repName, Map<DAFile,DAFile> replacements) throws JDOMException, IOException {
		logger.debug("Update paths in EAD file "+emms.getMetadataFile().getAbsolutePath());
		HashMap<String, String> eadReplacements = new HashMap<String, String>();
		List<String> eadRefs = emms.getMetsRefsInEad();
		
		for (Event e:object.getLatestPackage().getEvents()) {
			if(e.getType().equals("COPY") || e.getType().equals("CONVERT")) {
				DAFile sourceFile = e.getSource_file();
				
				for(String href : eadRefs) {
					File file = emms.getCanonicalFileFromReference(href, emms.getMetadataFile());					
					if(file.getAbsolutePath().contains(sourceFile.getRelative_path())) {
						DAFile targetDAFile = e.getTarget_file();
						File targetFile = targetDAFile.toRegularFile();
						String targetPath = href.replace(file.getName(), targetFile.getName());
						String targetValue;
						if(!isPresMode()) {
							targetValue = targetPath;
						} else {
							targetValue = preservationSystem.getUrisFile() + File.separator + object.getIdentifier() + File.separator + targetDAFile.getRelative_path();
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
			logger.debug("Update paths in METS file "+mms.getMetadataFile().getAbsolutePath());
			updatePathsInMets(mms, mms.getMetadataFile(), replacements);
		}
	}
	
	
	@SuppressWarnings("rawtypes")
	private void updatePathsInMets(MetsMetadataStructure mms, File metsFile, Map<DAFile,DAFile> replacements) throws IOException, JDOMException {
		String targetPath = "";
		List<Element> metsFileElemens = mms.getMetsFileElements();
		logger.debug("Checking references ... ");
		for(Element metsFileElement : metsFileElemens) {
			String href = mms.getHref(metsFileElement);
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
					logger.debug("DAFile "+sourceFile+" has been converted to "+targetDAFile+"!");
					mimetype = targetDAFile.getMimeType();
					File targetFile = targetDAFile.toRegularFile();
					targetPath = href.replace(file.getName(), targetFile.getName());
					addRefToFileNameConvertRewritingCountMap(href);
					if(unreferencedConvertedFiles.get(sourceFile)!=null) {
						unreferencedConvertedFiles.remove(sourceFile);
					}
					break;
				} 
			}
			if(!fileExists) {
				logger.debug("File not found! Search in previouos packages ...");
				List<String> referenceWithMimetype = getCorrReferencesAndMimetypeInDelta(href, "");
				if(!referenceWithMimetype.isEmpty()) {
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
					targetValue = preservationSystem.getUrisFile() + File.separator + object.getIdentifier() + File.separator + targetDAFile.getRelative_path();
					loctype = "URL";
				}
				mms.makeReplacementsInMetsFile(metsFile, href, targetValue, mimetype, loctype);
			} else {
				logger.error("There is no matching file! Reference: "+href);
			}
		}
	}
	
	@SuppressWarnings("rawtypes")
	private void updatePathsInLido(LidoMetadataStructure lms, Map<DAFile,DAFile> replacements) throws IOException {
		HashMap<String, String> lidoReplacements = new HashMap<String, String>();
		List<String> lidoRefs = lms.getLidoLinkResources();
		String targetPath = "";
		for(String href : lidoRefs) {
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
					File targetFile = targetDAFile.toRegularFile();
					targetPath = href.replace(file.getName(), targetFile.getName());
					addRefToFileNameConvertRewritingCountMap(href);
					if(unreferencedConvertedFiles.get(sourceFile)!=null) {
						unreferencedConvertedFiles.remove(sourceFile);
					}
					break;
				}
			}
			if(!fileExists) {
				logger.debug("File not found! Search in previouos packages ...");
				List<String> references = getCorrReferencesAndMimetypeInDelta(href, "");
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
					targetValue = preservationSystem.getUrisFile() + File.separator + object.getIdentifier() + File.separator + targetDAFile.getRelative_path();
				}
				lidoReplacements.put(href, targetValue);
			} else {
				logger.error("There is no matching file! Reference: "+href);
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
	
	private DAFile getSourceDAFile(String href) {
		logger.debug("Scan for dafile matching href "+href+" ... ");
		DAFile sourceDAFile = null;
		String fileName = Path.makeFile(href).getName();
		de.uzk.hki.da.model.Document doc = object.getDocument(FilenameUtils.getBaseName(fileName));
		
		DAFile lastDAFile = doc.getLasttDAFile();
		if(lastDAFile.toRegularFile().getName().equals(fileName)) {
			sourceDAFile = lastDAFile;
		} else {
			while(lastDAFile.getPreviousDAFile() != null){
	        	DAFile previousDAFile = lastDAFile.getPreviousDAFile();
	        	if(previousDAFile.toRegularFile().getName().equals(fileName)) {
	        		sourceDAFile = previousDAFile; 
	        		break;
	        	}
	        	lastDAFile = lastDAFile.getPreviousDAFile(); 
			}
		}
		logger.debug("Return dafile "+sourceDAFile);
		return sourceDAFile;
	}
	
	private List<String> getCorrReferencesAndMimetypeInDelta(String href, String sidecarExts) {
		logger.debug("Completing references in delta ...");
		List<String> reference = new ArrayList<String>();
		DAFile sourceFile = getSourceDAFile(href);
		String sourceFileName = sourceFile.toRegularFile().getName();
		List<DAFile> newestFiles = object.getNewestFilesFromAllRepresentations(sidecarExts);
		for(DAFile dafile : newestFiles) {
			logger.debug("DAFile: "+dafile);
			String dafileName = dafile.toRegularFile().getName();
			if(FilenameUtils.getBaseName(sourceFileName).equals(FilenameUtils.getBaseName(dafileName))&&(
					!(dafile.toRegularFile().getName().equals(sourceFile.toRegularFile().getName())))) {
				logger.debug("The newest representation of "+href+"!");
				reference.add(dafile.getRelative_path());
				String mimetype = "";
				try {
					mimetype = mtds.detectMimeType(dafile);
				} catch (IOException e) {
					logger.error("Unable to detect the mimetype!");
					e.printStackTrace();
				}
				logger.debug("Mimetype: "+mimetype);
				reference.add(mimetype);
				break;
			}
		}
		if(!reference.isEmpty()) {
			logger.debug("Return reference "+reference.get(0)+" with mimetype "+reference.get(1));
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
			targetFile.setMimeType(getMtds().detectMimeType(targetFile));
			
			replacements.put(sourceFile, targetFile);
		}
		logger.debug("Planned replacements: {}", replacements);
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
		
		DAFile srcMetadataFile = object.getLatest(metadataFileName);
		String extension = FilenameUtils.getExtension(srcMetadataFile.toRegularFile().getName());
		
		for (String repName : getRepNames()) {
			// rename metadatafile for presentation
			if (repName.startsWith("dip")) {
				metadataFileName = packageType + "." + extension;
			}
			
			File destFile = new File(object.getDataPath() + "/" + repName + "/" + metadataFileName);
			FileUtils.copyFile(srcMetadataFile.toRegularFile(), destFile);
			DAFile destMetadataFile = new DAFile(object.getLatestPackage(), repName, metadataFileName);
			destMetadataFile.setFormatPUID(srcMetadataFile.getFormatPUID());
			object.getLatestPackage().getFiles().add(destMetadataFile);
			
			Event e = new Event();
			e.setSource_file(srcMetadataFile);
			e.setTarget_file(destMetadataFile);
			e.setType("COPY");
			e.setDate(new Date());
			e.setAgent_type("NODE");
			e.setAgent_name(object.getTransientNodeRef().getName());							
			object.getLatestPackage().getEvents().add(e);
			
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
	 * @param packageType
	 * @param metadataFile
	 */
	private void copyDCdatastreamFromMetadata(String packageType,
			String metadataFile) {
		if (packageType != null && metadataFile != null) {
			String xsltFile = getDcMappings().get(packageType);
			if (xsltFile == null) {
				throw new RuntimeException("No conversion available for package type '" + packageType + "'. DC can not be created.");
			}
			try {
				for (String repName : getRepNames()) {
					if (!repName.startsWith("dip") 	|| !representationExists(repName)) continue;
					FileInputStream inputStream = new FileInputStream(Path.make(object.getDataPath(),repName,metadataFile).toString());
					BOMInputStream bomInputStream = new BOMInputStream(inputStream);
					XsltEDMGenerator xsltGenerator = new XsltEDMGenerator(xsltFile, bomInputStream);
					String result = xsltGenerator.generate();
					File file = new File(object.getDataPath() + "/"+repName + "/DC.xml");
					if (!file.exists()) file.createNewFile();
					FileOutputStream outputStream = new FileOutputStream(file);
					outputStream.write(result.getBytes("utf-8"));
					outputStream.flush();
					outputStream.close();
				}
			} catch (Exception e) {
				throw new RuntimeException("Unable to create DC file.", e);
			}
		}
	}

	/**
	 * @param srcFile
	 * @param repName
	 * @throws IOException
	 */
	
	private void copyXMLsToNewRepresentation(DAFile srcDAFile, String repName) 
			throws IOException {
		
		File srcFile = srcDAFile.toRegularFile();
		
		Iterator<File> xmlFiles = FileUtils.iterateFiles(
				srcFile.getParentFile(), new WildcardFileFilter("*.xml"), null);
		
		File destDir = null;
		
//		Implementierung für beliebige Baumtiefe steht noch aus!
		
		File[] subDirs = srcFile.getParentFile().listFiles();
		
		for(int file=-1; file<subDirs.length; file++) {
			
			if(file==-1) {
				destDir = new File(object.getDataPath() +"/"+ repName);
			} else {
				File currentFile = subDirs[file];
				if(currentFile.isDirectory()) {
					destDir = new File(Path.make(object.getDataPath(), repName, currentFile.getName()).toString());
					xmlFiles = FileUtils.iterateFiles(
							currentFile, new WildcardFileFilter("*.xml"), null);
				}
			}
			
			int count=0;
			while (xmlFiles.hasNext()) {
				count++;
				
				File xmlFile = xmlFiles.next();
				FileUtils.copyFileToDirectory(xmlFile, destDir);
				logger.debug("Copy "+xmlFile.getAbsolutePath()+" to "+destDir.getAbsolutePath());
			
				DAFile daFile = null;
				Event e = new Event();							
				for (Package p : object.getPackages()) {
					for (DAFile f : p.getFiles()) {
						if (xmlFile.getAbsolutePath()
								.equals(f.toRegularFile().getAbsolutePath())) {
							e.setSource_file(f);
							daFile = new DAFile(object.getLatestPackage(), repName, f.getRelative_path());
							daFile.setFormatPUID(f.getFormatPUID());
						}
					}
				}	
				
				object.getLatestPackage().getFiles().add(daFile);
				
				e.setTarget_file(daFile);
				e.setType("COPY");
				e.setDate(new Date());
				e.setAgent_type("NODE");
				e.setAgent_name(object.getTransientNodeRef().getName());							
				object.getLatestPackage().getEvents().add(e);
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
			String repPath = Path.make(object.getDataPath(),repName).toString();
			File repDir = new File(repPath);
			if (!repDir.exists()) {
				logger.info("representation directory {} does not exist. Skipping ...", repPath);
				continue;
			}
			
			List<DAFile> newestFiles = object.getNewestFilesFromAllRepresentations("xmp");
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
				
				DAFile sidecarTargetFile = new DAFile(object.getLatestPackage(), repName, xmpTargetPath);
				
				copyCommands.put(sidecarTargetFile, sidecarSourceFile);
			}
			logger.debug("collecting files in path: {}", repPath);
			
			XmpCollector.collect(newestXmpFiles, new File(repPath + "/XMP.rdf"));
			DAFile xmpFile = new DAFile(object.getLatestPackage(),repName,"XMP.rdf");
			object.getLatestPackage().getFiles().add(xmpFile);
			object.getLatestPackage().getEvents().add(createCreateEvent(xmpFile));
			
		}
		
		// run copy commands
		for (DAFile sidecarTargetFile : copyCommands.keySet()) {
			DAFile sidecarSourceFile = copyCommands.get(sidecarTargetFile);
			
			logger.debug("Copying {} to {}", sidecarSourceFile, sidecarTargetFile);
			FileUtils.copyFile(sidecarSourceFile.toRegularFile(), sidecarTargetFile.toRegularFile());
			sidecarTargetFile.setFormatPUID(sidecarSourceFile.getFormatPUID());
			
			object.getLatestPackage().getFiles().add(sidecarTargetFile);
			object.getLatestPackage().getEvents().add(
					createCopyEvent(sidecarSourceFile, sidecarTargetFile));
		}
		
	}
	
	private String determineTargetRelativePathWithoutExtension(DAFile sidecarSourceFile) {
		String relativePath = "";
		for (Event evt:object.getLatestPackage().getEvents()){
			if (evt.getType().equals("CONVERT")&&
					FilenameUtils.removeExtension(evt.getSource_file().toRegularFile().getAbsolutePath()).
						equals(FilenameUtils.removeExtension(sidecarSourceFile.toRegularFile().getAbsolutePath()))){
				relativePath = FilenameUtils.removeExtension(evt.getTarget_file().getRelative_path());
				break;
			}
		}
		
		if (relativePath.equals(""))
			logger.debug("No CONVERT event found for " + FilenameUtils.removeExtension(sidecarSourceFile.toRegularFile().getName()));
		
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
		e.setAgent_name(object.getTransientNodeRef().getName());
		return e;
	}
	
	private Event createCreateEvent(DAFile targetFile) {
		
		Event e = new Event();
		e.setTarget_file(targetFile);
		e.setType("CREATE");
		e.setDate(new Date());
		e.setAgent_type("NODE");
		e.setAgent_name(object.getTransientNodeRef().getName());
		return e;
	}

	private void logConvertEventsOnDebugLevel() {
		logger.debug("Showing events for pkg");
		
		for (Event e:object.getLatestPackage().getEvents()){			
			if (e.getType().equals("CONVERT")){
				logger.debug("Detail:"+e.getDetail());
				logger.debug("Source:"+e.getSource_file().toString());
				logger.debug("Target:"+e.getTarget_file().toString());
			}
		}
	}

	void writePackageTypeToDC(String packageType) {
		
		if (packageType != null) {
			for (String repName : getRepNames()) {
				if(representationExists(repName)) {
					File file = Path.make(object.getDataPath(),repName,"DC.xml").toFile();
					if (file.exists()) {
						try {
							FileInputStream inputStream = new FileInputStream(file);
							BOMInputStream bomInputStream = new BOMInputStream(inputStream);
						
							SAXBuilder builder = new SAXBuilder();
							Document doc;
						
							doc = builder.build(bomInputStream);
							writeDCForDIP(doc, packageType, file.getAbsolutePath());
						} catch (Exception e) {
							throw new RuntimeException("Unable to write package type to DC!", e);
						} 
					} else {
						logger.warn("Unable to locate DC file, creating one ...");
						Document doc = new Document();
						doc.setRootElement(new Element("dc", "oai_dc", "http://www.openarchives.org/OAI/2.0/oai_dc/"));
						String dcPath = object.getDataPath() +"/"+ repName + "/DC.xml";
						writeDCForDIP(doc, packageType, dcPath);
					}
				}
			}
		}	
	}
	
	private void writeDCForDIP(Document doc, String packageType, String dcPath) {
		try {
			doc.getRootElement().addContent(
				new Element("format","dc","http://purl.org/dc/elements/1.1/")
				.setText(packageType));
			XMLOutputter outputter = new XMLOutputter();
			outputter.setFormat(Format.getPrettyFormat());
			outputter.output(doc, new FileWriter(dcPath));
		} catch (Exception e) {
			throw new RuntimeException("Unable to write package type to DC!", e);
		} 
	}
	
	private boolean representationExists(String repName) {
		boolean repExists = false;
		String repPath = Path.make(object.getDataPath(),repName).toString();
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
		throw new NotImplementedException("No rollback implemented for this action");
	}

	/**
	 * Check if the package type is written to the
	 * Dublin Core metadata file.
	 * @return
	 */
	public boolean isWritePackageTypeToDC() {
		return writePackageTypeToDC;
	}

	/**
	 * Set wether the package type should be written to the
	 * Dublin Core metadata 
	 * @param writePackageTypeToDC
	 */
	public void setWritePackageTypeToDC(boolean writePackageTypeToDC) {
		this.writePackageTypeToDC = writePackageTypeToDC;
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
	 * Gets the map that describes which XSLTs should be
	 * used to convert Metadata to Dublin Core.
	 * @return a map, keys represent metadata formats,
	 * 	values the path to the XSLT file
	 */
	public Map<String,String> getDcMappings() {
		return dcMappings;
	}

	/**
	 * Sets the map that describes which XSLTs should be
	 * used to convert Metadata to Dublin Core.
	 * @param a map, keys represent metadata formats,
	 * 	values the path to the XSLT file
	 */
	public void setDcMappings(Map<String,String> dcMappings) {
		this.dcMappings = dcMappings;
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
				absUrlPrefix = preservationSystem.getUrisFile() + "/" + job.getObject().getIdentifier() + "/";
				logger.debug(":::::::::::::::::::::::::::::: Presentation ::::::::::::::::::::::::::::::");
			} else {
				logger.debug(":::::::::::::::::::::::::::::: LZA ::::::::::::::::::::::::::::::");
			}
		}
	}
	
	public void updateRepNames() {
		if (repNames == null || repNames.length == 0) {
			repNames = new String[]{ object.getPath("newest").getLastElement() };
		}
	}
}