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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.commons.io.input.BOMInputStream;
import org.apache.commons.lang.NotImplementedException;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;

import de.uzk.hki.da.core.ConfigurationException;
import de.uzk.hki.da.core.UserException;
import de.uzk.hki.da.core.UserException.UserExceptionId;
import de.uzk.hki.da.metadata.XmpCollector;
import de.uzk.hki.da.metadata.XsltEDMGenerator;
import de.uzk.hki.da.model.DAFile;
import de.uzk.hki.da.model.Event;
import de.uzk.hki.da.model.Package;
import de.uzk.hki.da.service.MimeTypeDetectionService;
import de.uzk.hki.da.utils.Path;
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
 * @author Sebastian Cuy
 * @author Daniel M. de Oliveira
 *
 */
public class UpdateMetadataAction extends AbstractAction {

	/** The namespaces. */
	private Map<String,String> namespaces;
	/** The xpaths to urls. */
	private Map<String,String> xpathsToUrls = new HashMap<String,String>();
	private boolean writePackageTypeToDC = false;	
	private String[] repNames;	
	private String absUrlPrefix;
	private Map<String,String> dcMappings = new HashMap<String,String>();
	
	private MimeTypeDetectionService mtds = new MimeTypeDetectionService();
	
//	temporary
	private static final Namespace METS_NS = Namespace.getNamespace("http://www.loc.gov/METS/");
	private static final Namespace XLINK_NS = Namespace.getNamespace("http://www.w3.org/1999/xlink");

	@Override
	public boolean implementation() throws IOException {
		
		this.setMtds(mtds);
		
		if (job==null) throw new ConfigurationException("job not set");
		
		String packageType = object.getPackage_type();
		String metadataFileName = object.getMetadata_file();
		
		if (packageType == null || metadataFileName == null) {
			logger.warn("Could not determine package type. No metadata to update.");
			return true;
		}
		logger.debug("Got data from ACS - package_type: {}, metadata_file: {}", packageType, metadataFileName);
		
		logConvertEventsOnDebugLevel();

		
		String absUrlPrefixFull = "";
		if (getAbsUrlPrefix() != null && !getAbsUrlPrefix().isEmpty()) {
			absUrlPrefixFull = getAbsUrlPrefix() + "/" + job.getObject().getIdentifier() + "/";
		}
	
		if (repNames == null || repNames.length == 0) {
			repNames = new String[]{ object.getNameOfNewestRep() };
		}
		
		if ("XMP".equals(packageType)){
			collectXMP();
			
			for (String repName : getRepNames()) {
				updatePathsInMetadata(
						object.getLatestPackage(),
						packageType,
						metadataFileName,
						repName,
						absUrlPrefixFull
						);
			}
		}
		else{
			metadataFileName = copyMetadataFileToNewReps(packageType,
					metadataFileName);
			
			for (String repName : getRepNames()){
				if ("EAD".equals(packageType))
					updatePathsInEADStructure(
						object.getLatestPackage(),metadataFileName,repName,absUrlPrefixFull);
				else
					updatePathsInMetadata(
						object.getLatestPackage(),packageType,metadataFileName,repName,absUrlPrefixFull);
			}
		}
		
		copyDCdatastreamFromMetadata(packageType, metadataFileName);
		if (isWritePackageTypeToDC())
			writePackageTypeToDC(packageType);
		
		return true;
	}
	
