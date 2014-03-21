package de.uzk.hki.da.repository;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;

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
import com.yourmediashelf.fedora.client.request.ModifyDatastream;
import com.yourmediashelf.fedora.client.request.PurgeObject;

public class Fedora3RepositoryFacade implements RepositoryFacade {
	
	private static Logger logger = LoggerFactory.getLogger(Fedora3RepositoryFacade.class);
	
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
		try {
			new PurgeObject(pid).execute(fedora);
			logger.info("Successfully purged object in Fedora. pid: {}", pid);
		} catch (FedoraClientException e) {
			throw new RepositoryException("Unable to purge package " + pid, e);
		}
		return true;
	}

	@Override 
	public boolean createObject(String objectId, String collection, String ownerId) throws RepositoryException {
		String pid = generatePid(objectId, collection);
		try {
			new Ingest(pid).ownerId(ownerId).execute(fedora);
			logger.info("Successfully created object in Fedora. pid: {}", pid);
		} catch (FedoraClientException e) {
			throw new RepositoryException("Unable to create package " + pid, e);
		}
		return true;
	}
	
	@Override
	public boolean ingestFile(String objectId, String collection, String dsId, File file, String label, String mimeType) throws RepositoryException, IOException {
		String pid = generatePid(objectId, collection);
		try {
			String dsLocation = "file://" + file.getAbsolutePath();
			new AddDatastream(pid, dsId).mimeType(mimeType)
				.controlGroup("E").dsLabel(label)
				.dsLocation(dsLocation).execute(fedora);
			logger.info("Successfully created datastream with dsID {} for file {}.", dsId, file.getName());
		} catch (FedoraClientException e) {
			throw new RepositoryException("Error while trying to add datastream for file " + file.getName(),e);
		}		
		return true;
		
	}
	
	@Override
	public boolean createMetadataFile(String objectId, String collection, String dsId, String content, String label, String mimeType) throws RepositoryException {
		String pid = generatePid(objectId, collection);
		try {
			new AddDatastream(pid, dsId).mimeType(mimeType)
				.controlGroup("X").dsLabel(label)
				.content(content).execute(fedora);
			logger.info("Successfully created metadata datastream with dsID {}.", dsId);
		} catch(FedoraClientException e) {
			throw new RepositoryException("Unable to create metadata file: " + dsId, e);
		}
		return true;
	}
	
	@Override
	public boolean updateMetadataFile(String objectId, String collection, String dsId, String content, String label, String mimeType) throws RepositoryException {
		String pid = generatePid(objectId, collection);
		try {
			new ModifyDatastream(pid, dsId).mimeType(mimeType)
				.dsLabel(label).content(content).execute(fedora);
			logger.info("Successfully updated metadata datastream with dsID {}.", dsId);
		} catch(FedoraClientException e) {
			throw new RepositoryException("Unable to update metadata file: " + dsId, e);
		}
		return true;
	}
	
	@Override
	public String generateFileId(String path) {
		
		// replace slashes
		String dsID = path.replace("/", "-");
		
		// eliminate disallowed beginnings
		if (Character.isDigit(dsID.charAt(0))
				|| dsID.startsWith("xml")
				|| dsID.startsWith("XML")) {
			dsID = "_" + dsID;
		}
		
		// replace disallowed characters
		dsID = dsID.replaceAll("[^\\p{L}\\p{Digit}\\._-]","_");
		
		return dsID;
	}

	@Override
	public InputStream retrieveFile(String objectId, String collection, String fileId)
			throws RepositoryException {
		String pid = generatePid(objectId, collection);
		try {
			return new GetDatastreamDissemination(pid, "DC")
				.execute(fedora).getEntityInputStream();
		} catch (FedoraClientException e) {
			throw new RepositoryException("Failed to retrieve datastream: " + fileId, e);
		}
	}
	
	@Override
	public boolean objectExists(String objectId, String collection) throws RepositoryException {
		String pid = generatePid(objectId, collection);
		try {
			new GetObjectProfile(pid).execute(fedora);
		} catch (FedoraClientException e) {
			if (e.getStatus() == 404) {
				// object does not exist and does not need to be purged
				return false;
			} else {
				throw new RepositoryException("Failed to check if package exists", e);
			}
		}
		return true;
	}
	
	@Override
	public void addRelationship(String objectId, String collection, String predicate, String object) throws FedoraClientException {
		String pid = generatePid(objectId, collection);
		new AddRelationship("info:fedora/" + pid)
			.predicate(predicate)
			.object(object).execute();
	}
	
	private String generatePid(String objectId, String collection) {
		return collection + ":" + objectId;
	}

}
