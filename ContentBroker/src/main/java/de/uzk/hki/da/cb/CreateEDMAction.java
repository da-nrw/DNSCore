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
import java.util.Map;

import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.commons.lang.NotImplementedException;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;

import de.uzk.hki.da.action.AbstractAction;
import de.uzk.hki.da.core.C;
import de.uzk.hki.da.core.ConfigurationException;
import de.uzk.hki.da.metadata.XMLUtils;
import de.uzk.hki.da.metadata.XsltEDMGenerator;
import de.uzk.hki.da.repository.RepositoryException;
import de.uzk.hki.da.repository.RepositoryFacade;

/**
 * This action transforms the primary metadata of an
 * object into EDM/RDF and adds the result into
 * the presentation repository.
 * 
 * The transformation is done with XSLT, the mappings
 * can be found in "conf/xslt/edm/".
 * 
 * @author Sebastian Cuy
 * @author Daniel M. de Oliveira
 */
public class CreateEDMAction extends AbstractAction {
	
	private RepositoryFacade repositoryFacade;
	private Map<String,String> edmMappings;

	/**
	 * @
	 */
	@Override
	public void checkActionSpecificConfiguration() throws ConfigurationException {
		if (repositoryFacade == null) 
			throw new ConfigurationException("Repository facade object not set. Make sure the action is configured properly");
	}



	@Override
	public void checkSystemStatePreconditions() throws IllegalStateException {
		if (preservationSystem.getUrisCho()==null||preservationSystem.getUrisCho().isEmpty()) throw new IllegalStateException("choBaseUri not set");
		if (preservationSystem.getUrisAggr()==null||preservationSystem.getUrisAggr().isEmpty()) throw new IllegalStateException("aggrBaseUri not set");
		if (preservationSystem.getUrisLocal()==null||preservationSystem.getUrisLocal().isEmpty()) throw new IllegalStateException("localBaseUri not set");
		if (edmMappings == null)
			throw new IllegalStateException("edmMappings not set.");
		for (String filePath:edmMappings.values())
			if (!new File(filePath).exists())
				throw new IllegalStateException("mapping file "+filePath+" does not exist");
		
		
	}



	@Override
	public boolean implementation() throws IOException, RepositoryException {
		
		String objectId = object.getIdentifier();
		
		InputStream dcStream = getDCdatastreamFromPresRepo(objectId, preservationSystem.getOpenCollectionName());

		String packageType = parseFormatElement(dcStream);
		if (packageType == null) {
			logger.warn("No format element found in DC. "+C.EDM_METADATA_STREAM_ID+" cannot be created!");
			return true;
		}
		
		String xsltFile = getEdmMappings().get(packageType);
		if (xsltFile == null) {
			throw new RuntimeException("No conversion available for package type '" + packageType + "'. "+C.EDM_METADATA_STREAM_ID+" can not be created.");
		}
		
		InputStream metadataStream = repositoryFacade.retrieveFile(objectId,preservationSystem.getOpenCollectionName(), packageType);
		if (metadataStream==null){
			throw new RuntimeException("Could not retrieve some of the metadata files  : " + packageType);
		}
		
		String edmResult = generateEDM(objectId, xsltFile, metadataStream);
		logger.debug(edmResult);
		
		try {
			repositoryFacade.createMetadataFile(objectId,preservationSystem.getOpenCollectionName(), C.EDM_METADATA_STREAM_ID, edmResult, "Object representation in Europeana Data Model", "application/rdf+xml");
		} catch (RepositoryException e) {
			throw new RuntimeException(e);
		}
		
		logger.info("Successfully created EDM datastream for object {}.", objectId);

		return true;
	}

	
	
	@Override
	public void rollback() throws Exception {
		throw new NotImplementedException();	
	}



	private String generateEDM(String objectId, String xsltFile,
			InputStream metadataStream) throws FileNotFoundException {
		
		XsltEDMGenerator edmGenerator=null;
		try {
			edmGenerator = new XsltEDMGenerator(xsltFile, metadataStream);
		} catch (TransformerConfigurationException e1) {
			throw new RuntimeException(e1);
		}	
		edmGenerator.setParameter("urn", object.getUrn());
		edmGenerator.setParameter("cho-base-uri", preservationSystem.getUrisCho() + "/" + objectId);
		edmGenerator.setParameter("aggr-base-uri", preservationSystem.getUrisAggr() + "/" + objectId);
		edmGenerator.setParameter("local-base-uri", preservationSystem.getUrisLocal());
		String edmResult=null;
		try {
			edmResult = edmGenerator.generate();
		} catch (TransformerException e1) {
			throw new RuntimeException(e1);
		}
		return edmResult;
	}
	
	/**
	 * 
	 * @param dcStream
	 * @return null if format el not found
	 * @throws IOException 
	 */
	private String parseFormatElement(InputStream dcStream) throws IOException{

		SAXBuilder builder = XMLUtils.createNonvalidatingSaxBuilder();
		Document doc;
		try {
			doc = builder.build(dcStream);
		} catch (JDOMException e) {
			throw new RuntimeException(e);
		}
		
		Element formatEl = doc.getRootElement().getChild("format",
				Namespace.getNamespace("http://purl.org/dc/elements/1.1/"));
		if (formatEl==null) return null;
		
		String packageType = formatEl.getTextNormalize();
		logger.debug("Read package type: {}", packageType);
		return packageType;
	}
	

	/**
	 * @param objectId
	 * @param collName
	 * @return
	 * @trows RuntimeException if file could not be retrieved or there was an error while trying to retrieve the file
	 */
	private InputStream getDCdatastreamFromPresRepo(String objectId,
			String collName) {
		
		logger.debug("Getting DC datastream for object: {}", objectId);
		InputStream dcStream;
		try {
			dcStream = repositoryFacade.retrieveFile(objectId,collName, "DC");
		} catch (RepositoryException e) {
			throw new RuntimeException("Error while trying to retrieve file from presentation repository.",e);
		}
		if (dcStream==null){
			throw new RuntimeException("File DC not existent in collection "+collName+ " of presentation repository.");
		}
		return dcStream;
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
	public void setEdmMappings(Map<String,String> edmMappings) {
		this.edmMappings = edmMappings;
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