	/**
	 * @param pkg
	 * @param metadataFilePath
	 * @param repName
	 * @param absUrlPrefix
	 * @throws JDOMException 
	 * @throws IOException 
	 */
	private void updatePathsInEADStructure(
			
			Package pkg,
			String metadataFilePath,
			String repName,
			String absUrlPrefix) 
					throws IOException
			{
		
		if (absUrlPrefix == null) absUrlPrefix = "";
		
		Map<String,DAFile> replacements = generateReplacementsMap(pkg, repName, absUrlPrefix);
		
		Set<DAFile> targetFiles = new HashSet<DAFile>();
		Iterator it = replacements.entrySet().iterator();
		while (it.hasNext()) {
	        Map.Entry entry = (Map.Entry)it.next();
	        targetFiles.add((DAFile)entry.getValue());
	    }
		
		int expectedReplacements = targetFiles.size();
		
		File metadataFile = Path.makeFile(pkg.getTransientBackRefToObject().getDataPath(),repName,metadataFilePath);
		if (!metadataFile.exists()) throw new FileNotFoundException();
		
		String xPathPath = xpathsToUrls.get("EAD");
		logger.debug("xPathPath: "+xPathPath);
		// replace paths in elements denoted by xpath
		XPath xPath;
		int actualReplacements = 0;
		@SuppressWarnings("rawtypes")
		List nodes = null;
		
		
		try {
			xPath = XPath.newInstance(xPathPath);

	
			FileInputStream fileInputStream;
			fileInputStream = new FileInputStream(metadataFile);
			BOMInputStream bomInputStream = new BOMInputStream(fileInputStream);
		
			for (String prefix : namespaces.keySet()) {
				xPath.addNamespace(prefix, namespaces.get(prefix));
			}
			nodes = xPath.selectNodes(XMLUtils.createNonvalidatingSaxBuilder().build(bomInputStream));
			if (nodes.size() == 0) {
				logger.warn("XPath expression did not match any Element. No paths will be updated!");
			}
		} catch (JDOMException e) {throw new RuntimeException(e);}

		try {
			Map<String,DAFile> metsReplacements = new HashMap<String,DAFile>();
			
			for (Object node : nodes) {
				logger.debug("Diving into EAD-node:"+node);
				
				Attribute attr = (Attribute) node;
				
				String value = attr.getValue();
				
				if (value.endsWith(".xml") || value.endsWith(".XML")) {
				
					actualReplacements+= updatePathsInFile(pkg, repName, value, xpathsToUrls.get("METS"), replacements);
					
					for (Event e:pkg.getEvents()) {
						if(e.getType().equals("COPY") || e.getType().equals("CONVERT")) {
							if(e.getSource_file().getRelative_path().contains(value)) {
								metsReplacements.put(value, e.getTarget_file());
							}
						}
					}
				}
			}
			logger.debug("Planned mets replacements: {}", metsReplacements);
			updatePathsInFile(pkg, repName, metadataFilePath, xPathPath, metsReplacements); // Updates of xml path in EAD file	
			
		} catch(Exception err) {
			throw new UserException(UserExceptionId.REPLACE_URLS_IN_METADATA_ERROR,
					"Could not replace file URLs in XML metadata.", metadataFilePath, err);
		}
		
		if (expectedReplacements!=actualReplacements) {
			throw new UserException(UserExceptionId.INCONSISTENT_PACKAGE,
					expectedReplacements+" file(s) have been converted and for each one an entry in a METS file has to be updated. "+
			"but only "+actualReplacements+" replacements could be done.", metadataFilePath, new Exception());
		}
	}


	
	
	/**
	 * Update paths in a packages metadata.
	 *
	 * @param pkg the current package
	 * @param packageType the metadata type of the package
	 * @param metadataFilePath the metadata file path
	 * @param repName the representation affected
	 * @param absUrlPrefix a prefix for generating absolute URLs, can be null
	 * @throws IOException 
	 */
	private void updatePathsInMetadata(
			Package pkg,
			String packageType,
			String metadataFilePath,
			String repName,
			String absUrlPrefix) throws IOException {
		
		if (absUrlPrefix == null) absUrlPrefix = "";
		Map<String,DAFile> replacements = generateReplacementsMap(pkg, repName, absUrlPrefix);
		
		// replace paths in elements denoted by xpath
		String xPathPath = xpathsToUrls.get(packageType);
		logger.debug("xPathPath: "+xPathPath);
		
		updatePathsInFile(pkg, repName, metadataFilePath, xPathPath, replacements);			
	}


