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

import java.util.List;
import java.util.Map;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

import com.github.jsonldjava.utils.JSONUtils;

import de.uzk.hki.da.metadata.RdfToJsonLdConverter;
import de.uzk.hki.fedorest.Fedora;
import de.uzk.hki.fedorest.FedoraException;
import de.uzk.hki.fedorest.FedoraResult;

/**
 * @author Sebastian Cuy
 */
public class IndexESAction extends AbstractAction {
	
	private Fedora fedora;
	private String[] esHosts;
	private String esCluster = "elasticsearch";
	private String esIndexName;

	@Override
	boolean implementation() {
		setKILLATEXIT(true);
		
		if (fedora == null) 
			throw new RuntimeException("Fedora object not set. Make sure the action is configured properly");
		if (esIndexName == null) 
			throw new RuntimeException("Elasticsearch index name not set. Make sure the action is configured properly");
		if (esHosts == null || esHosts.length == 0) 
			throw new RuntimeException("Elasticsearch hosts not set. Make sure the action is configured properly");

		// use test index for test packages
		// TODO move test contractors to config
		String contractorShortName = job.getObject().getContractor().getShort_name();
		if("TEST".equals(contractorShortName)
			|| "LVRInfoKom".equals(contractorShortName)
			|| "HBZ".equals(contractorShortName)
		) {
			esIndexName += "_test";
		}
		
		String pid = "danrw:" + object.getIdentifier().replace("+", ":");
		TransportClient client = null;
		try {
			
			FedoraResult result = fedora.getDatastreamDissemination()
					.param("pid", pid)
					.param("dsID", "EDM")
					.execute();
			if (result.getStatus() != 200)
				throw new RuntimeException("Error getting EDM datastream for pid: " + pid);
			
			// transform EDM to JSON
			RdfToJsonLdConverter converter = new RdfToJsonLdConverter("conf/frame.jsonld");
			Map<String, Object> json = converter.convert(result.getContent());
			
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
			
		} catch (FedoraException e) {
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
		// nothing to do		
	}
	
	public Fedora getFedora() {
		return fedora;
	}

	public void setFedora(Fedora fedora) {
		this.fedora = fedora;
	}

	public String[] getEsHosts() {
		return esHosts;
	}

	public void setEsHosts(String[] esHosts) {
		this.esHosts = esHosts;
	}

	public String getEsCluster() {
		return esCluster;
	}

	public void setEsCluster(String esCluster) {
		this.esCluster = esCluster;
	}

	public String getEsIndexName() {
		return esIndexName;
	}

	public void setEsIndexName(String esIndexName) {
		this.esIndexName = esIndexName;
	}

}
