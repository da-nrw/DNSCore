package de.uzk.hki.da.repository;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Set;

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
	 * Get the map that associates labels to filenames
	 * @return the label map
	 */
	public Map<String,String> getLabelMap();
	
	/**
	 * Set the map that associates labels to filenames
	 */
	public void setLabelMap(Map<String,String> labelMap);
	
	/**
	 * Purge a package if it exists.
	 * @param prefix the prefix
	 * @return boolean, true if package existed and was purged
	 * @throws RepositoryException
	 */
	public boolean purgePackageIfExists(String objectId, String prefix) throws RepositoryException;
	
	/**
	 * Get the set of filenames to be filtered during ingest
	 * @return the set of filenames to be filtered during ingest
	 */
	public Set<String> getFileFilter();
	
	/**
	 * Set the set of filenames to be filtered during ingest
	 * @param fileFilter the set of filenames to be filtered during ingest
	 */
	public void setFileFilter(Set<String> fileFilter);
	
	/**
	 * Ingest a package into the repository.
	 *
	 * @param urn the urn
	 * @param objectId the object id of object the file is attached to
	 * @param packagePath the package path
	 * @param contractorShortName the contractor short name
	 * @param packageType the package type
	 * @param prefix the prefix
	 * @return true, if successful
	 * @throws RepositoryException
	 * @throws IOException 
	 */
	public boolean ingestPackage(String urn, String objectId, String packagePath, String contractorShortName, String packageType, String prefix, String[] sets) throws RepositoryException, IOException;
	
	/**
	 * Attach a file to a package in the repository.
	 * 
	 * @param objectId the object id of object the file is attached to
	 * @param file the file to be ingested
	 * @param relPath the relative path to the file from the package root
	 * @return true, if successful
	 * @throws IOException 
	 * @throws RepositoryEcxeption
	 */
	public boolean ingestFile(String objectId, File file, String relPath) throws RepositoryException, IOException;
	
	/**
	 * Create a metadata file for a package in the repository.
	 * 
	 * @param objectId the object id of object the file is attached to
	 * @param fileId the name of the file to be created
	 * @param content the file content
	 * @param label the label of the file
	 * @param mimeType the MIME type
	 * @return true, if successful
	 * @throws RepositoryEcxeption
	 */
	public boolean createMetadataFile(String objectId, String fileId, String content, String label, String mimeType) throws RepositoryException;
	
	/**
	 * Get the file contents from the repository.
	 * 
	 * @param objectId the object id of object the file is attached to
	 * @param fileId the id for the file in the repository
	 * @return the contents of the file as an input stream
	 * @throws RepositoryException
	 */
	public InputStream retrieveFile(String objectId, String fileId) throws RepositoryException;

}