	private Map<String,DAFile> generateReplacementsMap(Package pkg,String repName,String absUrlPrefix) throws IOException{
		
		logger.debug("Generate replacements");
		   
		//  relativePath,DAFile
		//  Mimetype, DAFile
		Map<String,DAFile> replacements = new HashMap<String,DAFile>();
		
		// collect paths to be replaced in map
		for (Event e:pkg.getEvents()) {
		
			logger.debug("Event type: "+e.getType());
			
			if (!"CONVERT".equals(e.getType())) {
				continue;
			} 
			
			DAFile targetFile = e.getTarget_file();
			DAFile sourceFile = e.getSource_file();
			logger.debug("source file: "+sourceFile.getRelative_path());
			logger.debug("target file: "+targetFile.getRelative_path());
			if (!targetFile.getRep_name().equals(repName)) {
				logger.debug("!targetFile.getRep_name().equals(repName)");
				logger.debug("targetFile.getRep_name(): "+targetFile.getRep_name());
				logger.debug("repName: "+repName);
				continue;
			}
//			MIMETYPE
			String sourceMT = getMtds().detectMimeType(sourceFile);
			logger.debug("Source mimetype: "+sourceMT);
			targetFile.setMimeType(getMtds().detectMimeType(targetFile));
			logger.debug("Target file MIMETYPE: "+targetFile.getMimeType());
			
			logger.debug("Put "+sourceFile.getRelative_path()+" | "+targetFile);
			replacements.put(sourceFile.getRelative_path(), targetFile);
//			MIMETYPE temporary put the same targetFile again! The goal is to have just two DAFiles in the HashMap (replacements.put(sourceFile, targetFile))
			replacements.put(sourceMT, targetFile);
			logger.debug("Put "+sourceMT+" | "+targetFile);
		}
		
		logger.debug("Planned replacements: {}", replacements);
		
		return replacements;
	}
	
