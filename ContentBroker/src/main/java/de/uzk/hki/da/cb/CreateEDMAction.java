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

import static de.uzk.hki.da.core.C.*;
import static de.uzk.hki.da.utils.StringUtilities.isNotSet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Map;

import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;

import de.uzk.hki.da.action.AbstractAction;
import de.uzk.hki.da.metadata.XsltEDMGenerator;
import de.uzk.hki.da.repository.RepositoryException;
import de.uzk.hki.da.repository.RepositoryFacade;
import de.uzk.hki.da.util.ConfigurationException;

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
	private File edmDestinationFile = null;

	/**
	 * @
	 */
	@Override
	public void checkActionSpecificConfiguration() throws ConfigurationException {
		if (repositoryFacade == null) 
			throw new ConfigurationException("Repository facade object not set. Make sure the action is configured properly");
	}



	public void checkSystemStatePreconditions() throws IllegalStateException {
		
		if (edmMappings == null)
			throw new IllegalStateException("edmMappings not set.");
		for (String filePath:edmMappings.values())
			if (!new File(filePath).exists())
				throw new IllegalStateException("mapping file "+filePath+" does not exist");
		if (isNotSet(o.getPackage_type()))
			throw new IllegalStateException("missing package type");
	}



	@Override
	public boolean implementation() throws IOException, RepositoryException {
		checkSystemStatePreconditions();
		
		
		String xsltTransformationFile = getEdmMappings().get(o.getPackage_type());
		if (xsltTransformationFile == null)
			throw new RuntimeException("No mapping for package type: '" + o.getPackage_type());
		if (! new File(xsltTransformationFile).exists())
			throw new FileNotFoundException("Missing file: "+xsltTransformationFile);

		
		File metadataSourceFile = getWa().metadataStream(WA_PUBLIC,o.getPackage_type());
		if (!metadataSourceFile.exists())
			throw new RuntimeException("Missing file in public PIP: "+o.getPackage_type()+FILE_EXTENSION_XML);
		

		edmDestinationFile = generateEDM(xsltTransformationFile, metadataSourceFile);
		putToRepository(edmDestinationFile);
		
		return true;
	}
	
	
	
	
	private File generateEDM(String xsltTransformationFile,File metadataSourceFile) throws FileNotFoundException {
		
		File edm = getWa().metadataStream(WA_PUBLIC,METADATA_STREAM_ID_EDM); 
		
		String edmResult = generateEDM(o.getIdentifier(), xsltTransformationFile, new FileInputStream(metadataSourceFile));
		PrintWriter out = null;
		try {
			out = new PrintWriter(edm);
			out.println(edmResult);}
		finally {
			out.close();
		}
		
		return edm;

	}
	

	
	private void putToRepository(File file) throws RepositoryException, IOException {
		repositoryFacade.ingestFile(o.getIdentifier(), preservationSystem.getOpenCollectionName(), 
				METADATA_STREAM_ID_EDM+FILE_EXTENSION_XML, file, 
				"Object representation in Europeana Data Model", "application/rdf+xml");
	}

	
	
	@Override
	public void rollback() throws Exception {
		if ((edmDestinationFile!=null)&&(edmDestinationFile.exists())) 
			edmDestinationFile.delete();
	}



	private String generateEDM(String objectId, String xsltFile,
			InputStream metadataStream) throws FileNotFoundException {
		
		XsltEDMGenerator edmGenerator=null;
		try {
			edmGenerator = new XsltEDMGenerator(xsltFile, metadataStream);
		} catch (TransformerConfigurationException e1) {
			throw new RuntimeException(e1);
		}	
		edmGenerator.setParameter("urn", o.getUrn());
		edmGenerator.setParameter("cho-base-uri", preservationSystem.getUrisCho() + "/" + objectId);
		edmGenerator.setParameter("aggr-base-uri", preservationSystem.getUrisAggr() + "/" + objectId);
		edmGenerator.setParameter("local-base-uri", preservationSystem.getUrisLocal());
		String edmResult=null;
		try {
			edmResult = edmGenerator.generate();
		} catch (TransformerException e1) {
			throw new RuntimeException(e1);
		} catch (IOException e) {
			throw new RuntimeException(e);
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
