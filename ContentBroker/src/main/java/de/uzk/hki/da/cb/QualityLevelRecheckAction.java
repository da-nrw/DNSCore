package de.uzk.hki.da.cb;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TimeZone;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import java.util.stream.Stream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.jdom.JDOMException;
import org.xml.sax.SAXException;

import de.uzk.hki.da.action.AbstractAction;
import de.uzk.hki.da.core.MailContents;
import de.uzk.hki.da.core.RegisterObjectService;
import de.uzk.hki.da.core.SubsystemNotAvailableException;
import de.uzk.hki.da.core.UserException;
import de.uzk.hki.da.grid.GridFacade;
import de.uzk.hki.da.model.Agent;
import de.uzk.hki.da.model.DAFile;
import de.uzk.hki.da.model.DocumentsGenService;
import de.uzk.hki.da.model.Event;
import de.uzk.hki.da.model.Job;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.model.ObjectPremisXmlReader;
import de.uzk.hki.da.model.ObjectPremisXmlWriter;
import de.uzk.hki.da.model.Package;
import de.uzk.hki.da.model.User;
import de.uzk.hki.da.model.WorkArea;
import de.uzk.hki.da.pkg.ArchiveBuilder;
import de.uzk.hki.da.pkg.ArchiveBuilderFactory;
import de.uzk.hki.da.pkg.BagitUtils;
import de.uzk.hki.da.repository.RepositoryException;
import de.uzk.hki.da.service.HibernateUtil;
import de.uzk.hki.da.util.ConfigurationException;
import de.uzk.hki.da.utils.C;
import de.uzk.hki.da.utils.FolderUtils;
import de.uzk.hki.da.utils.Path;

import static de.uzk.hki.da.utils.C.FILE_EXTENSION_TAR;

import java.io.File;
import java.io.FileInputStream;

public class QualityLevelRecheckAction extends AbstractAction {
	private GridFacade gridRoot;
	private RegisterObjectService registerObjectService;
	private DocumentsGenService dgs = new DocumentsGenService();
	private static final String PREMIS = "premis.xml";
	public QualityLevelRecheckAction() {
		SUPPRESS_OBJECT_CONSISTENCY_CHECK = true;
	}


	@Override
	public void checkConfiguration() {
		if (gridRoot == null)
			throw new ConfigurationException("gridRoot");
	}

	@Override
	public void checkPreconditions() {
		// if (!wa.objectPath().toFile().exists()) throw new
		// IllegalStateException("object data path on fs doesn't exist on fs");
		// if (!wa.toFile(o.getLatest(C.PREMIS_XML)).exists()) throw new
		// RuntimeException("CRITICAL ERROR: premis file could has not been found");

	}

