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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.jsonldjava.utils.JSONUtils;
import com.yourmediashelf.fedora.client.FedoraClient;
import com.yourmediashelf.fedora.client.FedoraClientException;
import com.yourmediashelf.fedora.client.FedoraCredentials;
import com.yourmediashelf.fedora.client.request.AddDatastream;
import com.yourmediashelf.fedora.client.request.AddRelationship;
import com.yourmediashelf.fedora.client.request.GetDatastreamDissemination;
import com.yourmediashelf.fedora.client.request.GetObjectProfile;
import com.yourmediashelf.fedora.client.request.Ingest;
import com.yourmediashelf.fedora.client.request.ModifyDatastream;
import com.yourmediashelf.fedora.client.request.PurgeObject;

import de.uzk.hki.da.metadata.RdfToJsonLdConverter;

public class Fedora3RepositoryFacade implements RepositoryFacade {
	
	private static final String ORE_AGGREGATION = "ore:Aggregation";
	private static Logger logger = LoggerFactory.getLogger(Fedora3RepositoryFacade.class);
	private MetadataIndex metadataIndex;
	private String contextUriPrefix;
	private FedoraClient fedora;
	private String edmJsonFrame;
	
	/**
	 * Instantiates a new fedora 3 repository facade.
	 * @param fedoraUrl the url to the fedora web application
	 * @param fedoraUser the fedora user, e.g. fedoraAdmin
	 * @param fedoraPass the corresponding password
	 * @throws MalformedURLException 
	 */
	public Fedora3RepositoryFacade(String fedoraUrl, String fedoraUser, String fedoraPass, String edmJsonFrame) throws MalformedURLException {
		FedoraCredentials fedoraCredentials = new FedoraCredentials(fedoraUrl, fedoraUser, fedoraPass);
		this.fedora = new FedoraClient(fedoraCredentials);
		this.edmJsonFrame = edmJsonFrame;
	}

	@Override
	public boolean purgeObjectIfExists(String objectId, String collection)
			throws RepositoryException {
		String pid = generatePid(objectId, collection);
		if (!objectExists(objectId, collection)) return false;
		try {
			new PurgeObject(pid).execute(fedora);
			logger.info("Successfully purged object in Fedora. pid: {}", pid);
		} catch (FedoraClientException e) {
			throw new RepositoryException("Unable to purge package " + pid, e);
		}
		return true;
	}
	
	

	@Override 
	public void createObject(String objectId, String collection, String ownerId) throws RepositoryException {
		String pid = generatePid(objectId, collection);
		try {
			new Ingest(pid).ownerId(ownerId).execute(fedora);
			logger.info("Successfully created object in Fedora. pid: {}", pid);
		} catch (FedoraClientException e) {
			throw new RepositoryException("Unable to create package " + pid, e);
		}
	}
	
	@Override
	public void ingestFile(String objectId, String collection, String dsId, File file, String label, String mimeType) throws RepositoryException, IOException {
		String pid = generatePid(objectId, collection);
		try {
			String dsLocation = "file://" + file.getAbsolutePath();
			new AddDatastream(pid, dsId).mimeType(mimeType)
				.controlGroup("E").dsLabel(label)
				.dsLocation(dsLocation).execute(fedora);
			logger.info("Successfully created datastream with dsID {} for file {}.", dsId, file.getName());
		} catch (FedoraClientException e) {
			throw new RepositoryException("Error while trying to add datastream for file " + file.getName(),e);
		}		
	}
	
	@Override
	public void createMetadataFile(String objectId, String collection, String dsId, String content, String label, String mimeType) throws RepositoryException {
		String pid = generatePid(objectId, collection);
		try {
			new AddDatastream(pid, dsId).mimeType(mimeType)
				.controlGroup("X").dsLabel(label)
				.content(content).execute(fedora);
			logger.info("Successfully created metadata datastream with dsID {}.", dsId);
		} catch(FedoraClientException e) {
			throw new RepositoryException("Unable to create metadata file: " + dsId, e);
		}
	}
	
	@Override
	public void updateMetadataFile(String objectId, String collection, String dsId, String content, String label, String mimeType) throws RepositoryException {
		String pid = generatePid(objectId, collection);
		try {
			new ModifyDatastream(pid, dsId).mimeType(mimeType)
				.dsLabel(label).content(content).execute(fedora);
			logger.info("Successfully updated metadata datastream with dsID {}.", dsId);
		} catch(FedoraClientException e) {
			throw new RepositoryException("Unable to update metadata file: " + dsId, e);
		}
	}
	
	@Override
	public String generateFileId(String path) {
		
		// replace slashes
		String dsID = path.replace("/", "-");
		
		// eliminate disallowed beginnings
		if (Character.isDigit(dsID.charAt(0))
				|| dsID.startsWith("xml")
				|| dsID.startsWith("XML")) {
			dsID = "_" + dsID;
		}
		
		// replace disallowed characters
		dsID = dsID.replaceAll("[^\\p{L}\\p{Digit}\\._-]","_");
		
		return dsID;
	}

