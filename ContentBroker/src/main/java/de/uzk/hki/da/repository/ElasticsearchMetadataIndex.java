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

import java.util.Map;

import org.elasticsearch.ElasticSearchException;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uzk.hki.da.core.ConfigurationException;

/**
 * Metadata index implementation for elasticsearch.
 * @author Sebastian Cuy
 */
public class ElasticsearchMetadataIndex implements MetadataIndex {
	
	private static final Logger logger = LoggerFactory.getLogger(ElasticsearchMetadataIndex.class);
	
	private String[] hosts;
	private String cluster;
	
	@Override
	public void indexMetadata(String indexName, String type, String objectId,
			Map<String, Object> data) throws MetadataIndexException {
				
		if (cluster == null || hosts == null || hosts.length == 0) 
			throw new ConfigurationException("Elasticsearch cluster not set. Make sure the action is configured properly");
				
		Settings settings = ImmutableSettings.settingsBuilder()
		        .put("cluster.name", cluster).build();
		logger.debug("set cluster.name: {}", cluster);
		TransportClient client = new TransportClient(settings);
		for (String esHost : hosts) {
			client.addTransportAddress(new InetSocketTransportAddress(esHost, 9300));
		}
		
		logger.debug("set elasticsearch nodes: {}", client.transportAddresses());
		
		try {
			client.prepareIndex(indexName, type)
				.setId(objectId).setSource(data).execute().actionGet();
		} catch(ElasticSearchException e) {
			throw new MetadataIndexException("Unable to index metadata.", e);
		} finally {
			client.close();
		}
		
	}

	public String[] getHosts() {
		return hosts;
	}

	public void setHosts(String[] hosts) {
		this.hosts = hosts;
	}

	public String getCluster() {
		return cluster;
	}

	public void setCluster(String cluster) {
		this.cluster = cluster;
	}

}