	@Override
	public boolean implementation() throws FileNotFoundException, IOException, UserException, RepositoryException,
			JDOMException, ParserConfigurationException, SAXException, SubsystemNotAvailableException {
		logger.debug("QUALITY LEVEL RECHECK STARTED.");
		
		clearNonpersistentObjectProperties(o);
		String targetFilename = o.getIdentifier();
		Path targetFilePath = Path.make(n.getWorkAreaRootPath(), "work", o.getContractor().getShort_name(),
				targetFilename);
		Path tempFilePath = Path.make(n.getWorkAreaRootPath(), "work", o.getContractor().getShort_name(),
				"temp",targetFilename);
		Path tempFolder = Path.make(n.getWorkAreaRootPath(), "work", o.getContractor().getShort_name(),
				"temp");
		if (!(new File(targetFilePath.toString()).exists())) {
			logger.debug(targetFilePath + " already exists. iget can be skipped.");
			// return true;
		}
		RetrievePackagesHelper helper = new RetrievePackagesHelper(gridRoot, wa);
		try {
			helper.loadPackages(o, true);
		} catch (IOException e) {
			throw new RuntimeException("error while trying to get existing packages from lza area",e);
		}
		//deleteFolder(Paths.get(targetFilePath.toString()));
		dgs.addDocumentsToObject(o);
		copyARepresentationToWorkArea(tempFilePath);
		File oldSipPREMISFile = new File(Path.make(wa.dataPath(),o.getNameOfLatestBRep(),PREMIS).toString().replace("+b", "+a"));
		File newSipPREMISFile = new File(Path.make(wa.dataPath(),o.getNameOfLatestBRep(),PREMIS+".tmp").toString().replace("+b", "+a"));
		writeQualityLevelRecheckPremisXml(
				newSipPREMISFile,
			    o.getOrig_name(),
			    new Date(),
			    oldSipPREMISFile        // nur lesen
			);
		// atomarer Replace
		Files.move(
			newSipPREMISFile.toPath(),
		    oldSipPREMISFile.toPath(),
		    StandardCopyOption.REPLACE_EXISTING,
		    StandardCopyOption.ATOMIC_MOVE
		);
		deleteFolder(Paths.get(targetFilePath.toString()));
		File unarchivedATargetFile = tempFilePath.toFile();
		File ingestArchviedATargetFile = Paths.get(n.getIngestAreaRootPath() + File.separator
				+ o.getContractor().getShort_name() + File.separator + o.getOrig_name() + ".tgz").toFile();
		BagitUtils.buildBagit(unarchivedATargetFile.toString());
		try {
			ArchiveBuilder builder = ArchiveBuilderFactory.getArchiveBuilderForFile(ingestArchviedATargetFile);
			builder.archiveFolder(unarchivedATargetFile, ingestArchviedATargetFile, true);
		} catch (Exception e) {
			throw new IOException(
					"Existing AIP couldn't be unpacked to folder ", e);
		}
		deleteFolder(Paths.get(tempFolder.toString()));
		String origName = o.getOrig_name();
		User contractor = o.getContractor();
		Session session = HibernateUtil.openSession();
		Transaction tx = session.beginTransaction();
		try {
			o.setObject_state(Object.ObjectStatus.ArchivedAndValidAndNotInWorkflow);
			session.saveOrUpdate(o);
			session.getTransaction().commit();
		} catch (Exception e) {
			tx.rollback();
			throw e;
		} finally {
			session.close();
		}
		
		o = registerObjectService.registerObject(origName + ".tgz", contractor);
		return true;
	}

	@Override
	public void rollback() throws Exception {
		// TODO Auto-generated method stub

	}

	public GridFacade getGridRoot() {
		return gridRoot;
	}

	public void setGridRoot(GridFacade gridRoot) {
		this.gridRoot = gridRoot;
	}



	private void specialRetrieval(Path tempFolder) throws IOException {

		for (Package p : packagesToRetrieve()) {

			for (DAFile f : p.getFiles()) {

				File destDir = Path.makeFile(tempFolder, WorkArea.DATA, f.getRep_name(),
						FilenameUtils.getPath(f.getRelative_path()));
				destDir.mkdirs();
				FileUtils.copyFileToDirectory(wa.toFile(f), destDir);
			}
		}
	}

	private Set<Package> packagesToRetrieve() {

		String pp[] = j.getQuestion().replace("RETRIEVE:", "").split(",");
		Set<Package> packagesToRetrieve = new HashSet<Package>();

		for (int i = 0; i < pp.length; i++) {

			for (Package p_ : o.getPackages()) {
				if (p_.getDelta().toString().equals(pp[i]))
					packagesToRetrieve.add(p_);
			}
		}

		return packagesToRetrieve;
	}

	

	/**
	 * @param tempFolder
	 * @throws RuntimeException
	 * @throws IOException
	 */
	private void copyARepresentationToWorkArea(Path tempFolder) throws RuntimeException, IOException {

		String sce = "";
		if (o.getFriendlyFileExtensions() != null)
			sce = o.getFriendlyFileExtensions();

		List<DAFile> files = o.getNewestFilesFromARepresentations(sce);
		for (DAFile f : files) {
			File dest = Path.makeFile(tempFolder, WorkArea.DATA, f.getRelative_path());
			logger.info("file will be part of pip: " + dest.getAbsolutePath());
			String destFolder = dest.getAbsolutePath().substring(0, dest.getAbsolutePath().lastIndexOf("/"));

			new File(destFolder).mkdirs();
			FileUtils.copyFile(wa.toFile(f), dest);
		}
	}

