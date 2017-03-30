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
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yourmediashelf.fedora.client.FedoraClient;
import com.yourmediashelf.fedora.client.FedoraClientException;
import com.yourmediashelf.fedora.client.FedoraCredentials;
import com.yourmediashelf.fedora.client.request.AddDatastream;
import com.yourmediashelf.fedora.client.request.AddRelationship;
import com.yourmediashelf.fedora.client.request.GetDatastreamDissemination;
import com.yourmediashelf.fedora.client.request.GetObjectProfile;
import com.yourmediashelf.fedora.client.request.Ingest;
import com.yourmediashelf.fedora.client.request.PurgeObject;
import com.yourmediashelf.fedora.client.response.AddDatastreamResponse;
import com.yourmediashelf.fedora.client.response.FedoraResponse;

import de.uzk.hki.da.util.FileIdGenerator;
import de.uzk.hki.da.utils.Path;

public class Fedora3RepositoryFacade implements RepositoryFacade {
	
	private static Logger logger = LoggerFactory.getLogger(Fedora3RepositoryFacade.class);
	private String contextUriPrefix;
	private FedoraClient fedora;
	
	/**
	 * Instantiates a new fedora 3 repository facade.
	 * @param fedoraUrl the url to the fedora web application
	 * @param fedoraUser the fedora user, e.g. fedoraAdmin
	 * @param fedoraPass the corresponding password
	 * @throws MalformedURLException 
	 */
	public Fedora3RepositoryFacade(String fedoraUrl, String fedoraUser, String fedoraPass) throws MalformedURLException {
		FedoraCredentials fedoraCredentials = new FedoraCredentials(fedoraUrl, fedoraUser, fedoraPass);
		this.fedora = new FedoraClient(fedoraCredentials);
	}

	@Override
	public boolean purgeObjectIfExists(String objectId, String collection)
			throws RepositoryException {
		String pid = generatePid(objectId, collection);
		if (!objectExists(objectId, collection)) return false;
		
		FedoraResponse r=null;
		try {
			r = new PurgeObject(pid).execute(fedora);
			logger.info("Successfully purged object in Fedora. pid: {}", pid);
		} catch (FedoraClientException e) {
			throw new RepositoryException("Unable to purge package " + pid, e);
		} finally {
			if (r!=null) r.close();
		}
		return true;
	}
	
	

	@Override 
	public void createObject(String objectId, String collection, String ownerId) throws RepositoryException {
		String pid = generatePid(objectId, collection);
		
		FedoraResponse r =null;
		try {
			r=new Ingest(pid).ownerId(ownerId).execute(fedora);
			logger.info("Successfully created object in Fedora. pid: {}", pid);
		} catch (FedoraClientException e) {
			throw new RepositoryException("Unable to create package " + pid, e);
		} finally {
			if (r!=null) r.close();
		}
	}
	
	@Override
	public void ingestFile(String objectId, String collection, String dsId, File file, String label, String mimeType) throws RepositoryException, IOException {
		String pid = generatePid(objectId, collection);
		AddDatastreamResponse r = null;
		try {
			String fileName = file.getAbsolutePath();
			String fileNameWithoutWhitespace = fileName.replaceAll("\\s+","_");
			if(!fileName.equals(fileNameWithoutWhitespace)) {
				logger.debug("Whitespace(s) in file name! Rename file "+fileName +" in "+fileNameWithoutWhitespace);
				File newFile = Path.makeFile(fileNameWithoutWhitespace);
				FileUtils.moveFile(file, newFile);
				file = newFile;
			}

			String dsLocation = "file://" + file.getAbsolutePath();
			logger.debug("dsLocation: "+dsLocation);
			
			r=new AddDatastream(pid, dsId).mimeType(mimeType)
				.controlGroup("E").dsLabel(label)
				.dsLocation(dsLocation).execute(fedora);
			logger.info("Successfully created datastream with dsID {} for file {}.", dsId, file.getName());
		} catch (FedoraClientException e) {
			throw new RepositoryException("Error while trying to add datastream for file " + file.getName(),e);
		} finally {
			if (r!=null) r.close();
		}
	}
	
	@Override
	public String generateFileId(String path) {
		return FileIdGenerator.getFileId(path);
	}

	@Override
	public boolean fileExists(String objectId, String collection, String fileId) throws RepositoryException {
		String pid = generatePid(objectId, collection);
		FedoraResponse r = null;
		InputStream is = null;
		boolean ret = false;
		try {
			r = new GetDatastreamDissemination(pid, fileId).execute(fedora);
			is = r.getEntityInputStream();
			if (is != null) {
				ret = true;
				try {
					is.close();
				} catch (IOException e) {
				}
			}

			r.close();
		} catch (FedoraClientException e) {
			if (e.getStatus() == 404) {
				logger.error("Failed to recieve Datastream, due to not found reason: " + objectId + " " + fileId);
			} else {
				throw new RepositoryException("Failed to retrieve datastream: " + fileId, e);
			}
		}
		return ret;
	}

	@Override
	public void retrieveTo(OutputStream outputStream, String objectId, String collection, String fileId)
			throws RepositoryException {
		String pid = generatePid(objectId, collection);
		FedoraResponse r = null;
		InputStream inputStream = null;
		try {
			r=new GetDatastreamDissemination(pid, fileId)
				.execute(fedora);
			inputStream = r.getEntityInputStream();

			try {
				org.apache.commons.io.IOUtils.copy(inputStream, outputStream);
			} catch (Exception exc) {
				throw new RepositoryException("Failed to copy datastream: " + fileId, exc);
			}
			r.close();
		} catch (FedoraClientException e) {
			if (e.getStatus() == 404) { 
				throw new RepositoryException("Failed to recieve Datastream, due to not found reason: " + objectId + " " + fileId);
			} else {
				throw new RepositoryException("Failed to retrieve datastream: " + fileId, e);
			}
		} finally {}
	}
	
	@Override
	public boolean objectExists(String objectId, String collection) throws RepositoryException {
		String pid = generatePid(objectId, collection);
		FedoraResponse r=null;
		try {
			r=new GetObjectProfile(pid).execute(fedora);
		} catch (FedoraClientException e) {
			if (e.getStatus() == 404) {
				// object does not exist and does not need to be purged
				return false;
			} else {
				throw new RepositoryException("Failed to check if package exists", e);
			}
		}finally{
			if (r!=null) r.close();
		}
		return true;
	}
	
	@Override
	public void addRelationship(String objectId, String collection, String predicate, String object) throws RepositoryException {
		String pid = generatePid(objectId, collection);
		FedoraResponse r = null;
		try {
			r=new AddRelationship("info:fedora/" + pid)
				.predicate(predicate)
				.object(object).execute(fedora);
		} catch (FedoraClientException e) {
			throw new RepositoryException("Unable to add relationship: info:fedora/"
					+ pid + "-" + predicate + "-" + object,  e);
		} finally {
			if (r!=null) r.close();
		}
	}
	
	private String generatePid(String objectId, String collection) {
		return (collection + ":" + objectId);
	}
	
	
	/**
	 * Get the name of the index
	 * the data will be indexed in.
//	 * @return the index name
	 */
	public String getContextUriPrefix() {
		return contextUriPrefix;
	}

	/**
	 * Set the name of the index
	 * the data will be indexed in.
	 * @param the index name
	 */
	public void setContextUriPrefix(String contextUriPrefix) {
		this.contextUriPrefix = contextUriPrefix;
	}
}
