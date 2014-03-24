package de.uzk.hki.da.repository;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import org.apache.commons.io.FileUtils;

/**
 * Implements a simple file system based repository
 * for acceptance testing on developer machines.
 * @author Sebastian Cuy
 */
public class FakeRepositoryFacade implements RepositoryFacade {
	
	private String dipAreaRootPath;

	@Override
	public boolean purgeObjectIfExists(String objectId, String collection)
			throws RepositoryException {
		try {
			if (objectExists(objectId, collection)) {
				FileUtils.deleteDirectory(getFile(objectId, collection, null));
				return true;
			} else {
				return false;
			}
		} catch (IOException e) {
			throw new RepositoryException("Unable to purge object " + objectId, e);
		}
	}

	@Override
	public void createObject(String objectId, String collection,
			String ownerId) throws RepositoryException {
		if(!getFile(objectId, collection, null).mkdirs()) {
			throw new RepositoryException("Unable to create folder for object "
					+ collection + "/" + objectId);
		}
	}

	@Override
	public void ingestFile(String objectId, String collection,
			String fileId, File file, String label, String mimeType)
			throws RepositoryException, IOException {
		try {
			File destFile = getFile(objectId, collection, fileId);
			FileUtils.copyFile(file, destFile);
		} catch (IOException e) {
			throw new RepositoryException("Unable to create file "
					+ collection + "/" + objectId + "/" + fileId , e);
		}
	}

	@Override
	public void createMetadataFile(String objectId, String collection,
			String fileId, String content, String label, String mimeType)
			throws RepositoryException {
		PrintWriter pw = null;
		try {
			pw = new PrintWriter(getFile(objectId, collection, fileId), "UTF-8");
			pw.write(content);
		} catch (Exception e) {
			throw new RepositoryException("Unable to write to file "
					+ collection + "/" + objectId + "/" + fileId , e);
		} finally {
			if (pw != null)	pw.close();
		}
	}

	@Override
	public void updateMetadataFile(String objectId, String collection,
			String fileId, String content, String label, String mimeType)
			throws RepositoryException {
		createMetadataFile(objectId, collection, fileId, content, label, mimeType);
	}

	@Override
	public InputStream retrieveFile(String objectId, String collection,
			String fileId) throws RepositoryException {
		try {
			return new FileInputStream(getFile(objectId, collection, fileId));
		} catch (FileNotFoundException e) {
			throw new RepositoryException("Unable to read to file "
					+ collection + "/" + objectId + "/" + fileId , e);
		}
	}

	@Override
	public boolean objectExists(String objectId, String collection)
			throws RepositoryException {
		return getFile(objectId, collection, null).exists();
	}

	@Override
	public void addRelationship(String objectId, String collection,
			String predicate, String object) {
		// stub, fake repository does not handle relationships
	}

	@Override
	public String generateFileId(String path) {
		return new File(path).getName();
	}

	public String getDipAreaRootPath() {
		return dipAreaRootPath;
	}

	public void setDipAreaRootPath(String dipAreaRootPath) {
		this.dipAreaRootPath = dipAreaRootPath;
	}

	private File getFile(String objectId, String collection, String file) {
		String path = dipAreaRootPath + "_data" + File.pathSeparator
				+ collection + File.pathSeparator + objectId;
		if (file != null && !file.isEmpty()) {
			path += File.pathSeparator + file;
		}
		return new File(path);
	}

	@Override
	public void indexMetadata(String objectId, String collection, String fileId)
			throws RepositoryException {
		// stub, fake repository does not handle indexing
	}

}
