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
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.commons.io.input.BOMInputStream;
import org.apache.commons.lang.NotImplementedException;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import de.uzk.hki.da.core.ConfigurationException;
import de.uzk.hki.da.metadata.XmpCollector;
import de.uzk.hki.da.metadata.XsltGenerator;
import de.uzk.hki.da.model.DAFile;
import de.uzk.hki.da.model.Event;
import de.uzk.hki.da.model.Package;
import de.uzk.hki.da.service.UpdateMetadataService;

/**
 * Performs updates to metadata file that are necessary
 * to keep the metadata, especially the paths to referenced
 * files, in sync with the actual package content after
 * conversion actions took place.
 * @author Sebastian Cuy
 * @author Daniel M. de Oliveira
 *
 */
public class UpdateMetadataAction extends AbstractAction {
	
	private UpdateMetadataService updateMetadataService;
	
	private boolean writePackageTypeToDC = false;
	
	private String[] repNames;
	
	private String absUrlPrefix;

	@Override
	public boolean implementation() throws IOException {
		if (job==null) throw new ConfigurationException("job not set");
		if (actionCommunicatorService==null) throw new ConfigurationException("actionCommunicatorService not set");
		if (updateMetadataService==null) throw new ConfigurationException("updateMetadataService not set");
		
		String packageType = (String) actionCommunicatorService.extractDataObject(job.getId(), "package_type");
		String metadataFileName = (String) actionCommunicatorService.extractDataObject(job.getId(), "metadata_file");
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
		
		
		if ("XMP".equals(packageType)) 
			collectXMP();
		else 
			metadataFileName = copyMetadataFileToNewReps(packageType,
					metadataFileName);

		for (String repName : getRepNames()) {
			getUpdateMetadataService().updatePathsInMetadata(
					object.getLatestPackage(),
					packageType,
					metadataFileName,
					repName,
					absUrlPrefixFull
				);
		}
		
		
		copyDCdatastreamFromMetadata(packageType, metadataFileName);
		if (isWritePackageTypeToDC())
			writePackageTypeToDC(packageType);
		
		
		
			
		return true;
		
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
			File destFile = new File(object.getDataPath() + repName + "/" // XXX same problem with subdirs as above? Daniel M. de Oliveira
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
			actionCommunicatorService.addDataObject(job.getId(), "metadata_file", metadataFileName);
			
			// copy METS-Files if present in EAD-package
			if ("EAD".equals(packageType)) {
				copyXMLsToNewRepresentation(srcMetadataFile.toRegularFile(), repName);
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
			String xsltFile;
			if ("METS".equals(packageType)) {
				xsltFile = "mets-mods_to_dc.xsl";
			} else if ("EAD".equals(packageType)) {
				xsltFile = "ead_to_dc.xsl";
			} else if ("XMP".equals(packageType)) {
				xsltFile = "xmp_to_dc.xsl";
			} else if ("LIDO".equals(packageType)) {
				xsltFile = "lido_to_dc.xsl";
			} else {
				throw new RuntimeException("No conversion available for package type '" + packageType + "'. DC can not be created.");
			}
			try {
				for (String repName : getRepNames()) {
					if (!repName.startsWith("dip")) continue;
					FileInputStream inputStream = new FileInputStream(object.getDataPath() + repName + "/" + metadataFile);
					BOMInputStream bomInputStream = new BOMInputStream(inputStream);
					XsltGenerator xsltGenerator = new XsltGenerator("conf/xslt/dc/" + xsltFile, bomInputStream);
					String result = xsltGenerator.generate();
					File file = new File(object.getDataPath() + repName + "/DC.xml");
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
		File destDir = new File(object.getDataPath() + repName);
		Iterator<File> xmlFiles = FileUtils.iterateFiles(
				srcFile.getParentFile(), new WildcardFileFilter("*.xml"), null);
		int count=0;
		while (xmlFiles.hasNext()) {
			count++;
			
			File xmlFile = xmlFiles.next();
			FileUtils.copyFileToDirectory(xmlFile, destDir);
			
			String destFilePath = destDir.getAbsolutePath() + "/" + xmlFile.getName();							
			String xmlFileRelativePath = destFilePath.replace(object.getDataPath() + repName + "/", "");
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

	/**
	 * Copy xmp sidecar files and collect them into one "XMP manifest"
	 * @throws IOException
	 */
	private void collectXMP() throws IOException {
		for (String repName : getRepNames()) {
			String repPath = object.getDataPath() + repName;
			File repDir = new File(repPath);
			if (!repDir.exists()) {
				logger.info("representation directory {} does not exist. Skipping ...", repPath);
				continue;
			}
			List<DAFile> files = object.getNewestFilesFromAllRepresentations("xmp;XMP");
			for (DAFile file : files) {
				logger.debug("checking if file is xmp sidecar: {}", file);
				if (file.getRelative_path().toLowerCase().endsWith(".xmp")
						&& !Arrays.asList(repNames).contains(file.getRep_name())) {

					File targetDir = new File(repPath+"/"+FilenameUtils.getPath(file.getRelative_path()));
					
					logger.debug("Copying {} to {}", file, targetDir);
					FileUtils.copyFileToDirectory(file.toRegularFile(), targetDir);
					DAFile daFile = 
							new DAFile(object.getLatestPackage(), repName, file.getRelative_path());
					daFile.setFormatPUID(file.getFormatPUID());
					object.getLatestPackage().getFiles().add(daFile);
					
					Event e = new Event();							
					for (Package p : object.getPackages()) {
						for (DAFile f : p.getFiles()) {
							if (file.toRegularFile().getAbsolutePath()
									.equals(f.toRegularFile().getAbsolutePath())) {
								e.setSource_file(f);
							}
						}
					}							
					e.setTarget_file(daFile);
					e.setType("COPY");
					e.setDate(new Date());
					e.setAgent_type("NODE");
					e.setAgent_name(object.getTransientNodeRef().getName());							
					object.getLatestPackage().getEvents().add(e);
					logger.debug("created DAFile: {}", daFile);
				}
			}
			getUpdateMetadataService().renameSidecarFiles(object, object.getLatestPackage(), repName);
			logger.debug("collecting files in path: {}", repPath);
			
			XmpCollector.collect(repDir, new File(repPath + "/XMP.rdf"));
			object.getLatestPackage().getFiles().add(
					new DAFile(object.getLatestPackage(),repName,"XMP.rdf"));
		}
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
				File file = new File(object.getDataPath() + repName + "/DC.xml");
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
					String dcPath = object.getDataPath() + repName + "/DC.xml";
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

	public UpdateMetadataService getUpdateMetadataService() {
		return updateMetadataService;
	}

	public void setUpdateMetadataService(UpdateMetadataService updateMetadataService) {
		this.updateMetadataService = updateMetadataService;
	}

	public boolean isWritePackageTypeToDC() {
		return writePackageTypeToDC;
	}

	public void setWritePackageTypeToDC(boolean writePackageTypeToDC) {
		this.writePackageTypeToDC = writePackageTypeToDC;
	}

	public String[] getRepNames() {
		return repNames;
	}

	public void setRepNames(String[] repNames) {
		this.repNames = repNames;
	}

	public String getAbsUrlPrefix() {
		return absUrlPrefix;
	}

	public void setAbsUrlPrefix(String absUrlPrefix) {
		this.absUrlPrefix = absUrlPrefix;
	}

}
