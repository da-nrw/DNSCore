package de.uzk.hki.da.repository;

import java.util.Map;

/**
 * Decouples the repository logic used for indexing
 * metadata from specific implementations.
 * 
 * @author Sebastian Cuy
 *
 */
public interface MetadataIndex {
	
	/**
	 * Indexes metadata
	 * @param indexName the name of the index
	 * @param type the type or collection in the index
	 * @param data nested key value data to be indexed
	 */
	void indexMetadata(String indexName, String type, String id, Map<String, Object> data)
			throws MetadataIndexException;

}
