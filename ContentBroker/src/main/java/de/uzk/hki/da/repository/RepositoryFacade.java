package de.uzk.hki.da.repository;

import java.io.File;
import java.util.Map;

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
	 *
	 * @param urn the urn
	 * @param prefix the prefix
	 */
	public void purgePackageIfExists(String urn, String objectId, String prefix) throws RepositoryException;
	
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
	 */
	public boolean ingestPackage(String urn, String objectId, String packagePath, String contractorShortName, String packageType, String prefix, String[] sets) throws RepositoryException;
	
	/**
	 * Attach a file to a package in the repository.
	 * 
	 * @param objectId the object id of object the file is attached to
	 * @param file the file to be ingested
	 * @param fileId the id for the file in the repository
	 * @return true, if successful
	 * @throws RepositoryEcxeption
	 */
	public boolean ingestFile(String objectId, File file, String fileId) throws RepositoryException;
	
	/**
	 * Get the file contents from the repository.
	 * 
	 * @param objectId the object id of object the file is attached to
	 * @param fileId the id for the file in the repository
	 * @return the contents of the file as a string
	 * @throws RepositoryException
	 */
	public String retrieveFile(String objectId, String fileId) throws RepositoryException;

}
