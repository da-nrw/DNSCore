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

package de.uzk.hki.da.cb;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.NotImplementedException;

import com.github.jsonldjava.core.JSONLDProcessingError;
import com.github.jsonldjava.utils.JSONUtils;

import de.uzk.hki.da.core.ConfigurationException;
import de.uzk.hki.da.metadata.RdfToJsonLdConverter;
import de.uzk.hki.da.repository.RepositoryException;
import de.uzk.hki.da.repository.RepositoryFacade;

/**
 * Action that indexes metadata into a search index.
 * 
 * It fetches (RDF-)Metadata from the repository,
 * transforms it to hierarchical obejct structure
 * and indexes it in the repository's index. 
 * 
 * The index entries generated by this action can be
 * configured through <a href="http://json-ld.org/spec/latest/json-ld-framing/">
 * JSON-LD Frames</a>.
 * 
 * The context URI for every document is generated
 * by concatenating the context uri prefix with the
 * name of the frame file.
 * 
 * @author Sebastian Cuy 
 * @author Daniel M. de Oliveira
 */
public class IndexMetadataAction extends AbstractAction {
	
	private RepositoryFacade repositoryFacade;
	private Set<String> testContractors;
	private String indexName;
	private Map<String,String> frames;
	private String contextUriPrefix;

	@Override
	void checkActionSpecificConfiguration() throws ConfigurationException {
		if (getRepositoryFacade() == null) 
			throw new ConfigurationException("Repository facade object not set. Make sure the action is configured properly");
	}



	@Override
	void checkSystemStatePreconditions() throws IllegalStateException {
		if (indexName == null) 
			throw new IllegalStateException("Index name not set. Make sure the action is configured properly");
		if (getFrames()==null)
			throw new IllegalStateException("Frames not set.");
		if (getTestContractors()==null)
			throw new IllegalStateException("testContractors not set");
	}



	@Override
	boolean implementation() throws RepositoryException, IOException {
		setKILLATEXIT(true);
		
			for (String framePath : getFrames().keySet()) {
				if (!new File(framePath).exists())
					throw new FileNotFoundException(framePath+" does not exist.");
				
				String metadataFileId = getFrames().get(framePath);
				InputStream metadataStream;
				metadataStream = getRepositoryFacade().retrieveFile(
					object.getIdentifier(), preservationSystem.getOpenCollectionName(), metadataFileId);
				if (metadataStream == null) {
					logger.warn("Metadata file {} not found in repository! Skipping indexing.", metadataFileId);
					continue;
				}
				transformMetadataToJson(framePath,metadataStream,adjustIndexName(indexName));
			}
		
		return true;
	}

	

	
	@Override
	void rollback() throws Exception {
		throw new NotImplementedException();
	}




	/**
	 * use test index for test packages
	 * @param originalIndexName
	 * @return
	 */
	private String adjustIndexName(String originalIndexName){
		
		String contractorShortName = object.getContractor().getShort_name();
		String adjustedIndexName = indexName;
		if(testContractors != null && testContractors.contains(contractorShortName)) {
			adjustedIndexName += "_test";
		}
		return adjustedIndexName;
	}
	
	
	
	/**
	 * @param framePath
	 * @param metadataStream
	 * @param tempIndexName
	 * @throws RepositoryException
	 * @throws IOException
	 * @throws JSONLDProcessingError
	 */
	private void transformMetadataToJson(String framePath,InputStream metadataStream,String tempIndexName) 
			throws RepositoryException, IOException {
		String metadataContent = IOUtils.toString(metadataStream, "UTF-8");
		
		RdfToJsonLdConverter converter = new RdfToJsonLdConverter(framePath);
		Map<String, Object> json = null;
		try {
			json = converter.convert(metadataContent);
		} catch (Exception e) {
			throw new RuntimeException("An error occured during metadata conversion",e);
		}
		
		logger.debug("transformed RDF into JSON. Result: {}", JSONUtils.toPrettyString(json));
		
		@SuppressWarnings("unchecked")
		List<Object> graph = (List<Object>) json.get("@graph");
		
		// create index entry for every subject in graph (subject?)
		for (Object object : graph) {
			createIndexEntry(framePath, object);
		}
	}


	/**
	 * @param framePath
	 * @param object
	 * @throws RepositoryException
	 */
	private void createIndexEntry(String framePath, Object object)
			throws RepositoryException {
		
		@SuppressWarnings("unchecked")
		Map<String,Object> subject = (Map<String,Object>) object;
		// Add @context attribute
		String contextUri = contextUriPrefix + FilenameUtils.getName(framePath);
		subject.put("@context", contextUri);
		String[] splitId = ((String) subject.get("@id")).split("/");
		String id = splitId[splitId.length-1];
		// extract index name from type
		String[] splitType = ((String) subject.get("@type")).split("/");
		String type = splitType[splitType.length-1];
		getRepositoryFacade().indexMetadata(indexName, type, id, subject);
	}
	
	
	
	
	/**
	 * Get the name of the index
	 * the data will be indexed in.
//	 * @return the index name
	 */
	public String getIndexName() {
		return indexName;
	}

	/**
	 * Set the name of the index
	 * the data will be indexed in.
	 * @param the index name
	 */
	public void setIndexName(String indexName) {
		this.indexName = indexName;
	}

	/**
	 * Get the set of contractors that are considered test users.
	 * Objects ingested by these users will be indexed in the
	 * test index (index_name + "test").
	 * @return the set of test users
	 */
	public Set<String> getTestContractors() {
		return testContractors;
	}

	/**
	 * Set the set of contractors that are considered test users.
	 * Objects ingested by these users will be indexed in the
	 * test index (index_name + "test").
	 * @param the set of test users
	 */
	public void setTestContractors(Set<String> testContractors) {
		this.testContractors = testContractors;
	}

	/**
	 * Get the map of frames that defines the JSON-Structure
	 * of the objects being indexed. Different frames can be
	 * configured to allow for indexes of different granularity
	 * or based on different metadata.
	 * @return the map of frames, keys represent relative paths
	 * 	to a JSON-LD frame definition, values the name of the
	 * 	file in the repository
	 */
	public Map<String,String> getFrames() {
		return frames;
	}
	
	/**
	 * Set the map of frames that defines the JSON-Structure
	 * of the objects being indexed. Different frames can be
	 * configured to allow for indexes of different granularity
	 * or based on different metadata.
	 * @param the map of frames, keys represent relative paths
	 * 	to a JSON-LD frame definition, values the name of the
	 * 	file in the repository
	 */
	public void setFrames(Map<String,String> frames) {
		this.frames = frames;
	}

	/**
	 * Get the prefix for URIs to context files that
	 * are linked in JSON-LD output.
	 * @return the prefix
	 */
	public String getContextUriPrefix() {
		return contextUriPrefix;
	}

	/**
	 * Get the prefix for URIs to context files that
	 * are linked in JSON-LD output.
	 * @param the prefix
	 */
	public void setContextUriPrefix(String contextUriPrefix) {
		this.contextUriPrefix = contextUriPrefix;
	}

	/**
	 * Get the repository implementation
	 * @return the repository implementation
	 */
	public RepositoryFacade getRepositoryFacade() {
		return repositoryFacade;
	}
	
	/**
	 * Set the repository implementation
	 * @param repositoryFacade the repository implementation
	 */
	public void setRepositoryFacade(RepositoryFacade repositoryFacade) {
		this.repositoryFacade = repositoryFacade;
	}

}
