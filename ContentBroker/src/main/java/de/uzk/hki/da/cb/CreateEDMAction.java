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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.commons.lang.NotImplementedException;

import de.uzk.hki.da.action.AbstractAction;
import de.uzk.hki.da.core.C;
import de.uzk.hki.da.metadata.XsltEDMGenerator;
import de.uzk.hki.da.repository.RepositoryException;
import de.uzk.hki.da.repository.RepositoryFacade;
import de.uzk.hki.da.util.ConfigurationException;
import de.uzk.hki.da.util.Path;

import static de.uzk.hki.da.utils.Utilities.isNotSet;

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
		if (isNotSet(preservationSystem.getUrisCho())) 
			throw new IllegalStateException("missing choBaseUri");
		if (isNotSet(preservationSystem.getUrisAggr())) 
			throw new IllegalStateException("missing aggrBaseUri");
		if (isNotSet(preservationSystem.getUrisLocal())) 
			throw new IllegalStateException("localBaseUri not set");
		if (edmMappings == null)
			throw new IllegalStateException("edmMappings not set.");
		for (String filePath:edmMappings.values())
			if (!new File(filePath).exists())
				throw new IllegalStateException("mapping file "+filePath+" does not exist");
		if (isNotSet(object.getPackage_type()))
			throw new IllegalStateException("missing package type");
	}



	@Override
	public boolean implementation() throws IOException, RepositoryException {
		
		String objectId = object.getIdentifier();
		
		String xsltFile = getEdmMappings().get(object.getPackage_type());
		if (xsltFile == null) {
			throw new RuntimeException("No conversion available for package type '" + object.getPackage_type() + "'. "+C.EDM_METADATA_STREAM_ID+" can not be created.");
		}
		
		File metadataFile = Path.makeFile(localNode.getWorkAreaRootPath(),C.WA_PIPS,
				C.WA_PUBLIC,object.getContractor().getShort_name(),object.getIdentifier(),object.getPackage_type()+C.FILE_EXTENSION_XML);
		if (!metadataFile.exists())
			throw new RuntimeException("Missing file in public PIP: "+object.getPackage_type()+C.FILE_EXTENSION_XML);
		
		String edmResult = generateEDM(objectId, xsltFile, new FileInputStream(metadataFile));
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
