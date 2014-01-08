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
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

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
 *
 */
public class UpdateMetadataAction extends AbstractAction {
	
	private UpdateMetadataService updateMetadataService;
	
	private boolean writePackageTypeToDC = false;
	
	private String[] repNames;
	
	private String absUrlPrefix;

	@Override
	public boolean implementation() throws IOException {
		object.reattach();
		
		logConvertEventsOnDebugLevel();
		
		String packageType = (String) actionCommunicatorService.extractDataObject(job.getId(), "package_type");
		String metadataFile = (String) actionCommunicatorService.extractDataObject(job.getId(), "metadata_file");
		logger.debug("Got data from ACS - package_type: {}, metadata_file: {}", packageType, metadataFile);
		if (packageType == null || metadataFile == null) {
			logger.warn("Could not determine package type. No metadata to update.");
			return true;
		}
	
		// replace paths in newest rep when no reps are set
		if (repNames == null || repNames.length == 0) {
			repNames = new String[]{ object.getNameOfNewestRep() };
		}
		
		// copy xmp sidecar files and collect them into one "XMP manifest"
		if ("XMP".equals(packageType)) {
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
						DAFile daFile = new DAFile(object.getLatestPackage(), repName, file.getRelative_path());
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
			}
			
		// copy other metadata to rep(s)
		} else {		
			logger.debug("copy metadata to rep(s)");
			DAFile srcDaFile = object.getLatest(metadataFile);
			File srcFile = srcDaFile.toRegularFile();
			String extension = FilenameUtils.getExtension(srcFile.getName());
			for (String repName : getRepNames()) {
				logger.debug("checking rep " + repName);
				// rename metadatafile for presentation
				if (repName.startsWith("dip")) {
					metadataFile = packageType + "." + extension;
				}
				File destFile = new File(object.getDataPath() + repName + "/" // XXX same problem with subdirs as above? Daniel M. de Oliveira
						+ metadataFile);
				try {
					FileUtils.copyFile(srcFile, destFile);
					DAFile daFile = new DAFile(object.getLatestPackage(), repName, metadataFile);
					daFile.setFormatPUID(srcDaFile.getFormatPUID());
					object.getLatestPackage().getFiles().add(daFile);
					
					Event e = new Event();							
					for (Package p : object.getPackages()) {
						for (DAFile f : p.getFiles()) {
							if (srcDaFile.toRegularFile().getAbsolutePath()
									.equals(f.toRegularFile().getAbsolutePath())) {
								e.setSource_file(f);
								logger.debug("source file: " + f.toRegularFile().getAbsolutePath());
							}
						}
					}							
					e.setTarget_file(daFile);
					logger.debug("target file: " + daFile.toRegularFile().getAbsolutePath());
					e.setType("COPY");
					e.setDate(new Date());
					e.setAgent_type("NODE");
					e.setAgent_name(object.getTransientNodeRef().getName());							
					object.getLatestPackage().getEvents().add(e);
					
					logger.debug("copied metadata file {} to {}", srcFile.getAbsoluteFile(), destFile.getAbsolutePath());
					actionCommunicatorService.addDataObject(job.getId(), "metadata_file", metadataFile);
				} catch (IOException e1) {
					throw new RuntimeException("Unable to copy metadata file!", e1);
				}
				// copy METS-Files if present in EAD-package
				if ("EAD".equals(packageType)) {
					File destDir = new File(object.getDataPath() + repName);
					Iterator<File> xmlFiles = FileUtils.iterateFiles(
							srcFile.getParentFile(), new WildcardFileFilter("*.xml"), null);
					while (xmlFiles.hasNext()) {
						try {
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
						} catch (IOException e1) {
							throw new RuntimeException("Unable to copy metadata file!", e1);
						}
					}
				}
			}
		}
		
		// create DC datastream from metadata
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
		
		if (isWritePackageTypeToDC())
			writePackageTypeToDC(packageType);
		
		String absUrlPrefixFull = "";
		if (getAbsUrlPrefix() != null && !getAbsUrlPrefix().isEmpty()) {
			absUrlPrefixFull = getAbsUrlPrefix() + "/" + job.getObject().getIdentifier() + "/";
		}
		
		// update filepaths in metadata		
		for (String repName : getRepNames()) {
			getUpdateMetadataService().updatePathsInMetadata(
					object.getLatestPackage(),
					packageType,
					metadataFile,
					repName,
					absUrlPrefixFull
				);
		}
			
		return true;
		
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
