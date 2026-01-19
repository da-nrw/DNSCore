package de.uzk.hki.da.cb;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Comparator;
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
import org.jdom.JDOMException;
import org.xml.sax.SAXException;

import de.uzk.hki.da.action.AbstractAction;
import de.uzk.hki.da.core.MailContents;
import de.uzk.hki.da.core.RegisterObjectService;
import de.uzk.hki.da.core.SubsystemNotAvailableException;
import de.uzk.hki.da.core.UserException;
import de.uzk.hki.da.grid.GridFacade;
import de.uzk.hki.da.model.DAFile;
import de.uzk.hki.da.model.Package;
import de.uzk.hki.da.model.User;
import de.uzk.hki.da.model.WorkArea;
import de.uzk.hki.da.pkg.ArchiveBuilder;
import de.uzk.hki.da.pkg.ArchiveBuilderFactory;
import de.uzk.hki.da.pkg.BagitUtils;
import de.uzk.hki.da.repository.RepositoryException;
import de.uzk.hki.da.util.ConfigurationException;
import de.uzk.hki.da.utils.C;
import de.uzk.hki.da.utils.FolderUtils;
import de.uzk.hki.da.utils.Path;

import static de.uzk.hki.da.utils.C.FILE_EXTENSION_TAR;

import java.io.File;

public class QualityLevelRecheckAction extends AbstractAction {
	private GridFacade gridRoot;
	
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
		// newTar =
		// Path.make(n.getWorkAreaRootPath(),o.getContractor().getShort_name(),o.getIdentifier()
		// + FILE_EXTENSION_TAR);
		/*
		 * Path tempFolder = createTmpFolder();
		 * 
		 * 
		 * if (j.getQuestion()==null||j.getQuestion().isEmpty()){
		 * stdRetrieval(tempFolder); } else if
		 * (j.getQuestion().startsWith("RETRIEVE:")){ specialRetrieval(tempFolder); }
		 * 
		 * 
		 * bagitAndTarit(tempFolder);
		 * 
		 * cleanupFS();
		 */
		o.setObject_state(50);
		
		clearNonpersistentObjectProperties(o);
		String filename = o.getIdentifier() + ".pack_" + o.getLatestPackage().getName() + ".tar";
		String targetFilename = o.getIdentifier() + ".tar";
		String aipFilePath = o.getContractor().getShort_name() + File.separator + o.getIdentifier() + File.separator
				+ filename;
		// Path targetFilePath = Path.make(n.getWorkAreaRootPath(), "work",
		// o.getContractor().getShort_name(), filename);
		Path targetFilePath = Path.make(n.getWorkAreaRootPath(), "work", o.getContractor().getShort_name(),
				targetFilename);

		if (!(new File(targetFilePath.toString()).exists())) {
			logger.debug(targetFilePath + " already exists. iget can be skipped.");
			// return true;
		}
		gridRoot.get(new File(targetFilePath.toString()), aipFilePath);

		de.uzk.hki.da.model.Package latestPackage = o.getLatestPackage();
		//RetrievePackagesHelper helper = new RetrievePackagesHelper(gridRoot, wa);
		//helper.loadPackages(o,false);
		File container = new File(targetFilePath.toString());
		File unpackedContainer = new File(
				Path.make(n.getWorkAreaRootPath(), "work", o.getContractor().getShort_name(), "temp").toString());
		String unpackedFilePath = unpackedContainer.getAbsolutePath() + File.separator
				+ getFilenameWithoutExtension(aipFilePath);
		File unpackedFile = new File(unpackedFilePath);
		File unpackedFileData = new File(unpackedFilePath + File.separator + "data");
		unpackedContainer.mkdirs();
		try {
			ArchiveBuilder builder = ArchiveBuilderFactory.getArchiveBuilderForFile(container);
			builder.unarchiveFolder(container, unpackedContainer);
		} catch (Exception e) {
			throw new IOException(
					"Existing AIP \"" + container + "\" couldn't be unpacked to folder " + unpackedContainer, e);
		}
		Optional<File> newestAFolder = findNewestA(unpackedFileData);

		java.nio.file.Path newestASourcePath = newestAFolder
				.orElseThrow(() -> new IllegalStateException("No +a folder found")).toPath().toAbsolutePath();

		java.nio.file.Path newestATargetPath = Paths.get(n.getWorkAreaRootPath() + File.separator + "work"
				+ File.separator + o.getContractor().getShort_name() + File.separator + o.getIdentifier());
		copyData(newestATargetPath, newestASourcePath);
		java.nio.file.Path tempFolderToDelete = Paths.get(unpackedContainer.getAbsolutePath());
		java.nio.file.Path tarFileToDelete = Paths.get(container.getAbsolutePath());
		deleteTarAndTempFolder(tarFileToDelete, tempFolderToDelete);
		java.nio.file.Path renamedATargetPath = Paths.get(n.getWorkAreaRootPath() + File.separator + "work"
				+ File.separator + o.getContractor().getShort_name() + File.separator + o.getOrig_name());
		Files.move(newestATargetPath, renamedATargetPath, StandardCopyOption.ATOMIC_MOVE);

		BagitUtils.buildBagit(renamedATargetPath.toAbsolutePath().toString());
		File unarchivedATargetPath = renamedATargetPath.toAbsolutePath().toFile();
		File ingestArchviedATargetPath = Paths.get(n.getIngestAreaRootPath() + File.separator
				+ o.getContractor().getShort_name() + File.separator + o.getOrig_name() + ".tgz").toFile();
		try {
			ArchiveBuilder builder = ArchiveBuilderFactory.getArchiveBuilderForFile(ingestArchviedATargetPath);
			builder.archiveFolder(unarchivedATargetPath, ingestArchviedATargetPath, true);
		} catch (Exception e) {
			throw new IOException(
					"Existing AIP \"" + container + "\" couldn't be unpacked to folder " + unpackedContainer, e);
		}
		deleteFolder(unarchivedATargetPath.toPath());
		deleteFolder(newestATargetPath);
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

	private void stdRetrieval(Path tempFolder) throws IOException {
		moveNewestPremisToDIP(tempFolder);
		copySurfaceRepresentation(tempFolder);
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
	private void copySurfaceRepresentation(Path tempFolder) throws RuntimeException, IOException {

		String sce = "";
		if (o.getFriendlyFileExtensions() != null)
			sce = o.getFriendlyFileExtensions();

		List<DAFile> files = o.getNewestFilesFromAllRepresentations(sce);
		for (DAFile f : files) {
			if (wa.toFile(f).getName().equals(C.PREMIS_XML))
				continue;

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


}
