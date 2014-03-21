package de.uzk.hki.da.repository;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import com.yourmediashelf.fedora.client.FedoraClientException;

/**
 * Decouples the repository logic used for publishing PIPs from specific implementations.
 * 
 * Different repository applications can be connected to the ContentBroker by implementing
 * this interface and changing the corresponding class in the spring configuration.
 * 
 * @author Sebastian Cuy
 *
 */
public interface RepositoryFacade {
	
	/**
	 * Purge an object if it exists.
	 * @param objectId the id of the package
	 * @param collection the collection name
	 * @return boolean, true if package existed and was purged
	 * @throws RepositoryException
	 */
	public boolean purgeObjectIfExists(String objectId, String collection) throws RepositoryException;
	
	/**
	 * Create a new object in the repository.
	 * @param objectId the id for the package
	 * @param collection the collection name
	 * @param ownerId a user id
	 * @return boolean, true if package existed and was purged
	 * @throws RepositoryException
	 */
	public boolean createObject(String objectId, String collection, String ownerId) throws RepositoryException;
	
	/**
	 * Attach a file to a package in the repository.
	 * @param objectId the object id of object the file is attached to
	 * @param collection a named collection
	 * @param fileId the name of the file to be created
	 * @param file the file to be ingested
	 * @param label the label of the file
	 * @param mimeType the MIME type
	 * @return true, if successful
	 * @throws IOException 
	 * @throws RepositoryEcxeption
	 */
	boolean ingestFile(String objectId, String collection, String fileId,
			File file, String label, String mimeType) throws RepositoryException, IOException;
	
	/**
	 * Create a metadata file for a package in the repository.
	 * @param objectId the object id of object the file is attached to
	 * @param collection a named collection
	 * @param fileId the name of the file to be created
	 * @param content the file content
	 * @param label the label of the file
	 * @param mimeType the MIME type
	 * @return true, if successful
	 * @throws RepositoryEcxeption
	 */
	public boolean createMetadataFile(String objectId, String fileId, String collection,
			String content, String label, String mimeType) throws RepositoryException;
	
	/**
	 * Update a metadata file for a package in the repository.
	 * @param objectId the object id of object the file is attached to
	 * @param collection a named collection
	 * @param fileId the name of the file to be created
	 * @param content the file content
	 * @param label the label of the file
	 * @param mimeType the MIME type
	 * @return true, if successful
	 * @throws RepositoryEcxeption
	 */
	public boolean updateMetadataFile(String objectId, String collection, String fileId, 
			String content, String label, String mimeType) throws RepositoryException;
	
	/**
	 * Get the file contents from the repository.
	 * @param objectId the object id of object the file is attached to
	 * @param collection a named collection
	 * @param fileId the id for the file in the repository
	 * @return the contents of the file as an input stream
	 * @throws RepositoryException
	 */
	public InputStream retrieveFile(String objectId, String collection, String fileId) throws RepositoryException;

	/**
	 * Check if an object exists in the repository.
	 * @param objectId the id of the object in the repository
	 * @param collection a named collection
	 * @return true if object is present, false if not
	 * @throws RepositoryException
	 */
	boolean objectExists(String objectId, String collection) throws RepositoryException;

	/**
	 * Adds an RDF triple about an object to the repository.
	 * @param objectId the id of the object, i.e. the RDF subject
	 * @param collection a named collection
	 * @param predicate the predicate as absolute URL
	 * @param object the object as absolute URL
	 * @throws FedoraClientException
	 */
	public void addRelationship(String objectId, String collection, String predicate, String object)
			throws FedoraClientException;
	
	/**
	 * Generate a file id from a file path.
	 * @param path the path to the file
	 * @return the generated file id
	 */
	public String generateFileId(String path);

}
