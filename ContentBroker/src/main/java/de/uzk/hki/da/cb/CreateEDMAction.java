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
import java.util.Map;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;

import de.uzk.hki.da.metadata.XsltGenerator;
import de.uzk.hki.da.repository.RepositoryFacade;
import de.uzk.hki.da.utils.XMLUtils;

/**
 * This action transforms the primary metadata of an
 * object into EDM/RDF and adds the result into
 * the presentation repository.
 * 
 * The transformation is done with XSLT, the mappings
 * can be found in "conf/xslt/edm/".
 * 
 * @author Sebastian Cuy
 */
public class CreateEDMAction extends AbstractAction {
	
	private RepositoryFacade repositoryFacade;
	private String choBaseUri;
	private String aggrBaseUri;
	private String localBaseUri;
	private Map<String,String> edmMappings;

	@Override
	boolean implementation() {
		if (repositoryFacade == null) 
			throw new RuntimeException("Repository facade object not set. Make sure the action is configured properly");
		
		String objectId = object.getIdentifier();
		
		try {
			
			logger.debug("Getting DC datastream for object: {}", objectId);
			
			InputStream dcStream = repositoryFacade.retrieveFile(objectId, "danrw", "DC");
			
			SAXBuilder builder = XMLUtils.createNonvalidatingSaxBuilder();
			Document doc = builder.build(dcStream);
			Element formatEl = doc.getRootElement().getChild("format",
					Namespace.getNamespace("http://purl.org/dc/elements/1.1/"));
			if (formatEl == null) {
				logger.warn("No format element found in DC. EDM cannot be created!");
				return true;
			}
			String packageType = formatEl.getTextNormalize();
			
			logger.debug("Read package type: {}", packageType);
			
			String xsltFile = getEdmMappings().get(packageType);
			if (xsltFile == null) {
				throw new RuntimeException("No conversion available for package type '" + packageType + "'. EDM can not be created.");
			}
			
			InputStream metadataStream = repositoryFacade.retrieveFile(objectId, "danrw", packageType);
			
			XsltGenerator edmGenerator = new XsltGenerator(xsltFile, metadataStream);	
			edmGenerator.setParameter("urn", object.getUrn());
			edmGenerator.setParameter("cho-base-uri", choBaseUri + "/" + objectId);
			edmGenerator.setParameter("aggr-base-uri", aggrBaseUri + "/" + objectId);
			edmGenerator.setParameter("local-base-uri", localBaseUri);
			String edmResult = edmGenerator.generate();
			
			repositoryFacade.createMetadataFile(objectId, "danrw", "EDM", edmResult, "Object representation in Europeana Data Model", "application/rdf+xml");
			
			logger.info("Successfully created EDM datastream for object {}.", objectId);
			
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		return true;
	}

	@Override
	void rollback() throws Exception {
		// nothing to do		
	}

	/**
	 * Gets the base URI that will be prepended to the
	 * object ID in order to coin a URI for Cultural
	 * Heritage Objects in the EDM data.
	 * @return the base URI
	 */
	public String getChoBaseUri() {
		return choBaseUri;
	}
	
	/**
	 * Sets the base URI that will be prepended to the
	 * object ID in order to coin a URI for Cultural
	 * Heritage Objects in the EDM data.
	 * @param the base URI
	 */
	public void setChoBaseUri(String choBaseUri) {
		this.choBaseUri = choBaseUri;
	}

	/**
	 * Gets the base URI that will be prepended to the
	 * object ID in order to coin a URI for Aggregations
	 * in the EDM data.
	 * @return the base URI
	 */
	public String getAggrBaseUri() {
		return aggrBaseUri;
	}

	/**
	 * Sets the base URI that will be prepended to the
	 * object ID in order to coin a URI for Aggregations
	 * in the EDM data.
	 * @param the base URI
	 */
	public void setAggrBaseUri(String aggrBaseUri) {
		this.aggrBaseUri = aggrBaseUri;
	}

	/**
	 * Gets the base URI that will be prepended to the
	 * relative IDs in the EDM data.
	 * @return the base URI
	 */
	public String getLocalBaseUri() {
		return localBaseUri;
	}
	
	/**
	 * Sets the base URI that will be prepended to the
	 * relative IDs in the EDM data.
	 * @param the base URI
	 */
	public void setLocalBaseUri(String localBaseUri) {
		this.localBaseUri = localBaseUri;
	}

	/**
	 * Gets the map that describes which XSLTs should be
	 * used to convert Metadata to EDM.
	 * @return a map, keys represent metadata formats,
	 * 	values the path to the XSLT file
	 */
	public Map<String,String> getEdmMappings() {
		return edmMappings;
	}

	/**
	 * Sets the map that describes which XSLTs should be
	 * used to convert Metadata to EDM.
	 * @param a map, keys represent metadata formats,
	 * 	values the path to the XSLT file
	 */
	public void setEdmMappings(Map<String,String> mappings) {
		this.edmMappings = mappings;
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
