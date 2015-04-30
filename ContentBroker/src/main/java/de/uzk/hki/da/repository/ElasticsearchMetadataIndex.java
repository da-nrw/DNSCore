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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.impl.client.DefaultHttpClient;
import org.elasticsearch.ElasticSearchException;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.jsonldjava.utils.JSONUtils;

import de.uzk.hki.da.core.C;
import de.uzk.hki.da.metadata.RdfToJsonLdConverter;
import de.uzk.hki.da.util.ConfigurationException;

/**
 * Metadata index implementation for elasticsearch.
 * @author Polina Gubaidullina
 * @author Sebastian Cuy
 */
public class ElasticsearchMetadataIndex implements MetadataIndex {
	
	private static final Logger logger = LoggerFactory.getLogger(ElasticsearchMetadataIndex.class);
	
	private String[] hosts;
	private String cluster;
	private TransportClient client;
	private String edmJsonFrame;
	
	@Override
	public void prepareAndIndexMetadata(String indexName, String id, String edmContent
			) throws RepositoryException, FileNotFoundException {
		
		if(edmJsonFrame==null) 
			throw new IllegalStateException("Frames must not be null");
		if (!new File(edmJsonFrame).exists())
			throw new FileNotFoundException(edmJsonFrame+" does not exist.");

		
		RdfToJsonLdConverter converter = new RdfToJsonLdConverter(edmJsonFrame);
		Map<String, Object> json = null;
		try {
			json = converter.convert(edmContent);
		} catch (Exception e) {
			throw new RuntimeException("An error occured during metadata conversion",e);
		}
		
		
		@SuppressWarnings("unchecked")
		List<Object> graph = (List<Object>) json.get("@graph");
		for (Object object : graph) {
			logger.trace("Preparing json graph for indexing in elasticsearch: \n{}", JSONUtils.toPrettyString(object));
			createIndexEntryForGraphObject(indexName, edmJsonFrame, object, id);
		}		
	}
	
	public String getEdmJsonFrame() {
		return edmJsonFrame;
	}

	public void setEdmJsonFrame(String edmJsonFrame) {
		this.edmJsonFrame = edmJsonFrame;
	}

	@Override
	public void indexMetadata(String indexName, String type, String objectId,
			Map<String, Object> data) throws MetadataIndexException {

		client = initialize();
		if (client==null) throw new IllegalStateException("transport client not initialized");
		
		try {
			logger.trace("prepare index, set "+data+" to object with id "+objectId);
			client.prepareIndex(indexName, type)
				.setId(objectId).setSource(data).execute().actionGet();
		} catch(ElasticSearchException e) {
			throw new MetadataIndexException("Unable to index metadata.", e);
		} finally {
			client.close();
		}
		
	}
	
	private void createIndexEntryForGraphObject(String indexName, String framePath, Object object, String objectID)
			throws RepositoryException {
		
		@SuppressWarnings("unchecked")
		Map<String,Object> subject = (Map<String,Object>) object;
		
		
		eraseUnmappableContent(subject);
		logger.trace("Will index adjusted json graph in elasticsearch: \n{}", JSONUtils.toPrettyString(object));	
		
		// Add @context attribute
//		String contextUri = contextUriPrefix + FilenameUtils.getName(framePath);
//		subject.put("@context", contextUri);
		
		// Add @root attribute
		if(subject.get(C.EDM_AGGREGATED_CHO).toString().contains("is root element")) {
			subject.put("@root", "true");
		} else {
			subject.put("@root", "false");
		}
		
		String idAsString = subject.get("@id").toString();
		String id = idAsString.substring(idAsString.indexOf(objectID));
		
		// extract index name from type
		String[] splitType = ((String) subject.get("@type")).split("/");
		String type = splitType[splitType.length-1];

		logger.trace("indexName: "+indexName+", type: "+type+", id: "+id);
		type=C.ORE_AGGREGATION; // override on purpose, so that everything is mapped against es_mapping.json
		
		try {
			indexMetadata(indexName, type, id, subject);
		} catch (MetadataIndexException e) {
			throw new RepositoryException("Unable to index metadata", e);			
		}	
	}
	
	private void eraseUnmappableContent(Map<String, Object> subject) {
		Object temp = subject.get("edm:object");
		if (temp!=null && (temp instanceof String)&&
				(((String)temp==null)||((String)temp).isEmpty())){
			logger.warn("removing edm:object from graph since it is an empty string");
			subject.remove("edm:object");
		}
	}

	private TransportClient initialize() {
		
		if (cluster == null || hosts == null || hosts.length == 0) 
			throw new ConfigurationException("cluster||hosts");
				
		Settings settings = ImmutableSettings.settingsBuilder()
		        .put("cluster.name", cluster).build();
		logger.trace("set cluster.name: {}", cluster);
		TransportClient client = new TransportClient(settings);
		for (String esHost : hosts) {
			client.addTransportAddress(new InetSocketTransportAddress(esHost, 9300));
		}
		return client;
	}
	
	@Override
	public String getIndexedMetadata(String indexName, String objectId) {
		
		try {
			String requestURL = 
					"http://localhost:9200/"+indexName+"/"+C.ORE_AGGREGATION+"/_search?q=_id:"+objectId+"*";
			System.out.println("requestURL:"+requestURL);
			logger.debug("requestURL:"+requestURL);
			URL wikiRequest;
			wikiRequest = new URL(requestURL);
			URLConnection connection;
			connection = wikiRequest.openConnection();
			connection.setDoOutput(true);  
			
			Scanner scanner;
			scanner = new Scanner(wikiRequest.openStream());
			String response = scanner.useDelimiter("\\Z").next();
			System.out.println("R:"+response);
			
			scanner.close();
			return response;
    	}catch(Exception e){
			e.printStackTrace();
		}
		return "";
	}
	
	@Override
	public String getAllIndexedMetadataFromIdSubstring(String indexName, String objectId) {
		
		try {
			String requestURL = 
					"http://localhost:9200/"+indexName+"/"+C.ORE_AGGREGATION+"/_search?q=_id:"+objectId+""+"*";
			System.out.println("requestURL:"+requestURL);
			logger.debug("requestURL:"+requestURL);
			URL wikiRequest;
			wikiRequest = new URL(requestURL);
			URLConnection connection;
			connection = wikiRequest.openConnection();
			connection.setDoOutput(true);  
			
			Scanner scanner;
			scanner = new Scanner(wikiRequest.openStream());
			String response = scanner.useDelimiter("\\Z").next();
			System.out.println("R:"+response);
			
			scanner.close();
			return response;
    	}catch(Exception e){
			e.printStackTrace();
		}
		return "";
	}

	@Override
	public void deleteFromIndex(String indexName, String objectID) throws MetadataIndexException, RepositoryException {
		logger.debug("Delete object "+objectID+" from index "+indexName+"...");
		try{
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpDelete deleteRequest = 
					new HttpDelete("http://localhost:9200/"+indexName+"/"+C.ORE_AGGREGATION+"/_query?q=_id:"+objectID+""+"*");
			HttpResponse response = httpClient.execute(deleteRequest);
	
			int statusCode = response.getStatusLine().getStatusCode();
	        if (statusCode > 300)   {
	            throw new RuntimeException("Failed : HTTP error code : "
	                + response.getStatusLine().getStatusCode());
	        }
	        httpClient.getConnectionManager().shutdown();
		} catch (Exception e) {
			throw new RepositoryException("Unable to delete the object with id "+objectID+"from elasticsearch index", e);
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
