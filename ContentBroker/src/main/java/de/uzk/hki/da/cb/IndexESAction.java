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
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

import com.github.jsonldjava.utils.JSONUtils;

import de.uzk.hki.da.metadata.RdfToJsonLdConverter;
import de.uzk.hki.da.repository.RepositoryException;
import de.uzk.hki.da.repository.RepositoryFacade;

/**
 * Action that indexes EDM data into elasticsearch.
 * 
 * First the action fetches the EDM/RDF data from
 * the presentation repository.
 * In order for the EDM data to be compatible with
 * the elasticsearch data model the EDM/RDF data is
 * then converted into JSON-LD and indexed in
 * elasticsearch.
 * 
 * The frame that defines the JSON structure can be
 * configured in "conf/frame.jsonld".
 *  
 * @author Sebastian Cuy
 */
public class IndexESAction extends AbstractAction {
	
	private RepositoryFacade repositoryFacade;
	private String[] esHosts;
	private String esCluster = "elasticsearch";
	private String esIndexName;
	private Set<String> testContractors;

	@Override
	boolean implementation() {
		setKILLATEXIT(true);
		
		if (repositoryFacade == null) 
			throw new RuntimeException("Repository facade object not set. Make sure the action is configured properly");
		if (esIndexName == null) 
			throw new RuntimeException("Elasticsearch index name not set. Make sure the action is configured properly");
		if (esHosts == null || esHosts.length == 0) 
			throw new RuntimeException("Elasticsearch hosts not set. Make sure the action is configured properly");

		// use test index for test packages
		// TODO move test contractors to config
		String contractorShortName = job.getObject().getContractor().getShort_name();
		if(testContractors.contains(contractorShortName)) {
			esIndexName += "_test";
		}
		
		String objectId = object.getIdentifier();
		TransportClient client = null;
		try {
			
			InputStream edmStream = repositoryFacade.retrieveFile(objectId, "danrw", "EDM");
			String edmContent = IOUtils.toString(edmStream, "UTF-8");
			
			// transform EDM to JSON
			RdfToJsonLdConverter converter = new RdfToJsonLdConverter("conf/frame.jsonld");
			Map<String, Object> json = converter.convert(edmContent);
			
			logger.debug("transformed EDM RDF into JSON. Result: {}", JSONUtils.toPrettyString(json));
			
			// ingest JSON into ES
			// TODO create additional object for package level in ES
			
			Settings settings = ImmutableSettings.settingsBuilder()
			        .put("cluster.name", esCluster).build();
			logger.debug("set cluster.name: {}", esCluster);
			client = new TransportClient(settings);
			for (String esHost : esHosts) {
				client.addTransportAddress(new InetSocketTransportAddress(esHost, 9300));
			}
			
			logger.debug("set elasticsearch nodes: {}", client.transportAddresses());
			
			@SuppressWarnings("unchecked")
			List<Object> graph = (List<Object>) json.get("@graph");
			for (Object object : graph) {
				@SuppressWarnings("unchecked")
				Map<String,Object> aggregation = (Map<String,Object>) object;
				String[] split = ((String) aggregation.get("@id")).split("/");
				String id = split[split.length-1];
				client.prepareIndex(esIndexName, "aggregation")
					.setId(id).setSource(aggregation).execute().actionGet();
			}	
			
		} catch (RepositoryException e) {
			logger.warn("Could not retrieve EDM from Fedora, skipping ingest into elasticsearch!");
		} catch (Exception e) {
			throw new RuntimeException("Error while ingesting EDM into elasticsearch", e);
		} finally {
			if (client!=null)
			client.close();
		}
		
		return true;
		
	}

	@Override
	void rollback() throws Exception {
		throw new NotImplementedException();
	}

	/**
	 * Get the list of elasticsearch hosts
	 * the desired index is running on.
	 * @return the list of hosts
	 */
	public String[] getEsHosts() {
		return esHosts;
	}

	/**
	 * Set the list of elasticsearch hosts
	 * the desired index is running on.
	 * @param the list of hosts
	 */
	public void setEsHosts(String[] esHosts) {
		this.esHosts = esHosts;
	}

	/**
	 * Get the elasticsearch cluster name
	 * the desired index is running on.
	 * @return the cluster name
	 */
	public String getEsCluster() {
		return esCluster;
	}

	/**
	 * Set the elasticsearch cluster name
	 * the desired index is running on.
	 * @param the cluster name
	 */
	public void setEsCluster(String esCluster) {
		this.esCluster = esCluster;
	}

	/**
	 * Get the elasticsearch index name
	 * the data will be indexed in.
	 * @return the index name
	 */
	public String getEsIndexName() {
		return esIndexName;
	}

	/**
	 * Set the elasticsearch index name
	 * the data will be indexed in.
	 * @param the index name
	 */
	public void setEsIndexName(String esIndexName) {
		this.esIndexName = esIndexName;
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