	private static LocalDateTime parseDateSafe(String folderName) {
		DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy_MM_dd+HH_mm_ss");
		try {
			// alles bis vor "+a" bzw. "+b"
			String datePart = folderName.substring(0, folderName.lastIndexOf('+'));
			return LocalDateTime.parse(datePart, FORMATTER);
		} catch (DateTimeParseException | IndexOutOfBoundsException e) {
			return null; // ungültiger Name → ignorieren
		}
	}

	public static Optional<File> findNewestA(File baseDir) {
		if (baseDir == null || !baseDir.isDirectory()) {
			return Optional.empty();
		}

		File[] dirs = baseDir.listFiles(File::isDirectory);
		if (dirs == null) {
			return Optional.empty();
		}

		return Arrays.stream(dirs).filter(dir -> dir.getName().endsWith("+a"))
				.filter(dir -> parseDateSafe(dir.getName()) != null)
				.max(Comparator.comparing(dir -> parseDateSafe(dir.getName())));
	}

	public static String getFilenameWithoutExtension(String path) {
		java.nio.file.Path p = java.nio.file.Paths.get(path);
		String filename = p.getFileName().toString();

		int dotIndex = filename.lastIndexOf('.');
		return (dotIndex == -1) ? filename : filename.substring(0, dotIndex);
	}

	public static void copyMetadataAndData(java.nio.file.Path metadataSourceDir, java.nio.file.Path targetDir,
			java.nio.file.Path dataSourceDir) throws IOException {

		if (metadataSourceDir == null || targetDir == null || dataSourceDir == null) {
			throw new IllegalArgumentException("Paths must not be null.");
		}
		if (!Files.isDirectory(metadataSourceDir)) {
			throw new IllegalArgumentException("metadataSourceDir is not a directory: " + metadataSourceDir);
		}
		if (!Files.isDirectory(dataSourceDir)) {
			throw new IllegalArgumentException("dataSourceDir is not a directory: " + dataSourceDir);
		}

// Ensure target exists
		Files.createDirectories(targetDir);

// 1) Copy metadata files (non-recursive) into targetDir
		try (DirectoryStream<java.nio.file.Path> stream = Files.newDirectoryStream(metadataSourceDir)) {
			for (java.nio.file.Path p : stream) {
				if (Files.isRegularFile(p)) {
					Files.copy(p, targetDir.resolve(p.getFileName()), StandardCopyOption.REPLACE_EXISTING,
							StandardCopyOption.COPY_ATTRIBUTES);
				}
			}
		}

// 2) Create data folder in targetDir
		java.nio.file.Path dataTargetDir = targetDir.resolve("data");
		Files.createDirectories(dataTargetDir);

// 3) Copy data files (non-recursive) into targetDir/data
		try (DirectoryStream<java.nio.file.Path> stream = Files.newDirectoryStream(dataSourceDir)) {
			for (java.nio.file.Path p : stream) {
				if (Files.isRegularFile(p)) {
					Files.copy(p, dataTargetDir.resolve(p.getFileName()), StandardCopyOption.REPLACE_EXISTING,
							StandardCopyOption.COPY_ATTRIBUTES);
				}
			}
		}
	}

	public static void copyData(java.nio.file.Path targetDir, java.nio.file.Path dataSourceDir) throws IOException {

		if (!Files.isDirectory(dataSourceDir)) {
			throw new IllegalArgumentException("dataSourceDir is not a directory: " + dataSourceDir);
		}

// Ensure target exists
		Files.createDirectories(targetDir);

// 1) Create data folder in targetDir
		java.nio.file.Path dataTargetDir = targetDir.resolve("data");
		Files.createDirectories(dataTargetDir);

// 2) Copy data files (non-recursive) into targetDir/data
		try (DirectoryStream<java.nio.file.Path> stream = Files.newDirectoryStream(dataSourceDir)) {
			for (java.nio.file.Path p : stream) {
				if (Files.isRegularFile(p)) {
					Files.copy(p, dataTargetDir.resolve(p.getFileName()), StandardCopyOption.REPLACE_EXISTING,
							StandardCopyOption.COPY_ATTRIBUTES);
				}
			}
		}
	}