	private void updateLoctypeInMetsFile(Element fileElement, String newLoctype) {
		
		fileElement.removeAttribute("LOCTYPE");
		fileElement.setAttribute("LOCTYPE", newLoctype);
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
			
			File destFile = new File(object.getDataPath() + "/" + repName + "/" // XXX same problem with subdirs as above? Daniel M. de Oliveira
					+ metadataFileName);
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
			
			object.setMetadata_file(metadataFileName);
			
			// copy METS-Files if present in EAD-package
			if ("EAD".equals(packageType)) {
				copyXMLsToNewRepresentation(srcMetadataFile.toRegularFile(), repName);
			}
		}
		return metadataFileName;
	}
	
	/**
	 * Update paths in file.
	 *
	 * @param pkg the pkg
	 * @param repName the rep name
	 * @param metadataFilePath the metadata file path
	 * @param xPathPath the x path path
	 * @param replacements the replacements
	 */
	private int updatePathsInFile(
			Package pkg,
			String repName,
			String metadataFilePath,
			String xPathPath,
			Map<String,DAFile> replacements
	) {
		
		logger.debug("Check file for paths to replace: "+metadataFilePath);
		logger.debug("Planned replacements: {}", replacements);
		
		try {
			
			SAXBuilder builder = XMLUtils.createNonvalidatingSaxBuilder();
			File metadataFile = Path.make(pkg.getTransientBackRefToObject().getDataPath(),repName,metadataFilePath).toFile();
			
			FileInputStream fileInputStream = new FileInputStream(metadataFile);
			BOMInputStream bomInputStream = new BOMInputStream(fileInputStream);

			Document doc = builder.build(bomInputStream);
			
			XPath xPath = XPath.newInstance(xPathPath);
			for (String prefix : namespaces.keySet()) {
				xPath.addNamespace(prefix, namespaces.get(prefix));
			}
			@SuppressWarnings("rawtypes")
			List allNodes = xPath.selectNodes(doc);
			List nodes = new ArrayList<Object>();
			logger.debug("allNodes.size(): "+allNodes.size());
			for(Object i: allNodes) {
				try {
					Element element = (Element) i;
					String xmlURI = element.getNamespaceURI();
					if(xmlURI.toUpperCase().contains("LIDO")) {
						nodes = allNodes;
					} else {
						try {
						Attribute attr = element.getChild("FLocat", METS_NS).getAttribute("href", XLINK_NS);
						nodes.add(attr);
						
						Attribute attrMT = element.getAttribute("MIMETYPE");
						nodes.add(attrMT);
						if(!(absUrlPrefix==null)) {
							Element FLocat = element.getChild("FLocat", METS_NS);
							updateLoctypeInMetsFile(FLocat, "URL");
						}
						
						} catch (Exception e) { // swallow exception if cast not succesfull
						}
					} 
				} catch (Exception e) {
//					EAD or XMP
					nodes = allNodes;
				}	
			}
			
			int entitiesReplaced = 0;
			for (Object node : nodes) {
				logger.debug("NODE: "+node);
				String targetValue = null;
				if (node instanceof Attribute) {
					Attribute attr = (Attribute) node;
					String value = attr.getValue();
					if (replacements.containsKey(value)) {	
						if(attr.getName().equals("MIMETYPE")) {
							targetValue = replacements.get(value).getMimeType();
						} 
						else {
							if(isLZA()) {
								targetValue = replacements.get(value).getRelative_path();
							} else {
								targetValue = absUrlPrefix + File.separator + object.getIdentifier() + File.separator + replacements.get(value).getRelative_path();
							}
							entitiesReplaced++;
						}
						logger.debug("-- Replacing attribute \"{}\" with \"{}\"", attr.getValue(),targetValue);
						attr.setValue(targetValue);
					}
				} else if (node instanceof Element) {
//					LIDO
					Element elem = (Element) node;
					String value = elem.getText();
					
					if (replacements.containsKey(value)) {
						if(isLZA()) {
							targetValue = replacements.get(value).getRelative_path();
						} else {
							targetValue = absUrlPrefix + File.separator + object.getIdentifier() + File.separator + replacements.get(value).getRelative_path();
						}
						if (replacements.containsKey(value)) {
							logger.debug("-- Replacing element \"{}\" with \"{}\"", elem.getValue(),replacements.get(value));
							elem.setText(targetValue);
							entitiesReplaced++;
						}
					}
				}
			}
			if ((nodes.size() == 0)||(entitiesReplaced == 0 )) {
				logger.warn("XPath expression did not match any Element. No paths will be updated!");
				return 0;
			}
			
			XMLOutputter outputter = new XMLOutputter();
			outputter.setFormat(Format.getPrettyFormat());
			outputter.output(doc, new FileWriter(metadataFile));
			
			return entitiesReplaced;
			
		} catch (Exception err) {
			throw new UserException(UserExceptionId.REPLACE_URLS_IN_METADATA_ERROR,
					"Could not replace file URLs in XML metadata.", metadataFilePath, err);
		}
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
					if (!repName.startsWith("dip")) continue;
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
	
	private void copyXMLsToNewRepresentation(File srcFile, String repName) 
			throws IOException {
		
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
				
				String destFilePath = Path.make(destDir.getAbsolutePath(), xmlFile.getName()).toString();						
				String xmlFileRelativePath = destFilePath.replace(object.getDataPath() +"/"+ repName + "/", "");
				DAFile daFile = new DAFile(object.getLatestPackage(), repName, xmlFileRelativePath);
											
				Event e = new Event();							
				for (Package p : object.getPackages()) {
					for (DAFile f : p.getFiles()) {
						if (xmlFile.getAbsolutePath()
								.equals(f.toRegularFile().getAbsolutePath())) {
							e.setSource_file(f);
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

	@Override
	void rollback() throws Exception {
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
	 * Gets the prefix prepended to the updated file URLs.
	 * @return
	 */
	public String getAbsUrlPrefix() {
		return absUrlPrefix;
	}

	/**
	 * Sets the prefix prepended to the updated file URLs.
	 * If the prefix is null (default) the generated URLs
	 * will be relative.
	 * @param absUrlPrefix
	 */
	public void setAbsUrlPrefix(String absUrlPrefix) {
		this.absUrlPrefix = absUrlPrefix;
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
	
	public boolean isLZA() {
		if(absUrlPrefix==null) {
			return true;
		} else {
			return false;
		}
	}

}
