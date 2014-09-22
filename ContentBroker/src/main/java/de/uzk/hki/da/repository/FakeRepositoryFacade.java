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

package de.uzk.hki.da.repository;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implements a simple file system based repository
 * for acceptance testing on developer machines.
 * @author Sebastian Cuy
 */
public class FakeRepositoryFacade implements RepositoryFacade {
	
	private MetadataIndex metadataIndex;
	
	static final Logger logger = LoggerFactory.getLogger(FakeRepositoryFacade.class);
	
	private String workAreaRootPath;

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
		String dcContent = "<?xml version=\"1.0\" encoding=\"utf-8\"?><oai_dc:dc xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns:oai_dc=\"http://www.openarchives.org/OAI/2.0/oai_dc/\"><dc:title>Testpaket in Fake Repository</dc:title></oai_dc:dc>";
		createMetadataFile(objectId, collection, "DC", dcContent, "DC.xml", "text/xml");
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
		logger.debug("updateMetadataFile");
		createMetadataFile(objectId, collection, fileId, content, label, mimeType);
	}

	@Override
	public InputStream retrieveFile(String objectId, String collection,
			String fileId) throws RepositoryException {
		logger.debug("retrieveFile");
		try {
			return new FileInputStream(getFile(objectId, collection, fileId));
		} catch (FileNotFoundException e) {
			return null;
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

	public String getWorkAreaRootPath() {
		return workAreaRootPath;
	}

	public void setWorkAreaRootPath(String workAreaRootPath) {
		this.workAreaRootPath = workAreaRootPath + "/work";
	}

	private File getFile(String objectId, String collection, String file) {
		String path = workAreaRootPath + File.separator + "_data" + File.separator
				+ collection + File.separator + objectId;
		if (file != null && !file.isEmpty()) {
			path += File.separator + file;
		}
		logger.debug("getFile:"+path);
		return new File(path);
	}

	@Override
	public void indexMetadata(String indexName, String id,
			String edmContent) throws RepositoryException {
		
		FileOutputStream fop = null;
		File file;
 
		try {
 
			file = new File("/tmp/edmContent");
			fop = new FileOutputStream(file);
 
			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}
 
			// get the content in bytes
			byte[] contentInBytes = edmContent.getBytes();
			fop.write(contentInBytes);
			fop.flush();
			fop.close();
 
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (fop != null) {
					fop.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void setMetadataIndex(MetadataIndex metadataIndex) {
		this.metadataIndex = metadataIndex;
	}
	
	public MetadataIndex getMetadataIndex() {
		return metadataIndex;
	}

	@Override
	public String getIndexedMetadata(String indexName, String objectId) {
		if (objectId.equals("Inventarnummer")) // lido
			return "\"edm:provider\":\"DA-NRW - Digitales Archiv Nordrhein-Westfalen\"";
		else if(objectId.endsWith("d1e15821")) // ead
			return "VDA - Forschungsstelle Rheinlländer in aller Welt";
		else if(objectId.endsWith("-1"))       // xmp
			return "Dieser Brauch zum Sankt Martinstag";
		else                                   // mets
			return "ULB (Stadt) [Electronic ed.]";
	}
}