	public static void deleteTarAndTempFolder(java.nio.file.Path tarFile, java.nio.file.Path tempFolder)
			throws IOException {

		// 1) Delete tar file
		if (tarFile != null && Files.exists(tarFile)) {
			if (!Files.isRegularFile(tarFile)) {
				throw new IOException("Not a file: " + tarFile);
			}
			Files.delete(tarFile);
		}

		// 2) Delete temp folder recursively
		deleteFolder(tempFolder);
	}
	public static Object parsePremisFile(File premisFile) {
		
		Object premisData;
		
		ObjectPremisXmlReader reader = new ObjectPremisXmlReader();
		try {
			premisData = reader.deserialize(premisFile);
		} catch (IOException e) {
			throw new RuntimeException("Couldn't read file " + premisFile.getAbsolutePath(), e);
		} catch (ParseException pe){
			throw new RuntimeException("Error while parsing premis file", pe);
		}
		return premisData;
	}
	public static void addQualityLevelRecheckEventToPremis(File premisFile)throws IOException {
		Object sipPREMISObject = parsePremisFile(premisFile);
		System.out.println("DEBUG");
	}
	public static void deleteFolder(java.nio.file.Path tempFolder) throws IOException {

		if (tempFolder != null && Files.exists(tempFolder)) {
			if (!Files.isDirectory(tempFolder)) {
				throw new IOException("Not a directory: " + tempFolder);
			}

			Files.walkFileTree(tempFolder, new SimpleFileVisitor<java.nio.file.Path>() {

				@Override
				public FileVisitResult visitFile(java.nio.file.Path file, BasicFileAttributes attrs)
						throws IOException {
					Files.delete(file);
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult postVisitDirectory(java.nio.file.Path dir, IOException exc) throws IOException {
					if (exc != null) {
						throw exc;
					}
					Files.delete(dir);
					return FileVisitResult.CONTINUE;
				}
			});
		}
	}
	public void setRegisterObjectService(RegisterObjectService registerObjectService) {
		this.registerObjectService = registerObjectService;
	}
	
	public RegisterObjectService getRegisterObjectService() {
		return registerObjectService;
	}
	public static void writeQualityLevelRecheckPremisXml(
            File targetPremisFile,
            String packageName,
            Date eventDate,
            File rightsSourcePremis
    ) throws IOException {

        Date ts = (eventDate != null) ? eventDate : new Date();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        dateFormat.setTimeZone(TimeZone.getTimeZone("Europe/Berlin"));
        String tsString = formatDateLikeSipBuilder(ts, dateFormat);

        XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
        try (FileOutputStream out = new FileOutputStream(targetPremisFile)) {

            XMLStreamWriter w;
            try {
                w = outputFactory.createXMLStreamWriter(out, "UTF-8");
            } catch (XMLStreamException e) {
                throw new IOException("Failed to create XMLStreamWriter", e);
            }

            try {
                w.writeStartDocument("UTF-8", "1.0");
                w.setPrefix("xsi", C.XSI_NS_URI);

                // <premis ...>
                w.writeDTD("\n");
                w.writeStartElement("premis");
                w.writeAttribute("xmlns", "info:lc/xmlns/premis-v2");
                w.writeAttribute("xmlns:premis", "info:lc/xmlns/premis-v2");
                w.writeAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
                w.writeAttribute(C.XSI_NS_URI, "schemaLocation",
                        "info:lc/xmlns/premis-v2 http://www.loc.gov/standards/premis/v2/premis-v2-2.xsd");
                w.writeAttribute("version", "2.2");

                // object
                writeObjectRepresentation(w, packageName);

                // event (QUALITY_LEVEL_RECHECK)
                writeQualityLevelRecheckEvent(w, packageName, tsString);

                // agent (APPLICATION)
                writeQualityLevelRecheckAgent(w);

                // rights: copy or stub
                if (rightsSourcePremis != null) {
                    copyRightsElementFromPremisFile(w, rightsSourcePremis);
                } else {
                    writeMinimalRightsStub(w, tsString);
                }

                // </premis>
                w.writeDTD("\n");
                w.writeEndElement();

                w.writeDTD("\n");
                w.writeEndDocument();
                w.close();

            } catch (XMLStreamException e) {
                throw new IOException("Failed to write premis.xml", e);
            }
        }
    }

    // ---------- helpers (private static) ----------

    private static void writeObjectRepresentation(XMLStreamWriter w, String packageName) throws XMLStreamException {
        w.writeDTD("\n    ");
        w.writeStartElement("object");
        w.writeAttribute(C.XSI_NS_URI, "type", "representation");

        w.writeDTD("\n        ");
        w.writeStartElement("objectIdentifier");

        writeSimpleText(w, 3, "objectIdentifierType", "PACKAGE_NAME");
        writeSimpleText(w, 3, "objectIdentifierValue", packageName);

        w.writeDTD("\n        ");
        w.writeEndElement(); // objectIdentifier

        w.writeDTD("\n    ");
        w.writeEndElement(); // object
        w.writeDTD("\n");
    }

    private static void writeQualityLevelRecheckEvent(XMLStreamWriter w, String packageName, String tsString)
            throws XMLStreamException {

        w.writeDTD("\n    ");
        w.writeStartElement("event");

        w.writeDTD("\n        ");
        w.writeStartElement("eventIdentifier");

        writeSimpleText(w, 3, "eventIdentifierType", "QUALITY_LEVEL_RECHECK_ID");
        writeSimpleText(w, 3, "eventIdentifierValue", "Quality_Level_Recheck_" + tsString);

        w.writeDTD("\n        ");
        w.writeEndElement(); // eventIdentifier

        writeSimpleText(w, 2, "eventType", "QUALITY_LEVEL_RECHECK");
        writeSimpleText(w, 2, "eventDateTime", tsString);

        w.writeDTD("\n        ");
        w.writeStartElement("linkingAgentIdentifier");
        writeSimpleText(w, 3, "linkingAgentIdentifierType", "APPLICATION_NAME");
        writeSimpleText(w, 3, "linkingAgentIdentifierValue", "DA NRW Quality-Level-Recheck");
        w.writeDTD("\n        ");
        w.writeEndElement(); // linkingAgentIdentifier

        w.writeDTD("\n        ");
        w.writeStartElement("linkingObjectIdentifier");
        writeSimpleText(w, 3, "linkingObjectIdentifierType", "PACKAGE_NAME");
        writeSimpleText(w, 3, "linkingObjectIdentifierValue", packageName);
        w.writeDTD("\n        ");
        w.writeEndElement(); // linkingObjectIdentifier

        w.writeDTD("\n    ");
        w.writeEndElement(); // event
        w.writeDTD("\n");
    }

    private static void writeQualityLevelRecheckAgent(XMLStreamWriter w) throws XMLStreamException {
        w.writeDTD("\n    ");
        w.writeStartElement("agent");

        w.writeDTD("\n        ");
        w.writeStartElement("agentIdentifier");
        writeSimpleText(w, 3, "agentIdentifierType", "APPLICATION_NAME");
        writeSimpleText(w, 3, "agentIdentifierValue", "DA NRW Quality-Level-Recheck");
        w.writeDTD("\n        ");
        w.writeEndElement(); // agentIdentifier

        writeSimpleText(w, 2, "agentType", "APPLICATION");

        w.writeDTD("\n    ");
        w.writeEndElement(); // agent
        w.writeDTD("\n");
    }

    private static void writeMinimalRightsStub(XMLStreamWriter w, String tsString) throws XMLStreamException {
        // Intentionally minimal fallback. Prefer copying from existing premis.xml.
        String startOfDay = tsString.substring(0, 10) + "T00:00:00.000+01:00";

        w.writeDTD("\n    ");
        w.writeStartElement("rights");

        w.writeDTD("\n        ");
        w.writeStartElement("rightsStatement");

        w.writeDTD("\n            ");
        w.writeStartElement("rightsStatementIdentifier");
        writeSimpleText(w, 4, "rightsStatementIdentifierType", "rightsid");
        writeSimpleText(w, 4, "rightsStatementIdentifierValue", "");
        w.writeDTD("\n            ");
        w.writeEndElement(); // rightsStatementIdentifier

        writeSimpleText(w, 3, "rightsBasis", "license");

        w.writeDTD("\n            ");
        w.writeStartElement("rightsGranted");
        writeSimpleText(w, 4, "act", "MIGRATION");
        writeSimpleText(w, 4, "restriction", "see rightsExtension");
        w.writeDTD("\n                ");
        w.writeStartElement("termOfGrant");
        writeSimpleText(w, 5, "startDate", startOfDay);
        w.writeDTD("\n                ");
        w.writeEndElement(); // termOfGrant
        w.writeDTD("\n            ");
        w.writeEndElement(); // rightsGranted

        w.writeDTD("\n        ");
        w.writeEndElement(); // rightsStatement

        w.writeDTD("\n    ");
        w.writeEndElement(); // rights
        w.writeDTD("\n");
    }

    /**
     * Copies the entire <rights>...</rights> element from an existing premis.xml into the current writer.
     */
    private static void copyRightsElementFromPremisFile(XMLStreamWriter w, File rightsSourcePremis)
            throws XMLStreamException, IOException {

        try (FileInputStream in = new FileInputStream(rightsSourcePremis)) {
            XMLInputFactory inputFactory = XMLInputFactory.newInstance();
            XMLStreamReader r = inputFactory.createXMLStreamReader(in);

            boolean inRights = false;
            int depth = 0;

            while (r.hasNext()) {
                int ev = r.next();

                if (ev == XMLStreamConstants.START_ELEMENT) {
                    String local = r.getLocalName();

                    if (!inRights && "rights".equals(local)) {
                        inRights = true;
                        depth = 1;

                        // start <rights>
                        w.writeDTD("\n    ");
                        w.writeStartElement("rights");
                        continue;
                    }

                    if (inRights) {
                        depth++;
                        w.writeDTD("\n" + indent(Math.min(depth + 1, 20)));
                        w.writeStartElement(local);

                        // copy namespaces
                        for (int i = 0; i < r.getNamespaceCount(); i++) {
                            String nsPrefix = r.getNamespacePrefix(i);
                            String nsUri = r.getNamespaceURI(i);
                            if (nsPrefix == null) nsPrefix = "";
                            w.writeNamespace(nsPrefix, nsUri);
                        }

                        // copy attributes (important for rightsExtension subtree)
                        for (int i = 0; i < r.getAttributeCount(); i++) {
                            String aPrefix = r.getAttributePrefix(i);
                            String aNs = r.getAttributeNamespace(i);
                            String aLocal = r.getAttributeLocalName(i);
                            String aVal = r.getAttributeValue(i);

                            if (aPrefix != null && !aPrefix.isEmpty()) {
                                w.writeAttribute(aPrefix, aNs, aLocal, aVal);
                            } else if (aNs != null && !aNs.isEmpty()) {
                                w.writeAttribute(aNs, aLocal, aVal);
                            } else {
                                w.writeAttribute(aLocal, aVal);
                            }
                        }
                    }
                } else if (ev == XMLStreamConstants.CHARACTERS) {
                    if (inRights && !r.isWhiteSpace()) {
                        w.writeCharacters(r.getText());
                    }
                } else if (ev == XMLStreamConstants.END_ELEMENT) {
                    String local = r.getLocalName();

                    if (inRights) {
                        if ("rights".equals(local)) {
                            w.writeDTD("\n    ");
                            w.writeEndElement(); // </rights>
                            inRights = false;
                            continue;
                        }
                        depth--;
                        w.writeEndElement();
                    }
                }
            }

            r.close();
        }
    }

    private static void writeSimpleText(XMLStreamWriter w, int indentLevel, String name, String text)
            throws XMLStreamException {
    	w.writeDTD("\n" + indent(indentLevel));
        w.writeStartElement(name);
        w.writeCharacters(text != null ? text : "");
        w.writeEndElement();
    }

    private static String formatDateLikeSipBuilder(Date date, SimpleDateFormat format) {
        String dateString = format.format(date);
        // "...+0100" -> "...+01:00"
        if (dateString.endsWith("00") && !dateString.endsWith(":00")) {
            dateString = dateString.substring(0, dateString.length() - 2) + ":00";
        }
        return dateString;
    }

    private static String indent(int level) {
        StringBuilder sb = new StringBuilder(level * 4);
        for (int i = 0; i < level; i++) {
            sb.append("    ");
        }
        return sb.toString();
    }



}
