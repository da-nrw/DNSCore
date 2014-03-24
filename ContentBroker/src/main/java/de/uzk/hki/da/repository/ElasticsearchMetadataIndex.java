package de.uzk.hki.da.repository;

import java.util.Map;

import org.elasticsearch.ElasticSearchException;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Metadata index implementation for elasticsearch.
 * @author Sebastian Cuy
 */
public class ElasticsearchMetadataIndex implements MetadataIndex {
	
	private static final Logger logger = LoggerFactory.getLogger(ElasticsearchMetadataIndex.class);
	
	private boolean init = false;
	private String[] hosts;
	private String cluster;
	private String indexName;
	private TransportClient client;
	
	/**
	 * Initializes the connection to the elasticsearch cluster
	 */
	public void init() {
		
		if (indexName == null) 
			throw new RuntimeException("Elasticsearch index name not set. Make sure the action is configured properly");
		if (cluster == null || hosts.length == 0) 
			throw new RuntimeException("Elasticsearch cluster not set. Make sure the action is configured properly");
		if (hosts == null || hosts.length == 0) 
			throw new RuntimeException("Elasticsearch hosts not set. Make sure the action is configured properly");
				
		Settings settings = ImmutableSettings.settingsBuilder()
		        .put("cluster.name", cluster).build();
		logger.debug("set cluster.name: {}", cluster);
		client = new TransportClient(settings);
		for (String esHost : hosts) {
			client.addTransportAddress(new InetSocketTransportAddress(esHost, 9300));
		}
		
		logger.debug("set elasticsearch nodes: {}", client.transportAddresses());
		
	}
	
	@Override
	public void indexMetadata(String indexName, String type, String objectId,
			Map<String, Object> data) throws MetadataIndexException {
		
		if(!init) {
			throw new RuntimeException("Object not initialized. Make sure to call init() before indexMetadata()");
		}
		try {
			client.prepareIndex(indexName, type)
				.setId(objectId).setSource(data).execute().actionGet();
		} catch(ElasticSearchException e) {
			throw new MetadataIndexException("Unable to index metadata.", e);
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

	public String getIndexName() {
		return indexName;
	}

	public void setIndexName(String indexName) {
		this.indexName = indexName;
	}

}
