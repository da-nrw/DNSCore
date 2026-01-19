package de.uzk.hki.da.cb;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

import javax.xml.parsers.ParserConfigurationException;
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
import de.uzk.hki.da.model.DAFile;
import de.uzk.hki.da.model.DocumentsGenService;
import de.uzk.hki.da.model.Job;
import de.uzk.hki.da.model.Object;
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

public class QualityLevelRecheckAction extends AbstractAction {
	private GridFacade gridRoot;
	private RegisterObjectService registerObjectService;
	private DocumentsGenService dgs = new DocumentsGenService();
	public QualityLevelRecheckAction() {
		SUPPRESS_OBJECT_CONSISTENCY_CHECK = true;
	}

	private Path newTar;

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
		
		registerObjectService.registerObject(origName, contractor);
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

	private void moveNewestPremisToDIP(Path tempFolder) throws IOException {
		File dest = Path.makeFile(tempFolder, WorkArea.DATA, C.PREMIS_XML);
		FileUtils.copyFile(wa.toFile(o.getLatest(C.PREMIS_XML)), dest);
	}

	private Path createTmpFolder() {
		Path tmpFolder = Path.make(n.getWorkAreaRootPath(), WorkArea.WORK, o.getContractor().getShort_name(),
				o.getIdentifier(), o.getIdentifier());
		tmpFolder.toFile().mkdir();
		return tmpFolder;
	}

	private void bagitAndTarit(Path tempFolder) {

		BagitUtils.buildBagit(tempFolder.toString());

		logger.debug("Building tar at " + newTar);
		try {
			ArchiveBuilder builder = ArchiveBuilderFactory.getArchiveBuilderForFile(new File(FILE_EXTENSION_TAR));
			builder.archiveFolder(tempFolder.toFile(), newTar.toFile(), true);
		} catch (Exception e) {
			throw new RuntimeException("Tar couldn't be packed", e);
		}
	}

	private void cleanupFS() throws IOException {

		// cleanup

		FolderUtils.deleteDirectorySafe(wa.objectPath().toFile());
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

}
