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

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.NotImplementedException;

import com.github.jsonldjava.utils.JSONUtils;

import de.uzk.hki.da.metadata.RdfToJsonLdConverter;
import de.uzk.hki.da.repository.RepositoryFacade;

/**
 * Action that indexes metadata data into a search index. 
 * @author Sebastian Cuy 
 */
public class IndexMetadataAction extends AbstractAction {
	
	private RepositoryFacade repositoryFacade;
	private Set<String> testContractors;
	private String indexName;

	@Override
	boolean implementation() {
		setKILLATEXIT(true);
		
		if (repositoryFacade == null) 
			throw new RuntimeException("Repository facade object not set. Make sure the action is configured properly");
		if (indexName == null) 
			throw new RuntimeException("Index name not set. Make sure the action is configured properly");

		// use test index for test packages
		String contractorShortName = job.getObject().getContractor().getShort_name();
		String tempIndexName = indexName;
		if(testContractors.contains(contractorShortName)) {
			tempIndexName += "_test";
		}

		try {
			
			String objectId = object.getIdentifier();
			InputStream metadataStream = repositoryFacade.retrieveFile(objectId, "danrw", "EDM");
			String metadataContent = IOUtils.toString(metadataStream, "UTF-8");
			
			// transform metadata to JSON
			RdfToJsonLdConverter converter = new RdfToJsonLdConverter("conf/frame.jsonld");
			Map<String, Object> json = converter.convert(metadataContent);
			
			logger.debug("transformed RDF into JSON. Result: {}", JSONUtils.toPrettyString(json));
			
			@SuppressWarnings("unchecked")
			List<Object> graph = (List<Object>) json.get("@graph");
			// create index entry for every subject in graph
			for (Object object : graph) {
				// extract id from subject uri
				@SuppressWarnings("unchecked")
				Map<String,Object> subject = (Map<String,Object>) object;
				String[] splitId = ((String) subject.get("@id")).split("/");
				String id = splitId[splitId.length-1];
				// extract index name from type
				String[] splitType = ((String) subject.get("@type")).split("/");
				String type = splitType[splitType.length-1];
				repositoryFacade.indexMetadata(tempIndexName, type, id, subject);
			}
			
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
		
		return true;
		
	}

	@Override
	void rollback() throws Exception {
		throw new NotImplementedException();
	}

	/**
	 * Get the name of the index
	 * the data will be indexed in.
	 * @return the index name
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

}