	@Override
	public InputStream retrieveFile(String objectId, String collection, String fileId)
			throws RepositoryException {
		String pid = generatePid(objectId, collection);
		try {
			return new GetDatastreamDissemination(pid, fileId)
				.execute(fedora).getEntityInputStream();
		} catch (FedoraClientException e) {
			if (e.getStatus() == 404) { 
				logger.error("Failed to recieve Datastream, due to not found reason: " + objectId + " " + fileId);
				return null;
			} else {
				throw new RepositoryException("Failed to retrieve datastream: " + fileId, e);
			}
		}
	}
	
	@Override
	public boolean objectExists(String objectId, String collection) throws RepositoryException {
		String pid = generatePid(objectId, collection);
		try {
			new GetObjectProfile(pid).execute(fedora);
		} catch (FedoraClientException e) {
			if (e.getStatus() == 404) {
				// object does not exist and does not need to be purged
				return false;
			} else {
				throw new RepositoryException("Failed to check if package exists", e);
			}
		}
		return true;
	}
	
	@Override
	public void addRelationship(String objectId, String collection, String predicate, String object) throws RepositoryException {
		String pid = generatePid(objectId, collection);
		try {
			new AddRelationship("info:fedora/" + pid)
				.predicate(predicate)
				.object(object).execute(fedora);
		} catch (FedoraClientException e) {
			throw new RepositoryException("Unable to add relationship: info:fedora/"
					+ pid + "-" + predicate + "-" + object,  e);
		}
	}

	
	@Override
	public void indexMetadata(String indexName, String id, String edmContent
			) throws RepositoryException, FileNotFoundException {
		if(edmJsonFrame==null) 
			throw new IllegalStateException("Frames must not be null");
		if(metadataIndex==null)
			throw new IllegalStateException("Metadata index not set");
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
			
			logger.debug("Preparing json graph for indexing in elasticsearch: \n{}", JSONUtils.toPrettyString(object));
			createIndexEntryForGraphObject(indexName, edmJsonFrame, object);
		}		
	}
	
	
	
	

	public MetadataIndex getMetadataIndex() {
		return metadataIndex;
	}

	public void setMetadataIndex(MetadataIndex metadataIndex) {
		this.metadataIndex = metadataIndex;
	}
	
	private String generatePid(String objectId, String collection) {
		return (collection + ":" + objectId);
	}
	
	private void createIndexEntryForGraphObject(String indexName, String framePath, Object object)
			throws RepositoryException {
		
		@SuppressWarnings("unchecked")
		Map<String,Object> subject = (Map<String,Object>) object;
		
		
		eraseUnmappableContent(subject);
		logger.debug("Will index adjusted json graph in elasticsearch: \n{}", JSONUtils.toPrettyString(object));	
		
		// Add @context attribute
//		String contextUri = contextUriPrefix + FilenameUtils.getName(framePath);
//		subject.put("@context", contextUri);
		String[] splitId = ((String) subject.get("@id")).split("/");
		String id = splitId[splitId.length-1];
		// extract index name from type
		String[] splitType = ((String) subject.get("@type")).split("/");
		String type = splitType[splitType.length-1];

		logger.debug("indexName: "+indexName+", type: "+type+", id: "+id);
		type=ORE_AGGREGATION; // override on purpose, so that everything is mapped against es_mapping.json
		
		try {
			metadataIndex.indexMetadata(indexName, type, id, subject);
		} catch (MetadataIndexException e) {
			throw new RepositoryException("Unable to index metadata", e);			
		}	
	}

	private void eraseUnmappableContent(Map<String, Object> subject) {
		
		Object temp = subject.get("edm:object");
		if (temp==null) {
			logger.warn("removing edm:object from graph since it is null");
			subject.remove("edm:object");
		}else
		if ((temp instanceof String)&&(((String)temp==null)||((String)temp).isEmpty())){
			logger.warn("removing edm:object from graph since it is an empty string");
			subject.remove("edm:object");
		}
		Object isShownBy = subject.get("edm:isShownBy");
		if (isShownBy!=null) {
			logger.warn("removing edm:isShownBy from graph since it is null");
			subject.remove("edm:isShownBy");
		}
	}
	
	/**
	 * Get the name of the index
	 * the data will be indexed in.
//	 * @return the index name
	 */
	public String getContextUriPrefix() {
		return contextUriPrefix;
	}

	/**
	 * Set the name of the index
	 * the data will be indexed in.
	 * @param the index name
	 */
	public void setContextUriPrefix(String contextUriPrefix) {
		this.contextUriPrefix = contextUriPrefix;
	}

	
	@Override
	public String getIndexedMetadata(String indexName, String objectId) {
		
		try {
			String requestURL = 
					"http://localhost:9200/"+indexName+"/"+ORE_AGGREGATION+"/_search?q=_id:"+objectId;
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
}
